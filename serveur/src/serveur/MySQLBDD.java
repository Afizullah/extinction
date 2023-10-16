package serveur;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLBDD extends BDD {
	protected void initConnexion() throws SQLException {		
		if (!Local.LOCAL) {
			// base distante
			InfoBD.setProperty("user", Local.BDD_USER);
			InfoBD.setProperty("password", Local.BDD_PASS);
			InfoBD.setProperty("autoReconnect", "true");
			
			if (Local.BETA) {
			     BD = DriverManager.getConnection(Local.BDD_BETA_NAME, InfoBD);   
			} else {
			     BD = DriverManager.getConnection(Local.BDD_NAME, InfoBD); //jdbc:mysql://213.251.135.103/mini_jeux     
			}
			
		} else {
			// base locale
			InfoBD.setProperty("user", Local.BDD_USER_LOCAL);
			InfoBD.setProperty("password", Local.BDD_PASS_LOCAL);
			InfoBD.setProperty("autoReconnect", "true");
			BD = DriverManager.getConnection(Local.BDD_NAME_LOCAL, InfoBD);
		}
	}
	
	@Override
	protected void initRequetes() throws SQLException {
		// Connexion
		//
		Requete = BD.createStatement();
		
		// Requete

		// (n, p, i, a, o_f, o_s, o_sg, o_b, o_c, o_ca, o_tf, o_tc, o_os, o_nl, o_nv, o_nc, o_ns, o_nomp, sb_pj, sb_pg, sb_t, sb_m, sb_r, s_ti, s_tt, s_dc, modo_igno, modo_muet, sa_pj, sa_pg, sa_js, sa_jg, sa_p, sa_pf, sa_t, sa_rs, sa_rp, sa_rt, sa_rv, sa_e, d, ip, chgpass, ln, ban, muteforum, mutecp, hs, s, ur, s_dcf, g9, flag_msg_anim, rcmp_act, elo_flag)
		Requete_NouveauCompte = BD.prepareStatement("INSERT INTO $joueur VALUES (0, ?, ?, '0', 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, UNIX_TIMESTAMP(), 0, UNIX_TIMESTAMP(), '', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', NULL, 1, '', 0, 0, 0,NULL,'',0,NULL,0,0,1,'000')");
		//Requete_NouveauCompte = BD.prepareStatement("INSERT INTO $joueur VALUES (0, ?, ?, '0', 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, UNIX_TIMESTAMP(), 0, UNIX_TIMESTAMP(), '', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, '', 0, 0, 0,NULL,'',0,NULL,0,'0',0,1,'000',?)");
		Requete_Identification = BD.prepareStatement("SELECT p FROM $joueur WHERE n=? LIMIT 1");
		
		Requete_Elo_Aaaah = BD.prepareStatement("SELECT * FROM $elo_a WHERE id=?");
		Requete_Elo_Bouboum = BD.prepareStatement("SELECT * FROM $elo_b WHERE id=?");
		Requete_Elo_Forto = BD.prepareStatement("SELECT * FROM $elo_f WHERE id=?");
		
		Requete_Precedent_Elo_Aaaah = BD.prepareStatement("SELECT * FROM $stat_elo_a WHERE id=?");
		Requete_Precedent_Elo_Bouboum = BD.prepareStatement("SELECT * FROM $stat_elo_b WHERE id=?");
		Requete_Precedent_Elo_Forto = BD.prepareStatement("SELECT * FROM $stat_elo_f WHERE id=?");
		
		Requete_EloTeam_Aaaah = BD.prepareStatement("SELECT * FROM $elo_t_a WHERE id=?");
		Requete_EloTeam_Bouboum = BD.prepareStatement("SELECT * FROM $elo_t_b WHERE id=?");
		Requete_EloTeam_Forto = BD.prepareStatement("SELECT * FROM $elo_t_f WHERE id=?");
		
		Requete_GetId = BD.prepareStatement("SELECT id FROM $joueur WHERE n=? LIMIT 1");
		Requete_GetTeam = BD.prepareStatement("SELECT id_team FROM $membres WHERE id_joueur = ?");
		Requete_ChargementJoueur = BD.prepareStatement("SELECT j.*,m.*,t.nom,t.jeu,t.message,o.*,op.*"
													   + " FROM $joueur j LEFT JOIN $membres m ON j.id=m.id_joueur"
													   + " LEFT JOIN $teams t ON t.id = m.id_team"
													   + " LEFT JOIN $old_stats_j o ON o.id = j.id"
													   + " LEFT JOIN $old_parties op ON op.id = j.id"
													   + " WHERE j.n=? LIMIT 1");
		
		Requete_TransformationMODO = BD.prepareStatement("UPDATE $joueur SET a=5 WHERE n=? LIMIT 1");
		Requete_TransformationARB = BD.prepareStatement("UPDATE $joueur SET a=2 WHERE n=? LIMIT 1");
		Requete_TransformationFlag = BD.prepareStatement("UPDATE $joueur SET a=? WHERE n=? LIMIT 1");
		Requete_VisualisationFlag = BD.prepareStatement("SELECT a FROM $joueur where n=? LIMIT 1");
		Requete_BloquerCompte = BD.prepareStatement("UPDATE $joueur SET ban=1 WHERE n=? LIMIT 1");
		Requete_BloquerCompte2 = BD.prepareStatement("UPDATE $joueur SET ban=2 WHERE n=? LIMIT 1");
		Requete_Muteforum = BD.prepareStatement("UPDATE $joueur SET muteforum=1 WHERE n=? LIMIT 1");
		Requete_Mutecp = BD.prepareStatement("UPDATE $joueur SET mutecp=1 WHERE n=? LIMIT 1");
		Requete_ListeModo = BD.prepareStatement("SELECT n,a FROM $joueur WHERE a=5 or a=2 or a=8 or a=6 order by n asc");
		
		/// Classements
		Requete_ClassementBouboum = BD.prepareStatement("SELECT j.n,IF(o.sb_opj is NULL, j.sb_pj, j.sb_pj + o.sb_opj), IF(o.sb_victoire is NULL, j.sb_pg, j.sb_pg + o.sb_victoire),j.sb_r"
														+ " FROM $joueur j LEFT JOIN $old_stats_j o ON j.id=o.id"
														+ " WHERE IF(o.sb_opj is NULL, j.sb_pj, j.sb_pj + o.sb_opj)>20"
														+ " ORDER BY IF(o.sb_victoire is NULL, j.sb_pg, j.sb_pg + o.sb_victoire) DESC LIMIT 25");
		
		Requete_ClassementAaaah = BD.prepareStatement("SELECT n,sa_pj,sa_pg,sa_p,sa_pf,sa_rs,sa_t FROM $joueur WHERE sa_pg>=400 and UNIX_TIMESTAMP()-s_dc<=2678400 ORDER BY sa_rs DESC LIMIT 25");
		Requete_ClassementAaaahCourse = BD.prepareStatement("SELECT j.n,j.sa_pj,IF(o.sa_guidage is NULL, j.sa_pg, j.sa_pg + o.sa_guidage),IF(o.sa_victoire is NULL, j.sa_p, j.sa_p + o.sa_victoire),j.sa_pf,IF(o.sa_pguidage is NULL, 0, o.sa_pguidage),j.sa_t"
															+ " FROM $joueur j LEFT JOIN $old_stats_j o ON j.id=o.id"
															+ " WHERE j.sa_pj>=1000"
															+ " ORDER BY IF(o.sa_victoire is NULL, j.sa_p, j.sa_p + o.sa_victoire) DESC LIMIT 25");
		Requete_NouveauClassementAaaah = BD.prepareStatement("SELECT n,sa_pj,sa_pg,sa_p,sa_pf,sa_rs,sa_t FROM $joueur WHERE sa_pj>=20 ORDER BY sa_p DESC LIMIT 25");
		Requete_NouveauClassementBouboum = BD.prepareStatement("SELECT n,sb_pj,sb_pg,sb_r FROM $joueur WHERE sb_pj>20 ORDER BY sb_pg DESC LIMIT 25");
		
		Requete_ClassementEloA_RALLY_i = BD.prepareStatement("SELECT j.n, e.ra_ie FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.ra_ipj > 0 ORDER BY e.ra_ie DESC");
		Requete_ClassementEloA_DEF_i = BD.prepareStatement("SELECT j.n, e.def_ie FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.def_ipj > 0 ORDER BY e.def_ie DESC");
		
		Requete_ClassementEloA_RALLY_d = BD.prepareStatement("SELECT j.n, e.ra_de FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.ra_dpj > 0 ORDER BY e.ra_de DESC");// Duo
		Requete_ClassementEloA_DEF_d = BD.prepareStatement("SELECT j.n, e.def_de FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.def_dpj > 0 ORDER BY e.def_de DESC");
		Requete_ClassementEloA_MS_d = BD.prepareStatement("SELECT j.n, e.ms_de FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.ms_dpj > 0 ORDER BY e.ms_de DESC");
		
		Requete_ClassementEloA_RALLY_m = BD.prepareStatement("SELECT j.n, e.ra_me FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.ra_mpj > 0 ORDER BY e.ra_me DESC");
		Requete_ClassementEloA_DEF_m = BD.prepareStatement("SELECT j.n, e.def_me FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.def_mpj > 0 ORDER BY e.def_me DESC");
		Requete_ClassementEloA_MS_m = BD.prepareStatement("SELECT j.n, e.ms_me FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.ms_mpj > 0 ORDER BY e.ms_me DESC");
		Requete_ClassementEloA_FS_m = BD.prepareStatement("SELECT j.n, e.fs_me FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.fs_mpj > 0 ORDER BY e.fs_me DESC");
		Requete_ClassementEloA_RUN_m = BD.prepareStatement("SELECT j.n, e.run_me FROM $elo_a e LEFT JOIN $joueur j ON e.id = j.id WHERE e.run_mpj > 0 ORDER BY e.run_me DESC");
		
		Requete_ClassementEloA_T_RALLY = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.ra_e FROM $elo_t_a e LEFT JOIN $teams t ON e.id=t.id WHERE e.ra_pj > 0 ORDER BY e.ra_e DESC");
		Requete_ClassementEloA_T_DEF = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.def_e FROM $elo_t_a e LEFT JOIN $teams t ON e.id=t.id WHERE e.def_pj > 0 ORDER BY e.def_e DESC");
		Requete_ClassementEloA_T_MS = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.ms_e FROM $elo_t_a e LEFT JOIN $teams t ON e.id=t.id WHERE e.ms_pj > 0 ORDER BY e.ms_e DESC");
		Requete_ClassementEloA_T_FS = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.fs_e FROM $elo_t_a e LEFT JOIN $teams t ON e.id=t.id WHERE e.fs_pj > 0 ORDER BY e.fs_e DESC");
		Requete_ClassementEloA_T_RUN = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.run_e FROM $elo_t_a e LEFT JOIN $teams t ON e.id=t.id WHERE e.run_pj > 0 ORDER BY e.run_e DESC");
		
		// Bouboum
		Requete_ClassementEloB_BOUM_i = BD.prepareStatement("SELECT j.n, e.k_ie FROM $elo_b e LEFT JOIN $joueur j ON e.id = j.id WHERE e.k_ipj > 0 ORDER BY e.k_ie DESC");
		
		Requete_ClassementEloB_BOUM_d = BD.prepareStatement("SELECT j.n, e.k_de FROM $elo_b e LEFT JOIN $joueur j ON e.id = j.id WHERE e.k_dpj > 0 ORDER BY e.k_de DESC");
		
		Requete_ClassementEloB_BOUM_m = BD.prepareStatement("SELECT j.n, e.k_me FROM $elo_b e LEFT JOIN $joueur j ON e.id = j.id WHERE e.k_mpj > 0 ORDER BY e.k_me DESC");
		
		Requete_ClassementEloB_T_BOUM = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.k_e FROM $elo_t_b e LEFT JOIN $teams t ON e.id=t.id WHERE e.k_pj > 0 ORDER BY e.k_e DESC");
		
		// Forto
		Requete_ClassementEloF_FRIGO_i = BD.prepareStatement("SELECT j.n, e.fri_ie FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.fri_ipj > 0 ORDER BY e.fri_ie DESC");
		Requete_ClassementEloF_FRAG_i = BD.prepareStatement("SELECT j.n, e.fra_ie FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.fra_ipj > 0 ORDER BY e.fra_ie DESC");
		Requete_ClassementEloF_KILL_i = BD.prepareStatement("SELECT j.n, e.k_ie FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.k_ipj > 0 ORDER BY e.k_ie DESC");
		Requete_ClassementEloF_CROSS_i = BD.prepareStatement("SELECT j.n, e.crs_ie FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.crs_ie<=? && e.crs_ipj > 0 ORDER BY e.crs_ie DESC");
		
		Requete_ClassementEloF_FRIGO_d = BD.prepareStatement("SELECT j.n, e.fri_de FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.fri_dpj > 0 ORDER BY e.fri_de DESC");
		Requete_ClassementEloF_FRAG_d = BD.prepareStatement("SELECT j.n, e.fra_de FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.fra_dpj > 0 ORDER BY e.fra_de DESC");
		Requete_ClassementEloF_KILL_d = BD.prepareStatement("SELECT j.n, e.k_de FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.k_dpj > 0 ORDER BY e.k_de DESC");
		
		Requete_ClassementEloF_FRIGO_m = BD.prepareStatement("SELECT j.n, e.fri_me FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.fri_mpj > 0 ORDER BY e.fri_me DESC");
		Requete_ClassementEloF_FRAG_m = BD.prepareStatement("SELECT j.n, e.fra_me FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.fra_mpj > 0 ORDER BY e.fra_me DESC");
		Requete_ClassementEloF_KILL_m = BD.prepareStatement("SELECT j.n, e.k_me FROM $elo_f e LEFT JOIN $joueur j ON e.id = j.id WHERE e.k_mpj > 0 ORDER BY e.k_me DESC");
		
		Requete_ClassementEloF_T_FRIGO = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.fri_e FROM $elo_t_f e LEFT JOIN $teams t ON e.id=t.id WHERE e.fri_pj > 0 ORDER BY e.fri_e DESC");
		Requete_ClassementEloF_T_FRAG = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.fra_e FROM $elo_t_f e LEFT JOIN $teams t ON e.id=t.id WHERE e.k_pj > 0 ORDER BY e.fra_e DESC");
		Requete_ClassementEloF_T_KILL = BD.prepareStatement("SELECT if(t.nom is NULL, e.id, t.nom), e.k_e FROM $elo_t_f e LEFT JOIN $teams t ON e.id=t.id WHERE e.k_pj > 0 ORDER BY e.k_e DESC");
		
		///
		
		Requete_ListeTeam = BD.prepareStatement("SELECT * FROM $teams where membres >= 4 ORDER BY score DESC");
		Requete_ListeMembres = BD.prepareStatement("SELECT m.id_joueur,j.n,m.grade,m.role FROM $membres m JOIN $joueur j ON m.id_joueur=j.id WHERE m.id_team = ? ORDER BY m.grade desc, j.n asc");
		Requete_ListeAmis = BD.prepareStatement("SELECT a.id_joueur,a.id_ami,j.n FROM $amis a,$joueur j WHERE a.id_joueur=? and j.id=a.id_ami");
		Requete_SupprimerAmi = BD.prepareStatement("DELETE FROM $amis WHERE id_joueur=? and id_ami=?");
		Requete_AjouterAmi = BD.prepareStatement("INSERT INTO $amis (id_joueur,id_ami) VALUES (?,?)");
		Requete_IdJoueur = BD.prepareStatement("SELECT id FROM $joueur WHERE n=?");
		Requete_IdJoueurEtBan = BD.prepareStatement("SELECT id, ban FROM $joueur WHERE n=?");
		Requete_AjouterRecompense = BD.prepareStatement("INSERT INTO $recompenses (id_team,image,message) VALUES (?,?,?)");
		Requete_AjouterRecompenseJoueur = BD.prepareStatement("INSERT INTO $joueur_r (n,image,message) VALUES (?,?,?)");
		Requete_SupprimerRecompenseJoueur = BD.prepareStatement("delete from $joueur_r where n=? and id = ( SELECT tmp.id FROM ( SELECT r.id FROM $joueur_r AS r where n=? order by id asc limit ?,1) tmp)");
		Requete_AjouterRecompenseJoueurFlag = BD.prepareStatement("UPDATE $joueur set ur = 1 WHERE n=?");
		Requete_SupprimerRecompenseJoueurFlag = BD.prepareStatement("UPDATE $joueur set ur = 0 WHERE n=? and (select count(*) as c from $joueur_r where n=?)=0");
		Requete_SupprimerRecompenses = BD.prepareStatement("DELETE FROM $recompenses where id_team = ?");
		Requete_LogSanction = BD.prepareStatement("INSERT INTO $sanctions (nom_joueur,nom_modo,ip,sanction,temps,d,raison,fin) VALUES (?,?,?,?,?,NOW(),?,UNIX_TIMESTAMP()+?*3600)");
		Requete_FinSanction = BD.prepareStatement("SELECT nom_joueur,temps,fin FROM $sanctions WHERE id=?");
		Requete_ModifierFinSanction = BD.prepareStatement("UPDATE $sanctions SET fin = ? WHERE id=?");
		Requete_NomIpSanction = BD.prepareStatement("SELECT nom_joueur,ip,sanction,fin FROM $sanctions WHERE id=?");
		Requete_LogTeam = BD.prepareStatement("INSERT INTO $teams_his (id_team,auteur,ip,cible,action,date) VALUES (?,?,?,?,?,NOW())");
		Requete_HistoTeam = BD.prepareStatement("SELECT id_team, auteur, cible, action, date from $teams_his where id_team = ? order by id desc");
		Requete_HistoTeamJoueur = BD.prepareStatement("SELECT h.id_team, t.nom, h.auteur, h.cible, h.action, h.date from $teams_his h left join $teams t on t.id=h.id_team where (h.cible = ? and h.action in ('E','R') ) or (h.auteur = ? and h.action in ('C','D','Q')) order by h.id desc LIMIT 100");
		Requete_Chgpass = BD.prepareStatement("INSERT INTO $chgpass (nom,oldp,newp,ip,message,temps) VALUES (?,?,?,?,?,NOW())");
		Requete_ListeChgpass = BD.prepareStatement("SELECT nom,ip,message FROM $chgpass WHERE etat=0 ORDER by ID ASC");
		Requete_RetrouverJoueur = BD.prepareStatement("SELECT p,ip FROM $joueur WHERE n=?");
		Requete_ListeMulti = BD.prepareStatement("SELECT n FROM $joueur WHERE ip=? ORDER by s_dc desc LIMIT 50");
		Requete_ListeMultiPwd = BD.prepareStatement("SELECT n FROM $joueur WHERE p=? ORDER by s_dc desc LIMIT 50");
		Requete_ReloadSanctions = BD.prepareStatement("SELECT * FROM $sanctions WHERE id > 67000 and temps>=1 and fin is not null and fin>UNIX_TIMESTAMP() ORDER BY id ASC");
		Requete_ReloadBanHost = BD.prepareStatement("SELECT * FROM $ban_host WHERE actif=1 ORDER BY id ASC");
		Requete_ReloadBanIP = BD.prepareStatement("SELECT * FROM $ban_ip WHERE actif=1 ORDER BY id ASC");
		Requete_TempsJoueur = BD.prepareStatement("SELECT s_tt FROM $joueur WHERE n= ? LIMIT 1");
		Requete_InfoJoueur = BD.prepareStatement("SELECT s_dc,s_tt,ban FROM $joueur WHERE n= ? LIMIT 1");
		Requete_Autoriser_Grade_9 = BD.prepareStatement("UPDATE $joueur set g9=1 WHERE id= ? LIMIT 1");
		Requete_Autoriser_Grades_Speciaux = BD.prepareStatement("SELECT * FROM $grades_j WHERE id=?");
		Requete_Ajouter_Grade10 = BD.prepareStatement("INSERT INTO $grades_j (id, g10, g11) VALUES (?, 1, 0) ON DUPLICATE KEY UPDATE g10=1");
		Requete_Ajouter_Grade11 = BD.prepareStatement("INSERT INTO $grades_j (id, g10, g11) VALUES (?, 0, 1) ON DUPLICATE KEY UPDATE g11=1");
	 ///
		Requete_GetTitreMapFbyId = BD.prepareStatement("SELECT titre FROM $maps_forto WHERE id=?");
		Requete_GetPseudobyId = BD.prepareStatement("SELECT n FROM $joueur WHERE id=?");
		
		// Forum
		Requete_NouveauSujet = BD.prepareStatement("INSERT INTO $forum_s VALUES (0, ?, UNIX_TIMESTAMP(), ?, ?, ?, UNIX_TIMESTAMP(), 0, ?, 0,0,NULL,0,?)", Statement.RETURN_GENERATED_KEYS);
		Requete_NouveauMessage = BD.prepareStatement("INSERT INTO $forum_m VALUES (0, UNIX_TIMESTAMP(), ?, ?, ?, ?,?)");
		//Requete_NouveauSujet = BD.prepareStatement("INSERT INTO $forum_s VALUES (0, ?, UNIX_TIMESTAMP(), ?, ?, ?, UNIX_TIMESTAMP(), 0, ?, 0,0,NULL,0,?,?)");
		//Requete_NouveauMessage = BD.prepareStatement("INSERT INTO $forum_m VALUES (0, UNIX_TIMESTAMP(), ?, ?, ?, ?,?,?)");
		Requete_IPMessage = BD.prepareStatement("SELECT ip,a FROM $forum_m WHERE id=?");
		Requete_UpSujet = BD.prepareStatement("UPDATE $forum_s SET d=UNIX_TIMESTAMP(), m=m+1, p=? WHERE id=? LIMIT 1");
		Requete_ListeSujet = BD.prepareStatement("SELECT id,d,a,i,t,c,m,p,x,xm,xf,pi,an FROM $forum_s WHERE f=? ORDER BY pi DESC, d DESC LIMIT ?,50");
		Requete_RechercheListeSujet = BD.prepareStatement("SELECT id,d,a,i,t,c,m,p,x,xm,xf,pi,an FROM $forum_s WHERE f=? and t like ? ORDER BY pi DESC, d DESC LIMIT ?,50");
		Requete_RechercheListeSujetParAuteur = BD.prepareStatement("SELECT id,d,a,i,t,c,m,p,x,xm,xf,pi,an FROM $forum_s WHERE f=? and a=? ORDER BY pi DESC, d DESC LIMIT ?,50");
		Requete_ChangerDescription = BD.prepareStatement("UPDATE $joueur SET d=? WHERE id=? LIMIT 1");
		Requete_EditionMessage = BD.prepareStatement("UPDATE $forum_m SET m=? WHERE id=? AND s=? LIMIT 1");
		Requete_EditionTitre = BD.prepareStatement("UPDATE $forum_s SET t=? WHERE id=? LIMIT 1");
		Requete_EditionDescription = BD.prepareStatement("UPDATE $teams SET description=? WHERE id = ? LIMIT 1");
		Requete_EditionMessageTeam = BD.prepareStatement("UPDATE $teams SET message=? WHERE id = ? LIMIT 1");
		Requete_DeplacerSujet = BD.prepareStatement("UPDATE $forum_s SET f=? WHERE id=? LIMIT 1");
		Requete_FermetureSujet = BD.prepareStatement("UPDATE $forum_s SET x=?,xm=?,xf=? WHERE id=? LIMIT 1");
		Requete_QuestionSujetFerme = BD.prepareStatement("SELECT x,f,t FROM $forum_s WHERE id=? LIMIT 1");
		Requete_ListeMessagesSujet = BD.prepareStatement("SELECT m.id,m.d,m.a,m.i,m.m,d.d,d.c FROM $forum_m m LEFT JOIN $forum_d d ON m.a=d.n WHERE m.s=? ORDER BY m.d LIMIT ?,?");
		Requete_SelectionNombreMessage = BD.prepareStatement("SELECT m,f,t FROM $forum_s WHERE id=? LIMIT 1");
		Requete_SelectionNombreSujet = BD.prepareStatement("SELECT count(*) FROM $forum_s WHERE f=? LIMIT 1");
		//
		Requete_ListeMapJoueur = BD.prepareStatement("SELECT * FROM $maps WHERE auteur=? ORDER BY id desc LIMIT 1000 ");
		Requete_RechercheMapById = BD.prepareStatement("SELECT * FROM $maps WHERE id=? LIMIT 1 ");
		Requete_GetRecordForto = BD.prepareStatement("SELECT record, recordman, mode FROM $maps_forto WHERE id=?");
		Requete_UpdateRecordForto = BD.prepareStatement("UPDATE $maps_forto SET record=?, recordman=? WHERE id=?");
		Requete_CartesDunePlaylist = BD.prepareStatement("SELECT cartes FROM $playlist WHERE id_joueur = ? AND code = ?");
		Requete_PlaylistpopulairPlus = BD.prepareStatement("UPDATE $playlist SET populair = populair +? WHERE id_joueur = ? AND code = ?"); 
		Requete_PlaylistActualisationDernierLoad = BD.prepareStatement("UPDATE $playlist SET dernierLoad = CURRENT_TIMESTAMP WHERE id_joueur = ? AND code = ?");
		Requete_DeletePlaylist = BD.prepareStatement("DELETE FROM $playlist WHERE id_joueur = ? AND code = ?");
		Requete_CreatePlaylist = BD.prepareStatement("INSERT INTO $playlist(id_joueur, cartes, code, titre) VALUES(?,?,?,?)");
		Requete_CreatePlaylist_CodeUnique = BD.prepareStatement("SELECT count(*) as count FROM $playlist WHERE id_joueur = ? AND code = ?");
		Requete_CreatePlaylist_Nombre = BD.prepareStatement("SELECT count(*) as count FROM $playlist WHERE id_joueur = ?");
		Requete_RecherchePlaylist = BD.prepareStatement("SELECT id_joueur, j.n as auteur, code, titre, populair, featured FROM $playlist AS p LEFT JOIN $joueur AS j ON p.id_joueur = j.id WHERE j.n LIKE ? OR code LIKE ? OR titre LIKE ? LIMIT 1000");
		Requete_RechercheMap = BD.prepareStatement("SELECT * FROM $maps WHERE titre like ? ORDER by id desc LIMIT 1000");
		Requete_CreateMap = BD.prepareStatement("INSERT INTO $maps (id,code,titre,auteur,mode) VALUES (?,?,?,?,?)");
		Requete_UpdateMap = BD.prepareStatement("UPDATE $maps set code = ?, titre = ?, mode = ? where id=? limit 1");
		Requete_PermaMap = BD.prepareStatement("UPDATE $maps set perma=? where id=? limit 1");
		Requete_FlagMap = BD.prepareStatement("UPDATE $maps set mode=? where id=? limit 1");
		Requete_SupprVotesMap = BD.prepareStatement("UPDATE $maps set votes=0,pour=0 where id=? limit 1");
		Requete_SupprRecordMap = BD.prepareStatement("UPDATE $maps set record=120000,ghost='',recordman='-',mara_record=120000,mara_recordman='-' where id=? limit 1");
		Requete_UpdateStatsMap = BD.prepareStatement("UPDATE $maps set votes=?, pour=?, record=?, recordman=?, played=?, survie=?, mara_record=?, mara_recordman=?, mara_played=? where id=? limit 1");
		Requete_UpdateGhostMap = BD.prepareStatement("UPDATE $maps set ghost=? where id=? limit 1");
		Requete_DeleteMap = BD.prepareStatement("DELETE FROM $maps where id=? limit 1");
		Requete_CreateMapHis = BD.prepareStatement("INSERT INTO $maps_his (id,code,titre,auteur,respomap,type) VALUES (?,?,?,?,?,?)");
		Requete_DeleteMapHis = BD.prepareStatement("DELETE FROM $maps_his where id=? limit 1");
		Requete_RetrieveMapHis = BD.prepareStatement("SELECT * FROM $maps_his where id=? limit 1");
		Requete_RetrieveMapHisAuteur = BD.prepareStatement("SELECT * FROM $maps_his where auteur=?");
		Requete_RetrieveSanctionById = BD.prepareStatement("SELECT * FROM $sanctions where id=?");
		Requete_DeleteSanctions = BD.prepareStatement("DELETE FROM $sanctions where id=? limit 1");
		Requete_ArchivageSanction = BD.prepareStatement("UPDATE $sanctions set nom_joueur=CONCAT('_',nom_joueur), ip='_' where nom_joueur=? and (temps IS NULL or temps < 100) and (fin < ? || fin is NULL)");
		/// TODO : corriger
		Requete_CreerRally = BD.prepareStatement("INSERT INTO $maps_forto (id,auteur,titre,code,couleur,niveau,mode,note,status,couleur_perso,infos_monde) VALUES (?,?,?,COMPRESS(?),?,?,?,?,?,?,?)");
		Requete_UpdateRally = BD.prepareStatement("UPDATE $maps_forto SET titre=?,code=COMPRESS(?),couleur=?,niveau=?,mode=?,note=?,status=?,record=?,recordman=?,couleur_perso=?,infos_monde=? WHERE id=? LIMIT 1");
		Requete_MasseRefus = BD.prepareStatement("UPDATE $maps_forto SET status=2 WHERE auteur=? && status=0");
		
		Requete_ChargerRally = BD.prepareStatement("SELECT id,auteur,titre,UNCOMPRESS(code) as code,couleur,niveau,mode,note,status,record,recordman,couleur_perso,infos_monde FROM $maps_forto where id = ?");
		Requete_ListeRally = BD.prepareStatement("SELECT id,auteur,titre,niveau,mode,note,status,record,recordman FROM $maps_forto ORDER by id desc");
		Requete_DeleteRally = BD.prepareStatement("UPDATE $maps_forto SET status=3 WHERE status=2");

		
		Requete_MaraRetrieve = BD.prepareStatement("SELECT * FROM mara where n=? limit 1");
		//Requete_MaraSave = BD.prepareStatement("UPDATE mara SET tb=?, tj=?, js=?, jg=?, pg=?, p=?, pj=?, tf=?, sf=?, pjf=?, td=?, sd=?, pjd=?, tt=?, pjn=? WHERE n=? LIMIT 1");
		Requete_MaraSave = BD.prepareStatement("UPDATE mara SET tb=?, tj=?, pj=?, t=?, pg=?, a=?, s=?, m=? WHERE n=? LIMIT 1");
		Requete_MaraBanniSave = BD.prepareStatement("UPDATE mara SET tb=?, tj=? WHERE n=? LIMIT 1");
		Requete_MaraCreate = BD.prepareStatement("INSERT INTO mara(n) VALUES (?)");

		Requete_SelectIgno = BD.prepareStatement("select n,modo_igno from $joueur where modo_igno!=''");
		Requete_InsertIgno = BD.prepareStatement("update $joueur set ln=? where n=?");

		
		Requete_TeamRecruteurRestant = BD.prepareStatement("SELECT count(role) FROM $membres WHERE id_team = ? && role = 1");
		Requete_TeamScribeRestant = BD.prepareStatement("SELECT count(role) FROM $membres WHERE id_team = ? && role = 3");
		Requete_TeamLeaderRestant = BD.prepareStatement("SELECT count(role) FROM $membres WHERE id_team = ? && role = 2");

		//Requete_TeamLeaderRestant = BD.prepareStatement("SELECT role,count(*) AS c FROM $membres WHERE id_team = ? GROUP BY role ORDER BY role desc");
		Requete_TeamPromotionRecruteur = BD.prepareStatement("UPDATE $membres SET role = 2 WHERE role=1 AND id_team = ?");
		Requete_TeamPromotionScribe = BD.prepareStatement("UPDATE $membres SET role = 2 WHERE role = 3 && id_team = ?");
		Requete_TeamPromotionMembre = BD.prepareStatement("UPDATE $membres SET role = 2 WHERE id_team = ? LIMIT 2");
		Requete_TeamNombreMembre = BD.prepareStatement("SELECT COUNT(id_joueur) FROM $membres WHERE id_team = ?");
		Requete_TeamSuppression = BD.prepareStatement("DELETE FROM $teams WHERE id=? LIMIT 1");
		Requete_TeamJoueur = BD.prepareStatement("SELECT id_team,role FROM $membres WHERE id_joueur = ?");
		Requete_TeamEjecterJoueur = BD.prepareStatement("DELETE FROM $membres WHERE id_joueur=? LIMIT 1");
		Requete_TeamPerteJoueur = BD.prepareStatement("UPDATE $teams SET modif=NOW(),membres=membres-1 WHERE id = ? LIMIT 1");
		Requete_TeamRejoindreJoueur = BD.prepareStatement("INSERT INTO $membres VALUES (?,?,0,?)");
		Requete_TeamNouveauMembre = BD.prepareStatement("UPDATE $teams SET modif=NOW(),membres=membres+1 WHERE id = ? LIMIT 1");
		Requete_TeamChangerGrade = BD.prepareStatement("UPDATE $membres SET grade = ? WHERE id_joueur = ? LIMIT 1");
		Requete_TeamChangerRole = BD.prepareStatement("UPDATE $membres SET role = ? WHERE id_joueur = ? LIMIT 1");
		Requete_TeamEditerSite = BD.prepareStatement("UPDATE $teams SET site=? WHERE id = ? LIMIT 1");
		Requete_TeamCreerCreateur = BD.prepareStatement("SELECT id FROM $teams_his WHERE action='C' and auteur = ? and UNIX_TIMESTAMP()-UNIX_TIMESTAMP(date)<2592000 LIMIT 1");
		Requete_TeamCreerNom = BD.prepareStatement("SELECT id FROM $teams WHERE nom = ? LIMIT 1");
		Requete_TeamCreerInsert = BD.prepareStatement("INSERT INTO $teams (nom,tag,description,site,modif,creation,fondateur,jeu,membres,officiel,score) VALUES (?,?,?,?,NOW(),FROM_UNIXTIME(?),?,?,0,NOW(),0)");
		Requete_TeamSupprimerMembres = BD.prepareStatement("DELETE FROM $membres WHERE id_team =?");
		Requete_TeamSupprimer = BD.prepareStatement("DELETE FROM $teams WHERE id=? LIMIT 1");
		Requete_TeamInfo = BD.prepareStatement("SELECT nom,tag,description,site,UNIX_TIMESTAMP(creation) as creation,UNIX_TIMESTAMP(officiel) as officiel,UNIX_TIMESTAMP(modif) as modif,fondateur,jeu,id,message FROM $teams WHERE id=? LIMIT 1");
		Requete_TeamRecompenses = BD.prepareStatement("SELECT * FROM $recompenses WHERE id_team=? ORDER BY date ASC");
		Requete_JoueurRecompenses = BD.prepareStatement("SELECT * FROM $joueur_r WHERE n=? ORDER BY id ASC");
		Requete_TeamSetBlason = BD.prepareStatement("UPDATE $teams SET blason=? WHERE id=?");
		Requete_TeamGetBlason = BD.prepareStatement("SELECT blason FROM $teams WHERE id=?");	
		Requete_TeamInverserPartageTopic = BD.prepareStatement("UPDATE $teams SET partage_topic = NOT partage_topic WHERE id=?");
		Requete_TeamGetPartageTopic = BD.prepareStatement("SELECT partage_topic FROM $teams WHERE id=?");
		Requete_TeamGetNom = BD.prepareStatement("SELECT nom FROM $teams WHERE ID=?");

		Requete_ChangerPassword = BD.prepareStatement("UPDATE $joueur SET p=? WHERE n = ? LIMIT 1");
		Requete_ResetTemps = BD.prepareStatement("UPDATE $joueur SET s_tt=s_tt - ? WHERE n = ? LIMIT 1");
		Requete_AutoriserChgpass = BD.prepareStatement("UPDATE $joueur SET chgpass=? WHERE n = ? LIMIT 1");
		Requete_SelectChgpass = BD.prepareStatement("SELECT newp FROM $chgpass WHERE nom=? and etat=0 ORDER BY id ASC LIMIT 1");
		Requete_ValiderChgpass = BD.prepareStatement("UPDATE $joueur SET chgpass='0',p=? WHERE n = ? LIMIT 1");
		Requete_RefuserChgpass = BD.prepareStatement("UPDATE $joueur SET chgpass='0' WHERE n = ? LIMIT 1");
		Requete_TraiterChgpass = BD.prepareStatement("UPDATE $chgpass SET etat=? WHERE nom = ? and etat=0 ORDER BY id ASC LIMIT 1");
		Requete_CopiePwd = BD.prepareStatement("SELECT p FROM $joueur WHERE n=? LIMIT 1");

		Requete_ForumPostit = BD.prepareStatement("UPDATE $forum_s SET pi=NOT pi WHERE id = ? LIMIT 1");
		Requete_ForumTopicAnim = BD.prepareStatement("UPDATE $forum_s SET an=NOT an WHERE id = ? LIMIT 1");
		Requete_SondageQuestions = BD.prepareStatement("SELECT * FROM $poll_q WHERE id_vote=?");
		Requete_SondageRéponses = BD.prepareStatement("SELECT * FROM $poll_r WHERE id_vote=?");
		Requete_ForumSuppressionDiscussion = BD.prepareStatement("DELETE FROM $forum_m WHERE s=?");
		Requete_ForumSuppressionTopic = BD.prepareStatement("DELETE FROM $forum_s WHERE id=? LIMIT 1");
		Requete_ForumSuppressionMessageSelect = BD.prepareStatement("SELECT a FROM $forum_m WHERE id=? AND s=? LIMIT 1");
		Requete_ForumSuppressionMessage = BD.prepareStatement("DELETE FROM $forum_m WHERE id=? AND s=? LIMIT 1");
		Requete_ForumSuppressionMessageDiscussion = BD.prepareStatement("UPDATE $forum_s SET m=m-1 WHERE id=? LIMIT 1");
		Requete_ForumSuppressionMessageNbMsg = BD.prepareStatement("SELECT m FROM $forum_s WHERE id=? LIMIT 1");
		Requete_ForumSuppressionMessageDernierMsg = BD.prepareStatement("SELECT d,a FROM $forum_m WHERE s=? order by d desc LIMIT 1");
		Requete_ForumSuppressionMessageDernierMsgTopic = BD.prepareStatement("UPDATE $forum_s SET d=?,p=? WHERE id=? LIMIT 1");
		Requete_ForumSupprimerTitreJoueur = BD.prepareStatement("DELETE FROM $forum_d where n=? LIMIT 1");
		Requete_ForumAjouterTitreJoueur = BD.prepareStatement("INSERT INTO $forum_d(n,d,c) values (?,?,?)");

		Requete_ChangerAvatar = BD.prepareStatement("UPDATE $joueur SET i=? WHERE id=? LIMIT 1");
		Requete_SondageRéponse = BD.prepareStatement("SELECT * from $poll_v where (id_joueur=? or ip=?) and id_vote=?");
		Requete_SondageRépondre = BD.prepareStatement("INSERT INTO $poll_v (id_vote,id_joueur,id_reponse,ip,hs,tj) VALUES (?,?,?,?,?,?)");
		Requete_SondageChangerVote = BD.prepareStatement("UPDATE $poll_v SET id_reponse=? WHERE id_vote = ? AND id_joueur = ? LIMIT 1");

		Requete_StopMaraboum = BD.prepareStatement("UPDATE mara SET nm=1 where n=? LIMIT 1");
		Requete_SauvegardeStatsJoueur = BD.prepareStatement("UPDATE $joueur SET o_f=?, o_s=?, o_sg=?, o_b=?, o_c=?, o_ca=?, o_tf=?, o_os=?, o_tc=?" + ", o_nl=?, o_nc=?, o_ns=?, o_nv=?, s_tt=?, s_dc=UNIX_TIMESTAMP(), ln=?, modo_muet=?, sb_pj=?, sb_pg=?, sb_t=?" + ", sb_m=?, sb_r=?, sa_pj=?, sa_pg=?, sa_js=?, sa_jg=?, sa_p=?, sa_pf=?, sa_t=?, sa_rs=?, sa_rp=?, sa_rt=?, sa_rv=?, sa_e=?, s=?, o_nomp=?, s_dcf=?, flag_msg_anim=?, rcmp_act=? WHERE n=? LIMIT 1");
		Requete_HistoSanctionJoueur = BD.prepareStatement("SELECT * FROM $sanctions WHERE nom_joueur = ? or ip = ? ORDER BY id ASC");
		Requete_HistoSanctionLast = BD.prepareStatement("SELECT * FROM $sanctions ORDER BY id DESC LIMIT 100");
		Requete_HistoSanctionLastForum = BD.prepareStatement("SELECT * FROM $sanctions WHERE sanction='mute_forum' or sanction='mute_forum_nom' or sanction='noté' ORDER BY id DESC LIMIT 100");
		
		Requete_SupprimerAvatarJoueur = BD.prepareStatement("UPDATE $joueur SET i = 0 WHERE n=? LIMIT 1");
		Requete_SupprimerAvatarMessages = BD.prepareStatement("UPDATE $forum_m SET i = 0 WHERE a=?");
		Requete_SupprimerAvatarTopics = BD.prepareStatement("UPDATE $forum_s SET i = 0 WHERE a=?");
		Requete_SupprimerDescriptionJoueur = BD.prepareStatement("UPDATE $joueur SET d = 'Edité' WHERE n=? LIMIT 1");

		Requete_ListeMPRecus = BD.prepareStatement("SELECT id, a, r, d, m from $mp where r = ? order by d desc");
		Requete_ListeMPEnvoyés = BD.prepareStatement("SELECT id, a, r, d, m from $mp where a = ? order by d desc");
		Requete_SupprimerMP = BD.prepareStatement("DELETE from $mp where r = ?");
		Requete_SupprimerMPById = BD.prepareStatement("DELETE from $mp where r = ? and id = ?");
		Requete_NouveauMP = BD.prepareStatement("INSERT INTO $mp(a,r,m,ip) VALUES (?,?,?,?)");
		Requete_InfoJoueurMP = BD.prepareStatement("SELECT n,o_nomp,ln from $joueur where n=? limit 1");
		Requete_InfoDernierMP = BD.prepareStatement("SELECT count(id) as c from $mp where r=? and (a=? or ip=?) and UNIX_TIMESTAMP()-UNIX_TIMESTAMP(d)<86400");
		Requete_InfoNbDernierMP = BD.prepareStatement("SELECT count(id) as c from $mp where (a=? or ip=?) and UNIX_TIMESTAMP()-UNIX_TIMESTAMP(d)<86400");
		Requete_NbMPDest = BD.prepareStatement("SELECT count(id) as c from $mp where r=?");
		Requete_SupprimerAnciensMP = BD.prepareStatement("DELETE FROM $mp WHERE UNIX_TIMESTAMP()-UNIX_TIMESTAMP(d)>31536000");

		Requete_NouvelleFusion = BD.prepareStatement("INSERT INTO $fusion (id,liste) VALUES (?,?)");
		Requete_UpdateFusion = BD.prepareStatement("UPDATE $fusion SET liste=CONCAT(liste,?) WHERE id = ?");
		Requete_QuitterFusion = BD.prepareStatement("UPDATE $fusion SET liste=REPLACE(liste,?,'') WHERE id = ?");
		Requete_SupprimerFusion = BD.prepareStatement("DELETE FROM $fusion WHERE id = ? LIMIT 1");
		Requete_LoadFusions = BD.prepareStatement("SELECT id,liste FROM $fusion ORDER BY id asc");
		
		Requete_SelectStatsRun = BD.prepareStatement("SELECT * FROM $stat_run WHERE nom=? and type!='archive'");
		Requete_SelectStatsDef = BD.prepareStatement("SELECT * FROM $stat_def WHERE nom=? and type!='archive'");
		Requete_SelectStatsNg = BD.prepareStatement("SELECT * FROM $stat_ng WHERE nom=? and type!='archive'");
		Requete_SelectStatsRally = BD.prepareStatement("SELECT * FROM $stat_rally WHERE nom=? and type!='archive'");
		Requete_SelectStatsFs = BD.prepareStatement("SELECT * FROM $stat_fs WHERE nom=? and type!='archive'");
		Requete_SelectStatsMs = BD.prepareStatement("SELECT * FROM $stat_ms WHERE nom=? and type!='archive'");
		Requete_SelectStatsGdm = BD.prepareStatement("SELECT * FROM $stat_gdm WHERE nom=? and type!='archive'");
		Requete_SelectStatsForto = BD.prepareStatement("SELECT * FROM $stat_forto WHERE nom=? and type!='archive'");
		Requete_SelectStatsBoum = BD.prepareStatement("SELECT * FROM $stat_boum WHERE nom=? and type!='archive'");

		Requete_NewStatsRun = BD.prepareStatement("INSERT INTO $stat_run(nom,type) VALUES (?,?)");
		Requete_NewStatsDef = BD.prepareStatement("INSERT INTO $stat_def(nom,type) VALUES (?,?)");
		Requete_NewStatsNg = BD.prepareStatement("INSERT INTO $stat_ng(nom,type) VALUES (?,?)");
		Requete_NewStatsRally = BD.prepareStatement("INSERT INTO $stat_rally(nom,type) VALUES (?,?)");
		Requete_NewStatsFs = BD.prepareStatement("INSERT INTO $stat_fs(nom,type) VALUES (?,?)");
		Requete_NewStatsMs = BD.prepareStatement("INSERT INTO $stat_ms(nom,type) VALUES (?,?)");
		Requete_NewStatsGdm = BD.prepareStatement("INSERT INTO $stat_gdm(nom,type) VALUES (?,?)");
		Requete_NewStatsForto = BD.prepareStatement("INSERT INTO $stat_forto(nom,type) VALUES (?,?)");
		Requete_NewStatsBoum = BD.prepareStatement("INSERT INTO $stat_boum(nom,type) VALUES (?,?)");

		Requete_UpdateStatsRun = BD.prepareStatement("UPDATE $stat_run SET tps_jeu=?,parties=?,arr_prem=?,arr_vie=?,kil=? WHERE nom=? and type=?");
		Requete_UpdateStatsDef = BD.prepareStatement("UPDATE $stat_def SET tps_jeu=?,parties=?,arr_prem=?,arr_vie=?,kil=? WHERE nom=? and type=?");
		Requete_UpdateStatsNg = BD.prepareStatement("UPDATE $stat_ng SET tps_jeu=?,parties=?,arr_prem=?,arr_vie=?,rec=? WHERE nom=? and type=?");
		Requete_UpdateStatsRally = BD.prepareStatement("UPDATE $stat_rally SET tps_jeu=?,parties=?,arr_prem=?,arr_vie=?,rec=? WHERE nom=? and type=?");
		Requete_UpdateStatsFs = BD.prepareStatement("UPDATE $stat_fs SET tps_jeu=?,parties=?,der_survi=?,survie=?,kil=? WHERE nom=? and type=?");
		Requete_UpdateStatsMs = BD.prepareStatement("UPDATE $stat_ms SET tps_jeu=?,parties=?,jou_guid=?,jou_sauv=? WHERE nom=? and type=?");
		Requete_UpdateStatsGdm = BD.prepareStatement("UPDATE $stat_gdm SET tps_jeu=?,parties=?,jou_guid=?,jou_sauv=? WHERE nom=? and type=?");
		Requete_UpdateStatsForto = BD.prepareStatement("UPDATE $stat_forto SET tps_jeu=?,parties=? WHERE nom=? and type=?");
		Requete_UpdateStatsBoum = BD.prepareStatement("UPDATE $stat_boum SET tps_jeu=?,parties=?,win=?,manche=?,kil=?,suicide=?,kil_by_others=? WHERE nom=? and type=?");
		
		Requete_ArchiveStatsRun = BD.prepareStatement("UPDATE $stat_run SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsDef = BD.prepareStatement("UPDATE $stat_def SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsNg = BD.prepareStatement("UPDATE $stat_ng SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsRally = BD.prepareStatement("UPDATE $stat_rally SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsFs = BD.prepareStatement("UPDATE $stat_fs SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsMs = BD.prepareStatement("UPDATE $stat_ms SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsGdm = BD.prepareStatement("UPDATE $stat_gdm SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsForto = BD.prepareStatement("UPDATE $stat_forto SET type='archive',date_arch=NOW() WHERE type='mois'");
		Requete_ArchiveStatsBoum = BD.prepareStatement("UPDATE $stat_boum SET type='archive',date_arch=NOW() WHERE type='mois'");
		
		// Elo
		Requete_CreerEloAaaah = BD.prepareStatement("INSERT INTO $elo_a (id, ra_iv, ra_ipj, ra_ie, ra_dv, ra_dpj, ra_de, def_iv, def_ipj, def_ie, def_dv, def_dpj, def_de, ms_dv, ms_dpj, ms_de, def_mv, def_mpj, def_me, ms_mv, ms_mpj, ms_me, ra_mv, ra_mpj, ra_me, fs_mv, fs_mpj, fs_me, run_mv, run_mpj, run_me) VALUES (?, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200)");
		Requete_CreerEloBouboum = BD.prepareStatement("INSERT INTO $elo_b (id, k_iv, k_ipj, k_ie, k_dv, k_dpj, k_de, k_mv, k_mpj, k_me) VALUES (?, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200)");
		Requete_CreerEloForto = BD.prepareStatement("INSERT INTO $elo_f (id, fra_iv, fra_ipj, fra_ie, fra_dv, fra_dpj, fra_de, fri_iv, fri_ipj, fri_ie, fri_dv, fri_dpj, fri_de, k_iv, k_ipj, k_ie, k_dv, k_dpj, k_de, crs_iv, crs_ipj, crs_ie, crs_dv, crs_dpj, crs_de, fra_mv, fra_mpj, fra_me, fri_mv, fri_mpj, fri_me, k_mv, k_mpj, k_me) VALUES (?, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200)");
		
		Requete_CreerEloTeamAaaah = BD.prepareStatement("INSERT INTO $elo_t_a (id, ra_v, ra_pj, ra_e, def_v, def_pj, def_e, ms_v, ms_pj, ms_e, fs_v, fs_pj, fs_e, run_v, run_pj, run_e) VALUES (?, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200)");
		Requete_CreerEloTeamBouboum = BD.prepareStatement("INSERT INTO $elo_t_b (id, k_v, k_pj, k_e) VALUES (?, 0, 0, 1200)");
		Requete_CreerEloTeamForto = BD.prepareStatement("INSERT INTO $elo_t_f (id, fra_v, fra_pj, fra_e, fri_v, fri_pj, fri_e, k_v, k_pj, k_e) VALUES (?, 0, 0, 1200, 0, 0, 1200, 0, 0, 1200)");
		
		// Donjon
		Requete_AddDonjon = BD.prepareStatement("INSERT INTO $donjon(id_donjon, niv, duree, type, code) VALUES (?, ?, ?, ?, COMPRESS(?))");
		Requete_LoadMapNivDonjon = BD.prepareStatement("SELECT UNCOMPRESS(code), duree FROM $donjon WHERE id_donjon=? && niv=? && type=? LIMIT 1");
		Requete_ChargerDonjon = BD.prepareStatement("SELECT niv, niv_bonus, points, items, classe, groupe FROM $donjon_j WHERE id_donjon=? && id=? LIMIT 1");
		Requete_AddJoueurDonjon = BD.prepareStatement("INSERT INTO $donjon_j(id, id_donjon, niv, niv_bonus, points, items, classe, groupe) VALUES (?, ?, 0, FALSE, 0, '', 0, 0)");
		Requete_SvgdAvancementDonjon = BD.prepareStatement("UPDATE $donjon_j SET niv=?, niv_bonus=?, points=?, items=?, classe=?, groupe=? WHERE id_donjon=? && id=? LIMIT 1");
		
		// Suppression de comptes inactifs
		Requete_GetComptesInactifsASuppr = BD.prepareStatement("SELECT n, id FROM $joueur WHERE s_tt < 14400000 && s_dc < (UNIX_TIMESTAMP() - 63115200) && ban!=1 ORDER BY RAND() LIMIT 20");
		Requete_SupprCompteByID = BD.prepareStatement("DELETE FROM $joueur WHERE id=? LIMIT 1");
		Requete_SCI_CleanListesAmis = BD.prepareStatement("DELETE FROM $amis WHERE id_joueur=? || id_ami=?");
		Requete_SCI_CleanPostsForum = BD.prepareStatement("UPDATE $forum_m SET a=? WHERE a=?");
		Requete_SCI_CleanTopicsForum = BD.prepareStatement("UPDATE $forum_s SET a=? WHERE a=?");
		Requete_SCI_CleanEloA = BD.prepareStatement("DELETE FROM $elo_a WHERE id=?");
		Requete_SCI_CleanEloB = BD.prepareStatement("DELETE FROM $elo_b WHERE id=?");
		Requete_SCI_CleanEloF = BD.prepareStatement("DELETE FROM $elo_f WHERE id=?");
		Requete_SCI_CleanHistoSanctions = BD.prepareStatement("UPDATE $sanctions SET nom_joueur=? WHERE nom_joueur=?");
		Requete_SCI_CleanMessagerie = BD.prepareStatement("DELETE FROM $mp WHERE a=? || r=?");
		Requete_SCI_RemplaceAuteursMapsA = BD.prepareStatement("UPDATE $maps SET auteur=? WHERE auteur=?");
		Requete_SCI_RemplaceRecordmanMapsA = BD.prepareStatement("UPDATE $maps SET recordman=? WHERE recordman=?");
		Requete_SCI_RemplaceAuteursMapsF = BD.prepareStatement("UPDATE $maps_forto SET auteur=? WHERE auteur=?");
		Requete_SCI_RemplaceRecordmanMapsF = BD.prepareStatement("UPDATE $maps_forto SET recordman=? WHERE recordman=?");
		Requete_SCI_CleanAuteurPlaylist = BD.prepareStatement("DELETE FROM $playlist WHERE id_joueur=?");
		Requete_SCI_DeleteDonjonJ = BD.prepareStatement("DELETE FROM $donjon_j WHERE id=?");
		
		Requete_ImporterCompte = BD.prepareStatement("UPDATE $joueur SET i=?, s_ti=? WHERE n=?");
		
		Requete_Classements.add(BD.prepareStatement("SELECT nom,parties,ROUND(100*win/parties,1) as ratio FROM $stat_boum WHERE type='mois' ORDER BY parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,win,ROUND(100*win/parties,1) as ratio FROM $stat_boum WHERE type='mois' ORDER BY win DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,manche,ROUND(1000*manche/parties,1) as ratio FROM $stat_boum WHERE type='mois' ORDER BY manche DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_run WHERE type='mois' ORDER BY arr_prem DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_def WHERE type='mois' ORDER BY arr_prem DESC LIMIT 5;"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_ng WHERE type='mois' ORDER BY arr_prem DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_rally WHERE type='mois' ORDER BY arr_prem DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_vie,ROUND(100*arr_vie/parties,1) as ratio FROM $stat_run WHERE type='mois' ORDER BY arr_vie DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_vie,ROUND(100*arr_vie/parties,1) as ratio FROM $stat_def WHERE type='mois' ORDER BY arr_vie DESC LIMIT 5;"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,jou_sauv,ROUND(100*jou_sauv/jou_guid,1) as ratio FROM $stat_ms WHERE type='mois' ORDER BY jou_sauv DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,kil,ROUND(kil/parties,2) as ratio FROM $stat_fs WHERE type='mois' ORDER BY kil DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_run WHERE type='mois' and parties > 104 ORDER BY arr_prem/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_def WHERE type='mois' and parties > 204 ORDER BY arr_prem/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_vie,ROUND(100*arr_vie/parties,1) as ratio FROM $stat_def WHERE type='mois' and parties > 204 ORDER BY arr_vie/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_ng WHERE type='mois' and parties > 44 ORDER BY arr_prem/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,arr_prem,ROUND(100*arr_prem/parties,1) as ratio FROM $stat_rally WHERE type='mois' and parties > 44 ORDER BY arr_prem/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,rec,0 as ratio FROM $stat_rally WHERE type='mois' ORDER BY rec DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,jou_sauv,ROUND(100*jou_sauv/jou_guid,1) as ratio FROM $stat_gdm WHERE type='mois' and parties > 24 ORDER BY jou_sauv/jou_guid DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,der_survi,ROUND(100*der_survi/parties,1) as ratio FROM $stat_fs WHERE type='mois' and parties > 54 ORDER BY der_survi/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,survie,ROUND(100*survie/parties,1) as ratio FROM $stat_fs WHERE type='mois' and parties > 54 ORDER BY survie/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,win,ROUND(100*win/parties,1) as ratio FROM $stat_boum WHERE type='mois' and parties > 204 ORDER BY win/parties DESC LIMIT 5"));
		Requete_Classements.add(BD.prepareStatement("SELECT nom,kil,ROUND(kil/parties,2) as ratio FROM $stat_boum WHERE type='mois' and parties > 204 ORDER BY kil/parties DESC LIMIT 5"));
	}
}
