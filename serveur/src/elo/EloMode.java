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

import java.sql.ResultSet;

public class EloMode {
	public int elo_i;
	public int pj_i;
	public int pg_i;
	
	public int elo_d;
	public int pj_d;
	public int pg_d;
	
	public int elo_m;
	public int pj_m;
	public int pg_m;
	
	public EloMode() {
	}
	
	public void Init(boolean individuel, boolean duo, boolean multi) {
		// Le joueur n'avait pas d'elo avant
		if (individuel) {
			this.elo_i = Elo.POINTS_DEBUT_ELO;
			this.pj_i = 0;
			this.pg_i = 0;
		}
		
		if (duo) {
			this.elo_d = Elo.POINTS_DEBUT_ELO;
			this.pj_d = 0;
			this.pg_d = 0;
		}
		
		if (multi) {
			this.elo_m = Elo.POINTS_DEBUT_ELO;
			this.pj_m = 0;
			this.pg_m = 0;
		}
	}
	
	public void InitJoueur(ResultSet res, String colonne) throws Exception {
		InitJoueur(res, colonne, true, true, true);
	}
	
	public void InitJoueur(ResultSet res, String colonne, boolean individuel, boolean duo, boolean multi) throws Exception {
		if (individuel) {
			this.elo_i = res.getInt(colonne + "_ie");
			this.pj_i = res.getInt(colonne + "_ipj");
			this.pg_i = res.getInt(colonne + "_iv");
		}
		
		if (duo) {
			this.elo_d = res.getInt(colonne + "_de");
			this.pj_d = res.getInt(colonne + "_dpj");
			this.pg_d = res.getInt(colonne + "_dv");
		}
		
		if (multi) {
			this.elo_m = res.getInt(colonne + "_me");
			this.pj_m = res.getInt(colonne + "_mpj");
			this.pg_m = res.getInt(colonne + "_mv");
		}
	}
	
	public void InitTeam(ResultSet res, String colonne) throws Exception {
		this.elo_m = res.getInt(colonne + "_e");
		this.pj_m = res.getInt(colonne + "_pj");
		this.pg_m = res.getInt(colonne + "_v");
	}
}