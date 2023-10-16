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

import evenement.Evenement;
import joueur.Joueur;
import serveur.Serveur;

public class Bonus {
	public final static int BONUS_UNDEFINED = 0;
	public final static int BONUS_SUPERBOMBE = 1;
	public final static int BONUS_MAUVAISCONTACT = 2;
	public final static int BONUS_POSEAUTO = 3;
	public final static int BONUS_AUCUN = 4;
	public final static int BONUS_LENTEUR = 5;
	public final static int BONUS_VITESSE = 6;
	public final static int BONUS_ULTRABOMBE = 7;
	public final static int BONUS_ALLIN = 8;
	public final static int BONUS_BOMBEFANTOME = 9;
	
	public final static int MAX_BONUS_NO_EVENT = 9;
	//
	private final static int BONUS_DE_MODE = 50;
	public final static int BONUS_CHASSEUR = BONUS_AUCUN;//50;
	//
	private final static int BONUS_EVENT = 90;
	public final static int BONUS_BOMBECUPIDONNE = 90;
	public final static int BONUS_BOMBEFLOCON = 91;
	public final static int BONUS_CODE = 92;
	
	private int bonus;
	
	public Bonus() {
		bonus = BONUS_UNDEFINED;
	}
	
	public boolean equals(int bonus) {
		return this.bonus == bonus;
	}
	
	public Bonus dupliquer() {
		Bonus b = new Bonus();
		b.setBonus(bonus);
		
		return b;
	}
	
	public void setBonus(int bonus) {
		this.bonus = bonus;
		
		if (estBonusDeMode(bonus)) {
			//
		}
	}
	
	private static boolean estBonusDeMode(int bonus) {
		return bonus >= BONUS_DE_MODE && bonus < BONUS_EVENT;
	}
	
	public void resetBonus() {
		bonus = BONUS_AUCUN;
	}
	
	public int getTypeBombe() {
		if (bonus == BONUS_SUPERBOMBE) {
			return Bombe.BOMBE_SUPER;
		} else if (bonus == BONUS_MAUVAISCONTACT) {
			return Bombe.BOMBE_MAUVAIS_CONTACT;
		} else if (bonus == BONUS_ULTRABOMBE) {
			return Bombe.BOMBE_ULTRA;
		} else if (bonus == BONUS_BOMBEFANTOME) {
			return Bombe.BOMBE_FANTOME;
		} else if (bonus == BONUS_BOMBECUPIDONNE) {
			return Bombe.BOMBE_CUPIDONNE;
		} else if (bonus == BONUS_BOMBEFLOCON) {
			return Bombe.BOMBE_FLOCON;
		}
		
		return Bombe.BOMBE_NORMALE;
	}
	
	public boolean enPoseAuto() {
		return bonus == BONUS_POSEAUTO;
	}
	
	public int getDureeBombeBonus() {
		if (bonus == Bombe.BOMBE_MAUVAIS_CONTACT) {
			return 1000 + (int) (Math.random() * 7000);
		} else {
			return 3000;
		}
	}
	
	public String messageChangementBonus(Joueur J, boolean bonusSupplementaire) {
		return "ChB" + (bonusSupplementaire ? "S" : "") + "#" + bonus + "#" + J.getPseudo();
	}
	
	public void setRandomBonus(Joueur J, ModeBouboum mode) {
		boolean aEuAllIn = false;
		Bouboum PartieBoum = J.getPartieBouboum();
		if (PartieBoum == null) {
			return;
		}
		
		resetBonus();
		
		Evenement event = Serveur.getServeur().Evénement;
		
		boolean eventAvecBonus = event.aBonusSpeciauxBoum();
		final int coeffBase = 100, coeffEvent = event.getCoeffBonusBoum();
		int coeff = coeffBase + coeffEvent + mode.getBonusContributionRand();
		int rand = (int) (Math.random() * (coeff));
		
		if (rand < 20) {
			// Super Bombe
			setBonus(Bonus.BONUS_SUPERBOMBE);
		} else if (rand >= 20 && rand < 47) {
			// Mauvais contact
			setBonus(Bonus.BONUS_MAUVAISCONTACT);
		} else if (rand >= 47 && rand < 54) {
			// Lenteur
			setBonus(Bonus.BONUS_LENTEUR);
		} else  if (rand >= 54 && rand < 61) {
			// Vitesse
			setBonus(Bonus.BONUS_VITESSE);
		} else if (rand >= 61 && rand < 68 && PartieBoum.getTempsEcoulePartieMs() > 40000) {
			// Pose Automatique
			setBonus(Bonus.BONUS_POSEAUTO);
		} else if (rand >= 68 && rand <= 71) {
			// Ultra bombe
			setBonus(Bonus.BONUS_ULTRABOMBE);
		} else if (rand == 71 && (((int) (Math.random() * 4)) == 1)) { // chance divisée par 4
			// All in
			aEuAllIn = true;//setBonus(Bonus.BONUS_ALLIN);
			PartieBoum.SetAllIn(J.getPseudo(), Generer_Bonus_AllIn(mode));
		} else if (rand > 71 && rand <= 80) {
			// Bombe fantôme
			setBonus(Bonus.BONUS_BOMBEFANTOME);
		} else if (rand >= coeffBase) { // Bonus d'événement ou de mode de jeu
			rand -= coeffBase;
			if (eventAvecBonus && rand < coeffEvent) { // Bonus d'événement
				if (event.StValentin()) {
					rand -= event.getCoeffEventBonusBoum(Evenement.ST_VALENTIN);
					if (rand <= 0) {
						setBonus(Bonus.BONUS_BOMBECUPIDONNE);
					}
				}
				
				if (rand > 0 && event.Noel()) {
					rand -= event.getCoeffEventBonusBoum(Evenement.NOEL);
					if (rand <= 0) {
						setBonus(Bonus.BONUS_BOMBEFLOCON);
					}
				}
				/**
				if (rand > 0 && event.FeteAnnif10Ans()) {
					rand -= event.getCoeffEventBonusBoum(Evenement.FETES_10ANS);
					if (rand <= 0) {
						if (J.getPartieBouboum().PartieRecompensable()) {
							setBonus(Bonus.BONUS_CODE);
							J.joueurEv.addFlagDispoAnim(JoueurEv.ANIM10_B_BONUS);
						} else {
							setBonus(Bonus.BONUS_AUCUN);
						}
					}
				} **/
			} else {
				rand -= coeffEvent;
				if (rand < mode.getBonusContributionRand()) { // Bonus de mode de jeu
					mode.setRandomBonus(J);
				}
			}
			
		} else {
			setBonus(Bonus.BONUS_AUCUN);
		}
		
		if (!aEuAllIn) {
			Serveur.getServeur().Envoie(PartieBoum.ListeJoueur, messageChangementBonus(J, false));
		}
	}
	
	public static int Generer_Bonus_AllIn(ModeBouboum mode) {
		Evenement event = Serveur.getServeur().Evénement;
		int n = MAX_BONUS_NO_EVENT;
		int r;
		
		if (event.EvenementEnCours()) {
			if (event.StValentin()) {
				++n;
			}
			
			if (event.Noel()) {
				++n;
			}
		}
		
		n += mode.nombreBonusEnPlus();
		
		r = 1 + (int)(Math.random() * n);
		if (r == BONUS_ALLIN) {
			r = BONUS_AUCUN;
		} if (r > MAX_BONUS_NO_EVENT) {
			if (event.StValentin()) {
				if (r > 1) {
					--r;
				} else {
					return BONUS_BOMBECUPIDONNE;
				}
			}
			
			if (event.Noel()) {
				if (r > 1) {
					--r;
				} else {
					return BONUS_BOMBEFLOCON;
				}
			}
			
			return mode.getBonusNumero(r);
		}
		
		return r;
	}
	
	public static String NomBonus(int BONUS) {
		if (BONUS == BONUS_UNDEFINED) {
			return "off";
		} else if (BONUS == BONUS_SUPERBOMBE) {
			return "Super bombe";
		} else if (BONUS == BONUS_MAUVAISCONTACT) {
			return "Mauvais contact";
		}else if (BONUS == BONUS_POSEAUTO) {
			return "Pose automatique";
		} else if (BONUS == BONUS_AUCUN) {
			return "Aucun bonus";
		} else if (BONUS == BONUS_LENTEUR) {
			return "Lenteur";
		}else if (BONUS == BONUS_VITESSE) {
			return "Vitesse";
		} else if (BONUS == BONUS_ULTRABOMBE) {
			return "Ultra bombe";
		} else if (BONUS == BONUS_ALLIN) {
			return "All in";
		} else if (BONUS == BONUS_BOMBEFANTOME) {
			return "Bombe fantôme";
		} else if (BONUS == BONUS_BOMBECUPIDONNE) {
			return "Bombe cupidonne";
		} else if (BONUS == BONUS_BOMBEFLOCON) {
			return "Bombe flocon";
		} else if (BONUS == BONUS_CHASSEUR) {
			return "Chasseur";
		} else {
			return String.valueOf(BONUS);
		}
	}
	
	public boolean estMalus() {
		return (bonus == BONUS_LENTEUR || bonus == BONUS_MAUVAISCONTACT || bonus == BONUS_POSEAUTO);
	}
}
