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

package bouboum;

import joueur.Joueur;
import serveur.Serveur;

public class Bombe {
	
	public static final int BOMBE_NORMALE = 0;
	public static final int BOMBE_SUPER = 1;
	public static final int BOMBE_MAUVAIS_CONTACT = 2;
	public static final int BOMBE_ULTRA = 3;
	public static final int BOMBE_FANTOME = 4;
	public static final int BOMBE_FLOCON = 5;
	public static final int BOMBE_CUPIDONNE = 6;

	private Joueur Joueur;
	private int Puissance;
	private int Ligne;
	private int Type;
	private int Colonne;
	public int Propagation;
	public boolean Active = true;
	private long TempsZero;
	private long TempsExplosion;
	public Serveur.Action Action;
	private Bouboum Partie;

	public Bombe(Bouboum PARTIE, int PUISSANCE, int LIGNE, int COLONNE, Joueur JOUEUR, int PROPAGATION) {
		TempsZero = System.currentTimeMillis();
		TempsExplosion = TempsZero + JOUEUR._Bouboum.getDureeAvantExplosionBombes();
		Type = JOUEUR._Bouboum.getTypeBombe();
		Puissance = PUISSANCE;
		Ligne = LIGNE;
		Colonne = COLONNE;
		Joueur = JOUEUR;
		Propagation = PROPAGATION;
		Partie = PARTIE;
		
		Serveur.getServeur().Envoie(Partie.ListeJoueur, "BoP#" + LIGNE + "#" + COLONNE + "#" + JOUEUR.NomJoueur + "#" + Type);
		//
		Action = Serveur.getServeur().new Action(TempsExplosion);
		Action.BombeBouboum = true;
		Action.Bombe = this;
	}

	public void Boum() {
		if (Active) {
			Active = false;
			Action = null;
			Partie.ListeBombe.remove(this);
			Partie.Explosion(Ligne, Colonne, Puissance, Joueur, Propagation, Type);
		}
	}
	
	public boolean estTouchee(int LIGNE, int COLONNE) {
		return (Active && Ligne == LIGNE && Colonne == COLONNE);
	}
}
