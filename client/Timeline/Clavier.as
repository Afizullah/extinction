/*
    Copyright 2020, Atelier801

    These files are part of Extinction Minijeux.

    Extinction Minijeux is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Extinction Minijeux is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Extinction Minijeux.  If not, see https://www.gnu.org/licenses/.

*/

import com.adobe.crypto.SHA256;

var FreinIdentification:Boolean = true;
var LimiteChat:int = 0;
var DernierMessage:String = "wsefvgy";
var CompteurMessageIdentique:int = 1; 
//"<font color='#009D9D'>Politique de modération du chat principal :</font>\n <font color='#6C77C1'>Vous ne pouvez parler sur le chat principal que si vous cumulez 200 heures de jeu.
//_Interface._ChatSortie.htmlText = "<font color='#6C77C1'>Pour éviter les débordements, la politique de modération sur le chat principal est extrêmement sévère. En cas d'abus, les comptes incriminés seront purement et simplement </font><font color='#CB546B'>supprimés définitivement</font><font color='#6C77C1'>.</font>";

_Interface.ChatV2.BarreChat._ChatEntrée.restrict = "^#|<>";

var MdpConnexion : String = "";
var MdpCensuré : Boolean = false;

stage.addEventListener(KeyboardEvent.KEY_DOWN, Clavier_onPress);
stage.addEventListener(KeyboardEvent.KEY_UP, Clavier_onRelease);

function Tentative_Connexion() {
	if (FreinIdentification) {
		var Nom:String = _Serveur._Identification._Texte.text;
		var Mdp:String = _Serveur._Identification._Mdp.text;
		if (Nom != "") {
			if (Mdp == "" || Mdp == "Mot de passe") {
				Envoie_Serveur("CxN#"+Nom);
				MdpConnexion = "";
			} else {
				var Hash:String=com.adobe.crypto.SHA256.hash(Mdp);
				Envoie_Serveur("CxN#"+Nom+"#"+Hash);
				MdpConnexion = Mdp;
			}
			FreinIdentification = false;
			_Serveur._Identification.Erreur.visible = false;
			_Serveur._Identification.Aide.visible = false;
			_Serveur._Identification.Chargement.visible=true;
		}
	}
}

function Clavier_onPress(E:KeyboardEvent):void {
	Inaction = 0;
	if (_Serveur.visible) {
		if (_Serveur._Identification.visible && ! _Serveur._Identification._Compte.visible) {
			if (E.keyCode == 13 && FreinIdentification) {
					Tentative_Connexion();
			} else {
				if (E.keyCode == 9) {
					_Serveur._Identification.Clique_Identifier(null);
				}
			}
		}
	} else if (!_Profil.visible&&!_InfoTeam.visible&&!ChgPass.visible&&!_Messagerie.visible) {

		if ((E.keyCode == 226 || (E.keyCode == 192 && Capabilities.os.indexOf("Mac OS") == 0)) && !E.altKey &&  !_Forum.visible){
				_Interface.ChatV2.BarreChat.Changer_Cible(null);
				stage.focus = _Interface.ChatV2.BarreChat._ChatEntrée;
				return;
		}

		if (stage.focus == _Interface.ChatV2.BarreChat._ChatEntrée) {
			if (E.keyCode == 13) {

				//
				var Texte:String = _Interface.ChatV2.BarreChat._ChatEntrée.text;
				while (Texte.substr(0, 1) == " ") {
					Texte = Texte.substr(1);
				}

				_Interface.ChatV2.BarreChat._ChatEntrée.text = "";

				if (Texte == "") {
					return;
				}

				if (getTimer() - LimiteChat < 500) {
					Message_Chat("Doucement, merci.");
					return;
				}

				//

				//
				if (Texte.charAt(0) == "/") {
					Commandes(Texte.substr(1));
					return;
				}

				if (NomJoueur.substr(0,1) == "*") {
					Message_Chat("Vous devez créer un compte pour pouvoir parler.");
					_Interface.ChatV2.BarreChat._ChatEntrée.text = "";
					return;
				}

				//
				if (DernierMessage == Texte) {
					if (CompteurMessageIdentique == 2) {
						Nouveau_Message_Chat("Votre dernier message est identique.");
						return;
					}
					CompteurMessageIdentique++;
				}
				else {
					CompteurMessageIdentique = 1;
				}

				LimiteChat = getTimer();
				DernierMessage = Texte;
				if (Censure(Texte)) {
					Message_Chat("Merci de rester poli.");
				} else if (!CensureMdp(Texte)) {
					if (_Interface.ChatV2.BarreChat.Destinataire.visible) {
						var Destinataire:String = _Interface.ChatV2.BarreChat.Destinataire.text;
						if (Destinataire == "") {
							stage.focus = _Interface.ChatV2.BarreChat.Destinataire;
						} else {
							Chuchoter_Message(Destinataire, Texte);
						}
					} else {
						Texte = PrepareTexte(Texte);
						if (_Interface.ChatV2.BarreChat.Cible.MP.visible) {
							Envoie_MP(Texte);
						} else if (_Interface.ChatV2.BarreChat.Cible.Team.visible) {
							Envoie_Team(Texte);
						} else if (_Interface.ChatV2.BarreChat.Cible.Salon.visible) {
							Commandes("s " + Texte);
						} else if (_Interface.ChatV2.BarreChat.Cible.VIP.visible) {
							Envoie_VIP(Texte);
						} else if (_Interface.ChatV2.BarreChat.Cible.CT.visible) {
							Envoie_Fusion(Texte);
						} else if (_Interface.ChatV2.BarreChat.Cible.Elo.visible) {
							Commandes("melo " + Texte);
						} else if (NoChat && _ListePartie.visible){
							Message_Chat("Vous avez fermé le chat principal via l'option. Réactivez-le ou passez en mode étendu.");
						} else {
							// on retire les majs
							if (Texte.length > 4) {
								var Recherche : String = Texte;
								var Position : int = Recherche.search(/[A-Z]/);
								var Count : int = 0;
								while (Position != -1) {
									Recherche= Recherche.substr(Position+1);
									Position = Recherche.search(/[A-Z]/);
									Count++;
								}
								if (Count>15 || 2*Count > Texte.length) {
									Texte = Texte.substring(0,1) + Texte.substr(1).toLowerCase();
								}


							}
							Envoie_Serveur("CxM#"+Texte);
						}
					}
				}
			}
		} else {
			if (_Interface.visible && E.keyCode == 13) {
				stage.focus = _Interface.ChatV2.BarreChat._ChatEntrée;
			}
		}
	}
}

function Clavier_onRelease(E:KeyboardEvent):void {
}

var MotsCensurés : Array = new Array("pétasse","tepu","youporn","redtube","conard","cute-eva","sophiecalin","connard"," pute","salop","batard","fdp","f dp","fd p","connasse","encul","poufiasse","minitroopers");
var NombreMotsCensurés:int = MotsCensurés.length;
function Censure(Phrase : String) : Boolean {
	var Texte : String = Phrase.toLowerCase();
	for (var i:int=0; i<NombreMotsCensurés; i++){
		if (Texte.indexOf(MotsCensurés[i]) != -1) {
			return true;
		}
	}
	return false;
}

function CensureMdp(Phrase : String) : Boolean {
	var Texte : String = Phrase.toLowerCase();
	if (MdpConnexion!=""&&Texte.indexOf(MdpConnexion) !=-1) {
		Message_Serveur("Votre message n'a pas été envoyé car il contenait votre mot de passe. Il est interdit de donner son mot de passe sous peine de bannissement définitif. Les comptes sont strictement personnels. Ne vous connectez pas sur le compte de quelqu'un d'autre. Aucun modérateur ne vous demandera votre mot de passe.",2);
		//MdpCensuré = true;
		return true;
	}
	return false;
}

function PrepareTexte(Texte : String) : String{
	return Texte.replace(/[\r]/g,".").replace(/\[/g,"(").replace(/\]/g,")");
}