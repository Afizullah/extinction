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

package elo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import joueur.Joueur;
import serveur.Serveur;

public class Elo {
	static final public byte MS = 0;
	static final public byte RALLY_AAAAH = 1;
	static final public byte DEF = 2;
	static final public byte FS = 3;
	static final public byte RUN = 9;
	
	static final public byte BOUM = 4;
	
	static final public byte FRIGO = 5;
	static final public byte FRAG = 6;
	static final public byte KILL = 7;
	static final public byte CROSS = 8;
	
	static final public byte NB_MODES = 10;
	
	public static final int FORMAT_TEAM = 99;
	
	static final public int UP = 0; // Requête classement Elo : ceux >= au joueur
	static final public int DOWN = 1; // ceux <=
	
	protected int id;
	
	// Aaaah!
	protected EloMode Aaaah_MS = new EloMode();
	protected EloMode Aaaah_Rally = new EloMode();
	protected EloMode Aaaah_Def = new EloMode();
	protected EloMode Aaaah_FS = new EloMode();
	protected EloMode Aaaah_Run = new EloMode();
			
	// Bouboum
	protected EloMode Bouboum = new EloMode();
			
	// Forteresse
	protected EloMode Forto_Frigo = new EloMode();
	protected EloMode Forto_Frag = new EloMode();
	protected EloMode Forto_Kill = new EloMode();
	protected EloMode Forto_Cross = new EloMode();	
	
	static private String[][] strClassementsElo;
	static private boolean besoinUpdate = true; // Indique si le classement a changé depuis la dernière fois
	
	static final public int POINTS_DEBUT_ELO = 1200;
	
	protected static int numMatch = 0;
	
	public Elo(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	protected static void Calculer(EloMode elo, boolean Victoire, double DifferenceElo, int nbJoueurParEquipe) {
		/*
		 NELO = Nouveau score Elo
		 ELOB = Score Elo de base
		 K = 40 pour les 15 premières parties, 20 si Elo<2400, 10 si Elo>2400
		 W = 1 pour une victoire, 0 pour une défaite
		 D = Différence entre les scores Elo des joueurs (plafond à 400)

		 Départ à 1200 (<POINTS_DEBUT_ELO>) pr tout nouveau joueur
		
		 NELO = ELOB + K * (W - (1 + 10 ^ (-D / 400)) ^ (-1))
		*/
		double K;
		int victoire = (Victoire?1:0);
		
		if (nbJoueurParEquipe == 1) {
			if (elo.pj_i <= 15) {
				K = 40.0;
			} else if (elo.elo_i < 2400.0) {
				K = 20.0;
			} else {
				K = 10.0;
			}
			
			elo.pg_i += victoire;
			elo.pj_i++;
			elo.elo_i = (int) (elo.elo_i + (K * (victoire - 1.0 / (1.0 + Math.pow(10.0, -DifferenceElo / 400.0)))));
			if (elo.elo_i < 0) {
				elo.elo_i = 0;
			}
		} else if (nbJoueurParEquipe == 2) {
			if (elo.pj_d <= 15) {
				K = 40.0;
			} else if (elo.elo_d < 2400.0) {
				K = 20.0;
			} else {
				K = 10.0;
			}
			
			elo.pg_d += victoire;
			elo.pj_d++;
			elo.elo_d = (int) (elo.elo_d + (K * (victoire - 1.0 / (1.0 + Math.pow(10.0, -DifferenceElo / 400.0)))));
			if (elo.elo_d < 0) {
				elo.elo_d = 0;
			}
		} else if (nbJoueurParEquipe > 2 || nbJoueurParEquipe == Elo.FORMAT_TEAM){
			if (elo.pj_m <= 15) {
				K = 40.0;
			} else if (elo.elo_m < 2400.0) {
				K = 20.0;
			} else {
				K = 10.0;
			}
			
			elo.pg_m += victoire;
			elo.pj_m++;
			elo.elo_m = (int) (elo.elo_m + (K * (victoire - 1.0 / (1.0 + Math.pow(10.0, -DifferenceElo / 400.0)))));
			if (elo.elo_m < 0) {
				elo.elo_m = 0;
			}
		}
		///
		//System.out.println("CxINFO#Changement d'elo : + " + (int) (K * (victoire - 1.0 / (1.0 + Math.pow(10.0, -DifferenceElo / 400.0)))));
		///
	}
	
	public static double calcDifferenceElo(Elo[] Equipe1, Elo[] Equipe2, byte mode, boolean modeTeam) {
		long eloEquipe1 = 0, eloEquipe2 = 0;
		int taille = (!modeTeam ? Equipe1.length : Elo.FORMAT_TEAM);
		
		for (int i = Equipe1.length - 1; i >= 0; --i) {
			eloEquipe1 += getElo(Equipe1[i].getEloModeByMode(mode), taille);
			eloEquipe2 += getElo(Equipe2[i].getEloModeByMode(mode), taille);
		}
		
		return (eloEquipe1 - eloEquipe2) / Equipe1.length;
	}
	
	protected static int getElo(EloMode elo, int nbJoueurParEquipe) {
		if (elo == null) {
			return POINTS_DEBUT_ELO;
		}
		
		if (nbJoueurParEquipe == 1) {
			return elo.elo_i;
		} else if (nbJoueurParEquipe == 2) {
			return elo.elo_d;
		} else if (nbJoueurParEquipe == Elo.FORMAT_TEAM) {
			return elo.elo_m;
		} else {
			return elo.elo_m;
		}
	}
	
	protected static byte convertStringToMode(String jeu, String mode) {
		jeu = jeu.toLowerCase();
		mode = mode.toLowerCase();
		
		if (jeu.equals("a")) {
			if (mode.equals("ms")) {
				return MS;
			} else if (mode.equals("rally") || mode.equals("ng")) {
				return RALLY_AAAAH;
			} else if (mode.equals("def")) {
				return DEF;
			} else if (mode.equals("fs")) {
				return FS;
			} else if (mode.equals("run")) {
				return RUN;
			}
		} else if (jeu.equals("b")) {
			 if (mode.equals("boum")) {
				return BOUM;
			}
		} else if (jeu.equals("f")) {
			if (mode.equals("frig")) {
				return FRIGO;
			} else if (mode.equals("frag")) {
				return FRAG;
			} else if (mode.equals("kill")) {
				return KILL;
			} /*else if (mode.equals("cross")) {
				return CROSS;
			}*/
		}
		
		return -1;
	}
	
	protected static void UpdateElo(String Jeu, byte TYPE, EloMode elo, int IdJoueur, int nbJoueurParEquipe) {
		// UDPATE $elo_X SET MODE_INDIVe = ?, MODE_INDIVpj = ?, MODE_INDIVpg = ? WHERE id=?
		String debut = getSuffixeEloMode(TYPE) + "_";
		String Requete;
		
		Serveur serveur = Serveur.getServeur();
		
		if (nbJoueurParEquipe == 1) {
			debut += "i";
			Requete = "UPDATE `$elo_" + Jeu + "` SET "
					 + debut + "e=" + elo.elo_i + ", "
					 + debut + "pj=" + elo.pj_i + ", "
					 + debut + "v=" + elo.pg_i + " "
					 + "WHERE id=" + IdJoueur;
		} else if (nbJoueurParEquipe == 2){
			debut += "d";
			Requete = "UPDATE `$elo_" + Jeu + "` SET "
					 + debut + "e=" + elo.elo_d + ", "
					 + debut + "pj=" + elo.pj_d + ", "
					 + debut + "v=" + elo.pg_d + " "
					 + "WHERE id=" + IdJoueur;
		} else if (nbJoueurParEquipe == Elo.FORMAT_TEAM) {
			Requete = "UPDATE `$elo_t_" + Jeu + "` SET "
					 + debut + "e=" + elo.elo_m + ", "
					 + debut + "pj=" + elo.pj_m + ", "
					 + debut + "v=" + elo.pg_m + " "
					 + "WHERE id=" + IdJoueur;
		} else {
			debut += "m";
			Requete = "UPDATE `$elo_" + Jeu + "` SET "
					 + debut + "e=" + elo.elo_m + ", "
					 + debut + "pj=" + elo.pj_m + ", "
					 + debut + "v=" + elo.pg_m + " "
					 + "WHERE id=" + IdJoueur;
		}
		
		
		
		try {
			serveur.Bdd.Requete.execute(Requete);
		} catch (Exception e) {
			System.err.println("3XC UpdateElo : " + Requete); e.printStackTrace();
		}
		
	}
	
	protected EloMode getEloModeByMode(byte TYPE) {
		switch (TYPE) {
			case MS :
				return Aaaah_MS;
			case RALLY_AAAAH :
				return Aaaah_Rally;
			case DEF :
				return Aaaah_Def;
			case FS :
				return Aaaah_FS;
			case RUN :
				return Aaaah_Run;
			case BOUM :
				return Bouboum;
			case FRIGO :
				return Forto_Frigo;
			case FRAG :
				return Forto_Frag;
			case KILL :
				return Forto_Kill;
			case CROSS :
				return Forto_Cross;
		}
		return null;
	}
	
	public static String getSuffixeEloMode(byte TYPE) {
		switch (TYPE) {
			case MS :
				return "ms";
			case RALLY_AAAAH :
				return "ra";
			case DEF :
				return "def";
			case FS :
				return "fs";
			case RUN :
				return "run";
			case BOUM :
				return "k";
			case FRIGO :
				return "fri";
			case FRAG :
				return "fra";
			case KILL :
				return "k";
			case CROSS :
				return "crs";
		}
		
		return "";
	}
	
	public static String modeToString(int TYPE) {
		switch (TYPE) {
		case MS :
			return "ms";
		case RALLY_AAAAH :
			return "a_ra";
		case DEF :
			return "def";
		case FS :
			return "fs";
		case RUN :
			return "run";
		case BOUM :
			return "boum";
		case FRIGO :
			return "frig";
		case FRAG :
			return "frag";
		case KILL :
			return "f_kill";
		case CROSS :
			return "f_ra";
	}
	
	return "";
	}
	
	public static void initClassementElo() {
		strClassementsElo = new String[NB_MODES][];
	}
	
	public static void aEteModif() {
		besoinUpdate = true;
	}
	
	public static void updateClassementElo() {
		if (besoinUpdate) {
			strClassementsElo[MS] = executeClassementElo(MS);
			strClassementsElo[RALLY_AAAAH] = executeClassementElo(RALLY_AAAAH);
			strClassementsElo[DEF] = executeClassementElo(DEF);
			strClassementsElo[FS] = executeClassementElo(FS);
			strClassementsElo[RUN] = executeClassementElo(RUN);
			
			strClassementsElo[BOUM] = executeClassementElo(BOUM);
			
			strClassementsElo[FRIGO] = executeClassementElo(FRIGO);
			strClassementsElo[FRAG] = executeClassementElo(FRAG);
			strClassementsElo[KILL] = executeClassementElo(KILL);
			//strClassementsElo[CROSS] = excuteClassementElo(CROSS);
			
			besoinUpdate = false;
		} 
	}
	
	private static String[] executeClassementElo(int TYPE) {
		String[] classement = {"CxCl#4#", "CxCl#4#", "CxCl#4#", "CxCl#4#"};
		//StringBuilder msg;
		
		try {
			/*PreparedStatement req;
			ResultSet res;
			*/
			////
			classement[0] = getResultatClassement(TYPE, 1);
			classement[1] = getResultatClassement(TYPE, 2);
			classement[2] = getResultatClassement( TYPE, 3);
			classement[3] = getResultatClassement(TYPE, Elo.FORMAT_TEAM);
			////
			
			/*for (int i = 0; i < 3; ++i) {
				req = getRequeteByType(TYPE, serveur, i + 1);
				
				if (req != null) {
					res = req.executeQuery();
					
					msg = new StringBuilder("CxCl#4#");
					
					while (res.next()) {
						msg.append(res.getString(1));
						msg.append(",");
						msg.append(res.getInt(2));
						msg.append(";");
					}
					
					classement[i] = msg.toString();
				}
			}*/
			
		} catch (Exception e) {};
		
		return classement;
	}
	
	
	private static String getResultatClassement(int TYPE, int nbJoueurs) {
		StringBuilder msg = new StringBuilder("CxCl#4#");
		
		try {
			PreparedStatement req;
			ResultSet res;
			
			req = getRequeteByType(TYPE, nbJoueurs);
			
			if (req != null) {
				res = req.executeQuery();
				
				while (res.next()) {
					msg.append(res.getString(1));
					msg.append(",");
					msg.append(res.getInt(2));
					msg.append(";");
				}
				
			}
			
		} catch (Exception e) {};
		
		return msg.toString();
	}
	
	
	public static void envoieClassementElo(int TYPE, Joueur J, int nbJoueursParEquipe) {
		if (nbJoueursParEquipe == Elo.FORMAT_TEAM) { // Classement Team
			nbJoueursParEquipe = strClassementsElo[0].length - 1;
		} else {
			--nbJoueursParEquipe;
			if (nbJoueursParEquipe > 2) { // 3 ou + == mode multi
				nbJoueursParEquipe = 2;
			}
		}
		
		if (strClassementsElo[TYPE][nbJoueursParEquipe] != null) {
			J.Envoie(strClassementsElo[TYPE][nbJoueursParEquipe]);
		}
	}
	
	private static PreparedStatement getRequeteByType(int TYPE, int nbJoueursParEquipe) {
		Serveur serveur = Serveur.getServeur();
		
		// null si aucune requête assosciée
		switch (TYPE) {
			case MS :
				if (nbJoueursParEquipe == 1) {
					return null;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloA_MS_d;
				} else if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloA_T_MS;
				} else {
					return serveur.Bdd.Requete_ClassementEloA_MS_m;
				}
			case RALLY_AAAAH :
				if (nbJoueursParEquipe == 1) {
					return serveur.Bdd.Requete_ClassementEloA_RALLY_i;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloA_RALLY_d;
				} else if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloA_T_RALLY;
				} else {
					return serveur.Bdd.Requete_ClassementEloA_RALLY_m;
				}
			case DEF :
				if (nbJoueursParEquipe == 1) {
					return serveur.Bdd.Requete_ClassementEloA_DEF_i;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloA_DEF_d;
				} else if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloA_T_DEF;
				} else {
					return serveur.Bdd.Requete_ClassementEloA_DEF_m;
				}
			case FS :
				if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloA_T_FS;
				} else if (nbJoueursParEquipe > 2) {
					return serveur.Bdd.Requete_ClassementEloA_FS_m;
				}
			case RUN :
				if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloA_T_RUN;
				} else if (nbJoueursParEquipe > 2) {
					return serveur.Bdd.Requete_ClassementEloA_RUN_m;
				}
			case BOUM :
				if (nbJoueursParEquipe == 1) {
					return serveur.Bdd.Requete_ClassementEloB_BOUM_i;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloB_BOUM_d;
				} else if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloB_T_BOUM;
				} else {
					return serveur.Bdd.Requete_ClassementEloB_BOUM_m;
				}
			case FRIGO :
				if (nbJoueursParEquipe == 1) {
					return serveur.Bdd.Requete_ClassementEloF_FRIGO_i;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloF_FRIGO_d;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloF_T_FRIGO;
				} else {
					return serveur.Bdd.Requete_ClassementEloF_FRIGO_m;
				}
			case FRAG :
				if (nbJoueursParEquipe == 1) {
					return serveur.Bdd.Requete_ClassementEloF_FRAG_i;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloF_FRAG_d;
				} else if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloF_T_FRAG;
				} else {
					return serveur.Bdd.Requete_ClassementEloF_FRAG_m;
				}
			case KILL :
				if (nbJoueursParEquipe == 1) {
					return serveur.Bdd.Requete_ClassementEloF_KILL_i;
				} else if (nbJoueursParEquipe == 2) {
					return serveur.Bdd.Requete_ClassementEloF_KILL_d;
				} else if (nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return serveur.Bdd.Requete_ClassementEloF_T_KILL;
				} else {
					return serveur.Bdd.Requete_ClassementEloF_KILL_m;
				}
			case CROSS :
				if (nbJoueursParEquipe == 1) {
					return serveur.Bdd.Requete_ClassementEloF_CROSS_i;
				} else if (nbJoueursParEquipe == 2 || nbJoueursParEquipe == Elo.FORMAT_TEAM) {
					return null;
				}
		}
		return null;
	}
	
	/**
	 * Envoie le message à envoyer pour afficher les stats elo du profil (/elostats Joueur Jeu)
	 */
	public String Boite_EloToStats(String jeu, String Pseudo) {
		StringBuilder msg = new StringBuilder("CxStatsElo#");
		jeu = jeu.toLowerCase();
		
		msg.append(Pseudo);
		msg.append("#");
		msg.append(jeu);
		msg.append(Serveur.$);
		
		if (jeu.equals("a")) {
			EloModeToString(Aaaah_Rally, 1, msg);
			EloModeToString(Aaaah_Def, 1, msg);
			EloModeToString(Aaaah_MS, 2, msg);
			EloModeToString(Aaaah_FS, 3, msg);
			EloModeToString(Aaaah_Run, 3, msg);
		} else if (jeu.equals("b")) {
			EloModeToString(Bouboum, 1, msg);
		} else if (jeu.equals("f")) {
			EloModeToString(Forto_Frigo, 1, msg);
			EloModeToString(Forto_Frag, 1, msg);
			EloModeToString(Forto_Kill, 1, msg);
		}
		
		return msg.toString();
	}
	
	/**
	 * Utilisé par EloToStats pour convertir les stats d'un jeu en String.
	 */
	private void EloModeToString(EloMode eloMode, int nbJoueursMin, StringBuilder txt) {
		if (eloMode == null) {
			return;
		}
		
		if (nbJoueursMin <= 1) { // Mode 1 vs 1 existant
			txt.append((eloMode.pj_i > 0 ? eloMode.elo_i : "-"));
			txt.append(Serveur.$);
		}
		
		if (nbJoueursMin <= 2) { // Mode 2 vs 2 existant
			txt.append((eloMode.pj_d > 0 ? eloMode.elo_d : "-"));
			txt.append(Serveur.$);
		}
		
		// Mode 3 vs 3 / multi toujours existant
		txt.append((eloMode.pj_m > 0 ? eloMode.elo_m : "-"));
		txt.append(Serveur.$);
	}
}
/*
CREATE TABLE $elo_a (id INT UNSIGNED NOT NULL DEFAULT 0,
ra_iv INT UNSIGNED NOT NULL DEFAULT 0,
ra_ipj INT UNSIGNED NOT NULL DEFAULT 0,
ra_ie INT UNSIGNED NOT NULL DEFAULT 0,
ra_dv INT UNSIGNED NOT NULL DEFAULT 0,
ra_dpj INT UNSIGNED NOT NULL DEFAULT 0,
ra_de INT UNSIGNED NOT NULL DEFAULT 0,
def_iv INT UNSIGNED NOT NULL DEFAULT 0,
def_ipj INT UNSIGNED NOT NULL DEFAULT 0,
def_ie INT UNSIGNED NOT NULL DEFAULT 0,
def_dv INT UNSIGNED NOT NULL DEFAULT 0,
def_dpj INT UNSIGNED NOT NULL DEFAULT 0,
def_de INT UNSIGNED NOT NULL DEFAULT 0,
ms_dv INT UNSIGNED NOT NULL DEFAULT 0,
ms_dpj INT UNSIGNED NOT NULL DEFAULT 0,
ms_de INT UNSIGNED NOT NULL DEFAULT 0,
PRIMARY KEY (id),
INDEX ind_ira (ra_ie),
INDEX ind_dra (ra_de),
INDEX ind_idef (def_ie),
INDEX ind_def (def_de),
INDEX ind_dms (ms_de));

CREATE TABLE $elo_b (
id INT UNSIGNED NOT NULL DEFAULT 0,
k_iv INT UNSIGNED NOT NULL DEFAULT 0,
k_ipj INT UNSIGNED NOT NULL DEFAULT 0,
k_ie INT UNSIGNED NOT NULL DEFAULT 0,
k_dv INT UNSIGNED NOT NULL DEFAULT 0,
k_dpj INT UNSIGNED NOT NULL DEFAULT 0,
k_de INT UNSIGNED NOT NULL DEFAULT 0,
PRIMARY KEY (id),
INDEX ind_ik (k_ie),
INDEX ind_dk (k_de));

CREATE TABLE $elo_f (
id INT UNSIGNED NOT NULL DEFAULT 0,
fra_iv INT UNSIGNED NOT NULL DEFAULT 0,
fra_ipj INT UNSIGNED NOT NULL DEFAULT 0,
fra_ie INT UNSIGNED NOT NULL DEFAULT 0,
fra_dv INT UNSIGNED NOT NULL DEFAULT 0,
fra_dpj INT UNSIGNED NOT NULL DEFAULT 0,
fra_de INT UNSIGNED NOT NULL DEFAULT 0,
fri_iv INT UNSIGNED NOT NULL DEFAULT 0,
fri_ipj INT UNSIGNED NOT NULL DEFAULT 0,
fri_ie INT UNSIGNED NOT NULL DEFAULT 0,
fri_dv INT UNSIGNED NOT NULL DEFAULT 0,
fri_dpj INT UNSIGNED NOT NULL DEFAULT 0,
fri_de INT UNSIGNED NOT NULL DEFAULT 0,
k_iv INT UNSIGNED NOT NULL DEFAULT 0,
k_ipj INT UNSIGNED NOT NULL DEFAULT 0,
k_ie INT UNSIGNED NOT NULL DEFAULT 0,
k_dv INT UNSIGNED NOT NULL DEFAULT 0,
k_dpj INT UNSIGNED NOT NULL DEFAULT 0,
k_de INT UNSIGNED NOT NULL DEFAULT 0,
crs_iv INT UNSIGNED NOT NULL DEFAULT 0,
crs_ipj INT UNSIGNED NOT NULL DEFAULT 0,
crs_ie INT UNSIGNED NOT NULL DEFAULT 0,
PRIMARY KEY (id),
INDEX ind_ifra (fra_ie),
INDEX ind_dfra (fra_de),
INDEX ind_ifri (fri_ie),
INDEX ind_dfri (fri_de),
INDEX ind_ik (k_ie),
INDEX ind_dk (k_de),
INDEX ind_icrs (crs_ie));

ALTER TABLE $elo_a
 ADD INDEX ind_mra (ra_me),
 ADD INDEX ind_mdef (def_me),
 ADD INDEX ind_mms (ms_me),
 ADD INDEX ind_mfs (fs_me);

ALTER TABLE $elo_b
 ADD INDEX ind_mk (k_me);

ALTER TABLE $elo_f
 ADD INDEX ind_mfra (fra_me),
 ADD INDEX ind_mfri (fri_me),
 ADD INDEX ind_mk (k_me);
*/