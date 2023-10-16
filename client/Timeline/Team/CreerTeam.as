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


Quitter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
Annuler.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible = false;
}


var JeuEnCours:MovieClip;
var CodeJeu:String = "2";

NomTeam.restrict = "^<>#°;";
NomFondateur.restrict = "a-zA-Z";
Tag.restrict = "a-zA-Z0-9\-";
DateFondation.restrict = "0-9/";

function addZero(Value : int) : String {
	if (Value<10) {
		return "0"+Value;
	}else{
		return "" + Value;
	}
}

var now : Date = new Date();
DateFondation.text = addZero(now.getDate())+"/"+ addZero(now.getMonth()+1)+"/"+now.getFullYear();

Erreur.visible = false;

BtnBouboum.CodeJeu = "1";
BtnAaaah.CodeJeu = "2";
BtnForteresse.CodeJeu = "3";

// initialisation
JeuEnCours = BtnAaaah;
BtnAaaah.Encoche.E1.visible = true;
BtnAaaah.Encoche.E0.visible = false;
BtnBouboum.Encoche.E1.visible = false;
BtnBouboum.Encoche.E0.visible = true;
BtnForteresse.Encoche.E1.visible = false;
BtnForteresse.Encoche.E0.visible = true;

	
BtnAaaah.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Jeu);
BtnBouboum.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Jeu);
BtnForteresse.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Jeu);
function Clique_Jeu(E:MouseEvent):void {
	if (JeuEnCours != null) {
		JeuEnCours.Encoche.E0.visible = true;
		JeuEnCours.Encoche.E1.visible = false;
	}
	JeuEnCours = MovieClip(E.currentTarget);
	CodeJeu = JeuEnCours.CodeJeu;
	JeuEnCours.Encoche.E1.visible = true;
	JeuEnCours.Encoche.E0.visible = false;

}

Creer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Création);
function Clique_Création(E:Event):void {

		
		if (NomTeam.text=="") {
			Erreur.text="Vous devez entrer un nom de team.";
			Erreur.visible = true;
			return;
		}
		
		var date_ar = DateFondation.text.split("/");		
		if (date_ar.length!=3||date_ar[0]<1||date_ar[0]>31||date_ar[1]<1||date_ar[1]>12||date_ar[2]<2008||date_ar[2]>2020) {			
			Erreur.text="La date n'est pas correct ou le format est invalide.";
			Erreur.visible = true;
			return;
		}		
		var dateFondation : Date = new Date(date_ar[2], date_ar[1]-1, date_ar[0]);
		var timeStamp : int = int(dateFondation.getTime()/1000);
		
		if (Tag.text=="") {
			Tag.text="-";
		}
		
		if (NomFondateur.text=="") {
			NomFondateur.text=_root.NomJoueur;
		}		
		
		_root.Envoie_Serveur("TxC#"+NomTeam.text+"#"+Tag.text+"#"+NomFondateur.text+"#"+CodeJeu+"#"+timeStamp);
}

Informations.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Informations);
function Clique_Informations(E:Event):void {
	_root._Informations.visible = true;
}