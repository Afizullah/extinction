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

package yshCastle;

import java.util.Arrays;

import forteresse.Forteresse;
import joueur.Joueur;
import yshCastle.ObjetYsh.ObjetsDonjon;

public class JoueurDonjon {
	private int Points;
	private short Niveau;
	private Boolean NiveauBonus;
	private byte[] Items;
	private int Classe;
	private int Groupe;
	private int Donj;
	
	public long TempsStart;
	
	private Joueur joueur;
	
	/* Classes:
	0. Aléatoire - + 500 starting points
	1. Béni - can refill half of the hourglass (3 times)
	2. Guerrier - time * 1.2, score/2
	3. Cartographe - can draw the map of a visited floor (5 times, secrets don't appear)
	4. Chasseur de trésaure - can sense the presence of secrets
	5. Clairvoyant - can see the name of a floor before entering
	6. Héro - score * 2 + 0.2 * tempsmax, time * 0.8
	7. Joker - can skip a level three times (except lvl 50) - (unlocked by completing lvl 10)
	8. Fou - floors are in random order, cannot use items - (unlocked by completing lvl 20)
	9. Voleur - start with 2 random items, 20% chance to get an item every 5 levels(unlocked by completing lvl 30)
	*/
	
	public JoueurDonjon(Joueur joueur, int Donjon) {
		this(joueur, (short)1, false, 0, 0, 0, Donjon);
	}
	
	public JoueurDonjon(Joueur joueur, short niveau, boolean niveauBonus, int points, int classe, int groupe, int donjon) {
		this.joueur = joueur;
		this.Items = new byte[ObjetsDonjon.NbObjets.v];
		
		this.Points = points;
		this.Niveau = niveau;
		this.NiveauBonus = niveauBonus;
		this.Groupe = groupe;
		this.Donj = donjon;
		
		Arrays.fill(this.Items, (byte)0);
	}
	
	public void NouvelleClasse(int CLASSE) {
		
		if (CLASSE == 0) { // Aléatoire
			Classe = (int) (Math.random() * 8 + 1);
			Points = 500;
		} else {
			Classe = CLASSE;
		}
		
		if (Classe == 9) { // Voleur
			
			byte v;
			int i = 0;
			
			while (i < 2) { // TODO : fixer objets obtenables & leur rareté
				v = (byte) (Math.random() * 13 + 1);
				if (v != ObjetsDonjon.CoeurDemon.v) {
					addItem(v);
					++i;
				}
			}
		}
		
	}
	
	public void addItem(byte item) {
		Items[item]++;
	}
	
	public void utiliserItem(byte item) {
		if (Items[item] == 0) {
			joueur.Envoie("CxINFO#Vous n'avez pas cet objet dans votre inventaire.");
			return;
		}
	}
	
	public String getListeItems() {
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < Items.length; ++i) {
			str.append(String.valueOf(Items[i]));
			if (i != Items.length - 1) {
				str.append(",");
			}
		}
		
		return str.toString();
	}
	
	public void loadListeItems(String Ls) {
		String[] NbObjets = Ls.split(",");
		
		for (int i = 0; i < Items.length; ++i) {
			Items[i] = Byte.valueOf(NbObjets[i]);
		}
	}
	
	public void AjouterScoreFinMap(int Ajout) {
		if (Classe == Donjon.ClassesDonjon.Guerrier.v) {
			Points += Ajout / 2;
		} else if (Classe == Donjon.ClassesDonjon.Hero.v) {
			// score * 2 + 0.2 * tempsmax
			Points += Ajout * 2 + 0.2 * ((Forteresse)joueur.PartieEnCours.getJeu()).DureeMaxDonjon;
		}
	}
	
	public void AjouterPoints(int Ajout) {
		this.Points += Ajout;
	}
	
	public int getPoints() {
		return Points;
	}
	
	public boolean hasGroupe() {
		return Groupe != 0;
	}
	
	public boolean niveauBonusUnlock() {
		return Items[ObjetsDonjon.Cle1.v] > 0 && Items[ObjetsDonjon.Cle2.v] > 0;
	}
	
	public void niveauSuivant() {
		Niveau++;
	}
	
	public void setNiveau(short niveau) {
		Niveau = niveau;
	}
	
	public void setNiveauBonus(boolean bonus) {
		NiveauBonus = bonus;
	}
	
	public int getDonjon() {
		return Donj;
	}
	
	public short getNiveau() {
		return Niveau;
	}
	
	public boolean isNiveauBonus() {
		return NiveauBonus;
	}
	
	public int getClasse() {
		return Classe;
	}
	
	public int getGroupe() {
		return Groupe;
	}
}