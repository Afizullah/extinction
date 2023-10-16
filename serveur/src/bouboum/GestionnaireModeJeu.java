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

public class GestionnaireModeJeu {
	private ModeBouboum mode;
	private ModeBouboum modeBase;
	private ModeBouboum nouvModeEnAttente = null; // Contient le prochain mode à mettre par défaut à la prochaine partie
	private boolean lockMode;
	
	private int modeCounter = 0;
	
	public GestionnaireModeJeu(ModeBouboum mode) {
		this.mode = mode;
		this.modeBase = mode;
		this.lockMode = false;
	}
	
	public void lockMode() {
		lockMode = true;
	}
	
	public void unlockMode() {
		lockMode = false;
	}
	
	private boolean modeLocked() {
		return lockMode == true;
	}
	
	public void setNextMode(Bouboum partie) {
		if (nouvModeEnAttente != null) {
			modeBase = nouvModeEnAttente;
			mode = modeBase;
			
			appliquerMode(partie, nouvModeEnAttente);
			nouvModeEnAttente = null;
		}
		
		if (modeLocked()) {
			return;
		}
		
		ModeBouboum oldMode = mode;
		
		++modeCounter;
		int mc = modeCounter % 10;
		
		if (mc == 9 && ModeCiblePerso.ModeActivable(partie)) {
			mode = new ModeCiblePerso(partie);
		} else if (mc == 2 && ((int)(modeCounter / 20)) % 2 == 1 && ModeTeam.ModeActivable(partie)) {
			mode = new ModeTeam(partie);
		} else {
			if (!(mode.getClass().equals(modeBase.getClass()))) {
				mode = modeBase;
			}
		}
		
		appliquerMode(partie, oldMode);
	}
	
	public ModeBouboum getModeEnCours() {
		return mode;
	}
	
	/**
	 * Applique le mode en cours <this.mode> et désactive les effets
	 * de l'ancien mode <oldMode> pour la partie <partie>.
	 */
	public synchronized void appliquerMode(Bouboum partie, ModeBouboum oldMode) {
		oldMode.resetMode();
		mode.appliquerMode();
	}
	
	/**
	 * Mettre <mode> comme le mode de base.
	 * Prend effet à la fin de la partie en cours.
	 */
	public void setModeBase(ModeBouboum mode) {
		nouvModeEnAttente = mode;
	}
	
	/**
	 * Indique si une fin prématurée est nécessaire (ex. : il ne reste qu'une équipe
	 * en vie pour le mode team).
	 */
	public boolean finPartieSiBesoin() {
		return mode.finPartieSiBesoin();
	}
}
