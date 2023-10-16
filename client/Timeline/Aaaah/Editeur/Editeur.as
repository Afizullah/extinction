                                                                     
import flash.display.MovieClip;
import flash.display3D.IndexBuffer3D;
                                                                     
                                                                     
                                             
var _root:MovieClip = MovieClip(parent.parent);

var NomCarte:String = "";
var NomAuteur:String = "";
Note.mouseEnabled = false;

var EstAuteur : Boolean = false;

var PosX:int;
var PosY:int;

var Outil:int;
var OutilEnCours:MovieClip;
var FormeMax:int = 100;
var FormeSuperMax:int = 220;
var FormeEnCours:int;

// Test
var DernierTest:XMLNode;
var DernierTestGuidage:Boolean = false;

_root.Vignette(NewMessage,"");
NewMessage.addEventListener(MouseEvent.MOUSE_DOWN, Cacher);
NewMessage.buttonMode = true;
function Cacher(E:Event):void {	
	NewMessage.visible = false;
	NewMessage.VignetteTexte = "";
}

// Sélection
var SelectionEnCours:MovieClip;
OptionSelection.visible = false;
var DeplacementGroupeX:int;
var DeplacementGroupeY:int;

// Groupe
var ListeSelection:Array;
var GroupeEnCours:int;
OptionGroupe.visible = false;

// Ligne
var LigneEnCours:MovieClip;
OptionLigne.visible = false;

// Courbe
var CourbeEnCours:Boolean;
var CourbeFinX:int;
var CourbeFinY:int;

// Polygone
var PolygoneEnCours:MovieClip;
FinPolygone.visible = false;

// Btn Outils
O_Selection.useHandCursor = true;
O_Selection.buttonMode = true;
O_Ligne.useHandCursor = true;
O_Ligne.buttonMode = true;
O_Courbe.useHandCursor = true;
O_Courbe.buttonMode = true;
O_Rectangle.useHandCursor = true;
O_Rectangle.buttonMode = true;
O_Polygone.useHandCursor = true;
O_Polygone.buttonMode = true;
OptionLigne.Pleine.useHandCursor = true;
OptionLigne.Pleine.buttonMode = true;
O_Ellipse.useHandCursor = true;
O_Ellipse.buttonMode = true;
O_Grouper.useHandCursor = true;
O_Grouper.buttonMode = true;

FormeDispo.mouseEnabled = false;

Quitter.addEventListener(MouseEvent.MOUSE_DOWN, Nettoyage_Listener);

function Initialisation():void {	
	MovieClip(parent)._Monde.Quitter.visible = false;
	DernierTest = new XMLDocument("<C><G /><F /></C>").firstChild
	DernierTestGuidage = false;
	NomCarte = "";
	NomAuteur = "";
	//
	OptionGroupe.visible = false;
	OptionSelection.visible = false;
	OptionLigne.visible = false;
	// Outil
	FormeDispo.text = "0/"+FormeMax;
	FormeEnCours = 0;
	Outil = 0;
	OutilEnCours = O_Selection;
	//
	O_Selection.Encoche.E1.visible = true;
	O_Selection.Encoche.E0.visible = false;
	O_Ligne.Encoche.E1.visible = false;
	O_Ligne.Encoche.E0.visible = true;
	O_Courbe.Encoche.E1.visible = false;
	O_Courbe.Encoche.E0.visible = true;
	O_Rectangle.Encoche.E1.visible = false;
	O_Rectangle.Encoche.E0.visible = true;
	O_Polygone.Encoche.E1.visible = false;
	O_Polygone.Encoche.E0.visible = true;
	OptionLigne.Pleine.Encoche.E1.visible = false;
	OptionLigne.Pleine.Encoche.E0.visible = true;
	O_Ellipse.Encoche.E1.visible = false;
	O_Ellipse.Encoche.E0.visible = true;
	O_Grouper.Encoche.E1.visible = false;
	O_Grouper.Encoche.E0.visible = true;
	// Selection
	SelectionEnCours = null;
	// Groupe
	ListeSelection = new Array();
	GroupeEnCours = 0;
	// Ligne
	LigneEnCours = null;
	// Courbe
	CourbeEnCours = false;
	// Polygone
	PolygoneEnCours = null;
	FinPolygone.visible = O_Polygone.Encoche.E1.visible;
	//
	while (NouvelleCarte.numChildren != 0) {
		NouvelleCarte.removeChildAt(0);
	}
	//setup
	ZoneClique.addEventListener(MouseEvent.MOUSE_DOWN, Lancement_Trace);
	stage.addEventListener(MouseEvent.MOUSE_UP, Desactivation_Trace);
}

function Nettoyage_Listener(E:Event):void {
	//cleanup
	
	/*/// ---- Fuites mémoire
	NewMessage.removeEventListener(MouseEvent.MOUSE_DOWN, Cacher);
	
	OptionSelection.Suppr.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Suppr);
	O_Selection.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Selection);
	O_Ligne.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Ligne);
	O_Courbe.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Courbe);
	O_Rectangle.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Rectangle);
	O_Polygone.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Polygone);
	O_Ellipse.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Ellipse);
	O_Grouper.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grouper);
	OptionLigne.Pleine.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Pleine);
	OptionGroupe.Grouper.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Groupe);
	OptionGroupe.GrouperTout.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_GroupeTout);
	Test.removeEventListener(MouseEvent.CLICK, Clique_Test);
	MesCartes.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_MesMaps);
	EditeurExterne.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_EditeurExterne);
	Sauver.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Sauver);
	Charger.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Charger);
	Exporter.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Exporter);
	Code.removeEventListener(KeyboardEvent.KEY_DOWN, Clavier_Code);*/
	
	//Quitter.removeEventListener(MouseEvent.MOUSE_DOWN, Nettoyage_Listener);
	/// ----
	
	ZoneClique.removeEventListener(MouseEvent.MOUSE_DOWN, Lancement_Trace);
	stage.removeEventListener(MouseEvent.MOUSE_UP, Desactivation_Trace);
	if (PolygoneEnCours != null) {
		PolygoneEnCours.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Objet);
		PolygoneEnCours = null;
	}
	MovieClip(parent).Clique_Quitter(E);
}

// CLICK
ZoneClique.addEventListener(MouseEvent.CLICK, Clique_Trace);
function Clique_Trace(E:Event):void {
	if (Outil == 6) { // Polygone
		PosX = int(mouseX);
		PosY = int(mouseY);			
		var LePoint : String = PosX + "," + PosY;
		if (PolygoneEnCours == null) {
			PolygoneEnCours = new MovieClip();
			PolygoneEnCours.ListePoints = new Array();
			PolygoneEnCours.x = PosX;
			PolygoneEnCours.y = PosY;			
			PolygoneEnCours.Ep = OptionLigne.Ep;
			PolygoneEnCours.Polygone = true;
		}		
		if (LigneEnCours != null) {
			LigneEnCours.Polygone = true;
			LigneEnCours.Ep = PolygoneEnCours.Ep;
			PolygoneEnCours.ListePoints.push(LigneEnCours); 
			NouvelleCarte.addChild(LigneEnCours);
			LigneEnCours = null;
			Comptage_Forme(1);
		}
	}	
}

// Clic bouton Fin Polygone ou Changement outil
FinPolygone.addEventListener(MouseEvent.MOUSE_DOWN, Clique_FinPolygone);
function Clique_FinPolygone(E:Event):void {
	var Couleur:int = (0x000000);
	if (LigneEnCours != null) {
		NouvelleCarte.removeChild(LigneEnCours);
	}
	if (PolygoneEnCours != null) {
		if (PolygoneEnCours.ListePoints.length>=2) {
			// Dernière ligne : on revient au début
			var FermeturePoly : MovieClip = new MovieClip();
			NouvelleCarte.addChild(FermeturePoly); 		
			FermeturePoly.FinX = 0;
			FermeturePoly.FinY = 0;
			PolygoneEnCours.ListePoints.push(FermeturePoly); 
		
			if (OptionLigne.Pleine.Encoche.E1.visible) {
				PolygoneEnCours.graphics.beginFill(Couleur);
				PolygoneEnCours.Plein = true;
			} else {
				PolygoneEnCours.graphics.endFill();
				PolygoneEnCours.Plein = false;
			}
			PolygoneEnCours.graphics.lineStyle(PolygoneEnCours.Ep, Couleur);
			NouvelleCarte.addChild(PolygoneEnCours);
			PolygoneEnCours.graphics.moveTo(0,0);
			var MinX : int;
			var MaxX : int;
			var MinY : int;
			var MaxY : int;
			for each (var Ligne : MovieClip in PolygoneEnCours.ListePoints) {
				PolygoneEnCours.graphics.lineTo(Ligne.FinX, Ligne.FinY);
				NouvelleCarte.removeChild(Ligne); 		
				if (Ligne.FinX > MaxX) {
					MaxX = Ligne.FinX;
				}
				if (Ligne.FinY > MaxY) {
					MaxY = Ligne.FinY;
				}
				if (Ligne.FinX < MinX) {
					MinX = Ligne.FinX;
				}
				if (Ligne.FinY < MinY) {
					MinY = Ligne.FinY;
				}
			}
			//PolygoneEnCours.Largeur = ((MaxX+MinX)/2 >= 0 ? 1 : -1) * PolygoneEnCours.width;
			//PolygoneEnCours.Hauteur = ((MaxY+MinY)/2 >= 0 ? 1 : -1) * PolygoneEnCours.height;			
			PolygoneEnCours.Largeur = (MaxX+MinX)/2;
			PolygoneEnCours.Hauteur = (MaxY+MinY)/2;			
			PolygoneEnCours.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Objet);
		} else {
			// supprimer polygone si inférieur à 3 traits
			for each (var LigneSuppr : MovieClip in PolygoneEnCours.ListePoints) {
				NouvelleCarte.removeChild(LigneSuppr); 		
			}		
			Comptage_Forme(-PolygoneEnCours.ListePoints.length);
		}	
	}
	LigneEnCours = null;
	PolygoneEnCours = null;
}

// MOUSE_UP
function Desactivation_Trace(E:Event):void {
	if (Outil == 0) {
		if (SelectionEnCours != null) {
			stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Deplacement);
			if (SelectionEnCours.Groupe == null) {
				OptionSelection.TX.text = ""+SelectionEnCours.x;
				OptionSelection.TY.text = ""+SelectionEnCours.y;
			} else {
				OptionSelection.TX.text = ""+SelectionEnCours.GroupeX;
				OptionSelection.TY.text = ""+SelectionEnCours.GroupeY;
			}
		}
	} else if (Outil == 1) { // Ligne
		stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Trace);
		if (LigneEnCours != null) {
			LigneEnCours.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Objet);
			LigneEnCours.Ligne = true;
			LigneEnCours.Ep = OptionLigne.Ep;
			LigneEnCours = null;
			Comptage_Forme(1);
		}
	} else if (Outil == 2) { // Courbe
		if (CourbeEnCours) {
			stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Trace);
			LigneEnCours.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Objet);
			LigneEnCours.Courbe = true;
			LigneEnCours.FinX = CourbeFinX;
			LigneEnCours.FinY = CourbeFinY;
			LigneEnCours.Ep = OptionLigne.Ep;
			LigneEnCours = null;
			CourbeEnCours = false;
			Comptage_Forme(1);
		} else {
			if (LigneEnCours != null) {
				CourbeEnCours = true;
			} else {
				stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Trace);
			}
		}
	} else if (Outil == 3) { // Rectangle
		stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Trace);
		if (LigneEnCours != null) {
			LigneEnCours.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Objet);
			LigneEnCours.Rectangle = true;
			LigneEnCours.Ep = OptionLigne.Ep;
			LigneEnCours = null;
			Comptage_Forme(1);
		}
	} else if (Outil == 4) { // Ellipse
		stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Trace);
		if (LigneEnCours != null) {
			LigneEnCours.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Objet);
			LigneEnCours.Ellipse = true;
			LigneEnCours.Ep = OptionLigne.Ep;
			LigneEnCours = null;
			Comptage_Forme(1);
		}
	} 
}

// MOUSE_DOWN
function Lancement_Trace(E:Event) {
	if (Outil == 2 && LigneEnCours != null) {
		return;
	}
	if (Outil != 6) {
		PosX = int(mouseX);
		PosY = int(mouseY);
	}
	stage.addEventListener(MouseEvent.MOUSE_MOVE, Boucle_Trace);
}

// MOVE
function Boucle_Trace(E:Event):void {
	var X:int;
	var Y:int;
	var Largeur:int;
	var Hauteur:int;
	var Couleur:int = (0x000000);
	
	// Ligne
	if (Outil == 1) {
		if (LigneEnCours != null) {
			NouvelleCarte.removeChild(LigneEnCours);
		}
		X = int(mouseX);
		Y = int(mouseY);
		LigneEnCours = new MovieClip();
		LigneEnCours.x = PosX;
		LigneEnCours.y = PosY;
		LigneEnCours.graphics.lineStyle(OptionLigne.Ep, Couleur);
					NouvelleCarte.addChild(LigneEnCours);
		LigneEnCours.graphics.lineTo(X-PosX, Y-PosY);
		LigneEnCours.FinX = X-PosX;
		LigneEnCours.FinY = Y-PosY;
	} else if (Outil == 2) { // Courbe
		if (CourbeEnCours) {
			if (LigneEnCours != null) {
				NouvelleCarte.removeChild(LigneEnCours);
			}
			LigneEnCours = new MovieClip();
			LigneEnCours.x = PosX;
			LigneEnCours.y = PosY;
			LigneEnCours.graphics.lineStyle(OptionLigne.Ep, Couleur);
			NouvelleCarte.addChild(LigneEnCours);
			X = int(mouseX);
			Y = int(mouseY);
			LigneEnCours.graphics.curveTo(X-PosX, Y-PosY, CourbeFinX, CourbeFinY);
			LigneEnCours.CourbureX = X-PosX;
			LigneEnCours.CourbureY = Y-PosY;
		} else {
			if (LigneEnCours != null) {
				NouvelleCarte.removeChild(LigneEnCours);
			}
			LigneEnCours = new MovieClip();
			LigneEnCours.x = PosX;
			LigneEnCours.y = PosY;
			LigneEnCours.graphics.lineStyle(OptionLigne.Ep, Couleur);
			NouvelleCarte.addChild(LigneEnCours);
			LigneEnCours.graphics.moveTo(0, 0);
			X = int(mouseX);
			Y = int(mouseY);
			LigneEnCours.graphics.curveTo(X-PosX, Y-PosY, X-PosX, Y-PosY);
			CourbeFinX = X-PosX;
			CourbeFinY = Y-PosY;
		}
	} else if (Outil == 3) { // Rectangle
		if (LigneEnCours != null) {
			NouvelleCarte.removeChild(LigneEnCours);
		}
		LigneEnCours = new MovieClip();
		if (OptionLigne.Pleine.Encoche.E1.visible) {
			LigneEnCours.graphics.beginFill(Couleur);
			LigneEnCours.Plein = true;
		} else {
			LigneEnCours.graphics.endFill();
			LigneEnCours.Plein = false;
		}
		LigneEnCours.graphics.lineStyle(OptionLigne.Ep, Couleur, 1, true, "normal", null, JointStyle.MITER, 3);
		NouvelleCarte.addChild(LigneEnCours);
		LigneEnCours.x = PosX;
		LigneEnCours.y = PosY;
		X = int(mouseX);
		Y = int(mouseY);
		Largeur = X-PosX;
		Hauteur = Y-PosY;
		LigneEnCours.graphics.drawRect(0, 0, Largeur, Hauteur);
		LigneEnCours.Largeur = Largeur;
		LigneEnCours.Hauteur = Hauteur;
	} else if (Outil == 4) { // Ellipse
		if (LigneEnCours != null) {
			NouvelleCarte.removeChild(LigneEnCours);
		}
		LigneEnCours = new MovieClip();
		if (OptionLigne.Pleine.Encoche.E1.visible) {
			LigneEnCours.graphics.beginFill(Couleur);
			LigneEnCours.Plein = true;
		} else {
			LigneEnCours.graphics.endFill();
			LigneEnCours.Plein = false;
		}
		LigneEnCours.graphics.lineStyle(OptionLigne.Ep, Couleur);
		NouvelleCarte.addChild(LigneEnCours);
		LigneEnCours.x = PosX;
		LigneEnCours.y = PosY;
		X = int(mouseX);
		Y = int(mouseY);
		Largeur = X-PosX;
		Hauteur = Y-PosY;
		LigneEnCours.graphics.drawEllipse(0, 0, Largeur, Hauteur);
		LigneEnCours.Largeur = Largeur;
		LigneEnCours.Hauteur = Hauteur;
	} else if (Outil == 6) { // Polygone
		if (PolygoneEnCours != null) {
			if (LigneEnCours != null) {
				NouvelleCarte.removeChild(LigneEnCours);
			}
			X = int(mouseX);
			Y = int(mouseY);
			NouvelleLigneEnCoursPolygone(PosX,PosY,X,Y);
		}
	}
}

// Polygone on move
function NouvelleLigneEnCoursPolygone(OX : int, OY : int, X : int, Y : int):void {
	var Couleur:int = (0x000000);
	
	LigneEnCours = new MovieClip();
	LigneEnCours.x = OX;
	LigneEnCours.y = OY;
	LigneEnCours.graphics.lineStyle(OptionLigne.Ep, Couleur);
	NouvelleCarte.addChild(LigneEnCours);
	LigneEnCours.graphics.lineTo(X-OX, Y-OY);
	LigneEnCours.FinX = X-PolygoneEnCours.x;
	LigneEnCours.FinY = Y-PolygoneEnCours.y;
}

// Sélection
function Clique_Objet(E:MouseEvent, OBJET:MovieClip = null):void {
	var Objet_Cible:MovieClip;
	if (OBJET == null) {
		Objet_Cible = MovieClip(E.currentTarget);
	} else {
		Objet_Cible = OBJET;
	}
	// Selection
	if (Outil == 0) {
		if (SelectionEnCours != null) {
			if (SelectionEnCours.Groupe == null) {
				SelectionEnCours.transform.colorTransform = new ColorTransform(1, 1, 1, 1, -200, -201, -221);
			} else {
				for (var m:int = 0; m < SelectionEnCours.Groupe.length; m++) {
					SelectionEnCours.Groupe[m].transform.colorTransform = new ColorTransform(1, 1, 1, 1, -200, -201, -221);
				}
			}
		}
		SelectionEnCours = Objet_Cible;
		// Effet
		OptionSelection.MAJ_Translation();
		//
		if (SelectionEnCours.Groupe == null) {
			SelectionEnCours.transform.colorTransform = new ColorTransform(1, 1, 1, 1, 200, 201, 221);
			SelectionEnCours.DX = SelectionEnCours.mouseX;
			SelectionEnCours.DY = SelectionEnCours.mouseY;
			OptionSelection.TX.text = ""+SelectionEnCours.x;
			OptionSelection.TY.text = ""+SelectionEnCours.y;
			OptionSelection.visible = true;
		} else {
			if (SelectionEnCours.Groupe.length == 0 || SelectionEnCours.Groupe.length == 1) {
				SelectionEnCours.Groupe = null;
				SelectionEnCours.transform.colorTransform = new ColorTransform(1, 1, 1, 1, 200, 201, 221);
				SelectionEnCours.DX = SelectionEnCours.mouseX;
				SelectionEnCours.DY = SelectionEnCours.mouseY;
				OptionSelection.TX.text = ""+SelectionEnCours.x;
				OptionSelection.TY.text = ""+SelectionEnCours.y;
				OptionSelection.visible = true;
			} else {
				for (var k:int = 0; k < SelectionEnCours.Groupe.length; k++) {
					var Cible:MovieClip = SelectionEnCours.Groupe[k];
					Cible.transform.colorTransform = new ColorTransform(1, 1, 1, 1, 100, 150, 150);
					Cible.DX = Cible.mouseX;
					Cible.DY = Cible.mouseY;
					//
					PosX = mouseX;
					PosY = mouseY;
					DeplacementGroupeX = Cible.GroupeX;
					DeplacementGroupeY = Cible.GroupeY;
					OptionSelection.TX.text = ""+Cible.GroupeX;
					OptionSelection.TY.text = ""+Cible.GroupeY;
					OptionSelection.visible = true;
				}
			}
		}
		// Déplacement
		stage.addEventListener(MouseEvent.MOUSE_MOVE, Boucle_Deplacement);
	} else {
		// Groupe
		if (Outil == 5) {
			var ObjetCible:MovieClip = Objet_Cible;
			for (var i:int = 0; i < ListeSelection.length; i++) {
				if (ListeSelection[i] == ObjetCible) {
					ObjetCible.transform.colorTransform = new ColorTransform(1, 1, 1, 1, -200, -201, -221);
					ListeSelection.splice(i, 1);
					return;
				}
			}
			//
			ListeSelection.push(ObjetCible);
			ObjetCible.transform.colorTransform = new ColorTransform(1, 1, 1, 1, 200, 201, 221);
		}
	}
}

function Boucle_Deplacement(E:MouseEvent):void {
	if (SelectionEnCours.Groupe == null) {
		PosX = mouseX-SelectionEnCours.DX;
		PosY = mouseY-SelectionEnCours.DY;
		SelectionEnCours.x = PosX;
		SelectionEnCours.y = PosY;
		OptionSelection.TX.text = ""+PosX;
		OptionSelection.TY.text = ""+PosY;
	} else {
		var Nb:int = SelectionEnCours.Groupe.length;
		for (var i:int = 0; i < Nb; i++) {
			var Cible:MovieClip = SelectionEnCours.Groupe[i];
			Cible.GroupeX = DeplacementGroupeX+(mouseX-PosX);
			Cible.GroupeY = DeplacementGroupeY+(mouseY-PosY);
			Cible.x = mouseX-Cible.DX;
			Cible.y = mouseY-Cible.DY;
		}
		OptionSelection.TX.text = ""+SelectionEnCours.GroupeX;
		OptionSelection.TY.text = ""+SelectionEnCours.GroupeY;
	}
	E.updateAfterEvent();
}

function Changement_Outil(OUTIL:int, CLIP:MovieClip):void {
	Clique_FinPolygone(null);
	PolygoneEnCours = null;
	LigneEnCours = null;
	stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Trace);
	
	if (Outil == 0) {
		if (SelectionEnCours != null) {
			if (SelectionEnCours.Groupe == null) {
				SelectionEnCours.transform.colorTransform = new ColorTransform(1, 1, 1, 1, -200, -201, -221);
			} else {
				for (var m:int = 0; m < SelectionEnCours.Groupe.length; m++) {
					SelectionEnCours.Groupe[m].transform.colorTransform = new ColorTransform(1, 1, 1, 1, -200, -201, -221);
				}
			}
			SelectionEnCours = null;
		}
		OptionSelection.visible = false;
	} else if (Outil == 1) {
		OptionLigne.visible = false;
	} else if (Outil == 2) {
		OptionLigne.visible = false;
	} else if (Outil == 3) {
		OptionLigne.visible = false;
	} else if (Outil == 4) {
		OptionLigne.visible = false;
	} else if (Outil == 5) { // Groupe
		for (var i:int = 0; i< ListeSelection.length; i++) {
			ListeSelection[i].transform.colorTransform = new ColorTransform(1, 1, 1, 1, -200, -201, -221);
		}
		ListeSelection = new Array();
	} else if (Outil == 6) {
		OptionLigne.visible = false;
	}

	//
	Outil = OUTIL;
	OutilEnCours.Encoche.E1.visible = false;
	OutilEnCours.Encoche.E0.visible = true;
	OutilEnCours = CLIP;
	OutilEnCours.Encoche.E1.visible = true;
	OutilEnCours.Encoche.E0.visible = false;
	//
	OptionGroupe.visible = false;
	ZoneClique.visible = true;
	//
	if (Outil == 0) {
		ZoneClique.visible = false;
		SelectionEnCours = null;
		OptionSelection.visible = false;
	} else if (Outil == 1) { //Ligne
		LigneEnCours = null;
		OptionLigne.visible = true;
		OptionLigne.Pleine.visible = false;
	} else if (Outil == 2) { // Courbe
		LigneEnCours = null;
		CourbeEnCours = false;
		OptionLigne.visible = true;
		OptionLigne.Pleine.visible = false;
	} else if (Outil == 3) { // Rectangle
		LigneEnCours = null;
		OptionLigne.visible = true;
		OptionLigne.Pleine.visible = true;
	} else if (Outil == 4) { // Ellipse
		LigneEnCours = null;
		OptionLigne.visible = true;
		OptionLigne.Pleine.visible = true;
	} else if (Outil == 5) { // Groupe
		ZoneClique.visible = false;
		ListeSelection = new Array();
		OptionGroupe.visible = true;
	} else if (Outil == 6) { // Polygone
		LigneEnCours = null;
		OptionLigne.visible = true;
		OptionLigne.Pleine.visible = true;		
	}
	FinPolygone.visible = O_Polygone.Encoche.E1.visible;
}

function Moteur_Graphique(CLIP:MovieClip, CARTE:XMLNode):void {
	CLIP.ListeMouvement = new Array();
	// groupe
	var LsGroupe:Array = CARTE.firstChild.childNodes;
	var NbGroupe:int = LsGroupe.length;
	for (var i:int = 0; i<NbGroupe; i++) {
		var Groupe:MovieClip = new MovieClip();
		var GroupeXML:XMLNode = LsGroupe[i];
		var LsFormeGroupe:Array = GroupeXML.childNodes;
		var NbFormeGroupe:int = LsFormeGroupe.length;
		var ObjetBase:XMLNode;
		var MinX:int = 0;
		var MinY:int = 0;
		for (var k:int = 0; k<NbFormeGroupe; k++) {
			var FormeGroupe:XMLNode = LsFormeGroupe[k];
			if (k == 0) {
				var InfoGroupe:Array = GroupeXML.attributes.P.split(",");
				Groupe.x = int(InfoGroupe[0]);
				Groupe.y = int(InfoGroupe[1]);
				ObjetBase = FormeGroupe;
			}
			var FormeGroupeClip:MovieClip = Creation_Forme(FormeGroupe, true, Groupe.x, Groupe.y);
			if (FormeGroupeClip != null) {
				Groupe.addChild(FormeGroupeClip);
				//
				if (FormeGroupeClip.DepX < MinX) {
					MinX = FormeGroupeClip.DepX;
				}
				if (FormeGroupeClip.DepY < MinY) {
					MinY = FormeGroupeClip.DepY;
				}
			}
		}
		//
		if (ObjetBase.firstChild != null) {
			if (ObjetBase.firstChild.nodeName == "R") {
				var SuperClip:MovieClip = new MovieClip();
				SuperClip.addChild(Groupe);
				CLIP.addChild(SuperClip);
				var DecalageGroupeX:int;
				var DecalageGroupeY:int;
				if (MinX < 0) {
					SuperClip.x = int(Groupe.x+(Groupe.width/2))+MinX;

					DecalageGroupeX = -int(Groupe.width/2)-MinX;
					Groupe.x = DecalageGroupeX;
				} else {
					SuperClip.x = int(Groupe.x+(Groupe.width/2));
					DecalageGroupeX = -int(Groupe.width/2);
					Groupe.x = DecalageGroupeX;
				}
				if (MinY < 0) {
					SuperClip.y = int(Groupe.y+(Groupe.height/2))+MinY;
					DecalageGroupeY = -int(Groupe.height/2)-MinY;
					Groupe.y = DecalageGroupeY;
				} else {
					SuperClip.y = int(Groupe.y+(Groupe.height/2));
					DecalageGroupeY = -int(Groupe.height/2);
					Groupe.y = DecalageGroupeY;
				}
				Creation_Mouvement(CLIP, SuperClip, ObjetBase.firstChild, true);
				if (ObjetBase.firstChild.nextSibling != null) {
					Creation_Mouvement(CLIP, SuperClip, ObjetBase.firstChild.nextSibling, false, true, DecalageGroupeX, DecalageGroupeY);
				}
			} else {
				Creation_Mouvement(CLIP, Groupe, ObjetBase.firstChild, true);
				CLIP.addChild(Groupe);
			}
		} else {
			CLIP.addChild(Groupe);
		}
	}
	// Forme
	var LsForme:Array = CARTE.firstChild.nextSibling.childNodes;
	var NbForme:int = LsForme.length;
	for (var m:int = 0; m<NbForme; m++) {
		var FormeXML:XMLNode = LsForme[m];
		var ClipForme:MovieClip = Creation_Forme(FormeXML, false);
		if (ClipForme != null) {
			if (FormeXML.firstChild != null) {
				if (FormeXML.firstChild.nodeName == "R") {
					var ClipRotation:MovieClip = new MovieClip();
					ClipRotation.addChild(ClipForme);
					CLIP.addChild(ClipRotation);
					var DecalageX:int;
					var DecalageY:int;
					
					if (ClipForme.Polygone) {
						ClipRotation.x = ClipForme.x+ClipForme.DepX;
						DecalageX = -ClipForme.DepX;
						ClipForme.x = DecalageX;
						ClipRotation.y = ClipForme.y+ClipForme.DepY;
						DecalageY = -ClipForme.DepY;
						ClipForme.y = DecalageY;					
					} else {
						if (ClipForme.DepX < 0) {
							ClipRotation.x = int(ClipForme.x+(ClipForme.width/2))+ClipForme.DepX;
							DecalageX = -int(ClipForme.width/2)-ClipForme.DepX;
							ClipForme.x = DecalageX;
						} else {
							ClipRotation.x = int(ClipForme.x+(ClipForme.width/2));
							DecalageX = -int(ClipForme.width/2);
							ClipForme.x = DecalageX;
						}
						if (ClipForme.DepY < 0) {
							ClipRotation.y = int(ClipForme.y+(ClipForme.height/2))+ClipForme.DepY;
							DecalageY = -int(ClipForme.height/2)-ClipForme.DepY;
							ClipForme.y = DecalageY;
						} else {
							ClipRotation.y = int(ClipForme.y+(ClipForme.height/2));
							DecalageY = -int(ClipForme.height/2);
							ClipForme.y = DecalageY;
						}
					}
					Creation_Mouvement(CLIP, ClipRotation, FormeXML.firstChild, true);
					if (FormeXML.firstChild.nextSibling != null) {
						Creation_Mouvement(CLIP, ClipRotation, FormeXML.firstChild.nextSibling, false, true, DecalageX, DecalageY);
					}
				} else {
					Creation_Mouvement(CLIP, ClipForme, FormeXML.firstChild, true);
					CLIP.addChild(ClipForme);
				}
			} else {
				CLIP.addChild(ClipForme);
			}
		}
	}
}

function Creation_Forme(FORME:XMLNode, GROUPE:Boolean = false, X:int = 0, Y:int = 0):MovieClip {
	try {
		var Forme:MovieClip = new MovieClip();
		var Info:Array = FORME.attributes.P.split(",");
		var Ep:int = int(Info[0]);
		var Couleur:int = (0x000000);
		Forme.x = int(Info[1])-X;
		Forme.y = int(Info[2])-Y;
		//
		var X:int = int(Info[3]);
		var Y: int = int(Info[4]);
		if (GROUPE) {
			Forme.DepX = X+Forme.x;
			Forme.DepY = Y+Forme.y;
		} else {
			Forme.DepX = X;
			Forme.DepY = Y;
		}
		//
		if (FORME.nodeName == "L") {
			Forme.graphics.lineStyle(Ep, Couleur);
			Forme.graphics.lineTo(X, Y);
		} else if (FORME.nodeName == "C") {
			Forme.graphics.lineStyle(Ep, Couleur);
			Forme.graphics.curveTo(X, Y, int(Info[5]), int(Info[6]));
		} else if (FORME.nodeName == "R") {
			Forme.graphics.lineStyle(Ep, Couleur, 1, true, "normal", null, JointStyle.MITER, 3);	
			if (Info[5] == "1") {
				Forme.graphics.beginFill(Couleur);
			}
			Forme.graphics.drawRect(0, 0, X, Y);
		} else if (FORME.nodeName == "E") {
			Forme.graphics.lineStyle(Ep, Couleur);
			if (Info[5] == "1") {
				Forme.graphics.beginFill(Couleur);
			}
			Forme.graphics.drawEllipse(0, 0, X, Y);
		} else if (FORME.nodeName == "P") {
			Forme.graphics.lineStyle(Ep, Couleur);
			Forme.Polygone = true;
			if (Info[5] == "1") {
				Forme.graphics.beginFill(Couleur);
			}
			var Liste:Array = FORME.attributes.Z.split(";");
			if (Liste.length >= 2) {
				Forme.graphics.moveTo(0,0);
				var Coordonnees : Array;
				for each (var UnPoint : String in Liste) {
					Coordonnees = UnPoint.split(",");
					Forme.graphics.lineTo(int(Coordonnees[0]), int(Coordonnees[1]));				
				}
				// finir par 0,0
				if (Coordonnees[0] != "0" || Coordonnees[1] != "0") {
					Forme.graphics.lineTo(0,0);
				}
			} else {
				return null;
			}
			
			
		}
		return Forme;
	} catch (e:Error) {
		trace (" Error " + e);
	}
	return null;
}

function Creation_Mouvement(CARTE:MovieClip, FORME:MovieClip, INFO:XMLNode, AJOUT:Boolean, DECALAGE:Boolean = false, DecX:int = 0, DecY:int = 0):void {
	CARTE.Dynamique = true;
	if (AJOUT) {
		CARTE.ListeMouvement.push(FORME);
	}
	//
	var Info:Array = Info = INFO.attributes.P.split(",");
	if (INFO.nodeName == "T") {
		FORME.Translation = true;
		FORME.DX = FORME.x;
		FORME.DY = FORME.y;
		FORME.T0 = int(Info[0])*1000;
		FORME.D = int(Info[1])*1000;
		FORME.DF = FORME.T0+FORME.D;
		FORME.FX = int(Info[2]);
		FORME.FY = int(Info[3]);
		if (DECALAGE) {
			FORME.FX -= DecX;
			FORME.FY -= DecY;
		}
		FORME.MX = FORME.FX-FORME.DX;
		FORME.MY = FORME.FY-FORME.DY;
		if (Info[4] == "1") {
			FORME.B1 = true;
			FORME.B2 = false;
		} else {
			if (Info[4] == "2") {
				FORME.B1 = false;
				FORME.B2 = true;
			} else {
				FORME.B1 = false;
				FORME.B2 = false;
			}
		}
	} else {
		if (INFO.nodeName == "R") {
			FORME.Rotation = true;
			FORME.R_T0 = int(Info[0])*1000;
			FORME.R_D = int(Info[1])*1000;
			FORME.R_DF = FORME.R_T0+FORME.R_D;
			FORME.R_DEG = int(Info[2]);
			if (Info[3] == "1") {
				FORME.R_B1 = true;
				FORME.R_B2 = false;
			} else {
				if (Info[3] == "2") {
					FORME.R_B1 = false;
					FORME.R_B2 = true;
					FORME.R_Reverse = false;
				} else {
					FORME.R_B1 = false;
					FORME.R_B2 = false;
				}
			}
		}
	}
}

function Formatage_Carte():XMLNode {
	var NbObjet:int = NouvelleCarte.numChildren;
	for (var m:int = 0; m<NbObjet; m++) {
		MovieClip(NouvelleCarte.getChildAt(m)).F = true;
	}
	//
	var Carte:XMLNode = new XMLDocument("<C><G /><F /></C>").firstChild;
	var Groupes:XMLNode = Carte.firstChild;
	var Formes:XMLNode = Carte.firstChild.nextSibling;
	for (var i:int = 0; i<NbObjet; i++) {
		var Forme:MovieClip = MovieClip(NouvelleCarte.getChildAt(i));
		if (Forme.F) {
			if (Forme.Groupe == null) {
				var FormeXML:XMLNode = Formatage_Forme(Forme);
				if (FormeXML != null) {
					if (Forme.Rotation != null) {
						FormeXML.appendChild(Forme.Rotation.cloneNode(true));
					}
					if (Forme.Translation != null) {
						FormeXML.appendChild(Forme.Translation.cloneNode(true));
					}
					Formes.appendChild(FormeXML);
				}
				Forme.F = false;
			} else {
				var NouveauGroupe:XMLNode = new XMLDocument("<G />").firstChild;
				for (var k:int = 0; k<Forme.Groupe.length; k++) {
					var FormeCible:MovieClip = Forme.Groupe[k];
					if (FormeCible.F) {
						var FormeGroupeXML:XMLNode = Formatage_Forme(FormeCible);
						if (FormeGroupeXML != null) {
							if (k == 0) {
								NouveauGroupe.attributes.P = FormeCible.GroupeX+","+FormeCible.GroupeY;
								//
								if (FormeCible.Rotation != null) {
									FormeGroupeXML.appendChild(FormeCible.Rotation.cloneNode(true));
								}
								if (FormeCible.Translation != null) {
									FormeGroupeXML.appendChild(FormeCible.Translation.cloneNode(true));
								}
							}
							NouveauGroupe.appendChild(FormeGroupeXML);
						}
						FormeCible.F = false;
					}
				}
				Groupes.appendChild(NouveauGroupe);
			}
		}
	}
	return Carte;
}

function Formatage_Forme(Forme:MovieClip):XMLNode {
	var FormeXML:XMLNode = new XMLDocument("<F />").firstChild;
	if (Forme.Ligne) {
		FormeXML.nodeName = "L";
		FormeXML.attributes.P = Forme.Ep+","+Forme.x+","+Forme.y+","+Forme.FinX+","+Forme.FinY;
		return FormeXML;
	} else if (Forme.Courbe) {
		FormeXML.nodeName = "C";
		FormeXML.attributes.P = Forme.Ep+","+Forme.x+","+Forme.y+","+Forme.CourbureX+","+Forme.CourbureY+","+Forme.FinX+","+Forme.FinY;
		return FormeXML;
	} else {
		var Plein:String;
		if (Forme.Plein) {
			Plein = "1";
		} else {
			Plein = "0";
		}
		if (Forme.Rectangle) {
			FormeXML.nodeName = "R";
			FormeXML.attributes.P = Forme.Ep+","+Forme.x+","+Forme.y+","+Forme.Largeur+","+Forme.Hauteur+","+Plein;
			return FormeXML;
		} else if (Forme.Ellipse) {
			FormeXML.nodeName = "E";
			FormeXML.attributes.P = Forme.Ep+","+Forme.x+","+Forme.y+","+Forme.Largeur+","+Forme.Hauteur+","+Plein;
			return FormeXML;
		} else if (Forme.Polygone) {
			FormeXML.nodeName = "P";
			var Liste : String = "";
			for each (var Ligne : MovieClip in Forme.ListePoints) {
				if (Ligne.FinX != 0 || Ligne.FinY != 0) {
					if (Liste != "") {
						Liste += ";" ;
					}
					Liste += Ligne.FinX + "," +Ligne.FinY;
				}
			}
			FormeXML.attributes.P = Forme.Ep+","+Forme.x+","+Forme.y+","+Forme.Largeur+","+Forme.Hauteur+","+Plein;
			FormeXML.attributes.Z = Liste;
			return FormeXML;			
		}

	}
	return null;
}

function Chargement_Carte(CODE:String):void {
	Note.text = "";
	Initialisation();
	var ComptageForme:int = 0;
	try {
		
		var Carte:XMLNode = new XMLDocument(CODE).firstChild;
		if (Carte.attributes.N != null) {
			NomCarte = Carte.attributes.N;
		}
		if (Carte.attributes.A != null) {
			NomAuteur = Carte.attributes.A;
		}
		//
		var LsGroupe:Array = Carte.firstChild.childNodes;
		var NbGroupe:int = LsGroupe.length;
		for (var i:int = 0; i<NbGroupe; i++) {
			var CreationGroupe:Array = new Array();
			var Groupe:XMLNode = LsGroupe[i];
			var GroupeX:int = int(Groupe.attributes.P.split(",")[0]);
			var GroupeY:int = int(Groupe.attributes.P.split(",")[1]);
			var Translation:XMLNode=null;
			var Rotation:XMLNode=null;
			var LsForme:Array = Groupe.childNodes;
			var NbForme:int = LsForme.length;
			var k:int;
			var FormeClip:MovieClip;
			for (k = 0; k<NbForme; k++) {
				var FormeXML:XMLNode = LsForme[k];
				FormeClip = Creation_Forme_Chargement(FormeXML);
				var TotalFormes : int = ComptageForme+NbFormes(FormeClip);
				if (FormeClip != null && TotalFormes <= FormeSuperMax) {
					ComptageForme=TotalFormes;
					NouvelleCarte.addChild(FormeClip);
					CreationGroupe.push(FormeClip);
					if (k == 0) {
						if (FormeXML.firstChild != null) {
							if (FormeXML.firstChild.nodeName == "R") {
								Rotation = FormeXML.firstChild;
								if (FormeXML.firstChild.nextSibling != null) {
									Translation = FormeXML.firstChild.nextSibling;
								}
							} else {
								Translation = FormeXML.firstChild;
							}
						}
					}
				}
			}
			for (k = 0; k<NbForme; k++) {
				FormeClip = CreationGroupe[k];
				FormeClip.GroupeX = GroupeX;
				FormeClip.GroupeY = GroupeY;
				FormeClip.Groupe = CreationGroupe;
				if (Rotation != null) {
					FormeClip.Rotation = Rotation;
				}
				if (Translation != null) {
					FormeClip.Translation = Translation;
				}
			}
		}
		//
		var LsForme2:Array = Carte.firstChild.nextSibling.childNodes;
		var NbForme2:int = LsForme2.length;
		for (var m:int = 0; m<NbForme2; m++) {
			var FormeXML2:XMLNode = LsForme2[m];
			var FormeClip2:MovieClip = Creation_Forme_Chargement(FormeXML2);
			var TotalFormes2 : int = ComptageForme+NbFormes(FormeClip2);
			if (FormeClip2 != null && TotalFormes2 <= FormeSuperMax) {
				ComptageForme=TotalFormes2;
				NouvelleCarte.addChild(FormeClip2);
				if (FormeXML2.firstChild != null) {
					if (FormeXML2.firstChild.nodeName == "R") {
						FormeClip2.Rotation = FormeXML2.firstChild;
						if (FormeXML2.firstChild.nextSibling != null) {
							FormeClip2.Translation = FormeXML2.firstChild.nextSibling;
						}
					} else {
						FormeClip2.Translation = FormeXML2.firstChild;
					}
				}
			}
		}
		
	} catch (e:Error) {
		Code.text = "ERREUR";
		Code.setSelection(0, 0);
	}
	Comptage_Forme(ComptageForme);
	Changement_Outil(0, O_Selection);
}

function Creation_Forme_Chargement(FORME:XMLNode):MovieClip {
	try {
		var Forme:MovieClip = new MovieClip();
		var Info:Array = FORME.attributes.P.split(",");
		var Ep:int = int(Info[0]);
		var Couleur:int = (0x000000);
		Forme.Ep = Ep;
		Forme.x = int(Info[1]);
		Forme.y = int(Info[2]);
		//
		var X:int = int(Info[3]);
		var Y: int = int(Info[4]);

		//
		Forme.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Objet);
		//
		if (FORME.nodeName == "L") {
			Forme.Ligne = true;
			Forme.graphics.lineStyle(Ep, Couleur);
			Forme.graphics.lineTo(X, Y);
			Forme.FinX = X;
			Forme.FinY = Y;
		} else if (FORME.nodeName == "C") {
			Forme.Courbe = true;
			Forme.graphics.lineStyle(Ep, Couleur);
			Forme.FinX = int(Info[5]);
			Forme.FinY = int(Info[6]);
			Forme.CourbureX = X;
			Forme.CourbureY = Y;
			Forme.graphics.curveTo(X, Y, Forme.FinX, Forme.FinY);
		} else if (FORME.nodeName == "R") {
			Forme.Rectangle = true;
			Forme.graphics.lineStyle(Ep, Couleur, 1, true, "normal", null, JointStyle.MITER, 3);				
			if (Info[5] == "1") {
				Forme.graphics.beginFill(Couleur);
				Forme.Plein = true;
			} else {
				Forme.Plein = false;
			}
			Forme.graphics.drawRect(0, 0, X, Y);
			Forme.Largeur = X;
			Forme.Hauteur = Y;
		} else if (FORME.nodeName == "E") {
			Forme.Ellipse = true;
			Forme.graphics.lineStyle(Ep, Couleur);
			if (Info[5] == "1") {
				Forme.graphics.beginFill(Couleur);
				Forme.Plein = true;
			} else {
				Forme.Plein = false;
			}
			Forme.graphics.drawEllipse(0, 0, X, Y);
			Forme.Largeur = X;
			Forme.Hauteur = Y;
		} else if (FORME.nodeName == "P") {
			Forme.Polygone= true;
			Forme.Largeur = X;
			Forme.Hauteur = Y;			
			Forme.graphics.lineStyle(Ep, Couleur);
			if (Info[5] == "1") {
				Forme.graphics.beginFill(Couleur);
				Forme.Plein = true;
			} else {
				Forme.Plein = false;
			}
			var Liste:Array = FORME.attributes.Z.split(";");
			if (Liste.length >= 2) { 
				Forme.graphics.moveTo(0,0);
				Forme.ListePoints = new Array();
				var PointX : int;
				var PointY : int;
				for each (var UnPoint : String in Liste) { 
					var Coordonnees : Array = UnPoint.split(",");
					PointX = int(Coordonnees[0]);
					PointY = int(Coordonnees[1]);
					Forme.graphics.lineTo(PointX, PointY);
					var Ligne : MovieClip = new MovieClip();
					Ligne.FinX = PointX;
					Ligne.FinY = PointY;
					Forme.ListePoints.push(Ligne);
				}
				// si dernier point n'est pas 0,0, on le rajoute
				if (PointX!=0 || PointY!=0) {
					Forme.graphics.lineTo(0, 0);
					var LastLigne : MovieClip = new MovieClip();
					LastLigne.FinX = 0;
					LastLigne.FinY = 0;
					Forme.ListePoints.push(LastLigne);					
				}
			} else {
				return null;
			}
		
		}
		return Forme;
	} catch (e:Error) {
		trace(" Error " + e);
	}
	return null;
}


function NbFormes(FORME : MovieClip) : int {
	if (FORME == null) {
		return 0;
	} else if (FORME.Groupe != null) {
		var Total : int = 0;
		for each (var SousForme : MovieClip in FORME.Groupe) {
			Total += NbFormesNonGroupe(SousForme);
		}
		return Total;
	} else {
		return NbFormesNonGroupe(FORME);
	}
}

function NbFormesNonGroupe(FORME : MovieClip) : int {
	if (FORME.Polygone) {
		return FORME.ListePoints.length-1;
	} else {			
		return 1 ;
	}
}

function Comptage_Forme(NUM:int):void {
	FormeEnCours += NUM;
	FormeDispo.text = FormeEnCours+"/"+FormeMax;
	if (FormeEnCours == FormeMax || FormeEnCours >= FormeSuperMax) {
		Changement_Outil(0, O_Selection);
	}
}

OptionSelection.Suppr.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Suppr);
function Clique_Suppr(E:Event):void {
	if (SelectionEnCours != null) {
		if (SelectionEnCours.Groupe == null) {
			NouvelleCarte.removeChild(SelectionEnCours);
			Comptage_Forme(-NbFormes(SelectionEnCours));
		} else {
			Comptage_Forme(-NbFormes(SelectionEnCours));
			for (var i:int = 0; i<SelectionEnCours.Groupe.length; i++) {
				NouvelleCarte.removeChild(SelectionEnCours.Groupe[i]);
			}
		}
		SelectionEnCours = null;
		OptionSelection.visible = false;
	}
}

O_Selection.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Selection);
function Clique_Selection(E:Event):void {
	Changement_Outil(0, O_Selection);
}
O_Ligne.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Ligne);
function Clique_Ligne(E:Event):void {
	if (FormeEnCours != FormeMax && FormeEnCours < FormeSuperMax) {
		Changement_Outil(1, O_Ligne);
	}
}
O_Courbe.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Courbe);
function Clique_Courbe(E:Event):void {
	if (FormeEnCours != FormeMax && FormeEnCours < FormeSuperMax) {
		Changement_Outil(2, O_Courbe);
	}
}
O_Rectangle.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Rectangle);
function Clique_Rectangle(E:Event):void {
	if (FormeEnCours != FormeMax && FormeEnCours < FormeSuperMax) {
		Changement_Outil(3, O_Rectangle);
	}
}
O_Polygone.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Polygone);
function Clique_Polygone(E:Event):void {
	if (FormeEnCours != FormeMax && FormeEnCours < FormeSuperMax) {
		Changement_Outil(6, O_Polygone);
	}
}
O_Ellipse.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Ellipse);
function Clique_Ellipse(E:Event):void {
	if (FormeEnCours != FormeMax && FormeEnCours < FormeSuperMax) {
		Changement_Outil(4, O_Ellipse);
	}
}
O_Grouper.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Grouper);
function Clique_Grouper(E:Event):void {
	Changement_Outil(5, O_Grouper);
}

OptionLigne.Pleine.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Pleine);
function Clique_Pleine(E:Event):void {
	if (OptionLigne.Pleine.Encoche.E1.visible) {
		OptionLigne.Pleine.Encoche.E1.visible = false;
		OptionLigne.Pleine.Encoche.E0.visible = true;
	} else {
		OptionLigne.Pleine.Encoche.E1.visible = true;
		OptionLigne.Pleine.Encoche.E0.visible = false;
	}
}

OptionGroupe.Grouper.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Groupe);
function Clique_Groupe(E:Event):void {
	if (ListeSelection.length < 2) {
		return;
	}
	var GroupeX:int;
	var GroupeY:int;
	var Formes:Array = new Array();
	for (var k:int = 0; k < ListeSelection.length; k++) {
		var FormeCible:MovieClip = ListeSelection[k];
		Formes[k] = FormeCible;
		//
		if (k == 0) {
			GroupeX = FormeCible.x;
			GroupeY = FormeCible.y;
		} else {
			if (FormeCible.x < GroupeX) {
				GroupeX = FormeCible.x;
			}
			if (FormeCible.y < GroupeY) {
				GroupeY = FormeCible.y;
			}
		}
	}
	//
	for (var i:int = 0; i < ListeSelection.length; i++) {
		var Forme:MovieClip = ListeSelection[i];
		Forme.GroupeX = GroupeX;
		Forme.GroupeY = GroupeY;
		Forme.Translation = null;
		Forme.Rotation = null;
		Forme.transform.colorTransform = new ColorTransform(1, 1, 1, 1, 100, 150, 150);
		if (Forme.Groupe != null) {
			for (var m:int = 0; m<Forme.Groupe.length; m++) {
				var Cible:MovieClip = Forme.Groupe[m];
				if (Cible.Groupe != null) {
					for (var z:int = 0; z<Cible.Groupe.length; z++) {
						if (Cible.Groupe[z] == Forme) {
							Cible.Groupe.splice(z, 1);
							break;
						}
					}
				}
			}
		}
		Forme.Groupe = Formes;
	}
	//
	var ObjetClique:MovieClip = ListeSelection[0];
	Changement_Outil(0, O_Selection);
	//
	Clique_Objet(null, ObjetClique);
}

OptionGroupe.GrouperTout.addEventListener(MouseEvent.MOUSE_DOWN, Clique_GroupeTout);
function Clique_GroupeTout(E:Event):void {
	if (NouvelleCarte.numChildren>0) {
		var UnObjet : MovieClip = MovieClip(NouvelleCarte.getChildAt(0));
		if (UnObjet.Groupe==null) {
			// group all
			ListeSelection = new Array();
		
			for (var i:int = 0; i < NouvelleCarte.numChildren; i++) {
				var UneForme : MovieClip = MovieClip(NouvelleCarte.getChildAt(i));
				ListeSelection.push(UneForme);
				UneForme.transform.colorTransform = new ColorTransform(1, 1, 1, 1, 200, 201, 221);			
			}
			Clique_Groupe(null);
		} else {
			// degroup all
			for (var j:int = 0; j < NouvelleCarte.numChildren; j++) {
				var UneForm : MovieClip = MovieClip(NouvelleCarte.getChildAt(j));
				UneForm.Groupe = null;
			}
			Changement_Outil(0, O_Selection);
			Clique_Objet(null, UnObjet);			
		}
	}
	//

}


Test.addEventListener(MouseEvent.CLICK, Clique_Test);
function Clique_Test(E:Event):void {
	visible = false;
	MovieClip(parent)._Monde.visible = true;
	DernierTest = Formatage_Carte();
	DernierTestGuidage = false;
	_root.Envoie_Serveur("EdT#"+DernierTest);
}

MesCartes.addEventListener(MouseEvent.MOUSE_DOWN, Clique_MesMaps);
function Clique_MesMaps(E:Event):void {
	if (_root._FenetreMaps.InitEditeur) {
		_root._FenetreMaps.visible = true;
	} else {
		_root.Commandes("mapsde");
		_root._FenetreMaps.InitEditeur = true;
	}	
}

EditeurExterne.addEventListener(MouseEvent.MOUSE_DOWN, Clique_EditeurExterne);
function Clique_EditeurExterne(E:Event):void {
	navigateToURL(new URLRequest("http://sakisan.be/aaaah/editeur.swf"), "_blank");
}

Sauver.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Sauver);
function Clique_Sauver(E:Event):void {
	if (true || EstAuteur || CodeCarte.text == "Nouvelle") {	
		System.setClipboard(Formatage_Carte().toString());
	} else {
		Code.text = "Impossible d'effectuer cette action car vous n'êtes pas l'auteur de la carte.";	
	}	
}

Charger.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Charger);
function Clique_Charger(E:Event):void {
	if (Code.text != "") {
		if (Code.text.substr(0, 1) == "@" && Code.text.length < 20) {
			_root.Envoie_Serveur("EdC#"+Code.text.substr(1));
			Code.text = "";
		} else {
			_root.Envoie_Serveur("EdN");
			CodeCarte.text = "Nouvelle";
			Chargement_Carte(Code.text);
			Code.text = "";
		}
	}
}

Exporter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Exporter);
function Clique_Exporter(E:Event):void {
	if (true || EstAuteur || CodeCarte.text == "Nouvelle") {
		visible = false;
		MovieClip(parent)._Exportation.Initialisation(Formatage_Carte().toString());
	} else {
		Code.text = "Impossible d'effectuer cette action car vous n'êtes pas l'auteur de la carte.";	
	}
}

Code.addEventListener(KeyboardEvent.KEY_DOWN, Clavier_Code);
function Clavier_Code(E:KeyboardEvent):void {
	if (E.keyCode == 13) {
		Clique_Charger(null);
	}
}

