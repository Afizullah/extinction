_Monde._Texte.text = "";
_Monde.BarreChat._ChatEntrée.restrict = "^#|<>";

_root.Navigateur(_Monde.Ascenseur2, _Monde._Liste._Liste2, 188, 188, 40, _Monde._Liste);
_root.Navigateur_Texte(_Monde._Texte, true);

function MAJ_Liste_Joueur(RESET:Boolean = false):void {
	while (_Monde._Liste._Liste2.numChildren != 0) {
		_Monde._Liste._Liste2.removeChildAt(0);
	}
	//
	var Classement:Array = new Array();
	//
	var Orange:MovieClip = null;
	var Nb:int = ListeJoueur.length;
	_Monde._Partie.NombreJoueur.text = Nb;
	for (var i:int = 0; i<Nb; i++) {
		var JoueurMobile:MovieClip = ListeJoueur[i];
		var Joueur:MovieClip = new $JoueurBase();
		if (RESET) {
			if (JoueurMobile.Orange) {
				Couleur_Joueur(JoueurMobile, false);
			}
		}
		_Monde._Liste._Liste2.addChild(Joueur);
		var Nom:String = JoueurMobile.Nom.text;
		var Texte:String = "";
		if (Nom == NomJoueur) {
			Texte += "<font color='#009D9D'>";
		} else if(_root.SaintValentin && _root.StVAmoureux == Nom) {
			Texte += "<font color='#ED67EA'>";
		} else {
			Texte+="<font color='#C2C2DA'>";
		}
		
		Texte+=Nom+" - <font color='#C9CD36'>"+JoueurMobile.Score+"</font> - <font color='#7C7E9E'>"+JoueurMobile.Score2;
		//
		var Kikoo:Object = new Object();
		Kikoo.Cible=Joueur;
		Kikoo.Mobile=JoueurMobile;
		Kikoo.Score=JoueurMobile.Score;
		Classement.push(Kikoo);
		Joueur._Texte.htmlText=Texte;
		Joueur._Texte.addEventListener(MouseEvent.MOUSE_DOWN, _root.Clique_Joueur);
		Joueur.NomJoueur=Nom;
	}
	//
	Classement.sortOn("Score", 16 | 2);
	for (var k:int = 0; k<Nb; k++) {
		if (RESET) {
			if (k==0) {
				Couleur_Joueur(Classement[k].Mobile, true);
			}
		}
		Classement[k].Cible.y=k*21;
	}
	//
	_root.Ascenseur_MAJ(_Monde.Ascenseur2);
}


function Molette(E:MouseEvent):void {
	var Sens:int=E.delta;
	var Clip=E.currentTarget._Liste2;
	Clip.y+=15*Sens;
	if (Clip.y>0) {
		Clip.y=0;
	} else {
		if (Clip.y<192-Clip.height) {
			Clip.y=192-Clip.height;
		}
	}
}

function Nouveau_Message_Chat(MESSAGE:String, JOUEUR:String = null, CHUCHOTER:int = 0, MANIM=false):void {
	if (_root.ListeNoire.indexOf(JOUEUR) != -1) {
		return;
	}
	//
	var Message:String;
	MESSAGE = _root.ConvertToPseudoSGML(MESSAGE);
	if (JOUEUR==null) {
		Message="<font color='#6C77C1'>"+MESSAGE+"</font>";
	} else {
		if (MESSAGE.indexOf("<")!=-1) {
			return;
		}
		
		if (MANIM) {
			Message = "<font color='#B174DA'>" + MESSAGE + "</font>";
		} else if (CHUCHOTER==0) {
			//
			if (_root.Saturnaaaahles) {
				Message = "<font color='#009D9D'>["
			          +(_root.ListeBan.indexOf(JOUEUR) != -1 || _root.ListeKick.indexOf(JOUEUR) != -1 ? "?" : JOUEUR)
					  +"]</font> <font color='#C2C2DA'>"+MESSAGE+"</font>";
				if (_root.ListeMute.indexOf(JOUEUR) != -1) {
					Message = "<font size='7'>" + Message + "</font>";
				}
			} else if(_root.SaintValentin && _root.StVAmoureux == JOUEUR) {
				Message = "<font color='#ED67EA'>[" + JOUEUR + "] " + MESSAGE + "</font>";
			} else {
				Message = "<font color='#009D9D'>[" + JOUEUR + "]</font> <font color='#C2C2DA'>" + MESSAGE + "</font>";
			}
			//
			if (_root.AffichageMessages) {
				var JoueurCible:MovieClip=ClipListeJoueur[JOUEUR];
				JoueurCible.Bulle.Effet.Texte.text=MESSAGE;
				var TailleTexte:int=JoueurCible.Bulle.Effet.Texte.textWidth;
				var TailleBulle:int;
				if (TailleTexte>150) {
					TailleBulle=165;
					JoueurCible.Bulle.Effet.Fond.width=165;
					JoueurCible.Bulle.Effet.Texte.width=155;
				} else {
					TailleBulle = (TailleTexte+10)+int(TailleTexte/15);
					JoueurCible.Bulle.Effet.Fond.width=TailleBulle;
					JoueurCible.Bulle.Effet.Texte.width=TailleTexte+10;
				}
				JoueurCible.Bulle.x=- int(TailleBulle/2)+10;
				JoueurCible.Bulle.visible=true;
				JoueurCible.Bulle.gotoAndPlay(1);
			}
		} else if (CHUCHOTER == 1) {
			if(_root.SaintValentin && _root.StVAmoureux == JOUEUR) {
				Message = "<font color='#A7D889'>["+JOUEUR+"] vous susurre : "+MESSAGE+"</font>";
			} else {
				Message = "<font color='#A7D889'>["+JOUEUR+"] vous chuchote : "+MESSAGE+"</font>";
			}
		} else if (CHUCHOTER == 2) {
			if(_root.SaintValentin && _root.StVAmoureux == JOUEUR) {
				Message="<font color='#D3A769'>Vous susurrez à "+JOUEUR+" : "+MESSAGE+"</font>";
			} else {
				Message="<font color='#D3A769'>Vous chuchotez à "+JOUEUR+" : "+MESSAGE+"</font>";
			}
		} else if (_root.AffichageConnexionsAmis) {
			if (CHUCHOTER==3) {
				Message="<font color='#A7D889'>"+JOUEUR+" s'est connecté(e) au jeu.</font>";
			} else {
				Message="<font color='#A7D889'>"+JOUEUR+" s'est déconnecté(e) du jeu.</font>";
			}
		} else {
			return;
		}

	}
	var scroller:Boolean=_Monde._Texte.scrollV==_Monde._Texte.maxScrollV;
	var maxScrollV_old:int=_Monde._Texte.maxScrollV;
	var scrollV_old:int=_Monde._Texte.scrollV;

	if (_root.TexteEnCours.length>7500) {
		_root.TexteEnCours = _root.TexteEnCours.substr(_root.TexteEnCours.length-7500);
		_Monde._Texte.htmlText = _root.TexteEnCours;
	}
	var maxScrollV:int = _Monde._Texte.maxScrollV;
	_root.TexteEnCours += "\n" + Message;
	_Monde._Texte.htmlText = _root.TexteEnCours;	

	if (scroller) {
		_Monde._Texte.scrollV=_Monde._Texte.maxScrollV;
	} else {
		_Monde._Texte.scrollV=scrollV_old+maxScrollV-maxScrollV_old;
	}
}