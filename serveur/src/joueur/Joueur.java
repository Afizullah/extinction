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

package joueur;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aaaah.Aaaah;
import aaaah.Carte;
import aaaah.JoueurAaaah;
import bouboum.Bonus;
import bouboum.Bouboum;
import bouboum.JoueurBouboum;
import elo.Elo;
import elo.EloJoueur;
import elo.RecompenseElo;
import evenement.JoueurEv;
import forteresse.Forteresse;
import forteresse.JoueurForteresse;
import forteresse.MondeForteresse;
import forteresse.Rally;
import serveur.Ami;
import serveur.Boite;
import serveur.FusionTeam;
import serveur.Jeu;
import serveur.Local;
import serveur.MessageDecoupe;
import serveur.Nio;
import serveur.Partie;
import serveur.PartieAttenteElo;
import serveur.Serveur;
import serveur.Signalement;
import serveur.SignalementForum;
import serveur.SignalementMap;
import yshCastle.JoueurDonjon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.sql.ResultSet;

public class Joueur {
	public ConcurrentLinkedQueue<String> MessageBoite = new ConcurrentLinkedQueue<String>();
	public int CodeMalDeTêteEnCours = (int) (Math.random() * 8000) + 1000;
	public String AdresseIP;
	public String Password = "";
	public String AdresseHost = "";
	public String HostSimple = "";
	public String AutorisationChgPass = "0";
	public SocketChannel Socket;
	private Serveur serveur;
	private Nio ServeurNio;
	public LinkedList<ByteBuffer> ListeMessage = new LinkedList<ByteBuffer>();
	public MessageDecoupe MessageDecoupe;
	// speedhack test
	public long DernierMessageVide = 0;
	public long LastLoad = -99999999;
	public long LastMP = -99999999;
	public long LastIgnore = -99999999;
	public long DerniereCoForum = System.currentTimeMillis()/1000;
	int Speedhack = 0;
	public int SurplusMunitions = 0;
	int CroixRouge = 0;
	int Detecte = 0;
	public long LimiteChat;
	public boolean Ronflement = false;
	
	private boolean VersionOk = false;
	// TMP
	private boolean ADecoProchainMsg = false;
	//
	public boolean ReceptionBouboum = false;
	public boolean ReceptionAaaah = false;
	public boolean ReceptionForteresse = false;
	static String $ = String.valueOf((char) 2);
	
	public String NomJoueur;
	public String NomCanalArbitre;
	public String NomCanalForum;
	public String NomJoueurModifié;
	public boolean NomModifié = false;
	public String Avatar = Serveur.AVATAR_DEFAUT;
	public String Description = "";
	public int IdJoueur = 0;
	public JoueurBouboum _Bouboum;
	public JoueurAaaah _Aaaah;
	public JoueurForteresse _Forteresse;
	public Partie PartieEnCours;
	public Partie PartieEnAttente;
	public boolean SansPartie = false;
	
	//! Vivant = bleu ou conta, mais pas mort dans un trou
	public boolean Vivant = false;
	public boolean Afk = false;
	public int Afk_Score = 0;
	public Partie Afk_Partie;
	public boolean SurLeForum = false;
	public boolean DecoTricheAuto = true;
	public boolean RallyRéussi = false;
	public boolean MortAuto = false;
	public boolean ParticipeSurvivalKick = false;
	public boolean MessagesEvent = true; // Afficher les messages générés spécialement pendant un événement
	public String Amoureux = "";

	public String DernièreIP = "";
	

	// public int Score = 0;
	public boolean CréationCompte = true;
	public boolean Identifié = false;
	public ArrayList<String> DemandeBan = new ArrayList<String>();
	public ArrayList<String> DemandeBanIP = new ArrayList<String>();
	public String ListeJoueursIgnorésChaine = "";
	public ArrayList<String> ListeJoueursIgnorés = new ArrayList<String>();
	public ArrayList<String> ListeIPIgnorées = new ArrayList<String>();

	public ArrayList<Ami> ListeAmis = new ArrayList<Ami>();
	private ArrayList<String> ListeIgnore = new ArrayList<String>();
	public long TempsMuet;
	public boolean Banni = false;
	public boolean BanSilencieux = false;
	public boolean Muet = false;
	public boolean MuteChatPrincipal = false;
	public boolean MuetForum = false;
	public boolean AccepteChuchotement = true;
	public boolean AccepteCanalTeam = true;
	public boolean AccepteCanalMP = true;
	public boolean AccepteCanalAverto = true;
	public boolean AccepteCanalAvertoForum = false;
	public boolean AccepteCanalA = true;
	public boolean AccepteAVCandidatures = true;
	public String SalonChat = "";
	public boolean ContMute = false;
	public String elo_flag;

	//
	private boolean ToutEnMaj = false;
	private boolean ToutEnMin = false;
	public boolean CouleurPseudo = false;
	public boolean CouleurMessage = false;
	public String CouleurPseudoValeur;
	public String CouleurMessageValeur;
	//
	
	public int FlagMessageAnim = 0;

	// Forum
	public int ForumEnCours = 3;
	public int SujetEnCours = 0;
	public int PageEnCours = 0;
	public int PageForumEnCours = 0;

	// Autorisations
	public boolean AutorisationInscription = false;
	public boolean AutorisationFilmeur = false;
	public boolean FilmPartiePublique = false; // Autoriser un filmeur à filmer en publique
	public boolean AutorisationBeta = true;
	public boolean AutorisationModo = false;
	public boolean AutorisationModoPublic = false;
	public boolean AutorisationModoFPublic = false;
	public boolean AutorisationSuperModo = false;
	public boolean AutorisationModoForum = false;
	public boolean AutorisationAdmin = false;
	public boolean AutorisationDev = false;
	public boolean AutorisationArbitre = false;
	public boolean AutorisationRecrue = false;
	public boolean AutorisationRecrueForum = false;
	public boolean AutorisationSuperArbitre = false;
	public boolean AutorisationTournoiForto = false;
	public boolean AutorisationTournoiAaaah = false;
	public boolean AutorisationTounoiArbitreSecondaire = false;
	public boolean AutorisationForum = false;
	public boolean AutorisationModoTeam = false;
	public boolean AutorisationRespoTriTeam = false;
	public boolean AutorisationModoCartes = false;
	public boolean AutorisationRespoAaaahRecrue = false;
	public boolean AutorisationModoCartesRally = false;
	public boolean AutorisationAnimateur = false;
	public boolean AutorisationAnimateurSuperviseur = false;
	public boolean AutorisationHelper = false;
	public boolean SignalementModo = false;
	public boolean SignalementModoForum = false;
	public boolean AutorisationGrade9 = false;
	public boolean AutorisationGrade10 = false;
	public boolean AutorisationGrade11 = false;
	public boolean PureAnimateur = false;
	public boolean AutorisationGestionElo = false;
	public boolean AutorisationArbitreElo = false; // Autorisation temporaire d'arbitre tournoi pdt match classé
	public boolean AutorisationFilmeurElo = false;
	
	public boolean AutorisationDemiAdmin = false;

    // Stats	
	public long Stats_Inscription;
	public long Stats_TempsDeJeu;
	public long Stats_DernièreConnexion;
	public long CodeMapRally;
	
	public serveur.stats.Stats Stats = new serveur.stats.Stats();

	public boolean Mara_Activé;
	public boolean Mara_Désactivé = false;
	public long Mara_TempsBan; //tb
	public long Mara_TempsJeu; //tj
	public long Mara_Guide_JoueurSauvés; //js
	public long Mara_Guide_JoueurGuidées; //jg
	public long Mara_Guide_PartiesGuidées; //pg
	public long Mara_Run_ArrivéesPremier; //p
	public long Mara_Run_PartiesJouées; //pj
	public long Mara_Fight_Tués; //tf
	public long Mara_Fight_Survivor; //sf
	public long Mara_Fight_PartiesJouées; //pjf
	public long Mara_Défilantes_Tués; //td
	public long Mara_Défilantes_Survivor; //sd
	public long Mara_Défilantes_PartiesJouées; //pjd
	public long Mara_Rally_TempsTotal; //tt
	public long Mara_Rally_PartiesJouées; //pjn
	public long Mara_Rally_NombreRecord; //nr
	

	public long Mara_Boum_PartiesJouées; //pj
	public long Mara_Boum_Tués; //t
	public long Mara_Boum_PartiesGagnées; //pg
	public long Mara_Boum_Adversaires; //a
	public long Mara_Boum_Suicides; //s
	public long Mara_Boum_Manches; //m

	public long TempsZero = System.currentTimeMillis();
	public long TempsZeroRally = System.currentTimeMillis();
	public int SonActif;
	public int SonGuideActif;
	public int AfficherBulles;
	public int AfficherCoDeco;
	public int AfficherCoAmis;
	public int TriForteresse;
	public int NoLag;
	public int NoChat;
	public int NoVote;
	public int OldSprite;
	public int TirContinu;
	public int NoStats;
	public int NoMP;
	public String UneRecompense = "0";
	
	public EloJoueur elo;
	public RecompenseElo recompenseElo;
	public int NbPartiesJouées;

	// Info team
	public boolean Membre = false;
	public int IdTeam;
	public int JeuTeam = 0;
	public int IdTeamSouhaitée;
	public String NomTeam; // à supprimer éventuellement
	public int Grade;
	public boolean AutorisationGestionMembres = false;
	public boolean AutorisationGestionRoles = false;
	public boolean AutorisationScribe = false;
	public ArrayList<Integer> IgnorerFusion = new ArrayList<Integer>();
	public boolean IgnorerTouteFusion = false;
	public boolean PorteBlason = false;

	// Ban
	public boolean Ban = false;
	public boolean DemandeDeco = false;
	public String RaisonDeco = "Deconnexion";
	public boolean EntièrementConnecté = false;

	// Anti-Speed hack
	public long DernierControle = System.currentTimeMillis();
	public int ControleEnCours = 0;
	private boolean AntiSHEnCours = false;
	public final Hashtable<Integer, Long> ListeControle = new Hashtable<Integer, Long>();
	
	
	public JoueurEv joueurEv = new JoueurEv(this);
	
	// Donjon
	public JoueurDonjon JDonjon = new yshCastle.JoueurDonjon(this, yshCastle.Donjon.YSH_CASTLE);
	
	public LimiteurRequeteBoite limiteurBoite = new LimiteurRequeteBoite(this);
	public String ImageAutourPseudoCP = "";

	public Joueur(Serveur SERVEUR, Nio NIO, SocketChannel SOCKET, String ADRESSE_IP) {
		AdresseIP = ADRESSE_IP;
		serveur = SERVEUR;
		ServeurNio = NIO;
		Socket = SOCKET;

		_Bouboum = new JoueurBouboum(this);
		_Aaaah = new JoueurAaaah();
		_Forteresse = new JoueurForteresse(this);
		//
		recompenseElo = new RecompenseElo();
	}
	
	///
	public Joueur(String NOMJOUEUR) {
		NomJoueur = NOMJOUEUR;
		Avatar = Serveur.AVATAR_DEFAUT;
	}
	///

	public void Message_Info(String CHAINE) {
		Envoie("CxINFO#"+CHAINE);
	}
	public void Envoie_Info(String CHAINE) {
		Envoie("CxINFO#"+CHAINE);
	}
	
	public void Gardien(String CHAINE) {
		try {
			Ronflement = false;
			// Controle anti speed hack
			if (!AntiSHEnCours) {
				long Temps = System.currentTimeMillis();
				if (Temps - DernierControle > 15000) {
					ControleEnCours++;
					DernierControle = Temps;
					Envoie("CxHS#" + ControleEnCours);
					AntiSHEnCours = true;
				}
			}
			//System.out.println(NomJoueur + " --> " + CHAINE);
			if (ADecoProchainMsg) {
				Deconnexion(AdresseIP + " [Déco mauvaise version] :: " + CHAINE); /// TMP
				return;
			}
			if (!VersionOk) {
				if (CHAINE.equals(serveur.Version+serveur.VersionMineure)) {
					VersionOk = true;
					//System.out.println("Echange version");
					Envoie("CxV#" + serveur.ListeJoueur.size() + "#" + Calendar.getInstance().getTimeInMillis() + "#" + serveur.CodeMalDeTête + "#" + CodeMalDeTêteEnCours);
					if (serveur.ListeBanniIP.contains(AdresseIP)) {
						SetBan("Joueur banni");
					}
					
				} else {
					if (CHAINE.equals("<policy-file-request/>")) {
						//System.out.println("cross-domain");
						Envoie("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"" + (Local.LOCAL ? "3333" : (Local.BETA ? "4444" : "5555")) + "\" /></cross-domain-policy>");
					} else {
						Envoie("CxV");
						ADecoProchainMsg = true;
						System.out.println("Mauvaise version " + serveur.Version + serveur.VersionMineure + " vs " + CHAINE); /// TMP
						// Deconnexion("Mauvaise version " + serveur.Version + serveur.VersionMineure + " vs " + CHAINE); /// TMP
					}
				}
				return;
			}
			Analyse_Message(CHAINE);
		} catch (Exception e) {
			try {
				System.err.print("3XC Gardien "); e.printStackTrace(); // fait planter le serveur
				//System.out.println(e.toString()); 
			} catch (Exception z) {
				System.err.println("3EXCCC Gardien " + e.toString());
			}
			Deconnexion("Erreur");
		}
	}

	protected boolean Vérification_Mal_De_Tête(String CHAINE) {
		try {
			int Code = serveur.Aspirine[CHAINE.charAt(0)] * 1000 + serveur.Aspirine[CHAINE.charAt(1)] * 100 + serveur.Aspirine[CHAINE.charAt(2)] * 10 + serveur.Aspirine[CHAINE.charAt(3)];
			if (Code == (CodeMalDeTêteEnCours % 9000) + 1000) {
				CodeMalDeTêteEnCours++;
				return false;
			}
		} catch (Exception e) {
		}
		return true;
	}

	private void Analyse_Message(String CHAINE) {
		// System.out.println(NomJoueur + " --> " + CHAINE);
		
		//CHAINE = serveur.Decrypt(CHAINE);
		
		if (Vérification_Mal_De_Tête(CHAINE)) {
			CHAINE = CHAINE.replace("#", "&");
			serveur.Avertissement_Modo(NomJoueur + " a tenté d'envoyer un message non prévu (" + CHAINE.substring(4) + ")", false);
			Deconnexion("Triche, modification de paquet");
			return;
		}
		
		if (CHAINE.length() < 4 + Jeu.TAILLE_ID_MAP_EN_COURS) {
			System.out.println("C0M " + NomJoueur + " message trop court");
			Deconnexion("");
			return;
		}

        String Chaine = CHAINE.substring(4 + Jeu.TAILLE_ID_MAP_EN_COURS);
        
        boolean MessagePerimé = (PartieEnCours != null && !PartieEnCours.getJeu().IdMapEnCoursValide(Integer.parseInt(CHAINE.substring(4, 4 + Jeu.TAILLE_ID_MAP_EN_COURS))));
        
		if (serveur.Debug) {
			long Temps = System.currentTimeMillis();
			Reception(Chaine, MessagePerimé);
			long Diff = System.currentTimeMillis()-Temps;
			if (Diff > 20) {
				Joueur Admin = serveur.Joueur("Pyros");
				if (Admin!=null) {
					String Message = Chaine.replace("#","&");
					Admin.Envoie("CxINFO#Lag: " + Diff + " : " + NomJoueur+ " : " + Message);
				}
				System.out.println("GARB4GE " + Diff + " " + NomJoueur + " ### " + Chaine);
			}
		} else {
			Reception(Chaine, MessagePerimé);
		}
	}

    public void Reception(String Chaine, boolean MessagePerimé){
    	if (PartieEnCours != null) {
    		PartieEnCours.getJeu().Reception(this, Chaine, MessagePerimé);
    	} else {
    		Reception_Spéciale(Chaine);
    	}
		
	}

	public boolean Verif_IP_Dyna(String Nom, String Prefixe) {
		// ban IP dynamique..
		if (serveur.Refus(this)) {
			if (!serveur.ListeAutorisation.contains(Nom.toLowerCase())||Prefixe.equals("*")) { // Comptes autorisés parmi les IP refusées
				System.out.println(Prefixe + Nom + " Refusé " + AdresseIP + " " + AdresseHost);
				serveur.Avertissement_Modo("HOST - " + Prefixe + ""+ Nom + " est refusé." , false);
				SetBan("Refusé");
				//Deconnexion("Refusé");
				return true;
			}
		}
		return false;
	}

	public void SetBan(String Raison) {
		Envoie("CxBANC");				
		RaisonDeco = Raison;
		Serveur.Action TimerBan = serveur.new Action(System.currentTimeMillis() + 500);
		TimerBan.RefusConnexion = true;
		TimerBan.JoueurCible = this;
	}

	private void Rejoindre(Partie Partie) {
		if (Partie.AttenteElo) {
			PartieEnAttente = Partie;
			PartieEnAttente.AttenteEloPartie.Rejoindre_Partie(this);
		} else if (Partie.getJeu().BesoinPartieAttente()) {
			Partie.getJeu().AjouterPartieAttente(this);
		} else {
			Partie.Nouveau_Joueur(this, null);
		}
	}

	public void Reception_Spéciale(String CHAINE) {
		String[] Message = CHAINE.split("#");
		String Code = Message[0];
		/************************************
		 * SPEED GEAR ?
		 ************************************/
		if (VersionOk) {
			// Nouveau anti speed hack
			if (Code.equals("CxHS")) {
				//if (ControleEnCours == Integer.parseInt(Message[1])) {
				if (AntiSHEnCours) {
					int TempsEcoulé = (int) (System.currentTimeMillis() - DernierControle);
					if (TempsEcoulé < 9000) {
						serveur.Avertissement_Modo("Speedhack2 pour " + NomJoueur + " (" + AdresseIP + ") (" + TempsEcoulé + ") sur : " + Salon(), false);
						System.out.println(NomJoueur + " " + AdresseIP + " ### Speedhack2");
						if (serveur.DecoTricheAuto) {
							DemandeDeco = true;
							RaisonDeco = "Triche Speedhack2";
						}
					}
					AntiSHEnCours = false;
				}
				//}
				return;
			}

			if (Code.equals("CxS")) {
				// antispeedhack (Ancienne version)               
				long now = System.currentTimeMillis();
				if (now - DernierMessageVide < 9300) {
					Speedhack++;
					if (Speedhack == 4 && Detecte == 0) {
						serveur.Avertissement_Modo("Speedhack pour " + NomJoueur + " (" + AdresseIP + ") sur : " + Salon(), false);
						System.out.println(NomJoueur + " " + AdresseIP + " ### Speedhack");
						Detecte = 1;
					} else
						if (Speedhack == 7 && Detecte == 1) {
							if (PartieEnCours != null) {
								serveur.Envoie(PartieEnCours.getLsJoueurPartie(), "CxSH#" + NomJoueur);
							}
							Detecte = 2;
						} else
							if (Speedhack == 8 && Detecte == 2) {
								if (serveur.DecoTricheAuto) {
									boolean Silencieux = (PartieEnCours == null);
									serveur.Ban_Joueur(this, NomJoueur, 360000, "0", "[Bot] Activité suspecte", Silencieux, true);
								}
							}
				} else {
					Speedhack = 0;
				}
				DernierMessageVide = now;
				return;
			}
		}
		/************************************/

		if (Identifié) {
			// DEMANDE FORUM
			if (Code.equals("FoF")) {
				// Sélection du forum
				if (Message.length == 3) {
					int Cible = Integer.parseInt(Message[1]);
					if (Cible == Serveur.FORUM_MODO) { // forum modo
						if (AutorisationModo) {
							ForumEnCours = Serveur.FORUM_MODO;
						} else {
							Deconnexion("Client modifié");
							return;
						}
					} else if (Cible == Serveur.FORUM_VIP) { // forum VIP
						if (AutorisationModo || AutorisationArbitre || AutorisationModoForum || AutorisationModoTeam || AutorisationDev) {
							ForumEnCours = Serveur.FORUM_VIP;
						} else {
							Deconnexion("Client modifié");
							return;
						}
					} else if (Cible == Serveur.FORUM_ANIM) { // forum anim
						if (AutorisationModo || AutorisationAnimateur) {
							ForumEnCours = Serveur.FORUM_ANIM;
						} else {
							Deconnexion("Client modifié");
							return;
						}
					} else {
						ForumEnCours = Cible;
					}
					PageForumEnCours = Integer.parseInt(Message[2]);
				}
				serveur.BOITE.Requete(Boite.DEMANDE_LISTE_SUJET, this);
				return;
			}
			// Recherche
			if (Code.equals("FoSea")) {
				if (Message.length == 3) {
					if (Message[1].equals("0")) {
						serveur.BOITE.Requete(Boite.RECHERCHE_SUJET, this, Message[2]);
					} else {
						serveur.BOITE.Requete(Boite.RECHERCHE_SUJET_AUTEUR, this, Message[2]);
					}
				}

				return;
			}
			// Envoie des messages d'un sujet
			if (Code.equals("FoS")) {
				int Page = (Message.length != 3 ? 0 : Integer.parseInt(Message[2]));
				serveur.BOITE.Requete(Boite.DEMANDE_SUJET, this, Integer.parseInt(Message[1]), Page);
				return;
			}
			// Changement page
			if (Code.equals("FoP")) {
				serveur.BOITE.Requete(Boite.DEMANDE_SUJET, this, SujetEnCours, Integer.parseInt(Message[1]));
				return;
			}
			// Nouvelle réponse
			if (Code.equals("FoR")) {
				if (AutorisationForum) {
					serveur.BOITE.Requete(Boite.REPONSE_SUJET, this, Message[1], Message[2].equals("1"));
				} else {
					Envoie("CxFoMM");
				}
				return;
			}
			// Edition d'un message
			if (Code.equals("FoE")) {
				if (AutorisationForum) {
					if (Message.length == 2) {
						if (AutorisationModoForum || AutorisationModo || AutorisationDev) {
							serveur.BOITE.Requete(Boite.SUPPRESSION_MESSAGE, this, Integer.valueOf(Message[1]),true);
						}
					} else {
						serveur.BOITE.Requete(Boite.EDITION_MESSAGE, this, Integer.valueOf(Message[1]), Message[2]);
					}
				}
				return;
			}
			// Suppression d'un message en arriere plan
			if (Code.equals("FoEA")) {
				if (AutorisationForum) {
					if (Message.length == 2) {
						if (AutorisationModoForum || AutorisationModo || AutorisationDev) {
							serveur.BOITE.Requete(Boite.SUPPRESSION_MESSAGE, this, Integer.valueOf(Message[1]),false);
						}
					} 
				}
				return;
			}
			// Suppression discussion
			if (Code.equals("FoD")) {
				if (AutorisationModoForum) {
					if (Message.length == 2) {
						serveur.BOITE.Requete(Boite.SUPPRESSION_DISCUSSION, this, Integer.valueOf(Message[1]));
					}
				}
				return;
			}

			// Postit
			if (Code.equals("FoPI")) {
				if (AutorisationModoForum || AutorisationDev) {
					if (Message.length == 2) {
						serveur.BOITE.Requete(Boite.POSTIT_TOPIC, this, Message[1]);
					}
				}
				return;
			}

			// Topic Anim
			if (Code.equals("FoTA")) {
				if (AutorisationModoForum) {
					if (Message.length == 2) {
						serveur.BOITE.Requete(Boite.TOPIC_ANIM, this, Integer.valueOf(Message[1]));
					}
				}
				return;
			}
			
			// Edition titre
			if (Code.equals("FoT")) {
				if (AutorisationModoForum || AutorisationDev) {
					if (Message.length == 3) {
						serveur.BOITE.Requete(Boite.EDITION_TITRE, this, Integer.valueOf(Message[1]), ForumEnCours, Message[2]);
					}
				}
				return;
			}
			// Déplacer sujet
			if (Code.equals("FoM")) {
				if (AutorisationModoForum) {
					if (Message.length == 3) {
						serveur.BOITE.Requete(Boite.DEPLACEMENT_SUJET, this, Integer.valueOf(Message[1]), Integer.valueOf(Message[2]));
					}
				}
				return;
			}

			// Nouveau sujet
			if (Code.equals("FoN")) {
				if (AutorisationForum) {
					String Titre = Message[1];
					
					if (ForumEnCours == Serveur.FORUM_TEAM) {
						if (!AutorisationScribe && !AutorisationGestionRoles) {
							return;
						} else if (!Titre.startsWith("[" + IdTeam +"]")) {
							Titre = "[" + IdTeam +"] " + Titre;
						}
					}
					serveur.BOITE.Requete(Boite.NOUVEAU_SUJET, this, Titre, Message[2]);
				}
				return;
			}
			// Fermeture Sujet
			if (Code.equals("FoX")) {
				if (AutorisationModoForum) {
					int Id = Message.length > 2 ? Integer.valueOf(Message[2]) : SujetEnCours;
					serveur.BOITE.Requete(Boite.FERMETURE_SUJET, this, Id, Message[1]);
				}
				return;
			}
			//  Muet forum
			if (Code.equals("FoMM")) {
				if (AutorisationModoForum && !AutorisationHelper) {
					//serveur.Mute_Joueur(this, Message[1], 86400000);
					if (Message.length >= 3) {
						String IdMessage = Message[1];
						int Temps = Integer.valueOf(Message[2]);
						String Raison = "";
						if (Message.length > 3) {
							Raison = Message[3];
						}
						serveur.BOITE.Requete(Boite.MUTE_FORUM, this, IdMessage, Raison, Temps);
					}
				}
				return;
			}
			// Message
			if (Code.equals("CxM")) {
				if (AutorisationInscription) {
					if (System.currentTimeMillis() - LimiteChat < 400) {
						//JOUEUR.Deconnexion("Flood.");
						return;
					}
					LimiteChat = System.currentTimeMillis();
					if (Muet) {
						Envoie("CxMuet");
					} else {
						if (Message.length == 2) {
							// On ne peut pas parler sur le chat principale avec moins de 20 heures de jeu
							if (Stats_TempsDeJeu < RestrictionHeure.CHAT_PRINCIPAL && !AutorisationAdmin && !AutorisationModo) {
								Envoie("CxF#");
								return;
							}
							if (MuteChatPrincipal) {
								Envoie("CxMuet#");
								return;
							}
							if (serveur.ChatFerme) {
								Envoie("CxF");
							} else {
								CHAINE = serveur.FixeDoubleEsperlouette(CHAINE, this);

								if (CHAINE.indexOf("<") != -1 && !AutorisationModo) {
									return;
								}
								if (ToutEnMaj) {
									CHAINE = "CxM#" + Message[1].toUpperCase();
								} else {
									if (ToutEnMin) {
										CHAINE = "CxM#" + Message[1].toLowerCase();
									}
								}
								String Pseudo;
								if (CouleurPseudo) {
									Pseudo = ImageAutourPseudoCP + "<font color='" + CouleurPseudoValeur + "'>" + NomJoueurModifié + "</font>" + ImageAutourPseudoCP;
								} else {
									Pseudo = ImageAutourPseudoCP + NomJoueurModifié + ImageAutourPseudoCP;
								}
								String MessageChat;
								if (CouleurMessage) {
									MessageChat = "CxM#<font color='" + CouleurMessageValeur + "'>" + Message[1] + "</font>";
								} else {
									MessageChat = CHAINE;
								}
								
								CHAINE = serveur.FixeDoubleEsperlouette(CHAINE, this);
								
								serveur.Envoie_Chat(this, serveur.ListeJoueurSansPartie, MessageChat + "#" + Pseudo);
								serveur.Envoie_Chat(this, serveur.ListeJoueurEnAttenteElo, MessageChat + "#" + Pseudo);
							}
						} else {
							if (Message.length >= 3) {
								// Chuchotement                            
								Joueur JoueurCible = Message.length == 3 ? serveur.Joueur(Message[1]) : serveur.JoueurStartWith(Message[1]);
								if (JoueurCible == null) {
									// Joueur introuvable
									Envoie(CHAINE + "#####");
								} else
									if (JoueurCible != this) {
										// Joueur trouvé
										if (!JoueurCible.AccepteChuchotement) {
											Envoie("CxRef#0"); // Il refuse les chuchotements                                                                            
										} else if (!JoueurCible.ListeJoueursIgnorés.contains(NomJoueur)) {
											Message[2] = serveur.FixeDoubleEsperlouette(Message[2], this);
											
											Envoie("CxM#" + JoueurCible.NomJoueur + "#" + Message[2] + "#" + NomJoueur);
											ArrayList<Joueur> ListeJoueurCible = new ArrayList<Joueur>();
											ListeJoueurCible.add(JoueurCible);
											serveur.Envoie_Chat(this, ListeJoueurCible, "CxM#" + JoueurCible.NomJoueur + "#" + Message[2] + "#" + NomJoueur);
										} else {
											Envoie("CxIGN#" + JoueurCible.NomJoueur);
										}
									}
							} else {
								Deconnexion("Vieux client.");
							}
						}
					}
				}
				return;
			}
			// Editeur Aaaah!
			if (Code.equals("EdI")) {
				if (AutorisationInscription) {
					//JOUEUR.PartieEnCours.Quitter_partie(JOUEUR);
					_Aaaah.CodeCarte = 0;
					serveur.Rejoindre_Partie(this, 2, "Editeur Aaaah", "#", true);
					Envoie("EdI#" + (RestrictionHeure.EDITER_AAAAH > 540000000?"1":"0")); // 150 heures
				}
				return;
			}
			// Liste Joueur d'une partie
			if (Code.equals("CxG")) {
				if (PartieEnCours == null) {
					int CodePartie = Integer.parseInt(Message[1]);
					int Nb = serveur.ListePartie.size();
					for (int i = 0; i < Nb; i++) {
						Partie Partie = serveur.ListePartie.get(i);
						if (Partie.CodePartie == CodePartie) {
							StringBuilder LsJoueur = new StringBuilder();
							int NbJoueur = Partie.getLsJoueurPartie().size();
							ArrayList<Joueur> ListeJoueurPartie = Partie.getLsJoueurPartie();
							for (int j = 0; j < NbJoueur; j++) {
								Joueur Joueur = ListeJoueurPartie.get(j);
								if (serveur.Evénement.Saturnaaaahles()) {
									LsJoueur.append("," + Joueur.NomJoueur + ";" + (Joueur.ParticipeSurvivalKick ? "1" : "0"));
								} else {
									LsJoueur.append("," + Joueur.NomJoueur);
								}
							}
							
							if (NbJoueur != 0) {
								// NbJoueur == 0 <=> partie classée avec équipe en cours de préparation
								if (Partie.AttenteElo) {
									Envoie("CxG#0#0#" + LsJoueur.substring(1));
								} else {
									Envoie("CxG#" + Partie.getJeu().getTempsRestantAvantFin() + "#" + Partie.getJeu().getDureePartie()/1000 + "#" + LsJoueur.substring(1));
								}
							}
							return;
						}
					}
				}
				return;
			}
			// Rejoindre partie / multis détectés / 1er challenge
			if (Code.equals("CxMULTI")) {
				Joueur CeluiDejaSalon = this;
				if (Message.length > 2) {
					Joueur CeluiQuiRejoint = serveur.Joueur(Message[1]); // joueur rejoignant le salon
					if (CeluiQuiRejoint != null && CeluiQuiRejoint.AdresseIP.equals(CeluiDejaSalon.AdresseIP)) {
						String Cookie = Message[2];
						if (!Cookie.equals("0")) { // résultat du 1er challenge pour celui dans la partie
						 	if (CeluiQuiRejoint.PartieEnCours == null) {
						 		CeluiQuiRejoint.Envoie("CxMULTI#"+CeluiDejaSalon.NomJoueur+"#"+Cookie);
						 	}							
						} else {
							CeluiQuiRejoint.Envoie("CxINFO#Impossible de connecter plusieurs comptes sur ce salon avec la même IP. Créez un salon privé pour contourner cette limitation ou accepter automatiquement les cookies dans les options de Flash Player sur tous vos comptes.");
						}
					}
				}
				return;
			}
			
		// 2eme challenge multi
		if (Code.equals("CxMULTIJOIN")) {
			Joueur CeluiQuiRejoint = this;
			if (Message.length > 2 && PartieEnCours == null) {
				Joueur CeluiDejaSalon = serveur.Joueur(Message[1]); // joueur déjà dans le salon
				if (CeluiDejaSalon != null && CeluiQuiRejoint.AdresseIP.equals(CeluiDejaSalon.AdresseIP)) {
					String Cookie = Message[2];
					if (Cookie.equals("0")) { // résultat du 2eme challenge pour qui rejoint la partie
					 	if (CeluiDejaSalon.PartieEnCours != null && !CeluiDejaSalon.PartieEnCours.hasMotDePasse()) {
					 		// on rejoint la partie du joueur
					 		Rejoindre(CeluiDejaSalon.PartieEnCours);
					 	}
					} else {
						CeluiQuiRejoint.Envoie("CxINFO#Impossible de connecter plusieurs comptes sur ce salon avec la même IP. Créez un salon privé pour contourner cette limitation.");
					}
				}
			}
			return;
		}
	
			// Rejoindre partie
			if (Code.equals("CxR")) {
				if (PartieEnCours == null && Message.length > 1) {
					int CodePartie = Integer.parseInt(Message[1]);
					int Nb = serveur.ListePartie.size();
					for (int i = 0; i < Nb; i++) {
						Partie Partie = serveur.ListePartie.get(i);
						if (Partie.CodePartie == CodePartie) {
							if (Partie.getJeu().PeutEntrer(this)) {
								if (Partie.hasMotDePasse()) {
									if (Message.length>2 && (Message[2].equals(Partie.MotDePasse) || (AutorisationModo && Message[2].equals("nopass")))) {
										Rejoindre(Partie);
									} else {
										Envoie("CxR");
									}
								} else {
									Rejoindre(Partie);
								}
								return;
							}
						}
					}
				}
				Envoie("CxR");
				return;
			}
			// Choix équipe
			if (Code.equals("CxTe")) {
				if (Message.length == 2 && PartieEnAttente != null && PartieEnAttente.Forto && PartieEnAttente.getJeu() != null && PartieEnAttente.getJeu().BesoinPartieAttente() && PartieEnCours == null) {
					Envoie("CxJ#" + PartieEnAttente.CodeJeu);
					PartieEnAttente.Nouveau_Joueur(this, Message[1].equals("1") ? Boolean.TRUE : Boolean.FALSE);
				}
				return;
			}

			// Rejoindre partie rapide
			if (Code.equals("CxJ")) {
				String CodeJeu = Message[1];
				if (CodeJeu.endsWith("0")) {
					//quitte la partie
					serveur.Rejoindre_Liste_Partie(this);
				} else {
					if (PartieEnCours == null) {
						if (CodeJeu.endsWith("1")) {
							serveur.Rejoindre_Partie(this, 1, null, null, false);
						} else {
							if (CodeJeu.endsWith("2")) {
								serveur.Rejoindre_Partie(this, 2, null, null, false);
							} else {
								if (CodeJeu.endsWith("3")) {
									serveur.Rejoindre_Partie(this, 3, null, null, false);
								}
							}
						}
					}
				}
				return;
			}
			// Création partie
			if (Code.equals("CxP")) {
				if (AutorisationInscription && PartieEnCours == null) {
					if (Message.length >= 12) {
						// Temps par partie
						long TempsPartie = Long.valueOf(Message[3]) * 60000;
						if (TempsPartie < 10000) {
							TempsPartie = 10000;
						} else
							if (TempsPartie > 59940000) {
								TempsPartie = 59940000;
								// Score maximum sur Boum
							}
						int ScoreMaxBoum = Integer.valueOf(Message[6]);
						if (ScoreMaxBoum < 0) {
							ScoreMaxBoum = 0;
						} else
							if (ScoreMaxBoum > 100000 || (_Bouboum != null && _Bouboum.Stats_PartieGagnée > ScoreMaxBoum)) {
								ScoreMaxBoum = 100000;
								// Choix équipe
							}
						int NbMaxJoueurs = Integer.valueOf(Message[8]);
						if (NbMaxJoueurs < 2) {
							NbMaxJoueurs = 2;
						} else
							if (NbMaxJoueurs > 22) {
								NbMaxJoueurs = 22;
							}
						boolean AutoriserChoixEquipe = Message[4].equals("1");
						boolean Dessin = Message[5].equals("1");
						// Mot de passe
						String MotDePasse = null;
						if (Message.length == 17) {
							MotDePasse = Message[16];
						}
						boolean SansConta = Message[7].equals("0");
						boolean SalonFight = Message[9].equals("0");
						int ModeAaaah = Integer.valueOf(Message[10]);
						boolean SalonKill = Message[11].equals("0");
						int ModeForto = Integer.valueOf(Message[12]);
						String PseudoArbitre = (Message[13].equals("-") ? "" : Message[13]);
						String Guide = Message[14];
						boolean MatchTeam = (Message[15].equals("1"));

						serveur.Rejoindre_Partie(this, Integer.parseInt(Message[1]), Message[2], MotDePasse, false
												, TempsPartie, AutoriserChoixEquipe, Dessin, ScoreMaxBoum, SansConta
												, NbMaxJoueurs, SalonFight, ModeAaaah, SalonKill, ModeForto, PseudoArbitre
												, Guide, MatchTeam);
					}
				}
				return;
			}
			// Rejoindre une partie classée dans l'équipe rouge
			if (Code.equals("CxELOR")) {
				if (PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
					return;
				}
				PartieEnAttente.AttenteEloPartie.Ajouter_Joueur_Attente_Elo(true, this);
				return;
			}
			// Rejoindre une partie classée dans l'équipe bleue
			if (Code.equals("CxELOB")) {
				if (PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
					return;
				}
				PartieEnAttente.AttenteEloPartie.Ajouter_Joueur_Attente_Elo(false, this);
				return;
			}
			// Quitter son équipe
			if (Code.equals("CxELONOEQ")) {
				if (PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
					return;
				}
				PartieEnAttente.AttenteEloPartie.Joueur_Quitte_Equipe(this);
				return;
			}
			// Quitter l'attente d'une partie classée
			if (Code.equals("CxELOQ")) {
				if (PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
					return;
				}
				PartieEnAttente.AttenteEloPartie.Joueur_Quitte_Attente_Elo(this, true);
				return;
			}
			// Demander le lancement de la partie classée
			if (Code.equals("CxELOS")) {
				if (PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
					return;
				}
				PartieEnAttente.AttenteEloPartie.Start_Validation_Match(this, Integer.parseInt(Message[1]));
				return;
			}
			// Accepter la proposition de match
			if (Code.equals("CxELOOK")) {
				if (PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
					return;
				}
				PartieEnAttente.AttenteEloPartie.Accepter_Validation(this);
				return;
			}
			// Refuser la proposition de match
			if (Code.equals("CxELOREF")) {
				if (PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
					return;
				}
				PartieEnAttente.AttenteEloPartie.Refuser_Validation(this);
				return;
			}
			
			// Classements
			if (Code.equals("CxCl")) {
				int JeuClassement = Integer.parseInt(Message[1]);
				int ModeClassement = Integer.parseInt(Message[2]);
				/*
				-1 : Aucun
				 0 : Nouveau
				 1 : Ancien
				 2 : ELO
				 3 : Mensuel
				*/
				if (ModeClassement == -1 || JeuClassement == -1) { // Aucun
					return;
				} else if (ModeClassement == 3) { // Mensuel
					Envoie(serveur.Cl_Stats);
				}
				
				switch (JeuClassement) {
					case 0: // Aaaah
						if (ModeClassement == 0) { // Nouveau
							Envoie(serveur.Cl_NouvAaaah);
						} else if (ModeClassement == 1) { // Ancien
							Envoie(serveur.Cl_AaaahCourse);
						} else if (ModeClassement == 2) { // ELO
							serveur.BOITE.Requete(Boite.CLASSEMENT_ELO, this, Integer.valueOf(Message[3]), Integer.valueOf(Message[4]));
							// ... CxCl#4#
						}
						break;
					case 1: // Aaaah
						if (ModeClassement == 0) { // Nouveau
							Envoie(serveur.Cl_NouvBoum);
						} else if (ModeClassement == 1) { // Ancien
							Envoie(serveur.Cl_Bouboum);
						} else if (ModeClassement == 2) { // ELO
							serveur.BOITE.Requete(Boite.CLASSEMENT_ELO, this, Integer.valueOf(Message[3]), Integer.valueOf(Message[4]));
						}
						break;
					case 2: // Forteresse
						if (ModeClassement == 2) { // ELO
							serveur.BOITE.Requete(Boite.CLASSEMENT_ELO, this, Integer.valueOf(Message[3]), Integer.valueOf(Message[4]));
						}
						break;
				}
				/*
				if (CodeClassement.equals("1")) {
					Envoie(serveur.Cl_Bouboum);
				} else if (CodeClassement.equals("2")) {
					Envoie(serveur.Cl_AaaahCourse);					
				} else if (CodeClassement.equals("3")) {
					Envoie(serveur.Cl_Stats);
				} else if (CodeClassement.equals("4")) {
					Envoie(serveur.Cl_NouvBoum);
				} else if (CodeClassement.equals("5")) {
					Envoie(serveur.Cl_NouvAaaah);					
				}*/
				return;
			}

			// Temps BDD
			if (Code.equals("CxBDD")) {
				if (AutorisationAdmin || AutorisationDev) {
					if (serveur.AbonnementTempsBDD.contains(this)) {
						serveur.AbonnementTempsBDD.remove(this);
						if (serveur.AbonnementTempsBDD.isEmpty()) {
							serveur.TempsBDDActive = false;
						}
					} else {
						serveur.AbonnementTempsBDD.add(this);
						serveur.TempsBDDActive = true;
					}
				}
				return;
			}
			// Changement Sexe Forteresse
			if (Code.equals("CxSF")) {
				_Forteresse.Sexe = Integer.parseInt(Message[1]);
				if (_Forteresse.Sexe > 4 && !(AutorisationArbitre || AutorisationModo))  {
					_Forteresse.Sexe = 3;
				}
				return;
			}
			// Option son
			if (Code.equals("CxOS")) {
				SonActif = Integer.parseInt(Message[1]);
				return;
			}
			// Option son Guide
			if (Code.equals("CxOG")) {
				SonGuideActif = Integer.parseInt(Message[1]);
				return;
			}
			// Option AfficherCoDeco
			if (Code.equals("CxOC")) {
				AfficherCoDeco = Integer.parseInt(Message[1]);
				return;
			}
			// Option AfficherCoAmis
			if (Code.equals("CxOA")) {
				AfficherCoAmis = Integer.parseInt(Message[1]);
				return;
			}
			// Option AfficherBulles
			if (Code.equals("CxOM")) {
				AfficherBulles = Integer.parseInt(Message[1]);
				return;
			}
			// Option TriFrag
			if (Code.equals("CxOF")) {
				TriForteresse = Integer.parseInt(Message[1]);
				return;
			}
			// Option NoLag
			if (Code.equals("CxOL")) {
				NoLag = Integer.parseInt(Message[1]);
				return;
			}
			// Option NoChat
			if (Code.equals("CxONC")) {
				NoChat = Integer.parseInt(Message[1]);
				return;
			}
			// Option NoStats
			if (Code.equals("CxONS")) {
				NoStats = Integer.parseInt(Message[1]);
				return;
			}
			// Option NoMP
			if (Code.equals("CxONMP")) {
				NoMP = Integer.parseInt(Message[1]);
				return;
			}
			// Option NoVote
			if (Code.equals("CxOV")) {
				NoVote = Integer.parseInt(Message[1]);
				return;
			}
			// Option OldSprite
			if (Code.equals("CxOO")) {
				OldSprite = Integer.parseInt(Message[1]);
				return;
			}
			// Option TirContinu
			if (Code.equals("CxOT")) {
				TirContinu = Integer.parseInt(Message[1]);
				return;
			}
			// Modifier les récompenses elo
			if (Code.equals("CxORE")) {
				switch(Integer.parseInt(Message[1])) {
				   case 0:
					   recompenseElo.recompensesActives = RecompenseElo.ACTIVER;
					   break;
				   case 1:
					   recompenseElo.recompensesActives = RecompenseElo.DESACTIVER_PERSO;
					   break;
				   case 2:
					   recompenseElo.recompensesActives = RecompenseElo.DESACTIVER_ALL;
					   break;
				}
			}
			// Commandes
			if (Code.equals("CxC")) {
				Commande(Message[1]);
				return;
			}
			// Import de compte
			if (Code.equals("CxIMPCMPT")) {
				String imp = Message[1].trim();
				serveur.getExportImportCompte().importerJoueur(imp, this);
				return;
			}
			// Ping
			if (Code.equals("CxPing")) {
				Envoie(CHAINE);
				return;
			}
			// Récolte de ping
			if (Code.equals("CxPong")) {
				if (Message.length >= 3) {
					if (Message[1].equals("0")) {
						// demande de ping d'un autre joueur (réception message 1)
						if (AutorisationModo || AutorisationArbitre || AutorisationTournoiForto || AutorisationTournoiAaaah || AutorisationArbitreElo) {
							String Cible = Message[2];
							Joueur JoueurCible = serveur.Joueur(Cible);
							if (JoueurCible != null) {
								if (AutorisationTournoiForto && (JoueurCible.PartieEnCours==null || PartieEnCours != JoueurCible.PartieEnCours || !JoueurCible.PartieEnCours.Forto || !JoueurCible.PartieEnCours.hasMotDePasse())) {
									return;
								}
								if (AutorisationTournoiAaaah && (JoueurCible.PartieEnCours==null || PartieEnCours != JoueurCible.PartieEnCours  || !JoueurCible.PartieEnCours.hasMotDePasse())) {
									return;
								}
								
								if (AutorisationArbitreElo) {
									if (PartieEnCours == null || (JoueurCible.PartieEnCours != PartieEnCours && PartieEnCours.EquipeElo1.indexOf(JoueurCible.NomJoueur) == -1 && PartieEnCours.EquipeElo2.indexOf(JoueurCible.NomJoueur) == -1)) {
										return; // Pas de ping sur des joueurs qui ne font pas partie du match elo
									}
								}
								
								if (AutorisationTournoiAaaah || AutorisationTournoiForto) {
									System.out.println("L0G ping " + NomJoueur + " " + JoueurCible.NomJoueur + " " + AdresseIP + " " + Salon());
								}
								long Temps = System.currentTimeMillis();
								serveur.Pingeur = this; // on ne transmet pas le demandeur au pingé
								JoueurCible.Envoie("CxPong#1#" + Temps); // envoi message 2
							} else {
								Envoie("CxINFO#" + Cible + " n'est pas connecté.");
							}
						}
					} else {
						// recéption message 3 (résultats)
						if (serveur.Pingeur != null) {
							long Temps = Long.parseLong(Message[2]);
							long Resultat = System.currentTimeMillis() - Temps;
							serveur.Pingeur.Envoie("CxPong#0#" + NomJoueur + "#" + Resultat); // envoi message 4
							serveur.Pingeur = null;
						}
					}
				}
				return;
			}
			// Vote
			if (Code.equals("CxVote")) {
				if (AutorisationInscription) {
					if (Message.length >= 3) {
						serveur.BOITE.Requete(Boite.VOTE, this, Integer.valueOf(Message[1]), Integer.valueOf(Message[2]));
					}
				}
				return;
			}
			// Sondage
			if (Code.equals("CxPollV")) {
				if (AutorisationInscription) {
					if (Message.length >= 3) {
						serveur.BOITE.Requete(Boite.REPONSE_SONDAGE, this, Integer.valueOf(Message[1]), Integer.valueOf(Message[2]));
					}
				}
				return;
			}
			// Avatar
			if (Code.equals("CxAV")) {
				if (AutorisationInscription) {
                    if (serveur.WorkaroundAvatar) {
                    	Envoie("CxINFO#Pour modifier votre avatar, il faut l'upload sur http://atelier801.com/user-images-home, puis taper sur le chat /avatar [code] où [code] apparaît dans l'URL de votre image avec l'extension (par exemple pour http://img.atelier801.com/57a4f115.jpg, taper /avatar 57a4f115.jpg)");
                    	if (!Avatar.isEmpty()) {
                    		Envoie("CxINFO#Votre avatar actuel est " + Avatar);
                    	}
                    } else {
                        serveur.BOITE.Requete(Boite.CHANGEMENT_AVATAR, this);
                    }
				}
				return;
			}
			// Temps avant mise à jour
			if (Code.equals("CxMAJ")) {
				Envoie("CxMAJ#" + (86400000 - (System.currentTimeMillis() - serveur.DernièreMAJ)) / 60000);
				return;
			}
			
			// Recherche map Forteresse
			if (Code.equals("CxRMAF")) {
				String[] Param = new String[5];
				
				for(int i = 0; i < 4; ++i) {
					if (i < Message.length - 1) {
						Param[i] = Message[i + 1];
					} else {
						Param[i] = "";
					}
				}
				
				serveur.BOITE.Requete(Boite.RECHERCHE_MAP_FORTO, this, Param[0], Param[1], Param[2], Param[3]);
				
				return;
			}
			
			// Recherche de map Aaaah!
			if (Code.equals("CxMAR")) {
				if (Message.length == 6) {
					String IdMap = Message[1]; 
					String Auteur = Message[2]; 
					String Titre = Message[3]; 
					String Recordman = Message[4];
					int Flag = Integer.valueOf(Message[5]);
					if (!IdMap.isEmpty()) {
						// si le code est spécifié, on fait une recherche par code uniquement
						IdMap = IdMap.replaceAll("@", "");
						if (!IdMap.isEmpty()) {
							serveur.BOITE.Requete(Boite.RECHERCHE_MAP_BY_ID, this, IdMap);
						}
					} else if (Titre.isEmpty() && Recordman.isEmpty() && Flag==0 && !Auteur.isEmpty()) {
						// sinon si seul l'auteur est spécifié, on fait une recherche par auteur
						serveur.BOITE.Requete(Boite.LISTE_MAP_JOUEUR, this, Auteur);
					} else {
						// recherche avancée
						serveur.BOITE.Requete(Boite.RECHERCHE_MAP, this, Auteur, Titre, Recordman, Flag);
					}
				}
				return;
			}
			// Playlist de map
			if (Code.equals("CxMAPL")) {
				if (Message.length == 2) {
					String Codes = Message[1];
					if (!Codes.isEmpty()) {
						serveur.BOITE.Requete(Boite.RECHERCHE_MAP_BY_IDS, this, Codes);
					}
				}
				return;
			}
			// Playlist du server
			if (Code.equals("CxPLST")) {
				if (Message.length == 2) {
					String CodePlaylist = Message[1];
					if (!CodePlaylist.isEmpty()) {
						serveur.BOITE.Requete(Boite.PLAYLIST_STANDARD, this, CodePlaylist);
					}
				}
				return;
			}
			// Load direct d'une playlist publiée
			if (Code.equals("CxPLPL")){
				if (Message.length == 2) {
					String CodePlaylist = Message[1];
					if (!CodePlaylist.isEmpty()) {
						serveur.BOITE.Requete(Boite.PLAYLIST_STANDARD_COMMANDE, this, CodePlaylist);
					}
				}
				return;
			}
			//Publier une playlist
			if (Code.equals("CxPUPL")){
				if (Message.length == 4) {
					if(Message[1].equals("valider")){
						String CodePlaylist = Message[2];
						String TitrePlaylist = Message[3];
						serveur.BOITE.Requete(Boite.PLAYLIST_PUBLIER_VALIDER, this, CodePlaylist, TitrePlaylist);
					}
					else{
						String CartesPlaylist = Message[1];
						String CodePlaylist = Message[2];
						String TitrePlaylist = Message[3];
						if (!CartesPlaylist.isEmpty() && !CodePlaylist.isEmpty() && !TitrePlaylist.isEmpty()) {
							serveur.BOITE.Requete(Boite.PLAYLIST_PUBLIER, this, CartesPlaylist, CodePlaylist, TitrePlaylist);
						}
					}
				}
				return;
			}
			//Index de playlist + recherche de playlist
			if (Code.equals("CxPlist")){
				if (Message.length == 1) {
					serveur.BOITE.Requete(Boite.PLAYLIST_LISTE, this);
				}
				if (Message.length == 2) {
					serveur.BOITE.Requete(Boite.PLAYLIST_RECHERCHE, this, Message[1]);
				}
				return;
			}
			//Supprimer une playlist
			if(Code.equals("CxDPL")){
				if (Message.length == 3) {
					String Auteur = Message[1];
					String CodePlaylist =  Message[2];
					if (!Auteur.isEmpty() && !CodePlaylist.isEmpty()) {
						serveur.BOITE.Requete(Boite.PLAYLIST_SUPPRIMER, this, Auteur, CodePlaylist);
					}
				}
				return;
			}
			///skip
			if(Code.equals("CxSkip"))
			{
				ArrayList<Joueur> ListeStaffPartie = new ArrayList<Joueur>();
				
				// Si dans une partie
				if (PartieEnCours != null) {
					Joueur Joueur;
					int Nb = PartieEnCours.getLsJoueurPartie().size();
					ArrayList<Joueur> ListeJoueurPartie = PartieEnCours.getLsJoueurPartie();
					int i;
					
					// Pour tous les joueurs de la partie
					for(i=0;i<Nb;i++) {
						Joueur = ListeJoueurPartie.get(i);
						
						//Si le joueur est dans le staff (Arbitre, Modo, Helper, Admin) On l'ajoute à la liste
						if (Joueur.AutorisationArbitre || Joueur.AutorisationModo || Joueur.AutorisationHelper || Joueur.AutorisationAdmin) {
							ListeStaffPartie.add(Joueur);
						}
					}
					serveur.Envoie(ListeStaffPartie, "CxSkip#" + NomJoueur);
					if (PartieEnCours.PAaaah && !PartieEnCours.PartiePersonnalisée) {
						Envoie("CxINFO#Merci de limiter l'utilisation de /skip en salon officiel. Son abus peut être sanctionnable.");
					}
				}
				return;
			}
			
			// Triche TP sur forto de plus de 10 pixels
			if (Code.equals("CxNOREC")) {
				if (getPartieForto() != null) {
					this._Forteresse.RallyValide = false;
				}
				return;
			}
			
			// Triche signalée
			if (Code.equals("CxTriche")) {
				String Salon = Salon();
				if (Message[1].equals("CroixRouge")) {
					if (!Salon.contains("Editeur Aaaah")) {
						CroixRouge++;
						if (CroixRouge < 10 || CroixRouge % 20 == 0) {
							serveur.Envoie_Super_Modo(this,"Triche détectée pour " + NomJoueur + " : " + Message[1] + " (" + Message[2] + ") sur : " + Salon);
						}
					}
				} else {
					System.out.println("Triche " + NomJoueur + " " + AdresseIP + " : " + Message[1] + " (" + Message[2] + ") sur : " + Salon);
					serveur.Avertissement_Modo("Triche détectée pour " + NomJoueur + " : " + Message[1] + " (" + Message[2] + ") sur : " + Salon, false);
				}
				return;
			}
			// Triche signalée
			if (Code.equals("CxTriRep")) {
				if (serveur.Checkeur != null) {
					serveur.Checkeur.Envoie("CxINFO#Carrés : " + Message[1] + " - Sauts Forto : " + Message[2] + " - Téléportations : " + Message[3] + " - Sauts Aaaah : " + Message[4]);
					serveur.Checkeur = null;
				}
				return;
			}

			// Statistiques - profil
			if (Code.equals("CxSt")) {
				if (Message.length == 2) {
					GetProfil(Message[1], false);
				}
				return;
			}
			// Modifier son Profil
			if (Code.equals("CxPr")) {
				Envoie("CxPr#" + Avatar + "#" + Description);
				return;
			}
			// Modification profil
			if (Code.equals("CxPrM")) {
				if (!Muet) {
					if (Message.length == 2) {
						Description = Message[1];
					} else {
						Description = "";
					}
					serveur.BOITE.Requete(Boite.CHANGEMENT_DESCRIPTION, this);
				} else {
					Envoie("CxINFO#Vous ne pouvez pas éditer votre description car vous avez été sanctionné.");
				}
				return;
			}

			// Demande liste MP reçus
			if (Code.equals("MPxLR")) {
				serveur.BOITE.Requete(Boite.LISTE_MP_RECUS, this);
				return;
			}
			// Demande liste MP envoyés
			if (Code.equals("MPxLE")) {
				serveur.BOITE.Requete(Boite.LISTE_MP_ENVOYES, this);
				return;
			}
			// Supprimer MP reçus
			if (Code.equals("MPxS")) {
				if (Message.length == 1) {
					serveur.BOITE.Requete(Boite.SUPPRIMER_MP, this);
				} else {
					serveur.BOITE.Requete(Boite.SUPPRIMER_MP_BY_ID, this, Integer.parseInt(Message[1]));
				}
				return;
			}
			// Nouveau message
			if (Code.equals("MPxNM")) {
				if (Message.length == 3) {
					serveur.BOITE.Requete(Boite.NOUVEAU_MP, this, Message[1], Message[2]);
				}
				return;
			}
			// Contournement
			if (Code.equals("CxCtn")) {
				if (Message.length == 2) {
					int CodeErreur = Integer.valueOf(Message[1]);
					System.out.println("C0NT1 " + NomJoueur + " " + AdresseIP +" "+ CodeErreur + " " + AdresseHost);
					serveur.Avertissement_Modo(NomJoueur + " a refusé le cookie.", false);
				}
				return;
			}
			// Demande liste d'amis
			if (Code.equals("CxAM")) {

				StringBuilder Info = new StringBuilder("CxAM#0#"); // 0 signifie liste d'amis
				boolean JoueurEnLigne;

				int Nb = ListeAmis.size();
				for (int i = 0; i < Nb; i++) {
					Ami ami = ListeAmis.get(i);
					JoueurEnLigne = false;
					//on parcours la liste des joueurs connectés
					int NbJoueur = serveur.ListeJoueur.size();
					for (int j = 0; j < NbJoueur; j++) {
						Joueur Joueur = serveur.ListeJoueur.get(j);
						// on regarde si c'est un ami
						if (Joueur.IdJoueur == ami.IdAmi) {
							Info.append(Joueur.CodeSalon(true));
							JoueurEnLigne = true;
							break;
						}
					}

					// hors ligne 
					if (!JoueurEnLigne) {
						Info.append($ + $ + ami.NomAmi + $ + "0" + $ + " ");
					}
				}

				Envoie(Info.toString());

				return;
			}

			// Suppression ami
			if (Code.equals("CxAS")) {
				int Nb = ListeAmis.size();
				for (int i = 0; i < Nb; i++) {
					Ami ami = ListeAmis.get(i);
					if (ami.NomAmi.equalsIgnoreCase(Message[1])) {
						serveur.BOITE.Requete(Boite.SUPPRESSION_AMI, this, ami.IdAmi);
						ListeAmis.remove(ami);
						return;
					}
				}
				return;
			}

			// Ajout ami
			if (Code.equals("CxAA")) {
				if (Message.length == 2) {
					String NomAmi = Message[1];
					AjouterAmi(NomAmi);
				}
				return;
			}
			
			// Signalement joueur /signaler
			if (Code.equals("CxSig")) {
				if (!Muet) {
					if (AutorisationInscription) {
						if (Message.length == 3) {
							if (Stats_TempsDeJeu > RestrictionHeure.SIGNALER) {
								String Cible = Message[1];
								String Motif = Message[2];
								String CibleIP = "";
								String Salon = "";
								Joueur JoueurCible = serveur.Joueur(Cible);
								if (JoueurCible!=null) {
									Cible = JoueurCible.NomJoueur;
									CibleIP = JoueurCible.AdresseIP;
									Salon = JoueurCible.Salon();
								} else {
									Envoie("CxINFO#Merci de limiter les signalements aux problèmes urgents. Ce joueur étant hors ligne, envoyez un mail à la place.");
									return;
								}
								if (Salon.toLowerCase().contains("maison")) {
									Envoie("CxINFO#Les signalements ne sont pas pris en compte pour ce type de salon.");
									return;
								}
								String MotifMin = Motif.toLowerCase();
								if (MotifMin.contains("flood") || MotifMin.contains("insulte")) {
									Envoie("CxINFO#Réponse automatique : merci d'utiliser la commande /ignore pour ce type de problèmes.");
								}
								Signalement Signal = new Signalement(NomJoueur,AdresseIP,Cible,CibleIP,Salon,Motif);
								serveur.ListeSignal.add(Signal);
								Envoie("CxINFO#Merci, signalement enregistré : [" + Motif + "]. Si le problème persiste, merci d'envoyer un mail (ne pas réitérer votre signalement).");
								serveur.Signaler_Modo(Signal.toString(this));
							} else {
								Message_Info("Vous n'avez pas assez d'heures de jeu pour utiliser cette commande.");
							}
						}
					} else {
						Envoie("CxINFO#Cette commande est réservée aux comptes inscrits.");
					}
				} else {
					Envoie("CxINFO#Vous ne pouvez pas utiliser cette commande car vous avez été rendu muet.");
				}
				return;
			}
			
			if (Code.equals("CxVision")) {
				if(!(AutorisationModo || AutorisationArbitre || AutorisationTournoiForto || AutorisationArbitreElo || AutorisationFilmeurElo)){
					String Salon = Salon();
					if (AutorisationFilmeur) {
						if (!PartieEnCours.hasMotDePasse() && !FilmPartiePublique) {
							System.out.println("C0M VFILM " + NomJoueur + " " + AdresseIP + " sur : " + Salon);
							serveur.Avertissement_Modo("Vision depuis compte filmeur sur salon public " + NomJoueur + " sur : " + Salon, false);
						}
					} else {
						System.out.println("Triche Vision " + NomJoueur + " " + AdresseIP + " sur : " + Salon);
						serveur.Avertissement_Modo("Triche vision détectée pour " + NomJoueur + " sur : " + Salon, false);
					}
				}
				return;
			}
			
			// Signalement forum joueur /signaler
			if (Code.equals("CxSigF")) {
				if (AutorisationForum && !Muet) {
					if (AutorisationInscription) {
						if (Message.length == 6) {
							String Cible = Message[1];
							String Motif = Message[2];
							String Sujet = Message[3];
							String Msg = Message[4];
							String NomForum = Message[5];

							SignalementForum Signal = new SignalementForum(NomJoueur,AdresseIP,Cible,Sujet,Msg,NomForum,Motif);
							serveur.ListeSignalForum.add(Signal);
							Envoie("CxINFO#Merci, signalement enregistré : [" + Motif + "]. Si le problème persiste, merci de contacter directement la modération du forum (ne pas réitérer votre signalement).");
							serveur.Signaler_ModoForum(Signal.toString(this));

						}
					} else {
						Envoie("CxINFO#Cette commande est réservée aux comptes inscrits.");
					}
				} else {
					Envoie("CxINFO#Vous ne pouvez pas utiliser cette commande car vous avez été rendu muet.");
				}
				return;
			}

			// Signalement map
			if (Code.equals("CxSigMap")) {
				if (!Muet) {

					if (Message.length == 3) {
						String Map = Message[1];
						Map = Map.replaceAll("@", "");
						if (isLong(Map)) {
							long MapCode = Long.valueOf(Map);
							Carte Carte = serveur.Aaaah_ListeCarte.get(MapCode);
							if (Carte!=null && MapCode >= 1000) {
								String Motif = Message[2];
								SignalementMap Signal = new SignalementMap(NomJoueur,AdresseIP,Carte,Motif);
								serveur.ListeSignalMap.add(Signal);
								Envoie("CxINFO#Merci, signalement enregistré : [" + Motif + "]. Si le problème persiste, merci de le signaler sur le forum sur le topic prévu à cet effet.");
								return;
							}
						}
						Envoie("CxINFO#Le signalement n'a pas fonctionné. Le code était-il valide ?");

					}
				}

				return;
			}

			// Test
			if (Code.equals("CxTTT")) {
				System.out.println("TEST#" + NomJoueur + "#" + Message[1]);
				return;
			}

			// changer password
			if (Code.equals("CxPWD")) {
				if (AutorisationAdmin) {
					serveur.BOITE.Requete(Boite.CHANGER_PASSWORD, this, Message[1], Message[2]);
				} else {
					System.out.println(NomJoueur + " ### Tentative de hack");
				}
				return;
			}

			// changer password
			if (Code.equals("CxCPWD")) {
				if (AutorisationChgPass.equals("1")) {
					//serveur.BOITE.Requete(Boite.DEMANDE_CHGPASS, this, Message[1], Message[2], Message[3]);
					String OldPass = Message[1];
					String NewPass = Message[2];
					String Remarques = "";
					if (Message.length>3) {
						Remarques = Message[3];
					}
					if (OldPass.equals(Password)) {
						serveur.BOITE.Requete(Boite.CHGPASS, this, NewPass, Remarques);                                
					} else {
						// Envoie ancien password incorrect  
						Envoie("CxCPWD#1");
					}
				}
				return;
			}
			
			if (Code.equals("CxLoB")) {
				if (getPartieBouboum() != null && (AutorisationModo)) {
					Bouboum PartieBouboum = (Bouboum)PartieEnCours.getJeu();
					
					if ((PartieEnCours.Createur.equalsIgnoreCase(NomJoueur) && !PartieBouboum.SalonOffi)) {
						if (Message.length != 30) {
							System.out.println("Map Bouboum mauvaise taille (" + NomJoueur+ ")");
							return;
						}
						String Reponse = Message[1];
						for (int i = 2; i < Message.length; ++i) {
							Reponse += "#" + Message[i];
						}
						
						if(PartieBouboum.ListeJoueur.size() == 1) {
							PartieBouboum.Nouvelle_Partie(0, Reponse);
						} else {
							PartieBouboum.FutureMap = Reponse;
							Envoie("CxINFO#Votre carte sera chargée à la fin de cette partie.");
						}
						
						//PartieBouboum.Nouvelle_Partie(0, Reponse);
					} else {
						Envoie("CxINFO#Vous devez être le créateur d'un salon qui n'est pas un serveur officiel pour pouvoir utiliser cette commande.");
						
					}
					return;
				}
			}
			
			// load Forterese (/load)
			if (Code.equals("CxLo")) {
				if (getPartieForto() != null
						&& (AutorisationModo || (((Forteresse)PartieEnCours.getJeu()).Dessin && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)))) {
					/*
					 CxLo#IDMAP#
					 
					 [Si monde perso :]
					 NFrigo(-XF1-YF1(-XF2-YF2))-G
		   			 -ZR1X1-ZR1Y1-ZR1X2-ZR1Y2-ZR2X1-ZR2Y1-ZR2X2-ZR2Y2
		   			 -PR1X1-PR1Y1-PR2X1-PR2Y1-
		   			 -<PLAFONDS><SOL>
		   			 []
		   			 
					 COULEUR_1#COULEUR_2#COULEUR_3#COULEUR_4#COULEUR_5#COULEUR_6#CODE_MAP
					*/
					
					Forteresse PartieForteresse = (Forteresse)PartieEnCours.getJeu();
					
					PartieForteresse.NumeroDonjon = 0;
					PartieForteresse.NiveauDonjon = 0;
					PartieForteresse.NiveauBonus = false;
					
					if (Message.length == 9) {
						///PartieForteresse.IdRally = 0;
						PartieForteresse.LoadMapForto(this, Message[1],Message[2],Message[3],Message[4],Message[5],Message[6],Message[7],Message[8],0);
					} else if (Message.length >= 23 /* Monde perso 0 frigo (16) + 6 Couleurs + Map (1) + 'P' (1) */
							   && Message.length <= 28 /* Monde perso 2 frigos (20) + 6 Couleurs + Map (1) + 'P' (1) */) {
						
						///PartieForteresse.IdRally = 0;
						int i = 2; // Infos maps modulables commencent à [2]
						i++; // Nombre de frigos
						i += (Integer.parseInt(Message[2]) % 3) * 2; // Coordonnées des frigos
						i++; // Gravité
						i += 8; // Zones de respawn
						i += 4; // Positions de respawn
						i++; // Bords
						
						PartieForteresse.LoadMapForto(this, Message[1],Arrays.copyOfRange(Message, i, i+6),Message[i + 6],Arrays.copyOfRange(Message, 2, i));
					} else if (Message.length == 3) { // /rally
						///PartieForteresse.IdRally = 0;
						PartieForteresse.LoadMapForto(this, new Rally());
					} else {
						if (Message.length == 2 && Message[1].equals("off")) {
							/*PartieForteresse.MapParDefaut = null;
							PartieForteresse.IdMapParDefaut = -1;*/
							PartieForteresse.setPlaylist(null);
							Envoie("CxINFO#Le rally ne sera plus chargé automatiquement.");
						}
					}
				} else {
					// load non autorisé (pas dessin ou pas créateur)
					Envoie("CxRa##");
				}

				return;
			}

			// exportation de map forto (message reçu après avoir sauvegardé la carte et avant d'afficher la popup d'export)
			if (Code.equals("FxBEXP")) {
				
				if (getPartieForto() != null
						&& ((Forteresse)PartieEnCours.getJeu()).Dessin 
						&& PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)
						&& Stats_TempsDeJeu > RestrictionHeure.EXPORTER_FORTO) { // 100h
						// génération d'un id aléatoire
						CodeMapRally = Calendar.getInstance().getTimeInMillis();	
						Envoie("FxBEXP#"+CodeMapRally);
				} else {
					Envoie("CxINFO#Vous pourrez exporter vos cartes sur le serveur à partir de 100 heures de jeu et en étant le créateur d'un salon dessin.");
				}
				return;
			}	

			// exportation de map forto respomap
			if (Code.equals("FxBUPD")) {
				
				if (PartieEnCours != null 
						&& PartieEnCours.Forto 
						&& ((Forteresse)PartieEnCours.getJeu()).Dessin 
						/*&& AutorisationModoCartesRally*/) { // affichage autorisé à tout le monde
						// chargement avant update d'un rally existant
						
						if (((Forteresse)PartieEnCours.getJeu()).Carte_Repertoriee()) {
							String Id = String.valueOf(((Forteresse)PartieEnCours.getJeu()).getIDCarteEnCours());
							
							serveur.BOITE.Requete(Boite.CHARGER_RALLY_RESPOMAP, this, Id);
						} else {
							Envoie_Info("Cette carte n'est pas exportée sur le serveur.");
						}

				} else {
					Envoie("CxINFO#Vous devez être dans un salon dessin pour utiliser cette commande.");
				}
				return;
			}			
			
			// exportation de map forto (création finale)
			if (Code.equals("FxEXP")) {
				
				if (PartieEnCours != null 
						&& PartieEnCours.Forto 
						&& ((Forteresse)PartieEnCours.getJeu()).Dessin 
						&& PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)
						&& Stats_TempsDeJeu > 360000000L
						&& CodeMapRally != 0) { // 100h
					
					if (Message.length >= 7) {
						String Titre = Message[1];
						int Couleur = Integer.parseInt(Message[2]);
						int Difficulte = Integer.parseInt(Message[3]);
						int Mode = Integer.parseInt(Message[4]);
						int Note = Integer.parseInt(Message[5]);
						String CodeMap = Message[6];
						int Status = serveur.StatusRallyDefaut;
						
						Forteresse PartieForteresse = (Forteresse)PartieEnCours.getJeu();
						MondeForteresse Monde = PartieForteresse.MondeEnCours;
						
						Rally rally;
						
						if (CodeMap.equals("@")) { // Export via /exporter
							String Map = PartieForteresse.Liste_Case();
							if (Mode == Rally.MODE_RALLY && !Map.contains("" + Forteresse.CASE_CARRE_OBJECTIF)) {
								Envoie("CxINFO#Il faut une case objectif (touche 'O') pour pouvoir exporter une map en tant que rally.");
								return;
							}
							
							CodeMap = PartieForteresse.MondeEnCours.NumMonde + "-" + Map;
						}
						
						if (PartieForteresse.CouleursPersoActivees) {
							int[] c = PartieForteresse.CouleursPersonnalisees;
							rally = new Rally(CodeMapRally,Monde,NomJoueur,Titre,Couleur,Difficulte,Mode,Note,CodeMap,Status,c[0],c[1],c[2],c[3],c[4],c[5], 0, "");
						} else {
							rally = new Rally(CodeMapRally,Monde,NomJoueur,Titre,Couleur,Difficulte,Mode,Note,CodeMap,Status);
						}
						
						serveur.BOITE.Requete(Boite.CREER_RALLY, this, rally);
					}
				} else {
					Envoie("CxINFO#Un problème est survenu.");
				}
				return;
			}		
			
			
			// update de map forto (update final)
			if (Code.equals("FxUPD")) {
				
				if (PartieEnCours != null 
						&& PartieEnCours.Forto 
						&& ((Forteresse)PartieEnCours.getJeu()).Dessin 
						&& (AutorisationModoCartesRally || AutorisationModo)
						&& ((Forteresse)PartieEnCours.getJeu()).Carte_Repertoriee()) {
					if (Message.length >= 8) {
						String Titre = Message[1];
						int Couleur = Integer.parseInt(Message[2]);
						int Difficulte = Integer.parseInt(Message[3]);
						int Mode = Integer.parseInt(Message[4]);
						int Note = Integer.parseInt(Message[5]);
						String CodeMap = Message[6];
						int Status = Integer.parseInt(Message[7]);
						
						Forteresse PartieForteresse = (Forteresse)PartieEnCours.getJeu();
						MondeForteresse Monde = PartieForteresse.MondeEnCours;
						
						Rally rally;
						
						if (PartieForteresse.CouleursPersoActivees) {
							int[] c = PartieForteresse.CouleursPersonnalisees;
							rally = new Rally(PartieForteresse.getIDCarteEnCours(),Monde,"",Titre,Couleur,Difficulte,Mode,Note,CodeMap,Status,c[0],c[1],c[2],c[3],c[4],c[5],0,"");
						} else {
							rally = new Rally(PartieForteresse.getIDCarteEnCours(),Monde,"",Titre,Couleur,Difficulte,Mode,Note,CodeMap,Status);
						}
						
						//Rally rally = new Rally(PartieForteresse.IdRally,"",Titre,Couleur,Difficulte,Mode,Note,CodeMap,Status,c1,c2,c3,c4,c5,c6);
						serveur.BOITE.Requete(Boite.UPDATE_RALLY, this, rally);
					}
				} else {
					Envoie("CxINFO#Vous devez être respomap Forto pour utiliser cette commande.");
				}
				return;
			}
			
			// playlist de map
			if (Code.equals("CxPlay")) {
				if (PartieEnCours != null && PartieEnCours.PAaaah && (AutorisationModo || PartieEnCours.Createur.equalsIgnoreCase(NomJoueur))
						&& !PartieEnCours.EditeurAaaah
						&& ((Aaaah)PartieEnCours.getJeu()).LoadAutorisé) {
					
					Aaaah PartieAaaah = ((Aaaah)PartieEnCours.getJeu());
					
					if (Message.length == 4) {
						long Now = System.currentTimeMillis();
						if (Now - LastLoad > 20000 || AutorisationModo) {
							PartieAaaah.BoucleMonde.clear();
							PartieAaaah.BouclePlaylist.clear();
							LastLoad = Now;
							String Param = ExtraireRaison(3, Message);
							serveur.BOITE.Playlist_populair_plus(3, Message[1]);
							String[] Liste = Param.split("[^0-9]+");
							for (int i=0; i<Liste.length; ++i) {
								if (PartieAaaah.BouclePlaylist.size()<500) {
									PartieAaaah.BouclePlaylist.add(Long.valueOf(Liste[i]));
								}
							}
							if (!PartieAaaah.BouclePlaylist.isEmpty()) {
								PartieAaaah.Playlist = true;
								PartieAaaah.PlaylistAléatoire = Message[2].equals("1");
							}
							if (PartieAaaah.BouclePlaylist.size()>500) {
								Envoie("CxINFO#La playlist est limitée à 500 maps pour le moment.");
							}
							String Msg = "CxINFO#" + PartieEnCours.Createur+ " a chargé " + PartieAaaah.BouclePlaylist.size() + " maps.";
							serveur.Envoie(PartieAaaah.ListeJoueur, Msg);
							
							PartieAaaah.Nouvelle_Partie(-1);

						} else {
							Envoie("CxINFO#Vous pourrez utiliser cette commande dans moins de 20 secondes.");
						}

					} else {
						PartieAaaah.Playlist = false;
						PartieAaaah.BouclePlaylist.clear();
						PartieAaaah.BoucleMonde.clear();
						Envoie("CxINFO#Les maps seront chargées aléatoirement.");
					}
				} else {
					// load non autorisé (pas dessin ou pas créateur)
					Envoie("CxINFO#Vous n'êtes pas le créateur d'une partie Aaaah privée.");
				}

				return;
			}

			// Créer team
			if (Code.equals("TxC")) {
				if (AutorisationInscription && !Membre) {
					if (Message.length == 6) {
						serveur.BOITE.Requete(Boite.CREER_TEAM, this, Message[1], Message[2], Message[3], Integer.parseInt(Message[4]), Integer.parseInt(Message[5]));
					}
				}
				return;
			}

			// Joueurs team en ligne
			if (Code.equals("TxO")) {
				Envoie(serveur.OnlineMembers());
				return;
			}

			// Forum affiché
			if (Code.equals("CxVF")) {
				Envoie("CxVFD#"+DerniereCoForum);
				if (Message.length == 1) {
					SurLeForum = true;
					serveur.EnvoieCP("CxVF#" + NomJoueur);
					DerniereCoForum = System.currentTimeMillis()/1000;
				} else {
					//System.out.println("Quitte fofo");
					SurLeForum = false;
					serveur.EnvoieCP("CxVF#" + NomJoueur + "#");
					DerniereCoForum = System.currentTimeMillis()/1000-5; // 5 secondes pour garder le surlignement sur les messages postés récemments
				}
				return;
			}
			
			// Utiliser un objet
			if (Code.equals("CxUOBJ")) {
				// TODO : ...
				return;
			}
		} else {
			// Choix d'un pseudo
			if (Code.equals("CxN") && serveur.ServeurOuvert) {
				String Nom = Message[1];
				boolean Invité = (Message.length == 2);
				
				if (Verif_IP_Dyna(Nom, Invité ? "*" : "")) {
					return;
				}
				if (Invité) {
					if (Serveur.RefusConnexionInvites) { // Pas d'invités pour les bêtas
						Deconnexion("Pas d'invité pour les versions bêtas.");
						return;
					}
					if (Nom.equalsIgnoreCase("tigrounette") || Nom.equalsIgnoreCase("admin")) {
						Deconnexion("Fake Tigrounette.");
						return;
					} else if (Nom.equals("pokabcd")){
						// Easter eggs !!!
						Nom = "Tigrounette";
					} else if (Nom.equalsIgnoreCase("extinction")) {
						Stats_Inscription = 1215515000;
						Stats_DernièreConnexion = Calendar.getInstance().getTimeInMillis() / 1000;
					}
					// Pseudo
					if (!Joueur.PseudoConforme(Nom)) {
						Deconnexion("Pseudo Non Conforme.");
						return;
					}
					//
					Nom = "*" + Nom;
					//
					boolean NomJoueurInvalide = true;
					while (NomJoueurInvalide) {
						boolean Boucle = true;
						int Nb = serveur.ListeJoueur.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = serveur.ListeJoueur.get(i);
							if (Joueur.NomJoueur.equalsIgnoreCase(Nom)) {
								String[] Div = Nom.split("_");
								if (Div.length == 2) {
									int Num = Integer.parseInt(Div[1]) + 1;
									Nom = Div[0] + "_" + Num;
								} else {
									Nom = Div[0] + "_1";
								}
								Boucle = false;
								break;
							}
						}
						if (Boucle) {
							NomJoueurInvalide = false;
						}
					}
					//
					NomJoueur = Nom;
					NomJoueurModifié = Nom;
					serveur.Evénement.EnvoieEvenement(this);
					serveur.ListeJoueur.add(this);
					//
					Envoie("CxN#" + NomJoueur);
					Identifié = true;
					
					System.out.println("C0N [" + Calendar.getInstance().getTime()+"] " + NomJoueur + " " + AdresseIP + " ### Connexion");
					Envoie("CxINFO#Utilisez /aide pour avoir un aperçu de quelques commandes utiles.");
				} else {
					String Mdp = Message[2];
					serveur.BOITE.Requete(Boite.IDENTIFICATION, this, Nom, Mdp);
				}
				return;
			}
			// Création d'un nouveau compte
			if (Code.equals("CxCC") && !Local.BETA) {
				if (CréationCompte) {
					String Nom = Message[1];
					if (Verif_IP_Dyna(Nom, "[C] ")) {
						return;
					}
					serveur.BOITE.Requete(Boite.CREATION_COMPTE, this, Nom.toLowerCase(), Message[2]);
					return;
				}
			}
		}
		//
		Deconnexion("Message inconnu : " + CHAINE.substring(0, CHAINE.length() % 100));
	}

	public void Log_Ban(String Banni, long Temps, String RAISON) {
		System.out.println("[" + Calendar.getInstance().getTime() + "] " + NomJoueur + " (" + AdresseIP + ") a banni " + Banni + " " + Temps + " heures. " + (RAISON.equals("") ? "" : "Raison : " + RAISON));
	}

	public void Log_Mute(String Banni, long Temps, String RAISON) {
		System.out.println("[" + Calendar.getInstance().getTime() + "] " + NomJoueur + " (" + AdresseIP + ") a muté " + Banni + " " + Temps + " heures. "  + (RAISON.equals("") ? "" : "Raison : " + RAISON));
	}

	public void Log_MuteForum(String ID, long Temps, String RAISON) {
		System.out.println("[" + Calendar.getInstance().getTime() + "] " + NomJoueur + " (" + AdresseIP + ") a sanctionné " + ID + " par " + Temps + " heures. " + (RAISON.equals("") ? "" : "Raison : " + RAISON));
	}

	public String ExtraireRaison(int Shift, String[] Tableau) {
		String Raison = "";
		if (Tableau.length > Shift) {
			int NbC = Tableau.length;
			for (int z = Shift; z < NbC; z++) {
				Raison += Tableau[z];
				if (z != NbC - 1) {
					Raison += " ";
				}
			}
		}
		return Raison;
	}

	public String EnvoieProfil(String Nom) {
		if (_Aaaah!=null) {
			_Aaaah.Maj_RatioSauvetage();
			_Aaaah.Maj_RatioPremier();
		}
		return "CxSt#" + Nom + "#" + Stats_Inscription
				+ "#" + _Aaaah.Stats_PartieGuide + "#" + _Aaaah.Stats_RatioSauvetage + "#" + _Aaaah.Stats_PartiePremier + "#" + _Aaaah.Stats_RatioPremier + "#" + _Bouboum.Stats_PartieJouée + "#" + _Bouboum.Stats_PartieGagnée + "#" + _Bouboum.Stats_Ratio
				+ "#" + _Aaaah.Stats_OldGuidés + "#" + _Aaaah.Stats_OldPGuidés + "#" + _Aaaah.Stats_OldPremier + "#" + _Aaaah.Stats_OldPPremier + "#" + _Bouboum.Stats_OldGagnées + "#" + _Bouboum.Stats_OldPGagnées
				+ "#" + GetCorrectAvatar(Avatar, IdTeam, serveur) + "#" + Description + "#" + (Local.BETA ? "0" : Stats_DernièreConnexion) + "#1#"+ UneRecompense
				+ (Membre ? "#" + IdTeam + "#" + NomTeam() + "#" + Grade + "#" + RoleInt() + "#" + (Stats_TempsDeJeu < 3600000 * 50 ? "1" : "0") : "#0#0#0#0#0")
				+ "#" + elo_flag;
	}

	public void GetProfil(String Nom, boolean Commande) {

		// soi-même
		if (Nom.equalsIgnoreCase(NomJoueur)) {
			Envoie(EnvoieProfil(Nom));
			return;
		}
		ArrayList<Joueur> Liste;
		if (Commande) {
			Liste = serveur.ListeJoueur;
		} else if (PartieEnCours == null) {
			Liste = serveur.ListeJoueurSansPartie;
		} else {
			Liste = PartieEnCours.getLsJoueurPartie();
		}
		int Nb = Liste.size();
		for (int i = 0; i < Nb; i++) {
			Joueur Joueur = Liste.get(i);
			if (Joueur.NomJoueur.equalsIgnoreCase(Nom)) {
				Envoie(Joueur.EnvoieProfil(Nom));
				return;
			}
		}
		serveur.BOITE.Requete(Boite.DEMANDE_PROFIL, this, Nom);
	}
	
	/**
	 * Retourne un avatar correct. Si <avatar> existe potentiellement, il est retourné tel quel.
	 * S'il représente un avatar vide/inexistant, c'est celui par défaut (Serveur.AVATAR_DEFAUT)
	 * qui sera retourné.
	 */
	public static String GetCorrectAvatar(String avatar, int IdTeam, Serveur serveur) {
		if (avatar.equals("") || avatar.equals("0")) {
			return Serveur.AVATAR_DEFAUT;
		} else if (avatar.equals("_blason")) {
			// On est d'office dans le chargement via Boite (car remplacé à la co sinon), on peut donc utiliser
			// la BDD ici
			return serveur.BOITE.GetBlason(IdTeam);
		} else {
			return avatar;
		}
	}
	

	public String NomTeam() {
		return NomTeam;
	}

	public void FixeAutorisations(int AutorisationsTeams) {
		switch (AutorisationsTeams) {
		case (2): // Leader
			AutorisationGestionMembres = true;
			AutorisationGestionRoles = true;
			AutorisationScribe = true;
			break;
		case (1): // Recruteur
			AutorisationGestionMembres = true;
			AutorisationGestionRoles = false;
			AutorisationScribe = false;
			break;
		case (3) : // Scribe
			AutorisationScribe = true;
			AutorisationGestionRoles = false;
			AutorisationGestionMembres = false;
			break;
		default:
			AutorisationGestionMembres = false;
			AutorisationGestionRoles = false;
			AutorisationScribe = false;
		}
	}

	public void OnlineMembers_Team(int ID_TEAM) {
		int Nb = serveur.ListeJoueur.size();
		StringBuilder Chaine = new StringBuilder("CxINFO#/membres " + ID_TEAM + " : ");
		StringBuilder InfosSalon = new StringBuilder("CxAM#1#"); // 1 signifie liste des membres
		boolean First = true;
		for (int i = 0; i < Nb; i++) {
			Joueur Joueur = serveur.ListeJoueur.get(i);
			if (Joueur.Membre && Joueur.IdTeam == ID_TEAM) {
				if (!First)
					Chaine.append(", ");
				Chaine.append(Joueur.NomJoueur);
				First = false;
				InfosSalon.append(Joueur.CodeSalon(false));
			}
		}
		if (First) {
			Chaine.append("Aucun.");
		}
		Envoie(Chaine.toString());
		Envoie(InfosSalon.toString());
	}

	public void OnlineMembers_Salon(String SALON) {
		int Nb = serveur.ListeJoueur.size();
		StringBuilder Chaine = new StringBuilder("CxINFO#Salon " + SALON + " : ");
		StringBuilder InfosSalon = new StringBuilder("CxAM#2#"); // 2 signifie liste du salon
		boolean First = true;
		for (int i = 0; i < Nb; i++) {
			Joueur Joueur = serveur.ListeJoueur.get(i);
			if (Joueur.SalonChat.equals(SALON)) {
				if (!First)
					Chaine.append(", ");
				Chaine.append(Joueur.NomJoueur);
				First = false;
				InfosSalon.append(Joueur.CodeSalon(false));
			}
		}
		Envoie(Chaine.toString());
		Envoie(InfosSalon.toString());
	}
	
	// Modo public connecté
	public void OnlineModo() {
		int Nb = serveur.ListeJoueur.size();
		StringBuilder InfosSalon = new StringBuilder("CxAM#3#"); // 3 signifie liste des modos
		for (int i = 0; i < Nb; i++) {
			Joueur ModoPublic = serveur.ListeJoueur.get(i);
			if (ModoPublic.AutorisationModoPublic || ModoPublic.IdTeam == 1) {
				InfosSalon.append(ModoPublic.CodeSalon(false));
			}
		}
		Envoie(InfosSalon.toString());
	}
	
	// Modo forum public connecté
		public void OnlineModoF() {
			int Nb = serveur.ListeJoueur.size();
			StringBuilder InfosSalon = new StringBuilder("CxAM#3#");

			for (int i = 0; i < Nb; i++) {
				Joueur ModoPublic = serveur.ListeJoueur.get(i);
				if (ModoPublic.AutorisationModoFPublic) {
					InfosSalon.append(ModoPublic.CodeSalon(false));
				}
			}

			Envoie(InfosSalon.toString());
		}
	// Animateurs
		public void OnlineAnimateur() {
			int Nb = serveur.ListeJoueur.size();
			StringBuilder InfosSalon = new StringBuilder("CxAM#3#");

			for (int i = 0; i < Nb; i++) {
				Joueur Animateur = serveur.ListeJoueur.get(i);
				if (Animateur.PureAnimateur) {
					InfosSalon.append(Animateur.CodeSalon(false));
				}
			}

			Envoie(InfosSalon.toString());
		}
	
	// Arbitres tournois connectés
	public void OnlineArbitreTournoi() {
		StringBuilder InfosSalon = new StringBuilder("CxAM#3#");

		InfosSalon.append(ListeOnlineArbitreUneSection(serveur.ListeArbitresA, "[A] "));
		InfosSalon.append(ListeOnlineArbitreUneSection(serveur.ListeArbitresB, "[B] "));
		InfosSalon.append(ListeOnlineArbitreUneSection(serveur.ListeArbitresF, "[F] "));

		Envoie(InfosSalon.toString());
	}
	
	public String ListeOnlineArbitreUneSection(ArrayList<String> Ls, String debut) {
		StringBuilder Retour = new StringBuilder("");
		
		int Nb = Ls.size();

		for (int i = 0; i < Nb; i++) {
			Joueur Arbitre = serveur.Joueur(Ls.get(i));
			if (Arbitre != null) {
				Retour.append(Arbitre.CodeSalon(false, debut));
			}
		}
		
		return Retour.toString();
	}
		
	public boolean CheckParams(int NbParams, String[] Commande) {
		if (Commande.length < NbParams) {
			Envoie("CxINFO#Nombre de paramètres incorrect.");
			return false;
		}
		return true;
	}

	public void PasDroits() {
		Envoie("CxINFO#Vous n'avez pas les droits pour effectuer cette action.");
	}

	public boolean CheckDroits(boolean Condition) {
		if (!AutorisationModoTeam && !Condition) {
			PasDroits();
			return false;
		}
		return true;
	}

	public boolean CheckSelf(int ID_JOUEUR) {
		if (!AutorisationModoTeam && ID_JOUEUR == IdJoueur) {
			Envoie("CxINFO#Vous ne pouvez pas enlever vos droits.");
			return false;
		}
		return true;
	}

	public void Quitter_Team() {
		Membre = false;
		IdTeam = 0;
		Grade = 0;
		NomTeam = "";
		AutorisationGestionMembres = false;
		AutorisationGestionRoles = false;
		AutorisationScribe = false;
		Envoie("CxINFO#Vous n'êtes plus membre de votre team.");
		Envoie("TxQ");
	}

	private static final Pattern intPattern = Pattern.compile("\\d{1,10}");
	private static final Pattern longPattern = Pattern.compile("\\d{1,19}");
	private static final Pattern doublePattern = Pattern.compile("\\d(\\.\\d{1,3})?");

	public static final boolean isInteger(String str) {		
		boolean isInt = intPattern.matcher(str).matches();
		if (isInt) {
			try {
				Integer.valueOf(str);
			} catch (Exception e) {
				return false;
			}
		}
		return isInt;
	}

	public static final boolean isLong(String str) {
		return longPattern.matcher(str).matches();
	}

	public static final boolean isDouble(String str) {
		return doublePattern.matcher(str).matches();
	}

	public ArrayList<Joueur> JoueursPartie() {
		if (PartieEnCours != null) {
			return PartieEnCours.getLsJoueurPartie();
		} else {
			return serveur.ListeJoueurSansPartie;
		}
	}

	public String Jeu() {
		return PartieEnCours!=null?serveur.Jeu(PartieEnCours.CodeJeu):"Chat";
	}

	public boolean EnOfficiel() {
		return PartieEnCours!=null?!PartieEnCours.PartiePersonnalisée:false;
	}
	public boolean EnFight() {
		return PartieEnCours!=null && PartieEnCours.PAaaah ?((Aaaah)PartieEnCours.getJeu()).SalonFight:false;
	}
	public boolean EnDéfilantes() {
		return PartieEnCours!=null && PartieEnCours.PAaaah ?((Aaaah)PartieEnCours.getJeu()).ModeJeu == Carte.MODE_DEF:false;
	}
	public boolean EnNG() {
		return PartieEnCours!=null && PartieEnCours.PAaaah ?((Aaaah)PartieEnCours.getJeu()).ModeJeu == Carte.MODE_NG:false;
	}
	public String Salon() {
		if (PartieEnAttente != null && PartieEnAttente.AttenteElo) {
			return "Partie en attente";
		} else if (PartieEnCours != null) {
			return "[" + Jeu() + "] " + PartieEnCours.NomPartie;
		} else {
			if (Afk) {
				return "Afk.";
			} else if (SurLeForum) {
				return "Forum";
			} else {
				return "Chat principal.";
			}
		}

	}

	public String CodeSalon(boolean AMIS) {
		return CodeSalon(AMIS, "");
	}
	
	public String CodeSalon(boolean AMIS, String PseudoAjout) {
		String Nom = NomJoueur;
		if (!AMIS) {
			if (AutorisationGestionRoles) {
				Nom += "*";
			} else if (AutorisationGestionMembres) {
				Nom += "+";
			} else if(AutorisationScribe) {
				Nom += "$";
			}
		}
		if (PartieEnCours == null) {
			if (Afk) {
				// afk
				return $ + $ + PseudoAjout + Nom + $ + "-3" + $ + " ";
			} else
				if (SurLeForum) {
					return $ + $ + PseudoAjout + Nom + $ + "-4" + $ + " ";
				} else {
					// chat principal
					return $ + $ + PseudoAjout + Nom + $ + "-1" + $ + " ";
				}
		} else {
			// joue
			return $ + $ + PseudoAjout + Nom + $ + PartieEnCours.CodeJeu + $ + PartieEnCours.NomPartie + $ + PartieEnCours.CodePartie + $ + (PartieEnCours.hasMotDePasse() ? "1" : "0");
		}
	}

	public boolean AccèsForumsSpéciaux(int IdForum) {
		return !((IdForum == Serveur.FORUM_MODO && !AutorisationModo)
				|| (IdForum == Serveur.FORUM_VIP && !AutorisationModo && !AutorisationArbitre && !AutorisationModoForum && !AutorisationModoTeam && !AutorisationDev)
				|| (IdForum == Serveur.FORUM_ANIM && !AutorisationModo && !AutorisationAnimateur)
				|| (IdForum == Serveur.FORUM_TEAM && IdTeam == 0)
				|| IdForum >= 12 || IdForum == 10);
	}

	public void LogCommande(String CHAINE) {
		System.out.println("C0M " + NomJoueur + " " + CHAINE);
	}

	public void Commande(String CHAINE) {
		String[] Commande = CHAINE.split(" ");
		if (Commande.length == 0)
			return;
		String CodeCmd = Commande[0].toLowerCase();
		
		Aaaah PartieAaaah = null;
		Bouboum PartieBouboum = null;
		Forteresse PartieForteresse = null;
		
		if (PartieEnCours != null) {
			if (PartieEnCours.PAaaah) {
				PartieAaaah = ((Aaaah)PartieEnCours.getJeu());
			} else if (PartieEnCours.Bouboum) {
				PartieBouboum = ((Bouboum)PartieEnCours.getJeu());
			} else if (PartieEnCours.Forto) {
				PartieForteresse = ((Forteresse)PartieEnCours.getJeu());
			}
		}
		
		if (AutorisationAdmin || AutorisationDev) {
			// Courbe
			if (CodeCmd.equalsIgnoreCase("courbe")) {
				if (Commande.length == 2) {
					serveur.PRÉCISION_COURBE = Integer.parseInt(Commande[1]);
					if (!Nio.COURBE) {
						Nio.COURBE = true;
						serveur.Message_Modo("Courbe activée.", false);
					}
				} else {
					Nio.COURBE = !Nio.COURBE;
					if (Nio.COURBE) {
						serveur.Message_Modo("Courbe activée.", false);
					} else {
						serveur.Message_Modo("Courbe désactivée.", false);
					}
				}
				return;
			}
			
			// Donner un bonus Bouboum
			if (CodeCmd.equals("bbonus")) {
				LogCommande(CHAINE);
				if (Commande.length > 2) {
					String Nom = Commande[1];
					int Bonus = Integer.parseInt(Commande[2]);
					Joueur Cible = serveur.Joueur(Nom);
					if (Cible != null && PartieEnCours != null && Cible.PartieEnCours.Bouboum) {
						serveur.Envoie(Cible.PartieEnCours.getLsJoueurPartie(), "ChB#"+Bonus+"#" + Commande[1]);
						Cible._Bouboum.Reset_Bonus_WithAllIn(Bonus);
					}
				}
				return;
			}
			
			if (CodeCmd.equals("cleanfusion")) {
				serveur.BOITE.Requete(Boite.TMP_CLEAN_FUSIONS, this);
				Envoie("CxINFO#Nettoyage des fusions en cours...");
				return;
			}

			// Forcer la déconnexion de tous les comptes non admin - /serveurouvert pour réautoriser les co
			if (CodeCmd.equals("byebye") || CodeCmd.equals("kickall")) {
				Envoie("CxINFO#Déconnexion des joueurs en cours. Utilisez /serveurvouvert pour réautoriser les connexions.");
				serveur.ServeurOuvert = false;
				
				synchronized(serveur.ListeJoueur) {
					int Nb = serveur.ListeJoueur.size() - 1;
					for (int i = Nb; i >= 0; i--) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (!Joueur.AutorisationAdmin) {
							Joueur.Sauvegarde_Aaaah();
							Joueur.Sauvegarde_Bouboum();
							Joueur.Stats.save(serveur.BOITE,Joueur);
							Joueur.Deconnexion("Deconnexion");
						}
					}
				}
				
				return;
			}
			
			// Forcer la déconnexion de tous les comptes non admin et charge l'événement spécifié
			if (CodeCmd.equals("rebootev")) {
				if (Commande.length != 2) {
					Envoie("CxINFO#Un événement doit précisé.");
					return;
				}
				
				serveur.ServeurOuvert = false;
				
				synchronized(serveur.ListeJoueur) {
					int Nb = serveur.ListeJoueur.size() - 1;
					for (int i = Nb; i >= 0; i--) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (!Joueur.AutorisationAdmin) {
							Joueur.Sauvegarde_Aaaah();
							Joueur.Sauvegarde_Bouboum();
							Joueur.Stats.save(serveur.BOITE,Joueur);
							Joueur.Deconnexion("Deconnexion");
						}
					}
				}
				try {
					serveur.Evénement.setEvenementPrincipal(Integer.valueOf(Commande[1]));
				} catch(NumberFormatException e) {
					Envoie("CxINFO#Il faut envoyer un nombre en paramètre.");
				}
				
				serveur.ServeurOuvert = true;
				
				return;
			}

			// Extinction serveur
			if (CodeCmd.equals("quit")) {
				Envoie("CxINFO#Arrêt en cours");
				serveur.ServeurOuvert = false;
				serveur.StopSupprAutoComptesInut();
				int Nb = serveur.ListeJoueur.size();
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = serveur.ListeJoueur.get(i);
					Joueur.Sauvegarde_Aaaah();
					Joueur.Sauvegarde_Bouboum();
					Joueur.Stats.save(serveur.BOITE,Joueur);
				}
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					System.err.print("3XC quit "); e.printStackTrace();
				}
				System.out.println("Arret du serveur");
				System.exit(0);
				return;
			}

			// archivage des stats
			if (CodeCmd.equals("archive")) {
				Envoie("CxINFO#Archivage en cours");
				serveur.BOITE.Requete(Boite.ARCHIVER_STATS, this);
				return;
			}
			
			// désactivation maraaathon
			if (CodeCmd.equals("maraaathon")) {
				serveur.Maraaaathon = !serveur.Maraaaathon;
				Envoie("CxINFO#"+serveur.Maraaaathon);
				return;
			}
			// désactivation maraboum
			if (CodeCmd.equals("maraboum")) {
				serveur.Maraboum = !serveur.Maraboum;
				Envoie("CxINFO#"+serveur.Maraboum);
				// TODO sauvegarder stats joueurs pour la fin
				return;
			}

			// désactivation ServeurOuvert
			if (CodeCmd.equals("serveurouvert")) {
				serveur.ServeurOuvert = !serveur.ServeurOuvert;
				Envoie("CxINFO#"+serveur.ServeurOuvert);
				return;
			}
			
			// Info serveur
			if (CodeCmd.equals("etat")) {
				String JoueurCible = Commande[1];
				int Nb = serveur.ListeJoueur.size();
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = serveur.ListeJoueur.get(i);
					if (Joueur.NomJoueur.equalsIgnoreCase(JoueurCible)) {
						Joueur.Envoie("CxTTT");
						return;
					}
				}
				//System.out.println(serveur.ListeAction.size() + " " + serveur.Aaaah_ListeCarte.size() + " " + serveur.Aaaah_ListeMonde.size() + " " + serveur.Forteresse_ListeMonde.size());
				return;
			}
			
			// Ajouter un donjon : id_donjon niveau temps (spécial)
			if (CodeCmd.equals("add_donjon")) {
				if (Commande.length == 4 || Commande.length == 5) {
					if (PartieEnCours != null && PartieEnCours.Forto) {
						int[] Param = new int[4];
						
						Param[0] = Integer.valueOf(Commande[1]);
						Param[1] = Integer.valueOf(Commande[2]);
						Param[2] = Integer.valueOf(Commande[3]);
						Param[3] = (Commande.length == 5 ? Integer.valueOf(Commande[4]) : 0);
						
						serveur.BOITE.Requete(Boite.AJOUTER_DONJON, this, Param, ((Forteresse)PartieEnCours.getJeu()).Liste_Case());
					}
					
				} else {
					Envoie("CxINFO#Paramètres incorrect : /add_donjon id_donjon niveau temps [spécial*]");
				}
				return;
			}
			
			// Charger le niveau d'un donjon
			if (CodeCmd.equals("nivdonjon")) {
				
				if (Commande.length == 3 || Commande.length == 4) {
					if (PartieEnCours != null && PartieEnCours.Forto) {
						int[] Param = new int[4];
						
						Param[0] = Integer.valueOf(Commande[1]);
						Param[1] = Integer.valueOf(Commande[2]);
						Param[2] = (Commande.length == 4 ? Integer.valueOf(Commande[3]) : 0);
						
						serveur.BOITE.Requete(Boite.CHARGER_NIV_DONJON, this, Param);
					}
					
				} else {
					Envoie("CxINFO#Paramètres incorrect : /nivdonjon id_donjon niveau [spécial*]");
				}
				return;
			}
			
			// Téléporter sur Forteresse à partir de la position courante
			if (CodeCmd.equals("tpc") && (AutorisationAdmin || Local.LOCAL)) {
				if (Commande.length == 4) {
					Joueur JOUEUR = serveur.Joueur(Commande[1]);
					
					if (JOUEUR == null) {
						Envoie("CxINFO#" + Commande[1] + " n'est pas connecté.");
						return;
					}
					
					JOUEUR.Envoie("CxTPC#" + Commande[2] + "#" + Commande[3]);
				} else {
					Envoie("CxINFO#/tpc Joueur X Y");
				}
				return;
			}
			
			// Téléporter sur Forteresse à partir de la position absolue
			if (CodeCmd.equals("tpa") && (AutorisationAdmin || Local.LOCAL)) {
				if (Commande.length == 4) {
					Joueur JOUEUR = serveur.Joueur(Commande[1]);
					
					if (JOUEUR == null) {
						Envoie("CxINFO#" + Commande[1] + " n'est pas connecté.");
						return;
					}
					
					JOUEUR.Envoie("CxTPA#" + Commande[2] + "#" + Commande[3]);
				} else {
					Envoie("CxINFO#/tpa Joueur X Y");
				}
				return;
			}
			
			// Donne les ID des parties
			if (CodeCmd.equals("idp")) {
				if (Commande.length > 1) {
					String Recherche = ExtraireRaison(1, Commande);
					StringBuilder resultat = new StringBuilder("CxINFO#Parties trouvées :");
					int NbPartie = serveur.ListePartie.size();
					for (int i = 0; i < NbPartie; i++) {
						Partie Partie = serveur.ListePartie.get(i);
						if (Partie.NomPartie.contains(Recherche)) {
							resultat.append("\n");
							resultat.append(Partie.CodePartie);
							resultat.append(" [");
							resultat.append(Partie.CodeJeu);
							resultat.append("] ");
							resultat.append(Partie.NomPartie);
						}
					}
					Envoie(resultat.toString());
				}
				return;
			}
			// Supprimer une partie de force
			if (CodeCmd.equals("delp")) {
				if (Commande.length > 1) {
					int Recherche;
					try {
						Recherche = Integer.parseInt(Commande[1]);
					} catch(Exception e) {
						Envoie("CxINFO#/delp IdPartie : utilisez /idp NomDePartiePartiel pour obtenir l'id de la partie");
						return;
					}
					
					int NbPartie = serveur.ListePartie.size();
					for (int i = 0; i < NbPartie; i++) {
						Partie Partie = serveur.ListePartie.get(i);
						if (Partie.CodePartie == Recherche) {
							serveur.ListePartie.remove(Partie);
							Envoie("CxINFO#Partie supprimée.");
							return;
						}
					}
				}
				return;
			}
			
			// changer le mode de validation rally
			if (CodeCmd.equals("validforto")) {
				serveur.StatusRallyDefaut = 1-serveur.StatusRallyDefaut;
				Envoie("CxINFO#serveur.StatusRallyDefaut=" + serveur.StatusRallyDefaut);
				return;
			}

			// cleanup rally
			if (CodeCmd.equals("deleterallys")) {
				serveur.BOITE.Requete(Boite.DELETE_RALLYS, this);
				return;
			}
			
			// supprimer un monde Forteresse
			if (CodeCmd.equals("deletemonde")) {
				if (Commande.length == 1) {
					StringBuilder Msg = new StringBuilder("CxINFO#Mondes actuels :\n  ");
					for(MondeForteresse Monde : serveur.Forteresse_ListeMonde) {
						Msg.append(Monde.toString());
						Msg.append(", ");
					}
					Msg.append("\n" + serveur.Forteresse_ListeMonde.size() + " mondes.");
					Envoie(Msg.toString());
				} else {
					for(MondeForteresse Monde : serveur.Forteresse_ListeMonde) {
						if (Monde.toString().equalsIgnoreCase(Commande[1])) {
							serveur.Forteresse_ListeMonde.remove(Monde);
							Envoie("CxINFO#Monde " + Commande[1] + " supprimé.");
							return;
						}
					}
					Envoie("CxINFO#Aucun monde supprimé.");
				}
				return;
			}
			
			// activer/désactiver les effets de cadeau sur forto en général
			if (CodeCmd.equals("kdof")) {
				serveur.cadeauxFortoActives = !serveur.cadeauxFortoActives;
				Envoie("CxINFO#Effets cadeaux sur Forteresse : " + serveur.cadeauxFortoActives);
				return;
			}
			
			// ajouter un monde officiel Forteresse
			if (CodeCmd.equals("addmondef")) {
				// /addmondef Num FrigoX1 FrigoY1 FrigoX2 FrigoY2
				try {
					MondeForteresse Monde;
					if (Commande.length == 4) { // Map simple
						Monde = new MondeForteresse(1, 2);
					} else if (Commande.length == 6){ // Map double
						Monde = new MondeForteresse(Integer.valueOf(Commande[2]), Integer.valueOf(Commande[3]), Integer.valueOf(Commande[4]), Integer.valueOf(Commande[5]));
					} else {
						Envoie("CxINFO#/addmondef Num FrigoX1 FrigoY1 [FrigoX2 FrigoY2]");
						return;
					}
					Monde.NumMonde = Commande[1];
					
					serveur.Forteresse_ListeMonde.add(Monde);
					Envoie("CxINFO#Monde ajouté.");
				} catch (Exception e) {
					Envoie("CxINFO#/addmondef Num FrigoX1 FrigoY1 [FrigoX2 FrigoY2]");
				}
				return;
			}
			
			if (CodeCmd.equals("loadmap")) {
				if (Commande.length > 1) {
					serveur.BOITE.Requete(Boite.RELOAD_MAP_AAAAH, this, Commande[1]);
				} else {
					Envoie("CxINFO#/loadmap IdMap (sans '@')");
				}
				
				return;
			}
			
			// recharger le classement
			if (CodeCmd.equals("classement")) {
				Elo.aEteModif();
				serveur.MAJ_Classement();
				Envoie("CxINFO#Classement mis à jour");
				return;
			}
			// activer le Survival Kick
			if(CodeCmd.equals("startsurvival")) {
				serveur.SurvivalLance = !serveur.SurvivalLance;
				Envoie("CxINFO#Survival Kick en cours : " + serveur.SurvivalLance);
				return;
			}
			// activer un événement
			if(CodeCmd.equals("event") || CodeCmd.equals("event2")) {
				if(Commande.length == 1) {
					serveur.Evénement.display(this);
				} else {
					try {
						if (CodeCmd.equals("event")) {
							serveur.Evénement.setEvenementPrincipal(Integer.valueOf(Commande[1]));
							
							/*if (serveur.Evénement == Serveur.EventHalloween) {
								serveur.Aaaah_NombreMonde++;
								Serveur.MondeHalloweenAaaah = serveur.Aaaah_NombreMonde;
								serveur.Aaaah_ListeCarte.put((long)Serveur.MondeHalloweenAaaah, new Carte(99L, "" + Serveur.MondeHalloweenAaaah, 0, 1, 1, 120000, "-", 0, 0, "D", "Hell Box", 1, 120000, "-", 0, ""));
							}*/
							
							Envoie("CxINFO#Événement principal changé : " + Integer.valueOf(Commande[1]));
						} else {
							serveur.Evénement.setEvenementSecondaire(Integer.valueOf(Commande[1]));
							Envoie("CxINFO#Événement secondaire changé : " + Integer.valueOf(Commande[1]));
						}
						
					} catch(NumberFormatException e) {
						Envoie("CxINFO#Il faut envoyer un nombre en paramètre.");
					}
				}
				return;
			}
			// tester l'event pour un joueur uniquement
			if(CodeCmd.equals("eventtest")) {
				if(Commande.length == 1) {
					serveur.Evénement.display(this);
				} else if (Commande.length == 3){
					
					Joueur JOUEUR = serveur.Joueur(Commande[1]);
					
					if (JOUEUR == null) {
						Envoie("CxINFO#" + Commande[1] + " n'est pas connecté.");
						return;
					}
					
					if (!serveur.Evénement.EnvoieEvenement(Integer.parseInt(Commande[2]), JOUEUR)) {
						Envoie("CxINFO#Événement inconnu. Tapez /eventtest pour afficher les événements disponibles.");
					} else {
						Envoie("CxINFO#Changement d'event réussi.");
					}
				} else {
					Envoie("CxINFO#Nombre de paramètres incorrect.");
				}
				return;
			}
		}
		
		if (AutorisationAdmin) {
			// Blocage des parties classée s'il y a une trop grande différence d'elo
			if (CodeCmd.equals("bde")) {
				PartieAttenteElo.ActiverBlocageDiffElo = !PartieAttenteElo.ActiverBlocageDiffElo;
				Envoie("CxINFO#Blocage des parties en fonction de la différence d'elo : " + PartieAttenteElo.ActiverBlocageDiffElo);
				return;
			}
			
			// /i Joueur
			if (CodeCmd.equals("i")) {
				if(Commande.length < 2) {
					return;
				}
				LogCommande(CHAINE);
				String Info = serveur.Info_Joueur(Commande[1], false);
				if (!Info.isEmpty()) {
					// joueur en ligne
					Envoie("CxINFO#" + Info);
				} else {
					// hors ligne
					serveur.BOITE.Requete(Boite.DEMANDE_INFO, this, Commande[1]);
				}
				return;
			}
			// Promotion modo
			if (CodeCmd.equals("promotion")) {
				try {
					serveur.Bdd.Requete_TransformationMODO.setString(1, Commande[1].toLowerCase());
					serveur.Bdd.Requete_TransformationMODO.execute();
					Message_Info("Modo: " + Commande[1]);
				} catch (Exception e) {
					System.err.print("3XC Promotion "); e.printStackTrace();
				}
				return;
			}
			// Promotion arbitre
			if (CodeCmd.equals("arbitre")) {
				try {
					serveur.Bdd.Requete_TransformationARB.setString(1, Commande[1].toLowerCase());
					serveur.Bdd.Requete_TransformationARB.execute();
					Message_Info("Arbitre: " + Commande[1]);
				} catch (Exception e) {
					System.err.print("3XC Arbitre "); e.printStackTrace();
				}
				return;
			}
			// Flag
			if (CodeCmd.equals("flags")) {
				// Modification de flag
				if (Commande.length>2) {
					serveur.BOITE.Requete(Boite.FLAGS, this, Commande[1],  Commande[2]);
				}
				// Visualisation de flag
				else if (Commande.length== 2)
				{
					serveur.BOITE.Requete(Boite.FLAGS_JOUEUR, this, Commande[1]);
				}
				// Indication commande
				else {
					Envoie("CxINFO#"
							+ "1=modo forum\n"
							+ "2=arbitre\n"
							+ "3=respoteam\n"
							+ "4=respomap aaaah\n"
							+ "5=modo\n"
							+ "6=helper\n"
							+ "7=arbitre tournoi forto\n"
							+ "8=super arbitre\n"
							+ "9=arbitre tournoi aaaah\n"
							+ "10=admin\n"
							+ "11=recrue modo forum\n"
							+ "12=modo+modo forum\n"
							+ "13=beta tester\n"
							+ "14=filmeur\n"
							+ "15=respomap aaaah (recrue)\n"
							+ "16=gestionnaire elo\n"
							+ "17=développeur\n"
							+ "20=recrue arbitre\n"
							+ "40=respomap forto\n"
							+ "50=modo public\n"
							+ "60=animateur\n"
							+ "70=animateur superviseur\n"
							+ "80=modo forum public"
							);
				}
				return;
			}			
			
			// Suppression de 10 comptes inactifs (auto tous les jours)
			if (CodeCmd.equals("supprcmpt")) {
				serveur.BOITE.Requete(Boite.DELETE_COMPTES_INUTILISES);
				return;
			}
			
			// Attribuer des récompense elo
			if (CodeCmd.equals("recomp_elo")) {
				RecompenseElo.Attribuer(serveur);
				Envoie("CxINFO#Done !");
				return;
			}
			
			if (CodeCmd.equals("archvelo")) {
				serveur.BOITE.Requete(Boite.ARCHIVER_STATS_ELO);
				Envoie("CxINFO#Archivage elo initié (fin signalée dans les logs)");
				return;
			}
			
			// Comptes multiples password
			if (CodeCmd.equals("multipwd")) {
				if (Commande.length == 2) {
					serveur.BOITE.Requete(Boite.MULTI_PWD, this, Commande[1]);
				}
				return;
			}
			if (CodeCmd.equals("activerarb")) {
				serveur.ActiverArbitrage = !serveur.ActiverArbitrage;
				Envoie("CxINFO#"+serveur.ActiverArbitrage);
				return;
			}

			
			// simuler l'envoie d'une commande
			if (CodeCmd.equals("simulecmd")) {
				if (Commande.length > 2) {
					Joueur JOUEUR = serveur.Joueur(Commande[1]);
					
					if (JOUEUR == null) {
						Envoie("CxINFO#" + Commande[1] + " n'est pas connecté.");
						return;
					}
					
					JOUEUR.Envoie(CHAINE.replace('$', '#').substring(("simulecmd " + JOUEUR.NomJoueur + " ").length()));
					System.out.println("C0M SIMCMD " + CHAINE.substring("simulecmd".length()));
				} else if (Commande.length == 2) {
					Envoie(Commande[1]);
				}
				
				return;
			}

			if (CodeCmd.equals("filtrage")) {
				serveur.FiltrageHost = !serveur.FiltrageHost;
				Envoie("CxINFO#Filtrage " + serveur.FiltrageHost);
				return;
			}
			if (CodeCmd.equals("migration")) {
				Envoie("CxINFO#Migration.");
				serveur.MigrationLN();
				return;
			}

			// refus IP 
			if (CodeCmd.equals("refus_ip")) {
				if (Commande.length>1) {
					String IP = Commande[1];
					if (!serveur.ListeRefusIP.contains(IP)) {
						serveur.ListeRefusIP.add(IP);
						serveur.BOITE.Requete(Boite.BAN_IP, this, IP);
						Message_Info("Ajouté : " + IP);
					} else {
						serveur.ListeRefusIP.remove(IP);
						serveur.BOITE.Requete(Boite.DEBAN_IP, this, IP);
						Message_Info("Retiré : " + IP);
					}
				} else {
					Envoie("CxINFO#" + serveur.ListeRefusIP.toString());
				}
				return;
			}
			// refus Host                       
			if (CodeCmd.equals("refus_host")) {
				if (Commande.length>1) {
					String Nom = Commande[1];
					if (!serveur.ListeRefusHost.contains(Nom)) {
						serveur.ListeRefusHost.add(Nom);
						serveur.BOITE.Requete(Boite.BAN_HOST, this, Nom);
						Message_Info("Ajouté : " + Nom);
					} else {
						serveur.ListeRefusHost.remove(Nom);
						serveur.BOITE.Requete(Boite.DEBAN_HOST, this, Nom);
						Message_Info("Retiré : " + Nom);
					}
				} else {
					Envoie("CxINFO#" + serveur.ListeRefusHost.toString());
				}
				return;
			}

			// Autorisation acces special 
			if (CodeCmd.equals("autorisation")) {
				String Nom = Commande[1];
				Nom = Nom.toLowerCase();
				if (serveur.ListeAutorisation.contains(Nom)) {
					serveur.ListeAutorisation.remove(Nom);
				} else {
					serveur.ListeAutorisation.add(Nom);
				}
				Envoie("CxINFO#" + serveur.ListeAutorisation.toString());
				return;
			}

			// Ban IP
			if (CodeCmd.equals("ban_ip")) {
				if (Commande.length > 1) {
					boolean DEF = Commande.length>2;
					String Cible = Commande[1];
					if (!serveur.ListeBanniIP.contains(Cible)) {
						serveur.ListeBanniIP.add(Cible);
						if (DEF) {
							serveur.BOITE.Requete(Boite.BAN_IP, this, Cible);
						}
						Message_Info("Ajouté : " + Cible);
					} else {
						serveur.ListeBanniIP.remove(Cible);
						if (DEF) {
							serveur.BOITE.Requete(Boite.DEBAN_IP, this, Cible);
						}
						Message_Info("Retiré : " + Cible);
					}
				} else {
					Message_Info(serveur.ListeBanniIP.toString());
				}
				return;
			}
			// Ban Nom
			if (CodeCmd.equals("ban_nom")) {
				if (Commande.length > 1) {
					String Cible = Commande[1];
					if (!serveur.ListeBanniNom.contains(Cible)) {
						serveur.ListeBanniNom.add(Cible);
						Message_Info("Ajouté : " + Cible);
					} else {
						serveur.ListeBanniNom.remove(Cible);
						Message_Info("Retiré : " + Cible);
					}
				} else {
					Message_Info(serveur.ListeBanniNom.toString());
				}
				return;
			}
			// Mute forum IP
			if (CodeCmd.equals("mute_ip")) {
				if (Commande.length > 1) {
					String Cible = Commande[1];
					if (!serveur.ListeMuetForum.contains(Cible)) {
						serveur.ListeMuetForum.add(Cible);
					} else {
						serveur.ListeMuetForum.remove(Cible);
					}
				}
				return;
			}
			// Consultations ban
			if (CodeCmd.equals("ban_liste")) {
				Envoie("CxINFO#" + serveur.ListeBanniIP.toString() + " - " + serveur.ListeBanniNom.toString());
				return;
			}
			// Consultations mute forum
			if (CodeCmd.equals("mute_liste")) {
				Envoie("CxINFO#" + serveur.ListeMuetForum.toString() + ", " + serveur.ListeMuetForumNom);
				return;
			}

			// Historique des sanctions
			if (CodeCmd.equals("histo_ip")) {
				if (Commande.length > 1) {
					String IP = Commande[1];
					serveur.BOITE.Requete(Boite.DEMANDE_HISTO, this, "---", IP);
				}
				return;
			}

			// Valider le changement de password
			if (CodeCmd.equals("valider_pass")) {
				if (Commande.length > 1) {
					String Param = ExtraireRaison(1, Commande);
					String[] Liste = Param.split("[^a-zA-Z0-9_\\*]+");
					for (int i=0; i<Liste.length; ++i) {
						String Nom = Liste[i];
						if (!Nom.isEmpty()) {
							if (Nom.length()>=3) {
								serveur.BOITE.Requete(Boite.VALIDER_CHGPASS, this, Nom, "1");
							}
						}
					}
				}
				return;
			}
			
			// Valider le changement de password
			if (CodeCmd.equals("refuser_pass")) {
				if (Commande.length > 1) {
					String Param = ExtraireRaison(1, Commande);
					String[] Liste = Param.split("[^a-zA-Z0-9_\\*]+");
					for (int i=0; i<Liste.length; ++i) {
						String Nom = Liste[i];
						if (!Nom.isEmpty()) {
							if (Nom.length()>=3) {
								serveur.BOITE.Requete(Boite.VALIDER_CHGPASS, this, Nom, "2");
							}
						}
					}
				}
				return;
			}
			// Liste des demandes de changement de password
			if (CodeCmd.equals("liste_pass")) {
				serveur.BOITE.Requete(Boite.LISTE_CHGPASS, this);
				return;
			}    
			
			// Valider ou refuser les demandes de copies Journées Portes Ouvertes
			if (CodeCmd.equals("valider_jpo") || CodeCmd.equals("refuser_jpo")) {
				if (Commande.length > 1) {
					String Param = ExtraireRaison(1, Commande);
					String[] Liste = Param.split("[^a-zA-Z0-9_\\*]+");
					boolean validation = CodeCmd.equals("valider_jpo");
					for (int i=0; i<Liste.length; ++i) {
						String Nom = Liste[i].substring(0,1).toUpperCase() + Liste[i].substring(1).toLowerCase();
						if (!Nom.isEmpty()) {
							if (Nom.length()>=3) {
								serveur.BOITE.Requete(Boite.VALIDER_DEMANDE_JPO, this, Nom,validation);
							}
						}
					}
				}
				return;
			}
			
			// Afficher la liste des demandes de copies Journées Portes Ouvertes
			if (CodeCmd.equals("liste_jpo")) {
				serveur.BOITE.Requete(Boite.LISTE_JPO, this);
				return;
			}
			
			// Choisir la valeur <N> de la JPO (=> /jpoN)
			if (CodeCmd.equals("setjpo")) {
				if (Commande.length == 2) {
					serveur.CmdJPO = "jpo" + Commande[1].toLowerCase();
					Envoie("CxINFO#Nouvelle commande pour la jpo : /" + serveur.CmdJPO);
				} else {
					Envoie("CxINFO#/setjpo &lt;NomNextJPO&gt; =&gt; /jpo&lt;NomNextJPO&gt;");
				}
				return;
			}
			
			// Obtenir la valeur de la JPO en cours
			if (CodeCmd.equals("getjpo")) {
				Envoie("CxINFO#Commande de la JPO : /" + serveur.CmdJPO + (!serveur.JPOEnCours ? " (inactive)"
						: " (commencée il y a " + ((serveur.JournéePorteOuverte + 63071997L - System.currentTimeMillis() / 1000L) / 86400L) + " jours) [" + serveur.JournéePorteOuverte + "]"));
				return;
			}
			
			// Lancement / Fermeture d'une Journée Porte Ouverte ; Donne l'accès à la commande /jpoX
			if (CodeCmd.equals("jpo")) {
				if (!serveur.JPOEnCours) {
					
					serveur.JPOEnCours = true;
					
					// Contient la date limite de dernière connexion d'un compte récupérable (Date - 2 ans)
					serveur.JournéePorteOuverte = (System.currentTimeMillis() / 1000L) - 63071997L;
					Envoie("CxINFO#Lancement d'une journée porte ouverte effectué.");
				}
				else {
					serveur.JPOEnCours = false;
					Envoie("CxINFO#Fin de la journée porte ouverte.");
				}
				return;
			}
			
			// reload ban déf
			if (CodeCmd.equals("loadbandef")) {
				serveur.BOITE.Requete(Boite.LOADBANDEF, this);
				return;
			}  

		}
		
		if (AutorisationAdmin || AutorisationDemiAdmin || AutorisationDev) {
			// désactivation perfdb
			if (CodeCmd.equals("perfdb")) {
				serveur.PerfDB = !serveur.PerfDB;
				Envoie("CxINFO#"+serveur.PerfDB);
				return;
			}
			
			if (CodeCmd.equals("setlimiteboite")) {
				if (Commande.length > 1) {
					try {
						LimiteurRequeteBoite.setDureeMaxParMoyenne(Integer.valueOf(Commande[1]), this);
					} catch (Exception e) {
						Envoie("CxINFO#Erreur : le paramètre doit être un nombre.");
					}
				} else {
					Envoie("CxINFO#Paramètre nécessaire.");
				}
				
				return;
			}
			
			if (CodeCmd.equals("bloctropreq")) {
				LimiteurRequeteBoite.BLOQUE_SI_TROP_RAPIDE = !LimiteurRequeteBoite.BLOQUE_SI_TROP_RAPIDE;
				Envoie("CxINFO#BLOQUE_SI_TROP_RAPIDE : " + LimiteurRequeteBoite.BLOQUE_SI_TROP_RAPIDE);
				return;
			}
			
			if (CodeCmd.equals("lsspamcounter")) {
				if (Commande.length > 1) {
					try {
						int limite = Integer.valueOf(Commande[1]);
						StringBuilder msg = new StringBuilder("CxINFO#Liste des comptes (>= " + limite + ") :\n");
						
						for (int i = serveur.getLsJoueur().size() - 1; i >= 0; --i) {
							Joueur J = serveur.getLsJoueur().get(i);
							int v = J.limiteurBoite.getSpamCounter();
							
							if (v >= limite) {
								msg.append(J.getPseudo() + " spamcounter : " + v + "\n");
							}
						}
						
						Envoie(msg.toString());
					} catch (Exception e) {
						Envoie("CxINFO#Erreur : le paramètre doit être un nombre." + e.toString());
					}
				} else {
					Envoie("CxINFO#Paramètre nécessaire.");
				}
				return;
			}
			
			if (CodeCmd.equals("iboite")) {
				Envoie("CxINFO#" + serveur.BOITE.getInfosRequeteEnCours());
				return;
			}
			
			if (CodeCmd.equals("client")) {
				serveur.NoClientModifé = !serveur.NoClientModifé;
				Envoie("CxINFO#NoClientModifé = "+serveur.NoClientModifé);
				return;
			}
			//
			if (CodeCmd.equals("refus_invite")) {
				Serveur.RefusConnexionInvites = !Serveur.RefusConnexionInvites;
				Envoie("CxINFO#RefusConnexionInvites = " + Serveur.RefusConnexionInvites);
				return;
			}
			// debug
			if (CodeCmd.equals("debug")) {
				serveur.Debug = !serveur.Debug;
				Envoie("CxINFO#"+serveur.Debug);
				return;
			}
			// maj version
			if (CodeCmd.equals("version")) {
				if (Commande.length > 1) {
					String Version = Commande[1];
					serveur.VersionMineure = Version;
					System.out.println("Nouvelle version : "  + Version);
					Envoie("CxINFO#Version : " + Version);
				} else {
					Envoie("CxINFO#Version mineure : " + serveur.VersionMineure);
				}
				return;
			}
			
			if (CodeCmd.equals("eject3")) {
				Eject3(Commande);
				return;
			}
		}
		
		if (AutorisationArbitre) {
			// Kick
			if (CodeCmd.equals("kick")) {
				if (Commande.length >= 2) {
					String Raison = ExtraireRaison(2, Commande);
					Log_Ban(Commande[1], 0, Raison);
					serveur.Ban_Joueur(this, Commande[1], 0, "1", Raison, false, false);
				}
				return;
			}
			// Ban
			if (CodeCmd.equals("ban")) {
				if (Commande.length >= 2) {
					String Raison;
					int Temps;
					if (Commande.length>=3 && isInteger(Commande[2])) {
						Raison = ExtraireRaison(3, Commande);
						Temps = Integer.valueOf(Commande[2]);
						if (Temps>24 || (Temps>4 && !AutorisationSuperArbitre && !AutorisationHelper)) {
							Temps = 4;
						}
					}else{
						Raison = ExtraireRaison(2, Commande);
						Temps = 2;
					}
					Log_Ban(Commande[1], Temps, Raison);
					serveur.Ban_Joueur(this, Commande[1], Temps*3600000, "1", Raison, false, false);
				}
				return;
			}
			// Mute
			if (CodeCmd.equals("mute")) {
				if (Commande.length >= 2) {
					String Raison;
					int Temps;
					if (Commande.length>=3 && isInteger(Commande[2])) {
						Raison = ExtraireRaison(3, Commande);
						Temps = Integer.valueOf(Commande[2]);
						if (Temps>24 || (Temps>4 && !AutorisationSuperArbitre && !AutorisationHelper)) {
							Temps = 4;
						}
					}else{
						Raison = ExtraireRaison(2, Commande);
						Temps = 1;
					}
					Log_Mute(Commande[1], Temps, Raison); 
					serveur.Mute_Joueur(this, Commande[1], Temps*3600000, "1", Raison, false);
				}
				return;
			}
			// Démute
			if (CodeCmd.equals("unmute")) {
				if (Commande.length == 2) {
					Log_Mute(Commande[1], 0, "");
					serveur.Mute_Joueur(this, Commande[1], 0, "1", "", false);
				}
				return;
			}
		}
		if ((AutorisationModo || AutorisationModoForum) && !AutorisationHelper) {

			// Historique des sanctions
			if (CodeCmd.equals("histo")) {
				if (Commande.length > 1) {
					String NomCible = Commande[1];
					Joueur Cible = serveur.Joueur(NomCible);
					String IP = "";
					if (Cible != null && !AutorisationRecrueForum) {
						IP = Cible.AdresseIP;
					}
					serveur.BOITE.Requete(Boite.DEMANDE_HISTO, this, NomCible, IP);
				}
				return;
			}
			// Historique des dernières sanctions
			if (CodeCmd.equals("log")) {
				serveur.BOITE.Requete(Boite.DEMANDE_LOG, this);
				return;
			}
			// Donner l'autorisation de changer de password
			if (CodeCmd.equals("autoriser_pass") && !AutorisationRecrueForum) {
				if (Commande.length > 1) {
					String Param = ExtraireRaison(1, Commande);
					String[] Liste = Param.split("[^a-zA-Z0-9_\\*]+");
					for (int i=0; i<Liste.length; ++i) {
						String Nom = Liste[i];
						if (!Nom.isEmpty()) {
							Joueur Cible = serveur.Joueur(Nom);
							if (Cible != null) {
								Cible.AutorisationChgPass = "1";
							}
							serveur.BOITE.Requete(Boite.AUTORISER_CHGPASS, this, Nom, "1");
						}
					}
				}
				return;
			}
			if (CodeCmd.equals("note")) {
				if (Commande.length > 1) {
					String NomCible = Commande[1];
					String Message = ExtraireRaison(2, Commande);
					serveur.BOITE.Requete(Boite.LOG_SANCTION, this, NomCible, "-", Message, "noté", 0);
					String Phrase = "Note sur " + NomCible + " : " + Message;
					if (AutorisationModo) {
						Envoie("CxINFO#"+Phrase);
					}
					else {
						serveur.Avertissement_ModoForum(Phrase, false);
					}
				}
				return;
			}

			// Mute forum par pseudo
			if (CodeCmd.equals("mute_forum")) {
				if (Commande.length >= 3 && isInteger(Commande[2])) {
					String NomCible = Commande[1].toLowerCase();                                        
					int Temps = Integer.valueOf(Commande[2]);
					String Raison = "";
					if (Commande.length > 3) {
						Raison = ExtraireRaison(3, Commande);
					}
					Log_MuteForum(NomCible, Temps, Raison);

					if (Temps == 0) {
						serveur.Demute_Forum_Nom(NomCible);
						String Phrase = NomCible + " n'est plus mute par nom sur le forum.";
						if (!AccepteCanalAvertoForum) {
							Envoie("CxINFO#" + Phrase);
						}
						serveur.Avertissement_ModoForum(Phrase, false);
						serveur.BOITE.Requete(Boite.LOG_SANCTION, this, NomCible, "-", Raison, "mute_forum_nom", 0);
						return;
					}

					if (AutorisationRecrueForum) {
						if (Temps>300) {
							Temps = 300;
						}
					}

					// Mute Joueur s'il est connecté
					String IP = "-";
					Joueur JoueurCible = serveur.Joueur(NomCible);
					if (JoueurCible != null) {
						JoueurCible.AutorisationForum = false;  
						IP = JoueurCible.AdresseIP;
					}

					String Message = "Mute par nom de " + NomCible + " (" + Temps + " heures) effectué.";
					if (Raison.length() > 0) {
						Message += " Raison : " + Raison;
					}
					if (!AccepteCanalAvertoForum) {
						Envoie("CxINFO#" + Message);
					}
					serveur.Avertissement_ModoForum(Message, false);

					serveur.ListeMuetForumNom.add(NomCible.toLowerCase());
					serveur.Mute_Forum_Nom(NomCible, Temps);
					serveur.BOITE.Requete(Boite.LOG_SANCTION, this, NomCible, IP, Raison, "mute_forum_nom", (Long.valueOf(Temps)).intValue());
				} else {
					Envoie("CxINFO#/mute_forum NomJoueur Temps Raison");
				}
				return;
			}

			// Historique des sanctions forum
			if (CodeCmd.equals("histo_forum")) {
				if (Commande.length > 1) {
					String Id = Commande[1];
					serveur.BOITE.Requete(Boite.DEMANDE_HISTO_FORUM, this, Id);
				}
				return;
			}
			// Supprimer l'avatar
			if (CodeCmd.equals("suppr_avatar")) {
				if (Commande.length > 1) {
					String Cible = Commande[1];
					serveur.BOITE.Requete(Boite.SUPPRIMER_AVATAR, this, Cible);
					serveur.BOITE.Requete(Boite.LOG_SANCTION, this, Cible, "-", "Avatar supprimé", "noté", 0);
				}
				return;
			}
			// Supprimer le cache
			if (CodeCmd.equals("cache")) {
				serveur.Reset_Cache_Forum();
				return;
			}

			// Couleur / titre forum
			if (CodeCmd.equals("titre")) {
				if (Commande.length > 3) {
					String Cible = Commande[1];
					String Couleur = Commande[2];
					if (Couleur.length()==6) {
						String Titre = ExtraireRaison(3, Commande);
						serveur.BOITE.Requete(Boite.EDITER_TITRE_FORUM, this, Cible, Couleur, Titre);
						Envoie("CxINFO#Edité.");
					}
				} else if (Commande.length > 1) {
					serveur.BOITE.Requete(Boite.SUPPRIMER_TITRE_FORUM, this, Commande[1]);
					Envoie("CxINFO#Edité.");
				}
				return;
			}

			// Commande /noavf
			if (CodeCmd.equals("nof")) {
				LogCommande(CHAINE);
				if (AccepteCanalAvertoForum) {
					Envoie("CxINFO#Canal forum fermé.");
					AccepteCanalAvertoForum = false;
				} else {
					Envoie("CxINFO#Canal forum ouvert.");
					AccepteCanalAvertoForum = true;
				}
				return;
			}

			// surveillance messages forum
			if (CodeCmd.equals("suivre")) {
				if (Commande.length > 1) {
					LogCommande(CHAINE);

					String Param = ExtraireRaison(1, Commande);
					String[] Liste = Param.split("[^a-zA-Z0-9_\\*]+");
					for (int i=0; i<Liste.length; ++i) {
						String Cible = Liste[i];
						if (!Cible.isEmpty()) {
							Cible = Cible.toLowerCase();
							if (serveur.ListeSurveillanceForum.contains(Cible)){
								serveur.ListeSurveillanceForum.remove(Cible);
								Envoie("CxINFO#Retiré : " + Cible);
							} else {
								serveur.ListeSurveillanceForum.add(Cible);
								if (!AccepteCanalAvertoForum) {
									Envoie("CxINFO#Ajouté : " + Cible);
								}
								serveur.Avertissement_ModoForum(Cible + " est maintenant suivi.", false);

							}
						}
					}

				} else {
					Envoie("CxINFO#"+serveur.ListeSurveillanceForum.toString());
				}
				return;
			}
		}
		
		if (AutorisationModo || AutorisationDev) {
			try {
				// lancer des feux d'artifice
				if(CodeCmd.equals("artifice")) {
					if (Commande.length > 1 && isInteger(Commande[1])) {
						serveur.Envoie(serveur.ListeJoueur, "CxARTF#" + Commande[1]);
					} else {
						serveur.Envoie(serveur.ListeJoueur, "CxARTF#3");
					}
					return;
				}
				// colorer les messages des joueurs CP
				if (CodeCmd.equals("couleurcp")) {
					serveur.CouleursCPAuto = !serveur.CouleursCPAuto;
					Envoie("CxINFO#Couleurs CP automatiques : " + serveur.CouleursCPAuto);
				}
				
				// suppression bot
				if (CodeCmd.equals("bot")) {
					serveur.DecoTricheAuto = !serveur.DecoTricheAuto;
					Envoie("CxINFO#Déconnexion automatique en cas de triche : "+serveur.DecoTricheAuto);
					DecoTricheAuto = serveur.DecoTricheAuto;
					return;
				}
				// suppression bot2
				if (CodeCmd.equals("bot2")) {
					serveur.DecoTricheAuto = !serveur.DecoTricheAuto;
					Envoie("CxINFO#Déconnexion automatique en cas de triche : "+serveur.DecoTricheAuto);
					return;
				}
								
				// Changer message bienvenue arbitre
				if (CodeCmd.equals("msga")) {
					if (Commande.length == 1) {
						serveur.MessageBienvenueArbitre = "";
						serveur.Message_Modo("Message VIP retiré.", false);
						return;
					}
					serveur.MessageBienvenueArbitre = "[Message VIP] "+CHAINE.substring(5);
					serveur.Message_Arbitre(serveur.MessageBienvenueArbitre, false);
					return;
				}
				
				if (CodeCmd.equals("msgj")) {
					if (Commande.length == 1) {
						serveur.MessageBienvenueJoueur = "";
						serveur.Message_Modo("Message d'accueil retiré.", false);
						return;
					}
					LogCommande(CHAINE);
					serveur.MessageBienvenueJoueur = CHAINE.substring(5);
					serveur.Message_Modo("[Message d'accueil] "+serveur.MessageBienvenueJoueur, false);
					return;
				}

				// Définir une liste d'arbitres pour le tournoi
				if (CodeCmd.equals("set_arb")) {
					if (Commande.length > 1) {
						ArrayList<String> Ls = getLsArbitreTournoi(Commande[1]);
						if (Ls == null) {
							Envoie("CxINFO#/set_arb {a, b, f} Pseudo1, Pseudo2, ...");
							return;
						}
						
						if (Commande.length != 2) {
							Ls = new ArrayList<String>(Arrays.asList(ExtraireRaison(2, Commande).replace(" ", "").split(",")));
						}
						
						Envoie("CxINFO#Liste d'arbitres : " + String.join(", ", Ls));
					} else {
						Envoie("CxINFO#/set_arb {a, b, f} Pseudo1, Pseudo2, ...");
					}
					return;
				}
				// Ajouter une liste d'arbitres à ceux déjà présents
				if (CodeCmd.equals("add_arb")) {
					if (Commande.length > 1) {
						ArrayList<String> Ls = getLsArbitreTournoi(Commande[1]);
						if (Ls == null) {
							Envoie("CxINFO#/add_arb {a, b, f} Pseudo1, Pseudo2, ...");
							return;
						}
						
						if (Commande.length != 2) {
							Ls.addAll(Arrays.asList(ExtraireRaison(2, Commande).replace(" ", "").split(",")));
						}
						
						Envoie("CxINFO#Liste d'arbitres : " + String.join(", ", Ls));
					} else {
						Envoie("CxINFO#/add_arb {a, b, f} Pseudo1, Pseudo2, ...");
					}
					return;
				}
				// Supprimer une liste d'arbitres
				if (CodeCmd.equals("del_arb")) {
					if (Commande.length > 1) {
						ArrayList<String> Ls = getLsArbitreTournoi(Commande[1]);
						if (Ls == null) {
							Envoie("CxINFO#/del_arb {a, b, f} Pseudo1, Pseudo2, ... (mettez un * pour tous les supprimer)");
							return;
						}
						String str = ExtraireRaison(2, Commande).replace(" ", "");
						if (str.indexOf('*') != -1) {
							Ls.clear();
						} else {
							Ls.removeAll(Arrays.asList(str.split(",")));
						}
						
						Envoie("CxINFO#Liste d'arbitres : " + String.join(", ", Ls));
					} else {
						Envoie("CxINFO#/del_arb {a, b, f} Pseudo1, Pseudo2, ... (mettez un * pour tous les supprimer)");
					}
					return;
				}
				
				// Pour arbitrer un match
				if (CodeCmd.equals("jarbitre")) {
					Envoie("CxARBT");
					AutorisationTournoiForto = true;
					AutorisationTournoiAaaah = true;
					Envoie("CxINFO#Mode arbitre tournoi activé.");
					return;
				}
				
				// Arrêter d'arbitrer un match
				if (CodeCmd.equals("plusarbitre")) {
					Envoie("CxNARBT");
					AutorisationTournoiForto = false;
					AutorisationTournoiAaaah = false;
					Envoie("CxINFO#Mode arbitre tournoi désactivé.");
					return;
				}
				
				// Supprimer les effets d'un event sur un joueur
				if (CodeCmd.equals("noev")) {
					if (Commande.length == 2) {
						Joueur JOUEUR = serveur.Joueur(Commande[1]);
						
						if (JOUEUR == null) {
							Envoie("CxINFO#" + Commande[1] + " n'est pas connecté.");
							return;
						}
						
						JOUEUR.Envoie("CxNOEV");
						Envoie("CxINFO#Evénement retiré pour " + JOUEUR.NomJoueur);
					} else {
						Envoie("CxINFO#/noev Joueur : Supprimer tous les événements pour ce joueur jusqu'à sa prochaine reconnexion.");
					}
					return;
				}
				
				if (CodeCmd.equals("filmer_public")) {
					if (Commande.length == 2) {
						Joueur J = serveur.Joueur(Commande[1]);
						if (J != null) {
							J.FilmPartiePublique = !J.FilmPartiePublique;
							Envoie("CxINFO#Autorisation de filmer en public (" + J.NomJoueur + ") : " + J.FilmPartiePublique);
						} else {
							Envoie("CxINFO#Ce joueur n'est pas connecté.");
						}
					}
					return;
				}


				// Nouvelle Partie
				if (CodeCmd.equals("np")) {
					if (PartieEnCours != null) {
						if (PartieEnCours.PAaaah) {
							PartieAaaah.PartieEnCours = false;
							if (Commande.length == 2) {
								String IdMap = Commande[1];
								IdMap = IdMap.replaceAll("@", "");
								PartieAaaah.Nouvelle_Partie(Long.parseLong(IdMap));
							} else {
								PartieAaaah.Nouvelle_Partie(-1);
							}
						} else
							if (PartieEnCours.Bouboum) {
								if (Commande.length == 2) {
									PartieBouboum.Nouvelle_Partie(Integer.parseInt(Commande[1]), null);
								} else {
									PartieBouboum.Nouvelle_Partie(null, null);
								}
							} else
								if (PartieEnCours.Forto) {
									PartieForteresse.CouleurParDefaut();
									PartieForteresse.NumeroDonjon = 0;
									PartieForteresse.NiveauDonjon = 0;
									PartieForteresse.NiveauBonus = false;
									PartieForteresse.DecalageLancement = true;
									if (Commande.length == 2) {
										PartieForteresse.Nouvelle_Partie(Integer.parseInt(Commande[1]) % serveur.Forteresse_ListeMonde.size(), null, false);
									} else {
										PartieForteresse.Nouvelle_Partie(-1, null, false);
									}
								}
					}
					return;
				}
				
				// Message serveur
				if (CodeCmd.equals("mss")) {
					LogCommande(CHAINE);
					serveur.Envoie(serveur.ListeJoueur, "CxMSS#" + CHAINE.substring(4));
					return;
				}
				// Message modo
				if (CodeCmd.equals("ms")) {
					serveur.Envoie(JoueursPartie(), "CxMS#" + CHAINE.substring(3));
					if (PartieEnAttente != null && PartieEnAttente.AttenteElo) {
						serveur.Envoie(serveur.ListeJoueurEnAttenteElo, "CxMS#" + CHAINE.substring(3));
					}
					return;
				}
				
				// Bonus point Aaaah
				if (CodeCmd.equals("bonus")) {
					LogCommande(CHAINE);
					if (Commande.length > 2) {
						String Nom = Commande[1];
						Integer Points = Integer.parseInt(Commande[2]);
						Joueur Cible = serveur.Joueur(Nom);
						if (Cible != null && PartieEnCours != null && PartieEnCours.PAaaah) {
							Cible._Aaaah.Score = Points;
						}
					}
					return;
				}
				
			}  catch (Exception e) {
				Envoie("CxM#Je n'ai pas pu exécuter cette commande monsieur le " + (AutorisationModo?"Modo":"Dev")+" :'(#<font color='ED67EA'>[serveur]</font>");
			}
		}

		if (AutorisationModo) {
			try {
				// Commande /nomp
				if (CodeCmd.equals("nomp")) {
					if (AccepteCanalMP) {
						serveur.Message_Modo(NomJoueur + " a fermé le canal.", false);
						//Envoie("CxINFO#En tant que membre de l'équipe de modération, vous devriez limiter cette action au maximum.");
						AccepteCanalMP = false;
					} else {
						AccepteCanalMP = true;
						serveur.Message_Modo(NomJoueur + " a réouvert le canal.", false);
					}
					return;
				}

				// Commande /noav
				if (CodeCmd.equals("noav")) {
					LogCommande(CHAINE);  
					if (AccepteCanalAverto) {
						Envoie("CxINFO#Canal fermé.");
						AccepteCanalAverto = false;
					} else {
						Envoie("CxINFO#Canal ouvert.");
						AccepteCanalAverto = true;
					}
					return;
				}
				// Info Modo
				if (CodeCmd.equals("modo")) {
					Envoie("CxMODO#");
					return;
				}
				// Info Modo
				if (CodeCmd.equals("modos")) {
					LogCommande(CHAINE);
					serveur.BOITE.Requete(Boite.DEMANDE_LISTE_MODO, this);
					return;
				}

				// Info Fusions
				if (CodeCmd.equals("fusions")) {
					StringBuilder Chaine = new StringBuilder("CxINFO#Fusions: ");
					for (FusionTeam Fusion : new HashSet<FusionTeam>(serveur.ListeTeamAmie.values())) {
						Chaine.append("\n" + Fusion.Alliance);					
					}
					Envoie(Chaine.toString());
					return;
				}
				
				// Changer de pseudo sur le canal arbitre
				if (CodeCmd.equals("cpa")) {
					if (Commande.length == 2) {
						NomCanalArbitre = "*" + Commande[1];
					} else {
						NomCanalArbitre = NomJoueur;
					}
					serveur.Message_Modo("[" + NomJoueur + "] parlera maintenant sous le nom [" + NomCanalArbitre + "] sur le canal VIP.", false);
					return;
				}
				
				//
				if (CodeCmd.equals("retrouver")) {
					LogCommande(CHAINE);
					if (Commande.length == 2) {
						String Cible = Commande[1];
						Joueur Joueur = serveur.Joueur(Cible);
						if (Joueur!=null) {
							Envoie("CxINFO#"+Cible+" est en ligne.");
						} else {
							serveur.BOITE.Requete(Boite.RETROUVER_JOUEUR, this, Cible);
						}
					}
					return;
				}

				// /Info Joueur
				if (CodeCmd.equals("info")) {
					String Info = serveur.Info_Joueur(Commande[1], true);
					if (!Info.isEmpty()) {
						// joueur en ligne
						Envoie("CxINFO#" + Info);
					} else {
						// hors ligne
						serveur.BOITE.Requete(Boite.DEMANDE_INFO, this, Commande[1]);
					}
					return;
				}
				
				if (CodeCmd.equals("eject3")) {
					Eject3(Commande);
					return;
				}
				
				if (CodeCmd.equals("eject")) {
					if (Commande.length == 2) {
						ArrayList<Joueur> Liste = JoueursPartie();
						int Nb = Liste.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = Liste.get(i);
							if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
								if (PartieEnCours != null) {
									PartieEnCours.Quitter_partie(Joueur);
								} else {
									serveur.ListeJoueurSansPartie.remove(Joueur);
									int NP = serveur.ListePartie.size();
									for (int j = 0; j < NP; j++) {
										serveur.ListePartie.get(j).getLsJoueurPartie().remove(Joueur);
									}
								}
								
								if (Joueur.PartieEnAttente.AttenteElo) {
									Joueur.PartieEnAttente.AttenteEloPartie.Joueur_Quitte_Attente_Elo(Joueur, true);
								}
								return;
							}
						}

					}
					return;
				}
				if (CodeCmd.equals("eject2")) {
					if (Commande.length == 2) {
						ArrayList<Joueur> Liste = serveur.ListeJoueur;
						int Nb = Liste.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = Liste.get(i);
							if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
								serveur.ListeJoueur.remove(Joueur);
								Envoie("CxINFO#suppr de ListeJoueur");
							}
						}
						
						Liste = serveur.ListeJoueurSansPartie;
						Nb = Liste.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = Liste.get(i);
							if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
								serveur.ListeJoueurSansPartie.remove(Joueur);
								serveur.EnvoieCP("CxX#" + NomJoueur);
								Envoie("CxINFO#suppr de ListeJoueurSansPartie");
							}
						}
						
						Liste = serveur.ListeJoueurEnAttenteElo;
						Nb = Liste.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = Liste.get(i);
							if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
								serveur.ListeJoueurEnAttenteElo.remove(Joueur);
								Envoie("CxINFO#suppr de ListeJoueurEnAttenteElo");
							}
						}
					}
					return;
				}
				

				// Sans Tir Forteresse
				if (CodeCmd.equals("tir")) {
					if (PartieEnCours != null && PartieEnCours.Forto && ((Forteresse)PartieEnCours.getJeu()).Dessin) {
						if (Commande.length==2) {
							int valueTirOff = Integer.parseInt(Commande[1]);
							if(valueTirOff >= 0 && valueTirOff <= 3) {
								String Message = "CxINFO#Les tirs sont désormais ";

								switch(valueTirOff) {
								case 0:
									PartieForteresse.SansTirRouge = true;
									PartieForteresse.SansTirBleu = true;
									Message +="désactivés pour les 2 équipes.";
									break;
								case 1:
									PartieForteresse.SansTirRouge = true;
									PartieForteresse.SansTirBleu = false;
									Message +="désactivés uniquement pour l'équipe Rouge.";
									break;
								case 2:
									PartieForteresse.SansTirRouge = false;
									PartieForteresse.SansTirBleu = true;
									Message +="désactivés uniquement pour l'équipe Bleu.";
									break;
								default:
									PartieForteresse.SansTirRouge = false;
									PartieForteresse.SansTirBleu = false;
									Message +="activés pour les 2 équipes.";
									break;
								}
								serveur.Envoie(PartieForteresse.ListeJoueur, Message);   
							}
							else {								
								valueTirOff = 3;
								valueTirOff -= (PartieForteresse.SansTirRouge?2:0);
								valueTirOff -= (PartieForteresse.SansTirBleu?1:0);
								Envoie("CxINFO#Commande pour désactiver les tirs sur les adversaires\n"
										+"/tir {0 1 2 3} - État = "+valueTirOff+"\n"
										+ "0 pour tous, 1 pour les rouges, 2 pour les bleus, 3 pour aucun.");
							}
						} else {
							int valueTirOff = 3;
							valueTirOff -= (PartieForteresse.SansTirRouge?2:0);
							valueTirOff -= (PartieForteresse.SansTirBleu?1:0);
							Envoie("CxINFO#Commande pour désactiver les tirs sur les adversaires\n"
									+"/tir {0 1 2 3} - État = "+valueTirOff+"\n"
									+ "0 pour tous, 1 pour les rouges, 2 pour les bleus, 3 pour aucun.");
						}
					}
					else {
						Envoie("CxINFO#Vous ne pouvez pas utilisé cette commande en dehors d'un salon dessin/rally.");
					}
					return;
				}
				// Supprimer la description
				if (CodeCmd.equals("description")) {
					if (Commande.length > 1) {
						String Cible = Commande[1];
						serveur.BOITE.Requete(Boite.SUPPRIMER_DESCRIPTION, this, Cible);
						serveur.BOITE.Requete(Boite.LOG_SANCTION, this, Cible, "-", "Description éditée", "noté", 0);
					}
					return;
				}
				// Liste afk
				if (CodeCmd.equals("antiafk")) {
					String recherche = "";
					int Nb = serveur.ListeJoueur.size();
					long TempsMax = 90;
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.PartieEnCours!=null) {
							long Temps = (System.currentTimeMillis() - Joueur.TempsZero) / 60000;
							if (Temps > TempsMax) {
								recherche += Joueur.NomJoueur + " : " + Temps +" sur " + Joueur.Salon()+"\n";
								TempsMax = Temps;
							}
						}
					}
					Envoie("CxINFO#Temps de connexion au salon (en min) :\n" + recherche);
					return;
				}
				// Copie password
				if (CodeCmd.equals("copie")) {
					LogCommande(CHAINE);  
					if (Commande.length == 3) {  
						String Demandeur = Commande[1];
						String Cible = Commande[2];    
						if (Demandeur.length()>2 && Cible.length()>2) {
							serveur.BOITE.Requete(Boite.COPIE_PASSWORD, this, Demandeur, Cible);
						}
					} else {
						Envoie("CxINFO#/copie demandeur cible");                                            
					}
					return;
				}
				// Reset temps
				if (CodeCmd.equals("temps_moins")) {
					LogCommande(CHAINE);  
					if (Commande.length == 3) {  
						String Cible = Commande[1];
						int Temps = Integer.parseInt(Commande[2]);
						if (Cible.length()>2) {
							serveur.BOITE.Requete(Boite.TEMPS_MOINS, this, Cible, Temps);
						}
					}
					return;
				}

				// Changer question grade 9
				if (CodeCmd.equals("qgrade9")) {
					if (Commande.length == 1 || CHAINE.length() <= 8) {
						serveur.QuestionGrade9 = "";
					} else {
						serveur.QuestionGrade9 = CHAINE.substring(8);
					}
					Envoie_Info("Nouvelle question : " + serveur.QuestionGrade9);
					return;
				}

				// Changer reponse grade 9
				if (CodeCmd.equals("rgrade9")) {
					if (Commande.length == 1 || CHAINE.length() <= 8) {
						serveur.ReponseGrade9 = "";
					} else {
						serveur.ReponseGrade9 = CHAINE.substring(8);
					}
					Envoie_Info("Nouvelle réponse : " + serveur.ReponseGrade9);
					return;
				}

				// Mode super modo
				if (CodeCmd.equals("sm")) {
					AutorisationSuperModo = !AutorisationSuperModo;
					if (AutorisationSuperModo) {
						Envoie("CxINFO#" + NomJoueur + " vient de passer en mode SuperModo.");
					} else {
						Envoie("CxINFO#" + NomJoueur + " n'est plus en mode SuperModo.");
					}
					return;
				}

				// Mute chat principal déf
				if (CodeCmd.equals("mutecp")) {
					if (Commande.length>1) {
						Mutecp(Commande[1]);
						serveur.BOITE.Requete(Boite.MUTECP, this, Commande[1]);
					}
					return;
				}

				// Mute chat principal
				if (CodeCmd.equals("minimutecp")) {
					if (Commande.length>1) {
						String Cible = Commande[1];
						Mutecp(Cible);
					}
					return;
				}

				// Demute chat principal
				if (CodeCmd.equals("unmutecp")) {
					if (Commande.length>1) {
						String Nom = Commande[1];
						serveur.ListeMuteChatPrincipal.remove(Nom.toLowerCase());
						Joueur Joueur = serveur.Joueur(Nom);
						if (Joueur != null) {
							Joueur.MuteChatPrincipal = false;
						}
						serveur.BOITE.Requete(Boite.DEBAN, this, Nom, "mutecp");
						serveur.Message_Modo(NomJoueur + " vient de démute " + Nom + " sur le chat principal.", true);
						Commande("note " +Nom + " Démutecp.");

					}
					return;
				}

				// Arbitre
				if (CodeCmd.equals("arb")) {
					AutorisationArbitre = !AutorisationArbitre;
					Envoie("CxINFO#Arbitre " + AutorisationArbitre);
					return;
				}
				// ANIMATION
				// Image
				if (CodeCmd.equals("images")) {
					LogCommande(CHAINE);
					serveur.Envoie_Chat(this, serveur.ListeJoueurSansPartie, "CxM#<img src='" + Commande[1] + "' /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br />#" + NomJoueurModifié);
					serveur.Envoie_Chat(this, serveur.ListeJoueurEnAttenteElo, "CxM#<img src='" + Commande[1] + "' /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br />#" + NomJoueurModifié);
					
					return;
				}

				// Musique
				if (CodeCmd.equals("musiques")) {
					LogCommande(CHAINE);
					serveur.Envoie(JoueursPartie(), "CxMu#" + Commande[1]);
					return;
				}
				if (CodeCmd.equals("stop")) {
					serveur.Envoie(JoueursPartie(), "CxMu##");
					return;
				}

				// Fermer un salon
				if (CodeCmd.equals("close")) {
					if (PartieEnCours == null) {
						// chat principal
						serveur.ChatFerme = !serveur.ChatFerme;
						if (serveur.ChatFerme) {
							serveur.EnvoieCP("CxF");
							serveur.Message_Modo(NomJoueur + " vient de fermer le chat principal.", false);
						} else {
							serveur.Message_Modo(NomJoueur + " vient de réactiver le chat principal.", false);
						}
					} else {
						// salon
						PartieEnCours.ChatFerme = !PartieEnCours.ChatFerme;
						if (PartieEnCours.ChatFerme) {
							serveur.Envoie(PartieEnCours.getLsJoueurPartie(), "CxF");
							serveur.Message_Modo(NomJoueur + " vient de fermer le chat du salon : " + PartieEnCours.NomPartie, false);
						} else {
							serveur.Message_Modo(NomJoueur + " vient de réactiver le chat du salon : " + PartieEnCours.NomPartie, false);
						}
					}
					return;
				}
				
				// Muet
				if (CodeCmd.equals("mute")) {
					boolean ModeSilencieux = Commande[0].equals("MUTE");
					if (Commande.length > 1) {
						String Cible = Commande[1];
						String Raison = ExtraireRaison(3, Commande);
						long TempsMute = 1;
						if (Commande.length > 2) {
							TempsMute = Long.parseLong(Commande[2]);
						}
						serveur.Mute_Joueur(this, Cible, TempsMute * 3600000, "0", Raison, ModeSilencieux);
						Log_Mute(Cible, TempsMute, Raison);
					}
					return;
				}
				// Super Mute
				if (CodeCmd.equals("mumute")) {
					LogCommande(CHAINE);
					if (Commande.length < 2) {
						return;
					}
					String JoueurCible = Commande[1];
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.NomJoueur.equalsIgnoreCase(JoueurCible)) {
							long Temps = (Commande.length == 3 ? Integer.parseInt(Commande[2]) : 1);
							Joueur.TempsMuet = (Calendar.getInstance().getTimeInMillis() / 1000) + (Temps * 3600);
							Envoie("CxMUTE#1#" + Joueur.NomJoueur + "#" + Temps);
							return;
						}
					}
					return;
				}
				// Ban
				if (CodeCmd.equals("ban")) {
					boolean ModeSilencieux = Commande[0].equals("BAN");
					if (Commande.length > 1) {
						String Cible = Commande[1];
						long Temps = 1;
						if (Commande.length > 2) {
							Temps = Long.parseLong(Commande[2]);
						}
						String Raison = ExtraireRaison(3, Commande);
						Log_Ban(Cible, Temps, Raison);
						serveur.Ban_Joueur(this, Cible, Temps * 3600000, "0", Raison, ModeSilencieux, false);
					}
					return;
				}
				
				// Chucho modo
				if (CodeCmd.equals("cmodo")) {
					if (Commande.length > 2) {
						Joueur JoueurCible = serveur.Joueur(Commande[1]);
						if (JoueurCible != null) {
							String Msg = ExtraireRaison(2, Commande);
							Envoie("CxM#" + JoueurCible.NomJoueur + "#" + Msg + "#" + NomJoueur);
							ArrayList<Joueur> ListeJoueurCible = new ArrayList<Joueur>();
							ListeJoueurCible.add(JoueurCible);
							serveur.Envoie_Chat(this, ListeJoueurCible, "CxM#" + JoueurCible.NomJoueur + "#" + Msg + "#Modérateur");
						}
					}
					return;
				}

				// /clearsig nettoyage
				if (CodeCmd.equals("clearsig")) {
					LogCommande(CHAINE);
					serveur.ListeSignal.clear();
					Envoie("CxINFO#Signalements supprimés");
					return;
				}
				
				// Update profil joueur connecté
				if (CodeCmd.equals("upprofil")) {
					if (Commande.length >= 2) {
						serveur.BOITE.Requete(Boite.UPDATE_PROFIL_EN_LIGNE, this, Commande[1]);
					} else {
						Envoie("CxINFO#Il faut spécifier un pseudo.");
					}

					return;
				}
				
				// Kick
				if (CodeCmd.equals("kick")) {
					if (Commande.length >= 2) {
						boolean ModeSilencieux = Commande[0].equals("KICK");
						String Raison = ExtraireRaison(2, Commande);
						Log_Ban(Commande[1], 0, Raison);
						serveur.Ban_Joueur(this, Commande[1], 0, "0", Raison, ModeSilencieux, false);
					}
					return;
				}
				// Démute
				if (CodeCmd.equals("unmute")) {
					if (Commande.length == 2) {
						Log_Mute(Commande[1], 0, "");
						serveur.Mute_Joueur(this, Commande[1], 0, "0", "",true);
					}
					return;
				}
				
				// Déban
				if (CodeCmd.equals("deban")) {
					if (Commande.length == 2) {
						int IdSanction = Integer.parseInt(Commande[1]);
						serveur.BOITE.Requete(Boite.DEBAN_JOUEUR,this,IdSanction);
					}
					return;
				}

				// Comptes multiples
				if (CodeCmd.equals("multi")) {
					LogCommande(CHAINE);
					if (Commande.length == 2) {
						Joueur JoueurCible = serveur.Joueur(Commande[1]);
						if (JoueurCible == null) {
							Envoie("CxINFO#" + Commande[1] + " n'est pas connecté.");
							return;
						}
							
						String Reponse = "";
						int Nb = serveur.ListeJoueur.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = serveur.ListeJoueur.get(i);
							if (Joueur.AdresseIP.equals(JoueurCible.AdresseIP)) {
								Reponse += Joueur.NomJoueur + " ";
							}
						}
						Envoie("CxINFO#" + Reponse);
					}
					
					return;
				}

				// Comptes multiples offine
				if (CodeCmd.equals("multioff")) {
					LogCommande(CHAINE);
					if (Commande.length == 2) {
						serveur.BOITE.Requete(Boite.MULTI_OFF, this, Commande[1]);
					}
					return;
				}
				
				// Afficher les VIP présent
				if (CodeCmd.equals("ls")) {
					serveur.Affichage_Liste_VIP(this);
					return;
				}
				
				// Bloquer un compte
				if (CodeCmd.equals("bloquer")) {
					LogCommande(CHAINE);  
					if (Commande.length >= 3) {
						String Cible = Commande[1];
						String Raison =  ExtraireRaison(2, Commande);                                            
						serveur.BOITE.Requete(Boite.BLOQUER_COMPTE, this, Cible, Raison);
					} else {
						Message_Info("Usage: /bloquer Joueur Raison");
					}
					return;
				}
				
				// Bloquer un compte + le rendre invisible
				if (CodeCmd.equals("bloquer2")) {
					LogCommande(CHAINE);
					if (Commande.length >= 2) {
						String Cible = Commande[1];                                           
						serveur.BOITE.Requete(Boite.BLOQUER_COMPTE2, this, Cible);
						if (Commande.length > 2) {
							Envoie("CxINFO#Il semblerait que vous souhaitiez ajouter une raison");
						}
					} else {
						Message_Info("Usage: /bloquer2 Joueur");
					}
					return;
				}
				
				
				// DeBloquer un compte
				if (CodeCmd.equals("debloquer")) {
					LogCommande(CHAINE);  
					if (Commande.length >= 2) {
						String Cible = Commande[1];
						serveur.BOITE.Requete(Boite.DEBAN, this, Cible, "ban");
						Commande("note " +Cible + " Compte débloqué");
					}
					return;
				}
				
				// Changer message bienvenue modo
				if (CodeCmd.equals("msgm")) {
					if (Commande.length == 1) {
						serveur.MessageBienvenueModo = "";
						serveur.Message_Modo("Message Modo retiré.", false);
						return;
					}
					serveur.MessageBienvenueModo = "[Message Modo] "+CHAINE.substring(5);
					serveur.Message_Modo(serveur.MessageBienvenueModo, false);
					return;
				}

			} catch (Exception e) {
				Envoie("CxM#Je n'ai pas pu exécuter cette commande monsieur le Modo :'(#<font color='ED67EA'>[serveur]</font>");
			}
		}
		
		if (AutorisationArbitre || (AutorisationModoForum && !AutorisationRecrueForum)) {
			// Afficher les arbitres présents
			if (CodeCmd.equals("ls")) {
				serveur.Affichage_Liste_Arbitre(this);
				return;
			}
		}
		

		if (AutorisationModo || AutorisationSuperArbitre || AutorisationHelper) {

			// Supprimer votes ban
			if (CodeCmd.equals("sauver")) {
				if (Commande.length == 2) {
					Joueur Cible = serveur.Joueur(Commande[1]);
					if (Cible!=null) {
						Cible.DemandeBanIP.clear();
						Cible.DemandeBan.clear();
						Envoie("CxINFO#Votes ban supprimés pour " + Cible.NomJoueur);
					}

				}
				return;
			}

			// Liste /bannir
			if (CodeCmd.equals("listeban")) {
				if (Commande.length == 2) {
					Joueur Cible = serveur.Joueur(Commande[1]);
					if (Cible!=null) {
						Envoie("CxINFO#" + Cible.DemandeBan.toString() + " ont voté le ban de " + Cible.NomJoueur);
					}
				}
				return;
			}
			
			if (CodeCmd.equals("note")) {
				if (Commande.length > 1) {
					String NomCible = Commande[1];
					String Message = ExtraireRaison(2, Commande);
					serveur.BOITE.Requete(Boite.LOG_SANCTION, this, NomCible, "-", Message, "noté", 0);
					String Phrase = "Note sur " + NomCible + " : " + Message;
					if (AutorisationSuperArbitre || AutorisationHelper) {
						Envoie("CxINFO#"+Phrase);
					}
				}
				return;
			}
			
			// Historique des sanctions
			if (CodeCmd.equals("histo")) {
				if (Commande.length > 1) {
					String NomCible = Commande[1];
					String IP = "";
					serveur.BOITE.Requete(Boite.DEMANDE_HISTO, this, NomCible, IP);
				}
				return;
			}
		}

		if (AutorisationModo || AutorisationHelper || AutorisationDev) {
			// Changer de pseudo
			if (CodeCmd.equals("cn")) {
				if (Commande.length == 1) {
					String listeNomModifié = "";
					int nbPseudoModifié = 0;
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur j1 = serveur.ListeJoueur.get(i);
						if (j1.NomModifié) {
							listeNomModifié+=j1.NomJoueur+" = "+ j1.NomJoueurModifié + "\n";
							nbPseudoModifié++;
						}
					}
					if (nbPseudoModifié == 0) {
						Envoie("CxINFO#Aucun nom modifié.");
					}
					else {
						listeNomModifié = listeNomModifié.substring(0, listeNomModifié.length()-1);
						Envoie("CxINFO#Liste des noms modifiés :\n"+listeNomModifié);
					}					
				}
				else if (Commande.length > 1) {
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
							if (Commande.length > 2) {
								Joueur.NomJoueurModifié = Commande[2];
							} else {
								Joueur.NomJoueurModifié = Joueur.NomJoueur;
							}
							int NbC = Commande.length;
							for (int z = 3; z < NbC; z++) {
								Joueur.NomJoueurModifié += " " + Commande[z];
							}
							Joueur.NomModifié = !Joueur.NomJoueurModifié.equals(Joueur.NomJoueur);
							serveur.Message_Modo(Commande[1] + " parlera maintenant sous le nom de [" + Joueur.NomJoueurModifié + "]", false);
							return;
						}
					}
				}
				return;
			}
			// Forcer à parler en Minuscule
			if (CodeCmd.equals("maj")) {
				if (Commande.length > 1) {
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
							Joueur.ToutEnMin = !Joueur.ToutEnMin;
							return;
						}
					}
				}
				return;
			}
			// Changer la couleur du pseudo
			if (CodeCmd.equals("ccp")) {
				if (Commande.length > 1) {
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
							if (Commande.length < 3) {
								Joueur.CouleurPseudo = false;
							} else {
								Joueur.CouleurPseudo = true;
								Joueur.CouleurPseudoValeur = Commande[2];
							}
							return;
						}
					}
				}
				return;
			}
			// Changer la couleur des message
			if (CodeCmd.equals("ccm")) {
				if (Commande.length > 1) {
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.NomJoueur.equalsIgnoreCase(Commande[1])) {
							if (Commande.length < 3) {
								Joueur.CouleurMessage = false;
							} else {
								Joueur.CouleurMessage = true;
								Joueur.CouleurMessageValeur = Commande[2];
							}
							return;
						}
					}
				}
				return;
			}
			// Enlever les animations
			if (CodeCmd.equals("reset")) {
				int Nb = serveur.ListeJoueur.size();
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = serveur.ListeJoueur.get(i);
					Joueur.ToutEnMin = false;
					Joueur.ToutEnMaj = false;
					Joueur.CouleurMessage = false;
					Joueur.CouleurPseudo = false;
					Joueur.NomJoueurModifié = Joueur.NomJoueur;
					Joueur.NomModifié = false;
				}
				serveur.Message_Modo("Reset des modifications de noms sur le chat principal effectué.", false);
				return;
			}
		}

		// Chucho modo
		if ((CodeCmd.equals("cmodo") && (AutorisationModoForum || AutorisationHelper || AutorisationSuperArbitre))
				|| (CodeCmd.equals("carb") && AutorisationArbitre && false)) {
			if (Commande.length > 2) {
				Joueur JoueurCible = serveur.Joueur(Commande[1]);
				if (JoueurCible != null) {
					String Msg = ExtraireRaison(2, Commande);
					String Emetteur = NomJoueur;
					if (AutorisationHelper || AutorisationSuperArbitre || AutorisationArbitre) {
						Emetteur = "Arbitre";
					}
					Envoie("CxM#" + JoueurCible.NomJoueur + "#" + Msg + "#" + NomJoueur);
					ArrayList<Joueur> ListeJoueurCible = new ArrayList<Joueur>();
					ListeJoueurCible.add(JoueurCible);
					serveur.Envoie_Chat(this, ListeJoueurCible, "CxM#" + JoueurCible.NomJoueur + "#" + Msg + "#" + Emetteur);
					System.out.println(NomJoueur + " " + CHAINE);
				}
			}
			return;
		}


		if (AutorisationArbitre || AutorisationModo || AutorisationModoForum || AutorisationDev) {
			// Canal arbitre
			if (CodeCmd.equals("a") && (!AutorisationRecrueForum || AutorisationArbitre)) {
				if (CHAINE.length()>2) {
					if (NomCanalArbitre == null) {
						serveur.Message_Arbitre("[" + NomJoueur + "] " + CHAINE.substring(2), false);
					} else {
						serveur.Message_Arbitre("[" + NomCanalArbitre + "] " + CHAINE.substring(2), false);
					}
				}
				return;
			}

			// Canal AV forum
			if (CodeCmd.equals("f")) {
				if (CHAINE.length()>2) {
					serveur.Avertissement_ModoForum("[" + (NomCanalForum==null?NomJoueur:NomCanalForum) + "] " + CHAINE.substring(2), false);
				}
				return;
			}

			// Changer de pseudo sur le canal F
			if (CodeCmd.equals("cpf")) {
				if (Commande.length == 2) {
					NomCanalForum = "*" + Commande[1];
				} else {
					NomCanalForum = NomJoueur;
				}
				Envoie("CxINFO#Nouveau pseudo : " + NomCanalForum);
				return;
			}

			// Masquer Canal arbitre
			if (CodeCmd.equals("sa") && (!AutorisationRecrueForum || AutorisationArbitre)) {
				AccepteCanalA = !AccepteCanalA;
				if (AccepteCanalA) {
					Envoie("CxINFO#Le canal VIP est maintenant affiché.");
				} else {
					Envoie("CxINFO#Le canal VIP est maintenant masqué.");
				}
				return;
			}

			// Message modo
			if (CodeCmd.equals("mp") || CodeCmd.equals("bla")) {
				if(CHAINE.length() > CodeCmd.length()){
					String Message = CHAINE.substring(CodeCmd.length() + 1);
					serveur.Message_Modo("[" + NomJoueur + "] " + Message, false);
					if (!AutorisationModo) {
						Envoie("CxINFO#Message envoyé : " + Message);
					}
				}
				return;
			}

			// Uptime
			if (CodeCmd.equals("uptime")) {
				Envoie("CxINFO#serveur up depuis : " + serveur.LancementServeur);
				return;
			}

		}
		if (AutorisationModo || AutorisationModoCartes || AutorisationDev) {
			if (CodeCmd.equals("restore")) {
				if (Commande.length > 1) {
					String Code = Commande[1].replaceAll("@", "");
					if (isLong(Code)) {
						serveur.BOITE.Requete(Boite.RESTORE_MAP, this, Code);
					}
				}
				return;
			}

			// Suppression record
			if (CodeCmd.equals("record")) {
				/*if (PartieEnCours != null && PartieEnCours.Aaaah && Commande.length == 1) {
					PartieEnCours.PartieAaaah.InfoCarteCourante.Record = 120000;
					PartieEnCours.PartieAaaah.InfoCarteCourante.Recordman =  "-";
					PartieEnCours.PartieAaaah.InfoCarteCourante.Mara_Record =  120000;
					PartieEnCours.PartieAaaah.InfoCarteCourante.Mara_Recordman =  "-";
					PartieEnCours.PartieAaaah.InfoCarteCourante.Ghost =  "";
					serveur.BOITE.Requete(Boite.SUPPR_RECORD_MAP, this, PartieEnCours.PartieAaaah.InfoCarteCourante.Id.toString());
					LogCommande("Record " + PartieEnCours.PartieAaaah.InfoCarteCourante.Id);
					Envoie("CxINFO#Record supprimé.");
				} else */
				if(Commande.length == 2) {
					String Code = Commande[1].replaceAll("@", "");
					
					try {
						Carte Map = serveur.Aaaah_ListeCarte.get(Long.valueOf(Code));
						if (Map == null) {
							Envoie("CxINFO#La carte " + Commande[1] + " n'existe pas.");
						} else {
							Map.Record = 120000;
							Map.Recordman =  "-";
							Map.Mara_Record =  120000;
							Map.Mara_Recordman =  "-";
							Map.Ghost =  "";
							serveur.BOITE.Requete(Boite.SUPPR_RECORD_MAP, this, Map.Id.toString());
							LogCommande("Record " + Map.Id);
							Envoie("CxINFO#Record supprimé.");
						}
					} catch (Exception e) {
						Envoie("CxINFO#Le code doit être un nombre.");
					}
				}
				return;
			}
		}

		if (AutorisationArbitre || AutorisationModo || AutorisationModoCartes || AutorisationRespoAaaahRecrue || AutorisationDev)
		{
			if (CodeCmd.equals("flag")) {
				try {
					if (Commande.length > 1) {
						String Flag = Commande[1];
						String Code = "0";
						if (Commande.length > 2) {
							Code = Commande[2].replaceAll("@", "");
							if (!isLong(Code)) {
								Envoie("CxINFO#Usage : /flag ng/defilante/fight/normal/ms/rally [code]");
								return;
							}
						} else if (PartieEnCours!=null && PartieEnCours.PAaaah) {
							Code = String.valueOf(((Aaaah)PartieEnCours.getJeu()).MondeEnCours);
						}
						Long IntCode = Long.valueOf(Code);
						if (IntCode < 1000) {
							return;
						}
						int IntFlag = Carte.MODE_NORMAL;
						if (Flag.equalsIgnoreCase("ng")) {
							IntFlag = Carte.MODE_NG;
						} else  if (Flag.equalsIgnoreCase("defilante")) {
							IntFlag = Carte.MODE_DEF;
						} else  if (Flag.equalsIgnoreCase("rally")) {
							IntFlag = Carte.MODE_RALLY;
						} else if (Flag.equalsIgnoreCase("fight")) {
							IntFlag = Carte.MODE_FIGHT;
						} else if (Flag.equalsIgnoreCase("normal")) {
							IntFlag = Carte.MODE_NORMAL;
						} else if (Flag.equalsIgnoreCase("ms")) {
							IntFlag = Carte.MODE_MS;
						} else {
							Envoie("CxINFO#Usage : /flag ng/defilante/fight/normal/ms/rally [code]");
							return;
						}
						Envoie("CxINFO#Flag de @" + Code  + " : " + Flag);

						Carte Map = serveur.Aaaah_ListeCarte.get(IntCode);
						Map.Mode = IntFlag;
						LogCommande("flag " + Flag + " " + Code);
						serveur.BOITE.Requete(Boite.FLAG_MAP, this, Code, IntFlag);
					} else {
						if (PartieEnCours!=null && PartieEnCours.PAaaah) {
							Carte Map = serveur.Aaaah_ListeCarte.get(((Aaaah)PartieEnCours.getJeu()).MondeEnCours);
							if (Map!=null) {
								Envoie("CxINFO#Flag : " + Map.ModeMap());
							}
						}
					}
				} catch (Exception e) {
				}
				return;
			}
			
			// Suppression carte Aaaah
			if (CodeCmd.equals("suppr")) {
				try {
					String logChaine=CHAINE;
					long Monde = ((Aaaah)PartieEnCours.getJeu()).MondeEnCours;
					if (Commande.length>1) {
						Monde = Long.valueOf(Commande[1].replace("@", ""));
					}
					else {
						logChaine+=" "+Long.toString(Monde);
					}
					if (Monde > 1000) {
						LogCommande(logChaine);
						Envoie("CxINFO#Carte " + Monde + " supprimée.");
						serveur.BOITE.Requete(Boite.DELETE_MAP, this, Long.toString(Monde), "R");
					}
				} catch (Exception e) {
				}
				return;
			}
		}
		
		if (AutorisationArbitre || AutorisationModo || AutorisationModoCartes || AutorisationDev) {

			if (CodeCmd.equals("votes")) {
				try {
					String LogChaine = CHAINE;
					String Code = "0";
					if (Commande.length > 1) {
						Code = Commande[1].replaceAll("@", "");
						if (!isLong(Code)) {
							Envoie("CxINFO#Usage : /votes [code]");
							return;
						}
					} else if (PartieEnCours != null && PartieEnCours.PAaaah) {
						Code = String.valueOf(((Aaaah)PartieEnCours.getJeu()).MondeEnCours);
						LogChaine+=" "+Code;
					}
					Long IntCode = Long.valueOf(Code);
					if (IntCode < 1000) {
						return;
					}
					LogCommande(LogChaine);
					Envoie("CxINFO#Votes de @" + Code  + " supprimés");
					Carte Map = serveur.Aaaah_ListeCarte.get(IntCode);
					Map.Votes = 0;
					Map.Pour = 0;
					serveur.BOITE.Requete(Boite.SUPPR_VOTES_MAP, this, Code);

				} catch (Exception e) {
				}
				return;
			}

		}
		
		if (AutorisationModo || AutorisationModoForum) {
			// Temps fin de sanction
			if (CodeCmd.equals("fin")) {
				if (Commande.length == 2) {
					int IdSanction = Integer.parseInt(Commande[1]);
					serveur.BOITE.Requete(Boite.FIN_SANCTION,this,IdSanction);
				}
				return;
			}
		}

		if (AutorisationArbitre || AutorisationModo || AutorisationModoCartes) {

			if (CodeCmd.equals("clearmdm")) {
				LogCommande(CHAINE);
				serveur.ListeSignalMap.clear();
				Envoie("CxINFO#Signalements mdm supprimés");
				return;
			}
			if (CodeCmd.equals("sigmdm")) {
				StringBuilder Msg = new StringBuilder();
				int NbSignaux = serveur.ListeSignalMap.size();
				for (int i=0;i<NbSignaux;i++) {
					SignalementMap Signal = serveur.ListeSignalMap.get(i);
					Msg.append(Signal.toString(this));
					Msg.append("\n");
				}

				Envoie("CxHSF#"+Msg.toString());
				return;
			}
			if (CodeCmd.equals("sigdelete")) {
				if (Commande.length == 2) {
					long NumMap;
					try {
						if (Commande[1].charAt(0) == '@') {
							Commande[1] = Commande[1].substring(1);
						}
						NumMap = Long.valueOf(Commande[1]);
					} catch (Exception e) {
						Envoie("CxINFO#/sigdelete CODE pour supprimer tous les signalements de la map CODE");
						return;
					}
					StringBuilder Msg = new StringBuilder("CxINFO#Liste du/des signalement(s) supprimé(s) :\n");
					for(int i = serveur.ListeSignalMap.size() - 1; i >= 0; --i) {
						if (serveur.ListeSignalMap.get(i).carte.Id == NumMap) {
							SignalementMap Signal = serveur.ListeSignalMap.get(i);
							Msg.append(Signal.toString(this));
							Msg.append("\n");
							serveur.ListeSignalMap.remove(i);
						}
					}
					Envoie(Msg.toString());
				} else {
					Envoie("CxINFO#/sigdelete CODE pour supprimer tous les signalements de la map CODE");
				}
				return;
			}

			// Carte permanente Aaaah
			if (CodeCmd.equals("perma")) {
				try {
					Carte InfoCarte = serveur.Aaaah_ListeCarte.get(((Aaaah)PartieEnCours.getJeu()).MondeEnCours);
					int Perma = 1;
					if (InfoCarte.Perma==1) {
						Perma = 0;
						Envoie("CxINFO#Cette map n'est désormais plus permanente.");
					}
					InfoCarte.Perma = Perma;
					LogCommande(CHAINE);
					Envoie("IdPe#" + InfoCarte.Votes + "#" + InfoCarte.Pour + "#" + InfoCarte.Perma);
					serveur.BOITE.Requete(Boite.PERMA_MAP, this, String.valueOf(((Aaaah)PartieEnCours.getJeu()).MondeEnCours), Perma);
				} catch (Exception e) {
				}
				return;
			}

		}

		if (AutorisationArbitre || AutorisationModo) {

			// /notification de signalement
			if (CodeCmd.equals("notif")) {
				SignalementModo = !SignalementModo;
				Envoie("CxINFO#Signalements " + (SignalementModo?"activés":"désactivés") + ".");
				return;
			}

			// /signalements
			if (CodeCmd.equals("sig")) {
				if (Commande.length == 1) {
					ListeSignalements(1);
				} else if (isInteger(Commande[1])) {
					ListeSignalements(Integer.valueOf(Commande[1]));
				}
				return;
			}
			// /signalements
			if (CodeCmd.equals("signal")) {
				if (Commande.length == 1) {
					ListeSignalements(5);
				} else if (isInteger(Commande[1])) {
					ListeSignalements(Integer.valueOf(Commande[1]));
				}
				return;
			}

			// /signalements
			if (CodeCmd.equals("signalements")) {
				if (Commande.length == 1) {
					ListeSignalements(100,true);
				} else if (isInteger(Commande[1])) {
					ListeSignalements(Integer.valueOf(Commande[1]),true);
				}
				return;
			}

			// Editer nom salon
			if (CodeCmd.equals("nom_salon")) {
				if (Commande.length > 1) {
					String Recherche = ExtraireRaison(1, Commande);
					if (Recherche.length()<2) {
						Envoie("CxINFO#Motif trop court.");
					} else {
						int NbPartie = serveur.ListePartie.size();
						for (int i = 0; i < NbPartie; i++) {
							Partie Partie = serveur.ListePartie.get(i);
							if (Partie.NomPartie.contains(Recherche)) {
								Envoie("CxINFO#Nom de salon édité : " + Partie.NomPartie + "\nCréateur du salon : " + Partie.CreateurOriginel);
								
								// refresh liste partie
								serveur.EnvoieCP("CxRLP#"+Partie.CodePartie);
								Partie.NomPartie = "Edité";
								return;
							}
						}
						Envoie("CxINFO#Motif introuvable.");
					}
				}
				return;
			}

			// anti triche
			if (CodeCmd.equals("triche")) {
				if (Commande.length > 1) {
					Joueur Cible = serveur.Joueur(Commande[1]);
					if (Cible != null) {
						serveur.Checkeur = this;
						Cible.Envoie("CxTriche");
					}

				}
				return;
			}
		}

		if (AutorisationArbitre || AutorisationModo || AutorisationTournoiAaaah || AutorisationTournoiForto) {
			// voir maraaathon
			if (CodeCmd.equals("mara")) {
				Envoie("CxINFO# Infos :" +
						"\nActivé = " + Mara_Activé +
						"\nStats actives = " + (PartieEnCours!=null&&PartieEnCours.PAaaah?((Aaaah)PartieEnCours.getJeu()).MaraActive:"") +
						"\nTempsBan = " + Mara_TempsBan +
						"\nTempsJeu = " + Mara_TempsJeu +
						"\nGuide_JoueurSauvés = " + Mara_Guide_JoueurSauvés +
						"\nGuide_JoueurGuidés = " + Mara_Guide_JoueurGuidées +
						"\nGuide_PartiesGuidées = " + Mara_Guide_PartiesGuidées +
						"\nRun_ArrivéesPremier = " + Mara_Run_ArrivéesPremier +
						"\nRun_PartiesJouées = " + Mara_Run_PartiesJouées +
						"\nFight_Tués = " + Mara_Fight_Tués +
						"\nFight_Survivor = " + Mara_Fight_Survivor +
						"\nFight_PartiesJouées = " + Mara_Fight_PartiesJouées +
						"\nDéfilantes_Tués = " + Mara_Défilantes_Tués +
						"\nDéfilantes_Survivor = " + Mara_Défilantes_Survivor +
						"\nDéfilantes_PartiesJouées = " + Mara_Défilantes_PartiesJouées +
						"\nRally_TempsTotal = " + Mara_Rally_TempsTotal +
						"\nRally_PartiesJouées = " + Mara_Rally_PartiesJouées +
						"\nRally_NombreRecord = " + Mara_Rally_NombreRecord
						);
				return;
			}

			// voir maraaathon
			if (CodeCmd.equals("marb")) {
				Envoie("CxINFO# Infos :" +
						"\nActivé = " + Mara_Activé +
						"\nStats actives = " + (PartieEnCours!=null&&PartieEnCours.Bouboum?((Bouboum)PartieEnCours.getJeu()).SalonOffi:"") +
						"\nTempsBan = " + Mara_TempsBan +
						"\nTempsJeu = " + Mara_TempsJeu +
						"\nMara_Boum_PartiesJouées = " + Mara_Boum_PartiesJouées +
						"\nMara_Boum_PartiesGagnées = " + Mara_Boum_PartiesGagnées +
						"\nMara_Boum_Adversaires = " + Mara_Boum_Adversaires +
						"\nMara_Boum_Tués = " + Mara_Boum_Tués +
						"\nMara_Boum_Manches = " + Mara_Boum_Manches +
						"\nMara_Boum_Suicides = " + Mara_Boum_Suicides
						);
				return;
			}

		}

		if (AutorisationModoForum || AutorisationModo) {

			// /signalements
			if (CodeCmd.equals("sig") || CodeCmd.equals("sig_forum")) {
				if (Commande.length == 1) {
					ListeSignalementsForum(1);
				} else if (isInteger(Commande[1])) {
					ListeSignalementsForum(Integer.valueOf(Commande[1]));
				}
				return;
			}
			// /signalements
			if (CodeCmd.equals("signal") || CodeCmd.equals("signal_forum")) {
				if (Commande.length == 1) {
					ListeSignalementsForum(5);
				} else if (isInteger(Commande[1])) {
					ListeSignalementsForum(Integer.valueOf(Commande[1]));
				}
				return;
			}

			// /notification de signalement forum
			if (CodeCmd.equals("notif") || CodeCmd.equals("notif_forum")) {
				SignalementModoForum = !SignalementModoForum;
				Envoie("CxINFO#Signalements forum " + (SignalementModoForum?"activés":"désactivés") + ".");
				return;
			}

			// Changer message bienvenue forum
			if (CodeCmd.equals("msgf")) {
				if (Commande.length == 1) {
					serveur.MessageBienvenueForum = "";
					return;
				}
				serveur.MessageBienvenueForum = "[Message Auto] "+CHAINE.substring(5);
				Envoie("CxINFO#"+serveur.MessageBienvenueForum);
				return;
			}

			// /signauxclear nettoyage
			if (CodeCmd.equals("clearsig") || CodeCmd.equals("clearsig_forum")) {
				LogCommande(CHAINE);
				serveur.ListeSignalForum.clear();
				Envoie("CxINFO#Signalements forum supprimés");
				return;
			}

			// /signalements
			if (CodeCmd.equals("signalements") || CodeCmd.equals("signalements_forum")) {
				if (Commande.length == 1) {
					ListeSignalementsForum(100,true);
				} else if (isInteger(Commande[1])) {
					ListeSignalementsForum(Integer.valueOf(Commande[1]),true);
				}
				return;
			}

			if (CodeCmd.equals("histodel")) {
				if (Commande.length > 1) {
					if (!AutorisationRecrue) {
						String Id = Commande[1];
						serveur.BOITE.Requete(Boite.DELETE_HISTORY, this, Id);
					}
				}
				return;
			}

		}
		
		if(CodeCmd.equals("code_rally") || CodeCmd.equals("codef")) {
			if (Commande.length > 1) {
				serveur.BOITE.Requete(Boite.REVERSE_CODE_MAP, this, Commande[1], 0);
			 }
			else {
				Envoie("CxINFO#Aucun code spécifié.");
			}
			return;
		}
		
		if(CodeCmd.equals("recordforto") && (AutorisationArbitre || AutorisationModoCartesRally)) {
			if (Commande.length > 1) {
				serveur.BOITE.Requete(Boite.REVERSE_CODE_MAP, this, Commande[1], 1);
			 }
			else {
				Envoie("CxINFO#Aucun code spécifié.");
			}
			return;
		}
		
		// Commande /stat
		if (CodeCmd.equals("stat")) {
			String statLog;
			if (Commande.length > 1 && !NomJoueur.equalsIgnoreCase(Commande[1])) {
				Joueur LaCible = serveur.Joueur(Commande[1]);
				if (LaCible!=null && (AutorisationAdmin || LaCible.StatsActivées())) {
					statLog = LaCible.NomJoueur + "#" + LaCible.Stats.toFlash();
				} else {
					Envoie("CxINFO#Ce joueur est hors-ligne ou a désactivé les statistiques.");
					return;
				}
			} else {
				statLog = NomJoueur + "#" + Stats.toFlash();
			}
			Envoie("CxStats#"+statLog);
			return;
		}
		
		// Stats elo
		if (CodeCmd.equals("statelo")) {
			if (Commande.length > 2) {
				serveur.BOITE.Requete(Boite.OBTENIR_STATS_ELO_PROFIL, this, Commande[1], Commande[2], 0);
			} else {
				Envoie("CxINFO#Commande incorrect. Elle doit avoir la forme : /statelo Pseudo Jeu*\n*Jeu : Aaaah! = a, Bouboum = b, Forteresse = f");
			}
			return;
		}
		
		// Stats elo saison précédente
		if (CodeCmd.equals("elo")) {
			if (Commande.length > 2) {
				serveur.BOITE.Requete(Boite.OBTENIR_STATS_ELO_PROFIL, this, Commande[1], Commande[2], 1);
			} else {
				Envoie("CxINFO#Commande incorrect. Elle doit avoir la forme : /elo Pseudo Jeu*\n*Jeu : Aaaah! = a, Bouboum = b, Forteresse = f");
			}
			return;
		}
		
		// Demande liste noire
		if (CodeCmd.equals("ln")) {
			Envoie("CxIG#"+ListeJoueursIgnorésChaine);
			return;
		}
		
		// Commande /ignore
		if (CodeCmd.equals("ignore")) {
			if (Commande.length > 1) {
				String JoueurCible = Commande[1];
				if (!PseudoConforme(JoueurCible)) {
					Envoie("CxIg");
					return;
				}
				if (!JoueurCible.equalsIgnoreCase(NomJoueur) && JoueurCible.length() > 0 && JoueurCible.charAt(0) != '*') {
					JoueurCible = JoueurCible.substring(0, 1).toUpperCase() + JoueurCible.substring(1).toLowerCase();

					Joueur Cible = serveur.Joueur(JoueurCible);

					if (ListeJoueursIgnorés.contains(JoueurCible)) {
						// on désignore
						ListeJoueursIgnorés.remove(JoueurCible);
						ListeJoueursIgnorésChaine = ListeJoueursIgnorésChaine.replace("," + JoueurCible, "");
						Envoie("CxIg#" + JoueurCible + "##");
						if (Cible!=null) {
							ListeIPIgnorées.remove(Cible.AdresseIP);
							if (!Cible.ListeJoueursIgnorés.contains(NomJoueur) && AutorisationInscription) {
								// Message pas envoyé au joueur si l'auteur est un invité (éviter les spams)
								Cible.Envoie("CxIg#" + NomJoueur + "###");
							}
							Cible.ListeIgnore.remove(AdresseIP);
						}
					} else {
						// on ignore
						if (ListeJoueursIgnorés.size()>300){
							Envoie("CxINFO#Votre liste noire est pleine. Merci de faire du tri.");
							return;
						} else if (ListeJoueursIgnorés.size()>200){
							Envoie("CxINFO#Votre liste noire est grande. Merci de faire du tri.");
						}
						long Now = System.currentTimeMillis();
						if (Now - LastIgnore < 5000) {
							Message_Info("Merci de patienter quelques secondes avant de pouvoir ignorer à nouveau.");
							return;
						}
						LastIgnore = Now;
						ListeJoueursIgnorés.add(JoueurCible);
						ListeJoueursIgnorésChaine += "," + JoueurCible;
						Envoie("CxIg#" + JoueurCible);
						if (Cible!=null){
							if (!ListeIPIgnorées.contains(Cible.AdresseIP)) {
								ListeIPIgnorées.add(Cible.AdresseIP);
							}
							if (!Cible.ListeJoueursIgnorés.contains(NomJoueur) && AutorisationInscription) {
								Cible.Envoie("CxIg#" + NomJoueur + "#");
							}
							if (!Cible.ListeIgnore.contains(AdresseIP)) {
								Cible.ListeIgnore.add(AdresseIP);
								if (Cible.ListeIgnore.size() > 7) {
									Cible.TempsMuet = (Calendar.getInstance().getTimeInMillis() / 1000) + 43200;
								}
							}
						}
					}

				}
			}

			return;
		}

		if (AutorisationInscription) {
			// Exporter un compte
			if (CodeCmd.equals("exporter_compte")) {
				Envoie("CxINFO#" +serveur.getExportImportCompte().exporterJoueur(this));
				return;
			}
			
			// Saturnaaaahles
			if (serveur.Evénement.Saturnaaaahles()) {
				if (CodeCmd.equals("ejecte")) {
					if (Commande.length > 1) {
						if (Commande[1].equals(NomJoueur)) {
							Envoie("CxM#C'est une très mauvaise idée si vous voulez mon avis !#<font color='ED67EA'>Serveur</font>");
							return;
						} else if (Commande[1].startsWith("*")) {
							Envoie("CxMS#Vous osez vous en prendre à un pauvre invité ?!");
						}
						
						Joueur J = serveur.Joueur(Commande[1]);
						if (J == null) {
							Envoie("CxINFO#Ce joueur n'est pas connecté.");
							return;
						}
						if (J.MessagesEvent) {
							J.Envoie("CxMS#" + NomJoueur + " vous a éjecté du jeu.");
						}
						
						if (serveur.SurvivalLance) {
							if (J.ParticipeSurvivalKick) {
								if (ParticipeSurvivalKick) {
									Envoie("CxMS#Vous avez évincé " + J.NomJoueur + ".");
									J.ParticipeSurvivalKick = false;
									serveur.Envoie(serveur.ListeJoueur, "CxSATOUT#" + J.NomJoueur);
									serveur.CheckFinSurvivalKick();
								} else {
									Envoie("CxMS#Vous avez fait valdinguer " + J.NomJoueur + ".");
								}
								
							} else {
								Envoie("CxINFO#Ce joueur était déjà hors-jeu.");
							}
						} else {
							Envoie("CxMS#Vous avez éjecté " + J.NomJoueur + ".");
							Envoie("CxSATKK#" + J.NomJoueur);
						}
					} else {
						Envoie("CxINFO#/ejecte Pseudo : éjecte Pseudo du jeu.");
					}
					return;
				}
				
				if (CodeCmd.equals("muet")) {
					if (Stats_TempsDeJeu < RestrictionHeure.MUTE_SATURNAAAAHLES) {
						Envoie("CxINFO#Vous devez posséder au moins 100 heures de jeu pour pouvoir utiliser cette commande.");
					} else {
						if (Commande.length > 1) {
							if (Commande[1].equals(NomJoueur)) {
								Envoie("CxM#C'est une très mauvaise idée si vous voulez mon avis !#<font color='ED67EA'>Serveur</font>");
								return;
							} else if (Commande[1].startsWith("*")) {
								Envoie("CxMS#Vous osez vous en prendre à un pauvre invité ?!");
							}
							
							
							Joueur J = serveur.Joueur(Commande[1]);
							if (J == null) {
								Envoie("CxINFO#Ce joueur n'est pas connecté.");
								return;
							}
							
							if (J.MessagesEvent) {
								J.Envoie("CxMS#" + NomJoueur + " vous a mute.");
							}
							
							Envoie("CxMS#Vous avez mute " + J.NomJoueur + ".");
							Envoie("CxSATMT#" + J.NomJoueur);
						} else {
							Envoie("CxINFO#/muet Pseudo : rend muet Pseudo du jeu.");
						}
					}
					return;
				}
				
				if (CodeCmd.equals("banni")) {
					if (Stats_TempsDeJeu < RestrictionHeure.BAN_SATURNAAAAHLES) {
						Envoie("CxINFO#Vous devez posséder au moins 758 heures de jeu pour pouvoir utiliser cette commande.");
					} else {
						if (Commande.length > 1) {
							if (Commande[1].equals(NomJoueur)) {
								Envoie("CxM#C'est une très mauvaise idée si vous voulez mon avis !#<font color='ED67EA'>Serveur</font>");
								return;
							} else if (Commande[1].startsWith("*")) {
								Envoie("CxMS#Vous osez vous en prendre à un pauvre invité ?!");
							}
							
							Joueur J = serveur.Joueur(Commande[1]);
							if (J == null) {
								Envoie("CxINFO#Ce joueur n'est pas connecté.");
								return;
							}
							
							if (J.MessagesEvent) {
								J.Envoie("CxMS#" + NomJoueur + " vous a banni.");
							}
							
							if (serveur.SurvivalLance) {
								if (J.ParticipeSurvivalKick) {
									if (ParticipeSurvivalKick) {
										Envoie("CxMS#Vous avez évincé " + J.NomJoueur + ".");
										J.ParticipeSurvivalKick = false;
										serveur.Envoie(serveur.ListeJoueur, "CxSATOUT#" + J.NomJoueur);
										serveur.CheckFinSurvivalKick();
									} else { // Si on n'est plus en course : plus possible de kick/ban pour le Survival Kick
										Envoie("CxMS#Vous avez ratatiné " + J.NomJoueur + ".");
									}
									Envoie("CxSATBN#" + J.NomJoueur);
								} else {
									Envoie("CxINFO#Ce joueur était déjà hors-jeu.");
								}
							} else {
								Envoie("CxMS#Vous avez atomisé " + J.NomJoueur + ".");
								Envoie("CxSATBN#" + J.NomJoueur);
							}
						} else {
							Envoie("CxINFO#/banni Pseudo : banni Pseudo du jeu.");
						}
					}
					return;
				}
				
				if (CodeCmd.equals("secache")) {
					int Nb = serveur.ListeJoueur.size() - 1;
					boolean AuMoinsUnePersonne = false;
					StringBuilder Ls = new StringBuilder("CxINFO#");
					for (int i = Nb; i >= 0; --i) {
						Joueur J = serveur.ListeJoueur.get(i);
						if (J.ParticipeSurvivalKick && ((J.PartieEnCours != null && J.PartieEnCours.EditeurAaaah) || J.Afk)) {
							AuMoinsUnePersonne = true;
							Ls.append(J.NomJoueur + ", ");
						}
					}
					if (!AuMoinsUnePersonne) {
						Ls.append("Personne ne ");
					}
					Ls.append("se cache(nt) !");
					Envoie(Ls.toString());
					return;
				}
				
				if (CodeCmd.equals("survival")) {
					if (!serveur.SurvivalLance) {
						ParticipeSurvivalKick = !ParticipeSurvivalKick;
						Envoie("CxINFO#Participation au Survival Kick : " + (ParticipeSurvivalKick ? "oui" : "non") + ".");
						serveur.Envoie(serveur.ListeJoueur, "CxSATOUT#" + NomJoueur);
					} else {
						Envoie("CxINFO#Le survival kick est déjà en cours. Vous ne pouvez plus changer votre état de participation.");
					}
					return;
				}

			}
			
			if (serveur.Evénement.StValentin()) {
				if (CodeCmd.equals("rompre")) {
					RompreStValentin();
					Envoie("CxINFO#Union annulée.");
					return;
				}
				
				if (CodeCmd.equals("lier")) {
					if (Commande.length > 1) {
						if (NomJoueur.equalsIgnoreCase(Commande[1])) {
							Envoie("CxINFO#Cupidon n'approuve pas ce choix !");
							return;
						}
						
						Joueur J = serveur.Joueur(Commande[1]);
						
						if (J != null) {
							Joueur Ex = serveur.TrouverAmoureux(serveur.ListeJoueur, this);
							
							if (Ex != null && !Ex.Amoureux.equalsIgnoreCase(Commande[1])) {
								/*Ex.Envoie("CxSTVULINK");
								Ex.Envoie("CxINFO#" + NomJoueur + " a rompu votre union.");
								Envoie("CxSTVULINK");*/
								
								RompreStValentin();
							}
							
							Amoureux = J.NomJoueur;
							if (J.Amoureux.equals(NomJoueur)) {
								Envoie("CxINFO#Union établie !");
								Envoie("CxSTVLINK#" + Amoureux);
								J.Envoie("CxINFO#Votre demande d'union a été acceptée !");
								J.Envoie("CxSTVLINK#" + J.Amoureux);
								
								
								// Anti utilisation en tournoi
								if (PartieEnCours != null) {
									for (Joueur Joueur : PartieEnCours.getLsJoueurPartie()) {
										if (Joueur.AutorisationTournoiAaaah || Joueur.AutorisationTournoiForto || Joueur.AutorisationArbitreElo || Joueur.AutorisationTounoiArbitreSecondaire) {
											Joueur.Envoie("CxINFO#" + NomJoueur + " s'est lié à " + J.NomJoueur + ".");
										}
									}
								}
								
								if (J.PartieEnCours != null) {
									for (Joueur Joueur : J.PartieEnCours.getLsJoueurPartie()) {
										if (Joueur.AutorisationTournoiAaaah || Joueur.AutorisationTournoiForto || Joueur.AutorisationArbitreElo || Joueur.AutorisationTounoiArbitreSecondaire) {
											Joueur.Envoie("CxINFO#" + J.NomJoueur + " s'est lié à " + NomJoueur + ".");
										}
									}
								}
								//
							} else if (!((J.ListeJoueursIgnorés != null && J.ListeJoueursIgnorés.contains(NomJoueur)) || (J.ListeIPIgnorées != null && J.ListeIPIgnorées.contains(AdresseIP)))) {
								Envoie("CxINFO#Demande d'union envoyée à " + J.NomJoueur + ".");
								J.Envoie("CxINFO#" + NomJoueur + " vous a envoyé une demande d'union. Utilisez /lier " + NomJoueur + " pour l'accepter !");
							}
						} else {
							Envoie("CxINFO#Ce joueur n'est pas connecté.");
						}
					} else if (!Amoureux.isEmpty()) {
						RompreStValentin();
						Envoie("CxINFO#Union annulée.");
					}
					return;
				}
			}
			
			if (CodeCmd.equals("suivreevent")) {
				MessagesEvent = !MessagesEvent;
				Envoie("CxINFO#Suivis des messages de l'événement : " + (MessagesEvent ? "oui" : "non") + ".");
				return;
			}
			
			// Activer/désactiver l'affichage des récompenses elo
			if (CodeCmd.equals("recelo")) {
				String msg = "CxINFO#Récompenses elo ";
				if (Commande.length > 1) {
					if (Commande[1].equalsIgnoreCase("all")) {
						if (recompenseElo.recompensesActives == RecompenseElo.DESACTIVER_ALL) {
							recompenseElo.recompensesActives = RecompenseElo.ACTIVER;
							msg += "personnelles activées";
						} else {
							recompenseElo.recompensesActives = RecompenseElo.DESACTIVER_ALL;
							msg += "générales désactivées";
						}
						
					}
				} else if (Commande.length == 1){
					if (recompenseElo.recompensesActives != RecompenseElo.ACTIVER) {
						recompenseElo.recompensesActives = RecompenseElo.ACTIVER;
						msg += "personnelles activées";
					} else {
						recompenseElo.recompensesActives = RecompenseElo.DESACTIVER_PERSO;
						msg += "personnelles désactivées";
					}
				}
				Envoie( msg + " (\"/recelo all\" pour toutes les désactiver).");
				Envoie("CxRCMPELO#" + recompenseElo.recompensesActives);
				return;
			}

			// Commande /ami
			if (CodeCmd.equals("ami")) {
				if (Commande.length > 1) {
					String NomAmi = Commande[1];
					AjouterAmi(NomAmi);
				}
				return;
			}

			if (CodeCmd.equals("histomap")) {
				if (Commande.length > 1) {
					String Auteur = Commande[1];
					serveur.BOITE.Requete(Boite.HISTO_MAP, this, Auteur);
				} else {
					serveur.BOITE.Requete(Boite.HISTO_MAP, this, NomJoueur);
				}
				return;
			}
			
			// Commande /avatar
			if (CodeCmd.equals("avatar")) {
				if (serveur.WorkaroundAvatar) {
					String NewAvatar = "";
					if (Commande.length > 1) {
						if (Commande[1].equalsIgnoreCase("blason")) {
							NewAvatar = "_blason";
						} else {
							NewAvatar = Commande[1];
						}
					}
                    serveur.BOITE.Requete(Boite.CHANGEMENT_AVATAR_NEW, this,NewAvatar);
                 }
                return;
			}
			
			if (CodeCmd.equals("blason")) {
				if (Commande.length == 2) { // Mettre le blason de sa team en avatar
					serveur.BOITE.Requete(Boite.SET_BLASON_TEAM, this, Commande[1]);
				} else {
					Envoie("CxINFO#Paramètres incorrects.");
				}
				return;
			}
                       
			// Suppression carte Aaaah
			if (CodeCmd.equals("supprimer")) {
				try {
					if (Commande.length>1) {
						Long Monde = Long.valueOf(Commande[1].replace("@", ""));

						if (Monde > 1000) {
							Carte Carte = serveur.Aaaah_ListeCarte.get(Monde);
							if (Carte!=null && Carte.Auteur.equalsIgnoreCase(NomJoueur)) {
								System.out.println("DelMap " + NomJoueur + " " + AdresseIP + " " + Monde);
								Envoie("CxINFO#Carte supprimée.");
								serveur.BOITE.Requete(Boite.DELETE_MAP, this, String.valueOf(Monde), "S");
							}
						}
					}
				} catch (Exception e) {
				}
				return;
			}

			// changement de pass
			if (CodeCmd.equals("password")) {
				if (AutorisationChgPass.equals("1")) {
					Envoie("CxCHGPASS");
				} else {
					Envoie("CxINFO#Cette commande est actuellement indisponible. Vous pouvez contacter un modérateur directement sur le jeu ou nous contacter par mail en précisant votre pseudo pour en récupérer l'accès. Vous devez être le créateur de ce compte.");
				}
				return;
			}
			
			// Demande de récupération de compte Journée Porte Ouverte
			if (CodeCmd.equals(serveur.CmdJPO)) {
				String Cible;				
				if(AutorisationInscription) {
					if(serveur.JPOEnCours) {
						if (Commande.length == 2) {
							Cible = Commande[1];
							Cible = Cible.substring(0, 1).toUpperCase() + Cible.substring(1).toLowerCase();
	
							if (Stats_TempsDeJeu > RestrictionHeure.DEMANDE_COMPTE_JPO) { //300h
								serveur.BOITE.Requete(Boite.DEMANDE_COMPTE_JPO, this,Cible);
							}
							else {
								Envoie("CxINFO#Il vous faut 300 heures de jeu pour pouvoir réclamer un pseudo.");
							}
						}
						else {
							String Message ="";
							boolean DemandeEnCours = false;
							for(DemandeCompte Demande : serveur.ListeDemandeJPO) {
								if(this.NomJoueur.equals(Demande.Demandeur)) {
									Message = "Demande en cours de traitement pour le compte " + Demande.Cible + ".";
									DemandeEnCours = true;
									break;
								}
							}
							Envoie("CxINFO#Commande pour participer à la journée porte ouverte :"
							+ "\nTapez /"+CodeCmd.toString()+" Pseudo pour formuler une demande de récupération du compte Pseudo."
							+ "\nTapez /"+CodeCmd.toString()+ " " + NomJoueur + " pour formuler une demande de nettoyage de l'historique des sanctions de votre compte."
							+ (DemandeEnCours?"\n"+Message:""));
						}
					}
					else {							
						Envoie("CxINFO#Il n'y a pas de journée porte ouverte à l'heure actuelle.");
					}
				}
				return;
			}
			
			// sans cadeaux (Noël, visibles mais sans effet)
			if (CodeCmd.equals("cadeau") && serveur.Evénement.Noel()) {
				if (PartieEnCours != null && PartieEnCours.Forto) {
					if (((AutorisationModo&&Commande.length==2) || PartieEnCours.Createur.equalsIgnoreCase(NomJoueur))) {
						PartieForteresse.cadeuxActives = !PartieForteresse.cadeuxActives;
						String Message = "CxINFO#Les effets des cadeaux sont désormais "+(PartieForteresse.cadeuxActives?"":"dés")+"activés par le créateur de la partie " + PartieEnCours.Createur+ ".";
						serveur.Envoie(PartieForteresse.ListeJoueur, Message);   
					} else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie de type dessin/rally peut le faire. Actuellement les bombes sont : "+ (PartieForteresse.SansBombe?"désactivées":"activées"));
					}
				}
				return;
			}
			// sans bombes
			if (CodeCmd.equals("bombes")) {
				if (PartieEnCours != null && PartieEnCours.Forto) {
					if (((AutorisationModo&&Commande.length==2) || (PartieForteresse.Dessin && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)))) {
						PartieForteresse.SansBombe = !PartieForteresse.SansBombe;
						String Message = "CxINFO#Les bombes sont désormais "+(PartieForteresse.SansBombe?"interdites":"autorisées")+" par le créateur de la partie " + PartieEnCours.Createur+ ".";
						serveur.Envoie(PartieForteresse.ListeJoueur, Message);   
						if (PartieForteresse.SansBombe) {
							for (Joueur Joueur : PartieForteresse.ListeJoueur) {
								Joueur._Forteresse.Jambombe = 0;
							}
						}
					} else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie de type dessin/rally peut le faire. Actuellement les bombes sont : "+ (PartieForteresse.SansBombe?"désactivées":"activées"));
					}
				}
				return;
			}

			// sans frigo
			if (CodeCmd.equals("frigo")) {
				if (PartieEnCours != null && PartieEnCours.Forto) {
					if (((AutorisationModo&&Commande.length==2) || (PartieForteresse.Dessin && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)))) {
						PartieForteresse.SansFrigo = !PartieForteresse.SansFrigo;
						String Message = "CxINFO#Les frigos sont désormais "+(PartieForteresse.SansFrigo?"désactivés":"activés")+" par le créateur de la partie " + PartieEnCours.Createur+ ".";
						serveur.Envoie(PartieForteresse.ListeJoueur, Message);   
					} else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie de type dessin/rally peut le faire. Actuellement les frigos sont : "+ (PartieForteresse.SansFrigo?"désactivés":"activés") + ".");
					}
				}
				return;
			}
			// affichage des kills des joueurs à leurs déconnexions
			if (CodeCmd.equals("kill")) {
				if (PartieEnCours != null && PartieEnCours.Forto) {
					if (((AutorisationModo&&Commande.length==2) || (PartieForteresse.Dessin && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)))) {
						PartieForteresse.AfficherKills = !PartieForteresse.AfficherKills;
						String Message = "CxINFO#L'affichage des kills à chaque déconnexion est désormais "+(PartieForteresse.AfficherKills?"activé":"désactivé")+" par le créateur de la partie " + PartieEnCours.Createur + ".";
						serveur.Envoie(PartieForteresse.ListeJoueur, Message);   
					} else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie de type dessin/rally peut le faire. Actuellement l'affichage des kills est : " + (PartieForteresse.AfficherKills?"activé":"désactivé") + ".");
					}
				}
				return;
			}
			
			if (CodeCmd.equals("rally")) {
				if (PartieForteresse != null) {
					if (Commande.length>=2) {
						if (((AutorisationModo && Commande.length==3) || (!PartieForteresse.AutoriserChoixEquipe && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)))) {
							forteresse.Playlist playlist = new forteresse.Playlist();
							
							if (Commande[1].equalsIgnoreCase("f")) {
								playlist.setPlaylistOfficielle(forteresse.Playlist.FACILE);
							} else if (Commande[1].equalsIgnoreCase("m")) {
								playlist.setPlaylistOfficielle(forteresse.Playlist.MOYEN);
							} else {
								Envoie_Info("Tapez /rally {f, m} pour charger des rallys {faciles/moyens}. Exemple : /rally f");
								return;
							}
							
							((Forteresse)PartieEnCours.getJeu()).setPlaylist(playlist);
							PartieForteresse.Nouvelle_Partie(-1, null, false);
						} else {
							Envoie_Info("Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie de type dessin/rally sans choix d'équipe peut le faire.");
						}
					} else {
						Envoie_Info("Tapez /rally {f, m} pour charger des rallys {faciles/moyens}. Exemple : /rally f");
					}
					
					return;
				}
			}
			
			// ratio rouge
			if (CodeCmd.equals("ratio")) {
				if (PartieEnCours != null && PartieEnCours.Forto) {
					if (Commande.length>=2) {
						if (((AutorisationModo&&Commande.length==3) || (PartieForteresse.Dessin && !PartieForteresse.AutoriserChoixEquipe && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)))) {
							if (isDouble(Commande[1])) {
								PartieForteresse.RatioRouge = Double.parseDouble(Commande[1]);
								String Message = "CxINFO#"+PartieEnCours.Createur+ " a fixé le ratio de rouge à " + PartieForteresse.RatioRouge + ".";
								serveur.Envoie(PartieForteresse.ListeJoueur, Message);   
							} else {
								Envoie_Info("Le ratio doit être compris entre 0 et 1 avec 3 décimales maximum.");
							}
						} else {
							Envoie_Info("Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie de type dessin/rally sans choix d'équipe peut le faire.");
						}
					} else {
						Envoie_Info("Tapez /ratio x pour changer le pourcentage de joueurs rouges. Exemple : /ratio 0.5 pour avoir moitié de rouges. /ratio 1 pour n'avoir que des rouges.\nLe ratio actuel est " + PartieForteresse.RatioRouge + ".");
					}
				}
				return;
			}
			
			// choix des mondes
			if (CodeCmd.equals("monde")) {
				if (PartieEnCours != null && PartieEnCours.Forto) {
					if (PartieEnCours.PartiePersonnalisée && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {
						if (Commande.length>=2) {
							long Now = System.currentTimeMillis();
							if (Now - LastLoad > 20000 || AutorisationModo) {
								LastLoad = Now;
								PartieForteresse.BoucleMonde.clear();
								//PartieForteresse.Forteresse_ListeCustomMonde.clear();
								PartieForteresse.AttaqueRouge = 1;
								PartieForteresse.AttaqueBleue = 1;
								//
								PartieForteresse.NumeroDonjon = 0;
								PartieForteresse.NiveauDonjon = 0;
								PartieForteresse.NiveauBonus = false;
								//
								if (Commande[1].equals("off")) {
									PartieForteresse.CustomMonde = false;
									PartieForteresse.setListePersoMondes(new ArrayList<Integer>());
									serveur.Envoie(PartieForteresse.ListeJoueur, "CxINFO#Mode aléatoire.");
								} else {
									String Param = ExtraireRaison(1, Commande);
									String[] Liste = Param.split("[^0-9]+");
									ArrayList<Integer> lsMondes = new ArrayList<Integer>();
									
									for (int i=0; i<Liste.length; ++i) {
										if (isInteger(Liste[i])) {
											PartieForteresse.CustomMonde = true;
											lsMondes.add(Integer.valueOf(Liste[i]));
										}
									}
									String Message = "CxINFO#" + PartieEnCours.Createur+ " a chargé les maps : " + lsMondes;
									PartieForteresse.setListePersoMondes(lsMondes);
									serveur.Envoie(PartieForteresse.ListeJoueur, Message);

								}
								PartieForteresse.CouleurParDefaut();
								PartieForteresse.LoadIgnorerNbManches();
								PartieForteresse.Nouvelle_Partie(-1, null, false);
							} else {
								Envoie("CxINFO#Vous pourrez utiliser cette commande dans moins de 20 secondes.");
							}

						} else {
							Envoie("CxINFO#Usage : /monde 1 5 7 pour jouer sur les mondes 1, 5 et 7 par exemple\n/monde off pour repasser en mode aléatoire");
						}
					} else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie privée peut le faire.");
					}
				}
				return;
			}


			// guide permanent
			if (CodeCmd.equals("guide")) {
				if (PartieEnCours != null && PartieEnCours.PAaaah) {
					if (PartieAaaah.LoadAutorisé && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {
						String Message = "";
						if (Commande.length == 2) {
							String Cible = Commande[1];
							
							if (Cible.equals("0") && PartieEnCours.hasMotDePasse()) {
								PartieAaaah.GuidePermanent = "1"; // Devient 0 après le load
								Message = "Le guidage a été désactivé." ;
							} else {
								Joueur Joueur = serveur.Joueur(Cible);
								if (Joueur != null) {
									Cible = Joueur.NomJoueur;                                        
									PartieAaaah.GuidePermanent = Cible;
									Message = Cible + " est désormais guide permanent." ;
								} else {
									Envoie("CxINFO#Vous devez choisir un joueur présent dans la partie pour pouvoir le mettre en guide permanent.");
									return;
								}
							}
						} else {
							if (!PartieAaaah.GuidePermanent.equals("")) {
								Message = PartieAaaah.GuidePermanent + " n'est désormais plus guide permanent.";
								PartieAaaah.GuidePermanent = "";
							} else {
								Message = "Aucun guide permanent en cours.";
							}
						}
						if(!Message.isEmpty()) {
							serveur.Envoie(PartieAaaah.ListeJoueur, "CxINFO#"+ Message);
						}
					}else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie privée peut le faire.");
					}
				}
				return;
			}

			// ng
			if (CodeCmd.equals("ng")) {
				if (PartieEnCours != null && PartieEnCours.PAaaah) {
					if (PartieAaaah.LoadAutorisé && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {
						PartieAaaah.GuidePourNG = !PartieAaaah.GuidePourNG;
						if (PartieAaaah.GuidePourNG) {
							Envoie("CxINFO#Les cartes no guide seront guidées.");
						} else {
							Envoie("CxINFO#Les cartes no guide ne seront plus guidées.");
						}
					}else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie privée peut le faire.");
					}
				}
				return;
			}
			
			// Permettre le contamination avec moins de 3 joueurs
			if (CodeCmd.equals("forceconta")) {
				if (PartieEnCours != null && PartieEnCours.PAaaah) {
					if (PartieAaaah.LoadAutorisé && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {

						PartieAaaah.ForcerConta = !PartieAaaah.ForcerConta;
						Envoie("CxINFO#La contamination avec moins de 3 joueurs est " + (PartieAaaah.ForcerConta ? "" : "dés") + "activée.");
					}else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie privée peut le faire.");
					}
				}
				return;
			}
			
			if (CodeCmd.equals("cri")) {
				if (PartieEnCours != null && PartieEnCours.PAaaah) {
					if (!EnOfficiel()) {
						if (Commande.length != 2 && Commande.length != 3) {
							Envoie("CxINFO#" + PartieAaaah.InfoCri());
							return;
						}
						
						if (PartieEnCours.Createur.equalsIgnoreCase(NomJoueur) && !PartieEnCours.PartieElo) {
							if (Commande.length == 2) {
								Envoie("CxINFO#" + PartieAaaah.SetCris(Commande[1]));
							} else {
								Envoie("CxINFO#" + PartieAaaah.SetCris(Commande[1], Commande[2]));
							}
						} else {
							Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie non-officielle non classée peut le faire.");
						}
					} else {
						Envoie("CxINFO#" + PartieAaaah.InfoCri());
					}
					
					return;
				}
			}
			
			// Recommencer la partie au début
			if (CodeCmd.equals("recommencer") && PartieForteresse != null) {
				if (PartieForteresse.CarteEnCours == null || PartieForteresse.CarteEnCours.getMode() != Rally.MODE_RALLY) {
					Envoie("CxINFO#Cette commande n'est utilisable qu'en rally.");
					return;
				}
				PartieForteresse.RecommencerMap(this);
				return;
			}

			// reload map Aaaah / Forto
			if (CodeCmd.equals("reload")) {
				if (PartieEnCours != null && PartieEnCours.PAaaah && ((Aaaah)PartieEnCours.getJeu()) != null) {
					Commande("load " + ((Aaaah)PartieEnCours.getJeu()).MondeEnCours);
				} else if (PartieEnCours != null && PartieEnCours.Forto) {
					if ((AutorisationModo || (PartieForteresse.Dessin && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)))
							&& PartieForteresse.Carte_Repertoriee()) {
							serveur.BOITE.Requete(Boite.CHARGER_RALLY, this, Long.toString(PartieForteresse.getIDCarteEnCours()));
						} else {
							Envoie("CxINFO#Vous devez être le créateur ou responsable d'un salon de dessin, avec une carte de /map chargée, pour pouvoir utiliser cette commande.");
						}
				}
				return;
			}
			//
			if (CodeCmd.equals("addfilm")) {
				if (AutorisationTournoiAaaah || AutorisationTournoiForto || AutorisationArbitreElo || AutorisationTounoiArbitreSecondaire) {
					if (PartieEnCours != null && PartieEnCours.PartieElo) {
						if (Commande.length > 1) {
							if (PartieEnCours.addFilmeurElo(Commande[1])) {
								System.out.println("MTCELO " + PartieEnCours.IDMatchELO + " addfilm " + Commande[1] + " (par : " + NomJoueur + ")");
								serveur.Envoie(PartieEnCours.getLsJoueurPartie(), "CxINFO#" + Commande[1] + " ajouté à la liste des filmeurs par " + NomJoueur + ".");
							} else {
								Envoie("CxINFO#" + Commande[1] + " n'est pas connecté, est déjà filmeur ou est un invité.");
							}
						} else {
							Envoie("CxINFO#/addfilmeur Pseudo : ajouter un arbitre à votre partie classée.");
						}
						
						return;
					}
				}
			}
			
			// Ajouter un arbitre à la partie classée
			if (CodeCmd.equals("addarb")) {
				if (AutorisationTournoiAaaah || AutorisationTournoiForto || AutorisationArbitreElo || AutorisationTounoiArbitreSecondaire) {
					if (PartieEnCours != null && PartieEnCours.PartieElo) {
						if (Commande.length > 1) {
							if (PartieEnCours.addArbitreElo(Commande[1])) {
								System.out.println("MTCELO " + PartieEnCours.IDMatchELO + " addarb " + Commande[1] + " (par : " + NomJoueur + ")");
								serveur.Envoie(PartieEnCours.getLsJoueurPartie(), "CxINFO#" + Commande[1] + " ajouté à la liste des arbitres par " + NomJoueur + ".");
							} else {
								Envoie("CxINFO#" + Commande[1] + " n'est pas connecté, est déjà arbitre ou est un invité.");
							}
						} else {
							Envoie("CxINFO#/addarb Pseudo : ajouter un arbitre à votre partie classée.");
						}
						
						return;
					}
				}
			}
			
			// Activer/désactiver ou donner la mortauto aux joueurs
			if (CodeCmd.equals("mortauto")) {
				if (AutorisationTournoiAaaah || AutorisationTournoiForto || AutorisationFilmeur || AutorisationArbitreElo || AutorisationFilmeurElo || AutorisationTounoiArbitreSecondaire) {
					if ((AutorisationTounoiArbitreSecondaire || AutorisationFilmeur || AutorisationArbitreElo || AutorisationFilmeurElo) && Commande.length > 1) {
						Envoie("CxINFO#Vous ne pouvez utiliser /mortauto que pour vous-même.");
						return; // Les arbitres secondaires, filmeurs et arbitres elo ne peuvent pas activer la mortauto chez quelqu'un d'autre
					}
					
					if (Commande.length == 1) {
						MortAuto = !MortAuto;
						Envoie("CxINFO#Mort automatique en début de partie : " + (MortAuto ? "" : "dés") + "activée.");
					} else if (Commande.length == 2 && PartieEnCours != null){
						ArrayList<Joueur> ls = PartieEnCours.getLsJoueurPartie();
						int Nb = ls.size();
						
						for (int i = 0; i < Nb; i++) {
							Joueur J = ls.get(i);
							if (J.NomJoueur.equalsIgnoreCase(Commande[1])) {
								if (J.AutorisationTournoiAaaah || J.AutorisationTournoiForto) {
									Envoie("CxINFO#Impossible à activer pour un arbitre d'IT.");
									return;
								}
								J.AutorisationTounoiArbitreSecondaire = true;
								J.MortAuto = true;
								J.Envoie("CxINFO#La mort automatique en début de partie vous a été activée. Tapez /mortauto pour la désactiver.");
								Envoie("CxINFO#La mort automatique a été activée pour : " + J.NomJoueur + ".");
								return;
							}
						}
						
						Envoie("CxINFO#Ce joueur n'est pas dans votre salon.");
					}
					return;
				}
			}

			// sondage
			if (CodeCmd.equals("sondage")) {
				if (Commande.length > 1 && isInteger(Commande[1])) {
					serveur.BOITE.Requete(Boite.ENVOIE_SONDAGE, this, Integer.valueOf(Commande[1]));
				}
				return;
			}
			
			if (CodeCmd.equals("idc")) {
				if (PartieEnCours != null && PartieEnCours.PartieElo) {
					Envoie("CxINFO#ID du match : " + PartieEnCours.IDMatchELO);
					return;
				}
			}
			
			// Donjons Forteresse
			if (PartieEnCours != null && PartieEnCours.Forto) {
				// charger un donjon
				if (CodeCmd.equals("donjon")) {
					if (PartieEnCours != null && PartieEnCours.Forto && PartieEnCours.hasMotDePasse()) {
						if (AutorisationModo || PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {
							serveur.BOITE.Requete(Boite.CHARGER_DONJON, this, 1);
						} else {
							Envoie_Info("Vous devez être le créateur d'un salon dessin pour utiliser cette commande.");
						}
					}
					return;
				}
				// information sur le donjon en cours
				if (CodeCmd.equals("idonjon")) {
					if (PartieEnCours != null && PartieEnCours.Forto) {
						if (PartieForteresse.NumeroDonjon != 0) {
							Envoie("CxINFO#Donjon " + PartieForteresse.NumeroDonjon + " niveau " + (PartieForteresse.NiveauBonus ? "bonus" : "normal") + " " + PartieForteresse.NiveauDonjon + ".");
						} else {
							Envoie("CxINFO#Vous n'êtes pas dans un donjon.");
						}
						
					} else {
						Envoie("CxINFO#Vous devez être sur un salon Forteresse pour pouvoir utiliser cette commande.");
					}
					return;
				}
				
				if (CodeCmd.equals("obj") || CodeCmd.equals("objet")) {
					Envoie("CxDONJOBJ#" + JDonjon.getListeItems());
					return;
				}
				
				// Commandes des classes dans un donjon
				if (CodeCmd.equals("cartographier")) {
					return;
				}
			}
			
			// passer à la manche/map suivante
			if (CodeCmd.equals("nextmap")) {
				if (AutorisationModo || (((Forteresse)PartieEnCours.getJeu()).Dessin && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur))) {
					PartieForteresse.DecalageLancement = true;
					PartieForteresse.Nouvelle_Partie(-1, null, false);
				} else {
					Envoie_Info("Vous devez être le créateur d'un salon non officiel pour utiliser cette commande.");
				}
				
				return;
			}

			// load map Forto
			if (CodeCmd.equals("map")) {
				if (Commande.length>=2 && isLong(Commande[1]) && PartieEnCours != null && PartieEnCours.Forto) {						
					if (AutorisationModo || (((Forteresse)PartieEnCours.getJeu()).Dessin && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur))) {
						serveur.BOITE.Requete(Boite.CHARGER_RALLY, this, Commande[1]);
					} else {
						Envoie_Info("Vous devez être le créateur d'un salon non officiel pour utiliser cette commande.");
					}
				} else {
					serveur.BOITE.Requete(Boite.LISTE_RALLY, this);											
				}
				return;
			}

			// update map Forto
			if (CodeCmd.equals("update")) {
				if (PartieEnCours != null && PartieEnCours.Forto && ((Forteresse)PartieEnCours.getJeu()).Dessin  && (AutorisationModoCartesRally || AutorisationModo)) {						
					Envoie("FxUPD");
				}
				return;
			}
			
			if (CodeCmd.equals("masserefus")) {
				if (AutorisationModoCartesRally || AutorisationModo) {						
					if (Commande.length == 2) {
						serveur.BOITE.Requete(Boite.MASSE_REFUS_RALLY, this, Commande[1]);
					} else {
						Envoie("CxINFO#/MasseRefus Pseudo : refuser toutes les maps en attente de validation d'un joueur.");
					}
				}
				return;
			}
			
			// calculer l'elo d'un joueur
			if (CodeCmd.equals("celo")) {
				// /celo (Equipe 1) (Equipe 2) Jeu Mode
				if (AutorisationModo || AutorisationGestionElo) {
					if (Commande.length < 5) {
						Envoie("CxINFO#/celo (J1_1, J1_2, ...) (J2_1, J2_2, ...) Jeu Mode Victoire"
								+ "\nJeu : a = Aaaah! ; b = Bouboum; f = Forteresse"
								+ "\nMode : (a) rally, def, ms, fs, run (b) boum (f) frig, frag, kill"
								+ "\nVictoire : 0 = victoire de J1_1, J2_1, ... ; 1 victoire des autres");
					} else {
						serveur.BOITE.Requete(Boite.CALCULER_NOUVEL_ELO, this, CHAINE);
					}
					return;
				}
			}
			
			// calculer l'elo d'un joueur
			if (CodeCmd.equals("celo_t")) {
				// /celo_t IdTeam1 IdTeam2 {a, b, f} Mode {Victoire : 0=Equipe 1, 1=Equipe 2}
				if (AutorisationModo || AutorisationGestionElo) {
					if (Commande.length < 5) {
						Envoie("CxINFO#/celo_t IdTeam1 IdTeam2 Jeu Mode Victoire"
								+ "\nJeu : a = Aaaah! ; b = Bouboum; f = Forteresse"
								+ "\nMode : (a) rally, def, ms, fs, run (b) boum (f) frig, frag, kill"											+ "\nVictoire : 0 = victoire de J1_1, J2_1, ... ; 1 victoire des autres");
					} else {
						serveur.BOITE.Requete(Boite.CALCULER_NOUVEL_ELO_TEAM, this, CHAINE);
					}
					return;
				}
			}
			
			// load map aaaah
			if (CodeCmd.equals("load")) {
				if (PartieEnCours != null && PartieEnCours.PAaaah) {
					if (PartieAaaah.LoadAutorisé && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur) && !PartieEnCours.EditeurAaaah) {
						long Now = System.currentTimeMillis();
						if (Now - LastLoad > 10000 || (AutorisationModoCartes && PartieEnCours.hasMotDePasse())) {
							LastLoad = Now;
							String Message = "CxINFO#" + PartieEnCours.Createur+ " a chargé une nouvelle partie";
							if (Commande.length == 2) {
								String Code = Commande[1];
								if (!Code.isEmpty()){
									Code = Code.replaceAll("@", "");                                                
									if (isLong(Code)) {
										Message += ".";
										serveur.Envoie(PartieAaaah.ListeJoueur, Message);
										PartieAaaah.PartieEnCours = false;
										PartieAaaah.Nouvelle_Partie(Long.parseLong(Code), (AutorisationModoCartes && PartieEnCours.hasMotDePasse()));
									}
								}
							} else {
								Message += " aléatoirement.";
								serveur.Envoie(PartieAaaah.ListeJoueur, Message);
								PartieAaaah.PartieEnCours = false;
								
								PartieAaaah.Nouvelle_Partie(-1);
							}
						} else {
							Envoie("CxINFO#Merci de patienter quelques secondes avant de charger une nouvelle carte.");
						}

					} else {
						Envoie("CxINFO#Vous ne pouvez pas effectuer cette action. Seul le créateur d'une partie privée peut le faire.");
					}
				}
				return;
			}

			// Commande /temps
			if (CodeCmd.equals("temps")) {
				if (AutorisationModo && Commande.length > 1) {
					Joueur LaCible = serveur.Joueur(Commande[1]);
					if (LaCible!=null) {
						long Ajout = (System.currentTimeMillis() - LaCible.TempsZero) / 3600000;
						long Temps = Ajout + (LaCible.Stats_TempsDeJeu / 3600000);
						Envoie("CxINFO#Temps de jeu de "+Commande[1]+ " : " + (LaCible.Stats_TempsDeJeu/3600000) + "h => " + Temps + "h ("+Ajout+"h sur salon)");
					}
				} else {
					Envoie("CxINFO#Temps de jeu : " + (Stats_TempsDeJeu / 3600000) + " heures.");
				}
				return;
			}
			// Commande /elo
			if (CodeCmd.equals("eloaaa")) {
				Joueur Cible = this;
				if (Commande.length > 1) {
					Cible = serveur.Joueur(Commande[1]);
				}
				if (Cible != null) {
					double Ratio = 0;
					if (Cible._Aaaah.Stats_JoueursGuidés > Cible._Aaaah.Stats_JoueursGuidésInit) {
						Ratio = ((double) (Cible._Aaaah.Stats_JoueursSauvés - Cible._Aaaah.Stats_JoueursSauvésInit)) / (Cible._Aaaah.Stats_JoueursGuidés - Cible._Aaaah.Stats_JoueursGuidésInit);
					}
					Envoie("CxINFO#Elo: Elo " + Cible._Aaaah.Stats_Elo + " / % " + Ratio + " / guidages " + (Cible._Aaaah.Stats_PartieGuide - Cible._Aaaah.Stats_PartieGuideInit));
				}
				return;
			}
			
			// Nombre de parties jouées
			if (CodeCmd.equals("mesparties")) {
				int AOldGuidés = (_Aaaah.Stats_OldGuidés == -1 ? 0 : _Aaaah.Stats_OldGuidés);
				int BOldJouée = (_Bouboum.Stats_OldJouées == -1 ? 0 : _Bouboum.Stats_OldJouées);
				int AOldJouée = (_Aaaah.Stats_OldJouées == -1 ? 0 : _Aaaah.Stats_OldJouées);
				int AOldSauvés = (_Aaaah.Stats_OldSauvés == -1 ? 0 : _Aaaah.Stats_OldSauvés);
				
				Envoie("CxINFO#Nombre de parties jouées sur Aaaah! : " + (_Aaaah.Stats_PartieJouée + AOldJouée) + " (" + AOldJouée + ")"
						+ "\nNombre de guidages : " + (_Aaaah.Stats_PartieGuide + AOldGuidés) + " (" + AOldGuidés + ")"
						+ "\nNombre de joueurs sauvés : " + (_Aaaah.Stats_JoueursSauvés + AOldSauvés) + " (" + AOldSauvés + ")"
						+ "\nNombre de joueurs tués : " + _Aaaah.Stats_Tué
						+ "\n\nNombre de parties jouées sur Bouboum : " + (_Bouboum.Stats_PartieJouée + BOldJouée) + " (" + BOldJouée + ")"
						+ "\nNombre de suicides : " + _Bouboum.Stats_Mort
						+ "\nNombre de joueurs tués : " + _Bouboum.Stats_Tué);
				return;
			}
			
			// désactivation maraaathon
			if (CodeCmd.equals("stopmaraboum")) {
				if (Mara_Activé) {
					serveur.BOITE.Requete(Boite.STOP_MARABOUM, this);
				} else {
					Envoie("CxINFO#Vous n'avez pas de statistiques sur le maraboum.");
				}
				return;
			}

			// Commande /bannir
			if (CodeCmd.equals("bannir")) {
				if (Commande.length > 1) {
					String JoueurCible = Commande[1];
					if (!JoueurCible.equalsIgnoreCase(NomJoueur)) {
						int Nb = serveur.ListeJoueur.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = serveur.ListeJoueur.get(i);
							if (Joueur.NomJoueur.equalsIgnoreCase(JoueurCible)) {
								// envoi info modos présents
								if (Joueur.PartieEnCours != null) {
									ArrayList<Joueur> ListeStaffPartie = new ArrayList<Joueur>();
									Joueur Staff;
									// Pour tous les joueurs de la partie
									int Nb2 = Joueur.PartieEnCours.getLsJoueurPartie().size();
									ArrayList<Joueur> ListeJoueurPartie = Joueur.PartieEnCours.getLsJoueurPartie();
									for(int j=0;j < Nb2;j++) {
										Staff = ListeJoueurPartie.get(j);
										
										//Si le joueur est dans le staff (Arbitre, Modo, Helper, Admin) On l'ajoute à la liste
										if (Staff.AutorisationSuperArbitre || Staff.AutorisationModo || Staff.AutorisationHelper || Staff.AutorisationAdmin) {
											ListeStaffPartie.add(Staff);
										}
									}
									serveur.Envoie(ListeStaffPartie, "CxPB#" + NomJoueur + "#" + Joueur.NomJoueur);
									//serveur.Envoie(Joueur.PartieEnCours.ListeModoPartie, "CxPB#" + NomJoueur + "#" + Joueur.NomJoueur);
								}
								if (!Joueur.DemandeBanIP.contains(AdresseIP) && !Joueur.DemandeBan.contains(NomJoueur) 
										&& !Joueur.AutorisationModo && !Joueur.AutorisationModoForum
										&& (Joueur.PartieEnCours == PartieEnCours)
										&& PartieEnCours!=null
										&& RestrictionHeure.UTILISER_VOTE_POPULAIRE >= 36000000) {

									// on traite la demande                                                                        
									Joueur.DemandeBanIP.add(AdresseIP);
									Joueur.DemandeBan.add(NomJoueur);

									if (Joueur.DemandeBan.size() >= 8) {
										serveur.Ban_Joueur(this, Joueur.NomJoueur, 3600000, "0", "Vote populaire", false, false);
									} else {
										Envoie("CxPB#");
									}
									return;
								} else {
									Envoie("CxPB#");
									return;
								}
							}
						}
						Envoie("CxPB");
					}
				}
				return;
			}


			// Liste des maps
			if (CodeCmd.equals("mapsde")) {
				String Cible = NomJoueur;
				if (Commande.length > 1) {
					Cible = Commande[1];
				}
				serveur.BOITE.Requete(Boite.LISTE_MAP_JOUEUR, this, Cible);
				return;
			}
			// Recherche

		}

		// Créateur salon
		if (CodeCmd.equals("createur")) {
			if (PartieEnCours!=null) {
				if (Commande.length == 1) {
					if (PartieEnCours.Createur.equalsIgnoreCase(PartieEnCours.CreateurOriginel)) {
						Envoie("CxINFO#Créateur du salon : " + PartieEnCours.Createur);
					} else {
						Envoie("CxINFO#Créateur du salon : " + PartieEnCours.CreateurOriginel + ", Responsable : " + PartieEnCours.Createur);
					}
				} else if (NomJoueur.equalsIgnoreCase(PartieEnCours.Createur)
						||NomJoueur.equalsIgnoreCase(PartieEnCours.CreateurOriginel) || AutorisationModo){
					String NouveauRespo = Commande[1];
					Joueur Respo = JoueurSalon(NouveauRespo);
					if (Respo != null) {
						PartieEnCours.Createur = Respo.NomJoueur;
						Envoie("CxINFO#"+PartieEnCours.Createur+ " est maintenant responsable de la partie.");
						if (Respo!=this) {
							Respo.Envoie("CxINFO#Vous êtes maintenant responsable de la partie.");
						}
					}
				}
			}
			return;
		}
		
		// activer/désactiver la sélection d'un nouveau responsable de partie auto
		if (CodeCmd.equals("autocrea")) {
			if (PartieEnCours != null) {
				if (Commande.length == 1) {
					Envoie("CxINFO#La séléction automatique d'un nouveau responsable de partie quand il n'y en a plus est : " + (PartieEnCours.NewCreateurAuto ? "" : "dés") + "activé.");
				} else {
					if((Commande[1].equalsIgnoreCase("on") || Commande[1].equals("1")) && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {
						PartieEnCours.NewCreateurAuto = true;
						Envoie("CxINFO#La séléction automatique d'un nouveau responsable activé.");
					} else if((Commande[1].equalsIgnoreCase("off") || Commande[1].equals("0")) && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {
						PartieEnCours.NewCreateurAuto = false;
						Envoie("CxINFO#La séléction automatique d'un nouveau responsable désactivé.");
					} else {
						Envoie("CxINFO#/autocrea {ON|1 / OFF|0} : activer (ON ou 1) ou désactiver (OFF ou 0) la sélection automatique d'un nouveau responsable de partie quand il n'y en a plus.");
					}
				} 
			}
			return;
		}
		

		// force bonus
		if (CodeCmd.equals("allin")) {
			if (PartieEnCours != null && PartieEnCours.Bouboum) {
				
				
				if ((PartieEnCours.hasMotDePasse() || PartieBouboum.SalonFrag)) {
					if (Commande.length > 1) {
						if (isInteger(Commande[1])  && PartieEnCours.Createur.equalsIgnoreCase(NomJoueur)) {
							int bonus = Integer.parseInt(Commande[1]); 
							if (bonus == 0) {
								PartieBouboum.FinAllIn();
								serveur.Envoie(PartieBouboum.ListeJoueur, "CxINFO#"+NomJoueur + " a mis fin au all-in.");
							} else if (bonus <= Bonus.MAX_BONUS_NO_EVENT) {
								Joueur Createur = serveur.Joueur(PartieEnCours.Createur);
								if (PartieEnCours.hasMotDePasse() || (Createur!=null && Createur.Vivant)) {
									PartieBouboum.SetAllIn("",bonus);
									serveur.Envoie(PartieBouboum.ListeJoueur, "CxINFO#"+NomJoueur + " a activé le all-in " + Bonus.NomBonus(bonus)+ ".");
									if (bonus == Bonus.BONUS_POSEAUTO) {
										serveur.Envoie(PartieBouboum.ListeJoueur, "CxFPA"); // Fin pose auto
									}
								} else {
									Envoie("CxINFO#Vous devez être en vie ou salon privé pour utiliser cette commande.");
								}
							} else {
								Envoie("CxINFO#Code invalide");
							}
						} else {
							Envoie("CxINFO#Vous devez être le créateur du salon et taper un chiffre.");
						}
					} else {
						String Message = "CxINFO#All-in " + Bonus.NomBonus(PartieBouboum.AllInBonus) + ".\nTapez /allin i pour forcer le bonus i";
						for (int i = Bonus.BONUS_UNDEFINED; i <= Bonus.MAX_BONUS_NO_EVENT; ++i) {
							Message += ", " + Bonus.NomBonus(i) + " -> "+ i;
						}
						Message += ".";
						Envoie(Message);
						
					}
				} else {
					Envoie("CxINFO#Commande limitée aux salons privés ou kill.");
				}
				return;
			}
		}		

		// Mdp salon
		if (CodeCmd.equals("mdp")) {
			if (PartieEnCours!=null && PartieEnCours.hasMotDePasse() && !PartieEnCours.EditeurAaaah) {
				if (Commande.length > 1) {
					if (NomJoueur.equalsIgnoreCase(PartieEnCours.Createur)
						|| NomJoueur.equalsIgnoreCase(PartieEnCours.CreateurOriginel)){
						String NouveauPass = Commande[1];
						PartieEnCours.MotDePasse = NouveauPass;
					}
				}
				Envoie("CxINFO#Mot de passe du salon : " + PartieEnCours.MotDePasse);
			}
			return;
		}
		/* Gestion Team */


		// Historique de la team
		if (CodeCmd.equals("historique")) {
			if (Commande.length > 1) {
				String IdT = Commande[1];
				if (isInteger(IdT)) {
					serveur.BOITE.Requete(Boite.HISTO_TEAM, this, Integer.parseInt(IdT));
				} else {
					serveur.BOITE.Requete(Boite.HISTO_TEAM_JOUEUR, this, IdT);
				}
			} else if (Membre) {
				serveur.BOITE.Requete(Boite.HISTO_TEAM, this, IdTeam);
			}
			return;
		}

		// info team /fiche
		if (CodeCmd.equals("fiche")) {
			Integer DemandeIdTeam = IdTeam;
			if (Commande.length >= 2 && isInteger(Commande[1])) {
				DemandeIdTeam = Integer.parseInt(Commande[1]);
			} else
				if (!Membre) {
					return;
				}
			serveur.BOITE.Requete(Boite.INFO_TEAM, this, DemandeIdTeam);
			serveur.BOITE.Requete(Boite.MEMBRES_TEAM, this, DemandeIdTeam);

			return;
		}

		// message team /t
		if (CodeCmd.equals("t")) {
			if (Commande.length >= 2) {
				if (Membre) {
					if (Muet) {
						Envoie("CxMuet");
					} else {
						String message = ExtraireRaison(1, Commande);
						message = serveur.FixeDoubleEsperlouette(message, this);
						serveur.Message_Team(this, message, IdTeam, false);
					}
					
				} else {
					Envoie("CxINFO#Vous devez être membre d'une team.");
				}
			}
			return;
		}
		
		// /qgrade9 Bravo, vous êtes le premier joueur à découvrir cette commande !
		// /rgrade9 reponse
		// /grade9 NomJoueur reponse
		if (CodeCmd.equals("grade9")) {
			if (!serveur.QuestionGrade9.isEmpty() && !serveur.ReponseGrade9.isEmpty()) {
				if (Commande.length >= 3) {
					String Reponse = ExtraireRaison(2, Commande);
					if (Reponse.equalsIgnoreCase(serveur.ReponseGrade9)) {
						String Beneficiaire = Commande[1];
						Joueur Cible = serveur.Joueur(Beneficiaire);
						if (Cible != null && Cible.Membre) {
							Cible.AutorisationGrade9 = true;
							serveur.BOITE.Requete(Boite.AUTORISER_GRADE_9, this, Cible.NomJoueur);
							serveur.BOITE.Requete(Boite.CHANGER_GRADE, this, 9, Cible.NomJoueur, true);
							System.out.println("GR4DE9 " + NomJoueur + " " + Cible.NomJoueur);
							serveur.Message_Modo(NomJoueur + " a donné le grade 9 à " + Cible.NomJoueur, false);
							serveur.QuestionGrade9 = "";
							serveur.ReponseGrade9 = "";
						} else {
							Envoie_Info(Beneficiaire + " doit être en ligne et membre d'une team.");
						}
					} else {
						Envoie_Info("Désolé, ceci n'est pas la bonne réponse.");
					}
				} else {
					Envoie_Info(serveur.QuestionGrade9 
							+ "\nTapez /grade9 NomJoueur reponse pour tenter de gagner le grade 9 pour NomJoueur.\nAttention : un leader pourra toujours décider de changer son grade par la suite.");
				}
			} else {
				Envoie_Info("Aucun grade 9 en jeu pour le moment.");
			}
			return;
		}		

		// message chucho team /ct
		if (CodeCmd.equals("ct")) {
			if (Commande.length >= 2) {
				if (Membre) {
					if (Muet) {
						Envoie("CxMuet");
					} else {
						String message = ExtraireRaison(1, Commande);
						message = serveur.FixeDoubleEsperlouette(message, this);
						serveur.Chucho_Team(this, message, IdTeam, false);
					}
				} else {
					Envoie("CxINFO#Vous devez être membre d'une team.");
				}
			}
			return;
		}
		
		if (CodeCmd.equals("m") && AutorisationInscription) { // Chuchotement (client HTML5/JavaScript)
			if (Commande.length >= 3) {
				if (Commande[1].equalsIgnoreCase(NomJoueur)) {
					return;
				}
				
				Joueur Destinataire = serveur.Joueur(Commande[1]);
				
				if (Destinataire == null) {
					Envoie("CxINFO#Ce joueur n'est pas connecté.");
					return;
				}
				
				if (!Destinataire.AccepteChuchotement) {
					Envoie("CxRef#0"); // Il refuse les chuchotements                                                                            
				} else if (!Destinataire.ListeJoueursIgnorés.contains(NomJoueur)) {
					String message = ExtraireRaison(2, Commande);
					
					message = serveur.FixeDoubleEsperlouette(message, this);
					
					Envoie("CxM#" + Destinataire.NomJoueur + "#" + message + "#" + NomJoueur);
					ArrayList<Joueur> ListeJoueurCible = new ArrayList<Joueur>();
					ListeJoueurCible.add(Destinataire);
					serveur.Envoie_Chat(this, ListeJoueurCible, "CxM#" + Destinataire.NomJoueur + "#" + message + "#" + NomJoueur);
				} else {
					Envoie("CxIGN#" + Destinataire.NomJoueur);
				}
				
			} else {
				Envoie("CxINFO#Pour envoyer un message privé, utilisez : /m Destinataire Message");
			}
			return;
		}
		
		// message canal Tous
		if (CodeCmd.equals("ts")) {
			if (Commande.length < 2) {
				return;
			}
			
			if (PartieEnCours == null) {
				Reception_Spéciale("CxM#" + CHAINE.substring(3));
			} else if (PartieEnCours.PAaaah) {
				PartieEnCours.getJeu().Reception(this, "ChM" + Aaaah.$ + CHAINE.substring(3), false);
			} else if (PartieEnCours.Bouboum) {
				PartieEnCours.getJeu().Reception(this, "ChM" + Bouboum.$ + CHAINE.substring(3), false);
			} else if (PartieEnCours.Forto) {
				PartieEnCours.getJeu().Reception(this, ((char)13) + Forteresse.$ + CHAINE.substring(3), false);
			}
			
			return;
		}
		
		// message canal Alliés
		if (CodeCmd.equals("al")) {
			if (Commande.length < 2) {
				return;
			}
			
			if (PartieEnCours != null && PartieEnCours.Forto) {
				PartieEnCours.getJeu().Reception(this, ((char)13) + Forteresse.$ + CHAINE.substring(3) + Forteresse.$ + "0", false);
			}
			return;
		}
		
		// message canal attente elo
		if (CodeCmd.equals("melo")) {
			if (Commande.length < 2 || PartieEnAttente == null || PartieEnAttente.AttenteEloPartie == null) {
				return;
			}
			
			if (Muet) {
				Envoie("CxMuet");
			} else {
				String message = ExtraireRaison(1, Commande);
				message = serveur.FixeDoubleEsperlouette(message, this);
				serveur.Envoie(PartieEnAttente.AttenteEloPartie.getLsJoueurPartie(), "CxMELO#[" + NomJoueur + "] " + message);
			}
			return;
		}
		
		// message salon /s
		if (CodeCmd.equals("s")) {
			if (Commande.length >= 2) {
				if (!SalonChat.isEmpty()) {
					if (Muet) {
						Envoie("CxMuet");
					} else {
						String message = ExtraireRaison(1, Commande);
						message = serveur.FixeDoubleEsperlouette(message, this);
						serveur.Message_SalonChat(this, message, false);
					}
				} else {
					Envoie("CxINFO#Vous n'êtes pas connecté à un salon.");
				}
			} else if (!SalonChat.isEmpty()) {
				OnlineMembers_Salon(SalonChat);
			}
			return;
		}

		// /salon
		if (CodeCmd.equals("salon")) {
			if (Commande.length >= 2 && !Commande[1].isEmpty() && AutorisationInscription) {
				String NomSalon = Commande[1];
				if (NomSalon.length()>=20) {
					Envoie("CxINFO#La taille du nom du salon est limitée à 20 caractères.");
					return;
				}
				if (!SalonChat.isEmpty()) {
					serveur.Message_SalonChat(this, NomJoueur + " vient de se déconnecter du salon.", true);
				}
				if (NomSalon.equals(SalonChat) || NomSalon.equalsIgnoreCase("quitter")) {
					SalonChat = "";
				} else {
					SalonChat = NomSalon;
					serveur.Message_SalonChat(this, NomJoueur + " vient de se connecter au salon.", true);

				}
				Envoie("CxSAct#"+(SalonChat.isEmpty()?"0":"1"));

			} else {
				Envoie("CxINFO#Tapez /salon NomSalon pour entrer dans un salon.\n"
						+ "Tapez /salon quitter pour quitter le salon.\n"
						+ "Tapez /s Message pour parler sur le salon.\n"
						+ "Tapez /s pour voir la liste des gens connectés au salon.\n"
						+ "Rappel : la charte s'applique partout, y compris sur ces salons. Les modérateurs pourront vérifier et sanctionner sans avertissement les infractions graves.");
			}
			return;
		}

		// Commande /canal
		if (CodeCmd.equals("canal")) {
			if (AccepteCanalTeam) {
				serveur.Message_Team(this, NomJoueur + " a fermé le canal.", IdTeam, true);
				Envoie("CxINFO#Vous avez fermé le canal team.");
				AccepteCanalTeam = false;
			} else {
				AccepteCanalTeam = true;
				serveur.Message_Team(this, NomJoueur + " a réouvert le canal.", IdTeam, true);
				Envoie("CxINFO#Vous avez ouvert le canal team.");
			}
			return;
		}

		// membres online /membres
		if (CodeCmd.equals("membres")) {
			if (Commande.length >= 2 && isInteger(Commande[1])) {
				OnlineMembers_Team(Integer.parseInt(Commande[1]));
			} else
				if (Membre) {
					OnlineMembers_Team(IdTeam);
				}
			return;
		}
		
		// modo online /mod
		if (CodeCmd.equals("mod")) {
			OnlineModo();
			return;
		}
		
		if (CodeCmd.equals("modf")) {
			OnlineModoF();
			return;
		}
		
		if (CodeCmd.equals("anim")) {
			OnlineAnimateur();
			return;
		}
		
		if (CodeCmd.equals("arbt")) {
			OnlineArbitreTournoi();
			return;
		}

		
		if (CodeCmd.equals("v")) {
			Envoie("CxINFO#Version Open Source v1");
			return;
		}
		
		if(CodeCmd.equals("nocandidature")) {
			if(Membre) {
				AccepteAVCandidatures = !AccepteAVCandidatures;
				if(AccepteAVCandidatures) {
					Envoie("CxINFO#Vous recevrez à nouveau les candidatures pour votre team.");
				} else {
					Envoie("CxINFO#Vous ne recevrez plus les candidatures pour votre team.");
				}
			} else {
				Envoie("CxINFO#Vous devez être d'une team pour pouvoir utiliser cette commande.");
			}
			return;
		}
		
		// rejoindre une team /rejoindre
		if (CodeCmd.equals("rejoindre")) {
			if (AutorisationInscription) {
				if (Commande.length >= 2 && isInteger(Commande[1])) {
					Integer IdTeamCible = Integer.parseInt(Commande[1]);
					if (Membre) {
						Envoie("CxINFO#Vous devez d'abord quitter votre team.");
					} else
						if (Stats_TempsDeJeu >= RestrictionHeure.REJOINDRE_TEAM) {
							IdTeamSouhaitée = IdTeamCible;
							for(Joueur J : serveur.ListeJoueur) {
								if(J.IdTeam == IdTeamCible && J.AccepteAVCandidatures && J.AutorisationGestionMembres && !J.ListeJoueursIgnorés.contains(NomJoueur) && !J.ListeIPIgnorées.contains(AdresseIP)) {
									J.Envoie("CxINFO#" + NomJoueur + " a demandé à rejoindre votre team.");
								}
							}
							Envoie("CxINFO#Demande formulée. Il faut maintenant attendre qu'un recruteur de la team vous invite à la rejoindre avant votre déconnexion.");
						} else {
							Envoie("CxINFO#Vous ne pouvez pas rejoindre une team avant d'avoir cumulé 5 heures de jeu. Tapez /temps pour connaitre votre temps de jeu.");
						}
				}
			}
			return;
		}

		// inviter quelqu'un à rejoindre sa team /inviter
		if (CodeCmd.equals("inviter")) {
			if (Commande.length >= 2) {
				Joueur Joueur = serveur.Joueur(Commande[1]);
				if (Joueur != null) {
					if (CheckDroits(AutorisationGestionMembres)) {
						serveur.BOITE.Requete(Boite.REJOINDRE_TEAM, this, Joueur.IdJoueur, IdTeam);
					}
				} else {
					Envoie("CxINFO#Ce joueur n'est pas connecté.");
				}
			}
			return;
		}

		
		// fusion
		// ListeTeamAmieEnAttente contient IdTeam -> IdCible
		if (CodeCmd.equals("fusion")) {
			if (Membre) {
				if (Commande.length >= 2) {
					if (AutorisationGestionRoles) {
						if (Commande[1].equalsIgnoreCase("quitter")){
							// suppression d'une fusion
							if (serveur.ListeTeamAmie.containsKey(IdTeam)) {
								QuitterFusion();
							} else {
								Envoie("CxINFO#Vous n'êtes pas dans une fusion.");
							}
						} else if (Commande[1].equalsIgnoreCase("random")){
							if (serveur.ListeTeamAmieEnAttente.containsKey(IdTeam)) {
								// annuler la dernière proposition
								serveur.ListeTeamAmieEnAttente.remove(IdTeam);
								Envoie("CxINFO#Proposition de fusion annulée.");	
							} else {
								// proposer aléatoirement
								ArrayList<Integer> ListeTeam = new ArrayList<Integer>();
								int Nb = serveur.ListeJoueur.size();
								for (int i = 0; i < Nb; i++) {
									Joueur Joueur = serveur.ListeJoueur.get(i);
									if (Joueur.AutorisationGestionRoles && Joueur.Membre && Joueur.IdTeam != IdTeam
										&& !serveur.ListeTeamAmie.containsKey(Joueur.IdTeam)
										&& !((Joueur.ListeJoueursIgnorés != null && Joueur.ListeJoueursIgnorés.contains(NomJoueur)) || (Joueur.ListeIPIgnorées != null && Joueur.ListeIPIgnorées.contains(AdresseIP)))) {										
										ListeTeam.add(Joueur.IdTeam);
									}
								}
								if (!ListeTeam.isEmpty()) {
									Commande("fusion " + ListeTeam.get((int) (Math.random() * ListeTeam.size())));
								}
							}
						} else if (isInteger(Commande[1])) {				
							int IdCible = Integer.parseInt(Commande[1]);
							if (IdCible != IdTeam) {
								if (serveur.ListeTeamAmieEnAttente.containsKey(IdTeam) && serveur.ListeTeamAmieEnAttente.get(IdTeam) == IdCible) {
									// annuler une proposition de fusion
									serveur.ListeTeamAmieEnAttente.remove(IdTeam);
									Envoie("CxINFO#Proposition de fusion annulée.");						
								} else if (serveur.ListeTeamAmie.containsKey(IdTeam) && serveur.ListeTeamAmie.containsKey(IdCible)) {
									Envoie("CxINFO#Vous êtes déjà dans une fusion. Tapez d'abord /fusion quitter si vous souhaitez en sortir.");	
								} else {									
									// proposer une fusion
									boolean Found = false;
									int Nb = serveur.ListeJoueur.size();
									for (int i = 0; i < Nb; i++) {
										Joueur Joueur = serveur.ListeJoueur.get(i);
										if (Joueur.AutorisationGestionRoles && Joueur.Membre && Joueur.IdTeam == IdCible
											&& !((Joueur.ListeJoueursIgnorés != null && Joueur.ListeJoueursIgnorés.contains(NomJoueur)) || (Joueur.ListeIPIgnorées != null && Joueur.ListeIPIgnorées.contains(AdresseIP)))) {										
											Joueur.Envoie("CxINFO#"+ NomJoueur + " vous propose une fusion du canal de sa team avec le vôtre. Tapez /accepter " + IdTeam + " pour accepter et /ignore " + NomJoueur + " pour ne plus recevoir ce message.");
											Found = true;
										}
									}
									if (Found) {
										serveur.ListeTeamAmieEnAttente.put(IdTeam,IdCible);
										Envoie("CxINFO#Fusion avec " +IdCible+ " proposée, en attente de réponse.");									
									} else {
										Envoie("CxINFO#Aucun leader connecté, réessayez plus tard.");
									}
								}
							} else {
								Envoie("CxINFO#Vous ne pouvez pas fusionner avec votre propre team.");						
							}
						}
					} else {				
						Envoie("CxINFO#Vous devez être leader d'une team pour utiliser cette commande.");
					}
				} else if (serveur.ListeTeamAmie.get(IdTeam) != null) {
					// liste membres dans la fusion
					int Nb = serveur.ListeJoueur.size();
					StringBuilder Chaine = new StringBuilder("CxINFO#Fusion: " + serveur.ListeTeamAmie.get(IdTeam).Alliance);
					StringBuilder InfosFusion = new StringBuilder("CxAM#4#"); // 4 signifie liste des membres de la fusion
					Chaine.append("\nMembres de la fusion en ligne : ");
					boolean First = true;
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.Membre && serveur.ListeTeamAmie.get(IdTeam).contains(Joueur.IdTeam)) {
							if (!First)
								Chaine.append(", ");
							Chaine.append(Joueur.NomJoueur + " (" + Joueur.IdTeam + ")");							
							InfosFusion.append(Joueur.CodeSalon(false));
							First = false;
						}
					}

					Envoie(Chaine.toString());	
					Envoie(InfosFusion.toString());
				}  else {
					Envoie("CxINFO#Tapez /fusion IdTeam pour que le canal de votre team rejoigne celui de IdTeam et /fusion quitter pour sortir d'une fusion. Vous devez être leader. (/fusion random pour proposer à une team aléatoire)");							
				}
			} else {
				Envoie("CxINFO# Vous devez être membre d'une team.");
			}
			return;
		}
		
		if (CodeCmd.equals("partagertopic")) {
			if (Membre && AutorisationGestionRoles) {
				serveur.BOITE.Requete(Boite.PARTAGER_TOPIC_TEAM_FUSION, this);
			} else {				
				Envoie("CxINFO#Vous devez être leader d'une team pour utiliser cette commande.");
			}
			return;
		}
		// accepter fusion
		// ListeTeamAmieEnAttente contient IdCible -> IdTeam
		if (CodeCmd.equals("accepter")) {
			if (Membre && AutorisationGestionRoles) {
				if (Commande.length >= 2 && isInteger(Commande[1])) {				
					int IdCible = Integer.parseInt(Commande[1]);
					if (serveur.ListeTeamAmieEnAttente.containsKey(IdCible) 
							&& serveur.ListeTeamAmieEnAttente.get(IdCible) == IdTeam) {
						// valider une fusion proposée (celui qui accepte reste le maitre)
						if (serveur.ListeTeamAmie.containsKey(IdCible) && serveur.ListeTeamAmie.containsKey(IdTeam))
						{
							Envoie("CxINFO#" + IdCible + " est déjà dans une fusion.");																	
						} else {
							RejoindreFusion(IdCible);
						}
					} else {
						Envoie("CxINFO#La team " + IdCible + " n'a pas fait de proposition de fusion.");						
					}
				} else {
					Envoie("CxINFO#Tapez /accepter IdTeam pour inviter IdTeam sur votre canal (voir /fusion).");
				}
			} else {				
				Envoie("CxINFO#Vous devez être leader d'une team pour utiliser cette commande.");
			}
			return;
		}
	   // Ignorer une team dans une fusion
		if(CodeCmd.equals("ignoref")){
		   int IdCible;
		   
		   if(Commande.length == 1){
			   Envoie("CxINFO#Permet d'ignorer des teams en fusion avec la vôtre. Ils ne verront plus vos messages"
					   + " et vous ne recevrez plus les leurs. Tapez /ignoref IdTeam pour ignorer une team, l'effet est cumulatif."
					   + " Remplacez IdTeam par Aucune ou All pour ne plus ignorer de team ou ignorer toute équipe présente actuellement"
					   + " ou qui s'y ajoutera. La commande est oubliée après votre déconnexion.");
			   return;
		   }
		   
		   if(!Membre){
			  Envoie("CxINFO#Vous devez être membre d'une team.");
		      return;
		   }
		   if(serveur.ListeTeamAmie.get(IdTeam) == null){
			   Envoie("CxINFO#Votre team doit être en fusion pour pouvoir utiliser cette commande.");
			   return;
		   }
		   
		   if(!isInteger(Commande[1])){
			   Commande[1] = Commande[1].toLowerCase();
			   if(Commande[1].equals("all")){
				   Envoie("CxINFO#Toutes les teams de cette fusion excepté la vôtre seront désormais ignorées jusqu'à votre prochaine déconnexion.");
				   IgnorerTouteFusion = true;
				   return;
			   } else if(Commande[1].equals("aucune")){
				   Envoie("CxINFO#Liste noire des teams effacée.");
				   IgnorerTouteFusion = false;
				   IgnorerFusion = new ArrayList<Integer>();
				   return;
			   } else{
				   Envoie("CxINFO#Paramètre invalide.");
				   return;
			   }
		   }
		   
		   IdCible = Integer.parseInt(Commande[1]);
		   if(IdCible == IdTeam){
			   Envoie("CxINFO#Vous ne pouvez pas ignorer votre propre team.");
			   return;
		   }
		   if(!serveur.ListeTeamAmie.get(IdTeam).contains(IdCible)){
			   Envoie("CxINFO#Cette team n'est pas en fusion avec vous.");
			   return;
		   } else {
			   if(IgnorerFusion.contains(IdCible)){
				   IgnorerFusion.remove((Integer)IdCible); // Laisser (Integer) (sinon mauvaise méthode)
				   Envoie("CxINFO#La team " + IdCible + " a été retirée.");
				   return;
			   }
			   IgnorerFusion.add(IdCible);
			   Envoie("CxINFO#La team " + IdCible + " sera désormais ignorée jusqu'à votre prochaine déconnexion.");
			   return;
		   }
	   }
		// Afficher liste des teams ignorées
		if (CodeCmd.equals("lnf")){
			String msg = "CxINFO#Teams ignorées : ";
			if (IgnorerFusion.isEmpty()){
				Envoie("CxINFO#Aucune team dans cette liste (ignorer toutes les teams : " + (!IgnorerTouteFusion ? "dés" : "") + "activé).");
				return;
			}
			
			for(int id : IgnorerFusion)
			  msg += id;
			
			Envoie(msg + "(ignorer toutes les teams : " + (!IgnorerTouteFusion ? "dés" : "") + "activé).");
			return;
		}

		// postulants
		if (CodeCmd.equals("candidatures")) {
			if (Membre) {
				int Nb = serveur.ListeJoueur.size();
				StringBuilder Chaine = new StringBuilder("CxINFO#Postulants à la team " + IdTeam + " : ");
				boolean First = true;
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = serveur.ListeJoueur.get(i);
					if (Joueur.IdTeamSouhaitée == IdTeam) {
						if (!First)
							Chaine.append(",");
						Chaine.append(Joueur.NomJoueur);
						First = false;
					}
				}
				if (First) {
					Chaine.append("Aucun.");
				}

				Envoie(Chaine.toString());
			} else {
				Envoie("CxINFO#Vous devez être membre d'une team.");
			}
			return;
		}
		
		if (CodeCmd.equals("abonnement")) {
			if (Commande.length == 1) {
				Envoie("CxINFO#/abonnement {g, a, b, f} : suivre les animations pour le Forum/Chat Principal, Aaaah!, Bouboum ou Forteresse.\n"
						+ "Abonnements en cours :\n"
						+ "Forum/CP : " + ((FlagMessageAnim & 1) != 0 ? "abonné" : "non abonné")
						+ "\nAaaah! : " + ((FlagMessageAnim & 2) != 0 ? "abonné" : "non abonné")
						+ "\nBouboum : " + ((FlagMessageAnim & 4) != 0 ? "abonné" : "non abonné")
						+ "\nForteresse : " + ((FlagMessageAnim & 8) != 0 ? "abonné" : "non abonné"));
			} else if (Commande.length >= 2) {
				int Flag = 0;
				if (Commande[1].equalsIgnoreCase("g")) {
					FlagMessageAnim ^= 1;
					Flag = 1;
				} else if (Commande[1].equalsIgnoreCase("a")) {
					FlagMessageAnim ^= 2;
					Flag = 2;
				} else if (Commande[1].equalsIgnoreCase("b")) {
					FlagMessageAnim ^= 4;
					Flag = 4;
				} else if (Commande[1].equalsIgnoreCase("f")) {
					FlagMessageAnim ^= 8;
					Flag = 8;
				} else {
					Envoie("CxINFO#L'abonnement demandé n'existe pas.");
					return;
				}
				
				if ((Flag & FlagMessageAnim) != 0) {
					Envoie("CxINFO#Abonnement ajouté.");
				} else {
					Envoie("CxINFO#Arrêt de l'abonnement.");
				}
			}
			return;
		}
		
		if (CodeCmd.equals("manim")) {
			if (AutorisationAnimateur || AutorisationModo) {
				if (Commande.length > 2) {
					int Flag = 0;
					Commande[1] = Commande[1].toUpperCase();
					
					if (Commande[1].equals("G")) {
						Flag |= 1;
					} else if (Commande[1].equals("A")) {
						Flag |= 2;
					} else if (Commande[1].equals("B")) {
						Flag |= 4;
					} else if (Commande[1].equals("F")) {
						Flag |= 8;
					} else {
						return;
					}
					
					
					String msg = ExtraireRaison(2, Commande);
					String str = "CxMANIM#[" + Commande[1] + "] " + msg;
					
					for (Joueur J : serveur.ListeJoueur) {
						if ((J.FlagMessageAnim & Flag) != 0) {
							J.Envoie(str);
						}
					}
					
					System.out.println("MANIM " + Commande[1] + " " + NomJoueur + " " + msg);
					
					return;
				} else {
					Envoie("CxINFO#/manim {g, a, b, f} Message");
					return;
				}
			}
		}

		if (CodeCmd.equals("recompense")) { 
			if (CheckParams(4, Commande)) {
				if (AutorisationAnimateurSuperviseur) {
					serveur.BOITE.Requete(Boite.AJOUTER_RECOMPENSE_JOUEUR, this, Commande[1], Commande[2], ExtraireRaison(3, Commande));
					LogCommande(CHAINE);
					serveur.Avertissement_Modo("REC - " + NomJoueur + " vient d'ajouter la récompense "+ Commande[2]+" ("+ExtraireRaison(3, Commande)+") sur " + Commande[1], false);
				}
				else if (CheckDroits(false)) {
					serveur.BOITE.Requete(Boite.AJOUTER_RECOMPENSE_JOUEUR, this, Commande[1], Commande[2], ExtraireRaison(3, Commande));
					LogCommande(CHAINE);

				}
			} else {
				Envoie("CxINFO#/recompense Joueur IdRecompense Message");
			}
			return;
		}

		if (CodeCmd.equals("recompense_del")) {
			if (CheckParams(3, Commande) && isInteger(Commande[2])) {
				if (AutorisationAnimateurSuperviseur) {
					serveur.BOITE.Requete(Boite.SUPPRIMER_RECOMPENSE_JOUEUR, this, Commande[1], Integer.parseInt(Commande[2]));
					LogCommande(CHAINE);
					serveur.Avertissement_Modo("REC - " + NomJoueur + " vient de supprimer la récompense n°"+ Commande[2]+ " de " + Commande[1], false);
				}
				else if (CheckDroits(false)) {
					serveur.BOITE.Requete(Boite.SUPPRIMER_RECOMPENSE_JOUEUR, this, Commande[1], Integer.parseInt(Commande[2]));
					LogCommande(CHAINE);
				}
			} else {
				Envoie("CxINFO#/recompense_del Joueur IdRecompense");
			}
			return;
		}

		if (CodeCmd.equals("palmares")) {
			if (Commande.length >= 2) {
				serveur.BOITE.Requete(Boite.LISTE_RECOMPENSES_JOUEUR, this, Commande[1]);
			}
			return;
		}

		if (CodeCmd.equals("team")) { // /team
			try {
				if (Commande.length >= 2) {
					String Action = Commande[1];
					Action = Action.toLowerCase();

					if (Action.equals("aide")) {
						/*Envoie("CxINFO#" + "/team liste : liste des teams\n" + "/rejoindre IdTeam : rejoindre une team\n" + "/inviter NomJoueurConnecté : inviter le joueur à rejoindre sa team\n" + "/team quitter : quitter sa team\n"
								//+ "/team online : team des joueurs connectés (pas terminé)\n"  
								+ "/team recompense IdTeam IdImage Message: ajouter une récompense à la team (IdImage = 0, 1, 2 ..)\n" + "/team suppr_recompense IdTeam : supprimer les récompenses de la team\n" + "/team joueur NomJoueurConnecté : info team d'un joueur\n" + "/profil NomJoueurConnecté : profil d'un joueur\n" + "/candidatures : la liste des postulants à la team\n" + "/fiche IdTeam : fiche d'une team\n" + "/membres IdTeam : joueurs connectés d'une team\n" + "/t Message : écrire sur son canal team \n" + "Admin :\n"
								+ "/team editer IdTeam what Blablabla : éditer l'info d'une team (what = site, description, fondateur, creation, jeu, nom, tag, membres)\n" + "/team grade NomJoueur NouveauGrade : changer le grade d'un joueur\n" + "/team role NomJoueur NouveauGrade : changer le rôle d'un joueur (0=membre, 1=gestion membres, 2=gestion team)\n" + "/team dissoudre IdTeam : dissoudre une team\n" + "/team ajouter NomJoueur IdTeam : NomJoueur rejoint la team IdTeam\n" + "/team quitter NomJoueur : éjecter NomJoueur de sa team\n"
								+ "/fusion pour mélanger le canal de sa team avec une autre team");*/
						Envoie("CxINFO#" + "Commandes générales :\n"
								         + " /team liste : liste des teams\n"
								         + " /fiche IdTeam : fiche d'une team\n"
                  						 + " /rejoindre IdTeam : envoyer une demande pour rejoindre une team\n"
                  						 + " /inviter NomJoueurConnecté : inviter le joueur à rejoindre sa team\n"
										 + " /team quitter : quitter sa team\n"
										 + " /team joueur NomJoueurConnecté : info team d'un joueur\n"
										 + " /profil NomJoueurConnecté : profil d'un joueur\n"
										 + " /candidatures : la liste des postulants à la team\n"
										 + " /membres IdTeam : joueurs connectés d'une team\n"
										 + " /historique IdTeam : afficher l'historique de la team\n"
										 + " /historique NomJoueur : afficher l'historique team du joueur\n"
										 + " /t Message : écrire sur son canal team (mélangé si une fusion est en cours)\n"
										 + " /ct Message : parler sur son canal team de manière visible uniquement par les membres de votre team\n"
										 + " /ignoref IdTeam : ignorer une team en fusion avec la vôtre (/ignoref pour plus d'informations)\n"
										 + " /lnf : afficher la liste des teams que vous ignorez\n"
										 + " /nocandidature : activer/désactiver l'affichage des candidatures en temps réel\n\n"
										 + "Leader :\n"
										 + " /team grade NomJoueur NouveauGrade : changer le grade d'un joueur (y compris grade > 5 si autorisé)\n"
										 + " /team role NomJoueur NouveauRole : changer le rôle d'un joueur (0=membre, 1=gestion membres, 2=gestion team, 3=scribe)\n"
										 + " /fusion IdTeam : pour proposer de mélanger le canal de sa team avec une autre team\n"
										 + " /accepter IdTeam : accepter une fusion\n"
										 + " /team dissoudre IdTeam : dissoudre une team\n"
										 + " /team quitter NomJoueur : éjecter NomJoueur de sa team ou vous-même si aucun pseudo précisé\n"
										 + " /partagertopic : Partager votre topic team avec les fusions qui partagent le leur.\n"
										 + ((AutorisationModo || AutorisationModoTeam || AutorisationRespoTriTeam) ? "\nAdmin :\n"
										    + " /team editer IdTeam what Blablabla : éditer l'info d'une team (what = site, description, fondateur, creation, jeu, nom, tag, membres)\n"
										    + " /team ajouter NomJoueur IdTeam : NomJoueur rejoint la team IdTeam\n"
										    + " /team recompense IdTeam IdImage Message : ajouter une récompense à la team (IdImage = 0, 1, 2 ..)\n"
										    + " /team suppr_recompense IdTeam : supprimer les récompenses de la team" : ""));
					} else
						if (Action.equals("ajouter")) {
							if (CheckParams(4, Commande) && isInteger(Commande[3])) {
								if (CheckDroits(false)) {
									serveur.BOITE.Requete(Boite.AJOUTER_MEMBRE, this, Integer.parseInt(Commande[3]), Commande[2]);
								}
							}
						} else
							if (Action.equals("recompense")) {
								if (CheckParams(5, Commande) && isInteger(Commande[2])) {
									if (CheckDroits(false)) {
										serveur.BOITE.Requete(Boite.AJOUTER_RECOMPENSE, this, Commande[3], ExtraireRaison(4, Commande), Integer.parseInt(Commande[2]));
									}
								}
							} else
								if (Action.equals("suppr_recompense")) {
									if (CheckParams(3, Commande) && isInteger(Commande[2])) {
										if (CheckDroits(false)) {
											serveur.BOITE.Requete(Boite.SUPPRIMER_RECOMPENSES, this, Integer.parseInt(Commande[2]));
										}
									}
								} else
									if (Action.equals("quitter")) {
										if (AutorisationInscription) {
											String NomCible = NomJoueur;
											if (Commande.length >= 3) {
												NomCible = Commande[2];
											}
											serveur.BOITE.Requete(Boite.QUITTER_TEAM, this, NomCible);
										}
									} else
										if (Action.equals("editer")) {
											if (CheckParams(5, Commande) && isInteger(Commande[2])) {
												Integer IdTeamCible = Integer.parseInt(Commande[2]);
												String What = Commande[3];
												if (What.equals("site")) {
													serveur.BOITE.Requete(Boite.EDITER_SITE, this, IdTeamCible, Commande[4]);
												} else
													if (What.equals("description")) {
														serveur.BOITE.Requete(Boite.EDITER_DESCRIPTION, this, IdTeamCible, ExtraireRaison(4, Commande));
													} else
														if (What.equals("message")) {
															serveur.BOITE.Requete(Boite.EDITER_MESSAGE, this, IdTeamCible, ExtraireRaison(4, Commande));
														} else
															if (What.equals("fondateur") || What.equals("officiel") || What.equals("score") || What.equals("nom") || What.equals("tag") || What.equals("creation") || What.equals("jeu") || What.equals("membres")) {
																serveur.BOITE.Requete(Boite.EDITER_INEDITABLE, this, ExtraireRaison(4, Commande), What, IdTeamCible);
															} else {
																Envoie("CxINFO#Vous ne pouvez éditer que site, description, fondateur, creation, jeu, nom, tag, membres");
															}
											}
										} else
											if (Action.equals("grade")) {
												if (CheckParams(4, Commande) && isInteger(Commande[3])) {

													int NouveauGrade = Integer.parseInt(Commande[3]);
													if (NouveauGrade >= 0 && NouveauGrade <= 11) {
														serveur.BOITE.Requete(Boite.CHANGER_GRADE, this, NouveauGrade, Commande[2], false);
													} else {
														Envoie("CxINFO#Le grade doit être compris entre 0 et 11.");
													}
												}
											} else
												if (Action.equals("role")) {
													if (CheckParams(4, Commande) && isInteger(Commande[3])) {
														int NouveauRole = Integer.parseInt(Commande[3]);
														if (NouveauRole >= 0 && NouveauRole <= 3) {
															serveur.BOITE.Requete(Boite.CHANGER_ROLE, this, NouveauRole, Commande[2]);
														} else {
															Envoie("CxINFO#Le rôle doit être compris entre 0 et 3.");
														}
													}
												} else
													if (Action.equals("creer222")) {
														if (CheckParams(7, Commande) && isInteger(Commande[6])) {
															if (CheckDroits(false)) {
																serveur.BOITE.Requete(Boite.CREER_TEAM, this, Commande[2], Commande[3], Commande[4], Integer.parseInt(Commande[5]), Integer.parseInt(Commande[6]));
															}
														}
													} else
														if (Action.equals("dissoudre")) {
															Integer IdTeamCible;
															if (Commande.length >= 3 && isInteger(Commande[2])) {
																IdTeamCible = Integer.parseInt(Commande[2]);
															} else
																if (Membre) {
																	IdTeamCible = IdTeam; return; // fonction supprimée
																} else {
																	Envoie("CxINFO#Vous n'êtes pas membre d'une team.");
																	return;
																}
															serveur.BOITE.Requete(Boite.SUPPRIMER_TEAM, this, IdTeamCible);
															/*}else if (Action.equals("info")) {
															    if (CheckParams(3,Commande))                                            
															    serveur.BOITE.Requete(Boite.INFO_TEAM, this, Integer.parseInt(Commande[2])); */
														} else
															if (Action.equals("liste")) {
																if (!Boite.CacheListeTeamAJour) {
																	serveur.BOITE.Requete(Boite.LISTE_TEAM, this);
																} else {
																	Envoie(Boite.CacheListeTeam);
																}

															} else
																if (Action.equals("joueur")) {
																	if (CheckParams(3, Commande)) {
																		Joueur Joueur = serveur.Joueur(Commande[2]);
																		if (Joueur != null) {
																			Envoie("CxINFO#" + Joueur.InfoTeam());
																		} else {
																			Envoie("CxINFO#" + Commande[2] + " n'est pas connecté.");
																		}
																	}
																} else {
																	Envoie("CxINFO#Commande " + Action + " inconnu.");
																}
				} else {
					Envoie("CxINFO#Interface d'administration des teams.\nTapez /team aide pour la liste des commandes.\n");
				}
			} catch (Exception e) {
				Envoie("CxINFO#Une erreur s'est produite.");
				System.err.print("3XC Commande team "); e.printStackTrace();
			}
			return;
		}
		
		if (CodeCmd.equals("equi") || CodeCmd.equals("equilibrer")) {
			if (PartieForteresse != null && !PartieEnCours.PartieElo) {
				PartieForteresse.Equilibrer(this);
				return;
			}
		}

		// /chercher
		if (CodeCmd.equals("chercher")) {
			String Nom = NomJoueur;
			if (Commande.length >= 2) {
				Nom = Commande[1];
			}
			boolean Extended = Commande.length == 3;
			ChercherJoueur(Nom,"",Extended);

			return;
		}
		// /chercher ip
		if (CodeCmd.equals("ci")) {
			if (AutorisationAdmin) {
				if (Commande.length >= 2) {
					ChercherJoueur("",Commande[1],false);
				}
			}
			return;
		}

		if (CodeCmd.equals("code")) {
			if (PartieEnCours!=null && PartieEnCours.PAaaah) {
				Envoie("CxINFO#Code de la map : @" + ((Aaaah)PartieEnCours.getJeu()).MondeEnCours);
			} else if (PartieForteresse != null) {
				if (!PartieForteresse.Carte_Repertoriee()) {
					Envoie("CxINFO#Vous êtes sur une carte qui n'est pas un rally publié dans /map depuis " + PartieForteresse.getTempsZeroRally(this) + " secondes.");
				} else {
					Envoie("CxINFO#Vous êtes sur le rally " + PartieForteresse.getIDCarteEnCours() + " depuis " + PartieForteresse.getTempsZeroRally(this) + " secondes.");
				}
			}
			return;
		}
		
		if (CodeCmd.equals("lastcode")) {
			if (PartieAaaah!=null) {
				if (PartieAaaah.MondePrecedent != -1) {
					Envoie("CxINFO#Code de la map précédente : @" + PartieAaaah.MondePrecedent);
				} else {
					Envoie("CxINFO#Aucune map n'a été chargée avant celle-ci.");
				}
				
			}
			return;
		}
		
		if (CodeCmd.equals("licence")) {
			Envoie("CxINFO#Version 1 d'Extinction Minijeux sous licence GNU GPL v3 générée le 14 avril 2020.");
			return;
		}

		// /profil
		if (CodeCmd.equals("profil")) {
			String Cible = NomJoueur;
			if (Commande.length >= 2) {
				Cible = Commande[1];
			}
			GetProfil(Cible, true);
			return;
		}


		// Commande /chut
		if (CodeCmd.equals("chut")) {
			AccepteChuchotement = !AccepteChuchotement;
			if (AccepteChuchotement) {
				Envoie("CxRef#1"); // Accepte désormais les chuchotements 
			} else {
				Envoie("CxRef#2"); // Refuse les chuchotements 
			}
			return;
		}

		// Commande /afk
		if (CodeCmd.equals("afk")) {
			if (!Afk && PartieEnCours != null && PartieEnCours.PAaaah && !PartieAaaah.NomGuideSalon().equals(NomJoueur)) {
				PartieAaaah.AjouterAfk(this);
			} else if (Afk && PartieEnCours == null) {
				Rejoindre(Afk_Partie);
				_Aaaah.Score = (int) (0.99 * Afk_Score); // malus -1%
				((Aaaah)Afk_Partie.getJeu()).QuitterAfk(this);
			}
			
			return;
		}

		// Commande /mdm
		if (CodeCmd.equals("mdm")) {
			if (AutorisationInscription) {

				if (PartieEnCours != null && PartieEnCours.PAaaah) {
					Envoie("CxSigM#" + PartieAaaah.MondeEnCours);
				} else {
					Envoie("CxSigM");
				}
			}
			return;
		}
		Envoie("CxINFO#Cette commande n'existe pas ou ne vous est pas accessible.\nTapez /aide pour une liste non-exhaustive ou rendez-vous sur le forum Général, topic : « Liste des commandes », auteur : Moderateur pour une liste complète.");
	}

	private void Eject3(String[] Commande) {
		if (Commande.length > 1) {
			int c = 0;
			try {
				ArrayList<Joueur> Ls = serveur.ListeJoueur;
				int Nb = Ls.size();
				for (int i = 0; i < Nb; i++) {
					Joueur J = Ls.get(i);
					if (J.NomJoueur.equalsIgnoreCase(Commande[1])) {
						J.Deconnexion("EJECT3");
						++c;
					}
				}
				
				Ls = serveur.ListeJoueurEnAttenteElo;
				Nb = Ls.size();
				for (int i = 0; i < Nb; i++) {
					Joueur J = Ls.get(i);
					if (J.NomJoueur.equalsIgnoreCase(Commande[1])) {
						J.Deconnexion("EJECT3");
						++c;
					}
				}
				
				for (Partie p : serveur.ListePartie) {
					Ls = p.getLsJoueurPartie();
					Nb = Ls.size();
					for (int i = 0; i < Nb; i++) {
						Joueur J = Ls.get(i);
						if (J.NomJoueur.equalsIgnoreCase(Commande[1])) {
							J.Deconnexion("EJECT3");
							++c;
						}
					}
				}
			} catch (Exception e){}
			
			Envoie("CxINFO#Eject3 terminé : " + c);
		}
	}

	public void RejoindreFusion(Integer IdCible) {
		serveur.ListeTeamAmieEnAttente.remove(IdCible);
		// si la cible a déjà une fusion, la team la rejoint
		Integer IdMaster, IdSlave;
		if (serveur.ListeTeamAmie.containsKey(IdCible)) {
			IdMaster = IdCible; 
			IdSlave = IdTeam;
		} else {
			IdMaster = IdTeam;
			IdSlave = IdCible;
		}
		if (!serveur.ListeTeamAmie.containsKey(IdMaster)) {
			// nouvelle fuion
			serveur.ListeTeamAmie.put(IdMaster, new FusionTeam(serveur.FusionCompteur));
			serveur.BOITE.Requete(Boite.NOUVELLE_FUSION, this, new int[]{serveur.FusionCompteur, IdMaster, IdSlave.intValue()});
			serveur.FusionCompteur++;
			serveur.ListeTeamAmie.get(IdMaster).add(IdMaster);
		} else {
			// ajouter IdSlave à la fusion du Master
			serveur.BOITE.Requete(Boite.UPDATE_FUSION, this, serveur.ListeTeamAmie.get(IdMaster).Id, IdSlave);
		}
		serveur.ListeTeamAmie.get(IdMaster).add(IdSlave);
		serveur.ListeTeamAmie.put(IdSlave, serveur.ListeTeamAmie.get(IdMaster));
		serveur.Message_Team(this,"La team " + IdSlave + " vient de rejoindre la fusion de la team " + IdMaster + ".", IdMaster, true);
		Envoie("CxINFO#Fusion validée.");		
	}
	
	public void QuitterFusion() {
		serveur.Retirer_Team_Fusion(IdTeam, this);
	}
	
	private void AjouterAmi(String NomAmi) {
		// on ne peut pas s'ajouter soi-même
		if (NomAmi.equalsIgnoreCase(NomJoueur)) {
			return;
		}

		// on vérfie qu'il n'est pas déjà ajouté
		int Nb = ListeAmis.size();
		for (int i = 0; i < Nb; i++) {
			Ami ami = ListeAmis.get(i);
			if (ami.NomAmi.equalsIgnoreCase(NomAmi)) {
				Envoie("CxAA#1"); // déjà ami

				return;
			}
		}

		// on contrôle le nombre d'amis
		if (ListeAmis.size() >= 80 && !AutorisationModo) {
			Envoie("CxAA#2"); // trop d'amis

			return;
		}

		serveur.BOITE.Requete(Boite.AJOUT_AMI, this, NomAmi);

	}
	
	public String Info() {
		long Mumute = TempsMuet - (Calendar.getInstance().getTimeInMillis() / 1000);

		return NomJoueur + " | "
		+ (Muet ? "muet " : "")
		+ (MuetForum ? "muteforum " : "")
		+ (MuteChatPrincipal ? "mutecp " : "")
		+ (Mumute > 0 ? (Mumute / 3600) + "h de mumute " : "") + " | Speedhack=" + Speedhack + " | Surplus Munitions : " + SurplusMunitions + " | Croix rouges : " + CroixRouge + " | Temps de jeu=" + (Stats_TempsDeJeu / 3600000) + "h | Ciblé par " + (DemandeBan.size()) + " /bannir : " + (DemandeBan.toString());
	}

	public String IP() {
		return NomJoueur + " : " + AdresseIP + " : " + AdresseHost;
	}

	public void ChercherJoueur(String Nom, String IP, boolean Extended) {
		String recherche = "";
		Boolean Trouve = false;
		for (int i = serveur.ListeJoueur.size() - 1; i >= 0; i--) {
			Joueur Joueur = serveur.ListeJoueur.get(i);
			if (Joueur.NomJoueur.equalsIgnoreCase(Nom) || (AutorisationModo && Joueur.AdresseIP.equals(IP)) || (AutorisationModo && Extended && Joueur.NomJoueur.toLowerCase().contains(Nom.toLowerCase()))) {
				if (!recherche.isEmpty()) {
					recherche += "\n";
				}
				recherche += Joueur.NomJoueur + " se trouve sur : " + Joueur.Salon();
				Trouve = true;
			}
		}
		if (!Trouve) {
			recherche += Nom + " n'est pas connecté.";
		}
		Envoie("CxINFO#" + recherche);
	}
	
	public void Mutecp(String Cible) {
		Joueur Joueur = serveur.Joueur(Cible);
		String IP = null;
		if (Joueur != null) {
			Joueur.MuteChatPrincipal = true;
			serveur.EnvoieCP("CxMUTE#" + Joueur.NomJoueur);
			IP = Joueur.AdresseIP;
		}
		if (!serveur.ListeMuteChatPrincipal.contains(Cible.toLowerCase())) {
			serveur.ListeMuteChatPrincipal.add(Cible.toLowerCase());
		}
		serveur.Message_Modo(NomJoueur + " vient de rendre muet " + Cible + " sur le chat principal.", true);
		serveur.BOITE.Requete(Boite.LOG_SANCTION, this, Cible, IP, "", "mute-cp", 0);
	}

	public void ListeSignalements(int Nb) {
		ListeSignalements(Nb,false);
	}

	public void ListeSignalements(int Nb, boolean BIG) {
		StringBuilder Msg = new StringBuilder();
		int NbSignaux = serveur.ListeSignal.size();
		int Debut = (NbSignaux>Nb?NbSignaux-Nb:0);
		for (int i=Debut;i<NbSignaux;i++) {
			if (i!=Debut) {
				Msg.append("\n");
			}
			Signalement Signal = serveur.ListeSignal.get(i);
			Msg.append(Signal.toString(this));
		}
		if (!BIG) {
			Message_Info("Liste signalements :\n" + Msg.toString());
		} else {
			Envoie("CxHSF#"+Msg.toString());
		}
	}

	public void ListeSignalementsForum(int Nb) {
		ListeSignalementsForum(Nb,false);
	}

	public void ListeSignalementsForum(int Nb, boolean BIG) {
		StringBuilder Msg = new StringBuilder();
		int NbSignaux = serveur.ListeSignalForum.size();
		for (int i=(NbSignaux>Nb?NbSignaux-Nb:0);i<NbSignaux;i++) {
			SignalementForum Signal = serveur.ListeSignalForum.get(i);
			Msg.append(Signal.toString(this));
			Msg.append("\n");
		}
		if (!BIG) {
			Envoie("CxINFO#Liste signalements forum :\n" + Msg.toString());
		} else {
			Envoie("CxHSF#"+Msg.toString());
		}
	}


	public String Role() {
		if (AutorisationGestionRoles)
			return "Leader (gestion de la team)";
		else
			if (AutorisationGestionMembres)
				return "Recruteur (gestion des membres)";
			else if(AutorisationScribe)
				return "Scribe (gestion de la communication)";
			else
				return "Membre";
	}

	public int RoleInt() {
		if (AutorisationGestionRoles)
			return 2;
		else
			if (AutorisationGestionMembres)
				return 1;
			else if (AutorisationScribe)
				return 3;
			else
				return 0;
	}
	
	public void RompreStValentin() {
		if (Amoureux.equals("")) {
			Envoie("CxINFO#Vous n'êtes lié à personne.");
			return;
		}
		
		Joueur Ex = serveur.TrouverAmoureux(serveur.ListeJoueur, this);
		if (Ex != null) {
			Ex.Envoie("CxSTVULINK");
			Ex.Envoie("CxINFO#" + NomJoueur + " a rompu votre union.");
			AvertirArbitresFinUnion(Ex);
		}
		
		AvertirArbitresFinUnion(this);
		Envoie("CxSTVULINK");
		Amoureux = "";
	}
	
	private void AvertirArbitresFinUnion(Joueur J) {
		if (J.PartieEnCours != null) {
			for (Joueur Joueur : J.PartieEnCours.getLsJoueurPartie()) {
				if (Joueur.AutorisationTournoiAaaah || Joueur.AutorisationTournoiForto || Joueur.AutorisationArbitreElo || Joueur.AutorisationTounoiArbitreSecondaire) {
					Joueur.Envoie("CxINFO#" + J.NomJoueur + " n'est plus lié.");
				}
			}
		}
	}

	public String InfoTeam() {
		if (Membre) {
			return "IdTeam : " + IdTeam + ", Grade : " + Grade + ", Role : " + Role();
		} else {
			return NomJoueur + " n'est pas membre d'une team.";
		}
	}

	public Joueur JoueurSalon(String JOUEUR) {
		if (PartieEnCours!=null) {
			int Nb = PartieEnCours.getLsJoueurPartie().size();
			ArrayList<Joueur> ListeJoueurPartie = PartieEnCours.getLsJoueurPartie();
			for (int i = 0; i < Nb; i++) {
				Joueur Joueur = ListeJoueurPartie.get(i);
				if (Joueur.NomJoueur.equalsIgnoreCase(JOUEUR)) {
					return Joueur;
				}
			}
		}
		return null;
	}

	public void EnvoiePlaylist(ResultSet Resultat){
		try {
			StringBuilder Chaine = new StringBuilder("CxPlist#");
			while (Resultat.next()) {
				int id = Integer.parseInt(Resultat.getString("id_joueur"));
				int i = Integer.parseInt(Resultat.getString("populair"));
				//formule provisoire, à adapter quand on aura des statistiques
				int populair = (int)( ( (10000 - i) * ( Math.log(i)/Math.log(1.5) ) + i* ( Math.log(i)/Math.log(1.49)) )/10000  );
				int f = Integer.parseInt(Resultat.getString("featured"));
				populair += 100*f;
				Chaine.append(Serveur.$ + Serveur.$ + Resultat.getString("code")
						+ Serveur.$ + (id !=0 ? Resultat.getString("auteur") : "*")
						+ Serveur.$ + Resultat.getString("titre")
						+ Serveur.$ + populair);
			}
			Envoie(Chaine.toString());
		}catch(Exception e){
			System.err.print("3XC EnvoiePlaylist "); e.printStackTrace();
		}
	}
	
	public void EnvoieCartes(ResultSet Resultat, String identifiantPlaylist)
	{
		try {
			StringBuilder Chaine = new StringBuilder("CxMAP#"+identifiantPlaylist+"#");
			while (Resultat.next()) {
				Chaine.append(Serveur.$ + Serveur.$ + Resultat.getString("id")
						+ Serveur.$ + Resultat.getString("auteur")
						+ Serveur.$ + Resultat.getString("titre")
						+ Serveur.$ + Resultat.getString("votes")
						+ Serveur.$ + Resultat.getString("pour")
						+ Serveur.$ + Resultat.getString("played")
						+ Serveur.$ + Resultat.getString("survie")
						+ Serveur.$ + Resultat.getString("record")
						+ Serveur.$ + Resultat.getString("recordman"));
			}
			Envoie(Chaine.toString());
		}catch(Exception e){
			System.err.print("3XC EnvoieCartes "); e.printStackTrace();
		}
	}

	public void EnvoieCartes(ResultSet Resultat) {
		EnvoieCartes(Resultat, "");
	}

	public void Sauvegarde_Bouboum() {
		if (AutorisationInscription) {
			try {
				PreparedStatement Requete_SauvegardeBouboum = serveur.Bdd.BD.prepareStatement("UPDATE `$joueur` SET sb_pj=?, sb_pg=?, sb_t=?, sb_m=?, sb_r=? WHERE n=? COLLATE NOCASE LIMIT 1");
				Requete_SauvegardeBouboum.setInt(1, _Bouboum.Stats_PartieJouée);
				Requete_SauvegardeBouboum.setInt(2, _Bouboum.Stats_PartieGagnée);
				Requete_SauvegardeBouboum.setInt(3, _Bouboum.Stats_Tué);
				Requete_SauvegardeBouboum.setInt(4, _Bouboum.Stats_Mort);
				Requete_SauvegardeBouboum.setInt(5, _Bouboum.Stats_Ratio);
				Requete_SauvegardeBouboum.setString(6, NomJoueur.toLowerCase());
				Requete_SauvegardeBouboum.execute();
				Requete_SauvegardeBouboum.close();
			} catch (Exception e) {
				System.err.print("3XC SauvegardeBoum "); e.printStackTrace();
			}
		}
	}

	public void Sauvegarde_Aaaah() {
		if (AutorisationInscription) {
			try {
				PreparedStatement Requete_SauvegardeAaaah = serveur.Bdd.BD.prepareStatement("UPDATE `$joueur` SET sa_pj=?, sa_pg=?, sa_js=?, sa_jg=?, sa_p=?, sa_pf=?, sa_t=?, sa_rs=?, sa_rp=?, sa_rt=?, sa_rv=?, sa_e=? WHERE n=? COLLATE NOCASE LIMIT 1");
				_Aaaah.Maj_RatioPremier();
				_Aaaah.Maj_RatioSauvetage();
				_Aaaah.Maj_RatioSurvivant();
				_Aaaah.Maj_RatioTueur();
				_Aaaah.Maj_Elo();

				Requete_SauvegardeAaaah.setInt(1, _Aaaah.Stats_PartieJouée  + _Aaaah.Stats_OldJouées);
				Requete_SauvegardeAaaah.setInt(2, _Aaaah.Stats_PartieGuide);
				Requete_SauvegardeAaaah.setInt(3, _Aaaah.Stats_JoueursSauvés + _Aaaah.Stats_OldSauvés);
				Requete_SauvegardeAaaah.setInt(4, _Aaaah.Stats_JoueursGuidés + _Aaaah.Stats_OldJGuidés);
				Requete_SauvegardeAaaah.setInt(5, _Aaaah.Stats_PartiePremier);
				Requete_SauvegardeAaaah.setInt(6, _Aaaah.Stats_PartieFinie);
				Requete_SauvegardeAaaah.setInt(7, _Aaaah.Stats_Tué);
				Requete_SauvegardeAaaah.setInt(8, _Aaaah.Stats_RatioSauvetage);
				Requete_SauvegardeAaaah.setInt(9, _Aaaah.Stats_RatioPremier);
				Requete_SauvegardeAaaah.setInt(10, _Aaaah.Stats_RatioTueur);
				Requete_SauvegardeAaaah.setInt(11, _Aaaah.Stats_RatioSurvivant);
				Requete_SauvegardeAaaah.setInt(12, _Aaaah.Stats_Elo);
				Requete_SauvegardeAaaah.setString(13, NomJoueur.toLowerCase());
				Requete_SauvegardeAaaah.execute();
				Requete_SauvegardeAaaah.close();
			} catch (Exception e) {
				System.err.print("3XC Sauvegarde Aaaah "); e.printStackTrace();
			}
		}
	}

	public void Deconnexion(String MESSAGE) {
		SelectionKey Clef = Socket.keyFor(ServeurNio.selector);
		ServeurNio.Deconnexion(Clef, MESSAGE);
	}

	public void Deconnexion_Totale(String MESSAGE) {
		
		if (!serveur.ServeurOuvert && serveur.ListeJoueur.size() == 0) {
			// Le dernier joueur va se déconnecter alors que le serveur est fermé => impo a reboot via /quit
			serveur.ServeurOuvert = true;
		}
		
		if (NomJoueur != null) {
			System.out.println("[" + Calendar.getInstance().getTime() + "] " + NomJoueur + " " + AdresseIP + " ### " + MESSAGE);
		} else if (!MESSAGE.equals("Deconnexion")){
			System.out.println("[" + Calendar.getInstance().getTime() + "] " + null + " " + AdresseIP + " ### " + MESSAGE);
		}
		
		if (serveur.SurvivalLance && ParticipeSurvivalKick) {
			ParticipeSurvivalKick = false;
			serveur.CheckFinSurvivalKick();
		}
		
		if (serveur.Evénement.StValentin()) {
			RompreStValentin();
			/*Joueur J = serveur.Joueur(Amoureux);
			if (J != null && J.Amoureux.equals(NomJoueur)) {
				J.Envoie("CxINFO#" + NomJoueur + " a rompu votre union.");
				J.Envoie("CxSTVULINK");
			}*/
		}

		if (SurplusMunitions > 20) {
			System.out.println(NomJoueur + " " + AdresseIP + " --- Munitions infinies : " + SurplusMunitions);
		}
		try {
			try {
				if (AutorisationAdmin) {
					serveur.Message_Modo(NomJoueur + " (Admin) vient de se déconnecter.", true);
				} else {
					if (AutorisationDev) {
						serveur.Message_Modo(NomJoueur + " (Dev) vient de se déconnecter.", true);
					} else if (AutorisationModo) {
						serveur.Message_Modo(NomJoueur + " (Modo) vient de se déconnecter.", true);
					} else if (AutorisationArbitre) {
						serveur.Message_Modo(NomJoueur + " (Arbitre) vient de se déconnecter.", true);
					} else if(AutorisationModoForum) {
						serveur.Message_Modo(NomJoueur + " (Modo forum) vient de se déconnecter.", true);
					} else if(AutorisationRecrueForum){
						serveur.Message_Modo(NomJoueur + " (Recrue forum) vient de se déconnecter.", true);
					}
				}
				if (AutorisationModo && !DecoTricheAuto && !serveur.DecoTricheAuto) {
					serveur.DecoTricheAuto = true;
					serveur.Message_Modo("Bot réactivé automatiquement.", true);
				}
				// on envoie l'information à ceux qui ont ce joueur pour ami
				if (AutorisationInscription) {
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						int NbAmis = Joueur.ListeAmis.size();
						for (int A = 0; A < NbAmis; A++) {
							Ami ami = Joueur.ListeAmis.get(A);
							if (ami.IdAmi == IdJoueur) {
								// envoie message "NomJoueur déconnecté"
								Joueur.Envoie("CxM#" + NomJoueur + "###1");
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				System.err.print("3XC Deconnexion Totale "); e.printStackTrace();
			}

			if (Membre) {
				serveur.Message_Team(this, NomJoueur + " vient de se déconnecter.", IdTeam, true);
			}

			if (!SalonChat.isEmpty()) {
				serveur.Message_SalonChat(this, NomJoueur + " vient de se déconnecter.", true);
			}

			if (AutorisationAdmin) {
				serveur.AbonnementTempsBDD.remove(this);
			}
			serveur.ListeJoueur.remove(this);
			serveur.ListeClient.remove(this);
			serveur.ListeJoueurEnAttenteElo.remove(this);
			// ServeurNio.Deconnexion(Socket, ClefSocket);
			
			// Facteur.Actif = false;
			if (PartieEnCours != null) {
				PartieEnCours.Quitter_partie(this);
			}
			
			if (Afk) {
				((Aaaah)Afk_Partie.getJeu()).QuitterAfk(this);
			}
			
			if (PartieEnAttente != null && PartieEnAttente.AttenteEloPartie != null) {
				// Était en attente sur une partie elo
				PartieEnAttente.AttenteEloPartie.Joueur_Quitte_Attente_Elo(this, true);
			}
			
			/// Anti-ghost test
			while (serveur.ListeJoueurSansPartie.remove(this)) {
				serveur.EnvoieCP("CxX#" + NomJoueur);
			}
			
			///
			/*
			if (serveur.ListeJoueurSansPartie.remove(this)) {
				serveur.EnvoieCP("CxX#" + NomJoueur);
			}*/
			//
			
			if (Socket != null) {
				synchronized (serveur.ListeClefSocketJoueur) {
					serveur.ListeClefSocketJoueur.remove(Socket);
				}

				Socket.socket().close();
				Socket.close();
			}

			//
			if (AutorisationInscription) {
				_Aaaah.Maj_RatioPremier();
				_Aaaah.Maj_RatioSauvetage();
				_Aaaah.Maj_RatioSurvivant();
				_Aaaah.Maj_RatioTueur();
				_Aaaah.Maj_Elo();
				
				int[] Stats = new int[17];
				Stats[0] = _Bouboum.Stats_PartieJouée;
				Stats[1] = _Bouboum.Stats_PartieGagnée;
				Stats[2] = _Bouboum.Stats_Tué;
				Stats[3] = _Bouboum.Stats_Mort;
				Stats[4] = _Bouboum.Stats_Ratio;
				Stats[5] = _Aaaah.Stats_PartieJouée + _Aaaah.Stats_OldJouées;
				Stats[6] = _Aaaah.Stats_PartieGuide;
				Stats[7] = _Aaaah.Stats_JoueursSauvés + _Aaaah.Stats_OldSauvés;
				Stats[8] = _Aaaah.Stats_JoueursGuidés + _Aaaah.Stats_OldJGuidés;
				Stats[9] = _Aaaah.Stats_PartiePremier;
				Stats[10] = _Aaaah.Stats_PartieFinie;
				Stats[11] = _Aaaah.Stats_Tué;
				Stats[12] = _Aaaah.Stats_RatioSauvetage;
				Stats[13] = _Aaaah.Stats_RatioPremier;
				Stats[14] = _Aaaah.Stats_RatioTueur;
				Stats[15] = _Aaaah.Stats_RatioSurvivant;
				Stats[16] = _Aaaah.Stats_Elo;
				serveur.BOITE.Requete(Boite.SAUVEGARDE_JOUEUR, this, Stats);
				this.Stats.save(serveur.BOITE,this);

				if (serveur.Maraboum) {
					if (Stats_TempsDeJeu > 36000000 && Mara_TempsJeu > 72000) { // rajouter un 0
						if (!Mara_Activé) {
							serveur.BOITE.Requete(Boite.CREER_MARA, this);
						}
						if (!Mara_Désactivé) {
							if (Mara_TempsBan <= 72) {
								serveur.BOITE.Requete(Boite.SAUVEGARDE_MARA, this);
							} else {
								serveur.BOITE.Requete(Boite.SAUVEGARDE_MARA_BANNI, this);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Deco totale 2 "); e.printStackTrace();
		}
	}
	
	public ArrayList<String> getLsArbitreTournoi(String arg) {
		if (arg.equals("a")) {
			return serveur.ListeArbitresA;
		} else if (arg.equals("b")) {
			return serveur.ListeArbitresB;
		} else if (arg.equals("f")) {
			return serveur.ListeArbitresF;
		}
		
		return null;
	}

	public void Envoie(String MESSAGE) {
		String Message = MESSAGE + '\u0000';
		
		byte[] Donnée = Message.getBytes();
		ListeMessage.addFirst(ByteBuffer.wrap(Donnée));
		SelectionKey key = Socket.keyFor(ServeurNio.selector);
		try {
			if (key != null && key.isValid() && (key.interestOps() & SelectionKey.OP_WRITE) != SelectionKey.OP_WRITE) {
				key.channel().register(ServeurNio.selector, SelectionKey.OP_WRITE);
			}
		} catch (Exception e) {
			System.err.print("3XC Envoie "); e.printStackTrace();
			Deconnexion("Erreur envoie données");
		}
	}

	public boolean StatsActivées() {
		return NoStats == 0;
	}
	
	public boolean NewStatsActivées() {
		//return NoStats == 0;
		return true;
	}
	
	public String getPseudo() {
		return NomJoueur;
	}
	
	public Aaaah getPartieAaaah() {
		if (PartieEnCours != null && PartieEnCours.PAaaah) {
			return (Aaaah)PartieEnCours.getJeu();
		}
		
		return null;
	}
	
	public Bouboum getPartieBouboum() {
		if (PartieEnCours != null && PartieEnCours.Bouboum) {
			return (Bouboum)PartieEnCours.getJeu();
		}
		
		return null;
	}
	
	public Forteresse getPartieForto() {
		if (PartieEnCours != null && PartieEnCours.Forto) {
			return (Forteresse)PartieEnCours.getJeu();
		}
		
		return null;
	}
	
	public static boolean PseudoConforme(String pseudo) {
		Pattern pattern = Pattern.compile("[^a-zA-Z]");
		
		/*if (Serveur.getServeur().Evénement.PoissonAvril()) {
			pattern = Pattern.compile("[^a-zA-Z0-9\\_]");
		}*/
		
		Matcher matcher = pattern.matcher(pseudo);
		
		return !(matcher.find() || pseudo.length() < 1 || pseudo.length() > 12);
	}
}
