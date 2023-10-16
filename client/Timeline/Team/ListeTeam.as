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

visible=false;
var _root:MovieClip=MovieClip(parent);
var $:String=String.fromCharCode(2);

_root.Navigateur(Ascenseur_Aaaah, _Aaaah._MembresTeam2, 325, 325, 50, _Aaaah);
_root.Navigateur(Ascenseur_Boum, _Boum._MembresTeam2, 325, 325, 50, _Boum);
_root.Navigateur(Ascenseur_Forteresse, _Forteresse._MembresTeam2, 325, 325, 50, _Forteresse);

var SAVE_INFO:String="";

Quitter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible=false;
}

var Tri:int=0;
TypeTri.buttonMode=true;
TypeTri.mouseChildren=false;
TypeTri.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Tri);
function Clique_Tri(E:Event):void {
	if (Tri==4) {
		Tri = -1;
	}
	Tri = (Tri + 1)%4;
	var TriTexte:String="";
	if (Tri==0) {
		TriTexte="Tri standard";
	} else if (Tri==1) {
		TriTexte="Tri par membres";
	} else if (Tri==2) {
		TriTexte="Tri par nom";
	} else if (Tri==3) {
		TriTexte="Tri par date d'inscription";
	}
	TypeTri.TriTeam.text=TriTexte;
	MAJ_ListeTeams();
}


var ClipListeMembres_Aaaah:MovieClip=_Aaaah._MembresTeam2;
var ClipListeMembres_Boum:MovieClip=_Boum._MembresTeam2;
var ClipListeMembres_Forteresse:MovieClip=_Forteresse._MembresTeam2;


var ListeTeams:Array;
var IdTeamFiche:Number;

function Initialisation(INFO:String):void {
	_root.Inaction=0;
	ChangerMode.visible = _root.MODOTEAM || _root.RESPOTRITEAM;
	Edit.visible = false;
	
	SAVE_INFO="";
	if (Tri==4) {
		Clique_Tri(null);
	}

	// on rétablit la souris si besoin
	if (_root._Forteresse.visible) {
		_root._Forteresse.SourisVisible=true;
		Mouse.show();
		_root._Forteresse._Réticule.visible=false;
	}

	visible=true;

	if (_root.TEAM||_root.NomJoueur.substr(0,1)=="*") {
		Online.x=350;
		Creer.visible=false;
	} else {
		Online.x=411;
		Creer.visible=true;
	}



	ListeTeams = new Array();

	var LsMembres:Array=INFO.split($+$);

	for (var k:int = 1; k<LsMembres.length; k++) {
		var InfoTeam:Array=LsMembres[k].split($);

		var Team = new $JoueurBase();
		AddTeam(Team,InfoTeam);
		/*if (Team.Id=="2") { // TE
			InfoTeam[2]="1";
			var Team2 = new $JoueurBase();
			AddTeam(Team2,InfoTeam);
		}*/
	}


	MAJ_ListeTeams();
}

function AddTeam(Team : $JoueurBase, InfoTeam) {
	Team.Score=InfoTeam[5];
	Team.Membres=InfoTeam[3];
	Team.Nom=InfoTeam[1];
	Team.Offi=InfoTeam[4];
	Team.Jeu=InfoTeam[2];
	Team.Id=InfoTeam[0];
	Team.Online=0;

	ListeTeams.push(Team);

	Team._Texte.addEventListener(MouseEvent.CLICK, Clique_Team);	
}

function MAJ_ListeTeams() {


	while (ClipListeMembres_Aaaah.numChildren != 0) {
		ClipListeMembres_Aaaah.removeChildAt(0);
	}
	while (ClipListeMembres_Boum.numChildren != 0) {
		ClipListeMembres_Boum.removeChildAt(0);
	}
	while (ClipListeMembres_Forteresse.numChildren != 0) {
		ClipListeMembres_Forteresse.removeChildAt(0);
	}

	if (Tri==0) {
		ListeTeams.sortOn("Score", Array.NUMERIC | Array.DESCENDING);
	} else if (Tri==1) {
		ListeTeams.sortOn("Membres", Array.NUMERIC | Array.DESCENDING);
	} else if (Tri==2) {
		ListeTeams.sortOn("Nom", Array.CASEINSENSITIVE);
	} else if (Tri==3) {
		ListeTeams.sortOn("Offi");
	} else if (Tri==4) {
		ListeTeams.sortOn(["Online", "Membres"], Array.NUMERIC | Array.DESCENDING);
	}

	var B:int=0;
	var A:int=0;
	var F:int=0;
	for (var i:int=0; i<ListeTeams.length; i++) {
		var Team:$JoueurBase=ListeTeams[i];

		// texte label
		var Texte:String="";

		// couleur
		if (Team.Id==_root.IdTeam) {
			Texte+="<font color='#009D9D'>";
		} else {
			Texte+="<font color='#C2C2DA'>";
		}

		// info membres
		var What:String="";
		if (Tri!=4) {
			What=Team.Membres;
		} else {
			What=Team.Online;
			Team.MembresOnline.sort();
			if (! Team.hasVignette && Team.Online > 0) {
				_root.Vignette(Team,AfficherOnline(Team.MembresOnline)); 
			}			
		}
		Texte+=Team.Nom+"</font>  <font color='#C9CD36'>"+What+"</font>";

		// info modoteam
		if (_root.MODOTEAM || _root.RESPOTRITEAM) {
			Texte="<font size='10'>"+Texte+" <font color='#009D9D'>"+Team.Score+"</font></font>";
		}
		Team._Texte.htmlText=Texte;

		// affichage
		if (Tri!=4||Team.Online>0) {
			if (Team.Jeu==1) {
				Team.y=20*B;
				B++;
				ClipListeMembres_Boum.addChild(Team);
			} else if (Team.Jeu==2) {
				Team.y=20*A;
				A++;
				ClipListeMembres_Aaaah.addChild(Team);
			} else if (Team.Jeu==3) {
				Team.y=20*F;
				F++;
				ClipListeMembres_Forteresse.addChild(Team);
			}
		}

	}

	if (!EditMode) { // no scroll if modo team is editing
		_root.Ascenseur_Reset(Ascenseur_Aaaah);
		_root.Ascenseur_Reset(Ascenseur_Boum);
		_root.Ascenseur_Reset(Ascenseur_Forteresse);
	}

}

function FindTeam(Id : Number):$JoueurBase {

	for (var c:int=0; c<ListeTeams.length; c++) {
		var Team:$JoueurBase=$JoueurBase(ListeTeams[c]);
		if (Team.Id==Id) {
			return Team;
		}
	}
	return null;
}

function MembresOnline(INFO:String):void {

	SAVE_INFO=INFO;

	// réinit
	for (var c:int=0; c<ListeTeams.length; c++) {
		var T:$JoueurBase=$JoueurBase(ListeTeams[c]);
		T.Online=0;
		T.MembresOnline= new Array();
	}

	TypeTri.TriTeam.text="Tri par membres connectés";

	// traitement
	var LsMembres:Array=INFO.split($+$);

	for (var k:int = 1; k<LsMembres.length; k++) {
		var InfoTeam:Array=LsMembres[k].split($);

		var Id:Number=InfoTeam[0];
		var NomJoueur:String=InfoTeam[1];
		var Team:$JoueurBase=FindTeam(Id);
		if (Team!=null) {
		Team.Online++;

		Team.visible=true;
		Team._Texte.htmlText=Team.Nom+" <font color='#C9CD36'>"+Team.Online+"</font>";
		Team.MembresOnline.push(NomJoueur);
		}
	}

	Tri=4;

	MAJ_ListeTeams();
}

var EditMode : Boolean = false;
ChangerMode.addEventListener(MouseEvent.MOUSE_DOWN, Clique_ChangerMode);
function Clique_ChangerMode(E:Event):void {
	EditMode = !EditMode;
}

function Clique_Team(E:MouseEvent):void {
	var Team:MovieClip=E.currentTarget.parent;
	if ((_root.MODOTEAM || _root.RESPOTRITEAM) && EditMode) {	
		Clique_Team_Edition(Team.Id, Team.Score);
	} else {
		_root.Commandes("fiche " + Team.Id);		
	}
}

Creer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Creer);
function Clique_Creer(E:Event):void {
	Clique_Fermer(null);
	_root._NouvelleTeam.visible=true;
}

Informations.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Informations);
function Clique_Informations(E:Event):void {
	_root._Informations.visible=true;
}

Online.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Online);
function Clique_Online(E:Event):void {
	if (SAVE_INFO=="") {
		_root.Envoie_Serveur("TxO");
	} else {
		MembresOnline(SAVE_INFO);
	}
}

function AfficherOnline ( LISTE : Array ) : String {
	var Result : String = "";
	var size : int = LISTE.length;
	for (var c: int = 0; c < size; c++) {
		Result += LISTE[c];
		if (c!=size-1) {
			Result += ", ";
			if (c%10==9){
				Result += "\n";
			}			
		}
	}
	return Result;
}

Edit.restrict = "0-9";
var CurrentIdTeam : String = "";
var CurrentScore : String = "";
Edit.addEventListener(FocusEvent.FOCUS_OUT, Clique_FocusOut);
function Clique_FocusOut(E:Event):void {
	if (CurrentScore!=Edit.text) {		
		_root.Commandes("team editer " + CurrentIdTeam + " score " + Edit.text);
		FindTeam(int(CurrentIdTeam)).Score = Edit.text;
		MAJ_ListeTeams();
	}
	Edit.visible = false;	
}

function Clique_Team_Edition(IdTeam : String, Score : String):void {
	CurrentIdTeam = IdTeam;
	CurrentScore = Score;
	Edit.visible = true;	
	Edit.text = Score;	
	stage.focus = Edit;
	Edit.setSelection(0,Edit.text.length);	
}
