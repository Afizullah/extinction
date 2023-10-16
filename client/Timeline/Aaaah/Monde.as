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

var ListeLigne:Array = new Array();
var ListeTimer:Array = new Array();
var PosX:Number;
var PosY:Number;
var TempsLigne:int = 10000;
var ProjectileEnCours:MovieClip;
var TempsDepart:int = 14;
var TempsDebut:int;
var Monde:MovieClip;
var PremierJoueur:Boolean = false;
var JoueAuMoinsUneFois:Boolean = false;
var SalonEnCours:String = "";
var ArriveeCompteur:int = 1;
var VoteEnCours:Boolean = false;
var SalonFight:Boolean = false;
var RallyPublic:Boolean = false;

var MembreModeration:Boolean;

var JoueurDepart:int = 0;
var JoueurEnVie:int = 0;

_Monde._Dessin.mouseEnabled = false;
_Monde._Dessin.mouseChildren = false;

var TimerDessin:Timer = new Timer(50);
TimerDessin.addEventListener(TimerEvent.TIMER, Boucle_Trace);

var TimerDepart:Timer = new Timer(1000);
TimerDepart.addEventListener(TimerEvent.TIMER, Depart_1);

var TimerVote:Timer = new Timer(8000);
TimerVote.addEventListener(TimerEvent.TIMER, Fin_Vote);

_Monde.Porte.Retour.visible = false;
_Monde.Porte.Pharma.visible = false;
_Monde.Porte.Tombe.visible = _root.Halloween;
_Monde.Porte.Sapin.visible = _root.Noel;
_Monde.Porte.Pharma10Ans.visible = false;
_Monde.Porte.PharmaCDM.visible = false;
_Monde.Drapeau.visible = false;
_Monde.Drapeau.alpha = 0.3;
_Monde.Drapeau.Joueur.alpha = 1;
_Monde.Drapeau.NomJoueur = "";
_Monde._ListeJoueur.visible = false;
_Monde._Dessin.visible = false;
_Monde.Porte.visible = false;
_Monde.Flash.visible = false;
_Monde.Vote.visible = false;
_Editeur.visible = false;
_Monde.Info.Texte.htmlText = "La partie d'affichera dans <font color='#009D9D'>" + TempsDepart + "</font> secondes.";

function Actualiser_Pharma():void {
	_Monde.Porte.Pharma.visible = false;
	
	_Monde.Porte.Tombe.visible = _root.Halloween;
	if (_root.Noel) {
		_Monde.Porte.Sapin.visible = true;
	} else {
		_Monde.Porte.Pharma.visible = true;
	}
}

/**
 * Chargement map non-offi
 */
function Chargement_Monde(MONDE:String,AUTEUR:String):void {
	ListeMouve = new Array();
	if (Monde != null) {
		if (Monde.ResetMod) {
			Monde.Reset();
		}
		_Monde._Dessin.removeChild(Monde);
	}
	//
	Monde = new MovieClip();
	var MondeXML:XMLNode = new XMLDocument(MONDE).firstChild;
	_Editeur.Moteur_Graphique(Monde, MondeXML);
	_Monde._Dessin.addChild(Monde);
	//
	_Monde.Porte.Salon.htmlText = "Carte : <font color='#6C77C1'>" + MondeXML.attributes.N + "</font>\nAuteur : <font color='#6C77C1'>" + AUTEUR;
	
	Actualiser_Pharma();
}

/**
 * Chargement map offi
 */
function Changement_Monde(NumMonde:int, InfoSupp:String):void {
	if (_root.H != loaderInfo.bytes.toString().toUpperCase()) {
		_root.Serveur.close();
		return;
	}
	_Monde.Porte.Salon.htmlText = "<font color='#6C77C1'>" + SalonEnCours;
	//
	ListeMouve = new Array();
	if (Monde != null) {
		if (Monde.ResetMod) {
			Monde.Reset();
		}
		_Monde._Dessin.removeChild(Monde);
	}
	//
	Monde = Liste_Monde(NumMonde, InfoSupp);
	//
	_Monde._Dessin.addChild(Monde);
	
	Actualiser_Pharma();
}

function Liste_Monde(NumMonde:int, InfoSupp:String):MovieClip {
	var Monde:MovieClip;
		
	if (NumMonde == 0) {
		Monde = new $Monde0();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 1) {
		Monde = new $Monde1();
		Monde.M1.RotationInverse = true;
		Monde.M2.RotationInverse = true;
		ListeMouve.push(Monde.M1, Monde.M2);
		return Monde;
	}
	if (NumMonde == 2) {
		Monde = new $Monde2();
		return Monde;
	}
	if (NumMonde == 3) {
		Monde = new $Monde5();
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 4) {
		Monde = new $Monde14();
		return Monde;
	}
	if (NumMonde == 5) {
		Monde = new $Monde15();
		return Monde;
	}
	if (NumMonde == 6) {
		Monde = new $Monde16();
		Monde.M1.Rotation = true;
		Monde.M2.RotationInverse = true;
		ListeMouve.push(Monde.M1, Monde.M2);
		return Monde;
	}
	if (NumMonde == 7) {
		Monde = new $Monde17();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 8) {
		Monde = new $Monde18();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 9) {
		Monde = new $Monde20();
		return Monde;
	}
	if (NumMonde == 10) {
		Monde = new $Monde21();
		return Monde;
	}
	if (NumMonde == 11) {
		Monde = new $Monde22();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 12) {
		Monde = new $Monde23();
		return Monde;
	}
	if (NumMonde == 13) {
		Monde = new $Monde24();
		return Monde;
	}
	if (NumMonde == 14) {
		Monde = new $Monde30();
		return Monde;
	}
	if (NumMonde == 15) {
		Monde = new $Monde31();
		return Monde;
	}
	if (NumMonde == 16) {
		Monde = new $Monde32();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 17) {
		Monde = new $Monde33();
		Monde.M1.Rotation = true;
		Monde.M2.RotationInverse = true;
		ListeMouve.push(Monde.M1);
		ListeMouve.push(Monde.M2);
		return Monde;
	}
	if (NumMonde == 18) {
		Monde = new $Monde34();
		Monde.M1.RotationInverse = true;
		Monde.M2.Rotation = true;
		Monde.M3.RotationInverse = true;
		Monde.M4.Rotation = true;
		Monde.M5.Rotation = true;
		ListeMouve.push(Monde.M1, Monde.M2, Monde.M3, Monde.M4, Monde.M5);
		return Monde;
	}
	if (NumMonde == 19) {
		Monde = new $Monde35();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 20) {
		Monde = new $Monde36();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 21) {
		Monde = new $Monde37();
		Monde.M1.Rotation = true;
		Monde.M3.Rotation = true;
		Monde.M4.RotationInverse = true;
		Monde.M5.Rotation = true;
		Monde.M6.RotationInverse = true;
		Monde.M7.Rotation = true;
		Monde.M8.RotationInverse = true;
		Monde.M9.Rotation = true;
		Monde.M10.RotationInverse = true;
		ListeMouve.push(Monde.M1, Monde.M3, Monde.M4, Monde.M5, Monde.M6, Monde.M7, Monde.M8, Monde.M9, Monde.M10);
		return Monde;
	}
	if (NumMonde == 22) {
		Monde = new $Monde38();
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 23) {
		Monde = new $Monde39();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 24) {
		Monde = new $Monde41();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 25) {
		Monde = new $Monde42();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 26) {
		Monde = new $Monde43();
		return Monde;
	}
	if (NumMonde == 27) {
		Monde = new $Monde44();
		return Monde;
	}
	if (NumMonde == 28) {
		Monde = new $Monde45();
		Monde.M1.RotationInverse = true;
		Monde.M2.RotationInverse = true;
		Monde.M3.RotationInverse = true;
		ListeMouve.push(Monde.M1, Monde.M2, Monde.M3);
		return Monde;
	}
	if (NumMonde == 29) {
		Monde = new $Monde46();
		return Monde;
	}
	if (NumMonde == 30) {
		Monde = new $Monde47();
		return Monde;
	}
	if (NumMonde == 31) {
		Monde = new $Monde48();
		return Monde;
	}
	if (NumMonde == 32) {
		Monde = new $Monde50();
		Monde.Mouvement = true;
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 33) {
		Monde = new $Monde51();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 34) {
		Monde = new $Monde54();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 35) {
		Monde = new $Monde55();
		return Monde;
	}
	if (NumMonde == 36) {
		Monde = new $Monde58();
		return Monde;
	}
	if (NumMonde == 37) {
		Monde = new $Monde61();
		return Monde;
	}
	if (NumMonde == 38) {
		Monde = new $Monde62();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 39) {
		Monde = new $Monde65();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 40) {
		Monde = new $Monde66();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 41) {
		Monde = new $Monde67();
		return Monde;
	}
	if (NumMonde == 42) {
		Monde = new $Monde68();
		Monde.M1.Rotation = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 43) {
		Monde = new $Monde71();
		return Monde;
	}
	if (NumMonde == 44) {
		Monde = new $Monde72();
		return Monde;
	}
	if (NumMonde == 45) {
		Monde = new $Monde74();
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 46) {
		Monde = new $Monde75();
		return Monde;
	}
	if (NumMonde == 47) {
		Monde = new $Monde79();
		return Monde;
	}
	if (NumMonde == 48) {
		Monde = new $Monde82();
		return Monde;
	}
	if (NumMonde == 49) {
		Monde = new $Monde83();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 50) {
		Monde = new $Monde84();
		return Monde;
	}
	if (NumMonde == 51) {
		Monde = new $Monde86();
		return Monde;
	}
	if (NumMonde == 52) {
		Monde = new $Monde87();
		return Monde;
	}
	if (NumMonde == 53) {
		Monde = new $Monde88();
		return Monde;
	}
	if (NumMonde == 54) {
		Monde = new $Monde89();
		return Monde;
	}
	if (NumMonde == 55) {
		Monde = new $Monde91();
		return Monde;
	}
	if (NumMonde == 56) {
		Monde = new $Monde92();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 57) {
		Monde = new $Monde94();
		Monde.ResetMod = true;
		Monde.M1.HorizontalInfinie = true;
		ListeMouve.push(Monde.M1);
		return Monde;
	}
	if (NumMonde == 58) {
		Monde = new $Monde95();
		Monde.Mouvement = true;
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 59) {
		Monde = new $Monde100();
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 60) {
		Monde = new $Monde101();
		Monde.ResetMod = true;
		return Monde;
	}
	if (NumMonde == 61) {
		Monde = new $Monde102();
		Monde.Mouvement = true;
		return Monde;
	}
	if (NumMonde == 62) {
		Monde = new $Monde103();
		Monde.ResetMod = true;
		return Monde;
	}
	
	if (NumMonde == 63) {
		Monde = new $Monde104();
		Monde.Mouvement = true;
		return Monde;
	}
	
	if (NumMonde == 64) {
		Monde = new $Monde105();
		Monde.Mouvement = true;
		Monde.GuidageRestreint = true;
		return Monde;
	}
	
	/*if (NumMonde == 65) { // Monde offi événement
		if (_root.Halloween) {
			Monde = new $MondeAHalloween();
			if (InfoSupp != null) {
				Monde.setInfoSupp(InfoSupp); // Broyeurs à activer
			}
			Monde.Mouvement = true;
			Monde.DepartSpecial = true;
			Monde.DepartX = 275;
			Monde.DepartY = 200;
			return Monde;
		}
	}*/
	return null;
}

function Activation_Trace():void {
	stage.addEventListener(MouseEvent.MOUSE_UP, Desactivation_Trace);
	stage.addEventListener(MouseEvent.MOUSE_DOWN, Lancement_Trace);
}

function Depart_1(E:Event, VITE:Boolean = false):void {
	_Monde.Info.Texte.htmlText = "La partie s'affichera dans <font color='#009D9D'>"+(TempsDepart-TimerDepart.currentCount)+"</font> secondes.";
	if (TimerDepart.currentCount == TempsDepart || VITE || _root.MODO || _root.ARBITRE) {
		TimerDepart.stop();
		//TimerDepart.removeEventListener(TimerEvent.TIMER, Depart_1);
		_root.Envoie_Serveur("IdM");
		//TimerMoteur.start();
		stage.addEventListener(Event.ENTER_FRAME, Boucle_Moteur);
		_Monde.Info.visible = false;
		_Monde._ListeJoueur.visible = true;
		_Monde._Dessin.visible = true;
		_Monde.Porte.visible = true;
	}
}


function Desactivation_Trace(E:Event):void {
	TimerDessin.stop();
	TimerDessin.reset();
}

function Lancement_Trace(E:Event) {
	if (_Monde.visible) {
		_root.Inaction = 0;
		//
		
		/*if (Monde.GuidageRestreint == true && !Monde.checkZoneGuidable(mouseX, mouseY)) {
			return;
		}*/
		
		
		var TraceX:int = mouseX;
		var TraceY:int = mouseY;
		_root.Envoie_Serveur("DeS#"+TraceX+"#"+TraceY);
		TimerDessin.start();
		PosX = TraceX;
		PosY = TraceY;
		_Editeur.DernierTestGuidage = true;
	}
}



function Boucle_Trace(E:Event):void {
	var TraceX:int = mouseX;
	var TraceY:int = mouseY;
	
	if (Tracage(TraceX, TraceY)) {
		_root.Envoie_Serveur("DeT#"+TraceX+"#"+TraceY);
	}
}

function Tracage(X:int, Y:int):Boolean {
	var Ligne:Shape = new Shape();
	if (_root.Halloween) {
		Ligne.graphics.lineStyle(4, 0xFF9900, 1, false);
	} else if (_root.Noel) {
		Ligne.graphics.lineStyle(4, 0xA7A7A7, 1, false);
	} else if (_root.Carnaval || _root.Nouvelan) {
		Ligne.graphics.lineStyle(4, (Math.round(Math.random() * 100) % 255) | ((Math.round(Math.random() * 100) % 255) << 8) | ((Math.round(Math.random() * 100) % 255) << 16), 1, false);
	} else if (_root.SaintValentin) {
		Ligne.graphics.lineStyle(4, 0xED67EA, 1, false);
	} else {
		if (Guide.RecompenseElo == 0) {
			Ligne.graphics.lineStyle(4, 0x000000, 1, false);
		} else {
			Ligne.graphics.lineStyle(4, CouleurFromElo(Guide.RecompensesElo), 1, false);
		}
		
	}
	
	
	if (PosX == X && PosY == Y) {
		return false;
	}
	
	if (Monde.GuidageRestreint == true) { // Monde avec des zones non guidables
		var pointDebutDedans:Boolean = Monde.checkZoneGuidable(PosX, PosY);
		var pointFinDedans:Boolean = Monde.checkZoneGuidable(X, Y)
		if (!pointDebutDedans && !pointFinDedans) {
			// Trait tracé hors des zones guidables
			PosX = X;
			PosY = Y;
			return false;
		} else { // Trait dans une zone guidable
			var point:Array = Monde.CorrigeTrait(PosX, PosY, X, Y);
			_Monde._Dessin.addChild(Ligne);
			
			if (point != null) { // On a touché un bord des zones guidables
				
				if (pointFinDedans) { // On est entré dans la zone guidable
					Ligne.graphics.moveTo(point[0], point[1]);
					Ligne.graphics.lineTo(X, Y);
				} else if (pointDebutDedans) { // On est sorti de la zone guidable
					Ligne.graphics.moveTo(PosX, PosY);
					Ligne.graphics.lineTo(point[0], point[1]);
				}
			} else {
				Ligne.graphics.moveTo(PosX, PosY);
				Ligne.graphics.lineTo(X, Y);
			}
			
			PosX = X;
			PosY = Y;
				
			ListeLigne.push(Ligne);
		}
	} else { // Traçage normal*/
		_Monde._Dessin.addChild(Ligne);
		Ligne.graphics.moveTo(PosX, PosY);
		Ligne.graphics.lineTo(X, Y);
		
		PosX = X;
		PosY = Y;
	
		ListeLigne.push(Ligne);
	}
	
	//
	//if (ListeLigne.length > 150) {
	//_Monde._Dessin.removeChild(ListeLigne.shift());
	//}
	//
	var Temps:Timer = new Timer(TempsLigne,1);
	Temps.addEventListener(TimerEvent.TIMER, Effacement_Ligne);
	Temps.start();
	ListeTimer.push(Temps);
	
	return true;
}

function Effacement_Ligne(E:Event):void {
	var Ligne:Shape = ListeLigne[0];
	if (Ligne != null) {
		ListeTimer[0].stop();
		ListeTimer.shift();
		_Monde._Dessin.removeChild(Ligne);
		ListeLigne.shift();
	}
}

function Fin_Vote(E:Event):void {
	TimerVote.stop();
	TimerVote.reset();
	_Monde.Vote.visible = false;
	VoteEnCours = false;
}

_Monde.Vote.Non.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Non);
function Clique_Non(E:Event):void {
	Fin_Vote(null);
	_root.Envoie_Serveur("EdV");
}

_Monde.Vote.Oui.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Oui);
function Clique_Oui(E:Event):void {
	Fin_Vote(null);
	_root.Envoie_Serveur("EdV#1");
}

_Monde.Vote.SansAvis.addEventListener(MouseEvent.MOUSE_DOWN, Clique_SansAvis);
function Clique_SansAvis(E:Event):void {
	Fin_Vote(null);
}

_Monde.Vote.CacherVote.addEventListener(MouseEvent.MOUSE_DOWN, Clique_CacherVote);
function Clique_CacherVote(E:Event):void {
	Fin_Vote(null);
	Nouveau_Message_Chat("La fenêtre ne sera plus affichée. Néanmoins, vous pouvez toujours voter au clavier si vous le souhaitez à la fin de la partie : F4 pour non et F7 pour oui. Vous pourrez réactiver la fenêtre de vote dans les options.");				
	_root.Options.Clique_NoVote(null);
}


_Monde.Porte.Retour.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Retour);
function Clique_Retour(E:Event):void {
	Desactivation_Trace(null);
	_Monde.visible = false;
	_Editeur.visible = true;
}