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

var _root:MovieClip = MovieClip(parent.parent);
visible = false;

var ForumEnCours:Array;
var GlisseurY:int;
var ClipY:int;
var CodeForumEnCours:int;

_root.Navigateur(Ascenseur, ClipListe1.ClipListe2, 520, 500, 45);

function Affichage_Forum(FORUM,PAGE:int,PAGE_MAX:int) {
	MovieClip(parent).AffichageSujet.visible = false;

	if (_root.ForumLecture || (CodeForumEnCours == 11 && !_root.LEADER && !_root.SCRIBE)) { // Interdit de créer un noveau sujet pour les invités/mutes ou sur le forum team si pas leader/scribe
		NouveauSujet.visible = false;
		//Rechercher.visible = false;
	} else {
		NouveauSujet.visible = true;
	}

	ForumEnCours = FORUM;

	while (ClipListe1.ClipListe2.numChildren > 0) {
		ClipListe1.ClipListe2.removeChildAt(0);
	}

	// Gestion des pages
	//PageEnCours = PAGE;
	ClipPage.EffetTexte.Texte.text = "Page "+(PAGE+1)+"/"+PAGE_MAX;
	OptionPage = _root.ConstruirePages(PAGE,PAGE_MAX,Changement_Page, Atteindre_Page);


	// Date, Id, Auteur, Titre, Creation
	var LsSujet:Array = FORUM;
	var NbSujet:int = LsSujet.length;
	var Z:int = 0;
	for (var i:int = 0; i<NbSujet; i=i+12) {
		var SujetEnCours:MovieClip = new $SujetBase();
		ClipListe1.ClipListe2.addChild(SujetEnCours);
		SujetEnCours.y = Z*85;
		var TitreSujet:String = LsSujet[i+4];
		var Auteur:String = LsSujet[i+2];
		var MagicWord:RegExp = /\[Pseudo\]/g;
		if (Auteur == "Moderateur") {
			TitreSujet = TitreSujet.replace(MagicWord,_root.NomJoueur);
		}
		SujetEnCours.SujetClos = LsSujet[i+8] != "0";
		SujetEnCours.Pin.visible = LsSujet[i+10] == "1";
		SujetEnCours.Anim.visible = LsSujet[i+11] == "1";
		SujetEnCours.Titre1.Titre2.Texte.text = TitreSujet;
		SujetEnCours.Titre1.gotoAndStop(1);
		SujetEnCours.Fond1.gotoAndStop(1);
		var AuteurM : String = "- "+Auteur+"<font color='#6C77C1'>, "+_root.Formatage_Date(LsSujet[i+5]) + "</font>";
		if (SujetEnCours.SujetClos) {
			var MsgFermeture : String = "<font color='#CB546B'>, Discussion close";
			var Modo : String = LsSujet[i+9];
			if (_root.MODO|| (_root.MODOFORUM && !_root.RECRUEFORUM)) {
				if (Modo!="-") {
					MsgFermeture+= " par " + Modo;
				}
			}
			MsgFermeture += "</font>";
			AuteurM += MsgFermeture;
		}
		SujetEnCours.Auteur.htmlText = AuteurM;
		if (LsSujet[i+3] && LsSujet[i+3] != "0") {
			_root.Chargement_Image(SujetEnCours._Avatar, "", LsSujet[i+3], false, "http://img.atelier801.com/");
		} else {
			_root.Chargement_Image(SujetEnCours._Avatar, "", "6524f115.jpg", false, "http://img.atelier801.com/");
		}
		var NombreReponse:int = int(LsSujet[i+6]);
		SujetEnCours.Reponse.styleSheet = _root.StyleTexte;
		SujetEnCours.Reponse.htmlText = NombreReponse+" "+_root.Pluriel("message", NombreReponse)+",\n<font color='#6C77C1'>"+LsSujet[i+7]+"</font>, "+_root.Formatage_Date(LsSujet[i+1]);

		// colorisation
		var DernierMessage : int = int(LsSujet[i+1]);
		if (DernierMessage >= _root.DerniereCoForum) {
			SujetEnCours._Fond.NonLu.visible = true;
		}
		
		// gestion des pages 1..2..DernièrePage (oui à l'arrache :/)
		var NbPages:int = 1+(NombreReponse-1)/20;
		SujetEnCours.NbPages=NbPages;
		SujetEnCours.Multipages.buttonMode = true;
		if (NbPages<2){
			SujetEnCours.Page2.visible=false;
			SujetEnCours.LastPage.visible=false;
			SujetEnCours.Multipages.visible=false;
			SujetEnCours.LastPage.visible=false;
		}else{
			SujetEnCours.Multipages.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Page1);
			SujetEnCours.Page2.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Page2);
			if (NbPages==2){
				SujetEnCours.LastPage.visible=false;
			}else{
				SujetEnCours.LastPage.addEventListener(MouseEvent.MOUSE_DOWN, Clique_LastPage);
			}
		}

		SujetEnCours._Avatar.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Avatar);


		SujetEnCours.Id = LsSujet[i];
		//
		SujetEnCours.Auteur.mouseEnabled = false;
		SujetEnCours.Reponse.mouseEnabled = false;
		SujetEnCours.Titre1.mouseChildren = false;
		SujetEnCours.Titre1.mouseEnabled = false;
		SujetEnCours.Fond1.mouseChildren = false;
		SujetEnCours.Fond1.mouseEnabled = false;
		//SujetEnCours._Avatar.mouseChildren = false;
		//SujetEnCours._Avatar.mouseEnabled = false;
		SujetEnCours._Fond.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Sujet);
		SujetEnCours._Fond.addEventListener(MouseEvent.MOUSE_OVER, Over_Sujet);
		SujetEnCours._Fond.addEventListener(MouseEvent.ROLL_OUT, Out_Sujet);
		Z++;
	}
	_root.Ascenseur_Reset(Ascenseur);
	ClipListe1.visible = true;
	visible = true;
	Ascenseur.Glisseur.y = GlisseurY;
	Ascenseur.Clip.y = ClipY;
}

function Clique_Page1(E:MouseEvent):void {
	Lire_Sujet(E.currentTarget.parent);
	_root.Envoie_Serveur("FoS#"+E.currentTarget.parent.Id+"#0");
}
function Clique_Page2(E:MouseEvent):void {
	Lire_Sujet(E.currentTarget.parent);
	_root.Envoie_Serveur("FoS#"+E.currentTarget.parent.Id+"#1");
}
function Clique_LastPage(E:MouseEvent):void {
	var Sujet:MovieClip = E.currentTarget.parent;
	Lire_Sujet(Sujet);
	_root.Envoie_Serveur("FoS#"+Sujet.Id+"#"+(Sujet.NbPages-1));
}
function Clique_Avatar(E:MouseEvent):void {
	var Sujet:MovieClip = E.currentTarget.parent;
	var Options:Array = new Array();
	if (_root.MODOFORUM) {
		if (Sujet.SujetClos) {
			Options.push("Ouvrir la discussion", Ouverture_Sujet, Sujet.Id);
		} else {
			Options.push("Fermer la discussion", Fermeture_Sujet, Sujet.Id);
		}
		//if (!_root.RECRUEFORUM) {
			Options.push("Supprimer la discussion", Suppression_Sujet, Sujet.Id);
		//}
		Options.push("Déplacer la discussion", Déplacer_Sujet, Sujet.Id);
		Options.push("Editer le titre", Editer_Titre, Sujet);
		Options.push("Post-it", Postit_Sujet, Sujet.Id);
		Options.push("Topic Animateur", TopicAnim_Sujet, Sujet.Id);
		Options.push("Copier le lien de la discussion",Lien_Sujet,Sujet.Id);


	}
	_root.Vignette_Menu(Options);
}

function Editer_Titre(Sujet:MovieClip) {
	var FenetreTitre:MovieClip = new $EditTitre();
	FenetreTitre.Init(this,Sujet.Titre1.Titre2.Texte.text,Sujet.Id);
}

function Déplacer_Sujet(id:int) {
	var FenetreDeplacer:MovieClip = new $MoveSujet();
	FenetreDeplacer.Init(this,id);
}

function Fermeture_Sujet(id:int) {
	GlisseurY = Ascenseur.Glisseur.y;
	ClipY = Ascenseur.Clip.y;
	_root.Envoie_Serveur("FoX#1#"+id);
}
function Ouverture_Sujet(id:int) {
	GlisseurY = Ascenseur.Glisseur.y;
	ClipY = Ascenseur.Clip.y;
	_root.Envoie_Serveur("FoX#0#"+id);
}

function Suppression_Sujet(Id:int):void {
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir supprimer cette discussion ?",P_Supprimer_Sujet,Id);
}

function Postit_Sujet(Id:int):void {
	_root.Envoie_Serveur("FoPI#"+Id);
}

function TopicAnim_Sujet(Id:int):void {
	_root.Envoie_Serveur("FoTA#"+Id);
}

function Lien_Sujet(Id:int):void {
	var Lien : String = "<a href='event:t@"+Id+"'>Lien</a>";
	System.setClipboard(Lien);
}

function Recherche_Sujet_Event(E:Event) {
	Recherche_Sujet();
}

function Recherche_Sujet():void {
	var FenetreChercher:MovieClip = new $ChercherSujet();
	FenetreChercher.Init(this);
}

function P_Supprimer_Sujet(Id:int){
	GlisseurY = Ascenseur.Glisseur.y;
	ClipY = Ascenseur.Clip.y;
	_root.Envoie_Serveur("FoD#"+Id);
}


function Clique_Sujet(E:MouseEvent):void {
	Lire_Sujet(E.currentTarget.parent);
	_root.Envoie_Serveur("FoS#"+E.currentTarget.parent.Id);
}

function Lire_Sujet(Sujet:MovieClip):void {
	ClipListe1.visible = false;
	Ascenseur.visible = false;
	var Forum:MovieClip = parent as MovieClip;
	Forum.AffichageSujet.Titre.text = Sujet.Titre1.Titre2.Texte.text;
	Forum.AffichageSujet.SujetClos = Sujet.SujetClos;
	GlisseurY = Ascenseur.Glisseur.y;
	ClipY = Ascenseur.Clip.y;
	
	if (CodeForumEnCours == 11) {
		var DebTitre:String = "[" + _root.IdTeam + "]";
		Forum.AffichageSujet.ModifAutoriséeFofoTeam = ((_root.LEADER || _root.SCRIBE) && Forum.AffichageSujet.Titre.text.substr(0, DebTitre.length) == DebTitre);
	} else {
		Forum.AffichageSujet.ModifAutoriséeFofoTeam = false;
	}
}

function Over_Sujet(E:MouseEvent):void {
	E.currentTarget.gotoAndPlay(2);
	E.currentTarget.parent.Fond1.gotoAndPlay(2);
}

function Out_Sujet(E:MouseEvent):void {
	E.currentTarget.gotoAndPlay(21);
	E.currentTarget.parent.Fond1.gotoAndPlay(21);
}

NouveauSujet.addEventListener(MouseEvent.MOUSE_DOWN, Nouveau_Sujet);
function Nouveau_Sujet(E:Event):void {
	visible = false;
	_root._Forum.AffichageSujet.Titre.text = "Tapez votre titre ici";
	var NouveauSujet:Array = new Array(0, null, _root.NomJoueur, _root.Avatar, "Tapez votre message ici");
	_root._Forum.AffichageSujet.Nouveau_Sujet(NouveauSujet);
}

Rechercher.addEventListener(MouseEvent.MOUSE_DOWN, Recherche_Sujet_Event);

Retour1.addEventListener(MouseEvent.MOUSE_DOWN, Retour_Forums);
Retour2.addEventListener(MouseEvent.MOUSE_DOWN, Retour_Forums);
function Retour_Forums(E:Event = null):void {
	visible = false;
	_root._Forum.Initialisation(null);
}

var OptionPage:Array;
ClipPage.buttonMode = true;
ClipPage.useHandCursor = true;
ClipPage.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Page);
function Clique_Page(E:Event):void {
	if (OptionPage.length > 3) {
		_root.Vignette_Menu(OptionPage);
	}
}
ClipPage.addEventListener(MouseEvent.MOUSE_OVER, Over_Page);
function Over_Page(E:Event):void {
	ClipPage.gotoAndStop(2);
}
ClipPage.addEventListener(MouseEvent.MOUSE_OUT, Out_Page);
function Out_Page(E:Event):void {
	ClipPage.gotoAndStop(1);
}

function Changement_Page(NUM:int):void {
	visible = false;
	_root.Envoie_Serveur("FoF#"+CodeForumEnCours+"#"+NUM);
	GlisseurY = 0;
	ClipY = 0;
}

function Atteindre_Page(NUM:int) {
	var Aller:MovieClip = new $AllerPage();
	Aller.Init(this,NUM);

}