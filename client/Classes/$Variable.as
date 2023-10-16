package {
	public class $Variable {
		public var Liste:Array;
		public var Taille:int;
		public var Index:int;
		public var IndexImpossible:int;
		public var Fonction:Function;
		public function $Variable(VARIABLE:Object, TAILLE:int = 100, FONCTION:Function = null) {
			if (TAILLE<10) {
				TAILLE=10;
			}
			Fonction=FONCTION;
			Liste=new Array(TAILLE);
			Taille=TAILLE;
		}

		public function set Valeur(VARIABLE:Object) {
			Nouvelle_Index();
			Liste[Index]=VARIABLE;
		}

		public function get Valeur():Object {
			if (Index==IndexImpossible&&Fonction!=null) {
				Fonction();
			}
			return Liste[Index];
		}

		private function Nouvelle_Index():void {
			var NouvelleIndex:int=Math.random()*Taille;
			while (Index == NouvelleIndex) {
				NouvelleIndex=Math.random()*Taille;
			}
			IndexImpossible=Index;
			Index=NouvelleIndex;
		}
	}
}