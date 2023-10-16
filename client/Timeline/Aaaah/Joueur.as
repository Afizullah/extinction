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
import flash.geom.ColorTransform;

var _root:MovieClip = MovieClip(parent);

var ClipJoueur:MovieClip;
var NomJoueur:String;
var Vivant:Boolean = false;
var Incontrolable:Boolean = false;
var Incontrolable2:Boolean = false;
var Controlable2:int = 6;
var ATIncontrolable2Compteur:int = 0;
var ATIncontrolale2Bool:Boolean = false;
var AaaahDispo:Boolean = false;
var TempsAaaah:int = 0;

var SautDispo:Boolean = true;
var Guide:MovieClip;
var LimiteChat:int;
var DernierMessage:String = "wsedcftg";
var CompteurMessageIdentique:int = 1;
var Aaaah:Boolean = false;
var AuteurAaaah:MovieClip;

var DroiteEnCours:Boolean = false;
var GaucheEnCours:Boolean = false;
var ATSautsDispoBool:Boolean = false;

var LsPseudosEquipe2Elo:Array = null; // Liste des joueurs dont le pseudo est à colorer (étant dans l'équipe 2 d'une partie classée)

function Zombification(JOUEUR:MovieClip, OUI:Boolean, PRESQUE:Boolean = false):void {
	if (JOUEUR.Joueur) {
		// moi, non contaminé
		JOUEUR.Nom.textColor = 0xC2C2DA;
	} else {
		// autres non contaminés
		if (_Monde.Anonyme != "anonyme") {
			if (LsPseudosEquipe2Elo != null && LsPseudosEquipe2Elo.indexOf(JOUEUR.Nom.text) != -1) {
				JOUEUR.Nom.textColor = 0xC049F3;//0x7AF5FF;
			} else if (_root.SaintValentin && JOUEUR.Nom.text == _root.StVAmoureux) {
				JOUEUR.Nom.textColor = 0xF24AB8;
			} else if (JOUEUR.IdTeamJoueur!=0 && JOUEUR.IdTeamJoueur == _root.IdTeam) {
				JOUEUR.Nom.textColor=0x009D9D;
			} else if (JOUEUR.IdTeamJoueur != 0 && JOUEUR.IdTeamJoueur == _root.IdTeamColorée1) {
				JOUEUR.Nom.textColor = 0x9B4E00;
			} else if (JOUEUR.IdTeamJoueur != 0 && JOUEUR.IdTeamJoueur == _root.IdTeamColorée2) {
				JOUEUR.Nom.textColor = 0xC049F3;
			} else {
				JOUEUR.Nom.textColor = 0x6C77C1;
			}
		}
	}
	if (PRESQUE) {
		// jaune
		if (_root.SaintValentin && JOUEUR.Nom.text == _root.StVAmoureux) {
			JOUEUR.Nom.textColor = 0xF24AB8;
		} else {
			JOUEUR.Nom.textColor = 0xC9CD36;
		}
	} else {
		if (OUI) {
			//contaminé
			if (_root.SaintValentin && JOUEUR.Nom.text == _root.StVAmoureux) {
				JOUEUR.Nom.textColor = 0xF24AB8;
			} else if (JOUEUR.Joueur) {
				JOUEUR.Nom.textColor = 0xFF0044;
			} else{
				JOUEUR.Nom.textColor = 0xCB546B;
			}
			
			if (_root.Halloween) {
				Diaboliser(JOUEUR, true);
			}
			
			if (! JOUEUR.Zombie) {
				JOUEUR.Zombie = true;
				//
				if (JOUEUR._A._CourseDroite.visible) {
					JOUEUR.DeplacementX = 2.4;
				} else {
					if (JOUEUR._A._CourseGauche.visible) {
						JOUEUR.DeplacementX = -2.4;
					}
				}
				//
				JoueurEnVie--;
				_Monde.Porte.EnVie.text = JoueurEnVie + "/" + JoueurDepart;
				//
				if (JOUEUR.Joueur) {
					ClipJoueur.CompteurAaaah.visible = false;
				}
			}
		} else {
			if (JOUEUR.Zombie) {
				JOUEUR.Zombie = false;
			}
			//
			if (JOUEUR._A._CourseDroite.visible) {
				JOUEUR.DeplacementX = 2;
			} else {
				if (JOUEUR._A._CourseGauche.visible) {
					JOUEUR.DeplacementX = -2;
				}
			}
			//
			if (JOUEUR.Joueur) {
				ClipJoueur.CompteurAaaah.visible = true;
			}
		}
	}
}

function ActuValeursCri(Message:String):void {
	var ParamCri = Message.split(";");
	
	PuissanceAaaahX = parseInt(ParamCri[0]);
	TempsEntreAaaah = parseInt(ParamCri[1]);
}

function Initialisation_Joueurs(LISTE:Array):void {
	_Monde._Texte.htmlText = _root.TexteEnCours;
	_Monde._Texte.scrollV = _Monde._Texte.maxScrollV;
	
	_Monde.Vote.visible = false;
	VoteEnCours = false;
	//
	while (_Monde._ListeJoueur.numChildren != 0) {
		_Monde._ListeJoueur.removeChildAt(0);
	}
	//
	ListeJoueur = new Array();
	//var SansConta : Boolean = (LISTE[1] == "1");
	
	/*var MS:Boolean = (LISTE[1] == "1");
	if (MS) {
		PuissanceAaaahX = 6;
		TempsEntreAaaah = 15000;
	}else{
		PuissanceAaaahX = 4;
		TempsEntreAaaah = 10000;
	}*/
	SalonFight = (LISTE[2] == "1");
	if (SalonFight) {
		Nouveau_Message_Chat("Vous entrez dans un salon fight/survivor. L'infirmerie est désactivée. Le but est d'éliminer vos adversaires et de survivre aux autres.");	
	}
	// cartes spécifiques
	/*if (SansConta || !(LISTE[3] == "0")) {
		Nouveau_Message_Chat("Les statistiques sont désactivées.");		
	}*/
	// salon Rally public
	RallyPublic = (LISTE[4] == "1");
	// salon MS
	if (LISTE[3]=="5") {
		TempsEntreAaaah = 10000;
		Nouveau_Message_Chat("Vous entrez dans un salon Mutual Shout. Le but est d'arriver à l'infirmerie en s'aidant mutuellement avec les cris.");	
	} else if (LISTE[3]=="2") {
		Nouveau_Message_Chat("Vous entrez dans un salon NG. Le but est d'arriver le premier à l'infirmerie. Les cris sont autorisés.");	
	} else if (LISTE[3]=="6") {
		Nouveau_Message_Chat("Vous entrez dans un salon rally. Le but est de battre le fantôme. Les cris sont désactivés.");	
	}
	GhostLog = (LISTE[3]=="6"); // rally
	GhostView = GhostLog;
	
	var InfoJoueur:Array = LISTE[5].split(",");
	ListeJoueur.push(InfoJoueur);
	NomJoueur = InfoJoueur[0];
	ClipJoueur = new $JoueurAaaah();
	
	GestionRecompensesElo(int(InfoJoueur[1]), ClipJoueur);
	InfoJoueur.pop(); // On retire l'info sur l'elo
	
	ActuValeursCri(LISTE[1]);
	
	/// Noël/Halloween - Chargé par défaut pour laisser /eventtest fonctionnel
	var BonnetGauche = new $BGNoelA();
	var BonnetDroite = new $BDNoelA();
	var Citrouille = new $CitrouilleA();
	
	Citrouille.x = -4.75;
	Citrouille.y = -1.80;
	BonnetGauche.x = -12.50;
	BonnetGauche.y = 0;
	BonnetDroite.x = -0.5;
	BonnetDroite.y = 0;
	ClipJoueur.addChild(Citrouille);
	ClipJoueur.addChild(BonnetGauche);
	ClipJoueur.addChild(BonnetDroite);
	ClipJoueur.Citrouille = Citrouille;
	ClipJoueur.BonnetGauche = BonnetGauche;
	ClipJoueur.BonnetDroite = BonnetDroite;
	ClipJoueur.BonnetDroite.visible = false;
	if (!_root.Noel || _root.TeteAaaahSansEvent) { // Perso orienté sur la droite au début
		ClipJoueur.BonnetGauche.visible = false;
	}
	if (!_root.Halloween || _root.TeteAaaahSansEvent) {
		ClipJoueur.Citrouille.visible = false;
	}
	///
	
	ClipJoueur.mouseEnabled = false;
	ClipJoueur.mouseChildren = false;
	InfoJoueur.push(ClipJoueur);
	ClipJoueur.Nom.text = NomJoueur;
	ClipJoueur.Nom.autoSize = "left";
	ClipJoueur.Nom.x =  -  ClipJoueur.Nom.width / 2;
	ClipJoueur.Nom.textColor = 0xC2C2DA;
	ClipJoueur.removeChild(ClipJoueur.Bulle);
	ClipJoueur.CompteurAaaah = new $Marqueur();
	ClipJoueur.addChild(ClipJoueur.CompteurAaaah);
	ClipJoueur.Zombie = false;
	ClipJoueur.Nom.y = -20;
	ClipJoueur.CompteurAaaah.x = -15;
	ClipJoueur.CompteurAaaah.y = -4;
	ClipJoueur.y = 353;
	Formatage_Mobile(ClipJoueur, true, true);
	_Monde._ListeJoueur.addChild(ClipJoueur);
	_Monde._ListeJoueur[NomJoueur] = ClipJoueur;
	//
	var NbJoueur:int = LISTE.length;
	for (var i:int = 6; i<NbJoueur; i++) {
		var Info:Array = LISTE[i].split(",");
			//ListeJoueur.push(Info);
			var Joueur:MovieClip = new $JoueurAaaah();
			ListeJoueur.push([Info[0], Joueur]);
			
			// Noël - Chargé par défaut pour laisser /eventtest fonctionnel
			BonnetGauche = new $BGNoelA();
			BonnetDroite = new $BDNoelA();
			Citrouille = new $CitrouilleA();
	
			Citrouille.x = -4.75;
			Citrouille.y = -1.80;
			BonnetGauche.x = -12.50;
			BonnetGauche.y = 0;
			BonnetDroite.x = -0.5;
			BonnetDroite.y = 0;
			Joueur.addChild(Citrouille);
			Joueur.addChild(BonnetGauche);
			Joueur.addChild(BonnetDroite);
			Joueur.Citrouille = Citrouille;
			Joueur.BonnetGauche = BonnetGauche;
			Joueur.BonnetDroite = BonnetDroite;
			Joueur.BonnetDroite.visible = false;
			if (!_root.Noel || _root.TeteAaaahSansEvent) { // Perso orienté sur la droite au début
				Joueur.BonnetGauche.visible = false;
			}
			if (!_root.Halloween || _root.TeteAaaahSansEvent) {
				Joueur.Citrouille.visible = false;
			}
			///
			
			Joueur.mouseEnabled = false;
			Joueur.mouseChildren = false;
			//Info.push(Joueur);
			var Nom:String = Info[0];
			Joueur.Nom.text = Nom;
			Joueur.IdTeamJoueur = int(Info[3]);
			
			if (_Monde.Anonyme != "anonyme") {
				if (LsPseudosEquipe2Elo != null && LsPseudosEquipe2Elo.indexOf(Nom) != -1) {
					Joueur.Nom.textColor = 0xC049F3;
				} else if (_root.SaintValentin && Nom == _root.StVAmoureux) {
					Joueur.Nom.textColor = 0xF24AB8;
				} else if (Joueur.IdTeamJoueur!=0 && Joueur.IdTeamJoueur == _root.IdTeam) {
					Joueur.Nom.textColor=0x009D9D;
				} else if (Joueur.IdTeamJoueur != 0 && Joueur.IdTeamJoueur == _root.IdTeamColorée1) {
					Joueur.Nom.textColor = 0x9B4E00;
				} else if (Joueur.IdTeamJoueur != 0 && Joueur.IdTeamJoueur == _root.IdTeamColorée2) {
					Joueur.Nom.textColor = 0xC049F3;
				}
			}
			
			Joueur.Nom.autoSize = "left";
			Joueur.Nom.x =  -  Joueur.Nom.width / 2;
			Joueur.removeChild(Joueur.Bulle);
			Formatage_Mobile(Joueur, true, false);
			Joueur.Score = int(Info[1]);
			if (Info[2] == "1") {
				Zombification(Joueur, true);
			}
			//Info.splice(1, 3);
			_Monde._ListeJoueur.addChild(Joueur);
			_Monde._ListeJoueur[Nom] = Joueur;
			
		GestionRecompensesElo(int(Info[4]), Joueur);

	}
	//
	_root._Serveur.visible = false;
	if (! _Editeur.visible) {
		_Monde.visible = true;
	}
	//
	stage.removeEventListener(Event.ENTER_FRAME, Boucle_Moteur);
	_Monde._ListeJoueur.visible = false;
	_Monde._Dessin.visible = false;
	_Monde.Porte.visible = false;
	//
	TimerDepart.reset();
	TimerDepart.start();
	_Monde.Info.visible = true;
	//
	MAJ_Liste_Joueur();
	Ascenseur_Reset(_Monde.Ascenseur2);
}

function Nouveau_Joueur(INFO:Array):void {
	//ListeJoueur.push(INFO);
	var Joueur:MovieClip = new $JoueurAaaah();
	
	ListeJoueur.push([INFO[0], Joueur]);
	
	Joueur.mouseEnabled = false;
	Joueur.mouseChildren = false;
	
	/// Noël/Halloween - Chargé par défaut pour laisser /eventtest fonctionnel
	var BonnetGauche = new $BGNoelA();
	var BonnetDroite = new $BDNoelA();
	var Citrouille = new $CitrouilleA();
	
	Citrouille.x = -4.75;
	Citrouille.y = -1.80;
	BonnetGauche.x = -12.50;
	BonnetGauche.y = 0;
	BonnetDroite.x = -0.5;
	BonnetDroite.y = 0;
	Joueur.addChild(Citrouille);
	Joueur.addChild(BonnetGauche);
	Joueur.addChild(BonnetDroite);
	Joueur.Citrouille = Citrouille;
	Joueur.BonnetGauche = BonnetGauche;
	Joueur.BonnetDroite = BonnetDroite;
	Joueur.BonnetDroite.visible = false;
	if (!_root.Noel || _root.TeteAaaahSansEvent) { // Perso orienté sur la droite au début
		Joueur.BonnetGauche.visible = false;
	}
	if (!_root.Halloween || _root.TeteAaaahSansEvent) {
		Joueur.Citrouille.visible = false;
	}
	///
	
	//INFO.push(Joueur);
	var Nom:String = INFO[0];
	Joueur.Zombie = false;
	Joueur.Nom.text = Nom;
	Joueur.IdTeamJoueur = int(INFO[3]);
	
	if (LsPseudosEquipe2Elo != null && LsPseudosEquipe2Elo.indexOf(Nom) != -1) {
		Joueur.Nom.textColor = 0xC049F3;
	} else if (_root.SaintValentin && Nom == _root.StVAmoureux) {
		Joueur.Nom.textColor = 0xF24AB8;
	} else if (Joueur.IdTeamJoueur!=0 && Joueur.IdTeamJoueur == _root.IdTeam) {
		Joueur.Nom.textColor=0x009D9D;
	} else if (Joueur.IdTeamJoueur != 0 && Joueur.IdTeamJoueur == _root.IdTeamColorée1) {
		Joueur.Nom.textColor = 0x9B4E00;
	} else if (Joueur.IdTeamJoueur != 0 && Joueur.IdTeamJoueur == _root.IdTeamColorée2) {
		Joueur.Nom.textColor = 0xC049F3; // 0xC21682
	}
	Joueur.Nom.autoSize = "left";
	Joueur.Nom.x =  -  Joueur.Nom.width / 2;
	Joueur.removeChild(Joueur.Bulle);
	Joueur.x = int(INFO[1]);
	Joueur.y = int(INFO[2]);
	
	GestionRecompensesElo(int(INFO[4]), Joueur);
	////INFO.splice(1, 3); //// 084c
	Formatage_Mobile(Joueur, true, false);
	_Monde._ListeJoueur.addChild(Joueur);
	_Monde._ListeJoueur[Nom] = Joueur;
	MAJ_Liste_Joueur();
	Desactivation_Mobile(Joueur);
	
	if (_root.SaintValentin && Nom == _root.StVAmoureux) {
		Nouveau_Message_Chat("Connexion de votre tendre "+Nom+".");
	} else if (_root.AffichageConnexions) {
		Nouveau_Message_Chat("Connexion de "+Nom+".");
	}
}

function Diaboliser(JOUEUR:MovieClip, DIABOLISER:Boolean) {
	if (DIABOLISER) {
		var nouvCouleur:ColorTransform = new ColorTransform();
		nouvCouleur.color = 0xCC0033;
		JOUEUR._A.transform.colorTransform = nouvCouleur;
		
		JOUEUR._A._CourseGauche.course.Diable.visible = true;
		JOUEUR._A._CourseDroite.course.Diable.visible = true;
		JOUEUR._A._StatiqueDroite.Diable.visible = true;
		JOUEUR._A._StatiqueGauche.Diable.visible = true;
		JOUEUR.Citrouille.visible = false;
	} else {
		GestionRecompensesElo(JOUEUR.RecompensesElo, JOUEUR, 0);
		JOUEUR._A._CourseGauche.course.Diable.visible = false;
		JOUEUR._A._CourseDroite.course.Diable.visible = false;
		JOUEUR._A._StatiqueDroite.Diable.visible = false;
		JOUEUR._A._StatiqueGauche.Diable.visible = false;
		JOUEUR.Citrouille.visible = _root.Halloween && !_root.TeteAaaahSansEvent;
	}
}

/*
 ACTION :
   - 0 : Mise de couleur
   - 1 : Cacher la couleur (map Anonymous - 62)
   - 2 : Restaurer la couleur (après map Anonymous)
*/
function GestionRecompensesElo(RECOMPENSE:int, JOUEUR:MovieClip, ACTION:int = 0) {
	if (ACTION == 0 || ACTION == 2) { // Mise de couleur / Restauration
		if (ACTION == 0) {
			JOUEUR.RecompensesElo = RECOMPENSE;
		}
		
		var nouvCouleur:ColorTransform = new ColorTransform();
		nouvCouleur.color = CouleurFromElo(JOUEUR.RecompensesElo);
		JOUEUR._A.transform.colorTransform = nouvCouleur;
	} else if (ACTION == 1) {
		JOUEUR._A.transform.colorTransform = new ColorTransform();
	}
}

function CouleurFromElo(RECOMPENSE:int):int {
	/*
	#514100 Or
#454545 Argent
#3E2500 Bronze*/
	if (_root.RcmpEloActives == 2) { // Visibilité des récompenses désactivée
		return 0x000000;
	}

	if (RECOMPENSE == 3) { // Or
		return 0x846900;//0xAA8800;
	} else if (RECOMPENSE == 2) { // Argent
		return 0x4D4D4D;//0x868686;
	} else if (RECOMPENSE == 1) { // Bronze
		return 0x552B2B;//0x5F3F3A;
	}
	
	return 0;
}

function Deco_Joueur(JOUEUR:String, BANNI:Boolean = false):void {
	var NbJoueur:int = ListeJoueur.length;
	for (var i:int = 0; i<NbJoueur; i++) {
		var Info:Array = ListeJoueur[i];
		if (Info[0] == JOUEUR) {
			var MobileJ:MovieClip = _Monde._ListeJoueur[JOUEUR];
			
			MobileJ.Actif = true;
			Desactivation_Mobile(MobileJ);
			for (var j = 0; j < _Monde._ListeJoueur[JOUEUR].numChildren; ++j) {
				_Monde._ListeJoueur[JOUEUR].removeChildAt(0);
			}
			//_Monde._ListeJoueur[JOUEUR].removeChildren(0, _Monde._ListeJoueur[JOUEUR].numChildren - 1);
			_Monde._ListeJoueur[JOUEUR].BonnetGauche = null;
			_Monde._ListeJoueur[JOUEUR].BonnetDroite = null;
			_Monde._ListeJoueur[JOUEUR].Citrouille = null;
			_Monde._ListeJoueur[JOUEUR] = null;
			
			ListeJoueur.splice(i, 1);
			if (BANNI) {
				//Nouveau_Message_Chat("<font color='#CB546B'>"+JOUEUR+" a été banni.");
			} else {
				if (_root.AffichageConnexions) {
					Nouveau_Message_Chat(JOUEUR+" s'est déconnecté(e).");
				}
			}
			
			MAJ_Liste_Joueur();
			return;
		}
	}
}

function Replacement_Joueur(JOUEUR:MovieClip, X:int, Y:int, VX:Number, VY:Number):void {
	JOUEUR.x = X;
	JOUEUR.y = Y;
	JOUEUR.VitesseX = VX;
	JOUEUR.VitesseY = VY;
}

function Deplacement_Joueur(JOUEUR:MovieClip, X:int, Y:int, ARRET:Boolean, DROITE:Boolean = false):void {
	var ClipAnim:MovieClip = JOUEUR._A;
	JOUEUR.x = X;
	JOUEUR.y = Y;
	if (ARRET) {
		//JOUEUR.Temps = 0;
		JOUEUR.DeplacementX = 0;
		if (ClipAnim._CourseGauche.visible) {
			ClipAnim._StatiqueGauche.visible = true;
			ClipAnim._StatiqueDroite.visible = false;
			//
			if (ClipAnim.numChildren != 0) {
				ClipAnim.removeChildAt(0);
			}
			ClipAnim.addChild(ClipAnim._StatiqueGauche);
		} else {
			if (ClipAnim._CourseDroite.visible) {
				ClipAnim._StatiqueDroite.visible = true;
				ClipAnim._StatiqueGauche.visible = false;
				//
				if (ClipAnim.numChildren != 0) {
					ClipAnim.removeChildAt(0);
				}
				ClipAnim.addChild(ClipAnim._StatiqueDroite);
			}
		}
		ClipAnim._CourseGauche.visible = false;
		ClipAnim._CourseDroite.visible = false;
	} else {
		if (DROITE) {
			//JOUEUR.Distance = 0;
			//JOUEUR.Temps = getTimer();
			//JOUEUR.VitesseX = 1;
			if (JOUEUR.Zombie) {
				JOUEUR.DeplacementX = 2.4;
			} else {
				JOUEUR.DeplacementX = 2;
			}
			
			if(_root.Noel && !_root.TeteAaaahSansEvent) {
				JOUEUR.BonnetDroite.visible = false;
				JOUEUR.BonnetGauche.visible = true;
			}
			
			ClipAnim._CourseGauche.visible = false;
			ClipAnim._StatiqueGauche.visible = false;
			ClipAnim._StatiqueDroite.visible = false;
			ClipAnim._CourseDroite.visible = true;
			//
			if (ClipAnim.numChildren != 0) {
				ClipAnim.removeChildAt(0);
			}
			ClipAnim.addChild(ClipAnim._CourseDroite);
		} else {
			//JOUEUR.Distance = 0;
			//JOUEUR.Temps = getTimer();
			//JOUEUR.VitesseX = -1;
			if (JOUEUR.Zombie) {
				JOUEUR.DeplacementX = -2.4;
			} else {
				JOUEUR.DeplacementX = -2;
			}
			
			if(_root.Noel && !_root.TeteAaaahSansEvent) {
				JOUEUR.BonnetDroite.visible = true;
				JOUEUR.BonnetGauche.visible = false;
			}
			
			ClipAnim._StatiqueGauche.visible = false;
			ClipAnim._StatiqueDroite.visible = false;
			ClipAnim._CourseDroite.visible = false;
			ClipAnim._CourseGauche.visible = true;
			//
			if (ClipAnim.numChildren != 0) {
				ClipAnim.removeChildAt(0);
			}
			ClipAnim.addChild(ClipAnim._CourseGauche);
		}
	}
}

function Saut(JOUEUR:MovieClip, X:int, Y:int, DIRECTION:int):void {
	JOUEUR.VitesseY = -5;
}

var TimerSaut:Timer = new Timer(500);
TimerSaut.addEventListener(TimerEvent.TIMER, Timer_Saut);
function Timer_Saut(E:Event):void {
	SautDispo = true;
	TimerSaut.stop();
	TimerSaut.reset();
}

var GhostLog : Boolean = false; 
var GhostView : Boolean = false; 
var GhostMessages : String = "";
var GhostRecordPerso : int = 120000; // stocke le record perso pour l'autoghost
var GhostMessagesPerso : String = "";
var MapId : String = ""; // code de la dernière map

function Envoie_Deplacement(Message : String): void{
	_root.Envoie_Serveur(Message);
	if (GhostLog) {
		GhostMessages += ","+(getTimer()-TempsDebut)+":"+Message;
	}
}

function Clavier_onPress(E:KeyboardEvent):void {
	_root.Inaction = 0;
	isAfk = 0;
	//

							
	if ((E.keyCode == 226 || (E.keyCode == 192 && Capabilities.os.indexOf("Mac OS") == 0)) && !E.altKey ){
			_Monde.BarreChat.Changer_Cible(null);
			stage.focus = _Monde.BarreChat._ChatEntrée;
			return;				
	}
			
	if (E.keyCode == 13) {
		if (_Editeur.visible) {
			stage.focus = stage;
		} else {

		
			if (stage.focus == _Monde.BarreChat._ChatEntrée) {
				stage.focus = stage;

				var Invité : Boolean = (_root.NomJoueur.substr(0,1) == "*");

				//

				var Texte:String = _Monde.BarreChat._ChatEntrée.text;
				while (Texte.substr(0, 1) == " ") {
					Texte = Texte.substr(1);
				}

				_Monde.BarreChat._ChatEntrée.text = "";

				if (Texte == "") {
					return;
				}

				if (getTimer() - LimiteChat < 500) {
					Nouveau_Message_Chat("Doucement, merci.");
					return;
				}


				//
				if (Texte.toLowerCase() == "/editeur" && !Invité) {
					Commande_Editeur();
					return;
				}
				//
				if (Texte.charAt(0) == "/") {
					_root.Commandes(Texte.substr(1));
					return;
				}

				if (Invité) {
					Nouveau_Message_Chat("Vous devez créer un compte pour pouvoir parler.");
					_Monde.BarreChat._ChatEntrée.text = "";
					return;
				}
				
				//
				if (DernierMessage == Texte) {
					if(CompteurMessageIdentique == 2) {
						Nouveau_Message_Chat("Votre dernier message est identique.");
						return;
					}
					CompteurMessageIdentique++;
				}
				else {
					CompteurMessageIdentique = 1;
				}

				LimiteChat = getTimer();
				DernierMessage = Texte;
				if (_root.Censure(Texte)) {
					Nouveau_Message_Chat("Merci de rester poli.");
				} else if (!_root.CensureMdp(Texte)) {				
					if (_Monde.BarreChat.Destinataire.visible) {
						var Destinataire:String = _Monde.BarreChat.Destinataire.text;
						if (Destinataire == "") {
							stage.focus = _Monde.BarreChat.Destinataire;
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
						} else if (_Monde.BarreChat.Cible.VIP.visible) {
							_root.Envoie_VIP(Texte);
						} else if (_Monde.BarreChat.Cible.CT.visible) {
							_root.Envoie_Fusion(Texte);
						} else{
							_root.Envoie_Serveur("ChM#"+Texte);
						}
					}
				}
			} else if (!_root._InfoTeam.visible){
				stage.focus = _Monde.BarreChat._ChatEntrée;
			}
		}
	}
	//
	//if (Vivant && ! Incontrolable) {
	if (Vivant) {
		// DROITE
		if ((E.keyCode == 68 || E.keyCode == 39) && !DroiteEnCours) {
			Envoie_Deplacement("MvD#"+ClipJoueur.x+"#"+ClipJoueur.y);
			DroiteEnCours = true;
			Deplacement_Joueur(ClipJoueur, ClipJoueur.x, ClipJoueur.y, false, true);
			E.updateAfterEvent();
		} else {
			// GAUCHE
			if (((E.keyCode == 81 && _root.Azerty) || (E.keyCode == 65 && !_root.Azerty) || E.keyCode == 37) && !GaucheEnCours) {
				Envoie_Deplacement("MvG#"+ClipJoueur.x+"#"+ClipJoueur.y);
				GaucheEnCours = true;
				Deplacement_Joueur(ClipJoueur, ClipJoueur.x, ClipJoueur.y, false, false);
				E.updateAfterEvent();
			} else {
				// Saut
				if (((E.keyCode == 90 && _root.Azerty) || (E.keyCode == 87 && !_root.Azerty) || E.keyCode == 38) && SautDispo && !Incontrolable2) {
					SautDispo = false;
					SautDispo = false;
					SautDispo = false;					
					if (SautDispo && !ATSautsDispoBool) {
						ATSautsDispoBool = true;
						_root.Envoie_Serveur("CxTriche#SautsAaaah#????");										
					}	
					SautDispo = false;					
					TimerSaut.start();
					if (DroiteEnCours) {
						Envoie_Deplacement("MvH#"+ClipJoueur.x+"#"+ClipJoueur.y+"#"+1);
						Saut(ClipJoueur, ClipJoueur.x, ClipJoueur.y, 1);
					} else {
						if (GaucheEnCours) {
							Envoie_Deplacement("MvH#"+ClipJoueur.x+"#"+ClipJoueur.y+"#"+2);
							Saut(ClipJoueur, ClipJoueur.x, ClipJoueur.y, 2);
						} else {
							Envoie_Deplacement("MvH#"+ClipJoueur.x+"#"+ClipJoueur.y+"#"+0);
							Saut(ClipJoueur, ClipJoueur.x, ClipJoueur.y, 0);
						}
					}
					ClipJoueur.Apesanteur = true;
					Incontrolable2 = true;
					Incontrolable2 = true;
					Controlable2 = 0;					
					E.updateAfterEvent();
				} else {
					// Aaaah cri
					if (!ClipJoueur.Zombie && AaaahDispo && (E.keyCode == 83 || E.keyCode == 32 || E.keyCode == 40) && getTimer()-TempsAaaah > TempsEntreAaaah && (stage.focus != _Monde.BarreChat._ChatEntrée || E.keyCode == 40)) {
						TempsAaaah = getTimer();
						_root.Envoie_Serveur("IdB#"+ClipJoueur.x+"#"+ClipJoueur.y);
						E.updateAfterEvent();
					}
				}
			}
		}
	}
	
	if (E.keyCode == 77 && E.ctrlKey ){
			stage.quality="low";
			return;				
	}
	
}

function Clavier_onRelease(E:KeyboardEvent):void {
	if (Vivant) {
		if (E.keyCode == 68 || E.keyCode == 39) {
			DroiteEnCours = false;
			if (! GaucheEnCours) {
				Envoie_Deplacement("MvS#"+ClipJoueur.x+"#"+ClipJoueur.y);
				Deplacement_Joueur(ClipJoueur, ClipJoueur.x, ClipJoueur.y, true);
				E.updateAfterEvent();
			}
		} else {
			if ((E.keyCode == 81 && _root.Azerty) || (E.keyCode == 65 && !_root.Azerty) || E.keyCode == 37) {
				GaucheEnCours = false;
				if (! DroiteEnCours) {
					Envoie_Deplacement("MvS#"+ClipJoueur.x+"#"+ClipJoueur.y);
					Deplacement_Joueur(ClipJoueur, ClipJoueur.x, ClipJoueur.y, true);
					E.updateAfterEvent();
				}
			}
		}
	}
	if (VoteEnCours) {
		if (E.keyCode == 115 || E.keyCode == 116) {
			Clique_Non(null);
			if (_root.NoVote) {
				Nouveau_Message_Chat("<font color='#009D9D'>Vote \"Non\" enregistré.</font>");				
			}			
		} else if (E.keyCode == 117) {
			Clique_SansAvis(null);
		} else if (E.keyCode == 118) {
			Clique_Oui(null);
			if (_root.NoVote) {
				Nouveau_Message_Chat("<font color='#009D9D'>Vote \"Oui\" enregistré.</font>");				
			}						
		}
	}
}