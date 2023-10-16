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

package forteresse;

import java.util.ArrayList;

import joueur.Joueur;
import serveur.Boite;
import serveur.Jeu;
import serveur.Partie;
import serveur.Serveur;

public class Forteresse extends Jeu {
	public Serveur.Action TimerPartie;
	public MondeForteresse MondeEnCours = null;
	public int ProchainMonde = -1;
	public ArrayList<MondeForteresse> BoucleMonde = new ArrayList<MondeForteresse>();
	//public ArrayList<Integer> Forteresse_ListeCustomMonde = new ArrayList<Integer>();
	public boolean CustomMonde = false;
	public boolean DecalageLancement = false;
	public static final String $ = String.valueOf((char)2);;
	public int EquipeRouge;
	public int EquipeBleu;
	public boolean AutoriserChoixEquipe = false;
	public boolean ServeurOffi = false;
	public boolean Dessin = false;
	public double RatioRouge = 0.5;
	public Frigo Frigo1;
	public Frigo Frigo2;
	public int Vie1 = 40;
	public int Vie2 = 40;
	public boolean DoubleAttaque = false;
	public boolean SansBombe = false;
	public boolean SansFrigo = false;
	public boolean AfficherKills = false;
	public boolean SansTirBleu = false;
	public boolean SansTirRouge = false;
	public int AttaqueBleue = 0;
	public int AttaqueRouge = 0;
	private boolean AttaqueRougeEnCours;
	public String CodeAttaque;
	public int NumJambon;
	public ArrayList<Jambombe> ListeJambon = new ArrayList<Jambombe>();
	public boolean CouleursPersoActivees = false;
	public int[] CouleursPersonnalisees = new int[6]; // Destructible (bleu) 1, Indestructible 2, Objectif 3, Non reconstructible 8, Constructible 0, Inconstructible 9
	public int[][] ListeCase = new int[200][100];
	private boolean[][] ListeCadeaux = null;
	public boolean cadeuxActives = false;
	public boolean RallyOfficiel = false;
	public boolean RallyValide = true;
	private boolean MapSauvegardable = true;
	private boolean LoadOff = false;
	private int ModeMap = Rally.MODE_AUTRE;
	private Playlist playlist = null;
	private int numMancheEnCours = 1;
	private ArrayList<MondeForteresse> listePersoMondes = new ArrayList<MondeForteresse>();
	public Rally CarteEnCours;
	
	public String InfosMondePerso = ""; // Contient les informations de la map personnalisée (aka choix gravité, frigos, etc) en cours ("" si aucune en cours)
	public MondeForteresse MondePerso = null;
	
	public boolean ModePresident = false;
	public int PresidentRouge = -1;
	public int PresidentBleu = -1;
	public byte FrequencePresident = 0; // S'augmente après chaque map avec moins de 2 joueurs ; qd = 3 => load mode président
	
	public int Record = 0;
	public String Recordman = null;

	static final public int CASE_VIDE_CONSTRUCTIBLE = 0;
	static final public int CASE_CARRE_DESTRUCTIBLE = 1;
	static final public int CASE_CARRE_INDESTRUCTIBLE = 2;
	static final public int CASE_CARRE_OBJECTIF = 3;
	static final public int CASE_CARRE_OBJECTIF2 = 4;
	static final public int CASE_CARRE_NONRECONSTRUCTIBLE = 8;
	static final public int CASE_VIDE_INCONSTRUCTIBLE = 9;
	
	static final public String COULEUR_CARRE_DESTRUCTIBLE =       "14172E";
	static final public String COULEUR_CARRE_INDESTRUCTIBLE =     "14172E";
	static final public String COULEUR_CARRE_OBJECTIF =           "FF7F27";
	static final public String COULEUR_CARRE_NONRECONSTRUCTIBLE = "14172E";
	static final public String COULEUR_CARRE_CONSTRUCTIBLE =      "9E9EAF";
	static final public String COULEUR_CARRE_INCONSTRUCTIBLE =    "9E9EAF";
	
	static final public int MODE_AUCUN = 0;
	static final public int MODE_FRIGO = 1;
	static final public int MODE_FRAG = 2;
	static final public int MODE_KILL = 3;
	static final public int MODE_CROSS = 4;
	
	// Donjons
	public int NumeroDonjon = 0;
	public int NiveauDonjon = 0;
	public int DureeMaxDonjon = 0;
	public boolean NiveauBonus = false;
	
	public Forteresse(Serveur serveur, Partie partieMere, long dureePartie) {
		super(serveur, partieMere, dureePartie);
		//serveur = SERVEUR;
		
		if (serveur.Evénement.aCadeauxForto()) {
			ListeCadeaux = new boolean[200][100];
			for (int X = 0; X < 200; X++) {
				for (int Y = 0; Y < 100; Y++) {
					ListeCadeaux[X][Y] = false;
				}
			}
			
			cadeuxActives = true;
		}
	}

	public void Destruction() {
		if (TimerPartie != null) {
			TimerPartie.Active = false;
			TimerPartie = null;
		}
		BoucleMonde = null;
		if (ListeJambon != null) {
			int Nb = ListeJambon.size();
			for (int i = 0; i < Nb; i++) {
				Jambombe Jambombe = ListeJambon.get(i);
				Jambombe.Active = false;
			}
			ListeJambon = null;
		}
		ListeCase = null;
		ListeCadeaux = null;
	}

	public void Reception(Joueur JOUEUR, String STRING, boolean MessagePerimé) {
		String[] Message = STRING.split($);
		int Code = Message[0].charAt(0);
		
		// Actualisation
		if (Code == 1) {
			if (MessagePerimé) {
				return;
			}
			
			JOUEUR._Forteresse.PosX = Message[1];
			JOUEUR._Forteresse.PosY = Message[2];
			if (!JOUEUR._Forteresse.ListeJambonExplosion.isEmpty()) {
				JOUEUR._Forteresse.ListeJambonExplosion.remove(0).Explosion(JOUEUR);
			}
			serveur.Envoie(ListeJoueur, "1" + $ + JOUEUR._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.PosX + $ + JOUEUR._Forteresse.PosY + $ + Message[3] + $ + Message[4]);
			if (Message.length > 5) {
				Joueur_Degats_Jambon(JOUEUR, Integer.parseInt(Message[6]), Message[5]);
			}
			return;
		}
		// Tire
		if (Code == 3) {
			if (MessagePerimé) {
				return;
			}
			
			if (JOUEUR.Vivant) {
				boolean ToucheMur = Message[3].equals("");
				int ArmeEnCours = Integer.parseInt(Message[4]);

				if (ArmeEnCours == 0) {
					/************************************
					 * MUNITIONS INFINIES ?
					 * 
					 ************************************/
					int BalleEnCours = Integer.parseInt(Message[5]);
					if (BalleEnCours == JOUEUR._Forteresse.DernièreBalle && BalleEnCours == JOUEUR._Forteresse.DerDernièreBalle) {
						// Check côté client plus performant ~ on ne garde que l'AV en cas de potentiel client modifié
						serveur.Avertissement_Modo("Munition infinie pour " + JOUEUR.NomJoueur + " (" + JOUEUR.PartieEnCours.NomPartie + "), valeur bloquée sur " + BalleEnCours + ".", false);
						JOUEUR._Forteresse.DerDernièreBalle = JOUEUR._Forteresse.DernièreBalle;
						JOUEUR._Forteresse.DernièreBalle = BalleEnCours;
						return;
					}
					JOUEUR._Forteresse.DerDernièreBalle = JOUEUR._Forteresse.DernièreBalle; // ??                                        
					JOUEUR._Forteresse.DernièreBalle = BalleEnCours;
					/************************************/
					if (JOUEUR._Forteresse.Chargeur > 0) {
						JOUEUR._Forteresse.Chargeur--;
						String ImpactX = Message[1];
						String ImpactY = Message[2];
						if (ToucheMur) {
							int X = Integer.parseInt(ImpactX) / 10;
							int Y = Integer.parseInt(ImpactY) / 10;
							
							GestionCaseCadeaux(X, Y, JOUEUR);
							
							if (ListeCase[X][Y] == CASE_CARRE_DESTRUCTIBLE) {
								ListeCase[X][Y] = CASE_VIDE_CONSTRUCTIBLE;
								//Case_Morte(X, Y);
								serveur.Envoie(ListeJoueur, "3" + $ + JOUEUR._Forteresse.CodeJoueur + $ + ImpactX + $ + ImpactY);
							} else if (ListeCase[X][Y] == CASE_CARRE_NONRECONSTRUCTIBLE){
								ListeCase[X][Y] = CASE_VIDE_INCONSTRUCTIBLE;
								serveur.Envoie(ListeJoueur, "3" + $ + JOUEUR._Forteresse.CodeJoueur + $ + ImpactX + $ + ImpactY);
							} else if (ListeCase[X][Y] == CASE_CARRE_OBJECTIF || ListeCase[X][Y] == CASE_CARRE_OBJECTIF2) {
								if (!JOUEUR.RallyRéussi) {
									JOUEUR.RallyRéussi = true;
									long Temps = getTempsZeroRally(JOUEUR);
									serveur.Envoie(ListeJoueur, "CxINFO#"+JOUEUR.NomJoueur + " a réussi la carte en " + Temps + " secondes.");
									
									if (this.NumeroDonjon != 0) { // Un donjon est en cours
										if (JOUEUR.AutorisationInscription && JOUEUR._Forteresse.Santé == 10 && JOUEUR._Forteresse.estRouge()
											&& RallyValide) {
											
											serveur.DONJON.GestionDonjon(JOUEUR, ListeCase[X][Y]);
										}
									} else if (((CarteEnCours != null && CarteEnCours.estRepertoriée()) || RallyOfficiel) && JOUEUR.AutorisationInscription) {
										if (JOUEUR._Forteresse.Santé == 10 && JOUEUR._Forteresse.estRouge() && JOUEUR._Forteresse.RallyValide && RallyValide) {
											serveur.BOITE.Requete(Boite.RECORD_MAP_FORTO, JOUEUR, Long.toString(Temps), Long.toString(CarteEnCours.getID()));
											serveur.Envoie(ListeJoueur, "CxINFO#Code : " + CodeFinMapConfirme(JOUEUR.IdJoueur, Temps, CarteEnCours.getID()));
										} else {
											JOUEUR.Envoie("CxINFO#Aucun code n'est donné si vous avez moins de 10 points de vie, que vous êtes bleu, que vous avez construit un objectif (case orange) ou que vous êtes connecté sous invité."); // (ou si triche détectée)
										}
									}
								}
								
								if (ListeJoueur.size() == 1 && Dessin 
										&& JOUEUR.PartieEnCours != null
										&& JOUEUR.PartieEnCours.hasMotDePasse()) {
									ListeCase[X][Y] = CASE_VIDE_CONSTRUCTIBLE;
									serveur.Envoie(ListeJoueur, "3" + $ + JOUEUR._Forteresse.CodeJoueur + $ + ImpactX + $ + ImpactY);									
								}
							} else {
								serveur.Envoie(ListeJoueur, "3" + $ + JOUEUR._Forteresse.CodeJoueur + $ + ImpactX + $ + ImpactY);
							}
							if (!SansFrigo) {
								if (JOUEUR._Forteresse.estBleu() ^ AttaqueRougeEnCours) {
									if (Frigo1 != null /* (pas monde perso) */ && Frigo1.ToucheFrigo(X, Y)) {
										if (Vie1 > 0) {
											JOUEUR._Forteresse.ToucheFrigo += 1;
											Vie1--;

											if (Vie1 == 0) {
												if (!DecalageLancement) {
													Nouvelle_Partie(-1, null, false);
												}
											}
											if(DoubleAttaque) {
												serveur.Envoie(ListeJoueur, "14" + $ + Vie1 + $ + 1 + $ + Vie2);
											}
											else {
												serveur.Envoie(ListeJoueur, "14" + $ + Vie1 + $ + 1 + $ + "-1");												
											}
										}
									}
	
								} else if (DoubleAttaque && Frigo2.ToucheFrigo(X, Y)) {
									if (Vie2 > 0) {
										JOUEUR._Forteresse.ToucheFrigo += 1;
										Vie2--;
										
										if (Vie2 == 0) {
											if (!DecalageLancement) {
												Nouvelle_Partie(-1, null, false);
											}
										}
										serveur.Envoie(ListeJoueur, "14" + $ + Vie2 + $ + 2 + $ + Vie1);
									}
								}
							}
						} else {
							if (!((JOUEUR._Forteresse.estRouge() && SansTirRouge) ||
								(!JOUEUR._Forteresse.estRouge() && SansTirBleu))) {
								String Info = "3" + $ + JOUEUR._Forteresse.CodeJoueur + $ + ImpactX + $ + ImpactY + $ + Message[3] + $;
								Touche_Joueur(JOUEUR, Info, Integer.parseInt(Message[3]));
							}
						}
					} else {
						JOUEUR.SurplusMunitions++;
						if (JOUEUR.SurplusMunitions == 25) {
							serveur.Avertissement_Modo("Munitions infinies pour " + JOUEUR.NomJoueur + " (" + JOUEUR.AdresseIP + ") sur : " + JOUEUR.Salon(), false);
						} else
							if (JOUEUR.SurplusMunitions == 40) {
								if (serveur.DecoTricheAuto) {
									serveur.Ban_Joueur(JOUEUR, JOUEUR.NomJoueur, 360000, "0", "[Bot] Activité suspecte", false, true);
								}
							}
					}
				} else {
					if (ArmeEnCours == 1) {
						if (JOUEUR._Forteresse.Jambombe > 0) {
							JOUEUR._Forteresse.Jambombe--;
							String ImpactX = Message[1];
							String ImpactY = Message[2];
							if (ToucheMur) {
								new Jambombe(this, false, null, ImpactX, ImpactY, JOUEUR._Forteresse.CodeJoueur);
							} else {
								new Jambombe(this, true, Message[3], ImpactX, ImpactY, JOUEUR._Forteresse.CodeJoueur);
							}
						}
					}
				}
			}
			return;
		}
		// Mouvement Stop
		if (Code == 4) {
			if (MessagePerimé) {
				return;
			}
			JOUEUR._Forteresse.PosX = Message[1];
			JOUEUR._Forteresse.PosY = Message[2];
			serveur.Envoie(ListeJoueur, "4" + $ + JOUEUR._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.PosX + $ + JOUEUR._Forteresse.PosY + $ + Message[3] + $ + Message[4]);
			return;
		}
		// Mouvement Droite
		if (Code == 5) {
			if (MessagePerimé) {
				return;
			}
			
			JOUEUR._Forteresse.PosX = Message[1];
			JOUEUR._Forteresse.PosY = Message[2];
			serveur.Envoie(ListeJoueur, "5" + $ + JOUEUR._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.PosX + $ + JOUEUR._Forteresse.PosY + $ + Message[3] + $ + Message[4]);
			return;
		}
		// Mouvement Gauche
		if (Code == 6) {
			if (MessagePerimé) {
				return;
			}
			
			JOUEUR._Forteresse.PosX = Message[1];
			JOUEUR._Forteresse.PosY = Message[2];
			serveur.Envoie(ListeJoueur, "6" + $ + JOUEUR._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.PosX + $ + JOUEUR._Forteresse.PosY + $ + Message[3] + $ + Message[4]);
			return;
		}
		// Mouvement Saut
		if (Code == 7) {
			if (MessagePerimé) {
				return;
			}
			
			JOUEUR._Forteresse.PosX = Message[1];
			JOUEUR._Forteresse.PosY = Message[2];
			serveur.Envoie(ListeJoueur, "7" + $ + JOUEUR._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.PosX + $ + JOUEUR._Forteresse.PosY + $ + Message[3] + $ + Message[4]);
			return;
		}
		// Construction
		if (Code == 8) {
			if (MessagePerimé) {
				return;
			}
			
			if (JOUEUR.Vivant) {
				long SystemTemps = System.currentTimeMillis();
				long Temps = SystemTemps - JOUEUR._Forteresse.LimiteBrique;
				if (Temps > 2000) {
					JOUEUR._Forteresse.LimiteBrique = SystemTemps;
					JOUEUR._Forteresse.NombreBrique = 0;
				}
				JOUEUR._Forteresse.NombreBrique++;
				if (JOUEUR._Forteresse.NombreBrique > 30) {
					return;
				}
				int X = Integer.parseInt(Message[1]);
				int Y = Integer.parseInt(Message[2]);
				if (ListeCase[X][Y] == CASE_VIDE_CONSTRUCTIBLE) {
					ListeCase[X][Y] = CASE_CARRE_DESTRUCTIBLE;
					serveur.Envoie(ListeJoueur, "8" + $ + Message[1] + $ + Message[2] + $ + JOUEUR._Forteresse.EquipeToCode() + $ + JOUEUR._Forteresse.CodeJoueur);
					if (serveur.Evénement.aCadeauxForto() && ListeCadeaux != null  && Math.random() > 0.98) {
						serveur.Envoie(ListeJoueur, "19" + $ + X + $ + Y);
						ListeCadeaux[X][Y] = true;
					}
				}
			}
			return;
		}
		// Construction objectif
		if (Code == 17) {
			if (MessagePerimé) {
				return;
			}
			
			//RallyValide = false;
			
			if (JOUEUR.Vivant && ListeJoueur.size() == 1 && Dessin 
					&& JOUEUR.PartieEnCours != null
					&& JOUEUR.PartieEnCours.hasMotDePasse()) {
				int X = Integer.parseInt(Message[1]);
				int Y = Integer.parseInt(Message[2]);
				
				if (X>=0 && Y>= 0 && X < 200 && Y < 100 && ListeCase[X][Y] == CASE_VIDE_CONSTRUCTIBLE) {
					RallyValide = false;
					ListeCase[X][Y] = CASE_CARRE_OBJECTIF;
					serveur.Envoie(ListeJoueur, "17" + $ + Message[1] + $ + Message[2]);
				}
			}
			return;
		}
		// Recharge
		if (Code == 11) {
			if (MessagePerimé) {
				return;
			}
			
			JOUEUR._Forteresse.DernièreBalle = -1;
			JOUEUR._Forteresse.DerDernièreBalle = -1;
			JOUEUR._Forteresse.Chargeur = 12;
			serveur.Envoie(ListeJoueur, "11" + $ + JOUEUR._Forteresse.CodeJoueur);
			return;
		}
		// Chat
		if (Code == 13) {
			if (JOUEUR.AutorisationInscription) {
				if (System.currentTimeMillis() - JOUEUR.LimiteChat < 400) {
					//JOUEUR.Deconnexion("Flood.");
					return;
				}
				JOUEUR.LimiteChat = System.currentTimeMillis();
				if (JOUEUR.Muet) {
					JOUEUR.Envoie("CxMuet");
				} else {
					if (JOUEUR.PartieEnCours.ChatFerme) {
						// chat désactivé
						JOUEUR.Envoie("CxF");
					} else {
						// envoi du message à tout le salon / alliés
						Message[1] = serveur.FixeDoubleEsperlouette(Message[1], JOUEUR);
						
						if (Message.length == 3) {
							ArrayList<Joueur> ListeEnvoie = new ArrayList<Joueur>();
							int Nb = ListeJoueur.size();
							for (int i = 0; i < Nb; i++) {
								Joueur Joueur = ListeJoueur.get(i);
								if (Joueur._Forteresse.MemeEquipe(JOUEUR._Forteresse) && !Joueur.AutorisationFilmeur) {
									ListeEnvoie.add(Joueur);
								}
							}
							serveur.Envoie_Chat(JOUEUR, ListeEnvoie, "13" + $ + JOUEUR._Forteresse.CodeJoueur + $ + Message[1] + $);
						} else {
							serveur.Envoie_Chat(JOUEUR, ListeJoueur, "13" + $ + JOUEUR._Forteresse.CodeJoueur + $ + Message[1]);
						}
					}
				}
			}
			return;
		}
		//
		JOUEUR.Reception_Spéciale(STRING);
	}

	private void GestionCaseCadeaux(int X, int Y, Joueur J) {
		if (!serveur.Evénement.aCadeauxForto() || ListeCadeaux == null) {
			return;
		}
		
		if (cadeuxActives && ListeCadeaux[X][Y]) {
			ListeCadeaux[X][Y] = false;
			
			int rand = (int)(Math.random() * 100.0);
			
			if (rand < 60) {
				J._Forteresse.RedonnerBalles();
				J.Envoie("12" + $ + "0"); // Récup balles
			} else if (rand < 70) {
				J._Forteresse.AjouterJambombes(1);
				J.Envoie("12" + $ + "1");
			} else if (rand < 95) {
				J._Forteresse.RedonnerBlocs();
				J.Envoie("12" + $ + "2");
			} else {
				J._Forteresse.AjouterJambombes(2);
				J.Envoie("12" + $ + "3");
			}
		}
	}

	public void CouleurParDefaut() {
		CouleursPersoActivees = false;
	}
	
	public void SetCouleurs(String C1, String C2, String C3, String C4, String C5, String C6) {
		CouleursPersoActivees = true;
		CouleursPersonnalisees[0] = Integer.parseInt(C1, 16);
		CouleursPersonnalisees[1] = Integer.parseInt(C2, 16);
		CouleursPersonnalisees[2] = Integer.parseInt(C3, 16);
		CouleursPersonnalisees[3] = Integer.parseInt(C4, 16);
		CouleursPersonnalisees[4] = Integer.parseInt(C5, 16);
		CouleursPersonnalisees[5] = Integer.parseInt(C6, 16);
	}
	
	public String GetStrCouleursConnexionPartie() {
		if (CouleursPersoActivees) {
			return Long.toString(CouleursPersonnalisees[Rally.CARRE_DESTRUCTIBLE], 16)
				+ ";" + Long.toString(CouleursPersonnalisees[Rally.CARRE_INDESTRUCTIBLE], 16)
				+ ";" + Long.toString(CouleursPersonnalisees[Rally.CARRE_OBJECTIF], 16)
				+ ";" + Long.toString(CouleursPersonnalisees[Rally.CARRE_NONRECONSTRUCTIBLE], 16)
				+ ";" + Long.toString(CouleursPersonnalisees[Rally.CARRE_INCONSTRUCTIBLE], 16)
				+ ";" + Long.toString(CouleursPersonnalisees[Rally.CARRE_CONSTRUCTIBLE], 16);
		} else {
			return Forteresse.COULEUR_CARRE_DESTRUCTIBLE
				+ ";" + Forteresse.COULEUR_CARRE_INDESTRUCTIBLE
				+ ";" + Forteresse.COULEUR_CARRE_OBJECTIF
				+ ";" + Forteresse.COULEUR_CARRE_NONRECONSTRUCTIBLE
				+ ";" + Forteresse.COULEUR_CARRE_INCONSTRUCTIBLE
				+ ";" + Forteresse.COULEUR_CARRE_CONSTRUCTIBLE;
		}
	}
	
	private void StopperTimerPartie() {
		if (TimerPartie != null) {
			TimerPartie.Active = false;
		}
	}
	
	/**
	 * Décaler le lancement de la partie à dans 2 minutes.
	 */
	private void DecalerLancement() {
		DecalageLancement = true;
		
		TimerPartie = serveur.new Action(System.currentTimeMillis() + 2000);
		TimerPartie.NouvellePartieForteresse = true;
		TimerPartie.PartieForteresse = this;
	}
	
	private void NettoyerJambombes() {
		int Nb = ListeJambon.size();
		for (int i = 0; i < Nb; i++) {
			Jambombe Jambon = ListeJambon.get(i);
			Jambon.Active = false;
		}
		NumJambon = 0;
		ListeJambon.clear();
	}
	
	private void DeterminerAttaquant(boolean ResetScore) {
		if (ResetScore) {
			ResetScore = true;
			
			AttaqueRouge = 0;
			AttaqueBleue = 0;
		}
		
		if (AttaqueRouge < AttaqueBleue) {
			AttaqueRougeEnCours = true;
			CodeAttaque = "1";
			AttaqueRouge++;
		} else {
			AttaqueRougeEnCours = false;
			CodeAttaque = "0";
			AttaqueBleue++;
		}
	}
	
	private void ResetCasesMap() {
		ListeCase = new int[200][100];
		for (int X = 0; X < 200; X++) {
			for (int Y = 0; Y < 100; Y++) {
				ListeCase[X][Y] = CASE_VIDE_CONSTRUCTIBLE;
			}
		}
		//
		if (serveur.Evénement.aCadeauxForto()) {
			ListeCadeaux = new boolean[200][100];
			for (int X = 0; X < 200; X++) {
				for (int Y = 0; Y < 100; Y++) {
					ListeCadeaux[X][Y] = false;
				}
			}
		}
	}
	
	private void EnvoyerPointsFrigoIndividuels() {
		// Envoi du nombre de points frigos
		StringBuilder msg = new StringBuilder("38");
		int NbJoueurs = ListeJoueur.size();
		for (int i = 0; i < NbJoueurs; i++) {
			Joueur Joueur = ListeJoueur.get(i);
			msg.append($ + Joueur._Forteresse.CodeJoueur + "," + Joueur._Forteresse.ToucheFrigo);
		}
		
		serveur.Envoie(ListeJoueur, msg.toString());
	}
	
	private void ActualiserJoueursFinPartie(boolean ResetScore) {
		EquipeBleu = 0;
		EquipeRouge = 0;
		
		int NbJ = ListeJoueur.size();
		for (int i = 0; i < NbJ; i++) {
			Joueur Joueur = ListeJoueur.get(i);
			Joueur._Forteresse.Reset();
			
			if (ResetScore) {
				Joueur._Forteresse.Score = 0;
			}
			
			Joueur.Vivant = true;
			Joueur.RallyRéussi = false;
			
			if (Joueur._Forteresse.estRouge()) {
				EquipeRouge++;
			} else if (Joueur._Forteresse.estBleu()) {
				EquipeBleu++;
			}
			
			if (Joueur.NewStatsActivées()) {
				Joueur.Stats.FORTO.NouvellePartie();
			}
		}
	}
	
	private void ActualiserInfosFrigo() {
		Frigo1 = MondeEnCours.Frigo1;
		
		Vie1 = 40;
		if (DoubleAttaque) {
			Frigo2 = MondeEnCours.Frigo2;
			Vie2 = 40;
		}
	}
	
	/**
	 * Charger une nouvelle partie.
	 * 
	 * @param MondeChoisi : -1 = charger la manche suivante (change de monde si c'est la deuxième)
	 *                       0 > ... = charger le monde officiel
	 *                      -2 = charger une carte (/load et /map)
	 *                      
	 * @param Map : Map = monde à charger (MondeChoisi == -2)
	 * 
	 * @param estReload : true = garder les paramètres actuels (couleurs, rally, etc)
	 *                    false = reset les paramètres actuels
	 */
	public void Nouvelle_Partie(int MondeChoisi, Rally Map, boolean EstReload) {
		MondeForteresse mondeALoad = null;
		boolean ResetScore = false;
		String OldDoubleAttaque = (DoubleAttaque ? "1" : "0");
		int OldVie1 = Vie1;
		int OldVie2 = Vie2;
		
		if (MondeChoisi == -2 && Map == null) {
			return;
		}
		
		StopperTimerPartie();
		if (ListeJoueur.size() == 0)
			return;
		
		if (!DecalageLancement) { // On demande d'attendre 2 sec avant de commencer la nouvelle partie
			DecalerLancement();
			return;
		}
		
		// Nettoyer la partie
		NettoyerJambombes();
		
		if (!EstReload) {
			ResetCasesMap();
			RallyOfficiel = false;
			
			//IdRally = 0;
			
			if (ModePresident) { // Reset des effets du mode président
				SansFrigo = false; // TODO : mettre valeur par défaut
				PresidentRouge = -1;
				PresidentBleu = -1;
				CarteEnCours = null;
			}
			
			if (CarteEnCours != null) {
				CarteEnCours.derepertorier();
			}
		}
		
		DecalageLancement = false;
		
		TimerPartie = serveur.new Action(System.currentTimeMillis() + DureePartie);
		TimerPartie.NouvellePartieForteresse = true;
		TimerPartie.PartieForteresse = this;
		
		Record = 0;
		Recordman = null;
		//
		
		// Déterminer monde à charger
		if (MondeChoisi >= 0 && MondeChoisi < serveur.Forteresse_ListeMonde.size()) { // Load de monde officiel
			mondeALoad = serveur.Forteresse_ListeMonde.get(MondeChoisi);
		} else if (MondeChoisi == -1) { // Load manche suivante
			mondeALoad = getNextMonde();
			if (mondeALoad == null) {
				return; // Map de playlist en attente avec la boite d'être chargé
			}
		} else if (MondeChoisi == -2) { // Load monde d'une map
			CleanDonjon();
			
			CarteEnCours = Map;
			SetModeMap(Map.getMode());
			mondeALoad = Map.getMonde();
			numMancheEnCours = 1;
		}
		
		//
		
		ResetScore = (numMancheEnCours == 1); // Reset kills des joueurs
		
		DeterminerAttaquant(ResetScore);
		
		MondeEnCours = mondeALoad;
		DoubleAttaque = MondeEnCours.DoubleAttaque;
		
		ActualiserInfosFrigo();
		ActualiserJoueursFinPartie(ResetScore);
		EnvoyerPointsFrigoIndividuels();

		DebutPartie = System.currentTimeMillis();
		
		//
		String TypeAttaque;
		
		if (MondeEnCours.MondePresident) {
			Charge_Map_President();
			if (ModePresident) { // Si les conditions pour charger le mode sont ok
				TypeAttaque = "2"; // Mode président
			} else {
				TypeAttaque = (DoubleAttaque ? "1" : "0");
			}
		} else {
			TypeAttaque = (DoubleAttaque ? "1" : "0");
		}
		
		serveur.Envoie(ListeJoueur, "35" + $ + MondeEnCours + $ + DebutPartie + $ + CodeAttaque + $ + TypeAttaque + $ + (DureePartie / 1000) + $ + OldVie1 + $ + OldVie2 + $ + OldDoubleAttaque + $ + PresidentRouge + $ + PresidentBleu + (ResetScore ? $ : ""));
		super.Nouvelle_Partie();
		//

		if (CarteEnCours != null) {
			CarteEnCours.setCouleursPartie(this);
			Charger_Case(CarteEnCours.getMap());
			
			if (CarteEnCours.estRallyOfficiel()) {
				RallyOfficiel = true;
			    RallyValide = true;
			    CouleursPersoActivees = false;
			}
		} else if (MondeEnCours.MondePersonnalise) {
			Charger_Case(MondeEnCours.getMapMondePerso());
		}
		
		if (ModePresident) {
			serveur.Envoie(ListeJoueur,"18" + $ + PresidentRouge);
			serveur.Envoie(ListeJoueur,"18" + $ + PresidentBleu);
		}
		
		GestionComptesTournoi();
		
		if (Carte_Repertoriee()) {
			serveur.Envoie(getListeJoueurs(), "CxINFO#Carte chargée : " + CarteEnCours.getTitre());
		}
	}
	
	/**
	 * Force à loader la prochaine map en ignorant le fait
	 * qu'il y ait encore une manche à jouer sur celle en cours
	 * (le match retour ne se fait pas).
	 */
	public void LoadIgnorerNbManches() {
		numMancheEnCours = 2;
	}
	
	private MondeForteresse getNextMonde() {
		++numMancheEnCours;
		
		if (playlist != null) { // On charge la nouvelle map de la playlist
			numMancheEnCours = 1;
			serveur.BOITE.Requete(Boite.LOAD_MAP_PLAYLIST_FORTO, new Object[]{this, (Long)playlist.getRandom()});
			return null;
		}
		
		if (CarteEnCours != null && !LoadOff) {
			if (numMancheEnCours > 2) {
				numMancheEnCours = 1;
			}
			
			return MondeEnCours;
		}
		
		LoadOff = false;
		
		if (BoucleMonde.isEmpty() && (numMancheEnCours > 2 || MondeEnCours == null)) { // Fin de la liste & des manches
			ArrayList<MondeForteresse> base;
			
			numMancheEnCours = 1;
			
			if (listePersoMondes.isEmpty()) { // Pas de liste personnalisée des mondes à charger
				base = new ArrayList<MondeForteresse>(serveur.Forteresse_ListeMonde);
				
				if (!ServeurOffi) { // Pas de map président hors offi dans les mondes auto
					base.remove(IdMapPresident());
				}
			} else {
				base = new ArrayList<MondeForteresse>(listePersoMondes);
			}
			
			// Chargement aléatoire des mondes
			while (base.size() != 1) { // Dernier de <base> directement pris en map
				BoucleMonde.add(base.remove((int) (Math.random() * base.size())));
			}
			
			MondeEnCours = base.remove(0);
		}
		
		if (numMancheEnCours > 2) { // Fin de la 2ème manche
			numMancheEnCours = 1;
			
			if (!ServeurOffi) {
				return BoucleMonde.remove(0);
			} else {
				return getMondeOffiSelonNbJoueurs();
			}
		}
		
		return MondeEnCours;
	}
	
	private MondeForteresse getMondeOffiSelonNbJoueurs() {
		if (!ServeurOffi) {
			return BoucleMonde.remove(0);
		}
		
		if (EquipeRouge <= 2 || EquipeBleu <= 2) { // Load Vierge, Président et Couronne
			FrequencePresident++; // Reset avec le chargement du mode président (voir fonction)
			
			if (FrequencePresident < 3 || EquipeRouge == 0 || EquipeBleu == 0) {
				if (MondeEnCours == null || !MondeEnCours.toString().equals("7") || MondeEnCours.MondePresident) {
					return serveur.Forteresse_ListeMonde.get(7);
				} else {
					return serveur.Forteresse_ListeMonde.get(5);
				}
			} else {
				return serveur.Forteresse_ListeMonde.get(IdMapPresident());
			}
		} else if (EquipeRouge <= 3 || EquipeBleu <= 3) { // Doubles - Président
			FrequencePresident++;
			
			while(!BoucleMonde.isEmpty() && !BoucleMonde.get(0).DoubleAttaque) {
				// Maps doubles uniquement avec moins de 3 joueurs dans chaque équipe
				BoucleMonde.remove(0);
			}
			
			if (FrequencePresident < 4) {
				if(BoucleMonde.isEmpty()) { // S'il ne reste plus de maps doubles : map vierge
					return serveur.Forteresse_ListeMonde.get(7);
				} else {
					return BoucleMonde.remove(0);
				}
			} else {
				FrequencePresident = 0;
				return serveur.Forteresse_ListeMonde.get(IdMapPresident());
			}
		} else { // Load de toutes les maps existantes
			FrequencePresident++;
			
			if (FrequencePresident >= 6) {
				FrequencePresident = 0;
				return serveur.Forteresse_ListeMonde.get(IdMapPresident());
			} else {
				return BoucleMonde.remove(0);
			}
		}
	}
	
	public void setListePersoMondes(ArrayList<Integer> liste) {
		int sz = serveur.Forteresse_ListeMonde.size();
		listePersoMondes = new ArrayList<MondeForteresse>();
		
		while (liste.size() > 0) {
			listePersoMondes.add(serveur.Forteresse_ListeMonde.get(liste.remove(0) % sz));
		}
	}	
	
	
	private int IdMapPresident() {
		int i;
		for(i = serveur.Forteresse_ListeMonde.size() - 1; i >= 0 && !serveur.Forteresse_ListeMonde.get(i).MondePresident; --i);
		return i;
	}
	
	private boolean ChoisirPresidentRouge() {
		if (EquipeRouge < 1)
			return false;
		
		PresidentRouge = getPresident(JoueurForteresse.EQP_ROUGE);
		
		return PresidentRouge != -1;
	}
	
	private boolean ChoisirPresidentBleu() {
		if (EquipeBleu < 1)
			return false;
		
		PresidentBleu = getPresident(JoueurForteresse.EQP_BLEUE);
		
		return PresidentBleu != -1;
	}
	
	private int getPresident(int equipe) {
		final int N = ListeJoueur.size();
		Joueur j;
		
		ArrayList<JoueurForteresse> lsJoueursEqp = new ArrayList<JoueurForteresse>();
		
		for (int i = 0; i < N; ++i) {
			j = ListeJoueur.get(i);
			
			if (!j.MortAuto && j._Forteresse.getEquipe() == equipe)
				lsJoueursEqp.add(j._Forteresse);
		}
		
		if (lsJoueursEqp.size() == 0)
			return -1;
		
		return lsJoueursEqp.get((int) (Math.random() * lsJoueursEqp.size())).CodeJoueur;
	}
	
	public void Charge_Map_President() {
		if (ChoisirPresidentBleu() && ChoisirPresidentRouge()) {
			FrequencePresident = 0;
			ModePresident = true;
			SansFrigo = true;
		} else {
			CleanModePresident();
		}
	}
	
	private void CleanModePresident() {
		CarteEnCours = null;
		ModePresident = false;
		PresidentRouge = -1;
		PresidentBleu = -1;
		SansFrigo = false;
		MondeEnCours = serveur.Forteresse_ListeMonde.get(7);
	}
	
	public void Equilibrer(Joueur J) {
		if (ModePresident && (PresidentBleu == J._Forteresse.CodeJoueur || PresidentRouge == J._Forteresse.CodeJoueur)) {
			J.Envoie("CxINFO#Impossible d'équilibrer les équipes quand vous êtes la cible dans le mode président.");
			return;
		}
		
		if (EquipeRouge == Math.ceil(RatioRouge * (EquipeBleu + EquipeRouge))) {
			J.Envoie("CxINFO#Les équipes sont déjà équilibrées.");
			return;
		}
		
		boolean besoinEquiRouge = ((EquipeBleu + EquipeRouge) * RatioRouge) / EquipeRouge > 1.0;
		
		if ((besoinEquiRouge && J._Forteresse.estBleu())
		   || (!besoinEquiRouge && J._Forteresse.estRouge())) {
			
			if (J._Forteresse.estBleu()) {
				J._Forteresse.setRouge();
				++EquipeRouge;
				--EquipeBleu;
			} else if (J._Forteresse.estRouge()) {
				J._Forteresse.setBleu();
				++EquipeBleu;
				--EquipeRouge;
			}
			
			serveur.Envoie(ListeJoueur, "20" + Forteresse.$ + J._Forteresse.CodeJoueur + Forteresse.$ + J._Forteresse.EquipeToCode());
			Mort_Joueur(J);
		} else {
			J.Envoie("CxINFO#Vous êtes déjà dans la bonne équipe.");
		}
	}
	
	/**
	 * Serveur tue <J>.
	 */
	private void Mort_Joueur(Joueur J) {
		J.Vivant = false;
		
		Explosion_Total(J);
		//
		serveur.Envoie(ListeJoueur, "16" + $ + J._Forteresse.CodeJoueur + $ + J._Forteresse.CodeJoueur);
		//
		J.Vivant = false;
		
		if (J._Forteresse.Respawn != null) {
			J._Forteresse.Respawn.Active = false;
		}
		J._Forteresse.Respawn = serveur.new Action(System.currentTimeMillis() + 10000);
		J._Forteresse.Respawn.RespawnForteresse = true;
		J._Forteresse.Respawn.JoueurCible = J;
		//JOUEUR._Forteresse.Respawn = JOUEUR._Forteresse.new Respawn();
		
		if (serveur.Evénement.StValentin()) {
			Joueur StValAmoureux = serveur.TrouverAmoureux(ListeJoueur, J);
			if (StValAmoureux != null) {
				serveur.Envoie(ListeJoueur, "101" + $ + J._Forteresse.CodeJoueur + $ + J._Forteresse.CodeJoueur);
			}
		}
		
		if (ModePresident) {
			if (PresidentRouge == J._Forteresse.CodeJoueur) {
				serveur.Envoie(ListeJoueur, "CxINFO#Victoire des bleus !");
				Nouvelle_Partie(-1, null, false);
			} else if (PresidentBleu == J._Forteresse.CodeJoueur) {
				serveur.Envoie(ListeJoueur, "CxINFO#Victoire des rouges !");
				Nouvelle_Partie(-1, null, false);
			}
		}
	}
	
	public void Mort_Joueur_Balles(Joueur JoueurCible, Joueur Tueur, String INFO) {
		Explosion_Total(JoueurCible);
		//
		Tueur._Forteresse.Score += 1;
		JoueurCible.Vivant = false;
		
		//
		if (INFO.equals("STVAL")) {
			serveur.Envoie(ListeJoueur, "101" + $ + JoueurCible._Forteresse.CodeJoueur + $ + Tueur._Forteresse.CodeJoueur);
		} else {
			serveur.Envoie(ListeJoueur, INFO + "0");
		}
		
		if (JoueurCible._Forteresse.Respawn != null) {
			JoueurCible._Forteresse.Respawn.Active = false;
		}
		JoueurCible._Forteresse.Respawn = serveur.new Action(System.currentTimeMillis() + 10000);
		JoueurCible._Forteresse.Respawn.RespawnForteresse = true;
		JoueurCible._Forteresse.Respawn.JoueurCible = JoueurCible;
		
		
		if (ModePresident) {
			if (PresidentRouge == JoueurCible._Forteresse.CodeJoueur) {
				serveur.Envoie(ListeJoueur, "CxINFO# " + Tueur.getPseudo() + " a tué la cible rouge : victoire des bleus !");
				Nouvelle_Partie(-1, null, false);
			} else if (PresidentBleu == JoueurCible._Forteresse.CodeJoueur) {
				serveur.Envoie(ListeJoueur, "CxINFO# " + Tueur.getPseudo() + " a tué la cible bleue : victoire des rouges !");
				Nouvelle_Partie(-1, null, false);
			}
		}
	}

	public void Touche_Joueur(Joueur JOUEUR, String INFO, int CODE) {
		int Nb = ListeJoueur.size();
		for (int i = 0; i < Nb; i++) {
			Joueur JoueurCible = ListeJoueur.get(i);
			if (JoueurCible._Forteresse.CodeJoueur == CODE) {
				if (JoueurCible._Forteresse.MemeEquipe(JOUEUR._Forteresse)) { // Triche
					serveur.Avertissement_Modo("[Triche] " + JOUEUR.NomJoueur + " a touché via balle un joueur de son équipe sur Forteresse.", false);
					System.out.println("TRC001 " + JOUEUR.NomJoueur + " " + JOUEUR.AdresseIP);
					return;
				}
				if (JoueurCible.Vivant && System.currentTimeMillis() - JoueurCible._Forteresse.DerniereMort > 4000) {
					Joueur StValAmoureux = null;
					int StValVieSolo = JoueurCible._Forteresse.Santé - JOUEUR._Forteresse.Degats;
					
					// St Valentin ~ Vie partagée avec l'amoureux
					if (serveur.Evénement.StValentin() && !JoueurCible.Amoureux.isEmpty()) {
						int vie = JoueurCible._Forteresse.Santé;
						
						// Pas de dégâts par balles entre amoureux (mais recul)
						if (serveur.SontAmoureux(JOUEUR, JoueurCible)) {
							serveur.Envoie(ListeJoueur, INFO + String.valueOf(JoueurCible._Forteresse.Santé + JOUEUR._Forteresse.Santé));
							return;
						}
						
						StValAmoureux = serveur.TrouverAmoureux(ListeJoueur, JoueurCible);
						if (StValAmoureux != null) {
							vie += StValAmoureux._Forteresse.Santé;
						}
						
						JoueurCible._Forteresse.Santé = vie - JOUEUR._Forteresse.Degats;
					} else {
						JoueurCible._Forteresse.Santé -= JOUEUR._Forteresse.Degats;
					}
					
					if (JoueurCible._Forteresse.Santé <= 0) {
						Mort_Joueur_Balles(JoueurCible, JOUEUR, INFO);

						if (StValAmoureux != null) {
							Mort_Joueur_Balles(StValAmoureux, JOUEUR, "STVAL");
						}
						//JoueurCible._Forteresse.Respawn = JoueurCible._Forteresse.new Respawn();
					} else {
						serveur.Envoie(ListeJoueur, INFO + String.valueOf(JoueurCible._Forteresse.Santé));
						
						if (StValAmoureux != null) {
							serveur.Envoie(ListeJoueur, "100" + $ + StValAmoureux._Forteresse.CodeJoueur + $ + JoueurCible._Forteresse.Santé);
						}
					}
					
					
					if (serveur.Evénement.StValentin()) {
						JoueurCible._Forteresse.Santé = StValVieSolo;
					}
				}
				return;
			}
		}
	}

	public void Joueur_Degats_Jambon(Joueur JOUEUR, int DEGATS, String AUTEUR) {
		JOUEUR._Forteresse.Santé -= DEGATS;
		
		
		Joueur StValAmoureux = null;
		int StValVieSolo = JOUEUR._Forteresse.Santé;
		if (serveur.Evénement.StValentin() && !JOUEUR.Amoureux.isEmpty()) {
			int vie = JOUEUR._Forteresse.Santé;
			
			StValAmoureux = serveur.TrouverAmoureux(ListeJoueur, JOUEUR);
			if (StValAmoureux != null) {
				vie += StValAmoureux._Forteresse.Santé;
			}
			
			JOUEUR._Forteresse.Santé = vie;
		}
		
		if (JOUEUR._Forteresse.Santé <= 0) {
			Explosion_Total(JOUEUR);
			//
			int CodeAuteur = -1;
			int Nb = ListeJoueur.size();
			Joueur Joueur = null;
			for (int i = 0; i < Nb; i++) {
				Joueur = ListeJoueur.get(i);
				if (Joueur.NomJoueur.equals(AUTEUR)) {
					if (Joueur.equals(JOUEUR)) {
						Joueur._Forteresse.Score -= 2;
						if (Joueur._Forteresse.Score < 0) {
							Joueur._Forteresse.Score = 0;
						}
					} else {
						Joueur._Forteresse.Score += 1;
					}
					CodeAuteur = Joueur._Forteresse.CodeJoueur;
					break;
				}
			}
			//
			serveur.Envoie(ListeJoueur, "16" + $ + JOUEUR._Forteresse.CodeJoueur + $ + CodeAuteur);
			//
			JOUEUR.Vivant = false;
			
			if (JOUEUR._Forteresse.Respawn != null) {
				JOUEUR._Forteresse.Respawn.Active = false;
			}
			JOUEUR._Forteresse.Respawn = serveur.new Action(System.currentTimeMillis() + 10000);
			JOUEUR._Forteresse.Respawn.RespawnForteresse = true;
			JOUEUR._Forteresse.Respawn.JoueurCible = JOUEUR;
			//JOUEUR._Forteresse.Respawn = JOUEUR._Forteresse.new Respawn();
			
			if (StValAmoureux != null) {
				Mort_Joueur_Balles(StValAmoureux, (Joueur == null ? StValAmoureux : Joueur), "STVAL");
			}
		}
		
		if (serveur.Evénement.StValentin()) {
			if (StValAmoureux != null) {
				serveur.Envoie(ListeJoueur, "100" + $ + StValAmoureux._Forteresse.CodeJoueur + $ + JOUEUR._Forteresse.Santé);
			}
			JOUEUR._Forteresse.Santé = StValVieSolo;
		}
	}

	public void Explosion_Total(Joueur JOUEUR) {
		try {
			while (!JOUEUR._Forteresse.ListeJambonExplosion.isEmpty()) {
				JOUEUR._Forteresse.ListeJambonExplosion.remove(0).Explosion(JOUEUR);
			}
			int NbJambon = ListeJambon.size();
			for (int i = 0; i < NbJambon; i++) {
				Jambombe Jambon = ListeJambon.get(i);
				if (Jambon.estAttacheSur(JOUEUR)) {
					Jambon.Explosion(JOUEUR);
					i--;
					NbJambon--;
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Explosion_Totale "); e.printStackTrace();
		}
	}

	public String Liste_Case() {
		StringBuilder Chaine = new StringBuilder(20000);
		for (int X = 0; X < 200; X++) {
			for (int Y = 0; Y < 100; Y++) {
				Chaine.append(ListeCase[X][Y]);
			}
		}
		return Chaine.toString();
	}
	
	public boolean Carte_Repertoriee() {
		return (CarteEnCours != null && CarteEnCours.estRepertoriée());
	}
	
	public long getIDCarteEnCours() {
		return (CarteEnCours == null ? Rally.MAP_PAS_ENREGISTREE : CarteEnCours.getID());
	}
	
	public String getInfosCarteNouvConnexion() {
		String str = null;
		
		if (Carte_Repertoriee()) {
			if (CarteEnCours.estRallyOfficiel()) {
				str = "CxINFO#Rally chargé : Ultra Rally de Retiti (rally officiel)";
			} else {
				str = "CxINFO#Rally chargé : " + CarteEnCours.getTitre() + " (" + CarteEnCours.getID() + ").";
				if (CarteEnCours.getRecord() != -1) {
					str += "\nRecord de " + CarteEnCours.getRecord() + " sec par " + CarteEnCours.getRecordman() + ".";
				}
			}
			
		}
		
		return str;
	}

	public void Charger_Case(String Map) {
		/*if (Map == null) { // Codes des maps entre "" invisible sur Eclipse ! (visible via Notepad)
			//Map = "99111111111111199999999999999999999999999999991999999911999999999919999999199999191919191111999999999111111111999999999999999999999999999999999999199999991199999991991999999919999919999999999999999999991111111199999999999999999911999999999999999999999999119991999199199911991991991999999999999999999991111111119999999999999991111199999999999999999999999911999199919919991199199199199911111111999999991111111111999999999999111111119999999999999999999999991199919991991999119919919919991991919199999999991119999999999999911111111111999999999999999999991999119991999199199911991991991999199119199999999999111999999999991111111111111199999999999999999999999911999199919919991199199199111119919191999999999911199999999111111111111111119999999999999999999999991199919991991999119919919999999991191199999999111119999111111111111111111111999999999999999999999999119991999199199911991991999999999191911199999999999999999999999999999199911199999999999999919999999911999199919919991199999111111111111911999999999999999999999999999999919991119999999999999999911111111199919991991999119999911111111111919199999999999999999999999999999999999991999999999999999999999999119991999199199911999991111111111119119999999999999999999999999999999999999199999999999999999999999911999199911119991199999111111111119191999999999999999999999999999999999999919999999999999999999999991199919999999999119999911911111111191199999999999999999999999999999999999991999999999999999999999999119991999999999911999991991199911191999999999999999999999999999999999999999199999999999999999999999911999111111111111999999199999991111999999999999999999999999991119111111111119999999999999999999999991199999999999919999999919999999111999999999999999999999999999999999999919991999999999999999999999999119919999999991919999999999999999999999999999999999999999999999999999991999199999999999919999999999911991999999999999999999999999999999919999999999999999999999999999999999999911911111111111199999999991199199999999919999999999999999999991999999999999999999999999999999999999991999111111111111999999999111119999999991999999999999999999999119999999999999991999999999999999999999199911111111111199999999911119991999999199999999999999999999999999999999999999199999999999999999999919991111111111111919999991111999199999919999999999999999999999999999999999999999999999999999999999991999111111111111119999999111199919999991999999999999999999999999999999999199999999999999999999999999199911111111111111999919911111111999999199999999999999999999999999999999911111199999999999991999999919991111119111111199999991999999999999919999919999999999999999999999999999999999999999999999199999991191111111911111111119111199999999999991999991999999999999999991199999999999999999999111111111111111199919919999919999999999919999999999999119919111111111119999999999999999999999999999911111111111111119991991999991999999999991991111111111111999911991999119999999999999999999999919999991111111111111111999199999999999991999999199999999999999999991199999919999999999999999999999991999999111111111111111199919999999999999199999919999999199999199999119999991999999991999999999999999199999911111111111111119991999911111111111111111999199999991999199911999999199911111199999999999999999999991111111111111111999199999999999991111111199999999999999999991199999919991111119999999999999999999999911111111111111119119999999999999119999911111111111111111111119999999999111111999999999999999999999999111111111111119991999999999999911999999111111111111111111111999999199911111199999999999999999999999991111111111111999199999999999991199999991199999999999999991199999119991111119999999999999999999999999911111111111199919999999919999119919999119999199919991999119999111999111111199999999999999999999999999111111111119991999999999999911991999991999999999999999911999111199911111119999999999999919999999999991111111111999199999999999991199199999911111111111111111111111199991111111199999999999999999999999999911111111199911999999999999119919999999199199199999999999999999999111111119999999999999999999999999999111111111911119999999999911991999999919999919919991999199919919999999991999999999999999999999999999991111111999111999999999991199111999999991999999919991999199999999919999199999999999999999999999999999911111199911199999999999119911119999199199199999999999999999999999999999999999999999999999999999999999111119991111999999999911999911111111111111111111111111111111111111111999999999999999999999999999999991111999111199999999991199999999919999999999999999999999999999999999999999999999999999999999999999999911199911111999999999119999999999999999999991999999999999999999999999999999999919999999999999999999999119991111199999999911999999999199999999999999999199999999999999999999999999999999999999999999999999991191111111999999991199999999919999999999999999999999999999999999999999999999999999999999919999999999199911111199919999111999999991999999999999999999999999999999999999999999999999999999999991999999999919991111119999999911119999999119999999199999999999999999999999999999999991111111111199911199999999991999111111199999991111999999911199999999999999999999999991111111111999999199999991999991999999999999199911111119999999111199999991119999999999999999999999999999999999999999999999999111111199999999999919991111111999999911119999199111199999999999199999999999999999999999999999999999999999919999999999991999999991199999999999999999911119999999999999999999999999991999199999999999999999999991999919999999119111119119999999119999999999999999999999999999999999999999999999999999999999999999999119991999999919999999991999999911119999999999999999999999999999999999999111111199999999999999999999911111199999991999999999111199911119119999999999999999999999999999999999999999999999999999999999999999999919999999199999999919919999999999999999999999999999999999999999999999999999999999999999999999999999991999199911119111999991999999999999999999999999999999999999999999999999999999999999999999999999999999199919991199999999999199999999999999999999919999999999999999999999991999999999999991111111111191111111111999119999999999919999999999999999999991199999999999999999999999999999999999999111111111119999999999999911119111199991999999999999999999999111111119999999999999999999999999999119999999999991999999999999991999999999999199999999999999999999911111199999999999999999999999999999999999999999999199999999999999199999999999911111119999999999999991111119999999999999999999999999999999999999999999919999999999999919999999999999999999999999999999999111199999199999999999999999999999999999911111999999999999999999999999999999999999999999999999999999999911199991999999999999999999999999999991111999999999999999999999999999999999999999999999999999999999999199999999999999999999999999999999999111999199911119999999999999999999999999999999999999999999999991999999999999999999999999999999999999911199919991111999999999999911999999999999999999999999999999999199111199999999999999999999999999999991199991999911199999999999999999999999911999999999999999999999199999999999999999999999999999999999999119999199999119999999999999999999999991111111111111199999999119999999999999999999999999999999999999919999119999991199999999999999999999999999111111119999999999919999999999999999999999999999999999999991999911111999919999999999111999999999999999999999999999999911199999999999999999999999919999999999999999999111119999999999999919999999999999999999999999999999991119999999999999999999999999999999999999999999911111199999999999999999999999999999999119999999999999111999999999999999999999999999999999991999999991111111999999999999999999999999999999991999999999999911199999999999999999999999999999999991111119999199991119999999999911999999999999999991199999999999111119111111999999999999999999999999999911111999919999911999999999999999999999999999999919999999999911119999999199999999999999999999999999911111119991999999119999999999999999999999999999911999999999991111999999919999999999999999999999999991111111991199199999999999999999991111111111119999199999999999191199999999999999999999999999999999999111111119919911999999999999999999991111111111999119999999999919999999999999999999999999999999999999911111111191991119999119999999999999119999999199991999999999119999111199999999999999999999911111911999111111119199911199111999999999999911999999919999199999999911999999999999999999999999999999999999999911111111919999111111199999999999991999999999199919999999991199999999999999999999999999999999999991999999919191999991111119999999999991199999999199991999999911119999999999999999999999999999999999999999999919991111999999999999999999999199999999991999199999991199999999111111199999999999999999999999999999999191999119999999999999999999119999999991999919999999119999999999999999999999999999999999919999999999199919991199999999999999999911999999991919991999999911999999999999999999999999999999999999999999999991919999111999999999999999911999999191111111199999991999999999999999999999999999999999999999999999991999199991119999999999999991999999999991999991111111199999999999999999999999999999999999999999999999919199999911999999999999991199999199999199919911111111999999999999999999999999999999999999999999999919991999991119999999999999119999999999999999991111991999999999999999999999999999999999999199999999999191999999999999999999999119999999911119111119911199999999999999999999999999999999999999999999999999199919999999999999999999911999999999911919199991199999999999999999999999999999999999999999999999999911111111111111119999999911999999999999191919999119999999999999999999999999999999999999111111119999991111111111111111999999991199999999919999199999911999999999999999999999999999999999999999999999999999111111111111111999999991199999999991999999999999199999999999999919999999999999999999999999999999999999999999999999999999999119999999999199999999991119999999999999999999999999999999999999999999999999999999999999999999999999911999999999911919111119119999999999999999999999999999999999999999999999999999999999999999999999999911999999999991199999999991999999999999999999999999999999999999991999999999999999999911111999999999991199999999999119919999991199999999999999999999999999999999999999999999999999999999991999199999999999999999999111111119111119119999999999999999999999999999999999999999999999999999999999199919999999999999999999911111111991999991999999999999999999999999999999999999991999999999999999999911111999999111111111199999999999919919999199999999999999999999999999999999999999999999999999999999999999999999911111111199999999999999999199919999999999999999999999999999999999999199999999999999999999111119999999999999999999999999999911111991999999999999199999999999999999999999991999999999999999999991999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999919999999999999999999999999999999999999999999999999999999999999999999999999999199999999999991999999919999999199999999999999999111199999999999999999999999999999999999999999999999999999999999999999999911111999999999999999999999911119911999999999999999999999999999999999999999999999199999999999999999999999999999999999999999999999999991991919999999999999999911111111119999999999999999999999999999999999111119999999999999999999999999999111999999999999999999999999999999999999999999999999999999999999999919991999999999999999999999999999999999999999999999999999999999999999999999999991999999999999999999991919199999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999191119999999999111199999999999911111911111999999999999999999999999999999999999999999999999999999999999999999999999999999999999999991111191111119999999999911119111199999999999999999199999999999999999999999999999999999999999999999999999999999911199999999999999999999999999999999999999999999999999999999999999999999991119119999999999999999999991111999199999999999999999999999999999999999999999999199999999999999999999999999999999999999999999999911199999999999999999999999999999999199999999919999999999999999999999999999999999999999999999999999991111999919911119111111999999999999999999999999999999999999999999999999999999999199999999999999999999911199999999999999999999999999999999999999999199999999999999999999999999919111119999999111199999999991119999999999999999999999999999999999999999999999999999999999999999991999999999999999919999999999999911999999999999999999999999999999999999919911999999999999999911199999999999999999999999999999999999999199999999111191119999999999999999999999191999999999999999999119999919999999999999999999999999999999919991111999999999999999999999999991111111199999999999999999191999999991119111119999999999999999999999991991999999999999999999999999999999999919999999999999999199999999999999999999999999999999999999999991991999999999999999999999999999999999999999999199999999199999999999999999999999199999999999999999999911999999999999999999999999999999999999999999999999999999999999999999999999999999999919999999999999999999999999999999999999999999999999999999999999999999999999999999999911191111119999999999999999999999999999999999999999999999999999999999999999999999999999999999999999991999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999919999999999999999999991111191199999999999999999999999999999999999999999999999999999919999999999999999999999999999999999999119999919999999999999999999999999999999999999999999999999999999999999999999999999999999999199999999111199991999911119999999999999999999999999999999999999999999999999999999999999999999999999999999999999911999199991999999999999999999999999999911111199999999999999999999999999999999999999999999999999999991119919999999999999999999999999999999191111199999999999999991919191999999999999999999999999999999999111991911999999999999999999999999999111111111999999999999991919191999999999999999999999999999999999911199119999999999999999999999999999991911111999999999999999999999999999999999999999999999999111111991119911999999999999999999999999999999991111999999999999999999999999999999999999999999999111111111111111991199999999999999999999999999999999111199999999999999999999999999999999999999999999999999999999999999119999999999999999999999999999999911119999999999999999911111119999999999999999999999999999999999999911999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999991199999999199999999999999999999999999999999999999999999999999999999191111111199999999999999999999999119999999999999999999999999999999999999999999999999999999999999999999911111111119999999999999999999911999999999999999999999999999999999999999999999999999999999999999999991111111999999999999999999999991199999999999999919999999999999999999999999999999999999999999999999191111119999999999999991999999999119199999999999999999999999999999999999999999999999999999919999999999911111999991999999999999999999911111991191111119999999999999999999999999999999999999999999999999999991111199999999999999999999999991919191999991119999999999999999999999999999999999999999999999999999191111111999999999999999999999991119199999999199999999991999999999999999999999999999999999999999999999911111119999999999999999999999999991111999911111111999199999999999999999999999999999999999999999119991111111199999999999999999999999999911119999911111199919999999999999999999999999999999999999999999191111111111191111111199991999999999999111999999999999991999999999999999999999999999999999999999999999999999999999999999119999999999999999991111999999999999119999999999999999191999999999999999999999991999999999999999999911999999999999999999911119999999999911199999999999991919191999999999999999999999919999999999999999991199999999999999999999111199999999991111999999999999191919191999999999999999999999111111199999999999111111999999999999999911199999999999111111111111119111111111111111111111199999999999999999999999999911111119999999999999991199999999999911119999999999199999999999999999999999999999999999999999999999991111119999999999999999119999999999991119999999999999999999999999999999999999999999999999999999999999111111991119999999999911999919999999119999999999999999999999919999999999999999999999999111111111199911111199999999999919991199999999999919999999999999999999999999999999999999999999999999999999999999991111119999999919991999119999999999991999999999999999999999999999999999999999999999999999999999999999111111199999991999999911199999999999199999999999999999999999999999999999999999991111111111111111111111111111111991111111111119999999999919999999999999999999999999999999999999999991999111111111111111111111111111119111111111119999999999991999999999999999999999999999999999999999999999991999199919999111999999999999999999991119999999999999199999999999999999999999999999999999999999999919999999999999991199999999999999999999111999999999999919999999999999999999999999999999999999999999991999199919991999919999199999999999999911199999999999991999999999999999999999199999199999919999999999911111111111999911999999999999999999991199999999999999199999999999999999999999999999999999999999999991111111111199919199999999999999999999119999999999999919999991999999999999999999999999999999999999999999999999911919119999999999999999999911999999999999991999999999999999999999999999999999999999999999999999999991119191999999999999999999991111999919999999199999999999999999999999999999999999999999999999999999999119991199999999999999999999111119999999999919999999999999999999999999999999999999999999999999999999911999919999999999999999999911111999999999991999999999999999999999999999999999999999999999999999999991199911999999999999999999991111999999999999199999999999999999919999999999999999999999999999999999999199919199999999999999999999111199999999999919999999999111111999999999999999999999999999199999999999919919119999999999999999999911199999999999991999999999999999999999999999999999999999999919999999999991119191999999999111199919991119999999999999199999199999999999999999991111999991999999999999999999999119991199991999999999991999111999999999999919999191999999999999999999999999999199999999199999999999911999919999111111111111111111999999999999911111199919999999999999999999999999999999999911119999999991191911999911999999999999991199999999999991999991111111111111111111111111111199999999991999999999999111919199991199999999999999119919999999999199991199999999999999991111111999991999999999199999999999911919119999119991199999999911999999999999919999191119999999999999999911199999919999999999999999999991199991999911999119999999991199199999999999111199999999999999999999999999999999999999991999999999999119999199991199911999999999119999999999999911911111119999999999999999999999999999999999111119999999911119919999119991199999999911991999999999991999999999999999999999999999999999999999999919999999999991199111999911999119999999991199999999999999199911111111199999999999999999999999999999999999999999999119999199991199911999999999119999999999999919911111111119999999999199999999999999999999199999999991111999999999119991199999999911999999999999991999999999991999999999111999999999999999999919999999111991199991999911999119999999991199199999999999199999999999199999999911199999999999999999999999991199911111111111111199911999999999119999999999999919999999999919999999911999999999999999999999999111111111111111111111119991199999999911991999999999991999999999999999999999999999999999999999999999911111111111111111111111999119999999991199999999999999199999999999199999999999999999999999999999999991999999919991199911999199911111111111119999999999999919999999999919999999999999999999999999999999999199919991999119999999999911111111111111991111111111111111111199991999999999999999999999999999999999919991999199911999119999999999991119191199919991999199911111119999199999999999999999999999999999999999999199919991199911999999999999119191119991999199919999991111111119999999999999999999999999999999999199919999999119991199919999999111191911999991999199919999999999999999999999999999999999999999999999919991999199999999119991999999991191911199999199919991999999999999999999999999999999999999999999";
			//Map = "99999999999999999999999229999999999999999992999999999999999999999999999999999999999999999999999999999999999999999999999999922999999999992999992299999999999999999929999999999999999999299999999999999999999999999999999999999999299929292929292929229999999999999999922292999999999999999229999999999999999999999999999999999999999999999292929292929222222222222299999999299299999999999999922999999999999999999999999999999999999999992999999999999999992299999999999999922929929999999999999992299222222222222229999999999999999999999999299999999999999999229999922222222222222222222222222229999229922222222222222999992222222222222222222229999999999999999922999999999999999222222222222222222999922992299999299999999999292929292929292929292999999999999999992299999999999999922222229999999999999992299229999929999999999922929292929292929292299999999999999999222222222222999999992992999999999999999229922922292922299999992929292929292929292929999999999999999922999999999999999999299299999999922229922992292929292929999999229292929292929292922992229999999999992299999222222222222229929999999929999992299229292929292999999929292929292929292929299292999999999999229999999999999992222992999999929999999229922929992929999999992222222222222222222229922299999999999922999999999999999222299299999929999929922992222222222222999999299929992999299929992999999999999999992222222222229999999929922222229999999992299222222222222299999929292292292929292929299999999999999999229999999999999999992992999999299999999229929999999999999999992929299929992929299922229999999999999922999992222222222222299299999929999999922992999999999999999999222222222222222222222292999999999999992299999999999999922229929999999299999992222299999999999999999922222222222222222222222299999999999999229999999999999992222992999999299999999229999999999999999999992222222222222222222229999999999999999922222222222299999999299299999929999999922999999999999999999999999999999999999999922999999999999999992299999999999999999929922222229999999992299999999999999999999999999999999999999992299222999999999999229999922222222222222992999999999999999229999999999999999999999999999999999999999229929299999999999922999999999999999222299299999999999999922999299999999999999999999999999999999999922992229999999999992299999999999999922229929929999999999992299999999999999999999999999999999999999992299999999999999999222222222222999999992992999999999999999229929999999999999999999999999999999999999229999999999999999922999999999999999999299299999999999999922999999999999999999999999999999999999999922222999999999999992299999222222222222229929999999999999992299929999999999999999999999999999999999992229299999999999999229999999999999999999992999999999999999229999999999999999999999999999999999999999222229999999999999922999999999999999999999299299999999999922992999999999999999999922999999999999999922999999999999999992299999222229999999222229999999999999992299929999999999999999992299999999999999992299999999999999999229999922222229992222222999999999999999229929999999999999999999229999999999999999229922299999999999922999992222222222222222299999999999999922999299999999999999999922992999999999999922992929999999999992299999222999999999992229999999999999992299299999999999999999992299299999999999992299222999999999999229999922299999999999222992999999999999229999999999999992999999229929999999999999229999999999999999922999999229999999999922999999999999999922999999999999999999999922992999999999999922999999999999999992299999922999999999992299999999999999992299929999999999999999992299299299999999992222299999999999999229999992299999999999229999999999999999229999999999999999999999229929929999999999222929999999999999922999999929999999999929999999999999999922992999999999999999999922992992999999999922222999999999999992299999992999999999992999999999999999992299999999999999999999992299299299999999992299999999999999999229999999299999999999299299999999999999229999999999999999999999229929929929999999229999999999999999922999999229999999999922999999999999999922999999999999999999999922992992992999999922992229999999999992299999922999999999992299999999999999992299999999999999999999992299299299299999992299292999999999999229999992299999999999229999999999999999229999999999999999999222229929929929999999229922299999999999922999992229999999999922299999999999999922299999999999999999922222992992992992999222999999999999999992299999222999999999992229929999999999992292229999999999999992222299299299299299992299999999999999999229999922222222222222222999999999999999229999222999999999999999229929929929929999222229999999999999922999992222222999222222299999999999999922999999922299999999999922222222222222999922292999999999999992299999222229999999222229999999999999992299999999992229999999992222222222222299992222299999999999999229999999999999999922999992999999999999229999999999999999999999229999999999999999229999999999999999922999999999999999992299999999999999999922999999999999999999999922999999999999999922999999999999999992292999999999999999229999999999999999992299999999999999999999992299999999999999992299222999999999999222999999999999999922999999999999999999229999999999999999999999229992222222222222229929299999999999922999299999999999992299999999999999999922999999999999999999999922999222222222222222992229999999999992299299999999999999229999999999999999992299222222222222299999992299999999999999992299999999999999999229999999999999999922999999999999999999229922222222222229999999229999999999999999229999999999999999922929999999999999992299999999999999999922999999999999999999999922222299999999999922222999999999999992229999999999999999229929999999999999992222222299999999999999992299999999999999992229299999999999999229992999999999999922999999999999999999229999999999999999999999229999999999999999222229999999999999922992999999999999992299999999999999999922999999999999999999999922222299999999999922999222222222222222299999999999999999229999999999999999992222299999999999999999992299999999999999992299922222222222922229299999999999999922999999999999999999229999999999999999999999229999999999999999229992222222222292222299999999999999992299999999999999999922999999999999999999999922222299999999999922999999999999999992299929999999999999229922222222222222222222222299999999999999992299999999999999992299999999999999999229929999999999999922992222222222222222229999999999999999999999229999999999999999229929999999999999922999999999999999992299999999999999999922999999999999999999999922222299999999999922992999999999999992292999999999999999229999999999999999992222299999999999999999992299999999999999992299299999999999999222999999999999999922999999999999999999229999999999999999999999229999999999999999229922229222229999922999299999999999992222222222222222299922999999999999999999999922999999999999999922992999999999999992299299999999999999229999999999999929992222222299999999999999992229999999999999992299299999999999999229999999999999999922999999999999992999229999999999999999999999222222299999999999229922292222229999922992229999999999992222222222222222299922999999999999999999999922999922222999999922992999999299999992299222999999999999229999999999299999992222299999999999999999992299999999222229992299299999999999999229922299999999999922999999999929999999229999999999999999999999229999999999999999229929999992999999922999999999999999992222222222222999999922999999999999999999999922999999999999999922992292222299999992299999999999999999229999992999999999992222222299999999999999992299999999999999992299299999999999999222229999999999999922999999299999999999229999999999999999999999229999999999999999229922229999999999922222999999999999992222222229999999999922999999999999999999999922999999999999999922992999999999999992222299999999999999229929999999999999992222299999999999999999992299222222222222222299299999999999999229999999999999999922992999999999999999229999999999999999999999229922222222222222229922222222999999922999999999999999992222299999999999999922999999999999999999999922999999999999999922992999999999999992299222999999999999229999999999999999992299999999999999999999992299999999999999992299299999999999999229922299999999999922999999999999999999229999299929992999999999229999999999999999229922222999999999922992229999999999992299999999999999999922999292229222922999999922999999999999999922992999299999999992299999999999999999229999999999999999992299922222222222229999992299999999999999992299299999999999999229999999999999999922999999999999999999229999929999999999999999229999999999999999229929992999999999922222999999999999992299299999999999999922999999299999999999999922999999999999999922992222299999999992222299999999999999229999999999999999992299999299999999999999992229299999999999992299299999999999999222229999999999999922229999999999999999229999299999999999999999222299999999999999229929999999999999922999999999999999992299999999999999999922222299999999999999999922222999999999999922992999999999999992299999999999999999229999999999999999992299992999999999999999992222222222222999992299222222229999999229922299999999999922992999999999999999229999929999999999999999222229999999999999229929999999999999922992229999999999992299999999999999999922999999299999999999999922229999999999999922992222999999999992299222999999999999222299999999999999992299999299999999999999992229999999999999992222222299999999999229999999999999999922999999999999999999229999299999999999999999229999999999999999222222229999999999922999999999999999992299999999999999999922222299999999999999999922999999999999999922999999999999999992299929999999999999229929999999999999992299992999999999999999992299999999999999992299999999999999999229929299999999999922999999999999999999229999929999999999999999229999999999999999229999999999999999922999299999999999992222999999999999999922999999299999999999999922999922222222292222999999999999999992299999999999999999229999999999999999992299999299999999999999992299992222222229222299299999999999999222999999999999999922999999999999999999229999299999999999999999229999222222222222229992999999999999922229999999999999992299299999999999999922222299999999999999999922999929999999999922992999999999999992222999999999999999229999999999999999992299992999999999999999992299992999999999992299929999999999999222999999999999999922229999999999999999229999929999999999999999222222299999999999229929999999999999922999299999999999992299999999999999999922999999299999999999999922999999999999999922999999999999999992299292999999999999229999999999999999992299999299999999999999992299999999999999992299929999999999999229992999999999999922992999999999999999229999299999999999999999229999299999999999229999999999999999922999999999999999992299999999999999999922222299999999999999999922999929999999999922992999999999999992229999999999999999222299999999999999992299992999999999999999992222222999999999992299999999999999999222299999999999999922999999999999999999229999929999999999999999229999999999999999229992999999999999922229999999999999992299999999999999999922999999299999999999999922999999999999999922999999999999999992229999999999999999229929999999999999992299999299999999999999992299992999999999992299299999999999999229992999999999999922999999999999999999229999299999999999999999229999299999999999229992999999999999922992929999999999992299999999999999999922222299999999999999999922222229999999999922999999999999999992299929999999999999229999999999999999992299999999999999999999992299999999999999992299299999999999999229999999999999999922992222222229222222229999999999999999999999229999999999999999229992999999999999922299999999999999992299992222222922222222999999999999999999999922999929999999999922999999999999999992222999999999999999229999222222222222222299992999299929999999992299992999999999992299299999999999999222299999999999999922222222229222222222229992229222922299999999222222299999999999229992999999999999922299999999999999992299999999999999999922992222222222222299999922999999999999999922999999999999999992299929999999999999229999999999999999992299222222222222299999992299999999999999992299299999999999999229929299999999999922999999999999999999229999999999999999999999229999999999999999229992999999999999922999299999999999992299999999999999999922999999999999999999999922999999999999999922999999999999999992299999999999999999229999999992222229992222222999999999999999992299999299929992992299929999999999999229992999999999999922999992222229999999229999299999999999999999229922292229222922229999999999999999922992929999999999992222222229999999999922999929999999999999999922992222222222222222999999999999999992299929999999999999222229999999999999992222222999999999999999992299999999999999992299999999999999999229999999999999999922999999999999999999229999999999999999999999229999999999999999222222222229222222222999999999999999992299999999999999999922999999999999999999999922999999999999999922222222222922222222299999999999999999229999999999999999992222222999999999999999992222229999999999992299999922292229999999999999999999999922999999999999999999229999299999999999999999222222999999999999229999999999999999999999999999999999992299999999999999999922999929999999999999999922222222299999999922999999999999999999999999999999999999229999999999999999992222222999999999999999992222222229999999992299999999999999999999999999999999999922999999999999999999229999999999999999999999222222222222999999222222222222222222222999999999999999992299999999999999999922999999999999999999999922222222222299999922222222222222222222299999999999999999229999999999999999992222222999999999999999992222222222222229992222222222222222222229999999999999999922999999999999999999229999299999999999999999222222222222222999222929292929292929222999999999999999992299999999999999999922999929999999999999999922222222222222299922929292929292929222299299999999999999229999999222222222222222222999999999999999999999999999999999992229292929292929292229999999999999999922999999922222222222229999999999999999999999999999999999999999229292929292929292222999999299929992992299999999999999999992999999999999999999999999999999999999999922292929292929292922299999222922292229229999999999999999999222222999999999999999992222222222222222222222222222222222222222222222222222222222222222222222222229929999299999999999999999222222222222222222222222222222222222222222222222222222222222222222222222222992999929999999999999999999999999999999999922999999999222922292229999999999999999999999999999999992299222222999999999999999999999999999999999992299999999992999299922999999999999999999999999999999999229929999999999999999999999999999999999999999229999999999999999999299999999999999999999999999999999922992999999999999999999999999999999999999999922999999999999999999929999999999999999999999999999999992299222222999999999999999999999999999999999992299999999999999999999999999999999999999999999999999999229929999299999999999999992999999999999999999229929992222222229999222292222999299999999999999999999922992999929999999999999999999999999999999999922992999999999992999929929299999929999999999999999999992299222222999999999999999999999999999999999992299222222229999299992992229999922999999999999999999999229929999999999999999999999999999999999999999229929999999999999999299999999992299999999999999999999922992999999999999999999999999999999999999999922992999999999992999929999999999229992222999999999999992299299992999299929999999999999999999999999992299299922222222299992999999999922999222299999999999999229929992229222922299999999999999999999999999229929999999999929999222222222222222222222229999999999922992992222222222222999999999999999999999999922992222222299999999922222222222222222222222999999999992299299222222222222299999999999999999999999992299299999999999299992222222222222222222222299299999999229929999999999999999999999999999999999999999229929999999999929999229922229999999999929929999999999922992222229999999999999999299999999999999999922992999222222222999922992229999999999992992999999999992299299999999999999999999999999999999999999992299299999999999299992299229999999999999299229999999999229929999999999999999999999999999999999999999229922222222999999999229922299999999999929922999999999922992222229999999999999999999999999999999999922992999999999992999922992222999999999999992229999999992299299999999999999999222222229999999999999992299299999999999299992299299299999999999299222999999999229929999999999999992999222222999999999999999229929992222222229999229929999999999999929922999999999922992222229999999999999999999999999999999999922992999999999992999922992992999999999992992299999999992299299999999999999999999999999999999999999992299222222229999999992299222299999999999299299999999299229929999999999999992999222222999999999999999229929999999999929999229922299999999999929929992222292222992222229999999999299922222299999999999999922222999999999992999922992299999999999992992999222222222299299999999999999929999999999999999999999992222299922222222299992299222999999999999299299999999999229929999999999999999999999999999999999999999229929999999999929999229922229999999999929929999999999922992999999999999999999922222299999999999999922992222222299999999922992222222222222222992999999999992299299999999999999999999999999999999999999992299299999999999299992299222222222222222299222222229999229922222299999999999992999999999999999999999229929999999999929999229999999999999999929922222222999922992999999999999999999922222299999999999999922992222222222222999922999999999999999292992999999299992299299999999999999999999999999999999999999992299222222222222299992299999999999999992299299999929999229929999999999999999999999999999999999999999229999999999999999999229922222299999999929922222222999922992999999999999999999222222299999999999999922999999999999999999922992299999999999292992999999999992299222222999999999999922222229999999999999992299999999999999999992299229999999999992299299999999999229929999999999999999999999999999999999999999229922229999999999999229922222299999999929922222222999922992999999999999999999999999999999999999999922992999299999999999922992299929999999292992222222299992299299999999999999999992222229999999999999992299299929999999999992299229992999999992299299999929999229929999999999999999999222222999999999999999229929999299999999999229922999999999999929929999992999922992222229999999999299999999999999999999999922992999299999999999922992299929999999292992222222299992299299999999999999922999999999999999999999992299299929999999999992299222222999999992299299999999999229929999999999999999292222222999999999999999229922229999999999999229922999999999999929929999999999922992999999999999999229999999999999999999999922992999299999999999922992299999999999292992222222299992299222222999999999929999999999999999999999992299299929999999999992299229999999999992299299999929999229929999999999999999999222222999999999999999229929999299999999999229922222299999999929929999992999922992999999999999999999999999999999999999999922992999299999999999922992299999999999292992222222299992299299999999999999999999999999999999999999992299299929999999999992299229999999999992299222222229999229922222299999999999999999222299992222999999229922229999999999999229922222299999999929929999999999922992999999999999999999999922229999222299999922992999299999999999922222299999999999292992999999999992299299999999999999999999999999222299992222992299299929999999999992222229999999999992299222222229999229929999999999999999999999999922229999222299229929999299999999999222222999999999999929929999992999922992222229999999999999999999999999999999999922992999299999999999922992222229999999292992999999299992299299999999999999999999999999999999999999992299299929999999999992299229999999999992299222222229999229929999999999999999999999999999999999999999222222229999999999999229922222222222222222222222222999922999999999999999999999999999999999999999999999999999999999999999922999999999999999999999999999999922299999992999299929999999999999999999999999999999999999999999999992299999999999999999999999999999922229999992229222922299999999999999999999999999999999999999929999999999999999999999999999999992999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";
			//Map = "92222222222222292999999929222999299999992999999922999999999999999999999999999999999999999999222222229222222222222229299999292299299929999999299999992299999999999999999999999999999999999999999999999999922222222292222929222999292299992999999929992999229992222222222222222222222222222222222222999999999992222222222922292999299929992999299299992999299922999222222222222222222222222222222222222299999999999222999999929229299929992922999929929999299929992299929222222222222222222222222222222222229999999999922222222229222929222929299922922992999929992999229992992999992929222222222222222222222299999999999992222222229222222999299929222929299222222999299922999999222222222222222222222222222229999999999999999222222222222222999929992999299229929999299929992299999229292999992222222222222222999999999999999999922222222222222992222999292229992992999929992999229999922222222222222222222222999999999999999999999992222222222222999999292929992999299229992999299922999992222222222222222222299999999999999999999999999999999299929999999929992922229229992999299929992229999299922222222222229999999999999999999999999999999999999999999999992999299929292999222229992999222299929992222222222999999999999999999999999999999999999999929992999992299929222999222929992999299922229992999222222299999999999999999999999999999999999999222222222222222229999999299999992999999929992222999229999999999999999999999999999999999999999999999999299999999999999999999929999999299929992999222299922999999999222222222222222999299999999299999999999929999999999999999999992999999922222222299922229992299999999999222222222222999929999999929999999999999999999999999999222222222222222299299999992222999222299922922222222222222999929999999999999999999999999999999999999922299999999999299999999999222299922229999929222222222222299992222222222999999999999999999999999999999229999999299929992222222222229992222299292922299999999299999222222222299999999999999999999999999999922999999929992999229999999999999222222999992229999999999999222222222229999999992999999999999999999922299299992999299929999999999999922222299999222992999999999222929292922999999999992999999999999999992229929999299929992999999922222222222999922222299299999999922929292929229999999999999999999999999999222992999929992999299999922222222222299999999999929992299999929999999999999999999999999999999999999222299299992999999929999929929929929992999999999992999999999929929999929299999999999999299999999999222229929999299929992999992222222222222229999999999299999999929292999992992299999999999999999999999999992992222222222222299999222222222222222999992222929999999992299222222292992999999999999999999999999999999999999999999999999999999999999999999999999992999999999292929999999922299999999999999999999999992222999999999999999999999999999999999999999999999299999999922992999999929222999999999999999999999992222222222222222222222299992222222222999999999999929999999992929999999999222299999999999999999999992222222222222222222222999999222222222299999999992222999999999229922222229299999999299999999999999922222222222222299992999299999222222222229999999999999999999999929292999992992999999922299999999999922222222222222229229229229999222222222222299999999999999999999992299299999292999999929929999999999999922222222222222999929999999222222222222222999929999999999999999292922222229999999999992999999999999999222222222222222222222299222222222222222229992999999999999992222999999999929999999999299929999999999922222222222222222222229922229999999922222999229999999999999922929222299299999999999929929999999999999999999999999999999999992229992992999222299999999229999999992229929929929299999999992929999999999999999999299299299299299299229999999999992229999999992222222992229292992992992299999999229999999999999999999999999999999999999922929999999929222999999999992222292222229299222292929999999929999999999999999992222222222222222222222299222222229922299999999992222229999992999999999922999999992299999999999999999222222222222222222222222999999999922229999999999229999999999222929292929299999999292999999999999999922222222222222222222222222222222222222299999999922999999999992929292929229992999929929999999999999992222222222222222222222222222222229222229999999992299999999922222222222222299299992999299999999999999999999999999999999999999999999999999222999999999229999999992222222222222229929999299999999999999999999999999999999999999999999999999999999999999999922999999999222222222222222992999929999999999999999999999999999999999999999999999999999999999992222222299999999922222222222222229299992929299999999999999999999999999999999999999999999999999999999999999229999999992222222222222999929999229292999999999999999999999999999999999999999999999999999999999999922999999999222222222222229992999929999999999999999999999999999999999999999999999999999999999999999992299999999922222222222299999299992999999999999999999999999999999999999999999999999999999999999999999229999999992222222222229999929999292929999999999922222222222222222222222222222222222999999999999999922999999999999999922229999992999922929299999999992299999999999999999999999999999999999999999999999992299999999999999992222999999299992999999999999999299999999999999999999999999999999999999999999999999229999999999999992222299999929999299999999999999999999999999999999999999999999999999999999999929999922999999999999999222222999999999929999999999999999999999999999999999999999999999999999999999992299992299999999999999929999299999999992999999999999999999999999999999999999999999999999999999999999999999229999999999999999999929999999999222229999999999299999999999999999999999999999999999999999999999999922999999999999999999992999999999929999999999999999999999999999999999999999999999999999999999999999992999999999999999999999999999999992999999999999999999999999999999999999999999999999999999999999999999299999999999999999999999999999999222229999999999999999999999999999999999999999999999999999999999999229999999999999999999999999999999929999999999999999999999999999999999999999999999999999999999999999222999999999922229999999999999999992999999999999999999222222222222222222222222222222222222222222222222299999999999999999999999999999999222229999999999999922222222222292222222222222222222222222222222222229999999999999999999999999999999929999999999999999999999999999929299999999999999999999999999999999999999999999999999999999999999999992999999999999999999999999999999999999999999999999922222222222222222222299999999999999999999999999999299929999999999999999999999999999999922222222229999999999999999999999999999999999999999999999999999929929999999999999999999992229999999999999999992999999999999999999999999999999222229999999999999999992929299999999999999229999222999999999999999999299999999999999999999999999999999999999999999999999999229299999999999999922999999999999999999999999929999999999999999999999999999999999999999999999999999929292999999999999992299999999999999999999299992999999999999999999999999999999999999999999999999999992292999999999999999229999999999999999999999999299999999999999999999999999999999999999999999999999999292929999999999999999999999999999999999999999929999999999999999999999999999999999999999999999999999929929999999999999999999999999999999999999999992999999999999999999999999999999222229999999999999999992999299999999999999999999999999999999999999999299999999999999999999999999999999999999999999999999999299999999999999999999999999999999999999999999929999999999999999999999999999999999999999999999999999929999999999999999999222299999999999999999999992999999999999999999999999999999999999999999999999999992292229999999999999992229999999999999999999999299999999999999999999999999999999999999999999999999999292999999999999999999999999999999999999992999922299999999999999999999999999999999999999999999999999929999999999999999999999999999999999999999999999229999999999999999999999999999999999999999999999999992999999999999999999999999999999999999999999999299999999999999999999999999999999999299999999999999999222922999999999999999999999999999999999999999929999999999999999999999999999999999999999999999999999929999999999999999999992222999999999999992222222999999999999999999999999999999999999999999999999999992999999999999999999999222299999999999999999999999999999999999999999999999999999999999999999999999999299999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999929999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999992992992999999999999999999999222222299992222299999999999999999999999999999999999999999999999999999999999299999999999999999999999999999999999999929999999999999999999999999999999999999999999999999999999999929999999999999999999999999922299999999992999999999999992999999999999999999999999999999999999999999992999999999999999999999999992299999929999222229999999999999999999999999999999999999992999999999999999222999999999999999999999929299999929999999999999999999999999999999999999999999999999999999999999999999299999999999999999999992999999992999999999999999999999999999999999999999999999999999999999999999999922299999999999999999929292999999299999999999999999999999999999999999999999999999999999999999999999999929999999999999999999999222222229999999999999999999299999999999999999999999999999999999999999999999992299999999999999999999999992222999999999999999999929999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999299999999999999999999999999999999999999999999999999999999999999999999999999999999992929999999999999999999999999999999999999999999999999999999999999999999999992999999999999999999999992222992999999999999999999999999992299999999999999999999999999999999999999999999999999999999999999992222229999999999999999999999999999299999999999999999999999999999999999999999999999999999999999999992222222999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999992222222299299999999999999922299999999999999999999999999999999999999999999999999999999999999999999992222222999999999999999999992299999999992222222222222999999299999999999999999999999999999999999999992222222229999999999999999999922299999992999999999999999999999999999999999999999999999999999999999992222222229999999999999992292222222229999222222222222222222222999999999999999999999999999999999999992222222222999999999999999999999999999999999999999999999999999999999999999999999999999999999999999992222222222229999999999999922222222222222222222222222222222222229999999999999999999299999999999999999999999999999999999999999992999999999999999999999999999999999999999999999999999999999999992992299922299999999999999999999999999222222222222222222222222222222222222299999999999999999999999999222229999929999999999999999999999999922929292929292922222222222222222222229999999999999999999999999922222992992222222229999999999999999992292929292929292299999999999999999999999999999999999999999999992222299299999999999999999999999999999929292929292929229999999999999999999999999999999999999999999999222229999999222222222229999999922229292929292929292922999999999999999999999999999999999999999999999922222999999999999999992999999992222929292929292929292299999999999999999999999999999999999999922222222222299999999999999992999999999222292929292929292929229999999999999999999999999999999999999999999999222222999999999999992999999999922229222929292929292922999999999999999999999999999999999999999999999922222999999999999992999999999992222222222922292229222299999999999999999999999999999999999999999999992999999999999999992999999999999222922922222222222222929999999999999999999999999999999999999999999999299999999999999992999999999992222922922922922222222222999999999999999999999999999999999999999999999922999999999999992999999999999222922922922292222222922299999999999999999999999999999999999999999999922299999999999992999999999999922922922922229992229999929999999999999999999999999999999999999999999922229999929999992999999999992222922922922999229922999992999999999999999999999999999999999999999999922222299922222299222222222299222922922922299999999999999299999999999999999999999999999999999999999922222229999222229922222222299922922922922299999999999999229999999999999999999999999999999999999999922222229999922222992222222222922922922922229999999999999922999999999999999999999999999999999999999922222222299999999929299999999222922922999999999999999999992299999999999999999999999999999999999999922222222229999999999299999999922922922999999999999999999999229999999999999999999999999999999999999992222222222999999999999999999992922922929999999999299999999922999999999999999999999999999999999999999992222222299999999999999999999222922922999999929999999999992299999999999999999999999999999999999999999999222229999999999299999999922922922299999999999999999999229999999999999999999999999999999999999999999999999999999999929999999992922922299999999999999999999922999999999999999999999999999999999999999999992222999999999992999999999222222999999999999999999929992299999999999999999999999999999999999999999222222229999992222299999999929299999999999999999999999929229999999999999999999999999999999992222222222222299999999999999999999999999999999999999999999999299922999999999999999999999999999999992222222222222229999999999999999999999999999999999999999999999999292299999999999999999999999999999999222222222222229999999999999929999999999999999999999999999999992999229999999999999999999999999999999992929292929292999999999999992999999999999999999999999999999999999922999999999999999999999999999999992929292929292999999999999992222999999999999999999999999999292929292299999999999999999999999999999999229292929292929999999999999999299999999999999999992999999292929292229999999999999999999999999999999222222222222222999999999999922922999999999999999999999999992929292922999999999999999999999999999999922222222222222229999999999999999299999999999999999999999992929292922299999999999999999999999999999999222222222222229999999999999999929999999999999999999999999929292929229999999999999999999999999999999222222222222222999999999999999922999999999999999999999999929292929222999999999999999999999999999999992222222222299999999999999999992299999999999999999999999999999999992299999999999999999999999999999999222222222229999999999999999999299999999999999999999999999299999999229999999999999999999999999999999229999999999999999999999999999929999999999999999999999999922999999922999999999999999999999999999999229999999999999999299999999999992299999999999999922222222222229999222222929292929299229999999999999999999999999999999999999999999999999999999999999299999229292922999999999292929292929222999999999999999999999999999999999999999999999999999999999999999999922222222299999999929992999299922299999999999999999222999999999999299999992999999999999999999999999992929292229929999992222222222222222222222922229999999999999999999999999999299999999999999999999999999222222222992299999299929992999292929222222222999999999999999999999999999929999999929999999999999999922222222222222999929292929299922929222222222999999222999999999999999999992999999999999999999999999222222292222922229992999299929992922222922222299999999999999999999999999999299222222222299999999999929999999999999999999222222222222229222229222229999999999999999999999999999929999999999999999999999992999929992999999999929992999299929229999292222999999222999999999992299999992222299992999999999999999299999999999999999992929222929292292222292222999999999999999999999999999999299999999999999999999999929992222222222222222222229222222292222292222999999999999999999999999999999929999922222229999992999992999999999999999999999999222222222222222222222299999222999999999999999999992999999999999999999999999299999999999999999999999999999999999999999999992999999999999999999999999999299999999999999999999999929999999999999999999999999999999999999999999992299999999999999999999999999929999999999999999999999992999999999999999999999999999999999999999999992229999999999999999999999999992999999999999999999999999299999999999999999999999999299999999999999992222999999999999999999999999999299999999999999999999999922229999999999999999999999292992999999999992222229922299999999999999999999929999999999999999999299992222999929999999999999999999999999999999992222299992292222222222299999999992999999999999999999999999222999999999999999999999999999999999999999922222999222999222222229999999999292222999999999999999999922299999999999999999229999999999999999999999222999999999922222222999999999922999999999999999999999992292999999999999999299999999999992999999999992299999999999222222229999999992999999999999999999999999299992999999999999999999999999999999999999999929999999999992222222299999999299999999999999999999999929999299929292999999999999999999999999999999922999999999999922222229999999922929299999999999999299992299922299999999999999999999999999999999999922299999999999999222222999999992929299999999999999999999229999922222222999999999999999999999999999922222999222229999929999999999999299999999999999999999999929999999999999999999999999999999999999999999222229999992999992999999999999929999999999999999999999992999999999999992999999999999299999999999999992229999999299999299999999999992999999999999999999999999229299999999999292999999999999999999299999999922999999929999922229999999999222299999999999999999999922922999999999222299992999999999999999999999922229999999999992229999999999929999999999999999999999992222229999999999999999999999999999999999999922229999299999999929999999999992999999999999999999999999222999999992999999999999999999999999999999999222999992999999999999999999999999999999999999999999999922299299999999999999999999999999999999999999992299999929999999999999999999999999999999999999999999992299292222299999992999999999999999999999999992229999999999999999999999999999999992999999999999999999299999999999999999999999999999999999999999999922999222229999999999999999999999992992999999999999999929999999999999999999999999999999999999999999999229999992999999999999999999999222299299999999999299992999999999999999999999999999992999999999999999992999999299999999999999999999922222222229299999999999299999999999999999999999999999999999999999999999999999929999999999999999999992222222922229999999999922999999999999999999999999999999999999999999999999999992222222222222999999999999999929922299999999992229922222999929299999999999999999999999999999999999999922222222222229999999999999999992229999999999229292299999999999999999999999999999999999999999999999999222222222299999999992229999999222999999999922922299999999999999999999999999999999999999992999992922299299999999999999999229299999922299999999992999299999999999999999999999999999999999999992229999999929999999999999929992222229999992229999999999299999999999999999999999999999999999999999999929999999922999999999999999999999999999999222999299999929999999999999999999999999999999999999999999999999999999929999999999999999999999999999922299999999992999999999999999999999999999999999999999999999299992222222999999999999999999999999999999229999999999229999229299999999999999999999992999999999999992999922292299999999999999999999999999992222999999999922999999999999999999999999999999999999999999992999999299922999999999999999999999999992922299999999992299999999929999999999999999999999999999999999992999929992299999999999999999999999292922229999999999222999999999999999999999999999999999999999999999299992999999999999999999999999999992999222999999999922229999999999999999999999999999992229222222222222299929229999999999999922222222222999992299999999999222299999999999999999999999999999992229999999992222222992299999999999992222222222299999929999999999922222999999999299999999999999999992922929999299222229922299999999999999222222222299999999999999999992222229999999999999999999999999992922299922992922292929999999999999999922222222299999999999999999999222222299999999999999999992999999929229992299292229999999999999999999992222222229992222299929929999922222222999999999999999999299999999222929999299222229992999999999999999922222222999222229992992999999222222229999999999999999929999992229299999999922299999299999999999999";
			//Map = "99999999999999999999922999999999999999999999999999999999999992999999999999999999999999999999999999999999929999999999999929999999999999999999999999999299999999999299999999992999999999999999999299999999999992299999999999999999999992222299999999999999922992999999999999922222299999999999999999999992999999999999999999999999999999999222929222222992222992229299999992222299922229999922222999222222999299999999922222222222229292222299922222992999992299229292929999999999999999292999999999999999999999922299999992299999999999929292229992222299929999299992922292999999929999999922299999222999992999999999922299999229999999999992922222222222929929999929999292299299999992999999992229999922299999299999922222999929922999999999999292922222222222922222299999999222929999999299999999292999992229999929999992992299999922222222222999929222222222222299999999999999929292299292929999999222222922222999992999999292229999999229999999999992929222222222922222222222222222229229292922999999999929999922299999299999999292999999922999999999999292222222222222222222222222222229929999999299999999992999992229999929999999999299999992299999999999929292222222222929292922299999999992999992929999999999999999992999992999999222229999999229999999999992922222222999992929292222222299999299999222999999999922299922299999299999929922999999922999999999999292999999999999229292929299999999929992229299922222999999992229999929999992922299992992299999999999929222222229999222222222222999999992999929929999929999299999222999922999999992929999992229929999929992929999992999929229292229999999999222222222222222222229999922229922222222222222999999922992222222999222222999299999222229222229999999999999999999999999999999992922992222222222222299999992222222222299992229999929999999999992929999999992222222222222999222999299222999922922292229229999999299999999999999922999992222999999999222222299999999999999999999999999929222999999222222222222999999229999999999999992222229299999999999922999999999999999999999999992999992222229999999999999999999999999922222222222222229999929999999999992222999999999999999999999999299999292292999999999999999999999999992299999999999929999992999999999999292999999999999999999999999929999929922299999999999999999999999999229999999999992999999299999999999922222229222922292229222922292999999222299999999999999999929922222222999992999999299999929999999999992292292229222922292229222922299999922222999999999999999992999229922222222222299929999992222222222222222222222222222222222222222229999999922222222222222299999229922999999999929999992999999222999999999999922222222222222222222222222222222222992999999999929999992992299999929999999999299999992299999999999992299999999999999999999999999999992229299999999992999999992229229922999999999929999999929992222999999222299999999999999999999222222299229922222222222299999999999929992999999999992999999992999292999999922999222222222222222292922229992922992999999999929999999999992229222222222222299222222299922299999992229929292929292929229292222999292229299999999992999999999999292229229229229229929992229992229999999292992222222222222222929229299929229929999999999999999999999922222222222222222992222222999292999999922299292999999999992222929929999922992999999999999999999999992999222999999999222299299299922299999992299922299999999999222292292999992229229999999999999999999999222929299999999929922299929992229999999222992929999999999929222229299999222929999999999999999999999999222229999999992999999992999292222299929299222999999999992222922922999922222222222299999999999999999922222999999999299999999299922299929992229929299999999999292299992999992299999999999999999999299999992922999999999929999922229992229999999229992229999999999922229929229999229999999999999999999999999999222299999999992999992922999292999999922299292999999999992922992929999922999299999999999999999999999922229999999992222229222299922222299992929922299999999999222292292999992299929999999999999999999999992929999999922222229922229992229929999222992929999999999929292229299999229992999999999999999999922229222999999999292999222992999292992999922999222999999999992229992929999922999299999999999999999992229299299999999929299999999299922299999992229929299999999999292299222222222299929999999999999999992299999929999999992999999999229992229999999292992229999999999922222992222292229999999999999999999299999999999999999999292999999222999292222999922299292999999999992929999922922292992229999999999999929992999999999999999929222299999299922299299992299922222222222222222999992222222299292999999999999999299999999999999999992929999999929992229929999222992929292929292929299992222292222222222292229222999929922222222222229999292222999992999292999999929299222222222222222229992222922292222222222292229229999999999999999999999929299999999299922299999992229999222999929999999999222222222229222222222222222992999999999999999999992929999999929992222229999229999922299992299999999992222292222929229229229229222229999992222999999999292222999992999292992999922299999229999229999999999922922292292222222222222222222922922229999999999929299999999299922299299992929999922999922999999999992222222229999999999999999992299999229999999999992922229999929992229929999222999992299992299999999999222292222922999999999999992229999922999999999999292999999992999292992999922999999229999229999999999922922292292999999999999999922999992222299999999929299999999299922299299992229999922999922999999999992222222229299999999999999929299999299922999999992922229999929992229929999292999992299992299999999999222292222929999999999999999929922229992999999999292999999992999292992222222299999229999999999999999922922292292299999999999999229999999999229999999929222299999299929299229292299999922999999999999999992222222229299999999999999999999299999929999999992929999999929992929922229299999992299999999999999999222292222929999999999999992999999999992299999999292999999992999222999999929222222222999999999999999922922292292999999999999999922992992229299999999929222299999299922299999992229999922229999999999999992222222229229999999999999929299999292922999999992929999999929992929999999299222222222999222229999999222292222929999999999999999299929992222999999999292999999992999222222299929999999222222929992999999922922292292999999999999999229999999292229999999222299999999299922299999992999999922229992999299999992222222229229999999999999999992222292229999999999229999999229992929999999299999992222999299929999999222292222929999999999999992999999999292999999999992992222222999222999999929999299222999999992999999922922292992999999999999999999922999992299999999999299999292299922299999992222292922299999999299999992222222292222999999999999929999999999929999999999929999929929992929999999222992922222999299929999999222292222929299999999999999999929999992222222299922999999992999222999999929999929222299929992999999922922292922229999999999999299999999999929929929992299999999299922292222222999999222222292999299999992222222299999999999999999999929222222222222222922229999999929992929999229299999992222999222229999999222292229999999999999999999999999999999999999292922999999992999292999929229999999222299999999999999922922292999999999999999999999999999999999999999222299222229299929299222222999999922299999999999999992222222299992222922229222922999992999999299999929229929922229992929999929299999992222222222229999999222292229999299222992229922999999992999292999992222292222292999222999999229922222229999999999999999922922292999922222222222222299992222222222222999292299999999299922222299992229999922999999999999999992222222299999999999999999999999992999929299999922229992229929992999999999299222222299922222222222222222292229999999999999999999999999992999292999992922222299992999292999999929999999229999222222222222222922292222222222222299999999999999999999999999222299999999299929299999992999999922999992999999999992222222222222229999999999999999999999999999999929229999999929992929999999299999992229999222222222999222292229999992999999999999999999999999999999992222222299992999292222999929999999222299999999999999922922292222229299999999999999999999999999999999292299999999299929299999992999999922222999999999999992222222229292922222999999999999999999999992229922229999999929992929999999299999992222229999999999999222292229292992999999999999999999999999992222992922999999992999292999999929999929222222299999999999922922292999999299999999999999999992229999292229222299999999299929299999992992229222222222992999999992222222222222929999999999992999999922999999299999229999999929992922229999222299292299999999999999999222292222999292929999999999999999992222222222222222999999992999292999999929999299229922229999999999922922292922229229299999999999999999999999999922992299999999299922299999992999922922999999999999999992222222299999922299999999999999999922222222222999929999999929992229222222299999992299222222222222222222292222222292999999999999999922292922222992299992992229992999292929222929999999229992222292929292922922292929999299999999999999999999222229999929999222922222299929292229222999999922999922222222222222222222299299929929999999999999999929922999992999929999922229992929999929299999992299999229292929292222292222222292992999999999999999992222299999299992999992922999292999999929999999229999992222222222222922292999999299299999999999999999292229999929999299929222299922299999992999999922999999999999999922222222222222929299999999999999999922222999992999929992229229992229999999222299992299999999999999992222292229999292292999999999999999992922222999222992999292222999292999999922229299229922222222299929222922292222229299999999999999999999222229999929999299922222299922299999992992922222992999999999992922222222299999929999999999999999999929922222222222229992999229992229992922299292922299222222222222292222292222229292222922292999999999992222299992222222999292222999292999292929999992229929292929292929222922292292229229222922299999999999999999999299999299922999299929299229222992999922292929292929292922222222299992222222222229999999999922222229229999922299222229992929929299299299222222292929292929292222292229999992999999999999999922992222299992999999999999992999292922922222292929222229292929292929222922292999999299999999999999999929222222292299999999999999299929292929992229222222222929292929292922222222299992222222291111199999992992222222229999999999999929992929292222292929992222292929292929292222292229999992222229110119999999299999999992222222299999992999292929299229929222222222222922292929222922292922222222222911011929999929999999999299999999999999299929292992922992929922222222292222222222222222292929929992291101199999992999999999929999999999999929992929229222299229222299999999999999999222292229299999999229110119999999299999999992999999999999992999292992929929992929229999999999999999922922292999999299922911011999999929999999999299999999999999299922299299222999292222999999999999999992222222292999229992291101199999992222299999922999922299992229992229929929299999992299922222222222999222292229222222999229110119999999299999999992222229222222292999292222922229999999229992999999999999922922292929992299922901010999999929999999999222999222929222299922299999992999999922999292222222999992222222299999929992291000199999992999999999222292229222222929992229999999299922292299929929999999999222292229222222999229110119999999299999999222299992299222222999292999999929992929229992922222299999922922292992999299922911111999999929999999929999999999922922299922299999992299222922999299299999999992222222299929929992299999999999992999999992922299999992229229992229992922222229222299929222222299999222292229222222999229999999999999222229999292929999992292222999292999292929222922229992992999999999922222222999999299922999922299999929999999929292999999299992299922299929222229992922999292222222222222222222299999929992299999999999992222229992929229999929222229992229992929922999292299929999999999999999999999999999999229999999999999999999999292999999992929922999292999292222299922222222922222222222222222222222222222222222222299999999999999929292299999292222299922299929292929992922222222222222222222222222222222222222222222229999292222222292929299999929999229992229992922222999222299999999999999999999999999999999999999999999999999299999999292929999992922222999292999292922299929929929922299999999999999999999999999999999999999999922222999929222299999292922299922299929222229992222999992222229999999999999999999999999999999999999992999999992929299999929999229992229992929292999292299999222222222999999999999999999999999999999999999299999999292929999992292222999292999292222299922229922222222222222299999999999999999999999999999999922229999929292299999922292299922299929299229992922999992222222222222229999999999999999999999999999992999999992929999999992292229992229992922222999222299999222222222222222222999999999999999999999999999299999999292999999999222222999292999292929299929229922922222222222222222222299999999999999999999999929299999929292222222222222299922299929222229992222299992222222222222222222222229999999999999999999992929999992229222222222222229992229992929222999299299999222222222222222222222222222999999999999999999222999999229922222222222222999292999292222299922229922222222222222222222222222222222299999999999999999999999229992222222222222299922299929292929992922999222222222222222222222222222222222229999999999999999999229999222222222222229992222222922222999222299992222222222222222222222222222222222229999999999999999229999999999999999999999292999999922999999229999922222222222222222222222222222222222299999999999999929999999999999999999999922299999992999999922299992222222222222222222222222222222222229999999999999992222222222222222222222222229999999299999992222999222222222222222222222222222222222222999999999999999292292292292292292292292292999999929999999229999922222222222222222222222222222222222299999999999999922222222222222222222222222299999992222222222222222222222222222222222222222222222222229999999999999992999999292292999999999992229999999222222222222222222222222222222222222222222222222229999999999999992299999922222299999999999292999999999999999999999999999992229229229222222222222222299999999999999222929999992999999992222999922299999999999999999999999992999222222222222222222222222229922229999999292922999999299999999922999922222222222222222222222292222299922222222222222222222222222999992999929222222222229222222222229999992922922922922922922922229292999992922922922922222222222222299999299992222222222229222222222222999999222222222222222222222222922299999222222222222222222222222229999922999222222999222222229999222222299929999999999999999999922292929999922222222222222222222222222992222299922222299922222222229992222222992999999999999999999992229292999992922922922922222222222222929222229992222229992999999999929999999999299999999999999999999222929299999222222222222222222222222929222229999222222999222222222292299999999929999999999999999999922292229999922222222222222222222222292922222999922222299929222222929292222222222999999999999999999992229292999992229229229222222222222292992222299992222229992222222222222222222222299999992999992999999222929299999222222222222222222222229292222229999222222999292299999299999222222229999999299299299999922292222222222222222222222222222229292222222999922222299929292222229999999922222999999929999929999992229299999999999292999992222222222929222222299992222229992922999992999992999999999992222222222229992222929999999999992292922222222222929299999999999222222999299299999299999299922229222299999299992992922222222222222999929292922222222292929999999999222222299929999999929999929992992922229929929929222292292292292929999992929292222222292929999999999992222229992999999992999992999299922222999992999999929922222222292999999299999222222229292999999999999222222999222229999299999299929922222299999999999999999999999929229999929292222222229292999999999999922222299992929999929999229992929922229999999999999999999999992929999992929292222222929299999999999992222229992222999992999929992292222222999999999999999999999999292999999292992222222929299999999999999222222999299222222299922999292922222299999999922999999999999929299999929999922222292929999999999999922222299922299999929992999229222222229999999992229999999999992922999992929222222292929999999999999992222229992992229992992299929292922222222222222222999999999999292999999292992222229292999999999999999222229999299999999299299922922222222222222222222229999999999929299999929292222229292999999999999999922229999229999999929929992929229992222222222222222999999999992929999992999992222929299999999999999992229999222999999992992222292222299999222922292222299999999999292299999222222222929299999999999999999229999229299999999299299929292929992229222922292229999999999929299999922222222292929999999999999999929999222229999999922229992929222999299999929999292999999999992929999992222222292929999999999999999992999229292999999999992299999922999922222222999922299999999999292999999222222229992999999999999999999299929222299999999999299999992229992922292299992229999999999929222222222222222299999999999999999999929992229929999999999929999999292999222292929999292222222222222229999992222222229999999999999999999992999999929999999999992999999922299929922222999922222292292292292999999222222222299999999999999999999299922299299999999999999999992299992222929299992222222222222222222299922222222229922999999999999999229992922229999999999999999999222999292229229999999999999999999999999992222222222299999999999999999922999229292999229229229229229229299922222222999999999999999999999999999222222222229999999999999999992222222222222222222222222222222222292222222222222222222222222222222222222222222222299999999999999999992229222922292229222922292229222299999999999999999999999999999999999999992999922229999999999999999922292229222922292229222922222222299999999999999999999999999999999999999999299992222299999999999999999999999999999999999999999992222222992222222222222222222222222222222222222229999222229999999999999999922922292229222922292229222222222222222222222222222222222222222222222222222999922222299999999999999999222922292229222922292229229999999999999999999999999992999922222222222222299992222229229999999999999222222222222222222222222222999992922292229222922292229299992222222222999299999222222999999999999999222222222222222222222222222299999222922292229222922292222999222222229292929299922222229999999999999222222222222222222222222222229999922222222222222222222229999922229999929299929992222222999999999999999229222922292229222922299999999222999999922222222222222999999999999992922222222222222229999929999999922222222922292229222929999999929299999992229999922222299999929999999299999992922222222999222299999992292222222222222222222999929992222299999229222222222229999992999999929222222292222222229992222999999222222229222922292229299992999299929999922299999222222992222299999992222222229222222222999922299999922922222222222222222229999299922222999992292222222222299222922999999999999992922222222222222222999992222222292229222922292999922222999999999222999992222222222292999999999999999292299999999999299299999229222222222222222222299992999222229999922222222222222222229222999999999999999229999999999999929999922222222922292229222929999299929992999992299999922222222222999999999999999999922999999999222992999992292222222222222222222999929992222299999229222292222222222299999992299999999992292222229929299299999222222229222922292229299999999292999999922922229222222222229999999299999299999299292992992229929999922922222222222222222229999999922299999992292222922222222222999999922299929999929922299299292992999992222222292229222922292222229999929999999222222222222222222299999999999992999992999929922922299299999229222222222222222222222222999992222292222222222222222222222222222222222222222299992992999922229299922222222922292229222929292299999292229999999999999999999999999999999999999999999999999299999992999992292229222922292229222222229999922292999999999999999999999999999999999999999999999999929999999299999929999999999999999999299992999999999999999999999999999992299999292999999999999999992999999999999999999999999999999999999929999999999999999999999999999999999999999999299999299999999999999999999999999";
			//Map = "99999999999999999999999222222299999999999999999999999999999999999999999999999999999999999999999999999999999999999999929999922222229999999999999999999999999999999999299999999999929999929999999999999999992299999999999922222222222222222222299999999999999999222992992222922222222999992222229999999999299999229999999999999222222222222222222999999999999999999992299999922299299999222229992222999999999999999922292299999999922222222222222229292222222222222222299229929992292922229992299999922292999999992999992222922222999992222222222222229929999922222222299999929992299229299299992229999992229229999999299999222922999229229922222222222222992299999922222999222222999229922992922922222999999222922999999929999922992999992929992222222222222992299999999222992222222999222992229299299999229999222292229999992999992292299999299999222222222222299299999999922299222229999922999222292922222222222222229299999999299999299299999922299922222222222229929999999999299222299999922299922229292229929992222229929222222929999929929999922222992222222222229992299999999929222999999222299922229299229922929992222992992222222999999992999992222992222222222222992299999999992999999992222299992229929222992922999922299299999999999999999222999222292292222222222299299999999999292222222222999999222929922229992992992229229922222222999999999299222299999222222222229929999999999222222222299999999922292922222222222299922922922222992299999992222222229992922222222222992229999999922222299929229999992292992299999999229992292299922999929999999299222222999292222222222299992999999922222999292999999992299292229222222999999229222292299992999999929999222229922222222222229992299999992222999999222999999229299229922222222222222922299299999999992922999992222999222222222222999299999992222299222222222299222929222992222229999992292229229999999999299299999222229992222222222299929999999222299999999922222222929922999999999922992229229929999999999922922999992229999222222222229992999999222222992222299922222992922299222222222299222922922999999999992299299999229999992222222222299229999922222299999929999222292992222222222222929922992292999999999999222922999222999999222222222229922222922222222999292999922229292229999229999992292292299299999999999922229222222299999992222222222992299992222222222999299999229299229999229999999992229229229999999999992229229992229999999222222222299299999922222222222229999929929222929229999999922222922229999999999999229929999922929999922222222229929999992222222229999999992929922992929999222222222992929999999999999922922999992292999992222222222992992299922222299999999992292922292292292222222222292292999999999999992992999999929299999222222222299229929992222299999999999292992299299922222299222229229229999999929999299292999992922299222222222222992292299922229999999999229292229229992222999999229922992999999992999999299299999299922222222222222222229929992229999992222229992229929992222999999222922229299999999299999929222999922999999222222222229922292299922999992222999999922922999222999992222992222922999999229999999929299922299999999999999999999229929992299999229999999992292999222999922222992222299299999922999999922929992222999992222222222999922292299229999229999922222229299922299922229992222222929999922299999992992292222222992222222222299999229929922999922999222222222222992299922299922222222299999222229999999229222222222222222222222229999922292292299922999222222222299299229922999222222222222922222222999929992922222222222222222222222999929229929229999299922222222229922222922992222229222222222222222299992999992222999922222222222222999222922292222992229992222222222922922292992222999929999999999999229999299999229999999922222222222299929999229992299992299222222222292999229292229999992222222222299992999922999929999999999222222222229992229992229222999929922222222229299922299229999999992222229999999299992299992999929999922222222222992292299929992299992992222222222229992229222999999922229292299999999999222992299992299999222222222299922922992999229999229222222222299992222922999999922299929929999999999922222299999222229922222222229999229229299922299999922222222229222222292299999922299292222999999999992992222299222992292222222222999992292229992229999992222222222929222229222999222299929929999999999999999922222222999929922222222299992229222999222299999222222222292229222992222222229292222999999999999999992992222299992992222222229992292229229222222299222222222222299992929922222229929929999999999999999922299992229929299222222222292292929992292222222229999999999999999229299922222992222999999999922999292999999922299229922222222222292922999929999922222229222222229999922292222922299999999999999992999999222999992229929922222222222292922992292999999222299999922222222922222222292299999999999999999299999999299999922992292222222222299922999922299999222999999299999999222222222229229999999999999999229999999922299992299999222222222299999999992999999922999999922222222229222292222922999999999999999922999929222222999229999922222222222999999999229999922299999992999999999992299922292229999999999999922299999999992299992999992222222222222222222222229222229999999299999999992229992229292222222999299222229999999999929922299992222222222222222222222222222222299999999999999999229999929929292299999922222229999922999992992999922222222222229999922299922222222222222222999299999222999922992922999992222222299999999229992299992222222229992222222922299999922222222222222922299929922999992292292222222292999999999999992922222922222292299992222229999229999999299222222222222222229922299992229229299999922292229992999999299992222229999299992222299999922999999999999922222229992222222229999229922929999999929292999999999922999922229999999999922299999922229999992999999922222999999222222999992922292999999222922299999999999999922292299992999992229999992222222922222299999222299999999222229999992229222222229292229999999999229922299922229229999929999992222222229222222299922222222229229922299992222999999992229292299999999929992299922992222299992999999222229999999922222999222292229929999222922222299999999999929229999999992992229922999992229999299999922229999999999922299922299929992999992222222222222222222222222299999992299229922299999922299929999999222999999999999222992229992999299999922292222222222922222229222929992299922992299999999229992299999922299999999999922299929999299922999922229999292229222922292229292999229992299229999999999999222299922229999999999999229992999929999999922222292222292222222922222222999922992229922999999999299222222222222299999999999922999299992299222222222229222222222222222222222299992299229992229999999922222222222222222222229999922299929999222222222229222992222229999999222222229999229922999922999999922222222222222222222222222222222922299922292222229992229222222929999999922222999922992299992229999922222222222222999999999222222222222229999929999992999222922222292999999999922299992299229999922229922222222222222222229999999999922222222292222922222299992292222299299929999999229999229922992999222222222222222299922222992999999999929992222222222999999999229922229222999999999922999922992299299999222222222222229999999229222999999222229992222299999999999922292929922299999999999299992299229922999999222222222222999999999992229999999299999992229999999999922229292922229999999999929999229922292299999992222222222299999999999922299922222229999922292299999992222992992222299999999992999222299229922999999222222222222292222222222222999992999999999229929999992222229992222229999999999999922229922992299999922222222222299999999999992229922222992299922292999922222222222222222299999999999999222292229922999992222222222222222222222229922999929999992999229222222222222299999992222999999999999922229922992929992222222222222999999299992992229922299222299922929999222222222299999292222999999999992922292229922222222222222222299992222299299922999299999299992292999299222222929999922222222999999999999229922999922229222222222229999292999929992299929922229999922999999992222292999922222222222992999999999992229999999922222222222999999222222999222999999929999992292222229222229229999999999999999999999992222222299999992222222222299922999999999222299992222999999229999999992222992222222222222222229999992292999222299992222222222222999222222222222222999992222299922292222229292229299999999999999999992999299222992222292222999999222222992222222222222222922299999999229999999929922929999999999999992999999929222999922292229999999999922222922299999222222222922222222222922222222992292992999299929999299999999922299922299999999999999999922229299999992229999999992222222222222222999229992222229999999929999929922299922299999999999999999999222999999999922999999999999222229999999999922222222222229999992999992222229992222999999922222299999992299999999922222222229999999222922999999922292229999222299999299999222229992229222222222222222299999222299992992222222222222999992229222999222229299999999222999929999922999999229992299999999999999999229922222299229999992222222999222922222222222929999999992299992999992999999222999222299999999999992229999999999229999999999222229992299999992222992999999999922992299999222922922999992222229999999222299999222222222999992999999922999229299229922292292999922292292229999929992992299992222222222222229999999222299992299999299999992229922999992299229299999999929922222999999992292229992222222222222222222222222229992229999929999999922992299999922922922999222292922222299999999999229999222222222222222229999922229999222299992299999992299229999999292292222999922922222229999999999222999922229922222229999999999999999222222229229999999929922999999929229222222299999992922999999999222299222229999999999999999999999999222999992992299999999992229999922922922299929222222292299999999222299929922999222222299999999999992222999999999222999999999222222222292299299999222222999929999929222229992292999222222222222999992222222999299999992222999999922229992299222229999922229999992999992992222992922299922222222222222222222229999929999999922222299922292222299922929999999229999999299999229222299299929922222222222222222222222299992299999922222222222229999999292292999999922999999929999922992229929222922222222222222999999992222999922299222222222222222922222999229229999922299999992999992229222999299299922222222222299999229992229999222222222229999922222999229922992999992229929992299999222992299929229922222222222222992292999922299992222229992929222229222222222229229992222999999999999922229222992222299222222222222299292299999229999922299999292222229999999999222992292222229922292999999922992299299229922222222222229922299999922299922299999222222222999999999992229292222222222222299999999999229999922992222222222222992922999999222222299999929299922999999999999222999222222222222222999999222222299992299222222222222299299999999922222299999922222222299999999999992299229922222222222299999922992229999929992222222222229922929999922299999992222999992229999999999999222929999222222222299999992999922299999999222222222229999222299922222992222222292222229999999999999992292999922222299929999929229999222999999922222222222999929922222222299999922229999922999999999999999229299992229999999999992992999992222992222222222222229992999999992229999999992222222299999999999999922229999299999999299999229299299922222222222999992222222299299229922299999999992222299999999999999999229999999999999929999922229922992222229222222229999222222999992992229999999999992229999999999999999922999999999999992999992299992992222999992222299999929922299999299222299999999999922999999999999999992299999999999999299999299999292229999999922299999922999222999929922222299992222922299999999999999999229999999999999929999929999999229992999992229999992999999222292992222222222299999229999999999999999922999999999999992999999992229222999299999222999999292229929999292222222229999999992299992999992999922222222229999999299999999992222999922999222229999229929999999929222922229999992222229992229992229992292229222229999929999922229992299992222222222222222222999999999922999999999222222222999222999222999229992929222999992999999922992229999992999222222222999222999999922299222229222299992229922299922299222222292229299999299999999999229922922229999222229999992222999992299929992222999999922992229992229922999999922229999999999999229922992992992999999999999999992222992229922929922999999992229929999929922299999992222222229999999992992299999299299992222299999999999222229992992922299999999922999999999992299999999222299999999999992222229992229929922222222299999999992229992292992299999999992929999999992229922229222999999992999999299922999929992922222222222229999922229999299292229999999999992999999999229922222222999999999299999922992229222992992222222222222222922229999929299222999999999992229999999222999999222929999999929999299999922992999292222222222222222922222229999929222299999999999222299299222999999999999999999922999929992992222299929222222222222229999222222292929922222999999999222222222222299999992229999999992299992299229222999929992222222222229999992299222292922292299999999922222222222299999992222299992992222299222222992299999922222222222222999999299992929992299222999999922222992229229999992229222299922222299922992229222999992222222222222299999929299292992229992222999222222292929992999999229992222222222299992999922292222999222222222222299999922992929222222999299229222222229292999222229222992222222222229999299999922922222922222222222229999222299292929222229999999222922222229299929999922992929922222222999999999299229222222222222222222992222299929299222222992992222999999929929992999922292292999992222299999999929992992922222222222222299999999992929992222299222222292222222922299299992299292299999992229999999222999229299222222222222222999999999292999222229999222922299999992229929992229229292999999922999929929229992999222222292999222222999992299229922222992992299999999999222999999229929229999999992299992999992999999222299999222999222222222299992999222229929229222999999929299999222922929999999999929999229992229999222922929922999999222229999999299922222992992929222222992929999922992922999999999992999922229992229222999299222229999999222299999229992222299929222999999992292299992292292999999999999299992222229992222999922222222229999992222222229992222229992922222222999229222229299292299999999999999999229992999922999922222299222229992229999922999222222999292229999999922922999929229299999999999999999929999999922299922229999992222222229999999299922222299929292922222922999229992929229999999999999999992299929992299992299999992229222229999299999922222222992929222999992299922999292929999999999929929999229292299229992299999992229999222999222999992229922299292929999999229992299929222999999992922222299929929929922999929992999299999992229992222992229999229929292999999929999929292922999999292222222229992922922292299222299229922299992222229299222222999922992922299999992999992929292299999222222222222999992992999222999922992999922999992222229999922999922299292299999999999999992999229222222222222222999999292299922299222999292222229999999229999922299922922929229999999999999999222222999222222222222299999999999922222999299922222222229999999999922229992292929222999999999999222222222922999222222222229999999999922222299999922222222222222999999222229992299922922229999222222222222222292222999922222222999999999922299222999992222222222222222222222222999229999922222222229999992222222222299999999999222229992999992299992229992222222222229992222222222299922299222222222222222222222222222299999999999999922999299992229999922299222222222222999999222222229992222222222222222222222222222222229999999999999999999922999229922999222222222222222229999299922222999922222999992222222222222222222229999999999999999992992222922229299992222222222222222999999999922229992229999999992222222222222222222999999999999999999299222222299229992222222222222222299999999999222999229999999999922222222222222222299999999999999999929929222229999992222222222222222229999999999992299222999999999992222222222222222299929999999999999999999992229999922292229222222222229999999999999229922999999999999922222222222222229992299999999999999999999922999222299929992222222222229222999999922992299999999999992222222222222222999229999999999999999999922999222229992999222222222229922222299929299229999999999999222222222222222299222299999999999999999922992222292292229222222222222999922222222299922999999999999922222222222222229922229999999992299999992992222299929292922222222222999999992222299922299999999999992222222222222222992222299999999929999999992229229992929292222222222299999999999999992229999999999999222222222222222292222222222299992999999992229992999299999222222222229999999999999999222999999999999922222222222222229222222229999999299999999222999229922999222222222222999999999999999222299999999999992222222222222222222222222222222229999999222299999999222222222222222229999999999999922222999999999992222222999999999999999999999999992999999922229992229999992222222222222999999999999922222299999999999222299999999229999992299999922999299999292222999222999999922222222222229999999999922222229999999999922299999999929999999299999992999929999929922299922222229222222222222222299999999222299222299999999922229999992222999922229999222299922999992292222922299999992222222222222222299922222299929229999999922222999999999999999999999999999999999999229922222922222222222222222222222222222222299999922229999922222999222222222222222222222222222222999922292222299222222222222222222222222999222229999992922229222222299999992222999922229999222299922299992229922299922222222222222222222229992999229999999222999992222229999999999299999992999999929999992999222992229992222222222222222222229999999222999999992222222222222222229299999992999999929999999999999922999222992222222222222222222229999999929292999222229999999222222222229999222299992222999999299999992299922299222299922222222222222999999992929222222299999999992222992222222222222222222222222229992999299922229992299999222222222222999999999292222222299222222999222999922222222222222222222222222222999999992222999229999922222222222299999999929222222229929992229922299999999999999999999999999999999929999992222229922992992222222222229922222222222222222992229222992229999222222222222222222222222229929999992229222999299299922222222222999222222222222222299992992299922299222292992999999292229222229999999999229992299999929992222222222299922222222999999229999999229992222222229299299999222299999222999999999922999222999922999222222222222992222229999999992299999922299922222229929922229992299222992299999999992299992222222999922222222222299922229999222999222222999229999922222922999299922229999299229999999999222999922299222992222222222229992222999929229999222999222299999992292999922299929992929992992999999922299999999992992222222222222299222999922992999929999992922222292229299999922222999992999229999999999222992222292229222222222222229922299992929222922922229292922999992922222292999999999299992922999999222299999999922222222222222222992229992299929999299999929222999999292999999222229999929992299999999922222922222222222222222222222299229999299992922222229222999999999929299992229992999292999299299999992292222229992222222222222222229922999229999299222229999292229999992922999299922229999299229999999999299929992299922222222222222222992299929999229992229999229292299999299299222299922992229922999999999929992299999999222222222222222299229992999922999222999922922299999922922929999922229999922299999999992299999999999922222222222222229922299222922229992999922229999999992292992999999292229222229999999999229999999999999222222222222222999229999222222999299992229999999999229299222222222229992222999999999922299999999999922222222222222299922999922222299929999229222999999222229992929999999999922229929999992222299999999922222222222222222999229222222222999999222929922222999999999292999999999999999999999999922222229999922222222222222222299999992222222299999922292229999992222999929222222222222222222299992999229999999222222222222222222222999999222222222999922229999999999922999992929999999999999999999999999999999999999222222222222222222999999999229999999999999999999999999999999222299999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";
		    Map = "99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999929999999999999999999999999999999999999999999999999999999992299999999992222992222229922229999999299222992999999922299999222222222222222292222222299999929999999299999929999922992222222292222299222929222229999999922299222222229922222229299999992229999999999999929999922299992299999999999999999929992999999929999999929229999922999999922299999992222999999299999999999922222999929992229229922222992929229222292999992222929999222229999999229922992222222222229999999999992929299992992299922992999999992929999999292222229292999992222222299922992299222229299992999999999299292929299229299992299222222222222922229222299922229229999222222222992229992292222929999299999999929929292999922229299229999999922222299999992999999992922229992222222299222292229922292222222299999992299929992292229929922292222999922222922229299992922292299999222222299922222229292229299999999999999922992992292222922992299999999999222999999929999999229222299922299999992229999999222929999299999999929229992292222992992222222929999922292222922299222222999999992299999992229999999222292229929999999292292292292292299299922999999999999299999992229992999992299999229922222222992292222229999992999999929922222292299929999292222999292222999999999922299999999229999922999222222299229222222222222299999992299229299999992922229999999929999992999999922222299992992999992299999992229999999222299999929999999222992292299999299999922999922222222229999992222222229292229992222999999929299999992222222222929999922992292922999929999992222222222299929999992222992299922222292222222229992222222292222999999992999992992292229229992299992229999992299999929922222992222292992229222222222299222999999922292222222299999292292292292292222222222222222222299922222222999999929222222922229222229922299999992229299999999999999292299922922222229992299999922999999299999999229999222222999222992222292292222299222922222222999999992292999992992299999999999999229992999999999999999929922999992299222999929292229222299999999299999992292999299229999929222292999922292222222299222222292292999999229999999922922222222922222222929999999929999999229999992992299222922299929922222999229999929222299922999999222299999992299999999299999999992999222222999929222222229992922999999922229229999992992999992299222222222992299929222222229999999999999222922299999222299222229299999999999222222999999222229999229922222922222299992229999992999999929992222999229992929929929999229999299999922222299999229222999922999999992292222999222999999222999992292292292222999292222992222222999229999999229929992229992229922299999999229299229222222222229299999222299929992299929992922929922999929999999929929222299999992222229922992222992222222229999992929999922229992229229922292292292292299992999999992999229999999922222222992299222229999222222922222222999992922999999922999229292992929229929229999999299929922222922222222222992299222229999992299999999999999999229229222299922229999992922999292299999929922922222292222992222299229922222999999929222222222999999922222292222999999992299222992929922999992992999222299922222929222222992229999229992999999299999999999999922222299999999229929999992222229999299292222999299299292222222999229922222299222222929299999999222222222222999929299292299992922292299929922222222222929229222999929922922222229929999299929999999922922222222222922299992229929229222922922992922222222292922999999999922292922222992922222222299999992999999992222222222222222999922229229222299292222222229292299229922222229222222292292992999999999999299929299922922222222222229922299292292229929222922222929229922992222222922222222229299299299999999929992929292999222222222222222222222222222992922292222292922299999999922292229922222922229929999999992999292929292929999999922922292229299999299992229222229292222222299999222222299229299999992999999999299922922929292929229222999299929992292229999222922222929222222222299922299999922922222222299999999922922292992929992999999999929992999999992299222222222292929222992222922299999992299999999999999999992299292299292992229222229992929999922222222222222222229292222999922222229992299229222222222222999999222929299929222292222292992292929992292229992222222222929222299992222222999999922222222222992999999922292929992929929222222229229992222292229999922229992992922299229922222999922999222222229999999999992229299999292992229229292922292229229992222992222229992222299222299999999922229922222292229999999999229922999929299929992922299222229992999229992222222292222229922229922229992222992292999992999999999922922299922929292929299299922922999299922222222222229222229999229999229999922999229222922229999999992999222922299929292922929992929292229292922299999922922299922999922999922999999929992222922299999999299922222929992929999992999299929992999222222222299292299922229922229922229999922292229999929999999922999222222999292222999229229992999299922922999999929229992222992222992222999992929292229222999999992299999929299929222229922222292229222922999222292922922999922999922999922999999292299992229999999999222999999929992922922999222222222222222299929999999299299992292299992299992299922222222292229999999922222299299992222222299922922222222222929992999929929929922222222992222992222999222222299992999992922229999922222222222222922299999999999292999999992999922922222222299222299222299922922222222299999222222222222222222222222222222922222299999292299992292292292299992299992299992229992292922222999999922222292222999999992229999922299222222992222222922222229229299999999229999229222999229222999992999922922299922229999999929999929299999222999992222292222222922922999992222222222222299922922222922299999299999999999992999992999999999999992222992292299999992292222222222222222229999229922299299999929999922922999222299999992999992292229999229299929999999999929222222222222922222922229992222222292222299992292229222299992222292299922299999992222222999929999999999222292222292222992999992222999292922292999299922222999922299929922299222222299222222929922999999292222229292299922292222222222299999999229999929299222999922292222299922929999299922999999992999999229222229922229292229222222222229992999992999999922922299992922292222999292222222292999999992292222222922222929922929229222922222222292222292299999992299299992292999999999922929999999299922222229292929292222992922292222929292922292299929999929999999922929999292292222222299299999999229999999922229222929222292299229222292229222299922922222922999999992299999929299922222299922222999992929292929222929999999229222922922292222922229992999299999299999992922999992929992999999922922222999292229292929222929292922929299292929229292922999299999999929999999292299999222299222229999299999999929222922292922292929299292922929222922922292299929992999992999999922922999929299229299929929992229992929292229222922222922922229299922992222222299992292222222299999992292999992999992222222292992292299292929292922292222292299222922922292222292229999299929999939999999292299999299999299999299299292922929292929292929292929222922229292299229229229999929992988888999999929299999929999922222999929229292999929992929292929292992299222922229292929922999992999298989999999999229299992299922222999922929292299292929299999992999999222922292229229992922999999299929899999999999929929999929992999999992992929299929992922292229292929992299229292229999292299999929992999999999999922922299992299999999992299229229992999299929299922222929222922229222999929299999992999298988899999992992222292922299999922299992229992292922222922222929292992299229922999992929999992299929999999999999992222999222222222222999999999999229299929299929299999229222922922299999229999922229992988888999999992222222222229999999929999299999222222292922222929992922992299292299999922922222222999299889999999299922992299922222222222999922992229292929292929292229292229222929229999992299922222299929998899999929992222222292922222922229922222299999992999992999292929922992299929999999229999922229992988888999992292229922999992222222922299229922292929292929992929292922229222922999999922299999922292299999999999222222222229922292229222922929999922292922222929292999292922922292999999992222222222222222222222299922999999929922222292229292299999999229292222292922222229292292222222229999922222222222222222222299992999999999992222999992229222999999922929299929292999992229229222299999999999999999999999999999999999299929299992222999999992229299999999292929292929292929292922922222999992222222222222222222222929999922292929999922299922299992222299999922292929992929292929292292229229992222222222222222222222292999992999292222992299922929999929299999992929292292292922292929229222922999999999999999999999999999299999999929299999229992999992992222999999292929299929292229292922922292222922299922222292222222222929999999292222999922992299299929929299999929292929222929292999292292229229992299222222929222292222292999999992222999292999299929992292222229992999992999992229222229229222922292229222292292229222228222299999999299299922299229922992929929299999222292229929222929292922922299999922929229222222222222222229999999922222999999929992229992292222999929229229992929299929292292229999922292222222999999999999222999999992929992999992999222992929922299992929992929299929922999229222992222229222222229222222229992299999999222292222999299992229992299999992292929999929292922292922922292299222992222299992299992999229999999999299929999922999222222929922999999292922922929992929222292229299992229999999229222922229222929999999229992922999222992229992292222222222222222299929299922229222929999222222222229929222222222299999999999999992299999222229992229922222222222222222922999992222922292299222222222222922999999222229999929999999992222999999999999222292999999999222222222229229992299299299999999999922992292222999992999992222999922222222999999999222229292922292292229992222292922222999222992222222222292222229992222999999222229922228822222222222222222229292922922922229992222222222222222222222222222299222222222229999999922222992222882222222222222222222929222292992222922222222222222222222222222222229222992222222229999992222999922222222999999299999992292922292292292222299922999999999992299999999992922922292222229999999299999999922229999222929222999929222999299299222999999292222222929229222229999292922999992222929999999222222999229999222999992229992229299929229222999992929222229292922922922999999292999999992292999999992222992922992999992229999999229929999929992299929299922222222292292299999999922299222999929299999999999999299992222222222222222222222999922999229992929992999999999229222229999992299999229992929999999992999222299299999992229222999992299999229222999299929292222222922922999999999229922992929992999999999999992999922992292299999229999929992992992299999999929292229292292299999999222999299292992299999999222299999922299999999999999999992999229229222922222292922222229229222229999922299929929229229999999222999999222229922999999999999999299992992922999299929299999999922922999999992299292992929922999999922222229222922299222922222922999229999229222222222292222222222292292999299992229229292222922299999992999999992999299922222299992999992299292992229999999292222222229229299999999222922922222992229999999222222292229222992229222299222299299229229299992999929929992222922929999999222292292922292222999999922299999922222992299999999222299929992292999999999992922229922292292299992222299922299299222299999999222299999922299999999992222222992999229229999999999292999992229229222222222999899999929229229999999999999929999229999999999929999992299992222999999999929299999922922922999922992999299922929992999999999999222299929999999999222922222299999222229999999999922222999292292999999299229222222292999299999999999999299992299999999992999999999999922992229999999292999992229999229999999992922299999299929999999922229229999999999922299292222222999922292222222222222222222222222222229992999292299222222922999999922299999922999222222299222222222222222299229999929929999999929299999922299292229229999299999299999992222222222222222222222222222222222222229222229292922222222222222222299229229299922992222229229999929229999999922222999929222999922999299929922299929999229999992929999929922922922222229999929992999999922222222229222222922222222999292922222922222222222222222222222299999922999222222222229992999299999992222222299222229222222222999929299999999922992222222222922229222299222299222222222222229299929999999222999922222292229229222229992999222222922999922999929299999922999999229929999999999999922922999999992222999222299299999999922999292922222992299999999999922222292992222992922222222222222222222292999999999999922229922299999292999929992229292229999229999292999929299922999292929222222222222222929999999922229999229999929999992292922929292929222999922999929299999929992299929222999299992999992292999999922299999999999222299929299222299929292222299222229992922999992929229292999292222292292929229222999992299222222222222222229229222229992999299922929922999292999999292922929229929999999929292922992299999299222299999999222229929922922292292922222299222229929299999929222222922922222299992922292299229999929222922222299999922222992999222222222922222929922992922229992929229292299292999999292229229929999299922992999999999292992999299922999999222922299222229299299999222922922222929222222229222929999999929922299292999929929292299929992222222222222222929922929222299929292292922292999299992992992999999992292299929299922292929299992999229999922292922299222222229999992929229292229292222222222222229929999222229922922999299292999999299929992999299922222929922222299992299999999222922222222222222222222299922999992292299999929992999229999999299999992922992222229222222222922229222292999999299999999999999992999992229222999992292222222992299929992999222292992292222222222222222222229292222929292222292229999299922222922222992222229992229929922299292222299292229929299999992222222229929299992929922999922999999222222999222222222222229229992299999229292229299229922222299999922299222922929222292292222292229999999999999999999992999999992999999929992922229929222992929999222992299992299292922229929929229929999992222229992222222222222292299929992992292922929922992222222222999929999299929292922292292922292999999992222292222299992222999222992992229929222992922299292222222222992999999999929222299299292229999999999992229222999999999222222292229929992999299992299222229292922999299992999292992229229229292299999999929992922999299999999992229929992999299929299229929222922292222929999299929229229929922922299999929992292292299929999999999999992999292229992999929922229222222229992292929922922992922922292229299992229229929299922299229999992999999929992999299992999922929222922999299292292292229992292229229929999222222292929929292222229992229222922292229222992292222292929292999929929299229222992229222222922999922292299292992922222222222222222222222222222222222292229299999999992292229992922229922929222992299992999229929299922222222222229992929222222222222299222222922229929999222229999292229992292222292229999999929992929292299999999922222222999999999999999992292292999922299922999999929222929929922299922999999999929292999299999299999229222299222222222229999222229222999299999992222992922992292292229292299299992222929229229992222299929992222922222222222999222292999999999299299299999292292229229929929929999999929999929992999292999992929299992999299922992222222222222222299929922999929299992222292922292999999299929992929299922229999292922292292229292292222299999999222222292992222992999222222229992229999999922922292299929992999999929299929929999929299292299999999999222299299299222222222222222222222299999992292229222922999299999992922222292229222229222229999999999992229222929922222222292222282222229999999229222922999299222229999292229999292999229929229999999999999222922292222999992222222922292222999999929992999292929992999999929222222222229222922222999999999999992299299229992292992229999299992299999992929999999992999222999992929992222229922992922999999999999999229999929929929999929999222999929999999299929992999299929999999292929292222922292222229999999999992922922999999222229922999222229292999999922922292229229992229299229992999922992299292299999999999999299299299929929229999999999299999999999992292229222922292292222222222229992292229229222229999999999922222229299992222299922992222299929999999299222922292229222222222229929999929229992922999999999999992992992992992992999992999922299992999999929992999299929992222222222992992292222929222222999999999999292292299229992292992229999999992299999992999299929999999299999222999999999222992992922999999999999999229999922229999922282229999922229999999292929992999299922299992299999999922292229222229999999999999922992992882222222288822222922222999999999292999299929922929299222929299992299222292922999999999999922292229288222222228882222292229299999992929299929922299292229222992922992229222229222229999999999992229222922229999922282229999922229999999299929999999299999992992299292222229992229992922999999999992222992992299922929922299999999922999999929992292229999992992229299922222292929222999222229999999922222299299299299299999299992229999299999292992299929992992229229929992992229292992992992822222222222222299929299992222299922992222299929999922229229992992229929922922999222229929299292229222229999999922999992999299292299999999992999999999998222922999299929992922299299929222992922999222992222222929292992929299999922222992299922222929299999922999299999992229299222929992222299292299222222222999292229299292999929929929999929999222999929999989299922999992229922922992999292229929292922222229992929222922999299922299922929922299992999922999992829992222222229992992292299929222992929222222229929292992992222999222222299999222222292229222299999922999999992222999999229929992222999292922222222992929222222222222222222222222222292222282222929999928299922299922229992922292299292299929222999222999292929222292222222929292222222222222222222222999998922922929992222229299299229929229992922999992299999292922999992222222222222299222222222229222299999922292222299999999922999222922929999292299999299999999292992222222229299999222999999999222999229999982299922222222222222222222992222999929299229922299922999292299229292929292222992222222999229992999992229292999999999999992222299222999992929222292922229292929299992929999929292292222222992222999299999229929992292999929999992229992299999292922229222222222292929999999999922992299922222229922229929999922922292299299992992999922999299999929999999999222222229299999929999222229999299999999999999992999992999299929222999992299992299999999992229299292222229292922999922222222222222222222222299999229299999299929222992299299229999922999999992222222222222999229992222222222292222222222222222229999229929999999999999299222929222229992222222222222222222222999999922222299922222999999999299999929999992992999999992222929922299922299929229999999222999992929999229992922292229222292222222999292999299222299299999299929222992222922222222929999999992999999929999992999222292292292229929922229229299929222299929999922222922299222292222222292999992999299929992929999299929229299929929222229222922922992922922992999992999292229922299922299929299999999929299929992999929292229229992292929999992292292922292299999299999999222292992229292222299999992222299922222999299922929292929922929299929999929229222929229299922999999999999999229929922999992999999999999929992929999222929292299922929299999922922929222922999999999992999292229922999992299992229929992929299929292229929992922929992992922222922292292299292292222299999299929992992999929929999222999999992999999922299992299299292292292229929922229229299929222299999229922999992299299992999999222229999999299999992299999992929222922292222922222229992929992992222299999992222999999999999999992222822229999222999992222999999922922229992222299999999929999992999999999929999999299999999999999999999999222299999999999999999999999299999999999999999999999999999999999999929999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999992999";
		    RallyOfficiel = true;
		    RallyValide = true;
		    CouleursPersoActivees = false;
		}*/

		for (int X = 0; X < 200; X++) {
			for (int Y = 0; Y < 100; Y++) {
				ListeCase[X][Y] = Map.charAt(100 * X + Y) - 48;
			}
		}
		
		if(this.CouleursPersoActivees) {
			serveur.Envoie(ListeJoueur, "CxLo#"
		                                 + "#" + Long.toString(CouleursPersonnalisees[0], 16)
		                                 + "#" + Long.toString(CouleursPersonnalisees[1], 16)
		                                 + "#" + Long.toString(CouleursPersonnalisees[2], 16)
		                                 + "#" + Long.toString(CouleursPersonnalisees[3], 16)
		                                 + "#" + Long.toString(CouleursPersonnalisees[4], 16)
		                                 + "#" + Long.toString(CouleursPersonnalisees[5], 16)
		                                 + "#" + Map
		                                 + "#" + (PeutSauverMap() ? "1" : "0"));
		} else {
			serveur.Envoie(ListeJoueur, "CxLo#"
                    + "#" + COULEUR_CARRE_DESTRUCTIBLE
                    + "#" + COULEUR_CARRE_INDESTRUCTIBLE
                    + "#" + COULEUR_CARRE_OBJECTIF
                    + "#" + COULEUR_CARRE_NONRECONSTRUCTIBLE
                    + "#" + COULEUR_CARRE_CONSTRUCTIBLE
                    + "#" + COULEUR_CARRE_INCONSTRUCTIBLE
                    + "#" + Map
                    + "#" + (PeutSauverMap() ? "1" : "0"));
		}
		
		for(int i = ListeJoueur.size() - 1; i >= 0; --i) { // Forcer le respawn après chargement d'une map (pour les laggeurs)
			RecommencerMap(ListeJoueur.get(i));
		}
	}
	
	/**
	 * Faire recommencer sa partie à J.
	 * Si le joueur n'est pas sur cette partie, rien ne se passera.
	 */
	public void RecommencerMap(Joueur J) {
		if (J.PartieEnCours != this.partieMere)
			return;
		
		J._Forteresse.RallyValide = true;
		J.RallyRéussi = false;
		J._Forteresse.DerniereMort = System.currentTimeMillis();
		J.TempsZeroRally = System.currentTimeMillis();				
		serveur.Envoie(ListeJoueur, "15" + $ + J._Forteresse.CodeJoueur);
	}

	
	public void GestionComptesTournoi() {
		for(int i = ListeJoueur.size() - 1; i >= 0; --i) {
			Joueur J = ListeJoueur.get(i);
			
			if((J.AutorisationFilmeur || J.AutorisationTournoiForto || J.AutorisationArbitreElo || J.AutorisationFilmeurElo || J.AutorisationTounoiArbitreSecondaire) && J.MortAuto) {
				J.Vivant = false;
				Explosion_Total(J);
				serveur.Envoie(ListeJoueur, "3" + $ + J._Forteresse.CodeJoueur + $ + J._Forteresse.PosX + $ + J._Forteresse.PosY + $ + J._Forteresse.CodeJoueur + $ + "0");
				J._Forteresse.DerniereMort = 999 * 60;
			}
		}
	}
	
	public long getTempsZeroRally(Joueur JOUEUR) {
		return (System.currentTimeMillis() - Math.max(JOUEUR.TempsZeroRally,DebutPartie)) / 1000;
	}

	
	public void GestionDeconnexionPresident(int CodeJoueur) {
		if (PresidentRouge == CodeJoueur) {
			serveur.Envoie(ListeJoueur, "CxINFO#Victoire des bleus !");
			PresidentRouge = -1;
			PresidentBleu = -1;
			/*if (ListeJoueur.size() != 1) {
				Nouvelle_Partie(-1);
			}*/
			
			Nouvelle_Partie(-1, null, false);
		} else if (PresidentBleu == CodeJoueur) {
			serveur.Envoie(ListeJoueur, "CxINFO#Victoire des rouges !");
			PresidentRouge = -1;
			PresidentBleu = -1;
			/*if (ListeJoueur.size() != 1) {
				Nouvelle_Partie(-1);
			}*/
			
			Nouvelle_Partie(-1, null, false);
		}
	}
	
	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}
	
	public boolean LoadMapForto(Joueur Loader, String ID, String C1, String C2, String C3, String C4, String C5, String C6, String Map, int COULEUR) {
		/* Chargement d'une map Forto avec un monde qui n'est pas personnalisé */
		try {
			Rally map = new Rally(serveur.Forteresse_ListeMonde.get(Integer.valueOf(ID)), new String[]{C1, C2, C3, C4, C5, C6}, Map);
			return LoadMapForto(Loader, map);
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public boolean LoadMapForto(Joueur Loader, String ID, String[] Couleurs, String Map, String[] InfosMondePerso) {
		MondeForteresse monde = MondeForteresse.CreerMondePerso(Map, InfosMondePerso);
		Rally map = new Rally(monde, Couleurs, Map);
		
		return LoadMapForto(Loader, map);
	}
	
	public boolean LoadMapForto(Joueur Loader, Rally map) {
		long Now = System.currentTimeMillis();
		
		if (Loader == null || (Now - Loader.LastLoad > 20000 || Loader.AutorisationModo || Loader.AutorisationModoCartesRally)) {
			DecalageLancement = true;
			if (Loader != null) {
				Loader.LastLoad = Now;
			}
			
			Nouvelle_Partie(-2, map, false);
			
			return true;
		} else {
			Loader.Envoie("CxINFO#Vous devez attendre 20 secondes avant de pouvoir utiliser cette commande.");
			return false;
		}
	}
	
	public void CleanDonjon() {
		// On efface les caractéristiques par rapport au donjon
		NumeroDonjon = 0;
		NiveauDonjon = 0;
		NiveauBonus = false;
	}
	
	public void StopLoadAuto() {
		LoadOff = true;
	}
	
	public void SetModeMap(int Mode) {
		MapSauvegardable = (Mode != Rally.MODE_EXPLORATION);
		ModeMap = Mode;
	}
	
	public boolean PeutSauverMap() {
		return MapSauvegardable;
	}
	
	@Override
	public boolean BesoinPartieAttente() {
		return (AutoriserChoixEquipe && !partieMere.PartieElo);
	}
	
	@Override
	public void AjouterPartieAttente(Joueur J) {
		J.Envoie("CxTe#" + EquipeRouge + "#" + EquipeBleu);
		J.PartieEnAttente = partieMere;
	}
	
	public void EnvoieMessageMode(Joueur J) {
		if (ModeJeu != MODE_AUCUN) {
			switch (ModeJeu) {
				case MODE_FRIGO:
					J.Envoie("CxINFO#CxINFO#Vous entrez dans un salon Frig. Le but est de détruire le réfrigérateur adverse.");
					break;
				case MODE_FRAG:
					J.Envoie("CxINFO#CxINFO#Vous entrez dans un salon Frag. Le but est d'éliminer un maximum d'adversaires.");
					break;
				case MODE_KILL:
					J.Envoie("CxINFO#CxINFO#Vous entrez dans un salon Frag. Le but est d'éliminer un maximum d'adversaires.");
					break;
				case MODE_CROSS:
					J.Envoie("CxINFO#CxINFO#Vous entrez dans un salon Cross. Le but est de terminer le rally en cours.");
					break;
			}
		}
	}
	
///
	public String CodeFinMapConfirme(long JOUEUR, long Temps, long IdRally){

		/*

		--------------------------------------------------------------------------------
		|                                   HEADER                                     |
		--------------------------------------------------------------------------------
		| TAILLE EN CHIFFRE PREMIER BYTE (4b)  |  TAILLE EN CHIFFRE DEUXIÈME BYTE (4b) | (h1)
		--------------------------------------------------------------------------------
		| RANDOM1 |  TAILLE ID     |     TAILLE DURÉE    |    TAILLE ID MAP  | RANDOM2 | (h2)
		--------------------------------------------------------------------------------
		| 2 bits  |     4 bits     |        4 bits       |       4 bits      |  2 bits |
		--------------------------------------------------------------------------------
		| [0 - 1] |     [2 - 6]    |   [7 - 8] [0 - 1]   |       [2 - 6]     | [7 - 8] |
		--------------------------------------------------------------------------------

		--------------------------------------------------------------------------------
		|                                   CORPS                                      |
		--------------------------------------------------------------------------------
		|                    MIXTE ID JOUEUR, TEMPS, ID MAP 2 - 1 - 3                  |
		--------------------------------------------------------------------------------
		|                                  CHECKSUM                                    |
		--------------------------------------------------------------------------------

		Mixte ID : 2 premiers chiffres de ID JOUEUR + {RANDOM1 ou RANDOM2}
		        1 premier chiffre de TEMPS + {RANDOM2 ou RANDOM1}
		        2 premiers chiffres ID MAP + {RANDOM1 ou RANDOM2}
		        
		        Si >= 100 : LETTRE_ALÉATOIRE + (val % 10)

		Alternance des RANDOMS une fois sur deux.

		Checksum : (([(RANDOM1 + RANDOM2 + 1) * ID JOUEUR] % TEMPS) + ID MAP / (TAILLE ID + TAILLE DURÉE) + ID MAP % (TAILLE ID MAP - RANDOM2)) * 5 / 17

		*/
		 	String code = new String();
		 	
		 	byte random_byte[] = {(byte)((Math.random() * 10) % 4), (byte)((Math.random() * 10) % 4)};
		 	char random_lettre_100 = (char)((Math.random() * 100) % 26 + 'A');
		 	
		 	String JOUEUR_ID = String.valueOf(JOUEUR);
		 	String DUREE = String.valueOf(Temps);
		 	String ID_MAP = String.valueOf(IdRally);

		 	byte TAILLE_ID_J = (byte) JOUEUR_ID.length();
		 	byte TAILLE_DUREE = (byte) DUREE.length();
		 	byte TAILLE_ID_MAP = (byte) ID_MAP.length();
		 	
		 	String byte_h2_1 = new String();
		 	String byte_h2_2 = new String();
		 	
		 	int trois = Integer.parseInt("11", 2); // 0b11
		 	int douze = Integer.parseInt("1100", 2); // 0b1100
		 	int quinze = Integer.parseInt("1111", 2); // 0b1111
		 	
		 	int add;
		 	
		 	try {

		   // 1er octet h2
		 	byte_h2_1 = String.valueOf((random_byte[0] & trois)
		 			               | ((TAILLE_ID_J & quinze) << 2)
		 			               | ((TAILLE_DUREE & trois) << 6));
		  // 2ème octet h2
		 	byte_h2_2 = String.valueOf(((TAILLE_DUREE & douze) >> 2)
				                       | ((TAILLE_ID_MAP & quinze) << 2)
				                       | ((random_byte[1] & trois) << 6));
		  // h1 :
		 	code = Integer.toHexString((byte_h2_1.length() + ((byte_h2_2.length()) << 4)));
		  // h2 :
		 	code += byte_h2_1 + byte_h2_2;

		  // Corps
		 	int j = 0;
		 	int count_ji = 0, count_du = 0, count_ma = 0;
		 	
		 	for (int i = 0; i != TAILLE_ID_J + TAILLE_DUREE + TAILLE_ID_MAP;){

		 	  // Ajout des 2 <count_ji>ème chiffres de ID JOUEUR + {RANDOM1 ou RANDOM2}
		 		if (count_ji + 1 < TAILLE_ID_J ){
		 			add = (JOUEUR_ID.charAt(count_ji++) - 48) * 10 + (JOUEUR_ID.charAt(count_ji++) - 48) + random_byte[j];
		 			if (add >= 100) {
		 				code += "" + random_lettre_100 + (add % 10);
		 			}
		 			else if (add < 10) {
		 				code += "0" + (add % 10);
		 			}
		 			else {
		 				code += String.valueOf(add);
		 			}
		 			
		  		    j = (~j) & 1;
		 		    i += 2;
		 		 } else if(count_ji < TAILLE_ID_J) { // Ajout uniquement du dernier chiffre * 10 + {RANDOM1 ou RANDOM2}
		 		    if ((JOUEUR_ID.charAt(count_ji) - 48) == 0) {
		 		    	code += "0";
		 		    }
		 		    code += String.valueOf((JOUEUR_ID.charAt(count_ji++) - 48) * 10 + random_byte[j]);
		 			
		            j = (~j) & 1;
		            ++i;
		         }

		 	  // Ajout du xème chiffre * 10 de TEMPS + {RANDOM2 ou RANDOM1}
		 		if(count_du < TAILLE_DUREE){
		 			if((DUREE.charAt(count_du) - 48) == 0){
		 				code += "0";
		 			}
		 			code += String.valueOf((DUREE.charAt(count_du++) - 48) * 10 + random_byte[j]);
		            
		 			j = (~j) & 1;
		            ++i;
				  }

		 	  // Ajout des 2 <count_ma>èmes premiers chiffres ID MAP + {RANDOM1 ou RANDOM2}
		 		if (count_ma + 1 < TAILLE_ID_MAP){
		 			add = (ID_MAP.charAt(count_ma++) - 48) * 10 + (ID_MAP.charAt(count_ma++) - 48) + random_byte[j];
		 			if(add >= 100){
		 			  code += "" + random_lettre_100 + (add % 10);
		 			}
		 			else if(add < 10){
		 	 		  code += "0" + (add % 10);
		 			}
		 			else{
		  			   code += String.valueOf(add);
		 			}

		  		    j = (~j) & 1;
		  		    i+=2;
		  		 } else if(count_ma < TAILLE_ID_MAP){ // Ajout uniquement du dernier chiffre * 10 + {RANDOM1 ou RANDOM2}
		    		 if((ID_MAP.charAt(count_ma) - 48) == 0) {
		    	 		 code += "0";
		    		 }
		    	      code += String.valueOf((ID_MAP.charAt(count_ma++) - 48) * 10 + random_byte[j]);

		              j = (~j) & 1;
		              ++i;
		  		 }
		 	 }
		 	
		   // CheckSum :
		// Checksum : (([(RANDOM1 + RANDOM2 + 1) * ID JOUEUR] % TEMPS) + ID MAP / (TAILLE ID + TAILLE DURÉE) + ID MAP % (TAILLE ID MAP - RANDOM2)) * 5 / 17
		     code += String.valueOf((((((random_byte[0] + random_byte[1] + 1) * JOUEUR) % (Temps + 1))
		                            + IdRally / (TAILLE_ID_J + TAILLE_DUREE)
		                            + IdRally % ((TAILLE_ID_MAP - random_byte[1]) == 0 ? 1 : (TAILLE_ID_MAP - random_byte[1])))
		    		                * 5) / 17);

				return code;
		 	} catch(Exception e){
		 		return "";
		 	}

		}
}