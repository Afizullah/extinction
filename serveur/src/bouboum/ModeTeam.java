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
import serveur.Serveur;

public class ModeTeam extends ModeBouboum {
	public ModeTeam(Bouboum partie) {
		super(partie);
	}
	
	public static boolean ModeActivable(Bouboum partie) {
		return partie.ListeJoueur.size() >= 4;
	}

	public void appliquerMode() {
		int sz = partie.ListeJoueur.size();
		int i;
		
		int nbActuelJEquipe0 = 0;
		int nbActuelJEquipe1 = 0;
		int nbJParEquipe = sz / 2;
		
		StringBuilder msg = new StringBuilder("MoT");
		
		if (!ModeActivable(partie)) { // Pas de mode à moins de 8 joueurs
			return;
		}
		
		try {
			int equipe;
			for (i = sz - 1; i >= 0; --i) {
				Joueur j = partie.ListeJoueur.get(i);
				JoueurBouboum jboum = j._Bouboum;
				
				if (nbActuelJEquipe0 < nbJParEquipe) {
					if (nbActuelJEquipe1 < nbJParEquipe) {
						equipe = (int)(Math.random() * 2);
					} else {
						equipe = 0;
					}
				} else {
					equipe = 1;
				}
				
				if (equipe == 0) {
					nbActuelJEquipe0++;
				} else {
					nbActuelJEquipe1++;
				}
				
				jboum.setJoueurMode(new JoueurModeTeam(jboum, equipe));
				
				msg.append(Bouboum.$);
				msg.append(j.getPseudo());
				msg.append(",");
				msg.append(equipe);
			}
			
			Serveur.getServeur().Envoie(partie.ListeJoueur, msg.toString());
		} catch (Exception e) {
			System.err.println("3XC appliquerMode"); e.printStackTrace();
		}
	}

	public void resetMode() {
		super.resetMode();
		Serveur.getServeur().Envoie(partie.ListeJoueur, "MoT");
	}
	
	/**
	 * Termine la partie s'il ne reste qu'une seule équipe en vie.
	 */
	public boolean finPartieSiBesoin() {
		Joueur j;
		JoueurMode jmode;
		JoueurModeTeam jModeTeam;
		int equipeID = -1, e;
		ArrayList<Joueur> lsJDerniereEqp = new ArrayList<Joueur>();
		
		// On regarde si tous les joueurs en vie sont dans une même équipe
		for (int i = partie.ListeJoueur.size() - 1; i >= 0; --i) {
			j = partie.ListeJoueur.get(i);
			
			if (j.Vivant) {
				jmode = j._Bouboum.getJMode();
				
				if (jmode instanceof JoueurModeTeam) {
					jModeTeam = (JoueurModeTeam)jmode;
					e = jModeTeam.getEquipeID();
					
					if (equipeID == -1) {
						equipeID = e;
					} else if (e != equipeID) {
						return false;
					}
					
					lsJDerniereEqp.add(j);
				} else {
					return false;
				}
			}
		}
		
		// On fait gagner tous les joueurs de l'équipe qui le méritent
		for (int i = lsJDerniereEqp.size() - 1; i >= 0; --i) {
			j = lsJDerniereEqp.get(i);
			
			if (((JoueurModeTeam)j._Bouboum.getJMode()).meriteVictoire()) {
				partie.VictoireJoueur(j);
			}
		}
		
		Serveur.getServeur().Envoie(partie.ListeJoueur, "MoTV" + Bouboum.$ + equipeID);
		
		return true;
	}
	
	public class JoueurModeTeam extends JoueurMode {
		private int equipeID;
		private int adversairesTues = 0;
		
		public JoueurModeTeam(JoueurBouboum jbouboum, int equipeID) {
			super(jbouboum);
			this.equipeID = equipeID;
		}
		
		public void aTue(Joueur mort) {
			super.aTue(mort);
			
			JoueurMode jmode = mort._Bouboum.getJMode();
			if (jmode instanceof JoueurModeTeam) {
				if (((JoueurModeTeam)jmode).equipeAdverse(equipeID)) {
					++adversairesTues;
				}
			}
		}
		
		public boolean equipeAdverse(int equipeID) {
			return this.equipeID != equipeID;
		}
		
		public boolean meriteVictoire() {
			return (adversairesTues >= 1); // Note : doit être vivant aussi
		}
		
		public int getEquipeID() {
			return equipeID;
		}
		
		public JoueurMode cloneInstance(JoueurBouboum jbouboum) {
			return new JoueurModeTeam(jbouboum, 0);
		}
	}

}
