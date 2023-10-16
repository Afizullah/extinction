package  {
	import flash.display.Sprite;
	import flash.utils.getTimer;
	import flash.display.MovieClip;
	import flash.display.Shape;
	import flash.utils.Timer;
	import flash.events.TimerEvent;
	import flash.events.Event;
	
	public class $FeuxArtifice extends Sprite {
		private var _root:MovieClip;
		private var StartProgression:int;
		private var Couleur:int;
		private var ListeLignes:Array;
		private var LongMaxFaisceaux:int = 10; // (px)
		private var TimerFeu:Timer = new Timer(70);
		private var X:int;
		private var Y:int;
		
		public function $FeuxArtifice(ROOT:MovieClip, COULEUR:int, _X:int, _Y:int) {
			// constructor code
			Couleur = COULEUR;
			_root = ROOT;
			
			ListeLignes = new Array();
			
			X = _X;
			Y = _Y;
			
			TimerFeu.addEventListener(TimerEvent.TIMER, Feu);
		}
		
		public function Lancer():void {
			StartProgression = getTimer();
			TimerFeu.start();
		}
		
		public function Feu(E:Event):void {
			/*
			 Vitesse : 30 px/s -> 0.03 px/ms
			 Durée de vie : 3 s
			 Nombre de faisceaux : 40
			 Longueur max faisceaux : 10 px
			*/
			
			if (getTimer() - StartProgression > 3000) {
				TimerFeu.stop();
				
				while (ListeLignes.length != 0) {
					_root.removeChild(ListeLignes[0]);
					ListeLignes.shift();
				}
				return;
			}
			
			var Distance:int = (getTimer() - StartProgression) * 3 / 100;
			var NombreDivisions:int;
			var j:int = 0;
			var Ligne:Shape;
			
			if (Distance < 10) {
				NombreDivisions = Distance % LongMaxFaisceaux;
			} else {
				NombreDivisions = 10;
			}
			
			var ProgressionX:Number;
			var ProgressionY:Number;
			var AdditifX:Number = 0;
			var AdditifY:Number = 0;
			var cos:Number;
			var sin:Number;
			var DistAdditif:int = (Distance - 10);
			
			var moveToX:int;
			var moveToY:int;
			
			var i:int;
			
			for(var angle:int = 0; angle < 360; angle += 360/40) {
				cos = Math.cos(angle);
				sin = Math.sin(angle);
								
				ProgressionX = cos;
				ProgressionY = sin;
				
				if (Distance > 10) {
					AdditifX = cos * DistAdditif;
					AdditifY = sin * DistAdditif;
				} else {
					AdditifX = 0;
					AdditifY = 0;
				}
				
				moveToX = X + AdditifX;
				moveToY = Y + AdditifY;
				Ligne = new Shape();
						
				Ligne.graphics.lineStyle(1, Couleur, 1);
					
				Ligne.graphics.moveTo(moveToX, moveToY);
										  
				Ligne.graphics.lineTo(moveToX + ProgressionX * NombreDivisions
									, moveToY + ProgressionY * NombreDivisions);
				
				if (ListeLignes.length <= j) {
					ListeLignes.push(Ligne);
				} else {
					_root.removeChild(ListeLignes[j]);
					ListeLignes[j] = Ligne;
				}
				_root.addChild(Ligne);
				++j
			}
			
		}

	}
	
}
