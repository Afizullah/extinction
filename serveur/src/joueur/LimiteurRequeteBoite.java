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

package joueur;

import serveur.Serveur;

public class LimiteurRequeteBoite {
	private static final int MOYENNE_REQUETES = 20;
	private static int DUREE_MAX_PAR_MOYENNE = 200;
	public static boolean BLOQUE_SI_TROP_RAPIDE = false;
	
	private long[] temps_requetes = new long[MOYENNE_REQUETES];
	private int index_plus_ancienne;
	private int spamCounter;
	private Joueur possesseur;
	
	public LimiteurRequeteBoite(Joueur possesseur) {
		setValeurSentinelle();
		index_plus_ancienne = 0;
		spamCounter = 0;
		
		this.possesseur = possesseur;
	}
	
	private void setValeurSentinelle() {
		temps_requetes[MOYENNE_REQUETES - 1] = 0l;
	}
	
	private boolean aValeurSentinelle() {
		return temps_requetes[MOYENNE_REQUETES - 1] == 0l;
	}
	
	public boolean utilisable() {
		long last_t = temps_requetes[index_plus_ancienne];
		
		temps_requetes[index_plus_ancienne] = System.currentTimeMillis();
		index_plus_ancienne = (index_plus_ancienne + 1) % MOYENNE_REQUETES;
		
		if (aValeurSentinelle()) {
			return true;
		}
		
		boolean r;
		
		r = (System.currentTimeMillis() - last_t >= DUREE_MAX_PAR_MOYENNE);
		//System.out.println("" + System.currentTimeMillis() + " :: " + last_t + " :: " + r + " :: " + (System.currentTimeMillis() - last_t) + " :: " + DUREE_MAX_PAR_MOYENNE);
		
		if (!r) {
			spamCounter++;
			if (spamCounter % 25 == 0 && spamCounter != 0 && spamCounter < 100) { 
				Serveur.getServeur().Avertissement_Modo("Spam requêtes (" + spamCounter + ") : " + possesseur.getPseudo()
														+ " [0] : " + last_t % DUREE_MAX_PAR_MOYENNE
														+ "  ::  [" + MOYENNE_REQUETES + "] : " + System.currentTimeMillis() % DUREE_MAX_PAR_MOYENNE, false);
			} 
		}
		
		return r || !BLOQUE_SI_TROP_RAPIDE;
	}
	
	public int getSpamCounter() {
		return spamCounter;
	}
	
	public static void setDureeMaxParMoyenne(int v, Joueur J) {
		DUREE_MAX_PAR_MOYENNE = v;
		J.Envoie("CxINFO#DUREE_MAX_PAR_MOYENNE : " + DUREE_MAX_PAR_MOYENNE + " ms.");
	}
}
