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

import joueur.Joueur;
import serveur.Serveur;

public class EloTeam extends Elo {
	private EloTeam(Serveur serveur, int IdTeam, boolean CreerSiInexistant, String JeuACreer) {
		super(IdTeam);
		
		try {
			serveur.Bdd.Requete_EloTeam_Aaaah.setInt(1, IdTeam);
			serveur.Bdd.Requete_EloTeam_Bouboum.setInt(1, IdTeam);
			serveur.Bdd.Requete_EloTeam_Forto.setInt(1, IdTeam);
			
			ResultSet resultat;
			
			// Aaaah
			resultat = serveur.Bdd.Requete_EloTeam_Aaaah.executeQuery();
			if (resultat.next()) {
				Aaaah_Rally.InitTeam(resultat, "ra");
				Aaaah_Def.InitTeam(resultat, "def");
				Aaaah_MS.InitTeam(resultat, "ms");
				Aaaah_FS.InitTeam(resultat, "fs");
				Aaaah_Run.InitTeam(resultat, "run");
			} else if (CreerSiInexistant && JeuACreer.equals("a")) {
				CreerElo(JeuACreer, serveur, IdTeam);
				
				Aaaah_Rally.Init(true, true, true);
				Aaaah_Def.Init(true, true, true);
				Aaaah_MS.Init(false, true, true);
				Aaaah_FS.Init(false, false, true);
				Aaaah_Run.Init(false, false, true);
			}
			
			
			// Bouboum
			resultat = serveur.Bdd.Requete_EloTeam_Bouboum.executeQuery();
			if (resultat.next()) {
				Bouboum.InitTeam(resultat, "k");
			} else if (CreerSiInexistant && JeuACreer.equals("b")) {
				CreerElo(JeuACreer, serveur, IdTeam);
				
				Bouboum.Init(true, true, true);
			}
			
			// Forteresse
			resultat = serveur.Bdd.Requete_EloTeam_Forto.executeQuery();
			if (resultat.next()) {
				Forto_Frigo.InitTeam(resultat, "fri");
				Forto_Frag.InitTeam(resultat, "fra");
				Forto_Kill.InitTeam(resultat, "k");
				//Forto_Cross.InitTeam(resultat, "crs");
			} else if (CreerSiInexistant && JeuACreer.equals("f")) {
				CreerElo(JeuACreer, serveur, IdTeam);
				
				Forto_Frigo.Init(true, true, true);
				Forto_Frag.Init(true, true, true);
				Forto_Kill.Init(true, true, true);
				//Forto_Cross.Init(true, false, false);
			}
			
		} catch(Exception e) {
			System.err.println("3X Elo(" + IdTeam + ") : ");e.printStackTrace();
		}
	}
	

	private void CreerElo(String jeu, Serveur serveur, int IdJoueur) {
		jeu = jeu.toLowerCase();
		
		try {
			if (jeu.equals("a")) {
				serveur.Bdd.Requete_CreerEloTeamAaaah.setInt(1, IdJoueur);
				serveur.Bdd.Requete_CreerEloTeamAaaah.execute();
			} else if (jeu.equals("b")) {
				serveur.Bdd.Requete_CreerEloTeamBouboum.setInt(1, IdJoueur);
				serveur.Bdd.Requete_CreerEloTeamBouboum.execute();
			} else if (jeu.equals("f")) {
				serveur.Bdd.Requete_CreerEloTeamForto.setInt(1, IdJoueur);
				serveur.Bdd.Requete_CreerEloTeamForto.execute();
			}
		} catch (Exception e) {
			System.err.println("3X CreerElo(" + jeu + ", " + IdJoueur + ") : ");e.printStackTrace();
		}
	}
	
	public static void CommandeElo(String CHAINE, Joueur J) {
		/*
		  À utiliser via la Boite 
		*/
		
		Serveur serveur = Serveur.getServeur();
		
		// /celo_t IdTeam1 IdTeam2 {a, b, f} Mode {Victoire : 0=Equipe 1, 1=Equipe 2}
		String[] Infos = CHAINE.split(" ");
		
		if (Infos.length != 6) {
			J.Envoie("CxINFO#Nombre de paramètres incorrect.");
			return;
		}
		
		boolean VictoireEqp1;
		int IdTeam1 = Integer.valueOf(Infos[1]);
		int IdTeam2 = Integer.valueOf(Infos[2]);
		
		// <Jeu>, <Mode>
		
		String Jeu = Infos[3];
		String ModeStr = Infos[4];
		
		// <VictoireEqp1>
		byte VICTOIRE;
		try {
			VICTOIRE = Byte.valueOf(Infos[5]);
		} catch (Exception e) {
			J.Envoie("CxINFO#Votre paramètre victoire doit valoir soit 1 soit 0.");
			return;
		}
		
		if (VICTOIRE != 0 && VICTOIRE != 1) {
			J.Envoie("CxINFO#Votre paramètre victoire doit valoir soit 1 soit 0.");
			return;
		}
		
		VictoireEqp1 = (VICTOIRE == 0);
		
		// Obtention des elo indiv et du mode
		EloTeam[] EloModeEq1 = new EloTeam[]{new EloTeam(serveur, IdTeam1, true, Jeu)};
		EloTeam[] EloModeEq2 = new EloTeam[]{new EloTeam(serveur, IdTeam2, true, Jeu)};
		byte Mode = convertStringToMode(Jeu, ModeStr);
		
		//System.out.println("Teams : [" + IdTeam1 +", "+IdTeam2+"]\n Jeu & Mode : " + Jeu + " (" + Mode + " )\n EloMode : ");
		
		if (EloModeEq1 == null || EloModeEq2 == null || Mode == -1) {
			J.Envoie("CxINFO#Commande invalide. Tapez /celo_t pour afficher le format.");
			return;
		}
		
		// <DifferenceElo>
		double DifferenceElo;
		
		
		DifferenceElo = calcDifferenceElo(EloModeEq1, EloModeEq2, Mode, true);
		//
		
		EloMode eloMode = EloModeEq1[0].getEloModeByMode(Mode);
		int oldElo = getElo(eloMode, Elo.FORMAT_TEAM);
		
		Calculer(eloMode, VictoireEqp1, DifferenceElo, Elo.FORMAT_TEAM);
		UpdateElo(Jeu, Mode, eloMode, IdTeam1, Elo.FORMAT_TEAM);
		System.out.println("UPDELO " + J.NomJoueur + " [" + Jeu + " - " + ModeStr + "] " + IdTeam1 + " " + oldElo + " -> " + getElo(eloMode, FORMAT_TEAM) + " [" + numMatch + "]");
		
		eloMode = EloModeEq2[0].getEloModeByMode(Mode);
		oldElo = getElo(eloMode, Elo.FORMAT_TEAM);
		
		Calculer(eloMode, !VictoireEqp1, -DifferenceElo, Elo.FORMAT_TEAM);
		UpdateElo(Jeu, Mode, eloMode, IdTeam2, Elo.FORMAT_TEAM);
		System.out.println("UPDELO " + J.NomJoueur + " [" + Jeu + " - " + ModeStr + "] " + IdTeam2 + " " + oldElo + " -> " + getElo(eloMode, FORMAT_TEAM) + " [" + numMatch + "]");
		
		J.Envoie("CxINFO#Update des joueurs effectué (code : " + numMatch + ") :\n"
				+ " - [" + (VictoireEqp1?"V":"D") + "] " + IdTeam1 + "\n"
				+ " - [" + (!VictoireEqp1?"V":"D") + "] " + IdTeam2 + "\n");
		
		Elo.aEteModif();
		
		numMatch++;
	}
}
