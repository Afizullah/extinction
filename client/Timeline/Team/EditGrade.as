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
	Titre.text = "Grade de " + Nom;
    this.visible = true;
	this.Nom = Nom;
}

function Clique(Valeur : int) {
	this.visible = false;
	_root.Commandes("team grade "+this.Nom+" " + Valeur);
}

Annuler.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Annuler);
function Clique_Annuler(E:Event):void {
	this.visible = false;
}

Grade0.buttonMode = true;
Grade0.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grade0);
function Clique_Grade0(E:Event):void {
	Clique(0);
}
Grade1.buttonMode = true;
Grade1.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grade1);
function Clique_Grade1(E:Event):void {
	Clique(1);
}
Grade2.buttonMode = true;
Grade2.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grade2);
function Clique_Grade2(E:Event):void {
	Clique(2);
}
Grade3.buttonMode = true;
Grade3.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grade3);
function Clique_Grade3(E:Event):void {
	Clique(3);
}
Grade4.buttonMode = true;
Grade4.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grade4);
function Clique_Grade4(E:Event):void {
	Clique(4);
}
Grade5.buttonMode = true;
Grade5.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grade5);
function Clique_Grade5(E:Event):void {
	Clique(5);
}