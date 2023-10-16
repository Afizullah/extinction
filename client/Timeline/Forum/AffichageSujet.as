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
var _parent:MovieClip = MovieClip(parent);
visible = false;

var NombreMessagePage:int = 20;
var PageEnCours:int = 0;

var PopsTitre:Boolean;
var PopsTexte:Boolean = false;
var Reponse:Boolean = false;
var ModerationMessage:Boolean = false;
var AncienMessage:MovieClip;
var NouveauMessage:MovieClip;
var ListeCarteEditionXML:Array;
var OptionPage:Array;

_root.Navigateur(Ascenseur, ClipListePost, 596, 515, 100, this);

var StyleForum:StyleSheet = new StyleSheet();
StyleForum.setStyle("C", {color:'#7C7E9E', fontSize:'11', marginLeft:'20', marginRight:'20'}); //, fontStyle:'italic'
StyleForum.setStyle("J", {color:'#6C77C1', fontSize:'11'});
StyleForum.setStyle("N", {color:'#C2C2DA', textDecoration:'none'});
StyleForum.setStyle("V", {color:'#009D9D'}); //
StyleForum.setStyle("R", {color:'#CB546B'});
StyleForum.setStyle("T", {color:'#0066CE'});
StyleForum.setStyle("O", {color:'#D3A769'}); //
StyleForum.setStyle("D", {color:'#70DB93'}); //D3F9BC
StyleForum.setStyle("Y", {color:'#C9CD36'}); //
StyleForum.setStyle("M", {color:'#B84DD2'});

var CouleursPerso:Array = new Array();
/* <CC CODE_COULEUR></CC> : choix de la couleur */


// couleurs joueurs
var FinBalise: Object = {color:'#C2C2DA', textDecoration:'none'};
StyleForum.setStyle("VE", {color:'#009D9D'});
StyleForum.setStyle("NVE", FinBalise);
StyleForum.setStyle("OR", {color:'#D3A769'});
StyleForum.setStyle("NOR", FinBalise);
StyleForum.setStyle("JA", {color:'#C9CD36'});
StyleForum.setStyle("NJA", FinBalise);

///
StyleForum.setStyle("SJ", {textDecoration:'underline'});
StyleForum.setStyle("NSJ", {textDecoration:'none'});

StyleForum.setStyle("TI", {fontSize:'16'}); // Titre [titre][/titre]
StyleForum.setStyle("NTI", {fontSize:'12'});
StyleForum.setStyle("STI", {fontSize:'14'}); // Sous-titre [stitre][/stitre]
StyleForum.setStyle("NSTI", {fontSize:'12'});
StyleForum.setStyle("PT", {fontSize:'10'}); // Petit texte [petit][/petit]
StyleForum.setStyle("NPT", {fontSize:'12'});

// [milieu][/milieu]  (<p align='center'></p >)
// [gauche][/gauche]  (<p align='left'></p  >)
// [droite][/droite]  (<p align='right'></p   >)

StyleForum.setStyle("BL", {color:'#0066CE'}); // Bleu [b][/b]
StyleForum.setStyle("NBL", FinBalise);
StyleForum.setStyle("VC", {color:'#70DB93'}); // Vert clair [vc][/vc]
StyleForum.setStyle("NVC", FinBalise);
StyleForum.setStyle("MA", {color:'#B84DD2'}); // Mauve [m][/m]
StyleForum.setStyle("NMA", FinBalise);
StyleForum.setStyle("RO", {color:'#ED67EA'}); // Rose [ro][/ro]
StyleForum.setStyle("NRO", FinBalise);

StyleForum.setStyle("GR", {color:'#7C7E9E'}); // Gris citation [gr][/gr]
StyleForum.setStyle("NGR", FinBalise);
StyleForum.setStyle("BG", {color:'#6C77C1'}); // Bleu citation [bg][/bg]
StyleForum.setStyle("NBG", FinBalise);

///

/* Balise [SPOIL][/SPOIL] */

var ModifAutoriséeFofoTeam:Boolean;


function Clique_Link(E:TextEvent):void {
	var Data:String = E.text;
	if (Data.indexOf("@") == -1) {
		_root.Commandes("sondage "+Data);
	} else {
		var TexteDecoupe:Array = Data.split("@");
		if (TexteDecoupe.length>1) {
			var Quoi:String = TexteDecoupe[0];
			var Param:String = TexteDecoupe[1];
			if (Quoi=="t") { // "t@1234"
				_root.Envoie_Serveur("FoS#"+Param+"#0");
				visible = false;
				Titre.appendText(" [Affichage du lien]");
			}
		}
	}

}

function Initialisation(NB_MESSAGE:int, PAGE:int, SUJET:Array):void {
	//_root.MessageAuto = "Rapport automatique :\n<C><N>Système d'exploitation : <V>"+Capabilities.os+"\n<N>Résolution : <V>"+Capabilities.screenResolutionX+" x "+Capabilities.screenResolutionY+"\n<N>Version de Flash Player : <V>"+Capabilities.version+"\n<N>Mémoire utilisée : <V>"+Math.ceil(System.totalMemory/100000)/10+"<N> mo\nImages par seconde, en moyenne : <V>"+int(_root.MoyenneFPS/_root.NombreCapture)+"<N>, minimum : <V>"+_root.FPSMin+"\n\n<N>Vitesse de télégarchement du sujet : <V>"+(getTimer()-_root.TempsForum)+"<N> millisecondes\n";
	//_root.TempsForum = getTimer();


	PageEnCours = PAGE;
	var NombrePageMax:int = int((NB_MESSAGE-1)/NombreMessagePage)+1;
	ClipPage.EffetTexte.Texte.text = "Page "+(PAGE+1)+"/"+NombrePageMax;
	OptionPage = _root.ConstruirePages(PAGE,NombrePageMax,Changement_Page, Atteindre_Page);


	var DernièrePage:Boolean = PAGE == NombrePageMax-1;

	_parent.Sujets.visible = false;
	var ListeMessage:Array = SUJET;

	while (ClipListePost.numChildren > 0) {
		ClipListePost.removeChildAt(0);
	}
	PopsTitre = true;
	Titre.textColor = 0xC8C9DD;

	var NombrePost:int = ListeMessage.length;
	var Hauteur:int = 0;
	var Niveau:int = 30;
	var Z:int = 0;
	for (var i:int = 0; i<NombrePost && i < NombreMessagePage*7; i=i+7) { // provisoire
		var Message:MovieClip = new $MessageForumBase();
		ClipListePost.addChild(Message);
		var Auteur:String = ListeMessage[i+2];
		if (i==0 && PAGE ==0) {
			Message.PremierAuteur = Auteur;
		} else {
			Message.PremierAuteur = "";
		}
		if (ListeMessage[i+3] && ListeMessage[i+3] != "0") {
			_root.Chargement_Image(Message.Avatar, "", ListeMessage[i+3], false, "http://img.atelier801.com/");
		} else {
			_root.Chargement_Image(Message.Avatar, "", "6524f115.jpg", false, "http://img.atelier801.com/");
		}
		Message.y = Niveau+Hauteur+5;
		Message.IdMessage = ListeMessage[i];
		Message.DateTexte.text = _root.Formatage_Date(ListeMessage[i+1]);
		Message._Joueur.text = Auteur;
		var Texte:String = ListeMessage[i+4];
		var Titre:String = ListeMessage[i+5];
		var Couleur:String = ListeMessage[i+6];
		if (Titre!="null") {
			Message.BasPost.Description.htmlText="<font color='#"+Couleur+"'>"+Titre+"</font>";
		}
		// Affichage Carte
		var ListeCarte:Array = new Array();
		if (Texte.indexOf("<Ca>") != -1) {
			var TexteDecoupe:Array = Texte.split("<Ca>");
			var NbDecoupe:int = TexteDecoupe.length;
			var IdCarte:int = 0;
			for (var k:int = 1; k<NbDecoupe; k=k+2) {
				ListeCarte.push(TexteDecoupe[k]);
				var TexteReplace:String = "<img src='$CarteBase' id='"+IdCarte+"' hspace='150' />\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
				TexteDecoupe[k] = TexteReplace;
				IdCarte++;
			}
			Texte = TexteDecoupe.join("");
		}
		//
		Texte = Texte.replace(/\r/g, "<BR >");
		/// Couleurs personnalisées
		Texte = LoadCouleursPerso(Texte);
		/// htags
		Texte = Texte.replace(/&ht;/g, "<ht>#");
		
		
		Message.Texte.styleSheet = StyleForum;
		Message.Texte.htmlText = Texte;
		Message.Texte.mouseWheelEnabled = false;
		if (Texte.indexOf("<a href='event:") != 1) {
			Message.addEventListener(TextEvent.LINK, Clique_Link);
		}


		var NbCarte:int = ListeCarte.length;
		Message.NombreCarte = NbCarte;
		for (var z:int = 0; z<NbCarte; z++) {
			_root.Instance_Carte(Message.Texte.getImageReference(z), new XMLDocument(ListeCarte[z]).firstChild);
		}

		var HauteurPost:int = Message.Texte.textHeight;
		var MinHauteur:int = 90;
		if (HauteurPost<MinHauteur) {
			HauteurPost = MinHauteur;
		}
		Message.BasPost.y = HauteurPost + 34;
		Message.Texte.height = HauteurPost + 10;

		Hauteur = Math.floor(Message.height);
		Niveau = Message.y;

		Message.Avatar.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Avatar);
		
		Z++;


	}
	Position_Masque();

	// Placement des boutons
	if ((!this.SujetClos || _root.MODOFORUM) && SUJET[1] != null) {
		if (DernièrePage) {
			if (Auteur == _root.NomJoueur) {
				var Modifier:SimpleButton = new Forum$Modifier();
				Message.addChild(Modifier);
				Modifier.name = "Modifier";
				Modifier.y = Math.floor(Message.height)-19;
				Modifier.x = 635;
				Modifier.addEventListener(MouseEvent.MOUSE_DOWN, Modification_Message);
				if (_root.ForumLecture) {
					Modifier.visible = false;
				}
			}
			var Repondre:SimpleButton = new Forum$Repondre();
			Message.addChild(Repondre);
			Repondre.name = "Repondre";
			Repondre.y = Math.floor(Message.height)-19;
			Repondre.x = 705;
			Repondre.addEventListener(MouseEvent.MOUSE_DOWN, Reponse_Sujet);
			if (_root.ForumLecture) {
				Repondre.visible = false;
			}
		}
	}

	if (!DernièrePage) {
		var PS:SimpleButton = new $PageSuivante();
		Message.addChild(PS);
		PS.name = "PS";
		PS.y = Math.floor(Message.height)-19;
		PS.x = 685;
		PS.addEventListener(MouseEvent.MOUSE_DOWN, Page_Suivante);
	}

	if (PAGE!=0) {
		var PP:SimpleButton = new $PagePrecedente();
		Message.addChild(PP);
		PP.name = "PP";
		PP.y = Math.floor(Message.height)-19;
		PP.x = 10;
		PP.addEventListener(MouseEvent.MOUSE_DOWN, Page_Precedente);
	}

	_root.Ascenseur_Reset(Ascenseur);
	if (Reponse && ClipListePost.height>572) {
		_root.Fin_Ascenseur(Ascenseur);
	}
	Reponse = false;
	visible = true;

	//_root.MessageAuto += "Vitesse d'affichage du sujet : <V>"+(getTimer()-_root.TempsForum)+"<N> millisecondes";
	//if (_root.KikooTestForum == null && _root.NomJoueur != "Tigrounette") {
	//_root.Envoie_Serveur("FoR#"+_root.MessageAuto);
	//_root.KikooTestForum = "DEBUG";
	//}
}

function Clique_Avatar(E:MouseEvent):void {
	var Message:MovieClip = E.currentTarget.parent;
	var Options:Array = new Array();
	
	if (!_root.ForumLecture) {
		Options.push("Citer", Citation_Joueur, Message);
		Options.push("Profil", Profil, Message);
		if (!this.SujetClos || _root.MODOFORUM) {
			Options.push("Signaler ce message", Signaler, Message);
		}
		if (_root.MODOFORUM || (!this.SujetClos &&
			(Message.PremierAuteur == _root.NomJoueur || (ModifAutoriséeFofoTeam && Message.PremierAuteur != "")))) { // Message.PremierAuteur == "" => Pas le premier post
			Options.push("Modifier", Moderation_Edition, Message);
		}
		if (_root.MODOFORUM) {
			Options.push("Supprimer", Moderation_Suppression, Message);
			Options.push("Supprimer en arrière-plan", Moderation_Suppression_Multiple, Message);
			Options.push("Rendre "+Message._Joueur.text+" muet sur le forum", Moderation_Muet, Message);
			Options.push("Ajouter une note sur "+Message._Joueur.text, Moderation_Note, Message);
			if (this.SujetClos) {
				Options.push("Ouvrir la discussion", Fermeture_Sujet, false);
			} else {
				Options.push("Fermer la discussion", Fermeture_Sujet, true);
			}
			Options.push("Historique des sanctions forum", Histo_Sanctions, Message);
		}
	} else {
		Options.push("Profil", Profil, Message);
	}
	//var Options:Array = new Array("Citer", Citation_Joueur, Message, "Informations", _root.Information_Joueur, Message._Joueur.text, "Message privé", _root.Message_Prive, Message._Joueur.text);
	_root.Vignette_Menu(Options);
}

// Mise en place du masque
function Position_Masque():void {
	if (ClipListePost.height >= 568) {
		_Masque.height = 568;
		BasForum.y = 596;
	} else {
		_Masque.height = Math.floor(ClipListePost.height)+6;
		BasForum.y = _Masque.height+28;
	}
}

// Creation d'un nouveau sujet
function Nouveau_Sujet(BASE:Array):void {
	ListeCarteEditionXML = new Array();
	Initialisation(0, 0, BASE);
	var Message:* = ClipListePost.getChildAt(0);
	Message.BasPost.Description.htmlText="";
	Message.Texte.styleSheet = null;
	Message.Texte.type = TextFieldType.INPUT;
	Message.Texte.text = "Tapez votre message ici.";
	Message.Texte.height = 527;
	Message.BasPost.y = 543;
	BasForum.y = 596;
	Message.Texte.maxChars = 10000;
	if (!_root.MODOFORUM) {
		Message.Texte.restrict = _root.Restriction;
	}
	Message.Texte.textColor = 0x009D9D;
	PopsTexte = false;

	Titre.type = TextFieldType.INPUT;
	Titre.maxChars = 100;
	if (!_root.MODOFORUM) {
		Titre.restrict = _root.Restriction;
	}
	Titre.text = "Tapez votre titre ici.";
	Titre.textColor = 0x009D9D;
	PopsTitre = false;

	Titre.addEventListener(FocusEvent.FOCUS_IN, Pops_Titre);
	Message.Texte.addEventListener(FocusEvent.FOCUS_IN, Pops_Texte);

	_root.Ascenseur_Reset(Ascenseur);
	_Masque.height = 568;

	var Annulation:SimpleButton = new Forum$Annuler();
	Message.addChild(Annulation);
	Annulation.name = "Annuler";
	Annulation.y = 544;
	Annulation.x = 635;
	Annulation.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);

	var EnvoieMessage:SimpleButton = new Forum$Envoyer();
	Message.addChild(EnvoieMessage);
	EnvoieMessage.name = "Envoyer";
	EnvoieMessage.y = 544;
	EnvoieMessage.x = 705;
	EnvoieMessage.addEventListener(MouseEvent.MOUSE_DOWN, Envoie_NouveauSujet);

	Activation_Attache(Message);

	_root.Navigateur_Texte(Message.Texte);
}

function Moderation_Edition(MESSAGE:MovieClip):void {
	Modification_Message(null, MESSAGE);
}

function Moderation_Suppression(MESSAGE:MovieClip):void {
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir supprimer ce message ?",P_Moderation_Suppression,MESSAGE.IdMessage);
}

function P_Moderation_Suppression(MESSAGE:String):void {
	visible = false;
	_root.Envoie_Serveur("FoE#"+MESSAGE);
}


function Moderation_Suppression_Multiple(MESSAGE:MovieClip):void {
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir supprimer ce message en arrière-plan ?",P_Moderation_Suppression_AP,MESSAGE.IdMessage);
}

function P_Moderation_Suppression_AP(MESSAGE:String):void {
	_root.Envoie_Serveur("FoEA#"+MESSAGE);
}

function BaliseReelleToJoueur(TEXTE:String):String {
 TEXTE = TEXTE.replace(/<VE>/g, "[v]").replace(/<NVE>/g, "[/v]");
 TEXTE = TEXTE.replace(/<OR>/g, "[o]").replace(/<NOR>/g, "[/o]");
 TEXTE = TEXTE.replace(/<JA>/g, "[j]").replace(/<NJA>/g, "[/j]");
 
 ///
 TEXTE = TEXTE.replace(/<VC>/g, "[vc]").replace(/<NVC>/g, "[/vc]");
 TEXTE = TEXTE.replace(/<BL>/g, "[b]").replace(/<NBL>/g, "[/b]");
 //TEXTE = TEXTE.replace(/<VC>/g, "[vc]").replace(/<NVC>/g, "[/vc]");
 TEXTE = TEXTE.replace(/<MA>/g, "[m]").replace(/<NMA>/g, "[/m]");
 TEXTE = TEXTE.replace(/<RO>/g, "[ro]").replace(/<NRO>/g, "[/ro]");
 TEXTE = TEXTE.replace(/<GR>/g, "[gr]").replace(/<NGR>/g, "[/gr]");
 TEXTE = TEXTE.replace(/<BG>/g, "[bg]").replace(/<NBG>/g, "[/bg]");
 
 TEXTE = TEXTE.replace(/<TI>/g, "[titre]").replace(/<NTI>/g, "[/titre]");
 TEXTE = TEXTE.replace(/<STI>/g, "[stitre]").replace(/<NSTI>/g, "[/stitre]");
 TEXTE = TEXTE.replace(/<PT>/g, "[petit]").replace(/<NPT>/g, "[/petit]");
 TEXTE = TEXTE.replace(/<SJ>/g, "[u]").replace(/<NSJ>/g, "[/u]");
 
 TEXTE = TEXTE.replace(/<p align=\'center\'>/g, "[milieu]").replace(/<\/p >/g, "[/milieu]");
 TEXTE = TEXTE.replace(/<p align=\'left\'>/g, "[gauche]").replace(/<\/p  >/g, "[/gauche]");
 TEXTE = TEXTE.replace(/<p align=\'right\'>/g, "[droite]").replace(/<\/p   >/g, "[/droite]");
 ///
 TEXTE = TEXTE.replace(/<ht>#/g, "&ht;");
 ///
 return TEXTE;
}
function RetirerCouleurJoueur(TEXTE:String):String {
 TEXTE = TEXTE.replace(/<VE>/g, "").replace(/<NVE>/g, "");
 TEXTE = TEXTE.replace(/<OR>/g, "").replace(/<NOR>/g, "");
 TEXTE = TEXTE.replace(/<JA>/g, "").replace(/<NJA>/g, "");
 
 ///
 TEXTE = TEXTE.replace(/<VC>/g, "").replace(/<NVC>/g, "");
 TEXTE = TEXTE.replace(/<BL>/g, "").replace(/<NBL>/g, "");
 //TEXTE = TEXTE.replace(/<VC>/g, "").replace(/<NVC>/g, "");
 TEXTE = TEXTE.replace(/<MA>/g, "").replace(/<NMA>/g, "");
 TEXTE = TEXTE.replace(/<RO>/g, "").replace(/<NRO>/g, "");
 TEXTE = TEXTE.replace(/<GR>/g, "").replace(/<NGR>/g, "");
 TEXTE = TEXTE.replace(/<BG>/g, "").replace(/<NBG>/g, "");
 
 TEXTE = TEXTE.replace(/<TI>/g, "").replace(/<NTI>/g, "");
 TEXTE = TEXTE.replace(/<STI>/g, "").replace(/<NSTI>/g, "");
 TEXTE = TEXTE.replace(/<PT>/g, "").replace(/<NPT>/g, "");
 TEXTE = TEXTE.replace(/<SJ>/g, "").replace(/<NSJ>/g, "");
 
 TEXTE = TEXTE.replace(/<p align=\'center\'>/g, "").replace(/<\/p >/g, "");
 TEXTE = TEXTE.replace(/<p align=\'left\'>/g, "").replace(/<\/p  >/g, "");
 TEXTE = TEXTE.replace(/<p align=\'right\'>/g, "").replace(/<\/p   >/g, "");
 ///
 TEXTE = TEXTE.replace(/<ht>#/g, "&ht;");
 ///
 return TEXTE;
}

///
function LoadCouleursPerso(Texte:String):String{
 /* 
   Recherche de toutes les balises "<CC " + liste les différentes couleurs
   dans CouleursPerso & en crée des setStyle du type CCX + remplace les balises
   CC par des balises CCX (X = case CouleursPerso)
 */
 
 /* <CC CODE_COULEUR></CC> */
 
 var TexteBalisé:String = "";
 var balises:Array = Texte.split("<CC_");
 var couleurRGB:String;
 var deb:Boolean = true;
 var n:int;
 
 if(balises.length == 1)
  return Texte;
 
 for each(var i:String in Texte.split("<CC_")){
   if(!deb){
     couleurRGB =  "#" + i.slice(0, i.indexOf(">"));
     n = CouleursPerso.indexOf(couleurRGB); 
    }
   else{
	 couleurRGB = '';
	 deb = false;
	 n = 0;
    }

   if(n == -1){ // Couleur non présente => on l'ajoute
     CouleursPerso.push(couleurRGB);
	 n = CouleursPerso.length;
     StyleForum.setStyle("CC_" + i.slice(0, i.indexOf(">")), {color:couleurRGB});
    }
  }
 
 return Texte.replace(/<\/CC>/gi, "<N>");
}

///

function Modification_Message(E:MouseEvent, MESSAGE:MovieClip = null):void {
	ListeCarteEditionXML = new Array();
	var Message:MovieClip;
	ModerationMessage = E == null;
	if (ModerationMessage) {
		Message = MESSAGE;
	} else {
		Message = MovieClip(E.currentTarget.parent);
	}

	if (!ModerationMessage) {
		Message.getChildByName("Modifier").visible = false;
		Message.getChildByName("Repondre").visible = false;
	}

	var EditeTexte:String = Message.Texte.htmlText;
	// Carte
	if (Message.NombreCarte != 0) {
		for (var i:int = 0; i<Message.NombreCarte; i++) {
			ListeCarteEditionXML.push(Message.Texte.getImageReference(i)._XML);
			EditeTexte = EditeTexte.split("<img src='$CarteBase' id='" + i + "' hspace='150' />\n\n\n\n\n\n\n\n\n\n\n\n\n\n").join("\n\n[CARTE_"+i+"]\n\n");
		}
	}
	//
	EditeTexte = EditeTexte.replace(/<C>/g, "[c]").replace(/<\/C>/g, "[/c]\n").replace(/<J>Citation de /g, "[c=").replace(/ \:<\/J>\[c]/g, "]").replace(/<BR >/g, "\n");
	
	// couleurs joueurs
	EditeTexte = BaliseReelleToJoueur(EditeTexte);
	
	if (!_root.MODOFORUM) {
		EditeTexte = EditeTexte.replace(/</g, "[").replace(/>/g, "]");
	}
	AncienMessage = Message;
	Message.visible = false;
	var BaseY:int = Message.y;
	Message.y = 0;
	NouveauMessage = new $MessageForumBase();
	NouveauMessage.IdMessage = Message.IdMessage;
	ClipListePost.addChild(NouveauMessage);
	NouveauMessage.y = BaseY;
	NouveauMessage.Texte.text = EditeTexte;
	PopsTexte = true;
	NouveauMessage.Texte.addEventListener(FocusEvent.FOCUS_IN, Pops_Texte);

	if (ModerationMessage) {
		NouveauMessage.Texte.height = AncienMessage.Texte.height;
		NouveauMessage.BasPost.y = AncienMessage.BasPost.y;
	} else {
		var HauteurPost:int = NouveauMessage.Texte.textHeight;
		if (HauteurPost<500) {
			HauteurPost = 500;
		}
		NouveauMessage.BasPost.y = HauteurPost + 62;
		NouveauMessage.Texte.height = HauteurPost + 38;
	}

	NouveauMessage.Texte.type = TextFieldType.INPUT;
	NouveauMessage.Texte.maxChars = 10000;
	if (!_root.MODOFORUM) {
		NouveauMessage.Texte.restrict = _root.Restriction;
	}

	if (_root.Avatar && _root.Avatar != "0") {
		_root.Chargement_Image(NouveauMessage.Avatar, "", _root.Avatar, false, "http://img.atelier801.com/");
	} else {
		_root.Chargement_Image(NouveauMessage.Avatar, "", "6524f115.jpg", false, "http://img.atelier801.com/");
	}
	//
	NouveauMessage._Joueur.text = _root.NomJoueur;

	var Annulation:SimpleButton = new Forum$Annuler();
	NouveauMessage.addChild(Annulation);
	Annulation.name = "Annuler";
	Annulation.y = Math.floor(NouveauMessage.height)-18;
	Annulation.x = 635;
	Annulation.addEventListener(MouseEvent.MOUSE_DOWN, Annulation_Edition);

	var EnvoieMessage:SimpleButton = new Forum$Envoyer();
	NouveauMessage.addChild(EnvoieMessage);
	EnvoieMessage.name = "Envoyer";
	EnvoieMessage.y = Annulation.y;
	EnvoieMessage.x = 705;
	EnvoieMessage.addEventListener(MouseEvent.MOUSE_DOWN, Edition_Message);

	if (Message.NombreCarte == 0) {
		Activation_Attache(NouveauMessage);
	}

	if (!ModerationMessage) {
		_root.Ascenseur_Reset(Ascenseur);
		_root.Fin_Ascenseur(Ascenseur);
		Position_Masque();
	}

	_root.Navigateur_Texte(NouveauMessage.Texte, false);
}

function Annulation_Edition(E:MouseEvent):void {
	var Message:* = E.currentTarget.parent;
	var BaseY:int = Message.y;
	ClipListePost.removeChild(Message);
	//var AncienMessage:* = ClipListePost.getChildAt(ClipListePost.numChildren-1);

	_root.Destruction_Navigateur_Texte(Message.Texte);

	if (!ModerationMessage) {
		AncienMessage.getChildByName("Repondre").visible = true;
		AncienMessage.getChildByName("Modifier").visible = true;
	}

	AncienMessage.y = BaseY;
	AncienMessage.visible = true;
	if (!ModerationMessage) {
		Position_Masque();
		if (ClipListePost.height>620) {
			_root.Ascenseur_Reset(Ascenseur);
			_root.Fin_Ascenseur(Ascenseur);
		} else {
			_root.Ascenseur_Reset(Ascenseur);
		}
	}
}

function Reponse_Sujet(E:MouseEvent):void {
	ListeCarteEditionXML = new Array();
	var Message:MovieClip = MovieClip(ClipListePost.getChildAt(ClipListePost.numChildren-1));
	if (Message.getChildByName("Repondre") != null) {
		Message.getChildByName("Repondre").visible = false;
	}
	if (Message.getChildByName("Modifier") != null) {
		Message.getChildByName("Modifier").visible = false;
	}
	var NouveauMessage:MovieClip = new $MessageForumBase();
	ClipListePost.addChild(NouveauMessage);
	NouveauMessage.y = Math.floor(ClipListePost.height)+5;
	NouveauMessage.Texte.text = "Tapez votre message ici.";
	NouveauMessage.Texte.textColor = 0x009D9D;
	PopsTexte = false;
	NouveauMessage.Texte.height = 405;
	NouveauMessage.BasPost.y = 413;
	NouveauMessage.Texte.type = TextFieldType.INPUT;
	NouveauMessage.Texte.maxChars = 10000;
	if (!_root.MODOFORUM) {
		NouveauMessage.Texte.restrict = _root.Restriction;
	}
	if (_root.Avatar && _root.Avatar != "0") {
		_root.Chargement_Image(NouveauMessage.Avatar, "", _root.Avatar, false, "http://img.atelier801.com/");
	} else {
		_root.Chargement_Image(NouveauMessage.Avatar, "", "6524f115.jpg", false, "http://img.atelier801.com/");
	}
	//
	NouveauMessage._Joueur.text = _root.NomJoueur;

	_root.Ascenseur_Reset(Ascenseur);
	_root.Fin_Ascenseur(Ascenseur);

	NouveauMessage.Texte.addEventListener(FocusEvent.FOCUS_IN, Pops_Texte);

	Position_Masque();

	var Annulation:SimpleButton = new Forum$Annuler();
	NouveauMessage.addChild(Annulation);
	Annulation.name = "Annuler";
	Annulation.y = 415;
	Annulation.x = 635;
	Annulation.addEventListener(MouseEvent.MOUSE_DOWN, Annulation_NouveauMessage);

	var EnvoieMessage:SimpleButton = new Forum$Envoyer();
	NouveauMessage.addChild(EnvoieMessage);
	EnvoieMessage.name = "Envoyer";
	EnvoieMessage.y = 415;
	EnvoieMessage.x = 705;
	EnvoieMessage.addEventListener(MouseEvent.MOUSE_DOWN, Nouveau_Message);

	if (_root.MODO || _root.MODOFORUM){
	var EnvoiAnonyme:MovieClip = new $EnvoiAnonyme();
	NouveauMessage.addChild(EnvoiAnonyme);
	EnvoiAnonyme.y = 415;
	EnvoiAnonyme.x = 500;
	}

	Activation_Attache(NouveauMessage);

	_root.Navigateur_Texte(NouveauMessage.Texte, false);
}

function Activation_Attache(MESSAGE:MovieClip):void {
	//var AttacheMessage:SimpleButton = new Forum$Attache();
	//MESSAGE.addChild(AttacheMessage);
	//MESSAGE.AttacheMessage = AttacheMessage;
	//AttacheMessage.y = 100;
	//AttacheMessage.x = 17;
	//AttacheMessage.addEventListener(MouseEvent.MOUSE_DOWN, Attache_Carte);
}

function Attache_Carte(E:Event):void {
	_root._Menu._Inventaire.Initialisation(true);
	_root._Menu._ListeMenu.visible = false;
	_root._Menu._Forum.visible = false;
}

function Retour_Inventaire(CARTE:XMLNode):void {
	var IdCarte:int = ListeCarteEditionXML.length;
	var Carte:XMLNode = CARTE.cloneNode(true);
	Carte.attributes.D = "";
	ListeCarteEditionXML.push(CARTE);
	var Message:MovieClip = MovieClip(ClipListePost.getChildAt(ClipListePost.numChildren-1));
	Message.removeChild(Message.AttacheMessage);
	if (!PopsTexte) {
		PopsTexte = true;
		Message.Texte.text = "";
		Message.Texte.textColor = 0xC2C2DA;
	}
	Message.Texte.text += "[CARTE_"+IdCarte+"]";
	_root._Menu._ListeMenu.visible = true;
	_root._Menu._Forum.visible = true;
	_root._Menu._Inventaire.visible = false;
}

function Pops_Texte(E:FocusEvent):void {
	if (!PopsTexte) {
		PopsTexte = true;
		E.currentTarget.text = "";
		E.currentTarget.textColor = 0xC2C2DA;
	}
}

function Pops_Titre(E:FocusEvent):void {
	if (!PopsTitre) {
		PopsTitre = true;
		E.currentTarget.text = "";
		E.currentTarget.textColor = 0xC2C2DA;
	}
}

function Annulation_NouveauMessage(E:MouseEvent):void {
	_root.Destruction_Navigateur_Texte(E.currentTarget.parent.Texte);
	ClipListePost.removeChild(E.currentTarget.parent);
	var AncienMessage:MovieClip = MovieClip(ClipListePost.getChildAt(ClipListePost.numChildren-1));
	if (AncienMessage.getChildByName("Repondre")!=null) {
		AncienMessage.getChildByName("Repondre").visible = true;
	}
	if (AncienMessage._Joueur.text == _root.NomJoueur) {
		AncienMessage.getChildByName("Modifier").visible = true;
	}
	Position_Masque();

	if (ClipListePost.height>620) {
		_root.Ascenseur_Reset(Ascenseur);
		_root.Fin_Ascenseur(Ascenseur);
	} else {
		_root.Ascenseur_Reset(Ascenseur);
	}
}
////
function Formatage_Avant_Envoie(TEXTE:String):String {
	/*
	 Note : mettre les balises qui terminent avec "c]" avant la gestion
	 des citations !
	*/
	var Texte:String = TEXTE;
	var SautLigne:String = String.fromCharCode(13);
	//
	while (Texte.indexOf(" "+SautLigne) != -1) {
		Texte = Texte.split(" "+SautLigne).join(SautLigne);
	}
	
	Texte = Texte.replace(/\[vc\]/gi, "<VC>").replace(/\[\/vc\]/gi, "<NVC>");
	
	//
	while (Texte.indexOf("c]"+SautLigne) != -1 || Texte.indexOf("c] ") != -1) {
		Texte = Texte.split("c]"+SautLigne).join("c]");
		Texte = Texte.split("c] ").join("c]");
	}
	//
	while (Texte.indexOf(SautLigne+"[/c]") != -1 || Texte.indexOf(" [/c]") != -1) {
		Texte = Texte.split(SautLigne+"[/c]").join("[/c]");
		Texte = Texte.split(" [/c]").join("[/c]");
	}
	//
	while (Texte.indexOf(SautLigne+"[c") != -1 || Texte.indexOf(" [c") != -1) {
		Texte = Texte.split(SautLigne+"[c").join("[c");
		Texte = Texte.split(" [c").join("[c");
	}
	//
	Texte = Texte.split("[c").join(SautLigne+SautLigne+"[c");
	while (Texte.substr(0, 1) == SautLigne) {
		Texte = Texte.substr(1);
	}
	// Effacement des espace et des saut de ligne a la fin du message
	while (Texte.substr(-1) == " " || Texte.substr(-1) == SautLigne) {
		Texte = Texte.substr(0, -1);
	}
	while (Texte.substr(0, 1) == " " || Texte.substr(0, 1) == SautLigne) {
		Texte = Texte.substr(1);
	}
	//
	while (Texte.indexOf("[c") != -1) {
		Texte = Texte.replace(/\[c]/gi, "<C>").replace(/\[\/c]/gi, "</C>\n");
		var Coupe:Array = Texte.split("[c=");
		var NbCoupe:int = Coupe.length;
		for (var k:int = 1; k<NbCoupe; k++) {
			Coupe[k] = Coupe[k].replace(/]/, " :</J><C>");
		}
		Texte = Coupe.join("<J>Citation de ");
	}
	while (Texte.indexOf("[CARTE_0]"+SautLigne) != -1) {
		Texte = Texte.split("[CARTE_0]"+SautLigne).join("[CARTE_0]");
	}
	while (Texte.indexOf(SautLigne+"[CARTE_0]") != -1) {
		Texte = Texte.split(SautLigne+"[CARTE_0]").join("[CARTE_0]");
	}
	// Carte
	var NbCarte:int = ListeCarteEditionXML.length;
	for (var i:int = 0; i<NbCarte; i++) {
		Texte = Texte.split("[CARTE_" + i + "]").join("<Ca>"+ListeCarteEditionXML[i]+"<Ca>");
	}
	
	// Couleurs joueurs
	Texte = Texte.replace(/\[vert\]/gi, "<VE>").replace(/\[\/vert\]/gi, "<NVE>");
	Texte = Texte.replace(/\[orange\]/gi, "<OR>").replace(/\[\/orange\]/gi, "<NOR>");
	Texte = Texte.replace(/\[jaune\]/gi, "<JA>").replace(/\[\/jaune\]/gi, "<NJA>");
	Texte = Texte.replace(/\[v\]/gi, "<VE>").replace(/\[\/v\]/gi, "<NVE>");
	Texte = Texte.replace(/\[o\]/gi, "<OR>").replace(/\[\/o\]/gi, "<NOR>");
	Texte = Texte.replace(/\[j\]/gi, "<JA>").replace(/\[\/j\]/gi, "<NJA>");
	
    ///
	Texte = Texte.replace(/\[b\]/gi, "<BL>").replace(/\[\/b\]/gi, "<NBL>");
	Texte = Texte.replace(/\[m\]/gi, "<MA>").replace(/\[\/m\]/gi, "<NMA>");
	Texte = Texte.replace(/\[ro\]/gi, "<RO>").replace(/\[\/ro\]/gi, "<NRO>");
	Texte = Texte.replace(/\[gr\]/gi, "<GR>").replace(/\[\/gr\]/gi, "<NGR>");
	Texte = Texte.replace(/\[bg\]/gi, "<BG>").replace(/\[\/bg\]/gi, "<NBG>");
	
	Texte = Texte.replace(/\[titre\]/gi, "<TI>").replace(/\[\/titre\]/gi, "<NTI>");
	Texte = Texte.replace(/\[stitre\]/gi, "<STI>").replace(/\[\/stitre\]/gi, "<NSTI>");
	Texte = Texte.replace(/\[petit\]/gi, "<PT>").replace(/\[\/petit\]/gi, "<NPT>");

    Texte = Texte.replace(/\[u\]/gi, "<SJ>").replace(/\[\/u\]/gi, "<NSJ>");

    Texte = Texte.replace(/\[milieu\]/gi, "<p align=\'center\'>").replace(/\[\/milieu\]/gi, "</p >");
	Texte = Texte.replace(/\[gauche\]/gi, "<p align=\'left\'>").replace(/\[\/gauche\]/gi, "</p  >");
	Texte = Texte.replace(/\[droite\]/gi, "<p align=\'right\'>").replace(/\[\/droite\]/gi, "</p   >");
	///
	Texte = Texte.replace(/<ht>#/gi, "&ht;");
	
	return Texte;
}

var LastMessageForum:Number = -99999999;

function Nouveau_Message(E:Event):void {
	var NouveauMessage:* = ClipListePost.getChildAt(ClipListePost.numChildren-1);
	if (!PopsTexte || NouveauMessage.Texte.text == "") {
		NouveauMessage.Texte.text = "Tapez votre message ici.";
		NouveauMessage.Texte.textColor = 0xCB546B;
		PopsTexte = false;
		return;
	}
	// anti flood
	if ((!_root.MODOFORUM || _root.MODO) && getTimer() - LastMessageForum < 20000)
	{
		_root._ErreurFlood.visible=true;
		return;
	}
	LastMessageForum = getTimer();

	_root.Destruction_Navigateur_Texte(NouveauMessage.Texte);
	visible = false;
	Reponse = true;
	_root.DernierMessageForum = NouveauMessage.Texte.text.replace(/\r/g,"\r\n");
	var Texte:String = Formatage_Avant_Envoie(NouveauMessage.Texte.text);
	_root.Envoie_Serveur("FoR#"+Texte+"#"+isAnonyme());
	_root.EnvoiEnCours.visible = true;

}

function Edition_Message(E:Event):void {
	if (!PopsTexte || NouveauMessage.Texte.text == "") {
		NouveauMessage.Texte.text = "Tapez votre message ici.";
		NouveauMessage.Texte.textColor = 0xCB546B;
		PopsTexte = false;
		return;
	}
	visible = false;
	Reponse = true;
	_root.Destruction_Navigateur_Texte(NouveauMessage.Texte);
	_root.DernierMessageForum = NouveauMessage.Texte.text.replace(/<BR >/g, "\n").replace(/\r/g,"\r\n");
	var Texte:String = Formatage_Avant_Envoie(NouveauMessage.Texte.text);
	//
	_root.Envoie_Serveur("FoE#"+NouveauMessage.IdMessage+"#"+Texte);
	_root.EnvoiEnCours.visible = true;
}

function isAnonyme():String {
	if (_root.Anonyme){
		return "1";
	}else{
		return "0";
	}
}

var LastTopicForum:Number = getTimer()-90000;

function Envoie_NouveauSujet(E:Event):void {
	var Message:* = ClipListePost.getChildAt(0);
	if (Message.Texte.text == "Tapez votre message ici." || Message.Texte.text == "") {
		Message.Texte.text = "Tapez votre message ici.";
		Message.Texte.textColor = 0xCB546B;
		PopsTexte = false;
		return;
	}
	if (Titre.text == "Tapez votre titre ici." || Titre.text == "") {
		Titre.text = "Tapez votre titre ici.";
		Titre.textColor = 0xCB546B;
		PopsTitre = false;
		return;
	}
	// anti flood
	if ((!_root.MODOFORUM || _root.MODO) && getTimer() - LastTopicForum < 120000)
	{
		_root._ErreurFlood.visible=true;
		return;
	}
	LastTopicForum = getTimer();

	_root.Destruction_Navigateur_Texte(Message.Texte);
	visible = false;
	_root.DernierMessageForum = Message.Texte.text.replace(/\r/g,"\r\n");
	var Texte:String = Formatage_Avant_Envoie(Message.Texte.text);
	_root.Envoie_Serveur("FoN#"+Titre.text+"#"+Texte);
	//
	var NouveauSujet:Array = new Array(1, _root.DateServeur.valueOf()/1000, _root.NomJoueur, _root.Avatar, Texte, "", "");
	Initialisation(1, 0, NouveauSujet);
}

function Profil(POST:MovieClip):void {
	_root.Commandes("profil " + POST._Joueur.text);
}

function Citation_Joueur(POST:MovieClip):void {
	var NouveauMessage:MovieClip = MovieClip(ClipListePost.getChildAt(ClipListePost.numChildren-1));
	if (NouveauMessage.Texte.type == TextFieldType.INPUT) {
		stage.focus = NouveauMessage.Texte;
		
		var Texte:String = POST.Texte.htmlText;
		
		// couleurs joueurs
		Texte = RetirerCouleurJoueur(Texte);
		
		Texte = Texte.replace(/<BR >/g, "\n").replace(/</g, "[").replace(/>/g, "]").replace(/'event:t@\d*'/g,"");
		// Carte
		if (POST.NombreCarte != 0) {
			for (var i:int = 0; i<POST.NombreCarte; i++) {
				Texte = Texte.split("<img src='$CarteBase' id='" + i + "' hspace='150' />\n\n\n\n\n\n\n\n\n\n\n\n\n\n").join("\n\n");
			}
		}
		//
		var Debut:int;
		var Fin:int;
		while (Texte.indexOf("[J]") != -1) {
			Debut = Texte.indexOf("[J]");
			Fin = Texte.indexOf("[/J]");
			Texte = Texte.substr(0, Debut)+Texte.substr(Fin+4);
		}
		var pattern:RegExp = /\[C(=.*)?\]/;
		while (Texte.search(pattern) != -1) {
			Debut = Texte.search(pattern);
			Fin = Texte.indexOf("[/C]");
			Texte = Texte.substr(0, Debut)+Texte.substr(Fin+5);
		}
		NouveauMessage.Texte.text += "[c="+POST._Joueur.text+"]"+Texte+"[/c]\n\n";
		var FinTexte:int = NouveauMessage.Texte.text.length;
		NouveauMessage.Texte.setSelection(FinTexte, FinTexte);
	} else {
		Reponse_Sujet(null);
		Citation_Joueur(POST);
	}
}

function Fermeture_Sujet(OUI:Boolean) {
	if (OUI) {
		_root.Envoie_Serveur("FoX#1");
	} else {
		_root.Envoie_Serveur("FoX#0");
	}
}

//// Destructeur de sujet
Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible = false;
	if (_root.RechercheForum) {
		_root.Reception_Spéciale(_root.RechercheBackup);
	} else {
		_root.Envoie_Serveur("FoF");
	}
	_parent.Sujets.visible = true;
}

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
	_root.Envoie_Serveur("FoP#"+NUM);
}
function Page_Suivante(E:Event):void {
	Changement_Page(PageEnCours+1);
}
function Page_Precedente(E:Event):void {
	Changement_Page(PageEnCours-1);
}

function Moderation_Muet(MESSAGE:MovieClip) :void {
	var FenetreMute:MovieClip = new $MuteForum();
	FenetreMute.Init(this,MESSAGE.IdMessage,MESSAGE._Joueur.text);
}

function Signaler(MESSAGE:MovieClip) :void {
	var SignalerForum:MovieClip = new $SignalerForum();
	SignalerForum.Init(this,MESSAGE,Titre.text);
}

function Moderation_Note(MESSAGE:MovieClip) :void {
	var NoteForum:MovieClip = new $NoteForum();
	NoteForum.Init(this,MESSAGE._Joueur.text);
}

function Histo_Sanctions(MESSAGE:MovieClip) :void {
	_root.Commandes("histo_forum " + MESSAGE.IdMessage);
}

function Atteindre_Page(NUM:int) {
	var Aller:MovieClip = new $AllerPage();
	Aller.Init(this,NUM);

}