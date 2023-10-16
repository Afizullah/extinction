import flash.utils.ByteArray;

var LimiteChat:int;
var DernierMessage:String = "wsedfc";
var CompteurMessageIdentique:int = 1;
var ListeJoueur:Array = new Array();
var DebutPartie:int;

var NomJoueur:String;
var ClipJoueur:MovieClip;
var Vivant:Boolean = false;
var Fantome:Boolean = false; // Halloween (fantôme quand on est mort)
var DeplacementEnCours:Boolean = false;
var BombeDisponible:int;
var PoseAuto:Boolean = true;

var ListeTues:Array = new Array(); // Liste des joueurs tués
var TueurJoueur:String = "-"; // Joueur ayant tué le client (null == joueur pas tué; "-" <=> première co (pas afficher les kills))
var CiblePerso:String = "";

var DroiteEnCours:Boolean = false;
var GaucheEnCours:Boolean = false;
var HautEnCours:Boolean = false;
var BasEnCours:Boolean = false;
var EspaceEnCours:Boolean = false;

var IPS:Number = 0.048;
var ImageEcoule:int;
var TempsZero:int = 0;

var OldTemps:Number = 99999999999; // Pour les Croix Rouges

var LsPseudosEquipe2Elo:Array = null; // Liste des joueurs dont le pseudo est à colorer (étant dans l'équipe 2 d'une partie classée)

var LsPseudosModeTeam:Array = new Array();
var idEquipe:int = -1;

var immortel:Boolean = false;

function Animation(Joueur:MovieClip, ANIM:int):void {
	while (Joueur.Anim.numChildren != 0) {
		Joueur.Anim.removeChildAt(0);
	}
	//
	var A:MovieClip = Joueur.Anim.Anim2;
	
	if (Joueur.Vivant) {
		switch (ANIM) {
			case 0 :
			  Joueur.Anim.addChild(A.D);
			  break;
			case 1 :
			  Joueur.Anim.addChild(A.H);
			  break;
			case 2 :
			  Joueur.Anim.addChild(A.G);
			  break;
			case 3 :
			  Joueur.Anim.addChild(A.B);
			  break;
			case 4 :
			  Joueur.Anim.addChild(A.SD);
			  break;
			case 5 :
			  Joueur.Anim.addChild(A.SH);
			  break;
			case 6 :
			  Joueur.Anim.addChild(A.SG);
			  break;
			case 7 :
			  Joueur.Anim.addChild(A.SB);
			  break;
		}
	} else if (Joueur.Fantome) { // Halloween : fantôme des morts
		switch (ANIM) {
			case 0 :
			  Joueur.Anim.addChild(A.D);
			  break;
			case 1 :
			  Joueur.Anim.addChild(A.A);
			  break;
			case 2 :
			  Joueur.Anim.addChild(A.G);
			  break;
			case 3 :
			  Joueur.Anim.addChild(A.F);
			  break;
			case 4 :
			  Joueur.Anim.addChild(A.D);
			  break;
			case 5 :
			  Joueur.Anim.addChild(A.A);
			  break;
			case 6 :
			  Joueur.Anim.addChild(A.G);
			  break;
			case 7 :
			  Joueur.Anim.addChild(A.F);
			  break;
		}
	}
}

function Synchronization_Physique(E:Event):void {
	if(_root.ModuleIPS){
		_root.IPS.Rendu();
		_root.IPS.Nouvelle_Valeur(-1);
	}
	var Temps:int = getTimer();
	
	// Croix rouges
	var Diff:Number = Temps-OldTemps;
	if (Diff > 750 && Diff < 10000 && Vivant) {
		_root.Envoie_Serveur("CxTriche#CroixRouge#Boum - "+Diff);
	}
	OldTemps = Temps;
	//
	
	var TempsPartie:int = 120-(Temps-DebutPartie)/1000;
	var Secondes:int = TempsPartie % 60;
	if (Secondes < 10) {
		_Monde._Partie.Temps.text = "0" + int(TempsPartie / 60) + ":0" + Secondes;
	} else {
		_Monde._Partie.Temps.text = "0" + int(TempsPartie / 60) + ":" + Secondes;
	}

	if (_Monde._Partie.Bonus.text == "Invulnérabilité" && TempsPartie < 110) {
		_Monde._Partie.Bonus.text = "Aucun";
	}
	//
	var TempsEcoule:int = Temps - TempsZero;
	var ImageTotal:int = TempsEcoule * IPS;
	var Image:int = ImageTotal - ImageEcoule;
	if(_root.ModuleIPS){
		_root.IPS.Nouvelle_Valeur(0, true);
	}
	if (Image != 0) {
		ImageEcoule = ImageTotal;
		for (var i:int = 0; i<Image; i++) {
			Boucle_Physique(Temps);
		}
	}
	if(_root.ModuleIPS){
		_root.IPS.Nouvelle_Valeur(0);
	}
}

function Deplace_Joueur_BclPhys(Joueur:MovieClip, AjoutColonne:int, AjoutLigne:int):void {
	/*
	  <Joueur> : Joueur à déplacer
	  <AjoutColonne> : -1 (haut) ; 0 (rien) ; 1 (bas)
	  <AjoutLigne> : -1 (gauche) ; 0 (rien) ; 1 (droite)
	*/
	var C:int;
	var L:int;
	
	var D:Boolean;
	var G:Boolean;
	var H:Boolean;
	var B:Boolean;
	
	var Glisse:Boolean = false;
	
	if (Joueur.Lenteur && !Joueur.Vitesse) {
		Joueur.x += (1.2 * AjoutColonne);
		Joueur.y += (1.2 * AjoutLigne);
	} else if (Joueur.Vitesse && !Joueur.Lenteur) {
		Joueur.x += (1.6 * AjoutColonne);
		Joueur.y += (1.6 * AjoutLigne);
	} else {
		Joueur.x += (1.4 * AjoutColonne);
		Joueur.y += (1.4 * AjoutLigne);
	}
	
	C = Joueur.ColonneEnCours;
	L = Joueur.LigneEnCours;
	
	D = (AjoutColonne == 1);
	G = (AjoutColonne == -1);
	B = (AjoutLigne == 1);
	H = (AjoutLigne == -1);
	
	if (D) { // Droite
		C=Math.ceil(Joueur.x/20);
	} else if (G) { // Gauche
		C=Math.floor(Joueur.x/20);
	} else if (B) { // Bas
		L=Math.ceil(Joueur.y/20);
	} else if (H) { // Haut
		L=Math.floor(Joueur.y/20);
	}
	
	
	if (C != Joueur.ColonneEnCours || L != Joueur.LigneEnCours) {
		if (NiveauEnCours[Joueur.ColonneEnCours]==null) {
			return;
		}
		if (NiveauEnCours[Joueur.ColonneEnCours][Joueur.LigneEnCours] == -1) {
			NettoyerCase(Joueur.ColonneEnCours, Joueur.LigneEnCours);
			MondeBrique[Joueur.ColonneEnCours][Joueur.LigneEnCours].gotoAndStop(1);
			if (Joueur.ClipJoueur) {
				_root.Envoie_Serveur("BoB#"+Joueur.ColonneEnCours+"#"+Joueur.LigneEnCours);
			}
		}
		//
		if ((NiveauEnCours[Joueur.ColonneEnCours][Joueur.LigneEnCours] == CASE_GLACE)
			|| (NiveauEnCours[Joueur.ColonneEnCours][Joueur.LigneEnCours] == BOMBE_GLACE)
			|| NiveauEnCours[Joueur.ColonneEnCours][Joueur.LigneEnCours] == CASE_GLACE_EXPLOSABLE
			|| NiveauEnCours[Joueur.ColonneEnCours][Joueur.LigneEnCours] == BOMBE_GLACE_EXPLOSABLE) {
				// On était sur une case de glace => on glisse
				Glisse = true;
			}
		
		Joueur.ColonneEnCours = C;
		Joueur.LigneEnCours = L;
		//
		if (Joueur.ClipJoueur) {
			/*
			 On veut peut-être partir dans une autre direction (touche dans
			 la direction en cours relâchée pdt le déplacement)
			*/
			if (((D && !DroiteEnCours) || (G && !GaucheEnCours)
				|| (H && !HautEnCours) || (B && !BasEnCours))
				&& !Glisse) {
				
				Joueur.ColonneEnCours -= AjoutColonne;
				Joueur.LigneEnCours -= AjoutLigne;
				
				Deplacement(Joueur, Joueur.ColonneEnCours, Joueur.LigneEnCours
							, DroiteEnCours, GaucheEnCours, HautEnCours, BasEnCours);
			} else {
				if (ObjetBloquantDeplacement(Joueur.ColonneEnCours, Joueur.LigneEnCours)) {
					Joueur.ColonneEnCours -= AjoutColonne;
					Joueur.LigneEnCours -= AjoutLigne;
					Arret(Joueur, Joueur.ColonneEnCours, Joueur.LigneEnCours, true);
				}
			}
		}
	}
}

function Boucle_Physique(Temps:int):void {
	/*
	 Gère le déplacement des joueurs.
	*/
	try {
		var Nb:int = ListeJoueur.length;
		for (var i:int = 0; i<Nb; i++) {
			var Joueur:MovieClip = ListeJoueur[i];
			if (Joueur.Vivant || Joueur.Fantome) {
				//
				// Pose Auto
				if (Joueur.ClipJoueur) {
					if (EspaceEnCours || PoseAuto) {
						Demande_Pose_Bombe();
					}
				}
				//
				if (Joueur.Droite) {
					Deplace_Joueur_BclPhys(Joueur, 1, 0);
				} else if (Joueur.Gauche) {
					Deplace_Joueur_BclPhys(Joueur, -1, 0);
				} else if (Joueur.Haut) {
					Deplace_Joueur_BclPhys(Joueur, 0, -1);
				} else if (Joueur.Bas) {
					Deplace_Joueur_BclPhys(Joueur, 0, 1);
				}
			}
		}
	} catch (e:Error) {
	}
}

function Arret(JOUEUR:MovieClip, COLONNE:int, LIGNE:int, INDIC:Boolean):void {
	JOUEUR.ColonneEnCours=COLONNE;
	JOUEUR.LigneEnCours=LIGNE;
	JOUEUR.x=COLONNE*20;
	JOUEUR.y=LIGNE*20;
	//
	if (JOUEUR.ClipJoueur) {
		if (! DeplacementEnCours) {
			return;
		}
		DeplacementEnCours=false;
		if (INDIC) {
			_root.Envoie_Serveur("MvS#"+COLONNE+"#"+LIGNE);
		}
	}
	//
	if (JOUEUR.Droite) {
		JOUEUR.Droite=false;
		Animation(JOUEUR, 4);
	} else {
		if (JOUEUR.Gauche) {
			JOUEUR.Gauche=false;
			Animation(JOUEUR, 6);
		} else {
			if (JOUEUR.Haut) {
				JOUEUR.Haut=false;
				Animation(JOUEUR, 5);
			} else {
				if (JOUEUR.Bas) {
					JOUEUR.Bas=false;
					Animation(JOUEUR, 7);
				}
			}
		}
	}
}

function ObjetBloquantDeplacement(COLONNE:int, LIGNE:int):Boolean {
	if (COLONNE < 0 || COLONNE > 28 || LIGNE < 0 || LIGNE > 18) {
		return true;
	}
	
	if (NiveauEnCours[COLONNE][LIGNE] < 1 || NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE || NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE_EXPLOSABLE) {
		return false;
	} else if (ContientBombe(COLONNE, LIGNE)) {
		var Bombe:MovieClip = ClipBombe[COLONNE + "x" + LIGNE];
		if (Bombe != null && Bombe.Type == BOMBE_FANTOME) {
			// Bombe fantôme : traversable
			return false;
		}
	}
	
	return true;
}

function Deplacement(JOUEUR:MovieClip, COLONNE:int, LIGNE:int, D:Boolean, G:Boolean, H:Boolean, B:Boolean) {
	JOUEUR.ColonneEnCours=COLONNE;
	JOUEUR.LigneEnCours=LIGNE;
	//
	if (JOUEUR.ClipJoueur) {
		if (D) {
			if (!ObjetBloquantDeplacement(COLONNE + 1, LIGNE)) {
				_root.Envoie_Serveur("MvD#"+ClipJoueur.ColonneEnCours+"#"+ClipJoueur.LigneEnCours);
			} else {
				Arret(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, true);
				return;
			}
		} else if (G) {
			if (!ObjetBloquantDeplacement(COLONNE - 1, LIGNE)) {
				_root.Envoie_Serveur("MvG#"+ClipJoueur.ColonneEnCours+"#"+ClipJoueur.LigneEnCours);
			} else {
				Arret(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, true);
				return;
			}
		} else if (H) {
			if (!ObjetBloquantDeplacement(COLONNE, LIGNE - 1)) {
				_root.Envoie_Serveur("MvH#"+ClipJoueur.ColonneEnCours+"#"+ClipJoueur.LigneEnCours);
			} else {
				Arret(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, true);
				return;
			}
		} else if (B) {
			if (!ObjetBloquantDeplacement(COLONNE, LIGNE + 1)) {
				_root.Envoie_Serveur("MvB#"+ClipJoueur.ColonneEnCours+"#"+ClipJoueur.LigneEnCours);
			} else {
				Arret(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, true);
				return;
			}
		} else {
			Arret(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, true);
			return;
		}
	}
	//
	JOUEUR.TempsMouvement=getTimer();
	var PosX:int=COLONNE*20;
	var PosY:int=LIGNE*20;
	JOUEUR.PositionBaseX=PosX;
	JOUEUR.PositionBaseY=PosY;
	JOUEUR.x=PosX;
	JOUEUR.y=PosY;
	//
	if (JOUEUR.ClipJoueur) {
		DeplacementEnCours=true;
	}
	//
	if (D) {
		JOUEUR.Droite=true;
		JOUEUR.Gauche=false;
		JOUEUR.Haut=false;
		JOUEUR.Bas=false;
		Animation(JOUEUR, 0);
	} else if (G) {
		JOUEUR.Droite=false;
		JOUEUR.Gauche=true;
		JOUEUR.Haut=false;
		JOUEUR.Bas=false;
		Animation(JOUEUR, 2);
	} else if (H) {
		JOUEUR.Droite=false;
		JOUEUR.Gauche=false;
		JOUEUR.Haut=true;
		JOUEUR.Bas=false;
		Animation(JOUEUR, 1);
	} else if (B) {
		JOUEUR.Droite=false;
		JOUEUR.Gauche=false;
		JOUEUR.Haut=false;
		JOUEUR.Bas=true;
		Animation(JOUEUR, 3);
	}
}

function Clavier_onPress(E:KeyboardEvent):void {
	_root.Inaction=0;
	//
	if (_root._Serveur.visible) {
		if (E.keyCode==13) {
			var Nom:String=_root._Serveur._Identification._Texte.text;
			if (Nom!="") {
				_root.Envoie_Serveur("CxN#"+Nom);
				_root._Serveur.removeChild(_root._Serveur._Identification);
			}
		}
	} else {
		
		if ((E.keyCode == 226 || (E.keyCode == 192 && Capabilities.os.indexOf("Mac OS") == 0)) && !E.altKey ){
			_Monde.BarreChat.Changer_Cible(null);
			stage.focus = _Monde.BarreChat._ChatEntrée;
			return;				
		}
			
		if (stage.focus==_Monde.BarreChat._ChatEntrée) {
			if (E.keyCode==13) {

				var Texte:String=_Monde.BarreChat._ChatEntrée.text;
				while (Texte.substr(0, 1) == " ") {
					Texte=Texte.substr(1);
				}
				_Monde.BarreChat._ChatEntrée.text="";

				if (Texte=="") {
					stage.focus=stage;
					return;
				}

				if (getTimer()-LimiteChat<500) {
					Nouveau_Message_Chat("Doucement, merci.");
					return;
				}

				//

				//
				if (Texte.charAt(0)=="/") {
					_root.Commandes(Texte.substr(1));
					return;
				}
				
				if (_root.NomJoueur.substr(0,1)=="*") {
					Nouveau_Message_Chat("Vous devez créer un compte pour pouvoir parler.");
					_Monde.BarreChat._ChatEntrée.text="";
					return;
				}
				
				//
				if (DernierMessage == Texte) {
					if (CompteurMessageIdentique == 2) {
						Nouveau_Message_Chat("Votre dernier message est identique.");
						return;
					}
					CompteurMessageIdentique++;
				}
				else {
					CompteurMessageIdentique = 1;
				}

				LimiteChat=getTimer();
				DernierMessage=Texte;
				if (_root.Censure(Texte)) {
					Nouveau_Message_Chat("Merci de rester poli.");
				} else if (!_root.CensureMdp(Texte)) {				
					if (_Monde.BarreChat.Destinataire.visible) {
						var Destinataire:String=_Monde.BarreChat.Destinataire.text;
						if (Destinataire=="") {
							stage.focus=_Monde.BarreChat.Destinataire;
						} else {
							_root.Chuchoter_Message(Destinataire, Texte);
						}
					} else {
						Texte = _root.PrepareTexte(Texte); 
						if (_Monde.BarreChat.Cible.MP.visible) {
							_root.Envoie_MP(Texte);
						} else if (_Monde.BarreChat.Cible.Team.visible) {
							_root.Envoie_Team(Texte);
						} else if (_Monde.BarreChat.Cible.Salon.visible) {
							_root.Commandes("s " + Texte);			
						}else if (_Monde.BarreChat.Cible.VIP.visible) {
							_root.Envoie_VIP(Texte);
						} else if (_Monde.BarreChat.Cible.CT.visible) {
							_root.Envoie_Fusion(Texte);
						} else{
							_root.Envoie_Serveur("ChM#"+_root.PrepareTexte(Texte));
						}						
					}
				}
				//
				if (Vivant || Fantome) {
					stage.focus=stage;
				}
			}
		} else {
			if (E.keyCode==13) {
				if (!_root._InfoTeam.visible){
					stage.focus=_Monde.BarreChat._ChatEntrée;
					E.updateAfterEvent();
				}
			} else {
				if (Vivant || Fantome) {
					// DROITE
					if ((E.keyCode==39 || E.keyCode == 68)&&! DroiteEnCours) {
						DroiteEnCours=true;
						if (! DeplacementEnCours) {
							Deplacement(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, true, false, false, false);
							E.updateAfterEvent();
						}
					} else if ((E.keyCode==37 || (E.keyCode == 81 && _root.Azerty) || (E.keyCode == 65 && !_root.Azerty))&&! GaucheEnCours) {
						GaucheEnCours=true;
						if (! DeplacementEnCours) {
							Deplacement(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, false, true, false, false);
							E.updateAfterEvent();
						}
					} else if ((E.keyCode==38 || (E.keyCode == 90 && _root.Azerty) || (E.keyCode == 87 && !_root.Azerty))&&! HautEnCours) {
						// HAUT
						HautEnCours=true;
						if (! DeplacementEnCours) {
							Deplacement(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, false, false, true, false);
							E.updateAfterEvent();
						}
					} else if ((E.keyCode==40 ||E.keyCode==83)&&! BasEnCours) {
						// BAS
						BasEnCours=true;
						if (! DeplacementEnCours) {
							Deplacement(ClipJoueur, ClipJoueur.ColonneEnCours, ClipJoueur.LigneEnCours, false, false, false, true);
							E.updateAfterEvent();
						}
					} else if (E.keyCode==32 && Vivant) {
						// ESPACE
						EspaceEnCours=true;
						Demande_Pose_Bombe();
						E.updateAfterEvent();
					}
				}
			}
		}
	}
}

function Clavier_onRelease(E:KeyboardEvent):void {
	if (Vivant || Fantome) {
		if (E.keyCode==39 || E.keyCode == 68) {
			DroiteEnCours=false;
			E.updateAfterEvent();
		} else if (E.keyCode==37 || (E.keyCode == 81 && _root.Azerty) || (E.keyCode == 65 && !_root.Azerty)) {
			GaucheEnCours=false;
			E.updateAfterEvent();
		} else if (E.keyCode==38 || (E.keyCode == 90 && _root.Azerty) || (E.keyCode == 87 && !_root.Azerty)) {
			HautEnCours=false;
			E.updateAfterEvent();
		} else if (E.keyCode==40 ||E.keyCode==83) {
			BasEnCours=false;
			E.updateAfterEvent();
		} else if (E.keyCode==32 || E.keyCode==32) {
			EspaceEnCours=false;
			E.updateAfterEvent();
		}
	}
}