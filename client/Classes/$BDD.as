package {
	import flash.display.Sprite;
	import flash.utils.getTimer;
	import flash.events.MouseEvent;
	import flash.system.System;
	import flash.text.TextField;
	public class $BDD extends Sprite {
		private var TempsZero:int;
		private var Largeur:int;
		private var Hauteur:int;
		private var Graphique:Sprite = new Sprite();
		private var Cadre:Sprite = new Sprite();
		private var ListeValeur:Array = new Array();
		private var DecalageX:int;
		private var DecalageY:int;
		public function $BDD(L:int, H:int) {
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
			addChild(Cadre);
			Cadre.graphics.lineStyle(2, 0, 1, true);
			//Cadre.graphics.beginFill(0, 0.7);
			//Cadre.graphics.drawRoundRect(-1, 0, 40, Hauteur+2, 10, 10);
			//Cadre.graphics.lineStyle(2, 0, 1, true);
			Cadre.graphics.drawRoundRect(-1, 0, Largeur, Hauteur+2, 10, 10);
			//Cadre.graphics.endFill();
			//
			Graphique.graphics.lineStyle(1);
			//
			addEventListener(MouseEvent.MOUSE_DOWN, Clique);
		}

		public function Nouvelle_Valeur(VALEUR:int):void {
			ListeValeur.push(VALEUR);
			if (ListeValeur.length > 200) {
				ListeValeur.shift();
			}
			Rendu();
		}

		public function Rendu():void {
			Graphique.graphics.clear();
			Graphique.graphics.lineStyle(1, 0x009D9D,1,true);
			var CodeCouleur:int = 0;
			//
			Tracage_Courbe(ListeValeur);
		}

		private function Tracage_Courbe(COURBE:Array):void {
			var Nb:int = COURBE.length;
			if (Nb > 0) {
				Graphique.graphics.moveTo(0, Hauteur-COURBE[0]);
				//
				for (var i:int = 0; i<Nb; i++) {
					var Valeur:int = COURBE[i];
					Graphique.graphics.lineTo(i, Hauteur-Valeur);
				}
			}
		}

		private function Clique(E:MouseEvent):void {
			DecalageX = mouseX;
			DecalageY = mouseY;
			stage.addEventListener(MouseEvent.MOUSE_MOVE, Boucle_Souris);
			stage.addEventListener(MouseEvent.MOUSE_UP, Declique);
		}
		private function Declique(E:MouseEvent):void {
			stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Souris);
			stage.removeEventListener(MouseEvent.MOUSE_UP, Declique);
		}
		private function Boucle_Souris(E:MouseEvent):void {
			x = stage.mouseX - DecalageX;
			y = stage.mouseY - DecalageY;
			E.updateAfterEvent();
		}
	}
}