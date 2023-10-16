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

import com.adobe.crypto.SHA256;

var _root:MovieClip=MovieClip(parent.parent);
var EffacePseudo:Boolean=true;
var EffaceMDP1:Boolean=true;
var EffaceMDP2:Boolean=true;

var EffacePseudoId:Boolean=true;
var EffaceMDPId:Boolean=true;

_root.Navigateur(Ascenseur, News.Nouvelles, 200, 200, 40,News);
_root.Ascenseur_Reset(Ascenseur);

Windows.visible=false;
Windows.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Lien1);
function Clique_Lien1(E:Event):void {
	navigateToURL(new URLRequest("http://www.extinction.fr/minijeux/MiniJeux.exe"), "_blank");
}

Mac.visible=false;
Mac.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Lien2);
function Clique_Lien2(E:Event):void {
	navigateToURL(new URLRequest("http://www.extinction.fr/minijeux/MiniJeux.zip"), "_blank");
}

try {
	if (ExternalInterface.available) {
		var Adresse:String=ExternalInterface.call("window.location.href.toString");
		if (Adresse!=null) {
			Windows.visible=true;
			Mac.visible=true;
		}
	}
} catch (E:Error) {
}

Erreur.visible=false;
_Texte.restrict="A-Za-z";
_Mdp.restrict="^#";
_Compte.Pseudo.restrict="A-Za-z";
_Compte.MDP1.restrict="^#";
_Compte.MDP2.restrict="^#";

_Compte.visible=false;
_Mdp.visible=false;
FondMDP.visible=false;
_Charte.visible=false;
Invité.visible=false;
Chargement.visible=false;

Identifier.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Identifier);
function Clique_Identifier(E:Event):void {
	Identifier.visible=false;
	Invité.visible=true;
	Aide.y=45;
	FondMDP.visible=true;
	_Mdp.visible=true;
}

Invité.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Invité);
function Clique_Invité(E:Event):void {
	Identifier.visible=true;
	Invité.visible=false;
	Aide.y=14;
	FondMDP.visible=false;
	_Mdp.visible=false;
	_Mdp.text="";
	Erreur.visible=false;
}


Compte.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Compte);
function Clique_Compte(E:Event):void {
	_Compte.visible=true;
}

Charte.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Charte);
function Clique_Charte(E:Event):void {
	_Charte.visible=true;
}

_Compte.Valider.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Valider);
function Clique_Valider(E:Event):void {
	_Compte.Aide.text="";
	if (_Compte.Pseudo.text.length<3||_Compte.Pseudo.text=="Pseudo") {
		_Compte.Aide.text="Votre pseudo doit contenir au moins 3 caractères.";
		return;
	}
	if (_Compte.MDP1.text.length<5||! _Compte.MDP1.displayAsPassword) {
		_Compte.Aide.text="Votre mot de passe doit contenir au moins 5 caractères.";
		return;
	}
	if (_Compte.MDP1.text!=_Compte.MDP2.text) {
		_Compte.Aide.text="Vos deux mots de passe doivent être identiques.";
		return;
	}
	if (_Compte.MDP1.text==_Compte.Pseudo.text) {
		_Compte.Aide.text="Votre mot de passe ne doit pas être votre pseudo.";
		return;
	}	
	_Compte.Valider.visible=false;
	_root.Envoie_Serveur("CxCC#"+_Compte.Pseudo.text+"#"+com.adobe.crypto.SHA256.hash(_Compte.MDP1.text));
}

_Compte.Annuler.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Annuler);
function Clique_Annuler(E:Event):void {
	_Compte.visible=false;
}

_Charte.Retour.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Retour);
function Clique_Retour(E:Event):void {
	_Charte.visible=false;
}

_Mdp.addEventListener(FocusEvent.FOCUS_IN, E_MDPID);
function E_MDPID(E:Event):void {
	if (EffaceMDPId) {
		EffaceMDPId=false;
		_Mdp.text="";
		_Mdp.displayAsPassword=true;
	}
}
_Texte.addEventListener(FocusEvent.FOCUS_IN, E_PseudoId);
function E_PseudoId(E:Event):void {
	if (EffacePseudoId) {
		EffacePseudoId=false;
		_Texte.text="";
	}
}

_Compte.Pseudo.addEventListener(FocusEvent.FOCUS_IN, E_Pseudo);
function E_Pseudo(E:Event):void {
	if (EffacePseudo) {
		EffacePseudo=false;
		_Compte.Pseudo.text="";
	}
}
_Compte.MDP1.addEventListener(FocusEvent.FOCUS_IN, E_MDP1);
function E_MDP1(E:Event):void {
	if (EffaceMDP1) {
		EffaceMDP1=false;
		_Compte.MDP1.text="";
		_Compte.MDP1.displayAsPassword=true;
	}
}
_Compte.MDP2.addEventListener(FocusEvent.FOCUS_IN, E_MDP2);
function E_MDP2(E:Event):void {
	if (EffaceMDP2) {
		EffaceMDP2=false;
		_Compte.MDP2.text="";
		_Compte.MDP2.displayAsPassword=true;
	}
}

Aide.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Aide);
function Clique_Aide(E:Event):void {
	_root.Tentative_Connexion();
}