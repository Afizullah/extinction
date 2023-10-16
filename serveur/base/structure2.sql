-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mini_jeux
-- ------------------------------------------------------
-- Server version	5.5.31-0+wheezy1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `$amis`
--

DROP TABLE IF EXISTS `$amis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$amis` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_joueur` int(10) unsigned NOT NULL,
  `id_ami` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_joueur` (`id_joueur`)
) ENGINE=MyISAM AUTO_INCREMENT=2379705 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$ban_host`
--

DROP TABLE IF EXISTS `$ban_host`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$ban_host` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `host` varchar(100) NOT NULL,
  `raison` varchar(300) DEFAULT NULL,
  `actif` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=244 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$ban_ip`
--

DROP TABLE IF EXISTS `$ban_ip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$ban_ip` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `ip` varchar(16) NOT NULL,
  `raison` varchar(300) DEFAULT NULL,
  `actif` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4264 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$chgpass`
--

DROP TABLE IF EXISTS `$chgpass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$chgpass` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `oldp` text NOT NULL,
  `newp` text NOT NULL,
  `ip` varchar(16) NOT NULL,
  `message` text NOT NULL,
  `etat` tinyint(3) NOT NULL DEFAULT '0',
  `temps` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6652 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$donjon`
--

DROP TABLE IF EXISTS `$donjon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$donjon` (
  `id_donjon` int(10) unsigned NOT NULL DEFAULT '0',
  `niv` smallint(6) NOT NULL DEFAULT '0',
  `duree` int(10) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `code` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$donjon_j`
--

DROP TABLE IF EXISTS `$donjon_j`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$donjon_j` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `id_donjon` int(10) unsigned NOT NULL DEFAULT '0',
  `niv` smallint(6) NOT NULL DEFAULT '0',
  `niv_bonus` bit(1) NOT NULL DEFAULT b'0',
  `points` int(5) unsigned NOT NULL DEFAULT '0',
  `items` text,
  `classe` int(3) unsigned NOT NULL DEFAULT '0',
  `groupe` int(5) unsigned NOT NULL DEFAULT '0',
  `x` smallint(4) unsigned NOT NULL DEFAULT '0',
  `y` smallint(4) unsigned NOT NULL DEFAULT '0',
  `temps_ecoule` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$elo_a`
--

DROP TABLE IF EXISTS `$elo_a`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$elo_a` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_de` int(10) unsigned NOT NULL DEFAULT '0',
  `def_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `def_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `def_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `def_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_de` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_de` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_me` int(10) unsigned NOT NULL DEFAULT '0',
  `def_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `def_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_me` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_me` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_me` int(10) unsigned NOT NULL DEFAULT '0',
  `run_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `run_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `run_me` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `ind_ira` (`ra_ie`),
  KEY `ind_dra` (`ra_de`),
  KEY `ind_idef` (`def_ie`),
  KEY `ind_def` (`def_de`),
  KEY `ind_dms` (`ms_de`),
  KEY `ind_mra` (`ra_me`),
  KEY `ind_mdef` (`def_me`),
  KEY `ind_mms` (`ms_me`),
  KEY `ind_mfs` (`fs_me`),
  KEY `ind_mrun` (`run_me`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$elo_b`
--

DROP TABLE IF EXISTS `$elo_b`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$elo_b` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `k_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_de` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_me` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `ind_ik` (`k_ie`),
  KEY `ind_dk` (`k_de`),
  KEY `ind_mk` (`k_me`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$elo_f`
--

DROP TABLE IF EXISTS `$elo_f`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$elo_f` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_de` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_de` int(10) unsigned NOT NULL DEFAULT '0',
  `k_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_de` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_me` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_me` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_me` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_de` int(10) unsigned NOT NULL DEFAULT '1200',
  PRIMARY KEY (`id`),
  KEY `ind_ifra` (`fra_ie`),
  KEY `ind_dfra` (`fra_de`),
  KEY `ind_ifri` (`fri_ie`),
  KEY `ind_dfri` (`fri_de`),
  KEY `ind_ik` (`k_ie`),
  KEY `ind_dk` (`k_de`),
  KEY `ind_icrs` (`crs_ie`),
  KEY `ind_mfra` (`fra_me`),
  KEY `ind_mfri` (`fri_me`),
  KEY `ind_mk` (`k_me`),
  KEY `crs_de` (`crs_de`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$elo_t_a`
--

DROP TABLE IF EXISTS `$elo_t_a`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$elo_t_a` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_v` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_e` int(10) unsigned NOT NULL DEFAULT '0',
  `def_v` int(10) unsigned NOT NULL DEFAULT '0',
  `def_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_e` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_v` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_e` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_v` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_e` int(10) unsigned NOT NULL DEFAULT '0',
  `run_v` int(10) unsigned NOT NULL DEFAULT '0',
  `run_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `run_e` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$elo_t_b`
--

DROP TABLE IF EXISTS `$elo_t_b`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$elo_t_b` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `k_v` int(10) unsigned NOT NULL DEFAULT '0',
  `k_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_e` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$elo_t_f`
--

DROP TABLE IF EXISTS `$elo_t_f`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$elo_t_f` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_v` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_e` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_v` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_e` int(10) unsigned NOT NULL DEFAULT '0',
  `k_v` int(10) unsigned NOT NULL DEFAULT '0',
  `k_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_e` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$forum_d`
--

DROP TABLE IF EXISTS `$forum_d`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$forum_d` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `n` varchar(20) NOT NULL,
  `d` varchar(100) NOT NULL,
  `c` varchar(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `n` (`n`)
) ENGINE=MyISAM AUTO_INCREMENT=148 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$forum_m`
--

DROP TABLE IF EXISTS `$forum_m`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$forum_m` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `d` bigint(20) unsigned NOT NULL DEFAULT '0',
  `s` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `a` varchar(20) NOT NULL DEFAULT '',
  `i` varchar(25) DEFAULT NULL,
  `m` text NOT NULL,
  `ip` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `d` (`d`,`s`)
) ENGINE=MyISAM AUTO_INCREMENT=2096189 DEFAULT CHARSET=utf8 PACK_KEYS=0;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$forum_s`
--

DROP TABLE IF EXISTS `$forum_s`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$forum_s` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `d` bigint(20) unsigned NOT NULL DEFAULT '0',
  `a` varchar(20) NOT NULL DEFAULT '',
  `i` varchar(25) DEFAULT NULL,
  `t` tinytext NOT NULL,
  `c` bigint(20) unsigned NOT NULL DEFAULT '0',
  `m` smallint(5) unsigned NOT NULL DEFAULT '0',
  `p` varchar(20) NOT NULL DEFAULT '',
  `x` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `pi` tinyint(1) unsigned NOT NULL,
  `xm` varchar(20) DEFAULT NULL,
  `xf` tinyint(1) DEFAULT NULL,
  `an` char(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `d` (`d`),
  KEY `f` (`f`)
) ENGINE=MyISAM AUTO_INCREMENT=83371 DEFAULT CHARSET=utf8 PACK_KEYS=0;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$fusion`
--

DROP TABLE IF EXISTS `$fusion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$fusion` (
  `id` int(10) NOT NULL,
  `liste` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$grades_j`
--

DROP TABLE IF EXISTS `$grades_j`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$grades_j` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `g10` tinyint(1) NOT NULL DEFAULT '0',
  `g11` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$joueur`
--

DROP TABLE IF EXISTS `$joueur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$joueur` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `n` varchar(20) NOT NULL DEFAULT '',
  `p` text NOT NULL,
  `i` varchar(25) DEFAULT NULL,
  `a` varchar(20) NOT NULL DEFAULT '0',
  `o_f` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `o_s` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `o_sg` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `o_b` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `o_c` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `o_ca` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `o_tf` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `o_tc` tinyint(3) NOT NULL DEFAULT '1',
  `o_os` tinyint(3) NOT NULL DEFAULT '0',
  `o_nl` tinyint(3) NOT NULL DEFAULT '0',
  `o_nv` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `o_nc` tinyint(3) NOT NULL DEFAULT '0',
  `o_ns` tinyint(3) NOT NULL DEFAULT '0',
  `o_nomp` tinyint(3) NOT NULL DEFAULT '0',
  `sb_pj` mediumint(8) unsigned NOT NULL,
  `sb_pg` mediumint(8) unsigned NOT NULL,
  `sb_t` mediumint(8) unsigned NOT NULL,
  `sb_m` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `sb_r` mediumint(8) unsigned NOT NULL,
  `s_ti` int(10) unsigned NOT NULL DEFAULT '0',
  `s_tt` bigint(13) unsigned NOT NULL DEFAULT '0',
  `s_dc` int(10) unsigned NOT NULL DEFAULT '0',
  `modo_igno` mediumtext NOT NULL,
  `modo_muet` int(10) unsigned NOT NULL,
  `sa_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `sa_pg` int(10) unsigned NOT NULL DEFAULT '0',
  `sa_js` int(10) unsigned NOT NULL,
  `sa_jg` int(10) unsigned NOT NULL,
  `sa_p` int(10) unsigned NOT NULL,
  `sa_pf` int(10) unsigned NOT NULL,
  `sa_t` int(10) unsigned NOT NULL,
  `sa_rs` int(10) unsigned NOT NULL,
  `sa_rp` bigint(7) NOT NULL,
  `sa_rt` bigint(7) NOT NULL,
  `sa_rv` bigint(7) NOT NULL,
  `sa_e` bigint(13) NOT NULL DEFAULT '0',
  `d` tinytext NOT NULL,
  `ip` varchar(16) DEFAULT NULL,
  `chgpass` tinyint(3) NOT NULL DEFAULT '0',
  `ln` mediumtext NOT NULL,
  `ban` tinyint(2) NOT NULL DEFAULT '0',
  `muteforum` tinyint(1) NOT NULL DEFAULT '0',
  `mutecp` tinyint(1) NOT NULL DEFAULT '0',
  `hs` varchar(100) DEFAULT NULL,
  `s` varchar(25) NOT NULL DEFAULT '',
  `ur` tinyint(1) NOT NULL DEFAULT '0',
  `s_dcf` int(10) DEFAULT NULL,
  `g9` tinyint(1) NOT NULL DEFAULT '0',
  `flag_msg_anim` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `rcmp_act` tinyint(2) NOT NULL DEFAULT '1',
  `elo_flag` varchar(3) NOT NULL DEFAULT '000',
  PRIMARY KEY (`id`),
  KEY `n` (`n`)
) ENGINE=MyISAM AUTO_INCREMENT=1962830 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$joueur_r`
--

DROP TABLE IF EXISTS `$joueur_r`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$joueur_r` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `n` varchar(20) NOT NULL DEFAULT '',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `image` varchar(10) NOT NULL DEFAULT '0',
  `message` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `n` (`n`)
) ENGINE=MyISAM AUTO_INCREMENT=1337 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$maps`
--

DROP TABLE IF EXISTS `$maps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$maps` (
  `id` bigint(15) unsigned NOT NULL,
  `auteur` varchar(20) NOT NULL,
  `titre` varchar(150) NOT NULL,
  `code` text,
  `votes` int(15) unsigned NOT NULL DEFAULT '0',
  `pour` int(15) unsigned NOT NULL DEFAULT '0',
  `perma` tinyint(1) NOT NULL DEFAULT '0',
  `record` int(15) unsigned NOT NULL DEFAULT '120000',
  `recordman` varchar(20) NOT NULL DEFAULT '-',
  `guide` varchar(20) NOT NULL DEFAULT '',
  `played` int(15) unsigned NOT NULL DEFAULT '0',
  `survie` int(15) unsigned NOT NULL DEFAULT '0',
  `mode` smallint(3) NOT NULL DEFAULT '0',
  `mara_record` int(15) NOT NULL DEFAULT '120000',
  `mara_recordman` varchar(20) NOT NULL DEFAULT '-',
  `mara_played` int(15) NOT NULL DEFAULT '0',
  `ghost` text,
  PRIMARY KEY (`id`),
  KEY `auteur` (`auteur`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$maps_forto`
--

DROP TABLE IF EXISTS `$maps_forto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$maps_forto` (
  `id` bigint(15) NOT NULL,
  `auteur` varchar(20) NOT NULL,
  `titre` varchar(150) NOT NULL,
  `code` blob NOT NULL,
  `couleur` tinyint(1) NOT NULL DEFAULT '0',
  `niveau` tinyint(1) NOT NULL DEFAULT '0',
  `mode` tinyint(2) NOT NULL DEFAULT '0',
  `note` tinyint(2) NOT NULL DEFAULT '0',
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `record` int(10) NOT NULL DEFAULT '0',
  `recordman` varchar(20) NOT NULL DEFAULT '',
  `couleur_perso` tinytext,
  `infos_monde` tinytext,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$maps_his`
--

DROP TABLE IF EXISTS `$maps_his`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$maps_his` (
  `id` bigint(15) unsigned NOT NULL,
  `auteur` varchar(20) NOT NULL,
  `titre` varchar(150) NOT NULL,
  `code` text,
  `respomap` varchar(20) DEFAULT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type` char(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Auteur` (`auteur`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$membres`
--

DROP TABLE IF EXISTS `$membres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$membres` (
  `id_joueur` int(10) NOT NULL,
  `id_team` mediumint(5) NOT NULL,
  `grade` mediumint(5) NOT NULL,
  `role` mediumint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_joueur`),
  KEY `id_team` (`id_team`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$mp`
--

DROP TABLE IF EXISTS `$mp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$mp` (
  `id` int(100) NOT NULL AUTO_INCREMENT,
  `a` varchar(20) NOT NULL,
  `r` varchar(20) NOT NULL,
  `d` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `m` text NOT NULL,
  `ip` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `a` (`a`,`r`,`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=284323 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$old_parties`
--

DROP TABLE IF EXISTS `$old_parties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$old_parties` (
  `id` int(10) unsigned NOT NULL,
  `op_sa_js` int(10) unsigned NOT NULL,
  `op_sa_jg` int(10) unsigned NOT NULL,
  `op_sa_pj` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$old_stats_j`
--

DROP TABLE IF EXISTS `$old_stats_j`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$old_stats_j` (
  `id` int(10) unsigned NOT NULL,
  `sa_guidage` int(10) unsigned NOT NULL,
  `sa_pguidage` int(10) unsigned NOT NULL,
  `sa_victoire` int(10) unsigned NOT NULL,
  `sa_pvictoire` int(10) unsigned NOT NULL,
  `sb_victoire` int(10) unsigned NOT NULL,
  `sb_pvictoire` int(10) unsigned NOT NULL,
  `sb_opj` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$playlist`
--

DROP TABLE IF EXISTS `$playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$playlist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_joueur` int(10) unsigned NOT NULL,
  `cartes` varchar(3000) DEFAULT NULL,
  `code` varchar(10) NOT NULL,
  `titre` varchar(35) NOT NULL,
  `featured` int(2) unsigned DEFAULT '0',
  `dernierLoad` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `populair` int(10) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `auteur_code` (`id_joueur`,`code`)
) ENGINE=MyISAM AUTO_INCREMENT=283 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$poll_q`
--

DROP TABLE IF EXISTS `$poll_q`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$poll_q` (
  `id_vote` smallint(10) NOT NULL AUTO_INCREMENT,
  `question` text NOT NULL,
  PRIMARY KEY (`id_vote`)
) ENGINE=MyISAM AUTO_INCREMENT=796 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$poll_r`
--

DROP TABLE IF EXISTS `$poll_r`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$poll_r` (
  `id_vote` smallint(10) NOT NULL,
  ` id_reponse` smallint(10) NOT NULL,
  `reponse` text NOT NULL,
  PRIMARY KEY (`id_vote`,` id_reponse`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$poll_v`
--

DROP TABLE IF EXISTS `$poll_v`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$poll_v` (
  `id_vote` smallint(10) NOT NULL,
  `id_joueur` int(10) NOT NULL,
  `id_reponse` smallint(10) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `hs` varchar(100) NOT NULL,
  `tj` bigint(20) NOT NULL,
  PRIMARY KEY (`id_vote`,`id_joueur`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$recompenses`
--

DROP TABLE IF EXISTS `$recompenses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$recompenses` (
  `id_team` int(10) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `image` varchar(10) DEFAULT NULL,
  `message` varchar(100) DEFAULT NULL,
  KEY `id_team` (`id_team`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$sanctions`
--

DROP TABLE IF EXISTS `$sanctions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$sanctions` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `nom_joueur` varchar(20) NOT NULL,
  `nom_modo` varchar(20) DEFAULT NULL,
  `ip` varchar(16) DEFAULT NULL,
  `host` varchar(60) DEFAULT NULL,
  `sanction` varchar(15) NOT NULL,
  `temps` int(10) DEFAULT NULL,
  `d` varchar(30) DEFAULT NULL,
  `raison` varchar(300) DEFAULT NULL,
  `fin` bigint(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nom_joueur` (`nom_joueur`)
) ENGINE=MyISAM AUTO_INCREMENT=247950 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_boum`
--

DROP TABLE IF EXISTS `$stat_boum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_boum` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `win` int(10) NOT NULL DEFAULT '0',
  `manche` int(10) NOT NULL DEFAULT '0',
  `kil` int(10) NOT NULL DEFAULT '0',
  `suicide` int(10) NOT NULL DEFAULT '0',
  `kil_by_others` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_def`
--

DROP TABLE IF EXISTS `$stat_def`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_def` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `arr_prem` int(10) NOT NULL DEFAULT '0',
  `arr_vie` int(10) NOT NULL DEFAULT '0',
  `kil` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_elo_a`
--

DROP TABLE IF EXISTS `$stat_elo_a`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_elo_a` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_de` int(10) unsigned NOT NULL DEFAULT '0',
  `def_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `def_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `def_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `def_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_de` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_de` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_me` int(10) unsigned NOT NULL DEFAULT '0',
  `def_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `def_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_me` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_me` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_me` int(10) unsigned NOT NULL DEFAULT '0',
  `run_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `run_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `run_me` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_elo_b`
--

DROP TABLE IF EXISTS `$stat_elo_b`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_elo_b` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `k_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_de` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_me` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_elo_f`
--

DROP TABLE IF EXISTS `$stat_elo_f`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_elo_f` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_de` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_de` int(10) unsigned NOT NULL DEFAULT '0',
  `k_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_de` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_iv` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_ipj` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_ie` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_me` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_me` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mv` int(10) unsigned NOT NULL DEFAULT '0',
  `k_mpj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_me` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_dv` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_dpj` int(10) unsigned NOT NULL DEFAULT '0',
  `crs_de` int(10) unsigned NOT NULL DEFAULT '1200',
  PRIMARY KEY (`id`),
  KEY `ind_mfra` (`fra_me`),
  KEY `ind_mfri` (`fri_me`),
  KEY `ind_mk` (`k_me`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_elo_t_a`
--

DROP TABLE IF EXISTS `$stat_elo_t_a`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_elo_t_a` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_v` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `ra_e` int(10) unsigned NOT NULL DEFAULT '0',
  `def_v` int(10) unsigned NOT NULL DEFAULT '0',
  `def_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `def_e` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_v` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `ms_e` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_v` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `fs_e` int(10) unsigned NOT NULL DEFAULT '0',
  `run_v` int(10) unsigned NOT NULL DEFAULT '0',
  `run_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `run_e` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_elo_t_b`
--

DROP TABLE IF EXISTS `$stat_elo_t_b`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_elo_t_b` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `k_v` int(10) unsigned NOT NULL DEFAULT '0',
  `k_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_e` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_elo_t_f`
--

DROP TABLE IF EXISTS `$stat_elo_t_f`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_elo_t_f` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_v` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `fra_e` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_v` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `fri_e` int(10) unsigned NOT NULL DEFAULT '0',
  `k_v` int(10) unsigned NOT NULL DEFAULT '0',
  `k_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `k_e` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_forto`
--

DROP TABLE IF EXISTS `$stat_forto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_forto` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_fs`
--

DROP TABLE IF EXISTS `$stat_fs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_fs` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `der_survi` int(10) NOT NULL DEFAULT '0',
  `survie` int(10) NOT NULL DEFAULT '0',
  `kil` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_gdm`
--

DROP TABLE IF EXISTS `$stat_gdm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_gdm` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `jou_guid` int(10) NOT NULL DEFAULT '0',
  `jou_sauv` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_ms`
--

DROP TABLE IF EXISTS `$stat_ms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_ms` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `jou_guid` int(10) NOT NULL DEFAULT '0',
  `jou_sauv` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_ng`
--

DROP TABLE IF EXISTS `$stat_ng`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_ng` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `arr_prem` int(10) NOT NULL DEFAULT '0',
  `arr_vie` int(10) NOT NULL DEFAULT '0',
  `rec` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_rally`
--

DROP TABLE IF EXISTS `$stat_rally`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_rally` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `arr_prem` int(10) NOT NULL DEFAULT '0',
  `arr_vie` int(10) NOT NULL DEFAULT '0',
  `rec` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stat_run`
--

DROP TABLE IF EXISTS `$stat_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stat_run` (
  `nom` varchar(20) NOT NULL,
  `type` varchar(8) NOT NULL,
  `date_arch` date NOT NULL DEFAULT '0000-00-00',
  `tps_jeu` bigint(20) NOT NULL DEFAULT '0',
  `parties` int(10) NOT NULL DEFAULT '0',
  `arr_prem` int(10) NOT NULL DEFAULT '0',
  `arr_vie` int(10) NOT NULL DEFAULT '0',
  `kil` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nom`,`type`,`date_arch`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$stats`
--

DROP TABLE IF EXISTS `$stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$stats` (
  `n` varchar(20) NOT NULL DEFAULT '',
  `sa_pj` int(10) unsigned NOT NULL DEFAULT '0',
  `r_pj` mediumint(6) unsigned DEFAULT NULL,
  `sa_pg` int(10) unsigned NOT NULL DEFAULT '0',
  `r_pg` mediumint(6) unsigned DEFAULT NULL,
  `sa_js` int(10) unsigned NOT NULL,
  `sa_jg` int(10) unsigned NOT NULL,
  `sa_p` int(10) unsigned NOT NULL,
  `r_p` mediumint(6) unsigned DEFAULT NULL,
  `sa_pf` int(10) unsigned NOT NULL,
  `r_pf` mediumint(6) unsigned DEFAULT NULL,
  `sa_t` int(10) unsigned NOT NULL,
  `r_t` mediumint(6) unsigned DEFAULT NULL,
  `sa_rs` int(10) unsigned NOT NULL,
  `r_rs` mediumint(6) unsigned DEFAULT NULL,
  `sa_rp` smallint(5) unsigned NOT NULL,
  `r_rp` mediumint(6) unsigned DEFAULT NULL,
  `sa_rt` smallint(5) unsigned NOT NULL,
  `sa_rv` smallint(5) unsigned NOT NULL,
  `r_rv` mediumint(6) unsigned DEFAULT NULL,
  PRIMARY KEY (`n`),
  KEY `r_pj` (`r_pj`),
  KEY `r_pg` (`r_pg`),
  KEY `r_p` (`r_p`),
  KEY `r_pf` (`r_pf`),
  KEY `r_t` (`r_t`),
  KEY `r_rs` (`r_rs`),
  KEY `r_rp` (`r_rp`),
  KEY `r_rv` (`r_rv`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$teams`
--

DROP TABLE IF EXISTS `$teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$teams` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `tag` varchar(10) NOT NULL,
  `description` text NOT NULL,
  `site` varchar(255) DEFAULT NULL,
  `modif` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `creation` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `fondateur` varchar(20) NOT NULL,
  `jeu` smallint(2) NOT NULL DEFAULT '1',
  `membres` mediumint(5) NOT NULL DEFAULT '0',
  `officiel` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `score` int(10) NOT NULL DEFAULT '0',
  `message` varchar(1000) DEFAULT NULL,
  `blason` varchar(25) DEFAULT NULL,
  `partage_topic` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `score` (`score`)
) ENGINE=MyISAM AUTO_INCREMENT=75798 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$teams_his`
--

DROP TABLE IF EXISTS `$teams_his`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$teams_his` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `id_team` int(10) NOT NULL,
  `auteur` varchar(20) DEFAULT NULL,
  `ip` varchar(16) DEFAULT NULL,
  `cible` varchar(20) DEFAULT NULL,
  `action` varchar(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_team` (`id_team`),
  KEY `Personne` (`cible`,`auteur`)
) ENGINE=MyISAM AUTO_INCREMENT=1328482 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `$votes`
--

DROP TABLE IF EXISTS `$votes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `$votes` (
  `id_vote` smallint(10) unsigned NOT NULL,
  `id_joueur` int(10) unsigned NOT NULL,
  `ip` varchar(15) DEFAULT NULL,
  `vote` tinyint(3) unsigned NOT NULL,
  `fiabilite` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id_vote`,`id_joueur`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mara`
--

DROP TABLE IF EXISTS `mara`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mara` (
  `n` varchar(20) NOT NULL,
  `tb` int(10) NOT NULL DEFAULT '0',
  `tj` bigint(20) NOT NULL DEFAULT '0',
  `nm` tinyint(1) NOT NULL DEFAULT '0',
  `pj` int(10) NOT NULL DEFAULT '0',
  `top_pj` int(10) NOT NULL DEFAULT '0',
  `t` int(10) NOT NULL DEFAULT '0',
  `rt` decimal(5,2) NOT NULL DEFAULT '0.00',
  `top_rt` int(10) NOT NULL DEFAULT '0',
  `pg` int(10) NOT NULL DEFAULT '0',
  `rpg` decimal(5,2) NOT NULL DEFAULT '0.00',
  `top_rpg` int(10) NOT NULL DEFAULT '0',
  `a` int(10) NOT NULL DEFAULT '0',
  `ra` decimal(5,2) NOT NULL DEFAULT '0.00',
  `spg` decimal(5,2) NOT NULL DEFAULT '0.00',
  `top_spg` int(10) NOT NULL DEFAULT '0',
  `m` int(10) NOT NULL DEFAULT '0',
  `s` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`n`),
  KEY `top_p` (`top_pj`),
  KEY `top_rtf` (`top_rt`),
  KEY `top_rtd` (`top_rpg`),
  KEY `top_nr` (`top_spg`),
  KEY `top_tm` (`m`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `maraaaahton`
--

DROP TABLE IF EXISTS `maraaaahton`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maraaaahton` (
  `n` varchar(20) NOT NULL,
  `tb` int(10) NOT NULL DEFAULT '0',
  `tj` bigint(20) NOT NULL DEFAULT '0',
  `js` int(10) NOT NULL DEFAULT '0',
  `jg` int(10) NOT NULL DEFAULT '0',
  `rs` decimal(5,2) NOT NULL DEFAULT '0.00',
  `pg` int(10) NOT NULL DEFAULT '0',
  `top_pg` int(10) NOT NULL DEFAULT '0',
  `top_rs` int(10) NOT NULL DEFAULT '0',
  `p` int(10) NOT NULL DEFAULT '0',
  `pj` int(10) NOT NULL DEFAULT '0',
  `rp` decimal(5,2) NOT NULL DEFAULT '0.00',
  `top_p` int(10) NOT NULL DEFAULT '0',
  `top_rp` int(10) NOT NULL DEFAULT '0',
  `tf` int(10) NOT NULL DEFAULT '0',
  `sf` int(10) NOT NULL DEFAULT '0',
  `pjf` int(10) NOT NULL DEFAULT '0',
  `rtf` decimal(4,2) NOT NULL DEFAULT '0.00',
  `rsf` decimal(5,2) NOT NULL DEFAULT '0.00',
  `top_rtf` int(10) NOT NULL DEFAULT '0',
  `top_rsf` int(10) NOT NULL DEFAULT '0',
  `top_pjf` int(10) NOT NULL DEFAULT '0',
  `td` int(10) NOT NULL DEFAULT '0',
  `sd` int(10) NOT NULL DEFAULT '0',
  `pjd` int(10) NOT NULL DEFAULT '0',
  `rtd` decimal(4,2) NOT NULL DEFAULT '0.00',
  `rsd` decimal(5,2) NOT NULL DEFAULT '0.00',
  `top_rtd` int(10) NOT NULL DEFAULT '0',
  `top_rsd` int(10) NOT NULL DEFAULT '0',
  `top_pjd` int(10) NOT NULL DEFAULT '0',
  `tt` bigint(20) NOT NULL DEFAULT '0',
  `pjn` int(10) NOT NULL DEFAULT '0',
  `tm` decimal(5,2) NOT NULL DEFAULT '0.00',
  `nr` int(10) NOT NULL DEFAULT '0',
  `top_nr` int(10) NOT NULL DEFAULT '0',
  `top_tm` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`n`),
  KEY `top_rs` (`top_rs`),
  KEY `top_p` (`top_p`),
  KEY `top_rp` (`top_rp`),
  KEY `top_rtf` (`top_rtf`),
  KEY `top_rsf` (`top_rsf`),
  KEY `top_pjf` (`top_pjf`),
  KEY `top_rtd` (`top_rtd`),
  KEY `top_rsd` (`top_rsd`),
  KEY `top_pjd` (`top_pjd`),
  KEY `top_nr` (`top_nr`),
  KEY `top_tm` (`top_tm`),
  KEY `top_pg` (`top_pg`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tmp_event_10ans`
--

DROP TABLE IF EXISTS `tmp_event_10ans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tmp_event_10ans` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `flag_anim_dispo` int(10) unsigned NOT NULL DEFAULT '0',
  `flag_anim` int(10) unsigned NOT NULL DEFAULT '0',
  `pj_a` int(10) unsigned NOT NULL DEFAULT '0',
  `pj_b` int(10) unsigned NOT NULL DEFAULT '0',
  `pj_f` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-07-29 15:01:43
