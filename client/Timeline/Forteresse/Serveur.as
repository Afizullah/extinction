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

var $:String = String.fromCharCode(2);
var EntreeSalon:int;
var NePasEnvoyerPosition:Boolean = true;

// Liste des modes (_root.ModePartie) (cf Forteresse.java)
const MODE_F_AUCUN:int = 0;
const MODE_F_FRIGO:int = 1;
const MODE_F_FRAG:int = 2;
const MODE_F_KILL:int = 3;
const MODE_F_CROSS:int = 4;

_Monde.Quitter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Quitter);
function Clique_Quitter(E:Event):void {
	visible = false;
	NePasEnvoyerPosition = true;
	_root.Espionnage = false;
	stage.removeEventListener(KeyboardEvent.KEY_DOWN, Clavier_onPress);
	stage.removeEventListener(KeyboardEvent.KEY_UP, Clavier_onRelease);
	stage.removeEventListener(MouseEvent.MOUSE_UP, Glisseur_Relachement);
	stage.removeEventListener(MouseEvent.MOUSE_MOVE, Glisseur_Deplacement);
	stage.removeEventListener(Event.ENTER_FRAME, Synchronization_Physique);
	stage.removeEventListener(MouseEvent.MOUSE_MOVE, Boucle_Souris);
	stage.removeEventListener(MouseEvent.MOUSE_DOWN, Feu);
	stage.removeEventListener(MouseEvent.MOUSE_UP, Non_Feu);
	
	/// ----
	/*_Monde.Quitter.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Quitter);
	
	_Monde._Aide.Lien.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Lien);
	_Monde._Action.Arme0.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Arme0);
	_Monde._Action.Arme1.removeEventListener(MouseEvent.MOUSE_DOWN, Clique_Arme1);
	
	_Monde.Ascenseur2.Glisseur.Taille.removeEventListener(MouseEvent.MOUSE_DOWN, Glisseur_Pression);
	_Monde.Ascenseur2.Glisseur.Taille.removeEventListener(MouseEvent.MOUSE_OVER, Glisseur_Over);
	_Monde.Ascenseur2.Glisseur.Taille.removeEventListener(MouseEvent.MOUSE_OUT, Glisseur_Out);
	*/
	///



	TimerMemoire.stop();
	TimerMemoire.reset();
	stage.removeEventListener(TimerEvent.TIMER, Boucle_Memoire);
	TimerActualisation.stop();
	TimerActualisation.reset();
	stage.removeEventListener(TimerEvent.TIMER, Boucle_Actualisation);
	Mouse.show();
	
	CouleursPersonnalisées = false;
	NumPresidentRouge = "-1";
	NumPresidentBleu = "-1";
	VisionSur = -1;
	MapSauvegardable = true;
	
	_root.Envoie_Serveur("CxJ#0");
}

// Reception de données
function Reception(DATA:String):void {
	var Message:Array = DATA.split($);
	var Code:int = int(Message[0]);
	var Joueur:MovieClip;
	var Zombie:MovieClip;
	var Nb:int;
	var i:int;
	var k:int;

	// Actualisation
	if (Code == 1) {
		Joueur = ListeJoueur[Message[1]];
		if (! Joueur.ClipJoueur) {
			Joueur.x = int(Message[2]);
			Joueur.y = int(Message[3]);
			Joueur.VitesseX = int(Message[4]) / 100;
			Joueur.VitesseY = int(Message[5]) / 100;
		}
		return;
	}
	
	// Tire
	if (Code == 3) {
		Joueur = ListeJoueur[Message[1]];
		if (Message.length == 4) {
			var Ix:int = int(Message[2]);
			var Iy:int = int(Message[3]);
			if (! Joueur.ClipJoueur) {
				Affichage_Tire(Joueur, Ix, Iy);
			}
			Destruction_Case(int(Ix/10), int(Iy/10), true);
		} else {
			if (Message.length == 6) {
				var JoueurCible:MovieClip = ListeJoueur[Message[4]];
				if (JoueurCible.ClipJoueur) {
					ClipJoueur.Chargeur.Santé.text = Message[5];
				}
				if (Message[5] == "0") {
					if (! Joueur.ClipJoueur) {
						Affichage_Tire(Joueur, Message[2], Message[3], JoueurCible, true);
					}
					Joueur.Score += 1;
					Mort_Joueur(JoueurCible, Joueur);
					MAJ_Liste_Joueur();
				} else {
					if (! Joueur.ClipJoueur) {
						Affichage_Tire(Joueur, Message[2], Message[3], JoueurCible);
					}
				}
			}
		}
		return;
	}
	// Mouvement Stop
	if (Code==4) {
		Joueur=ListeJoueur[Message[1]];
		if (! Joueur.ClipJoueur) {
			Joueur.x=int(Message[2]);
			Joueur.y=int(Message[3]);
			Joueur.VitesseX=int(Message[4])/100;
			Joueur.VitesseY=int(Message[5])/100;
			Deplacement(Joueur, false, true);
		}
		return;
	}
	// Mouvement Droite
	if (Code==5) {
		Joueur=ListeJoueur[Message[1]];
		if (! Joueur.ClipJoueur) {
			Joueur.x=int(Message[2]);
			Joueur.y=int(Message[3]);
			Joueur.VitesseX=int(Message[4])/100;
			Joueur.VitesseY=int(Message[5])/100;
			Deplacement(Joueur, true, false);
		}
		return;
	}
	// Mouvement Gauche
	if (Code==6) {
		Joueur=ListeJoueur[Message[1]];
		if (! Joueur.ClipJoueur) {
			Joueur.x=int(Message[2]);
			Joueur.y=int(Message[3]);
			Joueur.VitesseX=int(Message[4])/100;
			Joueur.VitesseY=int(Message[5])/100;
			Deplacement(Joueur, false, false);
		}
		return;
	}
	// Mouvement Saut
	if (Code==7) {
		Joueur=ListeJoueur[Message[1]];
		if (! Joueur.ClipJoueur) {
			Joueur.x=int(Message[2]);
			Joueur.y=int(Message[3]);
			Joueur.VitesseX=int(Message[4])/100;
			Joueur.VitesseY=int(Message[5])/100;
			Saut(Joueur);
		}
		return;
	}
	// Construction
	if (Code==8) {
		if (VisionSur == -1 || VisionSur != parseInt(Message[4], 10)) { // Pas de joueur surveillé (/vision)
			Construction(Message[1], Message[2], Message[3] == "2" ? 0 : 1, 1);
		} else {
			Construction(Message[1], Message[2], -1, 999);
		}
		return;
	}
	// Jambombe
	if (Code==9) {
		Joueur=ListeJoueur[Message[2]];
		if (Message.length==5) {
			if (! Joueur.ClipJoueur) {
				Affichage_Tire(Joueur, Message[3], Message[4]);
			}
			Nouveau_Jambon(Message[1], Joueur, Message[3], Message[4], null);
		} else {
			var JoueurJambom:MovieClip=ListeJoueur[Message[5]];
			if (! Joueur.ClipJoueur) {
				Affichage_Tire(Joueur, Message[3], Message[4], JoueurJambom);
			}
			Nouveau_Jambon(Message[1], Joueur, Message[3], Message[4], JoueurJambom);
		}
		return;
	}
	// Explosion Jambombe
	if (Code==10) {
		Explosion_Jambombe(Message[1], Message[2], Message[3]);
		return;
	}
	// Recharge
	if (Code==11) {
		Joueur=ListeJoueur[Message[1]];
		if (! Joueur.ClipJoueur) {
			Joueur.Recharge=true;
		}
		return;
	}
	// Bonus d'armement
	if (Code == 12) {
		if (Message[1] == "0") {
			// Récupérer toutes ses balles
			Chargeur = 12 * ChargeurCoef + ChargeurBase;
			ChargeurNextValeur = 11 * ChargeurCoef + ChargeurBase;
			Changement_Arme(ArmeEnCours); // Actualiser les balles
		} else if (Message[1] == "1") {
			// + 1 jambombe
			++Jambombe;
			Changement_Arme(ArmeEnCours);
			_Monde._Action.Arme1.Texte.text=""+Jambombe;
		} else if (Message[1] == "2") {
			// full blocs
			CaseDispo = 1000;
			ATCaseDispo = 1;
		} else if (Message[1] == "3") {
			// + 2 jambombes
			Jambombe += 2;
			Changement_Arme(ArmeEnCours);
			_Monde._Action.Arme1.Texte.text=""+Jambombe;
		}
		return;
	}
	// Chat
	if (Code==13) {
		if (_root.Silence){
			return;
		}
		if (Message.length==4) {
			Nouveau_Message_Chat(Message[2], ListeJoueur[Message[1]], true);
		} else {
			Nouveau_Message_Chat(Message[2], ListeJoueur[Message[1]]);
		}
		return;
	}
	// Score
	if (Code==14) {
		var Frigo1:Boolean=Message[2]=="1";
		var VieFrigoGagnant:int=Message[3];
		if (Message[1]=="0") {
			if ((AttaqueRougeEnCours && Frigo1) || (!AttaqueRougeEnCours && !Frigo1)) {
				if (VieFrigoGagnant == -1) {
					Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#CB546B'>Rouge</font> remporte la partie !");
				}
				else {
					Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#CB546B'>Rouge</font> remporte la partie, sur le score de <font color='#CB546B'>"+VieFrigoGagnant+"</font> à <font color='#6C77C1'>0</font> !");
				}
			} else {
				if (VieFrigoGagnant == -1) {
					Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#6C77C1'>Bleue</font> remporte la partie !");
				}
				else {
					Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#6C77C1'>Bleue</font> remporte la partie, sur le score de <font color='#6C77C1'>"+VieFrigoGagnant+"</font> à <font color='#CB546B'>0</font> !");
				}
			}
		}
		if (Frigo1) {
			_Monde.Frigo1.text=Message[1];
		} else {
			_Monde.Frigo2.text=Message[1];
		}
		return;
	}
	// Respawn
	if (Code==15) {
		Joueur=ListeJoueur[Message[1]];
		Joueur.Vivant=true;
		if (Joueur.EquipeBleu) {
			if (AttaqueRougeEnCours) {
				Joueur.x=SpawnDéfenseX;
				Joueur.y=SpawnDéfenseY;
			} else {
				Joueur.x=SpawnAttaqueX;
				Joueur.y=SpawnAttaqueY;
			}
		} else {
			if (AttaqueRougeEnCours) {
				Joueur.x=SpawnAttaqueX;
				Joueur.y=SpawnAttaqueY;
			} else {
				Joueur.x=SpawnDéfenseX;
				Joueur.y=SpawnDéfenseY;
			}
		}
		Joueur.visible=true;
		if (Joueur.ClipJoueur) {
			Reset_Joueur();	
		}
		return;
	}
	// Mort jambombe
	if (Code==16) {
		Joueur=ListeJoueur[Message[1]];
		var Tueur:MovieClip=ListeJoueur[Message[2]];
		if (Tueur==Joueur) {
			Tueur.Score-=2;
			if (Tueur.Score<0) {
				Tueur.Score=0;
			}
		} else if (Tueur != null) {
			Tueur.Score+=1;
		}
		MAJ_Liste_Joueur();
		Mort_Joueur(Joueur, Tueur, true);
		return;
	}
	// Objectif
	if (Code==17) {
		Construction(Message[1], Message[2], 2, 3);
		return;
	}
	// Le joueur est une des cibles (=> insensibilité aux bombes)
	if (Code == 18) {
		EstUneCible = EstUneCible || (Message[1] == CodeJoueur);
		return;
	}
	// Case cadeau
	if (Code == 19) {
		ConstructionCadeau(Message[1], Message[2]);
		return;
	}
	// Changement de couleur
	if (Code == 20) {
		ChangerCouleur(ListeJoueur[Message[1]], int(Message[2]));
		return;
	}
	// Nouveau Joueur
	if (Code==30) {
		Nouveau_Joueur(Message[1], Message[2], Message[3], int(Message[4]));
		return;
	}
	// Deconnexion joueur
	if (Code==31) {
		Deco_Joueur(ListeJoueur[Message[1]], Message.length == 3);
		return;
	}
	// Nouvelle partie
	if (Code==35) {
		/* [1] Monde*
		   [2] Début partie serveur
		   [3] Attaque rouge en cours ("1" / "0")
		   [4] Double attaque ("1" / "0" / "2" (mode président))
		   [5] Durée de la partie
		   [6] Ancienne vie frigo 1
		   [7] Ancienne vie frigo 2
		   [8] Ancienne double attaque
		   [9] Président rouge
		   [10] Président bleu
		   [11] Si présent : reset les scores
		   
		   *Si monde perso :
		   
		   [1.0 - 1] 'P'
		   [1.1 - 1] Nombre de frigo
		   [1.2 -> ...] (Frigo1 :) X <$> Y <$> (Frigo2 :) X <$> Y <$>... (peut y en avoir 0 à autant qu'on veut - 2 max gérés pour le moment)
		   [1.3 - 1] Gravité
		   [1.4 - 4] Zone repsawn 1 : X1<$>Y1<$>X2<$>Y2
		   [1.5 - 4] Zone respawn 2 : X1<$>Y1<$>X2<$>Y2
		   [1.6 - 2] Position respawn rouge : X<$>Y
		   [1.7 - 2] Position respawn bleu : X<$>Y
		   [1.8 - 1] Bords (0 à 9) : <BordPlafond><BordSol>
		   
		*/
		var StarterMondePerso:int = 0;
		var NombreFrigos:int = -1;
		if (Message[1] == "P") { // Monde perso
			NombreFrigos = int(Message[2]);
			// 1-1000-400-400-0-0-20-20-40-40-70-70-30-30-100-100-55
			StarterMondePerso = 15 + NombreFrigos * 2; // 15 = 1 + 1 + 4 + 4 + 2 + 2 + 1 ('P' non compté car par défaut il y a déjà une valeur là)
		}
		
		
		var OldAttaqueRougeEnCours:Boolean=AttaqueRougeEnCours;
		AttaqueRougeEnCours=Message[StarterMondePerso + 3]=="1";
		TempsTotalPartie=Number(Message[StarterMondePerso + 5]);
		var DoubleAttaque:int = int(Message[StarterMondePerso + 4]);
		var OldVie1:int=Message[StarterMondePerso + 6];
		var OldVie2:int=Message[StarterMondePerso + 7];
		//var OldDoubleAttaque:Boolean=Message[8] == "1";
		var OldDoubleAttaque:int = int(Message[StarterMondePerso + 8]);
		//
		// Les anciens présidents précédents récupèrent leur bonne couleur (si un président s'est déco, son num vaudra "-1")
		if (NumPresidentRouge != "-1") {
			Joueur = ListeJoueur[NumPresidentRouge];
			Joueur.Nom.textColor=0xCB546B;
		}

		if (NumPresidentBleu != "-1") {
			Joueur = ListeJoueur[NumPresidentBleu];
			Joueur.Nom.textColor=0x6C77C1;
		}

		NumPresidentRouge = Message[StarterMondePerso + 9];
		NumPresidentBleu = Message[StarterMondePerso + 10];
		var PresidentRouge:String = "";
		var PresidentBleu:String = "";

		if (NumPresidentRouge != "-1") {
			Joueur = ListeJoueur[NumPresidentRouge];
			PresidentRouge = Joueur.NomJoueur;
			Joueur = ListeJoueur[NumPresidentBleu];
			PresidentBleu = Joueur.NomJoueur;
		}
		//
		var ResetScore:Boolean=Message.length==(StarterMondePerso + 12);
		//
		DebutPartie=getTimer();
		DebutPartieServeur=int(Message[StarterMondePerso + 2]);
		//
		EstUneCible = false;

		if (DoubleAttaque == 2) { // Mode président
			Nouveau_Message_Chat("<font color='#C9CD36'>Mode cible lancé, éliminez la cible adverse ! <font color='#CB546B'>"+PresidentRouge+"</font> est la cible côté rouge et <font color='#6C77C1'>"+PresidentBleu+"</font> est celle côté bleu !");
			ListeJoueur[NumPresidentRouge].Nom.textColor=0xC400C4;
			ListeJoueur[NumPresidentBleu].Nom.textColor=0x00B3B3;
		} else if(OldVie1 != 0 && OldVie2 != 0) {
			if (OldDoubleAttaque == 1) {
				if (OldAttaqueRougeEnCours) {
					Nouveau_Message_Chat("<font color='#C9CD36'>Fin du temps imparti sur le score de <font color='#6C77C1'>"+OldVie1+"</font> à <font color='#CB546B'>"+OldVie2+"</font>.");
				}
				else {
					Nouveau_Message_Chat("<font color='#C9CD36'>Fin du temps imparti sur le score de <font color='#CB546B'>"+OldVie1+"</font> à <font color='#6C77C1'>"+OldVie2+"</font>.");
				}
			} else {
				if (OldAttaqueRougeEnCours) {
					Nouveau_Message_Chat("<font color='#C9CD36'>Fin du temps imparti sur le score de <font color='#6C77C1'>"+OldVie1+"</font>.");				
				}
				else {			
					Nouveau_Message_Chat("<font color='#C9CD36'>Fin du temps imparti sur le score de <font color='#CB546B'>"+OldVie1+"</font>.");				
				}
			}
			
			if (DoubleAttaque == 1) {
				Nouveau_Message_Chat("<font color='#C9CD36'>Tout le monde attaque ! Détruisez le frigo ennemi et protégez le vôtre.");
			} else if (AttaqueRougeEnCours) {
				Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#CB546B'>Rouge</font> à l'attaque du frigo !");
			} else {
				Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#6C77C1'>Bleue</font> à l'attaque du frigo !");
			}
		}
		
		if (NombreFrigos != 0) { // = -1 si monde non perso
			_Monde.Frigo1.text="40";
			if (DoubleAttaque) {
				_Monde.Frigo2.text="40";
				_Monde.Tiret.text="-";
			} else {
				_Monde.Frigo2.text="";
				_Monde.Tiret.text="";
			}
			if (AttaqueRougeEnCours) {
				_Monde.Frigo1.textColor=0x6C77C1;
				_Monde.Frigo2.textColor=0xCB546B;
			} else {
				_Monde.Frigo1.textColor=0xCB546B;
				_Monde.Frigo2.textColor=0x6C77C1;
			}
		} else {
			_Monde.Frigo1.text="";
			_Monde.Frigo2.text="";
			_Monde.Tiret.text="";
		}

		ClipJoueur.Recharge=false;// suffit-ce?
		//
		ListeJambon = new Array();
		Incontrolable=false;
		Controlable=5;
		
		if (StarterMondePerso == 0) { // Monde habituel
			Chargement_Monde(Message[1]);
		} else { // Monde perso
			Chargement_Monde(-2, Message.slice(2 /* Pas besoin du 'P' */, 2 + StarterMondePerso));
		}
		
		Nb=ClipListeJoueur.numChildren;
		for (i = 0; i<Nb; i++) {
			Joueur=MovieClip(ClipListeJoueur.getChildAt(i));
			while (Joueur._ListeEffet.numChildren != 0) {
				Joueur._ListeEffet.removeChildAt(0);
			}
			Joueur.AnimEnCours.gotoAndStop(1);
			Joueur.AnimEnCours.visible=false;
			Joueur.AnimEnCours=Joueur.SD;
			Joueur.AnimEnCours.gotoAndPlay(1);
			Joueur.AnimEnCours.visible=true;
			if (ResetScore) {
				Joueur.Score=0;
			}
			Joueur.Vivant=true;
			Joueur.visible=true;
			//
			if (Joueur.EquipeBleu) {
				if (AttaqueRougeEnCours) {
					Joueur.x=SpawnDéfenseX;
					Joueur.y=SpawnDéfenseY;
				} else {
					Joueur.x=SpawnAttaqueX;
					Joueur.y=SpawnAttaqueY;
				}
			} else {
				if (AttaqueRougeEnCours) {
					Joueur.x=SpawnAttaqueX;
					Joueur.y=SpawnAttaqueY;
				} else {
					Joueur.x=SpawnDéfenseX;
					Joueur.y=SpawnDéfenseY;
				}
			}
			Deplacement(Joueur, false, true);
		}
		
		Reset_Joueur();
		MAJ_Liste_Joueur();
		Changer_Focus(_root.FocusSur);
		return;
	}

	// Classement touche frigo
	if (Code==38) {
		Message.shift();
		MAJ_ToucheFrigo(Message);
		return;
	}
	

	// Changement Salon
	if (Code==90) {
		TimerActualisation.stop();
		TimerActualisation.reset();
		Nouveau_Message_Chat("Vous entrez dans le salon : <font color='#C9CD36'>"+Message[1]+"</font>.\nTapez /salon NomDuSalon pour créer ou rejoindre un salon. Tapez /salon pour retourner sur le salon principal.");
		return;
	}
	// Reception Liste Joueur
	if (Code==91) {
		EntreeSalon=getTimer();
		Message.shift();
		Initialisation_Joueurs(Message);
		TimerActualisation.start();
		ATCoordCompteur = 0;
		return;
	}
	// Chargement Monde En Cours De Partie
	if (Code==99) {
		DebutPartie=getTimer()-int(Message[1]);
		EstUneCible = false;
		
		if (Message[12] != "P") {
			Chargement_Monde(int(Message[12]));
		} else { // Monde personnalisé
			Chargement_Monde(-2, Message.slice(13 /* Pas besoin du 'P' */));
		}
		

		//
		var Couleurs:Array = Message[3].split(";"); // 3->6 ; 7 -> 4 => -3
		CouleursPerso[0] = parseInt(Couleurs[0], 16);
		CouleursPerso[1] = parseInt(Couleurs[1], 16);
		CouleursPerso[2] = parseInt(Couleurs[2], 16);
		CouleursPerso[3] = parseInt(Couleurs[3], 16);
		
		CouleursPerso[4] = parseInt(Couleurs[4], 16);
		CouleursPerso[5] = parseInt(Couleurs[5], 16);
		
		CouleursPersonnalisées = (CouleursPerso[0] != CouleursBase[0]
								 || CouleursPerso[1] != CouleursBase[1]
								 || CouleursPerso[2] != CouleursBase[2]
								 || CouleursPerso[3] != CouleursBase[3]
								 || CouleursPerso[4] != CouleursBase[4]
								 || CouleursPerso[5] != CouleursBase[5]);

		MapSauvegardable = (Message[11] == "1");
		Chargement_Liste_Case(Message[2].split(""));
		
		AttaqueRougeEnCours=Message[4]=="1";
		TempsTotalPartie=Number(Message[5]);

		var PresiRouge:String = "";
		var PresiBleu:String = "";

		var DeuxFrigos=Message[8] != "";
		_Monde.Frigo1.text=Message[7];
		if (DeuxFrigos) {
			_Monde.Frigo2.text=Message[8];
			_Monde.Tiret.text="-";
		} else {
			_Monde.Frigo2.text="";
			_Monde.Tiret.text="";
		}
		if (Message[9] != "-1") {
			NumPresidentRouge = Message[9];
			NumPresidentBleu = Message[10];

			Joueur = ListeJoueur[NumPresidentRouge];
			PresiRouge = Joueur.NomJoueur;
			Joueur = ListeJoueur[NumPresidentBleu];
			PresiBleu = Joueur.NomJoueur;
			
			ListeJoueur[NumPresidentRouge].Nom.textColor=0xC400C4;
			ListeJoueur[NumPresidentBleu].Nom.textColor=0x00B3B3;
		}
		
		if (AttaqueRougeEnCours) {
			_Monde.Frigo1.textColor=0x6C77C1;
			_Monde.Frigo2.textColor=0xCB546B;
		} else {
			_Monde.Frigo1.textColor=0xCB546B;
			_Monde.Frigo2.textColor=0x6C77C1;
		}

		if (Message[6]!="") {
			Nouveau_Message_Chat("<font color='#C9CD36'>Vous entrez dans le salon dessin de "+Message[6]+".</font>");
		} else if (PresiRouge != "") {
			Nouveau_Message_Chat("<font color='#C9CD36'>Mode cible lancé, éliminez la cible adverse ! <font color='#CB546B'>"+PresiRouge+"</font> est la cible côté rouge et <font color='#6C77C1'>"+PresiBleu+"</font> est celle côté bleu !");
		}else if (DeuxFrigos) {
			Nouveau_Message_Chat("<font color='#C9CD36'>Tout le monde attaque ! Détruisez le frigo ennemi et protégez le vôtre.");
		} else if (AttaqueRougeEnCours) {
			Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#CB546B'>Rouge</font> à l'attaque du frigo !");
		} else {
			Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#6C77C1'>Bleue</font> à l'attaque du frigo !");
		}
		//
		if (ClipJoueur.EquipeBleu) {
			EquipeCible=ListeRouge;
			if (AttaqueRougeEnCours) {
				ClipJoueur.x=SpawnDéfenseX;
				ClipJoueur.y=SpawnDéfenseY;
			} else {
				ClipJoueur.x=SpawnAttaqueX;
				ClipJoueur.y=SpawnAttaqueY;
			}
		} else {
			EquipeCible=ListeBleu;
			if (AttaqueRougeEnCours) {
				ClipJoueur.x=SpawnAttaqueX;
				ClipJoueur.y=SpawnAttaqueY;
			} else {
				ClipJoueur.x=SpawnDéfenseX;
				ClipJoueur.y=SpawnDéfenseY;
			}
		}
		
		Changer_Focus(_root.FocusSur);
		
		//
		return;
	}
	
	/*if (Code==99) {
		DebutPartie=getTimer()-int(Message[2]);
		EstUneCible = false;
		
		//trace("Message[1] : " + Message[14]);
		
		Chargement_Monde(Message[1].split(";"));

		//
		CouleursPerso[0] = parseInt(Message[4], 16);
		CouleursPerso[1] = parseInt(Message[5], 16);
		CouleursPerso[2] = parseInt(Message[6], 16);
		CouleursPerso[3] = parseInt(Message[7], 16);
		
		CouleursPersonnalisées = true;

		Chargement_Liste_Case(Message[3].split(""));
		
		AttaqueRougeEnCours=Message[8]=="1";
		TempsTotalPartie=Number(Message[9]);

		var PresiRouge:String = "";
		var PresiBleu:String = "";

		var DeuxFrigos=Message[12] != "";
		_Monde.Frigo1.text=Message[11];
		if (DeuxFrigos) {
			_Monde.Frigo2.text=Message[12];
			_Monde.Tiret.text="-";
		} else {
			_Monde.Frigo2.text="";
			_Monde.Tiret.text="";
		}
		if (Message[13] != "-1") {
			NumPresidentRouge = Message[13];
			NumPresidentBleu = Message[14];

			Joueur = ListeJoueur[NumPresidentRouge];
			PresiRouge = Joueur.NomJoueur;
			Joueur = ListeJoueur[NumPresidentBleu];
			PresiBleu = Joueur.NomJoueur;
			
			ListeJoueur[NumPresidentRouge].Nom.textColor=0xC400C4;
			ListeJoueur[NumPresidentBleu].Nom.textColor=0x00B3B3;
		}
		if (AttaqueRougeEnCours) {
			_Monde.Frigo1.textColor=0x6C77C1;
			_Monde.Frigo2.textColor=0xCB546B;
		} else {
			_Monde.Frigo1.textColor=0xCB546B;
			_Monde.Frigo2.textColor=0x6C77C1;
		}

		if (Message[10]!="") {
			Nouveau_Message_Chat("<font color='#C9CD36'>Vous entrez dans le salon dessin de "+Message[10]+".</font>");
		} else if (PresiRouge != "") {
			Nouveau_Message_Chat("<font color='#C9CD36'>Mode cible lancé, éliminez la cible adverse ! <font color='#CB546B'>"+PresiRouge+"</font> est la cible côté rouge et <font color='#6C77C1'>"+PresiBleu+"</font> est celle côté bleu !");
		}else if (DeuxFrigos) {
			Nouveau_Message_Chat("<font color='#C9CD36'>Tout le monde attaque ! Détruisez le frigo ennemi et protégez le vôtre.");
		} else if (AttaqueRougeEnCours) {
			Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#CB546B'>Rouge</font> à l'attaque du frigo !");
		} else {
			Nouveau_Message_Chat("<font color='#C9CD36'>L'équipe <font color='#6C77C1'>Bleue</font> à l'attaque du frigo !");
		}
		//
		if (ClipJoueur.EquipeBleu) {
			EquipeCible=ListeRouge;
			if (AttaqueRougeEnCours) {
				ClipJoueur.x=SpawnDéfenseX;
				ClipJoueur.y=SpawnDéfenseY;
			} else {
				ClipJoueur.x=SpawnAttaqueX;
				ClipJoueur.y=SpawnAttaqueY;
			}
		} else {
			EquipeCible=ListeBleu;
			if (AttaqueRougeEnCours) {
				ClipJoueur.x=SpawnAttaqueX;
				ClipJoueur.y=SpawnAttaqueY;
			} else {
				ClipJoueur.x=SpawnDéfenseX;
				ClipJoueur.y=SpawnDéfenseY;
			}
		}
		//
		return;
	}*/
	
	if (Code == 100) { // Dégâts concubins
		Joueur = ListeJoueur[Message[1]];
		if (Joueur.ClipJoueur) {
			ClipJoueur.Chargeur.Santé.text = Message[2];
		}
		AfficherSang(15, 5, Joueur, 0);
		return;
	}
	
	if (Code == 101) { // Mort coeur brisé
		Joueur = ListeJoueur[Message[1]];
		Tueur = ListeJoueur[Message[2]];
		Tueur.Score++;
		
		MAJ_Liste_Joueur();
		Mort_Joueur(Joueur, Tueur, false, true);
		return;
	}
	
	//
	_root.Reception_Spéciale(DATA);
}

function Initialisation_Joueurs(LISTE:Array):void {
	_Monde._Texte.htmlText = _root.TexteEnCours;
	_Monde._Texte.scrollV = _Monde._Texte.maxScrollV;
	
	_root.Inaction=0;
	//
	while (ClipListeJoueur.numChildren != 0) {
		ClipListeJoueur.removeChildAt(0);
	}
	//
	ListeMobile = new Array();
	ListeJoueur = new Array();
	ListeBleu = new Array();
	ListeRouge = new Array();
	//
	var NbJoueur:int=LISTE.length;
	for (var i:int = 0; i<NbJoueur; i=i+9) {
		var Joueur:MovieClip;		
		var SEXE:int = int(LISTE[i+6]);
		if (_root.OldSprite&&SEXE<2){
			SEXE+=2;		
		}
		if (SEXE==4 && _root.VoirJack) {
			Joueur = new $JoueurJack();
			Joueur.Femme=false;
		} else if (SEXE==0) {
			Joueur = new $JoueurFemme();
			Joueur.Femme=true;
		} else if (SEXE==1) {
			Joueur =  new $JoueurHomme();
			Joueur.Femme=false;
		} else if (SEXE==2) {
			Joueur =  new $JoueurFemme2();
			Joueur.Femme=false;
		} else if (SEXE == 5 && (_root.MODO || _root.ARBITRE)) {
			Joueur = new $JoueurLucky();
			Joueur.Femme = false;
		} else { // 3
			Joueur =  new $JoueurHomme2();
			Joueur.Femme=false;
		}
		
		if(_root.Halloween && SEXE==4 && _root.VoirJack) {
			Joueur.NomJoueur=LISTE[i] + " l'éventreur";
		}
		else {
			Joueur.NomJoueur=LISTE[i];
		}
		
		Joueur.ClipJoueur = (i == 0);
		
		Joueur.Code=LISTE[i+1];
		ListeJoueur[Joueur.Code]=Joueur;
		Joueur.Score=int(LISTE[i+2]);
		Joueur.ToucheFrigo=int(LISTE[i+3]);
		
		Joueur.Nom.text=Joueur.NomJoueur;
		Joueur.Bulle.visible=false;
		
		// Equipe
		SetCouleurJoueur(Joueur, int(LISTE[i + 7]));
		
		Joueur.x=LISTE[i+4];
		Joueur.y=LISTE[i+5];
		
		ClipListeJoueur.addChild(Joueur);
		// Vivant
		if (LISTE[i+8]=="0") {
			Joueur.Vivant=false;
			Joueur.visible=false;
		} else {
			Joueur.Vivant=true;
		}
		// Equipe
		/*if (LISTE[i+7]=="1") {
			if (_root.Halloween) {
				Joueur.Nom.textColor = 0x973D00;
			} else {
				Joueur.Nom.textColor = 0xCB546B;
			}
			
			Joueur.EquipeBleu=false;
			ListeRouge.push(Joueur);
			if (i==0) {
				EquipeCible=ListeBleu;
				if (AttaqueRougeEnCours) {
					Joueur.x=SpawnAttaqueX;
					Joueur.y=SpawnAttaqueY;
				} else {
					Joueur.x=SpawnDéfenseX;
					Joueur.y=SpawnDéfenseY;
				}
			}
		} else {
			if (_root.Halloween) {
				Joueur.Nom.textColor = 0x26754E;
			} else {
				Joueur.Nom.textColor = 0x6C77C1;
			}
			Joueur.EquipeBleu=true;
			ListeBleu.push(Joueur);
			if (i==0) {
				EquipeCible=ListeRouge;
				if (AttaqueRougeEnCours) {
					Joueur.x=SpawnDéfenseX;
					Joueur.y=SpawnDéfenseY;
				} else {
					Joueur.x=SpawnAttaqueX;
					Joueur.y=SpawnAttaqueY;
				}
			}
		}*/
		
		if (_root.SaintValentin && Joueur.Nom == _root.StVAmoureux) {
			Joueur.Nom.textColor = 0xF24AB8;
		}
		
		if (i==0) {
			ClipJoueur=Joueur;
			CodeJoueur=ClipJoueur.Code;
			NomJoueur=Joueur.NomJoueur;
			Joueur.Chargeur = new $Chargeur();
			Joueur.addChild(Joueur.Chargeur);
			Joueur.Chargeur.Balle.text="12";
			//Joueur.Chargeur.Santé.text="10";
			if (_root.SaintValentin && StValAmoureuxPresent()) {
				Joueur.Chargeur.Santé.text = "20";
			} else {
				Joueur.Chargeur.Santé.text = "10";
			}
			Joueur.Nom.y=-30;
			Joueur.Bulle.y=-45;
			Joueur.Chargeur.x=-11;
			Joueur.Chargeur.y=-9;
			Joueur.Précision=10;
			Formatage_Mobile(ClipJoueur, true, true);
			stage.addEventListener(MouseEvent.MOUSE_MOVE, Boucle_Souris);
			Boucle_Souris(null, false);
			if (!_root.NoLag){			
			_Réticule.visible=true;
			Mouse.hide();			
			}
			Reset_Joueur();
		} else {
			Formatage_Mobile(Joueur, true, false);
		}
	}
	//
	_Monde.visible=true;
	//
	MAJ_Liste_Joueur();
	Ascenseur_Reset(_Monde.Ascenseur2);
	//
	stage.addEventListener(MouseEvent.MOUSE_DOWN, Feu);
	stage.addEventListener(MouseEvent.MOUSE_UP, Non_Feu);
}

function Nouveau_Joueur(NOM:String, CODE:int, SEXE:int, ROUGE:int):void {
	var Joueur:MovieClip;
	if (_root.OldSprite&&SEXE<2){
		SEXE+=2;		
	}
	if (SEXE==4 && _root.VoirJack) {
		Joueur = new $JoueurJack();
		Joueur.Femme = false;
	} else if (SEXE == 0) {
		Joueur = new $JoueurFemme();
		Joueur.Femme=true;
	} else if (SEXE == 1) {
		Joueur =  new $JoueurHomme();
		Joueur.Femme=false;
	} else if (SEXE == 2) {
		Joueur =  new $JoueurFemme2();
		Joueur.Femme = true;
	} else if (SEXE == 5 && (_root.MODO || _root.ARBITRE)) {
		Joueur = new $JoueurLucky();
		Joueur.Femme = false;
	} else { // 3
		Joueur =  new $JoueurHomme2();
		Joueur.Femme = false;
	}
	ListeJoueur[CODE]=Joueur;
	Joueur.Code=CODE;
	if(_root.Halloween && SEXE==4 && _root.VoirJack) {
		NOM += " l'éventreur";
	}
	Joueur.NomJoueur=NOM;
	Joueur.Score=0;
	Joueur.ToucheFrigo=0;
	Joueur.Nom.text=NOM;
	Joueur.Bulle.visible=false;
	Formatage_Mobile(Joueur, true, false);
	Joueur.Vivant=true;
	ClipListeJoueur.addChild(Joueur);
	/*if (ROUGE) { // Note : remettre ROUGE en booléen & tester "== "1"" pour la co d'un joueur
		Joueur.EquipeBleu=false;
		
		if (_root.Halloween) {
			Joueur.Nom.textColor = 0x973D00;
		} else {
			Joueur.Nom.textColor = 0xCB546B;
		}
		
		ListeRouge.push(Joueur);
	} else {
		Joueur.EquipeBleu=true;
		
		if (_root.Halloween) {
			Joueur.Nom.textColor = 0x26754E;
		} else {
			Joueur.Nom.textColor = 0x6C77C1;
		}
		ListeBleu.push(Joueur);
	}*/
	
	SetCouleurJoueur(Joueur, ROUGE);
	
	if (_root.SaintValentin && NOM == _root.StVAmoureux) {
		Joueur.Nom.textColor = 0xF24AB8;
		
		Nouveau_Message_Chat("Connexion de votre tendre "+NOM+".");
	} else if (_root.AffichageConnexions) {
		Nouveau_Message_Chat("Connexion de "+NOM+".");
	}
	
	/*if (Joueur.EquipeBleu) {
		if (AttaqueRougeEnCours) {
			Joueur.x=SpawnDéfenseX;
			Joueur.y=SpawnDéfenseY;
		} else {
			Joueur.x=SpawnAttaqueX;
			Joueur.y=SpawnAttaqueY;
		}
	} else {
		if (AttaqueRougeEnCours) {
			Joueur.x=SpawnAttaqueX;
			Joueur.y=SpawnAttaqueY;
		} else {
			Joueur.x=SpawnDéfenseX;
			Joueur.y=SpawnDéfenseY;
		}
	}*/
	MAJ_Liste_Joueur();
}

function SetCouleurJoueur(Joueur:MovieClip, Couleur:int, UpdateListeJoueurs:Boolean=false) {
	var index:int;
	
	if (Couleur == 1) {
		Joueur.EquipeBleu = false;
		
		if (_root.Halloween) {
			Joueur.Nom.textColor = 0x973D00;
		} else {
			Joueur.Nom.textColor = 0xCB546B;
		}
		
		if (AttaqueRougeEnCours) {
			Joueur.x=SpawnAttaqueX;
			Joueur.y=SpawnAttaqueY;
		} else {
			Joueur.x=SpawnDéfenseX;
			Joueur.y=SpawnDéfenseY;
		}
		
		index = ListeBleu.indexOf(Joueur);
		if (index != -1) {
			ListeBleu.splice(index, 1);
		}
		
		if (Joueur.ClipJoueur) {
			EquipeCible = ListeBleu;
		}
		
		ListeRouge.push(Joueur);
	} else if (Couleur == 2){
		Joueur.EquipeBleu=true;
		
		if (_root.Halloween) {
			Joueur.Nom.textColor = 0x26754E;
		} else {
			Joueur.Nom.textColor = 0x6C77C1;
		}
		
		if (AttaqueRougeEnCours) {
			Joueur.x=SpawnDéfenseX;
			Joueur.y=SpawnDéfenseY;
		} else {
			Joueur.x=SpawnAttaqueX;
			Joueur.y=SpawnAttaqueY;
		}
		
		index = ListeRouge.indexOf(Joueur);
		if (index != -1) {
			ListeRouge.splice(index, 1);
		}
		
		if (Joueur.ClipJoueur) {
			EquipeCible = ListeRouge;
		}
		
		ListeBleu.push(Joueur);
	}
	
	if (UpdateListeJoueurs) {
		MAJ_Liste_Joueur();
	}
}

function Deco_Joueur(JOUEUR:MovieClip, BANNI:Boolean = false):void {
	if (CibleCamera==JOUEUR) {
		CibleCamera=ClipJoueur;
		VisionSur = -1;
		_root.Espionnage = false;
	}
	
	if (JoueurFocus == JOUEUR) {
		JoueurFocus = null;
	}
	//
	ClipListeJoueur.removeChild(JOUEUR);
	Desactivation(JOUEUR);
	ListeJoueur[JOUEUR.Code]=null;
	//
	var NbB:int=ListeBleu.length;
	for (var B:int = 0; B<NbB; B++) {
		if (ListeBleu[B]==JOUEUR) {
			ListeBleu.splice(B, 1);
			break;
		}
	}
	var NbR:int=ListeRouge.length;
	for (var R:int = 0; R<NbR; R++) {
		if (ListeRouge[R]==JOUEUR) {
			ListeRouge.splice(R, 1);
			break;
		}
	}
	//
	if (JOUEUR.Code == parseInt(NumPresidentRouge)) {
		NumPresidentRouge = "-1";
	}
	if (JOUEUR.Code == parseInt(NumPresidentBleu)) {
		NumPresidentBleu = "-1";
	}
	//
	if (BANNI) {
		//Nouveau_Message_Chat("<font color='#CB546B'>"+JOUEUR.NomJoueur+" a été banni.");
	} else {
		if (_root.AffichageConnexions) {
			Nouveau_Message_Chat(JOUEUR.NomJoueur+" s'est déconnecté(e).");
		}
	}
	MAJ_Liste_Joueur();
}

var TimerActualisation:Timer=new Timer(2000);
TimerActualisation.addEventListener(TimerEvent.TIMER, Boucle_Actualisation);
function Boucle_Actualisation(E:Event):void {
	// Case Morte
	//var NbCase:int = ListeCaseMorte.length;
	//for (var i:int = 0; i<NbCase; i++) {
	//var Case:MovieClip = ListeCaseMorte[i];
	//if (getTimer()-Case.Temps > 5000) {
	//ClipListeCaseMorte.removeChild(Case);
	//ListeCaseMorte.splice(i, 1);
	//i--;
	//NbCase--;
	//var X:int = Case.PX;
	//var Y:int = Case.PY;
	//if (ListeCase[X][Y] == 8) {
	//ListeCase[X][Y] = 0;
	//}
	//}
	//}
	//
	if (getTimer()-DerniereActualisation>1000&&! Incontrolable&&Vivant && !NePasEnvoyerPosition) {
		_root.Envoie_Serveur(Codage(1)+$+ClipJoueur.x+$+ClipJoueur.y+$+int(ClipJoueur.VitesseX*100)+$+int(ClipJoueur.VitesseY*100));
	}
	//MAJ_Liste_Joueur();
}

function Codage(NUM:uint):String {
	return String.fromCharCode(NUM);
}