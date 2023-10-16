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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aaaah.Carte;
import elo.Elo;
import elo.EloJoueur;
import elo.EloTeam;
import elo.RecompenseElo;
import forteresse.Forteresse;
import forteresse.MondeForteresse;
import forteresse.Rally;
import joueur.DemandeCompte;
import joueur.Joueur;
import joueur.RestrictionHeure;
import serveur.stats.Stat;

public class Boite extends Thread {
	
	public String $ = String.valueOf((char) 2);
	public String $$ = String.valueOf((char) 3);
	
	private int TypeRequeteEnCours = 0;
	private int IdRequete = 0;
	private RequeteBoite RequeteEnCours = null;
	
	static public int DEMANDE_LISTE_SUJET = 1;
	static public int DEMANDE_SUJET = 2;
	static public int NOUVEAU_MESSAGE = 3;
	static public int SUPPRESSION_DISCUSSION = 4;
	static public int EDITION_TITRE = 5;
	static public int DEPLACEMENT_SUJET = 6;
	static public int NOUVEAU_SUJET = 7;
	static public int FERMETURE_SUJET = 8;
	static public int REPONSE_SUJET = 9;
	static public int EDITION_MESSAGE = 10;
	static public int CHANGEMENT_AVATAR = 11;
	static public int VOTE = 12;
	static public int CHANGEMENT_DESCRIPTION = 13;
	static public int SUPPRESSION_AMI = 14;
	static public int DEMANDE_LISTE_MODO = 15;
	static public int AJOUT_AMI = 16;
	static public int CHARGEMENT_JOUEUR = 17;
	static public int SAUVEGARDE_JOUEUR = 18;
	static public int IDENTIFICATION = 19;
	static public int CREATION_COMPTE = 20;
	static public int MUTE_FORUM = 21;
	static public int QUITTER_TEAM = 22;
	static public int REJOINDRE_TEAM = 23;
	static public int CHANGER_GRADE = 24;
	static public int CREER_TEAM = 25;
	static public int SUPPRIMER_TEAM = 26;
	static public int LISTE_TEAM = 27;
	static public int MEMBRES_TEAM = 28;
	static public int INFO_TEAM = 29;
	static public int CHANGER_ROLE = 30;
	static public int EDITER_SITE = 31;
	static public int EDITER_DESCRIPTION = 32;
	static public int EDITER_INEDITABLE = 33;
	static public int DEMANDE_PROFIL = 34;
	static public int AJOUTER_MEMBRE = 35;
	static public int AJOUTER_RECOMPENSE = 36;
	static public int SUPPRIMER_RECOMPENSES = 37;
	static public int DEMANDE_HISTO = 38;
	static public int LOG_SANCTION = 39;
	static public int DEMANDE_HISTO_FORUM = 40;
	static public int CHANGER_PASSWORD = 41;
	static public int POSTIT_TOPIC = 42;
	static public int SUPPRIMER_AVATAR = 43;
	static public int EDITER_MESSAGE = 44;
	static public int MAJ_CLASSEMENT = 45;
	static public int COPIE_PASSWORD = 47;
	static public int BLOQUER_COMPTE = 48;
	static public int AUTORISER_CHGPASS = 49;
	static public int CHGPASS = 50;
	static public int VALIDER_CHGPASS = 51;
	static public int LISTE_CHGPASS = 52;
	static public int TEMPS_MOINS = 56;
	static public int RECHERCHE_SUJET = 57;
	static public int SUPPRIMER_DESCRIPTION = 58;
	static public int LISTE_MAP_JOUEUR = 59;
	static public int RECHERCHE_MAP = 60;
	static public int RECHERCHE_MAP_BY_ID = 61;
	static public int CREATE_MAP = 62;
	static public int UPDATE_MAP = 63;
	static public int DELETE_MAP = 64;
	static public int UPDATE_STATS_MAP = 65;
	static public int FLAG_MAP = 66;
	static public int PERMA_MAP = 67;
	static public int RETROUVER_JOUEUR = 68;
	static public int LOG_TEAM = 69;
	static public int HISTO_TEAM = 70;
	static public int RECHERCHE_SUJET_AUTEUR = 71;
	static public int DEMANDE_LOG = 73;
	static public int MUTECP = 74;
	static public int MUTEFORUM = 75;
	static public int DEBAN = 76;
	static public int DEMANDE_INFO = 77;
	static public int LOADBANDEF = 78;
	static public int BAN_IP = 79;
	static public int DEBAN_IP = 80;
	static public int BAN_HOST = 81;
	static public int DEBAN_HOST = 82;
	static public int LOADSANCTIONS = 83;
	static public int SUPPR_VOTES_MAP = 84;
	static public int MULTI_OFF = 85;
	static public int HISTO_TEAM_JOUEUR = 86;
	static public int RESTORE_MAP = 87;
	static public int SUPPRESSION_MESSAGE = 88;
	static public int HISTO_MAP = 89;
	static public int DELETE_HISTORY = 90;
	static public int RECHERCHE_MAP_BY_IDS = 91;
	static public int SAUVEGARDE_MARA = 92;
	static public int RETRIEVE_MARA = 93;
	static public int CREER_MARA = 94;
	static public int SAUVEGARDE_MARA_BANNI = 95;
	static public int ENVOIE_SONDAGE = 98;
	static public int REPONSE_SONDAGE = 99;
	static public int EDITER_TITRE_FORUM = 100;
	static public int SUPPRIMER_TITRE_FORUM = 101;
	static public int STOP_MARABOUM = 102;
	static public int LISTE_MP_RECUS = 103;
	static public int LISTE_MP_ENVOYES = 104;
	static public int SUPPRIMER_MP = 105;
	static public int NOUVEAU_MP = 106;
	static public int NB_MP_RECU = 107;
	static public int AJOUTER_RECOMPENSE_JOUEUR = 108;
	static public int LISTE_RECOMPENSES_JOUEUR = 109;
	static public int SUPPRIMER_RECOMPENSE_JOUEUR = 110;
	static public int MULTI_PWD = 111;
	static public int FLAGS = 112;
	static public int PLAYLIST_STANDARD = 113;
	static public int PLAYLIST_STANDARD_COMMANDE = 114;
	static public int PLAYLIST_PUBLIER = 115;
	static public int PLAYLIST_LISTE = 116;
	static public int PLAYLIST_SUPPRIMER = 117; 
	static public int PLAYLIST_PUBLIER_VALIDER = 118;
	static public int PLAYLIST_RECHERCHE = 119;
	static public int CHANGEMENT_AVATAR_NEW = 120;
	static public int FLAGS_JOUEUR = 121;
	static public int DEBAN_JOUEUR = 122;
	static public int FIN_SANCTION = 123;
	static public int NOUVELLE_FUSION = 124;
	static public int QUITTER_FUSION = 125;
	static public int UPDATE_FUSION = 126;
	static public int CHARGER_FUSIONS = 127;
	static public int SUPPRIMER_FUSION = 128;
	static public int LOAD_STATS = 129;
	static public int LOAD_FUSIONS = 130;
	static public int SAUVEGARDE_STAT = 131;
	static public int NEW_STAT = 132;
	static public int ARCHIVER_STATS = 133;
	static public int TOPIC_ANIM = 134;
	public static int DEMANDE_COMPTE_JPO = 135;
	public static int VALIDER_DEMANDE_JPO = 136;
	public static int LISTE_JPO = 137;
	public static int SUPPRIMER_MP_BY_ID = 138;
	public static int UPDATE_GHOST_MAP = 139;
	public static int SUPPR_RECORD_MAP = 140;
	public static int CREER_RALLY = 141;
	public static int CHARGER_RALLY = 142;
	public static int LISTE_RALLY = 143;
	public static int UPDATE_RALLY = 144;
	public static int CHARGER_RALLY_RESPOMAP = 145;
	public static int DELETE_RALLYS = 146;
	public static int AUTORISER_GRADE_9 = 147;
	public static int REVERSE_CODE_MAP = 149;
	public static int SET_BLASON_TEAM = 153;
	public static int RECORD_MAP_FORTO = 154;
	public static int RECHERCHE_MAP_FORTO = 155;
	public static int PARTAGER_TOPIC_TEAM_FUSION = 157;
	public static int AJOUTER_DONJON = 158;
	public static int CHARGER_NIV_DONJON = 159;
	public static int CHARGER_DONJON = 160;
	public static int SVGD_AVANCEMENT_DONJON = 161;
	public static int MASSE_REFUS_RALLY = 163;
	public static int CALCULER_NOUVEL_ELO = 165;
	public static int CLASSEMENT_ELO = 166;
	public static int DELETE_COMPTES_INUTILISES = 169;
	public static int CALCULER_NOUVEL_ELO_TEAM = 170;
	public static int OBTENIR_STATS_ELO_PROFIL = 171;
	public static int RELOAD_MAP_AAAAH = 172;
	public static int ATTRIBUER_RECOMPENSES_ELO = 173;
	public static int LOAD_MAP_PLAYLIST_FORTO = 174;
	public static int ARCHIVER_STATS_ELO = 175;
	public static int AJOUTER_G10 = 176;
	public static int AJOUTER_G11 = 177;
	public static int BLOQUER_COMPTE2 = 178;
	public static int TMP_CLEAN_FUSIONS = 179;
	public static int UPDATE_PROFIL_EN_LIGNE = 180;
	public static int IMPORTER_COMPTE = 181;
	
	public ConcurrentLinkedQueue<RequeteBoite> ListeRequete = new ConcurrentLinkedQueue<RequeteBoite>();
	static public Hashtable<Integer, String> CacheListeSujet = new Hashtable<Integer, String>();
	static public Hashtable<Integer, Sujet> CacheSujet = new Hashtable<Integer, Sujet>();
	static public boolean CacheListeTeamAJour = false;
	static public String CacheListeTeam = "";

	private Serveur serveur;

	public Boite(Serveur SERVEUR) {
		serveur = SERVEUR;
		
		this.start();
	}
	
	public RequeteBoite AddRequete(Joueur J, RequeteBoite ReqBoite) {
		if(J == null || J.limiteurBoite.utilisable()) {
			ListeRequete.add(ReqBoite);
			return ReqBoite;
		}
		
		return null;
	}

	/**
	 * Toutes les requête possible
	 */
	public void Requete(int TYPE) {
		AddRequete(null, new RequeteBoite(TYPE, null, 0, 0, null, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, Stat STAT) {
		RequeteBoite Req = AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, null, null, false, null, null, null, null, null));
		Req.Stat = STAT;
		//ListeRequete.add(Req);
	}
	
	public void Requete(int TYPE, Joueur JOUEUR, Rally RALLY) {
		RequeteBoite Req = new RequeteBoite(TYPE, JOUEUR, 0, 0, null, null, false, null, null, null, null, null);
		Req.Rally = RALLY;
		ListeRequete.add(Req);
	}

	public void Requete(int TYPE, Joueur JOUEUR) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, null, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, int[] LISTE_ENTIER) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, null, null, false, null, LISTE_ENTIER, null, null, null));
	}
	
	public void Requete(int TYPE, Joueur JOUEUR, int[] LISTE_ENTIER, String CHAINE) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, CHAINE, null, false, null, LISTE_ENTIER, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String[] LISTE_STRING) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, null, null, false, null, null, null, LISTE_STRING, null));
	}


	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, CHAINE, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, int ENTIER) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER, 0, null, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE, boolean BOOLEAN) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, CHAINE, null, BOOLEAN, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, int ENTIER, boolean BOOLEAN) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER, 0, null, null, BOOLEAN, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, int ENTIER, String CHAINE) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER, 0, CHAINE, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, String CHAINE_2) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, CHAINE_1, CHAINE_2, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, int ENTIER_1) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER_1, 0, CHAINE_1, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, String CHAINE_2, String CHAINE_3) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, CHAINE_1, CHAINE_2, false, CHAINE_3, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, String CHAINE_2, String CHAINE_3, int ENTIER_1) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER_1, 0, CHAINE_1, CHAINE_2, false, CHAINE_3, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, String CHAINE_2, int ENTIER_1) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER_1, 0, CHAINE_1, CHAINE_2, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, String CHAINE_2, String CHAINE_3, int ENTIER_1, int ENTIER_2) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER_1, ENTIER_2, CHAINE_1, CHAINE_2, false, CHAINE_3, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, String CHAINE_2, String CHAINE_3, String CHAINE_4) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, 0, 0, CHAINE_1, CHAINE_2, false, CHAINE_3, null, CHAINE_4, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, String CHAINE_1, String CHAINE_2, String CHAINE_3, String CHAINE_4, int ENTIER_1) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER_1, 0, CHAINE_1, CHAINE_2, false, CHAINE_3, null, CHAINE_4, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, int ENTIER_1, int ENTIER_2) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER_1, ENTIER_2, null, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, int ENTIER_1, int ENTIER_2, String CHAINE) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER_1, ENTIER_2, CHAINE, null, false, null, null, null, null, null));
	}

	public void Requete(int TYPE, Joueur JOUEUR, int ENTIER, String CHAINE, boolean BOOLEAN) {
		AddRequete(JOUEUR, new RequeteBoite(TYPE, JOUEUR, ENTIER, 0, CHAINE, null, BOOLEAN, null, null, null, null, null));
	}
	
	public void Requete(int TYPE, Object[] args) {
		AddRequete(null, new RequeteBoite(TYPE, null, 0, 0, null, null, false, null, null, null, null, args));
	}


	/**
	 * Exécution des requêtes
	 */

	 // protection contre les injections SQL
	private String addSlash(String Param) {
		return Param.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'");
	}

	/*
	 * Récupère l'IdTeam d'un joueur
	 */
	private Integer GetTeam(Joueur DEMANDEUR, int ID_JOUEUR) {
		try {
			if (DEMANDEUR.IdJoueur == ID_JOUEUR) {
				return DEMANDEUR.IdTeam;
			} else {
				serveur.Bdd.Requete_GetTeam.setInt(1, ID_JOUEUR);
				ResultSet Result = serveur.Bdd.Requete_GetTeam.executeQuery();
				if (!Result.next()) {
					return null;
				}
				return Result.getInt("id_team");
			}
		} catch (Exception e) {
			System.err.print("3XC GetTeam "); e.printStackTrace();
		}
		return null;
	}

	/*
	 * Récupère l'id d'un joueur
	 */
	private Integer GetId(Joueur DEMANDEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_GetId.setString(1, CIBLE);
			ResultSet Result = serveur.Bdd.Requete_GetId.executeQuery();
			if (!Result.next()) {
				DEMANDEUR.Envoie("CxINFO#Ce joueur n'existe pas.");
				return null;
			}
			return Result.getInt("id");
		} catch (Exception e) {
			System.err.print("3XC GetId "); e.printStackTrace();
		}
		return null;
	}

	/*
	 * Teste si JOUEUR est le chef de team de ID_JOUEUR
	 */
	private boolean CheckChefTeam(Joueur JOUEUR, int ID_JOUEUR) {
		Integer IdTeam = GetTeam(JOUEUR, ID_JOUEUR);
		if (IdTeam == null || IdTeam == 0) {
			JOUEUR.Envoie("CxINFO#Ce joueur n'a pas de team.");
			return false;
		} else {
			return JOUEUR.AutorisationGestionRoles && IdTeam == JOUEUR.IdTeam;
		}
	}

	/*
	 * Teste s'il reste des membres à la team et s'il reste des leaders
	 */
	public void CheckMembresRestants(Integer IdTeam) {
		try {
			//ResultSet Result = serveur.Bdd.Requete.executeQuery("SELECT role,count(*) AS c FROM $membres WHERE id_team = " + IdTeam + " GROUP BY role ORDER BY role desc");
			serveur.Bdd.Requete_TeamLeaderRestant.setInt(1, IdTeam);
			ResultSet Result = serveur.Bdd.Requete_TeamLeaderRestant.executeQuery();
			if (Result.next()) {
				Integer NbrRole = Result.getInt("COUNT(role)");

				if(NbrRole == 0) { // il n'y a plus de leader
				    serveur.Bdd.Requete_TeamRecruteurRestant.setInt(1, IdTeam);
				    Result = serveur.Bdd.Requete_TeamRecruteurRestant.executeQuery(); // On récupère le nombre de recruteurs
				    
				    if (Result.next()) {
				        
				    	NbrRole = Result.getInt("COUNT(role)");
					  
				        if (NbrRole != 0) {
						    // on augmente les recruteurs
				        	serveur.Bdd.Requete_TeamPromotionRecruteur.setInt(1, IdTeam);
						    serveur.Bdd.Requete_TeamPromotionRecruteur.executeUpdate();
						   //serveur.Bdd.Requete.executeUpdate("UPDATE $membres SET role = 2 WHERE role=1 AND id_team = " + IdTeam);
				        } else { // On cherche un scribe
				        	serveur.Bdd.Requete_TeamScribeRestant.setInt(1, IdTeam);
						    Result = serveur.Bdd.Requete_TeamScribeRestant.executeQuery(); // On récupère le nombre de recruteurs
				        	
						    if(Result.next()) {
						    	NbrRole = Result.getInt("COUNT(role)");
				        		if (NbrRole != 0) { // Il reste un scribe
				        			serveur.Bdd.Requete_TeamPromotionScribe.setInt(1, IdTeam);
								    serveur.Bdd.Requete_TeamPromotionScribe.executeUpdate();
				        		} else {
				        			// On vérifie qu'il reste au moins un joueur
				        			serveur.Bdd.Requete_TeamNombreMembre.setInt(1,  IdTeam);
				        			Result = serveur.Bdd.Requete_TeamNombreMembre.executeQuery();
				        			if (Result.next() && Result.getInt(1) > 0) {
				        				// on désigne deux personnes
							        	serveur.Bdd.Requete_TeamPromotionMembre.setInt(1, IdTeam);
							        	serveur.Bdd.Requete_TeamPromotionMembre.executeUpdate();
									   //serveur.Bdd.Requete.executeUpdate("UPDATE $membres SET role = 2 WHERE id_team = " + IdTeam + " LIMIT 2");
				        			} else {
				        				serveur.Retirer_Team_Fusion(IdTeam, null);
				        				serveur.Bdd.Requete_TeamSuppression.setInt(1, IdTeam);
				        				serveur.Bdd.Requete_TeamSuppression.executeUpdate();
				        				Boite.CacheListeTeamAJour = false;
				        			}
					        	}
				        		
				        	}
				          }
				        }
				  }
			} else {
				// On supprime la team si elle n'a plus de membres 
				serveur.Retirer_Team_Fusion(IdTeam, null);
				serveur.Bdd.Requete_TeamSuppression.setInt(1, IdTeam);
				serveur.Bdd.Requete_TeamSuppression.executeUpdate();
				Boite.CacheListeTeamAJour = false;
				//serveur.Bdd.Requete.executeUpdate("DELETE FROM $teams WHERE id=" + IdTeam + " LIMIT 1");
			}

		} catch (Exception e) {
			System.err.print("3XC CheckMembresRestants "); e.printStackTrace();
		}
	}

	private void Quitter_Team(Joueur JOUEUR, String CIBLE) {
		try {
			long DEBUT = System.currentTimeMillis();

			Integer ID_JOUEUR = GetId(JOUEUR, CIBLE);

			// on récupère l'id et le role du joueur visé
			Integer IdTeam = null;
			Integer RoleCible = 0;
			try {
				serveur.Bdd.Requete_TeamJoueur.setInt(1, ID_JOUEUR);
				ResultSet Result = serveur.Bdd.Requete_TeamJoueur.executeQuery();
				//ResultSet Result = serveur.Bdd.Requete.executeQuery("SELECT id_team,role FROM $membres WHERE id_joueur = " + ID_JOUEUR);
				if (Result.next()) {
					IdTeam = Result.getInt("id_team");
					RoleCible = Result.getInt("role");
				}
			} catch (Exception e) {
				System.err.print("3XC Quitter_Team "); e.printStackTrace();
			}

			if (IdTeam == null || IdTeam == 0) {
				JOUEUR.Envoie("CxINFO#Ce joueur n'a pas de team.");
			} else {
				boolean CibleSelf = JOUEUR.IdJoueur == ID_JOUEUR; // éjection soi-même
				boolean CibleChef = RoleCible == 2; // on ne peut pas éjecter le chef de team
				if (JOUEUR.CheckDroits(CibleSelf /*le joueur quitte lui-même la team */
						|| (JOUEUR.AutorisationGestionMembres && IdTeam == JOUEUR.IdTeam && (!CibleChef || JOUEUR.AutorisationGestionRoles))) /* expulsé par le chef de team*/
						) {
					Supprimer_De_Team(ID_JOUEUR, IdTeam, CibleSelf, JOUEUR, CIBLE);
					/*serveur.Bdd.Requete_TeamEjecterJoueur.setInt(1, ID_JOUEUR);
					serveur.Bdd.Requete_TeamEjecterJoueur.executeUpdate();
					//serveur.Bdd.Requete.executeUpdate("DELETE FROM $membres WHERE id_joueur=" + ID_JOUEUR + " LIMIT 1");
					serveur.Bdd.Requete_TeamPerteJoueur.setInt(1, IdTeam);
					serveur.Bdd.Requete_TeamPerteJoueur.executeUpdate();
					//serveur.Bdd.Requete.executeUpdate("UPDATE $teams SET modif=NOW(),membres=membres-1 WHERE id = " + IdTeam + " LIMIT 1");
					JOUEUR.Envoie("CxINFO#Ce joueur n'est désormais plus membre de la team.");
					Joueur Cible = CibleSelf ? JOUEUR : (serveur.JoueurById(ID_JOUEUR));
					// log historique
					if (CibleSelf) {
						serveur.BOITE.Requete(Boite.LOG_TEAM, JOUEUR, null, "Q", IdTeam);
					} else {
						CIBLE = CIBLE.substring(0, 1).toUpperCase() + CIBLE.substring(1).toLowerCase();
						serveur.BOITE.Requete(Boite.LOG_TEAM, JOUEUR, CIBLE, "E", IdTeam);
					}
					if (Cible != null) {
						Cible.Quitter_Team();
					}
					CheckMembresRestants(IdTeam); // on regarde si on doit supprimer la team ou promouvoir des leaders*/
				}
			}
			serveur.Log(JOUEUR, DEBUT, "Quitter_Team " + ID_JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Quitter_Team 2 "); e.printStackTrace();
		}
	}
	
	private void Supprimer_De_Team(int IdJoueurVire, int IdTeam, boolean AutoSuppression, Joueur Vireur, String PseudoViré) throws Exception {
		serveur.Bdd.Requete_TeamEjecterJoueur.setInt(1, IdJoueurVire);
		serveur.Bdd.Requete_TeamEjecterJoueur.executeUpdate();
		//serveur.Bdd.Requete.executeUpdate("DELETE FROM $membres WHERE id_joueur=" + ID_JOUEUR + " LIMIT 1");
		serveur.Bdd.Requete_TeamPerteJoueur.setInt(1, IdTeam);
		serveur.Bdd.Requete_TeamPerteJoueur.executeUpdate();
		//serveur.Bdd.Requete.executeUpdate("UPDATE $teams SET modif=NOW(),membres=membres-1 WHERE id = " + IdTeam + " LIMIT 1");
		if (Vireur != null)
			Vireur.Envoie("CxINFO#Ce joueur n'est désormais plus membre de la team.");
		Joueur Cible = AutoSuppression ? Vireur : (serveur.JoueurById(IdJoueurVire));
		// log historique
		if (AutoSuppression) {
			serveur.BOITE.Requete(Boite.LOG_TEAM, Vireur, null, "Q", IdTeam);
		} else {
			PseudoViré = PseudoViré.substring(0, 1).toUpperCase() + PseudoViré.substring(1).toLowerCase();
			serveur.BOITE.Requete(Boite.LOG_TEAM, Vireur, PseudoViré, "E", IdTeam);
		}
		if (Cible != null) {
			Cible.Quitter_Team();
		}
		CheckMembresRestants(IdTeam); // on regarde si on doit supprimer la team ou promouvoir des leaders
	}

	private void Rejoindre_Team(Joueur JOUEUR, int ID_JOUEUR, int ID_TEAM, boolean Creation) {
		try {
			// On regarde si le joueur a déjà une team
			Integer IdTeam = GetTeam(JOUEUR, ID_JOUEUR);
			if (IdTeam != null && IdTeam != 0) {
				JOUEUR.Envoie("CxINFO#Ce joueur a déjà une team.");
			} else {
				Joueur Cible = serveur.JoueurById(ID_JOUEUR);
				if (JOUEUR.AutorisationModoTeam || Cible != null) {
					if (!JOUEUR.AutorisationModoTeam && Cible.IdTeamSouhaitée != ID_TEAM) {
						JOUEUR.Envoie("CxINFO#Ce joueur doit d'abord demander à rejoindre la team.");
					} else {
						int Droits = Creation ? 2 : 0;
						serveur.Bdd.Requete_TeamRejoindreJoueur.setInt(1, ID_JOUEUR);
						serveur.Bdd.Requete_TeamRejoindreJoueur.setInt(2, ID_TEAM);
						serveur.Bdd.Requete_TeamRejoindreJoueur.setInt(3, Droits);
						serveur.Bdd.Requete_TeamRejoindreJoueur.executeUpdate();
						//serveur.Bdd.Requete.executeUpdate("INSERT INTO $membres VALUES (" + ID_JOUEUR + "," + ID_TEAM + ",0," + Droits + ")");
						serveur.Bdd.Requete_TeamNouveauMembre.setInt(1, ID_TEAM);
						serveur.Bdd.Requete_TeamNouveauMembre.executeUpdate();
						//serveur.Bdd.Requete.executeUpdate("UPDATE $teams SET modif=NOW(),membres=membres+1 WHERE id = " + ID_TEAM + " LIMIT 1");
						if (JOUEUR != Cible) {
							JOUEUR.Envoie("CxINFO#Ce joueur est maintenant membre de la team.");
						}
						if (Cible != null) {
							if (!Creation) {
								// on ne log pas le fait que le joueur rejoigne la team si c'est une création
								serveur.BOITE.Requete(Boite.LOG_TEAM, JOUEUR, Cible.NomJoueur, "R", ID_TEAM);
							}
							Cible.Envoie("CxINFO#Vous êtes maintenant membre de la team " + ID_TEAM + ".");
							Cible.Envoie("TxR#" + ID_TEAM);
							Cible.IdTeamSouhaitée = 0;
							Cible.Membre = true;
							Cible.IdTeam = ID_TEAM;
							Cible.NomTeam = "A rejoint la team " + ID_TEAM;
							Cible.Grade = 0;
							Cible.AutorisationGestionMembres = false;
							Cible.AutorisationGestionRoles = false;
							Cible.AutorisationScribe = false;
						}
					}
				} else {
					JOUEUR.Envoie("CxINFO#Ce joueur n'est pas connecté.");
				}
			}

		} catch (Exception e) {
			System.err.print("3XC Rejoindre_Team "); e.printStackTrace();
		}
	}

	private void Changer_Grade(Joueur JOUEUR, int GRADE, String CIBLE, boolean DON_GRADE9) {
		try {
			Integer ID_JOUEUR = GetId(JOUEUR, CIBLE);
			if (ID_JOUEUR != null) {
				if (DON_GRADE9 || JOUEUR.CheckDroits(CheckChefTeam(JOUEUR, ID_JOUEUR))) {
					long derniereConnexion;
					int Stats_PremierAaaah;
					int Stats_PremierBouboum;
					long tempsJeu;
					boolean g9;
					boolean g10;
					boolean g11;
					Joueur Cible = serveur.JoueurById(ID_JOUEUR);
					if (Cible == null) {
						serveur.Bdd.Requete_ChargementJoueur.setString(1, CIBLE);
						ResultSet Result = serveur.Bdd.Requete_ChargementJoueur.executeQuery();
						
						if (!Result.next()) {
							return;
						}
						
						Result.getInt("sa_guidage");
						if (Result.wasNull()) { // Pas d'ancienne stat enregistrée ($old_stats_j)
							Stats_PremierAaaah = 0;
							Stats_PremierBouboum = 0;
						} else {
							Stats_PremierAaaah = Result.getInt("sa_victoire");
							Stats_PremierBouboum = Result.getInt("sb_victoire");
						}
						
						Stats_PremierAaaah += Result.getInt("sa_p");
						Stats_PremierBouboum += Result.getInt("sb_pg");
						
						derniereConnexion = Result.getInt("s_dc");
						
						g9 = (1 == Result.getInt("g9"));
						
						tempsJeu = Result.getLong("s_tt");
						
						boolean[] gradesSpe = Joueur_A_Grades_Speciaux(ID_JOUEUR);
						g10 = gradesSpe[0];
						g11 = gradesSpe[1];
						
					} else {
						derniereConnexion = Cible.Stats_DernièreConnexion;
						
						Stats_PremierAaaah = Cible._Aaaah.Stats_PartiePremier + Cible._Aaaah.Stats_OldPremier;
						Stats_PremierBouboum = Cible._Bouboum.Stats_PartieGagnée + Cible._Bouboum.Stats_OldGagnées;
						g9 = Cible.AutorisationGrade9;
						g10 = Cible.AutorisationGrade10;
						g11 = Cible.AutorisationGrade11;
						tempsJeu = Cible.Stats_TempsDeJeu;
					}
					
					if (GRADE>=6 && GRADE <= 8) {
						if (derniereConnexion < 1307966400L  || 
								(tempsJeu < RestrictionHeure.OBTENIR_G6_7_8 && Stats_PremierAaaah < 2000 && Stats_PremierBouboum < 2000)) {
							JOUEUR.Envoie("CxINFO#Le joueur doit avoir joué suffisamment de parties et s'être connecté après le 13 juin 2011.");
							return;
						}
					}
					
					if ((GRADE == 9 && !g9) || (GRADE == 10 && !g10) || (GRADE == 11 && !g11)) {
						JOUEUR.Envoie("CxINFO#Le joueur doit avoir obtenu l'accès à ce grade.");
						return;
					}
					
					serveur.Bdd.Requete_TeamChangerGrade.setInt(1, GRADE);
					serveur.Bdd.Requete_TeamChangerGrade.setInt(2, ID_JOUEUR);
					serveur.Bdd.Requete_TeamChangerGrade.executeUpdate();
					//serveur.Bdd.Requete.executeUpdate("UPDATE $membres SET grade = " + GRADE + " WHERE id_joueur = " + ID_JOUEUR + " LIMIT 1");
					JOUEUR.Envoie("CxINFO#Le grade a été changé.");
					if (Cible != null) {
						Cible.Grade = GRADE;
					}
				}

			}
		} catch (Exception e) {
			System.err.print("3XC Changer_Grade "); e.printStackTrace();
		}
	}
	
	private void Ajouter_Grade_10(int id) {
		try {
			serveur.Bdd.Requete_Ajouter_Grade10.setInt(1, id);
			serveur.Bdd.Requete_Ajouter_Grade10.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Ajouter_Grade_10"); e.printStackTrace();
		}
	}
	
	private void Ajouter_Grade_11(int id) {
		try {
			serveur.Bdd.Requete_Ajouter_Grade11.setInt(1, id);
			serveur.Bdd.Requete_Ajouter_Grade11.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Ajouter_Grade_11"); e.printStackTrace();
		}
	}
	
	private boolean[] Joueur_A_Grades_Speciaux(int id) {
		try {
			serveur.Bdd.Requete_Autoriser_Grades_Speciaux.setInt(1, id);
			
			ResultSet res = serveur.Bdd.Requete_Autoriser_Grades_Speciaux.executeQuery();
			if (res.next()) {
				boolean[] r = new boolean[2];
				
				r[0] = res.getBoolean("g10");
				r[1] = res.getBoolean("g11");
				return r;
			}
		} catch (Exception e) {
			System.err.println("3XC Joueur_A_Grades_Speciaux"); e.printStackTrace();
		}
		
		return new boolean[]{false, false};
	}
	
	private void Autoriser_Grade_9(Joueur JOUEUR, String CIBLE) {
	    	try {
	    		Integer ID_JOUEUR = GetId(JOUEUR, CIBLE);
	    		serveur.Bdd.Requete_Autoriser_Grade_9.setInt(1, ID_JOUEUR);
	    		serveur.Bdd.Requete_Autoriser_Grade_9.executeUpdate();		
	    		//serveur.Bdd.Requete.executeUPDATE("UPDATE $joueur set g9=1 WHERE id= ? LIMIT 1");	    	
	    	} catch (Exception e) {
	    		System.err.print("3XC Autoriser_Grade_9 "); e.printStackTrace();
	    	}
	}

	private void Changer_Role(Joueur JOUEUR, int ROLE, String CIBLE) {
		try {
			Integer ID_JOUEUR = GetId(JOUEUR, CIBLE);
			if (ID_JOUEUR != null && JOUEUR.CheckDroits(CheckChefTeam(JOUEUR, ID_JOUEUR))) {
				serveur.Bdd.Requete_TeamChangerRole.setInt(1, ROLE);
				serveur.Bdd.Requete_TeamChangerRole.setInt(2, ID_JOUEUR);
				serveur.Bdd.Requete_TeamChangerRole.executeUpdate();
				//serveur.Bdd.Requete.executeUpdate("UPDATE $membres SET role = " + ROLE + " WHERE id_joueur = " + ID_JOUEUR + " LIMIT 1");
				JOUEUR.Envoie("CxINFO#Le rôle a été changé.");
				Joueur Cible = serveur.JoueurById(ID_JOUEUR);
				if (Cible != null) {
					Cible.FixeAutorisations(ROLE);
					Cible.Envoie("TxD#" + Cible.RoleInt());
				}
				if (ID_JOUEUR == JOUEUR.IdJoueur) {
					// le joueur s'enlève lui-même les droits
					CheckMembresRestants(JOUEUR.IdTeam); // on regarde si on doit promouvoir des leaders                        
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Changer_Role "); e.printStackTrace();
		}
	}

	private void Editer_Ineditable(Joueur JOUEUR, int ID_TEAM, String INFO, String WHAT) {
		INFO = addSlash(INFO);
		try {

			if (JOUEUR.CheckDroits(false) || (WHAT.equals("score") && JOUEUR.AutorisationRespoTriTeam)) {
				String todo = "";
				if (WHAT.equals("creation") || WHAT.equals("officiel")) {
					todo = WHAT + "= FROM_UNIXTIME(" + INFO + ")";
				} else {
					todo = WHAT + "='" + INFO + "'";
				}
				if (WHAT.equals("score")) {
					CacheListeTeamAJour = false;
				}
				serveur.Bdd.Requete.executeUpdate("UPDATE `$teams` SET " + todo + " WHERE id = " + ID_TEAM);
				JOUEUR.Envoie("CxINFO#L'info a été changé.");
			}
		} catch (Exception e) {
			System.err.print("3XC Editer_Ineditable "); e.printStackTrace();
		}
	}

	private void Editer_Site(Joueur JOUEUR, int ID_TEAM, String SITE) {
		if (SITE.length() > 50) {
			JOUEUR.Envoie("Texte trop long.");
			return;
		}
		try {

			if (JOUEUR.CheckDroits(JOUEUR.AutorisationScribe && ID_TEAM == JOUEUR.IdTeam)) {
				serveur.Bdd.Requete_TeamEditerSite.setString(1, SITE);
				serveur.Bdd.Requete_TeamEditerSite.setInt(2, ID_TEAM);
				serveur.Bdd.Requete_TeamEditerSite.executeUpdate();
				//serveur.Bdd.Requete.executeUpdate("UPDATE $teams SET site='" + SITE + "' WHERE id = " + ID_TEAM + " LIMIT 1");
				JOUEUR.Envoie("CxINFO#Le site a été changé.");
			}
		} catch (Exception e) {
			System.err.print("3XC Editer_Site "); e.printStackTrace();
		}
	}

	private void Editer_Description(Joueur JOUEUR, int ID_TEAM, String DESCRIPTION) {
		if (DESCRIPTION.length() > 5000) {
			JOUEUR.Envoie("Texte trop long.");
			return;
		}
		try {
			if (JOUEUR.CheckDroits(JOUEUR.AutorisationScribe && ID_TEAM == JOUEUR.IdTeam)) {
				serveur.Bdd.Requete_EditionDescription.setString(1, DESCRIPTION);
				serveur.Bdd.Requete_EditionDescription.setInt(2, ID_TEAM);
				serveur.Bdd.Requete_EditionDescription.execute();
				JOUEUR.Envoie("CxINFO#La description a été changée.");
			}
		} catch (Exception e) {
			System.err.print("3XC Editer_Description "); e.printStackTrace();
		}
	}

	private void Editer_Message(Joueur JOUEUR, String MESSAGE) {
		if (MESSAGE.equals("-")) {
			MESSAGE = "";
		}
		if (MESSAGE.length() > 1000) {
			JOUEUR.Envoie("Texte trop long.");
			return;
		}
		try {
			if (JOUEUR.CheckDroits(JOUEUR.AutorisationScribe )) {
				serveur.Bdd.Requete_EditionMessageTeam.setString(1, MESSAGE);
				serveur.Bdd.Requete_EditionMessageTeam.setInt(2, JOUEUR.IdTeam);
				serveur.Bdd.Requete_EditionMessageTeam.execute();
				JOUEUR.Envoie("CxINFO#Le message d'accueil que vos membres recevront à leur connexion a été changé:\n" + MESSAGE);
			}
		} catch (Exception e) {
			System.err.print("3XC Editer_Message "); e.printStackTrace();
		}
	}

	private void Créer_Team(Joueur JOUEUR, String NOM, String TAG, String FONDATEUR, String DESCRIPTION, String SITE, int JEU, int DATE_CREATION) {
		if (!Joueur.PseudoConforme(FONDATEUR)) {
			JOUEUR.Envoie("CxINFO#Vous avez rentré un paramètre incorrect pour le fondateur.");
			return;
		}
		FONDATEUR = FONDATEUR.substring(0, 1).toUpperCase() + FONDATEUR.substring(1).toLowerCase(); //regexp    + majs     

		try {
			if (JOUEUR.AutorisationModoTeam || JOUEUR.Stats_TempsDeJeu > RestrictionHeure.CREER_TEAM) {
				serveur.Bdd.Requete_TeamCreerCreateur.setString(1, JOUEUR.NomJoueur );
				ResultSet LastTeam = serveur.Bdd.Requete_TeamCreerCreateur.executeQuery();
				//ResultSet LastTeam = serveur.Bdd.Requete.executeQuery("SELECT id FROM $teams_his WHERE action='C' and auteur = '" + JOUEUR.NomJoueur + "' and UNIX_TIMESTAMP()-UNIX_TIMESTAMP(date)<2592000 LIMIT 1"); // 30 jours
				if (LastTeam.next()) {
					JOUEUR.Envoie("TxC#3"); // Création récente
					return;
				}

				serveur.Bdd.Requete_TeamCreerNom.setString(1, NOM);
				ResultSet Result = serveur.Bdd.Requete_TeamCreerNom.executeQuery();
				//ResultSet Result = serveur.Bdd.Requete.executeQuery("SELECT id FROM $teams WHERE nom = '" + NOM + "'");
				if (Result.next()) {
					JOUEUR.Envoie("TxC#1"); // Nom de team déjà utilisé.
					return;
				}
				boolean CréateurRejoint = !JOUEUR.AutorisationModoTeam;
				serveur.Bdd.Requete_TeamCreerInsert.setString(1, NOM);
				serveur.Bdd.Requete_TeamCreerInsert.setString(2, TAG);
				serveur.Bdd.Requete_TeamCreerInsert.setString(3, DESCRIPTION);
				serveur.Bdd.Requete_TeamCreerInsert.setString(4, SITE);
				serveur.Bdd.Requete_TeamCreerInsert.setInt(5, DATE_CREATION);
				serveur.Bdd.Requete_TeamCreerInsert.setString(6, FONDATEUR);
				serveur.Bdd.Requete_TeamCreerInsert.setInt(7, JEU);
				serveur.Bdd.Requete_TeamCreerInsert.executeUpdate();
				//serveur.Bdd.Requete.executeUpdate("INSERT INTO $teams (nom,tag,description,site,modif,creation,fondateur,jeu,membres,officiel,score) VALUES ('" + NOM + "','" + TAG + "','" + DESCRIPTION + "','" + SITE + "',NOW(),FROM_UNIXTIME(" + DATE_CREATION + "),'" + FONDATEUR + "'," + JEU + "," + 0 + ",NOW(),0)");
				serveur.Bdd.Requete_TeamCreerNom.setString(1, NOM);
				ResultSet ResultId = serveur.Bdd.Requete_TeamCreerNom.executeQuery();
				//ResultSet ResultId = serveur.Bdd.Requete.executeQuery("SELECT id FROM $teams WHERE nom = '" + NOM + "'");
				if (ResultId.next()) {
					Integer IdTeam = ResultId.getInt("id");
					
					// Création topic team
					Nouveau_Sujet(JOUEUR, "[" + IdTeam + "] " + NOM, "Topic de la team " + IdTeam, Serveur.FORUM_TEAM);
					
					if (CréateurRejoint) { // on ajoute la personne si c'est pas un modo team                            
						JOUEUR.IdTeamSouhaitée = IdTeam;
						Rejoindre_Team(JOUEUR, JOUEUR.IdJoueur, IdTeam, true);
						JOUEUR.AutorisationGestionMembres = true;
						JOUEUR.AutorisationGestionRoles = true;
						JOUEUR.AutorisationScribe = true;
						JOUEUR.Envoie("TxD#2");
						serveur.BOITE.Requete(Boite.LOG_TEAM, JOUEUR, null, "C", JOUEUR.IdTeam);
						Boite.CacheListeTeamAJour = false;
					}
					JOUEUR.Envoie("TxCC#" + IdTeam);
				}
			} else {
				JOUEUR.Envoie("TxC#2"); // Pas assez de temps de jeu                    
			}
		} catch (Exception e) {
			System.err.print("3XC Créer_Team "); e.printStackTrace();
		}
	}


	private void Supprimer_Team(Joueur JOUEUR, int ID_TEAM) {
		try {
			long DEBUT = System.currentTimeMillis();
			if (JOUEUR.CheckDroits(JOUEUR.AutorisationGestionRoles && ID_TEAM == JOUEUR.IdTeam)) {
				System.out.println(JOUEUR.NomJoueur + " (" + JOUEUR.AdresseIP + ") a dissout la team " + ID_TEAM);
				serveur.BOITE.Requete(Boite.LOG_TEAM, JOUEUR, null, "D", ID_TEAM);

				if (ID_TEAM == JOUEUR.IdTeam) { // si pas admin team
					JOUEUR.QuitterFusion();
				}
				
				// on éjecte les membres
				int Nb = serveur.ListeJoueur.size();
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = serveur.ListeJoueur.get(i);
					if (Joueur.IdTeam == ID_TEAM) {
						Joueur.Quitter_Team();
					}
				}
				serveur.Bdd.Requete_TeamSupprimerMembres.setInt(1, ID_TEAM);
				serveur.Bdd.Requete_TeamSupprimerMembres.executeUpdate();
				//serveur.Bdd.Requete.executeUpdate("DELETE FROM $membres WHERE id_team =" + ID_TEAM);
				serveur.Bdd.Requete_TeamSupprimer.setInt(1, ID_TEAM);
				serveur.Bdd.Requete_TeamSupprimer.executeUpdate();
				//serveur.Bdd.Requete.executeUpdate("DELETE FROM $teams WHERE id=" + ID_TEAM + " LIMIT 1");
				JOUEUR.Envoie("CxINFO#Team supprimée.");
				Boite.CacheListeTeamAJour = false;
				
				serveur.Retirer_Team_Fusion(ID_TEAM, null);

			}
			serveur.Log(JOUEUR, DEBUT, "Supprimer_Team " + ID_TEAM);
		} catch (Exception e) {
			System.err.print("3XC Supprimer_Team "); e.printStackTrace();
		}
	}

	private void Info_Team(Joueur JOUEUR, int ID_TEAM) {
		try {
			String Chaine = "";
			serveur.Bdd.Requete_TeamInfo.setInt(1, ID_TEAM);
			ResultSet Resultat = serveur.Bdd.Requete_TeamInfo.executeQuery();
			//ResultSet Resultat = serveur.Bdd.Requete.executeQuery("SELECT nom,tag,description,site,UNIX_TIMESTAMP(creation) as creation,UNIX_TIMESTAMP(officiel) as officiel,UNIX_TIMESTAMP(modif) as modif,fondateur,jeu,id,message FROM $teams WHERE id=" + ID_TEAM + " LIMIT 1");
			if (Resultat.next()) {
				String Message = "";
				if (JOUEUR.IdTeam == ID_TEAM && Resultat.getString("message") != null) {
					Message = Resultat.getString("message");
				}
				Chaine = "TxF#" + Resultat.getString("nom") + "#" + Resultat.getString("tag") + "#" + Resultat.getString("description") + "#" + Resultat.getString("site") + "#" + Resultat.getString("creation") + "#" + Resultat.getString("modif") + "#" + Resultat.getString("fondateur") + "#" + serveur.Jeu(Resultat.getInt("jeu")) + "#" + Resultat.getInt("id") + "#" + Message + "#" + Resultat.getString("officiel") + "#" ;
				// récompenses
				serveur.Bdd.Requete_TeamRecompenses.setInt(1, ID_TEAM);
				ResultSet Recomp = serveur.Bdd.Requete_TeamRecompenses.executeQuery();
				//ResultSet Recomp = serveur.Bdd.Requete.executeQuery("SELECT * FROM $recompenses WHERE id_team=" + ID_TEAM + " ORDER BY date ASC");
				while (Recomp.next()) {
					Chaine += Serveur.$ + Serveur.$ + Recomp.getString("image") + Serveur.$ + Recomp.getString("message");
				}
			} else {
				Chaine = "CxINFO#Team introuvable.";
			}
			JOUEUR.Envoie(Chaine);

		} catch (Exception e) {
			System.err.print("3XC Info_Team "); e.printStackTrace();
		}
	}

	private void Liste_Recompenses_Joueur(Joueur JOUEUR, String CIBLE) {
		
		JOUEUR.Envoie("CxRec#" + CIBLE + "#" + ChaineMedailles(CIBLE));
		
		/*try {
			StringBuilder Chaine = new StringBuilder("CxRec#"+CIBLE+"#");
			serveur.Bdd.Requete_JoueurRecompenses.setString(1, CIBLE);
			ResultSet Recomp = serveur.Bdd.Requete_JoueurRecompenses.executeQuery();
			while (Recomp.next()) {
				Chaine.append(Serveur.$ + Serveur.$ + Recomp.getString("image") + Serveur.$ + Recomp.getString("message"));
			}
			JOUEUR.Envoie(Chaine.toString());
		} catch (Exception e) {
			System.err.print("3XC Liste_Recompenses_Joueur "); e.printStackTrace();
		}*/
	}
	
	public String ChaineMedailles(String CIBLE) {
		try {
			StringBuilder Chaine = new StringBuilder("");
			serveur.Bdd.Requete_JoueurRecompenses.setString(1, CIBLE);
			ResultSet Recomp = serveur.Bdd.Requete_JoueurRecompenses.executeQuery();
			while (Recomp.next()) {
				Chaine.append(Serveur.$ + Serveur.$ + Recomp.getString("image") + Serveur.$ + Recomp.getString("message"));
			}
			
			return Chaine.toString();
		} catch (Exception e) {
			System.err.print("3XC Liste_Recompenses_Joueur "); e.printStackTrace();
			return "";
		}
	}



	private void Liste_Team(Joueur JOUEUR) {
		try {
			ResultSet Resultat = serveur.Bdd.Requete_ListeTeam.executeQuery();
			StringBuilder Chaine = new StringBuilder("TxL#");
			while (Resultat.next()) {
				Chaine.append(Serveur.$ + Serveur.$ + Resultat.getInt("id") + Serveur.$ + Resultat.getString("nom") + Serveur.$ + Resultat.getInt("jeu") + Serveur.$ + Resultat.getInt("membres") + Serveur.$ + Resultat.getDate("officiel") + Serveur.$ + Resultat.getInt("score"));
			}
			CacheListeTeam = Chaine.toString();
			CacheListeTeamAJour = true;                        
			JOUEUR.Envoie(CacheListeTeam);

		} catch (Exception e) {
			System.err.print("3XC Liste_Team "); e.printStackTrace();
		}
	}

	private void Membres_Team(Joueur JOUEUR, int ID_TEAM) {
		try {
			serveur.Bdd.Requete_ListeMembres.setInt(1, ID_TEAM);
			ResultSet Resultat = serveur.Bdd.Requete_ListeMembres.executeQuery();
			StringBuilder Chaine = new StringBuilder("TxM#" + ID_TEAM + "#");
			while (Resultat.next()) {
				Chaine.append(Serveur.$ + Serveur.$ + Resultat.getInt("id_joueur") + Serveur.$ + Resultat.getString("n") + Serveur.$ + Resultat.getInt("grade") + Serveur.$ + Resultat.getInt("role"));
			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Membres_Team "); e.printStackTrace();
		}
	}

	private void Multi_Off(Joueur JOUEUR, String CIBLE) {
		try {

			String IP = "";
			Joueur Cible = serveur.Joueur(CIBLE);
			if (Cible != null) {
				IP = Cible.AdresseIP;
			} else {
				serveur.Bdd.Requete_RetrouverJoueur.setString(1, CIBLE);
				ResultSet Retour = serveur.Bdd.Requete_RetrouverJoueur.executeQuery();
				if (Retour.next()) {
					IP = Retour.getString("ip");
				} else {
					JOUEUR.Envoie("CxINFO#Joueur inconnu.");
				}
			}
			if (IP!=null && !IP.isEmpty()) {
				serveur.Bdd.Requete_ListeMulti.setString(1, IP);
				ResultSet Resultat = serveur.Bdd.Requete_ListeMulti.executeQuery();
				StringBuilder Chaine = new StringBuilder("CxINFO#Multis : ");
				while (Resultat.next()) {
					Chaine.append(Resultat.getString("n") + ", ");
				}
				JOUEUR.Envoie(Chaine.toString());
			} else {
				JOUEUR.Envoie("CxINFO#IP inconnue");
			}




		} catch (Exception e) {
			System.err.print("3XC Multi_off "); e.printStackTrace();
		}
	}

	private void Multi_Pwd(Joueur JOUEUR, String CIBLE) {
		try {

			String PWD = "";
			Joueur Cible = serveur.Joueur(CIBLE);
			if (Cible != null) {
				PWD = Cible.Password;
			} else {
				serveur.Bdd.Requete_RetrouverJoueur.setString(1, CIBLE);
				ResultSet Retour = serveur.Bdd.Requete_RetrouverJoueur.executeQuery();
				if (Retour.next()) {
					PWD = Retour.getString("p");
				} else {
					JOUEUR.Envoie("CxINFO#Joueur inconnu.");
				}
			}
			if (PWD!=null && !PWD.isEmpty()) {
				serveur.Bdd.Requete_ListeMultiPwd.setString(1, PWD);
				ResultSet Resultat = serveur.Bdd.Requete_ListeMultiPwd.executeQuery();
				StringBuilder Chaine = new StringBuilder("CxINFO#Multis pwd : ");
				while (Resultat.next()) {
					Chaine.append(Resultat.getString("n") + ", ");
				}
				JOUEUR.Envoie(Chaine.toString());
			}




		} catch (Exception e) {
			System.err.print("3XC Multi_pwd "); e.printStackTrace();
		}
	}

	private void Flags(Joueur JOUEUR, String CIBLE, String FLAG) {
		try {

			serveur.Bdd.Requete_TransformationFlag.setString(1, FLAG);
			serveur.Bdd.Requete_TransformationFlag.setString(2, CIBLE.toLowerCase());
			serveur.Bdd.Requete_TransformationFlag.execute();
			JOUEUR.Message_Info("Flag : " + CIBLE + " passé en " + FLAG);


		} catch (Exception e) {
			System.err.print("3XC Flags "); e.printStackTrace();
		}
	}

	//Visualisation du flag de CIBLE
	private void FlagsJoueur(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_VisualisationFlag.setString(1, CIBLE.toLowerCase());
			ResultSet SelectJoueur = serveur.Bdd.Requete_VisualisationFlag.executeQuery();
			
			if(SelectJoueur.next()){
				String flag = SelectJoueur.getString(1);
				JOUEUR.Message_Info("Flag : " + CIBLE + " actuellement en " + flag);
			}
			
		} catch (Exception e) {
			System.err.print("3XC Flags "); e.printStackTrace();
		}
	}
	
	private void Demande_Profil(Joueur JOUEUR, String CIBLE) {
		try {
			StringBuilder Message = new StringBuilder("CxSt#");
			serveur.Bdd.Requete_ChargementJoueur.setString(1, CIBLE);
			ResultSet Result = serveur.Bdd.Requete_ChargementJoueur.executeQuery();
			if (Result.next() && Result.getInt("ban") != 2) { // On n'affiche pas les comptes "bloqués invisibles (ban = 2)"
				// on n'affiche pas compte banni à cause des comptes comme Jade etc
				//String Description = (Result.getInt("ban")==1?"Ce compte est banni définitivement.":Result.getString("d"));
				/*String Description = Result.getString("d"); 
				JOUEUR.Envoie("CxSt#" + Result.getString("n") + "#" + Result.getInt("s_ti") + "#"
						+ Result.getInt("sa_pg") + "#" + Result.getInt("sa_rs") // Stat guidage
						+ "#" + Result.getInt("sa_p") + "#" + Result.getInt("sa_rp") // Stat victoires
						+ "#" + Result.getInt("sb_pj") + "#" + Result.getInt("sb_pg") // Stat victoires
						+ "#" + Result.getInt("sb_r") // TPM
						+ "#" + Result.getString("i") + "#" + Description + "#" + (Local.BETA ? "0" : Result.getString("s_dc")) + "#0#" + Result.getInt("ur") // hors ligne
						// Envoie des anciennes stats
						
						// Ajout si le joueur a une team :
						+ (Result.getInt("id_joueur") != 0 ? "#" + Result.getInt("id_team") + "#" + Result.getString("nom") + "#" + Result.getInt("grade") + "#" + Result.getInt("role") + "#" + (Result.getLong("s_tt") < 3600000 * 50 ? "1" : "0") : ""));*/
				
				int Stats_OldJouées ;
				int Stats_OldSauvés ;
				int Stats_OldJGuidés;
				
				Result.getInt("op_sa_pj");
				if (Result.wasNull()) { // $old_parties
					Stats_OldJouées = 0;
					Stats_OldSauvés = 0;
					Stats_OldJGuidés = 0;
				} else {
					Stats_OldJouées = Result.getInt("op_sa_pj");
					Stats_OldSauvés = Result.getInt("op_sa_js");
					Stats_OldJGuidés = Result.getInt("op_sa_jg");
				}
				
				String Description = Result.getString("d"); 
				Message.append(Result.getString("n") + "#");
				Message.append(Result.getInt("s_ti") + "#");
				// Stat guidage
				
				Message.append(Result.getInt("sa_pg") + "#");
				int sa_rs = (Result.getInt("sa_jg") - Stats_OldJGuidés) == 0 ? 0 : (int) (((double)(Result.getInt("sa_js") - Stats_OldSauvés) / (Result.getInt("sa_jg") - Stats_OldJGuidés)) * 10000);
				Message.append(sa_rs + "#");
				// Stat victoires Aaaah!
				Message.append(Result.getInt("sa_p") + "#");
				int sa_rp =(Result.getInt("sa_pj") - Stats_OldJouées) == 0 ? 0 : (int)(((double)Result.getInt("sa_p") / (Result.getInt("sa_pj") - Stats_OldJouées)) * 10000);
				Message.append(sa_rp + "#");
				// Stat victoires Bouboum
				Message.append(Result.getInt("sb_pj") + "#");
				Message.append(Result.getInt("sb_pg") + "#");
				// TPM
				Message.append(Result.getInt("sb_r") + "#"); // Info[8] côté client
				// Anciennes stats
				int sa_guidage = Result.getInt("sa_guidage");
				
				if(Result.wasNull()) {
					Message.append("-1#-1#-1#-1#-1#-1#");
				} else {
					Message.append(sa_guidage + "#");
					Message.append(Result.getInt("sa_pguidage") + "#"); // % => /100
					Message.append(Result.getInt("sa_victoire") + "#");
					Message.append(Result.getInt("sa_pvictoire") + "#"); // % => /100
					Message.append(Result.getInt("sb_victoire") + "#");
					Message.append(Result.getInt("sb_pvictoire") + "#"); // % => /100
				}
				// Général
				Message.append(Joueur.GetCorrectAvatar(Result.getString("i"), Result.getInt("id_team") , serveur ) + "#");
				Message.append(Description + "#");
				Message.append((Local.BETA ? "0" : Result.getString("s_dc")));
				Message.append("#0#");
				
				int uneMedaille = Result.getInt("ur");
				if (uneMedaille == 0) {
					Message.append("0");
				} else {
					Message.append(ChaineMedailles(CIBLE));
				}
				
				if (Result.getInt("id_joueur") != 0) { // Le joueur possède une team
					Message.append("#" + Result.getInt("id_team") + "#");
					Message.append(Result.getString("nom") + "#");
					Message.append(Result.getInt("grade") + "#");
					Message.append(Result.getInt("role") + "#");
					Message.append((Result.getLong("s_tt") < 3600000 * 50 ? "1" : "0"));
				} else {
					Message.append("#0#0#0#0#0");
				}
				
				String elo_flag = Result.getString("elo_flag");
				Message.append("#" + elo_flag.charAt(0) + "#" + elo_flag.charAt(1)+ "#" + elo_flag.charAt(2)); // Elo max Aaaah! ; Elo max Bouboum ; Elo max Forto
				
				JOUEUR.Envoie(Message.toString());
				
				
				/*JOUEUR.Envoie("CxSt#" + Result.getString("n") + "#" + Result.getInt("s_ti") + "#"
						+ Result.getInt("sa_pg") + "#" + Result.getInt("sa_rs") 
						+ "#" + Result.getInt("sa_p") + "#" + Result.getInt("sa_rp") // Stat victoires
						+ "#" + Result.getInt("sb_pj") + "#" + Result.getInt("sb_pg") 
						+ "#" + Result.getInt("sb_r") // TPM
						// Envoie des anciennes stats
						
						
						+ "#" + Result.getString("i") + "#" + Description + "#" + (Local.BETA ? "0" : Result.getString("s_dc")) + "#0#" + Result.getInt("ur") // hors ligne
						// Ajout si le joueur a une team :
						+ (Result.getInt("id_joueur") != 0 ? "#" + Result.getInt("id_team") + "#" + Result.getString("nom") + "#" + Result.getInt("grade") + "#" + Result.getInt("role") + "#" + (Result.getLong("s_tt") < 3600000 * 50 ? "1" : "0") : ""));
			*/
			} else {
				JOUEUR.Envoie("CxINFO#Ce joueur n'existe pas.");
			}

		} catch (Exception e) {
			System.err.print("3XC Demande_Profil "); e.printStackTrace();
		}
	}

	private void Demande_Info(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_ChargementJoueur.setString(1, CIBLE);
			ResultSet Result = serveur.Bdd.Requete_ChargementJoueur.executeQuery();
			if (Result.next()) {
				JOUEUR.Message_Info(CIBLE + (JOUEUR.AutorisationAdmin?" " + Result.getString("ip"):"") + " (hors-ligne) Temps de jeu=" + (Result.getLong("s_tt") / 3600000) + "h "
						+ (Result.getInt("ban") != 0 ? " bandéf" + (Result.getInt("ban") == 2 ? "2" : "") : "") + " " 
						+ (Result.getInt("muteforum") == 1 ? " muteforum" : "") + " "
						+ (Result.getInt("mutecp") == 1 ? " mutecp" : ""));

			} else {
				JOUEUR.Message_Info("Ce joueur n'existe pas.");
			}

		} catch (Exception e) {
			System.err.print("3XC Demande_Info "); e.printStackTrace();
		}
	}

	public void Changer_Password(Joueur JOUEUR, String CIBLE, String PASSWORD) {
		try {
			serveur.Bdd.Requete_ChangerPassword.setString(1, PASSWORD);
			serveur.Bdd.Requete_ChangerPassword.setString(2, CIBLE);
			serveur.Bdd.Requete_ChangerPassword.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET p='" + PASSWORD + "' WHERE n = '" + CIBLE + "' LIMIT 1");
			JOUEUR.Envoie("CxINFO#Mot de passe changé pour " + CIBLE);

		} catch (Exception e) {
			System.err.print("3XC Changer_Password "); e.printStackTrace();
		}
	}
	
	private void Demande_Compte_JPO(Joueur JOUEUR, String CIBLE) {

		try {
			serveur.Bdd.Requete_InfoJoueur.setString(1,CIBLE);
			ResultSet Result = serveur.Bdd.Requete_InfoJoueur.executeQuery();
			//serveur.Bdd.Requete.executeUpdate("SELECT s_dc,s_tt,ban FROM $joueur WHERE n='" + CIBLE + "' LIMIT 1");
			
			if(Result.next()) {
				long TempsDernièreConnexion = Result.getLong("s_dc");
				long TempsJeuCible = Result.getLong("s_tt");
				int CompteBloquéCible = Result.getInt("ban");
				// Dernière Co antérieur à 2 ans && moins de 30 heures de jeu && compte non bloqué 
				if((TempsDernièreConnexion < serveur.JournéePorteOuverte && TempsJeuCible < 30*3600000 && CompteBloquéCible == 0) || JOUEUR.NomJoueur.equals(CIBLE)) { 
					Boolean modifDemande = false, resetHisto=JOUEUR.NomJoueur.equals(CIBLE), mêmeDemande = false, demandeConcurrente = false;
					DemandeCompte demandeExistante = null;
					
					for(DemandeCompte Demande :serveur.ListeDemandeJPO) {

						if (Demande.Demandeur.equals(JOUEUR.NomJoueur)) {
							if (Demande.Cible.equals(CIBLE)) {
								mêmeDemande = true;
							}
							else {							
								modifDemande = true;
								demandeExistante = Demande;
							}
						} else if (Demande.Cible.equals(CIBLE)) {
							demandeConcurrente = true;
						}
					}
					
					
					if(demandeConcurrente) {
						JOUEUR.Envoie("CxINFO#Une demande de récupération du compte "+CIBLE+" a déjà été formulée par un autre joueur.");
						return;
					}
					
					if(mêmeDemande) {
						JOUEUR.Envoie("CxINFO#Vous avez déjà formulé une demande sur le compte "+CIBLE+".");
						return;
					}
					
					if (modifDemande && demandeExistante != null) {
						JOUEUR.Envoie("CxINFO#Annulation de la demande sur le compte "+demandeExistante.Cible+".");
						demandeExistante.Cible = CIBLE;
						if (resetHisto) {								
							JOUEUR.Envoie("CxINFO#Votre demande de nettoyage de votre historique des sanctions a été prise en compte.");
						} else {
							JOUEUR.Envoie("CxINFO#Votre demande de récupération du compte "+CIBLE+" a été prise en compte.");
						}
						System.out.println(serveur.CmdJPO.toUpperCase() + " " + demandeExistante.toString());
						return;
					}

					SimpleDateFormat formatDate = new SimpleDateFormat("[yyyy-MM-dd HH:mm]");
					Date d = new Date();
					String dateDemande = formatDate.format(d);
					DemandeCompte PremièreDemande = new DemandeCompte(JOUEUR.NomJoueur,CIBLE,dateDemande);
					serveur.ListeDemandeJPO.add(PremièreDemande);
					System.out.println(serveur.CmdJPO.toUpperCase() + " " + PremièreDemande.toString());
					if(resetHisto) {
						JOUEUR.Envoie("CxINFO#Votre demande de nettoyage de votre historique des sanctions a été prise en compte.");
					}
					else {
						JOUEUR.Envoie("CxINFO#Votre demande de récupération du compte "+CIBLE+" a été prise en compte.");
					}
				}
				else {
					JOUEUR.Envoie("CxINFO#Il n'est pas possible de récupérer le compte "+CIBLE+".");
				}
			}
			else {
				JOUEUR.Envoie("CxINFO#Ce joueur n'existe pas.");
			}
		}
		catch (SQLException e) {
			System.err.print("3XC Demande_Compte "); e.printStackTrace();
		}

	}

	private void Liste_JPO(Joueur JOUEUR) {
		String Message ="CxINFO#Liste demande JPO :";
		for(DemandeCompte Demande :serveur.ListeDemandeJPO) {
			Message+="\n"+Demande.toString();
		}
		JOUEUR.Envoie(Message);
	}

	private void Valider_Demande_JPO(Joueur JOUEUR, String DEMANDEUR, Boolean VALIDER) {

		for(DemandeCompte Demande : serveur.ListeDemandeJPO) {
			if(Demande.Demandeur.equals(DEMANDEUR)) {
				if(VALIDER) {
					//Nettoyage historique
					if(Demande.Cible.equals(DEMANDEUR)) {
						try {
							serveur.Bdd.Requete_ArchivageSanction.setString(1,DEMANDEUR);
							serveur.Bdd.Requete_ArchivageSanction.setLong(2, serveur.JournéePorteOuverte);
							int UpdateRéussi = serveur.Bdd.Requete_ArchivageSanction.executeUpdate();
							if(UpdateRéussi >= 1) {
								JOUEUR.Commande("note " +DEMANDEUR + " Historique nettoyé.");							
							}
							else {
								JOUEUR.Envoie("CxINFO#Pas de modification de l'historique pour " + DEMANDEUR + ".");
							}
						} catch (SQLException e) {
							System.err.print("3XC ArchivageSanction "); e.printStackTrace();
						}
					}
					//Récupération compte
					else {
						String Cible = Demande.Cible;
						Copie_Password(JOUEUR,DEMANDEUR,Cible);
						
						/*
						  Requete_TeamJoueur = BD.prepareStatement("SELECT id_team,role FROM $membres WHERE id_joueur = ?");
			Requete_TeamEjecterJoueur = BD.prepareStatement("DELETE FROM $membres WHERE id_joueur=? LIMIT 1");
			Requete_TeamPerteJoueur = BD.prepareStatement("UPDATE $teams SET modif=NOW(),membres=membres-1 WHERE id = ? LIMIT 1");
			*/
						int IdCompteDemande = 0;
						int IdTeamCompteDemande = 0;
						
						try {
							IdCompteDemande = ID_Joueur(Cible);
							IdTeamCompteDemande = ID_Team(IdCompteDemande);
							
							if (IdTeamCompteDemande != 0 && IdTeamCompteDemande != -1)
								Supprimer_De_Team(IdCompteDemande, IdTeamCompteDemande, true, null, Cible);
							
						} catch (Exception e) {
							JOUEUR.Envoie("Erreur dans le nettoyage des teams de " + Cible + " (IdJ :" + IdCompteDemande + " ; IdTeam : " + IdTeamCompteDemande + ")");
						}
					}
				}
				else {
					JOUEUR.Envoie("CxINFO#Demande refusée pour " + DEMANDEUR + ".");
				}
				serveur.ListeDemandeJPO.remove(Demande);
				return;
			}
		}
		
		JOUEUR.Envoie("CxINFO#Aucune demande en attente pour " + DEMANDEUR + ".");
	}
	
	public void Bloquer_Compte(Joueur JOUEUR, String CIBLE, String RAISON) {
		try {
			serveur.Bdd.Requete_BloquerCompte.setString(1, CIBLE);
			serveur.Bdd.Requete_BloquerCompte.execute();
			serveur.BOITE.Requete(Boite.LOG_SANCTION, JOUEUR, CIBLE, null, RAISON, "bandef", 0);
			serveur.Message_Modo("Compte " + CIBLE + " bloqué définitivement. Raison : " + RAISON, false);

		} catch (Exception e) {
			System.err.print("3XC Bloquer_Compte "); e.printStackTrace();
		}
	}
	
	public void Bloquer_Compte2(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_BloquerCompte2.setString(1, CIBLE);
			serveur.Bdd.Requete_BloquerCompte2.execute();
			serveur.BOITE.Requete(Boite.LOG_SANCTION, JOUEUR, CIBLE, null, "Bloquage définitif (compte rendu invisible)", "bandef2", 0);
			serveur.Message_Modo("Compte " + CIBLE + " bloqué définitivement + rendu invisible.", false);

		} catch (Exception e) {
			System.err.print("3XC Bloquer_Compte "); e.printStackTrace();
		}
	}  

	public void Mutecp(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_Mutecp.setString(1, CIBLE);
			serveur.Bdd.Requete_Mutecp.execute();

		} catch (Exception e) {
			System.err.print("3XC Mutecp "); e.printStackTrace();
		}
	}

	public void Muteforum(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_Muteforum.setString(1, CIBLE);
			serveur.Bdd.Requete_Muteforum.execute();

		} catch (Exception e) {
			System.err.print("3XC Muteforum "); e.printStackTrace();
		}
	}

	public void Deban(Joueur JOUEUR, String CIBLE, String QUOI) {
		try {
			if (QUOI.equals("ban") || QUOI.equals("mutecp") || QUOI.equals("muteforum")) {
				serveur.Bdd.Requete.executeUpdate("UPDATE `$joueur` SET " + QUOI + "=0 WHERE n = '" + CIBLE + "' LIMIT 1");
			}
		} catch (Exception e) {
			System.err.print("3XC Deban "); e.printStackTrace();
		}
	}
	
	public void Deban_joueur(Joueur JOUEUR, int IdSanction) {
		try {
			serveur.Bdd.Requete_NomIpSanction.setInt(1, IdSanction);
			ResultSet SelectNomIp = serveur.Bdd.Requete_NomIpSanction.executeQuery();

			if (SelectNomIp.next())
			{
				String IP = SelectNomIp.getString("ip");
				String NOM = SelectNomIp.getString("nom_joueur").toLowerCase();
				String TYPE = SelectNomIp.getString("sanction").toLowerCase();
				int FIN = SelectNomIp.getInt("fin");
				if (IP!=null && NOM!=null)
				{
					if(TYPE.equalsIgnoreCase("banni"))
					{
						boolean removeBanNom = serveur.ListeBanniNom.remove(NOM);
						boolean removeBanIp = serveur.ListeBanniIP.remove(IP);

						// Débloquage du compte
						//Deban(JOUEUR,NOM,"ban");
						
						// Retrait du ban dans $joueur et dans $ban_ip
						DeBanIP(JOUEUR,IP);
						Calendar Calendrier = Calendar.getInstance();
						
						long fin = (Calendrier.getTimeInMillis() / 1000L) ;
						int UpdateFin=0;
						if(fin<FIN)
						{
							serveur.Bdd.Requete_ModifierFinSanction.setLong(1,fin);
							serveur.Bdd.Requete_ModifierFinSanction.setInt(2,IdSanction);
							
							// Mise à jour de la fin du ban à l'heure actuelle
							UpdateFin = serveur.Bdd.Requete_ModifierFinSanction.executeUpdate();
						}
						
						if(UpdateFin == 1 || removeBanIp || removeBanNom)
						{
							System.out.println("DB4N [" + Calendrier.getTime() + "] " + JOUEUR.NomJoueur + " (" + JOUEUR.AdresseIP + ") a débanni " + NOM);
							Log_Sanction(JOUEUR, NOM,"-","Joueur déban (sanction "+ IdSanction +")", "noté",0);
							JOUEUR.Envoie("CxINFO#Déban de " + NOM + " effectué.");
						}
						else
						{
							JOUEUR.Envoie("CxINFO#Déban de " + NOM + " déjà effectué.");
						}
					}
					else
					{
						JOUEUR.Envoie("CxINFO#Sanction différente d'un ban.");
					}
				}
				else
				{
					JOUEUR.Envoie("CxINFO#Joueur ou IP inconnu.");
				}
			}
			else
			{
				JOUEUR.Envoie("CxINFO#Sanction inconnue.");
			}

		}
		catch(Exception e)
		{
			System.err.print("3XC Deban_joueur "); e.printStackTrace();
		}
		
	}
	
	public void Fin_sanction(Joueur JOUEUR, int IdSanction)
	{
		try {
			serveur.Bdd.Requete_FinSanction.setInt(1,IdSanction);
			ResultSet SelectSanction = serveur.Bdd.Requete_FinSanction.executeQuery();
			
			if(SelectSanction.next()) {
				
				long finSanction = SelectSanction.getLong("fin");
				SimpleDateFormat formatDate = new SimpleDateFormat("[yyyy-MM-dd HH:mm]");
				Date d = new Date(finSanction*1000);
				String dateFinSanction = formatDate.format(d);
				int tempsSanction = SelectSanction.getInt("temps");
				String NOM = SelectSanction.getString("nom_joueur");
				long timestamp = System.currentTimeMillis() / 1000;
				int tempsRestant = (int)((finSanction-timestamp)/3600);
				
				if(tempsRestant>0) {
					int tempsEcoule = tempsSanction - tempsRestant;
					JOUEUR.Envoie("CxINFO#Fin de la sanction sur " + NOM + " : " + dateFinSanction +"\n"
							+ "Temps écoulé : " + tempsEcoule + " heures\n"
							+ "Temps restant : " +tempsRestant + " heures");
				}
				else {
					JOUEUR.Envoie("CxINFO#Sanction sur " + NOM +" terminée depuis "+ dateFinSanction);
				}
			}
		}
		catch(Exception e) {			
			System.err.print("3XC Fin_sanction "); e.printStackTrace();
		}
	}

	public void Temps_Moins(Joueur JOUEUR, String CIBLE, int TEMPS) {
		try {
			Joueur LaCible = serveur.Joueur(CIBLE);
			String message=null;
			long TempsJoueur, TEMPS_MILLISECONDE;
			
			// Impossible de retirer une valeur négative ou de retirer une trop grande valeur
			if(TEMPS < 0 || TEMPS > 500) {
				TEMPS=0;
				message="Temps à retirer invalide (valeur négative ou trop grande).";
			}
			
			// Si joueur connecté
			if (LaCible!=null) {
				
				// Impossible de retirer plus d'heure que le joueur n'en possède
				TempsJoueur=LaCible.Stats_TempsDeJeu;
				if(TempsJoueur/3600000 - TEMPS < 0) {
					TEMPS=0;
					message="Temps de jeu à retirer supérieur au temps de jeu du joueur.";
				}
				else
				{
					//Application du retrait sur la session en cours
					LaCible.Stats_TempsDeJeu = TempsJoueur -(TEMPS*3600000);
				}
			}
			
			// Si joueur déconnecté
			else {
				TempsJoueur=1;
				
				serveur.Bdd.Requete_TempsJoueur.setString(1, CIBLE);
				ResultSet SelectTemps = serveur.Bdd.Requete_TempsJoueur.executeQuery();
				// serveur.Bdd.Requete.executeQuery("SELECT s_tt FROM $joueur WHERE n= '"+CIBLE+"' LIMIT 1");

				if(SelectTemps.next()){
					TempsJoueur=SelectTemps.getLong("s_tt");
										
					// Impossible de retirer plus d'heure que le joueur n'en possède
					if(TempsJoueur/3600000 - TEMPS < 0) {
						TEMPS=0;						
						message="Temps de jeu à retirer supérieur au temps de jeu du joueur.";
					}
				}
				else {
					message="Ce joueur n'existe pas.";
				}
			
			}
			TempsJoueur=TempsJoueur/3600000;
			TEMPS_MILLISECONDE=TEMPS*3600000;
			if(message == null) {
				message="Retrait de "+TEMPS+" heure(s) sur "+TempsJoueur+" heure(s) pour " + CIBLE +".";
				serveur.Bdd.Requete_ResetTemps.setLong(1, TEMPS_MILLISECONDE);
				serveur.Bdd.Requete_ResetTemps.setString(2, CIBLE);
				serveur.Bdd.Requete_ResetTemps.executeUpdate();
				//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET s_tt=s_tt - "+TEMPS_MILLISECONDE+" WHERE n = '" + CIBLE + "' LIMIT 1");
			}
			JOUEUR.Envoie("CxINFO#"+message);

		} catch (Exception e) {
			System.err.print("3XC Temps "); e.printStackTrace();
		}
	}


	public void Autoriser_Chgpass(Joueur JOUEUR, String CIBLE, String AUTO) {
		try {
			serveur.Bdd.Requete_AutoriserChgpass.setString(1, AUTO);
			serveur.Bdd.Requete_AutoriserChgpass.setString(2, CIBLE);
			serveur.Bdd.Requete_AutoriserChgpass.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET chgpass='"+AUTO+"' WHERE n = '" + CIBLE + "' LIMIT 1");
			if (AUTO.equals("1")) {
				JOUEUR.Envoie("CxINFO#"+CIBLE+ " peut désormais formuler une demande de changement de password.");
				System.out.println("C0M APW " + JOUEUR.NomJoueur + " " + CIBLE);
			}

		} catch (Exception e) {
			System.err.print("3XC Autoriser_Pass "); e.printStackTrace();
		}
	}               

	public void Valider_Chgpass(Joueur JOUEUR, String CIBLE, String ETAT) {


		try {

			// on récupère le password à mettre
			serveur.Bdd.Requete_SelectChgpass.setString(1, CIBLE);
			ResultSet Dem = serveur.Bdd.Requete_SelectChgpass.executeQuery();
			//ResultSet Dem = serveur.Bdd.Requete.executeQuery("SELECT newp FROM $chgpass WHERE nom='" + CIBLE + "' and etat=0 ORDER BY id ASC LIMIT 1");
			if (ETAT.equals("1")) {
				// requête validée
				if (Dem.next()) {
					String PASSWORD = Dem.getString("newp");
					Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
					Matcher matcher = pattern.matcher(PASSWORD);
					if (matcher.find()) {
						return;
					}
					serveur.Bdd.Requete_ValiderChgpass.setString(1, PASSWORD);
					serveur.Bdd.Requete_ValiderChgpass.setString(2, CIBLE);
					serveur.Bdd.Requete_ValiderChgpass.executeUpdate();
					//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET chgpass='0',p='"+PASSWORD+"' WHERE n = '" + CIBLE + "' LIMIT 1");

					JOUEUR.Envoie("CxINFO#Changement validé pour "+CIBLE);
				}
			} else if (ETAT.equals("2")){
				// requête refusée
				serveur.Bdd.Requete_RefuserChgpass.setString(1, CIBLE);
				serveur.Bdd.Requete_RefuserChgpass.executeUpdate();
				//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET chgpass='0' WHERE n = '" + CIBLE + "' LIMIT 1");
				JOUEUR.Envoie("CxINFO#Demande refusée pour " + CIBLE);
			}
			// on dit que la requete a été traitée
			serveur.Bdd.Requete_TraiterChgpass.setString(1, ETAT);
			serveur.Bdd.Requete_TraiterChgpass.setString(2, CIBLE);
			serveur.Bdd.Requete_TraiterChgpass.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $chgpass SET etat='"+ETAT+"' WHERE nom = '" + CIBLE + "' and etat=0 ORDER BY id ASC LIMIT 1");

		} catch (Exception e) {
			System.err.print("3XC Valider_pass "); e.printStackTrace();
		}
	} 

	public void Chgpass(Joueur JOUEUR, String PASSWORD, String MESSAGE) {
		try {
			// nom,oldp,newp,ip,message
			serveur.Bdd.Requete_Chgpass.setString(1, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_Chgpass.setString(2, JOUEUR.Password);
			serveur.Bdd.Requete_Chgpass.setString(3, PASSWORD);
			serveur.Bdd.Requete_Chgpass.setString(4, JOUEUR.AdresseIP);
			serveur.Bdd.Requete_Chgpass.setString(5, MESSAGE);
			serveur.Bdd.Requete_Chgpass.execute();

			// on prévient le joueur
			JOUEUR.Envoie("CxCPWD#2");

			// on valide
			serveur.BOITE.Requete(Boite.AUTORISER_CHGPASS, JOUEUR, JOUEUR.NomJoueur, "2");
			JOUEUR.AutorisationChgPass = "2";

		} catch (Exception e) {
			System.err.print("3XC ChgPass "); e.printStackTrace();
		}
	}

	public void BanIP(Joueur JOUEUR, String CIBLE) {
		try {
			ResultSet Resultat = serveur.Bdd.Requete.executeQuery("SELECT * from `$ban_ip` where ip = '" + CIBLE + "'");
			if (!Resultat.next())
			{
				serveur.Bdd.Requete.executeUpdate("INSERT INTO `$ban_ip` (ip,actif) VALUES ('"+CIBLE+"',1)");
			} else {
				serveur.Bdd.Requete.executeUpdate("UPDATE `$ban_ip` set actif = 1 WHERE ip = '" + CIBLE + "'");
			}

		} catch (Exception e) {
			System.err.print("3XC BanIP "); e.printStackTrace();
		}
	}

	public void BanHost(Joueur JOUEUR, String CIBLE) {
		try {
			ResultSet Resultat = serveur.Bdd.Requete.executeQuery("SELECT * from `$ban_host` where host = '" + CIBLE + "'");
			if (!Resultat.next()) {
				serveur.Bdd.Requete.executeUpdate("INSERT INTO `$ban_host` (host,actif) VALUES ('"+CIBLE+"',1)");
			} else {
				serveur.Bdd.Requete.executeUpdate("UPDATE `$ban_host` set actif = 1 WHERE host = '" + CIBLE + "'");
			}
		} catch (Exception e) {
			System.err.print("3XC BanHost "); e.printStackTrace();
		}
	}

	public void DeBanIP(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete.executeUpdate("UPDATE `$ban_ip` SET actif=0 WHERE ip='" + CIBLE + "'");
		} catch (Exception e) {
			System.err.print("3XC DeBanIP "); e.printStackTrace();
		}
	}

	public void DeBanHost(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete.executeUpdate("UPDATE `$ban_host` SET actif=0 WHERE host='" + CIBLE + "'");
		} catch (Exception e) {
			System.err.print("3XC DeBanHost "); e.printStackTrace();
		}
	}

	public void LoadBanDef(Joueur JOUEUR) {

		try {
			serveur.ListeRefusHost.clear();
			ResultSet Resultat = serveur.Bdd.Requete_ReloadBanHost.executeQuery();
			while (Resultat.next()) {
				String Host = Resultat.getString("host");
				serveur.ListeRefusHost.add(Host);
			}
			serveur.ListeRefusIP.clear();
			serveur.ListeBanniIP.clear();
			Resultat = serveur.Bdd.Requete_ReloadBanIP.executeQuery();
			while (Resultat.next()) {
				String IP = Resultat.getString("ip");
				if (IP.endsWith(".")) {
					serveur.ListeRefusIP.add(IP);
				} else {
					serveur.ListeBanniIP.add(IP);
				}
			}
			String Message = "\nBan host reloadés : " + serveur.ListeRefusHost.size() + "\n"
					+ "Ban ip reloadés : " + serveur.ListeBanniIP.size() + "\n"
					+ "Refus ip reloadés : " + serveur.ListeRefusIP.size();
			if (JOUEUR!=null) {

				JOUEUR.Message_Info(Message);
			}
			System.out.println(Message);
		} catch (Exception e) {
			System.err.print("3XC Reload_BanDef "); e.printStackTrace();
		}


	}

	public void Load_Sanctions(Joueur JOUEUR) {
		serveur.Reload_Sanctions();
	}

	public void Copie_Password(Joueur JOUEUR, String DEMANDEUR, String CIBLE) {
		try {
			// on récupère le password à copier
			serveur.Bdd.Requete_CopiePwd.setString(1, DEMANDEUR);
			ResultSet Dem = serveur.Bdd.Requete_CopiePwd.executeQuery();
			//ResultSet Dem = serveur.Bdd.Requete.executeQuery("SELECT p FROM $joueur WHERE n='" + DEMANDEUR + "' LIMIT 1");
			if (Dem.next()) {
				String Password = Dem.getString("p");
				// on récupère l'ancien password de la cible pour les logs
				serveur.Bdd.Requete_CopiePwd.setString(1,CIBLE);
				ResultSet Cib = serveur.Bdd.Requete_CopiePwd.executeQuery();
				//ResultSet Cib = serveur.Bdd.Requete_CopiePwd.executeQuery("SELECT p FROM $joueur WHERE n='" + CIBLE + "' LIMIT 1");
				if (Cib.next()) {
					String OldPassword = Cib.getString("p");
					serveur.Bdd.Requete_ValiderChgpass.setString(1, Password);
					serveur.Bdd.Requete_ValiderChgpass.setString(2, CIBLE);
					serveur.Bdd.Requete_ValiderChgpass.executeUpdate();
					//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET chgpass='0',p='" + Password + "' WHERE n = '" + CIBLE + "' LIMIT 1");
					System.out.println("CHGP4SS " + DEMANDEUR + " " + CIBLE + " " + OldPassword);
					JOUEUR.Envoie("CxINFO#Le mot de passe de " + DEMANDEUR + " a été copié sur " + CIBLE + ".");
				}                                
			}
		} catch (Exception e) {
			System.err.print("3XC CopiePassword "); e.printStackTrace();
		}
	}

	private void Liste_Chgpass(Joueur JOUEUR) {
		try {
			ResultSet Resultat = serveur.Bdd.Requete_ListeChgpass.executeQuery();
			StringBuilder Chaine = new StringBuilder("CxINFO#Liste des demandes de changement de password : \n");
			while (Resultat.next()) {
				Chaine.append("\n--------\nchpwd " + Resultat.getString("ip") + " " + Resultat.getString("nom") + "\n" + Resultat.getString("message"));
			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Liste_ChgPass "); e.printStackTrace();
		}
	}        

	public void Postit_Topic(Joueur JOUEUR, String TOPIC) {
		try {
			serveur.Bdd.Requete_ForumPostit.setString(1, TOPIC);
			serveur.Bdd.Requete_ForumPostit.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $forum_s SET pi=NOT pi WHERE id = " + TOPIC + " LIMIT 1");
			CacheListeSujet.remove(JOUEUR.ForumEnCours);
			Forum_Envoie_Liste_Sujet(JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Postit "); e.printStackTrace();
		}
	}

	public void Topic_Anim(Joueur JOUEUR, int TOPIC) {
		try {
			serveur.Bdd.Requete_ForumTopicAnim.setInt(1, TOPIC);
			serveur.Bdd.Requete_ForumTopicAnim.executeUpdate();
			CacheListeSujet.remove(JOUEUR.ForumEnCours);
			Forum_Envoie_Liste_Sujet(JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Topic_Anim "); e.printStackTrace();
		}
	}
	
	private void Ajouter_Membre(Joueur JOUEUR, String CIBLE, int IdTeamCible) {
		try {
			Integer ID_JOUEUR = GetId(JOUEUR, CIBLE);
			if (ID_JOUEUR != null) {
				serveur.BOITE.Requete(Boite.REJOINDRE_TEAM, JOUEUR, ID_JOUEUR, IdTeamCible);
			}
		} catch (Exception e) {
			System.err.print("3XC Ajouter_Membre "); e.printStackTrace();
		}
	}

	private void Ajouter_Recompense(Joueur JOUEUR, int IdTeamCible, String IMAGE, String MESSAGE) {
		try {
			serveur.Bdd.Requete_AjouterRecompense.setInt(1, IdTeamCible);
			serveur.Bdd.Requete_AjouterRecompense.setString(2, IMAGE);
			serveur.Bdd.Requete_AjouterRecompense.setString(3, MESSAGE);
			serveur.Bdd.Requete_AjouterRecompense.executeUpdate();
			JOUEUR.Envoie("CxINFO#Récompense " + IMAGE + " ajoutée à la team " + IdTeamCible + " : " + MESSAGE);
		} catch (Exception e) {
			System.err.print("3XC Ajouter_Recompense "); e.printStackTrace();
		}
	}

	private void Ajouter_Recompense_Joueur(Joueur JOUEUR, String CIBLE, String IMAGE, String MESSAGE) {
		try {
			serveur.Bdd.Requete_AjouterRecompenseJoueur.setString(1, CIBLE);
			serveur.Bdd.Requete_AjouterRecompenseJoueur.setString(2, IMAGE);
			serveur.Bdd.Requete_AjouterRecompenseJoueur.setString(3, MESSAGE);
			serveur.Bdd.Requete_AjouterRecompenseJoueur.executeUpdate();                        
			JOUEUR.Envoie("CxINFO#Récompense " + IMAGE + " ajoutée au joueur " + CIBLE + " : " + MESSAGE);
			serveur.Bdd.Requete_AjouterRecompenseJoueurFlag.setString(1, CIBLE);
			serveur.Bdd.Requete_AjouterRecompenseJoueurFlag.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Ajouter_Recompense_Joueur "); e.printStackTrace();
		}
	}

	private void Supprimer_Recompense_Joueur(Joueur JOUEUR, String CIBLE, int NUMERO) {
		try {
			if (NUMERO > 0) {
				serveur.Bdd.Requete_SupprimerRecompenseJoueur.setString(1, CIBLE);
				serveur.Bdd.Requete_SupprimerRecompenseJoueur.setString(2, CIBLE);
				serveur.Bdd.Requete_SupprimerRecompenseJoueur.setInt(3, NUMERO-1);
				serveur.Bdd.Requete_SupprimerRecompenseJoueur.executeUpdate();
				JOUEUR.Envoie("CxINFO#Récompense " + NUMERO + " supprimée au joueur " + CIBLE);
				serveur.Bdd.Requete_SupprimerRecompenseJoueurFlag.setString(1, CIBLE);
				serveur.Bdd.Requete_SupprimerRecompenseJoueurFlag.setString(2, CIBLE);
				serveur.Bdd.Requete_SupprimerRecompenseJoueurFlag.executeUpdate();
			}
		} catch (Exception e) {
			System.err.print("3XC Supprimer_Recompense_Joueur "); e.printStackTrace();
		}
	}

	private void Supprimer_Recompenses(Joueur JOUEUR, int IdTeamCible) {
		try {
			serveur.Bdd.Requete_SupprimerRecompenses.setInt(1, IdTeamCible);
			serveur.Bdd.Requete_SupprimerRecompenses.executeUpdate();
			//JOUEUR.Envoie("CxINFO#Récompenses supprimées avec succès.");
		} catch (Exception e) {
			System.err.print("3XC Supprimer_Recompense "); e.printStackTrace();
		}
	}
	
	private String Forum_Genere_Liste_Teams(Joueur JOUEUR) {
		StringBuilder RechercheForumTeam = new StringBuilder("");
		if (serveur.ListeTeamAmie.get(JOUEUR.IdTeam) != null || (JOUEUR.IdTeam != 75135 && JOUEUR.AutorisationGestionElo) || ((JOUEUR.AutorisationModoCartes || JOUEUR.AutorisationModoCartesRally) && JOUEUR.IdTeam != 75215)) { // La team du joueur est en fusion
			ArrayList<Integer> TeamsEnFusion;
			
			if (serveur.ListeTeamAmie.get(JOUEUR.IdTeam) == null) {
				TeamsEnFusion = new ArrayList<Integer>();
				TeamsEnFusion.add(JOUEUR.IdTeam);
			} else {
				TeamsEnFusion = new ArrayList<Integer>(serveur.ListeTeamAmie.get(JOUEUR.IdTeam).Alliance);
			}
			
			if (JOUEUR.AutorisationGestionElo && JOUEUR.IdTeam != 75135) {
				TeamsEnFusion.add(75135);
			}
			if ((JOUEUR.AutorisationModoCartes || JOUEUR.AutorisationModoCartesRally) && JOUEUR.IdTeam != 75215) {
				TeamsEnFusion.add(75215);
			}
			
			for (int i = 0; i < TeamsEnFusion.size(); ++i) {
				if (i == 0) {
					RechercheForumTeam.append("t LIKE '[" + TeamsEnFusion.get(i) + "]%'");
				} else {
					RechercheForumTeam.append(" || t LIKE '[" + TeamsEnFusion.get(i) + "]%'");
				}
			}
		} else { // Pas de fusion : on affiche uniquement la team du joueur
			RechercheForumTeam.append("t LIKE '[" + JOUEUR.IdTeam + "]%'");
		}
		return RechercheForumTeam.toString();
	}
	
	// Envoie liste sujets
	private void Forum_Envoie_Liste_Sujet(Joueur JOUEUR) {
		if (CacheListeSujet.containsKey(JOUEUR.ForumEnCours) && JOUEUR.PageForumEnCours == 0 && JOUEUR.ForumEnCours != Serveur.FORUM_TEAM) {
			JOUEUR.MessageBoite.add(CacheListeSujet.get(JOUEUR.ForumEnCours));
		} else {
			try {
				String RechercheForumTeam = "";

				if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM) { // Préparation des conditions à ajouter pour avoir les bons topics affichés
					RechercheForumTeam = Forum_Genere_Liste_Teams(JOUEUR);
				}
				
				ResultSet Retour;
				
				if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM) {
					String Requete = "SELECT count(*) FROM `$forum_s` WHERE f=" + Serveur.FORUM_TEAM + " AND ("
									+ RechercheForumTeam + ") LIMIT 1";
					Retour = serveur.Bdd.Requete.executeQuery(Requete);
				} else {
					serveur.Bdd.Requete_SelectionNombreSujet.setInt(1, JOUEUR.ForumEnCours);
					Retour = serveur.Bdd.Requete_SelectionNombreSujet.executeQuery();
				}

				if (Retour.next()) {
					int NombreSujet = Retour.getInt(1);
					StringBuilder Texte = new StringBuilder("FoF#0#" + JOUEUR.PageForumEnCours + "#" + (1 + NombreSujet / 50));
					ResultSet ListeSujet;
					
					if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM) {
						String Requete = "SELECT id,d,a,i,t,c,m,p,x,xm,xf,pi,an FROM `$forum_s` WHERE f=" + Serveur.FORUM_TEAM + " AND ("
									  	+ RechercheForumTeam + ") ORDER BY pi DESC, d DESC LIMIT 50";
						ListeSujet = serveur.Bdd.Requete.executeQuery(Requete);
					} else {
						serveur.Bdd.Requete_ListeSujet.setInt(1, JOUEUR.ForumEnCours);
						serveur.Bdd.Requete_ListeSujet.setInt(2, 50 * JOUEUR.PageForumEnCours);
						Retour = serveur.Bdd.Requete_SelectionNombreSujet.executeQuery();
						ListeSujet = serveur.Bdd.Requete_ListeSujet.executeQuery();
					}
					
					while (ListeSujet.next()) {
						Texte.append(Charge_Sujet_Forum(JOUEUR, ListeSujet));
					}
					//
					String ChaineListeSujet = Texte.toString();
					if (JOUEUR.PageForumEnCours == 0) {
						CacheListeSujet.put(JOUEUR.ForumEnCours, ChaineListeSujet);
					}
					JOUEUR.MessageBoite.add(ChaineListeSujet);
				}
			} catch (Exception e) {
				System.err.print("3XC Envoie_Liste_Sujets "); e.printStackTrace();
			}
		}
	}
	
	private String Charge_Sujet_Forum(Joueur JOUEUR, ResultSet ListeSujet) {
		/* 
		 Charge un sujet du forum à partir d'un ResultSet et le formate en String pour qu'il soit
		 compréhensible par le client. Nécessite l'utilisation de la requête <Requete_ListeSujet> (ou similaire)
		 au préalable.
		*/
		
		StringBuilder Texte = new StringBuilder("");
		
		try {
			String Titre = ListeSujet.getString(5);
			
			int IdTeamTopic = 1;
			if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM && Titre.charAt(0) == '[') { // Les topics team commencent par "[ID_TEAM] ..."
				try {
					IdTeamTopic = Integer.valueOf(Titre.substring(1, Titre.indexOf(']', 1)));
				} catch (Exception e) {}
			}
			
			// On vérifie si la team du joueur et celle dont c'est le topic autorise les autres teams de la fusion à voir leur topic team
			// pas de check pour ceux qui gèrent le tri team ou pour les respo elo si c'est la team de l'elo
			if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM && !JOUEUR.AutorisationModoTeam && !Titre.startsWith("[" + JOUEUR.IdTeam + "]")
				&& !(JOUEUR.AutorisationGestionElo && Titre.startsWith("[75135]"))
				&& !((JOUEUR.AutorisationModoCartes || JOUEUR.AutorisationModoCartesRally) && Titre.startsWith("[75215]"))) {
				
				serveur.Bdd.Requete_TeamGetPartageTopic.setInt(1, JOUEUR.IdTeam);
				
				ResultSet EtatPartage = serveur.Bdd.Requete_TeamGetPartageTopic.executeQuery();
				if (EtatPartage.next()) {
					if (EtatPartage.getBoolean(1)) { // La team partage aussi son topic -> elle peut voir celui des autres
						serveur.Bdd.Requete_TeamGetPartageTopic.setInt(1, IdTeamTopic);
						
						EtatPartage = serveur.Bdd.Requete_TeamGetPartageTopic.executeQuery();
						if (EtatPartage.next()) {
							if (!EtatPartage.getBoolean(1)) {
								return ""; // La team dont c'est le topic ne partage pas le sien
							}
						}
					} else {
						return "";
					}
				}
			}
			
			
			String Modo = FermetureModo(ListeSujet);
			
			Texte.append("#" + ListeSujet.getString(1) + "#" + ListeSujet.getString(2) + "#" + ListeSujet.getString(3) + "#");
			
			if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM) { // i est le blason team
				String Blason = GetBlason(IdTeamTopic);
				if (Blason != null) { // Pas d'erreur
					Texte.append(Blason);
				} else { // Erreur -> Avatar du créateur du topic
					Texte.append(ListeSujet.getString(4));
				}
			} else { // Recherche normale (avatar du posteur)
				Texte.append(ListeSujet.getString(4));
			}
			
			Texte.append("#" + ListeSujet.getString(5) + "#" + ListeSujet.getString(6) + "#" + ListeSujet.getString(7) + "#" + ListeSujet.getString(8) + "#" + ListeSujet.getString(9) + "#" + (JOUEUR.AutorisationModoForum ? Modo : "-") + "#" + ListeSujet.getString(12)+ "#" + ListeSujet.getString(13));
		} catch (Exception e) {
			return "";
		}
		
		return Texte.toString();
	}

	private String FermetureModo(ResultSet ListeSujet) throws SQLException {
		String Modo = "-";
		if (ListeSujet.getString(9).equals("1")) {
			int Flag = ListeSujet.getInt(11);
			if (Flag == 1) {
				Modo = "Modérateur";
			} else {
				Modo = ListeSujet.getString(10);
				if (Modo == null || Modo.equals("")) {
					Modo = "-";
				}
			}
		}
		return Modo;
	}

	// recherche
	private void Forum_Recherche_Sujet(Joueur JOUEUR, String RECHERCHE) {

		try {
			StringBuilder Texte = new StringBuilder("FoF#1#" + JOUEUR.PageForumEnCours + "#1");
			ResultSet ListeSujet;
			
			if (JOUEUR.ForumEnCours != Serveur.FORUM_TEAM || JOUEUR.AutorisationModoTeam) {
				serveur.Bdd.Requete_RechercheListeSujet.setInt(1, JOUEUR.ForumEnCours);
				
				serveur.Bdd.Requete_RechercheListeSujet.setString(2, "%" + RECHERCHE + "%");
				if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM) {
					System.out.println("C0M RFT " + JOUEUR.AdresseIP + " : " + RECHERCHE); // "Recherche Forum Team"
				}
				
				serveur.Bdd.Requete_RechercheListeSujet.setInt(3, 50 * JOUEUR.PageForumEnCours);
				
				ListeSujet = serveur.Bdd.Requete_RechercheListeSujet.executeQuery();
			} else {
				String Requete = "SELECT id,d,a,i,t,c,m,p,x,xm,xf,pi,an FROM `$forum_s` WHERE f=" + Serveur.FORUM_TEAM + " AND ("
						         + Forum_Genere_Liste_Teams(JOUEUR) + ") and t like \"%" + RECHERCHE + "%\" ORDER BY pi DESC, d DESC LIMIT 50";
				ListeSujet = serveur.Bdd.Requete.executeQuery(Requete);
			}
			
			while (ListeSujet.next()) {
				Texte.append(Charge_Sujet_Forum(JOUEUR, ListeSujet));
			}
			//
			String ChaineListeSujet = Texte.toString();
			JOUEUR.MessageBoite.add(ChaineListeSujet);
		} catch (Exception e) {
			System.err.print("3XC Recherche_Sujet "); e.printStackTrace();
		}

	}

	// recherche par auteur //dupliqué
	private void Forum_Recherche_Sujet_Auteur(Joueur JOUEUR, String RECHERCHE) {
		try {
			StringBuilder Texte = new StringBuilder("FoF#1#" + JOUEUR.PageForumEnCours + "#1");
			ResultSet ListeSujet;
			
			if (JOUEUR.ForumEnCours != Serveur.FORUM_TEAM || JOUEUR.AutorisationModoTeam) {
				serveur.Bdd.Requete_RechercheListeSujetParAuteur.setInt(1, JOUEUR.ForumEnCours);
				serveur.Bdd.Requete_RechercheListeSujetParAuteur.setString(2, RECHERCHE);
				serveur.Bdd.Requete_RechercheListeSujetParAuteur.setInt(3, 50 * JOUEUR.PageForumEnCours);
				
				ListeSujet = serveur.Bdd.Requete_RechercheListeSujetParAuteur.executeQuery();
				
				if (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM) {
					System.out.println("C0M RFT " + JOUEUR.AdresseIP + " : " + RECHERCHE); // "Recherche Forum Team"
				}
			} else {
				String Requete = "SELECT id,d,a,i,t,c,m,p,x,xm,xf,pi,an FROM `$forum_s` WHERE f=" + Serveur.FORUM_TEAM + " AND ("
						         + Forum_Genere_Liste_Teams(JOUEUR) + ") and a=\"" + RECHERCHE + "\" ORDER BY pi DESC, d DESC LIMIT 50";
				ListeSujet = serveur.Bdd.Requete.executeQuery(Requete);
			}
			
			while (ListeSujet.next()) {
				Texte.append(Charge_Sujet_Forum(JOUEUR, ListeSujet));
			}
			//
			String ChaineListeSujet = Texte.toString();
			JOUEUR.MessageBoite.add(ChaineListeSujet);
		} catch (Exception e) {
			System.err.print("3XC Recherche_Sujet_Auteur "); e.printStackTrace();
		}

	}

	// Envoie des messages d'un sujet
	private void Forum_Envoie_Sujet(Joueur JOUEUR, int NUM_SUJET, int PAGE) {
		Sujet LeSujet = CacheSujet.get(NUM_SUJET);
		if (LeSujet != null && LeSujet.getPages().containsKey(PAGE)) {
			int IdForum = LeSujet.getForum();
			if (JOUEUR.AccèsForumsSpéciaux(IdForum)) {
				JOUEUR.PageEnCours = PAGE;
				JOUEUR.SujetEnCours = NUM_SUJET;
				JOUEUR.MessageBoite.add(LeSujet.getPages().get(PAGE));
			}
		} else {
			try {
				serveur.Bdd.Requete_SelectionNombreMessage.setInt(1, NUM_SUJET);
				ResultSet Retour = serveur.Bdd.Requete_SelectionNombreMessage.executeQuery();
				int NombreMessage = 0;
				int IdForum = 3;
				String Titre = "";
				if (Retour.next()) {
					NombreMessage = Retour.getInt(1);
					IdForum = Retour.getInt(2);
					Titre = Retour.getString("t");
				}
				if (!JOUEUR.AccèsForumsSpéciaux(IdForum)) {
					JOUEUR.DemandeDeco = true;
					JOUEUR.RaisonDeco = "Client modifié";
					return;
				}

				//
				int Page;
				if (PAGE == -1) {
					Page = (NombreMessage - 1) / 20;
				} else {
					Page = PAGE;
				}

				JOUEUR.PageEnCours = Page;
				JOUEUR.SujetEnCours = NUM_SUJET;
				int LimiteDébut = Page * 20;
				int LimiteFin = 20;

				serveur.Bdd.Requete_ListeMessagesSujet.setInt(1, NUM_SUJET);
				serveur.Bdd.Requete_ListeMessagesSujet.setInt(2, LimiteDébut);
				serveur.Bdd.Requete_ListeMessagesSujet.setInt(3, LimiteFin);
				ResultSet LsMessage = serveur.Bdd.Requete_ListeMessagesSujet.executeQuery();

				StringBuilder Envoie = new StringBuilder("FoS#" + NombreMessage + "#" + JOUEUR.PageEnCours);
				while (LsMessage.next()) {
					Envoie.append("#" + LsMessage.getString(1)
							+ "#" + LsMessage.getString(2) +
							"#" + LsMessage.getString(3) +
							"#" + LsMessage.getString(4) +
							"#" + LsMessage.getString(5) +
							"#" + (LsMessage.getString(6)!=null?LsMessage.getString(6):"") +
							"#" + LsMessage.getString(7));
				}
				String ChaineSujet = Envoie.toString();
				Hashtable<Integer, String> PageSujet;
				if (CacheSujet.containsKey(NUM_SUJET)) {
					PageSujet = CacheSujet.get(NUM_SUJET).getPages();
				} else {
					PageSujet = new Hashtable<Integer, String>();
					Sujet NouveauSujet = new Sujet(Titre,IdForum,PageSujet);
					CacheSujet.put(NUM_SUJET, NouveauSujet);
				}
				PageSujet.put(JOUEUR.PageEnCours, ChaineSujet);
				JOUEUR.MessageBoite.add(ChaineSujet);
			} catch (Exception e) {
				System.err.print("3XC Envoie_Sujet "); e.printStackTrace();
			}
		}
	}

	// Envoie sondage
	private void Envoie_Sondage(Joueur JOUEUR, int NUM_SONDAGE) {
		try{
			serveur.Bdd.Requete_SondageQuestions.setInt(1, NUM_SONDAGE);
			ResultSet Sondage = serveur.Bdd.Requete_SondageQuestions.executeQuery();
			//ResultSet Sondage = serveur.Bdd.Requete.executeQuery("SELECT * FROM $poll_q WHERE id_vote=" + NUM_SONDAGE);
			if (Sondage.next()) {
				StringBuilder Envoie = new StringBuilder("CxPoll#"+NUM_SONDAGE+"#"+Sondage.getString("question")+"#");
				serveur.Bdd.Requete_SondageRéponses.setInt(1, NUM_SONDAGE);
				ResultSet Réponses = serveur.Bdd.Requete_SondageRéponses.executeQuery();
				//ResultSet Réponses = serveur.Bdd.Requete.executeQuery("SELECT * FROM $poll_r WHERE id_vote=" + NUM_SONDAGE);
				while (Réponses.next()) {
					Envoie.append(";" + Réponses.getString("reponse"));
				}
				JOUEUR.MessageBoite.add(Envoie.toString());
			}

		} catch (Exception e) {
			System.err.print("3XC Envoie_Sondage "); e.printStackTrace();
		}

	}

	private boolean Forum_Nouveau_Message(Joueur JOUEUR, int NUM_SUJET, String MESSAGE, boolean ANONYME) {
		CacheListeSujet.remove(JOUEUR.ForumEnCours);
		CacheSujet.remove(NUM_SUJET);
		try {
			serveur.Bdd.Requete_QuestionSujetFerme.setInt(1, NUM_SUJET);
			ResultSet Sujet = serveur.Bdd.Requete_QuestionSujetFerme.executeQuery();
			if (Sujet.next()) {
				boolean SujetOuvert = Sujet.getInt(1) == 0;
				if (SujetOuvert || JOUEUR.AutorisationModoForum) {
					String Nom = JOUEUR.NomJoueur;
					String Ava = JOUEUR.Avatar;
					if (ANONYME && (JOUEUR.AutorisationModo || JOUEUR.AutorisationHelper)) {
						Nom = "Moderateur";
						Ava = "_m";//"6e24f116.png";
					} else if (ANONYME && JOUEUR.AutorisationModoForum) {
						Nom = "Forum";
						Ava = "_m";
					} else if (ANONYME && JOUEUR.AutorisationDev) {
						Nom = "Développeur";
						Ava = "_m";
					}
					// Enregistrement nouveau message
					serveur.Bdd.Requete_NouveauMessage.setInt(1, NUM_SUJET);
					serveur.Bdd.Requete_NouveauMessage.setString(2, Nom);
					serveur.Bdd.Requete_NouveauMessage.setString(3, Ava);
					serveur.Bdd.Requete_NouveauMessage.setString(4, MESSAGE);
					serveur.Bdd.Requete_NouveauMessage.setString(5, JOUEUR.AdresseIP);
					serveur.Bdd.Requete_NouveauMessage.execute();
					// Remontage du sujet
					serveur.Bdd.Requete_UpSujet.setString(1, Nom);
					serveur.Bdd.Requete_UpSujet.setInt(2, NUM_SUJET);
					serveur.Bdd.Requete_UpSujet.execute();
					//
					//	Stat_MessageForum++;
					if (serveur.ListeSurveillanceForum.contains(JOUEUR.NomJoueur.toLowerCase()) && JOUEUR.ForumEnCours != Serveur.FORUM_TEAM) {
						int Forum = Sujet.getInt(2);
						if (Forum!=0) {
							serveur.Avertissement_ModoForum(JOUEUR.NomJoueur + " a posté un nouveau message sur ["+Forum+"] "+Sujet.getString(3)+" : " + (MESSAGE.length()<=100?MESSAGE:MESSAGE.substring(0,100)), false);
						}
					}
					return true;
				}
			}
			Forum_Envoie_Liste_Sujet(JOUEUR);
			return false;

		} catch (Exception e) {
			System.err.print("3XC Nouveau_Message "); e.printStackTrace();
			return false;
		}
	}

	private void Suppression_Discussion(Joueur JOUEUR, int ID) {
		try {
			serveur.Bdd.Requete_ForumSuppressionDiscussion.setInt(1, ID);
			serveur.Bdd.Requete_ForumSuppressionDiscussion.execute();
			//serveur.Bdd.Requete.execute("DELETE FROM $forum_m WHERE s=" + ID);
			serveur.Bdd.Requete_ForumSuppressionTopic.setInt(1, ID);
			serveur.Bdd.Requete_ForumSuppressionTopic.execute();
			//serveur.Bdd.Requete.execute("DELETE FROM $forum_s WHERE id=" + ID + " LIMIT 1");
			CacheListeSujet.remove(JOUEUR.ForumEnCours);
			Forum_Envoie_Liste_Sujet(JOUEUR);
			System.out.println("Suppression discussion par " + JOUEUR.NomJoueur);
		} catch (Exception e) {
			System.err.print("3XC Suppression Discussion "); e.printStackTrace();
		}
	}

	private void Edition_Titre(Joueur JOUEUR, int ID, int FORUM_EN_COURS, String TITRE) {
		try {
			serveur.Bdd.Requete_EditionTitre.setString(1, TITRE);
			serveur.Bdd.Requete_EditionTitre.setInt(2, ID);
			serveur.Bdd.Requete_EditionTitre.execute();
			CacheListeSujet.remove(FORUM_EN_COURS);
			Forum_Envoie_Liste_Sujet(JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Edition Titre "); e.printStackTrace();
		}
	}

	private void Deplacement_Sujet(Joueur JOUEUR, int ID, int FORUM_CIBLE) {
		try {
			serveur.Bdd.Requete_DeplacerSujet.setInt(1, FORUM_CIBLE);
			serveur.Bdd.Requete_DeplacerSujet.setInt(2, ID);
			serveur.Bdd.Requete_DeplacerSujet.execute();
			CacheListeSujet.remove(JOUEUR.ForumEnCours);
			CacheListeSujet.remove(FORUM_CIBLE);
			CacheSujet.get(ID).setForum(FORUM_CIBLE);
			Forum_Envoie_Liste_Sujet(JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Deplacement Sujet "); e.printStackTrace();
		}
	}

	private void Nouveau_Sujet(Joueur JOUEUR, String TITRE, String MESSAGE, int FORUM) {
		try {
			// Enregistrement lien sujet dans table
			// forums
			serveur.Bdd.Requete_NouveauSujet.setInt(1, FORUM);
			serveur.Bdd.Requete_NouveauSujet.setString(2, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_NouveauSujet.setString(3, JOUEUR.Avatar);
			serveur.Bdd.Requete_NouveauSujet.setString(4, TITRE);
			serveur.Bdd.Requete_NouveauSujet.setString(5, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_NouveauSujet.setString(6, (TITRE.contains("[Anim")||TITRE.contains("[IT")) && JOUEUR.AutorisationAnimateur ? "1" : "0");
			serveur.Bdd.Requete_NouveauSujet.execute();
			// Creation de la table du sujet
			ResultSet NumSujet = serveur.Bdd.Requete_NouveauSujet.getGeneratedKeys();
			NumSujet.next();
			// Enregistrement du premier message
			JOUEUR.SujetEnCours = NumSujet.getInt(1);
			Forum_Nouveau_Message(JOUEUR, JOUEUR.SujetEnCours, MESSAGE, false);
			if (serveur.ListeSurveillanceForum.contains(JOUEUR.NomJoueur.toLowerCase())) {
				if (JOUEUR.ForumEnCours != 0) {
					serveur.Avertissement_ModoForum(JOUEUR.NomJoueur + " a posté un nouveau sujet ["+JOUEUR.ForumEnCours+"] "+TITRE, false);
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Nouveau sujet "); e.printStackTrace();
		}
	}

	private void Mute_Forum(Joueur JOUEUR, String Id, long Temps, String Raison) {
		try {
			if (JOUEUR.AutorisationRecrueForum) {
				if (Temps>300) {
					Temps = 300;
				}
			}
			String Message = "";

			// on récupère le message concerné
			serveur.Bdd.Requete_IPMessage.setInt(1, Integer.parseInt(Id));
			ResultSet Result = serveur.Bdd.Requete_IPMessage.executeQuery();

			if (Result.next()) {
				String IP = Result.getString(1);
				String Cible = Result.getString(2);
				JOUEUR.Log_MuteForum(Cible, Temps, Raison);
				if (IP != null) {

					if (Temps == 0) {
						serveur.Demute_Forum(IP);
						String Phrase = Cible + " n'est plus mute IP sur le forum.";
						if (!JOUEUR.AccepteCanalAvertoForum) {
							JOUEUR.Envoie("CxINFO#"+Phrase);
						}
						serveur.Avertissement_ModoForum(Phrase, false);
						serveur.BOITE.Requete(Boite.LOG_SANCTION, JOUEUR, Cible, IP, Raison, "mute_forum", 0);
						return;
					}

					// Mute Joueur dont l'IP est IP                 
					String NotifJoueur = "Vous n'avez plus le droit de parler sur le forum pendant " + Temps + " heures." + (Raison.isEmpty()?"":" Raison : " + Raison);
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur JoueurCible = serveur.ListeJoueur.get(i);
						if (JoueurCible.AdresseIP.equals(IP)) {
							JoueurCible.AutorisationForum = false;
							JoueurCible.Message_Info(NotifJoueur);
						}
					}
					// Notif MP					
					EnvoyerNouveauMP("Forum", Cible, NotifJoueur, "-");
					
					Message = "Mute IP de " + Cible + " (" + Temps + " heures) effectué.";
					if (Raison.length() > 0) {
						Message += " Raison : " + Raison;
					}
					serveur.ListeMuetForum.add(IP);
					serveur.Mute_Forum(IP, Temps);
					serveur.BOITE.Requete(Boite.LOG_SANCTION, JOUEUR, Cible, IP, Raison, "mute_forum", (Long.valueOf(Temps)).intValue());
				} else {
					Message = "IP introuvable.";
				}
				if (!JOUEUR.AccepteCanalAvertoForum) {
					JOUEUR.Envoie("CxINFO#" + Message);
				}
				serveur.Avertissement_ModoForum(Message, false);

			}
		} catch (Exception e) {
			System.err.print("3XC Mute Forum "); e.printStackTrace();
		}
	}
	
	private void EnvoyerNouveauMP(String emetteur, String recepteur, String message, String ip) throws Exception {
		emetteur = emetteur.substring(0, 1).toUpperCase() + emetteur.substring(1).toLowerCase();
		recepteur = recepteur.substring(0, 1).toUpperCase() + recepteur.substring(1).toLowerCase();
		
		serveur.Bdd.Requete_NouveauMP.setString(1, emetteur);
		serveur.Bdd.Requete_NouveauMP.setString(2, recepteur);
		serveur.Bdd.Requete_NouveauMP.setString(3, message);
		serveur.Bdd.Requete_NouveauMP.setString(4, ip);
		serveur.Bdd.Requete_NouveauMP.executeUpdate();
	}

	private void Log_Sanction(Joueur JOUEUR, String CIBLE, String IP, String Raison, String SANCTION, int Temps) {
		try {
			if (CIBLE==null) {
				CIBLE = "?";
			}
			serveur.Bdd.Requete_LogSanction.setString(1, CIBLE);
			serveur.Bdd.Requete_LogSanction.setString(2, JOUEUR != null ? JOUEUR.NomJoueur : null);
			serveur.Bdd.Requete_LogSanction.setString(3, IP);
			serveur.Bdd.Requete_LogSanction.setString(4, SANCTION != null ? (SANCTION.equals("") ? null : SANCTION) : null);
			serveur.Bdd.Requete_LogSanction.setInt(5, Temps);
			serveur.Bdd.Requete_LogSanction.setString(6, Raison);
			serveur.Bdd.Requete_LogSanction.setInt(7, Temps);

			serveur.Bdd.Requete_LogSanction.execute();
		} catch (Exception e) {
			System.err.print("3XC Log sanction "); e.printStackTrace();
		}
	}

	private void Log_Team(Joueur JOUEUR, String CIBLE, String ACTION, int ID_TEAM) {
		try {
			serveur.Bdd.Requete_LogTeam.setInt(1, ID_TEAM);
			serveur.Bdd.Requete_LogTeam.setString(2, (JOUEUR == null ? "~" + CIBLE : JOUEUR.NomJoueur));
			serveur.Bdd.Requete_LogTeam.setString(3, (JOUEUR == null ? "*" : JOUEUR.AdresseIP));
			serveur.Bdd.Requete_LogTeam.setString(4, CIBLE);
			serveur.Bdd.Requete_LogTeam.setString(5, ACTION);
			serveur.Bdd.Requete_LogTeam.execute();
		} catch (Exception e) {
			System.err.print("3XC Log Team "); e.printStackTrace();
		}
	}

	private void Fermeture_Sujet(Joueur JOUEUR, String FERMETURE, int SUJET) {
		try {
			if (FERMETURE.equals("1")) {
				serveur.Bdd.Requete_FermetureSujet.setInt(1, 1);
			} else {
				serveur.Bdd.Requete_FermetureSujet.setInt(1, 0);
			}
			serveur.Bdd.Requete_FermetureSujet.setString(2, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_FermetureSujet.setInt(3, (JOUEUR.AutorisationModo||JOUEUR.AutorisationHelper)?1:0);
			serveur.Bdd.Requete_FermetureSujet.setInt(4, SUJET);
			serveur.Bdd.Requete_FermetureSujet.execute();
			CacheListeSujet.remove(JOUEUR.ForumEnCours);
			Forum_Envoie_Liste_Sujet(JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Fermeture_Sujet "); e.printStackTrace();
		}
	}

	private void Réponse_Sujet(Joueur JOUEUR, String MESSAGE, boolean ANONYME) {
		if (Forum_Nouveau_Message(JOUEUR, JOUEUR.SujetEnCours, MESSAGE, ANONYME)) {
			Forum_Envoie_Sujet(JOUEUR, JOUEUR.SujetEnCours, -1);
		}
	}

	private void Edition_Message(Joueur JOUEUR, int ID, String MESSAGE) {
		try {
			ResultSet Retour = serveur.Bdd.Requete.executeQuery("SELECT a FROM `$forum_m` WHERE id=" + ID + " AND s=" + JOUEUR.SujetEnCours + " LIMIT 1");
			if (Retour.next()) {
				if (Retour.getString(1).equals(JOUEUR.NomJoueur)
					|| (JOUEUR.ForumEnCours == Serveur.FORUM_TEAM && (JOUEUR.AutorisationScribe || JOUEUR.AutorisationGestionRoles))
					|| JOUEUR.AutorisationModoForum) {

					serveur.Bdd.Requete_EditionMessage.setString(1, MESSAGE);
					serveur.Bdd.Requete_EditionMessage.setString(2, Integer.toString(ID));
					serveur.Bdd.Requete_EditionMessage.setInt(3, JOUEUR.SujetEnCours);
					serveur.Bdd.Requete_EditionMessage.execute();

					CacheSujet.remove(JOUEUR.SujetEnCours);
					Forum_Envoie_Sujet(JOUEUR, JOUEUR.SujetEnCours, JOUEUR.PageEnCours);
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Edition Message "); e.printStackTrace();
		}
	}

	private void Suppression_Message(Joueur JOUEUR, int ID, boolean RECHARGER) {
		try {
			serveur.Bdd.Requete_ForumSuppressionMessageSelect.setInt(1, ID);
			serveur.Bdd.Requete_ForumSuppressionMessageSelect.setInt(2, JOUEUR.SujetEnCours);
			ResultSet Retour = serveur.Bdd.Requete_ForumSuppressionMessageSelect.executeQuery();
			//ResultSet Retour = serveur.Bdd.Requete.executeQuery("SELECT a FROM $forum_m WHERE id=" + ID + " AND s=" + JOUEUR.SujetEnCours + " LIMIT 1");
			if (Retour.next()) {
				if (Retour.getString(1).equals(JOUEUR.NomJoueur) || JOUEUR.AutorisationModoForum) {
					// suppression
					serveur.Bdd.Requete_ForumSuppressionMessage.setInt(1, ID);
					serveur.Bdd.Requete_ForumSuppressionMessage.setInt(2, JOUEUR.SujetEnCours);
					serveur.Bdd.Requete_ForumSuppressionMessage.execute();
					//serveur.Bdd.Requete.execute("DELETE FROM $forum_m WHERE id=" + ID + " AND s=" + JOUEUR.SujetEnCours + " LIMIT 1");
					serveur.Bdd.Requete_ForumSuppressionMessageDiscussion.setInt(1, JOUEUR.SujetEnCours);
					serveur.Bdd.Requete_ForumSuppressionMessageDiscussion.execute();
					//serveur.Bdd.Requete.execute("UPDATE $forum_s SET m=m-1 WHERE id=" + JOUEUR.SujetEnCours + " LIMIT 1");
					serveur.Bdd.Requete_ForumSuppressionMessageNbMsg.setInt(1, JOUEUR.SujetEnCours);
					ResultSet Retour2 = serveur.Bdd.Requete_ForumSuppressionMessageNbMsg.executeQuery();
					//ResultSet Retour2 = serveur.Bdd.Requete.executeQuery("SELECT m FROM $forum_s WHERE id=" + JOUEUR.SujetEnCours + " LIMIT 1");
					if (Retour2.next()) {
						int m = Retour2.getInt(1);
						if (m == 0) {
							serveur.Bdd.Requete_ForumSuppressionTopic.setInt(1, JOUEUR.SujetEnCours);
							serveur.Bdd.Requete_ForumSuppressionTopic.execute();
							//serveur.Bdd.Requete.execute("DELETE FROM $forum_s WHERE id=" + JOUEUR.SujetEnCours + " LIMIT 1");
						} else {
							serveur.Bdd.Requete_ForumSuppressionMessageDernierMsg.setInt(1, JOUEUR.SujetEnCours);
							ResultSet Retour3 = serveur.Bdd.Requete_ForumSuppressionMessageDernierMsg.executeQuery();
							//ResultSet Retour3 = serveur.Bdd.Requete.executeQuery("SELECT d,a FROM $forum_m WHERE s=" + JOUEUR.SujetEnCours + " order by d desc LIMIT 1");
							if (Retour3.next()) {
								serveur.Bdd.Requete_ForumSuppressionMessageDernierMsgTopic.setInt(1, Retour3.getInt(1));
								serveur.Bdd.Requete_ForumSuppressionMessageDernierMsgTopic.setString(2, Retour3.getString(2));
								serveur.Bdd.Requete_ForumSuppressionMessageDernierMsgTopic.setInt(3, JOUEUR.SujetEnCours);
								serveur.Bdd.Requete_ForumSuppressionMessageDernierMsgTopic.execute();
								//serveur.Bdd.Requete.execute("UPDATE $forum_s SET d=" + Retour3.getInt(1) + ",p='" + Retour3.getString(2) + "' WHERE id=" + JOUEUR.SujetEnCours + " LIMIT 1");
							}
						}
						if (m%20==0 && m/20 == JOUEUR.PageEnCours) JOUEUR.PageEnCours--;
					}
					CacheListeSujet.remove(JOUEUR.ForumEnCours);

					CacheSujet.remove(JOUEUR.SujetEnCours);

					if (RECHARGER) {
						Forum_Envoie_Sujet(JOUEUR, JOUEUR.SujetEnCours, JOUEUR.PageEnCours);
					}
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Suppression_Message  "); e.printStackTrace();
		}
	}

	private void Changement_Avatar(Joueur JOUEUR) {
		try {
			//String NomImage = "" + JOUEUR.IdJoueur;
			serveur.Bdd.Requete_ChangerAvatar.setInt(1, JOUEUR.IdJoueur);
			serveur.Bdd.Requete_ChangerAvatar.setInt(2, JOUEUR.IdJoueur);
			serveur.Bdd.Requete_ChangerAvatar.execute();
			//serveur.Bdd.Requete.execute("UPDATE $joueur SET i='" + JOUEUR.IdJoueur + "' WHERE id=" + JOUEUR.IdJoueur + " LIMIT 1");
			JOUEUR.Avatar = String.valueOf(JOUEUR.IdJoueur);
			JOUEUR.MessageBoite.add("CxAV#" + JOUEUR.Avatar);
		} catch (Exception e) {
			System.err.print("3XC Changement avatar "); e.printStackTrace();
		}
	}
	
	private boolean Charger_Avatar_Imgur(String Avatar) {
		try {
			Pattern pattern = Pattern.compile("^imgur@[a-zA-Z0-9]{7}?\\.[a-zA-Z]{3}?$");
			Matcher matcher = pattern.matcher(Avatar);
			
			return matcher.find();
		} catch (Exception e) {
			return false;
		}
	}

	private void Changement_Avatar_New(Joueur JOUEUR, String AVATAR) {
		try {
			/*Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\.]");
            Matcher matcher = pattern.matcher(AVATAR);*/
            
            if (AVATAR.isEmpty()) {
            	//JOUEUR.Envoie("CxINFO#Pour modifier votre avatar, il faut l'upload sur http://atelier801.com/user-images-home, puis taper sur le chat /avatar [code] où [code] apparaît dans l'URL de votre image avec l'extension (par exemple pour http://img.atelier801.com/57a4f115.jpg, taper /avatar 57a4f115.jpg)");
            	JOUEUR.Envoie("CxINFO#Pour modifier votre avatar faites :"
            			    + "\n1. Allez sur le site https://imgur.com/"
            			    + "\n2. Hébergez votre image"
            			    + "\n3. Faites un clic droit sur l'image et sélectionnez \"Copier l'adresse de l'image\""
            			    + "\n4. Faites /avatar [Lien Copié]"
            			    + "\n\nExemple :"
            			    + "\nSi votre image est hébergée sur https://imgur.com/a/RpYTfm1, vous obtenez en adresse de l'image https://i.imgur.com/syZxOd6.jpg et vous devez donc faire : /avatar https://i.imgur.com/syZxOd6.jpg"
            			    );
            	
            	if (!JOUEUR.Avatar.isEmpty()) {
            		JOUEUR.Envoie("CxINFO#Votre avatar actuel est " + JOUEUR.Avatar + (JOUEUR.PorteBlason ? " (blason team)" : ""));
            	}
            	
            	return;
            } else if (!AVATAR.equals("_blason") && !Charger_Avatar_Imgur(AVATAR)) {
            	JOUEUR.Envoie("CxINFO#Format invalide. Par exemple pour https://i.imgur.com/syZxOd6.jpg, tapez /avatar https://i.imgur.com/syZxOd6.jpg");
                return;
            } else {
				serveur.Bdd.Requete_ChangerAvatar.setString(1, AVATAR);
				serveur.Bdd.Requete_ChangerAvatar.setInt(2, JOUEUR.IdJoueur);
				serveur.Bdd.Requete_ChangerAvatar.execute();
				
				if (AVATAR.equals("_blason")) {
					ChargerBlason(JOUEUR);
					JOUEUR.PorteBlason = true;
				} else {
					JOUEUR.PorteBlason = false;
					JOUEUR.Avatar = AVATAR;
				}
				
				JOUEUR.MessageBoite.add("CxAV#" + JOUEUR.Avatar);
                JOUEUR.Envoie("CxINFO#Avatar modifié.");
            }
		} catch (Exception e) {
			System.err.print("3XC Changement avatar new "); e.printStackTrace();
		}
	}
        
	private void Vote(Joueur JOUEUR, int ID, int VOTE) {
		try {
			ResultSet Retour = serveur.Bdd.Requete.executeQuery("SELECT * from `$votes` where (id_joueur=" + JOUEUR.IdJoueur + " or ip='" + JOUEUR.AdresseIP + "') and id_vote=" + ID);
			if (!Retour.next()) {
				if (JOUEUR.Stats_TempsDeJeu > RestrictionHeure.VOTE_AAAAH) {
					int fiab = JOUEUR.JeuTeam == 3 ? 1 : 0;
					serveur.Bdd.Requete.execute("INSERT INTO `$votes` (id_vote,id_joueur,ip,vote,fiabilite) VALUES (" + ID + "," + JOUEUR.IdJoueur + ",'" + JOUEUR.AdresseIP + "'," + VOTE + "," + fiab + ")");
				}
			} else {
				serveur.Bdd.Requete.execute("UPDATE `$votes` SET vote='" + VOTE + "' WHERE id_vote = " + ID + " AND id_joueur = " + JOUEUR.IdJoueur);
			}

		} catch (Exception e) {
			System.err.print("3XC Vote "); e.printStackTrace();
		}
	}

	private void Reponse_Sondage(Joueur JOUEUR, int ID, int VOTE) {
		try {
			serveur.Bdd.Requete_SondageRéponse.setInt(1, JOUEUR.IdJoueur);
			serveur.Bdd.Requete_SondageRéponse.setString(2, JOUEUR.AdresseIP);
			serveur.Bdd.Requete_SondageRéponse.setInt(3, ID);
			ResultSet Retour = serveur.Bdd.Requete_SondageRéponse.executeQuery();
			
			if (!Retour.next()) {
				if (JOUEUR.Stats_TempsDeJeu > RestrictionHeure.VOTER_SONDAGE) {
					serveur.Bdd.Requete_SondageRépondre.setInt(1, ID);
					serveur.Bdd.Requete_SondageRépondre.setInt(2, JOUEUR.IdJoueur);
					serveur.Bdd.Requete_SondageRépondre.setInt(3, VOTE);
					serveur.Bdd.Requete_SondageRépondre.setString(4, JOUEUR.AdresseIP);
					serveur.Bdd.Requete_SondageRépondre.setString(5, JOUEUR.HostSimple);
					serveur.Bdd.Requete_SondageRépondre.setLong(6, JOUEUR.Stats_TempsDeJeu);
					serveur.Bdd.Requete_SondageRépondre.execute();
				}
			} else {
				serveur.Bdd.Requete_SondageChangerVote.setInt(1, VOTE);
				serveur.Bdd.Requete_SondageChangerVote.setInt(2, ID);
				serveur.Bdd.Requete_SondageChangerVote.setInt(3, JOUEUR.IdJoueur);
				serveur.Bdd.Requete_SondageChangerVote.execute();
			}

		} catch (Exception e) {
			System.err.print("3XC Sondage "); e.printStackTrace();
		}
	}

	private void Editer_Titre_Forum(Joueur JOUEUR, String CIBLE, String COULEUR, String TITRE) {
		try {
			serveur.Bdd.Requete_ForumSupprimerTitreJoueur.setString(1, CIBLE);
			serveur.Bdd.Requete_ForumSupprimerTitreJoueur.execute();
			//serveur.Bdd.Requete.execute("DELETE FROM $forum_d where n='"+CIBLE+"' LIMIT 1");
			serveur.Bdd.Requete_ForumAjouterTitreJoueur.setString(1, CIBLE);
			serveur.Bdd.Requete_ForumAjouterTitreJoueur.setString(2, TITRE);
			serveur.Bdd.Requete_ForumAjouterTitreJoueur.setString(3, COULEUR);
			serveur.Bdd.Requete_ForumAjouterTitreJoueur.execute();
			//serveur.Bdd.Requete.execute("INSERT INTO $forum_d(n,d,c) values ('"+CIBLE+"','"+TITRE+"','"+COULEUR+"')");

		} catch (Exception e) {
			System.err.print("3XC Editer_Titre_Forum "); e.printStackTrace();
		}
	}
	private void Supprimer_Titre_Forum(Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_ForumSupprimerTitreJoueur.setString(1, CIBLE);
			serveur.Bdd.Requete_ForumSupprimerTitreJoueur.execute();
			//serveur.Bdd.Requete.execute("DELETE FROM $forum_d where n='"+CIBLE+"' LIMIT 1");

		} catch (Exception e) {
			System.err.print("3XC Supprimer_Titre_Forum "); e.printStackTrace();
		}
	}
	private void Stop_Maraboum(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_StopMaraboum.setString(1, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_StopMaraboum.execute();
			//serveur.Bdd.Requete.execute("UPDATE mara SET nm=1 where n='"+JOUEUR.NomJoueur+"' LIMIT 1");
			JOUEUR.Envoie("CxINFO#Vos statistiques Maraboum n'évolueront plus et seront supprimées.");
		} catch (Exception e) {
			System.err.print("3XC Stop_Maraboum "); e.printStackTrace();
		}
	}

	private void Changement_Description(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_ChangerDescription.setString(1, JOUEUR.Description);
			serveur.Bdd.Requete_ChangerDescription.setInt(2, JOUEUR.IdJoueur);
			serveur.Bdd.Requete_ChangerDescription.execute();
		} catch (Exception e) {
			System.err.print("3XC Changement Description "); e.printStackTrace();
		}
	}

	private void Ajout_Ami(Joueur JOUEUR, String NOM_AMI) {
		// on essaye de l'ajouter
		try {
			serveur.Bdd.Requete_IdJoueurEtBan.setString(1, NOM_AMI);
			ResultSet rs = serveur.Bdd.Requete_IdJoueurEtBan.executeQuery();
			if (rs.next() && rs.getInt("ban") != 2) {
				int IdAmi = rs.getInt("id");
				serveur.Bdd.Requete_AjouterAmi.setInt(1, JOUEUR.IdJoueur);
				serveur.Bdd.Requete_AjouterAmi.setInt(2, IdAmi);
				serveur.Bdd.Requete_AjouterAmi.executeUpdate();
				JOUEUR.ListeAmis.add(new Ami(IdAmi, NOM_AMI));
				JOUEUR.MessageBoite.add("CxAA#0"); // L'ajout a réussi    

				return;
			}
		} catch (Exception e) {
			System.err.print("3XC Ajout AMi "); e.printStackTrace();
		}
		JOUEUR.MessageBoite.add("CxAA#3"); // L'ajout a échouché
	}

	private void Suppression_Ami(Joueur JOUEUR, int ID_AMI) {
		try {
			serveur.Bdd.Requete_SupprimerAmi.setInt(1, JOUEUR.IdJoueur);
			serveur.Bdd.Requete_SupprimerAmi.setInt(2, ID_AMI);
			serveur.Bdd.Requete_SupprimerAmi.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Suppression ami "); e.printStackTrace();
		}
	}

	private void Chargement_Joueur(Joueur JOUEUR, String NOM, boolean PAS_DE_FORUM) {
		// Banni
		if (serveur.ListeBanniNom.contains(NOM)) {
			JOUEUR.SetBan("Joueur banni");
			return;
		}
		// Muet
		if (serveur.ListeMuetNom.contains(NOM) || serveur.ListeMuetIP.contains(JOUEUR.AdresseIP)) {
			JOUEUR.Muet = true;
		}

		// Muet Forum
		if (serveur.ListeMuetForum.contains(JOUEUR.AdresseIP)) {
			JOUEUR.MuetForum = true;
		}
		if (serveur.ListeMuetForumNom.contains(NOM)) {
			JOUEUR.MuetForum = true;
		}

		// Muet Chat principal
		if (serveur.ListeMuteChatPrincipal.contains(NOM.toLowerCase())) {
			JOUEUR.MuteChatPrincipal = true;
		}

		try {
			serveur.Bdd.Requete_ChargementJoueur.setString(1, NOM);
			ResultSet InfoJoueur = serveur.Bdd.Requete_ChargementJoueur.executeQuery();
			if (InfoJoueur.next() && serveur.ServeurOuvert) {

				// restrictions
				if (InfoJoueur.getInt("ban") != 0) {
					JOUEUR.SetBan("Joueur banni");
					return;
				}

				if (InfoJoueur.getInt("mutecp") == 1) {
					JOUEUR.MuteChatPrincipal = true;
				}
				if (InfoJoueur.getInt("muteforum") == 1) {
					JOUEUR.MuetForum = true;
				}

				JOUEUR.Stats_TempsDeJeu = InfoJoueur.getLong("s_tt");
				// Autorisation

				JOUEUR.AutorisationInscription = true;
				if (!JOUEUR.Muet && !JOUEUR.MuetForum && !PAS_DE_FORUM && JOUEUR.Stats_TempsDeJeu > RestrictionHeure.POSTER_FORUM) {
					JOUEUR.AutorisationForum = true;
				}

				String NiveauAutorisation = InfoJoueur.getString("a");
				String[] ListeNiveau = NiveauAutorisation.split(",");

				for (int i = 0; i<ListeNiveau.length; i++) {
					int Niveau = Integer.valueOf(ListeNiveau[i]);

					switch (Niveau) { // Niveau d'autorisation
					case (10): // admin
						JOUEUR.AutorisationAdmin = true;
						JOUEUR.AutorisationBeta = true;
						JOUEUR.AutorisationModo = true;
						JOUEUR.AutorisationAnimateur = true;
						JOUEUR.AutorisationModoForum = true;
						//JOUEUR.AutorisationModoTeam = true;
						JOUEUR.SignalementModo = true;
						JOUEUR.MessageBoite.add("CxMOD");
						serveur.Message_Modo(JOUEUR.NomJoueur + " vient de se connecter (admin).", true);
						break;
					case (17): // dev
						JOUEUR.AutorisationDev = true;
						JOUEUR.AutorisationBeta = true;
						JOUEUR.MessageBoite.add("CxMODF");
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (dev).", true);
						break;
					case (5): // modo
						JOUEUR.AutorisationModo = true;
						JOUEUR.AutorisationBeta = true;
						JOUEUR.AutorisationModoForum = true;
						//JOUEUR.AutorisationModoTeam = true;
						JOUEUR.SignalementModo = true;
						JOUEUR.AutorisationAnimateur = true;
						//Envoie("CxMODO#" + serveur.Liste_modo());
						JOUEUR.MessageBoite.add("CxMOD");
						serveur.Message_Modo(JOUEUR.NomJoueur + " vient de se connecter (modo).", true);
						break;
					case (50): // modo public
						JOUEUR.AutorisationModo = true;
						JOUEUR.AutorisationBeta = true;
						JOUEUR.AutorisationModoPublic = true;
						JOUEUR.AutorisationModoForum = true;
						//JOUEUR.AutorisationModoTeam = true;
						JOUEUR.SignalementModo = true;
						JOUEUR.AutorisationAnimateur = true;
						//Envoie("CxMODO#" + serveur.Liste_modo());
						JOUEUR.MessageBoite.add("CxMOD");
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (modo public).", true);
						break;
					case (12): // modo+modo forum
						JOUEUR.AutorisationModo = true;
						JOUEUR.AutorisationModoForum = true;
						JOUEUR.SignalementModo = true;
						JOUEUR.SignalementModoForum = true;
						JOUEUR.AutorisationAnimateur = true;
						JOUEUR.AccepteCanalAvertoForum = true;
						JOUEUR.MessageBoite.add("CxMOD");
						serveur.Message_Modo(JOUEUR.NomJoueur + " vient de se connecter (modo).", true);
						break;
					case (4): // modo cartes Aaaah
						JOUEUR.AutorisationModoCartes = true;
						break;
					case (15): // recrue respo Aaaah
						JOUEUR.AutorisationRespoAaaahRecrue = true;
						break;
					case (40): // respomap forto
						JOUEUR.AutorisationModoCartesRally = true;
						JOUEUR.MessageBoite.add("CxRESPMF");
						break;
					case (60): // animateur
						JOUEUR.AutorisationAnimateur = true;
					    JOUEUR.PureAnimateur = true;
						JOUEUR.MessageBoite.add("CxANIM");
						break;
					case (70): // Animateur superviseur
						JOUEUR.AutorisationAnimateur = true;
						JOUEUR.AutorisationAnimateurSuperviseur = true;
						JOUEUR.PureAnimateur = true;
						JOUEUR.MessageBoite.add("CxANIM");
						break;
					case (6): // helper
						JOUEUR.AutorisationHelper = true;
						JOUEUR.AutorisationArbitre = true;
						JOUEUR.SignalementModo = true;
						JOUEUR.AutorisationModoForum = true;
						serveur.Message_Modo(JOUEUR.NomJoueur + " vient de se connecter (helper).", true);
						JOUEUR.MessageBoite.add("CxARBM");
						break;
					case (7): // arbitre tournoi forto
						JOUEUR.AutorisationTournoiForto = true;
						JOUEUR.MessageBoite.add("CxARBT");
						break;
					case (9): // arbitre tournoi Aaaah
						JOUEUR.AutorisationTournoiAaaah = true;
						JOUEUR.MessageBoite.add("CxARBTAB");
						break;
					case (8): // super arbitre
						JOUEUR.AutorisationArbitre = true;
						JOUEUR.SignalementModo = true;
						JOUEUR.AutorisationSuperArbitre = true;
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (arbitre+).", true);
						JOUEUR.MessageBoite.add("CxARB+");
						break;
					case (3): // modo team
						JOUEUR.AutorisationModoTeam = true;
						break;
					case (30): // respo tri team
						JOUEUR.AutorisationRespoTriTeam = true;
						break;
					case (2): // arbitre
						JOUEUR.AutorisationArbitre = true;
						JOUEUR.SignalementModo = true;//false;
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (arbitre).", true);
						JOUEUR.MessageBoite.add("CxARB");
					break;
					case (20): // recrue arbitre
						JOUEUR.AutorisationArbitre = true;
						JOUEUR.AutorisationRecrue = true;
						JOUEUR.SignalementModo = false;
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (recrue).", true);
						JOUEUR.MessageBoite.add("CxARBREC");
					break;
					case (1): // modo forum
						JOUEUR.AutorisationModoForum = true;
						JOUEUR.SignalementModoForum = true;
						JOUEUR.AccepteCanalAvertoForum = true;
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (modo forum).", true);
						JOUEUR.MessageBoite.add("CxMODF");
						break;
					case (80) : // modo forum publique
						JOUEUR.AutorisationModoForum = true;
						JOUEUR.AutorisationModoFPublic = true;
						JOUEUR.AutorisationAnimateur = true;
						JOUEUR.SignalementModoForum = true;
						JOUEUR.AccepteCanalAvertoForum = true;
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (modo forum public).", true);
						JOUEUR.MessageBoite.add("CxMODF");
						JOUEUR.MessageBoite.add("CxANIM");
						break;
					case (11): // recrue modo forum
						JOUEUR.AutorisationModoForum = true;
						JOUEUR.AutorisationRecrueForum = true;
						JOUEUR.SignalementModoForum = true;
						JOUEUR.AccepteCanalAvertoForum = true;
						serveur.Message_Modo_Tout_VIP(JOUEUR.NomJoueur + " vient de se connecter (recrue forum).", true);
						JOUEUR.MessageBoite.add("CxMODFREC");
						break;
					case (13): // Bêta tester
						JOUEUR.AutorisationBeta = true;
						break;
					case (14): // Filmeur
						JOUEUR.AutorisationFilmeur = true;
						JOUEUR.Envoie("CxFILMT");
					    break;
					case (16):
						JOUEUR.AutorisationGestionElo = true;
						break;
					}
				}
				
				if (Local.BETA) {
					if(!JOUEUR.AutorisationBeta) { // Connexion refusée
						JOUEUR.DemandeDeco = true;
						JOUEUR.RaisonDeco = "";
						return;
					}
				}

				//
				JOUEUR.FlagMessageAnim = InfoJoueur.getInt("flag_msg_anim");
				// Forteresse
				JOUEUR._Forteresse.Sexe = InfoJoueur.getInt("o_f");
				JOUEUR.SonActif = InfoJoueur.getInt("o_s");
				JOUEUR.SonGuideActif = InfoJoueur.getInt("o_sg");
				JOUEUR.AfficherBulles = InfoJoueur.getInt("o_b");
				JOUEUR.AfficherCoDeco = InfoJoueur.getInt("o_c");
				JOUEUR.AfficherCoAmis = InfoJoueur.getInt("o_ca");
				JOUEUR.TriForteresse = InfoJoueur.getInt("o_tf");
				JOUEUR.NoLag = InfoJoueur.getInt("o_nl");
				JOUEUR.NoChat = InfoJoueur.getInt("o_nc");
				JOUEUR.NoVote = InfoJoueur.getInt("o_nv");
				JOUEUR.OldSprite = InfoJoueur.getInt("o_os");
				JOUEUR.TirContinu = InfoJoueur.getInt("o_tc");
				JOUEUR.NoStats = InfoJoueur.getInt("o_ns");
				JOUEUR.NoMP = InfoJoueur.getInt("o_nomp");

				// Aaaah
				JOUEUR._Aaaah.Stats_PartieJouée = InfoJoueur.getInt("sa_pj");
				JOUEUR._Aaaah.Stats_PartieGuide = InfoJoueur.getInt("sa_pg");
				JOUEUR._Aaaah.Stats_JoueursSauvés = InfoJoueur.getInt("sa_js");
				JOUEUR._Aaaah.Stats_JoueursGuidés = InfoJoueur.getInt("sa_jg");
				JOUEUR._Aaaah.Stats_PartiePremier = InfoJoueur.getInt("sa_p");
				JOUEUR._Aaaah.Stats_PartieFinie = InfoJoueur.getInt("sa_pf");
				JOUEUR._Aaaah.Stats_Tué = InfoJoueur.getInt("sa_t");
				JOUEUR._Aaaah.Stats_RatioSauvetage = InfoJoueur.getInt("sa_rs");
				JOUEUR._Aaaah.Stats_RatioPremier = InfoJoueur.getInt("sa_rp");
				JOUEUR._Aaaah.Stats_RatioSurvivant = InfoJoueur.getInt("sa_rv");
				JOUEUR._Aaaah.Stats_RatioTueur = InfoJoueur.getInt("sa_rt");
				JOUEUR._Aaaah.Stats_Elo = InfoJoueur.getInt("sa_e");
				// Bouboum
				JOUEUR._Bouboum.Stats_PartieJouée = InfoJoueur.getInt("sb_pj");
				JOUEUR._Bouboum.Stats_PartieGagnée = InfoJoueur.getInt("sb_pg");
				JOUEUR._Bouboum.Stats_Tué = InfoJoueur.getInt("sb_t");
				JOUEUR._Bouboum.Stats_Mort = InfoJoueur.getInt("sb_m");
				JOUEUR._Bouboum.Stats_Ratio = InfoJoueur.getInt("sb_r");
				// Old stats
				
				InfoJoueur.getInt("sa_guidage");
				if (InfoJoueur.wasNull()) { // Pas d'ancienne stat enregistrée ($old_stats_j)
					JOUEUR._Aaaah.Stats_OldGuidés = -1;
					JOUEUR._Aaaah.Stats_OldPGuidés = -1;
					JOUEUR._Aaaah.Stats_OldPremier = -1;
					JOUEUR._Aaaah.Stats_OldPPremier = -1;
					
					JOUEUR._Bouboum.Stats_OldGagnées = -1;
					JOUEUR._Bouboum.Stats_OldPGagnées = -1;
				} else {
					JOUEUR._Aaaah.Stats_OldGuidés = InfoJoueur.getInt("sa_guidage");
					JOUEUR._Aaaah.Stats_OldPGuidés = InfoJoueur.getInt("sa_pguidage");
					JOUEUR._Aaaah.Stats_OldPremier = InfoJoueur.getInt("sa_victoire");
					JOUEUR._Aaaah.Stats_OldPPremier = InfoJoueur.getInt("sa_pvictoire");
					
					JOUEUR._Bouboum.Stats_OldGagnées = InfoJoueur.getInt("sb_victoire");
					JOUEUR._Bouboum.Stats_OldPGagnées = InfoJoueur.getInt("sb_pvictoire");
					JOUEUR._Bouboum.Stats_OldJouées = InfoJoueur.getInt("sb_opj");
				}
				
				InfoJoueur.getInt("op_sa_pj");
				if (InfoJoueur.wasNull()) { // $old_parties
					JOUEUR._Aaaah.Stats_OldJouées = 0;
					JOUEUR._Aaaah.Stats_OldSauvés = 0;
					JOUEUR._Aaaah.Stats_OldJGuidés = 0;
				} else {
					JOUEUR._Aaaah.Stats_OldJouées = InfoJoueur.getInt("op_sa_pj");
					JOUEUR._Aaaah.Stats_OldSauvés = InfoJoueur.getInt("op_sa_js");
					JOUEUR._Aaaah.Stats_OldJGuidés = InfoJoueur.getInt("op_sa_jg");
					
					// Correction des sats actu
					JOUEUR._Aaaah.Stats_PartieJouée -= JOUEUR._Aaaah.Stats_OldJouées;
					JOUEUR._Aaaah.Stats_JoueursSauvés -= JOUEUR._Aaaah.Stats_OldSauvés;
					JOUEUR._Aaaah.Stats_JoueursGuidés -= JOUEUR._Aaaah.Stats_OldJGuidés;
				}
				
				JOUEUR._Aaaah.Stats_PartieGuideInit = JOUEUR._Aaaah.Stats_PartieGuide;
				JOUEUR._Aaaah.Stats_JoueursSauvésInit = JOUEUR._Aaaah.Stats_JoueursSauvés;
				JOUEUR._Aaaah.Stats_JoueursGuidésInit = JOUEUR._Aaaah.Stats_JoueursGuidés;
				
				// Stats divers
				JOUEUR.Avatar = InfoJoueur.getString("i");
				JOUEUR.Description = InfoJoueur.getString("d");
				JOUEUR.IdJoueur = InfoJoueur.getInt("id");
				JOUEUR.Stats_Inscription = InfoJoueur.getInt("s_ti");
				JOUEUR.Stats_DernièreConnexion = InfoJoueur.getInt("s_dc");
				// test infos
				JOUEUR.DernièreIP = InfoJoueur.getString("ip");
				JOUEUR.Password = InfoJoueur.getString("p");
				JOUEUR.AutorisationChgPass = InfoJoueur.getString("chgpass");
				if (InfoJoueur.getLong("s_dcf") != 0) {
					JOUEUR.DerniereCoForum = InfoJoueur.getLong("s_dcf");
				}
				JOUEUR.AutorisationGrade9 = (1 == InfoJoueur.getInt("g9"));
				
				/*if (serveur.Maraaaathon) {
                                    serveur.Bdd.Requete_MaraRetrieve.setString(1,JOUEUR.NomJoueur);
                                    ResultSet InfoMara = serveur.Bdd.Requete_MaraRetrieve.executeQuery();
                                    if (InfoMara.next()) {
                                        JOUEUR.Mara_Activé = true;
                                        JOUEUR.Mara_TempsBan = InfoMara.getLong("tb");
                                        JOUEUR.Mara_TempsJeu = InfoMara.getLong("tj");
                                        JOUEUR.Mara_Guide_JoueurSauvés = InfoMara.getLong("js");
                                        JOUEUR.Mara_Guide_JoueurGuidées = InfoMara.getLong("jg");
                                        JOUEUR.Mara_Guide_PartiesGuidées = InfoMara.getLong("pg");
                                        JOUEUR.Mara_Run_ArrivéesPremier = InfoMara.getLong("p");
                                        JOUEUR.Mara_Run_PartiesJouées = InfoMara.getLong("pj");
                                        JOUEUR.Mara_Fight_Tués = InfoMara.getLong("tf");
                                        JOUEUR.Mara_Fight_Survivor = InfoMara.getLong("sf");
                                        JOUEUR.Mara_Fight_PartiesJouées = InfoMara.getLong("pjf");
                                        JOUEUR.Mara_Défilantes_Tués = InfoMara.getLong("td");
                                        JOUEUR.Mara_Défilantes_Survivor = InfoMara.getLong("sd");
                                        JOUEUR.Mara_Défilantes_PartiesJouées = InfoMara.getLong("pjd");
                                        JOUEUR.Mara_Rally_TempsTotal = InfoMara.getLong("tt");
                                        JOUEUR.Mara_Rally_PartiesJouées = InfoMara.getLong("pjn");
                                        //JOUEUR.Mara_Rally_NombreRecord = InfoMara.getLong("nr");
                                    } else {
                                        JOUEUR.Mara_Activé = false;
                                    }
                                }*/
				
				String elo_flag = InfoJoueur.getString("elo_flag");
				JOUEUR.elo_flag = elo_flag.charAt(0) + "#" + elo_flag.charAt(1)+ "#" + elo_flag.charAt(2); // Elo max Aaaah! ; Elo max Bouboum ; Elo max Forto
				JOUEUR.recompenseElo.setRecompensesByEloFlag(elo_flag);
				JOUEUR.recompenseElo.recompensesActives = InfoJoueur.getByte("rcmp_act");
				
				if (serveur.LOCAL) {
					JOUEUR.AdresseIP = JOUEUR.NomJoueur;
				}
				
				boolean[] gradesSpe = Joueur_A_Grades_Speciaux(JOUEUR.IdJoueur);
				JOUEUR.AutorisationGrade10 = gradesSpe[0];
				JOUEUR.AutorisationGrade11 = gradesSpe[1];
				
				JOUEUR.elo = new EloJoueur(serveur, JOUEUR.IdJoueur, JOUEUR.NomJoueur, false, "");
				
				if (serveur.Maraboum) {
					serveur.Bdd.Requete_MaraRetrieve.setString(1,JOUEUR.NomJoueur);
					ResultSet InfoMara = serveur.Bdd.Requete_MaraRetrieve.executeQuery();
					if (InfoMara.next()) {
						if (InfoMara.getInt("nm") == 0) { // marathon désactivé
							JOUEUR.Mara_Activé = true;
						JOUEUR.Mara_TempsBan = InfoMara.getLong("tb");
						JOUEUR.Mara_TempsJeu = InfoMara.getLong("tj");
						JOUEUR.Mara_Boum_PartiesJouées = InfoMara.getLong("pj");
						JOUEUR.Mara_Boum_Tués = InfoMara.getLong("t");
						JOUEUR.Mara_Boum_PartiesGagnées = InfoMara.getLong("pg");
						JOUEUR.Mara_Boum_Adversaires = InfoMara.getLong("a");
						JOUEUR.Mara_Boum_Suicides = InfoMara.getLong("s");
						JOUEUR.Mara_Boum_Manches = InfoMara.getLong("m");
						} else {
							JOUEUR.Envoie("CxINFO#Vos statistiques du Maraboum sont bloquées.");
							JOUEUR.Mara_Activé = false;
							JOUEUR.Mara_Désactivé = true;
						}
					} else {
						JOUEUR.Mara_Activé = false;
					}
				}
				
				JOUEUR.Stats.load(this,JOUEUR);

				if (InfoJoueur.getInt("ur") == 0) {
					JOUEUR.UneRecompense = "0";
				} else {
					JOUEUR.UneRecompense = ChaineMedailles(NOM);
				}

				// infos team
				JOUEUR.Membre = InfoJoueur.getInt("id_joueur") != 0;
				if (JOUEUR.Membre) {
					JOUEUR.IdTeam = InfoJoueur.getInt("id_team");
					JOUEUR.JeuTeam = InfoJoueur.getInt("jeu");
					JOUEUR.Grade = InfoJoueur.getInt("grade");
					JOUEUR.NomTeam = InfoJoueur.getString("nom");
					JOUEUR.FixeAutorisations(InfoJoueur.getInt("role"));
					serveur.Message_Team(JOUEUR, JOUEUR.NomJoueur + " vient de se connecter.", JOUEUR.IdTeam, true);
					String MessageAccueil = InfoJoueur.getString("message");
					if (MessageAccueil != null && !MessageAccueil.equals("")) {
						JOUEUR.Envoie("TxT#[Message Team] " + MessageAccueil);
					}
					
					if (serveur.ListeTeamAmie.containsKey(JOUEUR.IdTeam)) {
						JOUEUR.Envoie("CxFUSION");
					}
				}
				
				if (JOUEUR.Avatar.equals("_blason")) { // À mettre après la gestion des teams (sinon on ne connait pas les droits du joueur)
					ChargerBlason(JOUEUR);
				}
				
				serveur.Evénement.EnvoieEvenement(JOUEUR);
				if (serveur.Evénement.hasSecondaire()) {
					serveur.Evénement.EnvoieEvenement(false, JOUEUR);
				}
				
				if (serveur.Evénement.NouvelAn() || serveur.Evénement.Carnaval() || serveur.CouleursCPAuto) {
					if (serveur.Evénement.NouvelAn()) {
						Calendar date = Calendar.getInstance();   // Gets the current date and time
						JOUEUR.NomModifié = true;
						JOUEUR.NomJoueurModifié = date.get(Calendar.YEAR) + " ~ " + JOUEUR.NomJoueur;
					}
					
					JOUEUR.CouleurPseudo = true;
					JOUEUR.CouleurMessage = true;
					
					String Couleur = Integer.toHexString(Serveur.CouleursPseudoEvent[(int)(Math.random() * (Serveur.CouleursPseudoEvent.length - 1))]);
					JOUEUR.CouleurPseudoValeur = Couleur;
					JOUEUR.CouleurMessageValeur = Couleur;
				}

				JOUEUR.SalonChat = InfoJoueur.getString("s");
				if (!JOUEUR.SalonChat.isEmpty()) {
					serveur.Message_SalonChat(JOUEUR, JOUEUR.NomJoueur + " vient de se connecter au salon.", true);
					JOUEUR.Envoie("CxSAct#1");
				}

				// construction de la liste d'amis du joueur
				serveur.Bdd.Requete_ListeAmis.setInt(1, JOUEUR.IdJoueur);
				ResultSet InfoAmi = serveur.Bdd.Requete_ListeAmis.executeQuery();
				while (InfoAmi.next()) {
					JOUEUR.ListeAmis.add(new Ami(InfoAmi.getInt("id_ami"), InfoAmi.getString("n")));
				}

				// on prévient les gens qui ont ce joueur pour ami
				int Nb = serveur.ListeJoueur.size();
				for (int i = 0; i < Nb; i++) {
					Joueur Joueur = serveur.ListeJoueur.get(i);
					int NbAmis = Joueur.ListeAmis.size();
					for (int A = 0; A < NbAmis; A++) {
						Ami ami = Joueur.ListeAmis.get(A);
						if (ami.IdAmi == JOUEUR.IdJoueur) {
							// envoie message "NomJoueur connecté"
							Joueur.MessageBoite.add("CxM#" + JOUEUR.NomJoueur + "###0");
							break;
						}
					}
				}

				// Modo
				JOUEUR.ListeJoueursIgnorésChaine = InfoJoueur.getString("ln");
				String[] ListeJoueursIgnorés;
				if (JOUEUR.ListeJoueursIgnorésChaine.equals("")) {
					ListeJoueursIgnorés = JOUEUR.ListeJoueursIgnorésChaine.split(",");
				} else {
					ListeJoueursIgnorés = JOUEUR.ListeJoueursIgnorésChaine.substring(1).split(",");
				}
				for (int i = 0; i<ListeJoueursIgnorés.length; i++) {
					JOUEUR.ListeJoueursIgnorés.add(ListeJoueursIgnorés[i]);
				}

				serveur.BOITE.Requete(Boite.NB_MP_RECU, JOUEUR);

				JOUEUR.TempsMuet = InfoJoueur.getLong("modo_muet");
				//
				JOUEUR.MessageBoite.add("CxID#" + JOUEUR.NomJoueur + "#" + JOUEUR._Forteresse.Sexe + "#" + JOUEUR.Avatar + "#" + JOUEUR.IdJoueur + "#" + JOUEUR.SonActif + "#" + JOUEUR.SonGuideActif + "#" + JOUEUR.AfficherCoDeco + "#" + JOUEUR.AfficherCoAmis + "#" + JOUEUR.AfficherBulles + "#" + JOUEUR.TriForteresse + "#" + JOUEUR.OldSprite + "#" + JOUEUR.NoLag + "#" + JOUEUR.NoVote + "#" + (JOUEUR.AutorisationForum ? "1" : "0") + "#" + (JOUEUR.Membre ? "1" : "0") + "#" + JOUEUR.IdTeam + "#" + JOUEUR.RoleInt() + "#" + (JOUEUR.AutorisationModoTeam ? "1" : (JOUEUR.AutorisationRespoTriTeam ? "2" : "0")) + "#" + JOUEUR.TirContinu + "#" + JOUEUR.NoChat + "#" + JOUEUR.NoStats + "#" + JOUEUR.NoMP + "#" + JOUEUR.DerniereCoForum + "#" + JOUEUR.recompenseElo.recompensesActives);
				
				// Activer si nécessaire pour un événement
				// JOUEUR.joueurEv = new JoueurEv(JOUEUR);
			
			} else {
				JOUEUR.DemandeDeco = true;
				JOUEUR.RaisonDeco = "Erreur base de donnée";
			}
		} catch (Exception e) {
			System.err.print("3XC Chargement joueur "); e.printStackTrace();
			JOUEUR.DemandeDeco = true;
			JOUEUR.RaisonDeco = "Erreur base de donnée";
		}
	}

	private void Sauvegarde_Joueur(Joueur JOUEUR, int[] STATS) {
		try {
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(1, JOUEUR._Forteresse.Sexe);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(2, JOUEUR.SonActif);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(3, JOUEUR.SonGuideActif);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(4, JOUEUR.AfficherBulles);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(5, JOUEUR.AfficherCoDeco);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(6, JOUEUR.AfficherCoAmis);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(7, JOUEUR.TriForteresse);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(8, JOUEUR.OldSprite);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(9, JOUEUR.TirContinu);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(10, JOUEUR.NoLag);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(11, JOUEUR.NoChat);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(12, JOUEUR.NoStats);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(13, JOUEUR.NoVote);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setLong(14, JOUEUR.Stats_TempsDeJeu);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setString(15, JOUEUR.ListeJoueursIgnorésChaine);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setLong(16, JOUEUR.TempsMuet);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(17, STATS[0]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(18, STATS[1]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(19, STATS[2]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(20, STATS[3]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(21, STATS[4]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(22, STATS[5]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(23, STATS[6]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(24, STATS[7]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(25, STATS[8]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(26, STATS[9]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(27, STATS[10]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(28, STATS[11]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(29, STATS[12]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(30, STATS[13]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(31, STATS[14]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(32, STATS[15]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(33, STATS[16]);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setString(34, JOUEUR.SalonChat);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(35, JOUEUR.NoMP);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setLong(36, JOUEUR.DerniereCoForum);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setInt(37, JOUEUR.FlagMessageAnim);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setByte(38, JOUEUR.recompenseElo.recompensesActives);
			serveur.Bdd.Requete_SauvegardeStatsJoueur.setString(39, JOUEUR.NomJoueur.toLowerCase());

			serveur.Bdd.Requete_SauvegardeStatsJoueur.execute();
			
			//serveur.Bdd.Requete.execute("UPDATE $joueur SET o_f=" + JOUEUR._Forteresse.Sexe + ", o_s=" + JOUEUR.SonActif + ", o_sg=" + JOUEUR.SonGuideActif + ", o_b=" + JOUEUR.AfficherBulles + ", o_c=" + JOUEUR.AfficherCoDeco + ", o_ca=" + JOUEUR.AfficherCoAmis + ", o_tf=" + JOUEUR.TriForteresse + ", o_os=" + JOUEUR.OldSprite + ", o_tc=" + JOUEUR.TirContinu + ", o_nl=" + JOUEUR.NoLag + ", o_nc=" + JOUEUR.NoChat + ", o_ns=" + JOUEUR.NoStats + ", o_nv=" + JOUEUR.NoVote + ", s_tt=" + JOUEUR.Stats_TempsDeJeu + ", s_dc=UNIX_TIMESTAMP(), ln='" + JOUEUR.ListeJoueursIgnorésChaine + "', modo_muet=" + JOUEUR.TempsMuet + ", sb_pj=" + STATS[0] + ", sb_pg=" + STATS[1] + ", sb_t=" + STATS[2] + ", sb_m=" + STATS[3] + ", sb_r=" + STATS[4] + ", sa_pj=" + STATS[5] + ", sa_pg=" + STATS[6]
					//		+ ", sa_js=" + STATS[7] + ", sa_jg=" + STATS[8] + ", sa_p=" + STATS[9] + ", sa_pf=" + STATS[10] + ", sa_t=" + STATS[11] + ", sa_rs=" + STATS[12] + ", sa_rp=" + STATS[13] + ", sa_rt=" + STATS[14] + ", sa_rv=" + STATS[15] + ", sa_e=" + STATS[16] + " WHERE n='" + JOUEUR.NomJoueur.toLowerCase() + "' LIMIT 1");
		} catch (Exception e) {
			System.err.print("3XC Sauvegarde joueur "); e.printStackTrace();
		}
	}

	private void Creer_Mara(Joueur JOUEUR) {
		try {

			serveur.Bdd.Requete_MaraCreate.setString(1, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_MaraCreate.executeUpdate();

		} catch (Exception e) {
			System.err.print("3XC Creer_Mara "); e.printStackTrace();
		}
	}

	private void Sauvegarde_Mara_Banni(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_MaraBanniSave.setLong(1, JOUEUR.Mara_TempsBan);
			serveur.Bdd.Requete_MaraBanniSave.setLong(2, JOUEUR.Mara_TempsJeu);
			serveur.Bdd.Requete_MaraBanniSave.setString(3, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_MaraBanniSave.executeUpdate();

		} catch (Exception e) {
			System.err.print("3XC Sauvegarde_MaraBan "); e.printStackTrace();
		}
	}

	private void Sauvegarde_Mara(Joueur JOUEUR) {
		try {
			/*serveur.Bdd.Requete_MaraSave.setLong(1, JOUEUR.Mara_TempsBan);
			serveur.Bdd.Requete_MaraSave.setLong(2, JOUEUR.Mara_TempsJeu);
			serveur.Bdd.Requete_MaraSave.setLong(3, JOUEUR.Mara_Guide_JoueurSauvés);
			serveur.Bdd.Requete_MaraSave.setLong(4, JOUEUR.Mara_Guide_JoueurGuidées);
			serveur.Bdd.Requete_MaraSave.setLong(5, JOUEUR.Mara_Guide_PartiesGuidées);
			serveur.Bdd.Requete_MaraSave.setLong(6, JOUEUR.Mara_Run_ArrivéesPremier);
			serveur.Bdd.Requete_MaraSave.setLong(7, JOUEUR.Mara_Run_PartiesJouées);
			serveur.Bdd.Requete_MaraSave.setLong(8, JOUEUR.Mara_Fight_Tués);
			serveur.Bdd.Requete_MaraSave.setLong(9, JOUEUR.Mara_Fight_Survivor);
			serveur.Bdd.Requete_MaraSave.setLong(10, JOUEUR.Mara_Fight_PartiesJouées);
			serveur.Bdd.Requete_MaraSave.setLong(11, JOUEUR.Mara_Défilantes_Tués);
			serveur.Bdd.Requete_MaraSave.setLong(12, JOUEUR.Mara_Défilantes_Survivor);
			serveur.Bdd.Requete_MaraSave.setLong(13, JOUEUR.Mara_Défilantes_PartiesJouées);
			serveur.Bdd.Requete_MaraSave.setLong(14, JOUEUR.Mara_Rally_TempsTotal);
			serveur.Bdd.Requete_MaraSave.setLong(15, JOUEUR.Mara_Rally_PartiesJouées);
			serveur.Bdd.Requete_MaraSave.setString(16, JOUEUR.NomJoueur);*/

			serveur.Bdd.Requete_MaraSave.setLong(1, JOUEUR.Mara_TempsBan);
			serveur.Bdd.Requete_MaraSave.setLong(2, JOUEUR.Mara_TempsJeu);
			serveur.Bdd.Requete_MaraSave.setLong(3, JOUEUR.Mara_Boum_PartiesJouées);
			serveur.Bdd.Requete_MaraSave.setLong(4, JOUEUR.Mara_Boum_Tués);
			serveur.Bdd.Requete_MaraSave.setLong(5, JOUEUR.Mara_Boum_PartiesGagnées);
			serveur.Bdd.Requete_MaraSave.setLong(6, JOUEUR.Mara_Boum_Adversaires);
			serveur.Bdd.Requete_MaraSave.setLong(7, JOUEUR.Mara_Boum_Suicides);
			serveur.Bdd.Requete_MaraSave.setLong(8, JOUEUR.Mara_Boum_Manches);
			serveur.Bdd.Requete_MaraSave.setString(9, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_MaraSave.executeUpdate();

		} catch (Exception e) {
			System.err.print("3XC Sauvegarde_Mara "); e.printStackTrace();
		}
	}

	private void Identification(Joueur JOUEUR, String NOM, String MDP) {
		try {
			String NomPetit = NOM.toLowerCase();
			serveur.Bdd.Requete_Identification.setString(1, NomPetit);
			ResultSet Retour = serveur.Bdd.Requete_Identification.executeQuery();
			if (Retour.next()) {
				if (Retour.getString(1).equals(MDP)) {
					int Nb = serveur.ListeJoueur.size();
					for (int i = 0; i < Nb; i++) {
						Joueur Joueur = serveur.ListeJoueur.get(i);
						if (Joueur.NomJoueur.equalsIgnoreCase(NomPetit)) {
							// Déjà connecté !
							if (!JOUEUR.AdresseIP.equals(Joueur.AdresseIP))
							{
								// deux personnes différentes
								JOUEUR.Envoie("CxHACK"); // celui qui se connecte
								Joueur.Envoie("CxHACK"); // celui qui est déja connecté
								System.out.println("HACK " + Joueur.NomJoueur + " " + JOUEUR.AdresseIP + " " + Joueur.AdresseIP);
							}
							Joueur.Deconnexion("Tentative Reconnexion");
							serveur.ListeJoueur.remove(Joueur);
							JOUEUR.MessageBoite.add("CxID#");
							Retour.close();

							return;
						}
					}
					NOM = NOM.substring(0, 1).toUpperCase() + NOM.substring(1).toLowerCase();
					JOUEUR.NomJoueur = NOM;
					JOUEUR.NomJoueurModifié = NOM;
					if (serveur.Evénement.Halloween()) {
						JOUEUR.NomJoueurModifié += " " + Serveur.TitresHalloween[(int)(Math.random() * Serveur.TitresHalloween.length)];
					}
					serveur.JoueurAAjouter.add(JOUEUR);
					Chargement_Joueur(JOUEUR, NomPetit, false);
					JOUEUR.Identifié = true;
					//Log de la connexion avec le pseudo
					System.out.println("C0N [" + Calendar.getInstance().getTime() + "] " + JOUEUR.NomJoueur +" " + JOUEUR.AdresseIP + " ### Connexion");
					return;
				} else {
					JOUEUR.MessageBoite.add("CxID");
					return;
				}
			} else {
				JOUEUR.MessageBoite.add("CxID");
				return;
			}
		} catch (Exception e) {
			System.err.print("3XC Identification "); e.printStackTrace();
			JOUEUR.DemandeDeco = true;
			JOUEUR.RaisonDeco = "Erreur identification";
			return;
		}
	}

	private void Demande_Liste_Modo(Joueur JOUEUR) {
		MemoryUsage Memoire = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		StringBuilder Liste = new StringBuilder(Memoire.getUsed() + "," + Memoire.getMax() + "," + serveur.ListeClefSocketJoueur.size() + "," + serveur.ListeClient.size() + "," + serveur.ListeJoueur.size() + "," + serveur.ListeJoueurSansPartie.size() + "," + serveur.ListePartie.size() + "," + serveur.ListeMuetIP.size() + "," + serveur.ListeBanniIP.size() + "," + serveur.ListeMuetNom.size() + "," + serveur.ListeBanniNom.size() + "," + serveur.ListeKickIP.size() + "," + serveur.NbThread + ";");
		StringBuilder ListeArbitre = new StringBuilder();
		try {
			ResultSet ListeModo = serveur.Bdd.Requete_ListeModo.executeQuery();
			while (ListeModo.next()) {
				if (ListeModo.getInt(2)==5) {
					Liste.append("," + ListeModo.getString(1));
				} else {
					ListeArbitre.append("," + ListeModo.getString(1));
				}
			}
			Liste.append(";");
			Liste.append(ListeArbitre);
		} catch (Exception e) {
			System.err.print("3XC Demande Liste modo "); e.printStackTrace();
		}
		JOUEUR.MessageBoite.add("CxMODO#" + Liste);
	}

	private String hash(Joueur JOUEUR, String IP) {
		if (IP == null) {
			return "";
		}
		if (JOUEUR.AutorisationRecrueForum) {
			return "";
		}
		String[] Vals = IP.split("\\.");
		if (Vals.length >= 4) {
			Integer somme = Integer.valueOf(Vals[0]) + Integer.valueOf(Vals[1]) + Integer.valueOf(Vals[2]) + Integer.valueOf(Vals[3]);
			IP = somme.toString();
		}
		return IP;
	}

	private void Demande_Histo_Forum(Joueur JOUEUR, String Id) {
		try {
			// on récupère le message concerné
			serveur.Bdd.Requete_IPMessage.setInt(1, Integer.parseInt(Id));
			ResultSet Result = serveur.Bdd.Requete_IPMessage.executeQuery();

			if (Result.next()) {
				String IP = Result.getString(1);
				String Cible = Result.getString(2);
				serveur.BOITE.Requete(Boite.DEMANDE_HISTO, JOUEUR, Cible, IP);
			}
		} catch (Exception e) {
			System.err.print("3XC Demande histo forum "); e.printStackTrace();
		}
	}


	private void Demande_Histo(Joueur JOUEUR, String CIBLE, String IP_CIBLE, boolean RECENT) {
		try {
			ResultSet Resultat;
			if (!RECENT) {
				serveur.Bdd.Requete_HistoSanctionJoueur.setString(1, CIBLE);
				serveur.Bdd.Requete_HistoSanctionJoueur.setString(2, IP_CIBLE);
				Resultat = serveur.Bdd.Requete_HistoSanctionJoueur.executeQuery();
				//Resultat = serveur.Bdd.Requete.executeQuery("SELECT * FROM $sanctions WHERE nom_joueur = '" + CIBLE + "' or ip = '" + IP_CIBLE + "' ORDER BY id ASC");
			} else if (JOUEUR.AutorisationModo){
				Resultat = serveur.Bdd.Requete_HistoSanctionLast.executeQuery();
				//Resultat = serveur.Bdd.Requete.executeQuery("SELECT * FROM $sanctions ORDER BY id DESC LIMIT 40");
			} else {
				Resultat = serveur.Bdd.Requete_HistoSanctionLastForum.executeQuery();
				//Resultat = serveur.Bdd.Requete.executeQuery("SELECT * FROM $sanctions WHERE sanction='mute_forum' or sanction='mute_forum_nom' or sanction='noté' ORDER BY id DESC LIMIT 40");
			}
			StringBuilder Chaine = new StringBuilder("CxHIST#"+CIBLE+"#"+(JOUEUR.AutorisationAdmin ? IP_CIBLE : hash(JOUEUR, IP_CIBLE))+"#");
			while (Resultat.next()) {
				String Id = Resultat.getString("id");
				String Date = Resultat.getString("d");
				String Modo = Resultat.getString("nom_modo");
				String LaCible = Resultat.getString("nom_joueur");
				String Raison = Resultat.getString("raison");
				String Sanction = Resultat.getString("sanction");
				String IP = Resultat.getString("ip");
				String Temps = Resultat.getString("temps");

				if (
						((Sanction.equals("mute") 
								|| Sanction.equals("ban")
								|| Sanction.equals("banni")
								|| Sanction.equals("ban-vp")
								//|| Sanction.equals("megaban")
								|| Sanction.equals("ban-bot")
								|| Sanction.equals("noté"))
								&& JOUEUR.AutorisationSuperArbitre)
						|| (((Sanction.equals("mute") && !RECENT)
								||Sanction.equals("mute_forum")
								||Sanction.equals("mute_forum_nom")
								||Sanction.startsWith("noté"))
								&& JOUEUR.AutorisationModoForum)
						|| JOUEUR.AutorisationModo) {
					if (Date == null) {
						Date = "";
					}
					if (IP != null) {
						if (!JOUEUR.AutorisationAdmin) {
							IP = hash(JOUEUR, IP);
						}
						if(JOUEUR.AutorisationSuperArbitre) {
							IP = "";
						}
					} else {
						IP = "";
					}
					if (Modo == null || !JOUEUR.AutorisationModo) {
						if (JOUEUR.AutorisationModoForum && (Sanction.equals("noté") ||  Sanction.equals("mute_forum") || Sanction.equals("mute_forum_nom")) && Modo != null) {
							serveur.Bdd.Requete_VisualisationFlag.setString(1, Modo.toLowerCase());
							ResultSet SelectJoueur = serveur.Bdd.Requete_VisualisationFlag.executeQuery();
							
							if(SelectJoueur.next()){
								int FLAG = SelectJoueur.getInt(1);
								
							   // Affichage de l'auteur du mute forum/de la note si recrue forum, modo forum, modo + modo forum, modo fofo non ano
								if (!(FLAG == 1 || FLAG == 11 || FLAG == 12 || FLAG == 50 || FLAG == 80))
									Modo = "";
							}
						}
						else
						  Modo = "";
					}

					if (Raison == null) {
						Raison = "";
					}
					Chaine.append(Serveur.$$ + Date + Serveur.$ + Modo + Serveur.$ + LaCible + Serveur.$ + Raison + Serveur.$ + IP + Serveur.$ + Temps + Serveur.$ + Sanction + Serveur.$ + Id);

				}

			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Demande histo "); e.printStackTrace();
		}
	}


	private void Histo_Team_Joueur(Joueur JOUEUR, String NOM) {
		try {
			serveur.Bdd.Requete_HistoTeamJoueur.setString(1, NOM);
			serveur.Bdd.Requete_HistoTeamJoueur.setString(2, NOM);
			ResultSet Resultat = serveur.Bdd.Requete_HistoTeamJoueur.executeQuery();
			//StringBuilder Chaine = new StringBuilder("CxINFO#Historique des teams de " + NOM + " :");
			StringBuilder Chaine = new StringBuilder("TxHisJ#"+NOM+"#");
			while (Resultat.next()) {
				String Auteur = Resultat.getString("auteur");
				String Cible = Resultat.getString("cible");
				String Action = Resultat.getString("action");
				Date Date = Resultat.getDate("date");
				Integer IdTeam = Resultat.getInt("id_team");
				String NomTeam = Resultat.getString("nom");
				if (NomTeam == null || NomTeam.isEmpty()) {
					NomTeam = "Dissoute";
				}
				//Chaine.append("\n");
				Chaine.append(Serveur.$ + Serveur.$ + Date + Serveur.$ + Action + Serveur.$ + Auteur + Serveur.$ + Cible + Serveur.$ + IdTeam + Serveur.$ + NomTeam);
				/*if (Action.equals("C")) {
					Chaine.append("[" + Date + "] " + Auteur + " a créé la team "+IdTeam+" (" + NomTeam +").");
				} else if (Action.equals("D")) {
					//Chaine.append("[" + Date + "] " + Auteur + " a dissout la team "+IdTeam+" (" + NomTeam +").");
				} else if (Action.equals("R") && Cible != null && !Cible.equals("")) {
					//Chaine.append("[" + Date + "] " + Cible + " a été recruté dans la team "+IdTeam+" (" + NomTeam +").");
				} else if (Action.equals("Q")) {
					//Chaine.append("[" + Date + "] " + Auteur + " a quitté la team "+IdTeam+" (" + NomTeam +").");
				} else if (Action.equals("E") && Cible != null && !Cible.equals("")) {
					//Chaine.append("[" + Date + "] " + Cible + " a été éjecté de la team " + IdTeam+" (" + NomTeam +").");
				}
*/
			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Demande histo team joueur "); e.printStackTrace();
		}
	}

	private void Histo_Team(Joueur JOUEUR, int ID_TEAM) {
		try {
			serveur.Bdd.Requete_HistoTeam.setInt(1, ID_TEAM);
			ResultSet Resultat = serveur.Bdd.Requete_HistoTeam.executeQuery();
			StringBuilder Chaine = new StringBuilder("TxHis#"+ID_TEAM+"#");
			while (Resultat.next()) {
				String Auteur = Resultat.getString("auteur");
				String Cible = Resultat.getString("cible");
				String Action = Resultat.getString("action");
				Date Date = Resultat.getDate("date");
				Chaine.append(Serveur.$ + Serveur.$ + Date + Serveur.$ + Action + Serveur.$ + Auteur + Serveur.$ + Cible);
			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Demande histo team "); e.printStackTrace();
		}
	}

	private void Liste_MP_Recus(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_ListeMPRecus.setString(1, JOUEUR.NomJoueur);
			ResultSet Resultat = serveur.Bdd.Requete_ListeMPRecus.executeQuery();
			StringBuilder Chaine = new StringBuilder("MPxLR#");
			while (Resultat.next()) {
				String Auteur = Resultat.getString("a");
				String Destinataire = Resultat.getString("r");
				Timestamp DateTS = Resultat.getTimestamp("d");
				Date Date = new Date(DateTS.getTime());
				SimpleDateFormat SimpleFormat = new SimpleDateFormat("dd MMM HH:mm");
				String Message = Resultat.getString("m");
				String Id = Resultat.getString("id");
				Chaine.append(Serveur.$ + Serveur.$ + SimpleFormat.format(Date) + Serveur.$ + Auteur + Serveur.$ + Destinataire + Serveur.$ + Message + Serveur.$ + Id);
			}
			JOUEUR.Envoie(Chaine.toString());
		} catch (Exception e) {
			System.err.print("3XC Liste_MPRecus "); e.printStackTrace();
		}
	}

	private void Liste_MP_Envoyes(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_ListeMPEnvoyés.setString(1, JOUEUR.NomJoueur);
			ResultSet Resultat = serveur.Bdd.Requete_ListeMPEnvoyés.executeQuery();
			StringBuilder Chaine = new StringBuilder("MPxLE#");
			while (Resultat.next()) {
				String Auteur = Resultat.getString("a");
				String Destinataire = Resultat.getString("r");
				Timestamp DateTS = Resultat.getTimestamp("d");
				Date Date = new Date(DateTS.getTime());
				SimpleDateFormat SimpleFormat = new SimpleDateFormat("dd MMM HH:mm");
				String Message = Resultat.getString("m");
				String Id = Resultat.getString("id");
				Chaine.append(Serveur.$ + Serveur.$ + SimpleFormat.format(Date) + Serveur.$ + Auteur + Serveur.$ + Destinataire + Serveur.$ + Message + Serveur.$ + Id);
			}
			
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Liste_MPEnvoyes "); e.printStackTrace();
		}
	}

	private void Supprimer_MP(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_SupprimerMP.setString(1, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_SupprimerMP.execute();
			serveur.BOITE.Requete(Boite.LISTE_MP_RECUS, JOUEUR);

		} catch (Exception e) {
			System.err.print("3XC Supprimer_MP "); e.printStackTrace();
		}
	}

	private void Supprimer_MP_By_Id(Joueur JOUEUR, int ID) {
		try {
			serveur.Bdd.Requete_SupprimerMPById.setString(1, JOUEUR.NomJoueur);
			serveur.Bdd.Requete_SupprimerMPById.setInt(2, ID);
			serveur.Bdd.Requete_SupprimerMPById.execute();
			serveur.BOITE.Requete(Boite.LISTE_MP_RECUS, JOUEUR);

		} catch (Exception e) {
			System.err.print("3XC Supprimer_MP_By_Id "); e.printStackTrace();
		}
	}
	
	private void Nb_MP_Recu(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_NbMPDest.setString(1, JOUEUR.NomJoueur);
			ResultSet NbMP = serveur.Bdd.Requete_NbMPDest.executeQuery();
			if (NbMP.next()) {
				Integer Nb = NbMP.getInt("c");
				if (Nb>0) {
					JOUEUR.Envoie("MPxNB#"+Nb);
				}
			}

		} catch (Exception e) {
			System.err.print("3XC Nb_MP_Recu "); e.printStackTrace();
		}
	}

	private void Nouveau_MP(Joueur JOUEUR, String DESTINATAIRE, String MESSAGE) {
		if (!Joueur.PseudoConforme(DESTINATAIRE) || DESTINATAIRE.length() > 20 || JOUEUR.NomJoueur.equalsIgnoreCase(DESTINATAIRE)) {
			return;
		}

		try {
			// limite : temps de jeu, nb de messages envoyés, temps entre messages, limite nb de messsages reçus du destinataire, option pour désactiver, ln
			if (JOUEUR.Stats_TempsDeJeu > RestrictionHeure.UTILISER_MESSAGERIE) {
				if (!JOUEUR.Muet) {

					serveur.Bdd.Requete_InfoJoueurMP.setString(1, DESTINATAIRE);
					ResultSet Destin = serveur.Bdd.Requete_InfoJoueurMP.executeQuery();
					if (!Destin.next()) {
						JOUEUR.Envoie("MPxNM#6"); // Joueur inexistant
						return;
					}
					String Cible = Destin.getString("n");
					Joueur CibleJoueur = serveur.Joueur(Cible);
					boolean AutorisationMP = Destin.getString("o_nomp").equals("0");
					if (CibleJoueur!=null) AutorisationMP = CibleJoueur.NoMP == 0;
					String LNtxt = Destin.getString("ln");
					List<String> LN;
					
					if (LNtxt != null && LNtxt.length() > 0) {
						LN =  Arrays.asList(LNtxt.split(","));
					} else {
						LN = new ArrayList<String>();
					}
					
					if (AutorisationMP) {
						if (!LN.contains(JOUEUR.NomJoueur) && (CibleJoueur == null || !CibleJoueur.ListeIPIgnorées.contains(JOUEUR.AdresseIP))) {
							serveur.Bdd.Requete_NbMPDest.setString(1, Cible);
							ResultSet NbMP = serveur.Bdd.Requete_NbMPDest.executeQuery();
							if (NbMP.next()) {
								Integer Nb = NbMP.getInt("c");
								if (Nb<=60) {
									serveur.Bdd.Requete_InfoDernierMP.setString(1,Cible);
									serveur.Bdd.Requete_InfoDernierMP.setString(2,JOUEUR.NomJoueur);
									serveur.Bdd.Requete_InfoDernierMP.setString(3,JOUEUR.AdresseIP);
									ResultSet LastMP = serveur.Bdd.Requete_InfoDernierMP.executeQuery();
									if (LastMP.next() && LastMP.getInt("c") < 4) {
										serveur.Bdd.Requete_InfoNbDernierMP.setString(1,JOUEUR.NomJoueur);
										serveur.Bdd.Requete_InfoNbDernierMP.setString(2,JOUEUR.AdresseIP);
										ResultSet LastNbMP = serveur.Bdd.Requete_InfoNbDernierMP.executeQuery();
										if (LastNbMP.next() && LastNbMP.getInt("c") < 20) {
											long Now = System.currentTimeMillis();
											if (Now - JOUEUR.LastMP > 30000) {
												MESSAGE = MESSAGE.replace("#", "").replace("\u0002", "").replace("\u0001", "");
												
												if(MESSAGE.length() > 6000)
													MESSAGE = MESSAGE.substring(0, 6000);
												
												JOUEUR.LastMP = Now;
												EnvoyerNouveauMP(JOUEUR.NomJoueur, Cible, MESSAGE, JOUEUR.AdresseIP);
												serveur.BOITE.Requete(Boite.LISTE_MP_ENVOYES, JOUEUR);
												// notification si en ligne
												if (CibleJoueur!=null) {
													CibleJoueur.Envoie("CxINFO#Vous venez de recevoir un message privé de " + JOUEUR.NomJoueur+ ".");
													serveur.BOITE.Requete(Boite.NB_MP_RECU, CibleJoueur);
												}
												System.out.println("MMPP " + Now + " " + JOUEUR.NomJoueur + " (" + JOUEUR.AdresseIP + ") a MP " + Cible + " ");
											} else {
												JOUEUR.Envoie("MPxNM#4"); // temps de 30 secondes entre messages
											}
										} else {
											JOUEUR.Envoie("MPxNM#10"); // Pas plus de 20 messages envoyés en moins de 24 heures (IP+compte)
										}
									} else {
										JOUEUR.Envoie("MPxNM#8"); // Quatre messages par destinataire toutes les 24 heures
									}
								} else {
									JOUEUR.Envoie("MPxNM#9"); // boite pleine
								}
							}
						} else {
							JOUEUR.Envoie("MPxNM#2"); // liste noire
						}
					} else {
						JOUEUR.Envoie("MPxNM#5"); // joueur ne souhaite pas recevoir de messages
					}
				} else {
					JOUEUR.Envoie("MPxNM#7"); // mute
				}
			} else {
				JOUEUR.Envoie("MPxNM#1"); // temps de jeu insuffisant
			}
		} catch (Exception e) {
			System.err.print("3XC Créer_MP "); e.printStackTrace();
		}
	}


	private void Creation_Compte(Joueur JOUEUR, String NOM, String MDP) {
		boolean Réussite;
		// Pseudo
		if (!Joueur.PseudoConforme(NOM) || NOM.length() < 3) {
			JOUEUR.DemandeDeco = true;
			JOUEUR.RaisonDeco = "Pseudo Non Conforme.";
			Réussite = false;
			if (NOM.length()>0&&NOM.length()<3) {
				System.out.println("Modification client, ban auto");
				serveur.ListeBanniIP.add(JOUEUR.AdresseIP);
			}
		} else {
			//
			try {
				
				serveur.Bdd.Requete_Identification.setString(1, NOM);
				ResultSet Retour = serveur.Bdd.Requete_Identification.executeQuery();
				if (Retour.next()) {
					Réussite = false;
				} else {
					serveur.Bdd.Requete_NouveauCompte.setString(1, NOM);
					serveur.Bdd.Requete_NouveauCompte.setString(2, MDP);
					serveur.Bdd.Requete_NouveauCompte.execute();
					JOUEUR.Envoie("CxINFO#Utilisez /aide pour avoir un aperçu de quelques commandes utiles.");
					Réussite = true;
				}
			} catch (Exception e) {
				System.err.print("3XC CC "); e.printStackTrace();
				Réussite = false;
			}
		}

		if (Réussite) {
			JOUEUR.CréationCompte = false;
			NOM = NOM.substring(0, 1).toUpperCase() + NOM.substring(1).toLowerCase();
			JOUEUR.NomJoueur = NOM;
			JOUEUR.NomJoueurModifié = NOM;
			serveur.JoueurAAjouter.add(JOUEUR);
			Chargement_Joueur(JOUEUR, NOM.toLowerCase(), true);
			JOUEUR.Identifié = true;
			System.out.println("C0N [" + Calendar.getInstance().getTime() + "] " + JOUEUR.NomJoueur + " " + JOUEUR.AdresseIP + " ### Création Compte");
		} else {
			try {
				serveur.Bdd.Requete_IdJoueurEtBan.setString(1, NOM);
				ResultSet rs = serveur.Bdd.Requete_IdJoueurEtBan.executeQuery();
				
				if (rs.next() && rs.getInt("ban") == 2) {
					JOUEUR.MessageBoite.add("CxCC#");
				} else {
					JOUEUR.MessageBoite.add("CxCC");
				}
			} catch (Exception e) {
				JOUEUR.MessageBoite.add("CxCC");
				System.err.print("3XC CC2 "); e.printStackTrace();
			}
			
		}
	}



	private void Supprimer_Avatar(Joueur JOUEUR, String CIBLE) {
		if (!Joueur.PseudoConforme(CIBLE)) {
			return;
		}
		
		try {
			long DEBUT = System.currentTimeMillis();
			Joueur Cible = serveur.Joueur(CIBLE);
			if (Cible != null) {
				Cible.Avatar = Serveur.AVATAR_DEFAUT;
			}
			serveur.Bdd.Requete_SupprimerAvatarJoueur.setString(1, CIBLE);
			serveur.Bdd.Requete_SupprimerAvatarJoueur.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET i = 0 WHERE n='" + CIBLE + "' LIMIT 1");
			serveur.Bdd.Requete_SupprimerAvatarMessages.setString(1, CIBLE);
			serveur.Bdd.Requete_SupprimerAvatarMessages.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $forum_m SET i = 0 WHERE a='" + CIBLE + "'");
			serveur.Bdd.Requete_SupprimerAvatarTopics.setString(1, CIBLE);
			serveur.Bdd.Requete_SupprimerAvatarTopics.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $forum_s SET i = 0 WHERE a='" + CIBLE + "'");
			JOUEUR.Envoie("CxINFO#L'avatar de " + CIBLE + " a été supprimé.");
			serveur.Log(JOUEUR, DEBUT, "Supprimer avatar");
		} catch (Exception e) {
			System.err.print("3XC Supprimer Avatar "); e.printStackTrace();
		}
	}

	private void Supprimer_Description(Joueur JOUEUR, String CIBLE) {
		if (!Joueur.PseudoConforme(CIBLE)) {
			return;
		}
		
		try {
			Joueur Cible = serveur.Joueur(CIBLE);
			if (Cible !=null) {
				Cible.Description = "Edité";
			}
			serveur.Bdd.Requete_SupprimerDescriptionJoueur.setString(1, CIBLE);
			serveur.Bdd.Requete_SupprimerDescriptionJoueur.executeUpdate();
			//serveur.Bdd.Requete.executeUpdate("UPDATE $joueur SET d = 'Edité' WHERE n='" + CIBLE + "' LIMIT 1");
			JOUEUR.Envoie("CxINFO#La description de " + CIBLE + " a été supprimée.");
		} catch (Exception e) {
			System.err.print("3XC Supprimer description "); e.printStackTrace();
		}
	}

	private boolean CheckParameter(String Regexp, String Param) {
		Pattern pattern = Pattern.compile(Regexp);
		Matcher matcher = pattern.matcher(Param);
		return matcher.find();
	}

	private void Liste_Map_Joueur(Joueur JOUEUR, String CIBLE) {
		if (CheckParameter("[^a-zA-Z0-9 ]",CIBLE)) {
			return;
		}
		try {
			serveur.Bdd.Requete_ListeMapJoueur.setString(1, CIBLE);
			ResultSet Resultat = serveur.Bdd.Requete_ListeMapJoueur.executeQuery();
			JOUEUR.EnvoieCartes(Resultat);

		} catch (Exception e) {
			System.err.print("3XC Liste map joueur "); e.printStackTrace();
		}
	}

	private void Recherche_Map_By_Id(Joueur JOUEUR, String CODE) {

		Long IdMap = Long.parseLong(CODE);
		try {
			serveur.Bdd.Requete_RechercheMapById.setLong(1, IdMap);
			ResultSet Resultat = serveur.Bdd.Requete_RechercheMapById.executeQuery();
			JOUEUR.EnvoieCartes(Resultat);

		} catch (Exception e) {
			System.err.print("3XC RMBI "); e.printStackTrace();
		}
	}

	private void Recherche_Map_By_Ids(Joueur JOUEUR, String CODES) {
		Pattern pattern = Pattern.compile("\\d(,\\d)*");
		Matcher matcher = pattern.matcher(CODES);
		if (!matcher.find()) {
			return;
		}


		try {
			ResultSet Resultat = serveur.Bdd.Requete.executeQuery("SELECT * FROM `$maps` WHERE id in ("+ CODES + ") LIMIT 1000");
			JOUEUR.EnvoieCartes(Resultat);

		} catch (Exception e) {
			System.err.print("3XC RMBIS "); e.printStackTrace();
		}
	}
	
	private void Playlist_standard(Joueur JOUEUR, String CODE){
		try {
			ResultSet Resultat = playlist_generation(CODE);
			JOUEUR.EnvoieCartes(Resultat, CODE);
		} catch (Exception e) {
			System.err.print("3XC RMBIS2 "); e.printStackTrace();
		}
	}

	private void Playlist_standard_commande(Joueur JOUEUR, String CODE){
		try {
			ResultSet Resultat = playlist_generation(CODE);
			if(Resultat != null){
				StringBuilder Chaine = new StringBuilder("CxPlay#1#"+CODE+"#");
				while (Resultat.next()) {
					Chaine.append(Resultat.getString("id")+",");
				}
				Chaine.delete(Chaine.length()-3, Chaine.length());
				JOUEUR.Reception_Spéciale(Chaine.toString());
			}

		} catch (Exception e) {
			System.err.print("3XC RMBIS3 "); e.printStackTrace();
		}
	}

	private ResultSet playlist_generation(String CODE)
			throws SQLException {
		String[] code = CODE.split(" ");
		if(code.length != 2)
			return null;
		CODE = code[1];
		ResultSet Resultat = null;
		if(CODE.equals("offi")){
			Resultat = serveur.Bdd.Requete.executeQuery("SELECT * FROM `$maps` WHERE id < 100 LIMIT 200");
		}
		if(serveur.Bdd instanceof MySQLBDD && (CODE.equals("2008") ||CODE.equals("2009") ||CODE.equals("2010") ||CODE.equals("2011"))){
			String BestOf = "SELECT * FROM `$maps` " +
					"WHERE DATE_FORMAT(FROM_UNIXTIME(CAST(id/1000 AS DECIMAL(10,0) )), \"%Y\") = ";
			String BestOf2 =
					" AND votes > 100 " +
					"ORDER BY pour/votes DESC " +
					"LIMIT 200";
			
			if(CODE.equals("2008")){
				BestOf = BestOf + "2008" + BestOf2;
			}
			if(CODE.equals("2009")){
				BestOf = BestOf + "2009" + BestOf2;
			}
			if(CODE.equals("2010")){
				BestOf = BestOf + "2010" + BestOf2;
			}
			if(CODE.equals("2011")){
				BestOf = BestOf + "2011" + BestOf2;
			}
			Resultat = serveur.Bdd.Requete.executeQuery(BestOf);
		}
		if(CODE.equals("ng1")){
			Resultat = serveur.Bdd.Requete.executeQuery(
					"SELECT * " +
					"FROM `$maps` " +
					"WHERE mode = 2 " +
					"AND played > 100 " +
					"AND mara_record > 19000 " +
					"ORDER BY survie/played DESC "+
					"LIMIT 200");
		}
		if(CODE.equals("ng2")){
			Resultat = serveur.Bdd.Requete.executeQuery(
					"SELECT * " +
					"FROM `$maps` " +
					"WHERE mode = 2 " +
					"AND played > 100 " +
					"AND mara_record > 19000 " +
					"ORDER BY survie/played "+
					"LIMIT 200");
		}
		if(Resultat == null) //Playlist personnel
		{
			if(code.length == 2){
				serveur.Bdd.Requete_CartesDunePlaylist.setInt(1, ID_Joueur(code[0]));
				serveur.Bdd.Requete_CartesDunePlaylist.setString(2, code[1]);
				ResultSet preresult = serveur.Bdd.Requete_CartesDunePlaylist.executeQuery();
				preresult.next();
				String CODES = preresult.getString("cartes");
				if (CheckParameter("[^0-9,]",CODES)) {
					return null;
				}				
				Resultat = serveur.Bdd.Requete.executeQuery("SELECT * FROM `$maps` WHERE id in ("+ CODES + ") LIMIT 200");
			}
		}
		if(Resultat != null)
		{
			int id = ID_Joueur(code[0]);
			if (id != -1) {
				Playlist_populair_plus(1, id, code[1]);
				
				serveur.Bdd.Requete_PlaylistActualisationDernierLoad.setInt(1, id);
				serveur.Bdd.Requete_PlaylistActualisationDernierLoad.setString(2, code[1]);
				serveur.Bdd.Requete_PlaylistActualisationDernierLoad.execute();
			}
		}
		return Resultat;
	}
	
	/**
	 * Augmante la populair d'une playlist dans la base de données.
	 * 
	 * @param plus
	 * 			Le nombre à ajouter à la populair de la playlist
	 * @param id_joueur
	 * 			L'id du joueur qui est l'auteur de la playlist
	 * 			Voir ID_Joueur(NomDuJoueur)
	 * @param code
	 * 			Le code choisit par l'auteur pour designer la playlist
	 */
	public void Playlist_populair_plus(int plus, int id_joueur, String code)
	{
		try {
			serveur.Bdd.Requete_PlaylistpopulairPlus.setInt(1, plus);
			serveur.Bdd.Requete_PlaylistpopulairPlus.setInt(2, id_joueur);
			serveur.Bdd.Requete_PlaylistpopulairPlus.setString(3, code);
			serveur.Bdd.Requete_PlaylistpopulairPlus.execute();
		} catch (SQLException e) {
			System.err.println("3XC PPP ");
			e.printStackTrace();
		}
	}
	
	public void Playlist_populair_plus(int plus, String code){
		if(code != null && code.length()>0){
			String[] c = code.split(" ");
			if(c.length == 2){
				try{
					Playlist_populair_plus(plus, ID_Joueur(c[0]), c[1]);
				}
				catch (SQLException e) {
					System.err.println("3XC PPP2 ");
					e.printStackTrace();
				}
			}
		}
	}

	private boolean Playlist_publier_valider(Joueur JOUEUR, String CODE, String TITRE, boolean retourJoueur)
	{
		Boolean valide = true;
		try {
			if(CODE.isEmpty() || TITRE.isEmpty())
			{
				JOUEUR.Envoie("CxEPPL#Pas de champs vides");
				valide = false;
			}
			else if(!CODE.matches("[a-zA-Z]+")){
				JOUEUR.Envoie("CxEPPL#Code : que des lettres");
				valide = false;
			}
			else if(CODE.length() > 10 || TITRE.length() > 35){
				
			}
			else{
				serveur.Bdd.Requete_CreatePlaylist_CodeUnique.setInt(1, JOUEUR.IdJoueur);
				serveur.Bdd.Requete_CreatePlaylist_CodeUnique.setString(2, CODE);
				ResultSet check = serveur.Bdd.Requete_CreatePlaylist_CodeUnique.executeQuery();
				check.next();
				if(check.getInt("count") > 0){
					JOUEUR.Envoie("CxEPPL#Le code est déjà utilisé");
					valide = false;
				}
				else{
					serveur.Bdd.Requete_CreatePlaylist_Nombre.setInt(1, JOUEUR.IdJoueur);
					check = serveur.Bdd.Requete_CreatePlaylist_Nombre.executeQuery();
					check.next();
					if(check.getInt("count") >= 3){
						JOUEUR.Envoie("CxEPPL#Pas plus que 3 playlists");
						valide = false;
					}
				}
			}
		}
		catch(SQLException e)
		{
			System.err.print("3XC PBV "); e.printStackTrace();
			valide = false;
		}
		if(valide && retourJoueur)
		{
			JOUEUR.Envoie("CxVAPL");
		}
		
		return valide;
	}

	private void Playlist_publier(Joueur JOUEUR, String CARTES, String CODE, String TITRE){
		try {
			if(!CARTES.matches("[0-9,]+"))
				JOUEUR.Envoie("CxEPPL#Mauvais format de la playlist");
			else
			if(Playlist_publier_valider(JOUEUR, CODE, TITRE, false))
			{
				serveur.Bdd.Requete_CreatePlaylist.setInt(1, JOUEUR.IdJoueur);
				serveur.Bdd.Requete_CreatePlaylist.setString(2, CARTES);
				serveur.Bdd.Requete_CreatePlaylist.setString(3, CODE);
				serveur.Bdd.Requete_CreatePlaylist.setString(4, TITRE);
				serveur.Bdd.Requete_CreatePlaylist.execute();
			}
		}
		catch (SQLException e) {
			System.err.print("3XC PB "); e.printStackTrace();
		}
	}

	private void Recherche_Map(Joueur JOUEUR,  String AUTEUR, String TITRE, String RECORDMAN, int MODE) {
		if (CheckParameter("[^a-zA-Z0-9 ]",TITRE)
				|| CheckParameter("[^a-zA-Z*_0-9]",RECORDMAN)
				|| CheckParameter("[^a-zA-Z0-9 ]",AUTEUR)
				|| TITRE.length() > 20
				|| RECORDMAN.length() > 20
				|| AUTEUR.length() > 20) {
			return;
		}
		try {
			StringBuilder Query = new StringBuilder("SELECT id, auteur, titre, votes, pour, perma, record, recordman, guide, played, survie FROM `$maps` ");
			StringBuilder Where = new StringBuilder();
			// clause where
			if (!TITRE.isEmpty()) {
				Where.append("AND titre like '%" + TITRE + "%' ");
			}
			if (!AUTEUR.isEmpty()) {
				Where.append("AND auteur = '" + AUTEUR + "' ");
			}
			if (!RECORDMAN.isEmpty()) {
				Where.append("AND recordman = '" + RECORDMAN + "' ");
			}
			if (MODE!=0 || Where.toString().isEmpty()) { // si aucun champ de recherche, on regarde les dernières maps non classées
				Where.append("AND mode = '" + MODE + "' ");
			}


			String WhereClause = Where.toString();
			if (!WhereClause.isEmpty()) {
				Query.append("WHERE ");
				Query.append(WhereClause.substring(3)); // on enlève le premier AND
			}
			Query.append(" ORDER by id desc LIMIT 1000");
			ResultSet Resultat = serveur.Bdd.Requete.executeQuery(Query.toString());
			JOUEUR.EnvoieCartes(Resultat);

		} catch (Exception e) {
			System.err.print("3XC RM "); e.printStackTrace();
		}
	}
	
	private void Playlist_recherche(Joueur JOUEUR, String motClé)
	{
		try{
			motClé = "%"+motClé+"%";
			serveur.Bdd.Requete_RecherchePlaylist.setString(1, motClé);
			serveur.Bdd.Requete_RecherchePlaylist.setString(2, motClé);
			serveur.Bdd.Requete_RecherchePlaylist.setString(3, motClé);
			ResultSet Resultat = serveur.Bdd.Requete_RecherchePlaylist.executeQuery();
			JOUEUR.EnvoiePlaylist(Resultat);
		} catch (SQLException e) {
			System.err.print("3XC RP "); e.printStackTrace();
		}
	}
	
	private void Playlist_supprimer(Joueur JOUEUR, String auteur, String code)
	{
		if(JOUEUR.NomJoueur.equalsIgnoreCase(auteur) || JOUEUR.AutorisationModo)
		{
			try {
				int id = ID_Joueur(auteur);
				serveur.Bdd.Requete_DeletePlaylist.setInt(1, id);
				serveur.Bdd.Requete_DeletePlaylist.setString(2, code);
				serveur.Bdd.Requete_DeletePlaylist.execute();
			} catch (SQLException e) {
				System.err.print("3XC PS "); 
				e.printStackTrace();
			}
			JOUEUR.Reception_Spéciale("CxPlist");
		}
	}

	private int ID_Joueur(String auteur) throws SQLException {
		if(auteur != null && auteur.length() > 0){
			try {
				serveur.Bdd.Requete_IdJoueur.setString(1, auteur);
				ResultSet resultat = serveur.Bdd.Requete_IdJoueur.executeQuery();
				if (resultat.next()) {
					int id = resultat.getInt("id");
					return id;
				}
				return -1;
			}catch (SQLException e) {
				System.err.print("3XC ID "); 
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	private int ID_Team(int cible) throws SQLException {
		if (cible == -1)
			return -1;
		
		serveur.Bdd.Requete_TeamJoueur.setInt(1, cible);
		ResultSet resultat = serveur.Bdd.Requete_TeamJoueur.executeQuery();
		if (resultat.next()) {
			return resultat.getInt("id_team");
		}
		
		return -1;
	}

	private void Create_Map (Joueur JOUEUR, String ID, String CODE , String TITRE, int MODE) {
		if (CheckParameter("[^a-zA-Z0-9 ]",TITRE)) {
			return;
		}
		Long IdMap = Long.parseLong(ID);
		try {
			if (MODE!=1) {
				serveur.Bdd.Requete_CreateMap.setLong(1, IdMap);
				serveur.Bdd.Requete_CreateMap.setString(2, CODE);
				serveur.Bdd.Requete_CreateMap.setString(3, TITRE);
				serveur.Bdd.Requete_CreateMap.setString(4, JOUEUR.NomJoueur);
				serveur.Bdd.Requete_CreateMap.setInt(5, MODE);
				serveur.Bdd.Requete_CreateMap.executeUpdate();
			}

		} catch (Exception e) {
			System.err.print("3XC CM "); e.printStackTrace();
		}
	}

	private void Update_Map (Joueur JOUEUR, String ID, String CODE , String TITRE, int MODE) {
		if (CheckParameter("[^a-zA-Z0-9 ]",TITRE)) {
			return;
		}
		Long IdMap = Long.parseLong(ID);
		try {
			if (MODE!=1) {
				serveur.Bdd.Requete_UpdateMap.setString(1, CODE);
				serveur.Bdd.Requete_UpdateMap.setString(2, TITRE);
				serveur.Bdd.Requete_UpdateMap.setInt(3, MODE);
				serveur.Bdd.Requete_UpdateMap.setLong(4, IdMap);
				serveur.Bdd.Requete_UpdateMap.executeUpdate();
			}


		} catch (Exception e) {
			System.err.print("3XC UM "); e.printStackTrace();
		}
	}

	private void Perma_Map (Joueur JOUEUR, String ID, int PERMA) {

		Long IdMap = Long.parseLong(ID);
		try {
			serveur.Bdd.Requete_PermaMap.setInt(1, PERMA);
			serveur.Bdd.Requete_PermaMap.setLong(2, IdMap);
			serveur.Bdd.Requete_PermaMap.executeUpdate();


		} catch (Exception e) {
			System.err.print("3XC PM "); e.printStackTrace();
		}
	}

	private void Creer_Rally (Joueur JOUEUR, Rally RALLY) {
		try {
			serveur.Bdd.Requete_CreerRally.setLong(1, RALLY.getID());
			serveur.Bdd.Requete_CreerRally.setString(2, RALLY.getAuteur());
			serveur.Bdd.Requete_CreerRally.setString(3, RALLY.getTitre());
			serveur.Bdd.Requete_CreerRally.setString(4, RALLY.getMap());
			serveur.Bdd.Requete_CreerRally.setInt(5, RALLY.Couleur);
			serveur.Bdd.Requete_CreerRally.setInt(6, RALLY.getDifficulte());
			serveur.Bdd.Requete_CreerRally.setInt(7, RALLY.getMode());
			serveur.Bdd.Requete_CreerRally.setInt(8, RALLY.getNote());
			serveur.Bdd.Requete_CreerRally.setInt(9, RALLY.getStatus());
			serveur.Bdd.Requete_CreerRally.setString(10, RALLY.couleurCarresToString());
			serveur.Bdd.Requete_CreerRally.setString(11, RALLY.getInfosMondePerso());
			serveur.Bdd.Requete_CreerRally.executeUpdate();
			JOUEUR.Envoie("CxINFO#Carte sauvegardée avec l'id "+JOUEUR.CodeMapRally+". Elle sera visible dans /map par les autres joueurs si un responsable des maps la valide.");

		} catch (Exception e) {
			System.err.print("3XC Creer_Rally "); e.printStackTrace();
		}
	}

	private void Update_Rally (Joueur JOUEUR, Rally RALLY) {
		try {
			serveur.Bdd.Requete_UpdateRally.setString(1, RALLY.getTitre());
			serveur.Bdd.Requete_UpdateRally.setString(2, RALLY.getMap());
			serveur.Bdd.Requete_UpdateRally.setInt(3, RALLY.Couleur);
			serveur.Bdd.Requete_UpdateRally.setInt(4, RALLY.getDifficulte());
			serveur.Bdd.Requete_UpdateRally.setInt(5, RALLY.getMode());
			serveur.Bdd.Requete_UpdateRally.setInt(6, RALLY.getNote());
			serveur.Bdd.Requete_UpdateRally.setInt(7, RALLY.getStatus());
			serveur.Bdd.Requete_UpdateRally.setInt(8, RALLY.getRecord());
			serveur.Bdd.Requete_UpdateRally.setString(9, RALLY.getRecordman());
			serveur.Bdd.Requete_UpdateRally.setString(10, RALLY.couleurCarresToString());
			serveur.Bdd.Requete_UpdateRally.setString(11, RALLY.getInfosMondePerso());
			serveur.Bdd.Requete_UpdateRally.setLong(12, RALLY.getID());
			serveur.Bdd.Requete_UpdateRally.executeUpdate();
			JOUEUR.Envoie("CxINFO#Carte " + RALLY.getID() + " mise à jour.");
			JOUEUR.LogCommande("UpdateRally " + RALLY.getID() + " " + RALLY.getStatus());

		} catch (Exception e) {
			System.err.print("3XC Update_Rally "); e.printStackTrace();
		}
	}
	
	private void Delete_Rallys (Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_DeleteRally.executeUpdate();
			if (JOUEUR!=null) {
				JOUEUR.Envoie("CxINFO#Nettoyage effectué.");
			}

		} catch (Exception e) {
			System.err.print("3XC Delete_Rallys "); e.printStackTrace();
		}
	}
	
	private void Masse_Refus_Rally (Joueur JOUEUR, String PSEUDO) {
		try {
			serveur.Bdd.Requete_MasseRefus.setString(1, PSEUDO);
			serveur.Bdd.Requete_MasseRefus.executeUpdate();
			JOUEUR.Envoie("CxINFO#Les maps de " + PSEUDO + " en attente de validation ont été refusées.");
		} catch (Exception e) {
			System.err.print("3XC Masse_Refus_Rally "); e.printStackTrace();
			JOUEUR.Envoie("CxINFO#Une erreur est survenue pour /masserefus.");
		}
	}
	
	private void Charger_Rally (Joueur JOUEUR, String ID) {
		try {
			Long IdLong = Long.parseLong(ID);
			
			serveur.Bdd.Requete_ChargerRally.setLong(1, IdLong);
			ResultSet Retour = serveur.Bdd.Requete_ChargerRally.executeQuery();
			
			System.out.println("Load map : " + ID);
			
			if (Retour.next() && (Retour.getInt("status") != 3 || (JOUEUR.AutorisationModoCartesRally || JOUEUR.AutorisationModo))) {
				if (JOUEUR.PartieEnCours != null && JOUEUR.PartieEnCours.Forto) {
					Charger_Rally_To_Partie_Forteresse((Forteresse)JOUEUR.PartieEnCours.getJeu(), serveur.Bdd.Requete_ChargerRally.executeQuery(), JOUEUR, IdLong);
				}
			} else {
				JOUEUR.Envoie_Info("Code inconnu.");
			}

		} catch (Exception e) {
			System.err.print("3XC Charger_Rally "); e.printStackTrace();
		}
	}
	
	/**
	 * Charge une map de /map dans une partie Forteresse.
	 * 
	 * @param PartieForteresse : Partie dans laquelle load la map
	 * @param ResReqChargerRally : ResultSet d'une requête "Requete_ChargerRally"
	 * @param JOUEUR : Joueur qui a fait la demande de load (null si le jeu demande lui-même)
	 * @param IdLong : Id de la map à load
	 */
	private void Charger_Rally_To_Partie_Forteresse(Forteresse PartieForteresse, ResultSet ResReqChargerRally, Joueur JOUEUR, long IdLong) {
		try {
			if (ResReqChargerRally.next()) {
				String CodeMap = ResReqChargerRally.getString("code");
				int Couleur = ResReqChargerRally.getInt("couleur");
				String[] Map = CodeMap.split("-");
				String CouleursPersos = ResReqChargerRally.getString("couleur_perso");
				String InfosMondePerso = ResReqChargerRally.getString("infos_monde");
				boolean RallyChargé;
				
				String Auteur = ResReqChargerRally.getString("auteur");
				String Titre = ResReqChargerRally.getString("titre");
				int Difficulte = ResReqChargerRally.getInt("niveau");
				int Mode = ResReqChargerRally.getInt("mode");
				int Note = ResReqChargerRally.getInt("note");
				int Status = ResReqChargerRally.getInt("status");
				int Record = ResReqChargerRally.getInt("record");
				String Recordman = ResReqChargerRally.getString("recordman");
				
				String[] lsCouleurs;
				Rally map;
				
				if (CouleursPersos == null || CouleursPersos.isEmpty()) {
					lsCouleurs = new String[]{Forteresse.COULEUR_CARRE_DESTRUCTIBLE, Forteresse.COULEUR_CARRE_INDESTRUCTIBLE, Forteresse.COULEUR_CARRE_OBJECTIF, Forteresse.COULEUR_CARRE_NONRECONSTRUCTIBLE, Forteresse.COULEUR_CARRE_CONSTRUCTIBLE, Forteresse.COULEUR_CARRE_INCONSTRUCTIBLE};
				} else {
					lsCouleurs = CouleursPersos.split("-");
				}
				
				if (InfosMondePerso == null || InfosMondePerso.isEmpty()) {
					MondeForteresse monde = Serveur.getServeur().Forteresse_ListeMonde.get(Integer.parseInt(Map[0]));
					map = new Rally(IdLong, monde, Auteur, Titre, Couleur, Difficulte, Mode, Note, Map[1], Status, Integer.parseInt(lsCouleurs[0], 16), Integer.parseInt(lsCouleurs[1], 16), Integer.parseInt(lsCouleurs[2], 16), Integer.parseInt(lsCouleurs[3], 16), Integer.parseInt(lsCouleurs[4], 16), Integer.parseInt(lsCouleurs[5], 16), Record, Recordman);
				} else {
					String[] lsInfosMondePerso = InfosMondePerso.split("-");
					MondeForteresse monde = MondeForteresse.CreerMondePerso(Map[1], lsInfosMondePerso);
					map = new Rally(IdLong, monde, Auteur, Titre, Couleur, Difficulte, Mode, Note, Map[1], Status, Integer.parseInt(lsCouleurs[0], 16), Integer.parseInt(lsCouleurs[1], 16), Integer.parseInt(lsCouleurs[2], 16), Integer.parseInt(lsCouleurs[3], 16), Integer.parseInt(lsCouleurs[4], 16), Integer.parseInt(lsCouleurs[5], 16), Record, Recordman);
				}
				
				RallyChargé = PartieForteresse.LoadMapForto(JOUEUR, map);

				if (RallyChargé) {
					//PartieForteresse.IdRally = IdLong;
					PartieForteresse.RallyValide = true;
					PartieForteresse.Record = ResReqChargerRally.getInt("record");
					PartieForteresse.Recordman = ResReqChargerRally.getString("recordman");
					
					if (ResReqChargerRally.getInt("record") != 0) {
						serveur.Envoie(PartieForteresse.ListeJoueur, "CxINFO#Record du rally : " + PartieForteresse.Record + " sec par " + PartieForteresse.Recordman + ".");
					}
				}
			} else {
				if (JOUEUR != null) {
					JOUEUR.Envoie_Info("Code inconnu.");
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Charger_Rally "); e.printStackTrace();
		}
	}

	private void Charger_Rally_Respomap (Joueur JOUEUR, String ID) {
		try {
			Long IdLong = Long.parseLong(ID);
			serveur.Bdd.Requete_ChargerRally.setLong(1, IdLong);
			ResultSet Resultat = serveur.Bdd.Requete_ChargerRally.executeQuery();
			if (Resultat.next()) {
				long Id = Resultat.getLong("id");
				String Auteur = Resultat.getString("auteur");
				String Titre = Resultat.getString("titre");
				int Difficulte = Resultat.getInt("niveau");
				int Mode = Resultat.getInt("mode");
				int Note = Resultat.getInt("note");
				int Status = Resultat.getInt("status");
				int Record = Resultat.getInt("record");
				String Recordman = Resultat.getString("recordman");	
				int Couleur = Resultat.getInt("couleur");
				//String CouleursPersos = Resultat.getString("couleur_perso");
				JOUEUR.Envoie("FxBUPD#"+Id+"#"+Auteur+"#"+Titre+"#"+Difficulte+"#"+Mode+"#"+Note+"#"+Status+"#"+Record+"#"+Recordman+"#"+Couleur);
			} else {
				JOUEUR.Envoie_Info("Code inconnu.");
			}

		} catch (Exception e) {
			System.err.print("3XC Charger_Rally_Respomap "); e.printStackTrace();
		}
	}	
	
	private void Liste_Rally (Joueur JOUEUR) {
		try {
			// id,auteur,titre,niveau,mode,note,status,record,recordman FROM $maps_forto
			ResultSet Resultat = serveur.Bdd.Requete_ListeRally.executeQuery();
			StringBuilder Chaine = new StringBuilder("CxMAR#");
			//Chaine.append("Id, Soumis par, Titre, Difficulté, Mode, Note, Status, Record, Recordman\n");
			while (Resultat.next()) {
				long Id = Resultat.getLong("id");
				String Auteur = Resultat.getString("auteur");
				String Titre = Resultat.getString("titre");
				int Difficulte = Resultat.getInt("niveau");
				int Mode = Resultat.getInt("mode");
				int Note = Resultat.getInt("note");
				int Status = Resultat.getInt("status");
				int Record = Resultat.getInt("record");
				String Recordman = Resultat.getString("recordman");
				
				if (Status == 1 || (Status != 3 && (Auteur.equalsIgnoreCase(JOUEUR.NomJoueur) || (JOUEUR.AutorisationModoCartesRally || JOUEUR.AutorisationModo)))) {
					Chaine.append($$);
					Chaine.append(Id);
					Chaine.append($);
					Chaine.append(Auteur);
					Chaine.append($);
					Chaine.append(Titre);
					Chaine.append($);
					Chaine.append(Difficulte);
					Chaine.append($);
					Chaine.append(Mode);
					Chaine.append($);
					Chaine.append(Note);
					Chaine.append($);
					Chaine.append(Status);
					Chaine.append($);
					Chaine.append(Record);
					Chaine.append($);
					Chaine.append(Recordman);
				}
			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Liste_Rally "); e.printStackTrace();
		}
	}	
	
	private void Retrouver_Joueur (Joueur JOUEUR, String CIBLE) {
		try {
			serveur.Bdd.Requete_RetrouverJoueur.setString(1, CIBLE);
			ResultSet Retour = serveur.Bdd.Requete_RetrouverJoueur.executeQuery();
			if (Retour.next()) {
				String IP = Retour.getString("ip");
				if (IP!=null) {
					JOUEUR.ChercherJoueur(CIBLE, IP, false);
				} else {
					JOUEUR.Envoie("CxINFO#IP inconnue");
				}
			} else {
				JOUEUR.Envoie("CxINFO#Joueur inconnu.");
			}
		} catch (Exception e) {
			System.err.print("3XC Retrouver "); e.printStackTrace();
		}
	}

	private void Flag_Map (Joueur JOUEUR, String ID, int FLAG) {

		Long IdMap = Long.parseLong(ID);
		try {
			serveur.Bdd.Requete_FlagMap.setInt(1, FLAG);
			serveur.Bdd.Requete_FlagMap.setLong(2, IdMap);
			serveur.Bdd.Requete_FlagMap.executeUpdate();


		} catch (Exception e) {
			System.err.print("3XC FM "); e.printStackTrace();
		}
	}


	private void Suppr_Votes_Map (Joueur JOUEUR, String ID) {

		Long IdMap = Long.parseLong(ID);
		try {
			serveur.Bdd.Requete_SupprVotesMap.setLong(1, IdMap);
			serveur.Bdd.Requete_SupprVotesMap.executeUpdate();


		} catch (Exception e) {
			System.err.print("3XC Suppr_Votes_Map "); e.printStackTrace();
		}
	}
	
	private void Suppr_Record_Map (Joueur JOUEUR, String ID) {

		Long IdMap = Long.parseLong(ID);
		try {
			serveur.Bdd.Requete_SupprRecordMap.setLong(1, IdMap);
			serveur.Bdd.Requete_SupprRecordMap.executeUpdate();


		} catch (Exception e) {
			System.err.print("3XC Requete_SupprRecordMap "); e.printStackTrace();
		}
	}
	

	private void Update_Stats_Map (Joueur JOUEUR, String[] INFOS) {
		/*System.out.println("Update : " + INFOS[0] + " " + INFOS[1] + " " + INFOS[2] + " " + INFOS[3] + " " + INFOS[4] + " " +
                    INFOS[5] + " " + INFOS[6] + " " );*/
		try {
			// { Code, Votes, Pour, Record, Recordman, Played, Survie }
			serveur.Bdd.Requete_UpdateStatsMap.setInt(1, Integer.valueOf(INFOS[1]));
			serveur.Bdd.Requete_UpdateStatsMap.setInt(2, Integer.valueOf(INFOS[2]));
			serveur.Bdd.Requete_UpdateStatsMap.setInt(3, Integer.valueOf(INFOS[3]));
			serveur.Bdd.Requete_UpdateStatsMap.setString(4, INFOS[4]);
			serveur.Bdd.Requete_UpdateStatsMap.setInt(5, Integer.valueOf(INFOS[5]));
			serveur.Bdd.Requete_UpdateStatsMap.setInt(6, Integer.valueOf(INFOS[6]));
			serveur.Bdd.Requete_UpdateStatsMap.setInt(7, Integer.valueOf(INFOS[7]));
			serveur.Bdd.Requete_UpdateStatsMap.setString(8, INFOS[8]);
			serveur.Bdd.Requete_UpdateStatsMap.setInt(9, Integer.valueOf(INFOS[9]));
			serveur.Bdd.Requete_UpdateStatsMap.setLong(10, Long.valueOf(INFOS[0]));
			serveur.Bdd.Requete_UpdateStatsMap.executeUpdate();


		} catch (Exception e) {
			System.err.print("3XC USM "); e.printStackTrace();
		}
	}

	private void Update_Ghost_Map (Joueur JOUEUR, String GHOST, String ID) {
		try {

			serveur.Bdd.Requete_UpdateGhostMap.setString(1, GHOST);
			serveur.Bdd.Requete_UpdateGhostMap.setString(2, ID);
			serveur.Bdd.Requete_UpdateGhostMap.executeUpdate();


		} catch (Exception e) {
			System.err.print("3XC UGM "); e.printStackTrace();
		}
	}
	
	private void Delete_Map (Joueur JOUEUR, String ID, String TYPE) {

		Long IdMap = Long.parseLong(ID);
		try {
			Carte Carte = serveur.Aaaah_ListeCarte.get(IdMap);

			if (Carte != null) {
				serveur.Bdd.Requete_CreateMapHis.setLong(1, IdMap);
				serveur.Bdd.Requete_CreateMapHis.setString(2, Carte.Code);
				serveur.Bdd.Requete_CreateMapHis.setString(3, Carte.Titre);
				serveur.Bdd.Requete_CreateMapHis.setString(4, Carte.Auteur);
				serveur.Bdd.Requete_CreateMapHis.setString(5, JOUEUR!=null && TYPE.equals("R")?JOUEUR.NomJoueur:null);
				serveur.Bdd.Requete_CreateMapHis.setString(6, TYPE);
				serveur.Bdd.Requete_CreateMapHis.executeUpdate();

				serveur.Bdd.Requete_DeleteMap.setLong(1, IdMap);
				serveur.Bdd.Requete_DeleteMap.executeUpdate();

				serveur.Aaaah_ListeCarte.remove(IdMap);
			}

		} catch (Exception e) {
			System.err.print("3XC DM "); e.printStackTrace();
		}
	}

	private void Delete_History (Joueur JOUEUR, String ID) {

		Long IdSanction = Long.parseLong(ID);
		try {

			serveur.Bdd.Requete_RetrieveSanctionById.setLong(1,IdSanction);
			ResultSet Result = serveur.Bdd.Requete_RetrieveSanctionById.executeQuery();
			if (Result.next()) {
				String Sanction = Result.getString("sanction");
				String Cible = Result.getString("nom_joueur");
				if (Sanction.startsWith("mute_forum")) {
					if (Sanction.equals("mute_forum"))
					{
						String IP = Result.getString("ip");
						if (IP != null && IP.length() > 2) {
							serveur.Demute_Forum(IP);
						}
					} else if (Sanction.equals("mute_forum_nom")) {
						if (Cible != null) {
							serveur.Demute_Forum_Nom(Cible);
						}
					}


					serveur.Bdd.Requete_DeleteSanctions.setLong(1, IdSanction);
					serveur.Bdd.Requete_DeleteSanctions.executeUpdate();

					JOUEUR.Envoie("CxINFO#Sanction supprimée.");
					JOUEUR.Commande("note " +Cible + " Sanction enlevée");

				}


			}

		} catch (Exception e) {
			System.err.print("3XC DM "); e.printStackTrace();
		}
	}

	private void Restore_Map (Joueur JOUEUR, String ID) {

		Long IdMap = Long.parseLong(ID);
		try {

			serveur.Bdd.Requete_RetrieveMapHis.setLong(1,IdMap);
			ResultSet Result = serveur.Bdd.Requete_RetrieveMapHis.executeQuery();
			if (Result.next()) {
				String Code = Result.getString("code");
				String Titre = Result.getString("titre");
				String Auteur = Result.getString("auteur");

				serveur.Bdd.Requete_CreateMap.setLong(1, IdMap);
				serveur.Bdd.Requete_CreateMap.setString(2, Code);
				serveur.Bdd.Requete_CreateMap.setString(3, Titre);
				serveur.Bdd.Requete_CreateMap.setString(4, Auteur);
				serveur.Bdd.Requete_CreateMap.setInt(5, 0);
				serveur.Bdd.Requete_CreateMap.executeUpdate();

				serveur.Bdd.Requete_DeleteMapHis.setLong(1, IdMap);
				serveur.Bdd.Requete_DeleteMapHis.executeUpdate();

				Carte InfoCarte = new Carte(IdMap,
						Code, // Code
						0, // Votes
						0, // Pour
						0, // Perma
						120000, // Record
						"-", // Recordman
						0,// played
						0, // survie
						Auteur,
						Titre,
						0,
						120000,
						"-",
						0,
						"");

				serveur.Aaaah_ListeCarte.put(IdMap, InfoCarte);

				JOUEUR.Envoie("CxINFO#Carte " + IdMap + " restaurée.");
			}

		} catch (Exception e) {
			System.err.print("3XC DM "); e.printStackTrace();
		}
	}

	private void Histo_Maps_Joueur(Joueur JOUEUR, String NOM) {
		try {
			serveur.Bdd.Requete_RetrieveMapHisAuteur.setString(1, NOM);
			ResultSet Resultat = serveur.Bdd.Requete_RetrieveMapHisAuteur.executeQuery();
			StringBuilder Chaine = new StringBuilder("CxINFO#Historique des maps de " + NOM + " : (V=votes,R=respomap,S=suppr)\n");
			while (Resultat.next()) {
				String Id = Resultat.getString("id");
				String Titre = Resultat.getString("titre");
				String Type = Resultat.getString("type");
				Date Date = Resultat.getDate("date");

				Chaine.append("[" + Date + "] [" + Type + "] " + Id + " " +  Titre + "\n");


			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC Histo_Maps_Joueur "); e.printStackTrace();
		}
	}

	private void Load_Stats(Joueur JOUEUR, Stat STAT) {
		try {
			// SELECT * FROM $stats_xxx WHERE nom=? and type!='archive'
			STAT.internalLoad(serveur, JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Load_Stats "); e.printStackTrace();
		}
	}

	private void Sauvegarde_Stat(Joueur JOUEUR, Stat STAT) {
		try {
			// UPDATE $stats_xxx SET tps_jeu=?,parties=?,arr_prem=?,arr_vie=?,kill=? WHERE nom=? and type=?
			STAT.internalSave(serveur, JOUEUR);
		} catch (Exception e) {
			System.err.print("3XC Sauvegarde_Stat "); e.printStackTrace();
		}
	}

	private void New_Stat(Joueur JOUEUR, Stat STAT) {
		try {
			// INSERT INTO $stat_xxx(nom,type) VALUES (?,?)
			PreparedStatement PrepSt = STAT.internalNew(serveur);
			if (PrepSt!=null) {
				PrepSt.setString(1, JOUEUR.NomJoueur);
				PrepSt.setString(2, Stat.TYPE_MOIS);
				PrepSt.executeUpdate();			
			}
		} catch (Exception e) {
			System.err.print("3XC New_Stat "); e.printStackTrace();
		}
	}

	private void Archiver_Stats(Joueur JOUEUR) {
		try {
			serveur.Bdd.Requete_ArchiveStatsDef.executeUpdate();			
			serveur.Bdd.Requete_ArchiveStatsBoum.executeUpdate();			
			serveur.Bdd.Requete_ArchiveStatsForto.executeUpdate();			
			serveur.Bdd.Requete_ArchiveStatsFs.executeUpdate();			
			serveur.Bdd.Requete_ArchiveStatsGdm.executeUpdate();			
			serveur.Bdd.Requete_ArchiveStatsMs.executeUpdate();			
			serveur.Bdd.Requete_ArchiveStatsRun.executeUpdate();			
			serveur.Bdd.Requete_ArchiveStatsNg.executeUpdate();
			serveur.Bdd.Requete_ArchiveStatsRally.executeUpdate();
			int Nb = serveur.ListeJoueur.size();
			for (int i = 0; i < Nb; i++) {
				Joueur Joueur = serveur.ListeJoueur.get(i);
				Joueur.Stats.Reset();
			}			
			if (JOUEUR!=null) {
				JOUEUR.Envoie("CxINFO#Archivage terminé");
			}
			System.out.println("Archivage stats");
		} catch (Exception e) {
			System.err.print("3XC Archiver_Stats "); e.printStackTrace();
		}
	}
	
	private void Obtenir_Stats_Elo_Profil(Joueur J, String Cible, String Jeu, boolean StatsSaisonPrecedente) {
		Joueur JCible = serveur.Joueur(Cible);
		EloJoueur eloJ;
		
		if (Cible.startsWith("*")) {
			return;
		}
		
		if (JCible != null && !StatsSaisonPrecedente) {
			eloJ = JCible.elo;
		} else {
			try {
				serveur.Bdd.Requete_GetId.setString(1, Cible);
				ResultSet res = serveur.Bdd.Requete_GetId.executeQuery();
				
				if (res.next()) {
					eloJ = new EloJoueur(serveur, res.getInt(1), Cible, false, "", StatsSaisonPrecedente);
				} else { // Pas de joueur existant
					J.Envoie("CxINFO#Ce joueur n'existe pas.");
					return;
				}
			} catch (Exception e) {
				System.err.print("3XC Obtenir_Stats_Elo_Profil "); e.printStackTrace();
				return;
			}
		}
		
		J.Envoie(eloJ.Boite_EloToStats(Jeu, Cible));
	}
	
	private void Nouvelle_Fusion(Joueur JOUEUR, int[] LISTE) {
		try {
			serveur.Bdd.Requete_NouvelleFusion.setLong(1, LISTE[0]);
			serveur.Bdd.Requete_NouvelleFusion.setString(2, "," + LISTE[1] + "," + LISTE[2]);
			serveur.Bdd.Requete_NouvelleFusion.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Nouvelle_Fusion "); e.printStackTrace();
		}
	}
	
	private void Update_Fusion(Joueur JOUEUR, int FUSION, int ID_TEAM) {
		try {
			serveur.Bdd.Requete_UpdateFusion.setString(1, "," + ID_TEAM);
			serveur.Bdd.Requete_UpdateFusion.setLong(2, FUSION);
			serveur.Bdd.Requete_UpdateFusion.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Update_Fusion "); e.printStackTrace();
		}
	}	

	private void Quitter_Fusion(Joueur JOUEUR, int FUSION, int ID_TEAM) {
		try {
			serveur.Bdd.Requete_QuitterFusion.setString(1, "," + ID_TEAM);
			serveur.Bdd.Requete_QuitterFusion.setLong(2, FUSION);
			serveur.Bdd.Requete_QuitterFusion.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Requete_QuitterFusion "); e.printStackTrace();
		}
	}	

	private void Supprimer_Fusion(Joueur JOUEUR, int FUSION) {
		try {
			serveur.Bdd.Requete_SupprimerFusion.setLong(1, FUSION);
			serveur.Bdd.Requete_SupprimerFusion.executeUpdate();
		} catch (Exception e) {
			System.err.print("3XC Supprimer_Fusion "); e.printStackTrace();
		}
	}	

	private void Load_Fusions(Joueur JOUEUR) {
		try {
			ResultSet Resultat = serveur.Bdd.Requete_LoadFusions.executeQuery();
			while (Resultat.next()) {
				Long Id = Resultat.getLong("id");
				String Liste = Resultat.getString("liste");

				serveur.FusionCompteur = Id.intValue()+1;
				FusionTeam Fusion = new FusionTeam(Id.intValue());
				String[] ListeTeam = Liste.split(",");
				for (String Team : ListeTeam) {
					if (!Team.isEmpty()) {
						Integer IdTeam = Integer.parseInt(Team);
						Fusion.add(IdTeam);
						serveur.ListeTeamAmie.put(IdTeam, Fusion);
					}
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Load_Fusions "); e.printStackTrace();
		}
	}
	
	public void CodeFinMapReverse(Joueur Joueur, String code, int ExporterRecord){
		long JOUEUR;
		long Temps;
		long ID_Rally;
		 
		byte random_byte[] = {0, 0};
		 	
		String JOUEUR_ID = new String();
	 	String DUREE = new String();
	 	String ID_MAP = new String();

	 	byte TAILLE_ID_J;
	 	byte TAILLE_DUREE;
	 	byte TAILLE_ID_MAP;
		 	
	 	String byte_h2_1 = new String();
	 	String byte_h2_2 = new String();
	 	
	 	int trois = Integer.parseInt("11", 2); // 0b11
	 	int quinze = Integer.parseInt("1111", 2); // 0b1111
	 	
	 	String msg;

	 	if(CheckParameter("[^A-Z0-9]", code)) {
	 		Joueur.Envoie("CxINFO#Code incorrect.");
	 		return ;
	 	}
	 	
	 try {
		 	
	 // Étape 1 : interprétation de h1 -> récupérer les deux premiers chiffres sous forme hexa
		byte t_byte_h2_1;
		byte t_byte_h2_2;

		if(code.charAt(1) <= '9'){
		  t_byte_h2_1 = (byte)(code.charAt(1) - 48);
		} else{
		  t_byte_h2_1 = (byte)(code.charAt(1) - 65);
		}

		if (code.charAt(0) <= '9') {
		  t_byte_h2_2 = (byte)(code.charAt(0) - 48);
		} else {
		  t_byte_h2_2 = (byte)(code.charAt(0) - 65);
		}
		
	 // Étape 2 : récupérer les deux bytes de h2
		
	  // 2.1 Récupérer les valeurs de byte_h2_1 et byte_h2_2
		for (int i = 0; i != t_byte_h2_1; ++i) {
		  byte_h2_1 += "" + code.charAt(2 + i);
		}

		for (int i = 0; i != t_byte_h2_2; ++i) {
		  byte_h2_2 += "" + code.charAt(2 + t_byte_h2_1 + i);
		}

	  // 2.2 Restaurer les valeurs de TAILLE_ID_J, TAILLE_DUREE et TAILLE_ID_MAP
		int buf;
		
		buf = Integer.valueOf(byte_h2_1);
		 random_byte[0] = (byte)(buf & trois);
		 TAILLE_ID_J = (byte)((buf & (quinze << 2)) >> 2);
		 TAILLE_DUREE = (byte)((buf & (trois << 6)) >> 6);

		buf = Integer.valueOf(byte_h2_2);
		 TAILLE_DUREE |= (byte)((buf & trois) << 2); // 3ème & 4ème bits de TAILLE_DUREE
		 TAILLE_ID_MAP = (byte)((buf & (quinze << 2)) >> 2);
		 random_byte[1] = (byte)((buf & (trois << 6)) >> 6);

	  // Étape 3 : récupérer les valeurs de JOUEUR_ID, DUREE, ID_MAP
		 int j = 0;
		 int count_ji = 0, count_du = 0, count_ma = 0;
		 int add;
		 int start = 2 + t_byte_h2_1 + t_byte_h2_2;
		 int i = 0;
// count_ji + count_du + count_ma <= TAILLE_ID_J + TAILLE_DUREE + TAILLE_ID_MAP
		 for (i = start; count_ji < TAILLE_ID_J || count_du < TAILLE_DUREE || count_ma < TAILLE_ID_MAP;) {
			 if (count_ji < TAILLE_ID_J) {
		 		add = 0;
		 		
		 		if(code.charAt(i) > '9') {// Lettre
		 			add += 100;
		 			++i;
		 		} else {
		 			add += (code.charAt(i++) - 48) * 10;
		 		}
		 		add += (code.charAt(i++) - 48) - random_byte[j];
		 		if(!(count_ji + 1 < TAILLE_ID_J)) {
		 			add /= 10;
		 		}
		 		
		 		if ((count_ji + 1 < TAILLE_ID_J) && add < 10) {
		 			JOUEUR_ID += "0" + String.valueOf(add);
		 		} else{
		 		    JOUEUR_ID += String.valueOf(add);
		 		}
	 			
	  		    j = (~j) & 1;
	  		    count_ji += 2;
		 	}
		 	if (count_du < TAILLE_DUREE) {
		 		add = 0;
		 		add += (code.charAt(i++) - 48);
		 		++i;
		 		
		 		DUREE += String.valueOf(add);
	 			
	  		    j = (~j) & 1;
	 		    count_du++;
		 	}
		 	if (count_ma < TAILLE_ID_MAP) {
		 		add = 0;
		 		
		 		if (code.charAt(i) > '9') {// Lettre
		 			add += 100;
		 			++i;
		 		} else {
		 			add += (code.charAt(i++) - 48) * 10;
		 		}
		 		add += (code.charAt(i++) - 48) - random_byte[j];
		 		if (!(count_ma + 1 < TAILLE_ID_MAP))
		 			add /= 10;

		 		if ((count_ma + 1 < TAILLE_ID_MAP) && add < 10) {
		 			ID_MAP += "0" + String.valueOf(add);
		 		} else
	 	 		    ID_MAP += String.valueOf(add);
	 			
	  		    j = (~j) & 1;
	 		    count_ma += 2;
		 	}
		 }
	  // Étape 4 : CheckSum
		 JOUEUR = Long.valueOf(JOUEUR_ID);
		 Temps = Long.valueOf(DUREE);
		 ID_Rally = Long.valueOf(ID_MAP);

		 if (Long.valueOf(code.substring(i)) == (((((random_byte[0] + random_byte[1] + 1) * JOUEUR) % (Temps + 1))
	             + ID_Rally / (TAILLE_ID_J + TAILLE_DUREE)
	             + ID_Rally % ((TAILLE_ID_MAP - random_byte[1]) == 0 ? 1 : (TAILLE_ID_MAP - random_byte[1])))
	             * 5) / 17) {

			   serveur.Bdd.Requete_GetPseudobyId.setLong(1, JOUEUR);
			   ResultSet r1 = serveur.Bdd.Requete_GetPseudobyId.executeQuery();
			   r1.next();
			   String PseudoJoueur = r1.getString("n");

			   if (ExporterRecord == 1) {
				   serveur.Bdd.Requete_GetRecordForto.setLong(1, ID_Rally);
				   ResultSet resultat = serveur.Bdd.Requete_GetRecordForto.executeQuery();
				   
				   if (resultat.next() && (resultat.getInt(1) > Temps || resultat.getInt(1) == 0) && resultat.getByte(3) == 0) {
					// UPDATE $maps_forto SET record=?, recordman=? WHERE id=?
					   serveur.Bdd.Requete_UpdateRecordForto.setInt(1, (int)Temps);
					   serveur.Bdd.Requete_UpdateRecordForto.setString(2, PseudoJoueur);
					   serveur.Bdd.Requete_UpdateRecordForto.setLong(3, ID_Rally);
					   serveur.Bdd.Requete_UpdateRecordForto.executeUpdate();
					   
					   Joueur.Envoie("CxINFO#Record modifié.");
					   System.out.println("EXP " + Joueur.NomJoueur + " " + code + " " + PseudoJoueur + " (" + Temps + "s)");
				   } else {
					   Joueur.Envoie("CxINFO#Record inchangé.");
				   }
			   } else {
				   msg = "" + (char)(PseudoJoueur.charAt(0) - 32) + PseudoJoueur.substring(1) + " a fini le rally ";
				   
				   if(ID_Rally != 0) { // Pas le /rally (donc rally de /map)
					   serveur.Bdd.Requete_GetTitreMapFbyId.setLong(1, ID_Rally);
					   ResultSet r2 = serveur.Bdd.Requete_GetTitreMapFbyId.executeQuery();
					   r2.next();
					   msg += r2.getString("titre") + " (" + ID_Rally + ") en " + Temps + " secondes."; 
				   } else {
					   msg += "Ultra rally" + " (" + "Rally officiel" + ") en " + Temps + " secondes."; 
				   }
				   Joueur.Envoie("CxINFO#" + msg);
			   }
			   
			   return;
		 }
		 else {
			 Joueur.Envoie("CxINFO#Code incorrect.");
			 return;
		 }
		 
	 } catch(Exception e){
		 Joueur.Envoie("CxINFO#Code incorrect.");
		 return;
	 }
	}
	
	private void Reload_Map_Aaaah(Joueur J, String IdMapStr) {
		try {
			long IdMap = Long.valueOf(IdMapStr);
			serveur.Bdd.Requete_RechercheMapById.setLong(1, IdMap);
			ResultSet Resultat = serveur.Bdd.Requete_RechercheMapById.executeQuery();
			if (Resultat.next()) {

				Long Id = Resultat.getLong("id");
				String Auteur = Resultat.getString("auteur");
				String Titre = Resultat.getString("titre");
				String Code = Resultat.getString("code");
				Integer Votes = Resultat.getInt("votes");
				Integer Pour = Resultat.getInt("pour");
				Integer Perma = Resultat.getInt("perma");
				Integer Record = Resultat.getInt("record");
				String Recordman = Resultat.getString("recordman");
				Integer Played = Resultat.getInt("played");
				Integer Survie = Resultat.getInt("survie");
				Integer Flag = Resultat.getInt("mode");
				Integer Mara_Record = Resultat.getInt("mara_record");
				String Mara_Recordman = Resultat.getString("mara_recordman");
				Integer Mara_Played = Resultat.getInt("mara_played");
				String Ghost = Resultat.getString("ghost");
				
				serveur.Aaaah_ListeCarte.remove(IdMap);
				Carte InfoCarte = new Carte(Id, Code, Votes, Pour, Perma, Record, Recordman, Played, Survie, Auteur, Titre, Flag, Mara_Record, Mara_Recordman, Mara_Played, Ghost);
				serveur.Aaaah_ListeCarte.put(Id, InfoCarte);
				
				J.Envoie("CxINFO#Map " + Id + " rechargée.");
			}
		} catch (Exception e) {
			J.Envoie("CxINFO#Chargement de la map " + IdMapStr + " a échoué.");
		}
	}
	
	/**
	 * 
	 * @param obj[0] : (Forteresse) Partie en cours
	 * @param obj[1] : (Long) Id map à load de la playlist
	 */
	private void Load_Map_Playlist_Forto(Object[] obj) {
		try {
			Long IdLong = (Long)obj[1];
			
			serveur.Bdd.Requete_ChargerRally.setLong(1, IdLong);
			ResultSet Retour = serveur.Bdd.Requete_ChargerRally.executeQuery();
			
			Charger_Rally_To_Partie_Forteresse((Forteresse)obj[0], Retour, null, IdLong);
		} catch (Exception e) {
			System.err.print("3XC Charger_Rally "); e.printStackTrace();
		}
		
	}
	
	private void Attribuer_Recompenses_Elo(Joueur J) {
		RecompenseElo.Attribuer(serveur);
		J.Envoie("CxINFO#Récompenses attribuées.");
	}
	
	private void SetBlason(Joueur JOUEUR, String BLASON) {
		// UPDATE $teams SET blason=? WHERE id=?
		if (!JOUEUR.Membre || !JOUEUR.AutorisationScribe) {
			JOUEUR.Envoie("CxINFO#Vous devez être leader ou scribe pour pouvoir utiliser cette commande.");
			return;
		}
		
		try {
			//Pattern pattern = Pattern.compile("[^a-zA-Z0-9#\\.]");
            //Matcher matcher = pattern.matcher(BLASON);
            //if ((matcher.find() || (!BLASON.isEmpty() && BLASON.length() != 12)) && !BLASON.equals("0")) {

			if ( BLASON.isEmpty() || (!Charger_Avatar_Imgur(BLASON) && !BLASON.equals("0"))) {
            	JOUEUR.Envoie("CxINFO#Format invalide. Par exemple pour https://i.imgur.com/syZxOd6.jpg, taper /avatar https://i.imgur.com/syZxOd6.jpg. Tapez /blason 0 pour supprimer votre blason.");
                return;
            }
			
			if (BLASON.equals("0")) { // Plus de blason
				serveur.Bdd.Requete_TeamSetBlason.setNull(1, java.sql.Types.VARCHAR);
				serveur.Bdd.Requete_TeamSetBlason.setInt(2, JOUEUR.IdTeam);
				serveur.Bdd.Requete_TeamSetBlason.executeUpdate();
				
				for (int i = serveur.ListeJoueur.size() - 1; i >= 0; i--) {
					Joueur J = serveur.ListeJoueur.get(i);
					if (J.PorteBlason && J.IdTeam == JOUEUR.IdTeam) {
						J.Avatar = Serveur.AVATAR_DEFAUT;
					}
				}
				
				JOUEUR.Envoie("CxINFO#Le blason a été supprimé.");
			} else {
				serveur.Bdd.Requete_TeamSetBlason.setString(1, BLASON);
				serveur.Bdd.Requete_TeamSetBlason.setInt(2, JOUEUR.IdTeam);
				serveur.Bdd.Requete_TeamSetBlason.executeUpdate();
				
				for (int i = serveur.ListeJoueur.size() - 1; i >= 0; i--) {
					Joueur J = serveur.ListeJoueur.get(i);
					if (J.PorteBlason && J.IdTeam == JOUEUR.IdTeam) {
						J.Avatar = BLASON;
					}
				}
				
				JOUEUR.Envoie("CxINFO#Le blason a été changé !");
			}
		} catch(Exception e) {JOUEUR.Envoie("CxINFO#Impossible de modifier le blason.");};
		
	}
	
	private void ChargerBlason(Joueur JOUEUR) {
		// SELECT blason FROM $teams WHERE id=?
		if(!JOUEUR.Membre) {
			JOUEUR.Envoie("CxINFO#Vous devez être membre d'une team pour pouvoir mettre un blason.");
			return;
		}
		
		JOUEUR.PorteBlason = true;
		String Blason = GetBlason(JOUEUR.IdTeam);
		
		if (Blason == null) {
			JOUEUR.Envoie("CxINFO#Impossible de mettre le blason.");
		} else {
			JOUEUR.Avatar = Blason;
		}
	}
	
	public String GetBlason(int IdTeam) {
		try {
			ResultSet Requete;
			String Blason = "";
			
			serveur.Bdd.Requete_TeamGetBlason.setInt(1, IdTeam);
			Requete = serveur.Bdd.Requete_TeamGetBlason.executeQuery();

			
			if (Requete.next()) {
				Blason = Requete.getString(1);
				if (Blason == null) {
					Blason = Serveur.AVATAR_DEFAUT;
				}
			}
			
			return Blason;
		} catch(Exception e) {
			System.out.println("3X GetBlason"); e.printStackTrace();
			return null;
		}
	}
	
	private void Record_Map_Forto(Joueur Joueur, int Temps, long ID_Rally) {
		try {
			serveur.Bdd.Requete_GetRecordForto.setLong(1, ID_Rally);
			ResultSet resultat = serveur.Bdd.Requete_GetRecordForto.executeQuery();
			
			if(resultat.next() && (resultat.getInt(1) > Temps || resultat.getInt(1) == 0) && resultat.getByte(3) == 0) {
				int Record = resultat.getInt(1);
				String Recordman = resultat.getString(2);
				
			  // UPDATE $maps_forto SET record=?, recordman=? WHERE id=?
				serveur.Bdd.Requete_UpdateRecordForto.setInt(1, Temps);
				serveur.Bdd.Requete_UpdateRecordForto.setString(2, Joueur.NomJoueur);
				serveur.Bdd.Requete_UpdateRecordForto.setLong(3, ID_Rally);
				serveur.Bdd.Requete_UpdateRecordForto.executeUpdate();
				
				if (Joueur.PartieEnCours != null && Joueur.PartieEnCours.Forto) {
					Forteresse PartieForteresse = ((Forteresse)Joueur.PartieEnCours.getJeu());
					
					serveur.Envoie(Joueur.PartieEnCours.getLsJoueurPartie(), "CxINFO#" + Joueur.NomJoueur + " a fait un nouveau record !");
					PartieForteresse.Record = Temps;
					PartieForteresse.Recordman = Joueur.NomJoueur;
				}

				System.out.println("NEWREC " + ID_Rally + " : " + Recordman + ", " + Record + "s -> " + Joueur.NomJoueur + ", " + Temps);
				if (!Recordman.equals("") && !Joueur.NomJoueur.equals(Recordman)) {
					//System.out.println("Nouveau record");
					EnvoyerNouveauMP("Record Rally", Recordman, "Votre record (" + Record + " sec) sur la map " + ID_Rally + " a été battu par " + Joueur.NomJoueur + " de " + (Record - Temps) + " sec.", "-");
				}
			}
		} catch(Exception e) {System.out.print("3X Record_Map_Forto "); e.printStackTrace();};
	}
	
	private void Recherche_Map_Forto(Joueur JOUEUR, String ID_RALLY, String AUTEUR, String TITRE, String RECORDMAN) {
		StringBuilder Chaine = new StringBuilder("CxMAR#");
		
		if (CheckParameter("[^a-zA-Z0-9 ]",TITRE)
				|| CheckParameter("[^a-zA-Z*_0-9]",RECORDMAN)
				|| CheckParameter("[^a-zA-Z0-9 ]",AUTEUR)
				|| TITRE.length() > 20
				|| RECORDMAN.length() > 20
				|| AUTEUR.length() > 20) {
			return;
		}
		try {
			StringBuilder Query = new StringBuilder("SELECT id, auteur, titre, niveau, mode, note, status, record, recordman FROM `$maps_forto` ");
			
			StringBuilder Where = new StringBuilder();
			// clause where
			if (!ID_RALLY.isEmpty()) {
				Where.append("AND id=" + ID_RALLY + " ");
			}
			if (!TITRE.isEmpty()) {
				Where.append("AND titre like '%" + TITRE + "%' ");
			}
			if (!AUTEUR.isEmpty()) {
				Where.append("AND auteur = '" + AUTEUR + "' ");
			}
			if (!RECORDMAN.isEmpty()) {
				Where.append("AND recordman = '" + RECORDMAN + "' ");
			}

			String WhereClause = Where.toString();
			if (!WhereClause.isEmpty()) {
				Query.append("WHERE ");
				Query.append(WhereClause.substring(3)); // on enlève le premier AND
			}
			Query.append(" ORDER by id desc LIMIT 1000");
			ResultSet Resultat = serveur.Bdd.Requete.executeQuery(Query.toString());
			
			while (Resultat.next()) {
				long Id = Resultat.getLong("id");
				String Auteur = Resultat.getString("auteur");
				String Titre = Resultat.getString("titre");
				int Difficulte = Resultat.getInt("niveau");
				int Mode = Resultat.getInt("mode");
				int Note = Resultat.getInt("note");
				int Status = Resultat.getInt("status");
				int Record = Resultat.getInt("record");
				String Recordman = Resultat.getString("recordman");
				
				if (Status == 1 || (Status != 3 && (Auteur.equalsIgnoreCase(JOUEUR.NomJoueur) || (JOUEUR.AutorisationModoCartesRally || JOUEUR.AutorisationModo)))) {
					Chaine.append($$);
					Chaine.append(Id);
					Chaine.append($);
					Chaine.append(Auteur);
					Chaine.append($);
					Chaine.append(Titre);
					Chaine.append($);
					Chaine.append(Difficulte);
					Chaine.append($);
					Chaine.append(Mode);
					Chaine.append($);
					Chaine.append(Note);
					Chaine.append($);
					Chaine.append(Status);
					Chaine.append($);
					Chaine.append(Record);
					Chaine.append($);
					Chaine.append(Recordman);
				}
			}
			JOUEUR.Envoie(Chaine.toString());

		} catch (Exception e) {
			System.err.print("3XC RMF "); e.printStackTrace();
		}
	}
	
	private void PartageTopicTeamFusion(Joueur JOUEUR) {
		if (JOUEUR.Membre) {
			int ID_TEAM = JOUEUR.IdTeam;
			
			try {
				serveur.Bdd.Requete_TeamInverserPartageTopic.setInt(1, ID_TEAM);
				serveur.Bdd.Requete_TeamInverserPartageTopic.executeUpdate();
				
				serveur.Bdd.Requete_TeamGetPartageTopic.setInt(1, ID_TEAM);
				
				ResultSet EtatPartage = serveur.Bdd.Requete_TeamGetPartageTopic.executeQuery();
				if (EtatPartage.next()) {
					JOUEUR.Envoie("CxINFO#Partage du topic team avec les autres teams de la fusion "
								 + (EtatPartage.getBoolean(1) ? "" : "dés") + "activé.");
				}
			} catch (Exception e) {
				System.err.print("3XC PartageTopicTeamFusion "); e.printStackTrace();
			}
			
		}
	}
	
	private void Ajouter_Dojon(Joueur JOUEUR, int ID_DONJON, int NIVEAU, int DUREE, int SPECIAL, String CODE) {
		try {
			serveur.Bdd.Requete_AddDonjon.setInt(1, ID_DONJON);
			serveur.Bdd.Requete_AddDonjon.setShort(2, (short)NIVEAU);
			serveur.Bdd.Requete_AddDonjon.setInt(3, DUREE);
			serveur.Bdd.Requete_AddDonjon.setByte(4, (byte)SPECIAL);
			serveur.Bdd.Requete_AddDonjon.setString(5, CODE);
			
			serveur.Bdd.Requete_AddDonjon.executeUpdate();
			JOUEUR.Envoie("CxINFO#Niveau ajouté.");
		} catch (Exception e) {
			System.err.print("3XC Ajouter_Dojon "); e.printStackTrace();
		}
	}
	
	private void Load_Niveau_Donjon(Joueur JOUEUR, int DONJON, int NIVEAU, boolean SPECIAL) {
		try {
			serveur.Bdd.Requete_LoadMapNivDonjon.setInt(1, DONJON);
			serveur.Bdd.Requete_LoadMapNivDonjon.setShort(2, (short) NIVEAU);
			serveur.Bdd.Requete_LoadMapNivDonjon.setByte(3, (byte) (SPECIAL?1:0));
			
			ResultSet Map = serveur.Bdd.Requete_LoadMapNivDonjon.executeQuery();
			if (Map.next()) {
				if (JOUEUR.PartieEnCours.Forto) {
					Forteresse PartieForteresse = ((Forteresse)JOUEUR.PartieEnCours.getJeu());
					int Duree = Map.getInt(2); 
					
					PartieForteresse.LoadMapForto(JOUEUR, "7", Forteresse.COULEUR_CARRE_DESTRUCTIBLE, Forteresse.COULEUR_CARRE_INDESTRUCTIBLE, Forteresse.COULEUR_CARRE_OBJECTIF, Forteresse.COULEUR_CARRE_NONRECONSTRUCTIBLE, Forteresse.COULEUR_CARRE_CONSTRUCTIBLE, Forteresse.COULEUR_CARRE_INCONSTRUCTIBLE, Map.getString(1), 0);
					PartieForteresse.NumeroDonjon = DONJON;
					PartieForteresse.NiveauDonjon = NIVEAU;
					PartieForteresse.NiveauBonus = SPECIAL;
					
					if (JOUEUR.JDonjon.getClasse() == yshCastle.Donjon.ClassesDonjon.Hero.v) {
						PartieForteresse.DureeMaxDonjon = (int) (Duree * 0.8);
					} else if (JOUEUR.JDonjon.getClasse() == yshCastle.Donjon.ClassesDonjon.Guerrier.v) {
						PartieForteresse.DureeMaxDonjon = (int) (Duree * 1.2);
					} else {
						PartieForteresse.DureeMaxDonjon = Duree;
					}
					
					JOUEUR.JDonjon.setNiveau((short)NIVEAU);
					JOUEUR.JDonjon.setNiveauBonus(SPECIAL);
					
					JOUEUR.Envoie("CxDONJ#" + PartieForteresse.DureeMaxDonjon);
					
					serveur.Envoie(JOUEUR.PartieEnCours.getLsJoueurPartie(), "CxINFO#Niveau " + (SPECIAL ? "(bonus) " : "") + NIVEAU + " du donjon chargé. Ce niveau doit être fini avant " + PartieForteresse.DureeMaxDonjon + " secondes.");
					JOUEUR.JDonjon.TempsStart = System.currentTimeMillis();
				}
			} else {
				JOUEUR.Envoie("CxINFO#Chargement du niveau impossible. [" + DONJON + "|" + NIVEAU + "|" + SPECIAL + "]");
			}
		} catch (Exception e) {
			System.err.print("3XC Load_Niveau_Donjon "); e.printStackTrace();
		}
	}
	
	private void Charger_Donjon(Joueur JOUEUR, int DONJON) {
		try {
			// SELECT niv, niv_bonus, points, items, classe, groupe FROM $donjon_j WHERE id_donjon=? && id=? LIMIT 1
			serveur.Bdd.Requete_ChargerDonjon.setInt(1,  DONJON);
			serveur.Bdd.Requete_ChargerDonjon.setInt(2, JOUEUR.IdJoueur);
			
			ResultSet Infos = serveur.Bdd.Requete_ChargerDonjon.executeQuery();
			
			if (Infos.next()) {
				String Items_Str = Infos.getString(4); // Format : "Items1,Item2,..."
				
				JOUEUR.JDonjon = new yshCastle.JoueurDonjon(JOUEUR, Infos.getShort(1), Infos.getBoolean(2), Infos.getInt(3), Infos.getInt(5), Infos.getInt(6), DONJON);
				
				JOUEUR.JDonjon.loadListeItems(Items_Str);
			} else {
				JOUEUR.JDonjon = new yshCastle.JoueurDonjon(JOUEUR, DONJON);
				
				serveur.Bdd.Requete_AddJoueurDonjon.setInt(1, JOUEUR.IdJoueur);
				serveur.Bdd.Requete_AddJoueurDonjon.setInt(2,  DONJON);
				serveur.Bdd.Requete_AddJoueurDonjon.execute();
				
				// TODO : Envoyer message pour indiquer qu'il faut créer un compte (aucun pré-existant)
			}
			Load_Niveau_Donjon(JOUEUR, JOUEUR.JDonjon.getDonjon(), JOUEUR.JDonjon.getNiveau(), JOUEUR.JDonjon.isNiveauBonus());
		} catch (Exception e) {
			System.err.print("3XC Charger_Donjon "); e.printStackTrace();
		}
	}
	
	private void Sauver_Avancement_Donjon(Joueur JOUEUR) {
		try {
			// UPDATE $donjon_j SET niv=?, niv_bonus=?, points=?, items=?, classe=?, groupe=? WHERE id_donjon=? && id=? LIMIT 1
			serveur.Bdd.Requete_SvgdAvancementDonjon.setInt(1, JOUEUR.JDonjon.getNiveau());
			serveur.Bdd.Requete_SvgdAvancementDonjon.setBoolean(2, JOUEUR.JDonjon.isNiveauBonus());
			serveur.Bdd.Requete_SvgdAvancementDonjon.setInt(3, JOUEUR.JDonjon.getPoints());
			serveur.Bdd.Requete_SvgdAvancementDonjon.setString(4, JOUEUR.JDonjon.getListeItems());
			serveur.Bdd.Requete_SvgdAvancementDonjon.setInt(5, JOUEUR.JDonjon.getClasse());
			serveur.Bdd.Requete_SvgdAvancementDonjon.setInt(6, JOUEUR.JDonjon.getGroupe());
			
			serveur.Bdd.Requete_SvgdAvancementDonjon.setInt(7, JOUEUR.JDonjon.getDonjon());
			serveur.Bdd.Requete_SvgdAvancementDonjon.setInt(8, JOUEUR.IdJoueur);
			
			serveur.Bdd.Requete_SvgdAvancementDonjon.executeUpdate();
		} catch(Exception e) {
			System.err.print("3XC Sauver_Avancement_Donjon "); e.printStackTrace();
		}
	}
	
	private void Delete_Comptes_Inutilises() {
		if (Local.LOCAL) {
			return;
		}
		
		try {
			ResultSet res = serveur.Bdd.Requete_GetComptesInactifsASuppr.executeQuery();
			String lsPseudos = "";
			
			while (res.next()) {
				String pseudo = res.getString("n");
				int id = res.getInt("id");
				
				lsPseudos += pseudo + " ";
				
				SupprimerCompte(id, pseudo);
			}
			
			if (!lsPseudos.isEmpty()) {
				System.out.println("C0M AUTOSUPR " + lsPseudos);
			}
		} catch(Exception e) {
			System.err.print("3XC Delete_Comptes_Inutilises "); e.printStackTrace();
		}
	}
	
	public void SupprimerCompte(int id, String pseudo) throws Exception {
		String nouvPseudo = "$" + pseudo;
		
		serveur.Bdd.Requete_SupprCompteByID.setInt(1, id);
		serveur.Bdd.Requete_SupprCompteByID.executeUpdate();
		
		serveur.Bdd.Requete_SCI_CleanListesAmis.setInt(1, id);
		serveur.Bdd.Requete_SCI_CleanListesAmis.setInt(2, id);
		serveur.Bdd.Requete_SCI_CleanListesAmis.executeUpdate();
		
		serveur.Bdd.Requete_SCI_CleanPostsForum.setString(1, nouvPseudo);
		serveur.Bdd.Requete_SCI_CleanPostsForum.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_CleanPostsForum.executeUpdate();
		
		serveur.Bdd.Requete_SCI_CleanTopicsForum.setString(1, nouvPseudo);
		serveur.Bdd.Requete_SCI_CleanTopicsForum.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_CleanTopicsForum.executeUpdate();
		
		serveur.Bdd.Requete_SCI_CleanEloA.setInt(1, id);
		serveur.Bdd.Requete_SCI_CleanEloA.executeUpdate();
		serveur.Bdd.Requete_SCI_CleanEloB.setInt(1, id);
		serveur.Bdd.Requete_SCI_CleanEloB.executeUpdate();
		serveur.Bdd.Requete_SCI_CleanEloF.setInt(1, id);
		serveur.Bdd.Requete_SCI_CleanEloF.executeUpdate();
		
		serveur.Bdd.Requete_SCI_CleanHistoSanctions.setString(1, nouvPseudo);
		serveur.Bdd.Requete_SCI_CleanHistoSanctions.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_CleanHistoSanctions.executeUpdate();
		
		serveur.Bdd.Requete_SCI_CleanMessagerie.setString(1, pseudo);
		serveur.Bdd.Requete_SCI_CleanMessagerie.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_CleanMessagerie.executeUpdate();
		
		serveur.Bdd.Requete_SCI_RemplaceAuteursMapsA.setString(1, nouvPseudo);
		serveur.Bdd.Requete_SCI_RemplaceAuteursMapsA.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_RemplaceAuteursMapsA.executeUpdate();
		
		serveur.Bdd.Requete_SCI_RemplaceRecordmanMapsA.setString(1, nouvPseudo);
		serveur.Bdd.Requete_SCI_RemplaceRecordmanMapsA.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_RemplaceRecordmanMapsA.executeUpdate();
		
		serveur.Bdd.Requete_SCI_RemplaceAuteursMapsF.setString(1, nouvPseudo);
		serveur.Bdd.Requete_SCI_RemplaceAuteursMapsF.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_RemplaceAuteursMapsF.executeUpdate();
		
		serveur.Bdd.Requete_SCI_RemplaceRecordmanMapsF.setString(1, nouvPseudo);
		serveur.Bdd.Requete_SCI_RemplaceRecordmanMapsF.setString(2, pseudo);
		serveur.Bdd.Requete_SCI_RemplaceRecordmanMapsF.executeUpdate();
		
		serveur.Bdd.Requete_SCI_CleanAuteurPlaylist.setInt(1, id);
		serveur.Bdd.Requete_SCI_CleanAuteurPlaylist.executeUpdate();
		
		serveur.Bdd.Requete_SCI_DeleteDonjonJ.setInt(1, id);
		serveur.Bdd.Requete_SCI_DeleteDonjonJ.executeUpdate();
	}
	
	
	private void Clean_Fusions_Teams(Joueur J) {
		try {
			for (FusionTeam fusion : new HashSet<FusionTeam>(serveur.ListeTeamAmie.values())) {
				ArrayList<Integer> Alliance = fusion.Alliance;
				J.Envoie("CxINFO#Fusion num. " + fusion.Id + " : ");
				for (int i = Alliance.size() - 1; i >= 0; --i) {
					// On supprime toute team qui n'existe plus de la fusion
					ResultSet res = serveur.Bdd.Requete.executeQuery("SELECT EXISTS(SELECT * FROM `$teams` WHERE id=" + Alliance.get(i) + ") AS exist");

					if (!res.next() || res.getBoolean(1) == false) {
						J.Envoie("CxINFO#   " + Alliance.get(i) + " suppr");
						serveur.Retirer_Team_Fusion(Alliance.get(i), null);
					}
				}				
			}
		} catch (Exception e) {
			System.err.print("3XC Clean_Fusions_Teams "); e.printStackTrace();
		}
	}
	
	private void Update_Profil_En_Ligne(Joueur j, String pseudoCible) {
		Joueur cible = serveur.Joueur(pseudoCible);
		
		if (cible != null) {
			cible.UneRecompense = ChaineMedailles(cible.getPseudo());
			j.Envoie("CxINFO#Profil de " + cible.getPseudo() + " actualisé.");
		} else {
			j.Envoie("CxINFO#" + pseudoCible+ " n'est pas connecté.");
		}
	}
	
	private void Importer_Compte(Joueur importeur, String[] infos) {
		try {
			String[] recompense;
	        PreparedStatement Requete_ImporterCompte = Serveur.getServeur().Bdd.Requete_ImporterCompte;
	        
	        Requete_ImporterCompte.setString(1, infos[1]);
	        Requete_ImporterCompte.setInt(2, Integer.parseInt(infos[2]));
	        Requete_ImporterCompte.setString(3, infos[0]);
	        
	        for (String rec : infos[3].split("@@")) {
	        	if (rec.isEmpty()) {
	        		continue;
	        	}
	        	
	        	recompense = rec.split("@");
	        	Serveur.getServeur().BOITE.Requete(Boite.AJOUTER_RECOMPENSE_JOUEUR, importeur, infos[0], recompense[0], recompense[1]);
	        }
	        
	        Requete_ImporterCompte.execute();
		} catch (Exception e) {
			importeur.Envoie("CxINFO#Erreur lors de l'importation : " + e.getMessage() + ".");
			e.printStackTrace();
		}
	}

	/// ******************************************************************************************************
	public void run() {
		try {
			while (true) {
				RequeteBoite RequeteBoite = ListeRequete.poll();
				if (RequeteBoite != null) {
					try {
						setInfosRequeteEnCours(RequeteBoite);
						
						if (serveur.PerfDB) {
							RequeteBoite.Execution2();
						} else {
							RequeteBoite.Execution();
						}
					} catch (Exception e) {
						System.err.print("3XC run requete "); e.printStackTrace();
					}
					sleep(1);
				} else {
					setInfosRequeteEnCours(null);
					sleep(10);
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Boite "); e.printStackTrace();
		}
	}
	
	private synchronized void setInfosRequeteEnCours(RequeteBoite Requete) {
		if (Requete != null) {
			TypeRequeteEnCours = Requete.Type;
			++IdRequete;
			RequeteEnCours = Requete;
		} else {
			TypeRequeteEnCours = 0;
			RequeteEnCours = null;
		}
		
	}
	
	public synchronized String getInfosRequeteEnCours() {
		return "Type : " + TypeRequeteEnCours + "\nID : " + IdRequete + "\nJoueur : " + ((RequeteEnCours == null || RequeteEnCours.Joueur == null) ? "(null)" : (RequeteEnCours.Joueur.NomJoueur + "\nIP : " + RequeteEnCours.Joueur.AdresseIP));
	}

	private class RequeteBoite {
		Joueur Joueur;
		int Type;
		int Entier1;
		int Entier2;
		String Chaine1;
		String Chaine2;
		String Chaine3;
		String Chaine4;
		boolean Boolean;
		int[] ListeEntier;
		String[] ListeString;
		Object[] Obj;
		Stat Stat;
		Rally Rally;

		private RequeteBoite(int TYPE, Joueur JOUEUR, int ENTIER_1, int ENTIER_2, String CHAINE_1, String CHAINE_2, boolean BOOLEAN, String CHAINE_3, int[] LISTE_ENTIER, String CHAINE_4, String[] LISTE_STRING, Object[] OBJ) {
			Joueur = JOUEUR;
			Type = TYPE;
			Entier1 = ENTIER_1;
			Entier2 = ENTIER_2;
			Chaine1 = CHAINE_1;
			Chaine2 = CHAINE_2;
			Chaine3 = CHAINE_3;
			Chaine4 = CHAINE_4;
			Boolean = BOOLEAN;
			ListeEntier = LISTE_ENTIER;
			ListeString = LISTE_STRING;
			Obj = OBJ;
		}

		private void Execution2() {
			long debut = System.currentTimeMillis();
			Execution();
			long end = System.currentTimeMillis() - debut;
			System.out.println("Boite : " + Type + " : " + end);
		}

		private void Execution() {
			try {
				if (Type == DEMANDE_LISTE_SUJET) {
					Forum_Envoie_Liste_Sujet(Joueur);
				} else if (Type == RECHERCHE_SUJET) {
					Forum_Recherche_Sujet(Joueur, Chaine1);
				} else if (Type == RECHERCHE_SUJET_AUTEUR) {
					Forum_Recherche_Sujet_Auteur(Joueur, Chaine1);
				} else if (Type == DEMANDE_SUJET) {
					Forum_Envoie_Sujet(Joueur, Entier1, Entier2);
				} else if (Type == ENVOIE_SONDAGE) {
					Envoie_Sondage(Joueur, Entier1);
				} else if (Type == NOUVEAU_MESSAGE) {
					Forum_Nouveau_Message(Joueur, Entier1, Chaine1, Boolean);
				} else if (Type == SUPPRESSION_DISCUSSION) {
					Suppression_Discussion(Joueur, Entier1);
				} else if (Type == EDITION_TITRE) {
					Edition_Titre(Joueur, Entier1, Entier2, Chaine1);
				} else if (Type == DEPLACEMENT_SUJET) {
					Deplacement_Sujet(Joueur, Entier1, Entier2);
				} else if (Type == NOUVEAU_SUJET) {
					Nouveau_Sujet(Joueur, Chaine1, Chaine2, Joueur.ForumEnCours);
				} else if (Type == FERMETURE_SUJET) {
					Fermeture_Sujet(Joueur, Chaine1, Entier1);
				} else if (Type == REPONSE_SUJET) {
					Réponse_Sujet(Joueur, Chaine1, Boolean);
				} else if (Type == EDITION_MESSAGE) {
					Edition_Message(Joueur, Entier1, Chaine1);
				} else if (Type == SUPPRESSION_MESSAGE) {
					Suppression_Message(Joueur, Entier1, Boolean);
				} else if (Type == MUTE_FORUM) {
					Mute_Forum(Joueur, Chaine1, Entier1, Chaine2);
				} else if (Type == CHANGEMENT_AVATAR) {
					Changement_Avatar(Joueur);
				} else if (Type == CHANGEMENT_AVATAR_NEW) {
					Changement_Avatar_New(Joueur, Chaine1);
				} else if (Type == VOTE) {
					Vote(Joueur, Entier1, Entier2);
				} else if (Type == REPONSE_SONDAGE) {
					Reponse_Sondage(Joueur, Entier1, Entier2);
				} else if (Type == EDITER_TITRE_FORUM) {
					Editer_Titre_Forum(Joueur, Chaine1, Chaine2, Chaine3);
				} else if (Type == SUPPRIMER_TITRE_FORUM) {
					Supprimer_Titre_Forum(Joueur, Chaine1);
				} else if (Type == CHANGEMENT_DESCRIPTION) {
					Changement_Description(Joueur);
				} else if (Type == SUPPRESSION_AMI) {
					Suppression_Ami(Joueur, Entier1);
				} else if (Type == DEMANDE_LISTE_MODO) {
					Demande_Liste_Modo(Joueur);
				} else if (Type == AJOUT_AMI) {
					Ajout_Ami(Joueur, Chaine1);
				} else if (Type == CHARGEMENT_JOUEUR) {
					Chargement_Joueur(Joueur, Chaine1, Boolean);
				} else if (Type == SAUVEGARDE_JOUEUR) {
					Sauvegarde_Joueur(Joueur, ListeEntier);
				} else if (Type == SAUVEGARDE_MARA) {
					Sauvegarde_Mara(Joueur);
				} else if (Type == SAUVEGARDE_MARA_BANNI) {
					Sauvegarde_Mara_Banni(Joueur);
				} else if (Type == CREER_MARA) {
					Creer_Mara(Joueur);
				} else if (Type == IDENTIFICATION) {
					Identification(Joueur, Chaine1, Chaine2);
				} else if (Type == CREATION_COMPTE) {
					Creation_Compte(Joueur, Chaine1, Chaine2);
				} else if (Type == QUITTER_TEAM) {
					Quitter_Team(Joueur, Chaine1);
				} else if (Type == REJOINDRE_TEAM) {
					Rejoindre_Team(Joueur, Entier1, Entier2, false);
				} else if (Type == CHANGER_GRADE) {
					Changer_Grade(Joueur, Entier1, Chaine1, Boolean);
				} else if (Type == CREER_TEAM) {
					Créer_Team(Joueur, Chaine1, Chaine2, Chaine3, "Pas de description.", "Aucun", Entier1, Entier2);
				} else if (Type == SUPPRIMER_TEAM) {
					Supprimer_Team(Joueur, Entier1);
				} else if (Type == LISTE_TEAM) {
					Liste_Team(Joueur);
				} else if (Type == MEMBRES_TEAM) {
					Membres_Team(Joueur, Entier1);
				} else if (Type == INFO_TEAM) {
					Info_Team(Joueur, Entier1);
				} else if (Type == CHANGER_ROLE) {
					Changer_Role(Joueur, Entier1, Chaine1);
				} else if (Type == EDITER_SITE) {
					Editer_Site(Joueur, Entier1, Chaine1);
				} else if (Type == EDITER_DESCRIPTION) {
					Editer_Description(Joueur, Entier1, Chaine1);
				} else if (Type == EDITER_MESSAGE) {
					Editer_Message(Joueur, Chaine1);
				} else if (Type == EDITER_INEDITABLE) {
					Editer_Ineditable(Joueur, Entier1, Chaine1, Chaine2);
				} else if (Type == DEMANDE_PROFIL) {
					Demande_Profil(Joueur, Chaine1);
				} else if (Type == DEMANDE_INFO) {
					Demande_Info(Joueur, Chaine1);
				} else if (Type == AJOUTER_MEMBRE) {
					Ajouter_Membre(Joueur, Chaine1, Entier1);
				} else if (Type == AJOUTER_RECOMPENSE) {
					Ajouter_Recompense(Joueur, Entier1, Chaine1, Chaine2);
				} else if (Type == AJOUTER_RECOMPENSE_JOUEUR) {
					Ajouter_Recompense_Joueur(Joueur, Chaine1, Chaine2, Chaine3);
				} else if (Type == SUPPRIMER_RECOMPENSE_JOUEUR) {
					Supprimer_Recompense_Joueur(Joueur, Chaine1, Entier1);
				} else if (Type == SUPPRIMER_RECOMPENSES) {
					Supprimer_Recompenses(Joueur, Entier1);
				} else if (Type == DEMANDE_HISTO) {
					Demande_Histo(Joueur, Chaine1, Chaine2, false);
				} else if (Type == LOG_SANCTION) {
					Log_Sanction(Joueur, Chaine1, Chaine2, Chaine3, Chaine4, Entier1);
				} else if (Type == DEMANDE_HISTO_FORUM) {
					Demande_Histo_Forum(Joueur, Chaine1);
				} else if (Type == DEMANDE_LOG) {
					Demande_Histo(Joueur, "", "", true);
				} else if (Type == CHANGER_PASSWORD) {
					Changer_Password(Joueur, Chaine1, Chaine2);
				} else if (Type == COPIE_PASSWORD) {
					Copie_Password(Joueur, Chaine1, Chaine2);
				} else if (Type == POSTIT_TOPIC) {
					Postit_Topic(Joueur, Chaine1);
				} else if (Type == TOPIC_ANIM) {
					Topic_Anim(Joueur, Entier1);
				} else if (Type == SUPPRIMER_AVATAR) {
					Supprimer_Avatar(Joueur, Chaine1);
				} else if (Type == SUPPRIMER_DESCRIPTION) {
					Supprimer_Description(Joueur, Chaine1);
				} else if (Type == BLOQUER_COMPTE) {
					Bloquer_Compte(Joueur, Chaine1, Chaine2);
				} else if (Type == MUTECP) {
					Mutecp(Joueur, Chaine1);
				} else if (Type == MUTEFORUM) {
					Muteforum(Joueur, Chaine1);
				} else if (Type == DEBAN) {
					Deban(Joueur, Chaine1,Chaine2);
				} else if (Type == TEMPS_MOINS) {
					Temps_Moins(Joueur, Chaine1, Entier1);
				} else if (Type == AUTORISER_CHGPASS) {
					Autoriser_Chgpass(Joueur, Chaine1, Chaine2);
				} else if (Type == CHGPASS) {
					Chgpass(Joueur, Chaine1, Chaine2);
				} else if (Type == VALIDER_CHGPASS) {
					Valider_Chgpass(Joueur, Chaine1,Chaine2);
				} else if (Type == LISTE_CHGPASS) {
					Liste_Chgpass(Joueur);
				} else if (Type == BAN_IP) {
					BanIP(Joueur, Chaine1);
				} else if (Type == DEBAN_IP) {
					DeBanIP(Joueur, Chaine1);
				} else if (Type == BAN_HOST) {
					BanHost(Joueur, Chaine1);
				} else if (Type == DEBAN_HOST) {
					DeBanHost(Joueur, Chaine1);
				} else if (Type == LOADBANDEF) {
					LoadBanDef(Joueur);
				} else if (Type == LOADSANCTIONS) {
					Load_Sanctions(Joueur);
				} else if (Type == LISTE_MAP_JOUEUR) {
					Liste_Map_Joueur(Joueur, Chaine1);
				} else if (Type == RECHERCHE_MAP) {
					Recherche_Map(Joueur, Chaine1, Chaine2, Chaine3, Entier1);
				} else if (Type == RECHERCHE_MAP_BY_ID) {
					Recherche_Map_By_Id(Joueur, Chaine1);
				} else if (Type == RECHERCHE_MAP_BY_IDS) {
					Recherche_Map_By_Ids(Joueur, Chaine1);
				} else if (Type == CREATE_MAP) {
					Create_Map(Joueur, Chaine1, Chaine2, Chaine3, Entier1);
				} else if (Type == UPDATE_MAP) {
					Update_Map(Joueur, Chaine1, Chaine2, Chaine3, Entier1);
				} else if (Type == UPDATE_STATS_MAP) {
					Update_Stats_Map(Joueur, ListeString);
				} else if (Type == DELETE_MAP) {
					Delete_Map(Joueur, Chaine1, Chaine2);
				} else if (Type == FLAG_MAP) {
					Flag_Map(Joueur, Chaine1,Entier1);
				} else if (Type == SUPPR_VOTES_MAP) {
					Suppr_Votes_Map(Joueur,Chaine1);
				} else if (Type == SUPPR_RECORD_MAP) {
					Suppr_Record_Map(Joueur,Chaine1);
				} else if (Type == PERMA_MAP) {
					Perma_Map(Joueur, Chaine1,Entier1);
				} else if (Type == RETROUVER_JOUEUR) {
					Retrouver_Joueur(Joueur, Chaine1);
				} else if (Type == MULTI_OFF) {
					Multi_Off(Joueur, Chaine1);
				} else if (Type == MULTI_PWD) {
					Multi_Pwd(Joueur, Chaine1);
				} else if (Type == FLAGS) {
					Flags(Joueur, Chaine1, Chaine2);
				} else if (Type == FLAGS_JOUEUR) {
					FlagsJoueur(Joueur, Chaine1);
				} else if (Type == STOP_MARABOUM) {
					Stop_Maraboum(Joueur);
				} else if (Type == MAJ_CLASSEMENT) {
					serveur.MAJ_Classement();
				} else if (Type == LOG_TEAM) {
					Log_Team(Joueur, Chaine1, Chaine2, Entier1);
				} else if (Type == HISTO_TEAM) {
					Histo_Team(Joueur, Entier1);
				} else if (Type == HISTO_TEAM_JOUEUR) {
					Histo_Team_Joueur(Joueur, Chaine1);
				} else if (Type == RESTORE_MAP) {
					Restore_Map(Joueur, Chaine1);
				} else if (Type == HISTO_MAP) {
					Histo_Maps_Joueur(Joueur, Chaine1);
				} else if (Type == DELETE_HISTORY) {
					Delete_History(Joueur, Chaine1);
				} else if (Type == LISTE_MP_RECUS) {
					Liste_MP_Recus(Joueur);
				} else if (Type == LISTE_MP_ENVOYES) {
					Liste_MP_Envoyes(Joueur);
				} else if (Type == SUPPRIMER_MP) {
					Supprimer_MP(Joueur);
				} else if (Type == SUPPRIMER_MP_BY_ID) {
					Supprimer_MP_By_Id(Joueur, Entier1);
				} else if (Type == UPDATE_GHOST_MAP) {
					Update_Ghost_Map(Joueur, Chaine1, Chaine2);
				} if (Type == NOUVEAU_MP) {
					Nouveau_MP(Joueur, Chaine1, Chaine2);
				} else if (Type == NB_MP_RECU) {
					Nb_MP_Recu(Joueur);
				} else if (Type == LISTE_RECOMPENSES_JOUEUR) {
					Liste_Recompenses_Joueur(Joueur, Chaine1);
				} else if (Type == PLAYLIST_STANDARD) {
					Playlist_standard(Joueur, Chaine1);
				} else if (Type == PLAYLIST_STANDARD_COMMANDE) {
					Playlist_standard_commande(Joueur, Chaine1);
				} else if (Type == PLAYLIST_PUBLIER) {
					Playlist_publier(Joueur, Chaine1, Chaine2, Chaine3);
				} else if (Type == PLAYLIST_LISTE) {
					Playlist_recherche(Joueur, "");
				} else if (Type == PLAYLIST_SUPPRIMER){
					Playlist_supprimer(Joueur, Chaine1, Chaine2);
				} else if (Type == PLAYLIST_PUBLIER_VALIDER) {
					Playlist_publier_valider(Joueur, Chaine1, Chaine2, true);
				} else if (Type == PLAYLIST_RECHERCHE) {
					Playlist_recherche(Joueur, Chaine1);
				} else if (Type == DEBAN_JOUEUR) {
					Deban_joueur(Joueur, Entier1);
				} else if (Type == FIN_SANCTION) {
					Fin_sanction(Joueur,Entier1);
				} else if (Type == LOAD_STATS) {
					Load_Stats(Joueur,Stat);
				} else if (Type == SAUVEGARDE_STAT) {
					Sauvegarde_Stat(Joueur,Stat);
				} else if (Type == NEW_STAT) {
					New_Stat(Joueur,Stat);
				} else if (Type == ARCHIVER_STATS) {
					Archiver_Stats(Joueur);
				} else if (Type == NOUVELLE_FUSION) {
					Nouvelle_Fusion(Joueur,ListeEntier);
				} else if (Type == UPDATE_FUSION) {
					Update_Fusion(Joueur,Entier1,Entier2);
				} else if (Type == QUITTER_FUSION) {
					Quitter_Fusion(Joueur,Entier1,Entier2);
				} else if (Type == SUPPRIMER_FUSION) {
					Supprimer_Fusion(Joueur,Entier1);
				} else if (Type == LOAD_FUSIONS) {
					Load_Fusions(Joueur);
				} else if (Type == DEMANDE_COMPTE_JPO) {
					Demande_Compte_JPO(Joueur,Chaine1);
				} else if (Type == VALIDER_DEMANDE_JPO) {
					Valider_Demande_JPO(Joueur,Chaine1,Boolean);
				} else if (Type == LISTE_JPO) {
					Liste_JPO(Joueur);
				} else if (Type == CREER_RALLY) {
					Creer_Rally(Joueur,Rally);
				} else if (Type == UPDATE_RALLY) {
					Update_Rally(Joueur,Rally);
				} else if (Type == CHARGER_RALLY) {
					Charger_Rally(Joueur,Chaine1);
				} else if (Type == CHARGER_RALLY_RESPOMAP) {
					Charger_Rally_Respomap(Joueur,Chaine1); // 4 couleurs perso + id map
				} else if (Type == DELETE_RALLYS) {
					Delete_Rallys(Joueur);
				} else if (Type == LISTE_RALLY) {
					Liste_Rally(Joueur);
				} else if (Type == AUTORISER_GRADE_9) {
					Autoriser_Grade_9(Joueur,Chaine1);
				} else if (Type == REVERSE_CODE_MAP) {
					CodeFinMapReverse(Joueur, Chaine1, Entier1);
				} else if (Type == SET_BLASON_TEAM) {
					SetBlason(Joueur, Chaine1);
				} else if (Type == RECORD_MAP_FORTO) {
					Record_Map_Forto(Joueur, Integer.valueOf(Chaine1), Long.parseLong(Chaine2));
				} else if (Type == RECHERCHE_MAP_FORTO) {
					Recherche_Map_Forto(Joueur, Chaine1, Chaine2, Chaine3, Chaine4);
				} else if (Type == PARTAGER_TOPIC_TEAM_FUSION) {
					PartageTopicTeamFusion(Joueur);
				} else if (Type == AJOUTER_DONJON) {
					Ajouter_Dojon(Joueur, ListeEntier[0], ListeEntier[1], ListeEntier[2], ListeEntier[3], Chaine1);
				} else if (Type == CHARGER_NIV_DONJON) {
					Load_Niveau_Donjon(Joueur, ListeEntier[0], ListeEntier[1], (ListeEntier[2] == 1));
				} else if (Type == CHARGER_DONJON) {
					Charger_Donjon(Joueur, Entier1);
				} else if (Type == SVGD_AVANCEMENT_DONJON) {
					Sauver_Avancement_Donjon(Joueur);
				} else if (Type == MASSE_REFUS_RALLY) {
					Masse_Refus_Rally(Joueur, Chaine1);
				} else if (Type == CALCULER_NOUVEL_ELO) {
					EloJoueur.CommandeElo(Chaine1, Joueur);
				} else if (Type == CLASSEMENT_ELO) {
					Elo.envoieClassementElo(Entier1, Joueur, Entier2);
				} else if (Type == DELETE_COMPTES_INUTILISES) {
					Delete_Comptes_Inutilises();
				} else if (Type == CALCULER_NOUVEL_ELO_TEAM) {
					EloTeam.CommandeElo(Chaine1, Joueur);
				} else if (Type == OBTENIR_STATS_ELO_PROFIL) {
					Obtenir_Stats_Elo_Profil(Joueur, Chaine1, Chaine2, Entier1 == 1);
				} else if (Type == RELOAD_MAP_AAAAH) {
					Reload_Map_Aaaah(Joueur, Chaine1);
				} else if (Type == ATTRIBUER_RECOMPENSES_ELO) {
					Attribuer_Recompenses_Elo(Joueur);
				} else if (Type == LOAD_MAP_PLAYLIST_FORTO) {
					Load_Map_Playlist_Forto(Obj);
				} else if (Type == ARCHIVER_STATS_ELO) {
					RecompenseElo.ArchiverElo();
				} else if (Type == AJOUTER_G10) {
					Ajouter_Grade_10(Entier1);
				} else if (Type == AJOUTER_G11) {
					Ajouter_Grade_11(Entier1);
				} else if (Type == BLOQUER_COMPTE2) {
					Bloquer_Compte2(Joueur, Chaine1);
				} else if (Type == TMP_CLEAN_FUSIONS) {
					Clean_Fusions_Teams(Joueur);
				} else if (Type == UPDATE_PROFIL_EN_LIGNE) {
					Update_Profil_En_Ligne(Joueur, Chaine1);
				} else if (Type == IMPORTER_COMPTE) {
					Importer_Compte(Joueur, ListeString);
				}
				
			} catch (Exception e) {
				System.err.print("3XC Boite boucle "); e.printStackTrace();
				Joueur.DemandeDeco = true;
				Joueur.RaisonDeco = "Erreur boite.";
			}
		}
	}
}
