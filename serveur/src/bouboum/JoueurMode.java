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

public class JoueurMode {
	protected JoueurBouboum jbouboum;
	protected int gainScore;
	
	public JoueurMode(JoueurBouboum jbouboum) {
		gainScore = 0;
		this.jbouboum = jbouboum;
	}
	
	public void suicide() {
		gainScore -= 2;
	}
	
	public void aTue(Joueur mort) {
		gainScore += 2;
	}
	
	public void estTuePar(Joueur tueur) {
		//gainScore -= 2;
	}
	
	public void aGagne() {
		gainScore += 10;
	}
	
	public int calculeEtResetGainScore() {
		int g = gainScore;
		gainScore = 0;
		return g;
	}
	
	public void reset() {
	}
	
	public JoueurMode cloneInstance(JoueurBouboum jbouboum) {
		return new JoueurMode(jbouboum);
	}
	
	public static JoueurMode instanceModeParDefaut() {
		return new JoueurMode(null);
	}
}
