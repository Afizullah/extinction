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

import flash.display.MovieClip;
import flash.events.Event;

var _root:MovieClip = MovieClip(parent);
visible = false;

var Chat:MovieClip;
var AncienXChat:int;
var AncienYChat:int;

////
NewMessage.visible = true;
NewMessage.Griser();

var ListeForum:Array =
        ["Forum général",
        "Pour parler de tout et de rien.",
        "6",
        "7ba4f115.png",

        "Forum bugs",
        "Si vous rencontrez des bugs, c'est par ici que ça se passe. <font color='#7C7E9E'>(Pour les suggestions, utilisez le forum du jeu en question)",
        "5",
        "7b24f115.png",

        "Atelier des artistes",
        "Forum dédié aux créations : textes, avatars et images diverses.",
        "8",
        "7aa4f115.png",

        "Forum Aaaah !",
        "Espace de discussion pour les joueurs de Aaaah !",
        "2",
        "7a24f115.png",

        "Forum Bouboum",
        "Espace de discussion pour les joueurs de Bouboum.",
        "3",
        "79a4f115.png",

        "Forum Forteresse",
        "Espace de discussion pour les joueurs de Forteresse.",
        "4",
        "7924f115.png"];


_root.Vignette(NewMessage,"");
NewMessage.VignetteTexte = "";


var CodeForum:String;
var SujetEnCours:Array;

function Initialisation(ChatDInterface:MovieClip):void {
	_root.Inaction = 0;
	
	////
	if (ChatDInterface != null) { // Retour depuis la liste des forums
		_root._Interface.Activation_Salon(false); // On met en petit chat
		Chat = ChatDInterface;
		AncienXChat = Chat.x;
		AncienYChat = Chat.y;
		Chat.x = 356;
		Chat.y = 32;
		Chat.InitPourForum();
		addChild(Chat);
	}
	
	////
	Sujets.ClipListe1.visible = false;
	Sujets.Ascenseur.visible = false;
	AffichageSujet.visible = false;
	Sujets.visible = false;
	//
	while (Forums.ListeForum.numChildren != 0) {
		Forums.ListeForum.removeChildAt(0);
	}
	//
	var NbForum:int = ListeForum.length;
	//
	for (var i:int = 0; i < NbForum; i=i+4) {
		var Forum:MovieClip;
		if (i > 20) {
			Forum = new $ClipForumMini();
			Forum.AvatarModo.visible = (ListeForum[i + 2] == "0");
		} else {
			Forum = new $ClipForum();
		}
		
		if (ListeForum[i + 2] == "0") {
			Forum.AvatarModo.visible = (ListeForum[i + 2] == "0");
			/*var Image:MovieClip = new $ImgForumM();
			Image.x = 5;
			Image.y = 5;
			Forum.Image._Image.Img = Image;
			Forum.Image._Image.addChild(Image); */
		}
		
		Forums.ListeForum.addChild(Forum);
		Forum.Fond1.gotoAndStop(1);
		if (i > 20) {
			Forum.y = 900-(i/4)*85;
			Forum.x = 415;
		} else if (i > 8) {
			Forum.y = (i/4)*85+10;
		} else {
			Forum.y = (i/4)*85;
		}
		Forum.Texte.htmlText = "<font size='14'>"+ListeForum[i]+"</font>\n<font color='#6C77C1'>"+ListeForum[i+1];
		if (ListeForum[i + 2] != "0") {
			_root.Chargement_Image(Forum.Image, "", ListeForum[i+3], false, "http://img.atelier801.com/");
		}
        
		//
		Forum.NomForum = ListeForum[i];
		Forum.CodeForum = ListeForum[i+2];
		Forum.Texte.mouseEnabled = false;
		Forum.Fond1.mouseChildren = false;
		Forum.Fond1.mouseEnabled = false;
		Forum.Image.mouseChildren = false;
		Forum.Image.mouseEnabled = false;
		Forum.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Forum);
		Forum.addEventListener(MouseEvent.MOUSE_OVER, Over_Forum);
		Forum.addEventListener(MouseEvent.ROLL_OUT, Out_Forum);
	}
	//
	visible = true;
	Forums.visible = true;
	Forums.ListeForum.visible = true;
	Sujets.GlisseurY = 0;
	Sujets.ClipY = 0;
	if (NewMessage.VignetteTexte == "") {
		NewMessage.visible = true;////
		NewMessage.Griser();
	}
}

function Initialisation_Forum(FORUM:Array,PAGE:int,PAGE_MAX:int):void {
	Sujets.visible = true;
	Forums.visible = false;
	Sujets.Affichage_Forum(FORUM,PAGE,PAGE_MAX);
}

function Clique_Forum(E:MouseEvent):void {
	Forums.ListeForum.visible = false;
	Sujets.NomForum.text = E.currentTarget.NomForum;
	Sujets.CodeForumEnCours = E.currentTarget.CodeForum;
	_root.Envoie_Serveur("FoF#"+E.currentTarget.CodeForum+"#0");
}

function Over_Forum(E:MouseEvent):void {
	E.currentTarget.Fond1.gotoAndPlay(2);
}

function Out_Forum(E:MouseEvent):void {
	E.currentTarget.Fond1.gotoAndPlay(21);
}

Forums.Retour1.addEventListener(MouseEvent.MOUSE_DOWN, Retour_L);
Forums.Retour.addEventListener(MouseEvent.MOUSE_DOWN, Retour_L);
function Retour_L(E:Event):void {
	visible = false;
	NewMessage.visible = false;///
	NewMessage.VignetteTexte = "";
	
	////
	Chat.x = AncienXChat;
	Chat.y = AncienYChat;
	Chat.InitPourInterface();
	removeChild(Chat);
	
	if (!_root._Interface.contains(_root._Interface.ChatV2)) {
		_root._Interface.addChild(_root._Interface.ChatV2);
	}
	_root._Interface.Chat_Bas(null);
	////
	
	if (_root.PetitChat/*_root._Interface.ChatV2._ChatSortie.height <= 167*/){
		_root._ListePartie.visible = true;
	}
	_root._Interface.visible = true;
	_root.Envoie_Serveur("CxVF#1");
}

NewMessage.addEventListener(MouseEvent.MOUSE_DOWN, Clique_NewMessage);
NewMessage.buttonMode = true;
function Clique_NewMessage(E:Event):void {
	if (!NewMessage.EstGrise()) {
		Retour_L(E);
	}
}

DeployerChat.buttonMode = true;
DeployerChat.addEventListener(MouseEvent.MOUSE_DOWN, Clique_DeployerChat);
function Clique_DeployerChat(E:Event):void {
	Chat.visible = !Chat.visible;
}