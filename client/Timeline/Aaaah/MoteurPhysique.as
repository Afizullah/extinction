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

_Monde.visible = false;

var UnDeux:Boolean = true;

var IPS:Number = 0.024;
var ImageEcoule:int;
var TempsZero:int = 0;

var AffichagePoint:Boolean = false;
var ListeMobile:Array = new Array();
var ListeMouve:Array = new Array();

var GraviteY:Number = 0.4;
var GraviteX:Number = 0.2;
var Boost:Number = 1;

var PuissanceAaaahX:int = 4;
var PuissanceAaaahY:int = -2;
var TempsEntreAaaah:int = 10000;

var Contamination:int = 0;
var ContaminationInstant:int = 0;

var OldTemps:Number = 99999999999;

function Boucle_Moteur(E:Event):void {
	
	if (_root.ModuleIPS) {
		_root.IPS.Rendu();
		_root.IPS.Nouvelle_Valeur(-1);
	}
	var Temps:Number = getTimer();
	var Diff:Number = Temps-OldTemps;
	if (Diff > 750 && Diff < 10000 && Vivant) {
		_root.Envoie_Serveur("CxTriche#CroixRouge#"+Diff);
	}
	OldTemps = Temps;
	var TempsPartie:int = 120-(Temps-TempsDebut)/1000;
	var Secondes:int = TempsPartie % 60;
	if (Secondes < 10) {
		if (Secondes < 0) {
			_Monde.Porte.Temps.text = "0" + int(TempsPartie / 60) + ":00";
		} else {
			_Monde.Porte.Temps.text = "0" + int(TempsPartie / 60) + ":0" + Secondes;
		}
	} else {
		_Monde.Porte.Temps.text = "0" + int(TempsPartie / 60) + ":" + Secondes;
	}
	//
	var TempsEcoule:int = Temps - TempsZero;
	var ImageTotal:int = TempsEcoule * IPS;
	var Image:int = ImageTotal - ImageEcoule;
	if (_root.ModuleIPS) {
		_root.IPS.Nouvelle_Valeur(0, true);
	}
	if (Image != 0) {
		ImageEcoule = ImageTotal;
		Boucle(Temps, Image);
	}
	if (_root.ModuleIPS) {
		_root.IPS.Nouvelle_Valeur(0);
	}
}

// Info fantome
var GhostArray : Array = new Array();
var GhostCompteur : int = 1;

function Boucle(TEMPS:int, IMAGE:int):void {
	var SpendTime:Number = getTimer() - TempsDebut;
	if (SpendTime > 120000) {
		return;
	}
	
	// déplacer le fantôme
	while (GhostCompteur < GhostArray.length) {
		var InfoGhost : Array = GhostArray[GhostCompteur].split(":");
		if (SpendTime > int(InfoGhost[0])) {
			Reception(InfoGhost[1]+"#Ghost");
			++GhostCompteur;
		} else {
			break;
		}
	}
	if (Controlable2==0&&!Incontrolable2) {
		ATIncontrolable2Compteur++;
		if ((2900+ATIncontrolable2Compteur)%3000==0){
			_root.Envoie_Serveur("CxTriche#SautsAaaah#"+ATIncontrolable2Compteur);
		}
	}	
	
	if (SpendTime<21100) {
		_Monde._Dessin.BriqueDepart.y = 353 + int(SpendTime / 500);
	} else {
		_Monde._Dessin.BriqueDepart.y = 550;
	}
	// Mouvement
	var TempsEcoule:Number;
	var TempsBase:int = TEMPS - TempsDebut;
	if (Monde.Mouvement) {
		Monde.Boucle(TempsBase);
	} else {
		var NbMouve:int = ListeMouve.length;
		for (var w:int = 0; w<NbMouve; w++) {
			var Mouve:MovieClip = ListeMouve[w];
			if (Mouve.Rotation) {
				Mouve.rotation = TempsBase / 500;
			} else {
				if (Mouve.RotationInverse) {
					Mouve.rotation =  -  TempsBase / 500;
				} else {
					if (Mouve.HorizontalInfinie) {
						Mouve.x = TempsBase / 60;
					}
				}
			}
		}
	}
	//
	if (Monde.Dynamique) {
		var Nb:int = Monde.ListeMouvement.length;
		for (var z:int = 0; z<Nb; z++) {
			var Forme:MovieClip = Monde.ListeMouvement[z];
			if (Forme.Translation) {
				if (TempsBase > Forme.T0) {
					TempsEcoule = (TempsBase-Forme.T0)/Forme.D;
					if (Forme.B1) {
						if (TempsBase < Forme.DF) {
							Forme.x = Forme.DX+(Forme.MX*TempsEcoule);
							Forme.y = Forme.DY+(Forme.MY*TempsEcoule);
						} else {
							Forme.T0 = Forme.DF;
							Forme.DF += Forme.D;
						}
					} else {
						if (Forme.B2) {
							if (TempsBase<Forme.DF) {
								Forme.x = Forme.DX+(Forme.MX*TempsEcoule);
								Forme.y = Forme.DY+(Forme.MY*TempsEcoule);
							} else {
								Forme.MX*=-1;
								Forme.MY*=-1;
								Forme.T0=Forme.DF;
								Forme.DF+=Forme.D;
								var DX:int=Forme.DX;
								var DY:int=Forme.DY;
								Forme.DX=Forme.FX;
								Forme.DY=Forme.FY;
								Forme.FX=DX;
								Forme.FY=DY;
							}
						} else {
							if (TempsBase<Forme.DF) {
								Forme.x = Forme.DX+(Forme.MX*TempsEcoule);
								Forme.y = Forme.DY+(Forme.MY*TempsEcoule);
							}
						}
					}
				}
			}
			//
			if (Forme.Rotation) {
				if (TempsBase>Forme.R_T0) {
					TempsEcoule = (TempsBase-Forme.R_T0)/Forme.R_D;
					if (Forme.R_B1) {
						if (TempsBase<Forme.R_DF) {
							Forme.rotation=Forme.R_DEG*TempsEcoule;
						} else {
							Forme.R_T0=Forme.R_DF;
							Forme.R_DF+=Forme.R_D;
						}
					} else {
						if (Forme.R_B2) {
							if (TempsBase<Forme.R_DF) {
								if (Forme.R_Reverse) {
									Forme.rotation = Forme.R_DEG*(1-TempsEcoule);
								} else {
									Forme.rotation=Forme.R_DEG*TempsEcoule;
								}
							} else {
								Forme.R_T0=Forme.R_DF;
								Forme.R_DF+=Forme.R_D;
								Forme.R_Reverse=! Forme.R_Reverse;
							}
						} else {
							if (TempsBase<Forme.R_DF) {
								Forme.rotation=Forme.R_DEG*TempsEcoule;
							}
						}
					}
				}
			}
		}
	}
	
	// Trouver le joueur focus
	var JoueurFocus:MovieClip = null;
	if (_root.FocusSur != "") {
		JoueurFocus = _Monde._ListeJoueur[_root.FocusSur];
	}
	
	//
	for (var I:int = 0; I<IMAGE; I++) {
		var NbMobile:int=ListeMobile.length;
		for (var i:int = 0; i<NbMobile; i++) {
			var Mobile:MovieClip=ListeMobile[i];
			// Trop loin
			if (Mobile.x<10) {
				Mobile.x=10;
			}
			//
			if (Mobile.Joueur) {
				if (Mobile.y > 380 || (Mobile.x > 740 && Mobile.y > 70)) {
					if (SpendTime<300) {
						continue;
					}
					//Nouveau_Message_Chat("Temps: " + SpendTime + " / Plateforme : " + _Monde._Dessin.BriqueDepart.y);
					Mort(Mobile, i);
					NbMobile--;
					i--;
					continue;
				} else {
					if (!SalonFight && ! Mobile.Zombie&&Mobile.x>740&&Mobile.y<60) {
						if (GhostLog) {
							// nouveau record perso
							if (GhostRecordPerso > SpendTime) {
								GhostRecordPerso = SpendTime;
								GhostMessagesPerso = GhostMessages;
							}
						}
						_root.Envoie_Serveur("IdW");
						if (_Monde.Porte.Retour.visible) {
							_Editeur.DernierTest.attributes.V="";
						}
						Vivant=false;
						ListeMobile.splice(i, 1);
						i--;
						NbMobile--;
						continue;
					}
				}
				// Aaaah
				var Largeur:int = 30*((getTimer()-TempsAaaah)/TempsEntreAaaah);
				if (Largeur>30) {
					Largeur=30;
				}
				if (!AaaahDispo) {
					Largeur = 0;
				}
				Mobile.CompteurAaaah.Barre.width=Largeur;
			}
			// Pesanteur
			Mobile.VitesseY+=GraviteY;
			if (Mobile.VitesseX>0) {
				Mobile.VitesseX=Mobile.VitesseX-GraviteX;
				if (Mobile.VitesseX<0) {
					Mobile.VitesseX=0;
				}
			} else {
				if (Mobile.VitesseX<0) {
					Mobile.VitesseX=Mobile.VitesseX+GraviteX;
					if (Mobile.VitesseX>0) {
						Mobile.VitesseX=0;
					}
				}
			}
			//
			var DeplacementX:Number=Mobile.VitesseX+Mobile.DeplacementX+Mobile.SynchroX;
			var MouvementX:int=DeplacementX;
			Mobile.SynchroX=DeplacementX-MouvementX;
			//
			var CollisionSpe:Boolean;
			var SensVitesseX:int;
			var SensVitesseY:int;
			var VitesseXABS:int;
			var VitesseYABS:int;
			if (MouvementX<0 && Boost>=0 || MouvementX>=0 && Boost<0) {
				SensVitesseX=-1;
				VitesseXABS=- MouvementX * Boost;
			} else {
				SensVitesseX=1;
				VitesseXABS=MouvementX * Boost;
			}
			if (Mobile.VitesseY<0) {
				CollisionSpe=true;
				SensVitesseY=-1;
				VitesseYABS=- Mobile.VitesseY;
			} else {
				CollisionSpe=false;
				SensVitesseY=1;
				VitesseYABS=Mobile.VitesseY;
			}
			//
			//if (Mobile.Temps != 0) {
			//var Duree:Number = getTimer()-Mobile.Temps;
			//var DistanceMax:Number = 0.05*Duree;
			//var DistanceBonus:int = DistanceMax-Mobile.Distance;
			//Mobile.Distance += DistanceBonus;
			////VitesseYABS = DistanceBonus;
			//VitesseXABS = DistanceBonus;
			//}
			//
			var Yok:Boolean=false;
			var Xok:Boolean=false;
			var PlusGrand:int=Math.max(VitesseXABS,VitesseYABS);
			if (PlusGrand==0) {
				PlusGrand=1;
			}
			var Puissance:int;
			//if (ClipJoueur != Mobile) {
			if (PlusGrand > 2 || (ClipJoueur != Mobile && _root.NoLag)) {
				Puissance=2;
			} else {
				Puissance=1;
			}
			for (var k:int = 0; k<PlusGrand; k=k+Puissance) {
				if (k<VitesseXABS) {
					Mobile.x+=SensVitesseX*Puissance;
				}
				if (k<VitesseYABS) {
					Mobile.y+=SensVitesseY*Puissance;
				}
				//
				
				/**var MondePixel:BitmapData = new BitmapData(800, 600, true, 0x00FFFFFF);
				MondePixel.draw(_Monde._Dessin);**/
				
				// Collision
				/**var CD:Boolean=_Monde._Dessin.hitTestPoint(Mobile.PD.x+Mobile.x,Mobile.PD.y+Mobile.y,true) && (MondePixel.getPixel(Mobile.PD.x+Mobile.x,Mobile.PD.y+Mobile.y) != 0x303036);
				var CG:Boolean=_Monde._Dessin.hitTestPoint(Mobile.PG.x+Mobile.x,Mobile.PG.y+Mobile.y,true) && (MondePixel.getPixel(Mobile.PG.x+Mobile.x,Mobile.PG.y+Mobile.y) != 0x303036);
				var CB:Boolean=_Monde._Dessin.hitTestPoint(Mobile.PB.x+Mobile.x,Mobile.PB.y+Mobile.y,true) && (MondePixel.getPixel(Mobile.PB.x+Mobile.x,Mobile.PB.y+Mobile.y) != 0x303036);
				**/
				var CD:Boolean=_Monde._Dessin.hitTestPoint(Mobile.PD.x+Mobile.x,Mobile.PD.y+Mobile.y,true);
				var CG:Boolean=_Monde._Dessin.hitTestPoint(Mobile.PG.x+Mobile.x,Mobile.PG.y+Mobile.y,true);
				var CB:Boolean=_Monde._Dessin.hitTestPoint(Mobile.PB.x+Mobile.x,Mobile.PB.y+Mobile.y,true); 
				//var CH:Boolean = _Monde._Dessin.hitTestPoint(Mobile.PH.x+Mobile.x, Mobile.PH.y+Mobile.y, true);

				if (CD&&CG&&CB) {
					var MZ:int;
					if (Mobile.Zombie) {
						MZ=-1;
					} else {
						MZ=1;
					}
					if (Mobile.VitesseX==0) {
						Mobile.y+=2*MZ;
					} else {
						Mobile.y+=2*MZ;
						Mobile.x-=2*MZ;
					}
					Mobile.VitesseX=0;
					Mobile.VitesseY=0;
					break;
				}
				if (CD) {
					if (CollisionSpe&&! CB) {
						Mobile.y+=1;
						Yok=true;
						Mobile.VitesseY=- Mobile.VitesseY;
					}
					Mobile.x-=2*Puissance;
					Mobile.VitesseX=0;
					Xok=true;
				} else {
					if (CG) {
						if (CollisionSpe&&! CB) {
							Mobile.y+=1;
							Yok=true;
							Mobile.VitesseY=- Mobile.VitesseY;
						}
						Mobile.x+=2*Puissance;
						Mobile.VitesseX=0;
						Xok=true;
					}
				}
				//
				if (CB) {
					if (Mobile.Joueur&&Aaaah&&! Incontrolable2&&! Incontrolable) {
						Aaaah=false;
					}
					if (CollisionSpe&&Incontrolable2) {
						CollisionSpe=false;
						Mobile.VitesseY=0;
					} else {
						Mobile.y--;
						if (_Monde._Dessin.hitTestPoint(Mobile.PB.x+Mobile.x,Mobile.PB.y+Mobile.y,true) /**&& (MondePixel.getPixel(Mobile.PB.x+Mobile.x,Mobile.PB.y+Mobile.y) != 0x303036)**/) {
							Mobile.y--;
						}
						Mobile.VitesseY=0;
						Yok=true;

						if (Mobile.Joueur) {
							Mobile.Vide=0;
							Incontrolable2=false;
							Controlable2 = 6;
							if (! TimerMouvement.running) {
								TimerMouvement.start();
							}
							if (Mobile.Apesanteur) {
								Mobile.Apesanteur=false;
								if (DroiteEnCours) {
									Envoie_Deplacement("MvD#"+ClipJoueur.x+"#"+ClipJoueur.y);
									Deplacement_Joueur(ClipJoueur, ClipJoueur.x, ClipJoueur.y, false, true);
								} else {
									if (GaucheEnCours) {
										Envoie_Deplacement("MvG#"+ClipJoueur.x+"#"+ClipJoueur.y);
										Deplacement_Joueur(ClipJoueur, ClipJoueur.x, ClipJoueur.y, false, false);
									} else {
										Envoie_Deplacement("MvS#"+ClipJoueur.x+"#"+ClipJoueur.y);
										Deplacement_Joueur(ClipJoueur, ClipJoueur.x, ClipJoueur.y, true);
									}
								}
							}
						}
					}
				} else {
					if (Mobile.Joueur) {
						Mobile.Vide++;
						if (Mobile.Vide==30) {
							Mobile.Apesanteur=true;
							Incontrolable2=true;
							Controlable2 = 0;							
							if (TimerMouvement.running) {
								TimerMouvement.stop();
							}
						}
					}
					//if (CH) {
					//if (CollisionSpe) {
					//Mobile.y++;
					//Mobile.VitesseY = -Mobile.VitesseY;
					//Yok = true;
					//}
					//if (_Monde._Dessin.hitTestPoint(Mobile.PB.x+Mobile.x, Mobile.PB.y+Mobile.y, true)) {
					//Mobile.y--;
					//}
					//}
				}
				
				if (Xok&&Yok) {
					break;
				}
			}
			
			if (!Mobile.Joueur && JoueurFocus != null && Mobile != JoueurFocus) {
				if (Math.abs(Mobile.x - JoueurFocus.x) < 30 && Math.abs(Mobile.y - JoueurFocus.y) < 30) {
					Mobile.visible = false;
				} else {
					Mobile.visible = true;
				}
			} else {
				Mobile.visible = true;
			}
		}
	}
	//
	if (Vivant&&! ClipJoueur.Zombie) {
		if (UnDeux) {
			UnDeux=false;
		} else {
			UnDeux=true;
			var NbMobile2:int=ListeMobile.length;
			for (var C:int = 0; C<NbMobile2; C++) {
				var Mobile2:MovieClip=ListeMobile[C];
				if (! Mobile2.Joueur) {
					if (Mobile2.Zombie) {
						var C1:int=ClipJoueur.x-Mobile2.x;
						var C2:int=ClipJoueur.y-Mobile2.y;
						if (Math.sqrt(C1*C1+C2*C2)<10) {
							Contamination++;
							if (Contamination==15||Contaminé<2) {
								Zombification(ClipJoueur, true);
								_root.Envoie_Serveur("ZoC#"+Mobile2.Nom.text);
								if (_root.Noel) {
									Nouveau_Message_Chat("<font color='#CB546B'>"+Mobile2.Nom.text+" vous a transféré sa rage de dent ! AAaAAaAaaAaaaaah !!!!</font>");
								} else {
									Nouveau_Message_Chat("<font color='#CB546B'>"+Mobile2.Nom.text+" vous a contaminé ! AAaAAaAaaAaaaaah !!!!</font>");
								}
								
							}
							break;
						}
					}
				}
			}
		}
	}
}

function Mort(Mobile:MovieClip, i:int):void {
	if (Aaaah||Incontrolable) {
		Nouveau_Message_Chat("<font color='#CB546B'>"+AuteurAaaah.Nom.text+" vous a éliminé.</font>");
		_root.Envoie_Serveur("IdX#"+AuteurAaaah.Nom.text+"#"+Mobile.x);
	} else {
		Nouveau_Message_Chat("<font color='#CB546B'>Vous êtes mort.</font>");
		_root.Envoie_Serveur("IdX");
	}
	Vivant=false;
	ListeMobile.splice(i, 1);
}

function Formatage_Mobile(Mobile:MovieClip, CLIP_JOUEUR:Boolean, JOUEUR:Boolean = false):void {
	if (CLIP_JOUEUR) {
		Mobile._A._CourseDroite.visible=false;
		Mobile._A._CourseGauche.visible=false;
		Mobile._A._StatiqueGauche.visible=false;
		Mobile._A.removeChild(Mobile._A._StatiqueGauche);
		Mobile._A.removeChild(Mobile._A._CourseGauche);
		Mobile._A.removeChild(Mobile._A._CourseDroite);
		//
		Mobile.removeChild(Mobile.Aaaah);
		Mobile.Score=0;
	}
	Mobile.Joueur=JOUEUR;
	Mobile.ClipJoueur=CLIP_JOUEUR;
	Mobile.DeplacementX=0;
	Mobile.SynchroX=0;
	Mobile.VitesseX=0;
	Mobile.VitesseY=0;
	Mobile.visible=false;
	Mobile.Actif=false;
	Mobile.Vide=0;
	Mobile.Apesanteur=false;

	Mobile.Distance=0;

	var Largeur:int;
	var Hauteur:int;
	var DecalageX:int;
	var DecalageY:int;

	if (CLIP_JOUEUR) {
		DecalageX=-10;
		DecalageY=0;
		Largeur=18;
		Hauteur=19;
	} else {
		DecalageX=int(Mobile.MinX);
		DecalageY=int(Mobile.MinY);
		Largeur=int(Mobile.width);
		Hauteur=int(Mobile.height);
	}

	Mobile.PH = new Object();
	Mobile.PH.x=int(Largeur/2)+DecalageX;
	Mobile.PH.y=DecalageY;

	Mobile.PHD = new Object();
	Mobile.PHD.x = int(Largeur-(Largeur/8))+DecalageX;
	Mobile.PHD.y=int(Hauteur/8)+DecalageY;

	Mobile.PD = new Object();
	Mobile.PD.x=Largeur+DecalageX;
	Mobile.PD.y=int(Hauteur/2)+DecalageY;

	Mobile.PBD = new Object();
	Mobile.PBD.x=Mobile.PHD.x;
	Mobile.PBD.y = int(Hauteur-(Hauteur/8))+DecalageY;

	Mobile.PB = new Object();
	Mobile.PB.x=Mobile.PH.x;
	Mobile.PB.y=Hauteur+DecalageY;

	Mobile.PBG = new Object();
	Mobile.PBG.x=int(Largeur/8)+DecalageX;
	Mobile.PBG.y=Mobile.PBD.y;

	Mobile.PG = new Object();
	Mobile.PG.x=DecalageX;
	Mobile.PG.y=Mobile.PD.y;

	Mobile.PHG = new Object();
	Mobile.PHG.x=Mobile.PBG.x;
	Mobile.PHG.y=Mobile.PHD.y;

	if (AffichagePoint) {
		Mobile.graphics.lineStyle(6, 0xFF0000, 1, true);
		Mobile.graphics.moveTo(Mobile.PH.x, Mobile.PH.y);
		Mobile.graphics.lineTo(Mobile.PH.x+1, Mobile.PH.y+1);
		Mobile.graphics.moveTo(Mobile.PHD.x, Mobile.PHD.y);
		Mobile.graphics.lineTo(Mobile.PHD.x+1, Mobile.PHD.y+1);
		Mobile.graphics.moveTo(Mobile.PD.x, Mobile.PD.y);
		Mobile.graphics.lineTo(Mobile.PD.x+1, Mobile.PD.y+1);
		Mobile.graphics.moveTo(Mobile.PBD.x, Mobile.PBD.y);
		Mobile.graphics.lineTo(Mobile.PBD.x+1, Mobile.PBD.y+1);
		Mobile.graphics.moveTo(Mobile.PB.x, Mobile.PB.y);
		Mobile.graphics.lineTo(Mobile.PB.x+1, Mobile.PB.y+1);
		Mobile.graphics.moveTo(Mobile.PBG.x, Mobile.PBG.y);
		Mobile.graphics.lineTo(Mobile.PBG.x+1, Mobile.PBG.y+1);
		Mobile.graphics.moveTo(Mobile.PG.x, Mobile.PG.y);
		Mobile.graphics.lineTo(Mobile.PG.x+1, Mobile.PG.y+1);
		Mobile.graphics.moveTo(Mobile.PHG.x, Mobile.PHG.y);
		Mobile.graphics.lineTo(Mobile.PHG.x+1, Mobile.PHG.y+1);
	}
}

function MAJ_Positions(LISTE:Array, NouvelleMap:Boolean = false):void {
	ListeMobile = new Array();
	var Nb:int=LISTE.length;
	for (var i:int = 0; i<Nb; i++) {
		var InfoCible:Array=LISTE[i].split(",");
		var NomCible:String=InfoCible[0];
		var ClipCible:MovieClip=_Monde._ListeJoueur[NomCible];
		ClipCible.Actif=false;
		ClipCible.DeplacementX=0;
		ClipCible.VitesseX=0;
		ClipCible.VitesseY=0;
		ClipCible.Score=int(InfoCible[4]);
		
		// Position joueurs
		if (NouvelleMap && Monde.DepartSpecial != null && Monde.DepartSpecial) { // Position spéciale de départ par le monde
			ClipCible.x = Monde.DepartX;
			ClipCible.y = Monde.DepartY;
		} else {
			ClipCible.x=int(InfoCible[1]);
			ClipCible.y=int(InfoCible[2]);
		}
		
		
		Zombification(ClipCible, false);
		if (InfoCible[3]=="1" && (GhostView || NomCible != "Ghost")) {
			Activation_Mobile(ClipCible);
		} else {
			Desactivation_Mobile(ClipCible);
		}
	}
	//
	if (_Monde._ListeJoueur.contains(ClipJoueur)) {
		_Monde._ListeJoueur.setChildIndex(ClipJoueur, _Monde._ListeJoueur.numChildren-1);
	}
	
	//Monde103 (Map officiel 62) Sinon le guide voit les pseudos
	if(_Monde.Anonyme == "anonyme"){// && ClipCible.Nom.text != "?" 
		ClipCible.Nom.text = "?";
		ClipCible.Nom.x =  -  ClipCible.Nom.width / 2;
	}
	
	if(_Monde.Anonyme != "anonyme" && ClipCible.Nom.text != NomCible){
		ClipCible.Nom.text = NomCible;
		ClipCible.Nom.x =  -  ClipCible.Nom.width / 2;
	}
}

var TimerMouvement:Timer=new Timer(2000);
TimerMouvement.addEventListener(TimerEvent.TIMER, Boucle_Actualisation_Mouvement);
function Boucle_Actualisation_Mouvement(E:Event):void {
	Envoie_Deplacement("MvA#"+int(ClipJoueur.x)+"#"+int(ClipJoueur.y)+"#"+int(ClipJoueur.VitesseX*100)+"#"+int(ClipJoueur.VitesseY*100));
}

function Activation_Mobile(MOBILE:MovieClip):void {
	if (! MOBILE.Actif) {
		MOBILE.Actif=true;
		MOBILE.VitesseX=0;
		MOBILE.VitesseY=0;
		ListeMobile.push(MOBILE);
		MOBILE.visible=true;
		
		// Reset modif event
		if (_root.Noel) {
			MOBILE.BonnetDroite.visible = false;
			MOBILE.BonnetGauche.visible = !_root.TeteAaaahSansEvent;
		} else if (_root.Halloween) {
			Diaboliser(MOBILE, false);
		}
		
		if (MOBILE.Joueur) {
			Vivant=true;
			TimerMouvement.start();
			_Monde._ListeJoueur.addChild(MOBILE);
		} else {
			_Monde._ListeJoueur.addChildAt(MOBILE, 0);
		}
	}
}

function Desactivation_Mobile(MOBILE:MovieClip):void {
	if (_Monde._ListeJoueur.contains(MOBILE)) {
		_Monde._ListeJoueur.removeChild(MOBILE);
	}
	MOBILE.Actif=false;
	MOBILE.visible=false;
	MOBILE.VitesseX=0;
	MOBILE.VitesseY=0;
	MOBILE.DeplacementX=0;
	TimerMouvement.stop();
	TimerMouvement.reset();
	var Nb:int=ListeMobile.length;
	for (var i:int = 0; i<Nb; i++) {
		if (ListeMobile[i]==MOBILE) {
			ListeMobile.splice(i, 1);
			MOBILE.visible=false;
			return;
		}
	}
	if (MOBILE.ClipJoueur) {
		MOBILE._A._CourseGauche.visible=false;
		MOBILE._A._CourseDroite.visible=false;
		MOBILE._A._StatiqueGauche.visible=false;
		MOBILE._A._StatiqueDroite.visible=true;
		if (MOBILE._A.numChildren!=0) {
			MOBILE._A.removeChildAt(0);
		}
		MOBILE._A.addChild(MOBILE._A._StatiqueDroite);
	}
	if (MOBILE.Joueur) {
		Vivant=false;
		DroiteEnCours=false;
		GaucheEnCours=false;
	}
}

function Pousse_Pousse(JOUEUR:MovieClip, PX:int, PY:int):void {
	JOUEUR.Aaaah.gotoAndPlay(2);
	JOUEUR.addChild(JOUEUR.Aaaah);
	if (! JOUEUR.Joueur) {
		var C1:int = ClipJoueur.x-(PX+4);
		var C2:int=ClipJoueur.y-PY;
		if (Math.sqrt(C1*C1+C2*C2)<50) {
			var Direction:String;
			if (ClipJoueur.x<PX) {
				Direction="0";
				Projection(ClipJoueur, ClipJoueur.x, ClipJoueur.y, false, JOUEUR);
			} else {
				Direction="1";
				Projection(ClipJoueur, ClipJoueur.x, ClipJoueur.y, true, JOUEUR);
			}
			_root.Envoie_Serveur("IdA#"+ClipJoueur.x+"#"+ClipJoueur.y+"#"+Direction);
		}
	}
}

var TimerProjection:Timer=new Timer(1000);
TimerProjection.addEventListener(TimerEvent.TIMER, Timer_Projection);
function Timer_Projection(E:Event):void {
	Incontrolable=false;
}

function Projection(JOUEUR:MovieClip, PX:int, PY:int, DROITE:Boolean, AUTEUR:MovieClip = null) {
	var Direction:int;
	if (DROITE) {
		Direction=1;
	} else {
		Direction=-1;
	}
	if (JOUEUR.Joueur) {
		TimerProjection.stop();
		TimerProjection.reset();
		TimerProjection.start();
		Incontrolable=true;
		Aaaah=true;
		AuteurAaaah=AUTEUR;
	}
	if (JOUEUR.Zombie) {
		JOUEUR.VitesseY=PuissanceAaaahY;
		JOUEUR.VitesseX+=PuissanceAaaahX*2*Direction;
	} else {
		JOUEUR.VitesseY=PuissanceAaaahY;
		JOUEUR.VitesseX+=PuissanceAaaahX*Direction;
	}
}
