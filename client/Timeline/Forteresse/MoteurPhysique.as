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

var ClipListeJoueur:MovieClip = _Monde._Plateau._ListeJoueur;
var ClipListeTire:MovieClip = _Monde._Plateau._ListeTire;
var ClipListeEffet:MovieClip = _Monde._Plateau._ListeEffet;
var ClipCarte:MovieClip = _Monde._Plateau._Collision._Carte;
var ClipListeCase:MovieClip = _Monde._Plateau._ListeCase;
var ClipPlateau:MovieClip = _Monde._Plateau;
var ClipFondMonde:MovieClip = _Monde._Plateau._FondMonde;
var ClipCollision:MovieClip = _Monde._Plateau._Collision;
var ClipListeFrag:MovieClip = _Monde._ListeFrag;
var ClipListeCaseMorte:MovieClip = _Monde._Plateau._ListeCaseMorte;
var ClipRespawn:MovieClip = _Monde._Action.Respawn;

ClipListeJoueur.mouseEnabled = false;
ClipListeJoueur.mouseChildren = false;
ClipListeTire.mouseEnabled = false;
ClipListeTire.mouseChildren = false;
ClipCarte.mouseEnabled = false;
ClipCarte.mouseChildren = false;
ClipListeCase.mouseEnabled = false;
ClipListeCase.mouseChildren = false;
ClipListeFrag.mouseEnabled = false;
ClipListeFrag.mouseChildren = false;
ClipListeCaseMorte.mouseEnabled = false;
ClipListeCaseMorte.mouseChildren = false;

var CibleCamera:MovieClip;

var ListeMobile:Array = new Array();
//var ListeCaseMorte:Array = new Array();

var ClipJoueur:MovieClip;

var AffichagePoint:Boolean = false;
var IPS:Number = 0.048;
var ImageEcoule:int;
var TempsZero:int = 0;
var DerniereActualisation:int = 0;

var GraviteY:Number = 0.2;
var GraviteX:Number = 0.1;
var Limite:int = 500;

var PositionJoueurX:int = 381;
var PositionJoueurY:int = 195;

_Monde.visible = false;
ClipRespawn.visible = false;

function Demarrage_physique():void {
	if (TempsZero == 0) {	
		TempsZero = getTimer();
		ImageEcoule = 0;
		stage.addEventListener(Event.ENTER_FRAME, Synchronization_Physique);
	}
}

function Synchronization_Physique(E:Event):void {
	if(_root.ModuleIPS){
		_root.IPS.Rendu();
		_root.IPS.Nouvelle_Valeur(-1);
	}
	FPS++;
	//
	var Temps:int = getTimer();
	var TempsPartie:int = TempsTotalPartie-(Temps-DebutPartie)/1000;
	var Prefixe:String = TempsPartie>600?"":"0";
	var Secondes:int = TempsPartie%60;
	if (Secondes < 10) {
		_Monde.TexteTemps.text = Prefixe+int(TempsPartie/60)+":0"+Secondes;
	} else {
		_Monde.TexteTemps.text = Prefixe+int(TempsPartie/60)+":"+Secondes;
	}
	// Mort
	if (ClipRespawn.visible) {
		var Re:int = 10-int((getTimer()-DateMort)/1000);
		if (Re < 0) {
			Re = 0;
		}
		ClipRespawn.Texte.htmlText = "Réapparition dans : <font color='#009D9D'>"+Re;
	}
	//
	var TempsEcoule:int = Temps-TempsZero;
	var ImageTotal:int = TempsEcoule*IPS;
	var Image:int = ImageTotal-ImageEcoule;
	if(_root.ModuleIPS){
		_root.IPS.Nouvelle_Valeur(0, true);
	}
	if (Image != 0) {
		ImageEcoule = ImageTotal;
		Boucle_Physique(Image, Temps);
	}
	if(_root.ModuleIPS){
		_root.IPS.Nouvelle_Valeur(0);
	}
	if (TirAuto && _root.TirContinu) { 
		Feu(null);
	}
}

function Boucle_Physique(NUM:int, Temps:int):void {
	
	if (Controlable==0&&!Incontrolable) {
		ATIncontrolableCompteur++;
		if ((9900+ATIncontrolableCompteur)%10000==0){
			_root.Envoie_Serveur("CxTriche#Sauts#"+ATIncontrolableCompteur);
		}
	}	
	if (CaseDispo>500 && ATCaseDispo>9000) {
		ATCaseDispoCompteur++;
		if ((9900+ATCaseDispoCompteur)%10000==0){
			_root.Envoie_Serveur("CxTriche#Carrés#"+ATCaseDispoCompteur);
		}
	}	
	
	if (100000-2*ClipJoueur.x != ClipJoueur.x2 || 100000-2*ClipJoueur.y != ClipJoueur.y2 && ATCoordCompteurActive) {
		ATCoordCompteur++;
		if (ATCoordCompteur==2 || ATCoordCompteur==10 || ATCoordCompteur==100) {
			_root.Envoie_Serveur("CxTriche#Téléportations#"+ATCoordCompteur+" : "+ClipJoueur.x + "," + ClipJoueur.y + " vs " + ((100000-ClipJoueur.x2)/2) + "," + ((100000-ClipJoueur.y2)/2));		
		}
	}
	
	var NbMobile:int = ListeMobile.length;
	for (var z:int = 0; z<NUM; z++) {
		//
		var PrécisionEnCours:Number = Number(Précision.Valeur);
		if (PrécisionEnCours > 10) {
			PrécisionEnCours -= PrécisionEnCours/100;
			Précision.Valeur = PrécisionEnCours;
			Boucle_Souris(null, false);
		}
		//
		var Affichage:Boolean = z == NUM-1;
		//
		CaseDispo += 5;
		if (CaseDispo > 1000) {
			CaseDispo  = 1000;
			ClipJoueur.Chargeur.Barre.width = 30;
		} else {
			ClipJoueur.Chargeur.Barre.width = (CaseDispo/1000)*30;
		}
		ATCaseDispo -= 50;
		if (ATCaseDispo < 1) {
			ATCaseDispo  = 1;
		}
		
		//
		for (var i:int = 0; i<NbMobile; i++) {
			var Mobile:MovieClip = ListeMobile[i];
			var MobileObjet:Boolean = Mobile.Objet;
			var MobileClipJoueur:Boolean = Mobile.ClipJoueur;
			var MobileZombie:Boolean = Mobile.Zombie;
			// Recharge
			if (Mobile.Recharge && Affichage) {
				if (Mobile.SD.visible) {
					if (MobileClipJoueur) {
						Chargeur = 0 /* * ChargeurCoef*/ + ChargeurBase;
						ChargeurNextValeur = 12 * ChargeurCoef + ChargeurBase;
						ClipJoueur.Chargeur.Balle.text = "0";
					}
					Mobile.ImageRecharge = 0;
					Mobile.SD.gotoAndStop(1);
					Mobile.SD.visible = false;
					Mobile.AnimEnCours = Mobile.RD;
					Mobile.RD.visible = true;
					Mobile.RD.gotoAndStop(1);
				} else {
					if (Mobile.RD.visible) {
						Mobile.ImageRecharge += NUM;
						if (Mobile.ImageRecharge > 95) {
							Mobile.Recharge = false;
							Mobile.RD.visible = false;
							Mobile.AnimEnCours = Mobile.SD;
							Mobile.SD.gotoAndPlay(1);
							Mobile.SD.visible = true;
							if (MobileClipJoueur) {
								Chargeur = 12 * ChargeurCoef + ChargeurBase;
								ChargeurNextValeur = 11 * ChargeurCoef + ChargeurBase;
								if (ArmeEnCours == 0) {
									ClipJoueur.Chargeur.Balle.text = "12";
								}
							}
						} else {
							Mobile.RD.gotoAndStop(int(Mobile.ImageRecharge/4));
						}
					} else {
						if (Mobile.SG.visible) {
							if (MobileClipJoueur) {
								Chargeur = 0 /* * ChargeurCoef*/ + ChargeurBase;
								ChargeurNextValeur = 12 * ChargeurCoef + ChargeurBase;
								ClipJoueur.Chargeur.Balle.text = "0";
							}
							Mobile.ImageRecharge = 0;
							Mobile.SG.gotoAndStop(1);
							Mobile.SG.visible = false;
							Mobile.AnimEnCours = Mobile.RG;
							Mobile.RG.visible = true;
							Mobile.RG.gotoAndStop(1);
						} else {
							if (Mobile.RG.visible) {
								Mobile.ImageRecharge += NUM;
								if (Mobile.ImageRecharge > 95) {
									Mobile.Recharge = false;
									Mobile.RG.visible = false;
									Mobile.AnimEnCours = Mobile.SG;
									Mobile.SG.gotoAndPlay(1);
									Mobile.SG.visible = true;
									if (MobileClipJoueur) {
										Chargeur = 12 * ChargeurCoef + ChargeurBase;
										ChargeurNextValeur = 11 * ChargeurCoef + ChargeurBase;
										if (ArmeEnCours == 0) {
											ClipJoueur.Chargeur.Balle.text = "12";
										}
									}
								} else {
									Mobile.RG.gotoAndStop(int(Mobile.ImageRecharge/4));
								}
							} else {
								Mobile.Recharge = false;
							}
						}
					}
				}
			}
			//
			Mobile.VitesseY += GraviteY;
			//
			var DeplacementY:Number = Mobile.VitesseY+Mobile.SynchroY;
			var MouvementY:int = DeplacementY;
			Mobile.SynchroY = DeplacementY-MouvementY;
			//
			if (Mobile.VitesseX != 0) {
				if (Mobile.VitesseX < 0) {
					Mobile.VitesseX += GraviteX;
					if (Mobile.VitesseX > 0) {
						Mobile.VitesseX = 0;
					}
				} else {
					Mobile.VitesseX -= GraviteX;
					if (Mobile.VitesseX < 0) {
						Mobile.VitesseX = 0;
					}
				}
			}
			//
			var DeplacementX:Number = Mobile.VitesseX+Mobile.DeplacementX+Mobile.SynchroX;
			var MouvementX:int = DeplacementX;
			Mobile.SynchroX = DeplacementX-MouvementX;
			//
			var SensVitesseX:int;
			var SensVitesseY:int;
			var VitesseXABS:int;
			var VitesseYABS:int;
			if (MouvementX < 0) {
				SensVitesseX = -1;
				VitesseXABS = -MouvementX;
			} else {
				SensVitesseX = 1;
				VitesseXABS = MouvementX;
			}
			if (MouvementY < 0) {
				SensVitesseY = -1;
				VitesseYABS = -MouvementY;
			} else {
				SensVitesseY = 1;
				VitesseYABS = MouvementY;
			}
			var Yok:Boolean = false;
			var Xok:Boolean = false;
			var PlusGrand:int = Math.max(VitesseXABS, VitesseYABS);
			if (PlusGrand == 0) {
				PlusGrand = 1;
			}
			var Puissance:int;
			if (ClipJoueur != Mobile && _root.TrueNoLag) {
				Puissance = 2;
			} else {
				Puissance = 1;
			}			
			var Chute:Boolean = VitesseYABS > 1;
			for (var k:int = 0; k<PlusGrand; k=k+Puissance) {
				var BaseX:int = Mobile.x;
				if (k<VitesseXABS) {
					Mobile.x += SensVitesseX*Puissance;
				}
				if (k<VitesseYABS) {
					Mobile.y += SensVitesseY*Puissance;
				}
				// Collision
				var CD:Boolean = MondePixel.getPixel(Mobile.PD.x+Mobile.x, Mobile.PD.y+Mobile.y) != 0;
				var CG:Boolean = MondePixel.getPixel(Mobile.PG.x+Mobile.x, Mobile.PG.y+Mobile.y) != 0;
				var CB:Boolean = MondePixel.getPixel(Mobile.PB.x+Mobile.x, Mobile.PB.y+Mobile.y) != 0;
				//
				if (Mobile.x < 0) {
					Mobile.x = 0;
					Mobile.VitesseX = 0;
					CD = false;
					Xok = true;
				} else {
					if (Mobile.x > 1966) {
						Mobile.x = 1966;
						Mobile.VitesseX = 0;
						CG = false;
						Xok = true;
					}
				}
				if (MobileClipJoueur) {
					if (Mobile.y < -4 || Mobile.y > 964) {
						if (Mobile.EquipeBleu) {
							if (AttaqueRougeEnCours) {
								Mobile.x = SpawnDéfenseX;
								Mobile.y = SpawnDéfenseY;
							} else {
								Mobile.x = SpawnAttaqueX;
								Mobile.y = SpawnAttaqueY;
							}
						} else {
							if (AttaqueRougeEnCours) {
								Mobile.x = SpawnAttaqueX;
								Mobile.y = SpawnAttaqueY;
							} else {
								Mobile.x = SpawnDéfenseX;
								Mobile.y = SpawnDéfenseY;
							}
						}
						break;
					}
				}
				if (Mobile.y < 0) {
					//Mobile.y = 0;
					Mobile.VitesseY = 0;
					CB = false;
					Yok = true;
				} else {
					if (Mobile.y > 964) {
						CB = true;
					}
				}
				//
				if (!CD) {
					var PDx:int = int((Mobile.x+Mobile.PD.x)/10);
					var PDy:int = int((Mobile.y+Mobile.PD.y)/10);
					CD = ListeCase[PDx][PDy] == 1 || ListeCase[PDx][PDy] == 2 || ListeCase[PDx][PDy] == 8;
				}
				if (!CG) {
					var PGx:int = int((Mobile.x+Mobile.PG.x)/10);
					var PGy:int = int((Mobile.y+Mobile.PG.y)/10);
					CG = ListeCase[PGx][PGy] == 1 || ListeCase[PGx][PGy] == 2 || ListeCase[PGx][PGy] == 8;
				}
				if (!CB) {
					var PBx:int = int((Mobile.x+Mobile.PB.x)/10);
					var PBy:int = int((Mobile.y+Mobile.PB.y)/10);
					CB = ListeCase[PBx][PBy] == 1 || ListeCase[PBx][PBy] == 2 || ListeCase[PBx][PBy] == 8;
				}
				//
				var CibleX:int;
				var CibleY:int;
				if (CD && CG) {
					if (CB) {
						if (Mobile.y > Limite) {
							Mobile.y++;
						} else {
							Mobile.y--;
						}
					} else {
						Mobile.y++;
					}
					CB = false;
					Mobile.x = BaseX;
					Mobile.VitesseY = 0;
					Mobile.VitesseX = 0;
					Xok = true;
					Yok = true;
				} else {
					if (CD) {
						Mobile.x--;
						Mobile.VitesseX = 0;
						Xok = true;
					} else {
						if (CG) {
							Mobile.x++;
							Mobile.VitesseX = 0;
							Xok = true;
						}
					}
				}
				if (CB) {
					if (MobileClipJoueur) {
						Incontrolable = false;
						Controlable = 5;
					}
					Mobile.y--;
					Mobile.VitesseY = 0;
					Yok = true;
					//
					if (Chute) {
						Chute = false;
						if (MobileClipJoueur) {
							DerniereActualisation = Temps;
							_root.Envoie_Serveur(Codage(1)+$+Mobile.x+$+Mobile.y+$+int(Mobile.VitesseX*100)+$+int(Mobile.VitesseY*100));
							LimiteSaut = Temps;
						}
					}
				}
				
				if (Xok && Yok) {
					break;
				}
			}
			
			if (!MobileClipJoueur && (JoueurFocus != null || FocusJusteFini) && Mobile != JoueurFocus) {
				if (!FocusJusteFini && (Math.abs(Mobile.x - JoueurFocus.x) < 30 && Math.abs(Mobile.y - JoueurFocus.y) < 30)) {
					Mobile.visible = false;
				} else {
					Mobile.visible = true;
				}
			}
		}
	}
	// Vue
	ClipPlateau.x = PositionJoueurX-CibleCamera.x;
	ClipPlateau.y = PositionJoueurY-CibleCamera.y;
	
	ClipJoueur.x2 = 100000-2*ClipJoueur.x;
	ClipJoueur.y2 = 100000-2*ClipJoueur.y;
	
	FocusJusteFini = false;
	
}


function Formatage_Mobile(Mobile:MovieClip, CLIP_JOUEUR:Boolean = false, JOUEUR:Boolean = false):void {
	if (CLIP_JOUEUR) {
		Mobile.CD.gotoAndStop(1);
		Mobile.CG.gotoAndStop(1);
		Mobile.SG.gotoAndStop(1);
		Mobile.RD.gotoAndStop(1);
		Mobile.RG.gotoAndStop(1);
		Mobile.TD.gotoAndStop(1);
		Mobile.TG.gotoAndStop(1);
		Mobile.RD.visible = false;
		Mobile.RG.visible = false;
		Mobile.CD.visible = false;
		Mobile.CG.visible = false;
		Mobile.SG.visible = false;
		Mobile.TD.visible = false;
		Mobile.TG.visible = false;
		Mobile.AnimEnCours = Mobile.SD;
		//
		Mobile.Objet = false;
		Mobile.Recharge = false;
		Mobile.ImageRecharge = 0;
		Mobile.Image = 0;
	}

	var Largeur:int = int(Mobile.width);
	var Hauteur:int = int(Mobile.height)-1;


	Mobile.VitesseX = 0;
	Mobile.VitesseY = 0;
	Mobile.DeplacementX = 0;
	Mobile.SynchroX = 0;
	Mobile.DeplacementY = 0;
	Mobile.SynchroY = 0;

	Mobile.CentreX = 19;
	Mobile.CentreY = 10;

	if (JOUEUR) {
		Mobile.ClipJoueur = true;
	} else {
		Mobile.ClipJoueur = false;
	}
	//
	if (CLIP_JOUEUR) {
		Mobile.PB = new Object();
		Mobile.PB.x = 19;
		Mobile.PB.y = 35;

		Mobile.PD = new Object();
		Mobile.PD.x = 33;
		Mobile.PD.y = 15;

		Mobile.PG = new Object();
		Mobile.PG.x = 5;
		Mobile.PG.y = 15;
	}
	ListeMobile.push(Mobile);

	if (AffichagePoint) {
		Mobile.graphics.lineStyle(2, 0xFF0000, 1, true);
		Mobile.graphics.moveTo(Mobile.PB.x, Mobile.PB.y);
		Mobile.graphics.lineTo(Mobile.PB.x, Mobile.PB.y+1);
		Mobile.graphics.moveTo(Mobile.PD.x, Mobile.PD.y);
		Mobile.graphics.lineTo(Mobile.PD.x+1, Mobile.PD.y);
		Mobile.graphics.moveTo(Mobile.PG.x, Mobile.PG.y);
		Mobile.graphics.lineTo(Mobile.PG.x-1, Mobile.PG.y);
	}
}

function Desactivation(CLIP:MovieClip):void {
	var NbMobile:int = ListeMobile.length;
	for (var i:int = 0; i<NbMobile; i++) {
		if (ListeMobile[i] == CLIP) {
			ListeMobile.splice(i, 1);
			break;
		}
	}
}

// DEBUG
var MoyenneFPS:int = 0;
var NombreCapture:int = 0;
var FPS:int = 0;
var TimerMemoire:Timer = new Timer(500);
TimerMemoire.addEventListener(TimerEvent.TIMER, Boucle_Memoire);
function Boucle_Memoire(E:Event):void {
	var ValeurFPS:int = FPS*2;
	MoyenneFPS += ValeurFPS;
	NombreCapture++;
	_Monde.FPS.text = int((ValeurFPS/48)*100)+"%";
	FPS = 0;
}

function Changer_Focus(FocusSur:String) {
	if (FocusSur == "") {
		JoueurFocus = null;
		FocusJusteFini = true;
		//return;
	} else {
		FocusJusteFini = false;
	}
	
	var NbMobile:int = ListeMobile.length;
	for (var i:int = 0; i < NbMobile; i++) {
		if (ListeMobile[i].NomJoueur == FocusSur) {
			JoueurFocus = ListeMobile[i];
			//break;
		}
		
		if (ListeMobile[i].Vivant) {
			ListeMobile[i].visible = true;
		}
	}
}


