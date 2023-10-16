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

import java.sql.ResultSet;

import serveur.Serveur;

public class RecompenseElo {
	private static final int SEUIL_OR = 1350;
	private static final int SEUIL_ARGENT = 1180;
	
	public static final int DESACTIVER_PERSO = 0;
	public static final int ACTIVER = 1;
	public static final int DESACTIVER_ALL = 2;
	
	private static final int MIN_A_DEPASSER_RECOMPENSABLE = 3;
	
	public byte recompensesActives; // 0 = désactivées ; 1 = activées ; 2 = désactivées pour tout le monde
	private String recompenseAaaah;
	private String recompenseBoum;
	private String recompenseForto;
	
	public RecompenseElo() {
		recompensesActives = 1;
		recompenseAaaah = "0";
		recompenseBoum = "0";
		recompenseForto = "0";
	}
	
	public static void Attribuer(Serveur serveur) {
		try {
			// Aaaah! ~ rally, def, ms, fs, run
			
			// Bouboum ~ kill
			// Forteresse ~ frig, frag, kill, cross
			
			NettoyerAnciennesClassees();
			
			boolean modeRecompensable[][] = new boolean[Elo.NB_MODES][3];
			// Aaaah
			modeRecompensable[Elo.RALLY_AAAAH][0] = ModeRecompensable(serveur, "a", Elo.RALLY_AAAAH, 1);
			modeRecompensable[Elo.RALLY_AAAAH][1] = ModeRecompensable(serveur, "a", Elo.RALLY_AAAAH, 2);
			modeRecompensable[Elo.RALLY_AAAAH][2] = ModeRecompensable(serveur, "a", Elo.RALLY_AAAAH, 3);
			
			modeRecompensable[Elo.DEF][0] = ModeRecompensable(serveur, "a", Elo.DEF, 1);
			modeRecompensable[Elo.DEF][1] = ModeRecompensable(serveur, "a", Elo.DEF, 2);
			modeRecompensable[Elo.DEF][2] = ModeRecompensable(serveur, "a", Elo.DEF, 3);
			
			modeRecompensable[Elo.MS][1] = ModeRecompensable(serveur, "a", Elo.MS, 2);
			modeRecompensable[Elo.MS][2] = ModeRecompensable(serveur, "a", Elo.MS, 3);
			
			modeRecompensable[Elo.FS][2] = ModeRecompensable(serveur, "a", Elo.FS, 3);
			
			modeRecompensable[Elo.RUN][2] = ModeRecompensable(serveur, "a", Elo.RUN, 3);
			
			// Bouboum
			modeRecompensable[Elo.BOUM][0] = ModeRecompensable(serveur, "b", Elo.BOUM, 1);
			modeRecompensable[Elo.BOUM][1] = ModeRecompensable(serveur, "b", Elo.BOUM, 2);
			modeRecompensable[Elo.BOUM][2] = ModeRecompensable(serveur, "b", Elo.BOUM, 3);
			
			// Forteresse
			modeRecompensable[Elo.FRIGO][0] = ModeRecompensable(serveur, "f", Elo.FRIGO, 1);
			modeRecompensable[Elo.FRIGO][1] = ModeRecompensable(serveur, "f", Elo.FRIGO, 2);
			modeRecompensable[Elo.FRIGO][2] = ModeRecompensable(serveur, "f", Elo.FRIGO, 3);
			
			modeRecompensable[Elo.FRAG][0] = ModeRecompensable(serveur, "f", Elo.FRAG, 1);
			modeRecompensable[Elo.FRAG][1] = ModeRecompensable(serveur, "f", Elo.FRAG, 2);
			modeRecompensable[Elo.FRAG][2] = ModeRecompensable(serveur, "f", Elo.FRAG, 3);
			
			modeRecompensable[Elo.KILL][0] = ModeRecompensable(serveur, "f", Elo.KILL, 1);
			modeRecompensable[Elo.KILL][1] = ModeRecompensable(serveur, "f", Elo.KILL, 2);
			modeRecompensable[Elo.KILL][2] = ModeRecompensable(serveur, "f", Elo.KILL, 3);
			
			modeRecompensable[Elo.CROSS][0] = ModeRecompensable(serveur, "f", Elo.CROSS, 1);
			modeRecompensable[Elo.CROSS][1] = ModeRecompensable(serveur, "f", Elo.CROSS, 2);
			//modeRecompensable[Elo.CROSS][2] = ModeRecompensable(serveur, "f", Elo.CROSS, 3);
			
			String elo_flag;
			
			ResultSet res = serveur.Bdd.BD.createStatement().executeQuery("SELECT id FROM `$elo_a` "
                    + "UNION DISTINCT "
                    + "SELECT id FROM `$elo_b` "
                    + "UNION DISTINCT "
                    + "SELECT id FROM `$elo_f`;");

			int IdJoueur;
			EloJoueur elo;
			
			while (res.next()) {
				IdJoueur = res.getInt(1);
				elo = new EloJoueur(serveur, IdJoueur, "<AttrRecomp>", false, "");
				
				elo_flag = "";
				
				// Aaaah!
				elo_flag += getMaxTab(new int[] {
						    RangMaxElo(elo.getEloModeByMode(Elo.RALLY_AAAAH), 1, modeRecompensable[Elo.RALLY_AAAAH]),
						    RangMaxElo(elo.getEloModeByMode(Elo.DEF), 1, modeRecompensable[Elo.DEF]),
						    RangMaxElo(elo.getEloModeByMode(Elo.MS), 2, modeRecompensable[Elo.MS]),
						    RangMaxElo(elo.getEloModeByMode(Elo.FS), 3, modeRecompensable[Elo.FS]),
						    RangMaxElo(elo.getEloModeByMode(Elo.RUN), 1, modeRecompensable[Elo.RUN])
				});
				
				elo_flag += RangMaxElo(elo.getEloModeByMode(Elo.BOUM), 1, modeRecompensable[Elo.BOUM]);
				
				elo_flag += getMaxTab(new int[] {
					    RangMaxElo(elo.getEloModeByMode(Elo.FRIGO), 1, modeRecompensable[Elo.FRIGO]),
					    RangMaxElo(elo.getEloModeByMode(Elo.FRAG), 1, modeRecompensable[Elo.FRAG]),
					    RangMaxElo(elo.getEloModeByMode(Elo.KILL), 1, modeRecompensable[Elo.KILL]),
					    RangMaxElo(elo.getEloModeByMode(Elo.CROSS), 1, modeRecompensable[Elo.CROSS])
				});
				
				serveur.Bdd.BD.createStatement().execute("UPDATE `$joueur` SET elo_flag='" + elo_flag + "' WHERE id=" + IdJoueur);
			}
		} catch (Exception e) {
			System.err.println("3XC Attribuer"); e.printStackTrace();
		}
	}
	
	private static void NettoyerAnciennesClassees() {
		try {
			// Clean des récompenses précédentes
			Serveur.getServeur().Bdd.BD.createStatement().executeUpdate("UPDATE `$joueur` SET elo_flag='000'");
		} catch (Exception e) {
			System.err.println("3XC Attribuer"); e.printStackTrace();
		}
	}
	
	private static int getMaxTab(int t[]) {
		int m = t[0];
		for (int i = 1; i < t.length; ++i) {
			m = Math.max(t[i], m);
		}
		
		return m;
	}
	
	private static int RangMaxElo(EloMode eloMode, int nbJoueursMinParEq, boolean tabRecomp[]) {
		int rangMax = 0; // 3 = or ; 2 = argent ; 1 = bronze ; 0 = rien
		
		if (nbJoueursMinParEq < 2 && tabRecomp[0]) { // Mode 1 vs 1 existant & récompensable
			if (eloMode.pj_i > MIN_A_DEPASSER_RECOMPENSABLE) {
				if (eloMode.elo_i > SEUIL_OR) { // Or
					rangMax = 3;
				} else if (rangMax < 2 && eloMode.elo_i > SEUIL_ARGENT) { // Argent
					rangMax = 2;
				} else if (rangMax < 1) { // Bronze
					rangMax = 1;
				}
			}
		}
		
		if (nbJoueursMinParEq < 3 && tabRecomp[1]) { // Mode 2 vs 2 existant & récompensable
			if (eloMode.pj_d > MIN_A_DEPASSER_RECOMPENSABLE) {
				if (eloMode.elo_d > SEUIL_OR) {
					rangMax = 3;
				} else if (rangMax < 2 && eloMode.elo_d > SEUIL_ARGENT) {
					rangMax = 2;
				} else if (rangMax < 1){
					rangMax = 1;
				}
			}
		}
		
		 // Mode multi existant & récompensable
		if (tabRecomp[2]) {
			if (eloMode.pj_m > MIN_A_DEPASSER_RECOMPENSABLE) {
				if (eloMode.elo_m > SEUIL_OR) {
					rangMax = 3;
				} else if (rangMax < 2 && eloMode.elo_m > SEUIL_ARGENT) {
					rangMax = 2;
				} else if (rangMax < 3){
					rangMax = 1;
				}
			}
		}
		
		
		return rangMax;
	}
	
	private static boolean ModeRecompensable(Serveur serveur, String Jeu, byte TYPE, int nbJoueursParEq) {
		String debut = Elo.getSuffixeEloMode(TYPE) + "_";
		String Requete;
		
		if (nbJoueursParEq == 1) {
			debut += "i";
			Requete = "SELECT SUM(" + debut + "pj) FROM `$elo_" + Jeu + "` WHERE "  + debut + "pj > " + MIN_A_DEPASSER_RECOMPENSABLE;
		} else if (nbJoueursParEq == 2){
			debut += "d";
			Requete = "SELECT SUM(" + debut + "pj) FROM `$elo_" + Jeu + "` WHERE "  + debut + "pj > " + MIN_A_DEPASSER_RECOMPENSABLE;
		} else if (nbJoueursParEq == Elo.FORMAT_TEAM) {
			Requete = "SELECT SUM(" + debut + "pj) FROM `$elo_t_" + Jeu + " WHERE "  + debut + "pj > " + MIN_A_DEPASSER_RECOMPENSABLE;
		} else {
			debut += "m";
			Requete = "SELECT SUM(" + debut + "pj) FROM `$elo_" + Jeu + "` WHERE "  + debut + "pj > " + MIN_A_DEPASSER_RECOMPENSABLE;
		}
		
		try {
			ResultSet res = serveur.Bdd.BD.createStatement().executeQuery(Requete);
			if (res.next()) {
				if (nbJoueursParEq != Elo.FORMAT_TEAM) {
					return Math.floor(res.getInt(1) / (nbJoueursParEq * 2)) >= 50;
				} else {
					return Math.floor(res.getInt(1) / 2) >= 50; // TODO : fixer limite team (à discuter modé)
				}
			}
		} catch (Exception e) {
			System.err.println("3XC ModeRecompensable : " + Requete); e.printStackTrace();
		}
		
		return false;
	}
	
	
	public static void ArchiverElo() {
		try {
			// Update des personnes qui avaient déjà de l'elo
			Serveur.getServeur().Bdd.Requete.executeUpdate("UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ra_iv = `$stat_elo_a`.ra_iv + `$elo_a`.ra_iv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ra_ipj = `$stat_elo_a`.ra_ipj + `$elo_a`.ra_ipj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ra_dv = `$stat_elo_a`.ra_dv + `$elo_a`.ra_dv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ra_dpj = `$stat_elo_a`.ra_dpj + `$elo_a`.ra_dpj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ra_mv = `$stat_elo_a`.ra_mv + `$elo_a`.ra_mv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ra_mpj = `$stat_elo_a`.ra_mpj + `$elo_a`.ra_mpj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.def_iv = `$stat_elo_a`.def_iv + `$elo_a`.def_iv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.def_mv = `$stat_elo_a`.def_mv + `$elo_a`.def_mv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.def_dv = `$stat_elo_a`.def_dv + `$elo_a`.def_dv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.def_ipj = `$stat_elo_a`.def_ipj + `$elo_a`.def_ipj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.def_dpj = `$stat_elo_a`.def_dpj + `$elo_a`.def_dpj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.def_mpj = `$stat_elo_a`.def_mpj + `$elo_a`.def_mpj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ms_dv = `$stat_elo_a`.ms_dv + `$elo_a`.ms_dv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ms_mv = `$stat_elo_a`.ms_mv + `$elo_a`.ms_mv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ms_dpj = `$stat_elo_a`.ms_dpj + `$elo_a`.ms_dpj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.ms_mpj = `$stat_elo_a`.ms_mpj + `$elo_a`.ms_mpj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.fs_mv = `$stat_elo_a`.fs_mv + `$elo_a`.fs_mv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.fs_mpj = `$stat_elo_a`.fs_mpj + `$elo_a`.fs_mpj;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.run_mv = `$stat_elo_a`.run_mv + `$elo_a`.run_mv;"
					+ "UPDATE `$stat_elo_a` INNER JOIN `$elo_a` ON `$stat_elo_a`.id = `$elo_a`.id SET `$stat_elo_a`.run_mpj = `$stat_elo_a`.run_mpj + `$elo_a`.run_mpj;"

					+ "UPDATE `$stat_elo_b` INNER JOIN `$elo_b` ON `$stat_elo_b`.id = `$elo_b`.id SET `$stat_elo_b`.k_iv = `$stat_elo_b`.k_iv + `$elo_b`.k_iv;"
					+ "UPDATE `$stat_elo_b` INNER JOIN `$elo_b` ON `$stat_elo_b`.id = `$elo_b`.id SET `$stat_elo_b`.k_ipj = `$stat_elo_b`.k_ipj + `$elo_b`.k_ipj;"
					+ "UPDATE `$stat_elo_b` INNER JOIN `$elo_b` ON `$stat_elo_b`.id = `$elo_b`.id SET `$stat_elo_b`.k_dv = `$stat_elo_b`.k_dv + `$elo_b`.k_dv;"
					+ "UPDATE `$stat_elo_b` INNER JOIN `$elo_b` ON `$stat_elo_b`.id = `$elo_b`.id SET `$stat_elo_b`.k_dpj = `$stat_elo_b`.k_dpj + `$elo_b`.k_dpj;"
					+ "UPDATE `$stat_elo_b` INNER JOIN `$elo_b` ON `$stat_elo_b`.id = `$elo_b`.id SET `$stat_elo_b`.k_mv = `$stat_elo_b`.k_mv + `$elo_b`.k_mv;"
					+ "UPDATE `$stat_elo_b` INNER JOIN `$elo_b` ON `$stat_elo_b`.id = `$elo_b`.id SET `$stat_elo_b`.k_mpj = `$stat_elo_b`.k_mpj + `$elo_b`.k_mpj;"

					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fra_iv = `$stat_elo_f`.fra_iv + `$elo_f`.fra_iv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fra_mv = `$stat_elo_f`.fra_mv + `$elo_f`.fra_mv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fra_dv = `$stat_elo_f`.fra_dv + `$elo_f`.fra_dv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fra_ipj = `$stat_elo_f`.fra_ipj + `$elo_f`.fra_ipj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fra_dpj = `$stat_elo_f`.fra_dpj + `$elo_f`.fra_dpj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fra_mpj = `$stat_elo_f`.fra_mpj + `$elo_f`.fra_mpj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fri_iv = `$stat_elo_f`.fri_iv + `$elo_f`.fri_iv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fri_mv = `$stat_elo_f`.fri_mv + `$elo_f`.fri_mv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fri_dv = `$stat_elo_f`.fri_dv + `$elo_f`.fri_dv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fri_ipj = `$stat_elo_f`.fri_ipj + `$elo_f`.fri_ipj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fri_dpj = `$stat_elo_f`.fri_dpj + `$elo_f`.fri_dpj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.fri_mpj = `$stat_elo_f`.fri_mpj + `$elo_f`.fri_mpj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.k_iv = `$stat_elo_f`.k_iv + `$elo_f`.k_iv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.k_mv = `$stat_elo_f`.k_mv + `$elo_f`.k_mv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.k_dv = `$stat_elo_f`.k_dv + `$elo_f`.k_dv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.k_ipj = `$stat_elo_f`.k_ipj + `$elo_f`.k_ipj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.k_dpj = `$stat_elo_f`.k_dpj + `$elo_f`.k_dpj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.k_mpj = `$stat_elo_f`.k_mpj + `$elo_f`.k_mpj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.crs_iv = `$stat_elo_f`.crs_iv + `$elo_f`.crs_iv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.crs_ipj = `$stat_elo_f`.crs_ipj + `$elo_f`.crs_ipj;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.crs_dv = `$stat_elo_f`.crs_dv + `$elo_f`.crs_dv;"
					+ "UPDATE `$stat_elo_f` INNER JOIN `$elo_f` ON `$stat_elo_f`.id = `$elo_f`.id SET `$stat_elo_f`.crs_dpj = `$stat_elo_f`.crs_dpj + `$elo_f`.crs_dpj;"

					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.ra_v = `$stat_elo_t_a`.ra_v + `$elo_t_a`.ra_v;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.ra_pj = `$stat_elo_t_a`.ra_pj + `$elo_t_a`.ra_pj;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.def_v = `$stat_elo_t_a`.def_v + `$elo_t_a`.def_v;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.def_pj = `$stat_elo_t_a`.def_pj + `$elo_t_a`.def_pj;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.ms_v = `$stat_elo_t_a`.ms_v + `$elo_t_a`.ms_v;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.ms_pj = `$stat_elo_t_a`.ms_pj + `$elo_t_a`.ms_pj;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.fs_v = `$stat_elo_t_a`.fs_v + `$elo_t_a`.fs_v;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.fs_pj = `$stat_elo_t_a`.fs_pj + `$elo_t_a`.fs_pj;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.run_v = `$stat_elo_t_a`.run_v + `$elo_t_a`.run_v;"
					+ "UPDATE `$stat_elo_t_a` INNER JOIN `$elo_t_a` ON `$stat_elo_t_a`.id = `$elo_t_a`.id SET `$stat_elo_t_a`.run_pj = `$stat_elo_t_a`.run_pj + `$elo_t_a`.run_pj;"

					+ "UPDATE `$stat_elo_t_b` INNER JOIN `$elo_t_b` ON `$stat_elo_t_b`.id = `$elo_t_b`.id SET `$stat_elo_t_b`.k_v = `$stat_elo_t_b`.k_v + `$elo_t_b`.k_v;"
					+ "UPDATE `$stat_elo_t_b` INNER JOIN `$elo_t_b` ON `$stat_elo_t_b`.id = `$elo_t_b`.id SET `$stat_elo_t_b`.k_dpj = `$stat_elo_t_b`.k_dpj + `$elo_t_b`.k_dpj;"

					+ "UPDATE `$stat_elo_t_f` INNER JOIN `$elo_t_f` ON `$stat_elo_t_f`.id = `$elo_t_f`.id SET `$stat_elo_t_f`.fri_v = `$stat_elo_t_f`.fri_v + `$elo_t_f`.fri_v;"
					+ "UPDATE `$stat_elo_t_f` INNER JOIN `$elo_t_f` ON `$stat_elo_t_f`.id = `$elo_t_f`.id SET `$stat_elo_t_f`.fri_pj = `$stat_elo_t_f`.fri_pj + `$elo_t_f`.fri_pj;"
					+ "UPDATE `$stat_elo_t_f` INNER JOIN `$elo_t_f` ON `$stat_elo_t_f`.id = `$elo_t_f`.id SET `$stat_elo_t_f`.fra_v = `$stat_elo_t_f`.fra_v + `$elo_t_f`.fra_v;"
					+ "UPDATE `$stat_elo_t_f` INNER JOIN `$elo_t_f` ON `$stat_elo_t_f`.id = `$elo_t_f`.id SET `$stat_elo_t_f`.fra_pj = `$stat_elo_t_f`.fra_pj + `$elo_t_f`.fra_pj;"
					+ "UPDATE `$stat_elo_t_f` INNER JOIN `$elo_t_f` ON `$stat_elo_t_f`.id = `$elo_t_f`.id SET `$stat_elo_t_f`.k_v = `$stat_elo_t_f`.k_v + `$elo_t_f`.k_v;"
					+ "UPDATE `$stat_elo_t_f` INNER JOIN `$elo_t_f` ON `$stat_elo_t_f`.id = `$elo_t_f`.id SET `$stat_elo_t_f`.k_pj = `$stat_elo_t_f`.k_pj + `$elo_t_f`.k_pj;");
			
			// Update des personnes qui n'avaient pas déjà de l'elo
			Serveur.getServeur().Bdd.Requete.executeUpdate("INSERT INTO `$stat_elo_a` SELECT * FROM `$elo_a` e WHERE NOT EXISTS(SELECT * FROM `$stat_elo_a` WHERE id = e.id);"
					+ "INSERT INTO `$stat_elo_b` SELECT * FROM `$elo_b` e WHERE NOT EXISTS(SELECT * FROM `$stat_elo_b` WHERE id = e.id);"
					+ "INSERT INTO `$stat_elo_f` SELECT * FROM `$elo_f` e WHERE NOT EXISTS(SELECT * FROM `$stat_elo_f` WHERE id = e.id);");
			
			System.out.println("C0M Stats Elo archivées.");
		} catch (Exception e) {
			
		}
	}
	
	public void setRecompensesByEloFlag(String elo_flag) {
		recompenseAaaah = "" + elo_flag.charAt(0);
		recompenseBoum = "" + elo_flag.charAt(1);
		recompenseForto = "" + elo_flag.charAt(2);
	}
	
	public String getRecompenseBouboum() {
		return (recompensesActives == ACTIVER ? recompenseBoum : "0");
	}
	
	public String getRecompenseAaaah() {
		return (recompensesActives == ACTIVER ? recompenseAaaah : "0");
	}
	
	public String getRecompenseForto() {
		return (recompensesActives == ACTIVER ? recompenseForto : "0");
	}
}
