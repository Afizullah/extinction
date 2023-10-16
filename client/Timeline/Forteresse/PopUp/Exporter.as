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

visible = false;
var _root:MovieClip = MovieClip(parent);

Titre.restrict = "A-Za-z0-9 ";

Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible = false;
}

ChoixCouleur.buttonMode = true;
ChoixNiveau.buttonMode = true;
ChoixNote.buttonMode = true;
ChoixMode.buttonMode = true;

function InitCommon():void {
	_root._Forteresse.SourisVisible=true;
	Mouse.show();
	visible = true;
	Record.text = "Inconnu";
	Recordman.text = "Inconnu";
}

function Initialisation(CODEMAP: String):void {
	InitCommon();
	Auteur.text = _root.NomJoueur;
	Code.text = CODEMAP;
	Choix_Status(0);
	Choix_Couleur(0);
}

function Initialisation_Respomap(MESSAGE : Array):void {
	InitCommon();
	Code.text = MESSAGE[1];
	Auteur.text = MESSAGE[2];
	Titre.text = MESSAGE[3];
	Choix_Niveau(MESSAGE[4]);
	Choix_Mode(MESSAGE[5]);
	Choix_Note(MESSAGE[6]);
	Choix_Status(MESSAGE[7]); 
	Record.text = MESSAGE[8];
	//Recordman.text = MESSAGE[9];
	Choix_Couleur(MESSAGE[10]);
}


Exporter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Exporter);
function Clique_Exporter(E:Event):void {
	var TitreStr : String = Titre.text;
	var CouleurStr : String = ChoixCouleur.Code;
	var DifficulteStr : String = ChoixNiveau.Code;
	var ModeStr : String = ChoixMode.Code;
	var NoteStr : String = ChoixNote.Code;
	var CodeMapStr : String = _root.CodeMapRally;
	if (TitreStr!=""&&CouleurStr!=null&&DifficulteStr!=null&&ModeStr!=null&&NoteStr!=null) {
		
		if (CodeMapStr != "@") { // Exporter sur base de l'état de la map au dernier /save (donc pas /exporter)
			if (ChoixMode.Code == 0) {
				var CodeMapSplit : Array = CodeMapStr.split("-");
				if (CodeMapSplit.length != 2 || CodeMapSplit[1].indexOf("3") == -1) {
					_root._Forteresse.Nouveau_Message_Chat("Vous devez ajouter au minimum un carré objectif (touche o) pour exporter en tant que rally.");			
					return;
				}
			}
		}
		
		if (!_root.UpdateRally) {
			_root.Envoie_Serveur("FxEXP#"+TitreStr+"#"+CouleurStr+"#"+DifficulteStr+"#"+ModeStr+"#"+NoteStr+"#"+CodeMapStr);			
		} else {
			var StatusStr : String = ChoixStatus.Code;
			// TODO record
			_root.Envoie_Serveur("FxUPD#"+TitreStr+"#"+CouleurStr+"#"+DifficulteStr+"#"+ModeStr+"#"+NoteStr+"#"+CodeMapStr+"#"+StatusStr);
		}
		visible = false;
	}
}

ChoixStatus.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Status);	
ChoixCouleur.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Couleur);	
ChoixNiveau.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Niveau);	
ChoixNote.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Note);	
ChoixMode.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Mode);	

var ListeStatus : Array = ["En attente de validation","Validée","Rejetée","Supprimée"];
var ListeStatusUpdate : Array = ["En attente de validation","Validée","Rejetée"];
var ListeCouleur : Array = ["Choix libre","Rouge","Bleue"];
var ListeNiveau : Array = ["Très facile","Facile","Moyenne","Difficile","Très difficile"];
var ListeNote : Array = ["Très moyenne","Moyenne","Bonne","Très bonne","Excellente"];
var ListeMode : Array = ["Rally","Kill","Frigo","Traitre","Exploration","Autre"];

function Clique_Status(E:MouseEvent):void
{
	if (_root.UpdateRally) {
		var Options:Array = new Array();
		for (var i : int = 0; i < ListeStatusUpdate.length; ++i) {
			Options.push(ListeStatusUpdate[i], Choix_Status, i);		
		}
		_root.Vignette_Menu(Options);
	}
}

function Choix_Status(CODE : int):void
{
	ChoixStatus.Choix.text = ListeStatus[CODE];
	ChoixStatus.Code = CODE;
}

function Clique_Couleur(E:MouseEvent):void
{
	if (_root.UpdateRally) {	
		var Options:Array = new Array();
		for (var i : int = 0; i < ListeCouleur.length; ++i) {
			Options.push(ListeCouleur[i], Choix_Couleur, i);		
		}
		_root.Vignette_Menu(Options);
	}
}

function Choix_Couleur(CODE : int):void
{
	ChoixCouleur.Choix.text = ListeCouleur[CODE];
	ChoixCouleur.Code = CODE;
}

function Clique_Niveau(E:MouseEvent):void
{
	var Options:Array = new Array();
	for (var i : int = 0; i < ListeNiveau.length; ++i) {
		Options.push(ListeNiveau[i], Choix_Niveau, i);		
	}
	_root.Vignette_Menu(Options);
}

function Choix_Niveau(CODE : int):void
{
	ChoixNiveau.Choix.text = ListeNiveau[CODE];
	ChoixNiveau.Code = CODE;
}

function Clique_Note(E:MouseEvent):void
{
	var Options:Array = new Array();
	for (var i : int = 0; i < ListeNote.length; ++i) {
		Options.push(ListeNote[i], Choix_Note, i);		
	}
	_root.Vignette_Menu(Options);
}

function Choix_Note(CODE : int):void
{
	ChoixNote.Choix.text = ListeNote[CODE];
	ChoixNote.Code = CODE;
}

function Clique_Mode(E:MouseEvent):void
{
	var Options:Array = new Array();
	for (var i : int = 0; i < ListeMode.length; ++i) {
		Options.push(ListeMode[i], Choix_Mode, i);		
	}
	_root.Vignette_Menu(Options);
}

function Choix_Mode(CODE : int):void
{
	ChoixMode.Choix.text = ListeMode[CODE];
	ChoixMode.Code = CODE;
}
