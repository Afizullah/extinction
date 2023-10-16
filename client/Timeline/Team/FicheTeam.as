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

visible = false;
var _root:MovieClip = MovieClip(parent);
var $:String = String.fromCharCode(2);


var MessageTeam:String = "";

_root.Navigateur(Ascenseur2, _MembresTeam._MembresTeam2, 325, 325, 50, _MembresTeam);
		
function Initialisation(Info:Array):void {
	_root.Inaction = 0;
	
	// on rétablit la souris si besoin
	if (_root._Forteresse.visible) {
		_root._Forteresse.SourisVisible=true;
		Mouse.show();
		_root._Forteresse._Réticule.visible=false;
	}

	visible = true;
	Fondateur.text = Info[6];
	var DateCrea : String = _root.dateFormat(Info[4]);
	Jeu.text = Info[7];
	Site.text = Info[3];
	DateModif.text = _root.dateFormat(Info[5]);
	Description.text = Info[2];
	MessageTeam = Info[9];
	var DateOffi:String = _root.dateFormat(Info[10]);
	if (DateOffi!=DateCrea) {
		DateCreation.text = DateOffi + " (" + DateCrea + ")";		
	} else {
		DateCreation.text = DateCrea;
	}
	Nom.text = Info[0];
	var Tag:String = Info[1];
	if (Tag!="-"){
		Nom.text = Nom.text + " ("+Tag+")";
	}
	Commande.text = "Tapez /fiche "  +Info[8]+" pour réouvrir cette fiche.";
	
	// nettoyage
	while (Recompenses.numChildren != 0) {
		Recompenses.removeChildAt(0);
	}	
	// récompenses spéciales	
	var InfosRecomp : String = Info[11];
	var LsRecomps:Array=InfosRecomp.split($+$);
	for (var k:int = 1; k<LsRecomps.length; k++) {	
		var UneRecomp:Array=LsRecomps[k].split($);
		var ImageRecompense : String = UneRecomp[0];
		var Recompense : MovieClip = _root.NouvelleRecompense(ImageRecompense);
		Recompenses.addChild(Recompense);
		Recompense.x = 0;		
		Recompense.y = 20*k;
		_root.Vignette(Recompense, UneRecomp[1]);		
	}

}



QuitterFenetre.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
Fermer.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Fermer);
function Clique_Fermer(E:Event):void {
	visible = false;
}

function Clique_Nom(E:TextEvent):void {
	var Identifiant:String = E.text;
	_root.Commandes("profil "+Identifiant); 
}

var ClipListeMembres:MovieClip = _MembresTeam._MembresTeam2;
var ListeMembres:Array;
var IdTeamFiche : Number;

function Initialisation_Membres(FicheTeam: Number, INFO:String):void {
	IdTeamFiche = FicheTeam;
	// on est chef de la team
	Dissoudre.visible = (_root.MODOTEAM||(_root.LEADER&&_root.IdTeam==FicheTeam));
    EditerSite.visible = (Dissoudre.visible || (_root.SCRIBE && _root.IdTeam==FicheTeam));
    EditerDescription.visible = EditerSite.visible;
	EditerMessage.visible = EditerSite.visible;
		
	// on est membre de la team
	Quitter.visible = (_root.IdTeam==FicheTeam);
	

	while (ClipListeMembres.numChildren != 0) {
		ClipListeMembres.removeChildAt(0);
	}

	ListeMembres = new Array();

	var LsMembres:Array=INFO.split($+$);

	for (var k:int = 1; k<LsMembres.length; k++) {
		var InfoMembre:Array=LsMembres[k].split($);
		var Memb:MovieClip = new $Membre();
		Memb.Fond.gotoAndStop(1);
		//Memb.Supprimer.visible=false;
		Memb.IdJoueur = InfoMembre[0];
		var Nom:String=InfoMembre[1];

		Memb.NomMembre.addEventListener(TextEvent.LINK, Clique_Nom);

		Memb.NomMembre.htmlText= "<a href='event:"+Nom+"'>" + Nom.substr(0,1).toUpperCase()+Nom.substr(1)+"</a>";

		if (Nom.toLowerCase()!=_root.NomJoueur.toLowerCase() && (_root.MODOTEAM||((_root.RECRUTEUR||_root.LEADER)&&_root.IdTeam==FicheTeam))) { // on est recruteur de la team
			Memb.Supprimer.addEventListener(MouseEvent.CLICK, Supprimer_Membre);
			_root.Vignette(Memb.Supprimer, "Ejecter le membre de la team");			
			Memb.Supprimer.buttonMode=true;
		}else{
			Memb.Supprimer.visible = false;
		}
		if (_root.MODOTEAM||(_root.LEADER&&_root.IdTeam==FicheTeam)) { // on est chef de la team
			Memb.Editer.addEventListener(MouseEvent.CLICK, Editer_Role);
			_root.Vignette(Memb.Editer, "Changer le rôle du membre");						
			Memb.Editer.buttonMode=true;
		}else{
			Memb.Editer.visible = false;
		}		
		ListeMembres.push(Memb);

		ClipListeMembres.addChild(Memb);
		Memb.x=15;
		
		var Grade:MovieClip;
		var Role:MovieClip;		
		Role = _root.getRole(InfoMembre[3]);		
		if (Role != null) {
			Memb.addChild(Role);
			Role.x = 40;		
			_root.Vignette(Role, _root.NomRole(InfoMembre[3]));			
			
		}
		
		Grade = _root.getGrade(InfoMembre[2]);
		var TexteGrade : String = "Grade "+InfoMembre[2];
		if (Grade != null) {
			Memb.addChild(Grade);
			Grade.x = 60;		
			if (_root.MODOTEAM||(_root.LEADER&&_root.IdTeam==FicheTeam)){
				Grade.buttonMode = true;
				Grade.addEventListener(MouseEvent.CLICK, Editer_Grade);
				TexteGrade = TexteGrade + " - Modifier";
			}
			_root.Vignette(Grade, TexteGrade);			
		}	
		
	}

	MAJ_ListeMembres();
}

function MAJ_ListeMembres() {

	var i:int;
	for (i=0; i<ListeMembres.length; i++) {
		ListeMembres[i].y=18*i;
	}


	_root.Ascenseur_Reset(Ascenseur2);

}


function Supprimer_Membre(E:Event):void {
	var LeJoueur:MovieClip=E.currentTarget.parent;
	var Nom:String=LeJoueur.NomMembre.text;	
	_root.Commandes("team quitter "+Nom);
	var index:int;
	if ((index = ListeMembres.indexOf(LeJoueur)) != -1) {
		ListeMembres.splice(index,1);
	}
	ClipListeMembres.removeChild(LeJoueur);
	MAJ_ListeMembres();
}

function Editer_Grade(E:Event):void {
	var LeJoueur:MovieClip=E.currentTarget.parent;
	var Nom:String=LeJoueur.NomMembre.text;
	EditGrade.Init(Nom); 
}

function Editer_Role(E:Event):void {
	var LeJoueur:MovieClip=E.currentTarget.parent;
	var Nom:String=LeJoueur.NomMembre.text;
	EditRole.Init(Nom); 
}

Quitter.visible = false;
Quitter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Quitter);
function Clique_Quitter(E:Event):void {
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir quitter votre team ?",P_Quitter,null);	
}

function P_Quitter(v:MovieClip):void {
	_root.Commandes("team quitter");
}

Dissoudre.visible = false;
Dissoudre.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Dissoudre);
function Clique_Dissoudre(E:Event):void {
	var Popup:MovieClip = new $Confirmation();
	Popup.Init(this,"Etes-vous sûr de vouloir dissoudre la team ?",P_Dissoudre,null);
}


function P_Dissoudre(v:MovieClip):void {
	_root.Commandes("team dissoudre "+IdTeamFiche);
}


_root.Vignette(EditerSite, "Editer le site");
EditerSite.visible = false;
EditerSite.addEventListener(MouseEvent.MOUSE_DOWN, Clique_EditerSite);
function Clique_EditerSite(E:Event):void {
	EditSite.Init(Site.text,IdTeamFiche); 	
}

_root.Vignette(EditerDescription, "Editer la description");
EditerDescription.visible = false;
EditerDescription.addEventListener(MouseEvent.MOUSE_DOWN, Clique_EditerDescription);
function Clique_EditerDescription(E:Event):void {
	EditDescription.Init(Description.text,IdTeamFiche); 	
}

_root.Vignette(EditerMessage, "Editer le message d'accueil");
EditerMessage.visible = false;
EditerMessage.addEventListener(MouseEvent.MOUSE_DOWN, Clique_EditerMessage);
function Clique_EditerMessage(E:Event):void {
	EditMessage.Init(MessageTeam,IdTeamFiche); 	
}

MembresOnline.addEventListener(MouseEvent.MOUSE_DOWN, Clique_MembresOnline);
function Clique_MembresOnline(E:Event):void {
	//visible = false;
	//_root._ListeTeam.visible = false;
	_root.Commandes("membres " + IdTeamFiche);
}

Historique.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Historique);
function Clique_Historique(E:Event):void {
	_root.Commandes("historique " + IdTeamFiche);
}

_root.Vignette(Actualiser, "Actualiser la fiche");
Actualiser.buttonMode=true;
Actualiser.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Actualiser);
function Clique_Actualiser(E:Event):void {
	_root.Commandes("fiche " + IdTeamFiche);
}
