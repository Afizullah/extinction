package  {
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.display.MovieClip;
	import flash.utils.Timer;
	import flash.events.TimerEvent;
	import flash.events.Event;
	import flash.text.TextFieldAutoSize;

	public class $Neige extends Sprite {
		private var Flocons:Vector.<TextField>;
		private var DeplFlocons:Vector.<int>;
		private var _root:MovieClip;
		private var TimerChute:Timer = null;
		private var NChute:int; // Indique le nombre de fois que la neige a chutée
		
		public function $Neige(NOMBRE:int, ROOT:MovieClip, SYMBOLE:String='*', COULEUR:int=0xEEEEEE, MINSZ:int=15)  {
			Flocons = new Vector.<TextField>();
			DeplFlocons = new Vector.<int>()
			_root = ROOT;
			NChute = 0;
			
			var PROGRESSION:int = 600 / (NOMBRE / 15);
			
			for (var i:int = 0; i < NOMBRE; ++i) {
				var Text:TextField = new TextField();
				var Format:TextFormat = new TextFormat();
				
				Format.size = Math.random() * 3 + MINSZ;
				Text.defaultTextFormat = Format;
				
				Text.text = SYMBOLE;
				Text.autoSize = TextFieldAutoSize.LEFT;
				Text.selectable = false;
				
				Text.y = 0 - (PROGRESSION) * (int)(i / 10) + (int)(Math.random() * 30 - 15);
				
				// (int)(800 / 10) = 80
				
				Text.x = 7 + 80 * (int)(i % 10) + (int)(Math.random() * 80 - 40);
				
				Text.textColor = COULEUR;
				
				Text.mouseEnabled = false;
				
				Flocons.push(Text);
				DeplFlocons.push(0);
				_root.addChild(Text);
			}
			
			if (TimerChute == null) {
				TimerChute = new Timer(100);
			}
			TimerChute.addEventListener(TimerEvent.TIMER, Neiger);
			TimerChute.start();
		}
		
		public function ClearNeige():void {
			
			if (Flocons == null) {
				return;
			}
			
			TimerChute.stop();
			TimerChute.reset();
			removeEventListener(TimerEvent.TIMER, Neiger);
			TimerChute = null;
			
			var NOMBRE:int = Flocons.length;
			
			for (var i:int = 0; i < NOMBRE; ++i) {
				_root.removeChild(Flocons[i]);
				var Txt:TextField = Flocons[i];
				Txt = null;
				Flocons[i] = null;
			}
			Flocons = null;
		}
		
		private function Neiger(E:Event):void {
			var NOMBRE:int = Flocons.length;
		
			for (var i:int = 0; i < NOMBRE; ++i) {
				if (NChute % 5 == 0) {
					DeplFlocons[i] = (int)(Math.random() * 4 - 2);
				}
				
				var Txt:TextField = Flocons[i];
				Txt.y += 5.0;
				Txt.x += DeplFlocons[i];
				
				if (Txt.y > 600) {
					Txt.y -= 600.0;
				}
			}
			NChute++;
		}
	}
	
}
