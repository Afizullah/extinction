package {
	import flash.display.Sprite;
	import flash.utils.getTimer;
	import flash.events.MouseEvent;
	import flash.system.System;
	import flash.text.TextField;
	import flash.profiler.*;
	public class $IPS extends Sprite {
		private var TempsZero:int;
		private var Largeur:int;
		private var Hauteur:int;
		private var ListeCouleur:Array = new Array(0x6C77C1,0x009D9D,0xCB546B);
		private var Graphique:Sprite = new Sprite();
		private var Cadre:Sprite = new Sprite();
		private var ListeCourbe:Array = new Array();
		private var CourbeIPS:Array = new Array();
		private var Decalage:Array = new Array();
		private var CourbeSpéciale:Array;
		private var DecalageX:int;
		private var DecalageY:int;
		private var Mémoire:TextField = new TextField();
		private var TexteIPS:TextField = new TextField();
		private var NbImage:int = 0;
		private var TempsImage:int = 0;
		private var ISBase:int;
		private var Retracage:Boolean = false;

		public function $IPS(L:int, H:int, IS:int) {
			ISBase = IS;
			Largeur = L;
			Hauteur = H;
			//
			mouseChildren = false;
			addChild(Graphique);
			//
			graphics.beginFill(0, 0.8);
			graphics.drawRoundRect(-1, 0, Largeur, Hauteur+2, 10, 10);
			graphics.endFill();
			//
			Cadre.addChild(Mémoire);
			Mémoire.x = 5;
			Mémoire.y = Hauteur - 20;
			Mémoire.selectable = false;
			Mémoire.multiline = false;
			Mémoire.width = 50;
			Mémoire.height = 22;
			Mémoire.thickness = 200;
			Cadre.addChild(TexteIPS);
			TexteIPS.x = 10;
			TexteIPS.selectable = false;
			TexteIPS.multiline = false;
			TexteIPS.width = 50;
			TexteIPS.height = 22;
			TexteIPS.thickness = 200;
			//
			addChild(Cadre);
			Cadre.graphics.lineStyle(2, 0, 1, true);
			Cadre.graphics.beginFill(0, 0.7);
			Cadre.graphics.drawRoundRect(-1, 0, 40, Hauteur+2, 10, 10);
			Cadre.graphics.endFill();
			Cadre.graphics.lineStyle(1, 0xB84DD2, 0.8, true);
			Cadre.graphics.moveTo(0, Hauteur-Math.ceil(1000 / IS));
			Cadre.graphics.lineTo(Largeur, Hauteur-Math.ceil(1000 / IS));
			Cadre.graphics.lineStyle(2, 0, 1, true);
			Cadre.graphics.drawRoundRect(-1, 0, Largeur, Hauteur+2, 10, 10);
			//
			Graphique.graphics.lineStyle(1);
			//
			addEventListener(MouseEvent.MOUSE_DOWN, Clique);
		}

		public function Nouvelle_Valeur(COURBE:int, BASE:Boolean = false):void {
			var Valeur:int = getTimer();
			if (COURBE == -1) {
				if (Valeur - TempsImage > 1000) {
					TempsImage += 1000;
					if (NbImage<ISBase*0.6) {
						TexteIPS.htmlText="<font face='Verdana' size='10' color='#F83F43' >"+NbImage;
					} else if (NbImage < ISBase*0.8) {
						TexteIPS.htmlText="<font face='Verdana' size='10' color='#F7DB40' >"+NbImage;
					} else {
						TexteIPS.htmlText="<font face='Verdana' size='10' color='#2F7FCC' >"+NbImage;
					}
					NbImage=0;
				}
				NbImage++;
				//
				if (TempsZero==0) {
					TempsZero=Valeur;
				} else {
					CourbeIPS.push(Valeur-TempsZero);
					TempsZero=Valeur;
					if (CourbeIPS.length>Largeur) {
						CourbeIPS.shift();
					}
				}
			} else {
				var Courbe:Array=ListeCourbe[COURBE];
				if (Courbe==null) {
					Courbe = new Array();
					ListeCourbe[COURBE]=Courbe;
				}
				if (BASE) {
					Courbe.push(Valeur);
					if (Courbe.length>Largeur) {
						Courbe.shift();
					}
				} else {
					var ValeurCible:int=Courbe.length-1;
					Courbe[ValeurCible]=Valeur-Courbe[ValeurCible];
				}
			}
		}

		public function Courbe_Spéciale(VALEUR:int):void {
			if (! CourbeSpéciale) {
				CourbeSpéciale = new Array();
			}
			CourbeSpéciale.push(VALEUR);
			if (CourbeSpéciale.length>Largeur) {
				CourbeSpéciale.shift();
			}
		}

		public function Rendu():void {
			Mémoire.htmlText="<font face='Verdana' size='10' color='#6C77C1' >"+int(System.totalMemory/100000)/10;
			Graphique.graphics.clear();
			Graphique.graphics.lineStyle(1, 0xFFFFFF,1,true);
			var CodeCouleur:int=0;
			//Graphique.graphics.beginFill(0x143858);
			Tracage_Courbe(CourbeIPS);
			//Graphique.graphics.endFill();
			//
			var NbCourbe:int=ListeCourbe.length;
			for (var c:int = 0; c<NbCourbe; c++) {
				Graphique.graphics.lineStyle(1, ListeCouleur[c], 1, true);
				if (c==0) {
					Tracage_Courbe(ListeCourbe[c], false, true);
				} else {
					Tracage_Courbe(ListeCourbe[c], true);
				}
			}
			//
			if (CourbeSpéciale) {
				Graphique.graphics.lineStyle(1, 0xED67EA, 1, true);
				Tracage_Courbe(CourbeSpéciale);
			}
		}

		private function Tracage_Courbe(COURBE:Array, DECALAGE:Boolean = false, INIT_DECALAGE:Boolean = false):void {
			var Nb:int=COURBE.length;
			if (Nb>0) {
				Graphique.graphics.moveTo(0, Hauteur-COURBE[0]);
				//
				for (var i:int = 0; i<Nb; i++) {
					var Valeur:int=COURBE[i];
					if (INIT_DECALAGE) {
						Decalage[i]=Valeur;
						Graphique.graphics.lineTo(i, Hauteur-Valeur);
					} else if (DECALAGE) {
						Decalage[i]+=Valeur;
						Graphique.graphics.lineTo(i, Hauteur-Decalage[i]);
					} else {
						Graphique.graphics.lineTo(i, Hauteur-Valeur);
					}
				}
			}
		}

		private function Clique(E:MouseEvent):void {
			if (mouseX<40) {
				Retracage=! Retracage;
				showRedrawRegions(Retracage, 0x0044BB);
			} else {
				DecalageX=mouseX;
				DecalageY=mouseY;
				stage.addEventListener(MouseEvent.MOUSE_MOVE, Boucle_Souris);
				stage.addEventListener(MouseEvent.MOUSE_UP, Declique);
			}
		}
		private function Declique(E:MouseEvent):void {
			stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Souris);
			stage.removeEventListener(MouseEvent.MOUSE_UP, Declique);
		}
		private function Boucle_Souris(E:MouseEvent):void {
			x=stage.mouseX-DecalageX;
			y=stage.mouseY-DecalageY;
			E.updateAfterEvent();
		}
	}
}