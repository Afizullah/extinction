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
Desc.restrict = "^#";
visible = false;


function Init(DESCRIPTION:String, ID:int) {
	Desc.text = DESCRIPTION;
	this.Id = ID;
	visible = true;
}

BtnPublier.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Confirmer);
function Clique_Confirmer(E:Event):void {
	Clique_Fermer(null);
	_root.Commandes("team editer "+this.Id+" description "+Desc.text);
	_root._InfoTeam.Description.text = Desc.text;
}



Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible = false;
}

