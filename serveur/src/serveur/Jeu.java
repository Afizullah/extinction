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

package serveur;

import java.util.ArrayList;

import joueur.Joueur;

public abstract class Jeu {
	public static final int TAILLE_ID_MAP_EN_COURS = 5;
	
	public ArrayList<Joueur> ListeJoueur = new ArrayList<Joueur>();
	public ArrayList<Joueur> ListeModo = new ArrayList<Joueur>();
	
	public boolean PartieEnCours = false;
	public int ModeJeu = 0;
	
	protected Serveur serveur;
	public Partie partieMere;
	
	protected final long DureePartie;
	protected long DebutPartie;
	
	private int IdMapEnCours; // ID changeant à chaque map, empêchant les actions sur une map précédente d'avoir lieu une fois que ce n'est plus elle en cours

	public Jeu(Serveur serveur, Partie partieMere, long dureePartie) {
		this.serveur = serveur;
		this.partieMere = partieMere;
		this.DureePartie = dureePartie;
		UpdateIdMapEnCours();
	}
	
	public void UpdateIdMapEnCours() {
		IdMapEnCours = ((int)(Math.random() * 4885656)) % 75269; // Modifier <TAILLE_ID_MAP_EN_COURS> si modification du modulo
		serveur.Envoie(ListeJoueur, "CxIMEC#" + IdMapEnCours);
	}
	
	public boolean IdMapEnCoursValide(int id) {
		return (id == IdMapEnCours);
	}
	
	/**
	 * Envoie à <J> l'ID de la map en cours.
	 */
	protected void Nouveau_Joueur(Joueur J) {
		J.Envoie("CxIMEC#" + IdMapEnCours);
		J.Envoie("CxMODE#" + ModeJeu);
	}
	
	protected void Nouvelle_Partie() {
		UpdateIdMapEnCours();
	}
	
	public long getDureePartie() {
		return DureePartie;
	}
	
	public long getTempsRestantAvantFin() {
		return (DureePartie - (System.currentTimeMillis() - DebutPartie)) / 1000;
	}
	
	public long getTempsEcoulePartieMs() {
		return (System.currentTimeMillis() - DebutPartie);
	}
	
	public boolean BesoinPartieAttente() {
		return false;
	}
	
	public void AjouterPartieAttente(Joueur J) {
	}
	
	public abstract void Reception(Joueur joueur, String chaine, boolean messagePerimé);
	
	public abstract void Destruction();
	
	public boolean PeutEntrer(Joueur joueur) {
		return true;
	}
	
	public ArrayList<Joueur> getListeJoueurs() {
		return ListeJoueur;
	}
}
