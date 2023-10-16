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

import java.util.ArrayList;

import joueur.Joueur;

public abstract class ModeBouboum {
	protected Bouboum partie;
	
	public ModeBouboum(Bouboum partie) {
		this.partie = partie;
	}
	
	public abstract void appliquerMode();
	
	public void resetMode() {
		appliquerJoueurMode(JoueurMode.instanceModeParDefaut());
	}
	
	public boolean finPartieSiBesoin() {
		return false;
	}
	
	/**
	 * Mettre un JoueurMode de la même classe que <instanceJMode> à tous les joueurs
	 * de <partie>.
	 */
	protected final void appliquerJoueurMode(JoueurMode instanceJMode) {
		ArrayList<Joueur> ls = partie.ListeJoueur;
		JoueurBouboum jboum;
		
		for (int i = ls.size() - 1; i >= 0; --i) {
			jboum = ls.get(i)._Bouboum;
			jboum.setJoueurMode(instanceJMode.cloneInstance(jboum));
		}
	}
	
	/**
	 * Retourne le nombre de bonus en plus présents dans le mode.
	 * @return
	 */
	public int nombreBonusEnPlus() {
		return 0;
	}
	
	/**
	 * Retourne le bonus numéro <n> du mode.
	 * Numérotation commençant à 1.
	 */
	public int getBonusNumero(int n) {
		return Bonus.BONUS_AUCUN;
	}
	
	/**
	 * Renvoie la contribution du mode à la génération aléatoire de bonus.
	 */
	public int getBonusContributionRand() {
		return 0;
	}
	
	/**
	 * Retourne un bonus du mode aléatoirement (% de chances pris en compte).
	 */
	public int getRandomModeBonus() {
		return Bonus.BONUS_AUCUN;
	}
	
	public void setRandomBonus(Joueur j) {
		j._Bouboum.setBonus(getRandomModeBonus());
	}
}
