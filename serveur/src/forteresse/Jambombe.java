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

package forteresse;

import joueur.Joueur;
import serveur.Serveur;

public class Jambombe {

	public boolean Active = true;
	private boolean Attache;
	private int CodeJoueurAttache;
	private int Px;
	private int Py;
	private String CodeJambon;
	private long TempsExplosion;
	private Serveur.Action Action;
	private Forteresse Partie;

	public Jambombe(Forteresse PARTIE, boolean ATTACHE, String CODE, String X, String Y, int CODE_AUTEUR) {
		Partie = PARTIE;
		TempsExplosion = System.currentTimeMillis() + 5000;
		Partie.NumJambon++;
		CodeJambon = "J" + CODE_AUTEUR + Partie.NumJambon;
		Partie.ListeJambon.add(this);
		Attache = ATTACHE;
		Px = Integer.parseInt(X);
		Py = Integer.parseInt(Y);
		
		String $ = Serveur.$;
		
		if (Attache) {
			CodeJoueurAttache = Integer.parseInt(CODE);
			Serveur.getServeur().Envoie(Partie.ListeJoueur, "9" + $ + CodeJambon + $ + CODE_AUTEUR + $ + X + $ + Y + $ + CODE);
		} else {
			Serveur.getServeur().Envoie(Partie.ListeJoueur, "9" + $ + CodeJambon + $ + CODE_AUTEUR + $ + X + $ + Y);
		}
		//
		Action = Serveur.getServeur().new Action(TempsExplosion);
		Action.ForteresseJambombe = true;
		Action.Jambombe = this;
	}

	public void Explosion(Joueur JOUEUR) {
		if (Active) {
			Active = false;
			Partie.ListeJambon.remove(this);
			if (JOUEUR != null) {
				Px += Integer.parseInt(JOUEUR._Forteresse.PosX);
				Py += Integer.parseInt(JOUEUR._Forteresse.PosY);
			}
			int Bx = (Px / 10) + 5;
			int By = (Py / 10) + 5;
			String $ = Serveur.$;
			
			Serveur.getServeur().Envoie(Partie.ListeJoueur, "10" + $ + CodeJambon + $ + Bx + $ + By);
			
			for (int X = Bx - 9; X < Bx; X++) {
				for (int Y = By - 9; Y < By; Y++) {
					if (X >= 0 && X < 200 && Y >= 0 && Y < 100) {
						if (Partie.ListeCase[X][Y] == Forteresse.CASE_CARRE_DESTRUCTIBLE) {
							Partie.ListeCase[X][Y] = Forteresse.CASE_VIDE_CONSTRUCTIBLE;
							//new CaseMorte(X, Y);
						} else if (Partie.ListeCase[X][Y] == Forteresse.CASE_CARRE_NONRECONSTRUCTIBLE) {
							Partie.ListeCase[X][Y] = Forteresse.CASE_VIDE_INCONSTRUCTIBLE;
						}
					}
				}
			}
		}
	}

	public void Boum() {
		try {
			Action = null;
			if (Attache) {
				int NbJoueur = Partie.ListeJoueur.size();
				for (int i = 0; i < NbJoueur; i++) {
					Joueur JoueurCible = Partie.ListeJoueur.get(i);
					if (JoueurCible.Vivant && JoueurCible._Forteresse.CodeJoueur == CodeJoueurAttache) {
						JoueurCible._Forteresse.ListeJambonExplosion.add(this);
						break;
					}
				}
				
				if (Partie.ListeJambon != null) { // Partie non détruite
					Partie.ListeJambon.remove(this);
				}
			} else {
				Explosion(null);
			}
		} catch (Exception e) {
			System.err.print("3XC Boum "); e.printStackTrace();
		}
	}
	
	public boolean estAttacheSur(Joueur J) {
		return (Attache && CodeJoueurAttache == J._Forteresse.CodeJoueur);
	}
}