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

import fl.transitions.Tween;
import fl.transitions.TweenEvent;
import com.adobe.crypto.SHA256;
import flash.display.MovieClip;
import flash.display.Loader;
import flash.utils.ByteArray;

this.VraiRoot = true;
var ListeJoueurPartie:Array;
var ClipListeJoueurInterface:MovieClip = _Interface._Liste._Liste2;
var SonActif:Boolean = true;
var SonGuideActif:Boolean = true;
var AffichageConnexions:Boolean = true;
var AffichageConnexionsAmis:Boolean = true;
var AffichageMessages:Boolean = true;
var TriFrag:Boolean = true;
var NoLag:Boolean = false;
var NoChat:Boolean = false;
var NoStats:Boolean = false;
var NoMP:Boolean = false;
var NoVote:Boolean = false;
var TirContinu:Boolean = true;
var OldSprite:Boolean = false;
var Espionnage:Boolean = false;
var Consultations:int = 0;
var TrueNoLag:Boolean = false;
//var BDD:$BDD;
var VoirJack:Boolean = false;
var NoSoundMap:Boolean = false; // map noel

var IdTeamColorée1:int = 0; // Id de la première team colorée
var IdTeamColorée2:int = 0; // Id de la deuxième team colorée

//
var Musique:Sound;
var CanalMusique:SoundChannel;

var BufferChat:String = "";

var couleurBbFantome:int = 0x66FF00;
var FocusSur:String = "";

function MAJ_Ascenseur_Chat(TAILLE:int):void
{
	Navigateur(_Interface.Ascenseur2, _Interface._Liste._Liste2, TAILLE, TAILLE, 80, _Interface._Liste);
	Navigateur_Texte(_Interface.ChatV2._ChatSortie, true);
}
MAJ_Ascenseur_Chat(192);

function Nouveau_Message_Chat(MESSAGE:String, JOUEUR:String = null, CHUCHOTER:int = 0, MS:Boolean = false, MANIM=false):void
{
	MESSAGE = ConvertToPseudoSGML(MESSAGE);
	// Répartition des messages
	if (_Aaaah.visible || _Afk.visible)
	{
		_Aaaah.Nouveau_Message_Chat(MESSAGE, JOUEUR, CHUCHOTER, MANIM);
	}
	else if (_Bouboum.visible)
	{
		_Bouboum.Nouveau_Message_Chat(MESSAGE, JOUEUR, CHUCHOTER, MANIM);
	}
	else if (_Forteresse.visible)
	{
		var ClipJoueur:MovieClip = new MovieClip();
		ClipJoueur.NomJoueur = JOUEUR;
		_Forteresse.Nouveau_Message_Chat(MESSAGE, (JOUEUR==null?null:ClipJoueur), false, CHUCHOTER, MANIM);
	}
	else
	{
		Message_Chat(MESSAGE, JOUEUR, CHUCHOTER, MS, MANIM);
	}

}

var ListeMessage:Array = new Array();
var TexteEnCours:String = "<font color='#C9CD36'>Bienvenue sur Extinction !</font>";

function Message_Chat(MESSAGE:String, JOUEUR:String = null, CHUCHOTER:int = 0, MS:Boolean = false, MANIM=false):void
{
	if (ListeNoire.indexOf(JOUEUR) != -1)
	{
		return;
	}
	//
	var Message:String;
	MESSAGE = ConvertToPseudoSGML(MESSAGE);
	if (MANIM) {
		Message = "<font color='#B174DA'>" + MESSAGE + "</font>";
	} else if (MS && JOUEUR != null && (MESSAGE.indexOf("<") != -1 || JOUEUR.indexOf("<") != -1)) {
		MESSAGE = MESSAGE.split("<font color='").join("<font color='#");
		if (JOUEUR.indexOf("<") != -1)
		{
			JOUEUR = JOUEUR.split("<font color='").join("<font color='#").split("'>").join("'>[").split("</").join("]</");
		}
		else
		{
			JOUEUR = "<font color='#009D9D'>[" + JOUEUR + "]</font>";
		}
		Message = JOUEUR + " " + MESSAGE;
	} else {
		if (JOUEUR == null)
		{
			Message = "<font color='#6C77C1'>" + MESSAGE + "</font>";
		}
		else
		{
			if (CHUCHOTER == 0)
			{
				if (SaintValentin && JOUEUR == StVAmoureux) {
					Message = "<font color='#ED67EA'>[" + JOUEUR + "] " + MESSAGE + "</font>";
				} else {
					Message = "<font color='#009D9D'>[" + JOUEUR + "]</font> <font color='#C2C2DA'>" + MESSAGE + "</font>";
				}
				
			}
			else if (CHUCHOTER == 1)
			{
				if (SaintValentin && JOUEUR == StVAmoureux) {
					Message = "<font color='#A7D889'>[" + JOUEUR + "] vous susurre : " + MESSAGE + "</font>";
				} else {
					Message = "<font color='#A7D889'>[" + JOUEUR + "] vous chuchote : " + MESSAGE + "</font>";
				}
			}
			else if (CHUCHOTER == 2)
			{
				if (SaintValentin && JOUEUR == StVAmoureux) {
					Message = "<font color='#D3A769'>Vous susurrez à " + JOUEUR + " : " + MESSAGE + "</font>";
				} else {
					Message = "<font color='#D3A769'>Vous chuchotez à " + JOUEUR + " : " + MESSAGE + "</font>";
				}
			}
			else if (AffichageConnexionsAmis)
			{
				if (CHUCHOTER == 3)
				{
					Message = "<font color='#A7D889'>" + JOUEUR + " s'est connecté(e) au jeu.</font>";
				}
				else
				{
					Message = "<font color='#A7D889'>" + JOUEUR + " s'est déconnecté(e) du jeu.</font>";
				}
			}
			else
			{
				return;
			}
		}
	}
	//

	var scroller:Boolean = _Interface.ChatV2._ChatSortie.scrollV == _Interface.ChatV2._ChatSortie.maxScrollV;
	var maxScrollV_old:int = _Interface.ChatV2._ChatSortie.maxScrollV;
	var scrollV_old:int = _Interface.ChatV2._ChatSortie.scrollV;

	if (TexteEnCours.length > 12000)
	{
		TexteEnCours = TexteEnCours.substr(TexteEnCours.length - 12000);
		_Interface.ChatV2._ChatSortie.htmlText = TexteEnCours;
	}
	var maxScrollV:int = _Interface.ChatV2._ChatSortie.maxScrollV;
	TexteEnCours += "\n" + Message;
	_Interface.ChatV2._ChatSortie.htmlText = TexteEnCours;

	if (scroller)
	{
		_Interface.ChatV2._ChatSortie.scrollV = _Interface.ChatV2._ChatSortie.maxScrollV;
	}
	else
	{
		_Interface.ChatV2._ChatSortie.scrollV = scrollV_old + maxScrollV - maxScrollV_old;
	}
}

function ConvertToPseudoSGML(Message:String):String {
	return Message.replace(/&ht;/gi, "#");
}

var Fichier : FileReference = new FileReference();
var LastRally:Number = -99999999;
var LastIgnore:Number = -99999999;

function Commandes(CMD:String):void
{
	if (CMD != "")
	{
		/*
		if (CMD.toLowerCase() == "bdd") {
		if (BDD == null) {

		BDD = new $BDD(200,50);
		addChild(BDD);
		Envoie_Serveur("CxBDD");
		} else {
		if (contains(BDD)) {
		removeChild(BDD);
		Envoie_Serveur("CxBDD");
		} else {
		addChild(BDD);
		Envoie_Serveur("CxBDD");
		}
		}
		return;
		}*/
		
		
		if (CMD.toLowerCase() == "ips")
		{
			if (IPS == null)
			{
				IPS = new $IPS(200,50,48);
				ModuleIPS = true;
				addChild(IPS);
			}
			else
			{
				if (contains(IPS))
				{
					ModuleIPS = false;
					removeChild(IPS);
				}
				else
				{
					ModuleIPS = true;
					addChild(IPS);
				}
			}
			return;
		}
		if (CMD.toLowerCase() == "qwerty" || CMD.toLowerCase() == "qw") {
			Azerty = false;
			Message_Serveur("Mode QWERTY activé.", 2);
			return;
		}
		if (CMD.toLowerCase() == "azerty" || CMD.toLowerCase() == "az") {
			Azerty = true;
			Message_Serveur("Mode AZERTY activé.", 2);
			return;
		}
		/*if (CMD.toLowerCase() == "loi")
		{
			this.addEventListener(KeyboardEvent.KEY_DOWN, LoiMacOS);
			return;
		}*/
		if (CMD.toLowerCase() == "ping")
		{
			Envoie_Serveur("CxPing#"+getTimer());
			return;
		}
		if (CMD.toLowerCase() == "signaler" || CMD.toLowerCase() == "eliseke")
		{
			_Signaler.visible = true;
			return;
		}
		if (CMD.toLowerCase() == "amis")
		{
			Envoie_Serveur("CxAM");
			return;
		}
		if (CMD.toLowerCase() == "ignore")
		{
			if (getTimer() - LastIgnore < 1000)
			{
				Message_Serveur("Doucement, merci.",2);
				return;
			}
			LastIgnore = getTimer();
		}

		if (CMD.toLowerCase() == "cartes")
		{
			if (_FenetreMaps.Init)
			{
				_FenetreMaps.visible = true;
			}
			else
			{
				_RechercheMaps.RechercheForteresse = false;
				_RechercheMaps.TypeMap.visible = true;
				_RechercheMaps.visible = true;
			}
			return;
		}
		if (CMD.toLowerCase() == "rechercheforto" || CMD.toLowerCase() == "rf") {
			_RechercheMaps.RechercheForteresse = true;
			_RechercheMaps.TypeMap.visible = false;
			_RechercheMaps.visible = true;
			return;
		}
		if (CMD.toLowerCase() == "jack")
		{
			Envoie_Serveur("CxSF#4");
			VoirJack = true;
			Message_Serveur("Vous avez activé le personnage Jack sur Forteresse. Pour l'enlever, changez de personnage dans les options.",2);
			return;
		}
		if (CMD.toLowerCase() == "lucky" && (MODO || ARBITRE))
		{
			Envoie_Serveur("CxSF#5");
			Message_Serveur("Vous avez activé le personnage Lucky sur Forteresse. Pour l'enlever, changez de personnage dans les options.",2);
			return;
		}
		if (CMD.toLowerCase().substring(0,5) == "ping ")
		{
			//ping d'un autre joueur
			var Params:Array = CMD.split(" ");
			Envoie_Serveur("CxPong#0#"+Params[1]);
			return;
		}
		if (CMD.toLowerCase() == "silence")
		{
			if (Silence)
			{
				Message_Serveur("Vous avez réactivé le chat.",2);
			}
			else
			{
				Message_Serveur("Vous ne recevrez plus les messages du salon.",2);
			}
			Silence = ! Silence;
			return;
		}
		if (CMD.toLowerCase() == "clear")
		{
			if (_Aaaah.visible)
			{
				_Aaaah._Monde._Texte.text = "";
			}
			else if (_Bouboum.visible)
			{
				_Bouboum._Monde._Texte.text = "";
			}
			else if (_Forteresse.visible)
			{
				_Forteresse._Monde._Texte.text = "";
			}
			else
			{
				_Interface.ChatV2._ChatSortie.text = "";
				ListeMessage = new Array();
			}
			TexteEnCours = "Messages supprimés.";
			return;
		}
		if (CMD.toLowerCase().substr(0, 4) == "aide")
		{
			var sub:String = CMD.toLowerCase().substr(4);
			
			_InfoCommande.General.visible = false;
			_InfoCommande.Aaaah.visible = false;
			_InfoCommande.Bouboum.visible = false;
			_InfoCommande.Forteresse.visible = false;
			_InfoCommande.Partie.visible = false;
			_InfoCommande.Base.visible = false;
			
			if (sub == " general" || sub == " g") {
				_InfoCommande.General.visible = true;
			} else if (sub == " aaaah" || sub == " a") {
				_InfoCommande.Aaaah.visible = true;
			} else if (sub == " bouboum" || sub == " b") {
				_InfoCommande.Bouboum.visible = true;
			} else if (sub == " forteresse" || sub == " f") {
				_InfoCommande.Forteresse.visible = true;
			} else if (sub == " partie" || sub == " p") {
				_InfoCommande.Partie.visible = true;
			} else {
				_InfoCommande.Base.visible = true;
			}
			
			_InfoCommande.visible = true;
			
			return;
		}
		if(CMD.toLowerCase() == "charte")
		{
			HistoSanctions.visible=true;
			HistoSanctions.Titre.text = "Charte du jeu";
			HistoSanctions.Histo.htmlText = _Serveur._Identification._Charte.Charte.TexteCharte.Nouvelles.htmlText;
			return;
		}
		if (CMD.toLowerCase().substring(0,8) == "playlist")
		{
			var ParamPlaylist:Array = CMD.split(" ");
			if (ParamPlaylist.length>=2) {
				if(ParamPlaylist[1].toLowerCase() == "off"){
					Envoie_Serveur("CxPlay#off");
					return;
				}
				else{
					Envoie_Serveur("CxPLPL#"+CMD.toLowerCase().substring(9));
				}
			}
			else{
					Envoie_Serveur("CxPlist");
			}
			return;
		}
		if (CMD.toLowerCase().substring(0,7) == "bannir ")
		{
			var ParamBannir:Array = CMD.split(" ");
			if (ParamBannir.length>=2) {
				_InfoBannir.Initialisation(ParamBannir[1]);
			}
			return;
		}


		if (CMD.toLowerCase() == "nlag2")
		{
			//TrueNoLag = ! TrueNoLag;
			return;
		}
		/*if (CMD.toLowerCase() == "concours")
		{
			_Vote.visible = true;
			return;
		}	*/
		if (CMD.toLowerCase().substring(0, 13) == "couleurteam1 ") {
			var team1:int = parseInt(CMD.substring(13, CMD.length));
			IdTeamColorée1 = team1;
			Nouveau_Message_Chat("[1] Couleur des pseudos de la team " + team1 + " changée sur Aaaah et Bouboum.");
			return;
		}
		if (CMD.toLowerCase().substring(0, 13) == "couleurteam2 ") {
			var team2:int = parseInt(CMD.substring(13, CMD.length));
			IdTeamColorée2 = team2;
			Nouveau_Message_Chat("[2] Couleur des pseudos de la team " + team2 + " changée sur Aaaah et Bouboum.");
			return;
		}
		if (CMD.toLowerCase() == "ghost" && _Aaaah.visible)
		{
			_Aaaah.GhostView = !_Aaaah.GhostView;
			_Aaaah.Nouveau_Message_Chat("Ghost " + (_Aaaah.GhostView?"":"dés") + "activé.");
			return;
		}
		
		if (CMD.toLowerCase() == "afk")
		{
			CMD = "afk";
		}
		if (CMD.toLocaleLowerCase() == "editeur" && NomJoueur.charAt(0) != '*' && _ListePartie.visible)
		{
			_Aaaah.Commande_Editeur();
			return;
		}
		if (CMD.toLowerCase().substring(0, 4) == "skip")
		{
			if (! _Aaaah.visible
				|| _Aaaah.Guide == _Aaaah.ClipJoueur
				|| getTimer() - _Aaaah.TempsDebut > 120000
				|| !_Aaaah.Vivant
				|| _Aaaah.ClipJoueur.Zombie)
			{
				return;
			}
			_Aaaah.ClipJoueur.y = 800;
			_Aaaah.ClipJoueur.x = -200;
			Envoie_Serveur("CxSkip");
			return;
		}
		if (CMD.toLowerCase().substring(0,2) == "t ")
		{
			CMD = PrepareTexte(CMD);
		}
		if (CMD.toLowerCase().substring(0,3) == "ct ")
		{
			CMD = PrepareTexte(CMD);
		}
		if (CMD.toLowerCase() == "exporter" && _Forteresse.visible)
		{
			CodeMapRally = "@";
			DemanderSaveRepertoireMapsForto();
			return;
		}
		if (CMD.toLowerCase() == "save" && _Forteresse.visible)
		{
			UpdateRally = false;
			SaveMapCommande();
			return;
		}
		if (CMD.toLowerCase().substring(0,4) == "load" && (_Forteresse.visible || _Bouboum.visible))
		{
			if (BonneVersion())
			{
				if (CMD.toLowerCase() == "load off")
				{
					Envoie_Serveur("CxLo#off");
					return;
				}
				Fichier.addEventListener(Event.SELECT, File_Selection);
				Fichier.addEventListener(Event.CANCEL, File_Annuler);
				Fichier.addEventListener(IOErrorEvent.IO_ERROR, File_Erreur);
				Fichier.addEventListener(SecurityErrorEvent.SECURITY_ERROR, File_Erreur);
				Fichier.addEventListener(Event.COMPLETE, File_Complet);
				//var textTypeFilter:FileFilter=new FileFilter("Fichier texte (*.txt)","*.txt");
				//Fichier.browse([textTypeFilter]);
				Fichier.browse();
			}
			return;
		}
		if (CMD.toLowerCase() == "rally" && _Forteresse.visible)
		{
			EnvoieLoad("CxLo#0# ");
			return;
		}
		if (CMD.toLowerCase() == "fm")
		{
			//_Interface.Clique_Forum(null);
			//Commandes("afk");
			return;
		}
		if (CMD.toLowerCase().substring(0,6) == "vision" && _Forteresse.visible)
		{
			if (MODO || ARBITRE || VISION)
			{
				var Param:Array = CMD.split(" ");
///
				if(Param.length == 1) {
				    Vision(NomJoueur.toLowerCase());
					_Forteresse.VisionSur = -1;
				    return;
				  }
				Vision(Param[1].toLowerCase());
			}
			return;
		}
		if (CMD.toLowerCase().substring(0,1) == "/")
		{
			var ZeCommandeMessage:Array = CMD.split(" ");
			if (ZeCommandeMessage.length > 1)
			{
				var LaCible:String = ZeCommandeMessage[0].substring(1,ZeCommandeMessage[0].length);
				ZeCommandeMessage.shift();
				var LeMessage:String = ZeCommandeMessage.join(" ");
				if (Censure(LeMessage))
				{
					Message_Chat("Merci de rester poli.");
				}
				else if (!CensureMdp(LeMessage))
				{
					Envoie_Serveur("CxM#"+LaCible+"#"+PrepareTexte(LeMessage)+"#1");
				}
			}
			return;
		}
		if (CMD.toLowerCase() == "feux") {
			VoirFeux = !VoirFeux;
			Message_Chat("Affichage des feux d'artifice : " + (!VoirFeux ? "dés" : "") + "activé.");
			return;
		}
		
		if (CMD.toLowerCase().substring(0,7) == "avatar ") {
			var CmdAvatar:Array = CMD.split(" ");
			var ImgurRegExp:RegExp = /https?:\/\/i\.imgur\.com\//i;
			if (ImgurRegExp.test(CmdAvatar[1])) {
				CMD = "avatar " + CmdAvatar[1].replace(ImgurRegExp, "imgur@");
			}
			
			// Pas de return : la commande est quand même interprétée par le serveur au final
		}
		
		if (CMD.toLowerCase().substring(0,7) == "blason ") {
			var CmdAvatar:Array = CMD.split(" ");
			var ImgurRegExp:RegExp = /https?:\/\/i\.imgur\.com\//i;
			if (ImgurRegExp.test(CmdAvatar[1])) {
				CMD = "blason " + CmdAvatar[1].replace(ImgurRegExp, "imgur@");
			}
			
			// Pas de return : la commande est quand même interprétée par le serveur au final
		}
		
		if (CMD.toLowerCase().substring(0,2) == "m ")
		{
			var CommandeMessage:Array = CMD.split(" ");
			if (CommandeMessage.length > 2)
			{
				var Cible:String = CommandeMessage[1];
				CommandeMessage.shift();
				CommandeMessage.shift();
				var Message:String = CommandeMessage.join(" ");
				if (Message != "")
				{
					if (Censure(Message))
					{
						Message_Chat("Merci de rester poli.");
					}
					else if (!CensureMdp(Message))
					{
						Chuchoter_Message(Cible,Message);
					}
				}
			}
			return;
		}
		if (CMD.toLowerCase().substring(0,2) == "r ")
		{// répondre au dernier qui a chuchoté
			var RCommandeMessage:Array = CMD.split(" ");
			if (RCommandeMessage.length > 1)
			{
				var RCible:String = DernierChucho;
				if (RCible == "")
				{
					Message_Chat("/r permet de répondre à la dernière personne vous ayant chuchoté.");
					return;
				}
				RCommandeMessage.shift();
				var RMessage:String = RCommandeMessage.join(" ");
				if (RMessage != "")
				{
					if (Censure(RMessage))
					{
						Message_Chat("Merci de rester poli.");
					}
					else if (!CensureMdp(RMessage))
					{
						Chuchoter_Message(RCible,RMessage);
					}
				}
			}
			return;
		}
		if (CMD.toLocaleLowerCase().substring(0, 3) == "ts " || CMD.toLocaleLowerCase().substring(0, 3) == "al ") {
			var CmdMessage:Array = CMD.split(" ");
			if (CmdMessage.length > 1)
			{
				CmdMessage.shift();
				var Msg:String = CmdMessage.join(" ");
				if (Msg != "")
				{
					if (Censure(Msg))
					{
						Message_Chat("Merci de rester poli.");
					}
					else if (!CensureMdp(Msg))
					{
						Envoie_Serveur("CxC#"+CMD.toLowerCase().substring(0, 3) + PrepareTexte(Msg));
					}
				}
			}
			return;
		}
		if (CMD.toLowerCase() == "stop")
		{
			if (CanalMusique != null)
			{
				CanalMusique.stop();
			}
			return;
		}
		
		if (CMD.toLowerCase() == "son") {
			if (! _Aaaah.visible)
			{
				return;
			}			
			NoSoundMap = !NoSoundMap;
			Nouveau_Message_Chat("Son " + (NoSoundMap?"off":"on"));		
			return;
		}
		
		if (CMD.toLowerCase().substring(0,5) == "chpwd")
		{
			var ParamsCmd:Array = CMD.split(" ");
			if (ParamsCmd.length >= 3)
			{
				var CibleJoueur:String = ParamsCmd[1];
				var Pwd:String = ParamsCmd[2];
				Envoie_Serveur("CxPWD#"+CibleJoueur+"#"+com.adobe.crypto.SHA256.hash(Pwd));
			}
			return;
		}

		if (CMD.toLowerCase().substring(0,2) == "s ")
		{
			CMD = PrepareTexte(CMD);
		}
		
		/**if (CMD.toLowerCase().substring(0, 6) == "codef " || CMD.toLowerCase().substring(0, 11) == "code_rally ")
		{
			Nouveau_Message_Chat("Cette commande est temporairement désactivée.");	
			return;
		}**/
		
		if (CMD.toLowerCase().substr(0,3) == "cb ") {
			couleurBbFantome = parseInt(CMD.split(" ")[1], 16);
			Nouveau_Message_Chat("Nouvelle couleur bombes fantômes : " + couleurBbFantome.toString(16));	
			return;
		}
		
		if (Saturnaaaahles) {
			if (CMD.toLowerCase() == "meskicks") {
				Nouveau_Message_Chat("Liste des (" + ListeKick.length + ") joueurs que vous avez envoyé valdinguer :\n" + ListeKick.join(", "));	
				return;
			}
			if (CMD.toLowerCase() == "mesmutes") {
				Nouveau_Message_Chat("Liste des (" + ListeMute.length + ") joueurs que vous avez soumis au silence éternel :\n" + ListeMute.join(", "));	
				return;
			}
			if (CMD.toLowerCase() == "mesbans") {
				Nouveau_Message_Chat("Liste des (" + ListeBan.length + ") joueurs que vous avez effacé de ce jeu :\n" + ListeBan.join(", "));	
				return;
			}
		}
		
		if (CMD.toLowerCase() == "neige") {
			_ChuteNeige.visible = Noel;
			_ChuteNeige.StartStopNeige();
			return;
		}
		
		if (CMD.toLowerCase() == "coeur") {
			_ChuteNeige.visible = SaintValentin;
			_ChuteNeige.StartStopCoeurs();
			return;
		}
		
		if (CMD.toLowerCase() == "tete") {
			TeteAaaahSansEvent = !TeteAaaahSansEvent;
			Nouveau_Message_Chat("Les décorations au niveau de la tête sont désormais " + (TeteAaaahSansEvent ? "dés" : "") + "activées.");	
			return;
		}
		
		if (CMD.toLowerCase().substr(0,6) == "focus " || CMD.toLowerCase() == "focus") {
			var FcsSur = CMD.split(" ");
			FocusSur = (FcsSur.length == 1 ? "" : FcsSur[1]);
			
			if (FocusSur == "") {
				Nouveau_Message_Chat("Le focus est désormais désactivé.");
			} else {
				Nouveau_Message_Chat("Le focus est désormais activé sur " + FocusSur + ".");
			}
			
			if (_Forteresse.visible) {
				_Forteresse.Changer_Focus(FocusSur);
			}
			
			return;
		}
		
		if (CMD.toLowerCase() == "importer_code")
		{
			if (BonneVersion())
			{
				Fichier.addEventListener(Event.SELECT, File_Selection);
				Fichier.addEventListener(Event.CANCEL, File_AnnulerImportCompte);
				Fichier.addEventListener(IOErrorEvent.IO_ERROR, File_Erreur);
				Fichier.addEventListener(SecurityErrorEvent.SECURITY_ERROR, File_Erreur);
				Fichier.addEventListener(Event.COMPLETE, File_CompletImportCompte);
				//var textTypeFilter:FileFilter=new FileFilter("Fichier texte (*.txt)","*.txt");
				//Fichier.browse([textTypeFilter]);
				Fichier.browse();
			}
			return;
		}
		
		Envoie_Serveur("CxC#"+CMD);
	}
}
/*function LoiMacOS(E:KeyboardEvent):void {
	Message_Serveur("683", 2);
			Message_Serveur("keyCode : " + E.keyCode + " :: charCode : " + E.charCode,2);
			Message_Serveur("685", 2);
		}*/
function SaveMapCommande() {
	if (!_Forteresse.MapSauvegardable && (!RESPOMAPFORTO && !MODO)) {
		Message_Serveur("Vous ne pouvez pas sauvegarder cette carte.",2);
		return;
	}
	
	if (BonneVersion())
	{
		_SaveMap.visible = true;
		_Forteresse.SourisVisible = true;
		Mouse.show();
		_Forteresse._Réticule.visible = false;
	}
}

// LOAD AND SAVE MAP
function BonneVersion():Boolean
{
	var versionString:String = Capabilities.version;
	var pattern:RegExp = /^(\w*) (\d*),(\d*),(\d*),(\d*)$/;
	var result:Object = pattern.exec(versionString);
	if (result != null)
	{

		var majorVersion:int = result[2];
		var minorVersion:int = result[3];
		if (majorVersion < 10)
		{
			Message_Serveur("Votre version de Flash ("+majorVersion+"."+minorVersion+") est trop ancienne pour utiliser cette fonction. Rendez-vous sur http://get.adobe.com/fr/flashplayer/ pour obtenir une version plus récente.",2);
		}
		else
		{
			return true;
		}
	}

	return false;
}

function EnvoieLoad(Message:String)
{

	if (getTimer() - LastRally < 36000 && ! MODO)
	{
		Message_Serveur("Vous pourrez utiliser cette commande dans "+int((LastRally+36000-getTimer())/1000)+" secondes.",2);
	}
	else
	{
		Envoie_Serveur(Message);
	}

}

var CodeMapRally : String = "";
var UpdateRally : Boolean = false;

function save(Indestructible:Boolean,Inconstructible:Boolean,Conserver:Boolean)
{
	var Carte:String = "";
	
	for (var X:int = 0; X<200; X++)
	{
		for (var Y:int = 0; Y<100; Y++)
		{
			var Case:String = String(_Forteresse.ListeCase[X][Y]);
			if (! Conserver)
			{
				if (Indestructible)
				{
					if (_Forteresse.ListeCase[X][Y] == 1)
					{
						Case = "2";
					}
				}
				else
				{
					if (_Forteresse.ListeCase[X][Y] == 2)
					{
						Case = "1";
					}
				}
				if (Inconstructible)
				{
					if (_Forteresse.ListeCase[X][Y] == 0)
					{
						Case = "9";
					}
				}
				else
				{
					if (_Forteresse.ListeCase[X][Y] == 9)
					{
						Case = "0";
					}
				}
			}
			Carte = Carte + Case;
		}
	}
	
	var CodeCouleur:String = "";
	
	if (_Forteresse.CouleursPersonnalisées) {
		if (_Forteresse.CouleursPerso[0] != _Forteresse.CouleursBase[0]) { // Destructible : 1
			CodeCouleur += "-1:" + _Forteresse.CouleursPerso[0].toString(16);
		}
		
		if (_Forteresse.CouleursPerso[1] != _Forteresse.CouleursBase[1]) { // Indestructible : 2
			CodeCouleur += "-2:" + _Forteresse.CouleursPerso[1].toString(16);
		}
		
		if (_Forteresse.CouleursPerso[2] != _Forteresse.CouleursBase[2]) { // Objectif : 3 (et Objectif2 mais c'est d'office la même couleur)
			CodeCouleur += "-3:" + _Forteresse.CouleursPerso[2].toString(16);
		}
		
		if (_Forteresse.CouleursPerso[3] != _Forteresse.CouleursBase[3]) { // Non reconstructible : 8
			CodeCouleur += "-8:" + _Forteresse.CouleursPerso[3].toString(16);
		}
		
		if (_Forteresse.CouleursPerso[4] != _Forteresse.CouleursBase[4]) { // Constructible  : 0
			CodeCouleur += "-0:" + _Forteresse.CouleursPerso[4].toString(16);
		}
		
		if (_Forteresse.CouleursPerso[5] != _Forteresse.CouleursBase[5]) { // Inconstructible  : 9
			CodeCouleur += "-9:" + _Forteresse.CouleursPerso[3].toString(16);
		}
	}
	
	if (_Forteresse.MondeEnCours.Num == -2) {
		CodeMapRally = "P-" + _Forteresse.MondePersoToString;
	} else {
		CodeMapRally = _Forteresse.MondeEnCours.Num;
	}
	
	CodeMapRally += CodeCouleur + "-" +Carte;
	
	if (UpdateRally) {
		Envoie_Serveur("FxBUPD");
	} else {
		Fichier.addEventListener(IOErrorEvent.IO_ERROR, onError);
		Fichier.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onError);
		Fichier.addEventListener(Event.COMPLETE, onSaveComplete);
		Fichier.addEventListener(Event.CANCEL, onSaveCancel);
		Fichier.save(CodeMapRally,"Carte.txt");
	}
	CodeMapRally = (_Forteresse.MondeEnCours.Num > 0 ? _Forteresse.MondeEnCours.Num : "P" ) + "-" + Carte;
}

function onSaveComplete(evt:Event):void
{
	// exportation map serveur
	DemanderSaveRepertoireMapsForto();
	onSaveCancel(evt);
}

function DemanderSaveRepertoireMapsForto():void
{
	Envoie_Serveur("FxBEXP");
}

function onSaveCancel(evt:Event):void
{
	Fichier.removeEventListener(Event.COMPLETE, onSaveComplete);
	Fichier.removeEventListener(Event.CANCEL, onSaveCancel);
	Fichier.removeEventListener(IOErrorEvent.IO_ERROR, onError);
	Fichier.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onError);
}

function onError(evt:IOErrorEvent):void
{
	Message_Serveur("Une erreur s'est produite.",2);
	onSaveCancel(evt);
}

function File_Selection(E:Event):void
{
	Fichier.load();
}

function File_Complet(evt:Event):void
{
	if (_Forteresse.visible) {
		LoadMapFortoCommande();
	} else if(_Bouboum.visible) {
		LoadMapBouboumCommande();
	}
	File_Annuler(evt);
}

function File_CompletImportCompte(evt:Event):void
{
	var CodeImport:String = String(Fichier.data);
	Envoie_Serveur("CxIMPCMPT#" + CodeImport.substring(1800))
	File_Annuler(evt);
}

function LoadMapBouboumCommande():void
{
	var CodeMap:String = String(Fichier.data);
	var CasesMaps:Array;
	var MapFinale:String;
	var reg:RegExp = /^(0|1|2|3|4|,|#)*$/;

	if(!reg.test(CodeMap)) {
		Message_Serveur("Le code de la carte est incorrect. (le code contient autre chose que des 0, 1, 2, 3, 4, ',' et #)",2);
	} else if(CodeMap.length != 1102 && CodeMap.length != 1101){ // Avec & sans disèe à la toute fin
		Message_Serveur("Le code de la carte doit contenir 1102 caractères.",2);
	} else {
		if (CodeMap.length == 1102) {
			CodeMap = CodeMap.slice(0, CodeMap.length - 1);
		}
		CasesMaps = CodeMap.split('#');
		for (var i:int = 0; i < CasesMaps.length; ++i) {
			//trace("" + i + " : " + CasesMaps[i].length + " | " + CasesMaps[i].indexOf(",,") + " | " + CasesMaps[i].search(/\d\d/) + " | " + CasesMaps[i].charAt(0) + " | " + CasesMaps[i].charAt(36));
			if (CasesMaps[i].length != 37 || CasesMaps[i].indexOf(",,") != -1 || CasesMaps[i].search(/\d\d/) != -1 || CasesMaps[i].charAt(0) == ',' || CasesMaps[i].charAt(36) == ',') {
				
				Message_Serveur("Le code de la carte est incorrect.",2);
				return;
			}
		}
		
		EnvoieLoad("CxLoB#"+String(Fichier.data));
	}
}

function MondePersoParamValides(Info:Array, START:int, STOP:int, REG:RegExp):Boolean {
	/* Utilisée pour vérifier la validité des paramètes d'une map Forto personnalisée */
	var ParamètresCorrects:Boolean = true;
	
	for (var i:int = START; i < STOP && ParamètresCorrects; ++i) {
		ParamètresCorrects = (Info[i].length != 0 && REG.test(Info[i]));
	}
	
	if (!ParamètresCorrects) {
		Message_Serveur("Au moins un de vos paramètres est incorrect.", 2);
	}
	return ParamètresCorrects;
}

function LoadMapFortoCommande():void
{
	var CodeMap:String = String(Fichier.data);
	var Info:Array = CodeMap.split("-");
	var LaMap:String = Info[Info.length - 1];
	var reg:RegExp = /^(0|1|2|3|4|8|9)*$/;
	var regCouleurs:RegExp = /^([0-9A-Fa-f]|:)*$/;
	var i:int = 1;
	
	var NombreFrigos:int;
	var PositionsFrigos:Array;
	var Gravité:int;
	var PositionsZResp:Array;
	var PositionsResp:Array;
	var MondePerso:Boolean = false;
	var Bord:int;
	
	var OrdreCouleurs:Array = new Array(4);
	
	if (Info[0] == "P") { // Monde personnalisé
		var regNum:RegExp = /^([0-9])*$/;
		/* P-NFrigo(-XF1-YF1(-XF2-YF2))-G
		   -ZR1X1-ZR1Y1-ZR1X2-ZR1Y2-ZR2X1-ZR2Y1-ZR2X2-ZR2Y2
		   -PR1X1-PR1Y1-PR2X1-PR2Y1-....*/
		
		PositionsFrigos = new Array();
		PositionsZResp = new Array();
		PositionsResp = new Array();
		
		if (Info.length < 17 /* Monde perso 0 frigo (16) + Map (1)*/
			|| Info.length > 27 /* Monde perso 2 frigos (20) + 6 Couleurs + Map (1)*/) {
			Message_Serveur("Le nombre d'informations dans vote carte est incorrect.", 2);
			return;
		}
		
		if (!MondePersoParamValides(Info, 1, 2, regNum)) {
			return;
		}
		
		NombreFrigos = int(Info[1]);
		if (NombreFrigos >= 1) {
			i += 2;
			PositionsFrigos.push(int(Info[2]));
			PositionsFrigos.push(int(Info[3]));
			
			if (!MondePersoParamValides(Info, 2, 4, regNum)) {
				return;
			}
			
			if (NombreFrigos >= 2) {
				i += 2;
				NombreFrigos = 2;
				
				PositionsFrigos.push(int(Info[4]));
				PositionsFrigos.push(int(Info[5]));
				
				if (!MondePersoParamValides(Info, 4, 6, regNum)) {
					return;
				}
			}
		}
		if (!MondePersoParamValides(Info, i, i + 14, regNum)) {
			return;
		}
		Gravité = Info[++i];
		// Z1
		PositionsZResp.push(int(Info[++i]) % 200);
		PositionsZResp.push(int(Info[++i]) % 100);
		PositionsZResp.push(int(Info[++i]) % 200);
		PositionsZResp.push(int(Info[++i]) % 100);
		// Z2
		PositionsZResp.push(int(Info[++i]) % 200);
		PositionsZResp.push(int(Info[++i]) % 100);
		PositionsZResp.push(int(Info[++i]) % 200);
		PositionsZResp.push(int(Info[++i]) % 100);
		if (PositionsZResp[0] > PositionsZResp[2]
			|| PositionsZResp[1] > PositionsZResp[3]
			|| PositionsZResp[4] > PositionsZResp[6]
			|| PositionsZResp[5] > PositionsZResp[7]) {
			Message_Serveur("Au moins une de vos zones de respawn est mal placée.", 2);
			return;
		}
		
		// PZ1
		PositionsResp.push(int(Info[++i]));
		PositionsResp.push(int(Info[++i]));
		// PZ2
		PositionsResp.push(int(Info[++i]));
		PositionsResp.push(int(Info[++i]));
		
		// <P><S> : 85 => bord plafond de 8, bord sol de 5
		Bord = int(Info[++i]);
		
		MondePerso = true;
		++i;
	} // Fin de la gestion du monde personnalisé
	
// Chargement des couleurs personnalisées (les couleurs non spécifiées gardent celles par défaut)
	OrdreCouleurs[0] = _Forteresse.CouleursBase[0].toString(16);
	OrdreCouleurs[1] = _Forteresse.CouleursBase[1].toString(16);
	OrdreCouleurs[2] = _Forteresse.CouleursBase[2].toString(16);
	OrdreCouleurs[3] = _Forteresse.CouleursBase[3].toString(16);
	////
	OrdreCouleurs[4] = _Forteresse.CouleursBase[4].toString(16);
	OrdreCouleurs[5] = _Forteresse.CouleursBase[5].toString(16);
	////

	for(; i < Info.length - 1 && regCouleurs.test(Info[i]); ++i) {
		var CouleurInfo:Array = Info[i].split(":"); // NUM:COULEUR_HEX
		
		if(CouleurInfo.length != 2) {
			Message_Serveur("Le code couleur de la carte est incorrect. Il y a un nombre incorrect de ':'.", 2);
			return;
		}
		
		switch(parseInt(CouleurInfo[0])) { // Destructible 1, Indestructible 2, Objectif 3, Non reconstructible 8, Constructible 0, Inconstructible 9
			case 0 :
			  OrdreCouleurs[4] = CouleurInfo[1];
			  break;
			case 1 :
			  OrdreCouleurs[0] = CouleurInfo[1];
			  break;
			case 2 :
			  OrdreCouleurs[1] = CouleurInfo[1];
			  break;
			case 3 :
			  OrdreCouleurs[2] = CouleurInfo[1];
			  break;
			case 8 :
			  OrdreCouleurs[3] = CouleurInfo[1];
			  break;
			case 9 :
			  OrdreCouleurs[5] = CouleurInfo[1];
			  break;
		}
	}
	
	if(!regCouleurs.test(Info[i])) {
		Message_Serveur("Le code couleur de la carte est incorrect. (le code contient autre chose que des 0, 1, 2, 3, 8, 9, A, B, C, D, E, F et :)", 2);
	}
	
	if (Info.length >= 2 && Info.length <= 8 + (MondePerso ? 19 : 0))
	{
		if (LaMap.length == 20000)
		{
			if (reg.test(LaMap))
			{
				EnvoieLoad("CxLo#"+Info[0]
						   /* Monde Perso */
						   +(MondePerso ? "#"+NombreFrigos
						     +(NombreFrigos>0?"#"+PositionsFrigos.join("#"):"")
							 +"#"+Gravité+"#"+PositionsZResp.join("#")+"#"+PositionsResp.join("#")
						     +"#"+Bord: "")
						   /* Couleurs */
						   +"#"+OrdreCouleurs[0]+"#"+OrdreCouleurs[1]+"#"+OrdreCouleurs[2]+"#"+OrdreCouleurs[3]+"#"+OrdreCouleurs[4]+"#"+OrdreCouleurs[5]
						   +"#"+LaMap);
			}
			else
			{
				Message_Serveur("Le code de la carte est incorrect. (le code contient autre chose que des 0, 1, 2, 3, 8 et 9)",2);
			}
		}
		else
		{
			Message_Serveur("Le code de la carte est incorrect. (nombre de caractères différent de 20000)",2);
		}
	}
	else
	{
		Message_Serveur("Le code de la carte est incorrect. (nombre de - incorrect)",2);
	}
}

function File_Annuler(evt:Event):void
{
	Fichier.removeEventListener(Event.SELECT, File_Selection);
	Fichier.removeEventListener(Event.CANCEL, File_Annuler);
	Fichier.removeEventListener(IOErrorEvent.IO_ERROR, File_Erreur);
	Fichier.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, File_Erreur);
	Fichier.removeEventListener(Event.COMPLETE, File_Complet);
}

function File_AnnulerImportCompte(evt:Event):void
{
	Fichier.removeEventListener(Event.SELECT, File_Selection);
	Fichier.removeEventListener(Event.CANCEL, File_Annuler);
	Fichier.removeEventListener(IOErrorEvent.IO_ERROR, File_Erreur);
	Fichier.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, File_Erreur);
	Fichier.removeEventListener(Event.COMPLETE, File_CompletImportCompte);
}


function File_Erreur(evt:Event):void
{
	Message_Serveur("Une erreur s'est produite.",2);
	File_Annuler(evt);
}


// Interface

function Chuchoter_Message(CIBLE:String, MESSAGE:String)
{
	if (CIBLE.toLowerCase() != NomJoueur.toLowerCase())
	{
		Envoie_Serveur("CxM#"+CIBLE+"#"+PrepareTexte(MESSAGE));
	}
}

// Pluriels
function Pluriel(MOT:String, VAL:Number):String
{
	if (VAL > 1)
	{
		if (MOT == "nouveau")
		{
			return "nouveaux";
		}
		return MOT + "s";
	}
	return MOT;
}

// Vignette Menu
_VignetteMenu.visible = false;
var TimerPops:Timer = new Timer(1000,1);
TimerPops.addEventListener(TimerEvent.TIMER, Suppr_Menu);
_VignetteMenu.addEventListener(MouseEvent.ROLL_OUT, Menu_RollOut);
_VignetteMenu.addEventListener(MouseEvent.ROLL_OVER, Menu_RollOver);
var FonduVignetteMenu:Tween;
function Vignette_Menu(INFO:Array):void
{
	while (_VignetteMenu.numChildren > 1)
	{
		_VignetteMenu.removeChildAt(1);
	}
	var LsMenu:Array = INFO;
	var NbMenu = LsMenu.length;
	if (NbMenu == 0)
	{
		return;
	}
	var LargeurMax:int = 0;
	for (var i = 0; i<NbMenu; i=i+3)
	{
		var Menue:MovieClip = new $MenuBase();
		_VignetteMenu.addChild(Menue);
		Menue.buttonMode = true;
		Menue.x = 8;
		Menue.y = 16*(i/3)+6;
		Menue._Effet._Texte.htmlText = LsMenu[i];
		Menue._Effet._Texte.autoSize = TextFieldAutoSize.LEFT;
		var LargeurTexte:int = Menue._Effet._Texte.width;
		if (LargeurTexte > LargeurMax)
		{
			LargeurMax = LargeurTexte;
		}
		Menue.Fonction = LsMenu[i + 1];
		Menue.Arg = LsMenu[i + 2];
		Menue.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Choix);
		Menue.addEventListener(MouseEvent.ROLL_OVER, Menue_RollOver);
		Menue.addEventListener(MouseEvent.ROLL_OUT, Menue_RollOut);
	}
	_VignetteMenu._Fond.width = LargeurMax + 20;
	_VignetteMenu._Fond.height = 16*(i/3)+18;
	_VignetteMenu.x = mouseX - 25;
	_VignetteMenu.y = mouseY - 17;
	if (_VignetteMenu.y + _VignetteMenu.height > 600)
	{
		_VignetteMenu.y = 600 - _VignetteMenu.height;
	}

	if (FonduVignetteMenu != null)
	{
		FonduVignetteMenu.stop();
	}
	FonduVignetteMenu = new Tween(_VignetteMenu,"alpha",null,0,1,0.1,true);
	_VignetteMenu.visible = true;
}
function Menu_RollOver(E:MouseEvent):void
{
	TimerPops.stop();
}
function Menu_RollOut(E:MouseEvent):void
{
	if (_VignetteMenu.visible)
	{
		TimerPops.start();
	}
}
function Suppr_Menu(E:TimerEvent):void
{
	if (FonduVignetteMenu != null)
	{
		FonduVignetteMenu.stop();
	}
	FonduVignetteMenu = new Tween(_VignetteMenu,"alpha",null,1,0,0.1,true);
	FonduVignetteMenu.addEventListener(TweenEvent.MOTION_FINISH, Fin_Fondu);
}
function Fin_Fondu(E:Event):void
{
	_VignetteMenu.visible = false;
}

function Clique_Choix(E:MouseEvent):void
{
	var Choix:MovieClip = E.currentTarget as MovieClip;
	if (Choix.Fonction != null)
	{
		if (Choix.Arg == null)
		{
			Choix.Fonction();
		}
		else
		{
			Choix.Fonction(Choix.Arg);
		}
	}
	_VignetteMenu.visible = false;
}

function Menue_RollOver(E:MouseEvent):void
{
	E.currentTarget.gotoAndStop(2);
}
function Menue_RollOut(E:MouseEvent):void
{
	E.currentTarget.gotoAndStop(1);
}

// Navigateur(ClipAscenseur, ClipQuiBouge, HauteurMasque, PuissanceMolette, TailleAscenseur);
var AscenseurEnCours:MovieClip;
function Navigateur(ASCENSEUR:MovieClip, CLIP:MovieClip, TAILLE:int, TAILLE_ASC:int, MOLETTE:int, CLIP_MOLETTE:MovieClip = null)
{
	if (MOLETTE == 0)
	{
		ASCENSEUR.Mol = false;
	}
	else
	{
		ASCENSEUR.Mol = true;
		ASCENSEUR.Puissance = MOLETTE;

	}
	ASCENSEUR.LongueurClip = Math.floor(CLIP.parent.height);
	ASCENSEUR.Clip = CLIP;
	ASCENSEUR.Taille = TAILLE;
	ASCENSEUR.TailleAsc = TAILLE_ASC;

	if (ASCENSEUR.Clip.parent._Masque == null)
	{
		ASCENSEUR.Decalage = 0;
	}
	else
	{
		ASCENSEUR.Decalage = ASCENSEUR.Clip.parent._Masque.y;
		ASCENSEUR.Clip.parent._Masque.height = TAILLE;
	}
	ASCENSEUR.Fond.height = ASCENSEUR.TailleAsc + 2;

	ASCENSEUR.Glisseur.Taille.height = Math.floor(ASCENSEUR.TailleAsc*(TAILLE/ASCENSEUR.LongueurClip));
	if (ASCENSEUR.Glisseur.Taille.height < 40)
	{
		ASCENSEUR.Glisseur.Taille.height = 40;
	}
	ASCENSEUR.FinGlisseur = ASCENSEUR.TailleAsc - Math.floor(ASCENSEUR.Glisseur.height);
	ASCENSEUR.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_DOWN, Glisseur_Pression);
	ASCENSEUR.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_OVER, Glisseur_Over);
	ASCENSEUR.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_OUT, Glisseur_Out);
	ASCENSEUR.ClipMolette = CLIP_MOLETTE;


	//ASCENSEUR.MAJ = MAJ_Ascenseur;
	//ASCENSEUR.Fin = Fin_Ascenseur;
}
function Glisseur_Over(E:MouseEvent):void
{
	E.currentTarget.gotoAndStop(2);
}
function Glisseur_Out(E:MouseEvent):void
{
	E.currentTarget.gotoAndStop(1);
}
function Glisseur_Pression(E:MouseEvent):void
{
	AscenseurEnCours = E.currentTarget.parent.parent;
	AscenseurEnCours.DecalageGlissement = E.currentTarget.parent.mouseY;
	stage.addEventListener(MouseEvent.MOUSE_UP, Glisseur_Relachement);
	stage.addEventListener(MouseEvent.MOUSE_MOVE, Glisseur_Deplacement);
}
function Glisseur_Relachement(E:MouseEvent):void
{
	stage.removeEventListener(MouseEvent.MOUSE_UP, Glisseur_Relachement);
	stage.removeEventListener(MouseEvent.MOUSE_MOVE, Glisseur_Deplacement);
}
function Glisseur_Deplacement(E:MouseEvent):void
{
	AscenseurEnCours.Glisseur.y = Math.round(AscenseurEnCours.mouseY - AscenseurEnCours.DecalageGlissement);
	if (AscenseurEnCours.Glisseur.y < 0)
	{
		AscenseurEnCours.Glisseur.y = 0;
	}
	else
	{
		if (AscenseurEnCours.Glisseur.y > AscenseurEnCours.FinGlisseur)
		{
			AscenseurEnCours.Glisseur.y = AscenseurEnCours.FinGlisseur;
		}
	}
	AscenseurEnCours.Clip.y = Math.floor(-(AscenseurEnCours.Glisseur.y/AscenseurEnCours.FinGlisseur)*(AscenseurEnCours.LongueurClip-AscenseurEnCours.Taille));
	E.updateAfterEvent();
}

function Mouvement_Molette(E:MouseEvent):void
{
	var Ascenseur:MovieClip = E.currentTarget.MoletteSpeAscenseur;
	var Sens:int;
	if (E.delta < 0)
	{
		Sens = -1;
	}
	else
	{
		Sens = 1;
	}
	Ascenseur.Clip.y +=  Ascenseur.Puissance * Sens;
	if (Ascenseur.Clip.y > 0)
	{
		Ascenseur.Clip.y = 0;
	}
	else
	{
		var FinClip:int = Ascenseur.Taille - Ascenseur.LongueurClip;
		if (Ascenseur.Clip.y < FinClip)
		{
			Ascenseur.Clip.y = FinClip;
		}
	}
	Ascenseur.Glisseur.y = Math.floor((-Ascenseur.Clip.y/(Ascenseur.LongueurClip-Ascenseur.Taille))*Ascenseur.FinGlisseur);
}

function Ascenseur_MAJ(ASC:MovieClip):void
{
	ASC.DernierePosition = ASC.Clip.y;
	ASC.Clip.y = 0;
	var FinAscenseur:Boolean = false;

	if (Math.floor(ASC.Clip.height) + ASC.Decalage > ASC.Taille)
	{
		FinAscenseur = ASC.Glisseur.y == ASC.FinGlisseur && ASC.visible;
		if (FinAscenseur)
		{
			Fin_Ascenseur(ASC);
		}
		else
		{
			ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
			ASC.Glisseur.Taille.height = Math.floor(ASC.TailleAsc*(ASC.Taille/ASC.LongueurClip));
			if (ASC.Glisseur.Taille.height < 40)
			{
				ASC.Glisseur.Taille.height = 40;
			}
			ASC.FinGlisseur = ASC.TailleAsc - Math.floor(ASC.Glisseur.height);
			if (ASC.Glisseur.y > ASC.FinGlisseur)
			{
				ASC.Glisseur.y = ASC.FinGlisseur;
			}
			else
			{
				ASC.Glisseur.y = Math.floor((-ASC.DernierePosition/(ASC.LongueurClip-ASC.Taille))*ASC.FinGlisseur);
			}
			//ASC.Clip.y = Math.floor(-(ASC.Glisseur.y/ASC.FinGlisseur)*(ASC.LongueurClip-ASC.Taille));
			ASC.visible = true;
			// Molette
			if (ASC.ClipMolette == null)
			{
				ASC.Clip.MoletteSpeAscenseur = ASC;
				ASC.Clip.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
			}
			else
			{
				ASC.ClipMolette.MoletteSpeAscenseur = ASC;
				ASC.ClipMolette.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
			}
		}
	}
	else
	{
		// Molette
		if (ASC.ClipMolette == null)
		{
			ASC.Clip.MoletteSpeAscenseur = null;
			ASC.Clip.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
		else
		{
			ASC.ClipMolette.MoletteSpeAscenseur = null;
			ASC.ClipMolette.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
		ASC.Clip.y = 0;
		ASC.visible = false;
		return;
	}
	if (! FinAscenseur)
	{
		if (ASC.LongueurClip + ASC.DernierePosition < ASC.Taille)
		{
			ASC.Clip.y = Math.floor(-(ASC.LongueurClip-ASC.Taille));
		}
		else
		{
			ASC.Clip.y = ASC.DernierePosition;
		}
	}
}

function Ascenseur_Reset(ASC:MovieClip):void
{
	ASC.DernierePosition = ASC.Clip.y;
	ASC.Clip.y = 0;
	if (Math.floor(ASC.Clip.height) + ASC.Decalage > ASC.Taille)
	{
		ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
		ASC.Glisseur.Taille.height = Math.floor(ASC.TailleAsc*(ASC.Taille/ASC.LongueurClip));
		if (ASC.Glisseur.Taille.height < 40)
		{
			ASC.Glisseur.Taille.height = 40;
		}
		ASC.FinGlisseur = ASC.TailleAsc - Math.floor(ASC.Glisseur.height);
		ASC.Glisseur.y = 0;
		ASC.visible = true;
		// Molette
		if (ASC.ClipMolette == null)
		{
			ASC.Clip.MoletteSpeAscenseur = ASC;
			ASC.Clip.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
		else
		{
			ASC.ClipMolette.MoletteSpeAscenseur = ASC;
			ASC.ClipMolette.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
	}
	else
	{
		// Molette
		if (ASC.ClipMolette == null)
		{
			ASC.Clip.MoletteSpeAscenseur = null;
			ASC.Clip.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
		else
		{
			ASC.ClipMolette.MoletteSpeAscenseur = null;
			ASC.ClipMolette.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
		ASC.visible = false;
	}
}

function Ancienne_Position(ASC:MovieClip):void
{
	var AnciennePosition:int = ASC.DernierePosition;
	Ascenseur_Reset(ASC);
	if (ASC.visible)
	{
		ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
		ASC.Clip.y = AnciennePosition;
		var FinClip:int = ASC.Taille - ASC.LongueurClip;
		if (ASC.Clip.y < FinClip)
		{
			Fin_Ascenseur(ASC);
		}
		ASC.Glisseur.y = Math.floor((-ASC.Clip.y/(ASC.LongueurClip-ASC.Taille))*ASC.FinGlisseur);
	}
}

function Fin_Ascenseur(ASC:MovieClip):void
{
	ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
	ASC.Glisseur.Taille.height = Math.floor(ASC.TailleAsc*(ASC.Taille/ASC.LongueurClip));
	if (ASC.Glisseur.Taille.height < 40)
	{
		ASC.Glisseur.Taille.height = 40;
	}
	ASC.FinGlisseur = ASC.TailleAsc - Math.floor(ASC.Glisseur.height);
	ASC.Glisseur.y = ASC.FinGlisseur;
	ASC.Clip.y = Math.floor(-(ASC.LongueurClip-ASC.Taille));
}

Security.allowDomain("*");

// Chargement des images
//var Images:Object = new Object();
//var Chargeurs:Array = new Array();
//var Attente:Array = new Array();

function Chargement_Image(CLIP:MovieClip, CHEMIN:String, IMAGE:String, CACHE:Boolean = false, DOMAINE:String = "http://1.1.1.1/minijeux/"):void
{
	try {
		if (CLIP.AvatarHTML != null) {
			CLIP.AvatarHTML.htmlText = "";
			
			if (IMAGE.substr(0,1) == "_") {
				// Chargement SWF
				if (CLIP._Image.Img != null) {
					CLIP._Image.Img.visible = true;
					CLIP._Image.removeChild(CLIP._Image.Img);
					CLIP._Image.Img = null;
				}
				var loader:Loader = new Loader();
				
				DOMAINE = "http://nonol.000webhostapp.com/";
				CHEMIN = "";
				IMAGE = IMAGE.substr(1);
				loader.load(new URLRequest(DOMAINE + IMAGE + ".swf")); 
				CLIP._Image.Img = loader;
				CLIP._Image.addChild(loader);
				
			} else {
				if (CLIP._Image.Img != null) {
					CLIP._Image.Img.visible = false;
					CLIP._Image.removeChild(CLIP._Image.Img);
					CLIP._Image.Img = null;
				}
				
				if (IMAGE.indexOf("imgur@") != -1 && IMAGE.substr(0, 6) == "imgur@") {
					IMAGE = IMAGE.substr(6);
					DOMAINE = "http://i.imgur.com/";
				}
				
				if (IMAGE.indexOf(".") == -1) { // Ancien avatar (donc forcément incorrect)
					//IMAGE = "6524f115.jpg" // Avatar de base (le '?')
					DOMAINE = "http://i.imgur.com/";
					IMAGE = IMAGE + ".jpg";
				}
				
				CLIP.AvatarHTML.htmlText = "<img src=\"" + DOMAINE + IMAGE + "\" height=\"80\" width=\"120\" />";
			}
		}
	} catch (e:Error) {
		trace("Chargement_Image() : IOErrorEvent - " + DOMAINE + IMAGE);
		
		if (CLIP._Image.Img != null) {
			CLIP._Image.Img.visible = false;
			CLIP._Image.removeChild(CLIP._Image.Img);
			CLIP._Image.Img = null;
		}
	}
}

/*function Chargement_Image(CLIP:MovieClip, CHEMIN:String, IMAGE:String, CACHE:Boolean = false, DOMAINE:String = "http://1.1.1.1/minijeux/"):void
{
	if (CLIP._Image.Img != null)
	{
		if (CLIP._Image.contains(CLIP._Image.Img))
		{
			CLIP._Image.removeChild(CLIP._Image.Img);
		}
		else
		{
			CLIP._Image.Img = null;
		}
	}
	var Type:String;
	
	if (IMAGE.substr(0,1) == "_")
	{
		///
		DOMAINE = "http://nonol.comuf.com/";
		CHEMIN = "";
		IMAGE = IMAGE.substr(1);
		///
		Type = ".swf";
	} else if (IMAGE.indexOf(".") == -1) { // Ancien avatar (donc forcément incorrect)
		IMAGE = "6524f115.jpg" // Avatar de base (le '?')
		Type = "";
	}
	else
	{
		Type = "";
	}
	if (CACHE)
	{
		Type +=  "?t=" + getTimer();
	}
	var Code:String = "_" + CHEMIN + IMAGE;
	var Data:ByteArray = Images[Code];
	if (Data == null || CACHE)
	{
		var EnCours = false;
		var Nb:int = Chargeurs.length;
		for (var i:int = 0; i<Nb; i=i+2)
		{
			if (Chargeurs[i + 1] == Code)
			{
				EnCours = true;
				break;
			}
		}
		Attente.push(Code, CLIP);
		if (! EnCours)
		{
			var Chargeur:URLLoader = new URLLoader();
			Chargeur.dataFormat = URLLoaderDataFormat.BINARY;
			Chargeur.addEventListener(IOErrorEvent.IO_ERROR, Erreur_Chargement);
			Chargeur.addEventListener(Event.COMPLETE, Fin_Chargement);
			Chargeurs.push(Chargeur, Code);
			try{
			Chargeur.load(new URLRequest(DOMAINE+CHEMIN+IMAGE+Type));
			}  catch (error:SecurityError) 
            {
				trace(DOMAINE+CHEMIN+IMAGE+Type);
            }
		}
	}
	else
	{
		var Chargement:Loader = new Loader();
		Chargement.loadBytes(Data);
		CLIP._Image.Img = Chargement;
		CLIP._Image.addChild(Chargement);
	}
}

function Fin_Chargement(E:Event):void
{
	var Chargeur:URLLoader = URLLoader(E.target);
	var Data:ByteArray = Chargeur.data as ByteArray;
	var Nb:int = Chargeurs.length;
	for (var i:int = 0; i<Nb; i=i+2)
	{
		if (Chargeurs[i] == Chargeur)
		{
			Images[Chargeurs[i + 1]] = Data;
			Chargeurs.splice(i, 2);
			break;
		}
	}
	Traitement_Attente();
}
function Erreur_Chargement(E:IOErrorEvent)
{
	trace(E.text);
}

function Traitement_Attente():void
{
	var Nb:int = Attente.length;
	for (var i:int = 0; i<Nb; i=i+2)
	{
		var Data:ByteArray = Images[Attente[i]];
		if (Data != null)
		{
			var Chargement:Loader = new Loader();
			var Cible:MovieClip = Attente[i + 1]._Image;
			Chargement.loadBytes(Data);
			Cible.Img = Chargement;
			Cible.addChild(Chargement);
			Attente.splice(i, 2);
			i = i - 2;
			Nb = Nb - 2;
		}
	}
}//*/

//
var AscenseurTexteEnCours:MovieClip;
function Navigateur_Texte(TEXTE:TextField, SOURIS:Boolean = true):void
{
	TEXTE.mouseWheelEnabled = SOURIS;
	var Clip:MovieClip = MovieClip(TEXTE.parent);
	//
	var Ascenseur:MovieClip;
	if (Clip.Ascenseur)
	{
		Ascenseur = Clip.Ascenseur;
	}
	else
	{
		Ascenseur = new $Ascenseur();
		Clip.Ascenseur = Ascenseur;
		Clip.addChild(Ascenseur);
	}
	Ascenseur.x = TEXTE.x + TEXTE.width + 5;
	Ascenseur.y = TEXTE.y + 3;
	Ascenseur.TailleAsc = int(TEXTE.height) - 10;
	Ascenseur.Fond.height = Ascenseur.TailleAsc;
	Ascenseur.Texte = TEXTE;
	//
	Ascenseur.visible = false;
	TEXTE.addEventListener(Event.SCROLL, Defilement_Texte);
	Ascenseur.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_DOWN, Texte_Glisseur_Pression);
	Ascenseur.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_OVER, Texte_Glisseur_Over);
	Ascenseur.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_OUT, Texte_Glisseur_Out);
}

function Destruction_Navigateur_Texte(TEXTE:TextField):void
{
	var Clip:MovieClip = MovieClip(TEXTE.parent);
	if (Clip.Ascenseur != null)
	{
		Clip.Ascenseur.FinGlisseur = null;
		Clip.Ascenseur.NbLigne = null;
		Clip.Ascenseur.TailleAsc = null;
		Clip.Ascenseur.Texte = null;
		Clip.Ascenseur.Glisseur.Taille.removeEventListener(MouseEvent.MOUSE_DOWN, Texte_Glisseur_Pression);
		Clip.Ascenseur.Glisseur.Taille.removeEventListener(MouseEvent.MOUSE_OVER, Texte_Glisseur_Over);
		Clip.Ascenseur.Glisseur.Taille.removeEventListener(MouseEvent.MOUSE_OUT, Texte_Glisseur_Out);
		Clip.removeChild(Clip.Ascenseur);
		Clip.Ascenseur = null;
	}
	TEXTE.removeEventListener(Event.SCROLL, Defilement_Texte);
}

function Defilement_Texte(E:Event):void
{
	var Texte:TextField = TextField(E.currentTarget);
	var Clip:MovieClip = MovieClip(Texte.parent);
	if (Texte.maxScrollV > 1)
	{
		if (! Clip.Ascenseur.visible)
		{
			Clip.Ascenseur.visible = true;
			//Clip.Ascenseur.NbLigne = Texte.bottomScrollV;
		}
		Clip.Ascenseur.NbLigne = Texte.numLines - Texte.maxScrollV;
		var PourcentageTaille:Number = Clip.Ascenseur.NbLigne / Texte.numLines;
		Clip.Ascenseur.Glisseur.Taille.height = int(Clip.Ascenseur.TailleAsc * PourcentageTaille);
		Clip.Ascenseur.FinGlisseur = Clip.Ascenseur.TailleAsc - int(Clip.Ascenseur.Glisseur.height);
		//
		var Fin:Boolean = Texte.bottomScrollV == Texte.numLines;
		if (Fin)
		{
			Clip.Ascenseur.Glisseur.y = Clip.Ascenseur.FinGlisseur;
		}
		else
		{
			var PourcentagePosition:Number = (Texte.scrollV-1)/Texte.numLines;
			Clip.Ascenseur.Glisseur.y = Clip.Ascenseur.TailleAsc * PourcentagePosition;
		}
	}
	else
	{
		Clip.Ascenseur.visible = false;
	}
}

function Texte_Glisseur_Over(E:MouseEvent):void
{
	E.currentTarget.gotoAndStop(2);
}
function Texte_Glisseur_Out(E:MouseEvent):void
{
	E.currentTarget.gotoAndStop(1);
}
function Texte_Glisseur_Pression(E:MouseEvent):void
{
	AscenseurTexteEnCours = E.currentTarget.parent.parent;
	AscenseurTexteEnCours.DecalageGlissement = E.currentTarget.parent.mouseY;
	AscenseurTexteEnCours.Texte.removeEventListener(Event.SCROLL, Defilement_Texte);
	stage.addEventListener(MouseEvent.MOUSE_UP, Texte_Glisseur_Relachement);
	stage.addEventListener(MouseEvent.MOUSE_MOVE, Texte_Glisseur_Deplacement);

}
function Texte_Glisseur_Relachement(E:MouseEvent):void
{
	stage.removeEventListener(MouseEvent.MOUSE_UP, Texte_Glisseur_Relachement);
	stage.removeEventListener(MouseEvent.MOUSE_MOVE, Texte_Glisseur_Deplacement);
	AscenseurTexteEnCours.Texte.addEventListener(Event.SCROLL, Defilement_Texte);
}
function Texte_Glisseur_Deplacement(E:MouseEvent):void
{
	AscenseurTexteEnCours.Glisseur.y = Math.round(AscenseurTexteEnCours.mouseY - AscenseurTexteEnCours.DecalageGlissement);
	if (AscenseurTexteEnCours.Glisseur.y < 0)
	{
		AscenseurTexteEnCours.Glisseur.y = 0;
	}
	else
	{
		if (AscenseurTexteEnCours.Glisseur.y > AscenseurTexteEnCours.FinGlisseur)
		{
			AscenseurTexteEnCours.Glisseur.y = AscenseurTexteEnCours.FinGlisseur;
		}
	}
	AscenseurTexteEnCours.Texte.scrollV = Math.ceil((AscenseurTexteEnCours.Glisseur.y/AscenseurTexteEnCours.FinGlisseur)*(AscenseurTexteEnCours.Texte.numLines-(AscenseurTexteEnCours.NbLigne-2)));
	E.updateAfterEvent();
}


function Clique_Joueur(E:MouseEvent):void
{
	var LeJoueur:MovieClip = E.currentTarget.parent;
	var Nom:String = LeJoueur.NomJoueur;
	var Options:Array = new Array();
	var JoueurInscrit:Boolean = (Nom.substr(0,1)!="*");
	
	if (NomJoueur.substr(0,1) == "*")
	{
		if (JoueurInscrit) {
			Options.push("Profil de "+Nom, Profil_Joueur, LeJoueur);
			Options.push("Ignorer/Ne plus ignorer "+Nom, Ignorer_Joueur, LeJoueur);
		}
		
		Options.push("Statistiques de "+Nom, Statistiques_Joueur, LeJoueur);
		Vignette_Menu(Options);
		return;
	}
	
	if (FILMEUR && _Forteresse.visible) {
		Vision_Joueur(LeJoueur);
		return;
	}
	
	if (Nom != NomJoueur)
	{
		Options.push("Chuchoter à "+Nom, Chuchoter_Joueur, LeJoueur);
		if (JoueurInscrit)
		{
			Options.push("Ajouter "+Nom+" à la liste d'amis", Ajouter_Ami, LeJoueur);
			if (RECRUTEUR || LEADER)
			{
				Options.push("Accepter "+Nom+" dans la team", Accepter_Joueur, LeJoueur);
			}
			Options.push("Ignorer/Ne plus ignorer "+Nom, Ignorer_Joueur, LeJoueur);
		}
		Options.push("Voter le bannissement de "+Nom, Bannir_Joueur, LeJoueur);
		if (MODO)
		{
			Options.push("Infos sur "+Nom, Info_Joueur, LeJoueur);
			Options.push("Historique de "+Nom, Histo_Joueur, LeJoueur);
		}
		if (ARBITREPLUS)
		{
			Options.push("Historique de "+Nom, Histo_Joueur, LeJoueur);
		}
	}
	if (JoueurInscrit)
	{
		Options.push("Profil de "+Nom, Profil_Joueur, LeJoueur);
	}
	if ((MODO || ARBITRE || VISION) && !FILMEUR)
	{
		Options.push("Ping de "+Nom, Ping_Joueur, LeJoueur);
	}

	if (MODO || ARBITRE || VISION)
	{
		if (_Forteresse.visible)
		{
			Options.push("Vision de "+Nom, Vision_Joueur, LeJoueur);
		}
	}
	
	if (JoueurInscrit)
	{
		Options.push("Statistiques de "+Nom, Statistiques_Joueur, LeJoueur);
	}
	
	Vignette_Menu(Options);
}

function Chuchoter_Joueur(LeJoueur:MovieClip):void
{
	var BarreChat:MovieClip;
	if (_Aaaah.visible)
	{
		BarreChat = _Aaaah._Monde.BarreChat;
	}
	else if (_Bouboum.visible)
	{
		BarreChat = _Bouboum._Monde.BarreChat;
	}
	else if (_Forteresse.visible)
	{
		BarreChat = _Forteresse._Monde.BarreChat;
	}
	else
	{
		BarreChat = _Interface.ChatV2.BarreChat;
	}

	BarreChat.Mode_Chuchoter();
	BarreChat.Destinataire.text = LeJoueur.NomJoueur;
	stage.focus = BarreChat._ChatEntrée;

}

function Envoie_MP(Texte : String):void {
	Commandes("bla "+Texte);
}

function Envoie_Team(Texte : String):void {
	Commandes("t "+Texte);
}

function Envoie_Fusion(Texte : String):void {
	Commandes("ct " + Texte);
}

function Envoie_VIP(Texte:String):void {
	Commandes("a " + Texte);
}

function Ajouter_Ami(LeJoueur:MovieClip):void
{
	Envoie_Serveur("CxAA#"+LeJoueur.NomJoueur);
}

function Accepter_Joueur(LeJoueur:MovieClip):void
{
	Commandes("inviter "+LeJoueur.NomJoueur);
}

function Profil_Joueur(LeJoueur:MovieClip):void
{
	if (Consultations < 10000)
	{
		Consultations++;
		Envoie_Serveur("CxSt#"+LeJoueur.NomJoueur);
	}
	else
	{
		Nouveau_Message_Chat("Vous avez consommé beaucoup de ressources durant cette session. L'option est désactivée.");
	}
}

function Statistiques_Joueur(LeJoueur:MovieClip):void
{
	Commandes("stat "+LeJoueur.NomJoueur);
}


function Ignorer_Joueur(LeJoueur:MovieClip):void
{
	Commandes("ignore "+LeJoueur.NomJoueur);
}

function Bannir_Joueur(LeJoueur:MovieClip):void
{
	_InfoBannir.Initialisation(LeJoueur.NomJoueur);
}

function Kick_Joueur(LeJoueur:MovieClip):void
{
	Commandes("kick "+LeJoueur.NomJoueur);
}

function Info_Joueur(LeJoueur:MovieClip):void
{
	Commandes("info "+LeJoueur.NomJoueur);
}

function Histo_Joueur(LeJoueur:MovieClip):void
{
	Commandes("histo "+LeJoueur.NomJoueur);
}

function Ping_Joueur(LeJoueur:MovieClip):void
{
	Envoie_Serveur("CxPong#0#"+LeJoueur.NomJoueur);
}


function Kick_Arbitre(LeJoueur:MovieClip):void
{
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir kicker ou bannir "+LeJoueur.NomJoueur+" ?",P_Kick_Arbitre,LeJoueur.NomJoueur);
}

function P_Kick_Arbitre(LeJoueur:String):void
{
	Commandes("kick "+LeJoueur);
}

function Muet_Arbitre(LeJoueur:MovieClip):void
{
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir rendre muet "+LeJoueur.NomJoueur+" pendant une heure ?",P_Muet_Arbitre,LeJoueur.NomJoueur);
}

function P_Ban_Arbitre(LeJoueur:String):void
{
	Commandes("ban "+LeJoueur);
}

function Ban_Arbitre(LeJoueur:MovieClip):void
{
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir bannir "+LeJoueur.NomJoueur+" ?",P_Ban_Arbitre,LeJoueur.NomJoueur);
}

function P_Muet_Arbitre(LeJoueur:String):void
{
	Commandes("mute "+LeJoueur);
}

function Muet_Joueur(LeJoueur:MovieClip):void
{
	Commandes("mute "+LeJoueur.NomJoueur);
}

function Ban_Joueur(LeJoueur:MovieClip):void
{
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir bannir "+LeJoueur.NomJoueur+" 24 heures ?",P_Ban_Joueur,LeJoueur.NomJoueur);
}

function P_Ban_Joueur(LeJoueur:String):void
{
	Commandes("ban "+LeJoueur+" 24");
}

function find_player(Nom:String):MovieClip
{
	for (var i:int = 0; i < _Forteresse.ListeJoueur.length; i++)
	{
		if (_Forteresse.ListeJoueur[i] != null &&
		  _Forteresse.ListeJoueur[i].NomJoueur.toLowerCase() == Nom.toLowerCase())
		{
			return _Forteresse.ListeJoueur[i];
		}
	}
	return null;
}

function Vision_Joueur(LeJoueur:MovieClip):void
{
	Vision(LeJoueur.NomJoueur);
}

function Vision(LeJoueur:String):void
{
	var player:MovieClip = find_player(LeJoueur);
	if (player != null)
	{
		Envoie_Serveur("CxVision");
		Espionnage = !(LeJoueur.toLowerCase()==NomJoueur.toLowerCase());
		if(Espionnage && (MODO || ARBITRE)) {
			_Forteresse.VisionSur = player.Code;
		} else {
			_Forteresse.VisionSur = -1;
		}
		_Forteresse.CibleCamera = player;
	}
}

function dateFormat(Time : String)
{
	var date:Date = new Date();
	date.setTime(Number(Time)*1000);
	var monthLabels:Array = new Array("janvier","février","mars","avril","mai","juin","juillet","août","septembre","octobre","novembre","décembre");
	return date.getDate() + " " + monthLabels[date.getMonth()] + " " + date.getFullYear();
}

function getRole(Role : String):MovieClip
{
	// image role
	switch (Role)
	{
		case "1" :
			return new $RoleRecruteur();
		case "2" :
			return new $RoleLeader();
		case "3" :
		    return new $RoleScribe();
	}
	return null;
}

function getGrade(Grade : String):MovieClip
{
	// image grade
	switch (Grade)
	{
		case "0" : return new $Grade0();
		case "1" : return new $Grade1();
		case "2" : return new $Grade2();
		case "3" : return new $Grade3();
		case "4" : return new $Grade4();
		case "5" : return new $Grade5();
		case "6" : return new $Grade6();
		case "7" : return new $Grade7();
		case "8" : return new $Grade8();
		case "9" : return new $Grade9();
		case "10" : return new $Grade10();
		case "11" : return new $Grade11();
	}
	return null;
}


function NouvelleRecompense(Image : String) : MovieClip {
		switch(Image) {
			case "0": return new $RecompenseGold();
			case "1": return new $RecompenseSilver();
			case "2": return new $RecompenseBronze();
			case "3": return new $RecompenseRougeOr();
			case "4": return new $RecompenseRougeArgent();
			case "5": return new $RecompenseRougeBronze();
			case "6": return new $RecompenseCoeur();
			case "7": return new $RecompenseEclair();
			case "8": return new $RecompenseRuby();
			case "9" : return new $GradeRosette();
			case "10" : return new $Grade0();
			case "11" : return new $Grade1();
			case "12" : return new $Grade2();
			case "13" : return new $Grade3();
			case "14" : return new $Grade4();
			case "15" : return new $Grade5();
			case "16" : return new $Grade6();
			case "17" : return new $Grade7();
			case "18" : return new $Grade8();
			case "19" : return new $RecompenseZoom();
			case "20" : return new $RecompenseBomb();
			case "21" : return new $RecompenseCouronneOr();
			case "22" : return new $RecompenseCouronneArgent();
			case "23" : return new $RecompenseCouronneBronze();
			case "24" : return new $GradeFlocon();
			case "25" : return new $GradeSerpentOr();
			case "26" : return new $GradeSerpentArgent();
			case "27" : return new $GradeSerpentBronze();
			case "28" : return new $GradePika();
			case "30" : return new $RecompenseBonbonOr();
			case "31" : return new $RecompenseBonbonArgent();
			case "32" : return new $RecompenseBonbonBronze();			
			case "100" : return new $GradeEtoile();
			case "996" : return new $GradeRespoRouge();
			case "997" : return new $GradeRespoElo();
			case "998" : return new $GradeRespomap();
			case "999" : return new $GradeAnimateur();
		}
		return new $RecompenseGold();
}


function NomRole(Role : String):String
{
	if (Role == "2")
	{
		return "Leader";
	}
	else if (Role=="1")
	{
		return "Recruteur";
	}
	else if(Role == "3")
	  return "Scribe";
	  
	else
	{
		return "";
	}
}



/* Tooltip - Code à vérifier */


// Vignette
var _Vignette:MovieClip = ClipVignette;
var LimiteX:int;
var LimiteY:int;
var AnimVignette:Tween = new Tween(_Vignette,"alpha",null,1,0,0.1,true);
AnimVignette.addEventListener(TweenEvent.MOTION_FINISH, Fin_AnimVignette);
_Vignette.mouseChildren = false;
_Vignette.mouseEnabled = false;
var VignetteEnCours:Object;
function Vignette(OBJ:Object, TEXTE:String, FIXE:int = 0, X:int = 0, Y:int = 0, LARGEUR:int = 0):void
{
	OBJ.mouseEnabled = true;
	OBJ.VignetteTexte = TEXTE;
	OBJ.VignetteFixe = FIXE;
	OBJ.hasVignette = true;
	if (FIXE)
	{
		OBJ.VignetteFixeX = X;
		OBJ.VignetteFixeY = Y;
	}
	OBJ.VignetteLargeur = LARGEUR;
	OBJ.addEventListener(MouseEvent.MOUSE_OVER, Vignette_RollOver);
	OBJ.addEventListener(MouseEvent.ROLL_OUT, Vignette_Off);
}
function Vignette_RollOver(E:MouseEvent):void
{
	stage.removeEventListener(MouseEvent.MOUSE_MOVE, Vignette_On);
	VignetteEnCours = E.currentTarget;
	var Code:int = VignetteEnCours.VignetteFixe;
	var Largeur:int;
	var Hauteur:int;
	////
	if (VignetteEnCours.VignetteTexte == "" || (VignetteEnCours.EstGrise != null && VignetteEnCours.EstGrise())) {
		return;
	}
	////
	_Vignette._Texte.htmlText = VignetteEnCours.VignetteTexte;
	if (VignetteEnCours.VignetteLargeur == 0)
	{
		_Vignette._Texte.wordWrap = false;
		_Vignette._Texte.width = _Vignette._Texte.textWidth + 4;
	}
	else
	{
		_Vignette._Texte.wordWrap = true;
		_Vignette._Texte.width = VignetteEnCours.VignetteLargeur;
	}
	_Vignette._Texte.height = _Vignette._Texte.textHeight + 4;
	//
	Largeur = _Vignette._Texte.width + 10;
	Hauteur = _Vignette._Texte.height + 6;
	//
	_Vignette.graphics.clear();
	_Vignette.graphics.beginFill(0x222233);
	_Vignette.graphics.lineStyle(3, 0, 1, true, "normal", null, JointStyle.MITER);
	_Vignette.graphics.drawRect(0, 0, Largeur, Hauteur);
	_Vignette.graphics.endFill();
	//;
	if (Code == 0)
	{
		//Normal
		_Vignette.x = mouseX;
		_Vignette.y = mouseY + 22;
		stage.addEventListener(MouseEvent.MOUSE_MOVE, Vignette_On);
		LimiteX = 800 - _Vignette.width;
		LimiteY = 600 - _Vignette.height;
		if (_Vignette.x > LimiteX)
		{
			_Vignette.x = LimiteX;
		}
		if (_Vignette.y > LimiteY)
		{
			_Vignette.y = LimiteY;
		}
	}
	else if (Code == 1)
	{
		// Fixe
		_Vignette.x = VignetteEnCours.VignetteFixeX;
		_Vignette.y = VignetteEnCours.VignetteFixeY;
	}
	else if (Code == 2)
	{
		// Fixe à partir du point en bas à gauche
		_Vignette.x = VignetteEnCours.VignetteFixeX;
		_Vignette.y = int(VignetteEnCours.VignetteFixeY - _Vignette.height);
	}
	else if (Code == 3)
	{
		// Fixe à partir du point en bas à droite
		_Vignette.x = int(VignetteEnCours.VignetteFixeX - _Vignette.width);
		_Vignette.y = int(VignetteEnCours.VignetteFixeY - _Vignette.height);
	}
	else if (Code == 10)
	{
		// Largeur fixe
		_Vignette.x = mouseX;
		_Vignette.y = mouseY + 22;
		stage.addEventListener(MouseEvent.MOUSE_MOVE, Vignette_On);
		LimiteX = 800 - _Vignette.width;
		LimiteY = 600 - _Vignette.height;
		if (_Vignette.x > LimiteX)
		{
			_Vignette.x = LimiteX;
		}
		if (_Vignette.y > LimiteY)
		{
			_Vignette.y = LimiteY;
		}
	}
	//
	AnimVignette.stop();
	if (_Vignette.alpha != 1)
	{
		AnimVignette.begin = _Vignette.alpha;
		AnimVignette.finish = 1;
		AnimVignette.start();
	}
	addChild(_Vignette);
}
function Vignette_On(E:MouseEvent):void
{
	_Vignette.x = mouseX;
	if (_Vignette.x > LimiteX)
	{
		_Vignette.x = LimiteX;
	}
	_Vignette.y = mouseY + 22;
	if (_Vignette.y > LimiteY)
	{
		_Vignette.y = LimiteY;
	}
	E.updateAfterEvent();
}
function Vignette_Off(E:MouseEvent):void
{
	if (_Vignette._Texte.htmlText == "") {
		return;
	}
	
	AnimVignette.stop();
	AnimVignette.begin = _Vignette.alpha;
	AnimVignette.finish = 0;
	AnimVignette.start();
}

function Fin_AnimVignette(E:Event):void
{
	if (_Vignette.alpha == 0 && contains(_Vignette))
	{
		removeChild(_Vignette);
		stage.removeEventListener(MouseEvent.MOUSE_MOVE, Vignette_On);
	}
}

function ConstruirePages(PAGE: int, PAGE_MAX : int, Changement_Page : Function, Atteindre_Page : Function)
{
	var NombrePageMax:int = PAGE_MAX;// à sauvegarder
	var OptionPage = new Array();
	for (var P:int = 0; P<NombrePageMax; P++)
	{
		if ((P-PAGE<5 && P-PAGE>-5)
		||P==PAGE/2
		||P==(PAGE+1)/2
		||P==PAGE+(NombrePageMax-PAGE)/2
		||P==PAGE+(NombrePageMax-PAGE+1)/2
		||P<5
		||P>NombrePageMax-2 
		||P==NombrePageMax/2
		||P==(NombrePageMax+1)/2)
		{
			OptionPage.push("Page "+(P+1), Changement_Page, P);
		}
	}
	if (NombrePageMax > 5)
	{
		OptionPage.push("Autre", Atteindre_Page, PAGE_MAX);
	}
	return OptionPage;

}


function Afficher_Playlist(CodePlaylist:String):void
{
	Envoie_Serveur("CxPLST#"+CodePlaylist);
	_RechercheMaps.visible = false;
	//Playlist = true;
}
