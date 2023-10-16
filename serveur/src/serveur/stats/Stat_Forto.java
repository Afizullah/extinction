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

public class Stat_Forto extends Stat {

	public Stat_Forto() {
		super();
		LongName = "Forteresse";
	}
	
	public void internalLoad(Serveur Serveur, Joueur JOUEUR) throws SQLException {
		PreparedStatement PrepSt = Serveur.Bdd.Requete_SelectStatsForto;
		PrepSt.setString(1, JOUEUR.NomJoueur);
		ResultSet Result = PrepSt.executeQuery();
		if (Result.next()) {
			//
			NewStat = false;
			TempsDeJeu = Result.getLong("tps_jeu");
			Partie = Result.getInt("parties");
		}		
	}
	
	public void internalSave(Serveur Serveur, Joueur JOUEUR) throws SQLException {
		PreparedStatement PrepSt = Serveur.Bdd.Requete_UpdateStatsForto;
		PrepSt.setLong(1, TempsDeJeu);
		PrepSt.setInt(2, Partie);
		PrepSt.setString(3, JOUEUR.NomJoueur);
		PrepSt.setString(4, Stat.TYPE_MOIS);
		PrepSt.executeUpdate();	
	}
	
	public PreparedStatement internalNew(Serveur Serveur) {
		return Serveur.Bdd.Requete_NewStatsForto;
	}	

}


