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

public class ModeCiblePerso extends ModeBouboum {
	public ModeCiblePerso(Bouboum partie) {
		super(partie);
	}
	
	public static boolean ModeActivable(Bouboum partie) {
		return partie.ListeJoueur.size() >= 2;
	}
	
	public void appliquerMode() {
		int sz = partie.ListeJoueur.size();
		ArrayList<Integer> numJoueurs = new ArrayList<Integer>();
		int i, n;
		
		if (!ModeActivable(partie)) { // Pas de mode à moins de 2 joueurs
			return;
		}
		
		for (i = 0; i < sz; ++i) {
			numJoueurs.add(i);
		}
		
		// Attribution aléatoire d'une cible à chaque joueur
		int last_cible = 1;
		try {
			for (i = sz - 1; i >= 0; --i) {
				Joueur j = partie.ListeJoueur.get(i);
				JoueurBouboum jboum = j._Bouboum;
			
				n = numJoueurs.remove((int)(Math.random() * numJoueurs.size()));
				if (i == n) { // La cible est le joueur lui-même => on en génère une autre
					if (numJoueurs.size() != 0) { // Pas la dernière personne à attribuer
						n = numJoueurs.remove((int)(Math.random() * numJoueurs.size()));
						numJoueurs.add(i); // On remet <j> dans les cibles potentielles
					} else {
						//partie.ListeJoueur.get(1)._Bouboum.setCiblePerso(partie.ListeJoueur.get(n).getPseudo());
						partie.ListeJoueur.get(1)._Bouboum.setJoueurMode(new JoueurModeCiblePerso(jboum, partie.ListeJoueur.get(n).getPseudo()));
						n = last_cible;
					}
				}
			
				last_cible = n;
				jboum.setJoueurMode(new JoueurModeCiblePerso(jboum, partie.ListeJoueur.get(n).getPseudo()));
				//j._Bouboum.setCiblePerso(partie.ListeJoueur.get(n).getPseudo());
			}
		} catch (Exception e) {
			System.err.println("3XC appliquerMode"); e.printStackTrace();
		}
	}
	
	public int nombreBonusEnPlus() {
		return 1;
	}
	
	public int getBonusNumero(int n) {
		if (n == 1) {
			return Bonus.BONUS_CHASSEUR;
		}
		
		return Bonus.BONUS_AUCUN;
	}
	
	public int getRandomModeBonus() {
		return Bonus.BONUS_CHASSEUR;
	}
	
	public void setRandomBonus(Joueur j) {
		int b = getRandomModeBonus();
		
		j._Bouboum.setBonus(b);
		if (b == Bonus.BONUS_CHASSEUR && j._Bouboum.getJMode() instanceof JoueurModeCiblePerso) {
			((JoueurModeCiblePerso)j._Bouboum.getJMode()).obtentionBonusChasseur(j.getPartieBouboum());
		}
	}
	
	public int getBonusContributionRand() {
		return 15;
	}
	
	public class JoueurModeCiblePerso extends JoueurMode {
		String pseudoCible;
		boolean premiereCibleDejaTuee = false;
		
		public JoueurModeCiblePerso(JoueurBouboum jbouboum, String pseudoCible) {
			super(jbouboum);
			
			this.pseudoCible = pseudoCible;
			jbouboum.envoieAJoueur("MoC" + Bouboum.$ + pseudoCible);
		}
		
		private void changerCible(String cible) {
			pseudoCible = cible;
			jbouboum.envoieAJoueur("MoC" + Bouboum.$ + pseudoCible);
		}
		
		public void obtentionBonusChasseur(Bouboum partie) {
			if (partie == null) {
				return;
			}
			
			Joueur cible = Serveur.getServeur().Joueur(pseudoCible, partie.getListeJoueurs());
			
			if (cible == null || !cible.Vivant) {
				nouvelleCible(partie);
			}
		}
		
		private void nouvelleCible(Bouboum partie) {
			ArrayList<Joueur> lsJoueursPartie = partie.getListeJoueurs();
			int i = (int)(Math.random() * (partie.NombreJoueursVivants() - 1));
			Joueur joueur = null;
			
			for (int j = lsJoueursPartie.size() - 1; i != -1; --j) {
				joueur = lsJoueursPartie.get(j);
				
				if (joueur.Vivant && !joueur.getPseudo().equals(jbouboum.getPseudoOwner())) {
					--i;
				}
			}
			
			if (joueur != null) {
				changerCible(joueur.getPseudo());
			}
		}

		public void aTue(Joueur mort) {
			if (mort != null && pseudoCible.equals(mort.getPseudo())) {
				gainScore += 8;
				
				Bonus b = new Bonus();
				
				//JoueurBouboum JBMort = mort._Bouboum;
				
				if (!premiereCibleDejaTuee) {
					premiereCibleDejaTuee = true;
					
					b.setBonus(Bonus.BONUS_VITESSE);
					jbouboum.setBonusSupplementaire(b);
				} else {
					jbouboum.addNbBombes(5);
					jbouboum.addPuissance(5);
				}
				
				/*if (JBMort.aBonus(Bonus.BONUS_SUPERBOMBE, true)) {
					b.setBonus(bouboum.Bonus.BONUS_SUPERBOMBE);
				} else if (JBMort.aBonus(Bonus.BONUS_ULTRABOMBE, true)) {
					b.setBonus(bouboum.Bonus.BONUS_ULTRABOMBE);
				} else {
					b.setBonus(bouboum.Bonus.BONUS_VITESSE);
				}*/
				
				
			}
			
			super.aTue(mort);
		}
		
		public void reset() {
			jbouboum.setBonusSupplementaire(null);
			jbouboum.envoieAJoueur("MoC" + Bouboum.$);
		}
		
		public JoueurMode cloneInstance(JoueurBouboum jbouboum) {
			return new JoueurModeCiblePerso(jbouboum, "");
		}
	}
}
