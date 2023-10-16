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

import flash.events.Event;
import flash.display.MovieClip;

visible = false;
var _root:MovieClip = MovieClip(parent);
InfoTeam.visible = false;
NomTeam.visible = false;

BtnDistinctions.buttonMode = true;
BtnOldStat.buttonMode = true;
BtnClassees.buttonMode = true;
BtnStat.buttonMode = true;

OngletStat.visible = true
OngletClassees.visible = false;
OngletOldStat.visible = false;
Recompenses.visible = false;

Recomp.visible = false;

var InfoIdTeam : String;
var Grade:MovieClip;
var Role:MovieClip;
NomTeam.addEventListener(TextEvent.LINK, Clique_Nom);
function Clique_Nom(E:TextEvent):void {
	var Identifiant:String = E.text;
	_root.Commandes("fiche "+Identifiant); // Info sur la team
	Clique_Fermer(null);
}

var NomJoueur : String = "";

function Initialisation(Info:Array):void {
	Clique_Fermer(null);
	NomJoueur = Info[0];
	NomJoueur = NomJoueur.substr(0,1).toUpperCase()+NomJoueur.substr(1);
	NomFiche.text = "Fiche de "+NomJoueur;
	Inscription.text = _root.dateFormat(Info[1]);
	Connexion.text = _root.dateFormat(Info[17]);
	if (Info[18]=="0") {
		Connexion.appendText(" (Hors ligne)");
	}
	////Recomp.visible = Info[19] == "1";
	InitDistinctions(Info[19]);
	OngletStat.Guidage.text = Info[2] + " ("+Info[3]/100+"%)";
	OngletStat.VictoireAaaah.text = Info[4] + " ("+ Info[5]/100+"%)";
	OngletStat.VictoireBoum.text = Info[7] + " ("+int(10000*Info[7]/Info[6])/100+"%)";
	OngletStat.TuéMort.text = "" + Info[8]/100;
	/*
	Message.append(sa_guidage + "#");
	Message.append(Result.getInt("sa_pguidage") + "#"); // % => /100
						Message.append(Result.getInt("sa_victoire") + "#");
						Message.append(Result.getInt("sa_pvictoire") + "#"); // % => /100
						Message.append(Result.getInt("sb_victoire") + "#");
						Message.append(Result.getInt("sb_pvictoire") + "#"); // % => /100
	*/
	if (Info[9] != -1) {
		OngletOldStat.OldGuidage.text = (int(Info[2]) + int(Info[9])) + " (" + Info[10]/100 + "%)";
		OngletOldStat.OldVictoireAaaah.text = (int(Info[4]) + int(Info[11])) + " (" + Info[12]/100 + "%)";
		OngletOldStat.OldVictoireBoum.text = (int(Info[7]) + int(Info[13])) + " (" + Info[14]/100 + "%)";
	} else {
		OngletOldStat.OldGuidage.text = "-";
		OngletOldStat.OldVictoireAaaah.text = "-";
		OngletOldStat.OldVictoireBoum.text = "-";
	}
	
	
	Desc.text = Info[16];
	if (Desc.text==""){
		Desc.text="\n\nCe joueur n'a pas rempli sa description.";
	}
	//
	if (Info[15] && Info[15] != "0") {
		_root.Chargement_Image(Avatar, "", Info[15], false, "http://img.atelier801.com/");
	} else {
		_root.Chargement_Image(Avatar, "", Info[15], false, "http://img.atelier801.com/");
	}
	//khfkqjhsdkfh
	Grade = null;
	Role = null;
	//if (Info.length>20){ //team
	if (Info[20] != "0"){ //team
		InfoTeam.visible = true;
		NomTeam.visible = true;
		InfoIdTeam = Info[20];
		NomTeam.htmlText = "<a href='event:"+InfoIdTeam+"'>" + Info[21]+"</a>";

		Role = _root.getRole(Info[23]);
		if (Role != null) {
			this.addChild(Role);
			Role.x = InfoTeam.x + 67.25;
			Role.y = InfoTeam.y;
			if (Info[23]>0 && Info[23] != "3"){ // Si rôle de leader/recruteur
				Role.addEventListener(MouseEvent.CLICK, Rejoindre_Team);
				Role.buttonMode=true;
				_root.Vignette(Role, _root.NomRole(Info[23]) + " - Cliquer pour rejoindre la team");
			}
			else {
				_root.Vignette(Role, _root.NomRole(Info[23]));
			}
		}

		Grade = _root.getGrade(Info[22]);
		if (Grade != null) {
			this.addChild(Grade);
			Grade.x = InfoTeam.x + (Role==null?57.95:47.9);
			Grade.y = InfoTeam.y;
			var Label : String = "Grade "+Info[22];
			if (Info.length>24 && Info[24] == "1") {
				Label = "Recrue " + Label;
			}
			_root.Vignette(Grade, Label);

		}


	}
	
	/// Rang elo
	var meilleur:String = Info[25];
	var idMeilleur:int = 0;
	var tabJeux:Array = ["Aaaah!", "Bouboum", "Forteresse"];
	var alias:Array = ["a", "b", "f"]; // Lettre pour /statelo en fonction du titre
	var tabAutres:Array = [];
	
	if (meilleur < Info[26]) {
		meilleur = Info[26];
		idMeilleur = 1;
		tabAutres.push(0);
	} else {
		tabAutres.push(1);
	}
	
	if (meilleur < Info[27]) {
		meilleur = Info[27];
		tabAutres.push(idMeilleur);
		idMeilleur = 2;
	} else {
		tabAutres.push(2);
	}
	
	OngletClassees.RangElo2.SetRangElo(Info[25 + idMeilleur]);
	OngletClassees.RangElo2.SetJeu(tabJeux[idMeilleur]);
	OngletClassees.RangElo2.alias = alias[idMeilleur];
	OngletClassees.RangElo1.SetRangElo(Info[25 + tabAutres[0]]);
	OngletClassees.RangElo1.SetJeu(tabJeux[tabAutres[0]]);
	OngletClassees.RangElo1.alias = alias[tabAutres[0]];
	OngletClassees.RangElo3.SetRangElo(Info[25 + tabAutres[1]]);
	OngletClassees.RangElo3.SetJeu(tabJeux[tabAutres[1]]);
	OngletClassees.RangElo3.alias = alias[tabAutres[1]];
	///
	
	visible = true;
}

OngletClassees.RangElo1.addEventListener(MouseEvent.CLICK, Afficher_StatsElo);
OngletClassees.RangElo1.buttonMode = true;
OngletClassees.RangElo2.addEventListener(MouseEvent.CLICK, Afficher_StatsElo);
OngletClassees.RangElo2.buttonMode = true;
OngletClassees.RangElo3.addEventListener(MouseEvent.CLICK, Afficher_StatsElo);
OngletClassees.RangElo3.buttonMode = true;
function Afficher_StatsElo(E : Event) {
	_root.Commandes("statelo " + NomJoueur + " " + E.currentTarget.alias);
}

Recomp.addEventListener(MouseEvent.CLICK, Afficher_Palmares);
Recomp.buttonMode=true;
_root.Vignette(Recomp, "Cliquer pour afficher les distinctions");
function Afficher_Palmares(E : Event){
	_root.Commandes("palmares "+NomJoueur);
}


function Rejoindre_Team(E : Event){
	_root.Commandes("rejoindre "+InfoIdTeam);
	Clique_Fermer(null);
}

Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	InfoTeam.visible = false;
	NomTeam.visible = false;
	visible = false;
	if (Grade!=null && this.contains(Grade)) {
		this.removeChild(Grade);
	}
	if (Role!=null && this.contains(Role)){
		this.removeChild(Role);
	}

}

function Fermer_Onglet():void {
	OngletClassees.visible = false;
	OngletOldStat.visible = false;
	OngletStat.visible = false;
	Recompenses.visible = false;
}
BtnDistinctions.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Distinctions);
function Clique_Distinctions(E:Event):void {
	Fermer_Onglet();
	Recompenses.visible = true;
	//_root.Commandes("palmares "+NomJoueur);
}
BtnOldStat.addEventListener(MouseEvent.MOUSE_DOWN, Clique_OldStat);
function Clique_OldStat(E:Event):void {
	Fermer_Onglet();
	OngletOldStat.visible = true;
}
BtnClassees.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Classees);
function Clique_Classees(E:Event):void {
	Fermer_Onglet();
	OngletClassees.visible = true;
}
BtnStat.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Stat);
function Clique_Stat(E:Event):void {
	Fermer_Onglet();
	OngletStat.visible = true;
}


function InitDistinctions(InfosRecomp:String):void {
	var $:String = String.fromCharCode(2);
	
	//Titre.text = "Distinctions de "+Info[0];

	// nettoyage
	while (Recompenses.numChildren != 0) {
		Recompenses.removeChildAt(0);
	}
	
	if (InfosRecomp == "0") { // Pas de médailles
		return;
	}
	//// 350
	// récompenses spéciales
	var LsRecomps:Array=InfosRecomp.split($+$);
	var NbLignes:int = (LsRecomps.length-2)/17;
	/*for (var k:int = 1; k<LsRecomps.length; k++) {	
		var UneRecomp:Array=LsRecomps[k].split($);
		var ImageRecompense : String = UneRecomp[0];
		var Recompense : MovieClip = _root.NouvelleRecompense(ImageRecompense);
		Recompenses.addChild(Recompense);
		var Ligne:int = int((k-1)/11);
		var IndexSurLigne:int = k-11*Ligne;
		var MaxSurLigne:int = (Ligne==NbLignes ? (LsRecomps.length-2)%11 : 10);
		Recompense.x = 70+20*IndexSurLigne-10*MaxSurLigne;		
		Recompense.y = 24+20*Ligne-10*NbLignes;
		_root.Vignette(Recompense, UneRecomp[1]);		
	}*/
	
	
	/*
	   Dim récompense : 15x15 (LxH)
	   Largeur affichage médailles (La) : 350
	   Espace entre médailles (E) : 2
	*/
	LsRecomps.shift();///
	for (var i:int = 0; i < LsRecomps.length; ++i) {
		var UneRecomp:Array=LsRecomps[i].split($);
		var ImageRecompense : String = UneRecomp[0];
		var Recompense : MovieClip = _root.NouvelleRecompense(ImageRecompense);
		Recompenses.addChild(Recompense);
		
		// 1. Déterminer la position en y
		var Ligne:int = int(i / 17);
		Recompense.y = 10 + (15 + 4 + 3) * Ligne;
		// 2. Déterminer la position en x
		
		/*
		   RangLocal = i % 17
		   PlaceNecessaireLigne = NbMedaillesSurRang * (L + 2 * E)
		   StartLigne = (La - PlaceNecessaireLigne) / 2
		*/
		var NbMedaillesSurRang:int = (LsRecomps.length - int(i/17)*17 >= 16 ? 17 : LsRecomps.length % 17);
		
		Recompense.x = ((350 /* La */ - (NbMedaillesSurRang * (15 + 4) /* L + 2 * E*/) /* PlaceNecessaireLigne */) / 2) /* Début de la ligne */
		             + (i % 17) /* RangLocal */ * (19) /* L + 2 * E */;
		
		_root.Vignette(Recompense, UneRecomp[1]);
	}

}