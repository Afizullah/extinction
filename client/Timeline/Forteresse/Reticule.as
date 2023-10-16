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

var SourisVisible:Boolean=false;
var ViséeDroite:Boolean=true;
var ModeConstruction:Boolean=false;
var ConstructionActive:Boolean=false;

var Curseur:MovieClip=_Monde._Plateau._Curseur;
Curseur.visible=false;
_Réticule.visible=false;

function Boucle_Souris(E:MouseEvent, BOUCLE:Boolean = true):void {
	if (! _root.NoLag) {
		_Réticule.graphics.clear();
		///_Réticule.graphics.lineStyle(2, 0x00FF9900); // Halloween
		_Réticule.graphics.lineStyle(2, 0x009D9D);
		_Réticule.graphics.drawCircle(0, 0, Number(Précision.Valeur));
	}
	//
	if (ModeConstruction) {
		var X:int=int(ClipPlateau.mouseX/10);
		var Y:int=int(ClipPlateau.mouseY/10);
		Curseur.x=X*10;
		Curseur.y=Y*10;
		if (ConstructionActive) {
			var SC1:int=ClipPlateau.mouseX-ClipJoueur.x-ClipJoueur.CentreX;
			var SC2:int=ClipPlateau.mouseY-ClipJoueur.y-ClipJoueur.CentreY;
			if (SC1*SC1+SC2*SC2<10000) {
				if (CaseDispo>49&&ListeCase[X][Y]==0) {
					CaseDispo-=50;
					ATCaseDispo+=500;
					if (ATCaseDispo>10000) {
						ATCaseDispo=10000;
					}
					ClipJoueur.Chargeur.Barre.width = int((CaseDispo/1000)*30);
					Construction(X, Y, ClipJoueur.EquipeBleu ? 0 : 1, 1);
					_root.Envoie_Serveur(Codage(8)+$+X+$+Y);
				}
			}
		}
	} else {
		if (SourisVisible) {
			if (mouseY<400) {
				if (! _root._SaveMap.visible
					&&! _root._ExporterRally.visible
					&&! _root._InfoJoueur.visible
					&&! _root._InfoTeam.visible
					&&! _root._Vote.visible
					&&! _root._FenetreRally.visible
					&&! _root._RechercheMaps.visible
					&&! _root._AffichageObjetsYshCastle.visible) {
					SourisVisible=false;
					if (! _root.NoLag) {
						Mouse.hide();
						_Réticule.visible=true;
					}
				}
			}
		} else {
			if (mouseY>400) {
				SourisVisible=true;
				Mouse.show();
				_Réticule.visible=false;
			}
		}
		_Réticule.x=mouseX;
		_Réticule.y=mouseY;

		ViséeDroite=ClipJoueur.x+ClipJoueur.CentreX-ClipListeJoueur.mouseX<=0;
	}
	if (BOUCLE) {
		E.updateAfterEvent();
	}
}

var DernierClic:int=0;

function Feu(E:Event):void {
	//
	if (_Monde._Aide.visible) {
		_Monde._Aide.visible=false;
		return;
	}
	if (mouseY<30) {
		if (mouseX>700) {
			_Monde._Aide.visible=true;
			SourisVisible=true;
			Mouse.show();
		}
		return;
	} else if (SourisVisible) {
		return;
	}
	//
	if (! Vivant||_root.Espionnage) {
		return;
	}
	//
	if (ModeConstruction) {
		ConstructionActive=true;
		Boucle_Souris(null, false);
		return;
	}
	//
	TirAuto=true;
	if (ArmeEnCours==0&&Chargeur==0 /* * ChargeurCoef*/ + ChargeurBase) {
		if (! ClipJoueur.Recharge&&! ClipJoueur.TD.visible&&! ClipJoueur.TG.visible) {
			_root.Envoie_Serveur(Codage(11));
			ClipJoueur.Recharge=true;
		}
	} else {
		if (ArmeEnCours==1&&Jambombe==0) {
			Changement_Arme(0);
			return;
		} else {
			if (ArmeEnCours == 1 && ((getTimer()-EntreeSalon < 10000)  || (getTimer()-DebutPartie< 2000))) {
				return;
			}
			if (! ClipJoueur.CD.visible&&! ClipJoueur.CG.visible) {
				var Intervalle : int = getTimer()-DernierClic;
				if (Intervalle<125) {
					if (Intervalle<117 || _root.TirContinu) {
						return;
					}
				}
				DernierClic=getTimer();
				if (ArmeEnCours==0) {
					///Chargeur--;
					// ChargeurNextValeur = ((Chargeur - ChargeurBase) / ChargeurCoef - 1) * ChargeurCoef + ChargeurBase;
					Chargeur = (Chargeur - ChargeurBase) / ChargeurCoef - 1;
					var FutureValChargeur = Chargeur * ChargeurCoef + ChargeurBase;
					ClipJoueur.Chargeur.Balle.text=""+Chargeur;
					if (Chargeur * 2 + 2 >= 27 || FutureValChargeur != ChargeurNextValeur) {
						_root.Envoie_Serveur("CxTriche#Balles#" + Chargeur);
					}

					Chargeur = FutureValChargeur;
					ChargeurNextValeur = ((Chargeur - ChargeurBase) / ChargeurCoef - 1) * ChargeurCoef + ChargeurBase;
				} else {
					if (ArmeEnCours==1) {
						TirAuto=false;
						Jambombe--;
						ClipJoueur.Chargeur.Balle.text=""+Jambombe;
						_Monde._Action.Arme1.Texte.text=""+Jambombe;
						if (Jambombe * 2 + 3 >= 9 && !_root.Noel) {
							_root.Envoie_Serveur("CxTriche#Jambombe#" + Jambombe);
						}
					}
				}
				//
				var PrécisionEnCours:Number=Number(Précision.Valeur);
				//
				var Visée:int;
				if (ViséeDroite) {
					Visée=1;
				} else {
					Visée=-1;
				}
				var DX:int=ClipJoueur.x+ClipJoueur.CentreX;
				var DY:int=ClipJoueur.y+ClipJoueur.CentreY;
				var Angle:Number = Math.atan((ClipListeJoueur.mouseY-DY)/(ClipListeJoueur.mouseX-DX));
				var EcartMax:Number=PrécisionEnCours/250;
				var Hazard:Number = (Math.random()*PrécisionEnCours*2)/250;
				Angle+=Hazard-EcartMax;
				//
				var PX:int;
				var PY:int;
				var Cosinus:Number=Math.cos(Angle);
				var Sinus:Number=Math.sin(Angle);
				var NbJoueur:int=EquipeCible.length;
				var ImpactJoueur:Boolean=false;
				var JoueurTouché:MovieClip;
				for (var i:int = 0; i<100; i++) {
					var Palié:int=i*10*Visée;
					var FX:int=Cosinus*Palié+DX;
					var FY:int=Sinus*Palié+DY;
					if (FX<0||FX>=2000||FY<0||FY>=1000) {
						break;
					}
					PX=FX;
					PY=FY;
					// Test impact Joueur
					for (var k:int = 0; k<NbJoueur; k++) {
						var Joueur:MovieClip=EquipeCible[k];
						if (Joueur.Vivant) {
							var JoueurX:int=Joueur.x;
							var JoueurY:int=Joueur.y;
							if (PX>JoueurX+5&&PX<JoueurX+35) {
								if (PY>JoueurY-5&&PY<JoueurY+38) {
									ImpactJoueur=true;
									JoueurTouché=Joueur;
									PX-=JoueurX;
									PY-=JoueurY;
									break;
								}
							}
						}
					}
					if (ImpactJoueur) {
						break;
					}
					// Test impact case
					var Bx:int=int(PX/10);
					var By:int=int(PY/10);
					if (ListeCase[Bx][By]==1||ListeCase[Bx][By]==2||ListeCase[Bx][By]==8||ListeCase[Bx][By]==3||ListeCase[Bx][By]==4) {
						break;
					}

					// Test impact monde
					if (MondePixel.getPixel(PX,PY)!=0) {
						//if (ClipCarte.hitTestPoint(PX+_Monde._Plateau.x, PY+_Monde._Plateau.y, true)) {
						break;
					}
				}
				//
				if (ImpactJoueur) {
					_root.Envoie_Serveur(Codage(3) + $ + PX + $ + PY + $ + JoueurTouché.Code + $ + ArmeEnCours+$+((Chargeur - ChargeurBase) / ChargeurCoef));
					Affichage_Tire(ClipJoueur, PX, PY, JoueurTouché);
				} else {
					_root.Envoie_Serveur(Codage(3) + $ + PX + $ + PY + $ + $ + ArmeEnCours+$+((Chargeur - ChargeurBase) / ChargeurCoef));
					Affichage_Tire(ClipJoueur, PX, PY);
					if (ArmeEnCours==0) {
						Destruction_Case(int(PX/10), int(PY/10));
					}
				}
				//
				PrécisionEnCours=PrécisionEnCours*1.5;
				if (PrécisionEnCours>100) {
					PrécisionEnCours=100;
				}
				Précision.Valeur=PrécisionEnCours;
				Boucle_Souris(null, false);
			}
		}
	}
}

function Non_Feu(E:Event):void {
	_root.Inaction=0;
	TirAuto=false;
	ConstructionActive=false;
}

function Affichage_Tire(JOUEUR:MovieClip, X:int, Y:int, TOUCHE:MovieClip = null, MORT:Boolean = false):void {
	var ObjetTouché:Boolean=TOUCHE!=null;
	var Tire:MovieClip;

	if (_root.NoLag) { // Plus de transparence progressive dans les tirs
		Tire = new $TireOptiFlui();
	} else {
		Tire = new $Tire();
	}

	ClipListeTire.addChild(Tire);
	if (_root.Halloween) {
		Tire.Effet.graphics.lineStyle(1, 0xFF9900);
	} else if (_root.Carnaval || _root.Nouvelan) {
		Tire.Effet.graphics.lineStyle(1, (Math.round(Math.random() * 100) % 255) | ((Math.round(Math.random() * 100) % 255) << 8) | ((Math.round(Math.random() * 100) % 255) << 16));
	} else if (_root.SaintValentin) {
		Tire.Effet.graphics.lineStyle(1, 0xF24AB8);
	} else {
		Tire.Effet.graphics.lineStyle(1, 0xFFFFFF);
	}

	JOUEUR.AnimEnCours.gotoAndStop(1);
	JOUEUR.AnimEnCours.visible=false;
	var Gauche:Boolean;
	if (ObjetTouché) {
		Gauche=JOUEUR.x>TOUCHE.x;
	} else {
		Gauche=JOUEUR.x+JOUEUR.CentreX>X;
	}
	if (Gauche) {
		JOUEUR.AnimEnCours=JOUEUR.TG;
		Tire.x=JOUEUR.x+JOUEUR.CentreX-28;
		Tire.y=JOUEUR.y+JOUEUR.CentreY;
	} else {
		JOUEUR.AnimEnCours=JOUEUR.TD;
		Tire.x=JOUEUR.x+JOUEUR.CentreX+28;
		Tire.y=JOUEUR.y+JOUEUR.CentreY;
	}
	JOUEUR.AnimEnCours.gotoAndPlay(1);
	JOUEUR.AnimEnCours.visible=true;
	//
	if (ObjetTouché) {
		AfficherSang(X, Y, TOUCHE, (Gauche ? -1 : 1));

		if (TOUCHE.ClipJoueur && !MORT) {
			if (Gauche) {
				TOUCHE.VitesseX-=2;
				_root.Envoie_Serveur(Codage(1)+$+TOUCHE.x+$+TOUCHE.y+$+int(TOUCHE.VitesseX*100)+$+int(TOUCHE.VitesseY*100));
			} else {
				TOUCHE.VitesseX+=2;
				_root.Envoie_Serveur(Codage(1)+$+TOUCHE.x+$+TOUCHE.y+$+int(TOUCHE.VitesseX*100)+$+int(TOUCHE.VitesseY*100));
			}
		}

		/*var Sang:MovieClip = new $Sang();
		var Alea:int=int(Math.random()*3);
		Sang.D0.visible=false;
		Sang.G0.visible=false;
		Sang.D1.visible=false;
		Sang.G1.visible=false;
		Sang.D2.visible=false;
		Sang.G2.visible=false;
		TOUCHE.addChild(Sang);
		Sang.y=Y;
		//
		if (Gauche) {
			Sang.x=X-5;
			Sang["D"+Alea].visible=true;
			if (TOUCHE.ClipJoueur) {
				TOUCHE.VitesseX-=2;
				if (! MORT) {
					_root.Envoie_Serveur(Codage(1)+$+TOUCHE.x+$+TOUCHE.y+$+int(TOUCHE.VitesseX*100)+$+int(TOUCHE.VitesseY*100));
				}
			}
		} else {
			Sang.x=X+5;
			Sang["G"+Alea].visible=true;
			if (TOUCHE.ClipJoueur) {
				TOUCHE.VitesseX+=2;
				if (! MORT) {
					_root.Envoie_Serveur(Codage(1)+$+TOUCHE.x+$+TOUCHE.y+$+int(TOUCHE.VitesseX*100)+$+int(TOUCHE.VitesseY*100));
				}
			}
		}*/
		Tire.Effet.graphics.lineTo(TOUCHE.x+X-Tire.x, TOUCHE.y+Y-Tire.y);
	} else {
		Tire.Effet.graphics.lineTo(X-Tire.x, Y-Tire.y);
	}
}

function AfficherSang(X:int, Y:int, CIBLE:MovieClip, Zone:int = -1) {
	/*
	 (<X>;<Y>) : Impacte du tir
	 <CIBLE> : Joueur qui est touché
	 <Zone> : Gauche (-1), milieu (0), droite (1)
	*/
	var Sang:MovieClip = new $Sang();
	var Alea:int=int(Math.random()*3);
	Sang.D0.visible=false;
	Sang.G0.visible=false;
	Sang.D1.visible=false;
	Sang.G1.visible=false;
	Sang.D2.visible=false;
	Sang.G2.visible=false;

	CIBLE.addChild(Sang);

	Sang.y=Y;

	if (Zone == -1) { // Tir venant de gauche
		Sang["D"+Alea].visible=true;
		Sang.x=X-5;
	} else if (Zone == 0) { // Centré (utilisé pour la St Valentin uniquement)
		Sang["G"+Alea].visible=true;
		Sang.x=X;
	} else { // Droite
		Sang["G"+Alea].visible=true;
		Sang.x = X + 5;
	}
}
