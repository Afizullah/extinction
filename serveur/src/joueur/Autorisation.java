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

/**
 * Flags suppr :
 * 
 *  30 (AutorisationRespoTriTeam)
 *  6 (AutorisationHelper ; CxARBM)
 *  20 (AutorisationRecrue ; CxARBREC)
 *
 */

public class Autorisation {
	private final short MODERATION_RECR_FORUM = 1;
	private final short MODERATION_MODO_FORUM = 2;
	private final short MODERATION_ARBITRE = 4;
	private final short MODERATION_ARBITRE_PLUS = 8;
	private final short MODERATION_MODO_JEU = 16;
	private final short MODERATION_ADMIN = 32;
	private final short MODERATION_MODO_JEU_PUBLIC = 64;
	private final short MODERATION_MODO_FORUM_PUBLIC = 128;
	private final short MODERATION_ADMIN_PUBLIC = 256;
	
	private short ModerationFlag;
	
	private final short TOURNOI_FORTO = 1;
	private final short TOURNOI_AAAAH = 2;
	private final short TOURNOI_FILMEUR = 4;
	private final short TOURNOI_ARBITRE_SECONDAIRE = 8;
	private final short TOURNOI_MODO_MODE_IT = 16;
	
	private short TournoiFlag;
	
	private final short GENERAL_RESPO_TEAM = 1;
	private final short GENERAL_RECR_RESPOMAP_AAAAH = 2;
	private final short GENERAL_RESPOMAP_AAAAH = 4;
	private final short GENERAL_RESPOMAP_FORTO = 8;
	private final short GENERAL_BETA_TESTEUR = 16;
	private final short GENERAL_ANIMATEUR = 32;
	private final short GENERAL_ANIMATEUR_SUPERVISEUR = 64;
	private final short GENERAL_RESPO_ELO = 128;
	
	private short GeneralFlag;
	
	private short EnJeuFlag; // Flag temporaire relatif au jeu
	
	public Autorisation(String Flags) {
		InitFlags(Flags);
		EnJeuFlag = 0;
	}
	
	private void InitFlags(String Flags) {
		String[] LsFlags = Flags.split(",");
		
		ModerationFlag = 0;
		TournoiFlag = 0;
		GeneralFlag = 0;
		
		for (int i = 0; i < LsFlags.length; ++i) {
			try {
				switch (Integer.valueOf(LsFlags[i])) {
				 	case 1 :
				 		ModerationFlag |= MODERATION_MODO_FORUM;
				 		break;
				 	case 2 :
				 		ModerationFlag |= MODERATION_ARBITRE;
				 		break;
				 	case 3 :
				 		GeneralFlag |= GENERAL_RESPO_TEAM;
				 		break;
				 	case 4 :
				 		GeneralFlag |= GENERAL_RESPOMAP_AAAAH;
				 		break;
				 	case 5 :
						ModerationFlag |= MODERATION_MODO_JEU;
						break;
				 	case 7 :
				 		TournoiFlag |= TOURNOI_FORTO;
				 		break;
				 	case 8 :
				 		TournoiFlag |= MODERATION_ARBITRE_PLUS;
						break;
				 	case 9 :
				 		TournoiFlag |= TOURNOI_AAAAH;
				 		break;
				 	case 10 :
						ModerationFlag |= MODERATION_ADMIN;
						break;
				 	case 11 :
						ModerationFlag |= MODERATION_RECR_FORUM;
						break;
				 	case 12 :
				 		ModerationFlag |= (MODERATION_MODO_JEU | MODERATION_MODO_FORUM);
				 		break;
				 	case 13 :
				 		GeneralFlag |= GENERAL_BETA_TESTEUR;
				 		break;
				 	case 14 :
				 		TournoiFlag |= TOURNOI_FILMEUR;
				 		break;
				 	case 15 :
				 		GeneralFlag |= GENERAL_RECR_RESPOMAP_AAAAH;
				 		break;
				 	case 16 :
				 		GeneralFlag |= GENERAL_RESPO_ELO;
				 		break;
				 	case 40 :
				 		GeneralFlag |= GENERAL_RESPOMAP_FORTO;
				 		break;
				 	case 50 :
				 		ModerationFlag |= MODERATION_MODO_JEU_PUBLIC;
				 		break;
				 	case 60 :
				 		GeneralFlag |= GENERAL_ANIMATEUR;
						break;
				 	case 70 :
				 		GeneralFlag |= GENERAL_ANIMATEUR_SUPERVISEUR;
				 		break;
				 	case 80 :
				 		ModerationFlag |= MODERATION_MODO_FORUM_PUBLIC;
						break;
				}
			} catch (Exception e) {} // Catch les erreurs de valueOf()
		}
	}
	
	public static void displayCommandeFlag(Joueur J) {
		J.Envoie("CxINFO#"
				+ "1=modo forum\n"
				+ "2=arbitre\n"
				+ "3=respoteam\n"
				+ "4=respomap aaaah\n"
				+ "5=modo\n"
				+ "7=arbitre tournoi forto\n"
				+ "8=super arbitre\n"
				+ "9=arbitre tournoi aaaah\n"
				+ "10=admin\n"
				+ "11=recrue modo forum\n"
				+ "13=beta tester\n"
				+ "14=filmeur\n"
				+ "15=respomap aaaah (recrue)\n"
				+ "16=gestionnaire elo\n"
				+ "40=respomap forto\n"
				+ "50=modo public\n"
				+ "60=animateur\n"
				+ "70=animateur superviseur\n"
				+ "80=modo forum public"
				);
	}
	
	/** Modération **/
	
	public boolean MembreModeration() {
		return ModerationFlag != 0;
	}
	
	public boolean EstAdmin() {
		return (ModerationFlag & (MODERATION_ADMIN | MODERATION_ADMIN_PUBLIC)) != 0;
	}
	
	public boolean EstModoJeu(boolean OuPlus) {
		if (!OuPlus) {
			return (ModerationFlag & (MODERATION_MODO_JEU | MODERATION_MODO_JEU_PUBLIC)) != 0;
		}
		
		return ((ModerationFlag & (MODERATION_MODO_JEU | MODERATION_MODO_JEU_PUBLIC)) != 0) || EstAdmin();
	}
	
	public boolean EstArbitreJeu(boolean OuPlus) {
		if (!OuPlus) {
			return (ModerationFlag & MODERATION_ARBITRE) != 0;
		}
		
		return (ModerationFlag & (MODERATION_ARBITRE | MODERATION_ARBITRE_PLUS)) != 0 || EstModoJeu(true);
	}
	
	public boolean EstModoForum(boolean OuPlus) {
		if (!OuPlus) {
			return (ModerationFlag & (MODERATION_MODO_FORUM | MODERATION_MODO_FORUM_PUBLIC)) != 0;
		}
		
		return ((ModerationFlag & (MODERATION_MODO_FORUM | MODERATION_MODO_FORUM_PUBLIC)) != 0) || EstModoJeu(true);
	}
	
	public boolean EstRecrueForum(boolean OuPlus) {
		if (!OuPlus) {
			return (ModerationFlag & MODERATION_RECR_FORUM) != 0;
		}
		
		return (ModerationFlag & MODERATION_RECR_FORUM) != 0 || EstModoForum(true);
	}
	
	public boolean EstMembreModerationPublic() {
		return (ModerationFlag & (MODERATION_MODO_JEU_PUBLIC | MODERATION_MODO_FORUM_PUBLIC | MODERATION_ADMIN_PUBLIC)) != 0;
	}
	
	/** Tournoi **/
	
	public boolean EstArbitreTournoi(boolean OuPlus) {
		if (!OuPlus) {
			return (TournoiFlag & (TOURNOI_FORTO | TOURNOI_AAAAH | TOURNOI_MODO_MODE_IT)) != 0;
		}
		
		return (TournoiFlag & (TOURNOI_FORTO | TOURNOI_AAAAH | TOURNOI_MODO_MODE_IT)) != 0 || EstModoJeu(true);
	}
	
	public boolean EstArbitreTournoiSecondaire(boolean OuPlus) {
		if (!OuPlus) {
			return (TournoiFlag & TOURNOI_ARBITRE_SECONDAIRE) != 0;
		}
		
		return (TournoiFlag & TOURNOI_ARBITRE_SECONDAIRE) != 0 || EstArbitreTournoi(true);
	}
	
	public boolean EstFilmeur(boolean OuPlus) {
		if (!OuPlus) {
			return (TournoiFlag & TOURNOI_FILMEUR) != 0;
		}
		
		return (TournoiFlag & TOURNOI_FILMEUR) != 0 || EstArbitreTournoi(true);
	}
	
	/** Général **/
	
	public boolean EstRespoTeam(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & GENERAL_RESPO_TEAM) != 0;
		}
		
		return (GeneralFlag & GENERAL_RESPO_TEAM) != 0;
	}
	
	public boolean EstAnimateurSuperviseur(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & GENERAL_ANIMATEUR_SUPERVISEUR) != 0;
		}
		
		return (GeneralFlag & GENERAL_ANIMATEUR_SUPERVISEUR) != 0;
	}
	
	public boolean EstAnimateur(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & GENERAL_ANIMATEUR) != 0;
		}
		
		return (GeneralFlag & GENERAL_ANIMATEUR) != 0 || EstAnimateurSuperviseur(true) || EstModoJeu(true);
	}
	
	public boolean EstRespomap(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & (GENERAL_RESPOMAP_AAAAH | GENERAL_RESPOMAP_FORTO)) != 0;
		}
		
		return (GeneralFlag & (GENERAL_RESPOMAP_AAAAH | GENERAL_RESPOMAP_FORTO)) != 0 || EstModoJeu(true);
	}
	
	public boolean EstRespomapForto(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & GENERAL_RESPOMAP_FORTO) != 0;
		}
		
		return (GeneralFlag & GENERAL_RESPOMAP_FORTO) != 0 || EstModoJeu(true);
	}
	
	public boolean EstRespomapAaaah(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & GENERAL_RESPOMAP_AAAAH) != 0;
		}
		
		return (GeneralFlag & GENERAL_RESPOMAP_AAAAH) != 0 || EstModoJeu(true);
	}
	
	public boolean EstRecrueRespomap(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & GENERAL_RECR_RESPOMAP_AAAAH) != 0;
		}
		
		return (GeneralFlag & GENERAL_RECR_RESPOMAP_AAAAH) != 0 || EstRespomapAaaah(true);
	}
	
	public boolean EstRespoElo(boolean OuPlus) {
		if (!OuPlus) {
			return (GeneralFlag & GENERAL_RESPO_ELO) != 0;
		}
		
		return (GeneralFlag & GENERAL_RESPO_ELO) != 0 || EstModoJeu(true);
	}
}
