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

_Monde._Texte.text = "";
_Monde.BarreChat._ChatEntrée.restrict = "^#|<>";

Navigateur(_Monde.Ascenseur2, _Monde._Liste._Liste2, 194, 194, 40, _Monde._Liste);
_root.Navigateur_Texte(_Monde._Texte, true);

function MAJ_Liste_Joueur():void {
	var Nb:int = _Monde._Liste._Liste2.numChildren;
	for (var i:int = 0; i<Nb; i++) {
		_Monde._Liste._Liste2.removeChildAt(0);
	}
	//
	var Classement:Array = new Array();
	//
	Nb = ListeJoueur.length;
	_Monde.Porte.Joueurs.text = Nb;
	for (i = 0; i<Nb; i++) {
		var Joueur = new $JoueurBase();
		_Monde._Liste._Liste2.addChild(Joueur);
		var InfoJoueur:Array = ListeJoueur[i];
		var Nom:String = InfoJoueur[0];
		var JoueurMobile:MovieClip = InfoJoueur[1];
		JoueurMobile.ClipNom = Joueur;
		var Texte:String = "";
		if (Nom == NomJoueur) {
			Texte += "<font color='#009D9D'>";
		} else if(_root.SaintValentin && _root.StVAmoureux == Nom) {
			Texte += "<font color='#ED67EA'>";
		} else {
			Texte += "<font color='#C2C2DA'>";
		}
		Texte += Nom+"  <font color='#C9CD36'>"+JoueurMobile.Score;
		//
		var Kikoo:Object = new Object();
		Kikoo.Cible = Joueur;
		Kikoo.Score = JoueurMobile.Score;
		Classement.push(Kikoo);
		Joueur._Texte.htmlText = Texte;
		Joueur._Texte.addEventListener(MouseEvent.MOUSE_DOWN, _root.Clique_Joueur);	
		Joueur.NomJoueur = Nom;		
	}
	//
	Classement.sortOn("Score", 16 | 2);
	for (var k:int = 0; k<Nb; k++) {
		Classement[k].Cible.y = k*21;
	}
	//
	Ascenseur_MAJ(_Monde.Ascenseur2);
}



function Molette(E:MouseEvent):void {
	var Sens:int = E.delta;
	var Clip = E.currentTarget._Liste2;
	Clip.y += 15*Sens;
	if (Clip.y > 0) {
		Clip.y = 0;
	} else {
		if (Clip.y < 192-Clip.height) {
			Clip.y = 192-Clip.height;
		}
	}
}

function Commande_Editeur():void {
	_Editeur.NewMessage.visible = false;
	_Editeur.NewMessage.VignetteTexte = "";
	_Monde.Porte.Retour.visible = true;
	_Monde.Porte.Texte.visible = false;
	_root.Envoie_Serveur("EdI");
	_Monde.visible = false;
	_Editeur.visible = true;
	_Editeur.CodeCarte.text = "Nouvelle";
	_Editeur.Initialisation();
}


function Nouveau_Message_Chat(MESSAGE:String, JOUEUR:String = null, CHUCHOTER:int = 0, MANIM=false):void {
	if (_root.ListeNoire.indexOf(JOUEUR) != -1) {
		return;
	}
	//
	var Message:String;
	MESSAGE = _root.ConvertToPseudoSGML(MESSAGE);
	if (JOUEUR == null) {
		Message = "<font color='#6C77C1'>"+MESSAGE+"</font>";
	} else {
		if (MESSAGE.indexOf("<") != -1) {
			return;
		}
		if (MANIM) {
			Message = "<font color='#B174DA'>" + MESSAGE + "</font>";
		} else if (CHUCHOTER == 0) {
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
			
			if (_root.AffichageMessages){
				var JoueurCible:MovieClip = _Monde._ListeJoueur[JOUEUR];
				JoueurCible.Bulle.Effet.Texte.text = MESSAGE;
				var TailleTexte:int = JoueurCible.Bulle.Effet.Texte.textWidth;
				var TailleBulle:int;
				if (TailleTexte > 150) {
					TailleBulle = 165;
					JoueurCible.Bulle.Effet.Fond.width = 165;
					JoueurCible.Bulle.Effet.Texte.width = 155;
				} else {
					TailleBulle = (TailleTexte+10)+int(TailleTexte/15);
					JoueurCible.Bulle.Effet.Fond.width = TailleBulle ;
					JoueurCible.Bulle.Effet.Texte.width = TailleTexte+10;
				}
				JoueurCible.Bulle.x = -int(TailleBulle/2);
				JoueurCible.Bulle.gotoAndPlay(1);
				JoueurCible.addChild(JoueurCible.Bulle);
			}
		} else if (CHUCHOTER == 1) {
			if(_root.SaintValentin && _root.StVAmoureux == JOUEUR) {
				Message = "<font color='#A7D889'>["+JOUEUR+"] vous susurre : "+MESSAGE+"</font>";
			} else {
				Message = "<font color='#A7D889'>["+JOUEUR+"] vous chuchote : "+MESSAGE+"</font>";
			}
		} else if (CHUCHOTER == 2) {
			if(_root.SaintValentin && _root.StVAmoureux == JOUEUR) {
				Message = "<font color='#D3A769'>Vous susurrez à "+JOUEUR+" : "+MESSAGE+"</font>";
			} else {
				Message = "<font color='#D3A769'>Vous chuchotez à "+JOUEUR+" : "+MESSAGE+"</font>";
			}
		} else if (CHUCHOTER == 7 && _root.Halloween) { // Message map offi
			Message = "<font color='#CB546B' size='12'>[" + JOUEUR + "] " + MESSAGE + "</font>";
		} else if (_root.AffichageConnexionsAmis) {
			if (CHUCHOTER == 3) {
				Message = "<font color='#A7D889'>"+JOUEUR+" s'est connecté(e) au jeu.</font>";
			} else {
				Message = "<font color='#A7D889'>"+JOUEUR+" s'est déconnecté(e) du jeu.</font>";
			}
		} else {
			return;
		}
	}
	var scroller:Boolean = 	_Monde._Texte.scrollV == _Monde._Texte.maxScrollV;
	var maxScrollV_old:int = _Monde._Texte.maxScrollV;
	var scrollV_old:int = _Monde._Texte.scrollV;
	
	if (_root.TexteEnCours.length>7500) {
		_root.TexteEnCours = _root.TexteEnCours.substr(_root.TexteEnCours.length-7500);
		_Monde._Texte.htmlText = _root.TexteEnCours;
	}
	var maxScrollV:int = _Monde._Texte.maxScrollV;
	_root.TexteEnCours += "\n" + Message;
	_Monde._Texte.htmlText = _root.TexteEnCours;	
	
	if (scroller){
		_Monde._Texte.scrollV = _Monde._Texte.maxScrollV;
	} else {
		_Monde._Texte.scrollV = scrollV_old + maxScrollV - maxScrollV_old;	
	}
	
}
//
// Navigateur(ClipAscenseur, ClipQuiBouge, HauteurMasque, PuissanceMolette, TailleAscenseur);
var AscenseurEnCours:MovieClip;
function Navigateur(ASCENSEUR:MovieClip, CLIP:MovieClip, TAILLE:int, TAILLE_ASC:int, MOLETTE:int, CLIP_MOLETTE:MovieClip = null) {
	if (MOLETTE == 0) {
		ASCENSEUR.Mol = false;
	} else {
		ASCENSEUR.Mol = true;
		ASCENSEUR.Puissance = MOLETTE;

	}
	ASCENSEUR.LongueurClip = Math.floor(CLIP.parent.height);
	ASCENSEUR.Clip = CLIP;
	ASCENSEUR.Taille = TAILLE;
	ASCENSEUR.TailleAsc = TAILLE_ASC;

	if (ASCENSEUR.Clip.parent._Masque == null) {
		ASCENSEUR.Decalage = 0;
	} else {
		ASCENSEUR.Decalage = ASCENSEUR.Clip.parent._Masque.y;
		ASCENSEUR.Clip.parent._Masque.height = TAILLE;
	}
	ASCENSEUR.Fond.height = ASCENSEUR.TailleAsc+2;

	ASCENSEUR.Glisseur.Taille.height = Math.floor(ASCENSEUR.TailleAsc*(TAILLE/ASCENSEUR.LongueurClip));
	if (ASCENSEUR.Glisseur.Taille.height<40) {
		ASCENSEUR.Glisseur.Taille.height = 40;
	}
	ASCENSEUR.FinGlisseur = ASCENSEUR.TailleAsc-Math.floor(ASCENSEUR.Glisseur.height);
	ASCENSEUR.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_DOWN, Glisseur_Pression);
	ASCENSEUR.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_OVER, Glisseur_Over);
	ASCENSEUR.Glisseur.Taille.addEventListener(MouseEvent.MOUSE_OUT, Glisseur_Out);
	ASCENSEUR.ClipMolette = CLIP_MOLETTE;


	//ASCENSEUR.MAJ = MAJ_Ascenseur;
	//ASCENSEUR.Fin = Fin_Ascenseur;
}
function Glisseur_Over(E:MouseEvent):void {
	E.currentTarget.gotoAndStop(2);
}
function Glisseur_Out(E:MouseEvent):void {
	E.currentTarget.gotoAndStop(1);
}
function Glisseur_Pression(E:MouseEvent):void {
	AscenseurEnCours = E.currentTarget.parent.parent;
	AscenseurEnCours.DecalageGlissement = E.currentTarget.parent.mouseY;
	stage.addEventListener(MouseEvent.MOUSE_UP, Glisseur_Relachement);
	stage.addEventListener(MouseEvent.MOUSE_MOVE, Glisseur_Deplacement);
}
function Glisseur_Relachement(E:MouseEvent):void {
	stage.removeEventListener(MouseEvent.MOUSE_UP, Glisseur_Relachement);
	stage.removeEventListener(MouseEvent.MOUSE_MOVE, Glisseur_Deplacement);
}
function Glisseur_Deplacement(E:MouseEvent):void {
	AscenseurEnCours.Glisseur.y = Math.round(AscenseurEnCours.mouseY-AscenseurEnCours.DecalageGlissement);
	if (AscenseurEnCours.Glisseur.y < 0) {
		AscenseurEnCours.Glisseur.y = 0;
	} else {
		if (AscenseurEnCours.Glisseur.y > AscenseurEnCours.FinGlisseur) {
			AscenseurEnCours.Glisseur.y = AscenseurEnCours.FinGlisseur;
		}
	}
	AscenseurEnCours.Clip.y = Math.floor(-(AscenseurEnCours.Glisseur.y/AscenseurEnCours.FinGlisseur)*(AscenseurEnCours.LongueurClip-AscenseurEnCours.Taille));
	E.updateAfterEvent();
}

function Mouvement_Molette(E:MouseEvent):void {
	var Ascenseur:MovieClip = E.currentTarget.MoletteSpeAscenseur;
	var Sens:int;
	if (E.delta<0) {
		Sens = -1;
	} else {
		Sens = 1;
	}
	Ascenseur.Clip.y += Ascenseur.Puissance*Sens;
	if (Ascenseur.Clip.y>0) {
		Ascenseur.Clip.y = 0;
	} else {
		var FinClip:int = Ascenseur.Taille-Ascenseur.LongueurClip;
		if (Ascenseur.Clip.y < FinClip) {
			Ascenseur.Clip.y = FinClip;
		}
	}
	Ascenseur.Glisseur.y = Math.floor((-Ascenseur.Clip.y/(Ascenseur.LongueurClip-Ascenseur.Taille))*Ascenseur.FinGlisseur);
}

function Ascenseur_MAJ(ASC:MovieClip):void {
	ASC.DernierePosition = ASC.Clip.y;
	ASC.Clip.y = 0;
	var FinAscenseur:Boolean = false;

	if (Math.floor(ASC.Clip.height)+ASC.Decalage>ASC.Taille) {
		FinAscenseur = ASC.Glisseur.y == ASC.FinGlisseur && ASC.visible;
		if (FinAscenseur) {
			Fin_Ascenseur(ASC);
		} else {
			ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
			ASC.Glisseur.Taille.height = Math.floor(ASC.TailleAsc*(ASC.Taille/ASC.LongueurClip));
			if (ASC.Glisseur.Taille.height < 40) {
				ASC.Glisseur.Taille.height = 40;
			}
			ASC.FinGlisseur = ASC.TailleAsc-Math.floor(ASC.Glisseur.height);
			if (ASC.Glisseur.y > ASC.FinGlisseur) {
				ASC.Glisseur.y = ASC.FinGlisseur;
			} else {
				ASC.Glisseur.y = Math.floor((-ASC.DernierePosition/(ASC.LongueurClip-ASC.Taille))*ASC.FinGlisseur);
			}
			//ASC.Clip.y = Math.floor(-(ASC.Glisseur.y/ASC.FinGlisseur)*(ASC.LongueurClip-ASC.Taille));
			ASC.visible = true;
			// Molette
			if (ASC.ClipMolette == null) {
				ASC.Clip.MoletteSpeAscenseur = ASC;
				ASC.Clip.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
			} else {
				ASC.ClipMolette.MoletteSpeAscenseur = ASC;
				ASC.ClipMolette.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
			}
		}
	} else {
		// Molette
		if (ASC.ClipMolette == null) {
			ASC.Clip.MoletteSpeAscenseur = null;
			ASC.Clip.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		} else {
			ASC.ClipMolette.MoletteSpeAscenseur = null;
			ASC.ClipMolette.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
		ASC.Clip.y = 0;
		ASC.visible = false;
		return;
	}
	if (!FinAscenseur) {
		if (ASC.LongueurClip+ASC.DernierePosition<ASC.Taille) {
			ASC.Clip.y = Math.floor(-(ASC.LongueurClip-ASC.Taille));
		} else {
			ASC.Clip.y = ASC.DernierePosition;
		}
	}
}

function Ascenseur_Reset(ASC:MovieClip):void {
	ASC.DernierePosition = ASC.Clip.y;
	ASC.Clip.y = 0;
	if (Math.floor(ASC.Clip.height)+ASC.Decalage>ASC.Taille) {
		ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
		ASC.Glisseur.Taille.height = Math.floor(ASC.TailleAsc*(ASC.Taille/ASC.LongueurClip));
		if (ASC.Glisseur.Taille.height<40) {
			ASC.Glisseur.Taille.height = 40;
		}
		ASC.FinGlisseur = ASC.TailleAsc-Math.floor(ASC.Glisseur.height);
		ASC.Glisseur.y = 0;
		ASC.visible = true;
		// Molette
		if (ASC.ClipMolette == null) {
			ASC.Clip.MoletteSpeAscenseur = ASC;
			ASC.Clip.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		} else {
			ASC.ClipMolette.MoletteSpeAscenseur = ASC;
			ASC.ClipMolette.addEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
	} else {
		// Molette
		if (ASC.ClipMolette == null) {
			ASC.Clip.MoletteSpeAscenseur = null;
			ASC.Clip.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		} else {
			ASC.ClipMolette.MoletteSpeAscenseur = null;
			ASC.ClipMolette.removeEventListener(MouseEvent.MOUSE_WHEEL, Mouvement_Molette);
		}
		ASC.visible = false;
	}
}

function Ancienne_Position(ASC:MovieClip):void {
	var AnciennePosition:int = ASC.DernierePosition;
	Ascenseur_Reset(ASC);
	if (ASC.visible) {
		ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
		ASC.Clip.y = AnciennePosition;
		var FinClip:int = ASC.Taille-ASC.LongueurClip;
		if (ASC.Clip.y < FinClip) {
			Fin_Ascenseur(ASC);
		}
		ASC.Glisseur.y = Math.floor((-ASC.Clip.y/(ASC.LongueurClip-ASC.Taille))*ASC.FinGlisseur);
	}
}

function Fin_Ascenseur(ASC:MovieClip):void {
	ASC.LongueurClip = Math.floor(ASC.Clip.parent.height);
	ASC.Glisseur.Taille.height = Math.floor(ASC.TailleAsc*(ASC.Taille/ASC.LongueurClip));
	if (ASC.Glisseur.Taille.height<40) {
		ASC.Glisseur.Taille.height = 40;
	}
	ASC.FinGlisseur = ASC.TailleAsc-Math.floor(ASC.Glisseur.height);
	ASC.Glisseur.y = ASC.FinGlisseur;
	ASC.Clip.y = Math.floor(-(ASC.LongueurClip-ASC.Taille));
}