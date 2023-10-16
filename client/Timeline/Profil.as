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
var $:String=String.fromCharCode(2);
var Restriction:String="^#"+"§"+$;
Desc.restrict = Restriction;

//BtnAvatar.visible=false;

/*var Fichier:FileReference = new FileReference();
Fichier.addEventListener(Event.SELECT, UpLoad_Selection);
Fichier.addEventListener(Event.CANCEL, UpLoad_Annuler);
Fichier.addEventListener(HTTPStatusEvent.HTTP_STATUS, UpLoad_Erreur);
Fichier.addEventListener(SecurityErrorEvent.SECURITY_ERROR, UpLoad_Erreur);
Fichier.addEventListener(IOErrorEvent.IO_ERROR, UpLoad_Erreur);
Fichier.addEventListener(Event.COMPLETE, UpLoad_Complet);*/

/*BtnAvatar.addEventListener(MouseEvent.MOUSE_DOWN, Selection_Image);
function Selection_Image(OUI:Boolean):void {
	Fichier.browse();
}*/

BtnPublier.addEventListener(MouseEvent.MOUSE_DOWN, Publier_Desc);
function Publier_Desc(E:Event):void {
	if (Desc.maxScrollV<=1){
		Publié.text = "Description publiée.";
		_root.Envoie_Serveur("CxPrM#"+Desc.text);
	}else{
		Publié.text = "5 lignes maximum.";
	}
	Publié.visible = true;

}

/*function UpLoad_Selection(E:Event):void {
	var FichierCible:FileReference = FileReference(E.target);
	var InfoNom:Array = FichierCible.name.split(".");
	var Extension:String = InfoNom[InfoNom.length-1].toLowerCase();
	if (Extension == "jpg" || Extension == "jpeg") {
		if (FichierCible.size < 10000) {
			FichierCible.upload(new URLRequest("http://213.251.135.103/minijeux/Images.php?n="+_root.IdJoueur));
		} else {
			Erreur.textColor = 0xCB546B;
			Erreur.text = "Cette image est invalide, son poids est de "+Math.ceil(FichierCible.size/1000)+" ko alors qu'il ne devrait pas dépasser 10 ko.";
		}
	} else {
		Erreur.textColor = 0xCB546B;
		Erreur.text = "Vous ne pouvez utiliser que des images jpg ou jpeg.";
	}
}

function UpLoad_Complet(E:Event):void {
	Erreur.textColor = 0xCB546B;
	Erreur.text = "Avatar mis à jour avec succès.";
	_root.Envoie_Serveur("CxAV#1");
}

function UpLoad_Annuler(E:Event):void {
}

function UpLoad_Erreur(E:Event):void {
	Erreur.textColor = 0xCB546B;
	Erreur.text = "Une erreur au niveau du serveur s'est produite.";
}*/

function Initialisation(Info:Array):void {
	//Erreur.textColor = 0xC2C2DA;
	//Erreur.text="Votre avatar doit :\n- être de format jpg.\n- faire moins de 10 ko.\n- faire 120 pixels de largeur et 80 de hauteur.";
	Publié.visible = false;
	Desc.text = Info[1];
	if (Desc.text==""){
		Desc.text="\n\nVous n'avez pas encore rempli votre description.";
	}
	_root.Chargement_Image(Avatar, "", Info[0], false, "http://img.atelier801.com/");
	visible = true;
}

Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible = false;
}

