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

var ListeJoueur:Array = new Array();
var PremierContaminé:Boolean = true;
var Contaminé:int = 0;
var isAfk:int = 0;
var ListeTues:Array = new Array();

// Liste modes (_root.ModePartie) (cf Carte.<MODE> code Java)
const MODE_A_NORMAL:int = 0;
const MODE_A_OFFI:int = 1;
const MODE_A_NG:int = 2;
const MODE_A_DEF:int = 3;
const MODE_A_FIGHT:int = 4;
const MODE_A_MS:int = 5;
const MODE_A_RALLY:int = 6;

_Monde.Quitter.addEventListener(MouseEvent.MOUSE_DOWN, Clique_Quitter);
function Clique_Quitter(E:Event):void {
	Quitter_Aaaah(false);
}

function Quitter_Aaaah(Afk:Boolean):void {
	
	_Monde.Porte.Retour.visible = false;
	_Monde.Porte.Texte.visible = true;
	_Editeur.visible = false;
	_Monde.visible = true;
	_Monde.Quitter.visible = true;
	_Exportation.visible = false;
	_Editeur.NewMessage.visible = false;
	_Editeur.NewMessage.VignetteTexte = "";
	visible = false;
	if (!Afk) {
		_root.Envoie_Serveur("CxJ#0");
	} else {
		_root._Afk.visible = true;
	}
	stage.removeEventListener(KeyboardEvent.KEY_DOWN, Clavier_onPress);
	stage.removeEventListener(KeyboardEvent.KEY_UP, Clavier_onRelease);
	stage.removeEventListener(MouseEvent.MOUSE_UP, Desactivation_Trace);
	stage.removeEventListener(MouseEvent.MOUSE_DOWN, Lancement_Trace);
	stage.removeEventListener(Event.ENTER_FRAME, Boucle_Moteur);
	TimerSaut.stop();
	TimerSaut.reset();
	stage.removeEventListener(TimerEvent.TIMER, Timer_Saut);
	TimerDessin.stop();
	TimerDessin.reset();
	stage.removeEventListener(TimerEvent.TIMER, Boucle_Trace);
	TimerDepart.stop();
	TimerDepart.reset();
	stage.removeEventListener(TimerEvent.TIMER, Depart_1);
	TimerVote.stop();
	TimerVote.reset();
	stage.removeEventListener(TimerEvent.TIMER, Fin_Vote);
	TimerMouvement.stop();
	TimerMouvement.reset();
	stage.removeEventListener(TimerEvent.TIMER, Boucle_Actualisation_Mouvement);
	TimerProjection.stop();
	TimerProjection.reset();
	stage.removeEventListener(TimerEvent.TIMER, Timer_Projection);
	
	ListeTues = new Array();
	LsPseudosEquipe2Elo = new Array();
	SautDispo = true;
}

// Reception de données
function Reception(DATA:String):void {
	var Message:Array = DATA.split("#");
	var Code:String = Message[0];
	var SubCode:String = Code.substr(0,2);
	var Joueur:MovieClip;
	var i:int;
	var k:int;

	// Serveur
	if (SubCode == "Id") {
		// Pousse
		if (Code == "IdB") {
			Pousse_Pousse(_Monde._ListeJoueur[Message[3]], int(Message[1]), int(Message[2]));
			return;
		}
		// Projection
		if (Code == "IdA") {
			Joueur = _Monde._ListeJoueur[Message[4]];
			if (! Joueur.Joueur) {
				Projection(Joueur, int(Message[1]), int(Message[2]), Message[3] == "1");
			}
			return;
		}
		// Dieu
		if (Code == "IdO") {
			isAfk += 1;
			TempsDebut = getTimer();
			
			// reset ghost
			GhostCompteur = 1;			
			GhostMessages = "";
			OldTemps = TempsDebut;
			
			_Monde._Dessin.BriqueDepart.y = 353;
			Contamination = 0;
			Contaminé = 0;
			PremierContaminé = true;
			//
			AfficheTues();
			ListeTues = new Array();
			//
			while (ListeLigne.length != 0) {
				_Monde._Dessin.removeChild(ListeLigne.shift());
			}
			while (ListeTimer.length != 0) {
				ListeTimer.shift().stop();
			}
			//
			JoueAuMoinsUneFois = true;
			PremierJoueur = false;
			_Monde.Drapeau.visible = false;
			Incontrolable = false;
			Incontrolable2 = false;
			Controlable2 = 6;
			AaaahDispo = !RallyPublic;
			TempsAaaah = TempsDebut;
			ClipJoueur.DeplacementX = 0;
			ClipJoueur.CompteurAaaah.Barre.width = 0;
			//

			var CodeMonde:String = Message[3];
			//trace(CodeMonde);
			if (CodeMonde.substr(0, 2) != "<C") { // Map Offi
				if (CodeMonde.indexOf("-") != -1) {
					var Info:Array = CodeMonde.split("-");
					Changement_Monde(int(Info[0]), Info[1]);
				} else {
					Changement_Monde(int(CodeMonde), null);
				}
			} else {
				Chargement_Monde(CodeMonde,Message[5]);
			}
			//
			JoueurDepart = int(Message[2]);
			JoueurEnVie = JoueurDepart;
			_Monde.Porte.EnVie.text = JoueurEnVie + "/" + JoueurDepart;
			//
			//while (ListeLigne.length != 0) {
			//_Monde._Dessin.removeChild(ListeLigne.shift());
			//}
			//
			if (TimerDepart.running) {
				TimerDepart.stop();
				Depart_1(null, true);
			}
			Guide = _Monde._ListeJoueur[Message[1]];
			if (ListeJoueur.length > 1 && Guide!=null) {
				Desactivation_Mobile(Guide);
			}
			// son
			if ((_root.SonActif) && Guide == ClipJoueur) {
				if (! _root._Aaaah._Editeur.visible) {
					var bipGuide:BipDebutGuide = new BipDebutGuide();
					bipGuide.play();
				}
			} else if (_root.SonActif && _root.ModePartie != MODE_A_RALLY) { // bip avant pour le mode rally
				var bipPartie:BipDebutPartie = new BipDebutPartie();
				bipPartie.play();
			}
			// classement
			ArriveeCompteur = 1;
			_Monde.ClassementArrivee.text = "";

			if (Guide==null) {
				if (_root.Halloween) {
					_Monde.Porte.Guide.text = Message[1];
				} else {
					_Monde.Porte.Guide.text = "Aucun";
				}
				Nouveau_Message_Chat("<font color='#C9CD36'>Cette carte se joue sans guide.</font>");				
			} else {
				_Monde.Porte.Guide.text = Message[1];
				Nouveau_Message_Chat("<font color='#C9CD36'>"+Message[1]+" est maintenant votre guide !</font>");
			}
			if (Guide == ClipJoueur) {
				if (_root.AffichageMessages) {
					_Monde.Flash.gotoAndPlay(2);
					_Monde.Flash.visible = true;
				}else{
					Nouveau_Message_Chat("<font color='#C9CD36'>Sauvez un maximum de personne en traçant un chemin avec votre souris !</font>");				
				}
				Activation_Trace();
				
			} else {
				Desactivation_Trace(null);
				stage.removeEventListener(MouseEvent.MOUSE_UP, Desactivation_Trace);
				stage.removeEventListener(MouseEvent.MOUSE_DOWN, Lancement_Trace);
			}
			_Monde.Porte.Temps.text = "120";
			//
			_Monde._Dessin.BriqueDepart.y = 353;
			//
			//MAJ_Liste_Joueur(); // déplacé plus bas
			//
			var LsJ:Array = Message[4].split(";");
			LsJ.shift();
			MAJ_Positions(LsJ, true);

			MAJ_Liste_Joueur();// à enlever en cas de bug
			return;
		}
		// Nouveau Joueur
		if (Code == "IdJ") {
			Nouveau_Joueur(Message[1].split(","));
			return;
		}
		// Nouveau Joueur
		if (Code == "IdL") {
			Initialisation_Joueurs(Message);
			return;
		}
		// Positions
		if (Code == "IdP") {
			Message.shift();
			MAJ_Positions(Message);
			return;
		}
		// Mort
		if (Code == "IdX") {
			if (Message.length == 4) {
				Joueur = _Monde._ListeJoueur[Message[3]];
				var Frag:MovieClip = new $Frag();
				Frag.Effet.Texte.text = Message[1];
				Frag.y = 0;
				Frag.x = int(Message[2]) - 50;
				_Monde.ListeFrag.addChild(Frag);
				if (Message[1] == _root.NomJoueur) {
					ListeTues.push(Message[3]);
				}
			} else {
				Joueur = _Monde._ListeJoueur[Message[1]];
			}
			///
			if (Joueur == ClipJoueur) {
				ClipJoueur.y = 800;
				ClipJoueur.x = -200;
			}
			///
			//
			if (! Joueur.Zombie) {
				JoueurEnVie--;
				_Monde.Porte.EnVie.text = JoueurEnVie + "/" + JoueurDepart;
			}
			//
			Desactivation_Mobile(Joueur);
			return;
		}
		// Victoire
		if (Code == "IdW") {
			Joueur = _Monde._ListeJoueur[Message[1]];
			if (Joueur == ClipJoueur) {
				var TempsCourse:int = (getTimer()-TempsDebut)/1000;
				var Accord:String = (ArriveeCompteur==1?"ère":"ème");
				
				if (_root.Halloween) {
					Nouveau_Message_Chat("<font color='#009D9D'>Vous avez atteint l'enfer en "+ TempsCourse +" secondes en "+ArriveeCompteur+Accord+" position.</font>");
				} else if (_root.Noel) {
					Nouveau_Message_Chat("<font color='#009D9D'>Vous avez atteint le sapin en "+ TempsCourse +" secondes en "+ArriveeCompteur+Accord+" position.</font>");
				} else {
					Nouveau_Message_Chat("<font color='#009D9D'>Vous avez atteint l'infirmerie en "+ TempsCourse +" secondes en "+ArriveeCompteur+Accord+" position.</font>");
				}
			}
			// Petit classement
			if (ArriveeCompteur < 11) {
				var TexteCl:String = "<font color='#009D9D'>" + ArriveeCompteur + " - <font color='#6C77C1'>" + Message[1];//+ Joueur.Nom.text;
				var Tps:Number = int(Message[2]/10)/100;
				if (ArriveeCompteur == 1) {
					TexteCl += "<font color='#CB546B'> (+6)</font>";
				} else if (ArriveeCompteur==2) {
					TexteCl+="<font color='#CB546B'> (+4)</font>";
				} else if (ArriveeCompteur==3) {
					TexteCl+="<font color='#CB546B'> (+2)</font>";
				}
					
				TexteCl+=" - "+Tps+"s\n";
				_Monde.ClassementArrivee.htmlText+=TexteCl;
			}
			ArriveeCompteur=ArriveeCompteur+1;

			Desactivation_Mobile(Joueur);
			// Drapeau
			if (! PremierJoueur) {
				PremierJoueur=true;
				_Monde.Drapeau.gotoAndPlay(1);
				_Monde.Drapeau.NomJoueur=Message[1];
				_Monde.Drapeau.visible=true;
			}
			return;
		}
		// Info Carte
		if (Code=="IdIC") {
			var Temps : Number= int(Message[1]/10)/100;
			if (Temps==10) {
				Temps = int(Message[1])/1000;
			}
			var RecordMan : String = (Message[2]=="-"?"Pas de record.":"<font color='#009D9D'>Record : <font color='#6C77C1'>"+Temps+"s par <font color='#C9CD36'>"+Message[2]+"</font>");
			var Chance : int = (Message[3]=="0"?0:int((Number(Message[4])/Number(Message[3]))*100));
			if (Message[5]=="1") { // Notification de nouveau record au salon
				Nouveau_Message_Chat("<font color='#C9CD36'>"+Message[2]+"<font color='#6C77C1'> vient d'établir un nouveau record de vitesse sur cette carte : "+int(Message[1]/100)/10+" secondes ! Whoaaaah !");
			}else{ // Info carte en début de map
				_Monde.StatCarte.htmlText = "<font color='#6C77C1'>"+RecordMan+"\n<font color='#009D9D'>Moyenne de survie : <font color='#6C77C1'>"+Chance+"%</font>";
				if (MapId == Message[6] && GhostMessagesPerso != "" && GhostRecordPerso != 120000) {
					// même map que la précédente, on load un ghost perso					
					GhostArray = GhostMessagesPerso.split(",");
				} else {
					GhostMessagesPerso = "";
					GhostRecordPerso = 120000;
					if (Message.length == 8 && GhostLog) { // Ghost disponible
						GhostArray = Message[7].replace(/&/g,"#").split(",");
					} else {
						GhostArray = [];
					}				
				}
				if (!GhostView) { // on cache le ghost
					GhostArray = [];
				}
				MapId = Message[6];
			}
			return;
		}
	
		// Deco Joueur
		if (Code=="IdD") {
			Deco_Joueur(Message[1], Message.length == 3);
			return;
		}
		// BAN
		if (Code=="IdBAN") {
			//Banni = int(Message[1]);
			return;
		}
		if (Code=="IdBa") {
			if (Message.length==2) {
				Nouveau_Message_Chat("<font color='#CB546B'>Ce joueur n'existe pas.</font>");
			} else {
				Nouveau_Message_Chat("<font color='#CB546B'>Votre demande à été prise en compte.</font>");
			}
			return;
		}
		if (Code == "IdC") {
			ActuValeursCri(Message[1]);
			return;
		}
		// Carte permanente
		if (Code=="IdPe") {
			Nouveau_Message_Chat("<font color='#CB546B'>Cette carte ne peut plus être effacée. ("+Message[2]+"/"+Message[1]+")\n</font>");
			return;
		}
		//
		trace("Code inconnu");
	}
	
	if (Code == "EvHLWNRGN") { // Régénération
		var ClipCible:MovieClip=_Monde._ListeJoueur[Message[1]];
		ClipCible.x = int(Message[2]);
		ClipCible.y = int(Message[3]);
		Activation_Mobile(ClipCible);
		return;
	}
	
	// Dessin
	if (SubCode=="De") {
		if (Guide!=ClipJoueur) {
			// Dessin
			if (Code=="DeT") {
				Tracage(int(Message[1]), int(Message[2]));
				return;
			}
			// Départ Dessin
			if (Code=="DeS") {
				PosX=int(Message[1]);
				PosY=int(Message[2]);
				return;
			}
		}
		//
		return;
	}
	//
	//
	// Mouvement
	if (SubCode=="Mv") {
		if (Code=="MvH") {
			Joueur=_Monde._ListeJoueur[Message[4]];
			if (! Joueur.Joueur) {
				Saut(Joueur, int(Message[1]), int(Message[2]), int(Message[3]));
			}
			return;
		} else {
			if (Code=="MvA") {
				Joueur=_Monde._ListeJoueur[Message[5]];
				if (! Joueur.Joueur) {
					Replacement_Joueur(Joueur, int(Message[1]), int(Message[2]), int(Message[3])/100, int(Message[4])/100);
				}
				return;
			} else {
				Joueur=_Monde._ListeJoueur[Message[3]];
				if (! Joueur.Joueur) {
					if (Code=="MvD") {
						Deplacement_Joueur(Joueur, int(Message[1]), int(Message[2]), false, true);
						return;
					}
					if (Code=="MvG") {
						Deplacement_Joueur(Joueur, int(Message[1]), int(Message[2]), false, false);
						return;
					}
					if (Code=="MvS") {
						Deplacement_Joueur(Joueur, int(Message[1]), int(Message[2]), true);
						return;
					}
				}
			}
		}
		return;
	}
	//
	//
	// Chat
	if (SubCode=="Ch") {
		// Message
		if (Code=="ChM") {
			if (_root.Silence) {
				return;
			}
			Nouveau_Message_Chat(Message[1], Message[2]);
			return;
		}
		// Info Guide
		if (Code=="ChI") {
			Guide=_Monde._ListeJoueur[Message[1]];
			_Monde.Porte.Guide.text=Message[1];
			JoueurEnVie=int(Message[4]);
			_Monde.Porte.EnVie.text=JoueurEnVie+"/"+int(Message[3]);
			TempsDebut=getTimer()-int(Message[2]);
			return;
		}
		// Malus Points
		if (Code=="ChP") {
			if (Guide!=null) {
				var Sauve:int=int(Message[2]);
				var Perdu:int=int(Message[3]);
				if (_root.Halloween) {
					Nouveau_Message_Chat("<font color='#C9CD36'>"+Guide.Nom.text+" a égaré "+int(100 - (Sauve/(Sauve+Perdu)*100))+" % des âmes. "+Guide.Nom.text+" perd "+Message[1]+" points.</font>");
				} else if (_root.Noel) {
					Nouveau_Message_Chat("<font color='#C9CD36'>"+Guide.Nom.text+" a empaqueté "+int(Sauve/(Sauve+Perdu)*100)+" % des joueurs. "+Guide.Nom.text+" perd "+Message[1]+" points.</font>");
				} else {
					Nouveau_Message_Chat("<font color='#C9CD36'>"+Guide.Nom.text+" a sauvé "+int(Sauve/(Sauve+Perdu)*100)+" % des joueurs. "+Guide.Nom.text+" perd "+Message[1]+" points.</font>");
				}
			}
			return;
		}
		// Changement Monde
		if (Code=="ChL") {
			var CodeMonde2:String=Message[1];
			if (CodeMonde2.substr(0, 3) != "<C ") { // Map Offi
				if (CodeMonde2.indexOf("-") != -1) {
					var Info2:Array = CodeMonde2.split("-");
					Changement_Monde(int(Info2[0]), Info2[1]);
				} else {
					Changement_Monde(int(CodeMonde2), null);
				}
			} else {
				Chargement_Monde(CodeMonde2,Message[2]);
			}
			
			AfficheTues();
			ListeTues = new Array();
			
			return;
		}
		// Changement Salon
		if (Code=="ChS") {
			ListeTues = new Array();
			JoueAuMoinsUneFois=false;
			Fin_Vote(null);
			SalonEnCours=Message[1];
			Nouveau_Message_Chat("Vous entrez dans le salon : <font color='#C9CD36'>"+SalonEnCours+"</font>.\nTapez /salon NomDuSalon pour créer ou rejoindre un salon. Tapez /salon pour retourner sur le salon principal.");
			return;
		}
		// Changement Salon
		if (Code=="ChLS") {
			Nouveau_Message_Chat(Message[1]);
			return;
		}
		// Changement Salon
		if (Code=="ChX") {
			Nouveau_Message_Chat("<font color='#CB546B'>"+Message[1]+" n'a plus le droit de parler.</font>");
			if (Message[1]==NomJoueur) {
				Nouveau_Message_Chat("<font color='#CB546B'>Vous n'avez plus le droit de parler jusqu'au prochain reboot du serveur (Au moins 24 heures).");
			}
			return;
		}
		// Message Serveur
		if (Code=="ChA") {
			Nouveau_Message_Chat("<font size='14' color='#FF4040'>"+Message[1]+"</font>");
			return;
		}
		//
		trace("Code inconnu");
		return;
	}
	//
	//
	// Contamination
	if (SubCode=="Zo") {
		// Joueur PreContaminé
		if (Code=="ZoP") {
			Joueur=_Monde._ListeJoueur[Message[1]];
			if (! Joueur.Joueur&&ClipJoueur!=Guide) {
				Zombification(Joueur, false, true);
			}
			return;
		}
		// Joueur contaminé
		if (Code=="ZoC") {
			Contaminé++;
			Joueur=_Monde._ListeJoueur[Message[1]];
			if (PremierContaminé) {
				PremierContaminé=false;
				if(_root.Halloween) {
					Nouveau_Message_Chat("<font color='#BC5ED0'>Aaaah ! <font color='#C9CD36'>"+Joueur.Nom.text+"</font> est diabolisé ! Fuyez !!</font>");
				} else if (_root.Noel) {
					Nouveau_Message_Chat("<font color='#BC5ED0'>Aaaah ! <font color='#C9CD36'>"+Joueur.Nom.text+"</font> a une rage de dent ! Fuyez !!</font>");
				} else {
					Nouveau_Message_Chat("<font color='#BC5ED0'>Aaaah ! <font color='#C9CD36'>"+Joueur.Nom.text+"</font> est contaminé ! Fuyez !!</font>");
				}
			}
			Zombification(Joueur, true);
			return;
		}
		//
		trace("Code inconnu");
		return;
	}
	
	if (Code=="CxRecGh") {
		_root.Envoie_Serveur(DATA+"#"+GhostMessages.replace(/#/g, "&"));
		return;
	}
	//
	//
	// Editeur
	if (SubCode=="Ed") {
		// Vote
		if (Code=="EdV") {
			if (isAfk>1) {
				if (!_Editeur.visible) {
					_root.Commandes("afk");
				}
				isAfk = 0;
			}
			if (JoueAuMoinsUneFois) {
				var Votes:int=int(Message[1]);
				if (Votes==0) {
					_Monde.Vote.Texte.text="Cette carte n'a pas encore été notée.";
				} else {
					_Monde.Vote.Texte.htmlText = "Cette carte est appréciée à <font color='#009D9D'>"+(int(100*(int(Message[2])/Votes)))+"%</font> ("+Votes+" votes).";
				}
				if (!_root.NoVote) {
					_Monde.Vote.visible=true;
				}else{
					Nouveau_Message_Chat("<font color='#009D9D'>Les votes sont ouverts.</font>");				
				}
				VoteEnCours = true;
				TimerVote.start();
				
				if (_root.SonActif && _root.ModePartie == MODE_A_RALLY) {
					var bipPartie:BipDebutPartie = new BipDebutPartie();
					bipPartie.play();
				}
			}
			return;
		}
		// Code
		if (Code=="EdS") { // sauvegarde
			var CodeMap : String = Message[1];
			_Exportation.Code.Texte.htmlText="@"+CodeMap;
			_Exportation.Code.visible=true;
			_Editeur.CodeCarte.text = CodeMap;
			System.setClipboard("@"+CodeMap);
			return;
		}		
		// Temps de jeu
		if (Code=="EdI") {
			var Auto : String = Message[1];
			_Exportation.Autorisation = (Auto=="1"?true:false);
			return;
		}				
		//
		if (Code=="EdC") { // chargement
			if (Message.length==1) {
				_Editeur.Code.text="Introuvable";
				_Editeur.Code.setSelection(0, 0);
			} else if (Message.length == 2) {
				_Editeur.Code.text="Vous n'êtes pas l'auteur.";					
				_Editeur.Code.setSelection(0, 0);
				return;					
			} else {
				var Auteur : String = Message[9];
				_Editeur.EstAuteur = (Auteur.toLowerCase() == NomJoueur.toLowerCase());
				var VoteTotal : int = int(Message[2]);
				var VoteOui : int = int(Message[3]); 
				var MeilleurTemps : int = int(Message[4]);
				var MeilleurJoueur : String = Message[5];
				var JoueursJoués : int = int(Message[6]);
				var JoueursSauvés : int = int(Message[7]);				
				_Editeur.Chargement_Carte(Message[1]);
				_Editeur.Note.text = "Survie : "+int(100*JoueursSauvés/JoueursJoués)+"%\n\n"
				+"Note : "+(int(100*(VoteOui/VoteTotal)))+"%\nVotes : "+VoteTotal;
				if (MeilleurJoueur != "-") {
				_Editeur.Note.text = "Record : "+(int(MeilleurTemps/100)/10) +"s\n"
				+ "Par : "+MeilleurJoueur + "\n\n" + _Editeur.Note.text;
				}
				_Editeur.CodeCarte.text = Message[8];

			}
			return;
		}
		//
		trace("Code inconnu");
	}
	
	if (Code == "EloC") { // Changer la couleur des joueurs dans l'équipe 2 (partie classée)
		LsPseudosEquipe2Elo = Message[1].split(";");
		return;
	}
	//
	_root.Reception_Spéciale(DATA);
}

function AfficheTues():void {
	if (ListeTues.length != 0) {
		Nouveau_Message_Chat("Vous avez tué " + ListeTues.length + " joueur" + (ListeTues.length > 1 ? "s" : "") + " : " + ListeTues.join(", "));
	}
}