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
    along with Extinction Minijeux.  If not, see <https://www.gnu.org/licenses/>.

*/

package serveur;

import java.util.ArrayList;

import aaaah.Aaaah;
import aaaah.Carte;
import bouboum.Bouboum;
import elo.Elo;
import forteresse.Forteresse;
import joueur.Joueur;

public class Partie {

	public boolean ChatFerme = false;
	private ArrayList<Joueur> ListeJoueurPartie;
	public ArrayList<Joueur> ListeModoPartie;
	public int CodePartie = 0;
	public int CodeJeu;
	public int JoueurMaximum;
	public String NomPartie;
	public boolean PartiePersonnalisée;
	public boolean EditeurAaaah;
	public boolean Offi;
	public String MotDePasse;
	
	public String Createur = "";
	public String CreateurOriginel = "";
	
	public int NumPartieOfficelle;
	
	public boolean NewCreateurAuto = true; // Nouveau créa après déco de l'actu
	
	public boolean Bouboum;
	public boolean PAaaah;
	public boolean Forto;
	public boolean AttenteElo;
	
	private Jeu JeuPartie;
	public PartieAttenteElo AttenteEloPartie;
	
	private int NombreModo = 0;
	private int NombreArbitre = 0;
	private int NombreAdmin = 0;
	
	public ArrayList<String> EquipeElo1 = new ArrayList<String>();
	public ArrayList<String> EquipeElo2 = new ArrayList<String>();
	public boolean PartieElo = false;
	private ArrayList<String> ArbitreElo = new ArrayList<String>();
	private ArrayList<String> FilmeurElo = new ArrayList<String>();
	private String GuideElo = "";
	private boolean MatchEloTeam = false;
	private int IdTeamsAutorisees[];
	public int ModeElo = -1; // Mode de jeu pour l'elo (les valeurs des modes sont dans la classe Elo)
	public int IDMatchELO = 0;
	
	private Serveur serveur;
	
	public Partie(boolean PARTIE_PERSO, int CODE_JEU, String NOM, String MOT_DE_PASSE, boolean EDITEUR_AAAAH, boolean OFFI) {
		this(PARTIE_PERSO, CODE_JEU, NOM, MOT_DE_PASSE, EDITEUR_AAAAH, 480000, false, false, 100000, false, 22, false, 0, false, OFFI, 0, "", "", false);
	}

	public Partie(boolean PARTIE_PERSO, int CODE_JEU, String NOM, String MOT_DE_PASSE, boolean EDITEUR_AAAAH, long TEMPS_PARTIE, boolean AutoriserChoixEquipe, boolean Dessin, int SCORE_MAX_BOUM, boolean SANS_CONTA, int MAX_JOUEURS, boolean SALON_FIGHT, int MODE_AAAAH, boolean SALON_KILL, boolean OFFI, int MODE_FORTO, String ARBITRE_ELO, String GUIDE_ELO, boolean MATCH_TEAM) {
		EditeurAaaah = EDITEUR_AAAAH;
		CodeJeu = CODE_JEU;
		NomPartie = NOM;
		PartiePersonnalisée = PARTIE_PERSO;
		ArbitreElo.add((ARBITRE_ELO.isEmpty() ? "" : ARBITRE_ELO.substring(0,1).toUpperCase() + ARBITRE_ELO.substring(1).toLowerCase()));
		GuideElo = (GUIDE_ELO.isEmpty() ? "" : GUIDE_ELO.substring(0,1).toUpperCase() + GUIDE_ELO.substring(1).toLowerCase());
		
		serveur = Serveur.getServeur();
		
		PartieElo = !ARBITRE_ELO.isEmpty();
		if (PartieElo) {
			NomPartie = "Classé ~ Arbitre : " + ArbitreElo.get(0)
					  + (GuideElo.isEmpty() ? "" : " | Guide : " + GuideElo);
		}
		
		EquipeElo1 = new ArrayList<String>();
		EquipeElo2 = new ArrayList<String>();

		if (MOT_DE_PASSE == null) {
			MotDePasse = "";
		} else {
			MotDePasse = MOT_DE_PASSE;
		}
		
		 if (CODE_JEU == 1) {
			InitBouboum(MAX_JOUEURS, SCORE_MAX_BOUM, SALON_KILL, PARTIE_PERSO);
		} else if (CODE_JEU == 2) {
			InitAaaah(SANS_CONTA, SALON_FIGHT, MODE_AAAAH);
		} else if (CODE_JEU == 3) {
			InitForteresse(TEMPS_PARTIE, AutoriserChoixEquipe, Dessin, OFFI, MODE_FORTO);
		}
		 
		if (PartieElo) {
			MatchEloTeam = MATCH_TEAM;
			AttenteEloPartie = new PartieAttenteElo(this, ArbitreElo.get(0), GuideElo, MatchEloTeam);
		}

		int Code = 0;
		while (true) {
			boolean CodeValide = true;
			int Nb = serveur.ListePartie.size();
			for (int i = 0; i < Nb; i++) {
				Partie Partie = serveur.ListePartie.get(i);
				if (Partie.CodePartie == Code) {
					CodeValide = false;
					break;
				}
			}
			if (CodeValide) {
				CodePartie = Code;
				break;
			}
			Code++;
		}

		if (!EditeurAaaah) {
			EnvoieCreationPartie(serveur);
		}
		
		Offi = OFFI;

		serveur.ListePartie.add(this);
	}
	
	private void EnvoieCreationPartie(Serveur serveur) {
		serveur.EnvoieCP("CxNP#" + CodePartie + "#" + CodeJeu + "#" + NomPartie + "#0#" + (hasMotDePasse() ? "1" : "0") + "#" + TypePartie());
	}
	
	public boolean hasMotDePasse() {
		return !MotDePasse.isEmpty();
	}
	
	public ArrayList<Joueur> getLsJoueurPartie() {
		if (!AttenteElo) {
			return ListeJoueurPartie;
		} else {
			return AttenteEloPartie.getLsJoueurPartie();
		}
		
	}
	
	public Jeu getJeu() {
		return JeuPartie;
	}
	
	public void InitBouboum(int MAX_JOUEURS, int SCORE_MAX_BOUM, boolean SALON_KILL, boolean PARTIE_PERSO) {
		Bouboum = true;
		JoueurMaximum = MAX_JOUEURS;
		Bouboum PartieBouboum = new Bouboum(serveur, this);
		PartieBouboum.ScoreMaxBoum = SCORE_MAX_BOUM;
		PartieBouboum.SalonFrag = SALON_KILL;
		PartieBouboum.SalonOffi = !PARTIE_PERSO;
		ListeJoueurPartie = PartieBouboum.ListeJoueur;
		ListeModoPartie = PartieBouboum.ListeModo;
		
		if (SALON_KILL) {
			PartieBouboum.ChangerModeJeu(new bouboum.ModeKill(PartieBouboum));
			//PartieBouboum.mode = new GestionnaireModeJeu(new bouboum.ModeKill());
		}
		
		if (!PartieBouboum.SalonOffi) {
			PartieBouboum.mode.lockMode();
		}
		
		JeuPartie = PartieBouboum;
		ModeElo = Elo.BOUM;
	}
	
	public void InitAaaah(boolean SANS_CONTA, boolean SALON_FIGHT, int MODE_AAAAH) {
		PAaaah = true;
		JoueurMaximum = 20;
		Aaaah PartieAaaah = new Aaaah(serveur, this);
		
		ListeJoueurPartie = PartieAaaah.ListeJoueur;
		ListeModoPartie = PartieAaaah.ListeModo;
		PartieAaaah.ModeJeu = MODE_AAAAH;
		PartieAaaah.SansConta = SANS_CONTA;
		PartieAaaah.InitCris(PartieAaaah.SansConta);
		PartieAaaah.SalonFight = SALON_FIGHT;
		PartieAaaah.LoadAutorisé = hasMotDePasse() || MODE_AAAAH != 0; // pour le moment équivalent à partie privée
		PartieAaaah.PartiePrivée = hasMotDePasse();
		PartieAaaah.GuidePourNG = !PartieAaaah.LoadAutorisé; // pour le moment égal à !LoadAutorisé
		PartieAaaah.TempsParPartie = (hasMotDePasse() && (MODE_AAAAH == Carte.MODE_RALLY) ? 120000 : 120000); // TODO 300000 sec en privé?
		
		if (EditeurAaaah) {
			PartieAaaah.SalonTest = true;
		}
		
		JeuPartie = PartieAaaah;
		
		if (MODE_AAAAH == Carte.MODE_DEF) {
			ModeElo = Elo.DEF;
		} else if (MODE_AAAAH == Carte.MODE_RALLY || MODE_AAAAH == Carte.MODE_NG) {
			ModeElo = Elo.RALLY_AAAAH;
		} else if (MODE_AAAAH == Carte.MODE_MS) {
			ModeElo = Elo.MS;
		} else if (MODE_AAAAH == Carte.MODE_FIGHT) {
			ModeElo = Elo.FS;
		} else if (MODE_AAAAH == Carte.MODE_OFFI) {
			ModeElo = Elo.RUN;
		}
	}
	
	public void InitForteresse(long TEMPS_PARTIE, boolean AutoriserChoixEquipe, boolean Dessin, boolean OFFI, int MODE_FORTO) {
		Forto = true;
		JoueurMaximum = 20;
		Forteresse PartieForteresse = new Forteresse(serveur, this, TEMPS_PARTIE);
		PartieForteresse.AutoriserChoixEquipe = AutoriserChoixEquipe;
		PartieForteresse.Dessin = Dessin;
		PartieForteresse.ServeurOffi = OFFI;
		PartieForteresse.RatioRouge = Dessin ? 1 : 0.5;
		PartieForteresse.ModeJeu = MODE_FORTO;
		ListeJoueurPartie = PartieForteresse.ListeJoueur;
		ListeModoPartie = PartieForteresse.ListeModo;
		
		JeuPartie = PartieForteresse;
		
		if (MODE_FORTO == Forteresse.MODE_FRAG || MODE_FORTO == Forteresse.MODE_KILL) {
			PartieForteresse.SansBombe = true;
			PartieForteresse.AfficherKills = true;
			PartieForteresse.SansFrigo = true;
			
			ModeElo = (MODE_FORTO == Forteresse.MODE_FRAG ? Elo.FRAG : Elo.KILL);
		} else if (MODE_FORTO == Forteresse.MODE_CROSS) {
			PartieForteresse.SansBombe = true;
			PartieForteresse.SansFrigo = true;
			
			ModeElo = Elo.CROSS;
		} else {
			ModeElo = Elo.FRIGO;
		}
		
		if (OFFI) { // Cadeaux aléatoires lors de la construction (event Noël)
			PartieForteresse.cadeuxActives = serveur.cadeauxFortoActives;
		}
	}
	
	public void setTeamsAutorisees(int team1, int team2) {
		IdTeamsAutorisees = new int[2];
		IdTeamsAutorisees[0] = team1;
		IdTeamsAutorisees[1] = team2;
	}

	public void Nouveau_Joueur(Joueur JOUEUR, Boolean ChoixEquipeRouge) {
		if (AttenteElo) {
			AttenteEloPartie.Rejoindre_Partie(JOUEUR);
			return;
		} else if (PartieElo && !ArbitreElo.contains(JOUEUR.NomJoueur) && !GuideElo.equals(JOUEUR.NomJoueur) && !FilmeurElo.contains(JOUEUR.NomJoueur) &&
				((!MatchEloTeam && !EquipeElo1.contains(JOUEUR.NomJoueur) && !EquipeElo2.contains(JOUEUR.NomJoueur))
				|| (MatchEloTeam && IdTeamsAutorisees[0] != JOUEUR.IdTeam && IdTeamsAutorisees[1] != JOUEUR.IdTeam))) {
			
			JOUEUR.Envoie("CxINFO#Cette partie classée a déjà commencé et ne vous est pas accessible.");
			return;
		} else if (ArbitreElo.contains(JOUEUR.NomJoueur)) {
			JOUEUR.Envoie("CxARBT");
			JOUEUR.AutorisationArbitreElo = true;
		} else if (FilmeurElo.contains(JOUEUR.NomJoueur)) {
			JOUEUR.Envoie("CxFILMTE");
			JOUEUR.AutorisationFilmeurElo = true;
		} else if (PartieElo && MatchEloTeam) { // Log des joueurs non arbitres pas dans les joueurs de base
			if (!EquipeElo1.contains(JOUEUR.NomJoueur) && !EquipeElo2.contains(JOUEUR.NomJoueur)) {
				System.out.println("MTCELO " + IDMatchELO + " " + JOUEUR.getPseudo());
			}
		}
		
		JOUEUR.TempsZero = System.currentTimeMillis();
		JOUEUR.TempsZeroRally = System.currentTimeMillis();
		serveur.ListeJoueurSansPartie.remove(JOUEUR);
		JOUEUR.SansPartie = false;
		JOUEUR.PartieEnCours = this;
		JOUEUR.ReceptionBouboum = false;
		JOUEUR.ReceptionAaaah = false;
		JOUEUR.ReceptionForteresse = false;
		
		Completer_Connexion(JOUEUR, ChoixEquipeRouge);
	}
	
	public void Completer_Connexion(Joueur JOUEUR, Boolean ChoixEquipeRouge) {
		JOUEUR.Envoie("CxJ#" + CodeJeu);
		
		// 
		if (serveur.Evénement.StValentin()) {
			Joueur Amoureux = serveur.TrouverAmoureux(serveur.ListeJoueur, JOUEUR);
			if (Amoureux != null) {
				for (Joueur Joueur : getLsJoueurPartie()) {
					if (Joueur.AutorisationTournoiAaaah || Joueur.AutorisationTournoiForto || Joueur.AutorisationArbitreElo || Joueur.AutorisationTounoiArbitreSecondaire) {
						Joueur.Envoie("CxINFO#" + JOUEUR.NomJoueur + " est lié avec " + Amoureux.NomJoueur +".");
					}
				}
			}
			
		}
		//
		
		if (Bouboum) {
			JOUEUR.ReceptionBouboum = true;
			Nouveau_Joueur_Bouboum(JOUEUR);
		} else if (PAaaah) {
			JOUEUR.ReceptionAaaah = true;
			Nouveau_Joueur_Aaaah(JOUEUR);
		} else if (Forto) {
			JOUEUR.ReceptionForteresse = true;
			Nouveau_Joueur_Forteresse(JOUEUR, ChoixEquipeRouge);
		}
		//
		if (JOUEUR.AutorisationAdmin) {
			NombreAdmin++;
		} else if (JOUEUR.AutorisationModo) {
			NombreModo++;
		} else if (JOUEUR.AutorisationArbitre) {
			NombreArbitre++;
		}
		//
		if (EditeurAaaah) {
			serveur.EnvoieCP("CxR#" + JOUEUR.NomJoueur);
		} else {
			serveur.MAJ_Liste_Partie("CxR#" + JOUEUR.NomJoueur + "#" + CodePartie + "#" + JOUEUR.PartieEnCours.getLsJoueurPartie().size() + "#", this);
		}
		// Première connexion
		if (!JOUEUR.EntièrementConnecté) {
			JOUEUR.EntièrementConnecté = true;
			serveur.Joueur_Entièrement_Connecté(JOUEUR);
		}
	}

	/**
	 * @param JOUEUR
	 * 			Le joueur qui reçoit la couleur dans sa liste
	 */
	public int Couleur_Partie(Joueur JOUEUR) {
		if (JOUEUR.AutorisationInscription) {
			if(JOUEUR.AutorisationModo) 
			{
				if (NombreAdmin > 0) 
					return 1;
				else if (NombreModo > 0) 
					return 2;
				else if (NombreArbitre > 0) 
					return 3;
			}
		}


		boolean CoequipierTrouvé = false;
		int i = 0;
		if(JOUEUR.Membre){
			
			while(!CoequipierTrouvé && i < ListeJoueurPartie.size() )
			{
				Joueur joueur = ListeJoueurPartie.get(i);
				CoequipierTrouvé = joueur.Membre && joueur.IdTeam == JOUEUR.IdTeam;
				i++;
			}
			
		}

		if(CoequipierTrouvé)
			return 4;
		else{
			boolean AmiTrouvé = false;
			i = 0;

			//Trouver s'il y a un ami dans la partie
			while(!AmiTrouvé && i < JOUEUR.ListeAmis.size())
			{
				int j = 0;
				while(!AmiTrouvé && j < ListeJoueurPartie.size())
				{
					AmiTrouvé = JOUEUR.ListeAmis.get(i).IdAmi == ListeJoueurPartie.get(j).IdJoueur;
					j++;
				}
				i++;
			}

			if(AmiTrouvé) 
				return 5;
		}

		return 0;
	}

	public String TypePartie() {
		String Resultat = "";
		
		if (PartieElo) {
			Resultat = "[C] ";
		}
		
		if (!PartiePersonnalisée) {
			Resultat += "Officiel";
		} else if (JeuPartie instanceof Forteresse) {
			Forteresse PartieForteresse = (Forteresse)JeuPartie;
			if (PartieForteresse.ModeJeu != Forteresse.MODE_AUCUN) {
				Resultat += " - Maps ";
				
				switch (PartieForteresse.ModeJeu) {
					case Forteresse.MODE_FRIGO:
						Resultat += "Frig";
						break;
					case Forteresse.MODE_FRAG:
						Resultat += "Frag";
						break;
					case Forteresse.MODE_KILL:
						Resultat += "Kill";
						break;
					case Forteresse.MODE_CROSS:
						Resultat += "Cross";
						break;
				}
			}
		} else if (JeuPartie instanceof Bouboum) {
			Bouboum PartieBouboum = (Bouboum)JeuPartie;
			if (PartieBouboum.SalonFrag) {
				Resultat += "Kill";
			}
		} else if (JeuPartie instanceof Aaaah) {
			Aaaah PartieAaaah = (Aaaah)JeuPartie;
			
			if (PartieAaaah.SalonFight) {
				Resultat += "Fight";
			}
			if (PartieAaaah.ModeJeu != Carte.MODE_NORMAL) {
				if (!Resultat.isEmpty()) {
					Resultat += " - ";
				}
				Resultat += "Maps ";
				
				switch (PartieAaaah.ModeJeu) {
					case Carte.MODE_OFFI:
						Resultat += "run";
						break;
					case Carte.MODE_NG:
						Resultat += "NG";
						break;
					case Carte.MODE_DEF:
						Resultat += "défilantes";
						break;
					case Carte.MODE_FIGHT:
						Resultat += "fight";
						break;
					case Carte.MODE_MS:
						Resultat += "MS";
						break;
					case Carte.MODE_RALLY:
						Resultat += "rally";
						break;
				}
			}
			}
		if (Resultat.isEmpty()) {
			Resultat = "-";
		}
		return Resultat;
	}

	private void Nouveau_Joueur_Bouboum(Joueur JOUEUR) {
		Bouboum PartieBouboum = (Bouboum)JeuPartie;
		
		JOUEUR.Vivant = false;
		JOUEUR._Bouboum.Score = 0;
		//
		StringBuilder LsJoueur = new StringBuilder("IdL#" + JOUEUR.NomJoueur + "," + JOUEUR._Bouboum.Score + "%" + JOUEUR._Bouboum.Stats_PartieGagnée + "," + JOUEUR.recompenseElo.getRecompenseBouboum());
		int Nb = ListeJoueurPartie.size();
		for (int i = 0; i < Nb; i++) {
			Joueur Joueur = ListeJoueurPartie.get(i);
			LsJoueur.append("#" + Joueur.NomJoueur + "," + Joueur.IdTeam + "," + Joueur._Bouboum.Score + "%" + Joueur._Bouboum.Stats_PartieGagnée + "," + Joueur.recompenseElo.getRecompenseBouboum());
		}
		//Liste_Joueur_Chat(JOUEUR);
		serveur.Envoie(ListeJoueurPartie, "IdJ#" + JOUEUR.NomJoueur + "#" + JOUEUR._Bouboum.Stats_PartieGagnée + "#" + JOUEUR.IdTeam + "#" + JOUEUR.recompenseElo.getRecompenseBouboum());
		ListeJoueurPartie.add(JOUEUR);
		if (JOUEUR.AutorisationModo) {
			ListeModoPartie.add(JOUEUR);
			//JOUEUR.Envoie(LsJoueur.toString());
		}
		JOUEUR.Envoie(LsJoueur.toString());
		//
		int NombreJoueur = ListeJoueurPartie.size();
		if (NombreJoueur == 1) {
			PartieBouboum.Nouvelle_Partie(null, null);
			Createur = JOUEUR.NomJoueur;
			CreateurOriginel = Createur;
		} else {
			if (NombreJoueur > 1) {
				JOUEUR.Envoie("IdI#" + (System.currentTimeMillis() - PartieBouboum.DebutPartie));
			}
		}
		//
		PartieBouboum.Nouveau_Joueur(JOUEUR);
		//
		if (PartieBouboum.SalonFrag) {
			JOUEUR.Envoie("CxINFO#Vous entrez dans un salon kill. Les statistiques sont désactivées. Le but est de tuer le plus d'adversaires possible sans vous suicider.");
		}
		
		//JOUEUR._Bouboum.resetModeCiblePerso();
		
		Envoyer_Couleurs_Equipe_Elo(JOUEUR);
	}
	
	private void Envoyer_Couleurs_Equipe_Elo(Joueur J) {
		if (PartieElo) {
			
			if (MatchEloTeam) { // Gestion du cas où un joueur changerait de team
				if (!ArbitreElo.contains(J.NomJoueur) && (GuideElo != null && !GuideElo.equals(J.NomJoueur) && !FilmeurElo.contains(J.NomJoueur))) { // Arbitres & Guides sans couleur
					EquipeElo1.remove(J.NomJoueur);
					EquipeElo2.remove(J.NomJoueur);
					
					if (J.IdTeam == IdTeamsAutorisees[0]) {
						EquipeElo1.add(J.getPseudo());
					} else if (J.IdTeam == IdTeamsAutorisees[1]) {
						EquipeElo2.add(J.getPseudo());
					}
				}
			}
			
			serveur.Envoie(getLsJoueurPartie(), "EloC#" + String.join(";", EquipeElo2));
		}
	}

	private void Nouveau_Joueur_Aaaah(Joueur JOUEUR) {
		Aaaah PartieAaaah = (Aaaah)JeuPartie;
		
		JOUEUR.Vivant = false;
		JOUEUR._Aaaah.Score = 0;
		JOUEUR._Aaaah.Guide = false;
		//
		StringBuilder LsJoueur = new StringBuilder("IdL#" + PartieAaaah.GetParamCris()+ "#" + (PartieAaaah.SalonFight ? "1" : "0") + "#" + PartieAaaah.ModeJeu + "#" + (PartieAaaah.ModeJeu == Carte.MODE_RALLY && !PartieAaaah.PartiePrivée ? "1" : "0") + "#" + JOUEUR.NomJoueur + "," + JOUEUR.recompenseElo.getRecompenseAaaah());
		int Nb = ListeJoueurPartie.size();
		for (int i = 0; i < Nb; i++) {
			Joueur Joueur = ListeJoueurPartie.get(i);
			LsJoueur.append("#" + Joueur.NomJoueur + "," + Joueur._Aaaah.Score + "," + Joueur._Aaaah.CodeZombie + "," + Joueur.IdTeam + "," + Joueur.recompenseElo.getRecompenseAaaah());
		}
		// ajouter faux joueur ghost
		if (PartieAaaah.ModeJeu == Carte.MODE_RALLY) {
			LsJoueur.append("#Ghost,1000,0,0,0");
		}
		//Liste_Joueur_Chat(JOUEUR);
		serveur.Envoie(ListeJoueurPartie, "IdJ#" + Aaaah.GenererInfoNouveauJoueur(JOUEUR));
		ListeJoueurPartie.add(JOUEUR);
		if (JOUEUR.AutorisationModo) {
			ListeModoPartie.add(JOUEUR);
			//JOUEUR.Envoie(LsJoueur.toString());
		}
		JOUEUR.Envoie(LsJoueur.toString());
		if (PartieAaaah.MondeEnCours != -1) {
			if (PartieAaaah.MondeEnCours > 1000) {
				Carte Carte = serveur.Aaaah_ListeCarte.get(PartieAaaah.MondeEnCours);
				if (Carte == null) {
					JOUEUR.Envoie("ChL#0");
				} else {
					JOUEUR.Envoie("ChL#" + Carte.Code + "#" + Carte.Auteur);
				}
			} else {
				JOUEUR.Envoie("ChL#" + PartieAaaah.MondeEnCours);
			}
		}
		//
		if (ListeJoueurPartie.size() == 1) {
			PartieAaaah.Nouvelle_Partie(-1);
			Createur = JOUEUR.NomJoueur;
			CreateurOriginel = Createur;
		}
		//
		PartieAaaah.Nouveau_Joueur(JOUEUR);
		//
		JOUEUR.Envoie("ChI#" + PartieAaaah.NomGuideSalon() + "#" + (System.currentTimeMillis() - PartieAaaah.DebutPartie) + "#" + PartieAaaah.JoueurDepart + "#" + PartieAaaah.JoueurEnVie);
		Envoyer_Couleurs_Equipe_Elo(JOUEUR);
		
		JOUEUR.Envoie(PartieAaaah.Positions());
		
		if (!PartieAaaah.ParamCriRecordValides()) {
			JOUEUR.Envoie("CxINFO#Les records sont désactivés à cause de la modification des cris.");
		}
	}

	private void Nouveau_Joueur_Forteresse(Joueur JOUEUR, Boolean ChoixEquipeRouge) {
		Forteresse PartieForteresse = (Forteresse)JeuPartie;
		JOUEUR._Forteresse.Score = 0;
		JOUEUR.RallyRéussi = false;
		JOUEUR._Forteresse.ToucheFrigo = 0;
		//JOUEUR.Envoie("90" + $ + SALON);
		JOUEUR._Forteresse.CodeJoueur = serveur.Code_Joueur_Forteresse(JOUEUR, PartieForteresse);
		
		if (PartieElo) {
			if (MatchEloTeam) {
				if (!ArbitreElo.contains(JOUEUR.NomJoueur) && !FilmeurElo.contains(JOUEUR.NomJoueur)) { // Arbitres & Guides sans couleur
					if (JOUEUR.IdTeam == IdTeamsAutorisees[0]) {
						ChoixEquipeRouge = true;
					} else if (JOUEUR.IdTeam == IdTeamsAutorisees[1]) {
						ChoixEquipeRouge = false;
					}
				}
			} else if (EquipeElo2.contains(JOUEUR.NomJoueur)) { // Equipe bleue
				ChoixEquipeRouge = false;
			} else { // Equipe rouge / arbitre
				ChoixEquipeRouge = true;
			}
		}
		
		// Equipe  
		if ((ChoixEquipeRouge == null && (PartieForteresse.RatioRouge == 0 || ((PartieForteresse.RatioRouge)*PartieForteresse.EquipeBleu < (1-PartieForteresse.RatioRouge)*PartieForteresse.EquipeRouge))) 
				|| (ChoixEquipeRouge != null && !ChoixEquipeRouge.booleanValue())) {
			//JOUEUR._Forteresse.Equipe = 2;
			JOUEUR._Forteresse.setBleu();
			PartieForteresse.EquipeBleu++;
		} else {
			//JOUEUR._Forteresse.Equipe = 1;
			JOUEUR._Forteresse.setRouge();
			PartieForteresse.EquipeRouge++;
		}
		JOUEUR._Forteresse.Reset();
		//
		boolean NouvellePartie = ListeJoueurPartie.size() == 0;
		//
		String $ = Serveur.$;
		StringBuilder LsJoueur = new StringBuilder("91" + $ + JOUEUR.NomJoueur + $ + JOUEUR._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.Score + $ + JOUEUR._Forteresse.ToucheFrigo + $ + JOUEUR._Forteresse.PosX + $ + JOUEUR._Forteresse.PosY + $ + JOUEUR._Forteresse.Sexe + $ + JOUEUR._Forteresse.EquipeToCode() + $ + "1");
		int Nb = ListeJoueurPartie.size();
		for (int i = 0; i < Nb; i++) {
			Joueur Joueur = ListeJoueurPartie.get(i);
			String Vivant;
			if (Joueur.Vivant) {
				Vivant = "1";
			} else {
				Vivant = "0";
			}
			LsJoueur.append($ + Joueur.NomJoueur + $ + Joueur._Forteresse.CodeJoueur + $ + Joueur._Forteresse.Score + $ + Joueur._Forteresse.ToucheFrigo + $ + Joueur._Forteresse.PosX + $ + Joueur._Forteresse.PosY + $ + Joueur._Forteresse.Sexe + $ + Joueur._Forteresse.EquipeToCode() + $ + Vivant);
		}
		serveur.Envoie(ListeJoueurPartie, "30" + $ + JOUEUR.NomJoueur + $ + JOUEUR._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.Sexe + $ + JOUEUR._Forteresse.EquipeToCode());
		ListeJoueurPartie.add(JOUEUR);
		if (JOUEUR.AutorisationModo) {
			ListeModoPartie.add(JOUEUR);
		}
		JOUEUR.Envoie(LsJoueur.toString());
		if (NouvellePartie) {
			Createur = JOUEUR.NomJoueur;
			CreateurOriginel = Createur;
			PartieForteresse.DecalageLancement = true;
			PartieForteresse.Nouvelle_Partie(-1, null, false);
		} else {
			String ViesFrigos;
			if (PartieForteresse.MondeEnCours.DoubleAttaque) {
				ViesFrigos = PartieForteresse.Vie1 + $ + PartieForteresse.Vie2;
			} else {
				ViesFrigos = String.valueOf(PartieForteresse.Vie1) + $ + "";
			}
			
			JOUEUR.Envoie("99" + $ + (System.currentTimeMillis() - PartieForteresse.DebutPartie) + $ + PartieForteresse.Liste_Case()
			              + $ + PartieForteresse.GetStrCouleursConnexionPartie()
			              + $ + PartieForteresse.CodeAttaque + $ + (PartieForteresse.getDureePartie() / 1000) + $ + /*(PartieForteresse.Dessin ? Createur : "")*/"" + $ + ViesFrigos
			              + $ + PartieForteresse.PresidentRouge + $ + PartieForteresse.PresidentBleu + $ + (PartieForteresse.PeutSauverMap()?"1":"0") + $ + PartieForteresse.MondeEnCours);
			
			String infos = PartieForteresse.getInfosCarteNouvConnexion();
			if (infos != null) {
				JOUEUR.Envoie(infos);
			}
		}
		//
		PartieForteresse.Nouveau_Joueur(JOUEUR);
		//
		if (PartieForteresse.SansBombe) {
			JOUEUR.Envoie("CxINFO#Les bombes sont désactivées dans ce salon.");
		}
		if (PartieForteresse.SansFrigo) {
			JOUEUR.Envoie("CxINFO#Les frigos sont désactivés dans ce salon.");
		}
		if (PartieForteresse.AfficherKills) {
			JOUEUR.Envoie("CxINFO#L'affichage des kills aux déconnexions est activé dans ce salon.");
		}
	}

	public void Quitter_partie(Joueur JOUEUR) {		
		if(!JOUEUR.PartieEnCours.EditeurAaaah) {
			// Affichage des options débloquées via le temps de jeu
			serveur.OptionsDebloqueesTemps(JOUEUR);
		}
		if ((JOUEUR.PartieEnCours == null || !JOUEUR.PartieEnCours.EditeurAaaah) && !(JOUEUR.AutorisationTournoiForto || JOUEUR.AutorisationTournoiAaaah || JOUEUR.AutorisationFilmeur)) {
			// on incrémente le temps de jeu (si on n'est pas dans l'éditeur)
			long TempsEnPlus = System.currentTimeMillis() - JOUEUR.TempsZero;
			
			JOUEUR.Stats_TempsDeJeu += TempsEnPlus;
			if (JOUEUR.PartieEnCours != null && JOUEUR.PartieEnCours.Bouboum) { //Aaaah
				JOUEUR.Mara_TempsJeu += TempsEnPlus;
			}
			if (JOUEUR.Stats.statByGame(JOUEUR.PartieEnCours) != null) {
				JOUEUR.Stats.statByGame(JOUEUR.PartieEnCours).TempsDeJeu += TempsEnPlus;						 
			}
			JOUEUR.TempsZero = System.currentTimeMillis();
		}
		
		if (JOUEUR.PartieEnCours.Forto) {
			Forteresse PartieForteresse = (Forteresse)JOUEUR.PartieEnCours.getJeu();
			if (PartieForteresse.NumeroDonjon != 0) {
				serveur.BOITE.Requete(Boite.SVGD_AVANCEMENT_DONJON, JOUEUR);
			}
			
			if (PartieForteresse.AfficherKills) { // Affichage des kills sur Forteresse si la commande est activée (ici car sinon pas affichée aux déco du jeu)
				serveur.Envoie(PartieForteresse.ListeJoueur, "CxINFO#" + JOUEUR.NomJoueur + " (" + JOUEUR._Forteresse.Score + ") kills.");
			}
			
			if (JOUEUR.PartieEnCours.PartieElo) {
				serveur.Envoie(PartieForteresse.ListeJoueur, "CxINFO#" + JOUEUR.NomJoueur + " vie : " + JOUEUR._Forteresse.Santé + " ; bombes : " + JOUEUR._Forteresse.Jambombe);
			}
			
			if (serveur.Evénement.StValentin()) {
				Joueur J = serveur.Joueur(JOUEUR.Amoureux);
				if (J != null && J.Amoureux.equals(JOUEUR.NomJoueur)) {
					if (JOUEUR.PartieEnCours != null && JOUEUR.PartieEnCours.Forto && getLsJoueurPartie().contains(J)) {
						PartieForteresse.Mort_Joueur_Balles(J, JOUEUR, "STVAL");
					}
				}
			}
		}
		
		if (ArbitreElo.contains(JOUEUR.NomJoueur)) { // Arbitre temporaire pour les classés
			if (!(JOUEUR.AutorisationTournoiAaaah || JOUEUR.AutorisationTournoiForto)) {
				JOUEUR.Envoie("CxNARBT");
			}
			JOUEUR.AutorisationArbitreElo = false;
		}
		
		if (FilmeurElo.contains(JOUEUR.NomJoueur)) {
			if (!(JOUEUR.AutorisationTournoiAaaah || JOUEUR.AutorisationTournoiForto)) {
				JOUEUR.Envoie("CxNFILMTE");
			}
			JOUEUR.AutorisationFilmeurElo = false;
		}
		
		//VerifierDecoElo(JOUEUR.NomJoueur);
		
		if (!JOUEUR.PartieEnCours.hasMotDePasse() && !JOUEUR.PartieEnCours.Offi) { // Nouveau responsable en cas de déco de celui actuel
			if (((!JOUEUR.PartieEnCours.Createur.isEmpty() && JOUEUR.PartieEnCours.Createur.equals(JOUEUR.NomJoueur))
				|| (JOUEUR.PartieEnCours.Createur.isEmpty() && JOUEUR.PartieEnCours.CreateurOriginel.equals(JOUEUR.NomJoueur)))
				&& JOUEUR.PartieEnCours.NewCreateurAuto) {
					
				ArrayList<Joueur> ListeJoueurDansPartie = JOUEUR.PartieEnCours.getLsJoueurPartie();
				
				if (ListeJoueurDansPartie != null && ListeJoueurDansPartie.size() > 0) {
					int  i;
					
					for(i = 0; i < ListeJoueurDansPartie.size() && (ListeJoueurDansPartie.get(i).NomJoueur.charAt(0) == '*' || ListeJoueurDansPartie.get(i).NomJoueur.equalsIgnoreCase(JOUEUR.NomJoueur)); ++i);
					
					if (i == ListeJoueurDansPartie.size()) { // Que des comptes invités restants -> sélection premier compte invité différent du joueur
						for(i = 0; i < ListeJoueurDansPartie.size() && ListeJoueurDansPartie.get(i).NomJoueur.equalsIgnoreCase(JOUEUR.NomJoueur); ++i);
					}
					
					if (i != ListeJoueurDansPartie.size()) {
						JOUEUR.PartieEnCours.Createur = ListeJoueurDansPartie.get(i).NomJoueur;
						serveur.Envoie(ListeJoueurDansPartie, "CxINFO#" + JOUEUR.PartieEnCours.Createur + " est maintenant responsable de la partie.");
					}
				} 
				
			}
		}

		if (JOUEUR.PartieEnCours.getJeu() instanceof Bouboum) {
			Bouboum PartieBouboum = (Bouboum)JOUEUR.PartieEnCours.getJeu();
			
			ListeJoueurPartie.remove(JOUEUR);
			if (JOUEUR.AutorisationModo) {
				ListeModoPartie.remove(JOUEUR);
				//
			}
			if (JOUEUR.NomJoueur != null) {
				if (JOUEUR.Vivant) {
					PartieBouboum.Mort(JOUEUR, JOUEUR.NomJoueur, JOUEUR, true, "");
				}
				if (JOUEUR.Banni && !JOUEUR.BanSilencieux) {
					serveur.Envoie(ListeJoueurPartie, "IdD#" + JOUEUR.NomJoueur + "#1");
				} else {
					serveur.Envoie(ListeJoueurPartie, "IdD#" + JOUEUR.NomJoueur);
				}
			}
		}
		//
		if (JOUEUR.PartieEnCours.getJeu() instanceof Aaaah) {
			Aaaah PartieAaaah = (Aaaah)JOUEUR.PartieEnCours.getJeu();
			
			ListeJoueurPartie.remove(JOUEUR);
			if (JOUEUR.AutorisationModo) {
				ListeModoPartie.remove(JOUEUR);
				//
			}
			if (JOUEUR.NomJoueur != null) {
				if (JOUEUR.Vivant) {
					serveur.Envoie(ListeJoueurPartie, "IdX#" + JOUEUR.NomJoueur + "#1");
					if (!JOUEUR._Aaaah.Zombie) {
						PartieAaaah.JoueurSauve++;
						PartieAaaah.JoueurEnMoins();
					}
					// skip la partie s'il ne reste qu'un survivant et aucun contaminé
					PartieAaaah.CheckDernierJoueur();
				}
				if (JOUEUR.Banni && !JOUEUR.BanSilencieux) {
					serveur.Envoie(ListeJoueurPartie, "IdD#" + JOUEUR.NomJoueur + "#1");
				} else {
					serveur.Envoie(ListeJoueurPartie, "IdD#" + JOUEUR.NomJoueur);
				}
				if (PartieAaaah.PartieEnCours && PartieAaaah.Guide != null && PartieAaaah.Guide.equals(JOUEUR)) {
					// c'est le guide qui est parti
					PartieAaaah.PartieEnCours = false;
					PartieAaaah.TimerPartie.Active = false;
					PartieAaaah.Nouvelle_Partie(-1);
				}
			}
		}
		//
		if (JOUEUR.PartieEnCours.getJeu() instanceof Forteresse) {
			Forteresse PartieForteresse = (Forteresse)JOUEUR.PartieEnCours.getJeu();
			
			ListeJoueurPartie.remove(JOUEUR);
			if (JOUEUR.AutorisationModo) {
				ListeModoPartie.remove(JOUEUR);

				//
			}
			if (JOUEUR.NomJoueur != null) {
				if (ListeJoueurPartie.isEmpty()) {
					PartieForteresse.Nouvelle_Partie(-1, null, false);
				}
				if (JOUEUR._Forteresse.Respawn != null) {
					JOUEUR._Forteresse.Respawn.Active = false;
				}
				if (JOUEUR._Forteresse.aEquipe()) {
					if (JOUEUR._Forteresse.estRouge()) {
						PartieForteresse.EquipeRouge--;
					} else {
						PartieForteresse.EquipeBleu--;
					}
				}
				PartieForteresse.Explosion_Total(JOUEUR);
				//
				String $ = Serveur.$;
				
				if (JOUEUR.Banni && !JOUEUR.BanSilencieux) {
					serveur.Envoie(ListeJoueurPartie, "31" + $ + JOUEUR._Forteresse.CodeJoueur + $ + "1");
				} else {
					serveur.Envoie(ListeJoueurPartie, "31" + $ + JOUEUR._Forteresse.CodeJoueur);
				}
			}
			
			if (PartieForteresse.ModePresident) { // Défaite de l'équipe si déco du président
				PartieForteresse.GestionDeconnexionPresident(JOUEUR._Forteresse.CodeJoueur);
			}
		}
		//
		JOUEUR.AutorisationTounoiArbitreSecondaire = false;
		//
		int NbJoueur = ListeJoueurPartie.size();
		if (NbJoueur == 0) {
			JeuPartie.Destruction();
			JeuPartie = null;
			//				ListeJoueurPartie = null;  //Pourquoi le mettre sur null ? (NullPointerException dans Couleur_Partie())
			serveur.ListePartie.remove(this);
		}
		//
		if (JOUEUR.AutorisationAdmin) {
			NombreAdmin--;
		} else {
			if (JOUEUR.AutorisationModo) {
				NombreModo--;
			} else {
				if (JOUEUR.AutorisationArbitre) {
					NombreArbitre--;
				}
			}
		}
		//
		JOUEUR.PartieEnCours = null;
		serveur.MAJ_Liste_Partie("CxQ#" + CodePartie + "#" + NbJoueur + "#", this);
	}
	
	public String getArbitreElo() {
		return (ArbitreElo.size() > 0 ? ArbitreElo.get(0) : "");
	}
	
	public boolean addArbitreElo(String ARBITRE) {
		if (!ARBITRE.startsWith("*")) {
			Joueur J = serveur.Joueur(ARBITRE);
			
			if (J == null) {
				return false;
			}
			
			if (!ArbitreElo.contains(J.getPseudo())) {
				ArbitreElo.add(J.getPseudo());
				//Generer_Code_Match_Elo();
				return true;
			}
		}
		
		return false;
	}
	
	public boolean addFilmeurElo(String FILMEUR) {
		if (!FILMEUR.startsWith("*")) {
			Joueur J = serveur.Joueur(FILMEUR);
			
			if (J == null) {
				return false;
			}
			
			if (!FilmeurElo.contains(J.getPseudo())) {
				FilmeurElo.add(J.getPseudo());
				//Generer_Code_Match_Elo();
				return true;
			}
		}
		
		return false;
	}
}