var _root:MovieClip = MovieClip(parent.parent);
visible = false;
var Carte:XMLNode;
Code.visible = false;
var Autorisation: Boolean = false;

InfoCarte.NomCarte.restrict = "A-Za-z0-9 ";
InfoCarte.NomAuteur.restrict = "A-Za-z0-9 ";
InfoCarte.TypeMap.Offi.visible = false;

function Initialisation(CARTE:String):void {
	InfoCarte.NomAuteur.text = _root.NomJoueur;
	Code.visible = false;
	Carte = new XMLDocument(CARTE).firstChild;
	Carte.attributes.V = "";
	if (Carte.toString() == MovieClip(parent)._Editeur.DernierTest.toString()) {
		InfoCarte.NomCarte.text = MovieClip(parent)._Editeur.NomCarte;
		Erreur.visible = false;
		InfoCarte.visible = true;
	} else {
		InfoCarte.visible = false;
		Erreur.visible = true;
	}
	visible = true;
	if (!Autorisation) {
		InfoCarte.Mode.visible = false;
		InfoCarte.TypeMap.visible = false;
	}
}

InfoCarte.Exporter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Exporter);
function Clique_Exporter(E:Event):void {
	var Flag : String = (Autorisation ? InfoCarte.TypeMap.GetFlag():"0");	
	if ((Flag == "3" || Flag == "2"|| Flag == "6")&&MovieClip(parent)._Editeur.DernierTestGuidage) {
		InfoCarte.NomCarte.text = "A tester sans guide";
		return;
	}
	if (InfoCarte.NomAuteur.text == "" || InfoCarte.NomCarte.text == "" || InfoCarte.NomCarte.text.length > 15) {
		return;
	}
	Carte.attributes.N = InfoCarte.NomCarte.text;
	Carte.attributes.A = ""; //InfoCarte.NomAuteur.text;
	//
	InfoCarte.visible = false;
	_root._FenetreMaps.InitEditeur = false;			
	_root.Envoie_Serveur("EdS#"+Carte+"#"+InfoCarte.NomCarte.text+"#"+Flag);
}

Retour.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Retour);
function Clique_Retour(E:Event):void {
	visible = false;
	MovieClip(parent)._Editeur.visible = true;
}