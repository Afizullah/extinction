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

import forteresse.Forteresse;
import joueur.Joueur;
import serveur.Boite;
import serveur.Serveur;
import yshCastle.ObjetYsh.ObjetsDonjon;

public class Donjon {
	private Serveur serveur;
	
/*
Items :

1. Gemme du destion - reveal the name of a floor (up to 5 levels above yours)
2. Orbe de vision - see the map of a random floor
3. (1)Sable doré - add 5 minutes to the hourglass (for one floor)
4. (2)Sace de bombes - use them carefully
5. (3)Coeur de démon - can resurrect a dead player (on the same floor) 
6. (1)Charme d'éternité - the player can start again if he dies but will lose his special abilities
7. (1)Parchemin des révélations - can make the truth appear
8. (1)Anneau du chaos - Destroy a random wall in every level
9. Cookie magique - double points for this level
10. (1)Commande du troll - swap 2 levels for every player
11. Bénédiction des dieux - time increase by 1 min or 600 points
12. Parchemin du respect - change your class
13. Dague sacrificielle (sacrifice a member of the party to advance 5 levels)
14. Sphère de réunion - bring all members of a group together

*/
	public static final int YSH_CASTLE = 1;
	
	public Donjon(Serveur SERVEUR) {
		serveur = SERVEUR;
	}
	
	public void GestionDonjon(Joueur JOUEUR, int CASE_DETRUITE) {
		Forteresse PartieForteresse = ((Forteresse)JOUEUR.PartieEnCours.getJeu());
		
		if (PartieForteresse.NumeroDonjon == 1) { // Ysh's Castle
			if (PartieForteresse.NiveauBonus) {
				NiveauBonusYshCastle(JOUEUR, CASE_DETRUITE);
			} else {
				SecretsYshCastle(JOUEUR, CASE_DETRUITE);
			}
		}
	}
	
	private void NiveauBonusYshCastle(Joueur JOUEUR, int CASE_DETRUITE) {
		int[] Param = new int[3];
		Param[0] = 1; // => Ysh's Castle
		Param[1] = -1;
		Param[2] = 0; // => Niveau spécial
		
		Forteresse PartieForteresse = ((Forteresse)JOUEUR.PartieEnCours.getJeu());
		
		switch(PartieForteresse.NiveauDonjon) {
			case 1 : // Pyramide
				// La stratégie est d'attaquer le frigo adverse pour respawn en bleu et pouvoir terminer le rally (+ 200 points, Sable doré)
				Param[1] = 3;
				JOUEUR.JDonjon.AjouterPoints(200);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.SableDore.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Sable Doré\" ainsi que 150 points bonus !");
				break;
			case 2 : // mini-rally double
				// + 150 points, gemme du destin, orbe des visions
				Param[1] = 9;
				JOUEUR.JDonjon.AjouterPoints(150);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.OrbeVision.v);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.GemmeDestin.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir \"l'Orbe de Vision\" et la \"Gemme du Destin\" ainsi que 150 points bonus !");
				break;
			case 3 : // Élégance
				// + 200 points, Charme d'éternité (si le joueur est seul) ou Dague sacrificielle (si le joueur est dans un groupe)
				Param[1] = 13;
				JOUEUR.JDonjon.AjouterPoints(200);
				if (!JOUEUR.JDonjon.hasGroupe()) {
					JOUEUR.JDonjon.addItem(ObjetsDonjon.CharmeEternite.v);
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Charme d'Eternité\" ainsi que 200 points bonus !");
				} else {
					JOUEUR.JDonjon.addItem(ObjetsDonjon.DagueSacrificielle.v);
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir la \"Dague Sacrificielle\" ainsi que 200 points bonus !");
				}
				break;
			case 4 : // marathon 2
				// + 200 points, cookie magique, gemme du destin
				Param[1] = 20;
				JOUEUR.JDonjon.AjouterPoints(200);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.CookieMagique.v);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.GemmeDestin.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Cookie Magique\" et la \"Gemme du Destin\" ainsi que 200 points bonus !");
				break;
			case 5 : // Stick World
				// + 250 points, bénédiction des dieux
				Param[1] = 22;
				JOUEUR.JDonjon.AjouterPoints(250);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.BenedictionDieux.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir la \"Bénédiction des Dieux\" ainsi que 250 points bonus !");
				break;
			/*
			  LVL Bonus Enfer: Si le joueur a les deux clefs, il peut ouvrir les portes de l'enfer après le lvl 30.
			  L'enfer est composé de 3 niveaux, chacun d'eux contient un coeur de démon, le troisième contient également l'anneau du chaos.
		      Le joueur à la possibilité de quitter l'enfer après chaque niveau s'il le désire.
		    */
			case 6 : // Enfer 1
				// + 300 poins, coeur de démon
				JOUEUR.JDonjon.AjouterPoints(300);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.CoeurDemon.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Coeur du Démon\" ainsi que 300 points bonus !");
				break;
			case 7 : // Enfer 2
				// + 300 poins, coeur de démon
				JOUEUR.JDonjon.AjouterPoints(300);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.CoeurDemon.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Coeur du Démon\" ainsi que 300 points bonus !");
				break;
			case 8 : // Enfer 3
				// + 1000 points, coeur de démon, anneau du chaos
				JOUEUR.JDonjon.AjouterPoints(1000);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.CoeurDemon.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Coeur de Démon\" ainsi que 1000 points bonus !");
				break;
			case 9 : // Paranoia
				// + 300 points, sac de bombes, gemme du destin
				JOUEUR.JDonjon.AjouterPoints(300);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.SacBombes.v);
				JOUEUR.JDonjon.addItem(ObjetsDonjon.GemmeDestin.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Sac de Bombes\" et la \"Gemme du Destin\" ainsi que 300 points bonus !");
				break;
		}
		
		if (Param[1] == -1) {
			PartieForteresse.NiveauDonjon++;
			Param[1] = PartieForteresse.NiveauDonjon;
			Param[2] = 0; // Niveau normal
		}
		serveur.BOITE.Requete(Boite.CHARGER_NIV_DONJON, JOUEUR, Param);
	}
	
	private void SecretsYshCastle(Joueur JOUEUR, int CASE_DETRUITE) {
		String Map;
		int[] Param = new int[3];
		Param[0] = 1; // => Ysh's Castle
		Param[1] = -1;
		Param[2] = 1; // => Niveau spécial
		
		Forteresse PartieForteresse = ((Forteresse)JOUEUR.PartieEnCours.getJeu());
		
		switch(PartieForteresse.NiveauDonjon) {
			case 2 : // LVL 2:	Un passage sous le 'L' de 'FACILE' permet d'accéder au niveau secret 'Pyramide' + 100 points
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					Param[1] = 1;
					JOUEUR.Envoie("CxINFO#Vous venez de gagner 100 points !");
					JOUEUR.JDonjon.AjouterPoints(100);
				}
				break;
			case 4 : // Un passage vers le bas au niveau de la fin permet d'accéder à un coffre (+ 150 points, Cookie magique, gemme du destin)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Cookie Magique\" et la \"Gemme du Destin\" ainsi que 150 points bonus !");
					JOUEUR.JDonjon.AjouterPoints(150);
					JOUEUR.JDonjon.addItem(ObjetsDonjon.CookieMagique.v);
					JOUEUR.JDonjon.addItem(ObjetsDonjon.GemmeDestin.v);
				}
				break;
			case 8 : // Le rally contient un carré destructible en bas à droite, il permet d'accéder au niveau secret 'mini-rally double'.(Le joueur doit quand meme terminer le rally) (+ 100 points)
				if (PartieForteresse.Liste_Case().contains("" + Forteresse.CASE_VIDE_CONSTRUCTIBLE)) {
					Param[1] = 2;
					JOUEUR.JDonjon.AjouterPoints(100);
					JOUEUR.Envoie("CxINFO#Vous venez de gagner 100 points bonus !");
				}
				break;
			case 12 : // Juste avant la fin, le joueur peut se couler pour attraper une liane en dessous et accéder au niveau secret 'rally élégance'. (+ 300 points)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					Param[1] = 3;
					JOUEUR.JDonjon.AjouterPoints(300);
					JOUEUR.Envoie("CxINFO#Vous venez de gagner 300 points bonus !");
				}
				break;
			case 13 : // A partir de la zone centrale, le joueur peut remonter sur la cible de gauche pour trouver un secret. (+ 200 points, sac de bombes)
				Map = PartieForteresse.Liste_Case();
				
				if (Map.contains("" + Forteresse.CASE_VIDE_CONSTRUCTIBLE) && !Map.contains("" + Forteresse.CASE_CARRE_DESTRUCTIBLE)) {
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Sac de Bombes\" ainsi que 200 points bonus !");
					JOUEUR.JDonjon.AjouterPoints(200);
					JOUEUR.JDonjon.addItem(ObjetsDonjon.SacBombes.v);
				}
				break;
			case 15 : // Antichambre 2:	La 2ème antichambre apparait à partir du lvl 15, celle-ci contient un secret accessible par le bas du pentagramme.
				// Objet aléatoire (les objets rares ont moins de chance d'être choisis)
				Param[2] = 0;
				Param[1] = 16; // TODO
				break;
			case 17 : // Un passage avant la fin permet d'entrer dans le logo pour obtenir un secret (+ 250 points, Sphère de réunion)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					Param[1] = 4;
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir la \"Sphère de Réunion\" ainsi que 250 points bonus !");
					JOUEUR.JDonjon.AjouterPoints(250);
					JOUEUR.JDonjon.addItem(ObjetsDonjon.SphereReunion.v);
				}
				break;
			case 18 : // Terminer la partie bonus du rally permet d'obtenir un objet (+ 500 points, Télécommande du troll)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir la \"Télécommande du Troll\" ainsi que 500 points bonus !");
					JOUEUR.JDonjon.AjouterPoints(500);
					JOUEUR.JDonjon.addItem(ObjetsDonjon.CommandeTroll.v);
				}
				break;
			case 19 : // Les carrés oranges cachés dans la zone du frgo bleu permettent d'accéder au niveau secret 'rally marathon 2' (+ 200 points)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					Param[1] = 4;
					JOUEUR.Envoie("CxINFO#Vous venez de gagner 200 points bonus !");
					JOUEUR.JDonjon.AjouterPoints(200);
				}
				break;
			case 21 : //  Reconstruire le carré en haut à droite permet d'accéder au niveau secret 'rally stick world'. (le joueur doit terminer le rally (+ 100 points)
				if (PartieForteresse.Liste_Case().contains("" + Forteresse.CASE_CARRE_DESTRUCTIBLE)) {
					Param[1] = 5;
					JOUEUR.Envoie("CxINFO#Vous venez de gagner 100 points bonus !");
					JOUEUR.JDonjon.AjouterPoints(100);
				}
				break;
			case 25 : // Cet objet s'obtient automatiquement en terminant le rally (+Parchemin de respécialisation)
				JOUEUR.JDonjon.addItem(ObjetsDonjon.ParcheminRespecialisation.v);
				JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Parchemin de Respécialisation\" !");
				break;
			case 26 : // Au dessus du frigo rouge se trouve des carrés reconstructibles (+ 666 points, 1ère clef)
				if (PartieForteresse.Liste_Case().contains("" + Forteresse.CASE_CARRE_DESTRUCTIBLE)) {
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir une \"Vieille Clé Tordue\" ainsi que 666 points bonus !");
					JOUEUR.JDonjon.addItem(ObjetsDonjon.Cle1.v);
					JOUEUR.JDonjon.AjouterPoints(666);
				}
				break;
			case 30 : // Avant d'arriver a la fin, une des lianes est différente des autres. Elle se remarque par un carré supplémentaire au sol. En prenant cette liane, le joueur accède à un secret (+ 666 points, 2ème clef)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir une \"Vieille Clé Racrapotée\" ainsi que 666 points bonus !");
					JOUEUR.JDonjon.addItem(ObjetsDonjon.Cle2.v);
					JOUEUR.JDonjon.AjouterPoints(666);
				}
				break;
			case 33 : // Le secret se trouve sous le coeur final en détruisant le sol (+ 300 points, orbe des visions)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir \"l'Orbe de Vision\" ainsi que 300 points !");
					JOUEUR.JDonjon.addItem(ObjetsDonjon.OrbeVision.v);
					JOUEUR.JDonjon.AjouterPoints(300);
				}
				
				break;
			case 35 : // Un passage vers le niveau bonus 'Paranoia' se trouve après la fin, depuis la voile centrale du bateau (+ 400 points)
				if (CASE_DETRUITE == Forteresse.CASE_CARRE_OBJECTIF2) {
					Param[1] = 9;
					JOUEUR.Envoie("CxINFO#Vous venez de gagner 400 points bonus !");
					JOUEUR.JDonjon.AjouterPoints(400);
				}
				break;
			case 38 : // Une croix destructible se trouve dans le coin haut-droit (+ 200 points, gemme du destin)
				// TODO : Décider si les bb peuvent déclencher les secrets ou non
				Map = PartieForteresse.Liste_Case();
				
				if (Map.contains("" + Forteresse.CASE_VIDE_CONSTRUCTIBLE) && !Map.contains("" + Forteresse.CASE_CARRE_DESTRUCTIBLE)) {
					Param[1] = 9;
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir la \"Gemme du Destin\" ainsi que 200 points !");
					JOUEUR.JDonjon.addItem(ObjetsDonjon.GemmeDestin.v);
					JOUEUR.JDonjon.AjouterPoints(200);
				}
				break;
			case 40 : // Un objet se trouve au dessus de la fin, il est accessible par la liane en dessous (+ 300 points, Parchemin des révélations)
				Map = PartieForteresse.Liste_Case();
				
				if (Map.contains("" + Forteresse.CASE_VIDE_CONSTRUCTIBLE) && !Map.contains("" + Forteresse.CASE_CARRE_DESTRUCTIBLE)) {
					Param[1] = 9;
					//JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Parchemin des Révélations\" ainsi que 300 points !");
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir 300 points !");
					//JOUEUR.JDonjon.addItem(ObjetsDonjon.ParcheminRevelations.v);
					JOUEUR.JDonjon.AjouterPoints(300);
				}
				break;
			case 43 : // Il y a des carrés destructibles au milieu du rally, au dessus du 'LOL' (+ 300 points, cookie magique)
				Map = PartieForteresse.Liste_Case();
				
				if (Map.contains("" + Forteresse.CASE_VIDE_CONSTRUCTIBLE) && !Map.contains("" + Forteresse.CASE_CARRE_DESTRUCTIBLE)) {
					Param[1] = 9;
					JOUEUR.Envoie("CxINFO#Félicitations ! Vous venez d'obtenir le \"Cookie Magique\" ainsi que 300 points !");
					JOUEUR.JDonjon.addItem(ObjetsDonjon.CookieMagique.v);
					JOUEUR.JDonjon.AjouterPoints(300);
				}
				break;
			case 50 : // Fin donjon
				JOUEUR.Envoie("CxINFO#Fin !");
				
				if (JOUEUR.JDonjon.niveauBonusUnlock()) {
					Param[6] = 0;
					// TODO : Gérer l'entrée dans les enfers graphiquement (proposer s'il veut entrer)
				}
				break;
		}
		/*
LVL 50: Si le joueur à le parchemin des révalations, la carte 'boss_open' est chargée, sinon on charge la map boss.
	La statégie est de traverser les 5 épreuves et d'attaquer la frigo bleu. Le joueur peut alors respawn en bleu et aller affronter le boss.
	Après avoir détruit le socier Ysh, le jeu n'est pas terminé. Le joueur doit pénétrer dans la dimension X pour détruire la source de son pouvoir.
	Le portail situé en haut à droite n'est accessible que si le joueur possède le Parchemin des Révélations.

Castle Core: En fonction du nombre de niveux bonus terminés par le joueur (alt1 à alt5), les carrés roses sous les 5 épreuves seront ouverts.
		Le joueur doit d'abord détruire le frigo bleu pour aller détruire le carré rose à gauche.
		Il doit ensuite emprunter le passage du milieu pour retourner à droite, retraverser les épreuves et prendre la liane en dessous pour accéder à la partie haute (accessible après avoir cassé les carrés rose à gauche).
		Une fois en haut, il ne reste plus qu'a aller au centre et détruire le coeur.*/
		
		if (Param[1] == -1) {
			PartieForteresse.NiveauDonjon++;
			JOUEUR.JDonjon.niveauSuivant();
			
			Param[1] = PartieForteresse.NiveauDonjon;
			Param[2] = 0; // Niveau normal
		}
		serveur.BOITE.Requete(Boite.CHARGER_NIV_DONJON, JOUEUR, Param);
	}
	
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
	
	public enum ClassesDonjon{
		Ressusite(0), Beni(1), Guerrier(2), Cartographe(3), ChasseurTresaurs(4), Clairvoyant(5), Hero(6),
		Joker(7), Fou(8), Voleur(9);

		public final byte v;
		private ClassesDonjon(int value) {
            v = (byte)value;
        }
	}
}
