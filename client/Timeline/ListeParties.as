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

var ListePartie:Array;
var ClipListePartie:MovieClip=_ListePartie._Liste._Liste2;
var ClipListeAmis:MovieClip=_FenetreAmis._ListeAmis._ListeAmis2;
var ClipListeNoire:MovieClip=_FenetreLN._ListeNoire._ListeNoire2;
var ClipListeJoueurSalon:MovieClip=_ListePartie._ListeJoueur._ListeJoueurSalon._ListeJoueurSalon2;
var CliquePartieEnCours:MovieClip;
var TriParties:int=0; // 0 = pas de tri, 1 = par jeu, 2 = par nom partie, 3 = par joueurs


var JoueurForum:int=0;
var JoueurChatPrincipal:int=0;

_ListePartie._MotDePasse.Texte.restrict=Restriction;

Navigateur(_ListePartie.Ascenseur, _ListePartie._Liste._Liste2, 350, 350, 40, _ListePartie._Liste);
Navigateur(_Interface.Ascenseur2, _Interface._Liste._Liste2, 192, 192, 0);
Navigateur(_ListePartie._ListeJoueur.AscenseurSalon, _ListePartie._ListeJoueur._ListeJoueurSalon._ListeJoueurSalon2, 270, 270, 50, _ListePartie._ListeJoueur);
Navigateur(_FenetreAmis.Ascenseur, _FenetreAmis._ListeAmis._ListeAmis2, 290, 290, 50, _FenetreAmis);
Navigateur(_FenetreLN.Ascenseur, _FenetreLN._ListeNoire._ListeNoire2, 300, 300, 50, _FenetreLN);

function MAJ_Compteur():void {
	var NbA:int=0;
	var NbB:int=0;
	var NbF:int=0;
	var Nb:int=ListePartie.length;
	for (var i:int = 0; i<Nb; i++) {
		var Partie:MovieClip=ListePartie[i];
		if (Partie.CodeJeu==1) {
			NbB+=int(Partie.Joueur.text);
		} else {
			if (Partie.CodeJeu==2) {
				NbA+=int(Partie.Joueur.text);
			} else {
				if (Partie.CodeJeu==3) {
					NbF+=int(Partie.Joueur.text);
				}
			}
		}
	}
	//
	_ListePartie._Jouer.NA.text="("+NbA+")";
	_ListePartie._Jouer.NB.text="("+NbB+")";
	_ListePartie._Jouer.NF.text="("+NbF+")";
	MAJ_Liste_Partie();
}

// LISTE DES PARTIES
function Initialisation_ListePartie(INFO:String):void {
	_Interface.ChatV2._ChatSortie.htmlText = TexteEnCours;
	_Interface.ChatV2._ChatSortie.scrollV = _Interface.ChatV2._ChatSortie.maxScrollV;

	while (ClipListePartie.numChildren != 0) {
		ClipListePartie.removeChildAt(0);
	}
	while (ClipListeJoueurInterface.numChildren != 0) {
		ClipListeJoueurInterface.removeChildAt(0);
	}
	ListePartie = new Array();
	ListeJoueurPartie = new Array();
	//
	var LsPartie:Array=INFO.split($+$);
	var LsJoueur:Array=LsPartie.shift().split(";");
	var NbJoueur:int=LsJoueur.length;
	for (var i:int = 1; i<NbJoueur; i++) {
		var InfoJoueur:Array=LsJoueur[i].split(",");
		Nouveau_Joueur(InfoJoueur[0], InfoJoueur[1] == "1", (Saturnaaaahles ? InfoJoueur[2] == "1" : "0"));
	}
	_Interface.visible=true;
	MAJ_Liste_Joueur();
	Ascenseur_Reset(_Interface.Ascenseur2);
	//
	var NbPartie:int=LsPartie.length;
	for (var k:int = 0; k<NbPartie; k++) {
		var InfoPartie:Array=LsPartie[k].split($);
		Nouvelle_Partie(InfoPartie[0], InfoPartie[1], InfoPartie[2], InfoPartie[3], InfoPartie[4] == "1", InfoPartie[5], InfoPartie[6]);
	}
	//
	MAJ_Liste_Partie();
	Ascenseur_Reset(_ListePartie.Ascenseur);
	//
	_ListePartie._MotDePasse.visible=false;
	_ListePartie._NouvellePartie.visible=false;
	_ListePartie.visible=true;
}

function Nouveau_Joueur(NOM:String, SUR_LE_FORUM:Boolean, SURVIVALKICK:Boolean):void {
	var Nom:String=NOM;
	var Joueur:MovieClip = new $JoueurBase();
	var Texte:String;
	if (NomJoueur==Nom) {
		Texte="<font color='#009D9D'>";
		Joueur.ClipJoueur=true;
	} else {
		if (Saturnaaaahles && !SURVIVALKICK) {
			Texte="<font color='#7C7E9E'>";
		} else {
			Texte="";
		}
		Joueur.ClipJoueur=false;
	}
	Joueur.NomJoueur=Nom;
	Joueur.Score=0;
	Joueur._Texte.htmlText=Texte+Nom;
	ClipListeJoueurInterface.addChild(Joueur);
	Joueur._F = new $IconeForum();
	Joueur.addChild(Joueur._F);
	Joueur._F.x=133;
	Joueur._F.y=3;
	Joueur._F.visible=SUR_LE_FORUM;
	ListeJoueurPartie.push(Joueur);
	JoueurChatPrincipal++;
	MAJ_Compteur_CP();
}

function MAJ_Compteur_CP():void {
	_ListePartie._Jouer.JCP.text="("+JoueurChatPrincipal+")";
	_ListePartie._Jouer.JF.text="("+JoueurForum+")";
}

function Suppr_Joueur(NOM:String):void {
	var NbJoueur:int=ListeJoueurPartie.length;
	for (var i:int = 0; i<NbJoueur; i++) {
		var Joueur:MovieClip=ListeJoueurPartie[i];
		if (Joueur.NomJoueur==NOM) {
			ListeJoueurPartie.splice(i, 1);
			ClipListeJoueurInterface.removeChild(Joueur);
			MAJ_Liste_Joueur();
			return;
		}
	}
}

function MAJ_Icone_Forum(NOM:String, ICONE:Boolean):void {
	JoueurForum=0;
	JoueurChatPrincipal=0;
	var NbJoueur:int=ListeJoueurPartie.length;
	for (var i:int = 0; i<NbJoueur; i++) {
		var Joueur:MovieClip=ListeJoueurPartie[i];
		if (Joueur.NomJoueur==NOM) {
			Joueur._F.visible=ICONE;
		}
		if (Joueur._F.visible) {
			JoueurForum++;
		} else {
			JoueurChatPrincipal++;
		}
	}
	MAJ_Compteur_CP();
}

function MAJ_SurvivalKick(NOM:String):void {
	var NbJoueur:int=ListeJoueurPartie.length;
	for (var i:int = 0; i<NbJoueur; i++) {
		var Joueur:MovieClip=ListeJoueurPartie[i];
		if (Joueur.NomJoueur==NOM && Joueur.NomJoueur != NomJoueur) {
			if (Joueur._Texte.htmlText.indexOf("#7C7E9E") != -1) {
				Joueur._Texte.htmlText="<font color='#C2C2DA'>" + Joueur.NomJoueur;
			} else {
				Joueur._Texte.htmlText="<font color='#7C7E9E'>" + Joueur.NomJoueur;
			}
		}
	} 
}

function Code_Partie(CODE:int):MovieClip {
	var NbPartie:int=ListePartie.length;
	for (var i:int = 0; i<NbPartie; i++) {
		var Partie:MovieClip=ListePartie[i];
		if (Partie.CodePartie==CODE) {
			return Partie;
		}
	}
	return null;
}

function NomJeu(JEU:int):String {
	if (JEU==1) {
		return "Bouboum";
	} else if (JEU == 2) {
		return "Aaaah";
	} else {
		return "Forteresse";
	}
}

function Nouvelle_Partie(CODE:int, JEU:int, NOM:String, NB_JOUEUR:int, CADENAS:Boolean, CODE_COULEUR:int, TYPE:String):void {
	var Partie:MovieClip = new $Partie();
	Partie.Fond.gotoAndStop(1);
	ListePartie.push(Partie);
	Partie.CodePartie=CODE;
	Partie.CodeJeu=JEU;
	Partie.JeuPartie=NomJeu(JEU) + " " + (TYPE!=null&&TYPE!="null"&&TYPE!="-"?TYPE:"");//(TYPE!=null&&TYPE!="null"&&TYPE!="-"&&CADENAS?TYPE:"");
	Partie.Jeu.text = Partie.JeuPartie;
	Partie.Nom.text=NOM;
	Partie.NomPartie=NOM;
	Partie.Joueur.text=NB_JOUEUR;
	Partie.NbJoueur=NB_JOUEUR;
	Partie.Cadenas.visible=CADENAS;
	//
	MAJ_Couleur_Partie(Partie, CODE_COULEUR);
	//
	Partie.addEventListener(MouseEvent.MOUSE_OVER, Partie_Allumage);
	Partie.addEventListener(MouseEvent.MOUSE_OUT, Partie_DeAllumage);
	Partie.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Partie);
}

function MAJ_Couleur_Partie(PARTIE:MovieClip, CODE:int):void {
	if (CODE==1 && MODO) {
		PARTIE.Jeu.textColor=0xED67EA; // rose MP pour admin
		PARTIE.Joueur.textColor=0xED67EA;
		PARTIE.Nom.textColor=0xED67EA;
	} else if (CODE == 2 && MODO) {
		PARTIE.Jeu.textColor=0xE491EE; // rose AV pour modo
		PARTIE.Joueur.textColor=0xE491EE;
		PARTIE.Nom.textColor=0xE491EE;
	} else if (CODE==3 && MODO) { // vert perso pour arbitre
		PARTIE.Jeu.textColor=0x009D9D;
		PARTIE.Joueur.textColor=0x009D9D;
		PARTIE.Nom.textColor=0x009D9D;
	} else if (CODE == 4) { // bleu pour coéquipier
		PARTIE.Jeu.textColor=0x6C77C1;
		PARTIE.Joueur.textColor=0x6C77C1;
		PARTIE.Nom.textColor=0x6C77C1;
	} else if (CODE == 5) { // gris pour ami
		PARTIE.Jeu.textColor=0x7C7E9E;
		PARTIE.Joueur.textColor=0x7C7E9E;
		PARTIE.Nom.textColor=0x7C7E9E;

	} else { // blanc
		PARTIE.Jeu.textColor=0xC2C2DA;
		PARTIE.Joueur.textColor=0xC2C2DA;
		PARTIE.Nom.textColor=0xC2C2DA;
	}
}

function Suppression_Partie(PARTIE:MovieClip):void {
	if (PARTIE!=null&&ClipListePartie.contains(PARTIE)) {
		ClipListePartie.removeChild(PARTIE);
	}
	var NbPartie:int=ListePartie.length;
	for (var i:int = 0; i<NbPartie; i++) {
		var Partie:MovieClip=ListePartie[i];
		if (Partie==PARTIE) {
			ListePartie.splice(i, 1);
			break;
		}
	}
	MAJ_Liste_Partie();
}

_ListePartie.TriJoueurs.addEventListener(MouseEvent.MOUSE_DOWN, Clique_TriJoueurs);
function Clique_TriJoueurs(E:MouseEvent):void {
	TriParties = (TriParties == 3 ? 0 : 3);
	MAJ_Liste_Partie();
}

_ListePartie.TriNomPartie.addEventListener(MouseEvent.MOUSE_DOWN, Clique_TriNomPartie);
function Clique_TriNomPartie(E:MouseEvent):void {
	TriParties = (TriParties == 2 ? 0 : 2);
	MAJ_Liste_Partie();
}

_ListePartie.TriJeu.addEventListener(MouseEvent.MOUSE_DOWN, Clique_TriJeu);
function Clique_TriJeu(E:MouseEvent):void {
	TriParties = (TriParties == 1 ? 0 : 1);
	MAJ_Liste_Partie();
}

function MAJ_Liste_Partie():void {
	while (ClipListePartie.numChildren != 0) {
		ClipListePartie.removeChildAt(0);
	}
	var Niveau:int=0;
	if (TriParties==3) {
		ListePartie.sortOn(["NbJoueur", "CodePartie"], Array.DESCENDING | Array.NUMERIC);
	}else if (TriParties==2) {
		ListePartie.sortOn(["NomPartie", "CodePartie"], Array.CASEINSENSITIVE);
	}else if (TriParties==1) {
		ListePartie.sortOn(["JeuPartie", "CodePartie"]);
	}else {
		//ListePartie.sortOn(["CodePartie"], Array.NUMERIC);
	}
	var NbPartie:int=ListePartie.length;
	for (var i:int = 0; i<NbPartie; i++) {
		var Partie:MovieClip=ListePartie[i];
		if (Partie.CodeJeu==1&&_ListePartie._Jouer.S_Bouboum.Encoche.E1.visible||Partie.CodeJeu==2&&_ListePartie._Jouer.S_Aaaah.Encoche.E1.visible||Partie.CodeJeu==3&&_ListePartie._Jouer.S_Forteresse.Encoche.E1.visible) {
			if (! Partie.Cadenas.visible||_ListePartie._Jouer.S_Prive.Encoche.E1.visible) {
				ClipListePartie.addChild(Partie);
				Partie.y=18*Niveau;
				Niveau++;
			}

		}
	}
	Ascenseur_MAJ(_ListePartie.Ascenseur);
}

// LISTE DES JOUEURS DU SALON
var TempsPremierClic:int=0;
var EcartTemps:int=300;
var SimpleClic:Timer=new Timer(EcartTemps);
SimpleClic.addEventListener(TimerEvent.TIMER, DetectionClic);
_ListePartie._ListeJoueur.visible=false;

_ListePartie._ListeJoueur.Retour.addEventListener(MouseEvent.MOUSE_DOWN,Afficher_Jouer);
function Afficher_Jouer(E:MouseEvent):void {
	_ListePartie._Jouer.visible=true;
	_ListePartie._ListeJoueur.visible=false;
}

_ListePartie._ListeJoueur.Rejoindre.addEventListener(MouseEvent.MOUSE_DOWN, Rejoindre_Partie);


function Clique_Partie(E:MouseEvent):void {
	var PartieCible:MovieClip=MovieClip(E.currentTarget);
	if (PartieCible!=CliquePartieEnCours||getTimer()-TempsPremierClic>EcartTemps) {//
		//premier clic
		CliquePartieEnCours=PartieCible;
		Envoie_Serveur("CxG#"+CliquePartieEnCours.CodePartie);
		_ListePartie._ListeJoueur.NomPartie.text=CliquePartieEnCours.Nom.text;
		Inaction=0;
		TempsPremierClic=getTimer();
		SimpleClic.start();
		//Vignette_Menu(new Array("Rejoindre cette partie", Rejoindre_Partie, null));
	} else {
		// double clic
		TempsPremierClic=0;
		Rejoindre_Partie(null);
	}

}


function DetectionClic(E:Event):void {
	//if (TempsPremierClic != 0) {
	// simple clic
	//trace("CxG#"+CliquePartieEnCours.CodePartie);
	//Envoie_Serveur("CxG#"+CliquePartieEnCours.CodePartie);
	//_ListePartie._ListeJoueur.NomPartie.text = CliquePartieEnCours.Nom.text;
	//}
	SimpleClic.stop();
	SimpleClic.reset();
}

function MAJ_ListeJoueurSalon(InfoListe : Array):void {
	while (ClipListeJoueurSalon.numChildren != 0) {
		ClipListeJoueurSalon.removeChildAt(0);
	}

	if (_ListePartie.visible) {
		_ListePartie._Jouer.visible=false;
		_ListePartie._ListeJoueur.visible=true;
	}

	var ListeJoueurSalon:Array = new Array();
	for (var i:int = 0; i < InfoListe.length; i++) {
		var JoueurSalon:MovieClip = new $JoueurSalon();
		ClipListeJoueurSalon.addChild(JoueurSalon);
		JoueurSalon.y=18*i;
		if (Saturnaaaahles || Halloween) {
			var InfoSupp:Array = InfoListe[i].split(";");
			JoueurSalon.LeJoueur.text = InfoSupp[0];
			if (InfoSupp[1] == "0" && !Halloween) {
				JoueurSalon.LeJoueur.textColor = 0x7C7E9E;
			}
		} else {
			JoueurSalon.LeJoueur.text=InfoListe[i];
		}
	}
	Ascenseur_Reset(_ListePartie._ListeJoueur.AscenseurSalon);

}
var LastEntréePartie:Number = -99999999;
var TempsAntiFlood : int = 7000;

function FloodSalon() : Boolean {
	if (getTimer() - LastEntréePartie < TempsAntiFlood)  {
		Message_Serveur("Veuillez patienter quelques secondes avant de rejoindre à nouveau une partie.", 2);
		return true;
	}
	return false;
}

function Rejoindre_Partie(E:Event = null):void {
	if (! _ListePartie.visible) {
		Message_Serveur("Vous devez quitter votre salon avant de rejoindre une partie.", 2);
		return;
	}
	if (FloodSalon()){
		return;
	}
	_InfoTeam.visible=false;
	_ListeTeam.visible=false;
	_FenetreAmis.visible=false;
	Afficher_Jouer(null);
	if (CliquePartieEnCours.Cadenas.visible) {
		_ListePartie._MotDePasse.visible=true;
		_ListePartie._MotDePasse.Valider.visible=true;
	} else {
		_ListePartie.visible=false;
		Envoie_Serveur("CxR#"+CliquePartieEnCours.CodePartie);
	}
}

function MAJ_Liste_Joueur():void {
	var Classement:Array = new Array();
	//
	var NbJoueur:int=ListeJoueurPartie.length;
	for (var i:int = 0; i<NbJoueur; i++) {
		var Joueur:MovieClip=ListeJoueurPartie[i];
		Joueur.y = i*21;
		//
		if (! Joueur._Texte.hasEventListener(MouseEvent.MOUSE_DOWN)) {
			Joueur._Texte.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Joueur);
		}
	}
	//
	Ascenseur_MAJ(_Interface.Ascenseur2);
	MAJ_Icone_Forum(null, false);
}

_ListePartie._Jouer.BtnNouvellePartie.addEventListener(MouseEvent.MOUSE_DOWN, Clique_NouvellePartie);
function Clique_NouvellePartie(E:Event):void {
	/*
	_ListePartie._NouvellePartie.CodeJeu = "2";
	_ListePartie._NouvellePartie.JeuEnCours = _ListePartie._NouvellePartie.BtnAaaah;
	_ListePartie._NouvellePartie.BtnAaaah.Encoche.E1.visible = true;
	_ListePartie._NouvellePartie.BtnAaaah.Encoche.E0.visible = false;
	_ListePartie._NouvellePartie.BtnBouboum.Encoche.E1.visible = false;
	_ListePartie._NouvellePartie.BtnBouboum.Encoche.E0.visible = true;
	_ListePartie._NouvellePartie.BtnForteresse.Encoche.E1.visible = false;
	_ListePartie._NouvellePartie.BtnForteresse.Encoche.E0.visible = true;
	*/
	_ListePartie._NouvellePartie.visible=true;
}

_ListePartie._NouvellePartie.Création.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Création);
function Clique_Création(E:Event):void {
	if (_ListePartie._NouvellePartie.NomPartie.text!="") {
		
		var PartieElo:Boolean = _ListePartie._NouvellePartie.BtnRanked.Encoche.E1.visible;
		if (PartieElo && (_ListePartie._NouvellePartie.MotDePasse.text == "" || _ListePartie._NouvellePartie.ArbitreElo.PseudoArbitreElo.text == "")) {
			_ListePartie._NouvellePartie.AvManqueInfoElo.visible = true;
			return;
		} else {
			_ListePartie._NouvellePartie.AvManqueInfoElo.visible = false;
		}

		if (_ListePartie._NouvellePartie.OptionsForteresse.TempsPartie.text=="") {
			_ListePartie._NouvellePartie.OptionsForteresse.TempsPartie.text=10;
		}
		if (_ListePartie._NouvellePartie.OptionsBoum.ScoreMax.text=="") {
			_ListePartie._NouvellePartie.OptionsBoum.ScoreMax.text=100000;
		}
		if (_ListePartie._NouvellePartie.OptionsBoum.JoueursMax.text=="") {
			_ListePartie._NouvellePartie.OptionsBoum.JoueursMax.text=22;
		}
		_ListePartie.visible=false;
		var AutoChoixEquipe:String=_ListePartie._NouvellePartie.OptionsForteresse.BtnAutoChoixEquipe.Encoche.E1.visible?"1":"0";
		var Dessin:String="1";//_ListePartie._NouvellePartie.OptionsForteresse.BtnDessin.Encoche.E1.visible?"1":"0";
		var ActiverConta:String=_ListePartie._NouvellePartie.OptionsAaaah.ActiverConta.Encoche.E1.visible?"1":"0";
		var ActiverFight:String= _ListePartie._NouvellePartie.OptionsAaaah.CartesSpe.Encoche.E1.visible?
							(_ListePartie._NouvellePartie.OptionsAaaah.Mode.GetFlag()=="4" ? "0" : "1"):"1"; //0=true, 1=false
		//_ListePartie._NouvellePartie.OptionsAaaah.ActiverFight.Encoche.E1.visible?"0":"1";
		var SalonKill:String=_ListePartie._NouvellePartie.OptionsBoum.SalonKill.Encoche.E1.visible?"0":"1";
		var CartesSpe:String=_ListePartie._NouvellePartie.OptionsAaaah.CartesSpe.Encoche.E1.visible?
							(_ListePartie._NouvellePartie.OptionsAaaah.Mode.GetFlag()):"0";
		
		var ModeForto:String = _ListePartie._NouvellePartie.OptionsForteresse.BtnMode.Encoche.E1.visible?
							(_ListePartie._NouvellePartie.OptionsForteresse.ModeForto.GetFlag()):"0";
		
		var ArbitreElo:String = (PartieElo ? _ListePartie._NouvellePartie.ArbitreElo.PseudoArbitreElo.text : "-");
		
		var MatchTeam:String = (PartieElo &&
								((_ListePartie._NouvellePartie.OptionsAaaah.MatchTeam.Encoche.E1.visible && _ListePartie._NouvellePartie.estPartieAaaah())
								 || (_ListePartie._NouvellePartie.OptionsBoum.MatchTeam.Encoche.E1.visible && _ListePartie._NouvellePartie.estPartieBoum())
								 || (_ListePartie._NouvellePartie.OptionsForteresse.MatchTeam.Encoche.E1.visible && _ListePartie._NouvellePartie.estPartieForteresse()))
								 ? "1" : "0");
		
		Envoie_Serveur("CxP#"+_ListePartie._NouvellePartie.CodeJeu+"#"+_ListePartie._NouvellePartie.NomPartie.text
					   +"#"+_ListePartie._NouvellePartie.OptionsForteresse.TempsPartie.text
					   +"#"+AutoChoixEquipe+"#"+Dessin+"#"+_ListePartie._NouvellePartie.OptionsBoum.ScoreMax.text
					   +"#"+ActiverConta+"#"+_ListePartie._NouvellePartie.OptionsBoum.JoueursMax.text
					   +"#"+ActiverFight+"#"+CartesSpe+"#"+SalonKill+"#"+ModeForto+"#"+ArbitreElo+
					   "#"+_ListePartie._NouvellePartie.GuideElo.PseudoGuideElo.text+"#"+MatchTeam
					   +"#"+_ListePartie._NouvellePartie.MotDePasse.text);
	}
}


function Partie_Allumage(E:MouseEvent):void {
	var Partie:MovieClip=MovieClip(E.currentTarget);
	Partie.Fond.gotoAndPlay(2);
}
function Partie_DeAllumage(E:MouseEvent):void {
	var Partie:MovieClip=MovieClip(E.currentTarget);
	Partie.Fond.gotoAndPlay(22);
}

_ListePartie._Jouer.S_Bouboum.Encoche.E0.visible=false;
_ListePartie._Jouer.S_Aaaah.Encoche.E0.visible=false;
_ListePartie._Jouer.S_Forteresse.Encoche.E0.visible=false;
_ListePartie._Jouer.S_Prive.Encoche.E0.visible=false;
_ListePartie._Jouer.S_Bouboum.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Sélection);
_ListePartie._Jouer.S_Aaaah.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Sélection);
_ListePartie._Jouer.S_Forteresse.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Sélection);
_ListePartie._Jouer.S_Prive.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Sélection);
function Clique_Sélection(E:MouseEvent):void {
	var Sélecteur:MovieClip=MovieClip(E.currentTarget);
	if (Sélecteur.Encoche.E0.visible) {
		Sélecteur.Encoche.E0.visible=false;
		Sélecteur.Encoche.E1.visible=true;
	} else {
		Sélecteur.Encoche.E0.visible=true;
		Sélecteur.Encoche.E1.visible=false;
	}
	MAJ_Liste_Partie();
}

_ListePartie._Jouer.P_Bouboum.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Partie_Bouboum);
function Clique_Partie_Bouboum(E:MouseEvent):void {
	if (FloodSalon()){
		return;
	}
	_ListePartie.visible=false;
	Envoie_Serveur("CxJ#1");
}
_ListePartie._Jouer.P_Aaaah.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Partie_Aaaah);
function Clique_Partie_Aaaah(E:MouseEvent):void {
	if (FloodSalon()){
		return;
	}
	_ListePartie.visible=false;
	Envoie_Serveur("CxJ#2");
}
_ListePartie._Jouer.P_Forteresse.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Partie_Forteresse);
function Clique_Partie_Forteresse(E:MouseEvent):void {
	if (FloodSalon()){
		return;
	}
	_ListePartie.visible=false;
	Envoie_Serveur("CxJ#3");
}

// LISTE D'AMIS
var AmisEnLigne:Array;
var AmisHorsLigne:Array;

function Initialisation_ListeAmis(INFO:String, LISTE_AMIS: Boolean):void {
	_FenetreAmis.visible=true;
	_FenetreAmis.ListeVide.visible=false;

	while (ClipListeAmis.numChildren != 0) {
		ClipListeAmis.removeChildAt(0);
	}

	AmisEnLigne = new Array();
	AmisHorsLigne = new Array();

	var LsAmis:Array=INFO.split($+$);

	for (var k:int = 1; k<LsAmis.length; k++) {
		var InfoAmi:Array=LsAmis[k].split($);
		var Ami:MovieClip = new $Ami();
		Ami.Fond.gotoAndStop(1);
		Ami.Cadenas.visible=false;
		Ami.Rejoindre.visible=false;
		var Nom:String=InfoAmi[0];
		Ami.NomAmi.htmlText=Nom.substr(0,1).toUpperCase()+Nom.substr(1);
		var Activite:int=InfoAmi[1];
		if (Activite!=0) {
			Ami.NomAmi.htmlText = "<font color='#009D9D'>" + Ami.NomAmi.text + "</font>";
			AmisEnLigne.push(Ami);
			if (Activite>0) {
				Ami.NomJeu.text=NomJeu(Activite);
				Ami.NomSalon.text=InfoAmi[2];
				Ami.Rejoindre.addEventListener(MouseEvent.CLICK, Rejoindre_Ami);
				Ami.Rejoindre.buttonMode=true;
				Ami.Rejoindre.visible=_ListePartie.visible;
				Ami.Cadenas.visible=InfoAmi[4]=="1";
				Ami.CodePartie=InfoAmi[3];
			} else {
				if (Activite==-1) {
					Ami.NomJeu.text="Chat principal";
				} else if (Activite==-2) {
					Ami.NomJeu.text="Salon principal";
				} else if (Activite==-3) {
					Ami.NomJeu.text="Afk";
				} else if (Activite==-4) {
					Ami.NomJeu.text="Forum";
				}
				Ami.NomSalon.text="-";
			}
		} else {
			AmisHorsLigne.push(Ami);
			Ami.NomJeu.text="Hors Ligne";
			Ami.NomSalon.text="-";
		}
		Ami.addEventListener(MouseEvent.MOUSE_OVER, Partie_Allumage);
		Ami.addEventListener(MouseEvent.MOUSE_OUT, Partie_DeAllumage);
		if (LISTE_AMIS) {
			Ami.Supprimer.addEventListener(MouseEvent.CLICK, Supprimer_Ami);
			Ami.Supprimer.buttonMode=true;
		} else {
			Ami.Supprimer.visible=false;
		}

		ClipListeAmis.addChild(Ami);
		Ami.x=15;
	}

	MAJ_ListeAmis();
	Ascenseur_Reset(_FenetreAmis.Ascenseur);
}

function MAJ_ListeAmis() {
	if (AmisEnLigne.length+AmisHorsLigne.length==0) {
		_FenetreAmis.ListeVide.visible=true;
	}

	AmisEnLigne.sort(CompareNom);
	AmisHorsLigne.sort(CompareNom);

	var i:int;
	for (i=0; i<AmisEnLigne.length; i++) {
		AmisEnLigne[i].y=18*i;
	}
	for (i=0; i<AmisHorsLigne.length; i++) {
		AmisHorsLigne[i].y = 18*(AmisEnLigne.length+i);
	}



}
function CompareNom(a:$Ami, b:$Ami):int {
	if (a.NomAmi.text<b.NomAmi.text) {
		return -1;
	} else if (a.NomAmi.text == b.NomAmi.text) {
		return 0;
	} else {
		return 1;
	}
}

function Supprimer_Ami(E:Event):void {
	var LeJoueur:MovieClip=E.currentTarget.parent;
	var Nom:String=LeJoueur.NomAmi.text;
	Envoie_Serveur("CxAS#"+Nom);
	var index:int;
	if ((index = AmisEnLigne.indexOf(LeJoueur)) != -1) {
		AmisEnLigne.splice(index,1);
	} else if ((index = AmisHorsLigne.indexOf(LeJoueur)) != -1) {
		AmisHorsLigne.splice(index,1);
	}
	ClipListeAmis.removeChild(LeJoueur);
	MAJ_ListeAmis();
}

function Rejoindre_Ami(E:Event):void {
	CliquePartieEnCours=E.currentTarget.parent;
	Rejoindre_Partie(null);
}

// LISTE NOIRE
var ListeIgnorés:Array;
function Initialisation_ListeNoire(INFO:String):void {
	while (ClipListeNoire.numChildren != 0) {
		ClipListeNoire.removeChildAt(0);
	}
	_FenetreLN.visible = true;
	var LsIgnorés:Array=INFO.split(",");
	LsIgnorés.shift();
	LsIgnorés.sort();
	ListeIgnorés= new Array();
	for (var k:int = 0; k<LsIgnorés.length; k++) {
		var NomIgnoré:String=LsIgnorés[k];
		var Ignoré:MovieClip = new $Ignoré();
		Ignoré.Fond.gotoAndStop(1);
		Ignoré.NomIgnoré.text=NomIgnoré;
		Ignoré.addEventListener(MouseEvent.MOUSE_OVER, Partie_Allumage);
		Ignoré.addEventListener(MouseEvent.MOUSE_OUT, Partie_DeAllumage);
		ClipListeNoire.addChild(Ignoré);
		Ignoré.x=15;
		ListeIgnorés.push(Ignoré);
		Ignoré.Supprimer.addEventListener(MouseEvent.CLICK, Supprimer_LN);
		Ignoré.Supprimer.buttonMode=true;
	}
	MAJ_ListeNoire();
	Ascenseur_Reset(_FenetreLN.Ascenseur);

}

function MAJ_ListeNoire() {
	_FenetreLN.ListeVide.visible=(ListeIgnorés.length == 0);

	var i:int;
	for (i=0; i<ListeIgnorés.length; i++) {
		ListeIgnorés[i].y=18*i;
	}

}

function Supprimer_LN(E:Event):void {
	var LeJoueur:MovieClip=E.currentTarget.parent;
	var Nom:String=LeJoueur.NomIgnoré.text;
	Commandes("ignore "+Nom);
	var index:int;
	if ((index = ListeIgnorés.indexOf(LeJoueur)) != -1) {
		ListeIgnorés.splice(index,1);
	}
	ClipListeNoire.removeChild(LeJoueur);
	MAJ_ListeNoire();
}