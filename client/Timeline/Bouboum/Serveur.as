import flash.display.MovieClip;

var SalonCible:String;

_Monde.Quitter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Quitter);
function Clique_Quitter(E:Event):void {
	visible = false;
	_root.Envoie_Serveur("CxJ#0");
	stage.removeEventListener(KeyboardEvent.KEY_DOWN, Clavier_onPress);
	stage.removeEventListener(KeyboardEvent.KEY_UP, Clavier_onRelease);
	stage.removeEventListener(Event.ENTER_FRAME, Synchronization_Physique);
	LsPseudosEquipe2Elo = new Array();
	LsPseudosModeTeam = new Array();
	idEquipe = -1;
	immortel = false;
}

function Reception(DATA:String):void {
	var Message:Array = DATA.split("#");
	var Code:String = Message[0];
	var SubCode:String = Code.substr(0,2);
	var Joueur:MovieClip;
	var i:int;
	var k:int;

	// mouvement
	if (SubCode == "Mv") {
		Joueur = ClipListeJoueur[Message[3]];
		if (! Joueur.ClipJoueur) {
			// Droite
			if (Code == "MvD") {
				Deplacement(Joueur, int(Message[1]), int(Message[2]), true, false, false, false);
				return;
			}
			// Gauche
			if (Code == "MvG") {
				Deplacement(Joueur, int(Message[1]), int(Message[2]), false, true, false, false);
				return;
			}
			// Haut
			if (Code == "MvH") {
				Deplacement(Joueur, int(Message[1]), int(Message[2]), false, false, true, false);
				return;
			}
			// Bas
			if (Code == "MvB") {
				Deplacement(Joueur, int(Message[1]), int(Message[2]), false, false, false, true);
				return;
			}
			// Stop
			if (Code == "MvS") {
				Arret(Joueur, int(Message[1]), int(Message[2]), false);
			}
		}
		return;
	}
	//
	//
	// Bombe
	if (SubCode == "Bo") {
		// Pose bombe
		if (Code == "BoP") {
			Pose_Bombe(int(Message[1]), int(Message[2]), ClipListeJoueur[Message[3]], int(Message[4]));
			return;
		}
		// Explosion
		if (Code == "BoE") {
			Explosion(int(Message[1]), int(Message[2]), int(Message[3]), int(Message[4]), Message[5]);
			return;
		}
		// Mort
		if (Code == "BoM") {
			Joueur = ClipListeJoueur[Message[1]];
			var Tueur:MovieClip = ClipListeJoueur[Message[2]];
			//if (Joueur == Tueur) {
			//if (Joueur.Score != 0) {
			//Joueur.Score -= 2;
			//if (Joueur.Score < 0) {
			//Joueur.Score = 0;
			//}
			//MAJ_Liste_Joueur();
			//}
			//} else {
			//Tueur.Score += 2;
			//MAJ_Liste_Joueur();
			//}
			Mort_Joueur(Joueur, (Joueur.Fantome && !Joueur.ClipJoueur));
			
			if (Joueur.Nom.text == NomJoueur) { // Joueur tué
				TueurJoueur = Message[2];
			} else if (Tueur.Nom.text == NomJoueur) { // Le joueur a tué
				ListeTues.push(Joueur.Nom.text);
			}
			
			/*var Frag:MovieClip = new $Frag();
			Frag.Effet.Texte.text = Message[2];
			Frag.y = Joueur.y + 10;
			Frag.x = Joueur.x - 30;
			_Monde._Partie._Niveau.addChild(Frag);*/
			Affiche_Tueur_Sur_Map(Joueur, Message[2]);
			/*var Mort:MovieClip;
			if (Joueur.Orange) {
				Mort = new $MortOrange();
			} else {
				Mort = new $MortBlanche();
			}
			Mort.x = Joueur.x + 9;
			Mort.y = Joueur.y + 9;
			_Monde._Partie._Niveau.addChild(Mort);*/

			// Liste de frag en bas à droite
			var FragBoum:MovieClip = new $FragForteresse();
			//FragBoum.Effet.Texte1.textColor = 0x6C77C1;
			FragBoum.Effet.Texte1.text = Message[2];
			FragBoum.Effet.Texte2.text = Joueur.Nom.text;
			FragBoum.Effet.Lait.visible = false;
			FragBoum.Effet.Jambon.visible = false;
			
			if (Message[4] == "1") {
				// Mort joueurs liés St Valentin via la Bombe cupidonne
				FragBoum.Effet.Bombe.visible = false;
			} else { // Mort normale
				FragBoum.Effet.CoeurStVal.visible = false;
			}
			
			FragBoum.y = 20 * _Monde._ListeFrag.numChildren;
			_Monde._ListeFrag.addChild(FragBoum);

			return;
		}
		// Augmentation Bombe
		if (Code == "BoU") {
			/* 0.83b
			if (Message.length == 2) {
				_Monde._Partie.Puissance.text = ""+(int(_Monde._Partie.Puissance.text)+1);
			} else {
				BombeDisponible += 1;
				_Monde._Partie.NombreBombe.text=BombeDisponible;
			}
			*/
			if (Message[1] == "0") {
				BombeDisponible += parseInt(Message[2]);
				_Monde._Partie.NombreBombe.text=BombeDisponible;
			} else {
				_Monde._Partie.Puissance.text = ""+(int(_Monde._Partie.Puissance.text)+parseInt(Message[2]));
			}
			return;
		}
		// Apparition d'un bloc en cours de partie
		if (Code == "BoC") {
			if (! ClipAttente.visible) {
				var TypeCase:int=int(Message[1]);
				var C:int=int(Message[2]);
				var L:int=int(Message[3]);
				
				if (TypeCase == CASE_GLACE_EXPLOSABLE) {
					MondeBrique[C][L].gotoAndStop(Sprite_SolGlace);
					NiveauEnCours[C][L] = CASE_GLACE_EXPLOSABLE;
				}
			}
			return;
		}
		// Bonus
		if (Code=="BoB") {
			if (! ClipAttente.visible) {
				var Type:String=Message[1];
				var C:int=int(Message[2]);
				var L:int=int(Message[3]);
				NiveauEnCours[C][L]=-1;
				if (Type=="0") {
					MondeBrique[C][L].gotoAndStop(Sprite_BonusCrane);
				} else if (Type=="1") {
					MondeBrique[C][L].gotoAndStop(Sprite_BonusBombe);
				} else {
					MondeBrique[C][L].gotoAndStop(Sprite_BonusPuissance);
				}
			}
			return;
		}
		// Fin all in
		if (Code=="BoAI") {
			if (! ClipAttente.visible) {				
				NiveauEnCours[2][0]=-1;
				MondeBrique[2][0].gotoAndStop(Sprite_BonusCrane);
				NiveauEnCours[25][0]=-1;
				MondeBrique[25][0].gotoAndStop(Sprite_BonusCrane);
				NiveauEnCours[2][18]=-1;
				MondeBrique[2][18].gotoAndStop(Sprite_BonusCrane);
				NiveauEnCours[25][18]=-1;
				MondeBrique[25][18].gotoAndStop(Sprite_BonusCrane);	
				PoseAuto=false;
				Affichage_Bonus(Message[1]);
			}
			return;
		}		
		trace("Code inconnu");
		return;
	}
	//
	//
	// Salon
	if (SubCode=="Sa") {
		// niveau
		if (Code=="SaN") {
			Construction_Niveau(Message[1]);
			_Monde._Partie.PartiesRestantes.text=Message[2]+"/10";
			
			if (TueurJoueur != "-") { // Si on ne vient pas d'entrer dans la partie
				if (ListeTues.length == 0) {
					if (TueurJoueur != null) {
						Nouveau_Message_Chat("<font color='#C9CD36'>Vous n'avez tué personne et "
											 + (TueurJoueur == NomJoueur ? "vous vous êtes tué" : TueurJoueur + " vous a tué") + ".</font>");
					}
				} else {
					Nouveau_Message_Chat("<font color='#C9CD36'>Vous avez tué " + ListeTues.length + " personne"
									 + (ListeTues.length > 1 ? "s" : "") + " (" + ListeTues.join(", ") + ")"
									 + (TueurJoueur != null ? " et " + (TueurJoueur == NomJoueur ? "vous vous êtes tué" : TueurJoueur + " vous a tué") : "") + ".</font>");
				}
				
			}
			
			TueurJoueur = null;
			ListeTues = new Array();
			
			return;
		}
		// Liste Joueur
		if (Code=="SaJ") {
			Message.shift();
			Placement_Joueur(Message);
			return;
		}
		// Reset Score
		if (Code=="SaR") {
			if (Message[2]=="0") {
				_root.Message_Serveur("Remise à zéro des scores. "+Message[1]+" a été la plus forte pendant ces 10 parties avec "+Message[3]+" points.", 2);
			} else {
				_root.Message_Serveur("Remise à zéro des scores. "+Message[1]+" a été le plus fort pendant ces 10 parties avec "+Message[3]+" points.", 2);
			}
			return;
		}
		trace("Code inconnu");
		return;
	}
	//
	//
	// Chat
	if (SubCode=="Ch") {
		// Message
		if (Code=="ChM") {
			if (_root.Silence){
				return;
			}
			Nouveau_Message_Chat(Message[1], Message[2]);
			return;
		}


		// Fin Pose auto
		if (Code=="CxFPA") {
			PoseAuto = false;
			return;
		}
		
		// Effet bonus
		if (Code=="ChB") {
			var Effet:String=Message[1];
			Joueur=ClipListeJoueur[Message[2]];
			var JoueurJoueur:Boolean = (Joueur == ClipJoueur);
			// Défaut
			if (JoueurJoueur) {
				PoseAuto=false;
			}
			if (Effet=="0") { // all-in, pas d'annulation du bonus
				return;
			}			
			if (Joueur!=null) {
				Joueur.Lenteur=false;
				if (Joueur.BonusSecondaire != 6) {
					Joueur.Vitesse = false;
				}
			}
			//
			if (Effet=="1") {
				if (JoueurJoueur) {
					_Monde._Partie.Bonus.text="Super bombe";
				}
			} else if (Effet=="2") {
				if (JoueurJoueur) {
					_Monde._Partie.Bonus.text="Mauvais contact";
				}
			} else if (Effet=="3") {
				if (JoueurJoueur) {
					PoseAuto=true;
					_Monde._Partie.Bonus.text="Pose automatique !";
				}
			} else if (Effet=="4") {
				if (JoueurJoueur) {
					_Monde._Partie.Bonus.text="Aucun";
				}
			} else if (Effet=="5") {
				Joueur.Lenteur=true;
				if (Joueur==ClipJoueur) {
					_Monde._Partie.Bonus.text="Lenteur";
				}
			} else if (Effet=="6") {
				Joueur.Vitesse=true;
				if (Joueur==ClipJoueur) {
					_Monde._Partie.Bonus.text="Vitesse";
				}
			} else if (Effet=="7") {
				if (Joueur==ClipJoueur) {
					_Monde._Partie.Bonus.text="Ultra bombe";
				}
			} else if (Effet=="8") {
				_Monde._Partie.Bonus.text="All-in (" + (Message[2]!="" ? Message[2] : Message[3]) + ")";
				var EffetAllIn:String=Message[3];
				if (EffetAllIn=="3") {
					PoseAuto = true;
				}
				var Nb : int = ListeJoueur.length;
				for (var j:int = 0; j<Nb; j++) {
					var JoueurMobile:MovieClip = ListeJoueur[j];
					JoueurMobile.Lenteur=false;
					if (JoueurMobile.BonusSecondaire != 6) {
						JoueurMobile.Vitesse = false;
					}
					
					if (EffetAllIn == "5") {
						JoueurMobile.Lenteur=true;
					} else if (EffetAllIn == "6") {
						JoueurMobile.Vitesse=true;												
					}
				}
			} else if (Effet == "9") {
				if (Joueur==ClipJoueur) {
					_Monde._Partie.Bonus.text="Bombe fantôme";
				}
			} else if (Effet == "50") {
				if (Joueur==ClipJoueur) {
					_Monde._Partie.Bonus.text="Chasseur";
				}
			} else if (_root.SaintValentin && Effet == "90") {
				if (Joueur==ClipJoueur) {
					_Monde._Partie.Bonus.text="Bombe cupidonne";
				}
			} else if (_root.Noel && Effet == "91") {
				if (Joueur==ClipJoueur) {
					_Monde._Partie.Bonus.text="Bombe flocon";
				}
			}
			
			return;
		}
		
		// Effet bonus secondaire
		if (Code=="ChBS") {
			var Effet:String = Message[1];
			Joueur = ClipListeJoueur[Message[2]];
			
			if (Joueur != null) {
				Joueur.Lenteur=false;
				Joueur.BonusSecondaire = 6;
				Joueur.Vitesse = true;
				/*if (Joueur.BonusSecondaire != 6) {
					Joueur.Vitesse = false;
				}
				
				Joueur.BonusSecondaire = int(Effet);
				if (Joueur.BonusSecondaire == 6) {
					Joueur.Vitesse = true;
				}*/
			}
			
			return;
		}
		// Changement Salon
		if (Code=="ChS") {
			Nouveau_Message_Chat("Vous entrez dans le salon : <font color='#C9CD36'>"+Message[1]+"</font>. Tapez /salon NomDuSalon pour créer ou rejoindre un salon.");
			return;
		}
		// Changement Salon
		if (Code=="ChLS") {
			Nouveau_Message_Chat(Message[1]);
			return;
		}
		// Victoire
		if (Code=="ChV") {
			ClipJoueur.Vivant=false;
			ClipJoueur.Fantome=false;
			ClipJoueur.Droite=false;
			ClipJoueur.Gauche=false;
			ClipJoueur.Haut=false;
			ClipJoueur.Bas=false;
			Vivant=false;
			Fantome=false;
			DeplacementEnCours=false;
			DroiteEnCours=false;
			GaucheEnCours=false;
			HautEnCours=false;
			BasEnCours=false;
			EspaceEnCours=false;
			//
			Nouveau_Message_Chat("<font color='#C9CD36'>"+Message[1]+" remporte la partie !</font>");
			
			//ClipListeJoueur[Message[1]].Score += 10;
			MAJ_Liste_Joueur();
			return;
		}
		// Muet
		if (Code=="ChX") {
			Nouveau_Message_Chat("<font color='#CB546B'>"+Message[1]+" n'a plus le droit de parler.</font>");
			if (Message[1]==NomJoueur) {
				Nouveau_Message_Chat("<font color='#CB546B'>Vous n'avez plus le droit de parler pendant une heure.");
			}
			return;
		}
		// Message Serveur
		if (Code=="ChA") {
			Nouveau_Message_Chat("<font size='14' color='#FF4040'>"+Message[1]+"</font>");
			return;
		}
		// Salon erreur
		if (Code=="ChE") {
			Nouveau_Message_Chat("<font color='#CB546B'>Ce salon est complet.");
			return;
		}
		//
		trace("Code inconnu");
		return;
	}
	//
	if (Code == "EloC") { // Changer la couleur des joueurs dans l'équipe 2 (partie classée)
		LsPseudosEquipe2Elo = Message[1].split(";");
		for (i = ListeJoueur.length - 1; i >= 0; --i) {
			Colorer_Pseudo(ListeJoueur[i]);
		}
		return;
	}
	//
	// Serveur
	if (SubCode=="Id") {
		// Nouveau Joueur
		if (Code=="IdJ") {
			Nouveau_Joueur(Message[1], Message[2], Message[3], Message[4]); // TODO
			return;
		}
		// Nouveau Joueur
		if (Code=="IdL") {
			Initialisation_Joueurs(Message);
			return;
		}
		// Deco Joueur
		if (Code=="IdD") {
			Deco_Joueur(Message[1], Message.length == 3);
			return;
		}
		// Initialisation Monde
		if (Code=="IdI") {
			DebutPartie=getTimer()-int(Message[1]);
			return;
		}
		//
		trace("Code inconnu");
		return;
	}
	//
	// Mode jeu
	if (SubCode == "Mo") {
		// Mode Cible Perso
		if (Code == "MoC") {
			Clean_Cible_Perso(); // Retrait de la cible
			
			if (Message[1] != "") { // Nouvelle cible définie
				CiblePerso = Message[1];
				Joueur = ClipListeJoueur[CiblePerso];
				Colorer_Pseudo(Joueur);
				Nouveau_Message_Chat("<font color='#C9CD36'>"+CiblePerso+" est votre cible, éliminez-la !</font>");
			}
			return;
		}
		
		// Mode Team contre Team
		if (Code == "MoT") {
			if (Message.length > 1) {
				var ls0:Array = new Array();
				var ls1:Array = new Array();
				
				idEquipe = -1;
				
				for (i = 1; i < Message.length; ++i) {
					var infos:Array = Message[i].split(",");
					if (infos[1] == "0") {
						ls0.push(infos[0])
					} else {
						ls1.push(infos[0])
					}
					
					if (infos[0] == NomJoueur) {
						idEquipe = int(infos[1]);
					}
				}
				
				if (idEquipe == 0) {
					LsPseudosModeTeam = ls1;
				} else if (idEquipe == 1) {
					LsPseudosModeTeam = ls0;
				}
				
				for (i = ListeJoueur.length - 1; i >= 0; --i) {
					Colorer_Pseudo(ListeJoueur[i]);
				}
				
				Nouveau_Message_Chat("<font color='#C9CD36'>Vous avez rejoint l'équipe " + (idEquipe + 1)
									 + ". Éliminez vos adversaires dont les pseudos sont en rouge !</font>");
				return;
			} else {
				LsPseudosModeTeam = new Array();
				for (i = ListeJoueur.length - 1; i >= 0; --i) {
					Colorer_Pseudo(ListeJoueur[i]);
				}
			}
		}
		
		if (Code == "MoTV") {
			if (idEquipe == -1) {
				Nouveau_Message_Chat("<font color='#C9CD36'>Victoire de l'équipe " + (idEquipe + 1) + " !</font>");
			} else if (idEquipe == int(Message[1])) {
				Nouveau_Message_Chat("<font color='#C9CD36'>Votre équipe a triomphé !</font>");
			} else {
				Nouveau_Message_Chat("<font color='#C9CD36'>Victoire de l'équipe adverse !</font>");
			}
			return;
		}
	}
	//
	_root.Reception_Spéciale(DATA);
}

function Clean_Cible_Perso():void {
	if (CiblePerso != "") {
		var Joueur:MovieClip = ClipListeJoueur[CiblePerso];
		CiblePerso = "";
		
		Colorer_Pseudo(Joueur);
	}
}

function Affichage_Bonus(BONUS : String) {
	if (BONUS=="1") {
		_Monde._Partie.Bonus.text="Super bombe";
	} else if (BONUS=="2") {
		_Monde._Partie.Bonus.text="Mauvais contact";
	} else if (BONUS=="3") {
		_Monde._Partie.Bonus.text="Pose automatique !";
	} else if (BONUS=="4") {
		_Monde._Partie.Bonus.text="Aucun";
	} else if (BONUS=="5") {
		_Monde._Partie.Bonus.text="Lenteur";
	} else if (BONUS=="6") {
		_Monde._Partie.Bonus.text="Vitesse";
	} else if (BONUS=="7") {
		_Monde._Partie.Bonus.text="Ultra bombe";
	}
}

function Initialisation_Joueurs(LISTE:Array):void {
	_Monde._Texte.htmlText = _root.TexteEnCours;
	_Monde._Texte.scrollV = _Monde._Texte.maxScrollV;	
	while (ClipListeJoueur.numChildren != 0) {
		ClipListeJoueur.removeChildAt(0);
	}
	ListeJoueur = new Array();
	var InfoJoueur:Array=LISTE[1].split(",");
	NomJoueur=InfoJoueur[0];
	ClipJoueur = new $Joueur();
	
	ClipJoueur.ClipJoueur=true;
	ListeJoueur.push(ClipJoueur);
	InfoJoueur.push(ClipJoueur);
	var InfoScore:Array=InfoJoueur[1].split("%");
	ClipJoueur.Score=int(InfoScore[0]);
	ClipJoueur.Score2=int(InfoScore[1]);
	ClipJoueur.RecompensesElo = int(InfoJoueur[2]);
	ClipJoueur.Nom.text=NomJoueur;
	ClipJoueur.Fantome = false;
	Couleur_Joueur(ClipJoueur, false);
	ClipJoueur.Nom.textColor=0xC9CD36;
	ClipJoueur.visible=false;
	ClipJoueur.Bulle.visible=false;
	ClipListeJoueur[NomJoueur]=ClipJoueur;
	ClipListeJoueur.addChild(ClipJoueur);
	//
	var NbJoueur:int=LISTE.length;
	for (var i:int = 2; i<NbJoueur; i++) {
		var Info:Array=LISTE[i].split(",");
		var Joueur:MovieClip = new $Joueur();
		Info.push(Joueur);
		var Nom:String=Info[0];
		var IdTeamJoueur:int = Info[1];
		Joueur.IdTeamJoueur = IdTeamJoueur;
		
		var InfoScore2:Array=Info[2].split("%");
		Joueur.Score=int(InfoScore2[0]);
		Joueur.Score2=int(InfoScore2[1]);
		Joueur.RecompensesElo = int(Info[3]);
		Joueur.Vivant=false;
		Joueur.Fantome=false;
		ListeJoueur.push(Joueur);
		Joueur.Nom.text=Nom;
		Colorer_Pseudo(Joueur);
		Couleur_Joueur(Joueur, false);
		Joueur.visible=false;
		//
		Joueur.Bulle.visible=false;
		ClipListeJoueur[Nom]=Joueur;
		ClipListeJoueur.addChild(Joueur);
	}
	//
	_root._Serveur.visible=false;
	_Monde.visible=true;
	//
	MAJ_Liste_Joueur();
	_root.Ascenseur_Reset(_Monde.Ascenseur2);
}

function Colorer_Pseudo(Joueur:MovieClip):void {
	if (Joueur == null)
		return;
	
	var IdTeamJoueur:int = Joueur.IdTeamJoueur;
	
	if (Joueur.Nom.text == NomJoueur) {
		Joueur.Nom.textColor = 0xC9CD36;
	} else if (LsPseudosEquipe2Elo != null && LsPseudosEquipe2Elo.indexOf(Joueur.Nom.text) != -1) {
		Joueur.Nom.textColor = 0x7AF5FF;
	} else if (_root.SaintValentin && Joueur.Nom.text == _root.StVAmoureux) {
		Joueur.Nom.textColor = 0xF24AB8;
	} else if ((CiblePerso != "" && Joueur.Nom.text == CiblePerso)
			   || LsPseudosModeTeam.indexOf(Joueur.Nom.text) != -1) {
		Joueur.Nom.textColor = 0xF00000;
	} else if (IdTeamJoueur!=0 && IdTeamJoueur == _root.IdTeam) {
		Joueur.Nom.textColor = 0x009D9D;
	} else if (IdTeamJoueur != 0 && IdTeamJoueur == _root.IdTeamColorée1) {
		Joueur.Nom.textColor = 0x7AF5FF;
	} else if (IdTeamJoueur != 0 && IdTeamJoueur == _root.IdTeamColorée2) {
		Joueur.Nom.textColor = 0xC049F3;
	} else {
		Joueur.Nom.textColor = 0x6C77C1;
	}
}

function Nouveau_Joueur(JOUEUR:String, SCORE:int, TEAM:int, RECOMPENSE_ELO:int):void {
	var Joueur:MovieClip = new $Joueur();
	ListeJoueur.push(Joueur);
	Joueur.Nom.text=JOUEUR;
	Joueur.IdTeamJoueur = TEAM;
	
	Colorer_Pseudo(Joueur);
	
	Joueur.RecompensesElo = RECOMPENSE_ELO;
	Joueur.Fantome = false;
	Couleur_Joueur(Joueur, false);
	Joueur.visible=false;
	Joueur.Bulle.visible=false;
	Joueur.Score=0;
	Joueur.Score2=SCORE;
	ClipListeJoueur[JOUEUR]=Joueur;
	ClipListeJoueur.addChild(Joueur);
	MAJ_Liste_Joueur();
	if (_root.SaintValentin && JOUEUR == _root.StVAmoureux) {
		Nouveau_Message_Chat("Connexion de votre tendre "+JOUEUR+".");
	} else if (_root.AffichageConnexions) {
		Nouveau_Message_Chat("Connexion de "+JOUEUR+".");
	}
}

function Couleur_Joueur(JOUEUR:MovieClip, ORANGE:Boolean, FANTOME:Boolean = false):void {
	if (JOUEUR.Anim.numChildren!=0) {
		JOUEUR.Anim.removeChildAt(0);
	}
	JOUEUR.Orange=ORANGE;
	JOUEUR.Fantome = FANTOME;
	
	if (FANTOME) { // Halloween
		JOUEUR.Anim.Anim2 = new $AnimFantome();
		JOUEUR.Anim.addChild(JOUEUR.Anim.Anim2);
	} else if (ORANGE) {
		if (_root.Noel) {
			JOUEUR.Anim.Anim2 = new $AnimNOrange();
		} else {
			JOUEUR.Anim.Anim2 = new $AnimOrange();
		}
		JOUEUR.Anim.Anim2 = new $AnimOrange();
		JOUEUR.Anim.addChild(JOUEUR.Anim.Anim2);
	} else {
		if (_root.Noel) {
			JOUEUR.Anim.Anim2 = new $AnimNBlanc();
		} else {
			JOUEUR.Anim.Anim2 = new $AnimBlanc();
		}
		JOUEUR.Anim.addChild(JOUEUR.Anim.Anim2);
	}
	Animation(JOUEUR, 7);
}

function Deco_Joueur(JOUEUR:String, BANNI:Boolean):void {
	var MobileJ:MovieClip=ClipListeJoueur[JOUEUR];
	var NbJoueur:int=ListeJoueur.length;
	for (var i:int = 0; i<NbJoueur; i++) {
		if (ListeJoueur[i]==MobileJ) {
			if (ClipListeJoueur.contains(MobileJ)) {
				ClipListeJoueur.removeChild(MobileJ);
			}
			ClipListeJoueur[JOUEUR]=null;
			ListeJoueur.splice(i, 1);
			if (BANNI) {
				//Nouveau_Message_Chat("<font color='#CB546B'>"+JOUEUR+" a été banni.");
			} else {
				if (_root.AffichageConnexions) {
					Nouveau_Message_Chat(JOUEUR+" s'est déconnecté(e).");
				}
			}
			MAJ_Liste_Joueur();
			return;
		}
	}
}