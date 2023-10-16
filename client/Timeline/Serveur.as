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

import flash.display.MovieClip;
import com.adobe.crypto.SHA256;

include 'domaine.as'

var Version:String="1.0 ";
var VersionMineure:String="a";
var MauvaiseVersion:Boolean=false;
var $:String=String.fromCharCode(2);
var Restriction:String="^<>#°"+"§"+$;
var ModeModo:Boolean=false;
var ForumLecture:Boolean=false;
var ModuleIPS:Boolean=false;
var IPS:$IPS;
var MDT:Array=new Array(10);
var CMDTEC:int;
var PetitChat : Boolean = true; // Salon activé ou non
var Tentatives : int = 0; // Nb de tentatives de connexions
var ModePartie:int = 0; // Mode de la partie (MS, Frag, Cross, etc) en cours (valeur dépendante du jeu)
// Joueur
var NomJoueur:String;
var Avatar:String="0";
var DateServeur:Date;
var MODO:Boolean=false;
var MODOFORUM:Boolean=false;
var RECRUEFORUM:Boolean=false;
var MODOTEAM:Boolean=false;
var RESPOTRITEAM:Boolean=false;
var ARBITRE:Boolean=false;
var ARBITREPLUS:Boolean = false;
var VISION:Boolean=false;
var ARBITRET:Boolean=false; // Arbitre tournoi
var FILMEUR:Boolean=false;
var RESPOMAPFORTO:Boolean = false;
var IdJoueur:String="0";
var Silence:Boolean=false;
var HackDétecté:Boolean=false;
var Anonyme:Boolean=false;
var ListeNoire:Array = new Array();
var RechercheForum:Boolean = false;
var RechercheBackup:String = "";
var DernierChucho:String = "";
var DernierMessageForum:String = "";
var DerniereCoForum:int = 9999999999999;
var RcmpEloActives:int = 1; // 0 = récompenses perso désactivées, 1 = récompenses activées, 2 = toutes récompenses invisibles
var IsBan:Boolean = false;
var ForumsVisibles:int = 0; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
var Azerty:Boolean = true;
var VoirFeux:Boolean = true;

// Team
var TEAM:Boolean=false;
var FUSION:Boolean = false;
var IdTeam:Number=0;
var LEADER:Boolean=false;
var RECRUTEUR:Boolean=false;
var SCRIBE:Boolean = false;
var SALONCHAT:Boolean=false;

// Event
var Halloween:Boolean = false;
var Noel:Boolean = false;
var Carnaval:Boolean = false;
var Avril:Boolean = false;
var Paques:Boolean = false;
var Nouvelan:Boolean = false;
var SaintValentin:Boolean = false;
var Saturnaaaahles:Boolean = false;

///
var StVAmoureux:String = "";

var ListeKick:Array = new Array();
var ListeMute:Array = new Array();
var ListeBan:Array = new Array();

var TeteAaaahSansEvent:Boolean = false;
//

var IdMapPartieEnCours:String = "00000";
SetIdMapPartieEnCours("0");

_Serveur._Identification.visible=false;
_Bouboum.visible=false;
_Aaaah.visible=false;
_Forteresse.visible=false;
_Interface.visible=false;
_ListePartie.visible=false;

var ReceptionBouboum:Boolean=false;
var ReceptionAaaah:Boolean=false;
var ReceptionForteresse:Boolean=false;

_Serveur._Identification.visible=false;
_Serveur._Identification._Version.text="Version : "+Version;

var Serveur:XMLSocket;

if (!AfficherParametresIP) {
	ConnexionServeurJeu(Domaine, Port);
}

function ConnexionServeurJeu(Dom:String, NumPort:int):void {
	Serveur = new XMLSocket(Dom,NumPort); //443
	
	Serveur.addEventListener(Event.CONNECT, Connexion);
	Serveur.addEventListener(Event.CLOSE, Deconnexion);
	Serveur.addEventListener(IOErrorEvent.IO_ERROR, Connexion_Impossible);
	Serveur.addEventListener(SecurityErrorEvent.SECURITY_ERROR, Connexion_Interdite);
	Serveur.addEventListener(DataEvent.DATA, Reception);
}

//Security.loadPolicyFile("http://www.extinction.fr/crossdomain.xml");
Security.loadPolicyFile("http://atelier801.com/crossdomain.xml");
Security.allowDomain("*");
Security.allowInsecureDomain("img.atelier801.com");
Security.allowInsecureDomain("atelier801.com");

// Anti triche
var H:String = loaderInfo.bytes.toString().toUpperCase();

// Connexion au serveur
var BoucleReveille:Timer=new Timer(10000);
var TempsZeroBR:int;
var Kikoo:Boolean=true;
function Connexion(E:Event):void {
	if (est_banni()) {
		Connexion_Bannie();
		return;
	}
	_Serveur._Message.visible=false;
	_Serveur._Identification.visible=true;
	//Serveur.send(Encrypt(Version+VersionMineure));
	Serveur.send(Version+VersionMineure);
	BoucleReveille.addEventListener(TimerEvent.TIMER, Boucle_Reveille);
	BoucleReveille.start();
	TempsZeroBR=getTimer();

	/*if (ExternalInterface.available) {
		var Adresse:String = ExternalInterface.call("window.location.href.toString");
		if (Adresse == null) {
			trace("Adresse is null");
		} else {
			var InfoSalon:Array = Adresse.split("?n=");
			if (InfoSalon.length <= 1) {
				trace("No n=");
			} else {
				trace("n = ");
			}
		}
	} else {
		trace("External non available");
	}*/
	
	
}

var Inaction:int=0;
var Centaine:int=0;
function Boucle_Reveille(E:Event):void {
	if (! _Aaaah._Editeur.visible&&! _Forum.visible) {
		Inaction++;
	}
	if (Inaction>20 && (_Aaaah.visible||_Forteresse.visible||_Bouboum.visible) && !FILMEUR && !ARBITRET) {
		if (_Aaaah.visible) {
			_Aaaah.Clique_Quitter(null);
		} else if (_Forteresse.visible) {
			_Forteresse.Clique_Quitter(null);
		} else if (_Bouboum.visible) {
			_Bouboum.Clique_Quitter(null);
		}
		Nouveau_Message_Chat("Vous avez été déconnecté du salon car vous étiez inactif depuis trop longtemps.");
		//Serveur.close();
		//Deconnexion(null);
		//_Serveur._Message._Texte.text="La connexion avec le serveur vient d'être interrompue. Vous êtes inactif depuis trop longtemps :'( ";
	} else {
		var TempsAvantEnvoie:int=getTimer();
		Envoie_Serveur("CxS");
		TempsZeroBR=TempsAvantEnvoie;
	}
}

// Connexion au serveur impossible
function Connexion_Impossible(E:Event):void {
	_Serveur._Message._Texte.text="\nConnexion au serveur impossible :'(";
}

function Connexion_Bannie():void {
	MauvaiseVersion=true;
	IsBan = true;
	_Serveur._Message._Texte.text="\nVous avez été banni.";
	Deconnexion(null);
}


// Connexion interdite
function Connexion_Interdite(E:SecurityErrorEvent):void {
	_Serveur._Message._Texte.text="\nConnexion au serveur impossible :'(";
}

// Deconnexion du serveur
function Deconnexion(E:Event):void {

	BoucleReveille.stop();
	var CarteSauve:Boolean=false;
	if (_Aaaah._Monde.Porte.Retour.visible) {
		CarteSauve=true;
		System.setClipboard(_Aaaah._Editeur.Formatage_Carte().toString());
	}
	var MessageSauve:Boolean=false;
	if (_Forum.AffichageSujet.visible) {
		var Message:MovieClip = MovieClip(_Forum.AffichageSujet.ClipListePost.getChildAt(_Forum.AffichageSujet.ClipListePost.numChildren-1));
		if (Message.getChildByName("Envoyer") != null) {
			MessageSauve=true;
			var LeTexte:String = Message.Texte.text.replace(/\r/g,"\r\n");
			System.setClipboard(LeTexte);
		}
		
	}
	for (var i:int = 0; i<numChildren; i++) {
		DisplayObject(getChildAt(i)).visible=false;
	}
	_Serveur.visible=true;
	if (_Serveur._Identification!=null) {
		_Serveur._Identification.visible=false;
	}
	if (_SelectionJeu!=null) {
		_SelectionJeu.visible=false;
	}
	_Serveur._Message.visible=true;
	if (CarteSauve) {
		_Serveur._Message._Texte.text="La connexion avec le serveur vient d'être interrompue :'(\n\nVotre carte a été sauvegardée dans le presse-papier (faites CTRL+V dans le petit cadre de l'éditeur pour la récupérer).";
	} else if (MessageSauve) {
		_Serveur._Message._Texte.text="La connexion avec le serveur vient d'être interrompue :'(\n\nVotre message a été sauvegardé dans le presse-papier (faites CTRL+V pour le récupérer).";
	} else if (IsBan) {
		//Connexion_Bannie();		
	} else if (! MauvaiseVersion) {
		_Serveur._Message._Texte.text="\nLa connexion avec le serveur vient d'être interrompue :'(";
	}
	if (HackDétecté) {
		_InfosHack.visible = true;
	}
}

/*function encrypt(CHAINE:String, cle:String):String {
    var encrypt:Array = new Array;
    var i:int;
    var p:String = "?";
    for (i = 0; i < CHAINE.length; i++) {
        encrypt[i] = String.fromCharCode(CHAINE.charCodeAt(i) + cle.charCodeAt(i % cle.length) + p.charCodeAt(0));
        p = String.fromCharCode(CHAINE.charCodeAt(i) + CHAINE.length - i);
    }
    CHAINE = encrypt.join("");
    return CHAINE;
}*/

/**
 * Permet d'attribuer une valeur correct à <IdMapPartieEnCours> (aka 
 * un String de 5 caractères représentant le nombre <nb>)
 */
function SetIdMapPartieEnCours(nb:String):void {
	// % 75269 => 5 chiffres max
	//IdMapPartieEnCours = "" + nb;
	IdMapPartieEnCours = "00000".substr(0, 5 - nb.length) + nb;
}

// Envoie de données
function Envoie_Serveur(CHAINE:String):void {
	var LCMDT:Array = String((CMDTEC % 9000)+1000).split("");
	var cle:String = MDT[LCMDT[0]]+MDT[LCMDT[1]]+MDT[LCMDT[2]]+MDT[LCMDT[3]];
//    CHAINE = encrypt(CHAINE, cle);
	try {
		Serveur.send(cle+IdMapPartieEnCours+CHAINE);
	} catch (error:Error) {
		trace("Erreur Envoie_Serveur");
	}
    
	//Serveur.send(Encrypt(cle+CHAINE));
	CMDTEC++;
}
/*
function Encrypt(CHAINE:String):String {
	var message:String = CHAINE;
	var bytes:ByteArray = new ByteArray();
	
	bytes.writeUTF(CHAINE);
	
	//const TAILLE:int = CHAINE.length;
	const TAILLE:int = bytes.length;
	
	trace(message);

	for(var i:int = 2; i < TAILLE; ++i) {
		bytes[i] = ~(((bytes[i] & 0x03) << 6) | (bytes[i] >> 2));
	}
	
	trace(bytes.readUTF());
	
	return bytes.readUTF();
}*/

// Reception de données
function Reception(E:DataEvent):void {
	var Donnée:String=E.data; 
	//Donnée = Decrypt(Donnée);
	if (ReceptionBouboum) {
		_Bouboum.Reception(Donnée);
		return;
	}
	if (ReceptionAaaah) {
		_Aaaah.Reception(Donnée);
		return;
	}
	if (ReceptionForteresse) {
		_Forteresse.Reception(Donnée);
		return;
	}
	//
	Reception_Spéciale(Donnée);
}

/*function Decrypt(CHAINE:String):String {
	var message:String = CHAINE;
	const TAILLE:int = CHAINE.length;
	
	*//*for(var i:int = 0; i < TAILLE; ++i) {
        var b:int = ~(message.charAt(i));
		message.charAt(i) = (((b & 0xC0) >> 6) | (b << 2));
	}*/
/*
	return message;
}*/

function Reception_Spéciale(DATA:String):void {
	var Message:Array=DATA.split("#");  
	var Code:String=Message[0];
	var Partie:MovieClip;
	// Message
	if (Code=="CxM") {
		if (Message.length<4) {
			if (Silence || (NoChat /*&& PetitChat*/)) {
				return;
			}
			
			var MSG:String = Message[1];
			var Pseudo:String = Message[2];
			
			if (Saturnaaaahles) {
				if (ListeMute.indexOf(Message[2]) != -1) {
					MSG = "<font size='7'>" + MSG + "</font>";
				}
				if (ListeBan.indexOf(Message[2]) != -1 || ListeKick.indexOf(Message[2]) != -1) {
					Pseudo = "?";
				}
			}
			
			Nouveau_Message_Chat(MSG, Pseudo, 0, true);
		} else {
			if (Message.length==4) {
				// chuchotement
				if (Message[3]==NomJoueur) {
					// envoyé
					Nouveau_Message_Chat(Message[2], Message[1], 2);
				} else {
					// reçu
					if (SonGuideActif && !(_Aaaah.visible||_Forteresse.visible||_Bouboum.visible)) {
						var bipChucho:BipChucho = new BipChucho();
						bipChucho.play();
					}
					DernierChucho = Message[3];
					_Forum.NewMessage.visible=true;
					_Messagerie.NewMessage.visible=true;
					_Aaaah._Editeur.NewMessage.visible=true;
					////
					_Forum.NewMessage.Degriser();
					_Messagerie.NewMessage.Degriser();
					_Aaaah._Editeur.NewMessage.Degriser();
					////
					
					var LeChucho : String = _Forum.NewMessage.VignetteTexte+"["+Message[3]+"] "+Message[2]+"\n";
					_Forum.NewMessage.VignetteTexte=LeChucho;
					_Messagerie.NewMessage.VignetteTexte=LeChucho;
					_Aaaah._Editeur.NewMessage.VignetteTexte=LeChucho;
					Nouveau_Message_Chat(Message[2], Message[3], 1);
				}
			} else if (Message.length == 5) {
				// connexion/déconnexion des amis
				if (Message[4]=="0") {
					Nouveau_Message_Chat("", Message[1], 3);
				} else {
					Nouveau_Message_Chat("", Message[1], 4);
				}
			} else {
				// Chuchotement échoué
				Nouveau_Message_Chat("Ce joueur n'est pas connecté.");
			}
		}
		return;
	}
	if (Code == "CxMANIM") {
		Nouveau_Message_Chat(Message[1], "", 0, false, true);
		return;
	}
	if (Code=="CxSH") {
		/*if (Message.length<2) {
			Message_Serveur("Activité suspecte détectée. Un modérateur a été alerté.", 2);
		} else {
			Message_Serveur("Activité suspecte détectée pour " + Message[1]+".", 3);
		}*/
		return;
	}
	// Vérification du speed hack
	if (Code=="CxHS") {
		CodeCSH=int(Message[1]);
		CSHT0=getTimer();
		addEventListener(Event.ENTER_FRAME, Boucle_CSH);
		return;
	}
	// Ajout ami
	if (Code=="CxAA") {
		if (Message.length==2) {
			if (Message[1]=="0") {
				Nouveau_Message_Chat("Ce joueur est désormais dans votre liste d'amis.");
			} else if (Message[1]=="1") {
				Nouveau_Message_Chat("Ce joueur est déjà dans votre liste d'amis.");
			} else if (Message[1]=="2") {
				Nouveau_Message_Chat("Votre liste d'amis est pleine.");
			} else if (Message[1]=="3") {
				Nouveau_Message_Chat("Impossible d'ajouter ce joueur.");
			}
		}
		return;
	}
	// Un joueur rejoint une partie
	if (Code=="CxR") {
		if (Message.length==1) {
			_ListePartie._MotDePasse.Valider.visible=true;
			_ListePartie._MotDePasse.Texte.text="Mot de passe incorrect";
		} else {
			if (Message.length==2) {
				Suppr_Joueur(Message[1]);
			} else {
				Partie=Code_Partie(Message[2]);
				Partie.Joueur.text=Message[3];
				Partie.NbJoueur=int(Message[3]);
				MAJ_Couleur_Partie(Partie, Message[4]);
				//Message_Chat(Message[1]+" a rejoint une partie ("+Partie.Jeu.text+").");
				Suppr_Joueur(Message[1]);
				MAJ_Compteur();
			}
		}
		return;
	}
	// Nouveau Joueur
	if (Code=="CxO") {
		if (Saturnaaaahles) {
			Nouveau_Joueur(Message[1], false, Message[2] == "1");
		} else {
			Nouveau_Joueur(Message[1], false, false);
		}
		MAJ_Liste_Joueur();
		return;
	}
	// Deco Joueur
	if (Code=="CxX") {
		Suppr_Joueur(Message[1]);
		return;
	}
	// Un Joueur quitte une partie
	if (Code=="CxQ") {
		Partie=Code_Partie(Message[1]);
		var NbJoueur:String=Message[2];
		if (NbJoueur=="0") {
			Suppression_Partie(Partie);
		} else {
			Partie.Joueur.text=NbJoueur;
			Partie.NbJoueur=int(NbJoueur);
			MAJ_Couleur_Partie(Partie, Message[3]);
		}
		MAJ_Compteur();
		return;
	}
	// Nouvelle Partie
	if (Code=="CxNP") {
		Nouvelle_Partie(Message[1], Message[2], Message[3], Message[4], Message[5] == "1",0, (Message.length>6?Message[6]:""));
		MAJ_Liste_Partie();
		MAJ_Compteur();
		return;
	}
	// Partie classée > choisir une équipe
	if (Code == "CxTelo") {
		_ChoixEquipeElo.visible = true;
		_ChoixEquipeElo.ActualiserListe(Message[1], Message[2], Message[3], Message[4], int(Message[5]));
		return;
	}
	
	// Demande de validation des équipes pour lancer le match classé
	if (Code == "CxELOVAL") {
		_ValiderMatchElo.Initialisation(Message[1], Message[2], Message[3]);
		return;
	}
	
	// Un joueur a refusé le match classé
	if (Code == "CxELOREF") {
		_ValiderMatchElo.MatchRefuse();
		return;
	}
	
	// Début du match
	if (Code == "CxELOSTRT") {
		_ValiderMatchElo.DebutMatch();
		return;
	}
	
	// Un joueur a accepté le match classé
	if (Code == "CxELOOK") {
		_ValiderMatchElo.ValiderPour(int(Message[1]), (Message[2] == "1"));
		return;
	}
	
	if (Code == "CxEXCLELO") {
		_ChoixEquipeElo.Clean();
		_ValiderMatchElo.DebutMatch();
		_ChoixEquipeElo.visible = false;
		_ListePartie.visible = true;
		Nouveau_Message_Chat("La partie elo où vous étiez a commencé.");
		return;
	}
	// Id map partie en cours
	if (Code == "CxIMEC") {
		SetIdMapPartieEnCours(Message[1]);
		return;
	}
	// Mode de la partie en cours
	if (Code == "CxMODE") {
		ModePartie = parseInt(Message[1]);
		return;
	}
	// Changement Jeu
	if (Code=="CxJ") {
		Changement_Jeu(Message[1], Message[2]);
		return;
	}
	// Liste des joueurs du salon
	if (Code=="CxG") {
		var InfoListe:Array=Message[3].split(",");
		InfoListe=InfoListe.sort();
		MAJ_ListeJoueurSalon(InfoListe);
		_ListePartie._ListeJoueur.TempsRestant.text="Temps : "+Message[1]+"/"+Message[2];
		return;
	}
	// Stats
	if (Code=="CxSt") {
		Message.shift();
		_InfoJoueur.Initialisation(Message);
		return;
	}
	// Profil
	if (Code=="CxPr") {
		Message.shift();
		_Profil.Initialisation(Message);
		return;
	}
	// Récompenses
	if (Code=="CxRec") {
		Message.shift();
		_Palmares.Initialisation(Message);
		return;
	}	
	// Activation salon
	if (Code=="CxSAct") {
		SALONCHAT = (Message[1] == "1");
		return;
	}	
	
	// Choix Equipe
	if (Code=="CxTe") {
		_ChoixEquipe.Initialisation(Message[1],Message[2]);
		return;
	}
	// Choix Equipe
	if (Code=="CxSigM") {
		_SignalerMap.visible = true;
		if (Message.length == 2) {
			_SignalerMap.Code.text = "@" + Message[1];
		}
		return;
	}	
	// Infos hack
	if (Code=="CxHACK") {
		HackDétecté = true;
		_InfosHack.visible = true;
		return;
	}		
	// Réception des sujets d'un forum
	if (Code=="FoF") {		
		RechercheForum = (Message[1]=="1");
		if (RechercheForum) {
			RechercheBackup = DATA;
		}
		var PageVoulue:int=Message[2];
		var PageMax:int=Message[3];
		Message.splice(0, 4);
		_Forum.Initialisation_Forum(Message,PageVoulue,PageMax);
		return;
	}
	// Réception d'un sujet                                                                                                                                                                              
	if (Code=="FoS") {
		Message.shift();
		//var TEMPS = getTimer();
		var NombreMessage:int=Message.shift();
		var Page:int=Message.shift();
		EnvoiEnCours.visible = false;
		_Forum.AffichageSujet.Initialisation(NombreMessage, Page, Message);
		//trace((Message.length/3)+" messages affichés en "+(getTimer()-TEMPS)+" millisecondes.");
		return;
	}
	// Mauvaise version
	if (Code=="CxV") {
		if (Message.length==5) {
			_Serveur._Identification.TexteJoueur.text=Message[1]+" joueurs en ligne";
			DateServeur = new Date();
			DateServeur.setTime(Number(Message[2]));
			// Mal de tête
			var LCMDT:Array=Message[3].split("");
			for (var i:int = 0; i<10; i++) {
				var CMDT:int=int(LCMDT[i]);
				if (CMDT==0) {
					MDT[i]=String.fromCharCode(10);
				} else {
					MDT[i]=String.fromCharCode(CMDT);
				}
			}
			CMDTEC=int(Message[4]);
			//_Serveur._Identification.Joueurs.text = Message[1];
		} else {
			var str = Capabilities.os + "|" + + Capabilities.screenResolutionX + "x" + Capabilities.screenResolutionY;
			var env = "";
			for (var __i = 0; __i < str.length; __i++) {
				env += String.fromCharCode(str.charCodeAt(__i) + 1);
			}
			Envoie_Serveur("CxV#" + env);
			//trace("Mauvaise version");
			_Serveur._Identification.visible = false;
			_Serveur._Message.visible = true;
			//_Serveur._Message._Texte.text="\nVotre version du jeu est incorrecte.";
			MauvaiseVersion=true;
		}
		return;
	}
	// Reception Nom Joueur
	if (Code=="CxN") {
		NomJoueur=Message[1];
		_Serveur.visible=false;
		_SelectionJeu.visible=true;
		_ListePartie._Jouer.Bienvenue.htmlText="Bienvenue, "+NomJoueur;
		_ListePartie._Jouer.TexteInfo.text="Enregistrez-vous pour créer vos propres parties et bénéficier de toutes les fonctionnalités du jeu.";
		_ListePartie._Jouer.BtnNouvellePartie.visible=false;
		_Interface.Amis.visible=false;
		Options.ListeNoire.visible=false;
		_Interface.Messagerie.visible=false;
		_Interface.Profil.visible=false;
		_Interface.Vote.visible=false;
		NoVote = true;
		Options.NoVote.Encoche.E0.visible=! NoVote;
		Options.NoVote.Encoche.E1.visible=NoVote;		
		TriFrag = false;
		Options.TriFrag.Encoche.E0.visible=! TriFrag;
		Options.TriFrag.Encoche.E1.visible=TriFrag;				
		ForumLecture=true;
		//_Forum.Forums.Avatar.visible = false;
		return;
	}
	if (Code == "CxFUSION") {
		FUSION = true;
		return;
	}
	// Erreur Identification
	if (Code=="CxID") {
		if (Message.length==2) {
			FreinIdentification=true;
			_Serveur._Identification.Aide.visible=true;
			_Serveur._Identification.Erreur.visible=true;
			_Serveur._Identification.Erreur.text="Ce joueur est déjà connecté.";
			_Serveur._Identification.Chargement.visible=false;
			_Serveur._Identification._Mdp.text="";			
		} else {
			if (Message.length==25) {
				TempsAntiFlood = 3000;
				_Serveur.visible=false;
				NomJoueur=Message[1];
				_ListePartie._Jouer.Bienvenue.htmlText="Bienvenue, "+NomJoueur;
				_Serveur.removeChild(_Serveur._Identification);
//				_Serveur._Identification=null;
				_SelectionJeu.visible=true;
				// Sexe Forteresse
				if (Message[2]=="0") {
					Options.Clique_Femme(null);
				} else if (Message[2] == "1") {
					Options.Clique_Homme(null);
				} else if (Message[2] == "2") {
					Options.Clique_Femme2(null);
				} else if (Message[2] == "3") {
					Options.Clique_Homme2(null);
				} else if (Message[2] == "4") {					
					VoirJack=true;
				}
				
				//
				Avatar=Message[3];
				SonActif=Message[5]=="1";
				SonGuideActif=Message[6]=="1";
				AffichageConnexions=Message[7]=="1";
				AffichageConnexionsAmis=Message[8]=="1";
				AffichageMessages=Message[9]=="1";
				TriFrag=Message[10]=="1";
				OldSprite=Message[11]=="1";
				NoLag=Message[12]=="1";
				NoVote=Message[13]=="1";
				TirContinu=Message[19]=="1";
				NoChat=Message[20]=="1";
				NoStats=Message[21]=="1";
				NoMP=Message[22]=="1";
				DerniereCoForum = int(Message[23]);
				RcmpEloActives = int(Message[24]); // 0 = récompenses perso désactivées, 1 = récompenses activées, 2 = toutes récompenses invisibles
				
				if (NoStats) {
					Nouveau_Message_Chat("Vous avez désactivé les statistiques.");						
				}
				
				Options.Son.Encoche.E0.visible=! SonActif;
				Options.Son.Encoche.E1.visible=SonActif;
				Options.SonGuide.Encoche.E0.visible=! SonGuideActif;
				Options.SonGuide.Encoche.E1.visible=SonGuideActif;
				Options.AffichageConnexions.Encoche.E0.visible=! AffichageConnexions;
				Options.AffichageConnexions.Encoche.E1.visible=AffichageConnexions;
				Options.AffichageConnexionsAmis.Encoche.E0.visible=! AffichageConnexionsAmis;
				Options.AffichageConnexionsAmis.Encoche.E1.visible=AffichageConnexionsAmis;
				Options.AffichageMessages.Encoche.E0.visible=! AffichageMessages;
				Options.AffichageMessages.Encoche.E1.visible=AffichageMessages;
				Options.TriFrag.Encoche.E0.visible=! TriFrag;
				Options.TriFrag.Encoche.E1.visible=TriFrag;
				Options.OldSprite.Encoche.E0.visible=! OldSprite;
				Options.OldSprite.Encoche.E1.visible=OldSprite;
				Options.NoLag.Encoche.E0.visible=! NoLag;
				Options.NoLag.Encoche.E1.visible=NoLag;
				Options.NoChat.Encoche.E0.visible=! NoChat;
				Options.NoChat.Encoche.E1.visible=NoChat;
				Options.NoStats.Encoche.E0.visible=! NoStats;
				Options.NoStats.Encoche.E1.visible=NoStats;		
				Options.NoMP.Encoche.E0.visible=! NoMP;
				Options.NoMP.Encoche.E1.visible=NoMP;					
				Options.NoVote.Encoche.E0.visible=! NoVote;
				Options.NoVote.Encoche.E1.visible=NoVote;
				Options.TirContinu.Encoche.E0.visible=! TirContinu;
				Options.TirContinu.Encoche.E1.visible=TirContinu;
				IdJoueur=Message[4];
				//Chargement_Image(_Forum.Forums.Avatar.Avatar, "I/C/", Avatar, true);
				//Chargement_Image(_Forum.Forums,"ImgForumModo", true)
				if (Message[14]=="0") {
					ForumLecture=true;
				}

				if (Message[15]=="1") {
					JoinTeam(Number(Message[16]));
					ChangeDroits(Message[17]);
				}
				if (Message[18]=="1") {
				    if(!(ForumsVisibles & 1)){
		               ForumsVisibles |= 1; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		               _Forum.ListeForum.push("Forum VIP", "Pour les comptes flagués.", "7", "4a24f186.png");//
		             }
					
					if (Message[15]!="1") {
						_Forum.ListeForum.push("Forum Team", "Votre topic team et ceux des teams en fusion avec la vôtre.", "11", "6524f115.jpg");
					}
					MODOTEAM=true;
				} else if (Message[18]=="2") {
					RESPOTRITEAM=true;
				}
			} else {
				_Serveur._Identification.Erreur.visible=true;
				_Serveur._Identification.Chargement.visible=false;					
				
				if (Tentatives>5) {
					_Serveur._Identification.Aide.visible=false;					
					FreinIdentification = false;
					_Serveur._Identification.Erreur.text="Trop de tentatives. L'admin a été averti.";
				} else {
					_Serveur._Identification.Aide.visible=true;
					Tentatives++;
					FreinIdentification=true;
					_Serveur._Identification.Erreur.text="Nom ou mot de passe incorrect.";
				}

			}
		}
		return;
	}
	
	if (Code == "CxBBIMRTL") {
		_Bouboum.immortel = true;
		return;
	}

    if (Code == "CxNOEV") {
		Halloween = false;
		Noel = false;
		Carnaval = false;
		Avril = false;
		Paques = false;
		Nouvelan = false;
		SaintValentin = false;
		Saturnaaaahles = false;
		
		_Forteresse._Monde._Action.Arme0.PH.visible = false;
		_Forteresse._Monde._Action.Arme0.PN.visible = true;
		/*var nc2:ColorTransform = new ColorTransform();
		nc2.color = 0x36364B;
		_Bouboum._Monde._Partie._Niveau._FondNiveauBoum.transform.colorTransform = nc;*/
		return;
	}
	if (Code == "CxEVHLWN") {
		Halloween = !Halloween;
		_Aaaah._Monde.Porte.Tombe.visible = Halloween;
		_ListePartie.toile.visible = Halloween;
		
		_Forteresse._Monde._Action.Arme0.PH.visible = Halloween;
		_Forteresse._Monde._Action.Arme0.PN.visible = !Halloween;
		
		/*var nc:ColorTransform = new ColorTransform();
		nc.color = (Halloween ? 0x663E00 : 0x36364B);
		_Bouboum._Monde._Partie._Niveau._FondNiveauBoum.transform.colorTransform = nc;*/
		return;
	}
	if (Code == "CxEVNOEL") {
		Noel = !Noel;
		if (Noel) {
			_ChuteNeige.visible = true;
			//_ChuteNeige.StartStopNeige();
		} else {
			_ChuteNeige.visible = false;
		}
		return;
	}
	if (Code == "CxEVCRVL") {
		Carnaval = !Carnaval;
		return;
	}
	if (Code == "CxEVAVR") {
		Avril = !Avril;
		return;
	}
	if (Code == "CxEVPQS") {
		Paques = !Paques;
		return;
	}
	if (Code == "CxEVNVAN") {
		Nouvelan = !Nouvelan;
		return;
	}
	if (Code == "CxEVSTV") {
		SaintValentin = !SaintValentin;
		return;
	}
	if (Code == "CxEVSAT") {
		Saturnaaaahles = !Saturnaaaahles;
		return;
	}
	
	if (Code == "CxSATKK") { // Kick Saturnaaaahles
		if(ListeKick.indexOf(Message[1]) == -1) {
		   ListeKick.push(Message[1]);
		}
		return;
	}
	if (Code == "CxSATMT") { // Mute Saturnaaaahles
		if(ListeMute.indexOf(Message[1]) == -1) {
		   ListeMute.push(Message[1]);
		}
		return;
	}
	if (Code == "CxSATBN") { // Ban Saturnaaaahles
		if(ListeBan.indexOf(Message[1]) == -1) {
		   ListeBan.push(Message[1]);
		}
		return;
	}
	if (Code == "CxSATOUT") {
		if (_ListePartie.visible || _Interface.visible || _Forum.visible) {
			MAJ_SurvivalKick(Message[1]);
		}
		return;
	}
	
	if ((Nouvelan && Code == "CxARTF") || Code == "CxARTF2") {
		if (!VoirFeux) {
			return;
		}
		
		_FeuxArtifice.visible = true;
		_FeuxArtifice.Go(int(Message[1]) % 5);
		
		return;
	}
	
	if (SaintValentin && Code == "CxSTVLINK") { // Union de 2 joueurs établie
		StVAmoureux = Message[1];
		
		if (_Forteresse.visible) {
			_Forteresse.MAJ_Liste_Joueur();
		} else if (_Aaaah.visible) {
			_Aaaah.MAJ_Liste_Joueur();
		} else if (_Bouboum.visible) {
			_Bouboum.MAJ_Liste_Joueur();
		}
		return;
	}
	if (SaintValentin && Code == "CxSTVULINK") { // Rupture d'union
		StVAmoureux = "";
		
		if (_Forteresse.visible) {
			_Forteresse.MAJ_Liste_Joueur();
		} else if (_Aaaah.visible) {
			_Aaaah.MAJ_Liste_Joueur();
		} else if (_Bouboum.visible) {
			_Bouboum.MAJ_Liste_Joueur();
		}
		return;
	}
	if (Code == "CxRCMPELO") {
		RcmpEloActives = int(Message[1]);
		if (_Aaaah.visible) {
			var action:int = (RcmpEloActives == 2 ? 1 /* Cache les couleurs */ : 0 /* Mettre les couleurs */);
			var ls:Array = _Aaaah.ListeJoueur;
			var joueur:MovieClip;
			for (var i:int = ls.length - 1; i > -1; --i) {
				joueur = ls[i][1];
				if (!Halloween || !joueur.Zombie) {
					_Aaaah.GestionRecompensesElo(joueur.RecompensesElo, joueur, action);
				}
			}
		}
		return;
	}
	// Téléportation Position Courante
	if (Code == "CxTPC") {
		if (_Forteresse.visible) {
			_Forteresse.ClipJoueur.x += int(Message[1]);
			_Forteresse.ClipJoueur.y += int(Message[2]);
			
			_Forteresse.ClipJoueur.x = Math.round(_Forteresse.ClipJoueur.x) % 2000;
			_Forteresse.ClipJoueur.y = Math.round(_Forteresse.ClipJoueur.y) % 1000;
			
			_Forteresse.Deplacement(_Forteresse.ClipJoueur, false, true);
			Envoie_Serveur(_Forteresse.Codage(1)+$+_Forteresse.ClipJoueur.x+$+_Forteresse.ClipJoueur.y+$+"0"+$+"0");
			_Forteresse.ClipJoueur.x2 = 100000-2*_Forteresse.ClipJoueur.x;
			_Forteresse.ClipJoueur.y2 = 100000-2*_Forteresse.ClipJoueur.y;
		}
		return;
	}
	// Téléportation Position Absolue
	if (Code == "CxTPA") {
		if (_Forteresse.visible) {
			_Forteresse.ClipJoueur.x = int(Message[1]) % 2000;
			_Forteresse.ClipJoueur.y = int(Message[2]) % 1000;
			
			_Forteresse.Deplacement(_Forteresse.ClipJoueur, false, true);
			Envoie_Serveur(_Forteresse.Codage(1)+$+_Forteresse.ClipJoueur.x+$+_Forteresse.ClipJoueur.y+$+"0"+$+"0");
			_Forteresse.ClipJoueur.x2 = 100000-2*_Forteresse.ClipJoueur.x;
			_Forteresse.ClipJoueur.y2 = 100000-2*_Forteresse.ClipJoueur.y;
		}
		return;
	}
	
	// Classement
	if (Code=="CxCl") {
		var CodeClassement=Message[1];
		if (CodeClassement=="1") {
			Classement.ReceptionBouboum(Message[2].split(";"));
		} else if (CodeClassement == "2") {
			if (Message[2]=="1") {
				Classement.ReceptionAaaah(Message[3].split(";"));
			} else if (Message[2]=="2"  || Message[2] == "3") {
				Classement.ReceptionAaaahCourse(Message[3].split(";"));
			}
		} else if (CodeClassement=="3") {
			Classement.ReceptionStats(Message[2].split($));
		} else if (CodeClassement == "4") {
			Classement.ReceptionElo((Message[2] == "" ? null : Message[2].split(";")));
		}
		return;
	}
	// Temps avant mise à jour du classement
	if (Code=="CxMAJ") {
		var Temps : Number = Message[1];
		var Heures : int = Temps/60;
		var Min : int = Temps%60;
		var Minutes : String = (Min<10?"0"+Min:""+Min);
		Classement.ProchaineMAJ.htmlText="Prochaine mise à jour dans <font color='#009D9D'>"+Heures+" heures et "+Minutes+" minutes</font>.";
		return;
	}
	// liste des ignorés
	if (Code=="CxIG") {
		Initialisation_ListeNoire(Message[1]);
		return;
	}

	// chuchotement ignoré
	if (Code=="CxIGN") {
		Message_Serveur(Message[1]+" n'a pas reçu votre message car vous êtes dans sa liste noire.", 2);
		return;
	}

	// liste d'amis / membres teams
	if (Code=="CxAM") {
		Initialisation_ListeAmis(Message[2],Message[1]=="0");
		return;
	}
	// liste des maps
	if (Code=="CxMAP") {
		_FenetreMaps.Initialisation_ListeMaps(Message[1],Message[2]);
		return;
	}
	// liste des maps rally
	if (Code=="CxMAR") {
		_FenetreRally.Initialisation_ListeMaps(Message[1]);
		return;
	}
	
	// exporter rally
	if (Code=="FxBEXP") {
		_ExporterRally.Initialisation(Message[1]);
		return;
	}
	// update rally
	if (Code=="FxBUPD") {
		_ExporterRally.Initialisation_Respomap(Message); //
		return;
	}
	// update rally
	if (Code=="FxUPD") {
		UpdateRally = true;
		SaveMapCommande();
		return;
	}
	// liste des playlist
	if (Code=="CxPlist") {
		_FenetrePlaylist.initialisation(Message[1]);
		return;
	}
	//Validation publication playlist
	if (Code=="CxVAPL") {
		_FenetrePlaylist.OptionsPublier.importer();
	}
	// Erreur publication playlist
	if (Code=="CxEPPL") {
		_FenetrePlaylist.OptionsPublier.visible = true;
		_FenetrePlaylist.OptionsPublier.Erreur.text = Message[1];
		_FenetrePlaylist.OptionsPublier.Erreur.visible = true;
		return;
	}
	// Ping
	if (Code=="CxPing") {
		Message_Serveur("Votre ping est de "+(getTimer()-int(Message[1]))+" millisecondes.", 2);
		return;
	}
	// Demande de ping
	if (Code=="CxPong") {
		if (Message[1]=="1") {
			// Quelqu'un demande le ping de cette personne
			Envoie_Serveur("CxPong#1#"+Message[2]);// on renvoie
		} else {
			// Réception du ping d'une personne
			Message_Serveur("Le ping de "+Message[2]+" est de "+Message[3]+" millisecondes.", 2);
		}
		return;
	}
	// Erreur Création compte
	if (Code=="CxCC") {
		if (Message.length == 2) {
			_Serveur._Identification._Compte.Aide.text="Nice try.";
		} else {
			_Serveur._Identification._Compte.Aide.text="Ce nom est déjà utilisé.";
		}
		_Serveur._Identification._Compte.Valider.visible=true;
		_Serveur._Identification._Compte.EffacePseudo=true;
		return;
	}
	// Message Modo
	if (Code=="CxMS") {
		ModeModo=true;
		Message_Serveur(Message[1], 1);
		return;
	}
	// Message Serveur
	if (Code=="CxMSS") {
		Message_Serveur(Message[1], 0);
		return;
	}
	// Message info serveur (réservé au modo)
	if (Code=="CxMSI") {
		Message_Serveur(Message[1], -1);
		return;
	}
	// Canal arbitre
	if (Code=="CxMSA") {
		Message_Serveur(Message[1], -2);
		return;
	}
	// Message avertos modos
	if (Code=="CxMSV") {
		Message_Serveur(Message[1], -3);
		return;
	}	
	// Message avertos modos f
	if (Code=="CxMSF") {
		Message_Serveur(Message[1], -4);
		return;
	}		
	// Info Modo
	if (Code=="CxMODO") {
		MODO=true;
		MODOFORUM=true;
		_Forteresse._Monde._Action.Vision.visible=true;
		_Forum.Forums.Retour.y=575;
		_InfoModo.Initialisation(Message[1]);
		return;
	}
	// Forum Modo
	if (Code=="CxMOD") {
		MODO=true;
		MODOFORUM=true;
		
		_Forteresse._Monde._Action.Vision.visible=true;
		_Forum.Forums.Retour.y=575;
		if(!(ForumsVisibles & 4)){
		    ForumsVisibles |= 4;
		    _Forum.ListeForum.push("Forum modo", "Rien que pour les modos.", "0", "$ImgForumM");
		   }
		if(!(ForumsVisibles & 1)){
		   ForumsVisibles |= 1; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		  _Forum.ListeForum.push("Forum VIP", "Pour les comptes flagués.", "7", "4a24f186.png");//
		 }
		if(!(ForumsVisibles & 2)){
		   ForumsVisibles |= 2; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		  _Forum.ListeForum.push("Forum Animateurs", "Pour les animateurs.", "9", "b724f1ac.png");//
		 }
		
		return;
	}
	// Statut forum
	if (Code=="CxVF") {
		if (Message.length==2) {
			MAJ_Icone_Forum(Message[1], true);
		} else {
			MAJ_Icone_Forum(Message[1], false);
		}
		return;
	}
	// Dernière co forum
	if (Code=="CxVFD") {
		DerniereCoForum=int(Message[1]);
		return;
	}
	
	// Chat fermé
	if (Code=="CxF") {
		if (Message.length==1) {
			Message_Serveur("Ce chat a été désactivé par un modérateur.", 2);
		} else {
			Message_Serveur("Vous ne pouvez pas parler sur le chat principal avant d'avoir cumulé 20 heures de jeu. Tapez /temps pour connaitre votre temps de jeu.", 2);
		}
		return;
	}
	// Contrôle chuchotement
	if (Code=="CxRef") {
		if (Message[1]=="0") {
			Message_Serveur("Ce joueur n'accepte pas les chuchotements.", 2);
		} else if (Message[1]=="1") {
			Message_Serveur("Vous recevrez à nouveau les chuchotements des autres joueurs.", 2);
		} else if (Message[1]=="2") {
			Message_Serveur("Vous ne recevrez plus les chuchotements des autres joueurs.", 2);
		}
		return;
	}
	// Info Joueur
	if (Code=="CxINFO") {
		Message_Serveur(Message[1], 2);
		return;
	}
	if (Code=="CxARB") {
		ARBITRE=true;
		if(!(ForumsVisibles & 1)){
		   ForumsVisibles |= 1; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		  _Forum.ListeForum.push("Forum VIP", "Pour les comptes flagués.", "7", "4a24f186.png");//
		 }
		return;
	}
	if (Code == "CxARB+") {
		ARBITREPLUS = true;
		ARBITRE = true;
		if(!(ForumsVisibles & 1)){
		   ForumsVisibles |= 1; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		  _Forum.ListeForum.push("Forum VIP", "Pour les comptes flagués.", "7", "4a24f186.png");//
		 }
		return;
	}
	if (Code=="CxANIM") {
		if(!(ForumsVisibles & 2)){
		   ForumsVisibles |= 2; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		  _Forum.ListeForum.push("Forum Animateurs", "Pour les animateurs.", "9", "b724f1ac.png");//
		 }
		return;
	}


	if (Code=="CxARBREC") {
		ARBITRE=true;
		return;
	}	
	if (Code=="CxARBM") {
		ARBITRE=true;
		MODOFORUM=true;
		if(!(ForumsVisibles & 1)){
		   ForumsVisibles |= 1; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		  _Forum.ListeForum.push("Forum VIP", "Pour les comptes flagués.", "7", "4a24f186.png");//
		 }
		return;
	}	
	if (Code=="CxARBT") {
		VISION=true;
		ARBITRET=true;
		return;
	}
	if (Code=="CxFILMTE") {
		VISION=true;
		FILMEUR=true;
		return;
	}
	if (Code=="CxARBTAB") { // Arbitre tournoi Aaaah! & Bouboum
		ARBITRET=true;
		return;
	}
	if (Code=="CxNARBT") {
		ARBITRET=false;
		if (!(MODO || ARBITRE)) {
			VISION=false;
		}
		return;
	}
	if (Code=="CxFILMT") {
		VISION=true;
		FILMEUR=true;
		return;
	}
	if (Code=="CxNFILMTE") {
		VISION=false;
		FILMEUR=false;
		return;
	}
	if (Code=="CxRESPMF") {
		RESPOMAPFORTO = true;
		return;
	}
	if (Code=="CxFoMM") {
		_ErreurMute.visible=true;
		return;
	}
	if (Code=="CxMODF") {
		MODOFORUM=true;
		if(!(ForumsVisibles & 1)){
		   ForumsVisibles |= 1; // Flags : VIP 1 (0b1), Animateur 2 (0b10), Modo 4 (0b100)
		  _Forum.ListeForum.push("Forum VIP", "Pour les comptes flagués.", "7", "4a24f186.png");//
		 }
		return;
	}
	if (Code=="CxMODFREC") {
		MODOFORUM=true;
		RECRUEFORUM=true;
		return;
	}	
	// Ignore
	if (Code=="CxIg") {
		if (Message.length==1) {
			Message_Serveur("Ce joueur n'existe pas.", 2);
		} else {
			if (Message.length==2) {
				Message_Serveur("Vous ne recevrez plus les messages de "+Message[1]+".", 2);
			} else {
				if (Message.length==3) {
					Message_Serveur("Vous êtes maintenant sur la liste noire de "+Message[1]+".", 1);
				} else {
					if (Message.length==4) {
						Message_Serveur(Message[1]+" n'est plus sur votre liste noire.", 2);
					} else {
						Message_Serveur("Vous n'êtes plus sur la liste noire de "+Message[1]+". Tapez /ignore " +Message[1] + " pour ne plus recevoir ce message.", 1);
					}
				}
			}
		}
		return;
	}
	// Ignore 2
	if (Code=="CxIgn") {
		if (Message.length==2) {
			var NomCible:String=Message[1];
			var NbLN:int=ListeNoire.length;
			var IndexNom:int=ListeNoire.indexOf(NomCible);
			if (IndexNom!=-1) {
				ListeNoire.splice(IndexNom, 1);
			}
			Message_Chat("Vous n'ignorez plus les messages de "+NomCible+".");
		} else {
			if (Message[1]==NomJoueur) {
				ListeNoire.push(Message[2]);
				Message_Serveur("Vous ne recevrez plus les messages de "+Message[2]+" jusqu'à votre prochaine connexion. Un modérateur va être mit au courant de ses propos.", 1);
				Message_Chat("Tapez /ignore "+Message[2]+" pour ne plus ignorer "+Message[2]+".");
			} else {
				if (ListeNoire.indexOf(Message[1])==-1) {
					Message_Serveur(Message[1]+" ne recevra plus vos messages. Un modérateur va être mit au courant de vos propos.", 1);
				}
			}
		}
		return;
	}
	// Déco avec message
	if (Code == "CxDECO") {
		MauvaiseVersion = true;
		Serveur.close();
		_Serveur._Message._Texte.text=Message[1];
		Deconnexion(null);
		return;
	}
	// Ban
	if (Code=="CxBAN") {
		if (Message.length==3) {
			MauvaiseVersion=true;
			var FinBan : String = "";
			var TempsInt : Number = Number(Message[1]);
			if (TempsInt>3600000) {
				var now:Date = new Date();
				now.seconds += int(TempsInt/1000);
				FinBan = " jusqu'au " + now.getDate() + "/" + (now.getMonth()+1) + "/" + now.getFullYear() + " " + now.getHours() + ":" + now.getMinutes();
			}
			if (Message[2]!="") {
				_Serveur._Message._Texte.text="\nVous avez"+messageBan(Message[1])+FinBan+"\nPour la raison suivante :\n"+Message[2];
			} else {
				_Serveur._Message._Texte.text="\nVous avez"+messageBan(Message[1])+FinBan;
			}
			bannir_cookie(Number(Message[1]));
			
		} else {
			Message_Serveur(Message[1] + " a" + messageBan(Message[2]) + (Message[3]=="0"||true?"":" par un arbitre") + "."+(Message[4]!=""?" Raison : "+Message[4]:""),1);
		}
		return;
	}
	// Ban déf
	if (Code=="CxBANC") {
		Connexion_Bannie();
		return;
	}	
	// Demande ban
	if (Code=="CxPB") {

		if (Message.length==1) {
			Message_Serveur("Ce joueur n'existe pas.", 1);
		} else if (Message.length == 2) {
			Message_Serveur("Votre demande a été prise en compte.", 1);
		} else if (Message.length == 3) {
			Message_Serveur(Message[1]+ " a demandé le bannissement de " + Message[2] + ".", 2);

		}
		return;
	}
	//Notification d'un /skip
	if(Code=="CxSkip")
	{
		Message_Serveur(Message[1]+" a fait /skip.", 2);
	}
	// Mute
	if (Code=="CxMUTE") {
		if (Message.length==2) {
			Message_Serveur(Message[1]+" n'a plus le droit de parler sur le chat principal.", 1);
		} else if (Message.length>=3) {
			if (Message[1]=="1") {
				Message_Serveur(Message[2]+" sera ignoré par tout le monde pendant "+Message[3]+" heures.", 2);
			} else {
				Message_Serveur(Message[2]+" n'a plus le droit de parler pendant "+Message[3]+" "+Pluriel("heure",Message[3])+"." + (Message[5]!=""?" Raison : "+Message[5]:""), 1);
			}
		}
		return;
	}
	// Muet
	if (Code=="CxMuet") {
		if (Message.length==1) {
			Message_Serveur("Vous n'avez plus le droit de parler pendant au moins une heure.", 1);
		} else {
			Message_Serveur("Vous n'avez plus le droit de parler sur le chat principal.", 1);
		}
		return;
	}
	// Erreur, partie bouboum pleine
	if (Code=="CxEB") {
		_ListePartie.visible=true;
		Message_Chat("Il n'y a plus de place dans cette partie.");
		return;
	}
	// Erreur, score bouboum trop élevé
	if (Code=="CxES") {
		_ListePartie.visible=true;
		Message_Chat("Votre score ("+Message[1]+") excède la limite du salon ("+Message[2]+").");
		return;
	}
	// Avatar
	if (Code=="CxAV") {
		Avatar=Message[1];
		Chargement_Image(_Profil.Avatar, "I/C/", Avatar, true);
		return;
	}
	// Load
	if (Code=="CxLo") {
		_Forteresse.CouleursPerso[0] = parseInt(Message[2], 16);
		_Forteresse.CouleursPerso[1] = parseInt(Message[3], 16);
		_Forteresse.CouleursPerso[2] = parseInt(Message[4], 16);
		_Forteresse.CouleursPerso[3] = parseInt(Message[5], 16);
		_Forteresse.CouleursPerso[4] = parseInt(Message[6], 16);
		_Forteresse.CouleursPerso[5] = parseInt(Message[7], 16);
		
		_Forteresse.CouleursPersonnalisées = (_Forteresse.CouleursPerso[0] != _Forteresse.CouleursBase[0]
										   || _Forteresse.CouleursPerso[1] != _Forteresse.CouleursBase[1]
										   || _Forteresse.CouleursPerso[2] != _Forteresse.CouleursBase[2]
										   || _Forteresse.CouleursPerso[3] != _Forteresse.CouleursBase[3]
										   || _Forteresse.CouleursPerso[4] != _Forteresse.CouleursBase[4]
										   || _Forteresse.CouleursPerso[5] != _Forteresse.CouleursBase[5]);
		_Forteresse.Chargement_Liste_Case(Message[8].split(""));
		_Forteresse.MapSauvegardable = (Message[9] == "1");
		return;
	}
	
	// Prochaine partie Aaaah
	if (Code=="CxPP") {
		if (_Aaaah.isAfk>1 && !ARBITRET && !FILMEUR) {
			Commandes("afk");
			_Aaaah.isAfk = 0;
		}		
		Nouveau_Message_Chat("<font color='#009D9D'>Prochaine partie dans dix secondes.</font>");				
		return;
	}
	
	// Refus utilisation /afk
	if (Code == "CxNAFK") {
		if (Message.length == 1) {
			Nouveau_Message_Chat("Vous devez attendre plus longtemps avant de pouvoir utiliser cette commande.");
		} else {} // Refus sans msg (arbitres IT par exemple)
		return;
	}
	if (Code == "CxAFK") {
		if (!(!_Aaaah.visible || _Aaaah.Guide == _Aaaah.ClipJoueur))
		  _Aaaah.Quitter_Aaaah(true);
		return;
	}
	if (Code == "CxPDETR") {
		_Afk.visible = false;
		Nouveau_Message_Chat("La partie sur laquelle vous étiez afk n'existe plus.");  
		return;
	}
	
	// Rally
	if (Code=="CxRa") {

		LastRally=getTimer();
		if (Message.length==2) {
			Message_Serveur("Ultra rally de Retiti.",2);
		} else if (Message.length == 1) {
			Message_Serveur("Carte chargée avec succès.",2);
		} else {
			Message_Serveur("Impossible de charger la carte. Assurez-vous d'être le créateur d'un salon Dessin/RallyCross.",2);
		}

		return;
	}
	// Musique
	if (Code=="CxMu") {
		if (SonActif) {
			if (CanalMusique!=null) {
				CanalMusique.stop();
			}
			if (Message.length==2) {
				if (Musique!=null&&Musique.isBuffering) {
					Musique.close();
				}
				Musique=new Sound(new URLRequest("http://www.extinction.fr/minijeux/musique/"+Message[1]+".mp3"));
				CanalMusique=Musique.play();
			}
		}
		return;
	}
	// BDD
	if (Code=="CxBDD") {
		//BDD.Nouvelle_Valeur(int(Message[1]));
		return;
	}

	// Courbe
	if (Code=="CxCou") {
		if (IPS==null) {
			IPS=new $IPS(200,50,48);
			ModuleIPS=true;
			addChild(IPS);
		}
		IPS.Courbe_Spéciale(Message[1]);
		IPS.Rendu();
		return;
	}

	// triche ?
	if (Code=="CxTriche") {
		Envoie_Serveur("CxTriRep#"+_Forteresse.ATCaseDispoCompteur+"#"+_Forteresse.ATIncontrolableCompteur+"#"+_Forteresse.ATCoordCompteur+"#"+_Aaaah.ATIncontrolable2Compteur);
		return;
	}
	// Messagerie
	if (Code=="MPxLE") {
		_Messagerie.visible=true;
		_Messagerie.Afficher_Messages(Message[1],false);		
		return;
	}
	// Messagerie
	if (Code=="MPxLR") {
		_Messagerie.visible=true;
		_Messagerie.Afficher_Messages(Message[1],true);		
		return;
	}
	// Messagerie
	if (Code=="MPxNM") {
		_Messagerie.NouveauMP.Erreur_MP(Message[1]);		
		return;
	}
	// NbMP
	if (Code=="MPxNB") {
		_Interface.NbMP.text= "(" + Message[1] + ")";		
		return;
	}
	// Histo sanctions
	if (Code=="CxHIST") {
		HistoSanctions.visible=true;
		HistoSanctions.Titre.text = "Historique des sanctions";
		HistoSanctions.Afficher_Historique_Sanctions(Message[1], Message[2], Message[3]);		
		
		return;
	}
	
	// Histo signalements 
	if (Code=="CxHSF") {
		HistoSanctions.visible=true;
		HistoSanctions.Titre.text = "Derniers signalements";
		HistoSanctions.Histo.htmlText = "<font size='10'></font>";
		HistoSanctions.Histo.text = Message[1];
		return;
	}

	// Sondage
	if (Code=="CxPoll") {
		if (Message[3] == null) {
			Message[3] = "";
		}
		_Sondage.Init(Message[1],Message[2],Message[3]);
		return;
	}

	// /stat
	if (Code=="CxStats") {
		trace(DATA);
		_Stats.Afficher_Stats(Message[2],Message[1]);
		return;
	}
	
	// /statelo
	if (Code=="CxStatsElo") {
		_StatsElo.Afficher_Stats(Message[2],Message[1]);
		return;
	}


	// Team

	// Fiche de team
	if (Code=="TxF") {
		Message.shift();
		_InfoTeam.Initialisation(Message);
		return;
	}

	// Histo team
	if (Code=="TxHis") {
		HistoSanctions.Afficher_Historique(Message[2],Message[1]);
		return;
	}
	
	// Histo team joueur
	if (Code=="TxHisJ") {
		HistoSanctions.Afficher_Historique_Joueur(Message[2],Message[1]);
		return;
	}
	
	// Membres
	if (Code=="TxM") {
		_InfoTeam.Initialisation_Membres(Number(Message[1]),Message[2]);
		return;
	}

	// Liste des teams
	if (Code=="TxL") {
		_ListeTeam.Initialisation(Message[1]);
		return;
	}

	// Membres online des teams
	if (Code=="TxO") {
		_ListeTeam.MembresOnline(Message[1]);
		return;
	}

	// On quitte la team
	if (Code=="TxQ") {
		LeaveTeam();
		return;
	}

	// On rejoint une team
	if (Code=="TxR") {
		JoinTeam(Number(Message[1]));
		return;
	}

	// On change ses droits
	if (Code=="TxD") {
		ChangeDroits(Message[1]);
		return;
	}

	// Message Team
	if (Code=="TxT") {
		Message_Serveur(Message[1], 4);
		return;
	}
	
	// Message CT
	if (Code=="TxCT") {
		Message_Serveur(Message[1], 4);
		return;
	}
	
	// Message Salon
	if (Code=="CxSalon") {
		Message_Serveur(Message[1], 5);
		return;
	}
	
	// Message Elo
	if (Code=="CxMELO") {
		Message_Serveur(Message[1], 6);
		return;
	}

	// Team créée avec succès
	if (Code=="TxCC") {
		Message_Serveur("Team créée avec succès. L'id de la team est " + Message[1] + ".", 2);
		_NouvelleTeam.visible=false;
		_ListeTeam.visible=false;
		return;
	}

	// Echec création de team
	if (Code=="TxC") {
		_NouvelleTeam.Erreur.visible=true;
		if (Message[1]=="1") {
			_NouvelleTeam.Erreur.text="Nom de team déjà utilisé.";
		} else if (Message[1] == "2") {
			_NouvelleTeam.Erreur.text="Vous ne pouvez pas créer de team avant d'avoir cumulé 30 heures de jeu.";
		} else if (Message[1] == "3") {
			_NouvelleTeam.Erreur.text="Vous ne pouvez pas créer de team car vous en avez déjà créé une il y a moins d'un mois.";
		}
		return;
	}

	// change pwd
	if (Code=="CxCHGPASS") {
		ChgPass.visible = true;		
		return;
	}	
	
	// Echec change pwd
	if (Code=="CxCPWD") {
		ChgPass.Reponse(Message[1]);
		return;
	}	

	// Multi sur un salon
	if (Code=="CxMULTI") {
		var challenge:SharedObject=SharedObject.getLocal("cha");			
		if (Message[2] == "0") { // premier challenge
			challenge.data.rand = uint(Math.random() * 2147483647);
			try{
				var status:String  = challenge.flush();
				if (status == SharedObjectFlushStatus.PENDING){
					Envoie_Serveur(DATA);
				} else {
					Envoie_Serveur("CxMULTI#"+Message[1]+"#"+challenge.data.rand);
				}
			} catch (error:Error){
				Envoie_Serveur(DATA);
			}					
		} else { // deuxième challenge
			if (challenge.data.rand==null || Message[2] == challenge.data.rand) {
				Envoie_Serveur("CxMULTIJOIN#"+Message[1]+"#1"); // même id => on refuse la connexion				
			} else {
				Envoie_Serveur("CxMULTIJOIN#"+Message[1]+"#0");
			}
		}
		return;
	}	
	
	// Signalement
	if (Code=="CxSIM") {
		Signalement.visible = true;
		Signalement.VignetteTexte=Signalement.VignetteTexte+Message[1]+"\n";
		Signalement.NbrSigJeu += 1;
		return;
	}	
	
	// Signalement forum
	if (Code=="CxSIF") {
		Signalement.visible = true;
		Signalement.VignetteTexte=Signalement.VignetteTexte+Message[1]+"\n";
		Signalement.NbrSigForum += 1;
		return;
	}		

	// Activation Salon
	if (Code=="CxSALON") {
		var ModeSalon:Boolean=Message[1]=="1";
		_Interface.Activation_Salon(ModeSalon);
		if (ModeSalon) {
			Message_Serveur("Vous venez d'entrer dans le salon. Seuls les joueurs ayant tapé /salon pourront vous entendre et vous parler.", 2);
		} else {
			Message_Serveur("Vous venez de quitter le salon.", 2);
		}
		return;
	}
	// Edition nom salon
	if (Code=="CxRLP") {
		var CodePartie:int=int(Message[1]);
		var NbPartie:int=ListePartie.length;
		for (var m:int = 0; m<NbPartie; m++) {
			var LaPartie:MovieClip=ListePartie[m];
			if (LaPartie.CodePartie==CodePartie) {
				LaPartie.Nom.text="Edité";
				LaPartie.NomPartie="Edité";
				MAJ_Liste_Partie();
				return;
			}			
		}
		return;
	}
	
	if (Code == "CxDONJOBJ") {
		_AffichageObjetsYshCastle.Init(Message[1]);
		return;
	}

	trace("Code inconnu "+Code);
}

function Changement_Jeu(NUM:int, INFO:String):void {
	ReceptionBouboum=false;
	ReceptionAaaah=false;
	ReceptionForteresse=false;
	_Bouboum.visible=false;
	_Aaaah.visible=false;
	_Forteresse.visible=false;
	_ListePartie.visible=false;
	_ChoixEquipeElo.visible = false;
	_Interface.visible=false;// test
	Inaction=0;
	stage.removeEventListener(KeyboardEvent.KEY_DOWN, Clavier_onPress);
	stage.removeEventListener(KeyboardEvent.KEY_UP, Clavier_onRelease);
	if (NUM==0) {
		//_Interface._ChatSortie.htmlText =  BufferChat;	
		Initialisation_ListePartie(INFO);
		MAJ_Compteur();
		stage.addEventListener(KeyboardEvent.KEY_DOWN, Clavier_onPress);
		stage.addEventListener(KeyboardEvent.KEY_UP, Clavier_onRelease);
		Kikoo=true;
		//_Interface._ChatSortie.scrollV=_Interface._ChatSortie.maxScrollV;		
	} else {
		LastEntréePartie=getTimer(); // antiflood
		if (NUM==1) {
			//_Bouboum._Monde._Texte.htmlText =  BufferChat;				
			ReceptionBouboum=true;
			_Bouboum.visible=true;
			_Bouboum.TempsZero=getTimer();
			stage.addEventListener(KeyboardEvent.KEY_DOWN, _Bouboum.Clavier_onPress);
			stage.addEventListener(KeyboardEvent.KEY_UP, _Bouboum.Clavier_onRelease);
			//stage.addEventListener(Event.ENTER_FRAME, _Bouboum.Mouvement);
			stage.addEventListener(Event.ENTER_FRAME, _Bouboum.Synchronization_Physique);
			//_Bouboum._Monde._Texte.scrollV=_Aaaah._Monde._Texte.maxScrollV;					
		} else if (NUM==2) {
			//_Aaaah._Monde._Texte.htmlText =  BufferChat;				
			ReceptionAaaah=true;
			_Aaaah.visible=true;
			_Aaaah.TempsZero=getTimer();
			stage.addEventListener(KeyboardEvent.KEY_DOWN, _Aaaah.Clavier_onPress);
			stage.addEventListener(KeyboardEvent.KEY_UP, _Aaaah.Clavier_onRelease);
			//_Aaaah._Monde._Texte.scrollV=_Aaaah._Monde._Texte.maxScrollV;
		} else if (NUM==3) {
			//_Forteresse._Monde._Texte.htmlText =  BufferChat;				
			ReceptionForteresse=true;
			_Forteresse.visible=true;
			stage.addEventListener(KeyboardEvent.KEY_DOWN, _Forteresse.Clavier_onPress);
			stage.addEventListener(KeyboardEvent.KEY_UP, _Forteresse.Clavier_onRelease);
			_Forteresse.TempsZero=0;
			_Forteresse.TimerMemoire.start();
			_Forteresse.SourisVisible=false;
			//_Forteresse._Monde._Texte.scrollV=_Aaaah._Monde._Texte.maxScrollV;
		}
	}

}

function Message_Serveur(MESSAGE:String, TYPE:int):void {
	var Chaine:String="";
	if (TYPE==0) {
		Chaine="<font color='#B84DD2' size='13'>[Message serveur] "+MESSAGE+"</font>";
	} else if (TYPE == 1) {
		if (Halloween) {
			Chaine="<font color='#B84DD2'>[Modémoniaques] "+MESSAGE+"</font>";
		} else {
			Chaine="<font color='#B84DD2'>[Modération] "+MESSAGE+"</font>";
		}
	} else if (TYPE == 2) {
		Chaine=MESSAGE;
	} else if (TYPE == 3) {
		Chaine="<font color='#009D9D'>[Bot] "+MESSAGE+"</font>";
	} else if (TYPE == 4) {//team
		Chaine="<font color='#68EDAD'>"+MESSAGE+"</font>";
	} else if (TYPE == 5) {//salon
		Chaine="<font color='#77B5FE'>"+MESSAGE+"</font>";		
	} else if (TYPE == 6) {
		Chaine = "<font color='#FFD5EA'>[Elo] "+MESSAGE+"</font>";
	} else if (TYPE == -1) {
		Chaine="<font color='#ED67EA'>[MP] "+MESSAGE+"</font>";
	} else if (TYPE == -2) {
		Chaine="<font color='#E4C76D'>[VIP]</font> <font color='#7BA3CA'>"+MESSAGE+"</font>";
	} else if (TYPE == -3) {
		Chaine="<font color='#E491EE'>[AV] "+MESSAGE+"</font>";
	} else if (TYPE == -4) {
		Chaine="<font color='#02A0DE'>[F] "+MESSAGE+"</font>";
	}

	if (_Aaaah.visible) {
		_Aaaah.Nouveau_Message_Chat(Chaine);
	} else if (_Bouboum.visible) {
		_Bouboum.Nouveau_Message_Chat(Chaine);
	} else if (_Forteresse.visible) {
		_Forteresse.Nouveau_Message_Chat(Chaine);
	} else {
		Message_Chat(Chaine);
	}
}

// Date
var MoisTexte:Array=new Array("Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Août","Septembre","Octobre","Novembre","Décembre");
function Formatage_Date(DATE:String) {
	if (DATE==""||DATE==null) {
		return "";
	}
	//                                                                        
	var NouvelleDate:Date = new Date();
	NouvelleDate.setTime(int(DATE)*1000);// correction temporaire de l'heure incorrecte 13860000
	//
	var Annee=NouvelleDate.getFullYear();
	var Mois:int=NouvelleDate.getMonth();
	var Jours:int=NouvelleDate.getDate();
	var Heure:int=NouvelleDate.getHours();
	var Minute=NouvelleDate.getMinutes();
	//
	if (Minute<10) {
		Minute="0"+Minute;
	}
	// Année                                                                                                                                                                                                                                                                                                       
	if (Annee==DateServeur.getFullYear()) {
		Annee="";
	} else {
		Annee=" "+Annee;
	}
	//
	if (DateServeur.getMonth()==Number(Mois)&&Annee=="") {
		if (DateServeur.getDate()==Number(Jours)) {
			return "aujourd'hui à " + Heure + ":" + Minute;
		} else {
			if (DateServeur.getDate()-1==Number(Jours)) {
				return "hier à " + Heure + ":" + Minute;
			} else {
				return "le " + Jours + " " + MoisTexte[Mois] + Annee;
			}
		}
	} else {
		return "le " + Jours + " " + MoisTexte[Mois] + Annee;
	}
}

function messageBan(Temps : String) {
	var TempsBan:Number = (Number(Temps)/3600000);
	if (TempsBan==0) {
		return " été éjecté du jeu";
	} else {
		if (TempsBan==1) {
			return " été banni pendant 1 heure";
		} else {
			return " été banni pendant " + TempsBan + " heures";
		}
	}
}

var cookie:SharedObject=SharedObject.getLocal("inf");

function est_banni():Boolean {

	if (cookie.data.limiteban==null) {
		return false;
	}

	
	var limite:Number=cookie.data.limiteban;
	var now:Date = new Date();
	var datelimite:Date = new Date();
	datelimite.setTime(limite);
	if (datelimite>now) {
		return true;
	}
	return false;
}


function bannir_cookie(temps:Number):void {
	if (temps>0) {
		var now:Date = new Date();
		var limiteban:Number=now.getTime()+temps;
		cookie.data.limiteban=limiteban;
		try{
			var status:String  = cookie.flush();
			if (status == SharedObjectFlushStatus.PENDING){
				Envoie_Serveur("CxCtn#1");
			} else {
				//Envoie_Serveur("CxCtn#0");
			}
		} catch (error:Error){
			Envoie_Serveur("CxCtn#2");
		}		
		
	}
}

function JoinTeam(IdT : Number):void {
	TEAM=true;
	IdTeam=IdT;
	_Interface.MaTeam.visible=true;
	_Interface.MembresOnline.visible=true;
	if (!MODOTEAM) {
		_Forum.ListeForum.push("Forum Team", "Votre topic team et ceux des teams en fusion avec la vôtre.", "11", "6524f115.jpg");
	}
}

function LeaveTeam():void {
	TEAM=false;
	IdTeam=0;
	_Interface.MaTeam.visible=false;
	_Interface.MembresOnline.visible=false;
	
	for(var i:int = 2; i < _Forum.ListeForum.length; i += 4) {
		if (_Forum.ListeForum[i] == "11") {
			_Forum.ListeForum.splice(i - 2, 4);
		}
	}

	LEADER=false;
	RECRUTEUR=false;
	SCRIBE = false;
}

function ChangeDroits(Droits : String):void {
	LEADER = (Droits == "2");
	RECRUTEUR = (Droits == "1");
	SCRIBE = (Droits == "3");
}

var CodeCSH:int;
var CSHT0:int;
function Boucle_CSH(E:Event):void {
	if (getTimer()-CSHT0>11000) {
		removeEventListener(Event.ENTER_FRAME, Boucle_CSH);
		Envoie_Serveur("CxHS#"+CodeCSH);
	}
}

var myContextMenu:ContextMenu = new ContextMenu();
this.contextMenu = myContextMenu;

myContextMenu.addEventListener(ContextMenuEvent.MENU_SELECT,AntiTriche);
function AntiTriche(e:ContextMenuEvent):void {
	if (_Aaaah.visible && _Aaaah.Guide!= null && NomJoueur == _Aaaah.Guide.Nom.text){	
		_Aaaah.PosX = mouseX;
		_Aaaah.PosY = mouseY;
		Envoie_Serveur("DeS#"+_Aaaah.PosX+"#"+_Aaaah.PosY);				
	}
}