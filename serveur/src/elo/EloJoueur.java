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

public class EloJoueur extends Elo {
	public EloJoueur(Serveur serveur, int IdJoueur, String NomJoueur, boolean CreerSiInexistant, String JeuACreer) {
		this(serveur, IdJoueur, NomJoueur, CreerSiInexistant, JeuACreer, false);
	}
	
	public EloJoueur(Serveur serveur, int IdJoueur, String NomJoueur, boolean CreerSiInexistant, String JeuACreer, boolean EloPrecedent) {
		super(IdJoueur);
		
		PreparedStatement Requete_Aaaah;
		PreparedStatement Requete_Boum;
		PreparedStatement Requete_Forto;
		
		if (EloPrecedent) {
			Requete_Aaaah = serveur.Bdd.Requete_Precedent_Elo_Aaaah;
			Requete_Boum = serveur.Bdd.Requete_Precedent_Elo_Bouboum;
			Requete_Forto = serveur.Bdd.Requete_Precedent_Elo_Forto;
		} else {
			Requete_Aaaah = serveur.Bdd.Requete_Elo_Aaaah;
			Requete_Boum = serveur.Bdd.Requete_Elo_Bouboum;
			Requete_Forto = serveur.Bdd.Requete_Elo_Forto;
		}
		
		try {
			Requete_Aaaah.setInt(1, IdJoueur);
			Requete_Boum.setInt(1, IdJoueur);
			Requete_Forto.setInt(1, IdJoueur);
			
			ResultSet resultat;
			
			// Aaaah
			resultat = Requete_Aaaah.executeQuery();
			if (resultat.next()) {
				Aaaah_Rally.InitJoueur(resultat, "ra");
				Aaaah_Def.InitJoueur(resultat, "def");
				Aaaah_MS.InitJoueur(resultat, "ms", false, true, true);
				Aaaah_FS.InitJoueur(resultat, "fs", false, false, true);
				Aaaah_Run.InitJoueur(resultat, "run", false, false, true);
			} else if (CreerSiInexistant && JeuACreer.equals("a")) {
				CreerElo(JeuACreer, serveur, IdJoueur);
				
				Aaaah_Rally.Init(true, true, true);
				Aaaah_Def.Init(true, true, true);
				Aaaah_MS.Init(false, true, true);
				Aaaah_FS.Init(false, false, true);
				Aaaah_Run.Init(false, false, true);
			}
			
			
			// Bouboum
			resultat = Requete_Boum.executeQuery();
			if (resultat.next()) {
				Bouboum.InitJoueur(resultat, "k");
			} else if (CreerSiInexistant && JeuACreer.equals("b")) {
				CreerElo(JeuACreer, serveur, IdJoueur);
				
				Bouboum.Init(true, true, true);
			}
			
			// Forteresse
			resultat = Requete_Forto.executeQuery();
			if (resultat.next()) {
				Forto_Frigo.InitJoueur(resultat, "fri");
				Forto_Frag.InitJoueur(resultat, "fra");
				Forto_Kill.InitJoueur(resultat, "k");
				Forto_Cross.InitJoueur(resultat, "crs", true, true, false);
			} else if (CreerSiInexistant && JeuACreer.equals("f")) {
				CreerElo(JeuACreer, serveur, IdJoueur);
				
				Forto_Frigo.Init(true, true, true);
				Forto_Frag.Init(true, true, true);
				Forto_Kill.Init(true, true, true);
				Forto_Cross.Init(true, true, false);
			}
			
		} catch(Exception e) {
			System.err.println("3X Elo(" + NomJoueur + ") : ");e.printStackTrace();
		}
	}
	
	private void CreerElo(String jeu, Serveur serveur, int IdJoueur) {
		jeu = jeu.toLowerCase();
		
		try {
			if (jeu.equals("a")) {
				serveur.Bdd.Requete_CreerEloAaaah.setInt(1, IdJoueur);
				serveur.Bdd.Requete_CreerEloAaaah.execute();
			} else if (jeu.equals("b")) {
				serveur.Bdd.Requete_CreerEloBouboum.setInt(1, IdJoueur);
				serveur.Bdd.Requete_CreerEloBouboum.execute();
			} else if (jeu.equals("f")) {
				serveur.Bdd.Requete_CreerEloForto.setInt(1, IdJoueur);
				serveur.Bdd.Requete_CreerEloForto.execute();
			}
		} catch (Exception e) {
			System.err.println("3X CreerElo(" + jeu + ", " + IdJoueur + " ) : ");e.printStackTrace();
		}
	}

	public static EloJoueur[] getEloByPseudos(String[] Pseudos, String Jeu) {
		Serveur serveur = Serveur.getServeur();
		EloJoueur[] Elos = new EloJoueur[Pseudos.length];
		ResultSet res;
		int IdJoueur = 0;
		
		for (int i = Elos.length - 1; i >= 0; --i) {
			
			try {
				serveur.Bdd.Requete_IdJoueur.setString(1, Pseudos[i]);
				res = serveur.Bdd.Requete_IdJoueur.executeQuery();
				
				if (res.next()) {
					IdJoueur = res.getInt(1);
					Elos[i] = new EloJoueur(serveur, IdJoueur, Pseudos[i], true, Jeu);
				} else {
					return null;
				}
			} catch (Exception e) {
				System.err.println("3X getEloByPseudos(" + Pseudos[i] + ") : ");e.printStackTrace();
			}
		}
		
		return Elos;
	}
	
	public static void CommandeElo(String CHAINE, Joueur J) {
		/*
		  À utiliser via la Boite 
		*/
		// /celo (J1_1, J1_2, ...) (J2_1, J2_2, ...) {a, b, f} Mode {Victoire : 0=Equipe 1, 1=Equipe 2}
		String[] Pseudos = CHAINE.split("\\(");
		if (Pseudos.length != 3) {
			J.Envoie("CxINFO#Commande invalide. Tapez /celo pour afficher le format.");
			return;
		}
		
		boolean VictoireEqp1;
		String[] PseudosEquipe1 = Pseudos[1].replace(" ", "").split(",");
		String[] PseudosEquipe2 = Pseudos[2].split("\\)")[0].replace(" ", "").split(",");
		
		String[] Param = CHAINE.split(" ");
		
		// <Jeu>, <Mode>
		
		String Jeu = Param[Param.length - 3];
		String ModeStr = Param[Param.length - 2];
		
		// <PseudosEquipeX>
		
		PseudosEquipe1[PseudosEquipe1.length - 1] = PseudosEquipe1[PseudosEquipe1.length - 1].substring(0, PseudosEquipe1[PseudosEquipe1.length - 1].length() - 1);
		
		if (PseudosEquipe1.length != PseudosEquipe2.length) {
			J.Envoie("CxINFO#Le nombre de joueurs est différent dans chaque équipe (" + PseudosEquipe1.length
					+ " vs " + PseudosEquipe2.length + ").");
			return;
		}
		
		// <VictoireEqp1>
		byte VICTOIRE;
		try {
			VICTOIRE = Byte.valueOf(Param[Param.length - 1]);
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
		EloJoueur[] EloModeEq1 = getEloByPseudos(PseudosEquipe1, Jeu);
		EloJoueur[] EloModeEq2 = getEloByPseudos(PseudosEquipe2, Jeu);
		byte Mode = convertStringToMode(Jeu, ModeStr);
		
		if (EloModeEq1 == null || EloModeEq2 == null || Mode == -1) {
			J.Envoie("CxINFO#Commande invalide. Tapez /celo pour afficher le format.");
			return;
		}
		
		// <DifferenceElo>
		double DifferenceElo;
		
		DifferenceElo = calcDifferenceElo(EloModeEq1, EloModeEq2, Mode, false);
		//
		
		for (int i = EloModeEq1.length - 1; i >= 0; --i) {
			EloMode eloMode = EloModeEq1[i].getEloModeByMode(Mode);
			int oldElo = getElo(eloMode, PseudosEquipe1.length);
			Calculer(eloMode, VictoireEqp1, DifferenceElo, PseudosEquipe1.length);
			UpdateElo(Jeu, Mode, eloMode, EloModeEq1[i].getID(), PseudosEquipe1.length);
			
			System.out.println("UPDELO " + J.NomJoueur + " [" + Jeu + " - " + ModeStr + "] " + PseudosEquipe1[i] + " " + oldElo + " -> " + getElo(eloMode, PseudosEquipe1.length) + " [" + numMatch + "]");
			
			eloMode = EloModeEq2[i].getEloModeByMode(Mode);
			oldElo = getElo(eloMode, PseudosEquipe1.length);
			Calculer(eloMode, !VictoireEqp1, -DifferenceElo, PseudosEquipe1.length);
			UpdateElo(Jeu, Mode, eloMode, EloModeEq2[i].getID(), PseudosEquipe1.length);
			
			System.out.println("UPDELO " + J.NomJoueur + " [" + Jeu + " - " + ModeStr + "] " + PseudosEquipe2[i] + " " + oldElo + " -> " + getElo(eloMode, PseudosEquipe1.length) + " [" + numMatch + "]");
		}
		
		J.Envoie("CxINFO#Update des joueurs effectué (code : " + numMatch + ") :\n"
				+ " - [" + (VictoireEqp1?"V":"D") + "] " + String.join(", ", PseudosEquipe1) + "\n"
				+ " - [" + (!VictoireEqp1?"V":"D") + "] " + String.join(", ", PseudosEquipe2) + "\n");
		
		Elo.aEteModif();
		
		numMatch++;
	}
}
