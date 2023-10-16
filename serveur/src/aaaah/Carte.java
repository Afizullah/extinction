package aaaah;

import java.sql.ResultSet;
import java.util.ArrayList;

import serveur.Serveur;

public class Carte {

	public Long Id;
	public String Code; // 0
	public Integer Votes; // 1
	public Integer Pour; // 2
	public Integer Perma; // 3
	public Integer Record; // 4
	public String Recordman; // 5
	public Integer Played; // 6
	public Integer Survie; // 7
	public String Auteur; // 8
	public String Titre; // 9
	public Integer Mode; // 10
	public Integer Mara_Record = 120000;
	public String Mara_Recordman = "-";
	public Integer Mara_Played = 0;
	public String Ghost = "";

	// Mode : 0 = normal, 1 = officiel, 2 = ng, 3 = defilante, 4 = fight, 5 = MS, 6 = Rally

	public Carte( Long Id,
			String Code,
			Integer Votes,
			Integer Pour,
			Integer Perma,
			Integer Record,
			String Recordman,
			Integer Played,
			Integer Survie,
			String Auteur,
			String Titre,
			Integer Mode,
			Integer Mara_Record,
			String Mara_Recordman,
			Integer Mara_Played,
			String Ghost) {
		
		this.Id = Id;
		this.Auteur = Auteur;
		this.Titre = Titre;
		this.Code = Code;
		this.Votes = Votes;
		this.Pour = Pour;
		this.Perma = Perma;
		this.Record = Record;
		this.Recordman = Recordman;
		this.Played = Played;
		this.Survie = Survie;
		this.Mode = Mode;
		this.Mara_Record = Mara_Record;
		this.Mara_Recordman = Mara_Recordman;
		this.Mara_Played = Mara_Played;
		this.Ghost = (Ghost != null ? Ghost : "");
	}

	public static final int MODE_NORMAL = 0;
	public static final int MODE_OFFI = 1;
	public static final int MODE_NG = 2;
	public static final int MODE_DEF = 3;
	public static final int MODE_FIGHT = 4;
	public static final int MODE_MS = 5;
	public static final int MODE_RALLY = 6;

	public boolean isNoGuide() {
		return Mode==MODE_NG || Mode==MODE_DEF || Mode==MODE_MS || Mode==MODE_RALLY;
	}

	public String ModeMap() {
		if (Mode==MODE_NORMAL) {
			return "Normal";
		}else if (Mode==MODE_OFFI){
			return "Officielle";
		} else if (Mode==MODE_NG) {
			return "No guide";
		} else if (Mode==MODE_DEF) {
			return "Défilante";
		} else if (Mode==MODE_FIGHT) {
			return "Fight";
		} else if (Mode==MODE_MS) {
			return "MutualShout";
		} else if (Mode==MODE_RALLY) {
			return "Rally";
		} else {
			return "Autre";
		}
	}
	
	/**
	 * Ajouter une map Aaaah! à la liste des maps (sans doublons).
	 * 
	 * @param res : résultat de chargement d'une map (ne doit pas être vide)
	 * @param serveur : instance du serveur (Serveur.getServeur())
	 * @param AntiDouble : ArrayList contenant les codes des maps dont on veut vérifier
	 *                     que la map à ajouter (chargée via <res>) n'est pas pas une copie.
	 * 
	 * @throws Exception
	 */
    public static final void AjouterMap(ResultSet res, Serveur serveur, ArrayList<String> AntiDouble) throws Exception {
    	String Code = res.getString("code");
		
		if (!AntiDouble.contains(Code)) {
			AntiDouble.add(Code);
			
			AjouterMapAuServeur(ResultSetToCarte(res), serveur);
		}
    }
    
    /**
	 * Ajouter une map Aaaah! à la liste des maps (avec doublons potentiels).
	 * 
	 * @param res : résultat de chargement d'une map (ne doit pas être vide)
	 * @param serveur : instance du serveur (Serveur.getServeur())
	 * 
	 * @throws Exception
	 */
    public static final void AjouterMap(ResultSet res, Serveur serveur) throws Exception {
    	AjouterMapAuServeur(ResultSetToCarte(res), serveur);
    }
    
    private static final Carte ResultSetToCarte(ResultSet res) throws Exception {
    	Long Id = res.getLong("id");
		String Auteur = res.getString("auteur");
		String Titre = res.getString("titre");
		String Code = res.getString("code");
		Integer Votes = res.getInt("votes");
		Integer Pour = res.getInt("pour");
		Integer Perma = res.getInt("perma");
		Integer Record = res.getInt("record");
		String Recordman = res.getString("recordman");
		Integer Played = res.getInt("played");
		Integer Survie = res.getInt("survie");
		Integer Flag = res.getInt("mode");
		Integer Mara_Record = res.getInt("mara_record");
		String Mara_Recordman = res.getString("mara_recordman");
		Integer Mara_Played = res.getInt("mara_played");
		String Ghost = res.getString("ghost");
		
    	return new Carte(Id, Code, Votes, Pour, Perma, Record, Recordman, Played, Survie, Auteur, Titre, Flag, Mara_Record, Mara_Recordman, Mara_Played, Ghost);
    }
    
    public static final void AjouterMapAuServeur(Carte map, Serveur serveur) {
    	serveur.Aaaah_ListeCarte.put(map.Id, map);
    }
}