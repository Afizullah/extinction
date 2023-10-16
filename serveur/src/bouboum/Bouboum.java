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

package bouboum;


import java.util.ArrayList;

import joueur.Joueur;
import serveur.Boite;
import serveur.Jeu;
import serveur.Partie;
import serveur.Serveur;

public class Bouboum extends Jeu {
	public final static String $ = "#";
	private int[][] NiveauEnCours;
	private boolean DemarragePartieEnCours = false;
	public int ScoreMaxBoum = 100000;
	public boolean AllIn = false;
	public String AllInJoueur = "";
	public int AllInBonus = 0;
	private int EncoreEnVie;
	private boolean StatsActive = false;
	public boolean NewStatsActive = false;
	private boolean MaraActive = false;
	private int PartieAvantReset = 11;
	private Serveur.Action TimerPartie;
	public boolean SalonFrag = false;
	public boolean SalonOffi = false;
	public String FutureMap = null;
	public GestionnaireModeJeu mode;
	
	public byte H_ApparitionMapEvent = 0;
	
	//
	public final static int CASE_VIDE = 0;
	public final static int CASE_NOIRE = 1;
	public final static int CASE_BLEUE = 2;
	public final static int CASE_TRESOR = 3;
	public final static int CASE_GLACE = 4;
	public final static int CASE_GLACE_EXPLOSABLE = 5;
	public final static int BOMBE_VIDE = 8; // Bombe sur une case vide
	public final static int BOMBE_GLACE = 9; // Bombe sur glace
	public final static int BOMBE_GLACE_EXPLOSABLE = 10;
	//

	public ArrayList<Bombe> ListeBombe = new ArrayList<Bombe>();

	public Bouboum(Serveur serveur, Partie partieMere) {
		super(serveur, partieMere, 120000);
		mode = new GestionnaireModeJeu(new ModeNormal(this));
		//serveur = SERVEUR;
	}
	
	public void ChangerModeJeu(ModeBouboum modeJeu) {
		mode.setModeBase(modeJeu);
	}

	public void Destruction() {
		if (ListeBombe != null) {
			int Nb = ListeBombe.size();
			for (int i = 0; i < Nb; i++) {
				Bombe Bombe = ListeBombe.get(i);
				Bombe.Active = false;
			}
			ListeBombe = null;
		}
		NiveauEnCours = null;
		if (TimerPartie != null) {
			TimerPartie.Active = false;
			TimerPartie = null;
		}
	}

	public void Reception(Joueur JOUEUR, String STRING, boolean MessagePerimé) {
		String[] Message = STRING.split($);
		String Code = Message[0];
		String Code1 = "";
		if (Code.length()>1) Code1 = Message[0].substring(0, 2);
		
		// Mouvement
		if (Code1.equals("Mv")) {
				if (MessagePerimé) {
					return;
				}

			if (JOUEUR.Vivant || serveur.Evénement.Halloween()) {
				JOUEUR._Bouboum.Colonne = Message[1];
				JOUEUR._Bouboum.Ligne = Message[2];
				serveur.Envoie(ListeJoueur, STRING + "#" + JOUEUR.NomJoueur);
			}
			return;
		}

		// Bombe
		if (Code1.equals("Bo")) {
				if (MessagePerimé) {
					return;
				}

			if (JOUEUR.Vivant || serveur.Evénement.Halloween()) {
				// Pose Bombe
				if (Code.equals("BoP")) {
					Pose_Bombe(JOUEUR, Integer.parseInt(Message[1]), Integer.parseInt(Message[2]));
					return;
				}
				// Mort
				if (Code.equals("BoM")) {
					String Tueur = Message[1];
					if (Tueur.equals(JOUEUR.NomJoueur)) { // Suicide
						if (StatsActive && JOUEUR.StatsActivées()) {
							JOUEUR._Bouboum.Stats_Mort++;
							JOUEUR._Bouboum.Maj_Ratio();
						}
						if (NewStatsActive && JOUEUR.NewStatsActivées()) {
							JOUEUR.Stats.BOUM.Suicide++;
						}
						if (MaraActive) {
							JOUEUR.Mara_Boum_Suicides++;
						}
						/*JOUEUR._Bouboum.Score -= 2;
						if (JOUEUR._Bouboum.Score < 0) {
							JOUEUR._Bouboum.Score = 0;
						}*/
						
						Mort(JOUEUR, JOUEUR.NomJoueur, JOUEUR, false, Message[2]);
					} else {
						int Nb = ListeJoueur.size();
						for (int i = 0; i < Nb; i++) {
							Joueur Joueur = ListeJoueur.get(i);
							if (Joueur.NomJoueur.equals(Tueur)) {
								if (StatsActive && !Joueur.AdresseIP.equals(JOUEUR.AdresseIP) && Joueur.StatsActivées()) {
									Joueur._Bouboum.Stats_Tué++;
									Joueur._Bouboum.Maj_Ratio();
								}
								if (NewStatsActive && !Joueur.AdresseIP.equals(JOUEUR.AdresseIP) && Joueur.NewStatsActivées()) {
									Joueur.Stats.BOUM.Kill++;
								}								
								if (NewStatsActive && !Joueur.AdresseIP.equals(JOUEUR.AdresseIP) && JOUEUR.NewStatsActivées()) {
									JOUEUR.Stats.BOUM.TuéParLesAutres++;
								}								
								if (MaraActive && !Joueur.AdresseIP.equals(JOUEUR.AdresseIP)) {
									Joueur.Mara_Boum_Tués++;
								}
								//Joueur._Bouboum.Score += 2;
								Mort(JOUEUR, Joueur.NomJoueur, Joueur, false, Message[2]);
								return;
							}
						}
						
						Mort(JOUEUR, "", null, false, Message[2]);                        
					}
					
					return;
				}
				// Bonus
				if (Code.equals("BoB")) {
				if (MessagePerimé) {
					return;
				}

					int Colonne = Integer.parseInt(Message[1]);
					int Ligne = Integer.parseInt(Message[2]);
					if (NiveauEnCours[Colonne][Ligne] < 0) {
						Declenchement_Bonus(JOUEUR, Colonne, Ligne);
					}
					return;
				}
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
		//
		JOUEUR.Reception_Spéciale(STRING);
	}

	public void Nouvelle_Partie(Integer MondeChoisi, String Map) {
		DemarragePartieEnCours = false;
		// Timer
		DebutPartie = System.currentTimeMillis();
		if (TimerPartie != null) {
			TimerPartie.Active = false;
		}
		// Score
		boolean ResetScore = false;
		if (PartieAvantReset <= 1) {
			PartieAvantReset = 10;
			if (!SalonFrag) {
				ResetScore = true;
			}
		} else {
			PartieAvantReset--;
		}
		
		// Niveau
		if (FutureMap != null) {
			NiveauEnCours = Nouveau_Niveau(0, FutureMap);
			FutureMap = null;
		} else {
			NiveauEnCours = Nouveau_Niveau(MondeChoisi, Map);
		}
		
		serveur.Envoie(ListeJoueur, "SaN#" + Formatage_Niveau() + "#" + PartieAvantReset);
		// Bombe
		int Nb = ListeBombe.size();
		for (int i = 0; i < Nb; i++) {
			Bombe Bombe = ListeBombe.get(i);
			Bombe.Active = false;
		}
		ListeBombe = new ArrayList<Bombe>();
		//
		EncoreEnVie = 0;
		ArrayList<String> Emplacement = new ArrayList<String>(serveur.Bouboum_ListeDepart);
		int EmplacementRestant = ListeJoueur.size();
		if (EmplacementRestant > 22) {
			EmplacementRestant = 22;
		}
		StringBuilder NouvellePartie = new StringBuilder("SaJ");
		//
		int NbJoueur = ListeJoueur.size();
		ArrayList<String> ListeIP = new ArrayList<String>();
		//
		for (int i = 0; i < NbJoueur; i++) {
			Joueur Joueur = ListeJoueur.get(i);
			if (!ListeIP.contains(Joueur.AdresseIP)){
				ListeIP.add(Joueur.AdresseIP);
			}
		}
		//
		StatsActive = ListeIP.size() > 6 && !SalonFrag;
		NewStatsActive = ListeIP.size() > 6 && SalonOffi;
		MaraActive = serveur.Maraboum && ListeIP.size() > 10 && (ListeJoueur.size() - ListeIP.size()) <= 2 && SalonOffi ;
		if (!AllInJoueur.isEmpty()) { // AllInJoueur is empty with /allin
			AllIn = false;
			AllInBonus = Bonus.BONUS_UNDEFINED;
		}
		//
		if(serveur.LOCAL) {
			StatsActive = true;
			MaraActive = true;
			NewStatsActive = true;
		}
		Joueur MeilleurJoueur = null;
		int MeilleurScore = -1;
		for (int i = 0; i < NbJoueur; i++) {
			Joueur Joueur = ListeJoueur.get(i);
			try {
				if (StatsActive && Joueur.StatsActivées()) {
					// Stats				
					Joueur._Bouboum.Stats_PartieJouée++;
				}
				if (NewStatsActive && Joueur.NewStatsActivées()) {
					Joueur.Stats.BOUM.NouvellePartie();
				}				
				if (MaraActive) {
					Joueur.Mara_Boum_PartiesJouées++;
					Joueur.Mara_Boum_Adversaires+=ListeIP.size();
				}

				//
				Joueur._Bouboum.Reset_Bonus_WithAllIn(AllInBonus);
				int Alea = (int) (Math.random() * EmplacementRestant) * 2;
				Joueur._Bouboum.Colonne = Emplacement.get(Alea);
				Joueur._Bouboum.Ligne = Emplacement.get(Alea + 1);
				EmplacementRestant--;
				Emplacement.remove(Alea);
				Emplacement.remove(Alea);
				Joueur._Bouboum.BombeEnJeu = 0;
				Joueur._Bouboum.actualiserScore();
				if (ResetScore) {
					if (Joueur._Bouboum.Score > MeilleurScore) {
						MeilleurScore = Joueur._Bouboum.Score;
						MeilleurJoueur = Joueur;
					}
					Joueur._Bouboum.Score = 0;
				}
				NouvellePartie.append("#" + Joueur.NomJoueur + "," + Joueur._Bouboum.Colonne + "," + Joueur._Bouboum.Ligne + "," + Joueur._Bouboum.Score + "%" + Joueur._Bouboum.Stats_PartieGagnée);
				EncoreEnVie++;
				Joueur.Vivant = true;
				Joueur._Bouboum.PuissanceBombe = 1;
				Joueur._Bouboum.BombeDisponible = 1;
			} catch (Exception e) {
				Joueur.Vivant = false;
			}
			
		}
		
		//
		if (MeilleurJoueur != null) {
			serveur.Envoie(ListeJoueur, "SaR#" + MeilleurJoueur.NomJoueur + "#" + MeilleurJoueur._Forteresse.Sexe + "#" + MeilleurScore);
			if (StatsActive && MeilleurJoueur.StatsActivées()) {
				// Point bonus
				MeilleurJoueur._Bouboum.Stats_PartieGagnée++;
				if (MeilleurJoueur._Bouboum.Stats_PartieGagnée + MeilleurJoueur._Bouboum.Stats_OldGagnées == 10000) {
					serveur.BOITE.Requete(Boite.AJOUTER_RECOMPENSE_JOUEUR, MeilleurJoueur, MeilleurJoueur.NomJoueur, "100", "10000 victoires !");
				}
			}
			if (NewStatsActive && MeilleurJoueur.NewStatsActivées()) {
				MeilleurJoueur.Stats.BOUM.MancheGagnée++;
			}			
			if (MaraActive) {
				MeilleurJoueur.Mara_Boum_Manches++;
			}
		}
		//
		serveur.Envoie(ListeJoueur, NouvellePartie.toString());
		super.Nouvelle_Partie();
		

		if (AllIn && AllInJoueur.isEmpty()) {
			serveur.Envoie(ListeJoueur, "ChB#"+Bonus.BONUS_ALLIN+"##"+AllInBonus);
		}
		
		//
		TimerPartie = serveur.new Action(System.currentTimeMillis() + 120000);
		TimerPartie.NouvellePartieBouboum = true;
		TimerPartie.PartieBouboum = this;
		
		mode.setNextMode(this);
		GestionComptesTournoi();
	}
	
	public void Mort(Joueur JOUEUR, String TUEUR, Joueur JTueur, boolean DECO, String TYPE_BOMBE) {
		Mort(JOUEUR, TUEUR, JTueur, DECO, TYPE_BOMBE, false);
	}
	
	public void Mort(Joueur JOUEUR, String TUEUR, Joueur JTueur, boolean DECO, String TYPE_BOMBE, boolean MortStV, int trash) {
		// Gestion de la mort
		if (!DECO) {
			serveur.Envoie(ListeJoueur, "BoM#" + JOUEUR.NomJoueur + "#" + TUEUR + "#" + (int) (Math.random() * 10) + "#" + (MortStV?"1":"0"));
		}
		
		// Fin All-in
		if (AllIn && JOUEUR.NomJoueur.equals(AllInJoueur)) {
			FinAllIn();
		}
		
		if (!serveur.Evénement.Halloween() || JOUEUR.Vivant) { // Fantômes sinon > ne diminuent pas le nombre de joueurs morts
			EncoreEnVie--;
		}
		
		JOUEUR.Vivant = false;
		JOUEUR._Bouboum.estTuePar(JTueur);
		if (JTueur != null) {
			JTueur._Bouboum.aTue(JOUEUR);
		}
		
		if (TYPE_BOMBE.equals("" + Bombe.BOMBE_CUPIDONNE)) { // Mort de l'amoureux
			
		}
		
		// Gestion de la victoire
	}

	public void Mort(Joueur JOUEUR, String TUEUR, Joueur JTueur, boolean DECO, String TYPE_BOMBE, boolean MortStV) {
		/*
		 <JOUEUR> : Personne tuée
		 <JTueur> : Instance de type Joueur du tueur
		 <TYPE_BOMBE> : Type de bombe (cf ...)
		 <MortStV> : Mort spéciale Saint Valentin de personnes liées via la Bombe cupidonne 
		*/
		boolean StVDejaVerif = false; // Permet de vérifier l'All-in
		boolean BombeEvent = TYPE_BOMBE.equals("" + Bombe.BOMBE_CUPIDONNE);
		
		JOUEUR._Bouboum.estTuePar(JTueur);
		if (JTueur != null) {
			JTueur._Bouboum.aTue(JOUEUR);
		}
		
		if (!DECO) {
			serveur.Envoie(ListeJoueur, "BoM#" + JOUEUR.NomJoueur + "#" + TUEUR + "#" + (int) (Math.random() * 10) + "#" + (MortStV?"1":"0"));
		}
		
		if (serveur.Evénement.StValentin() && BombeEvent && EncoreEnVie >= 3 && !MortStV) {
			Joueur J = serveur.TrouverAmoureux(ListeJoueur, JOUEUR);
			if (J != null && J.Vivant) {
				Mort(J, TUEUR, JTueur, false, TYPE_BOMBE, true);
				StVDejaVerif = true;
			}
		}
		
		if (!serveur.Evénement.Halloween() || JOUEUR.Vivant) {
			EncoreEnVie--;
		}
		
		JOUEUR.Vivant = false;
		
		if (AllIn && JOUEUR.NomJoueur.equals(AllInJoueur)) {
			FinAllIn();
		}
		
		if (!DemarragePartieEnCours && ListeJoueur.size() > 0 && (EncoreEnVie < 2
			 || (serveur.Evénement.StValentin() && EncoreEnVie < 3 && ListeJoueur.size() > 3)) && !StVDejaVerif /* Double victoire St Val */) {

			// on cherche le gagnant
			Joueur Gagnant = null;
			Joueur GagnantStVal = null;
			int Nb = ListeJoueur.size();
			for (int i = 0; i < Nb; i++) {
				Joueur Joueur = ListeJoueur.get(i);
				if (Joueur.Vivant) {
					// Check double victoire Saint Valentin
					if (EncoreEnVie == 2) {
						if (serveur.Evénement.StValentin()) {
							GagnantStVal = serveur.TrouverAmoureux(ListeJoueur, Joueur);
							if (GagnantStVal == null || !GagnantStVal.Vivant) { // Les deux joueurs en vie ne sont pas liés
								return;
							}
						} else {
							return;
						}
					}
					
					Gagnant = Joueur;
					break;
				}
			}
			
			VictoireJoueur(Gagnant);
			VictoireJoueur(GagnantStVal);

			//new TempsMort();
			FinPartie();
		} else {
			if (mode.finPartieSiBesoin()) {
				FinPartie();
			}
		}
	}
	
	private void FinPartie() {
		TimerPartie.Active = false;
		TimerPartie = serveur.new Action(System.currentTimeMillis() + 2000);
		TimerPartie.NouvellePartieBouboum = true;
		TimerPartie.PartieBouboum = this;
	}
	
	public void VictoireJoueur(Joueur Gagnant) {
		if (Gagnant != null) {
			// on regarde si d'autres personnes ont la même IP
			int MemeIP = 0;
			int Nb = ListeJoueur.size();
			
			for (int i = 0; i < Nb; i++) {
				Joueur Joueur = ListeJoueur.get(i);
				if (Joueur.AdresseIP.equals(Gagnant.AdresseIP)) {
					MemeIP++;
				}
			}

			// scores
			if (StatsActive && MemeIP <= 3 && Gagnant.StatsActivées()) {
				Gagnant._Bouboum.Stats_PartieGagnée++;
				if (Gagnant._Bouboum.Stats_PartieGagnée + Gagnant._Bouboum.Stats_OldGagnées == 10000) {
					serveur.BOITE.Requete(Boite.AJOUTER_RECOMPENSE_JOUEUR, Gagnant, Gagnant.NomJoueur, "100", "10000 victoires !");
				}
			}
			if (NewStatsActive && MemeIP <= 3 && Gagnant.NewStatsActivées()) {
				Gagnant.Stats.BOUM.PartieGagnée++;
			}				
			if (MaraActive) {
				Gagnant.Mara_Boum_PartiesGagnées++;
			}
			/*if (!SalonFrag) {
				Gagnant._Bouboum.Score += 10;
			}*/
			
			Gagnant._Bouboum.aGagne();
			
			serveur.Envoie(ListeJoueur, "ChV#" + Gagnant.NomJoueur);
		}
	}

	public void FinAllIn() {
		AllIn = false;
		AllInJoueur = "";
		NiveauEnCours[2][0] = -1;
		NiveauEnCours[25][0] = -1;
		NiveauEnCours[2][18] = -1;
		NiveauEnCours[25][18] = -1;
		if (AllInBonus==Bonus.BONUS_POSEAUTO) { // si pose auto, aucun bonus
			AllInBonus = Bonus.BONUS_AUCUN;
		}
		serveur.Envoie(ListeJoueur, "BoAI#"+AllInBonus);
		AllInBonus = Bonus.BONUS_UNDEFINED;
	}
	
	private void SetBombe(int COLONNE, int LIGNE) {
		if (NiveauEnCours[COLONNE][LIGNE] == CASE_VIDE) {
			NiveauEnCours[COLONNE][LIGNE] = BOMBE_VIDE;
		} else if (NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE) { // Bloc de glace
			NiveauEnCours[COLONNE][LIGNE] = BOMBE_GLACE;
		} else if (NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE_EXPLOSABLE) {
			NiveauEnCours[COLONNE][LIGNE] = BOMBE_GLACE_EXPLOSABLE;
		}
	}
	
	private void NettoyerCase(int COLONNE, int LIGNE) {
		if (NiveauEnCours[COLONNE][LIGNE] == CASE_VIDE || NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE) {
			return;
		} else if (NiveauEnCours[COLONNE][LIGNE] == BOMBE_GLACE) { // Glace + Bombe
			NiveauEnCours[COLONNE][LIGNE] = CASE_GLACE;
		} else {
			NiveauEnCours[COLONNE][LIGNE] = CASE_VIDE;
		}
	}
	
	private boolean CodeBombe(int Code) {
		return (Code == BOMBE_VIDE || Code == BOMBE_GLACE || Code == BOMBE_GLACE_EXPLOSABLE);
	}

	public void Pose_Bombe(Joueur JOUEUR, int COLONNE, int LIGNE) {
		if (JOUEUR._Bouboum.BombeEnJeu + 1 > JOUEUR._Bouboum.BombeDisponible) {
			return;
		}
		if (NiveauEnCours[COLONNE][LIGNE] < 0) {
			Declenchement_Bonus(JOUEUR, COLONNE, LIGNE);
		}
		if (NiveauEnCours[COLONNE][LIGNE] == CASE_VIDE || NiveauEnCours[COLONNE][LIGNE] == CASE_GLACE) {
			SetBombe(COLONNE, LIGNE);
			//NiveauEnCours[COLONNE][LIGNE] = 8;
			JOUEUR._Bouboum.BombeEnJeu++;
			Bombe Bombe = new Bombe(this, JOUEUR._Bouboum.PuissanceBombe, LIGNE, COLONNE, JOUEUR, 0);
			ListeBombe.add(Bombe);
		}
	}

	public void Explosion(int LIGNE, int COLONNE, int PUISSANCE, Joueur JOUEUR, int PROPAGATION, int TYPE) {
		boolean SouffleDroite = PROPAGATION != 1;
		boolean SouffleGauche = PROPAGATION != 2;
		boolean SouffleBas = PROPAGATION != 3;
		boolean SouffleHaut = PROPAGATION != 4;

		boolean SuperBombe = TYPE == Bombe.BOMBE_SUPER;
		boolean UltraBombe = TYPE == Bombe.BOMBE_ULTRA;
		boolean BombeFlocon = TYPE == Bombe.BOMBE_FLOCON;

		JOUEUR._Bouboum.BombeEnJeu--;


		serveur.Envoie(ListeJoueur, "BoE#" + LIGNE + "#" + COLONNE + "#" + PUISSANCE + "#" + PROPAGATION + "#" + TYPE);


		//NiveauEnCours[COLONNE][LIGNE] = 0;
		NettoyerCase(COLONNE, LIGNE);
		for (int i = 1; i <= PUISSANCE; i++) {
			int Souffle;
			if (SouffleDroite) {
				Souffle = COLONNE + i;
				if (Souffle < 29) {
					int CodeCase = NiveauEnCours[Souffle][LIGNE];
					
					
					if (CodeCase == Bouboum.CASE_BLEUE) {
						NettoyerCase(Souffle, LIGNE);
						//NiveauEnCours[Souffle][LIGNE] = 0;
						if (!SuperBombe) {
							SouffleDroite = false;
						}
						Bonus(Souffle, LIGNE, TYPE);
					} else {
						if (CodeCase == Bouboum.CASE_NOIRE) {
							if (UltraBombe) {
								UltraBombeNouveauBonus(Souffle,LIGNE);
							}
							SouffleDroite = false;
						} else if (CodeCase == Bouboum.CASE_TRESOR) { // Bloc aux trésors
							ExplosionBlocTresors(Souffle, LIGNE, JOUEUR, SuperBombe);
							if (!SuperBombe) {
								SouffleDroite = false;
							}
						} else {
							if (CodeBombe(CodeCase)) {
								Enchainement(LIGNE, Souffle, 2);
								SouffleDroite = false;
							} else {
								NettoyerCase(Souffle, LIGNE);
								//NiveauEnCours[Souffle][LIGNE] = 0;
								if (BombeFlocon && CodeCase == Bouboum.CASE_VIDE) {
									GenerationBlocsGlaceBombeFlocon(Souffle, LIGNE);
								}
							}
						}
					}
				} else {
					SouffleDroite = false;
				}
			}

			if (SouffleGauche) {
				Souffle = COLONNE - i;
				if (Souffle >= 0) {
					int CodeCase = NiveauEnCours[Souffle][LIGNE];
					if (CodeCase == 2) {
						NettoyerCase(Souffle, LIGNE);
						//NiveauEnCours[Souffle][LIGNE] = 0;
						if (!SuperBombe) {
							SouffleGauche = false;
						}
						Bonus(Souffle, LIGNE, TYPE);
					} else {
						if (CodeCase == 1) {
							if (UltraBombe) {
								UltraBombeNouveauBonus(Souffle,LIGNE);
							}
							SouffleGauche = false;
						} else if (CodeCase == 3) { // Bloc aux trésors
							ExplosionBlocTresors(Souffle, LIGNE, JOUEUR, SuperBombe);
							if (!SuperBombe) {
								SouffleGauche = false;
							}
						} else {
							if (CodeBombe(CodeCase)) {
								Enchainement(LIGNE, Souffle, 1);
								SouffleGauche = false;
							} else {
								NettoyerCase(Souffle, LIGNE);
								
								if (BombeFlocon && CodeCase == Bouboum.CASE_VIDE) {
									GenerationBlocsGlaceBombeFlocon(Souffle, LIGNE);
								}
								//NiveauEnCours[Souffle][LIGNE] = 0;
							}
						}
					}
				} else {
					SouffleGauche = false;
				}
			}

			if (SouffleBas) {
				Souffle = LIGNE + i;
				if (Souffle < 19) {
					int CodeCase = NiveauEnCours[COLONNE][Souffle];
					if (CodeCase == 2) {
						NettoyerCase(COLONNE, Souffle);
						//NiveauEnCours[COLONNE][Souffle] = 0;
						if (!SuperBombe) {
							SouffleBas = false;
						}
						Bonus(COLONNE, Souffle, TYPE);
					} else {
						if (CodeCase == 1) {
							if (UltraBombe) {
								UltraBombeNouveauBonus(COLONNE,Souffle);
							}
							SouffleBas = false;
						} else if (CodeCase == 3) { // Bloc aux trésors
							ExplosionBlocTresors(COLONNE, Souffle, JOUEUR, SuperBombe);
							if (!SuperBombe) {
								SouffleBas = false;
							}
						} else {
							if (CodeBombe(CodeCase)) {
								Enchainement(Souffle, COLONNE, 4);
								SouffleBas = false;
							} else {
								NettoyerCase(COLONNE, Souffle);
								//NiveauEnCours[COLONNE][Souffle] = 0;
								if (BombeFlocon && CodeCase == Bouboum.CASE_VIDE) {
									GenerationBlocsGlaceBombeFlocon(COLONNE, Souffle);
								}
							}
						}
					}
				} else {
					SouffleBas = false;
				}
			}

			if (SouffleHaut) {
				Souffle = LIGNE - i;
				if (Souffle >= 0) {
					int CodeCase = NiveauEnCours[COLONNE][Souffle];
					if (CodeCase == 2) {
						NettoyerCase(COLONNE, Souffle);
						//NiveauEnCours[COLONNE][Souffle] = 0;
						if (!SuperBombe) {
							SouffleHaut = false;
						}
						Bonus(COLONNE, Souffle, TYPE);
					} else {
						if (CodeCase == 1) {
							if (UltraBombe) {
								UltraBombeNouveauBonus(COLONNE,Souffle);
							}
							SouffleHaut = false;
						} else if (CodeCase == 3) { // Bloc aux trésors
							ExplosionBlocTresors(COLONNE, Souffle, JOUEUR, SuperBombe);
							if (!SuperBombe) {
								SouffleHaut = false;
							}
						} else {
							if (CodeBombe(CodeCase)) {
								Enchainement(Souffle, COLONNE, 3);
								SouffleHaut = false;
							} else {
								NettoyerCase(COLONNE, Souffle);
								//NiveauEnCours[COLONNE][Souffle] = 0;
								if (BombeFlocon && CodeCase == Bouboum.CASE_VIDE) {
									GenerationBlocsGlaceBombeFlocon(COLONNE, Souffle);
								}
							}
						}
					}
				} else {
					SouffleHaut = false;
				}
			}
		}
	}

	private void UltraBombeNouveauBonus(int COLONNE, int LIGNE) {
		NettoyerCase(COLONNE, LIGNE);
		//NiveauEnCours[COLONNE][LIGNE] = 0;
		
		int LeBonus = (int) (Math.random() * 2);
		if (LeBonus == 0) {
			Bonus_Puissance(COLONNE, LIGNE);
		} else {
			Bonus_Bombe(COLONNE, LIGNE);
		}
	}
	
	private void ExplosionBlocTresors(int COLONNE, int LIGNE, Joueur Poseur, boolean SuperBombe) {
		NettoyerCase(COLONNE, LIGNE);
		//NiveauEnCours[COLONNE][LIGNE] = 0;
		
		if (SuperBombe) {
			return;
		}
		
		if (Math.random() < 0.5) {
			Poseur._Bouboum.addNbBombes(5);
		} else {
			Poseur._Bouboum.addPuissance(5);
		}
	}
	
	private void GenerationBlocsGlaceBombeFlocon(int COLONNE, int LIGNE) {
		if (Math.random() < 0.25) {
			EnvoieChangementCase(LIGNE, COLONNE, CASE_GLACE_EXPLOSABLE);
		}
	}

	private void Bonus(int COLONNE, int LIGNE, int TYPE_BOMBE) {
		double Alea = Math.random();
		if (Alea < 0.2) {
			NiveauEnCours[COLONNE][LIGNE] = -1; // tete de mort
			serveur.Envoie(ListeJoueur, "BoB#0#" + COLONNE + "#" + LIGNE);
		} else if (Alea < 0.5) {
			Bonus_Bombe(COLONNE,LIGNE); // -2
		} else if (Alea < 0.9) {
			Bonus_Puissance(COLONNE,LIGNE); // -3
		}
	}

	private void Bonus_Puissance(int COLONNE, int LIGNE) {
		NiveauEnCours[COLONNE][LIGNE] = -3;
		serveur.Envoie(ListeJoueur, "BoB#2#" + COLONNE + "#" + LIGNE);
	}
	private void Bonus_Bombe(int COLONNE, int LIGNE) {
		NiveauEnCours[COLONNE][LIGNE] = -2;
		serveur.Envoie(ListeJoueur, "BoB#1#" + COLONNE + "#" + LIGNE);
	}

	public void Declenchement_Bonus(Joueur JOUEUR, int COLONNE, int LIGNE) {
		int Type = NiveauEnCours[COLONNE][LIGNE];
		NettoyerCase(COLONNE, LIGNE);
		//NiveauEnCours[COLONNE][LIGNE] = 0;
		if (Type == -1) {
			//NiveauEnCours[COLONNE][LIGNE] = 0;
			if (AllIn && AllInJoueur.isEmpty() && AllInBonus == Bonus.BONUS_ALLIN) {
				int bonus = 1 + (int) (Math.random() * ((serveur.Evénement.StValentin() || serveur.Evénement.Noel()) ? Bonus.MAX_BONUS_NO_EVENT : Bonus.MAX_BONUS_NO_EVENT - 1));
				if (bonus == Bonus.BONUS_POSEAUTO) {
					bonus = Bonus.BONUS_VITESSE;
				}
				SetAllIn("",bonus);
				AllInBonus = Bonus.BONUS_ALLIN; // on restaure le bonus allin
				return;
			} else if (AllIn) { // pas de prise de bonus
				// Aucun bonus
				JOUEUR.Envoie("ChB#"+Bonus.BONUS_UNDEFINED+"#" + JOUEUR.NomJoueur);
				return;
			}
			
			JOUEUR._Bouboum.setRandomBonus();
		} else if (Type == -2) {
			JOUEUR._Bouboum.BombeDisponible += 1;
			JOUEUR.Envoie("BoU#0#1");
		} else if (Type == -3) {
			JOUEUR._Bouboum.PuissanceBombe += 1;
			JOUEUR.Envoie("BoU#1#1");
		}
	}
	
	public void SetAllIn(String JOUEUR, int BONUS) {
		AllInJoueur = JOUEUR;
		AllInBonus = BONUS;
		AllIn = true;
		serveur.Envoie(ListeJoueur, "ChB#"+Bonus.BONUS_ALLIN+"#" + AllInJoueur + "#"+BONUS);
		for (Joueur Joueur : ListeJoueur) {
			Joueur._Bouboum.Reset_Bonus_WithAllIn(BONUS);
		}
		
	}

	private void Enchainement(int LIGNE, int COLONNE, int PROPAGATION) {
		int Nb = ListeBombe.size();
		for (int i = 0; i < Nb; i++) {
			Bombe Bombe = ListeBombe.get(i);
			if (Bombe.estTouchee(LIGNE, COLONNE)) {
				Bombe.Action.Temporisation(System.currentTimeMillis() + 100);
				//Bombe.Action.Activation = System.currentTimeMillis() + 100;
				Bombe.Propagation = PROPAGATION;
				//
				//				Bombe.Active = false;
				//				ListeBombe.remove(Bombe);
				//			Bombe NBombe = new Bombe(100, Bombe.Puissance, Bombe.Ligne, Bombe.Colonne, Bombe.Joueur, true, PROPAGATION, Bombe.Type);
				//				ListeBombe.add(NBombe);
				return;
			}
		}
	}

	private int[][] Nouveau_Niveau(Integer MondeChoisi, String Map) {
		//int NiveauAleatoire;
		int[][] Niveau = new int[29][19];
		
		if (Map != null) { // Load perso via /load
			String[] Coupe1 = Map.split("#");
			for (int k = 0; k < Coupe1.length; k++) {
				String[] Coupe2 = Coupe1[k].split(",");
				for (int m = 0; m < Coupe2.length; m++) {
					Niveau[k][m] = Integer.parseInt(Coupe2[m]);
				}
			}
		} else {
			int[][] MondeALoad;
			
			if (MondeChoisi == null) {
				int mondeAleatoire = (int) (Math.random() * serveur.Bouboum_ListeMonde.length);
				
				if (mondeAleatoire == 0 && partieMere.PartieElo) { // Pas la map quadrillée (0) en mode elo
					mondeAleatoire = 1;
				}
				
				H_ApparitionMapEvent++;
				if (H_ApparitionMapEvent < 24 || !serveur.Evénement.EvenementEnCours()) {
					//NiveauAleatoire = (int) (Math.random() * serveur.Bouboum_ListeMonde.length);
					MondeALoad = serveur.Bouboum_ListeMonde[mondeAleatoire];
				} else {
					H_ApparitionMapEvent = 0;
					if (serveur.Evénement.Halloween()) {
						MondeALoad = serveur.MondeEventBouboum[Serveur.MondeHalloweenBoum];
					} else {
						MondeALoad = serveur.Bouboum_ListeMonde[mondeAleatoire];
					}
					
				}
			} else {
				//NiveauAleatoire = MondeChoisi % serveur.Bouboum_ListeMonde.length;
				MondeALoad = serveur.Bouboum_ListeMonde[MondeChoisi % serveur.Bouboum_ListeMonde.length];
			}
			
			boolean Noel = serveur.Evénement.Noel();

			for (int i = 0; i < 29; i++) {
				for (int k = 0; k < 19; k++) {
					//Niveau[i][k] = serveur.Bouboum_ListeMonde[NiveauAleatoire][i][k];
					if (MondeALoad[i][k] == Bouboum.CASE_BLEUE && Math.random() < 0.01) { // 1% de chances d'avoir des blocs au trésor
						Niveau[i][k] = Bouboum.CASE_TRESOR;
					} else if (Noel && MondeALoad[i][k] == Bouboum.CASE_VIDE && Math.random() < 0.1) { // 10% de chances d'avoir des cases de glace
						Niveau[i][k] = Bouboum.CASE_GLACE;
					} else {
						Niveau[i][k] = MondeALoad[i][k];
					}
				}
			}
		}

		// Emplacement joueur
		Niveau[2][0] = 0;
		Niveau[3][0] = 0;
		Niveau[2][1] = 0;

		Niveau[6][0] = 0;
		Niveau[7][0] = 0;
		Niveau[6][1] = 0;

		Niveau[10][0] = 0;
		Niveau[11][0] = 0;
		Niveau[10][1] = 0;

		Niveau[14][0] = 0;
		Niveau[15][0] = 0;
		Niveau[14][1] = 0;

		Niveau[18][0] = 0;
		Niveau[19][0] = 0;
		Niveau[18][1] = 0;

		Niveau[22][0] = 0;
		Niveau[23][0] = 0;
		Niveau[22][1] = 0;

		Niveau[26][0] = 0;
		Niveau[27][0] = 0;
		Niveau[26][1] = 0;

		Niveau[0][3] = 0;
		Niveau[0][4] = 0;
		Niveau[1][4] = 0;

		Niveau[0][7] = 0;
		Niveau[0][8] = 0;
		Niveau[1][8] = 0;

		Niveau[0][11] = 0;
		Niveau[0][10] = 0;
		Niveau[1][10] = 0;

		Niveau[0][15] = 0;
		Niveau[0][14] = 0;
		Niveau[1][14] = 0;

		Niveau[28][3] = 0;
		Niveau[28][4] = 0;
		Niveau[27][4] = 0;

		Niveau[28][7] = 0;
		Niveau[28][8] = 0;
		Niveau[27][8] = 0;

		Niveau[28][11] = 0;
		Niveau[28][10] = 0;
		Niveau[27][10] = 0;

		Niveau[28][15] = 0;
		Niveau[28][14] = 0;
		Niveau[27][14] = 0;

		Niveau[2][18] = 0;
		Niveau[1][18] = 0;
		Niveau[2][17] = 0;

		Niveau[6][18] = 0;
		Niveau[5][18] = 0;
		Niveau[6][17] = 0;

		Niveau[10][18] = 0;
		Niveau[9][18] = 0;
		Niveau[10][17] = 0;

		Niveau[14][18] = 0;
		Niveau[13][18] = 0;
		Niveau[14][17] = 0;

		Niveau[18][18] = 0;
		Niveau[17][18] = 0;
		Niveau[18][17] = 0;

		Niveau[22][18] = 0;
		Niveau[21][18] = 0;
		Niveau[22][17] = 0;

		Niveau[26][18] = 0;
		Niveau[25][18] = 0;
		Niveau[26][17] = 0;
		//
		return Niveau;
	}

	public String Formatage_Niveau() {
		StringBuilder Niveau = new StringBuilder(1131);
		for (int i = 0; i < 29; i++) {
			Niveau.append(";");
			for (int k = 0; k < 19; k++) {
				Niveau.append("," + NiveauEnCours[i][k]);
			}
		}
		return Niveau.toString();
	}
	
	public void GestionComptesTournoi() {
		if (ListeJoueur.size() < 3) {
			return;
		}
		for(int i = ListeJoueur.size() - 1; i >= 0; --i) {
			Joueur J = ListeJoueur.get(i);
			
			if ((J.AutorisationFilmeur || J.AutorisationTournoiAaaah || J.AutorisationArbitreElo || J.AutorisationFilmeurElo || J.AutorisationTounoiArbitreSecondaire) && J.MortAuto) {
				Mort(J, "M. Serveur", null, false, "");
			}
		}
	}
	
	private void EnvoieChangementCase(int LIGNE, int COLONNE, int NEW_CASE) {
		serveur.Envoie(ListeJoueur, "BoC#" + NEW_CASE + "#" + COLONNE + "#" + LIGNE);
		NiveauEnCours[COLONNE][LIGNE] = NEW_CASE;
	}
	
	@Override
	public boolean PeutEntrer(Joueur J) {
		if (ListeJoueur.size() >= 22) {
			J.Envoie("CxEB");
			return false;
		}
		if (J._Bouboum != null && J._Bouboum.Stats_PartieGagnée > ScoreMaxBoum) {
			J.Envoie("CxES#" + J._Bouboum.Stats_PartieGagnée + "#" + ScoreMaxBoum);
			return false;
		}
		
		return true;
	}
	
	public ModeBouboum getModeEnCours() {
		return mode.getModeEnCours();
	}
	
	public int NombreJoueursVivants() {
		return EncoreEnVie;
	}

}