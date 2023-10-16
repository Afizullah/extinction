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

public class JoueurAaaah {

	public String PosX = "40";
	public String PosY = "0";
	public boolean Guide = false;
	public boolean Banni = false;
	public boolean Muet = false;
	public boolean PeurDispo = false;
	public long IntervalPeur = 0;
	public long CodeCarte = 0;
	public boolean VoteDisponible = false;
	public int Score = 0;
	public boolean Zombie = false;
	public String CodeZombie = "0";
	public int Stats_PartieJouée = 0;
	public int Stats_PartieGuide = 0;
	public int Stats_PartieGuideInit = 0;
	public int Stats_JoueursSauvés = 0;
	public int Stats_JoueursSauvésInit = 0;
	public int Stats_JoueursGuidés = 0;
	public int Stats_JoueursGuidésInit = 0;
	public int Stats_PartiePremier = 0;
	public int Stats_PartieFinie = 0;
	public int Stats_Tué = 0;
	public int Stats_RatioSauvetage = 0;
	public int Stats_RatioPremier = 0;
	public int Stats_RatioSurvivant = 0;
	public int Stats_RatioTueur = 0;
	public int Stats_Elo = 0;
	// Anciennes stats (celles d'avant le reset de la MàJ 0.84)
	public int Stats_OldGuidés = -1; // -1 <=> pas d'anciennes stats enregistrées
	public int Stats_OldPGuidés = -1; // %
	public int Stats_OldPremier = -1;
	public int Stats_OldPPremier = -1; // %
	
	public int Stats_OldJouées = 0;
	public int Stats_OldSauvés = 0;
	public int Stats_OldJGuidés = 0;
	

	public JoueurAaaah() {
	}

	public void Maj_RatioSauvetage() {
		if (Stats_JoueursGuidés == 0) {
			Stats_RatioSauvetage = 0;
		} else {
			Stats_RatioSauvetage = (int) (((double) Stats_JoueursSauvés / Stats_JoueursGuidés) * 10000);
		}
	}

	public void Maj_RatioPremier() {
		if (Stats_PartieJouée == 0) {
			Stats_RatioPremier = 0;
		} else {
			Stats_RatioPremier = (int) (((double) Stats_PartiePremier / Stats_PartieJouée) * 10000);
		}
	}

	public void Maj_RatioTueur() {
		if (Stats_PartieJouée == 0) {
			Stats_RatioTueur = 0;
		} else {
			Stats_RatioTueur = (int) (((double) Stats_Tué / Stats_PartieJouée) * 10000);
		}
	}

	public void Maj_RatioSurvivant() {
		if (Stats_PartieJouée == 0) {
			Stats_RatioSurvivant = 0;
		} else {
			Stats_RatioSurvivant = (int) (((double) Stats_PartieFinie / Stats_PartieJouée) * 10000);
		}
	}

	public void Maj_Elo() {

		int Malus = 10;
		int JoueursSauvés = Stats_JoueursSauvés - Stats_JoueursSauvésInit;
		int JoueursGuidés = Stats_JoueursGuidés - Stats_JoueursGuidésInit;
		int Guidages = Stats_PartieGuide - Stats_PartieGuideInit;
		int NbJoueursParPartie = 20;
		int JoueursGuidésThéorique = Guidages * NbJoueursParPartie;
		if (Guidages > 0 && JoueursGuidés > 0) {
			double RatioFacilité = ((double) JoueursGuidés) / JoueursGuidésThéorique;
			double CoeffFacilité = Math.pow(RatioFacilité, 2);
			double Efficacité = ((double) JoueursSauvés) / JoueursGuidés;
			double Pondération = CoeffFacilité * Guidages * Math.pow(Efficacité, 10);
			int Bonus = 40;
			if (Guidages >= 7) {
				Bonus = 50;
			}

			double Gain = Bonus * Pondération - Malus;
			Stats_Elo += Gain;
			//System.out.println("ST4TS " + Guidages + " " + JoueursSauvés + " " + JoueursGuidés + " " + Efficacité + " " + Gain + " " + Stats_Elo);
		}
		if (Stats_Elo < 0) {
			Stats_Elo = 0;
		}
	}
}