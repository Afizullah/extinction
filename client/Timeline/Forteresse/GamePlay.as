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
import flash.display.Shape;
import flash.display.BitmapData;

var _root:MovieClip = MovieClip(parent);
_Monde._Plateau.mask = _Monde.MasquePlateau;

var NomJoueur:String;
var CodeJoueur:int;
var Vivant:Boolean = false;
var DateMort:int;
var VisionSur:int = -1;

var MondeEnCours:MovieClip;
var DataCase:BitmapData = null; // Blocs construits sur la map
var MondePixel:BitmapData = null; // Monde en cours sous forme de pixels

var CaseDispo:int = 20;
var ATCaseDispo:int = 9800; // AntiTriche CaseDispo
var ATCaseDispoCompteur: int = 0; // AntiTriche compteur
var ATCoordCompteur: int = 0; // AntiTriche ATCoordCompteur compteur
var ATCoordCompteurActive:Boolean = true; // Permet de désactivé temporairement la détection des TP
/// Modification via triche des balles moins aisées :
var ChargeurCoef:int = Math.random() * 10 + 5;
var ChargeurBase:int = Math.random() * 100 + 123;
var ChargeurNextValeur:int = 11 * ChargeurCoef + ChargeurBase;
///
var Chargeur:int = 12 * ChargeurCoef + ChargeurBase;
var Jambombe:int;
var EstUneCible:Boolean = false; // Indique si le joueur est une des cibles
var DebutPartie:int;
var DebutPartieServeur:int;

var ListeCase:Array = new Array();
var ListeCaseCadeaux:Array = new Array();

var JoueurFocus:MovieClip = null;
var FocusJusteFini:Boolean = false;

var ListeJoueur:Array = new Array();
var ListeBleu:Array = new Array();
var ListeRouge:Array = new Array();
var EquipeCible:Array;
var TirAuto:Boolean = false;
var Précision:$Variable = new $Variable(0, 1000);
Précision.Valeur = 10;
// Constructible 0 - 4, Destructible 1 - 0, Indestructible 2 - 1, Objectif 3 - 2 (, Objectif2 4), Non reconstructible 8 - 3, Inconstructible 9 - 5
var CouleursPerso:Array = new Array(6);
var CouleursBase:Array = new Array(6);
CouleursBase[0] = 0x14172E, CouleursBase[1] = 0x14172E, CouleursBase[2] = 0xFF7F27, CouleursBase[3] = 0x14172E;
CouleursPerso[0] = CouleursBase[0], CouleursPerso[1] = CouleursBase[1], CouleursPerso[2] = CouleursBase[2], CouleursPerso[3] = CouleursBase[3];
////
CouleursBase[4] = 0x9E9EAF, CouleursBase[5] = 0x9E9EAF;
CouleursPerso[4] = CouleursBase[4], CouleursPerso[5] = CouleursBase[5];
////
var CouleursPersonnalisées:Boolean = false;

var NumPresidentRouge:String = "-1";
var NumPresidentBleu:String = "-1";

// Arme
var ArmeEnCours:int = 0;
var ListeJambon:Array;

// Monde
var MondePersoToString:String = ""; // String monde personnalisé pour sauvegarde
var AttaqueRougeEnCours:Boolean;
var TempsTotalPartie:Number=480;
var SpawnDéfenseX:int;
var SpawnDéfenseY:int;
var SpawnAttaqueX:int;
var SpawnAttaqueY:int;

// Saut
var ListeSaut:Array = new Array(1000);
var DernierSaut:int = 0;

var MapSauvegardable:Boolean = true;

_Monde._Aide.visible = false;

function StValAmoureuxPresent():Boolean {
	var J:MovieClip;
	for (var i:int = 0; i < ListeJoueur.length; ++i) {
		J = ListeJoueur[i];
		if (J != null && J.NomJoueur == _root.StVAmoureux) {
			return true;
		}
	}
	
	return false;
}

function Reset_Joueur():void {
	if (_root.H != loaderInfo.bytes.toString().toUpperCase()) {
		_root.Serveur.close();
		return;
	}
	while (ClipJoueur._ListeEffet.numChildren != 0) {
		ClipJoueur._ListeEffet.removeChildAt(0);
	}
	//
	ToucheConstruction = false;
	ModeConstruction = false;
	Curseur.visible = false;
	if (!_root.NoLag) {
	_Réticule.visible = true;
	}
	ConstructionActive = false;
	//
	CaseDispo = 1000;
	ATCaseDispo = 1;
	Chargeur = 12 * ChargeurCoef + ChargeurBase;
	ChargeurNextValeur = 11 * ChargeurCoef + ChargeurBase;
	Jambombe = 3;
	_Monde._Action.Arme1.Texte.text = ""+Jambombe;
	ClipJoueur.Chargeur.Balle.text = "12";
	
	if (_root.SaintValentin && StValAmoureuxPresent()) {
		ClipJoueur.Chargeur.Santé.text = "20";
	} else {
		ClipJoueur.Chargeur.Santé.text = "10";
	}
	ClipJoueur.Chargeur.Barre.width = 30;
	Vivant = true;
	ClipJoueur.Vivant = true;
	ClipJoueur.visible = true;
	if (!(_root.Espionnage)) CibleCamera = ClipJoueur;
	ClipJoueur.VitesseX = 0;
	ClipJoueur.VitesseY = 0;
	_root.Envoie_Serveur(Codage(1)+$+ClipJoueur.x+$+ClipJoueur.y+$+"0"+$+"0");
	ClipRespawn.visible = false;
	Changement_Arme(0);
	ClipJoueur.x2 = 100000-2*ClipJoueur.x;
	ClipJoueur.y2 = 100000-2*ClipJoueur.y;	
}

function ChangerCouleur(J:MovieClip, NouvCouleur:int):void {
	ATCoordCompteurActive = false;
	SetCouleurJoueur(J, NouvCouleur, true);
	ClipJoueur.x2 = 100000-2*ClipJoueur.x;
	ClipJoueur.y2 = 100000-2*ClipJoueur.y;
	
	Deplacement(ClipJoueur, false, true);
	ATCoordCompteurActive = true;
}

function ConstructionCadeau(X:int, Y:int):void {
	var r:Number = Math.random();
	var Cadeau:MovieClip = new MovieClip();
	
	if (r < 0.143) {
		Cadeau = new $CadeauFNoel0();
	} else if (r < 0.286) {
		Cadeau = new $CadeauFNoel1();
	} else if (r < 0.429) {
		Cadeau = new $CadeauFNoel2();
	} else if (r < 0.572) {
		Cadeau = new $CadeauFNoel3();
	} else if (r < 0.715) {
		Cadeau = new $CadeauFNoel4();
	} else if (r < 0.858) {
		Cadeau = new $CadeauFNoel5();
	} else {
		Cadeau = new $CadeauFNoel6();
	}
	
	Cadeau.x = X * 10;
	Cadeau.y = Y * 10;
	ClipCarte.addChild(Cadeau);
	ListeCaseCadeaux[X][Y] = Cadeau;
}

function Construction(X:int, Y:int, COULEUR:int, TYPE:int):void {
	var Couleur:int;
	
	if (ListeCase[X][Y] == 0) {
		ListeCase[X][Y] = 1;
		
		if (COULEUR == 0) { // BLEU
			
			if (CouleursPersonnalisées) {
				Couleur = CouleursPerso[0];
			} else if (_root.Carnaval) {
				Couleur = (Math.round(Math.random() * 100) % 255) | ((Math.round(Math.random() * 100) % 255) << 8) | ((Math.round(Math.random() * 100) % 255) << 16);
			} else if (_root.Halloween) {
				Couleur = 0x26754E;
			} else {
				Couleur = CouleursBase[0];
			}
		} else if (COULEUR == 1) { // ROUGE
			// Carnaval
			if (_root.Carnaval) {
				Couleur = (Math.round(Math.random() * 100) % 255) | ((Math.round(Math.random() * 100) % 255) << 8) | ((Math.round(Math.random() * 100) % 255) << 16);
			} else if (_root.Halloween) {
				Couleur = 0x973D00;
			} else {
				Couleur = 0x29030B;
			}
		} else { // Indestructible, Objectif, Non reconstructible
			if (TYPE == 2) { // Indestructible
				if (CouleursPersonnalisées) {
					Couleur = CouleursPerso[1];
				} else {
					Couleur = CouleursBase[1];
				}
			} else if (TYPE == 3 || TYPE == 4) { // Objectif/Objectif 2
				ListeCase[X][Y] = TYPE;
				
				if (CouleursPersonnalisées) {
					Couleur = CouleursPerso[2];
				} else {
					Couleur = CouleursBase[2];
				}
			} else if (TYPE == 8) { // Non reconstructible
				if (CouleursPersonnalisées) {
					Couleur = CouleursPerso[3];
				} else {
					Couleur = CouleursBase[3];
				}
			} else if (TYPE == 999) { // Joueur surveillé par /vision
				Couleur = 0xED67EA;
			} else if (TYPE == 0) {
				ListeCase[X][Y] = 0;
				
				if (CouleursPersonnalisées) {
					Couleur = CouleursPerso[4];
				} else {
					Couleur = CouleursBase[4];
				}
			}
		}
	} else if (TYPE == 9) {
		ListeCase[X][Y] = 9;
		
		if (CouleursPersonnalisées) {
			Couleur = CouleursPerso[5];
		} else {
			Couleur = CouleursBase[5];
		}
	} else {
		return;
	}
	
	DataCase.lock();
	var Bx:int = X*10;
	var Fx:int = Bx+10;
	var By:int = Y*10;
	var Fy:int = By+10;
	for (var Cx:int = Bx; Cx<Fx; Cx++) {
		for (var Cy:int = By; Cy<Fy; Cy++) {
			DataCase.setPixel(Cx, Cy, Couleur);
		}
	}
	DataCase.unlock();
}

function Destruction_Case_Graphique(X:int, Y:int):void {
	var Px:int = X*10;
	var Py:int = Y*10;
	DataCase.lock();
	var Bx:int = Px;
	var Fx:int = Bx+10;
	var By:int = Py;
	var Fy:int = By+10;

    var Couleur:int = 0x9E9EAF;
	if (CouleursPersonnalisées) {
		if (ListeCase[X][Y] == 0) { // Si la case a été remplacée par du vide constructible
			Couleur = CouleursPerso[4];
		} else if (ListeCase[X][Y] == 9) { // Vide inconstructible
			Couleur = CouleursPerso[5];
		}
	}
	
	if (ListeCaseCadeaux[X][Y] != null) {
		ClipCarte.removeChild(ListeCaseCadeaux[X][Y]);
		ListeCaseCadeaux[X][Y] = null;
	}
	
	for (var Cx:int = Bx; Cx<Fx; Cx++) {
		for (var Cy:int = By; Cy<Fy; Cy++) {
			DataCase.setPixel(Cx, Cy, Couleur); // TODO : tracer directement un rectangle ?
		}
	}
	DataCase.unlock();	
}

function Destruction_Case(X:int, Y:int, SERVEUR : Boolean = false):void {
	if (ListeCase[X][Y] == 1) {
		ListeCase[X][Y] = 0;
		Destruction_Case_Graphique(X,Y);
	} else	if (ListeCase[X][Y] == 8) {
		ListeCase[X][Y] = 9;
		Destruction_Case_Graphique(X,Y);
	} else if (SERVEUR && ListeCase[X][Y] == 3) {
		Destruction_Case_Objectif(X,Y);
	}
}

function Destruction_Case_Objectif(X:int, Y:int):void {
	ListeCase[X][Y] = 0;
	Destruction_Case_Graphique(X,Y);
}

function Chargement_Liste_Case(LISTE:Array):void {
	
	var ConstruirePourVideConstr:Boolean = (CouleursPersonnalisées && CouleursPerso[4] != CouleursBase[4]);
	var ConstruirePourVideInconstr:Boolean = (CouleursPersonnalisées && CouleursPerso[5] != CouleursBase[5]);
	
	for (var i:int = 0; i<20000; i++) {
		var X:int = int(i/100);
		var Y:int = i%100;
		if (LISTE[i] == "1") {
			Construction(X, Y, 0, parseInt(LISTE[i], 10));
			ListeCase[X][Y] = LISTE[i];
		} else if (LISTE[i] == "3" || LISTE[i] == "2" || LISTE[i] == "4" || LISTE[i] == "8") {
			Construction(X, Y, 2, parseInt(LISTE[i], 10)); // objectif
			ListeCase[X][Y] = LISTE[i];
		} else if (LISTE[i] == "9") {
			ListeCase[X][Y] = 9;
			if (ConstruirePourVideInconstr) {
				Construction(X, Y, 2, 9); // Vide inconstructible
			}
		} else if (ConstruirePourVideConstr && LISTE[i] == "0") {
			ListeCase[X][Y] = 0;
			Construction(X, Y, 2, 0); // Vide constructible
		}
	}
	
	TracerZonesMortes(); // Restauration état zones mortes
}


function Nouveau_Jambon(CODE_JAMBON:String, JOUEUR:MovieClip, X:int, Y:int, CIBLE:MovieClip):void {
	var Jambombe:MovieClip = new $Jambombe();
	Jambombe.Code = CODE_JAMBON;
	Jambombe.Auteur = JOUEUR.NomJoueur;
	Jambombe.EquipeBleu = JOUEUR.EquipeBleu;
	ListeJambon.push(Jambombe);
	if (CIBLE == null) {
		ClipListeEffet.addChild(Jambombe);
		Jambombe.x = X;
		Jambombe.y = Y;
		Jambombe.Attache = false;
	} else {
		CIBLE._ListeEffet.addChild(Jambombe);
		Jambombe.x = X;
		Jambombe.y = Y;
		Jambombe.Attache = true;
	}
}

function Explosion_Jambombe(CODE_JAMBON:String, BX:int, BY:int):void {	
	var NbJ:int = ListeJambon.length;
	var Trouve : Boolean = false;
	for (var i:int = 0; i<NbJ; i++) {
		var Jambon:MovieClip = ListeJambon[i];

		
		if (Jambon.Code == CODE_JAMBON) {
			if (Jambon.parent != null) {
				Jambon.parent.removeChild(Jambon);
			}
			Trouve = true;
			ListeJambon.splice(i, 1);
			break;
		}
	}
	
	//
	var Explosion:MovieClip = new $Explosion();
	ClipListeEffet.addChild(Explosion);
	var EpcX:int = (BX-5)*10;
	var EpcY:int = (BY-5)*10;
	Explosion.x = EpcX-40;
	Explosion.y = EpcY-70;
	//
	
		
	for (var X:int = BX-9; X<BX; X++) {
		for (var Y:int = BY-9; Y<BY; Y++) {
			if (X >= 0 && X < 200 && Y >= 0 && Y < 100) {
				Destruction_Case(X, Y);
			}
		}
	}
	
	if (!Trouve) {
		return;
	}
	
	// Projection
	var Sante:int = int(ClipJoueur.Chargeur.Santé.text);
	if (Vivant && Sante > 0) {
		
		var C1:int = EpcX-ClipJoueur.x-17;
		var C2:int = EpcY-ClipJoueur.y-ClipJoueur.CentreY;
		var Distance:int = Math.sqrt(C1*C1+C2*C2);
		var Puissance:int = 7-int(Distance/10);
		if (Puissance <= 3) {
			Puissance = 3;
		}
		
		var PuissanceX:int;
		if (Math.abs(C1) < 5 && !Jambon.Attache) {
			PuissanceX = 0;
		} else {
			PuissanceX = Puissance;
		}
		
		if (Distance < 100) {
			if (C1 < 0) {
				ClipJoueur.VitesseX += PuissanceX;
			} else {
				ClipJoueur.VitesseX -= PuissanceX;
			}
			if (C2 < 0) {
				ClipJoueur.VitesseY += Puissance;
			} else {
				ClipJoueur.VitesseY -= Puissance+4;
			}
			Incontrolable = true;
			Controlable = 0;
			
			if (!EstUneCible) {
				var Aie:int = int(Puissance/2);
				var Bobo:int = Sante-Aie;
				if (Bobo < 0) {
					Bobo = 0;
				}
				ClipJoueur.Chargeur.Santé.text = ""+Bobo;
				_root.Envoie_Serveur(Codage(1)+$+ClipJoueur.x+$+ClipJoueur.y+$+int(ClipJoueur.VitesseX*100)+$+int(ClipJoueur.VitesseY*100)+$+Jambon.Auteur+$+Aie);
			}
		}
	}
}

function Mort_Joueur(Joueur:MovieClip, TUEUR:MovieClip, JAMBON:Boolean = false, COEURSTVAL:Boolean = false):void {
	if (Joueur.Vivant) {
		while (Joueur._ListeEffet.numChildren != 0) {
			Joueur._ListeEffet.removeChildAt(0);
		}
		//
		Joueur.Vivant = false;
		var Splatch:MovieClip;
		Splatch = new $Splatch_G();
		Splatch.x = Joueur.x-20;
		Splatch.y = Joueur.y-42;
		ClipListeEffet.addChild(Splatch);
		Joueur.VitesseX = 0;
		Joueur.VitesseY = 0;
		Joueur.DeplacementX = 0;
		//
		if (Joueur.ClipJoueur) {
			Reset_Joueur();
			Vivant = false;
			if (!(_root.Espionnage) && TUEUR != null) CibleCamera = TUEUR;
			DateMort = getTimer();
			ClipRespawn.visible = true;
		}
		//
		if (CibleCamera == Joueur) {
			if (!(_root.Espionnage) && TUEUR != null) CibleCamera = TUEUR;
		}
		//
		Joueur.visible = false;
		//
		if (TUEUR != null){
		var Frag:MovieClip = new $FragForteresse();
		if (TUEUR.EquipeBleu) {
			Frag.Effet.Texte1.textColor = 0x6C77C1;
		} else {
			Frag.Effet.Texte1.textColor = 0xCB546B;
		}
		if (Joueur.EquipeBleu) {
			Frag.Effet.Texte2.textColor = 0x6C77C1;
		} else {
			Frag.Effet.Texte2.textColor = 0xCB546B;
		}
		Frag.Effet.Texte1.text = TUEUR.NomJoueur;
		Frag.Effet.Texte2.text = Joueur.NomJoueur;
		Frag.Effet.Bombe.visible = false;
		Frag.Effet.CoeurStVal.visible = false;
		if (COEURSTVAL) {
			Frag.Effet.Lait.visible = false;
			Frag.Effet.Jambon.visible = false;
			
			Frag.Effet.CoeurStVal.visible = true;
		} else if (JAMBON) {
			Frag.Effet.Lait.visible = false;
		} else {
			Frag.Effet.Jambon.visible = false;
		}
		Frag.y = 20*ClipListeFrag.numChildren;
		ClipListeFrag.addChild(Frag);
		}
	}	
}

function Changement_Arme(NUM:int):void {
	ArmeEnCours = NUM;
	//
	Tab = new Array();
	_Monde._Action.Arme0.filters = Tab;
	_Monde._Action.Arme1.filters = Tab;
	//
	if (NUM == 0) {
		ClipJoueur.Chargeur.Balle.text = ""+((Chargeur - ChargeurBase) / ChargeurCoef);
	} else {
		if (NUM == 1) {
			ClipJoueur.Chargeur.Balle.text = ""+Jambombe;
		}
	}
	//
	var Halo:GlowFilter = new GlowFilter(0x6C77C1, 1, 4, 4, 2, 3);
	var Tab:Array = new Array();
	Tab.push(Halo);
	_Monde._Action["Arme"+NUM].filters = Tab;
}

function Chargement_Monde(NUM:int, InfosMondePerso:Array = null):void {
	var X:int;
	var Y:int;
	
	while (ClipCarte.numChildren != 0) {
		ClipCarte.removeChildAt(0);
	}
	while (ClipFondMonde.numChildren != 0) {
		ClipFondMonde.removeChildAt(0);
	}
	while (ClipListeCase.numChildren != 0) {
		ClipListeCase.removeChildAt(0);
	}
	while (ClipListeCaseMorte.numChildren != 0) {
		ClipListeCaseMorte.removeChildAt(0);
	}
	while (ClipListeEffet.numChildren != 0) {
		ClipListeEffet.removeChildAt(0);
	}
	//
	//ListeCaseMorte = new Array();
	ListeJambon = new Array();
	//
	
	/// ----
	if (DataCase != null) {
		DataCase.dispose();
	}
	///	
	DataCase = new BitmapData(2000, 1000, false, 0x9E9EAF);
	var Image:Bitmap = new Bitmap(DataCase);
	ClipListeCase.addChild(Image);
	//
	ListeCase = new Array();
	for (X = 0; X<200; X++) {
		var ListeY:Array = new Array();
		for (Y = 0; Y<100; Y++) {
			ListeY[Y] = 0;
		}
		ListeCase[X] = ListeY;
	}
	
	ListeCaseCadeaux = new Array();
	for (X = 0; X<200; X++) {
		var ListeY:Array = new Array();
		for (Y = 0; Y<100; Y++) {
			ListeY[Y] = null;
		}
		ListeCaseCadeaux[X] = ListeY;
	}
	//
	MondeEnCours = Gen_Monde(NUM, InfosMondePerso);

	//
	TracerZonesMortes();
	//
	MondeEnCours.S1_x = 500;
	MondeEnCours.S1_y = 500;
	MondeEnCours.S2_x = 500;
	MondeEnCours.S2_y = 500;
	//
	//MondeEnCours.cacheAsBitmap = true;
	if (MondeEnCours._Décors != null) {
		ClipFondMonde.addChild(MondeEnCours._Décors);
	}
	//
	
	/// ----
	if (MondePixel != null) {
		MondePixel.dispose();
	}
	///
	
	MondePixel = new BitmapData(2000, 1000, true, 0x00FFFFFF);
	MondePixel.draw(MondeEnCours);
	var ImageMonde:Bitmap = new Bitmap(MondePixel);
	//
	ClipCarte.addChild(ImageMonde);
	//ClipCarte.addChild(MondeEnCours);
	//
	AjouteBonnets(ClipCarte, MondeEnCours);
	//
	NePasEnvoyerPosition = false;
	Demarrage_physique();
}

function TracerZonesMortes():void {
	DataCase.lock();
	var NbZoneMorte:int = MondeEnCours.ListeZoneMorte.length;
	for (var ZM:int = 0; ZM<NbZoneMorte; ZM += 4) {
		var X1:int = MondeEnCours.ListeZoneMorte[ZM];
		var X2:int = MondeEnCours.ListeZoneMorte[ZM+1];
		var Y1:int = MondeEnCours.ListeZoneMorte[ZM+2];
		var Y2:int = MondeEnCours.ListeZoneMorte[ZM+3];
		var FRX:int = X2*10;
		var FRY:int = Y2*10;
		for (var BRx:int = X1*10; BRx<FRX; BRx++) {
			for (var BRy:int = Y1*10; BRy<FRY; BRy++) {
				if (ListeCase[int(BRx/10)][int(BRy/10)] == 0) {
					ListeCase[int(BRx/10)][int(BRy/10)] = 9;
				}
				
				if (CouleursPersonnalisées && CouleursPerso[5] != CouleursBase[5]) {
					DataCase.setPixel(BRx, BRy, CouleursPerso[5]);
				} else {
					DataCase.setPixel(BRx, BRy, 0x828F9F);
				}
			}
		}
	}
	DataCase.unlock();
}

function AjouteBonnets(Cible:MovieClip, Clip:MovieClip):void {
	if (_root.Noel && Clip.MondePerso == null) {
		Clip.Bonnet1.visible = true;
		Cible.addChild(Clip.Bonnet1);
		if (Clip.Frigo2 != null) {
			Clip.Bonnet2.visible = true;
			Cible.addChild(Clip.Bonnet2);
		}
	}
}

function FiltreDessins(Clip:MovieClip):MovieClip {
	if (Clip.MondePerso == null) {
		Clip.Frigo1.araignee.visible = _root.Halloween;
		Clip.Bonnet1.visible = false; // Sont activés par la suite avec AjouteBonnets()
		if (Clip.Frigo2 != null) {
			Clip.Frigo2.araignee.visible = _root.Halloween;
			Clip.Bonnet2.visible = false;
		}
	} else if (Clip.Frigo1 != null) {
		Clip.Frigo1.araignee.visible = _root.Halloween;
		if (Clip.Frigo2 != null) {
			Clip.Frigo2.araignee.visible = _root.Halloween;
		}
	}
	
	return Clip;
}

function Gen_Monde(NUM:int, InfosMondePerso:Array = null):MovieClip {
	return FiltreDessins(Crea_Monde(NUM, InfosMondePerso));
}

/*
 <NUM> : numéro du monde (valeur sans importance si monde perso)
 <InfosMondePerso> : se base sur les infos
*/

function Crea_Monde(NUM:int, InfosMondePerso:Array = null):MovieClip {
	var Monde:MovieClip;
	
	if (InfosMondePerso != null) {
		/**Si monde perso :
		   
		   [1.0 - 1] 'P'
		   [1.1 - 1] Nombre de frigo
		   [1.2 -> ...] (Frigo1 :) X <$> Y <$> (Frigo2 :) X <$> Y <$>... (peut y en avoir 0 à autant qu'on veut - 2 max gérés pour le moment)
		   [1.3 - 1] Gravité
		   [1.4 - 4] Zone repsawn 1 : X1<$>Y1<$>X2<$>Y2
		   [1.5 - 4] Zone respawn 2 : X1<$>Y1<$>X2<$>Y2
		   [1.6 - 2] Position respawn rouge : X<$>Y
		   [1.7 - 2] Position respawn bleu : X<$>Y
		   [1.8 - 1] Bords (0 à 9) : <BordPlafond><BordSol>*/
		 
		Monde = new MovieClip();
		Monde.width = 2000;
		Monde.height = 1000;
		
		Monde.MondePerso = true;
		
		Monde.Num = NUM;
		Monde.ListeZoneMorte = new Array();
		
		Monde.Frigo1 = null; // Pour les bonnets de Noël, qu'on sache qu'il n'y ait pas de frigo

		var i:int = 0;
		var Frigo:$Frigo;
		
		MondePersoToString = "";
		
		switch(int(InfosMondePerso[0])) { // Nombre de frigos
			case 0:
				MondePersoToString = "0";
				break;
			case 1:
				Frigo = new $Frigo();
				Frigo.x = InfosMondePerso[1];
				Frigo.y = InfosMondePerso[2];
			
				Monde.addChild(Frigo);
				Monde.Frigo1 = Frigo;
				
				MondePersoToString = "1-" + InfosMondePerso[1] + "-" + InfosMondePerso[2];
				
				i = 2;
				break;
			case 2:
				Frigo = new $Frigo();
				Frigo.x = InfosMondePerso[1];
				Frigo.y = InfosMondePerso[2];
			
				Monde.addChild(Frigo);
				Monde.Frigo1 = Frigo;
				
				Frigo = new $Frigo();
				
				Frigo.x = InfosMondePerso[3];
				Frigo.y = InfosMondePerso[4];
			
				Monde.addChild(Frigo);
				Monde.Frigo2 = Frigo;
				
				MondePersoToString = "2-" + InfosMondePerso[1] + "-" + InfosMondePerso[2]
								   + "-" + InfosMondePerso[3] + "-" + InfosMondePerso[4];
				
				i = 4;
				break;
		}
		
		Limite = InfosMondePerso[++i];
		
		// Zone 1
		Monde.ListeZoneMorte[0] = InfosMondePerso[++i]; // x1
		Monde.ListeZoneMorte[2] = InfosMondePerso[++i]; // y1
		
		Monde.ListeZoneMorte[1] = InfosMondePerso[++i]; // x2
		Monde.ListeZoneMorte[3] = InfosMondePerso[++i]; // y2
		
		// Zone 2
		Monde.ListeZoneMorte[4] = InfosMondePerso[++i];
		Monde.ListeZoneMorte[6] = InfosMondePerso[++i];
		
		Monde.ListeZoneMorte[5] = InfosMondePerso[++i];
		Monde.ListeZoneMorte[7] = InfosMondePerso[++i];
		
		// Position resp
		SpawnDéfenseX = InfosMondePerso[++i];
		SpawnDéfenseY = InfosMondePerso[++i];
		SpawnAttaqueX = InfosMondePerso[++i];
		SpawnAttaqueY = InfosMondePerso[++i];
		
		MondePersoToString += "-" + Limite + "-" + Monde.ListeZoneMorte[0] + "-" + Monde.ListeZoneMorte[2]
							+ "-" + Monde.ListeZoneMorte[1] + "-" + Monde.ListeZoneMorte[3]
							+ "-" + Monde.ListeZoneMorte[4] + "-" + Monde.ListeZoneMorte[6]
							+ "-" + Monde.ListeZoneMorte[5] + "-" + Monde.ListeZoneMorte[7]
							+ "-" + SpawnDéfenseX + "-" + SpawnDéfenseY + "-" + SpawnAttaqueX
							+ "-" + SpawnAttaqueY;
		
		// Plafond
		var Bord:Shape = new Shape();
		
		Bord.graphics.beginFill(0x010101);
		Bord.graphics.drawRect(0, 0, 2000, int(InfosMondePerso[++i]) / 10);
		
		Monde.addChild(Bord);
		
		// Sol
		Bord = new Shape();
		
		Bord.graphics.beginFill(0x010101);
		Bord.graphics.drawRect(0, 1000 - (int(InfosMondePerso[i]) % 10), 2000, 1000);
		
		Monde.addChild(Bord);
		
		MondePersoToString += "-" + InfosMondePerso[i];
		
		return Monde;
	}
	
	if (NUM == 0) { 
		Monde = new $MondeF0();
		Monde.Num = NUM;		
		Monde.ListeZoneMorte = new Array();
		Limite = 700;
		SpawnDéfenseX = 1727;
		SpawnDéfenseY = 448;
		SpawnAttaqueX = 81;
		SpawnAttaqueY = 403;
		Monde.ListeZoneMorte[0] = 164;
		Monde.ListeZoneMorte[1] = 185;
		Monde.ListeZoneMorte[2] = 45;
		Monde.ListeZoneMorte[3] = 70;
		Monde.ListeZoneMorte[4] = 0;
		Monde.ListeZoneMorte[5] = 23;
		Monde.ListeZoneMorte[6] = 32;
		Monde.ListeZoneMorte[7] = 49;
		
		return Monde;
	}
	if (NUM == 1) {
		Monde = new $MondeF1();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		
		Limite = 464;
		
		SpawnDéfenseX = 1812;
		SpawnDéfenseY = 334;
		SpawnAttaqueX = 100;
		SpawnAttaqueY = 160;
		
		Monde.ListeZoneMorte[0] = 0;
		Monde.ListeZoneMorte[1] = 31;
		Monde.ListeZoneMorte[2] = 0;
		Monde.ListeZoneMorte[3] = 100;

		Monde.ListeZoneMorte[4] = 167;
		Monde.ListeZoneMorte[5] = 196;
		Monde.ListeZoneMorte[6] = 32;
		Monde.ListeZoneMorte[7] = 45;
		/*Limite = 370;
		SpawnDéfenseX = 1930;
		SpawnDéfenseY = 385;
		SpawnAttaqueX = 110;
		SpawnAttaqueY = 700;
		Monde.ListeZoneMorte[0] = 176; // X1 début
		Monde.ListeZoneMorte[1] = 200; // X1 fin
		Monde.ListeZoneMorte[2] = 34;  // Y1 début
		Monde.ListeZoneMorte[3] = 42;  // Y1 fin
		Monde.ListeZoneMorte[4] = 0;   // X2 début
		Monde.ListeZoneMorte[5] = 40;  // X2 fin
		Monde.ListeZoneMorte[6] = 53;  // Y2 début
		Monde.ListeZoneMorte[7] = 87;  // Y2 fin */
		
		return Monde;
	}
	if (NUM == 2) {
		Monde = new $MondeF2();
		Monde.ListeZoneMorte = new Array();
		Monde.Num = NUM;				
		Limite = 370;
	
		SpawnDéfenseX = 1820;
		SpawnDéfenseY = 390;
		SpawnAttaqueX = 90;
		SpawnAttaqueY = 250;
		
		Monde.ListeZoneMorte[0] = 147;
		Monde.ListeZoneMorte[1] = 190;
		Monde.ListeZoneMorte[2] = 23;
		Monde.ListeZoneMorte[3] = 45;
	
		Monde.ListeZoneMorte[4] = 8;
		Monde.ListeZoneMorte[5] = 23;
		Monde.ListeZoneMorte[6] = 15;
		Monde.ListeZoneMorte[7] = 35;
		/*Limite = 370;
		SpawnDéfenseX = 1820;
		SpawnDéfenseY = 390;
		SpawnAttaqueX = 90;
		SpawnAttaqueY = 250;
		Monde.ListeZoneMorte[0] = 147;
		Monde.ListeZoneMorte[1] = 190;
		Monde.ListeZoneMorte[2] = 23;
		Monde.ListeZoneMorte[3] = 43;
		Monde.ListeZoneMorte[4] = 8;
		Monde.ListeZoneMorte[5] = 23;
		Monde.ListeZoneMorte[6] = 15;
		Monde.ListeZoneMorte[7] = 35; */
		
		return Monde;
	}
	if (NUM == 3) {
		Monde = new $MondeF3();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 510;
		SpawnDéfenseX = 1880;
		SpawnDéfenseY = 525;
		SpawnAttaqueX = 215;
		SpawnAttaqueY = 500;
		Monde.ListeZoneMorte[0] = 7;
		Monde.ListeZoneMorte[1] = 27;
		Monde.ListeZoneMorte[2] = 44;
		Monde.ListeZoneMorte[3] = 55;
		
		Monde.ListeZoneMorte[4] = 159;
		Monde.ListeZoneMorte[5] = 194;
		Monde.ListeZoneMorte[6] = 51;
		Monde.ListeZoneMorte[7] = 61;
		/*Limite = 510;
		SpawnDéfenseX = 1880;
		SpawnDéfenseY = 525;
		SpawnAttaqueX = 230;
		SpawnAttaqueY = 170;
		Monde.ListeZoneMorte[0] = 4;
		Monde.ListeZoneMorte[1] = 33;
		Monde.ListeZoneMorte[2] = 3;
		Monde.ListeZoneMorte[3] = 21;
		Monde.ListeZoneMorte[4] = 158;
		Monde.ListeZoneMorte[5] = 194;
		Monde.ListeZoneMorte[6] = 53;
		Monde.ListeZoneMorte[7] = 60; */
		
		return Monde;
	}
	if (NUM == 4) {
		Monde = new $MondeF4();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 437;
		SpawnDéfenseX = 1656;
		SpawnDéfenseY = 370;
		SpawnAttaqueX = 320;
		SpawnAttaqueY = 370;
		Monde.ListeZoneMorte[0] = 10;
		Monde.ListeZoneMorte[1] = 36;
		Monde.ListeZoneMorte[2] = 38;
		Monde.ListeZoneMorte[3] = 51;
		Monde.ListeZoneMorte[4] = 164;
		Monde.ListeZoneMorte[5] = 189;
		Monde.ListeZoneMorte[6] = 38;
		Monde.ListeZoneMorte[7] = 51;
		
		return Monde;
	}
	if (NUM == 5) {
		Monde = new $MondeF5();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 325;
		SpawnDéfenseX = 1860;
		SpawnDéfenseY = 320;
		SpawnAttaqueX = 120;
		SpawnAttaqueY = 320;
		Monde.ListeZoneMorte[0] = 6;
		Monde.ListeZoneMorte[1] = 19;
		Monde.ListeZoneMorte[2] = 33;
		Monde.ListeZoneMorte[3] = 42;
		Monde.ListeZoneMorte[4] = 181;
		Monde.ListeZoneMorte[5] = 194;
		Monde.ListeZoneMorte[6] = 33;
		Monde.ListeZoneMorte[7] = 42;
		
		return Monde;
	}
	if (NUM == 6) {
		Monde = new $MondeF6();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 630;
		SpawnDéfenseX = 1330;
		SpawnDéfenseY = 847;
		SpawnAttaqueX = 75;
		SpawnAttaqueY = 854;
		Monde.ListeZoneMorte[0] = 0;
		Monde.ListeZoneMorte[1] = 29;
		Monde.ListeZoneMorte[2] = 65;
		Monde.ListeZoneMorte[3] = 90;
		Monde.ListeZoneMorte[4] = 129;
		Monde.ListeZoneMorte[5] = 178;
		Monde.ListeZoneMorte[6] = 48;
		Monde.ListeZoneMorte[7] = 90;

		return Monde;
	}
	if (NUM == 7) {
		Monde = new $MondeF7();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 550;
		SpawnDéfenseX = 1610;
		SpawnDéfenseY = 510;
		SpawnAttaqueX = 365;
		SpawnAttaqueY = 510;
		Monde.ListeZoneMorte[0] = 31;
		Monde.ListeZoneMorte[1] = 44;
		Monde.ListeZoneMorte[2] = 52;
		Monde.ListeZoneMorte[3] = 63;
		Monde.ListeZoneMorte[4] = 156;
		Monde.ListeZoneMorte[5] = 169;
		Monde.ListeZoneMorte[6] = 52;
		Monde.ListeZoneMorte[7] = 63;
		
		return Monde;
	}
	if (NUM == 8) {
		Monde = new $MondeF8();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 560;
		SpawnDéfenseX = 1235;
		SpawnDéfenseY = 542;
		SpawnAttaqueX = 743;
		SpawnAttaqueY = 700;

		Monde.ListeZoneMorte[0] = 66;
		Monde.ListeZoneMorte[1] = 99;
		Monde.ListeZoneMorte[2] = 57;
		Monde.ListeZoneMorte[3] = 74;
		
		Monde.ListeZoneMorte[4] = 114;
		Monde.ListeZoneMorte[5] = 149;
		Monde.ListeZoneMorte[6] = 48;
		Monde.ListeZoneMorte[7] = 76;
		/*Limite = 560;
		SpawnDéfenseX = 1217;
		SpawnDéfenseY = 710;
		SpawnAttaqueX = 743;
		SpawnAttaqueY = 700;
		Monde.ListeZoneMorte[0] = 64;
		Monde.ListeZoneMorte[1] = 95;
		Monde.ListeZoneMorte[2] = 63;
		Monde.ListeZoneMorte[3] = 75;
		Monde.ListeZoneMorte[4] = 108;
		Monde.ListeZoneMorte[5] = 144;
		Monde.ListeZoneMorte[6] = 48;
		Monde.ListeZoneMorte[7] = 75;*/
		
		return Monde;
	}	
	if (NUM == 9) {
		Monde = new $MondeF9();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 500;
		SpawnDéfenseX = 1717;
		SpawnDéfenseY = 525;
		SpawnAttaqueX = 150;
		SpawnAttaqueY = 162;
		Monde.ListeZoneMorte[0] = 3;
		Monde.ListeZoneMorte[1] = 30;
		Monde.ListeZoneMorte[2] = 9;
		Monde.ListeZoneMorte[3] = 23;
		
		Monde.ListeZoneMorte[4] = 154;
		Monde.ListeZoneMorte[5] = 175;
		Monde.ListeZoneMorte[6] = 47;
		Monde.ListeZoneMorte[7] = 64;

		return Monde;
	}		
	if (NUM == 10) {
		Monde = new $MondeF10();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 548;
		SpawnDéfenseX = 1950;
		SpawnDéfenseY = 520;
		SpawnAttaqueX = 30;
		SpawnAttaqueY = 520;
		Monde.ListeZoneMorte[0] = 1;
		Monde.ListeZoneMorte[1] = 50;
		Monde.ListeZoneMorte[2] = 51;
		Monde.ListeZoneMorte[3] = 61;
		Monde.ListeZoneMorte[4] = 151;
		Monde.ListeZoneMorte[5] = 200;
		Monde.ListeZoneMorte[6] = 51;
		Monde.ListeZoneMorte[7] = 61;
		
		return Monde;
	}		
	if (NUM == 11) {
		Monde = new $MondeF11();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 475;
		SpawnDéfenseX = 1692;
		SpawnDéfenseY = 452;
		SpawnAttaqueX = 272;
		SpawnAttaqueY = 452;
		Monde.ListeZoneMorte[0] = 159;
		Monde.ListeZoneMorte[1] = 180;
		Monde.ListeZoneMorte[2] = 43;
		Monde.ListeZoneMorte[3] = 54;
		Monde.ListeZoneMorte[4] = 20;
		Monde.ListeZoneMorte[5] = 41;
		Monde.ListeZoneMorte[6] = 43;
		Monde.ListeZoneMorte[7] = 54;
		
		return Monde;
	}
	if (NUM == 12) {
		Monde = new $MondeF12();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 535;
		SpawnDéfenseX = 292;
		SpawnDéfenseY = 496;
		SpawnAttaqueX = 1878;
		SpawnAttaqueY = 626;
		Monde.ListeZoneMorte[0] = 174;
		Monde.ListeZoneMorte[1] = 198;
		Monde.ListeZoneMorte[2] = 60;
		Monde.ListeZoneMorte[3] = 71;
		Monde.ListeZoneMorte[4] = 22;
		Monde.ListeZoneMorte[5] = 39;
		Monde.ListeZoneMorte[6] = 43;
		Monde.ListeZoneMorte[7] = 59;
		
		return Monde;
	}	
	if (NUM == 13) {
		Monde = new $MondeF13();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 558;
		
		SpawnDéfenseX = 1721;
		SpawnDéfenseY = 586;
		SpawnAttaqueX = 34;
		SpawnAttaqueY = 838;
		Monde.ListeZoneMorte[0] = 146;
		Monde.ListeZoneMorte[1] = 176;
		Monde.ListeZoneMorte[2] = 38;
		Monde.ListeZoneMorte[3] = 62;
		Monde.ListeZoneMorte[4] = 0;
		Monde.ListeZoneMorte[5] = 33;
		Monde.ListeZoneMorte[6] = 81;
		Monde.ListeZoneMorte[7] = 99;
		/*Limite = 544;
		SpawnDéfenseX = 1679;
		SpawnDéfenseY = 558;
		SpawnAttaqueX = 34;
		SpawnAttaqueY = 838;
		Monde.ListeZoneMorte[0] = 146;
		Monde.ListeZoneMorte[1] = 176;
		Monde.ListeZoneMorte[2] = 39;
		Monde.ListeZoneMorte[3] = 61;
		Monde.ListeZoneMorte[4] = 0;
		Monde.ListeZoneMorte[5] = 33;
		Monde.ListeZoneMorte[6] = 81;
		Monde.ListeZoneMorte[7] = 99;*/
		
		return Monde;
	}	
	if (NUM == 14) {
		Monde = new $MondeF14();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 940;
		SpawnDéfenseX = 1868;
		SpawnDéfenseY = 594;
		SpawnAttaqueX = 123;
		SpawnAttaqueY = 597;
		Monde.ListeZoneMorte[0] = 9;
		Monde.ListeZoneMorte[1] = 31;
		Monde.ListeZoneMorte[2] = 51;
		Monde.ListeZoneMorte[3] = 64;
		Monde.ListeZoneMorte[4] = 171;
		Monde.ListeZoneMorte[5] = 193;
		Monde.ListeZoneMorte[6] = 52;
		Monde.ListeZoneMorte[7] = 65;
		
		return Monde;
	}
	if (NUM == 15) {
		Monde = new $MondeF15();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 475;
		SpawnDéfenseX = 1843;
		SpawnDéfenseY = 391;
		SpawnAttaqueX = 90;
		SpawnAttaqueY = 334;
		Monde.ListeZoneMorte[0] = 0; // x début
		Monde.ListeZoneMorte[1] = 24;  // x fin
		Monde.ListeZoneMorte[2] = 25; // y début
		Monde.ListeZoneMorte[3] = 46;  // y fin
		Monde.ListeZoneMorte[4] = 177; // autre équipe
		Monde.ListeZoneMorte[5] = 197;
		Monde.ListeZoneMorte[6] = 27;
		Monde.ListeZoneMorte[7] = 50;
		
		return Monde;
	}			
	if (NUM == 16) {
		Monde = new $MondeF16();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 548;
		SpawnDéfenseX = 1678;
		SpawnDéfenseY = 489;
		SpawnAttaqueX = 277;
		SpawnAttaqueY = 495;
		Monde.ListeZoneMorte[0] = 25; // x début
		Monde.ListeZoneMorte[1] = 43;  // x fin
		Monde.ListeZoneMorte[2] = 44; // y début
		Monde.ListeZoneMorte[3] = 65;  // y fin
		Monde.ListeZoneMorte[4] = 153; // autre équipe
		Monde.ListeZoneMorte[5] = 171;
		Monde.ListeZoneMorte[6] = 43;
		Monde.ListeZoneMorte[7] = 64;
		
		return Monde;
	}
	if (NUM == 17) {
		Monde = new $MondeF17();
		Monde.Num = NUM;				
		Monde.ListeZoneMorte = new Array();
		Limite = 502;
		SpawnDéfenseX = 1733;
		SpawnDéfenseY = 453;
		SpawnAttaqueX = 268;
		SpawnAttaqueY = 453;
		Monde.ListeZoneMorte[0] = 12; // x début
		Monde.ListeZoneMorte[1] = 38;  // x fin
		Monde.ListeZoneMorte[2] = 44; // y début
		Monde.ListeZoneMorte[3] = 60;  // y fin
		Monde.ListeZoneMorte[4] = 163; // autre équipe
		Monde.ListeZoneMorte[5] = 189;
		Monde.ListeZoneMorte[6] = 44;
		Monde.ListeZoneMorte[7] = 60;
		
		return Monde;
	}
	if (NUM == 18) {
		Monde = new $MondeF18();
		Monde.Num = NUM;
		Monde.ListeZoneMorte = new Array();
		Limite = 595;
		SpawnDéfenseX = 365;
		SpawnDéfenseY = 530;
		SpawnAttaqueX = 1725;
		SpawnAttaqueY = 200;
		Monde.ListeZoneMorte[0] = 29; // x début
		Monde.ListeZoneMorte[1] = 58;  // x fin
		Monde.ListeZoneMorte[2] = 43; // y début
		Monde.ListeZoneMorte[3] = 69;  // y fin
		Monde.ListeZoneMorte[4] = 163; // autre équipe
		Monde.ListeZoneMorte[5] = 200;
		Monde.ListeZoneMorte[6] = 12;
		Monde.ListeZoneMorte[7] = 41;
		
		return Monde;
	}
	if (NUM == 19) {
		Monde = new $MondeF19();
		Monde.Num = NUM;
		Monde.ListeZoneMorte = new Array();
		Limite = 495;
		SpawnDéfenseX = 1745;
		SpawnDéfenseY = 420;
		SpawnAttaqueX = 235;
		SpawnAttaqueY = 420;
		Monde.ListeZoneMorte[0] =17; // x début
		Monde.ListeZoneMorte[1] =43;  // x fin
		Monde.ListeZoneMorte[2] =41; // y début
		Monde.ListeZoneMorte[3] =55;  // y fin
		Monde.ListeZoneMorte[4] =159; // autre équipe
		Monde.ListeZoneMorte[5] =185;
		Monde.ListeZoneMorte[6] =41;
		Monde.ListeZoneMorte[7] =55;
		
		return Monde;
	}
	if (NUM == 20) { 
		Monde = new $MondeF20();
		Monde.Num = NUM;		
		Monde.ListeZoneMorte = new Array();
		Limite = 469;
		
		SpawnDéfenseX = 865;
		SpawnDéfenseY = 544;
		
		SpawnAttaqueX = 1960;
		SpawnAttaqueY = 461;
		
		Monde.ListeZoneMorte[0] = 61;
		Monde.ListeZoneMorte[1] = 90;
		Monde.ListeZoneMorte[2] = 52;
		Monde.ListeZoneMorte[3] = 66;
		
		Monde.ListeZoneMorte[4] = 179;
		Monde.ListeZoneMorte[5] = 200;
		Monde.ListeZoneMorte[6] = 42;
		Monde.ListeZoneMorte[7] = 55;
		
		return Monde;
	}
	/*if (NUM == 21) {
		Monde = new $MondeF21();
		Monde.Num = NUM;		
		Monde.ListeZoneMorte = new Array();
		Limite = 650;
		
		SpawnDéfenseX = 1545;
		SpawnDéfenseY = 376;
		
		SpawnAttaqueX = 434;
		SpawnAttaqueY = 376;
		
		Monde.ListeZoneMorte[0] = 147;
		Monde.ListeZoneMorte[1] = 185;
		Monde.ListeZoneMorte[2] = 27;
		Monde.ListeZoneMorte[3] = 47;
		
		Monde.ListeZoneMorte[4] = 15;
		Monde.ListeZoneMorte[5] = 55;
		Monde.ListeZoneMorte[6] = 27;
		Monde.ListeZoneMorte[7] = 47;
		
		return Monde;
	}*/
	
	// Halloween
	if (NUM == 21) {
		Monde = new $MondeFH();
		Monde.Num = NUM;
		
		Monde.ListeZoneMorte = new Array();
		Limite = 428;
			
		SpawnDéfenseX = 1561;
		SpawnDéfenseY = 437;
		
		SpawnAttaqueX = 478;
		SpawnAttaqueY = 422;
		
		Monde.ListeZoneMorte[0] = 20;
		Monde.ListeZoneMorte[1] = 58;
		Monde.ListeZoneMorte[2] = 40;
		Monde.ListeZoneMorte[3] = 50;
		
		Monde.ListeZoneMorte[4] = 145;
		Monde.ListeZoneMorte[5] = 166;
		Monde.ListeZoneMorte[6] = 42;
		Monde.ListeZoneMorte[7] = 64;

		
		return Monde;
	} //*/

	// Monde inconnu -> Vierge
	if (_root.MODO) {
		Nouveau_Message_Chat("Erreur : monde inconnu.");
	}
	Monde = new $MondeF7();
	Monde.Num = NUM;				
	Monde.ListeZoneMorte = new Array();
	Limite = 550;
	SpawnDéfenseX = 1610;
	SpawnDéfenseY = 510;
	SpawnAttaqueX = 365;
	SpawnAttaqueY = 510;
	Monde.ListeZoneMorte[0] = 31;
	Monde.ListeZoneMorte[1] = 44;
	Monde.ListeZoneMorte[2] = 52;
	Monde.ListeZoneMorte[3] = 63;
	Monde.ListeZoneMorte[4] = 156;
	Monde.ListeZoneMorte[5] = 169;
	Monde.ListeZoneMorte[6] = 52;
	Monde.ListeZoneMorte[7] = 63;
	
	return Monde;
}

_Monde._Aide.Lien.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Lien);
function Clique_Lien(E:Event):void {
	navigateToURL(new URLRequest("http://www.extinction.fr/minijeux/MiniJeux.exe"), "_blank");
	_Monde._Aide.visible = true;
}

_Monde._Action.Arme0.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Arme0);
function Clique_Arme0(E:Event):void {
	Changement_Arme(0);

}

_Monde._Action.Arme1.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Arme1);
function Clique_Arme1(E:Event):void {
	Changement_Arme(1);
}