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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public abstract class BDD {
	public Properties InfoBD = new Properties();
	public Connection BD;
	
	public Statement Requete;
	
	// Requete
	public PreparedStatement Requete_Identification;
	public PreparedStatement Requete_Elo_Aaaah;
	public PreparedStatement Requete_Elo_Bouboum;
	public PreparedStatement Requete_Elo_Forto;
	public PreparedStatement Requete_Precedent_Elo_Aaaah;
	public PreparedStatement Requete_Precedent_Elo_Bouboum;
	public PreparedStatement Requete_Precedent_Elo_Forto;
	public PreparedStatement Requete_EloTeam_Aaaah;
	public PreparedStatement Requete_EloTeam_Bouboum;
	public PreparedStatement Requete_EloTeam_Forto;
	public PreparedStatement Requete_GetId;
	public PreparedStatement Requete_GetTeam;
	public PreparedStatement Requete_NouveauCompte;
	public PreparedStatement Requete_ChargementJoueur;
	public PreparedStatement Requete_TransformationMODO;
	public PreparedStatement Requete_TransformationARB;
	public PreparedStatement Requete_TransformationFlag;
	public PreparedStatement Requete_VisualisationFlag;
	public PreparedStatement Requete_BloquerCompte;
	public PreparedStatement Requete_BloquerCompte2;
	public PreparedStatement Requete_Mutecp;
	public PreparedStatement Requete_Muteforum;
	public PreparedStatement Requete_ListeModo;
	
	// Classements
	public PreparedStatement Requete_ClassementBouboum;
	public PreparedStatement Requete_ClassementAaaah;
	public PreparedStatement Requete_ClassementAaaahCourse;
	
	public PreparedStatement Requete_NouveauClassementBouboum;
	public PreparedStatement Requete_NouveauClassementAaaah;
	
	public PreparedStatement Requete_ClassementEloA_RALLY_i; // Individuel
	public PreparedStatement Requete_ClassementEloA_DEF_i;
	public PreparedStatement Requete_ClassementEloA_MS_d; // Duo
	public PreparedStatement Requete_ClassementEloA_RALLY_d;
	public PreparedStatement Requete_ClassementEloA_DEF_d;
	public PreparedStatement Requete_ClassementEloA_MS_m; // Trio ou plus
	public PreparedStatement Requete_ClassementEloA_RALLY_m;
	public PreparedStatement Requete_ClassementEloA_DEF_m;
	public PreparedStatement Requete_ClassementEloA_FS_m;
	public PreparedStatement Requete_ClassementEloA_RUN_m;
	public PreparedStatement Requete_ClassementEloA_T_MS; // Team
	public PreparedStatement Requete_ClassementEloA_T_RALLY;
	public PreparedStatement Requete_ClassementEloA_T_DEF;
	public PreparedStatement Requete_ClassementEloA_T_FS;
	public PreparedStatement Requete_ClassementEloA_T_RUN;
	
	public PreparedStatement Requete_ClassementEloB_BOUM_i;
	public PreparedStatement Requete_ClassementEloB_BOUM_d;
	public PreparedStatement Requete_ClassementEloB_BOUM_m;
	public PreparedStatement Requete_ClassementEloB_T_BOUM;
	
	public PreparedStatement Requete_ClassementEloF_FRIGO_i;
	public PreparedStatement Requete_ClassementEloF_FRAG_i;
	public PreparedStatement Requete_ClassementEloF_KILL_i;
	public PreparedStatement Requete_ClassementEloF_CROSS_i;
	public PreparedStatement Requete_ClassementEloF_FRIGO_d;
	public PreparedStatement Requete_ClassementEloF_FRAG_d;
	public PreparedStatement Requete_ClassementEloF_KILL_d;
	public PreparedStatement Requete_ClassementEloF_FRIGO_m;
	public PreparedStatement Requete_ClassementEloF_FRAG_m;
	public PreparedStatement Requete_ClassementEloF_KILL_m;
	public PreparedStatement Requete_ClassementEloF_T_FRIGO;
	public PreparedStatement Requete_ClassementEloF_T_FRAG;
	public PreparedStatement Requete_ClassementEloF_T_KILL;
	
	//
	public PreparedStatement Requete_ListeTeam;
	public PreparedStatement Requete_ListeMembres;
	public PreparedStatement Requete_ListeAmis;
	public PreparedStatement Requete_SupprimerAmi;
	public PreparedStatement Requete_AjouterAmi;
	public PreparedStatement Requete_IdJoueur;
	public PreparedStatement Requete_IdJoueurEtBan;
	public PreparedStatement Requete_ChangerDescription;
	public PreparedStatement Requete_AjouterRecompense;
	public PreparedStatement Requete_AjouterRecompenseJoueur;
	public PreparedStatement Requete_SupprimerRecompenseJoueur;
	public PreparedStatement Requete_AjouterRecompenseJoueurFlag;
	public PreparedStatement Requete_SupprimerRecompenseJoueurFlag;
	public PreparedStatement Requete_SupprimerRecompenses;
	public PreparedStatement Requete_LogSanction;
	public PreparedStatement Requete_FinSanction;
	public PreparedStatement Requete_ModifierFinSanction;
	public PreparedStatement Requete_NomIpSanction;
	public PreparedStatement Requete_LogTeam;
	public PreparedStatement Requete_HistoTeam;
	public PreparedStatement Requete_HistoTeamJoueur;
	public PreparedStatement Requete_Chgpass;
	public PreparedStatement Requete_ListeChgpass;
	public PreparedStatement Requete_RetrouverJoueur;
	public PreparedStatement Requete_ListeMulti;
	public PreparedStatement Requete_ListeMultiPwd;
	public PreparedStatement Requete_ReloadSanctions;
	public PreparedStatement Requete_ReloadBanHost;
	public PreparedStatement Requete_ReloadBanIP;
	public PreparedStatement Requete_TempsJoueur;
	public PreparedStatement Requete_InfoJoueur;
	public PreparedStatement Requete_Autoriser_Grade_9;
	public PreparedStatement Requete_Autoriser_Grades_Speciaux;
	public PreparedStatement Requete_Ajouter_Grade10;
	public PreparedStatement Requete_Ajouter_Grade11;
	public PreparedStatement Requete_GetTitreMapFbyId;
	public PreparedStatement Requete_GetPseudobyId;
	
	// Forum
	public PreparedStatement Requete_NouveauSujet;
	public PreparedStatement Requete_NouveauMessage;
	public PreparedStatement Requete_UpSujet;
	public PreparedStatement Requete_IPMessage;
	public PreparedStatement Requete_ListeSujet;
	public PreparedStatement Requete_RechercheListeSujet;
	public PreparedStatement Requete_RechercheListeSujetParAuteur;
	public PreparedStatement Requete_EditionMessage;
	public PreparedStatement Requete_EditionTitre;
	public PreparedStatement Requete_EditionDescription;
	public PreparedStatement Requete_EditionMessageTeam;
	public PreparedStatement Requete_DeplacerSujet;
	public PreparedStatement Requete_FermetureSujet;
	public PreparedStatement Requete_QuestionSujetFerme;
	public PreparedStatement Requete_ListeMessagesSujet;
	public PreparedStatement Requete_SelectionNombreMessage;
	public PreparedStatement Requete_SelectionNombreSujet;

	public PreparedStatement Requete_ListeMapJoueur;
	public PreparedStatement Requete_RechercheMapById;
	public PreparedStatement Requete_GetRecordForto;
	public PreparedStatement Requete_UpdateRecordForto;
	public PreparedStatement Requete_CartesDunePlaylist;
	public PreparedStatement Requete_PlaylistpopulairPlus;
	public PreparedStatement Requete_PlaylistActualisationDernierLoad;
	public PreparedStatement Requete_DeletePlaylist;
	public PreparedStatement Requete_CreatePlaylist;
	public PreparedStatement Requete_CreatePlaylist_CodeUnique;
	public PreparedStatement Requete_CreatePlaylist_Nombre;
	public PreparedStatement Requete_RecherchePlaylist;
	public PreparedStatement Requete_RechercheMap;
	public PreparedStatement Requete_CreateMap;
	public PreparedStatement Requete_CreerRally;
	public PreparedStatement Requete_UpdateRally;
	public PreparedStatement Requete_MasseRefus;
	public PreparedStatement Requete_ChargerRally;
	public PreparedStatement Requete_ListeRally;
	public PreparedStatement Requete_DeleteRally;
	public PreparedStatement Requete_NouvelleFusion;
	public PreparedStatement Requete_UpdateFusion;
	public PreparedStatement Requete_QuitterFusion;
	public PreparedStatement Requete_SupprimerFusion;
	public PreparedStatement Requete_LoadFusions;
	public PreparedStatement Requete_UpdateMap;
	public PreparedStatement Requete_PermaMap;
	public PreparedStatement Requete_FlagMap;
	public PreparedStatement Requete_SupprVotesMap;
	public PreparedStatement Requete_SupprRecordMap;
	public PreparedStatement Requete_UpdateStatsMap;
	public PreparedStatement Requete_UpdateGhostMap;
	public PreparedStatement Requete_DeleteMap;
	public PreparedStatement Requete_SelectIgno;
	public PreparedStatement Requete_InsertIgno;
	public PreparedStatement Requete_CreateMapHis;
	public PreparedStatement Requete_DeleteMapHis;
	public PreparedStatement Requete_RetrieveMapHis;
	public PreparedStatement Requete_RetrieveMapHisAuteur;
	public PreparedStatement Requete_RetrieveSanctionById;
	public PreparedStatement Requete_DeleteSanctions;
	public PreparedStatement Requete_ArchivageSanction;
	
	public PreparedStatement Requete_MaraRetrieve;
	public PreparedStatement Requete_MaraSave;
	public PreparedStatement Requete_MaraBanniSave;
	public PreparedStatement Requete_MaraCreate;

	public PreparedStatement Requete_TeamLeaderRestant;
	public PreparedStatement Requete_TeamRecruteurRestant;
	public PreparedStatement Requete_TeamScribeRestant;
	public PreparedStatement Requete_TeamPromotionRecruteur;
	public PreparedStatement Requete_TeamPromotionScribe;
	public PreparedStatement Requete_TeamPromotionMembre;
	public PreparedStatement Requete_TeamNombreMembre;
	public PreparedStatement Requete_TeamSuppression;
	public PreparedStatement Requete_TeamJoueur;
	public PreparedStatement Requete_TeamEjecterJoueur;
	public PreparedStatement Requete_TeamPerteJoueur;
	public PreparedStatement Requete_TeamRejoindreJoueur;
	public PreparedStatement Requete_TeamNouveauMembre;
	public PreparedStatement Requete_TeamChangerGrade;
	public PreparedStatement Requete_TeamChangerRole;
	public PreparedStatement Requete_TeamEditerSite;
	public PreparedStatement Requete_TeamCreerCreateur;
	public PreparedStatement Requete_TeamCreerNom;
	public PreparedStatement Requete_TeamCreerInsert;
	public PreparedStatement Requete_TeamSupprimerMembres;
	public PreparedStatement Requete_TeamSupprimer;
	public PreparedStatement Requete_TeamInfo;
	public PreparedStatement Requete_TeamRecompenses;
	public PreparedStatement Requete_JoueurRecompenses;
	public PreparedStatement Requete_TeamSetBlason;
	public PreparedStatement Requete_TeamGetBlason;
	public PreparedStatement Requete_TeamInverserPartageTopic;
	public PreparedStatement Requete_TeamGetPartageTopic;
	public PreparedStatement Requete_TeamGetNom;

	public PreparedStatement Requete_ChangerPassword;
	public PreparedStatement Requete_ResetTemps;
	public PreparedStatement Requete_AutoriserChgpass;
	public PreparedStatement Requete_SelectChgpass;
	public PreparedStatement Requete_ValiderChgpass;
	public PreparedStatement Requete_RefuserChgpass;
	public PreparedStatement Requete_TraiterChgpass;
	public PreparedStatement Requete_CopiePwd;

	public PreparedStatement Requete_ForumPostit;
	public PreparedStatement Requete_ForumTopicAnim;
	public PreparedStatement Requete_SondageQuestions;
	public PreparedStatement Requete_SondageRéponses;
	public PreparedStatement Requete_ForumSuppressionDiscussion;
	public PreparedStatement Requete_ForumSuppressionTopic;
	public PreparedStatement Requete_ForumSuppressionMessageSelect;
	public PreparedStatement Requete_ForumSuppressionMessage;
	public PreparedStatement Requete_ForumSuppressionMessageDiscussion;
	public PreparedStatement Requete_ForumSuppressionMessageNbMsg;
	public PreparedStatement Requete_ForumSuppressionMessageDernierMsg;
	public PreparedStatement Requete_ForumSuppressionMessageDernierMsgTopic;
	public PreparedStatement Requete_ForumSupprimerTitreJoueur;
	public PreparedStatement Requete_ForumAjouterTitreJoueur;

	public PreparedStatement Requete_ChangerAvatar;
	public PreparedStatement Requete_SondageRéponse;
	public PreparedStatement Requete_SondageRépondre;
	public PreparedStatement Requete_SondageChangerVote;
	public PreparedStatement Requete_StopMaraboum;
	public PreparedStatement Requete_SauvegardeStatsJoueur;
	public PreparedStatement Requete_HistoSanctionJoueur;
	public PreparedStatement Requete_HistoSanctionLast;
	public PreparedStatement Requete_HistoSanctionLastForum;
	public PreparedStatement Requete_SupprimerAvatarJoueur;
	public PreparedStatement Requete_SupprimerAvatarMessages;
	public PreparedStatement Requete_SupprimerAvatarTopics;
	public PreparedStatement Requete_SupprimerDescriptionJoueur;

	public PreparedStatement Requete_ListeMPRecus;
	public PreparedStatement Requete_ListeMPEnvoyés;
	public PreparedStatement Requete_SupprimerMP;
	public PreparedStatement Requete_SupprimerMPById;
	public PreparedStatement Requete_NouveauMP;
	public PreparedStatement Requete_InfoJoueurMP;
	public PreparedStatement Requete_InfoDernierMP;
	public PreparedStatement Requete_NbMPDest;
	public PreparedStatement Requete_InfoNbDernierMP;
	public PreparedStatement Requete_SupprimerAnciensMP;

	// stats
	public PreparedStatement Requete_SelectStatsRun;
	public PreparedStatement Requete_SelectStatsDef;
	public PreparedStatement Requete_SelectStatsNg;
	public PreparedStatement Requete_SelectStatsRally;
	public PreparedStatement Requete_SelectStatsFs;
	public PreparedStatement Requete_SelectStatsMs;
	public PreparedStatement Requete_SelectStatsGdm;
	public PreparedStatement Requete_SelectStatsForto;
	public PreparedStatement Requete_SelectStatsBoum;

	public PreparedStatement Requete_NewStatsRun;
	public PreparedStatement Requete_NewStatsDef;
	public PreparedStatement Requete_NewStatsNg;
	public PreparedStatement Requete_NewStatsRally;
	public PreparedStatement Requete_NewStatsFs;
	public PreparedStatement Requete_NewStatsMs;
	public PreparedStatement Requete_NewStatsGdm;
	public PreparedStatement Requete_NewStatsForto;
	public PreparedStatement Requete_NewStatsBoum;

	public PreparedStatement Requete_UpdateStatsRun;
	public PreparedStatement Requete_UpdateStatsDef;
	public PreparedStatement Requete_UpdateStatsNg;
	public PreparedStatement Requete_UpdateStatsRally;
	public PreparedStatement Requete_UpdateStatsFs;
	public PreparedStatement Requete_UpdateStatsMs;
	public PreparedStatement Requete_UpdateStatsGdm;
	public PreparedStatement Requete_UpdateStatsForto;
	public PreparedStatement Requete_UpdateStatsBoum;

	public PreparedStatement Requete_ArchiveStatsRun;
	public PreparedStatement Requete_ArchiveStatsDef;
	public PreparedStatement Requete_ArchiveStatsNg;
	public PreparedStatement Requete_ArchiveStatsRally;
	public PreparedStatement Requete_ArchiveStatsFs;
	public PreparedStatement Requete_ArchiveStatsMs;
	public PreparedStatement Requete_ArchiveStatsGdm;
	public PreparedStatement Requete_ArchiveStatsForto;
	public PreparedStatement Requete_ArchiveStatsBoum;
	
	// Elo
	public PreparedStatement Requete_CreerEloAaaah;
	public PreparedStatement Requete_CreerEloBouboum;
	public PreparedStatement Requete_CreerEloForto;
	public PreparedStatement Requete_CreerEloTeamAaaah;
	public PreparedStatement Requete_CreerEloTeamBouboum;
	public PreparedStatement Requete_CreerEloTeamForto;
	//

	public ArrayList<PreparedStatement> Requete_Classements = new ArrayList<PreparedStatement>();
	
	// Donjon
	public PreparedStatement Requete_AddDonjon;
	public PreparedStatement Requete_LoadMapNivDonjon;
	public PreparedStatement Requete_ChargerDonjon;
	public PreparedStatement Requete_AddJoueurDonjon;
	public PreparedStatement Requete_SvgdAvancementDonjon;
	
	// Suppression de comptes inactifs : SCI
	public PreparedStatement Requete_GetComptesInactifsASuppr;
	public PreparedStatement Requete_SupprCompteByID;
	public PreparedStatement Requete_SCI_CleanListesAmis;
	//public PreparedStatement Requete_SCI_CleanListesNoires; un peu trop gourmand en ressource pour pas grand chose
	public PreparedStatement Requete_SCI_CleanPostsForum;
	public PreparedStatement Requete_SCI_CleanTopicsForum;
	public PreparedStatement Requete_SCI_CleanEloA;
	public PreparedStatement Requete_SCI_CleanEloB;
	public PreparedStatement Requete_SCI_CleanEloF;
	public PreparedStatement Requete_SCI_CleanHistoSanctions;
	public PreparedStatement Requete_SCI_CleanMessagerie;
	//public PreparedStatement Requete_SCI_CleanHistoTeams; // TODO à faire si jamais la limite des heures pour les actions teams diminue
	public PreparedStatement Requete_SCI_RemplaceAuteursMapsA;
	public PreparedStatement Requete_SCI_RemplaceRecordmanMapsA;
	public PreparedStatement Requete_SCI_RemplaceAuteursMapsF;
	public PreparedStatement Requete_SCI_RemplaceRecordmanMapsF;
	public PreparedStatement Requete_SCI_CleanAuteurPlaylist;
	public PreparedStatement Requete_SCI_DeleteDonjonJ;
	
	public PreparedStatement Requete_ImporterCompte;
	
	private boolean UtiliserSQLite = true;
	
	public BDD() {
		// Base de donnéee
		try {
			Driver Pilote = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(Pilote);
			
			initConnexion();
			initRequetes();
		} catch (Exception e) {
			System.err.print("3XC BDD");
			e.printStackTrace();
		}
	}
	
	protected abstract void initRequetes() throws SQLException;
	
	protected abstract void initConnexion() throws SQLException;
}
