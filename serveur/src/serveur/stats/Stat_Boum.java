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
import java.sql.ResultSet;
import java.sql.SQLException;

import joueur.Joueur;
import serveur.Serveur;

public class Stat_Boum extends Stat {

	// PartieGagnée, MancheGagnée, Kill, TuéParLesAutres, Suicides
	
	public Stat_Boum() {
		super();
		LongName = "Bouboum Officiel";
	}
	
	
	public String display() {
		return super.display() 
				+ "Parties gagnées : " + PartieGagnée + "\n"
				+ "Manches gagnées : " + MancheGagnée + "\n"
				+ "Kill : "+ Kill + "\n"
				+ "Tué par les autres : "+ TuéParLesAutres + "\n" 
				+ "Suicides : " + Suicide +"\n";
	}
	
	public void toFlash() {
		super.toFlash();
		addField(PartieGagnée);
		addField(MancheGagnée);
		addField(Kill);
		addField(TuéParLesAutres);
		addField(Suicide);
	}
	
	public void internalLoad(Serveur Serveur, Joueur JOUEUR) throws SQLException {
		PreparedStatement PrepSt = Serveur.Bdd.Requete_SelectStatsBoum;
		PrepSt.setString(1, JOUEUR.NomJoueur);
		ResultSet Result = PrepSt.executeQuery();
		if (Result.next()) {
			//
			NewStat = false;
			TempsDeJeu = Result.getLong("tps_jeu");
			Partie = Result.getInt("parties");
			PartieGagnée = Result.getInt("win");
			MancheGagnée = Result.getInt("manche");
			Kill = Result.getInt("kil");
			Suicide = Result.getInt("suicide");
			TuéParLesAutres = Result.getInt("kil_by_others");
		}		
	}
	
	public void internalSave(Serveur Serveur, Joueur JOUEUR) throws SQLException {
		// UPDATE $stat_boum SET tps_jeu=?,parties=?,win=?,manche=?,kil=?,suicide=?,kill_by_others=? WHERE nom=? and type=?
		PreparedStatement PrepSt = Serveur.Bdd.Requete_UpdateStatsBoum;
		PrepSt.setLong(1, TempsDeJeu);
		PrepSt.setInt(2, Partie);
		PrepSt.setInt(3, PartieGagnée);
		PrepSt.setInt(4, MancheGagnée);
		PrepSt.setInt(5, Kill);
		PrepSt.setInt(6, Suicide);
		PrepSt.setInt(7, TuéParLesAutres);
		PrepSt.setString(8, JOUEUR.NomJoueur);
		PrepSt.setString(9, Stat.TYPE_MOIS);
		PrepSt.executeUpdate();	
	}
	
	public PreparedStatement internalNew(Serveur Serveur) {
		return Serveur.Bdd.Requete_NewStatsBoum;
	}	
	
}



