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

package aaaah;

import java.util.ArrayList;
import java.util.Map.Entry;

import serveur.Serveur;

public class LancementPartieAaaah extends Thread {
	private Aaaah Partie;

	public LancementPartieAaaah(Aaaah _Partie) {
		Partie = _Partie;
		this.start();
	}

	public void run() {
		try {
			ArrayList<Long> Base = new ArrayList<Long>();
			if (!Partie.Playlist) {
				for(Entry<Long, Carte> entry : Serveur.getServeur().Aaaah_ListeCarte.entrySet()) {
					Long Id = entry.getKey();
					Carte Map = entry.getValue();
					// on ajoute la map si :
					if (Partie.ModeJeu == Carte.MODE_OFFI && Partie.partieMere.PartieElo) {
						// En partie elo, le mode offi = partie run
						if (Map.Mode == Carte.MODE_NORMAL || Map.Mode == Carte.MODE_OFFI) {
							Base.add(Id);
						}
					} else {
						if (Map.Mode==Partie.ModeJeu || // la map correspond au mode de jeu choisit
								(Partie.ModeJeu == Carte.MODE_NORMAL // Par défaut, sans mode spécifique
								&& (Map.Mode == Carte.MODE_OFFI
									|| Map.Mode == Carte.MODE_NG
									|| (Map.Mode == Carte.MODE_MS && (Math.random() < 0.25))))						
							) {
							Base.add(Id);
						}
					}
						
				}
			} else { // playlist
				if (Partie.PlaylistAléatoire) {
					// indirection
					for(Long CodeMap : Partie.BouclePlaylist) {
						Base.add(CodeMap);
					}
				} else {
					// directement dans la liste
					for(Long CodeMap : Partie.BouclePlaylist) {
						Partie.BoucleMonde.add(CodeMap);
					}
				}
			}
			if (!Partie.Playlist || Partie.PlaylistAléatoire) {
				while (Base.size() != 0) {
					int Valeur = (int) (Math.random() * Base.size());
					Partie.BoucleMonde.add(Base.get(Valeur));
					Base.remove(Valeur);
				}
			}
		} catch (Exception e) {
			System.err.print("3XC LancementPartie "); e.printStackTrace();
		}
	}
}
