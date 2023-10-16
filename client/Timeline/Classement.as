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

var _root:MovieClip = MovieClip(parent);

visible = false;

var ClassementEnCours:int = -1;
/*
-1 : Aucun
 0 : Nouveau
 1 : Ancien
 2 : ELO
 3 : Mensuel
*/

var JeuClassementEnCours:int = 0; // 0 = Aaaah! ; 1 = Bouboum ; 2 = Forteresse

var ClassementJParEquipe:int = 1;
var CodeEloEnCours:int = -1;
SetCorrectClassementEloForGame();

var TriEloPar:int = 0; // 0 = Soi ; 1 = Top 20 ; 2 = Joueur recherché (<PseudoRechercheElo>)
var PseudoRechercheElo:String = "";

function ReceptionBouboum(LISTE:Array):void {
	var TexteNom:String = "";
	var TexteRatio:String = "";
	var TextePartie:String = "";
	var NbJoueur:int = LISTE.length;
	for (var i:int = 1; i<NbJoueur; i++) {
		var InfoJoueur:Array = LISTE[i].split(",");
		var Nom:String = InfoJoueur[0];
		var PartiesJouées:int = int(InfoJoueur[1]);
		var PartiesGagnées:int = int(InfoJoueur[2]);
		var Ratio:int = int(InfoJoueur[3]);
		TexteNom += (i<10?" ":"")+i+" - "+Nom.substr(0, 1).toUpperCase()+Nom.substr(1)+"\n";
		TextePartie += (Ratio/100)+"\n";
		TexteRatio += PartiesGagnées + "\n";//+" ("+Pourcent(PartiesGagnées, PartiesJouées)+")\n";
	}
	//
	Bouboum._TexteNom.text = TexteNom;
	Bouboum._TexteScore.text = TexteRatio;
	Bouboum._TextePartie.text = TextePartie;
	//
	Bouboum.visible = true;
	Titre.text = "Classement Bouboum - " + (ClassementEnCours==0?"Nouveau Classement":"Ancien Classement");
}

function Pourcent(Numerateur:int, Denominateur:int):String {
	if (Denominateur==0){
		return "0%";
	}else{
		return int(Numerateur / Denominateur * 1000) / 10 + "%";
	}
}

function ReceptionAaaah(LISTE:Array):void {
	var TexteNom:String = "";
	var TexteGuidage:String = "";
	var TexteCourseTete:String = "";
	var TexteCourseVie:String = "";
	var TexteKill:String = "";

	var NbJoueur:int = LISTE.length;
	
	Close_All();

	for (var i:int = 1; i<NbJoueur; i++) {
		var InfoJoueur:Array = LISTE[i].split(",");
		var Nom:String = InfoJoueur[0];
		var NbCourses:int = int(InfoJoueur[1]);
		var NbGuidages:int = int(InfoJoueur[2]);
		var NbArrivéesPremier:int = int(InfoJoueur[3]);
		var NbArrivéesEnVie:int = int(InfoJoueur[4]);
		var RatioSauvetage:int = int(InfoJoueur[5]);
		var Tués:int = int(InfoJoueur[6]);
		TexteNom += (i<10?" ":"")+i+" - "+Nom.substr(0, 1).toUpperCase()+Nom.substr(1)+"\n";
		TexteGuidage += NbGuidages + " (" + RatioSauvetage/100 + "%)\n";
		TexteCourseTete += Pourcent(NbArrivéesPremier, NbCourses) + "\n";
		TexteCourseVie  += Pourcent(NbArrivéesEnVie, NbCourses) +"\n";
		TexteKill += Tués + "\n";
	}
	//
	Aaaah._TexteNom.text = TexteNom;
	Aaaah._TexteGuidage.text = TexteGuidage;
	Aaaah._TexteCourseVie.text = TexteCourseVie;
	Aaaah._TexteCourseTete.text = TexteCourseTete;
	Aaaah._TexteKill.text = TexteKill;
	
	Aaaah.visible = true;
}

function ReceptionAaaahCourse(LISTE:Array):void {
	var TexteNom:String = "";
	var TexteGuidage:String = "";
	var TexteCourseTete:String = "";
	var TexteCourseVie:String = "";
	var TexteKill:String = "";

	var NbJoueur:int = LISTE.length;
	
	Close_All();

	for (var i:int = 1; i<NbJoueur; i++) {
		var InfoJoueur:Array = LISTE[i].split(",");
		var Nom:String = InfoJoueur[0];
		var NbCourses:int = int(InfoJoueur[1]);
		var NbGuidages:int = int(InfoJoueur[2]);
		var NbArrivéesPremier:int = int(InfoJoueur[3]);
		var NbArrivéesEnVie:int = int(InfoJoueur[4]);
		var RatioSauvetage:int = int(InfoJoueur[5]);
		var Tués:int = int(InfoJoueur[6]);
		TexteNom += (i<10?" ":"")+i+" - "+Nom.substr(0, 1).toUpperCase()+Nom.substr(1)+"\n";
		TexteGuidage +=  RatioSauvetage/100 + "%\n";
		TexteCourseTete += NbArrivéesPremier + "\n";//+ " (" + Pourcent(NbArrivéesPremier, NbCourses) + ")\n";
		TexteKill += Tués + "\n";
	}
	//
	AaaahCourse._TexteNom.text = TexteNom;
	AaaahCourse._TexteGuidage.text = TexteGuidage;
	AaaahCourse._TexteCourseTete.text = TexteCourseTete;
	AaaahCourse._TexteKill.text = TexteKill;
	
	AaaahCourse.visible = true;
	Titre.text = "Classement Aaaah! - " + (ClassementEnCours==0?"Nouveau Classement":"Ancien Classement");
}

var ClipListClassements:MovieClip = ClassementsMensuels._ListeClassements._ListeClassements2;
_root.Navigateur(ClassementsMensuels.Ascenseur, ClipListClassements, 400, 400, 50, ClassementsMensuels);
_root.Ascenseur_Reset(ClassementsMensuels.Ascenseur);

var TitreArray : Array = new Array("", "Bouboum", "Bouboum", "Bouboum", "Run", "Défilantes", "No Guide", "Rally", "Run", "Défilantes", "Mutual Shoot", "Fight/Survie", "Run", "Défilantes", "Défilantes", "No Guide", "Rally", "Rally", "Guidage", "Fight/Survie", "Fight/Survie", "Bouboum", "Bouboum");
var StatArray : Array = new Array( "", "Parties jouées", "Parties gagnées", "Manches gagnées", "Arrivées en tête", "Arrivées en tête", "Arrivées en tête", "Arrivées en tête", "Arrivées en vie", "Arrivées en vie", "Joueurs sauvés", "Joueurs tués", "Arrivées en tête", "Arrivées en tête", "Arrivées en vie", "Arrivées en tête", "Arrivées en tête", "Records", "Joueurs sauvés", "Dernier survivant", "Survie", "Parties gagnées", "Joueurs tués");

function ReceptionStats(LISTE:Array):void {
	Close_All();
	
	while (ClipListClassements.numChildren>0) {
		ClipListClassements.removeChildAt(0);
	}
	for (var j:int = 1; j<LISTE.length; j++) {
		var ClassementStr: Array = LISTE[j].split(";");
		var ClassementStat : MovieClip = new $ClassementStat();
		var TexteNom:String = "";
		var TexteRatio:String = "";
		var NbJoueur:int = ClassementStr.length;
		for (var i:int = 1; i<NbJoueur; i++) {
			var InfoJoueur:Array = ClassementStr[i].split(",");
			var Nom:String = InfoJoueur[0];
			var Stat:int = int(InfoJoueur[1]);
			var Ratio:Number = Number(InfoJoueur[2]);
			TexteNom += (i<10?" ":"")+i+" - "+Nom+"\n";
			TexteRatio += Stat + (Ratio>0 ? " ("+Ratio+(StatArray[j].indexOf("tués")>=0?"":"%")+")":"") + "\n";
		}
		//
		ClassementStat.x = 260*((j-1)%3);
		ClassementStat.y = 5+120*int((j-1)/3);		
		ClassementStat.Titre.text = TitreArray[j];
		ClassementStat.Stat.text = StatArray[j];
		ClassementStat._TexteNom.text = TexteNom;
		ClassementStat._TexteScore.text = TexteRatio;
		ClipListClassements.addChild(ClassementStat);
		//
	}
	_root.Ascenseur_Reset(ClassementsMensuels.Ascenseur);
	ClassementsMensuels.visible = true;
	Titre.text = "Classement Mensuel";
}

BtnBouboum.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Bouboum);
function Clique_Bouboum(E:Event):void {
	Close_All(1);
	Warning.visible = false;
	JeuClassementEnCours = 1;
	SetCorrectClassementEloForGame();
	Envoie_Demande_Classement();
}

BtnAaaah.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Aaaah);
function Clique_Aaaah(E:Event):void {
	Close_All(0);
	Warning.visible = false;
	JeuClassementEnCours = 0;
	SetCorrectClassementEloForGame();
	Envoie_Demande_Classement();
}

BtnForteresse.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Forteresse);
function Clique_Forteresse(E:Event):void {
	Close_All(2);
	JeuClassementEnCours = 2;
	Warning.visible = false;
	SetCorrectClassementEloForGame();
	Envoie_Demande_Classement();
}


ChoixClassement.BtnNouvClassement.addEventListener(MouseEvent.MOUSE_DOWN, Clique_NouveauClassement);
function Clique_NouveauClassement(E:Event):void {
	Close_All();
	Warning.visible = false;
	ClassementEnCours = 0;
	Envoie_Demande_Classement();
}

ChoixClassement.BtnAncientClassement.addEventListener(MouseEvent.MOUSE_DOWN, Clique_AncientClassement);
function Clique_AncientClassement(E:Event):void {
	Close_All();
	Warning.visible = false;
	ClassementEnCours = 1;
	Envoie_Demande_Classement();
}

ChoixClassement.BtnClassementElo.addEventListener(MouseEvent.MOUSE_DOWN, Clique_ClassementElo);
function Clique_ClassementElo(E:Event):void {
	Close_All();
	Warning.visible = false;
	ClassementEnCours = 2;
	SetCorrectClassementEloForGame();
	Envoie_Demande_Classement();
	
	///
	ClassementElo.visible = true;
}

ClassementsMensuels.visible = false;
ChoixClassement.BtnClassementMensuel.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Mensuel);
function Clique_Mensuel(E:Event):void {
	Close_All();
	Warning.visible = false;
	ClassementEnCours = 3;
	Envoie_Demande_Classement();
}
/*AaaahCourse.Guidage.visible = false;
AaaahCourse.Guidage.addEventListener(MouseEvent.MOUSE_DOWN, Clique_AaaahGuidage);
function Clique_AaaahGuidage(E:Event):void {
	Close_All();
	Aaaah.visible = true;
}*/

/**Aaaah.Course.visible = false;
Aaaah.Course.addEventListener(MouseEvent.MOUSE_DOWN, Clique_AaaahCourse);
function Clique_AaaahCourse(E:Event):void {
	Close_All();
	AaaahCourse.visible = true;
}**/

FermerFenetre.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible = false;
	Close_All();
}

function Close_All(excepter:int=-1):void {
	if (excepter != 0) {
		Aaaah.visible = false;
		AaaahCourse.visible = false;
	}
	if (excepter != 1) {
		Bouboum.visible = false;
	}
	
	if (excepter == -1) { // Close_All() pas appelé via clique sur l'un des 3 jeux => Changement de type de classement
		ClassementsMensuels.visible = false;
		//
		ClassementElo.Pseudos.text = "";
		ClassementElo.Points.text = "";
		ClassementElo.Num.text = "";
		ClassementElo.visible = false;
	}
}



/*******************************************
 **            Classement Elo             **
 *******************************************/
ClassementElo.RechercheElo.visible = false;
 
ClassementElo.Btn_Top20.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Top20Elo);
ClassementElo.Btn_Rechercher.addEventListener(MouseEvent.MOUSE_DOWN, Clique_RechercherElo);

function Envoie_Demande_Classement():void {
	_root.Envoie_Serveur("CxCl#" + JeuClassementEnCours + "#" + ClassementEnCours + "#" + CodeEloEnCours + "#" + ClassementJParEquipe);
}


var ModeJeuStr:Array = ["MS", "NG/Rally", "Défilante", "FS", "Normal", "Frigo", "Frag", "Kill", "Cross", "Run"];
ClassementElo.visible = false;
ClassementElo.ModeJeuElo.buttonMode = true;
ClassementElo.ModeJeuElo.ModeJeu.text = "Choix du mode";

ClassementElo.ModeJeuElo.ModeJeu.addEventListener(MouseEvent.MOUSE_DOWN, Clique_ModeJeu);
function Clique_ModeJeu(E:Event):void {
	var Options:Array = new Array();
	// 0 = Aaaah! ; 1 = Bouboum ; 2 = Forteresse
	if (JeuClassementEnCours == 0) { // Aaaah!
		Options.push(ModeJeuStr[0], Choix_ModeJeuElo, 0);
		Options.push(ModeJeuStr[1], Choix_ModeJeuElo, 1);
		Options.push(ModeJeuStr[2], Choix_ModeJeuElo, 2);
		Options.push(ModeJeuStr[3], Choix_ModeJeuElo, 3); // FS
		Options.push(ModeJeuStr[9], Choix_ModeJeuElo, 9);
	} else if (JeuClassementEnCours == 1) { // Bouboum
		Options.push(ModeJeuStr[4], Choix_ModeJeuElo, 4);
	} else if (JeuClassementEnCours == 2) { // Forteresse
		Options.push(ModeJeuStr[5], Choix_ModeJeuElo, 5);
		Options.push(ModeJeuStr[6], Choix_ModeJeuElo, 6);
		Options.push(ModeJeuStr[7], Choix_ModeJeuElo, 7);
		//Options.push(ModeJeuStr[8], Choix_ModeJeuElo, 8);
	}
	
	_root.Vignette_Menu(Options);
}

function Choix_ModeJeuElo(CODE:int):void {
	CodeEloEnCours = CODE;
	ClassementElo.ModeJeuElo.ModeJeu.text = ModeJeuStr[CodeEloEnCours];
	
	SetCorrectClassementEloForMode(); // Empêche d'avoir un "2 vs 2" FS par exemple en passant d'un mode avec x vs x vers un mode ne le supportant pas
	
	Envoie_Demande_Classement();
}

ClassementElo.NbJoueursElo.buttonMode = true;
ClassementElo.NbJoueursElo.NbJoueurs.text = "1 vs 1";
ClassementElo.NbJoueursElo.NbJoueurs.addEventListener(MouseEvent.MOUSE_DOWN, Clique_NbJoueursElo);
function Clique_NbJoueursElo(E:Event):void {
	var Options:Array = new Array();
	var txt:String = ClassementElo.ModeJeuElo.ModeJeu.text;
	
	if (txt != "MS" && txt != "FS" && txt != "Run") {
		Options.push("1 vs 1", Choix_NbJoueursElo, 1);
	}
	
	if (txt != "FS" && txt != "Run") {
		Options.push("2 vs 2", Choix_NbJoueursElo, 2);
	}
	
	Options.push("3 vs 3", Choix_NbJoueursElo, 3);
	
	Options.push("Team", Choix_NbJoueursElo, 99);
	
	_root.Vignette_Menu(Options);
}

function Choix_NbJoueursElo(CODE:int, actu:Boolean = true):void {
	ClassementJParEquipe = CODE; // 99 = team
	ClassementElo.NbJoueursElo.NbJoueurs.text = (CODE != 99 ? CODE + " vs " + CODE : "Team vs Team");
	if (actu) {
		Envoie_Demande_Classement();
	}
}

function ClassementEloCentre(LISTE:Array, PSEUDO:String) {
	var TextePseudos:String = "";
	var TextePoints:String = "";
	var TexteNumeros:String = "";
	
	var classement:Array = new Array();
	var positionJoueur:int = -1;
	var i:int;
	var i_recherche:int;
	
	PSEUDO = PSEUDO.toLocaleLowerCase();
	if (!isNaN(parseInt(PSEUDO))) {
		i_recherche = uint(parseInt(PSEUDO)) - 1;
	} else {
		i_recherche = -1;
	}
	
	for (i = 0; i < LISTE.length; ++i) {
		var InfoJoueur:Array = LISTE[i].split(",");
		
		classement.push(InfoJoueur[0]);
		classement.push(InfoJoueur[1]);
			
		if (PSEUDO == InfoJoueur[0] || i == i_recherche) {
			positionJoueur = i;
		}
	}
	
	///
	if (i_recherche != -1 && positionJoueur == -1) { // La position recherchée n'existe pas
		positionJoueur = LISTE.length - 1;//LISTE.length - 10; // On affiche la fin (la recherche étant > 0, la valeur demandée est donc forcément un rang trop bas => affiche le bas du classement)
		if (positionJoueur < 0) {
			positionJoueur = 0;
		}
	}
	///
	
	if (positionJoueur == -1) {
		ClassementEloTop20(LISTE);
	} else {
		
		//
		if (LISTE.length - (positionJoueur + 10) < 10) {
			i = positionJoueur - 10 - (positionJoueur - LISTE.length + 10);
		} else {
			i = positionJoueur - 10;
		}
		
		if (i < 0) {
			i = 0;
		}
		//
		
		const max_i:int = i + 20;
		const max:int = classement.length / 2 - 1;
		
		for (; i < max_i && i < max; ++i) {
			if (i != positionJoueur &&  i != i_recherche) {
				TextePseudos += FormatePseudos(classement[i * 2]) + "\n";
				TextePoints += classement[i * 2 + 1] + "\n";
				TexteNumeros += (i + 1) + ".\n";
			} else {
				TextePseudos += "<font color='#C9CD36'>" + FormatePseudos(classement[i * 2]) + "</font>\n";
				TextePoints += "<font color='#C9CD36'>" + classement[i * 2 + 1] + "</font>\n";
				TexteNumeros += (i + 1) + ".\n";
			}
		}
		
		ClassementElo.Pseudos.htmlText = TextePseudos;
		ClassementElo.Points.htmlText = TextePoints;
		ClassementElo.Num.text = TexteNumeros;
	}
}

function ClassementEloTop20(LISTE:Array) {
	var TextePseudos:String = "";
	var TextePoints:String = "";
	var TexteNumeros:String = "";
	
	var Pseudo:String = _root.NomJoueur.toLowerCase();
	const max:int = LISTE.length - 1;

	for (var i:int = 0; i < max && i < 21; ++i) {
		var InfoJoueur:Array = LISTE[i].split(",");
		
		if (Pseudo != InfoJoueur[0]) {
			TextePseudos += FormatePseudos(InfoJoueur[0]) + "\n";
			TextePoints += InfoJoueur[1] + "\n";
			TexteNumeros += (i + 1) + ".\n";
		} else {
			TextePseudos += "<font color='#C9CD36'>" + FormatePseudos(InfoJoueur[0]) + "</font>\n";
			TextePoints += "<font color='#C9CD36'>" + InfoJoueur[1] + "</font>\n";
			TexteNumeros += (i + 1) + ".\n";
		}
	}
	
	ClassementElo.Pseudos.htmlText = TextePseudos;
	ClassementElo.Points.htmlText = TextePoints;
	ClassementElo.Num.text = TexteNumeros;
}

function ReceptionElo(LISTE:Array) {
	if (LISTE == null) {
		ClassementElo.Pseudos.htmlText = "";
		ClassementElo.Points.htmlText = "";
		ClassementElo.Num.text = "";
		Titre.text = "Classement Elo - " + ModeJeuStr[CodeEloEnCours];
		return;
	}
	
	if (TriEloPar == 0) { // Classement de base
		ClassementEloCentre(LISTE, _root.NomJoueur);
	} else if (TriEloPar == 1) { // Top 20
		ClassementEloTop20(LISTE);
	} else if (TriEloPar == 2) { // Centré sur une recherche
		ClassementEloCentre(LISTE, PseudoRechercheElo);
	}
	
	
	Titre.text = "Classement Elo - " + ModeJeuStr[CodeEloEnCours];
}

function FormatePseudos(PSEUDO:String):String {
	if (PSEUDO == null || PSEUDO.length < 2) {
		return "";
	}
	return PSEUDO.substring(0, 1).toUpperCase() + PSEUDO.substring(1).toLocaleLowerCase();
}

function SetCorrectClassementEloForGame():void {
	// Mettre l'elo d'un mode de jeu qui existe pour le jeu dont le classement est affiché
	// (mise d'un mode par défaut au changement de jeu)
	if (ClassementEnCours == 2) { // Classement ELO
		if (JeuClassementEnCours == 0) { // Aaaah
			if ((CodeEloEnCours < 0 || CodeEloEnCours > 3) && CodeEloEnCours != 9) { // MS -> FS && RUN
				CodeEloEnCours = 1; // NG/Rally
				ClassementElo.Pseudos.htmlText = "";
				ClassementElo.Points.htmlText = "";
				ClassementElo.Num.text = "";
			}
		} else if (JeuClassementEnCours == 1) {
			if (CodeEloEnCours != 4) { // Normal
				CodeEloEnCours = 4; // Normal
				ClassementElo.Pseudos.htmlText = "";
				ClassementElo.Points.htmlText = "";
				ClassementElo.Num.text = "";
			}
		} else if (JeuClassementEnCours == 2) { // Forteresse
			if (CodeEloEnCours < 5 && CodeEloEnCours < 8) { // < Frigo && > Cross (bien que pas visible)
				CodeEloEnCours = 6; // Frag
				ClassementElo.Pseudos.htmlText = "";
				ClassementElo.Points.htmlText = "";
				ClassementElo.Num.text = "";
			}
		}
		
		ClassementElo.ModeJeuElo.ModeJeu.text = ModeJeuStr[CodeEloEnCours];
	}
}

function SetCorrectClassementEloForMode():void {
	if (ClassementEnCours == 2) { // Classement ELO
		var txt:String = ClassementElo.ModeJeuElo.ModeJeu.text;
		var txtVS:String = ClassementElo.NbJoueursElo.NbJoueurs.text;
		
		if (txt == "MS" && ClassementJParEquipe == 1) {
			Choix_NbJoueursElo(2, false);
		} else if ((txt == "FS" || txt == "Run") && (ClassementJParEquipe == 1 || ClassementJParEquipe == 2)) {
			Choix_NbJoueursElo(3, false);
		}
	}
}

function Clique_Top20Elo(E:Event):void {
	TriEloPar = 1; // 0 = Soi ; 1 = Top 20 ; 2 = Joueur recherché (<PseudoRechercheElo>)
	Envoie_Demande_Classement();
}

function Clique_RechercherElo(E:Event):void {
	ClassementElo.RechercheElo.visible = true;
}

function RechercheEffectuee(pseudoRecherche:String):void {
	if (pseudoRecherche.length > 0) {
		TriEloPar = 2; // 0 = Soi ; 1 = Top 20 ; 2 = Joueur recherché (<PseudoRechercheElo>)
		PseudoRechercheElo = pseudoRecherche;
		
		Envoie_Demande_Classement();
	}
}