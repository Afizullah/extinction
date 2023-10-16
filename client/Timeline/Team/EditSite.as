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
Site.restrict = "^<>#";
visible = false;


function Init(SITE:String, ID:int) {
	Site.text = SITE;
	this.Id = ID;
	visible = true;
}

Annuler.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Annuler);
function Clique_Annuler(E:Event):void {
	visible = false;
}

Confirmer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Confirmer);
function Clique_Confirmer(E:Event):void {
	Clique_Annuler(null);
	_root.Commandes("team editer "+this.Id+" site "+Site.text);
	_root._InfoTeam.Site.text = Site.text;	
}

