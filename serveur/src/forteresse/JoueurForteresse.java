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

package forteresse;

import java.util.ArrayList;

import joueur.Joueur;
import serveur.Serveur;

public class JoueurForteresse {

	public static final int EQP_AUCUNE = -1;
	public static final int EQP_ROUGE = 0;
	public static final int EQP_BLEUE = 1;
	
	public boolean RallyValide = true;
	private int Equipe = EQP_AUCUNE;
	public int Sexe = 1;
	public int CodeJoueur = 0;
	public String PosX = "0";
	public String PosY = "0";
	public int Score = 0;
	public int ToucheFrigo = 0;
	public long LimiteBrique = 0;
	public int NombreBrique = 0;
	public int Chargeur = 12;
	public int DernièreBalle = -1;
	public int DerDernièreBalle = -1;
	public int Jambombe = 3;
	public ArrayList<Jambombe> ListeJambonExplosion = new ArrayList<Jambombe>();
	public int Degats = 1;
	public int Santé = 10;
	public long DerniereMort = 0;
	public Serveur.Action Respawn;
	private Joueur joueur;

	public JoueurForteresse(Joueur J) {
		joueur = J;
	}

	public void Reset() {
		if (Respawn != null) {
			Respawn.Active = false;
		}
		ListeJambonExplosion.clear();
		//			while (!ListeJambonExplosion.isEmpty()) {
		//				ListeJambonExplosion.remove();
		//			}
		joueur.Vivant = true;
		Santé = 10;
		NombreBrique = 0;
		Chargeur = 12;
		Jambombe = 3; 
		if (joueur.PartieEnCours.Forto && ((Forteresse)joueur.PartieEnCours.getJeu()).SansBombe) {
			Jambombe = 0;
		}
		DernièreBalle = -1;
		DerDernièreBalle = -1;
		RallyValide = true;
	}

	public void Respawn() {
		Reset();
		DerniereMort = System.currentTimeMillis();
		if (joueur.PartieEnCours.Forto) {
			joueur.TempsZeroRally = System.currentTimeMillis();				
			Serveur.getServeur().Envoie(joueur.PartieEnCours.getLsJoueurPartie(), "15" + Serveur.$ + CodeJoueur);
		}
	}
	
	public void AjouterJambombes(int nb) {
		Jambombe += nb;
	}
	
	public void RedonnerBalles() {
		DernièreBalle = -1;
		DerDernièreBalle = -1;
		Chargeur = 12;
	}
	
	public void RedonnerBlocs() {
		LimiteBrique = System.currentTimeMillis();
		NombreBrique = 0;
	}
	
	public void setRouge() {
		Equipe = 0;
	}
	
	public void setBleu() {
		Equipe = 1;
	}
	
	public boolean estRouge() {
		return Equipe == EQP_ROUGE;
	}
	
	public boolean estBleu() {
		return aEquipe() && !estRouge();
	}
	
	public int getEquipe() {
		return Equipe;
	}
	
	public boolean aEquipe() {
		return Equipe != EQP_AUCUNE;
	}
	
	/**
	 * Retourne le code correspondant à la couleur de l'équipe
	 * en une valeur connue pour le client.
	 */
	public int EquipeToCode() {
		if (estRouge()) {
			return 1;
		}
		
		return 2;
	}
	
	public boolean MemeEquipe(JoueurForteresse JF) {
		return this.EquipeToCode() == JF.EquipeToCode();
	}
}
