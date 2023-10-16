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

package serveur.stats;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import joueur.Joueur;
import serveur.Boite;
import serveur.Serveur;

public class Stat {
	
	public final static String TYPE_COURANT = "courant";
	public final static String TYPE_RECORD = "record";
	public final static String TYPE_MOIS = "mois";
	public final static String TYPE_TOTAL = "total";
	public final static String TYPE_ARCHIVE = "archive";
	
	// common
	private boolean Dirty = false;
	protected boolean NewStat = true;
	protected String LongName;  // virtual
	protected int LimitePartie = 100;
	public int Partie = 0;
	public int Kill = 0;
	public long TempsDeJeu = 0;
	public StringBuilder Chaine;

	// Boum
	public int TuéParLesAutres = 0;
	public int Suicide = 0;
	public int MancheGagnée = 0;
	public int PartieGagnée = 0;
	
	// Sauvetage
	public int JoueursSauvés = 0;
	public int JoueursGuidés = 0;
	
	// Course
	public int ArriveePremier = 0;
	public int ArriveeEnVie = 0;
	
	// FS
	public int Survie = 0;
	public int DernierSurvivant = 0;
	
	// NG
	public int NbRecords = 0;
	
	public void NouvellePartie() {
		Dirty = true;
		Partie++;
	}

	public void NouveauRecord() {
		Dirty = true;
		NbRecords++;
	}	
	
	public String display() {
		return LongName + "\nNombre de parties : " + Partie + "\n" + (TempsDeJeu / 3600000) + " heures de jeu.\n";
	}
	
	public void addField(Object o) {
		Chaine.append(Serveur.$);
		Chaine.append(o);		
	}
	
	public void toFlash() {
		addField(LongName);
		addField(Partie);
		//addField(TempsDeJeu / 3600000);
	}
	
	public void save(Boite BOITE, Joueur JOUEUR) {
		if (Dirty) {
			if (NewStat) {
				// INSERT
				BOITE.Requete(Boite.NEW_STAT, JOUEUR, this);
			}
			// UPDATE
			BOITE.Requete(Boite.SAUVEGARDE_STAT, JOUEUR, this);
			
		}
	}
	
	public void load(Boite BOITE, Joueur JOUEUR) {
		// SELECT
		BOITE.Requete(Boite.LOAD_STATS, JOUEUR, this);
	}
	
	public void internalLoad(Serveur Serveur, Joueur JOUEUR) throws SQLException {
	}
	
	public void internalSave(Serveur Serveur, Joueur JOUEUR) throws SQLException {
	}	
	
	public PreparedStatement internalNew(Serveur Serveur) {
		return null;
	}	
	
}
