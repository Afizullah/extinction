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
public class JoueurBouboum {
	private final Joueur joueur;

	public String Colonne;
	public String Ligne;
	public int BombeEnJeu;
	public int PuissanceBombe;
	public int BombeDisponible;
	public int Stats_PartieJouée = 0;
	public int Stats_PartieGagnée = 0;
	public int Stats_Tué = 0;
	public int Stats_Mort = 1;
	public int Stats_Ratio = 0;
	// Anciennes stats (celles d'avant le reset de la MàJ 0.84)
	public int Stats_OldGagnées = -1; // -1 <=> pas d'anciennes stats enregistrées
	public int Stats_OldPGagnées = -1; // %
	public int Stats_OldJouées = -1;
	
	public int Score = 0;
	private Bonus Bonus;
	private Bonus BonusSupplementaire = null;
	
	// Mode Cible Perso (cible aléatoire attribuée aux joueurs) :
	private JoueurMode jmode;
	

	public JoueurBouboum(Joueur J) {
		Bonus = new Bonus();
		joueur = J;
		jmode = new JoueurMode(this);
	}

	public void Maj_Ratio() {
		if (Stats_Mort == 0) {
			Stats_Ratio = 0;
		} else {
			Stats_Ratio = (int) (((double) Stats_Tué / Stats_Mort) * 100);
		}
	}

	public void Reset_Bonus() {
		Bonus.resetBonus();
	}
	
	public void Reset_Bonus_WithAllIn(int AllInBonus) {
		Reset_Bonus();
		setBonus(AllInBonus);
	}
	
	public int getTypeBombe() {
		return Bonus.getTypeBombe();
	}
	
	public int getDureeAvantExplosionBombes() {
		return Bonus.getDureeBombeBonus();
	}
	
	public void addPuissance(int puissance) {
		PuissanceBombe += puissance;
		joueur.Envoie("BoU#1#5");
	}
	
	public void addNbBombes(int nbBombes) {
		BombeDisponible += nbBombes;
		joueur.Envoie("BoU#0#5");
	}
	
	public boolean aBonus(int bonus, boolean principalUniquement) {
		return Bonus.equals(bonus) || (!principalUniquement && Bonus.equals(bonus));
	}
	
	public void setBonus(int bonus) {
		Bonus.setBonus(bonus);
	}
	
	
	public void setBonusSupplementaire(Bonus bonus) {
		BonusSupplementaire = bonus;
		
		if (BonusSupplementaire != null) {
			Bouboum b = joueur.getPartieBouboum();
			if (b != null) {
				Serveur.getServeur().Envoie(b.ListeJoueur, bonus.messageChangementBonus(joueur, true));
			}
		}
	}
	
	public void setRandomBonus() {
		Bouboum p = joueur.getPartieBouboum();
		if (p != null) {
			Bonus.setRandomBonus(joueur, p.getModeEnCours());
		}
	}
	
	public void envoieAJoueur(String msg) {
		joueur.Envoie(msg);
	}
	//
	public void setJoueurMode(JoueurMode jmode) {
		this.jmode.reset();
		this.jmode = jmode;
	}
	
	public JoueurMode getJMode() {
		return jmode;
	}
	
	public void aTue(Joueur mort) {
		if (mort != joueur) { // Suicide géré par "estTuePar()"
			jmode.aTue(mort);
		}
	}
	
	public void estTuePar(Joueur tueur) {
		if (tueur == joueur) {
			jmode.suicide();
		} else {
			jmode.estTuePar(tueur);
		}
	}
	
	public void aGagne() {
		jmode.aGagne();
	}
	
	/**
	 * Actualise <Score> en se basant sur les gains et pertes (tuer, mourir, ...)
	 * obtenues durant le mode de jeu en cours.
	 */
	public void actualiserScore() {
		Score = Math.max(0, Score + jmode.calculeEtResetGainScore());
	}
	
	public String getPseudoOwner() {
		return joueur.getPseudo();
	}
}