import flash.geom.ColorTransform;
import flash.display.MovieClip;

var _root:MovieClip = MovieClip(parent);

var NiveauEnCours:Array;
var MondeBrique:Array;

var ClipAttente:MovieClip = _Monde._Partie.Attente;
var ClipBombe:MovieClip = _Monde._Partie._Niveau._Bombes;
var ClipBrique:MovieClip = _Monde._Partie._Niveau._Briques;
var ClipListeJoueur:MovieClip = _Monde._Partie._Niveau._Joueurs;

final var CASE_VIDE:int = 0;
final var CASE_NOIRE:int = 1;
final var CASE_BLEUE:int = 2;
final var CASE_TRESOR:int = 3;
final var CASE_GLACE:int = 4;
final var CASE_GLACE_EXPLOSABLE:int = 5; // Case de glace qui se casse après une explosion
final var BOMBE_VIDE:int = 8;
final var BOMBE_GLACE:int = 9;
final var BOMBE_GLACE_EXPLOSABLE:int = 10;

final var BOMBE_SUPER:int = 1;
final var BOMBE_MAUVAIS_CONTACT:int = 2;
final var BOMBE_ULTRA:int = 3;
final var BOMBE_FANTOME:int = 4;
final var BOMBE_FLOCON:int = 5;
final var BOMBE_CUPIDONNE:int = 6;

var Sprite_Bas:int = 11;
var Sprite_Centre:int = 6;
var Sprite_Droite:int = 10;
var Sprite_Gauche:int = 12;
var Sprite_Haut:int = 9;
var Sprite_Horizontal:int = 8;
var Sprite_Vertical:int = 7;
var Sprite_Explosion:int = 16;

var Sprite_BonusBombe:int = 13;
var Sprite_BonusPuissance:int = 14;
var Sprite_BonusCrane:int = 15;

var Sprite_SolGlace:int = 5;
var Sprite_SolVide:int = 1;

ClipBombe.visible = false;

function Construction_Niveau(MESSAGE:String):void {
	if (_root.H != loaderInfo.bytes.toString().toUpperCase()) {
		_root.Serveur.close();
		return;
	}
	//
	ClipBombe.visible = true;
	PoseAuto = false;
	ClipAttente.visible = false;
	//
	while (ClipBrique.numChildren != 0) {
		ClipBrique.removeChildAt(0);
	}
	while (ClipBombe.numChildren != 0) {
		ClipBombe.removeChildAt(0);
	}
	//
	NiveauEnCours = new Array();
	MondeBrique = new Array();
	var C:Array = MESSAGE.split(";");
	C.shift();
	var NbC:int = C.length;
	for (var i:int = 0; i<NbC; i++) {
		var L:Array = C[i].split(",");
		L.shift();
		var SousNiveau:Array = new Array();
		var SousBrique:Array = new Array();
		NiveauEnCours.push(SousNiveau);
		MondeBrique.push(SousBrique);
		var NbL:int = L.length;
		for (var k:int = 0; k<NbL; k++) {
			var Code:int = int(L[k]);
			SousNiveau.push(Code);
			var Brique:MovieClip = new $Brique();
			Brique.gotoAndStop(Code+1);
			ClipBrique.addChild(Brique);
			Brique.Glace.visible = (Code == CASE_GLACE || Code == CASE_GLACE_EXPLOSABLE);
			Brique.x = i * 20;
			Brique.y = k * 20;
			SousBrique.push(Brique);
		}
	}
	if (! _root.NoLag) {
		Sprite_Bas = 11;
		Sprite_Centre = 6;
		Sprite_Droite = 10;
		Sprite_Gauche = 12;
		Sprite_Haut = 9;
		Sprite_Horizontal = 8;
		Sprite_Vertical = 7;
		Sprite_Explosion = 16;
	} else {
		Sprite_Explosion = Sprite_Bas = Sprite_Centre = Sprite_Droite = Sprite_Gauche = Sprite_Haut = Sprite_Horizontal = Sprite_Vertical = 1;
	}
}

function Placement_Joueur(LISTE:Array):void {
	if (_root.SonActif) {
		var bip:BipDebutPartie = new BipDebutPartie();
		bip.play();
	}
	
	while(ClipListeJoueur.numChildren != 0){
		ClipListeJoueur.removeChildAt(0);
	}

	Vivant = true;
	Fantome = false;
	DebutPartie = getTimer();

	BombeDisponible = 1;
	_Monde._Partie.NombreBombe.text = "1";
	_Monde._Partie.Puissance.text = "1";
	_Monde._Partie.Bonus.text = "Invulnérabilité";

	var Nb:int = LISTE.length;
	for (var i:int = 0; i<Nb; i++) {
		var InfoJoueur:Array = LISTE[i].split(",");
		var Clip:MovieClip = ClipListeJoueur[InfoJoueur[0]];
		
		if (_root.Halloween) { // Le joueur n'est plus un fantôme -> remettre la bonne image
			Couleur_Joueur(Clip, Clip.Orange);
		}
		
		ClipListeJoueur.addChild(Clip);
		Clip.Vivant = true;
		Clip.Fantome = false;
		var C:int = int(InfoJoueur[1]);
		var L:int = int(InfoJoueur[2]);
		var InfoScore:Array = InfoJoueur[3].split("%");
		Clip.Score = int(InfoScore[0]);
		Clip.Score2 = int(InfoScore[1]);
		Arret(Clip, C, L, false);
		Animation(Clip, 7);
		//
		Clip.Lenteur = false;
		Clip.Vitesse = false;
		Clip.BonusSecondaire = null;
		Clip.visible = true;
	}
	//
	MAJ_Liste_Joueur(true);
	_root.Ascenseur_Reset(_Monde.Ascenseur2);
}

function Demande_Pose_Bombe():void {
	if (BombeDisponible <= 0 && ! PoseAuto) {
		return;
	}
	if (DeplacementEnCours) {
		var Colonne:int = Math.round(ClipJoueur.x / 20);
		var Ligne:int = Math.round(ClipJoueur.y / 20);
		if (NiveauEnCours[Colonne][Ligne] < 1 || NiveauEnCours[Colonne][Ligne] == 4) {
			_root.Envoie_Serveur("BoP#"+Colonne+"#"+Ligne);
		}
	} else {
		if (NiveauEnCours[ClipJoueur.ColonneEnCours][ClipJoueur.LigneEnCours] < 1
			|| NiveauEnCours[ClipJoueur.ColonneEnCours][ClipJoueur.LigneEnCours] == 4) {
			_root.Envoie_Serveur("BoP#"+ClipJoueur.ColonneEnCours+"#"+ClipJoueur.LigneEnCours);
		}
	}
}

function SetBombe(COLONNE:int, LIGNE:int) {
	if (NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE) {
		// Case de glace + bombe : pas 8 mais 9
		NiveauEnCours[COLONNE][LIGNE] = BOMBE_GLACE;
	} else if (NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE_EXPLOSABLE) {
		NiveauEnCours[COLONNE][LIGNE] = BOMBE_GLACE_EXPLOSABLE;
	} else {
		NiveauEnCours[COLONNE][LIGNE] = BOMBE_VIDE;
	}
}

function ContientBombe(COLONNE:int, LIGNE:int):Boolean {
	return EstCodeBombe(NiveauEnCours[COLONNE][LIGNE]);
}

function EstCodeBombe(Code:int):Boolean {
	return (Code == BOMBE_VIDE || Code == BOMBE_GLACE || Code == BOMBE_GLACE_EXPLOSABLE);
}

function NettoyerCase(COLONNE:int, LIGNE:int) {
	if (NiveauEnCours[COLONNE][LIGNE] == CASE_VIDE
		|| NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE) {
		return; // Bloc glace / vide
	} else if (NiveauEnCours[COLONNE][LIGNE] == BOMBE_GLACE) { // Bloc glace + bombe
		NiveauEnCours[COLONNE][LIGNE] = CASE_GLACE;
	} else {
		NiveauEnCours[COLONNE][LIGNE] = CASE_VIDE;
	}
}

function Pose_Bombe(LIGNE:int,  COLONNE:int, JOUEUR:MovieClip, TYPE:int):void {
	if (ClipAttente.visible) {
		return;
	}

	var Bombe:MovieClip;
	if (TYPE == BOMBE_SUPER) {
		Bombe = new $BombeSuper();
	} else if (TYPE == BOMBE_MAUVAIS_CONTACT) {
		Bombe = new $BombeAlea();
	} else if (TYPE == BOMBE_ULTRA) {
		Bombe = new $BombeUltra();
	} else if (TYPE == BOMBE_CUPIDONNE) {
		Bombe = new $BombeCupidonne();
	} else if (TYPE == BOMBE_FANTOME){
		Bombe = new $BombeFantome();
		Bombe.alpha = 0.3;
		var nouvCouleur:ColorTransform = new ColorTransform();
		nouvCouleur.color = _root.couleurBbFantome;
		Bombe.Bombe.Couleur.transform.colorTransform = nouvCouleur;
	} else if (TYPE == BOMBE_FLOCON) {
		Bombe = new $BombeSnow();
	} else {
		Bombe = new $Bombe();
		
		if (JOUEUR.RecompensesElo != 0) {
			var nouvCouleur:ColorTransform = new ColorTransform();
			
			if (_root.RcmpEloActives == 2) { // Visibilité des récompenses désactivée > aucune modif de la bombe
			} else if (JOUEUR.RecompensesElo == 3) { // Or
				nouvCouleur.color = 0xAA8800;
			} else if (JOUEUR.RecompensesElo == 2) { // Argent
				nouvCouleur.color = 0x868686;
			} else if (JOUEUR.RecompensesElo == 1) { // Bronze
				nouvCouleur.color = 0x5F3F3A;
			}
		
			Bombe.Bombe.Couleur.transform.colorTransform = nouvCouleur;
		}
	}
	
	/*if (JOUEUR.BonusSecondaire == 1) { // Super Bombe
		AjouteEtincellesBombe(Bombe, 0xFF0000);
	} else if (JOUEUR.BonusSecondaire == 7) { // Ultra Bombe
		AjouteEtincellesBombe(Bombe, 0x0000FF);
	} */
	
	
	var EmplacementCible:MovieClip = ClipBombe[COLONNE + "x" + LIGNE];
	if (EmplacementCible != null && ClipBombe.contains(EmplacementCible)) {
		ClipBombe.removeChild(EmplacementCible);
	}
	ClipBombe.addChild(Bombe);
	ClipBombe[COLONNE + "x" + LIGNE] = Bombe;
	Bombe.Colonne = COLONNE;
	Bombe.Ligne = LIGNE;
	Bombe.Joueur = JOUEUR;
	Bombe.Type = TYPE;
	Bombe.x = COLONNE * 20;
	Bombe.y = LIGNE * 20;
	if (NiveauEnCours[COLONNE][LIGNE] == -1) {
		MondeBrique[COLONNE][LIGNE].gotoAndStop(1);
	}
	
	SetBombe(COLONNE, LIGNE);
	
	if (JOUEUR.ClipJoueur) {
		BombeDisponible--;
		_Monde._Partie.NombreBombe.text = BombeDisponible;
	}
}

function AjouteEtincellesBombe(Bombe:MovieClip, Couleur:int):void {
	Bombe.Etincelles = new $EtincellesB();
	
	var nouvCouleur:ColorTransform = new ColorTransform();
	nouvCouleur.color = Couleur;
	Bombe.Etincelles.transform.colorTransform = nouvCouleur;
	
	Bombe.addChild(Bombe.Etincelles);
}

function GestionSouffle(COLONNE:int, LIGNE:int, SUPER_BOMBE:Boolean, ULTRA_BOMBE:Boolean, Sprite_Explo1:int, Sprite_Explo2:int, Bombe:MovieClip, MaxRange:Boolean):Boolean {
	var CodeCase:int = NiveauEnCours[COLONNE][LIGNE];
	if (CodeCase == CASE_BLEUE || CodeCase == CASE_TRESOR) {
		NettoyerCase(COLONNE, LIGNE);
		//NiveauEnCours[Souffle][LIGNE] = 0;
		MondeBrique[COLONNE][LIGNE].gotoAndStop(Sprite_Explosion);
		if (! SUPER_BOMBE) {
			return false;
		}
	} else if (CodeCase == CASE_NOIRE) {
		if (ULTRA_BOMBE) {
			NettoyerCase(COLONNE, LIGNE);
			//NiveauEnCours[Souffle][LIGNE] = 0;
			MondeBrique[COLONNE][LIGNE].gotoAndStop(Sprite_Explosion);						
		}
		return false;
	} else if (EstCodeBombe(CodeCase)) {
		Esquive(COLONNE, LIGNE, Bombe);
		return false;
	} else {
		Esquive(COLONNE, LIGNE, Bombe);
		NettoyerCase(COLONNE, LIGNE);
		//NiveauEnCours[Souffle][LIGNE] = 0;
		if (MaxRange) {
			MondeBrique[COLONNE][LIGNE].gotoAndStop(Sprite_Explo1);
		} else {
			MondeBrique[COLONNE][LIGNE].gotoAndStop(Sprite_Explo2);
		}
	}
	return true;
}

function Explosion(LIGNE:int, COLONNE:int, PUISSANCE:int, PROPAGATION:int, TYPE_BOMBE:String):void {
	if (ClipAttente.visible) {
		return;
	}
	var SUPER_BOMBE: Boolean = (TYPE_BOMBE == "" + BOMBE_SUPER);
	var ULTRA_BOMBE: Boolean = (TYPE_BOMBE == "" + BOMBE_ULTRA);
	
	var SouffleDroite:Boolean = (PROPAGATION != 1);
	var SouffleGauche:Boolean = (PROPAGATION != 2);
	var SouffleBas:Boolean = (PROPAGATION != 3);
	var SouffleHaut:Boolean = (PROPAGATION != 4);

	var Bombe:MovieClip = ClipBombe[COLONNE + "x" + LIGNE];

	var Joueur:MovieClip = Bombe.Joueur;
	if (Joueur.ClipJoueur) {
		BombeDisponible++;
		_Monde._Partie.NombreBombe.text = BombeDisponible;
	}
	//
	Bombe.Type = TYPE_BOMBE; // Utilisé St Valentin & Halloween
	//
	Esquive(COLONNE, LIGNE, Bombe);

	MondeBrique[COLONNE][LIGNE].gotoAndStop(Sprite_Centre);
	NettoyerCase(COLONNE, LIGNE);
	//NiveauEnCours[COLONNE][LIGNE] = 0;

	var CodeCase:int;
	for (var i:int = 1; i <= PUISSANCE; i++) {
		var Souffle:int;
		if (SouffleDroite) {
			Souffle = COLONNE + i;
			if (Souffle < 29) {
				SouffleDroite = GestionSouffle(Souffle, LIGNE, SUPER_BOMBE, ULTRA_BOMBE,
							   				   Sprite_Droite, Sprite_Horizontal, Bombe,
											   PUISSANCE == i);
			} else {
				SouffleDroite = false;
			}
		}
		if (SouffleGauche) {
			Souffle = COLONNE - i;
			if (Souffle >= 0) {
				SouffleGauche = GestionSouffle(Souffle, LIGNE, SUPER_BOMBE, ULTRA_BOMBE,
							   				   Sprite_Gauche, Sprite_Horizontal, Bombe,
											   PUISSANCE == i);
			} else {
				SouffleGauche = false;
			}
		}
		
		if (SouffleBas) {
			// Sprite_Vertical
			Souffle = LIGNE + i;
			if (Souffle < 19) {
				SouffleBas = GestionSouffle(COLONNE, Souffle, SUPER_BOMBE, ULTRA_BOMBE,
							   				Sprite_Bas, Sprite_Vertical, Bombe,
											PUISSANCE == i);
			} else {
				SouffleBas = false;
			}
		}
		if (SouffleHaut) {
			Souffle = LIGNE - i;
			if (Souffle >= 0) {
				SouffleHaut = GestionSouffle(COLONNE, Souffle, SUPER_BOMBE, ULTRA_BOMBE,
							   				 Sprite_Haut, Sprite_Vertical, Bombe,
											 PUISSANCE == i);
			} else {
				SouffleHaut = false;
			}
		}
		/*
		if (SouffleDroite) {
			Souffle = COLONNE + i;
			if (Souffle < 29) {
				CodeCase = NiveauEnCours[Souffle][LIGNE];
				if (CodeCase == CASE_BLEUE || CodeCase == CASE_TRESOR) {
					NettoyerCase(Souffle, LIGNE);
					//NiveauEnCours[Souffle][LIGNE] = 0;
					MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Explosion);
					if (! SUPER_BOMBE) {
						SouffleDroite = false;
					}
				} else if (CodeCase == CASE_NOIRE) {
					if (ULTRA_BOMBE) {
						NettoyerCase(Souffle, LIGNE);
						//NiveauEnCours[Souffle][LIGNE] = 0;
						MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Explosion);						
					}
					SouffleDroite = false;
				} else if (EstCodeBombe(CodeCase)) {
					SouffleDroite = false;
					Esquive(Souffle, LIGNE, Bombe);
				} else {
					Esquive(Souffle, LIGNE, Bombe);
					NettoyerCase(Souffle, LIGNE);
					//NiveauEnCours[Souffle][LIGNE] = 0;
					if (i == PUISSANCE) {
						MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Droite);
					} else {
						MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Horizontal);
					}
				}
			} else {
				SouffleDroite = false;
			}
		}
		//
		if (SouffleGauche) {
			Souffle = COLONNE - i;
			if (Souffle >= 0) {
				CodeCase = NiveauEnCours[Souffle][LIGNE];
				if (CodeCase == 2) {
					NettoyerCase(Souffle, LIGNE);
					//NiveauEnCours[Souffle][LIGNE] = 0;
					MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Explosion);
					if (! SUPER_BOMBE) {
						SouffleGauche = false;
					}
				} else {
					if (CodeCase == 1) {
						if (ULTRA_BOMBE) {
							NettoyerCase(Souffle, LIGNE);
							//NiveauEnCours[Souffle][LIGNE] = 0;
							MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Explosion);						
						}						
						SouffleGauche = false;
					} else {
						if (EstCodeBombe(CodeCase)) {
							SouffleGauche = false;
							Esquive(Souffle, LIGNE, Bombe);
						} else {
							NettoyerCase(Souffle, LIGNE);
							//NiveauEnCours[Souffle][LIGNE] = 0;
							Esquive(Souffle, LIGNE, Bombe);
							if (i == PUISSANCE) {
								MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Gauche);
							} else {
								MondeBrique[Souffle][LIGNE].gotoAndStop(Sprite_Horizontal);
							}
						}
					}
				}
			} else {
				SouffleGauche = false;
			}
		}
		//
		if (SouffleBas) {
			Souffle = LIGNE + i;
			if (Souffle < 19) {
				CodeCase = NiveauEnCours[COLONNE][Souffle];
				if (CodeCase == 2) {
					NettoyerCase(COLONNE, Souffle);
					//NiveauEnCours[COLONNE][Souffle] = 0;
					
					MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Explosion);
					if (! SUPER_BOMBE) {
						SouffleBas = false;
					}
				} else {
					if (CodeCase == 1) {
						if (ULTRA_BOMBE) {
							NettoyerCase(COLONNE, Souffle);
							//NiveauEnCours[COLONNE][Souffle] = 0;
							MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Explosion);						
						}							
						SouffleBas = false;
					} else {
						if (EstCodeBombe(CodeCase)) {
							SouffleBas = false;
							Esquive(COLONNE, Souffle, Bombe);
						} else {
							NettoyerCase(COLONNE, Souffle);
							//NiveauEnCours[COLONNE][Souffle] = 0;
							Esquive(COLONNE, Souffle, Bombe);
							if (i == PUISSANCE) {
								MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Bas);
							} else {
								MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Vertical);
							}
						}
					}
				}
			} else {
				SouffleBas = false;
			}
		}
		//
		if (SouffleHaut) {
			Souffle = LIGNE - i;
			if (Souffle >= 0) {
				CodeCase = NiveauEnCours[COLONNE][Souffle];
				if (CodeCase == 2) {
					NettoyerCase(COLONNE, Souffle);
					//NiveauEnCours[COLONNE][Souffle] = 0;
					MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Explosion);
					if (! SUPER_BOMBE) {
						SouffleHaut = false;
					}
				} else {
					if (CodeCase == 1) {
						if (ULTRA_BOMBE) {
							NettoyerCase(COLONNE, Souffle);
							//NiveauEnCours[COLONNE][Souffle] = 0;
							MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Explosion);						
						}						
						SouffleHaut = false;
					} else {
						if (EstCodeBombe(CodeCase)) {
							SouffleHaut = false;
							Esquive(COLONNE, Souffle, Bombe);
						} else {
							NettoyerCase(COLONNE, Souffle);
							//NiveauEnCours[COLONNE][Souffle] = 0;
							Esquive(COLONNE, Souffle, Bombe);
							if (i == PUISSANCE) {
								MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Haut);
							} else {
								MondeBrique[COLONNE][Souffle].gotoAndStop(Sprite_Vertical);
							}
						}
					}
				}
			} else {
				SouffleHaut = false;
			}
		}*/
	}
	//
	ClipBombe[COLONNE + "x" + LIGNE] = null;
	if (ClipBombe.contains(Bombe)) {
		ClipBombe.removeChild(Bombe);
	}
}

function Esquive(COLONNE:int, LIGNE:int, BOMBE:MovieClip):void {
	if ((Vivant || (ClipJoueur.Fantome && BOMBE.Type == BOMBE_FANTOME)) && !immortel
		 && (COLONNE == ClipJoueur.ColonneEnCours && LIGNE == ClipJoueur.LigneEnCours)
		 && getTimer() - DebutPartie > 10000) {
		
		_root.Envoie_Serveur("BoM#"+BOMBE.Joueur.Nom.text + "#" + BOMBE.Type);
		//Mort_Joueur(ClipJoueur);
		Mort_Joueur(ClipJoueur, (ClipJoueur.Fantome && BOMBE.Type == BOMBE_FANTOME), BOMBE.Joueur.Nom.text);
	}
}

/**
 * Affiche le tueur <Tueur> (nom en rouge) de <JoueurMort> sur la map.
 */
function Affiche_Tueur_Sur_Map(JoueurMort:MovieClip, Tueur:String):void {
	var Frag:MovieClip = new $Frag();
	
	Frag.Effet.Texte.text = Tueur;
	Frag.y = JoueurMort.y + 10;
	Frag.x = JoueurMort.x - 30;
	_Monde._Partie._Niveau.addChild(Frag);
}

/**
 * Tuer <JOUEUR>. Si <ForcerMort> = true, même les fantômes d'Halloween
 * meurent.
 */
function Mort_Joueur(JOUEUR:MovieClip, ForcerMort:Boolean = false, Tueur:String=""):void {
	if (JOUEUR.ClipJoueur) {
		if (!Vivant && (_root.Halloween && !Fantome)) {
			return;
		}
		Vivant = false;
		Fantome = (_root.Halloween && !ForcerMort);
		DeplacementEnCours = false;
		DroiteEnCours = false;
		GaucheEnCours = false;
		HautEnCours = false;
		BasEnCours = false;
		EspaceEnCours = false;
		
		Affiche_Tueur_Sur_Map(JOUEUR, Tueur);
	}
	
	JOUEUR.Vivant = false;

	JOUEUR.Droite = false;
	JOUEUR.Gauche = false;
	JOUEUR.Haut = false;
	JOUEUR.Bas = false;
	
	JOUEUR.Fantome = (_root.Halloween && !ForcerMort);
	
	if(!JOUEUR.Fantome) {
		if (ClipListeJoueur.contains(JOUEUR)) {
			ClipListeJoueur.removeChild(JOUEUR);
		}
	} else {
		Couleur_Joueur(JOUEUR, JOUEUR.Orange, true);
	}
	
	var Mort:MovieClip;
	if (JOUEUR.Orange) {
		Mort = new $MortOrange();
	} else {
		Mort = new $MortBlanche();
	}
	Mort.x = JOUEUR.x + 9;
	Mort.y = JOUEUR.y + 9;
	_Monde._Partie._Niveau.addChild(Mort);

	//ClipListeJoueur.removeChild(JOUEUR);
}