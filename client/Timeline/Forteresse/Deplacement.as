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

var DroiteEnCours:Boolean = false;
var GaucheEnCours:Boolean = false;
var ToucheConstruction:Boolean = false;
var Incontrolable:Boolean = false;
var Controlable:int = 5;
var ATIncontrolableCompteur:int = 0;
var ATIncontrolaleBool:Boolean = false;
var LimiteSaut:int = 0;
var IntervalSaut:int = 100;
var LimiteChat:int = 0;
var DernierMessage:String = "wsefvgy";
var CompteurMessageIdentique:int = 1;

function Deplacement(MOBILE:MovieClip, DROITE:Boolean, STOP:Boolean):void {
	var Mouvement:MovieClip;
	if (STOP) {
		MOBILE.DeplacementX = 0;
		if (MOBILE.CD.visible) {
			Mouvement = MOBILE.SD;
		} else {
			Mouvement = MOBILE.SG;
		}
	} else {
		if (DROITE) {
			Mouvement = MOBILE.CD;
			MOBILE.DeplacementX = 2;
		} else {
			Mouvement = MOBILE.CG;
			MOBILE.DeplacementX = -2;
		}
	}
	//
	MOBILE.AnimEnCours.gotoAndStop(1);
	MOBILE.AnimEnCours.visible = false;
	MOBILE.AnimEnCours = Mouvement;
	MOBILE.AnimEnCours.gotoAndPlay(1);
	MOBILE.AnimEnCours.visible = true;
}

function Saut(JOUEUR:MovieClip):void {
	JOUEUR.VitesseY = -4;
}

function Clavier_onPress(E:KeyboardEvent):void {
	_root.Inaction = 0;
	//
	if (_Monde.visible) {
		
		if ((E.keyCode == 226 || (E.keyCode == 192 && Capabilities.os.indexOf("Mac OS") == 0)) && !E.altKey ){
				_Monde.BarreChat.Changer_Cible(null);
				stage.focus = _Monde.BarreChat._ChatEntrée;
				return;				
		}
			
		if (stage.focus == _Monde.BarreChat._ChatEntrée) {
			if (E.keyCode == 13) {
				stage.focus = stage;

				//
				var Texte:String = _Monde.BarreChat._ChatEntrée.text;
				while (Texte.substr(0, 1) == " ") {
					Texte = Texte.substr(1);
				}

				_Monde.BarreChat._ChatEntrée.text = "";

				if (Texte == "") {
					return;
				}

				if (getTimer() - LimiteChat < 500) {
					Nouveau_Message_Chat("Doucement, merci.");
					return;
				}

				//
				if (Texte.charAt(0) == "/") {
					_root.Commandes(Texte.substr(1));
					return;
				}
				
				if (_root.NomJoueur.substr(0,1) == "*") {
					Nouveau_Message_Chat("Vous devez créer un compte pour pouvoir parler.");
					_Monde.BarreChat._ChatEntrée.text = "";
					return;
				}				
				
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
				if (_root.Censure(Texte)) {
					Nouveau_Message_Chat("Merci de rester poli.");
				} else if (!_root.CensureMdp(Texte)) {				
					if (_Monde.BarreChat.Destinataire.visible) {
						var Destinataire:String = _Monde.BarreChat.Destinataire.text;
						if (Destinataire == "") {
							stage.focus = _Monde.BarreChat.Destinataire;
						} else {
							_root.Chuchoter_Message(Destinataire, Texte);
						}
					} else {
						Texte = _root.PrepareTexte(Texte);
						if (_Monde.BarreChat.Cible.Allies.visible) {
							_root.Envoie_Serveur(Codage(13)+$+Texte+$+"0");
						} else if (_Monde.BarreChat.Cible.MP.visible) {
							_root.Envoie_MP(Texte);
						} else if (_Monde.BarreChat.Cible.Salon.visible) {
							_root.Commandes("s " + Texte);							
						} else if (_Monde.BarreChat.Cible.Team.visible) {
							_root.Envoie_Team(Texte);
						} else if (_Monde.BarreChat.Cible.VIP.visible) {
							_root.Envoie_VIP(Texte);
						} else if (_Monde.BarreChat.Cible.CT.visible) {
							_root.Envoie_Fusion(Texte);
						} else{
							_root.Envoie_Serveur(Codage(13)+$+Texte);
						}
					}
				}

			}
		} else {
			// DROITE
			if (Vivant) {
				if (!DroiteEnCours && (E.keyCode == 68 || E.keyCode == 39)) {
					DroiteEnCours = true;
					Deplacement(ClipJoueur, true, false);
					_root.Envoie_Serveur(Codage(5)+$+ClipJoueur.x+$+ClipJoueur.y+$+int(ClipJoueur.VitesseX*100)+$+int(ClipJoueur.VitesseY*100));
					E.updateAfterEvent();
				} else {
					// GAUCHE
					if (!GaucheEnCours && ((E.keyCode == 81 && _root.Azerty) || (E.keyCode == 65 && !_root.Azerty) || E.keyCode == 37)) {
						GaucheEnCours = true;
						Deplacement(ClipJoueur, false, false);
						_root.Envoie_Serveur(Codage(6)+$+ClipJoueur.x+$+ClipJoueur.y+$+int(ClipJoueur.VitesseX*100)+$+int(ClipJoueur.VitesseY*100));
						E.updateAfterEvent();
					} else {
						// SAUT
						if (!Incontrolable && ((E.keyCode == 90 && _root.Azerty) || (E.keyCode == 87 && !_root.Azerty) || E.keyCode == 38) && getTimer()-LimiteSaut > IntervalSaut) {							
							Incontrolable = true || true;
							Controlable = 0;							
							Incontrolable = true;							
							Incontrolable = true;							
							Controlable = 0;
							Incontrolable = true && true;														
							Saut(ClipJoueur);
							_root.Envoie_Serveur(Codage(7)+$+ClipJoueur.x+$+ClipJoueur.y+$+int(ClipJoueur.VitesseX*100)+$+int(ClipJoueur.VitesseY*100));
							E.updateAfterEvent();

							if (!Incontrolable && !ATIncontrolaleBool) {
								ATIncontrolaleBool = true && true && true;
								_root.Envoie_Serveur("CxTriche#Sauts#????");										
							}
							
						} else {
							// Construction
							if (!ToucheConstruction && (E.keyCode == 83 /*S*/|| E.keyCode == 40 /*Bas*/)) {
								ToucheConstruction = true;
								ModeConstruction = true;
								Curseur.visible = true;
								_Réticule.visible = false;
								ConstructionActive = false;
								Boucle_Souris(null, false);
								E.updateAfterEvent();
							} else if (E.keyCode == 79 /*O*/) {
								// TODO réfléchir case objectif
								var X:int=int(ClipPlateau.mouseX/10);
								var Y:int=int(ClipPlateau.mouseY/10);								
								_root.Envoie_Serveur(Codage(17)+$+X+$+Y); // carré objectif								
							} else {
								// RECHARGE
								if (!ClipJoueur.Recharge && ArmeEnCours == 0 && (E.keyCode == 69 || E.keyCode == 17) && !ClipJoueur.TD.visible && !ClipJoueur.TG.visible) {
									ClipJoueur.Recharge = true;
									_root.Envoie_Serveur(Codage(11));
									E.updateAfterEvent();
								} else {
									// Changement d'arme
									if (E.keyCode == 65 || E.keyCode == 16) {
										if (ArmeEnCours == 0) {
											Changement_Arme(1);
										} else {
											Changement_Arme(0);
										}
										E.updateAfterEvent();
									} else {
										// CHAT
										if (E.keyCode == 13 &&!_root._InfoTeam.visible) {
											stage.focus = _Monde.BarreChat._ChatEntrée;
											E.updateAfterEvent();
										}
									}
								}
							}
						}
					}
				}
			} else {
				// CHAT
				if (E.keyCode == 13) {
					stage.focus = _Monde.BarreChat._ChatEntrée;
				}
			}
		}
	} else {
		//if (_Identification.visible) {
		//if (E.keyCode == 13) {
		//var Nom:String = _Identification._Texte.text;
		//if (Nom != "") {
		//var Sexe:String;
		//if (_Identification.Homme.Encoche.E1.visible) {
		//Sexe = "1";
		//} else {
		//Sexe = "0";
		//}
		//_root.Envoie_Serveur(Codage(118)+$+Nom+$+SalonCible+$+Sexe);
		//_Identification.visible = false;
		//}
		//}
		//}
	}
}

function Clavier_onRelease(E:KeyboardEvent):void {
	if (Vivant) {
		if (DroiteEnCours && (E.keyCode == 68 || E.keyCode == 39)) {
			DroiteEnCours = false;
			if (ClipJoueur.CD.visible) {
				Deplacement(ClipJoueur, false, true);
				_root.Envoie_Serveur(Codage(4)+$+ClipJoueur.x+$+ClipJoueur.y+$+int(ClipJoueur.VitesseX*100)+$+int(ClipJoueur.VitesseY*100));
				E.updateAfterEvent();
			}
		} else {
			if (GaucheEnCours && ((E.keyCode == 81 && _root.Azerty) || (E.keyCode == 65 && !_root.Azerty) || E.keyCode == 37)) {
				GaucheEnCours = false;
				if (ClipJoueur.CG.visible) {
					Deplacement(ClipJoueur, false, true);
					_root.Envoie_Serveur(Codage(4)+$+ClipJoueur.x+$+ClipJoueur.y+$+int(ClipJoueur.VitesseX*100)+$+int(ClipJoueur.VitesseY*100));
					E.updateAfterEvent();
				}
			} else {
				// Construction
				if (ToucheConstruction && (E.keyCode == 83 || E.keyCode == 40)) {
					ToucheConstruction = false;
					ModeConstruction = false;
					Curseur.visible = false;
					if (!_root.NoLag) {
						_Réticule.visible = true;
					}					
					ConstructionActive = false;
					Boucle_Souris(null, false);
					E.updateAfterEvent();
				}
			}
		}
	}
}