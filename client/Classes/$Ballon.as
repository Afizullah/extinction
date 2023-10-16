package  {
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.display.MovieClip;
	import flash.utils.Timer;
	import flash.events.TimerEvent;
	import flash.events.Event;
	import flash.text.TextFieldAutoSize;
	import flash.geom.ColorTransform;

	public class $Ballon extends Sprite {
		private var Ballons:Vector.<$BallonAnnif>;
		private var DeplBallons:Vector.<int>;
		private var _root:MovieClip;
		private var TimerEnvol:Timer = null;
		private var NEnvol:int; // Indique le nombre de fois que les ballons se sont envolés
		private const DECALAGE_GAUCHE:int = 200
		
		public function $Ballon(NOMBRE:int, ROOT:MovieClip)  {
			Ballons = new Vector.<$BallonAnnif>();
			DeplBallons = new Vector.<int>()
			_root = ROOT;
			NEnvol = 0;
			
			var Y:int = 700;
			
			for (var i:int = 0; i < NOMBRE * 3; ++i) {
				var Ballon:$BallonAnnif = new $BallonAnnif();
				
				Ballon.x = int(1200 / (NOMBRE + 1) * ((i % NOMBRE) + 1)) - DECALAGE_GAUCHE;
				Ballon.y = Y - (Math.random() * 170 * (i % 2));
				
				/*var nouvCouleur:ColorTransform = new ColorTransform();
				nouvCouleur.color = RandomColeur();
				Ballon.transform.colorTransform = nouvCouleur;*/
				Ballon.SetRandomCouleur();
				
				Ballons.push(Ballon);
				DeplBallons.push(0);
				_root.addChild(Ballon);
				Ballon.SetRandomCouleur();
				
				if (i == NOMBRE - 1) {
					Y = 500;
				} else if (i == NOMBRE * 2 - 1) {
					Y = 200;
				}
			}
			
			if (TimerEnvol == null) {
				TimerEnvol = new Timer(150);
			}
			TimerEnvol.addEventListener(TimerEvent.TIMER, LancerBallons);
			TimerEnvol.start();
		}
		
		public function ClearBallons():void {
			
			if (Ballons == null) {
				return;
			}
			
			TimerEnvol.stop();
			TimerEnvol.reset();
			removeEventListener(TimerEvent.TIMER, LancerBallons);
			TimerEnvol = null;
			
			var NOMBRE:int = Ballons.length;
			
			for (var i:int = 0; i < NOMBRE; ++i) {
				_root.removeChild(Ballons[i]);
				var Ballon:$BallonAnnif = Ballons[i];
				Ballon = null;
				Ballons[i] = null;
			}
			Ballons = null;
		}
		
		private function LancerBallons(E:Event):void {
			var NOMBRE:int = Ballons.length;
		
			for (var i:int = 0; i < NOMBRE; ++i) {
				if (NEnvol % 4 == 0) {
					DeplBallons[i] = (int)(Math.random() * 4 - 2);
				}
				
				var Ballon:$BallonAnnif = Ballons[i];
				Ballon.y -= 3.0;
				Ballon.x += DeplBallons[i];
				
				Ballon.x = Math.min(Math.max((1200 / (int)(NOMBRE / 3 + 1)) * (int)(i % (NOMBRE / 3)) - DECALAGE_GAUCHE, Ballon.x), (1200 / (int)(NOMBRE / 3 + 1)) * ((int)(i % (NOMBRE / 3)) + 1) - DECALAGE_GAUCHE);
				
				if (Ballon.y <= -100) {
					Ballon.y += 700.0;
					/*var nouvCouleur:ColorTransform = new ColorTransform();
					nouvCouleur.color = RandomColeur();
					Ballon.transform.colorTransform = nouvCouleur;*/
					Ballon.SetRandomCouleur();
				}
			}
			NEnvol++;
		}
	}
	
}
