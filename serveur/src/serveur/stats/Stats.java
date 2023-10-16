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

import aaaah.Carte;
import joueur.Joueur;
import serveur.Boite;
import serveur.Serveur;

public class Stats {

	public String Type; // courant, record, mensuel, total
	public Stat_Run RUN = new Stat_Run();
	public Stat_Ng NG = new Stat_Ng();
	public Stat_Def DEF = new Stat_Def();
	public Stat_Fs FS = new Stat_Fs();
	public Stat_Gdm GDM = new Stat_Gdm();
	public Stat_Ms MS = new Stat_Ms();
	public Stat_Boum BOUM = new Stat_Boum();
	public Stat_Forto FORTO = new Stat_Forto();
	public Stat_Rally RALLY = new Stat_Rally();
	
	public Stat[] Liste = { BOUM, RUN, GDM, DEF, NG, RALLY, FS, MS, FORTO};
	
	public void Reset() {
		RUN = new Stat_Run();
		NG = new Stat_Ng();
		DEF = new Stat_Def();
		FS = new Stat_Fs();
		GDM = new Stat_Gdm();
		MS = new Stat_Ms();
		BOUM = new Stat_Boum();
		FORTO = new Stat_Forto();
		RALLY = new Stat_Rally();
		//ugly reset of Liste
		Liste[0] = BOUM;
		Liste[1] = RUN;
		Liste[2] = GDM;
		Liste[3] = DEF;
		Liste[4] = NG;
		Liste[5] = RALLY;
		Liste[6] = FS;
		Liste[7] = MS;
		Liste[8] = FORTO;
	}
	
	public void NouvellePartie(int ModeJeu) {
		Stat stat = statByMode(ModeJeu);
		if (stat!=null) {
			stat.NouvellePartie();
		}
	}
	
	public void NouveauKill(int ModeJeu) {
		Stat massacre = statByMode(ModeJeu);
		if (massacre!=null) {
			massacre.Kill++;
		}
	}

	public Stat statByGame(serveur.Partie Partie) {
		if (Partie != null) {
			if (Partie.PAaaah) {
				return statByMode(Partie.getJeu().ModeJeu);
			} else if (Partie.Bouboum) {
				return BOUM;
			} else if (Partie.Forto) {
				return FORTO;
			}
		}
		return null;
	}
	
	public Stat statByMode(int ModeJeu) {
		if (ModeJeu == Carte.MODE_NG) {
			return NG;
		} else if (ModeJeu == Carte.MODE_RALLY) {
			return RALLY;
		} else if (ModeJeu == Carte.MODE_DEF) {
			return DEF;
		} else if (ModeJeu == Carte.MODE_NORMAL || ModeJeu == Carte.MODE_OFFI) {
			return RUN;
		} else if (ModeJeu == Carte.MODE_FIGHT) {
			return FS;
		} else if (ModeJeu == Carte.MODE_MS) {
			return MS;
		}
		return null;
	}	

	
	public String display() {
		String res = "";
		for (Stat stat : Liste) {
			res += stat.display() + "\n";
		}
		return res;
	}
	
	public String toFlash() {
		StringBuilder Res = new StringBuilder();
		for (Stat stat : Liste) {
			Res.append(Serveur.$);
			stat.Chaine = new StringBuilder();
			stat.toFlash();
			Res.append(stat.Chaine.toString());
		}
		return Res.toString();
	}
	
	public void save(Boite BOITE, Joueur JOUEUR) {
		for (Stat stat : Liste) {
			stat.save(BOITE,JOUEUR);
		}
	}
	
	public void load(Boite BOITE, Joueur JOUEUR) {
		for (Stat stat : Liste) {
			stat.load(BOITE,JOUEUR);
		}
	}	
}
