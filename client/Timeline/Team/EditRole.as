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

var _root:MovieClip = MovieClip(parent.parent);
visible = false;

function Init(Nom:String) {
	Titre.text = "Rôle de de " + Nom;
    this.visible = true;
	this.Nom = Nom;
}

function Clique(Valeur : int) {
	this.visible = false;
	_root.Commandes("team role "+this.Nom+" " + Valeur);
}

Membre.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Membre);
function Clique_Membre(E:Event):void {
	Clique(0);
}

Recruteur.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Recruteur);
function Clique_Recruteur(E:Event):void {
	Clique(1);
}

Leader.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Leader);
function Clique_Leader(E:Event):void {
	Clique(2);
}

Scribe.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Scribe);
function Clique_Scribe(E:Event):void {
	Clique(3);
}