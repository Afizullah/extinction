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

package aaaah;

import java.util.ArrayList;
import java.util.Calendar;

import elo.Elo;
import joueur.Joueur;
import serveur.Boite;
import serveur.Jeu;
import serveur.Partie;
import serveur.Serveur;

public class Aaaah extends Jeu {
	public final static String $ = "#";
	public Joueur Guide;
	public String GuidePermanent = ""; // Pseudo du joueur | "1" si demande de no guide en cours | "0" si no guide
	private ArrayList<Joueur> ListeAfk = new ArrayList<Joueur>();
	public Serveur.Action TimerPartie;
	public int JoueurEnVie;
	public long MondeEnCours = -1;
	public long MondePrecedent = -1;
	Carte InfoCarteCourante;
	public long ProchainMonde = -1;
	public ArrayList<Long> BoucleMonde = new ArrayList<Long>();
	public ArrayList<Long> BoucleMondeAncien = new ArrayList<Long>();
	public int JoueurDepart = 0;
	public int JoueurSauve = 0;
	public int TempsParPartie = 120000;
	private int PointBonus = 0; 
	public boolean Playlist = false;
	public boolean PlaylistAléatoire = true;
	public ArrayList<Long> BouclePlaylist = new ArrayList<Long>();
	private boolean StatsActive = false;
	private boolean NewStatsActive = false;
	private boolean RecordActive = false;
	private boolean RecordRallyActive = false;
	public boolean MaraActive = false;
	public boolean MaraNGActive = false;
	public boolean SansConta = false;
	public boolean LoadAutorisé = false;
	public boolean PartiePrivée = false;
	public boolean LoadUtilisé = false;
	public boolean GuidePourNG = true;
	public boolean SalonFight = false;
	private boolean DemandeVote = false;
	private boolean VoteEnCours = false;
	public int VoteOui;
	public int VoteNon;
	public boolean ForcerConta = false;
	public boolean SalonTest = false;
	public String CarteTest;
	public String DernierRecordman = "";
	public int BoucleCarte = 0;
	public Joueur FuturContamine;
	private Serveur.Action TempsPreContamination;
	private Serveur.Action TempsContamination;
	private ParametresCri ParamCri;
	
	// Halloween
	private ArrayList<Joueur> lsMortsDurantPartie = new ArrayList<Joueur>(); // Stocke les joueurs morts en jouant en pouvant donc être ressucités sur la map offi halloween
	//

	public Aaaah(Serveur serveur, Partie partieMere) {
		super(serveur, partieMere, 120000);
		//serveur = SERVEUR;
	}

	public void Destruction() {
		Guide = null;
		if (TimerPartie != null) {
			TimerPartie.Active = false;
			TimerPartie = null;
		}
		BoucleMonde = null;
		CarteTest = null;
		
		IndiquerDestructionPartieAfk(); // Dire aux afks qu'ils sont sur le CP
	}

	public void Reception(Joueur JOUEUR, String STRING, boolean MessagePerimé) {
		//System.out.println(NomJoueur + " --> " + STRING);
		//
		String[] Message = STRING.split($);
		String Code = Message[0];
		String Code1 = Message[0].substring(0, 2);

		if (Code1.equals("Id")) {
			// Pousse
			if (Code.equals("IdB")) {
				if (JOUEUR.Vivant) {
					long Temps = System.currentTimeMillis();
					if (JOUEUR._Aaaah.PeurDispo && Temps - JOUEUR._Aaaah.IntervalPeur > ParamCri.DureeSafeEntreCris()) { // HERE
						JOUEUR._Aaaah.IntervalPeur = Temps;
						serveur.Envoie(ListeJoueur, STRING + "#" + JOUEUR.NomJoueur);
					}
				}
				return;
			}
			// projection
			if (Code.equals("IdA")) {
				if (MessagePerimé) {
					return;
				}
				if (JOUEUR.Vivant) {
					serveur.Envoie(ListeJoueur, STRING + "#" + JOUEUR.NomJoueur);
				}
				return;
			}
			// Lancement Moteur
			if (Code.equals("IdM")) {
				JOUEUR.Envoie(Positions());
				return;
			}		
			// Mort
			if (Code.equals("IdX")) {
				if (MessagePerimé) {
					return;
				}

				if (JOUEUR.Vivant) {
					JOUEUR.Vivant = false;
					JOUEUR._Aaaah.Score--;
					if (JOUEUR._Aaaah.Score < 0) {
						JOUEUR._Aaaah.Score = 0;
					}
					if (Message.length == 3) {
						String NomTueur = Message[1];
						int Nb = ListeJoueur.size();
						boolean trouve = false;
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = ListeJoueur.get(i);
							if (Joueur.NomJoueur.equals(NomTueur)) {
								trouve = true;
								if (StatsActive && Joueur.StatsActivées()) {
									Joueur._Aaaah.Stats_Tué++;
								}
								if (NewStatsActive && Joueur.NewStatsActivées()) {
									Joueur.Stats.NouveauKill(ModeJeu);
								}
								Joueur._Aaaah.Score++;
								if (MaraActive) {
									if (Joueur.EnFight()) {
										Joueur.Mara_Fight_Tués++;
									} else if (Joueur.EnDéfilantes()) {
										Joueur.Mara_Défilantes_Tués++;
									}

								}
								
								
								if (serveur.Evénement.StValentin()) {
									if (serveur.SontAmoureux(Joueur, JOUEUR)) { // A tué son amoureux = suicide
										serveur.Envoie(ListeJoueur, "IdX#" + Joueur.NomJoueur);
										Joueur.Envoie("CxINFO#Votre chagrin vous a éliminé.");
									} else {
										Joueur J = serveur.TrouverAmoureux(ListeJoueur, JOUEUR);
										if (J != null) {
											J.Envoie("CxINFO#" + Joueur.NomJoueur + " a tué votre amour ! Vengez-vous !");
											Contamination(J);
										}
									}
								}
								break;
							}
						}
						
						if (!trouve && serveur.Joueur(NomTueur) == null) { // Triche de Tektonit
							serveur.Avertissement_Modo("[Aaaah] " + JOUEUR.NomJoueur + " a été tué par un joueur pas (plus ?) connecté (" + NomTueur + ").", false);
						} else {
							serveur.Envoie(ListeJoueur, STRING + "#" + JOUEUR.NomJoueur);
						}
					} else {
						serveur.Envoie(ListeJoueur, "IdX#" + JOUEUR.NomJoueur);
					}
					if (!JOUEUR._Aaaah.Zombie) {
						JoueurEnMoins();
					}
					
					// Halloween
					if (serveur.Evénement.Halloween()) {
						if (MondeEnCours == Serveur.MondeHalloweenAaaah) {
							lsMortsDurantPartie.add(JOUEUR);
						}
					}
					//
					// skip la partie s'il ne reste qu'un survivant et aucun contaminé
					CheckDernierJoueur();
				}
				return;
			}
			// Victoire
			if (Code.equals("IdW")) {
				if (MessagePerimé) {
					return;
				}
				
				// St Valentin - on détermine si <JOUEUR> a un lié encore en vie à faire win avec lui
				// Note : Ne pas déplacer, Victoire() termine la partie et reset les infos des joueurs ce qui
				//        empêche de voir si la personne est éligible sinon !
				Joueur Amoureux = null;
				if (serveur.Evénement.StValentin()) {
					Amoureux = serveur.TrouverAmoureux(ListeJoueur, JOUEUR);
					if (Amoureux == null || !Amoureux.Vivant || Amoureux._Aaaah.Zombie || Amoureux._Aaaah.Guide) {
						Amoureux = null;
					}
				}
				
				Victoire(JOUEUR);
				
				if (Amoureux != null) { // Win du lié (St Valentin)
					Victoire(Amoureux);
				}
				
				return;
			}
			//
			JOUEUR.Deconnexion("Code inconnu");
		}

		// Dessin
		if (Code1.equals("De")) {
			if (MessagePerimé) {
				return;
			}

			if (JOUEUR._Aaaah.Guide) {
				// Dessin
				if (Code.equals("DeT")) {
					serveur.Envoie(ListeJoueur, STRING);
					return;
				}
				// Départ dessin
				if (Code.equals("DeS")) {
					serveur.Envoie(ListeJoueur, STRING);
					return;
				}
			}
			return;
		}

		// Mouvement
		if (Code1.equals("Mv")) {
			if (MessagePerimé) {
				return;
			}

			if (JOUEUR.Vivant) {
				JOUEUR._Aaaah.PosX = Message[1];
				JOUEUR._Aaaah.PosY = Message[2];
				serveur.Envoie(ListeJoueur, STRING + "#" + JOUEUR.NomJoueur);
			}
			return;
		}

		// Contamination
		if (Code1.equals("Zo")) {
			if (MessagePerimé) {
				return;
			}

			// Joueur contaminé
			if (Code.equals("ZoC")) {
				if (System.currentTimeMillis() - DebutPartie < 1000) {
					return;
				}
				
				
				String NomTueur = Message[1];
				int Nb = ListeJoueur.size();
				Joueur Joueur = null;
				for (int i = 0; i < Nb; i++) {
					Joueur = ListeJoueur.get(i);
					if (Joueur.NomJoueur.equals(NomTueur)) {
						break;
					}
				}
				
				if (SansConta && !(serveur.Evénement.StValentin() && Joueur != null && Joueur._Aaaah.Zombie)) {
					System.out.println("Triche " + JOUEUR.NomJoueur + " conta en salon non conta");
					return;
				}
				Contamination(JOUEUR);
				//
				
				if (Joueur == null) {
					return;
				}
				
				Joueur._Aaaah.Score++;
				if (StatsActive && Joueur.StatsActivées()) {
					Joueur._Aaaah.Stats_Tué++;
				}
				if (NewStatsActive && Joueur.NewStatsActivées()) {
					Joueur.Stats.NouveauKill(ModeJeu);
				}
				if (MaraActive && Joueur.EnFight()) {
					Joueur.Mara_Fight_Tués++;
				}
				
				/*String NomTueur = Message[1];
				int Nb = ListeJoueur.size();
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = ListeJoueur.get(i);
					if (Joueur.NomJoueur.equals(NomTueur)) {
						Joueur._Aaaah.Score++;
						if (StatsActive && Joueur.StatsActivées()) {
							Joueur._Aaaah.Stats_Tué++;
						}
						if (NewStatsActive && Joueur.NewStatsActivées()) {
							Joueur.Stats.NouveauKill(ModeJeu);
						}
						if (MaraActive && Joueur.EnFight()) {
							Joueur.Mara_Fight_Tués++;
						}
						break;
					}
				}*/
				return;
			}
			return;
		}

		// Sauvegarde ghost
		if (Code.equals("CxRecGh")) {
			// format is CxRecGh#@1420393815335#<code ghost>
			if (DernierRecordman.equalsIgnoreCase(JOUEUR.NomJoueur)
					&& InfoCarteCourante != null 
					&& InfoCarteCourante.Id.equals(Long.parseLong(Message[1]))
					&& Message.length == 3
					&& !Message[2].isEmpty()) {				
				InfoCarteCourante.Ghost = Message[2];
				serveur.BOITE.Requete(Boite.UPDATE_GHOST_MAP, JOUEUR, InfoCarteCourante.Ghost, InfoCarteCourante.Id.toString());
			}
			return;
		}	
		
		// Chat
		if (Code1.equals("Ch")) {
			if (Message.length == 1) {
				return;
			}
			// Message
			if (Code.equals("ChM")) {
				if (JOUEUR.AutorisationInscription) {
					if (System.currentTimeMillis() - JOUEUR.LimiteChat < 400) {
						//JOUEUR.Deconnexion("Flood.");
						return;
					}
					JOUEUR.LimiteChat = System.currentTimeMillis();
					if (JOUEUR.Muet) {
						JOUEUR.Envoie("CxMuet");
					} else {
						if (Message.length == 2) {
							if (JOUEUR.PartieEnCours.ChatFerme) {
								JOUEUR.Envoie("CxF");
							} else {
								// Envoi à tout le salon
								STRING = serveur.FixeDoubleEsperlouette(STRING, JOUEUR);
								serveur.Envoie_Chat(JOUEUR, ListeJoueur, STRING + "#" + JOUEUR.NomJoueur);
							}
						} else {
							JOUEUR.Deconnexion("Triche.");
						}	
					}
				}
				return;
			}
			//
			JOUEUR.Deconnexion("Code inconnu");
		}

		// Editeur
		if (Code1.equals("Ed")) {
			// Vote
			if (Code.equals("EdV")) {
				if (JOUEUR._Aaaah.VoteDisponible) {
					JOUEUR._Aaaah.VoteDisponible = false;
					if (Message.length == 2) {
						VoteOui++;
					} else {
						VoteNon++;
					}
				}
				return;
			}
			// Test de la carte
			if (Code.equals("EdT")) {
				if (JOUEUR.AutorisationInscription) {
					CarteTest = Message[1];
					Nouvelle_Partie(-1);
				}
				return;
			}
			// Initialisation
			if (Code.equals("EdI")) {
				if (JOUEUR.AutorisationInscription) {
					JOUEUR.PartieEnCours.Quitter_partie(JOUEUR);
					JOUEUR._Aaaah.CodeCarte = 0;
					serveur.Rejoindre_Partie(JOUEUR, 2, "Editeur Aaaah", "#", true);
					JOUEUR.Envoie("EdI#" + (JOUEUR.Stats_TempsDeJeu > 540000000?"1":"0")); // 150 heures
				}
				return;
			}
			// Sauvegarde
			if (Code.equals("EdS")) {
				if (JOUEUR.AutorisationInscription) {
					String Carte = Message[1];
					String Titre = Message[2];
					int Flag = Integer.valueOf(Message[3]);
					if (Flag==1){
						//pas officielle
						System.out.println("Client modifié carte officielle");
						Flag = 0;
					}
					if (JOUEUR._Aaaah.CodeCarte == 0) {
						JOUEUR._Aaaah.CodeCarte = Calendar.getInstance().getTimeInMillis();
						Carte InfoCarte = new Carte(JOUEUR._Aaaah.CodeCarte,
								Carte, // Code
								0, // Votes
								0, // Pour
								0, // Perma
								120000, // Record
								"-", // Recordman
								0,// played
								0, // survie
								JOUEUR.NomJoueur,
								Titre,
								Flag,
								120000,
								"-",
								0,
								"");

						serveur.Aaaah_ListeCarte.put(JOUEUR._Aaaah.CodeCarte, InfoCarte);
						serveur.BOITE.Requete(Boite.CREATE_MAP, JOUEUR, String.valueOf(JOUEUR._Aaaah.CodeCarte), Carte, Titre, Flag);
					} else {
						Carte InfoCarte = serveur.Aaaah_ListeCarte.get(JOUEUR._Aaaah.CodeCarte);
						if (InfoCarte!=null) {
							String Auteur = InfoCarte.Auteur;
							if (Auteur.equalsIgnoreCase(JOUEUR.NomJoueur) || JOUEUR.AutorisationModo || JOUEUR.AutorisationModoCartes) {
								InfoCarte.Code = Carte;
								InfoCarte.Titre = Titre;
								InfoCarte.Mode = Flag;
								serveur.Aaaah_ListeCarte.put(JOUEUR._Aaaah.CodeCarte, InfoCarte);
								serveur.BOITE.Requete(Boite.UPDATE_MAP, JOUEUR, String.valueOf(JOUEUR._Aaaah.CodeCarte), Carte, Titre, Flag);
							}
						}
					}
					JOUEUR.Envoie("EdS#" + JOUEUR._Aaaah.CodeCarte);
				}
				return;
			}
			//
			if (Code.equals("EdN")) {
				JOUEUR._Aaaah.CodeCarte = 0;
				return;
			}
			// Chargement
			if (Code.equals("EdC")) {
				if (JOUEUR.AutorisationInscription && Message.length == 2) {
					try {
						String CodeString = Message[1];
						CodeString = CodeString.replaceAll("@", "");
						CodeString = CodeString.trim();
						if (Joueur.isLong(CodeString)) {
							Long CodeCarte = Long.parseLong(CodeString);
							if (serveur.Aaaah_ListeCarte.containsKey(CodeCarte)) {
								Carte InfoCarte = serveur.Aaaah_ListeCarte.get(CodeCarte);
								String Auteur = InfoCarte.Auteur;
								if (Auteur.equalsIgnoreCase(JOUEUR.NomJoueur) || JOUEUR.AutorisationModo || JOUEUR.AutorisationModoCartes) {
									JOUEUR._Aaaah.CodeCarte = CodeCarte;
									JOUEUR.Envoie("EdC#" + InfoCarte.Code + "#" + InfoCarte.Votes + "#" + InfoCarte.Pour + "#" + InfoCarte.Record + "#" + InfoCarte.Recordman + "#" + InfoCarte.Played + "#" + InfoCarte.Survie + "#" + JOUEUR._Aaaah.CodeCarte +  "#" + InfoCarte.Auteur);
								} else {
									JOUEUR.Envoie("EdC#Non");
								}
							}else{
								JOUEUR.Envoie("EdC");
							}
						} else {
							JOUEUR.Envoie("EdC");
						}
					} catch (Exception e) {
						System.err.print("3XC EdC "); e.printStackTrace();
						JOUEUR.Envoie("EdC");
					}
				}
				return;
			}
			//
			JOUEUR.Deconnexion("Code inconnu");
		}
		
		// Message spécial événement
		if (serveur.Evénement.EvenementEnCours() && Code1.equals("Ev")) {
			if (MessagePerimé) {
				return;
			}
			
			if (serveur.Evénement.Halloween() && Code.equals("EvHLWNRGN")) {
				if (MondeEnCours == Serveur.MondeHalloweenAaaah) {
					if (lsMortsDurantPartie.contains(JOUEUR)) { // Résurrection en diable
						serveur.Envoie(ListeJoueur, "EvHLWNRGN#" + JOUEUR.NomJoueur + "#" + JOUEUR._Aaaah.PosX + "#" + JOUEUR._Aaaah.PosY);
						
						// Contamination (diabolisation) du joueur mort
						JOUEUR._Aaaah.Zombie = false;
						JOUEUR.Vivant = true;
						JoueurEnVie++;
						Contamination(JOUEUR);
					}
					
				}
				return;
			}
		}
		//
		JOUEUR.Reception_Spéciale(STRING);
	}

	public void Nouvelle_Partie(long MONDE) {
		Nouvelle_Partie(MONDE, false);
	}
	public void Nouvelle_Partie(long MONDE, boolean loadDirectement) {
		
		MondePrecedent = MondeEnCours;
		
		if (TimerPartie != null) {
			TimerPartie.Active = false;
		}
		if (TempsContamination != null) {
			TempsContamination.Active = false;
		}
		if (TempsPreContamination != null) {
			TempsPreContamination.Active = false;
		}
		//
		if (VoteEnCours && !SalonTest) {
			// Fin du vote
			try {
				Carte InfoCarte = serveur.Aaaah_ListeCarte.get(MondeEnCours);
				if (InfoCarte!=null) {
					int VoteTotal = InfoCarte.Votes + VoteNon + VoteOui;
					int VotePositif = InfoCarte.Pour + VoteOui;
					InfoCarte.Votes = VoteTotal;
					InfoCarte.Pour = VotePositif;
					if (InfoCarte.Perma == 0 && InfoCarte.Mode == Carte.MODE_NORMAL) {
						if (VoteTotal > 50) {
							if ((double) VotePositif / (double) VoteTotal < 0.5) {
								serveur.BOITE.Requete(Boite.DELETE_MAP, null, String.valueOf(MondeEnCours), "V");
							}
						}
					}
				}

			} catch (Exception e) {
				System.err.print("3XC Nouvelle_Partie "); e.printStackTrace();
			} finally {
				VoteEnCours = false;
			}
		}
		if (DemandeVote && !SalonTest && !loadDirectement) {
			// On lance le vote
			DemandeVote = false;
			try {
				VoteEnCours = true;
				VoteOui = 0;
				VoteNon = 0;
				if (!LoadUtilisé && !Playlist) {
					// On prépare le vote
					Carte InfoCarte = serveur.Aaaah_ListeCarte.get(MondeEnCours);
					String Texte = "EdV#" + InfoCarte.Votes + "#" + InfoCarte.Pour;
					int Nb = ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = ListeJoueur.get(i);
						Joueur._Aaaah.VoteDisponible = true;
						Joueur.Envoie(Texte);
					}
				} else {
					// On annonce la prochaine partie sans vote
					serveur.Envoie(ListeJoueur,"CxPP");
					super.Nouvelle_Partie();
				}
				//
				ProchainMonde = MONDE;
				TimerPartie = serveur.new Action(System.currentTimeMillis() + 10000);
				TimerPartie.NouvellePartieAaaah = true;
				TimerPartie.PartieAaaah = this;
			} catch (Exception e) {
				VoteEnCours = false;
				Nouvelle_Partie(MONDE);
			}
			return; // On s'arrête là s'il s'agit de lancer le vote
		}
		
		// survivant fight
		if (NewStatsActive && ModeJeu == Carte.MODE_FIGHT) {
			try {
				int Nb = ListeJoueur.size();
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = ListeJoueur.get(i);
					if (Joueur.NewStatsActivées() && Joueur.Vivant && !Joueur._Aaaah.Zombie) {
						Joueur.Stats.FS.Survie++;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception in Nouvelle_Partie with fight stats");
			}
		}
		
		LoadUtilisé = MONDE != -1;

		// sauvegarde stats
		if (serveur.Aaaah_ListeCarte.containsKey(MondeEnCours)) {
			Carte InfoCarte = serveur.Aaaah_ListeCarte.get(MondeEnCours);
			String Code = String.valueOf(MondeEnCours);
			String Votes = String.valueOf(InfoCarte.Votes);
			String Pour = String.valueOf(InfoCarte.Pour);
			String Record = String.valueOf(InfoCarte.Record);
			String Recordman = InfoCarte.Recordman;
			String Played = String.valueOf(InfoCarte.Played);
			String Survie = String.valueOf(InfoCarte.Survie);
			String Mara_Record = String.valueOf(InfoCarte.Mara_Record);
			String Mara_Recordman = InfoCarte.Mara_Recordman;
			String Mara_Played = String.valueOf(InfoCarte.Mara_Played);
			serveur.BOITE.Requete(Boite.UPDATE_STATS_MAP, null, new String[] { Code, Votes, Pour, Record, Recordman, Played, Survie, Mara_Record, Mara_Recordman, Mara_Played });
		}


		FuturContamine = null;
		if (!SansConta) {
			TempsPreContamination = serveur.new Action(System.currentTimeMillis() + 15000 + (int) (Math.random() * 30000));
			TempsPreContamination.PreContamination = true;
			TempsPreContamination.PartieAaaah = this;
		}
		ProchainMonde = -1;
		//
		ArrayList<Joueur> Joueurs = ListeJoueur;
		int NbJoueur = Joueurs.size();
		if (NbJoueur == 0) {
			return;
		}
		if (SalonTest && CarteTest == null) {
			return;
		}
		boolean UnSeulJoueur = NbJoueur == 1;

		ArrayList<String> ListeIP = new ArrayList<String>();
		//
		for (int i = 0; i < NbJoueur; i++) {
			Joueur Joueur = ListeJoueur.get(i);
			if (!ListeIP.contains(Joueur.AdresseIP)){
				ListeIP.add(Joueur.AdresseIP);
			}
		}

		boolean GuideSuperflu = GuidePourNG && ModeJeu!=Carte.MODE_NORMAL && ModeJeu!=Carte.MODE_OFFI; // pas de stats si un guide est là en NG
		StatsActive = NbJoueur > 7 && ListeIP.size() > 7 && !SansConta && !LoadAutorisé;
		NewStatsActive = NbJoueur > 6 && ListeIP.size() > 6 && !PartiePrivée && !LoadUtilisé && !Playlist && !GuideSuperflu;
		RecordActive = NbJoueur > 6 && ListeIP.size() > 6 && !PartiePrivée && !LoadUtilisé && !Playlist && !GuideSuperflu;
		RecordRallyActive = (NbJoueur > 1 && ListeIP.size() > 1 && !PartiePrivée && !LoadUtilisé && !Playlist && !GuideSuperflu && ModeJeu == Carte.MODE_RALLY)
				|| (GuidePermanent.equals("0") && !PartiePrivée && !LoadUtilisé && !Playlist && !GuideSuperflu && ModeJeu == Carte.MODE_RALLY);
		
		MaraActive = serveur.Maraaaathon && NbJoueur > 9 && ListeIP.size() > 9 && ( SalonFight || (!LoadUtilisé && !Playlist)) && !PartiePrivée;
		if (serveur.LOCAL) {
			MaraActive = ( SalonFight || (!LoadUtilisé && !Playlist)) && !PartiePrivée;
			StatsActive = true;
			NewStatsActive = true;
			RecordActive = true;
			RecordRallyActive = true;
		}
		//
		if (MONDE == -1) {
			if (BoucleCarte % 6 == 5 && (ModeJeu == Carte.MODE_NORMAL || ModeJeu == Carte.MODE_OFFI) && !Playlist) {
				///BoucleCarte = 0;
				if (BoucleMondeAncien.isEmpty()) {
					ArrayList<Long> Base = new ArrayList<Long>(serveur.Aaaah_ListeMonde);
					while (Base.size() != 0) {
						int Valeur = (int) (Math.random() * Base.size());
						BoucleMondeAncien.add(Base.get(Valeur));
						Base.remove(Valeur);
					}
				}
				
				MondeEnCours = BoucleMondeAncien.get(0);
				BoucleMondeAncien.remove(0);
			} else {
				///
				//BoucleCarte++;
				while (true) {
					if (BoucleMonde.isEmpty()) {
						new LancementPartieAaaah(this);
						int Valeur = (int) (Math.random() * serveur.Aaaah_ListeMonde.size());
						BoucleMonde.add(serveur.Aaaah_ListeMonde.get(Valeur));
						if (ModeJeu!=Carte.MODE_NORMAL&&ModeJeu!=Carte.MODE_OFFI) {
							serveur.Envoie(Joueurs, "CxINFO#Les cartes spécifiques seront chargées à la fin de cette partie.");
						}

					}
					MondeEnCours = BoucleMonde.get(0);
					BoucleMonde.remove(0);
					if (MondeEnCours > serveur.Aaaah_NombreMonde) {
						if (serveur.Aaaah_ListeCarte.get(MondeEnCours) == null) {
							continue;
						} else {
							break;
						}
					} else {
						break;
					}
				}
			}
			
			BoucleCarte++;
		} else {
			MondeEnCours = MONDE;
		}


		if (Guide != null) {
			Guide._Aaaah.Guide = false;
			if (JoueurDepart != 0) {
				float JoueurPerdu = JoueurDepart - JoueurSauve;
				float MalusSystematique = 0.10f;
				int PointPerdu = (int) (Guide._Aaaah.Score * (MalusSystematique + JoueurPerdu / JoueurDepart));
				Guide._Aaaah.Score -= PointPerdu;
				if (Guide._Aaaah.Score < 0) {
					Guide._Aaaah.Score = 0;
				}
				serveur.Envoie(Joueurs, "ChP#" + PointPerdu + "#" + JoueurSauve + "#" + JoueurPerdu);
			}
		}
		//
		PointBonus = 3;
		DebutPartie = System.currentTimeMillis();
		//
		PartieEnCours = true;
		JoueurEnVie = 0;

		// Map courante
		InfoCarteCourante = serveur.Aaaah_ListeCarte.get(MondeEnCours);

		// Choix Guide
		Joueur GuideEnCours = null;
		int Nb = Joueurs.size();
		for (int J = 0; J < Nb; J++) {
			Joueur Joueur = Joueurs.get(J);
			Joueur._Aaaah.Zombie = false;
			Joueur._Aaaah.CodeZombie = "0";
			if (GuidePourNG || (InfoCarteCourante!=null&&!InfoCarteCourante.isNoGuide()&&ModeJeu!=Carte.MODE_NG&&ModeJeu!=Carte.MODE_RALLY&&ModeJeu!=Carte.MODE_DEF&&ModeJeu!=Carte.MODE_MS&&ModeJeu!=Carte.MODE_FIGHT)) {

				if (GuideEnCours == null) {
					GuideEnCours = Joueur;
				} else {
					if (Joueur.NomJoueur.equalsIgnoreCase(GuidePermanent)) {
						GuideEnCours = Joueur;
					} else {
						if (GuidePermanent.equals("")) {
							if (Joueur._Aaaah.Score > GuideEnCours._Aaaah.Score) {
								GuideEnCours = Joueur;
							} else if (Joueur._Aaaah.Score == GuideEnCours._Aaaah.Score) {
								// un peu d'aléatoire
								int NumGuideHazard = (int) (Math.random() * Joueurs.size());
								if (NumGuideHazard==1) {
									GuideEnCours = Joueur;
								}
							}
						}
					}
				}
			}

		}
		
		// Pas de guide sur la map Hell Box (offi Halloween)
		if (serveur.Evénement.Halloween() && MondeEnCours == Serveur.MondeHalloweenAaaah) {
			GuideEnCours = null;
		}


		// Infos Map
		if (InfoCarteCourante != null) {
			if (ModeJeu == Carte.MODE_NG) { 
				serveur.Envoie(ListeJoueur, "IdIC#" + InfoCarteCourante.Mara_Record + "#" + InfoCarteCourante.Mara_Recordman + "#" + InfoCarteCourante.Played + "#" + InfoCarteCourante.Survie + "#0#"+InfoCarteCourante.Id);
			} else if (ModeJeu == Carte.MODE_RALLY) {
				serveur.Envoie(ListeJoueur, "IdIC#" + InfoCarteCourante.Record + "#" + InfoCarteCourante.Recordman + "#" + InfoCarteCourante.Played + "#" + InfoCarteCourante.Survie + "#0#"+InfoCarteCourante.Id+"#" + InfoCarteCourante.Ghost);
			} else {
				serveur.Envoie(ListeJoueur, "IdIC#" + InfoCarteCourante.Record + "#" + InfoCarteCourante.Recordman + "#" + InfoCarteCourante.Played + "#" + InfoCarteCourante.Survie+"#0"+InfoCarteCourante.Id);
			}
			if (StatsActive && !SalonFight) {
				InfoCarteCourante.Played += Nb;
			}
			if (MaraActive && ModeJeu == Carte.MODE_NG) {
				InfoCarteCourante.Mara_Played += Nb;
			}
		}

		//
		JoueurDepart = 0;
		JoueurSauve = 0;
		//
		Guide = null;
		
		if (ModeJeu == Carte.MODE_MS) {
			int NbJ = Joueurs.size();
			for (int i = 0; i < NbJ; i++) {
				Joueur Joueur = Joueurs.get(i);
				if (Joueur.NewStatsActivées()) {
					Joueur.Stats.MS.JoueursGuidés += NbJoueur;
				}
			}
		}	
		
		StringBuilder LsJoueur = new StringBuilder();
		int NbJ = Joueurs.size();
		for (int i = 0; i < NbJ; i++) {
			Joueur Joueur = Joueurs.get(i);
			String Vivant = "1";
			if (Joueur.equals(GuideEnCours) && !UnSeulJoueur) {
				Guide = Joueur;
				Joueur._Aaaah.Guide = true;
				Joueur.Vivant = false;
				Joueur._Aaaah.PeurDispo = false;
				Vivant = "0";
				if (StatsActive && Joueur.StatsActivées()) {
					Joueur._Aaaah.Stats_PartieGuide++;
					Joueur._Aaaah.Stats_JoueursGuidés += NbJoueur - 1;
				}
				if (NewStatsActive && Joueur.NewStatsActivées()) {
					Joueur.Stats.GDM.NouvellePartie();
					Joueur.Stats.GDM.JoueursGuidés += NbJoueur - 1;
				}
				if (MaraActive && Joueur.EnOfficiel()) {
					Joueur.Mara_Guide_PartiesGuidées++;
					Joueur.Mara_Guide_JoueurGuidées += NbJoueur - 1;
				}
			} else {
				if (UnSeulJoueur && (!GuidePermanent.equals("0") && !GuidePermanent.equals("1"))) {
					Guide = Joueur;
					Joueur._Aaaah.Guide = true;
				} else {
					if (GuidePermanent.equals("1")) {
						Guide = null;
						GuidePermanent = "0";
					} else {
						if (StatsActive && Joueur.StatsActivées()) {
							Joueur._Aaaah.Stats_PartieJouée++;
							//Joueur._Aaaah.Stats_Elo-=10;
						}
						if (NewStatsActive && Joueur.NewStatsActivées()) {
							Joueur.Stats.NouvellePartie(ModeJeu);					
						}
						if (MaraActive) {
							if (Joueur.EnOfficiel()) {
								Joueur.Mara_Run_PartiesJouées++;
							} else if (Joueur.EnFight()) {
								Joueur.Mara_Fight_PartiesJouées++;
							} else if (Joueur.EnDéfilantes()) {
								Joueur.Mara_Défilantes_PartiesJouées++;
							} else if (Joueur.EnNG()) {
								MaraNGActive = InfoCarteCourante.Mara_Played>5;
								if (MaraNGActive) {
									Joueur.Mara_Rally_PartiesJouées++;
									Joueur.Mara_Rally_TempsTotal+=120000;
								}
							}
						}
					}
				}
				Vivant = "1";
				Joueur.Vivant = true;
				Joueur._Aaaah.PeurDispo = (ModeJeu != Carte.MODE_RALLY || PartiePrivée);
				Joueur._Aaaah.IntervalPeur = DebutPartie;
				JoueurEnVie++;
				JoueurDepart++;
			}
			Joueur._Aaaah.PosX = "60";
			Joueur._Aaaah.PosY = "320";
			LsJoueur.append(";" + Joueur.NomJoueur + "," + Joueur._Aaaah.PosX + "," + Joueur._Aaaah.PosY + "," + Vivant + "," + Joueur._Aaaah.Score);
		}
		// ajouter faux joueur ghost
		if (ModeJeu == Carte.MODE_RALLY && InfoCarteCourante != null) {
			String GhostVivant = InfoCarteCourante.Ghost.isEmpty() ? "0" : "1";
			LsJoueur.append(";Ghost,60,320,"+GhostVivant+",1000");			
		}
		//System.out.println( LsJoueur.toString());
		//
		if (SalonTest) {
			DemandeVote = false;
			serveur.Envoie(Joueurs, "IdO#" + NomGuideSalon() + "#" + JoueurDepart + "#" + CarteTest + "#" + LsJoueur.toString());
		} else {
			if (MondeEnCours > serveur.Aaaah_NombreMonde) {
				Carte Map = serveur.Aaaah_ListeCarte.get(MondeEnCours);
				if (Map != null) {
					DemandeVote = true;
					serveur.Envoie(Joueurs, "IdO#" + NomGuideSalon() + "#" + JoueurDepart + "#" + Map.Code + "#" + LsJoueur.toString() + "#" + Map.Auteur);
				} else {
					// code erroné
					MondeEnCours = 0;
					DemandeVote = false;
					InfoCarteCourante = serveur.Aaaah_ListeCarte.get(MondeEnCours);
					serveur.Envoie(Joueurs, "IdO#" + NomGuideSalon() + "#" + JoueurDepart + "#0#" + LsJoueur.toString());
				}
			} else { // Monde offi
				DemandeVote = false;
				if (serveur.Evénement.Halloween() && MondeEnCours == Serveur.MondeHalloweenAaaah) {
					serveur.Envoie(Joueurs, "IdO#" + "Le Démoniaaaahque" + "#" + JoueurDepart + "#" + MondeEnCours + "-" + (int)(Math.random() * 4) + ";" + (int)(Math.random() * 4)
								  + "#" + LsJoueur.toString());
					
					lsMortsDurantPartie = new ArrayList<Joueur>();
				} else { // Load de map normal
					serveur.Envoie(Joueurs, "IdO#" + NomGuideSalon() + "#" + JoueurDepart + "#" + MondeEnCours + "#" + LsJoueur.toString());
				}
				
			}
		}
		//
		//
		TimerPartie = serveur.new Action(System.currentTimeMillis() + TempsParPartie);
		TimerPartie.NouvellePartieAaaah = true;
		TimerPartie.PartieAaaah = this;
		
		GestionComptesTournoi();
		ParamCri.ResetValidite();
	}
	
	public void Victoire(Joueur JOUEUR) {
		if (JOUEUR.Vivant && !JOUEUR._Aaaah.Zombie) {

			int TempsArrivée = (int) (System.currentTimeMillis() - DebutPartie);
			
			// Mode Rally en classé : 25 sec par map min ; Def : 30 sec
			if (partieMere.PartieElo && ((partieMere.ModeElo == Elo.RALLY_AAAAH && TempsArrivée < 25_000)
				|| (partieMere.ModeElo == Elo.DEF && TempsArrivée < 30_000))) {
				serveur.Envoie(super.ListeJoueur, "CxINFO#La carte (" + InfoCarteCourante.Titre + ") a été terminée trop rapidement.");
			}
			
			// Info Map 
			if (StatsActive) {
				InfoCarteCourante.Survie++;
			}
			//
			if (PointBonus == 0) {
				JOUEUR._Aaaah.Score += 10;
			} else {
				if (PointBonus == 3) {
					if (RecordRallyActive && ParamCri.CarteValide()) {
						if (TempsArrivée < InfoCarteCourante.Record && TempsArrivée > 10000) {
							JOUEUR.Envoie("CxRecGh#"+InfoCarteCourante.Id);
							DernierRecordman = JOUEUR.NomJoueur;
							JOUEUR.Stats.RALLY.NouveauRecord();
						} else {
							DernierRecordman = "";
						}
					}
					if (StatsActive) {
						if (JOUEUR.StatsActivées()) {
							JOUEUR._Aaaah.Stats_PartiePremier++;
							if (JOUEUR._Aaaah.Stats_PartiePremier + JOUEUR._Aaaah.Stats_OldPremier == 10000) {
								serveur.BOITE.Requete(Boite.AJOUTER_RECOMPENSE_JOUEUR, JOUEUR, JOUEUR.NomJoueur, "100", "10000 victoires !");
							}
							//JOUEUR._Aaaah.Stats_Elo+=130;
						}
					}
					if ((StatsActive || RecordRallyActive) && ParamCri.CarteValide()) {
						// record
						int RecordActuel = InfoCarteCourante.Record;
						if (TempsArrivée < RecordActuel && TempsArrivée > 10000) {
							if (RecordActuel != 120000) {
								JOUEUR._Aaaah.Score += 2;
								if (JOUEUR.StatsActivées()) {
									//JOUEUR._Aaaah.Stats_Elo+=20;
								}
							}
							
							if (ParamCri.CarteValide()) {
								System.out.println("C0M RECA " + MondeEnCours + " " + (!InfoCarteCourante.equals("-") ? InfoCarteCourante.Recordman + " " + InfoCarteCourante.Record : "[NEW]") + " -> " + JOUEUR.NomJoueur + " (" + JOUEUR.AdresseIP + ")");
								InfoCarteCourante.Record = TempsArrivée;
								InfoCarteCourante.Recordman = JOUEUR.NomJoueur;
								serveur.Envoie(ListeJoueur, "IdIC#" + InfoCarteCourante.Record + "#" + InfoCarteCourante.Recordman + "#" + InfoCarteCourante.Played + "#" + InfoCarteCourante.Survie + "#1");
							}
							
						}
					}
					if (NewStatsActive && JOUEUR.NewStatsActivées() && JOUEUR.Stats.statByMode(ModeJeu) != null) {
						JOUEUR.Stats.statByMode(ModeJeu).ArriveePremier++;			
						if (ModeJeu == Carte.MODE_NG && TempsArrivée < InfoCarteCourante.Mara_Record && TempsArrivée > 10000 && ParamCri.CarteValide()) {
							JOUEUR.Stats.NG.NbRecords++;
						}							
					}
					if ((RecordActive && ParamCri.CarteValide()) && JOUEUR.EnNG() && JOUEUR.AutorisationInscription && !GuidePourNG) {
						// record
						int RecordActuel = InfoCarteCourante.Mara_Record;
						if (TempsArrivée < RecordActuel && TempsArrivée > 10000) {
							System.out.println("C0M RECA " + MondeEnCours + " " + InfoCarteCourante.Recordman + " " + InfoCarteCourante.Record);
							InfoCarteCourante.Mara_Record = TempsArrivée;
							InfoCarteCourante.Mara_Recordman = JOUEUR.NomJoueur;
							serveur.Envoie(ListeJoueur, "IdIC#" + InfoCarteCourante.Mara_Record + "#" + InfoCarteCourante.Mara_Recordman + "#" + InfoCarteCourante.Played + "#" + InfoCarteCourante.Survie + "#1");
						}
					}
					if (MaraActive && JOUEUR.EnOfficiel()) {
						JOUEUR.Mara_Run_ArrivéesPremier++;
					}
				}
				JOUEUR._Aaaah.Score += 10 + PointBonus * 2;
				PointBonus--;
			}
			if (StatsActive) {
				if (Guide!=null && Guide.StatsActivées()) {
					Guide._Aaaah.Stats_JoueursSauvés++;
				}
				if (JOUEUR.StatsActivées()) {
					JOUEUR._Aaaah.Stats_PartieFinie++;
				}
			}
			if (NewStatsActive) {
				if (Guide!=null && Guide.NewStatsActivées()) {
					Guide.Stats.GDM.JoueursSauvés++;
				}
				if (JOUEUR.NewStatsActivées() && JOUEUR.Stats.statByMode(ModeJeu) != null) {
					JOUEUR.Stats.statByMode(ModeJeu).ArriveeEnVie++;
				}	
				if (ModeJeu == Carte.MODE_MS) {
					int Nb = ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = ListeJoueur.get(i);
						if (Joueur.NewStatsActivées()) {
							Joueur.Stats.MS.JoueursSauvés++;
						}
					}
				}						
			}
			if (MaraActive) {
				if (Guide!=null && Guide.EnOfficiel()) {
					Guide.Mara_Guide_JoueurSauvés++;
				}
				if (JOUEUR.EnDéfilantes()) {
					JOUEUR.Mara_Défilantes_Survivor++;
				} else if (JOUEUR.EnNG()) {
					if (MaraNGActive) {
						JOUEUR.Mara_Rally_TempsTotal += TempsArrivée-InfoCarteCourante.Mara_Record - 120000;
					}
				}
			}
			
			JoueurSauve++;
			JOUEUR.Vivant = false;
			serveur.Envoie(ListeJoueur, "IdW#" + JOUEUR.NomJoueur + "#" + TempsArrivée);
			JoueurEnMoins();
		}
	}

	public String Positions() {
		StringBuilder LsJoueur = new StringBuilder("IdP");
		int Nb = ListeJoueur.size();
		for (int i = 0; i < Nb; i++) {
			Joueur Joueur = ListeJoueur.get(i);
			if (Joueur.Vivant) {
				LsJoueur.append("#" + Joueur.NomJoueur + "," + Joueur._Aaaah.PosX + "," + Joueur._Aaaah.PosY + ",1," + Joueur._Aaaah.Score);
			} else {
				LsJoueur.append("#" + Joueur.NomJoueur + "," + Joueur._Aaaah.PosX + "," + Joueur._Aaaah.PosY + ",0," + Joueur._Aaaah.Score);
			}
		}
		// ajouter faux joueur ghost
		if (ModeJeu == Carte.MODE_RALLY) {
			LsJoueur.append("#Ghost,60,320,0,1000");
		}
		return LsJoueur.toString();
	}

	public void Contamination(Joueur JOUEUR) {
		if (JOUEUR != null && ListeJoueur.contains(JOUEUR) && !JOUEUR._Aaaah.Zombie && JOUEUR.Vivant) {
			JOUEUR._Aaaah.Zombie = true;
			JOUEUR._Aaaah.CodeZombie = "1";
			JOUEUR._Aaaah.Score--;
			if (JOUEUR._Aaaah.Score < 0) {
				JOUEUR._Aaaah.Score = 0;
			}
			//
			serveur.Envoie(ListeJoueur, "ZoC#" + JOUEUR.NomJoueur);
			//
			JoueurEnMoins();
		}
	}

	public void JoueurEnMoins() {
		JoueurEnVie--;
		//System.out.println("JEM " + JoueurEnVie);
		if (JoueurEnVie == 1 && SalonFight) {
			for (Joueur Joueur : ListeJoueur) {
				if (Joueur.Vivant && !Joueur._Aaaah.Zombie) {
					serveur.Envoie(ListeJoueur, "CxINFO#Dernier survivant : " + Joueur.NomJoueur );
					Joueur._Aaaah.Score += 5;
					if (MaraActive && Joueur.EnFight()) {
						Joueur.Mara_Fight_Survivor++;
					}
					if (NewStatsActive && Joueur.NewStatsActivées()) {
						Joueur.Stats.FS.DernierSurvivant++;
					}
					break;
				}
			}			
			
		} else if (JoueurEnVie <= 0) {
			if (PartieEnCours) {
				TimerPartie.Active = false;
				PartieEnCours = false;
				Nouvelle_Partie(-1);
			}
		}            
	}

	public void CheckDernierJoueur() {
		if (JoueurEnVie == 1 && (SalonFight || ModeJeu == Carte.MODE_MS)) {
			boolean ZombieRestant = false;
			for (Joueur Joueur : ListeJoueur) {
				if (Joueur.Vivant && Joueur._Aaaah.Zombie) {
					ZombieRestant = true;
				}
			}
			// skip la partie s'il ne reste qu'un survivant et aucun contaminé
			if (!ZombieRestant && PartieEnCours) {
				TimerPartie.Active = false;
				PartieEnCours = false;
				Nouvelle_Partie(-1);
			}				
			
		}          
	}
	
	public void PreContamination() {
		int NbJoueur = ListeJoueur.size();
		if (NbJoueur < 3 && !ForcerConta) {
			return;
		}
		ArrayList<Joueur> LsPotentiel = new ArrayList<Joueur>();
		for (int i = 0; i < NbJoueur; i++) {
			Joueur J = ListeJoueur.get(i);
			if (!J._Aaaah.Guide && J.Vivant && !J._Aaaah.Zombie) {
				LsPotentiel.add(J);
			}
		}
		double Num = LsPotentiel.size();
		if (Num == 0) {
			return;
		}
		FuturContamine = LsPotentiel.get((int) (Math.random() * Num));
		//
		serveur.Envoie(ListeJoueur, "ZoP#" + FuturContamine.NomJoueur);
		//
		TempsContamination = serveur.new Action(System.currentTimeMillis() + 3000);
		TempsContamination.Contamination = true;
		TempsContamination.PartieAaaah = this;
		//
		TempsPreContamination = serveur.new Action(System.currentTimeMillis() + 30000 + (int) (Math.random() * 60000));
		TempsPreContamination.PreContamination = true;
		TempsPreContamination.PartieAaaah = this;
	}

	public String NomGuideSalon() {
		if (Guide!=null) {
			return Guide.NomJoueur;
		} else {
			return "-";
		}
	}
	
	public void GestionComptesTournoi() {
		if (ListeJoueur.size() < 3) {
			return;
		}
		for(int i = ListeJoueur.size() - 1; i >= 0; --i) { // Forcer le respawn après chargement d'une map (pour les laggeurs)
			Joueur J = ListeJoueur.get(i);
			
			if ((J.AutorisationFilmeur || J.AutorisationTournoiAaaah || J.AutorisationArbitreElo || J.AutorisationFilmeurElo || J.AutorisationTounoiArbitreSecondaire) && !J._Aaaah.Guide && J.MortAuto) {
				J.Vivant = false;
				serveur.Envoie(ListeJoueur, "IdX#" + J.NomJoueur);
				JoueurEnMoins();
			}
		}
	}
	
	public void AjouterAfk(Joueur J) {
		if ((System.currentTimeMillis() - J.TempsZero) / 1000 < 15) { // Empêcher d'afk avant 15 sec
			J.Envoie("CxNAFK");
			return;
		}
		
		if (J.AutorisationFilmeur || J.AutorisationTournoiAaaah || J.AutorisationArbitreElo || J.AutorisationFilmeurElo) {
			J.Envoie("CxNAFK#");
			return;
		}
		
		ListeAfk.add(J);
		
		J.Afk_Score = J._Aaaah.Score; // sauvegarde du score
		J.Afk_Partie = J.PartieEnCours; // sauvegarde de la partie
		// On quitte la partie
		J.Afk = true;
		
		J.PartieEnCours.Quitter_partie(J);
		J.ReceptionAaaah = false;
		J.Envoie("CxAFK"); // Autoriser l'afk
	}
	
	public void QuitterAfk(Joueur J) {
		ListeAfk.remove(J);
		
		J.Afk_Score = 0;
		J.Afk_Partie = null;
		J.Afk = false;
	}
	
	private void IndiquerDestructionPartieAfk() {
		for (int i = ListeAfk.size() - 1; i >= 0; --i) {
			Joueur J = ListeAfk.get(i);
			
			QuitterAfk(J);
			
			J.Envoie("CxPDETR");
			serveur.Rejoindre_Liste_Partie(J);
		}
	}
	
	/**
	 * Utilisé pour avoir les informations nécessaire à la connexion d'un nouveau
	 * joueur. Utile pour la résurrection aussi (cf map offi Halloween)
	 */
	public static String GenererInfoNouveauJoueur(Joueur J) {
		StringBuilder msg = new StringBuilder();
		
		msg.append(J.NomJoueur);
		msg.append(",");
		msg.append(J._Aaaah.PosX);
		msg.append(",");
		msg.append(J._Aaaah.PosY);
		msg.append(",");
		msg.append(J.IdTeam);
		msg.append(",");
		msg.append(J.recompenseElo.getRecompenseAaaah());
		
		return msg.toString();
	}

	public String SetCris(String Puissance) {
		// Note : Puissance peut valoir un entier en string ou "off", "ng", "base" ou "*"
		// cf /cri
		return SetCris(Puissance, "");
	}
	
	public String SetCris(String Puissance, String DureeRechargeMS) {
		// Note : Puissance et  DureeRechargeMS peuvent valoir un entier en string ou "*" (ou "" pour la durée)
		// cf /cri
		String MsgAuteurCmd = ParamCri.SetCris(Puissance, DureeRechargeMS);
		serveur.Envoie(getListeJoueurs(), "CxINFO#Les paramètres de cri ont été modifiés (les records sur les prochaines cartes seront " + (ParamCri.NextCartesSerontValides() ? "" : "in") + "valides).");
		serveur.Envoie(getListeJoueurs(), "IdC#" + ParamCri.toString());
		return MsgAuteurCmd;
	}

	public void InitCris(boolean SansConta) {
		ParamCri = new ParametresCri(SansConta, ModeJeu == Carte.MODE_MS);
	}
	
	public String InfoCri() {
		return ParamCri.MessageDescriCommandeCri();
	}
	
	public boolean ParamCriRecordValides() {
		return ParamCri.CarteValide();
	}

	public String GetParamCris() {
		return ParamCri.toString();
	}

}
