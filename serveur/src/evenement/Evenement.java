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

package evenement;

import joueur.Joueur;
import serveur.Serveur;

public class Evenement {
	private int EvenementPrincipal;
	private int EvenementSecondaire; // Pas d'interactions spécifiques client-serveur possibles
	
	public static final int AUCUN = 0;
	public static final int HALLOWEEN = 1;
	public static final int NOEL = 2;
	public static final int NOUVEL_AN = 3;
	public static final int CARNAVAL = 4;
	public static final int PAQUES = 5;
	public static final int POISSON_AVRIL = 6;
	public static final int ST_VALENTIN = 7;
	public static final int STAURNAAAAHLES = 8;
	
	public Evenement(int Secondaire, int Principal) {
		EvenementPrincipal = Principal;
		EvenementSecondaire = Secondaire;
	}
	
	public void EnvoieEvenement(Joueur J) {
		EnvoieEvenement(true, J);
	}
	
	public void EnvoieEvenement(boolean EvPrincipal, Joueur J) {
		if (EvPrincipal) {
			EnvoieEvenement(EvenementPrincipal, J);
		} else {
			EnvoieEvenement(EvenementSecondaire, J);
		}
	}
	
	public boolean EnvoieEvenement(int Event, Joueur J) {
		switch(Event) {
			case AUCUN : // Aucun
				J.Envoie("CxNOEV");
				break;
			case HALLOWEEN : // Halloween
				J.Envoie("CxEVHLWN");
				break;
			case NOEL : // Noel
				J.Envoie("CxEVNOEL");
				break;
			case NOUVEL_AN : // Nouvel An
				J.Envoie("CxEVNVAN");
				break;
			case CARNAVAL : // Carnaval
				J.Envoie("CxEVCRVL");
				break;
			case PAQUES : // Pâques
				J.Envoie("CxEVPQS");
				break;
			case POISSON_AVRIL : // 1er Avril
				J.Envoie("CxEVAVR");
				break;
			case ST_VALENTIN : // Saint-Valentin
				J.Envoie("CxEVSTV");
				break;
			case STAURNAAAAHLES : // Saturnaaaahles
				J.Envoie("CxEVSAT");
				break;
			default :
				return false;
		}
		
		return true;
	}
	
	public void display(Joueur J) {
		J.Envoie("CxINFO#"
				+ "0 = normal\n"
				+ HALLOWEEN + " = Halloween\n"
				+ NOEL + " = Noël\n"
				+ NOUVEL_AN + " = Nouvel an\n"
				+ CARNAVAL + " = Carnaval\n"
				+ PAQUES + " = Pâques\n"
				+ POISSON_AVRIL + " = 1er Avril\n"
				+ ST_VALENTIN + " = St Valentin\n"
				+ STAURNAAAAHLES + " = Saturnaaaahles\n"
				+ "Événement en cours : " + EvenementPrincipal
				+ (EvenementSecondaire != 0 ? " (secondaire : " + EvenementSecondaire + ")" : ""));
	}
	
	public boolean hasSecondaire() {
		return EvenementSecondaire != 0;
	}
	
	private boolean hasOne(int Ev) {
		return (EvenementPrincipal == Ev || EvenementSecondaire == Ev);
	}
	
	public boolean EvenementEnCours() {
		return EvenementPrincipal != 0;
	}
	
	public boolean Halloween() {
		return hasOne(HALLOWEEN);
	}
	public boolean Noel() {
		return hasOne(NOEL);
	}
	public boolean NouvelAn() {
		return hasOne(NOUVEL_AN);
	}
	public boolean Carnaval() {
		return hasOne(CARNAVAL);
	}
	public boolean Paques() {
		return hasOne(PAQUES);
	}
	public boolean PoissonAvril() {
		return hasOne(POISSON_AVRIL);
	}
	public boolean StValentin() {
		return hasOne(ST_VALENTIN);
	}
	public boolean Saturnaaaahles() {
		return hasOne(STAURNAAAAHLES);
	}

	
	public void setEvenementPrincipal(int Ev) {
		EvenementPrincipal = Ev;
		
		Serveur.getServeur().cadeauxFortoActives = aCadeauxForto();
	}
	
	public void setEvenementSecondaire(int Ev) {
		EvenementSecondaire = Ev;
		
		Serveur.getServeur().cadeauxFortoActives = aCadeauxForto();
	}

	public boolean aBonusSpeciauxBoum() {
		return Noel() || StValentin();
	}
	
	public int getCoeffBonusBoum() {
		return getCoeffEventBonusBoum(EvenementPrincipal) + getCoeffEventBonusBoum(EvenementSecondaire);
	}
	
	public int getCoeffEventBonusBoum(int event) {
		if (event == Evenement.NOEL) {
			return 20;
		} else if (event == Evenement.ST_VALENTIN) {
			return 20;
		}
		
		return 0;
	}
	
	public boolean aCadeauxForto() {
		return Noel();
	}
}
