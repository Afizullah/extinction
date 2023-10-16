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

import aaaah.Aaaah;
import elo.Elo;
import elo.EloJoueur;
import joueur.Joueur;

public class PartieAttenteElo {
	private ArrayList<Joueur> ListeJoueurPartie;
	private ArrayList<Joueur> EquipeElo1, EquipeElo2;
	private Joueur Arbitre;
	private String PseudoArbitreElo;
	private String PseudoGuideElo;
	private int CodeValidationMatchElo;
	private boolean MatchTeam;
	private Partie partieOriginelle;
	private Serveur serveur;
	
	private boolean[] EtatValidationRouges;
	private boolean[] EtatValidationBleus;
	private boolean AccepteAjoutJoueurs; // Indique si des joueurs peuvent rejoindre une des deux équipes (donc pas durant la validation)
	
	private final int MAX_PAR_EQUIPE = 5; // 5;
	
	public static boolean ActiverBlocageDiffElo = true; 
	private static int numMatch = 0;
	
	public PartieAttenteElo(Partie partieOriginelle, String ArbitreElo, String Guide, boolean MatchTeam) {
		/*
		 <partieOriginelle> : Partie "mère" qui se charge une fois l'attente terminée
		*/
		
		this.partieOriginelle = partieOriginelle;
		this.partieOriginelle.AttenteElo = true;
		this.serveur = Serveur.getServeur();
		
		this.ListeJoueurPartie = new ArrayList<Joueur>();
		this.CodeValidationMatchElo = 0;
		this.EquipeElo1 = new ArrayList<Joueur>();
		this.EquipeElo2 = new ArrayList<Joueur>();
		this.PseudoArbitreElo = ArbitreElo;
		this.PseudoGuideElo = Guide;
		this.AccepteAjoutJoueurs = true;
		this.MatchTeam = MatchTeam;
	}
	
	public void Rejoindre_Partie(Joueur J) {
		J.PartieEnAttente = this.partieOriginelle;
		J.PartieEnCours = null;
		J.SansPartie = false;
		ListeJoueurPartie.add(J);
		Actualiser_Liste_Attente_Elo();
		
		serveur.ListeJoueurEnAttenteElo.add(J);
		serveur.ListeJoueurSansPartie.remove(J);
		serveur.MAJ_Liste_Partie("CxR#" + J.NomJoueur + "#" + partieOriginelle.CodePartie + "#" + ListeJoueurPartie.size() + "#", partieOriginelle);
	}
	
	public void Actualiser_Liste_Attente_Elo() {
		StringBuilder str = new StringBuilder("CxTelo#");
		
		for(int i = 0; i < EquipeElo1.size(); ++ i) {
			str.append(EquipeElo1.get(i).NomJoueur);
			str.append(";");
		}
		str.append(" #"); // Espace pour ne pas avoir des vides s'il y a 0 joueur dans une des équipes
		for(int i = 0; i < EquipeElo2.size(); ++ i) {
			str.append(EquipeElo2.get(i).NomJoueur);
			str.append(";");
		}
		str.append(" #");
		str.append(PseudoArbitreElo);
		str.append("#");
		str.append(PseudoGuideElo);
		
		str.append("#" + CodeValidationMatchElo);
		
		serveur.Envoie(ListeJoueurPartie, str.toString());
	}
	
	/**
	 * Cliquer sur "Rejoindre l'équipe bleue" ou "Rejoindre l'équipe rouge"
	 */
	public void Ajouter_Joueur_Attente_Elo(boolean Equipe1, Joueur J) {
		if (!J.AutorisationInscription) {
			J.Envoie("CxINFO#Vous ne pouvez pas rejoindre une partie classée en tant qu'invité.");
			return;
		} else if (!AccepteAjoutJoueurs) {
			J.Envoie("CxINFO#Le match est en cours de validation, vous ne pouvez pas rejoindre d'équipe pour le moment.");
			return;
		} else if (!PseudoArbitreElo.equals(J.NomJoueur) && !PseudoGuideElo.equals(J.NomJoueur)) {
			if (Equipe1) {
				if (EquipeElo1.size() < MAX_PAR_EQUIPE && !EquipeElo1.contains(J)) {
					EquipeElo1.add(J);
					if (!ListeJoueurPartie.contains(J)) {
						ListeJoueurPartie.add(J);
					}
					if (EquipeElo2.contains(J)) {
						EquipeElo2.remove(J);
					}
					Generer_Code_Match_Elo();
					Actualiser_Liste_Attente_Elo();
				}
			} else {
				if (EquipeElo2.size() < MAX_PAR_EQUIPE && !EquipeElo2.contains(J)) {
					EquipeElo2.add(J);
					if (!ListeJoueurPartie.contains(J)) {
						ListeJoueurPartie.add(J);
					}
					if (EquipeElo1.contains(J)) {
						EquipeElo1.remove(J);
					}
					Generer_Code_Match_Elo();
					Actualiser_Liste_Attente_Elo();
				}
			}
		}
	}
	
	public void Joueur_Quitte_Equipe(Joueur J) {
		// Partir de son équipe
		
		if (EquipeElo1.remove(J)) {
			if (!AccepteAjoutJoueurs) {
				Refuser_Validation(J);
			}
			
			Generer_Code_Match_Elo();
			Actualiser_Liste_Attente_Elo();
		} else if(EquipeElo2.remove(J)) {
			if (!AccepteAjoutJoueurs) {
				Refuser_Validation(J);
			}
			
			Generer_Code_Match_Elo();
			Actualiser_Liste_Attente_Elo();
		}
	}
	
	/**
	  Un joueur quitte l'attente (construction des équipes) avant le lancement d'une partie classée.
	  
	  <detruireSi0> : indique s'il faut supprimer la partie originale (true) ou non lorsqu'il n'y a plus de
	  joueurs. Vaudra true si c'est une personne qui quitte volontairement la partie (clique sur le bouton
	  "Annuler" du client/déconnexion) et false si c'est la fin de l'attente et que le joueur n'est pas dans
	  ceux validés. 
	**/
	public void Joueur_Quitte_Attente_Elo(Joueur J, boolean detruireSi0) {
		J.PartieEnAttente = null;
		ListeJoueurPartie.remove(J);
		serveur.ListeJoueurEnAttenteElo.remove(J);
		
		Joueur_Quitte_Equipe(J);
		
		if (detruireSi0 && ListeJoueurPartie.size() == 0) { // Les joueurs quittent la partie
			partieOriginelle.getJeu().Destruction();
			serveur.ListePartie.remove(partieOriginelle);
		}
		
		serveur.Rejoindre_Liste_Partie(J);
		
		if (detruireSi0) {
			serveur.MAJ_Liste_Partie("CxQ#" + partieOriginelle.CodePartie + "#" + ListeJoueurPartie.size() + "#", partieOriginelle);
		}
	}
	
	private boolean PeutDemarrer(Joueur Arbitre, int Code) {
		if (Arbitre.NomJoueur.equals(PseudoArbitreElo) && Code == CodeValidationMatchElo
				&& EquipeElo1.size() > 0 && EquipeElo1.size() == EquipeElo2.size()) { // Conditions de base
			
			final int nbJoueursParEquipe = EquipeElo1.size();
			
			if ((partieOriginelle.ModeElo == Elo.FS || partieOriginelle.ModeElo == Elo.RUN) && nbJoueursParEquipe < 3) {
				Arbitre.Envoie("CxINFO#Il faut 3 joueurs dans chaque équipe pour le mode F/S et Run.");
				return false;
			} else if (partieOriginelle.ModeElo == Elo.MS && nbJoueursParEquipe < 2) {
				Arbitre.Envoie("CxINFO#Il faut au moins 2 joueurs dans chaque équipe pour le mode MS.");
				return false;
			} else if (MatchTeam && nbJoueursParEquipe < 3) {
				Arbitre.Envoie("CxINFO#Il faut au moins 3 joueurs dans chaque équipe pour les matchs team.");
				return false;
			} else if (!MatchTeam && nbJoueursParEquipe > 3) {
				Arbitre.Envoie("CxINFO#Seuls les matchs teams peuvent avoir plus de 3 joueurs par équipe.");
				return false;
			}
			
			if (MatchTeam) {
				int team1 = EquipeElo1.get(0).IdTeam;
				int team2 = EquipeElo2.get(0).IdTeam;
				
				if (team1 == 0 || team2 == 0 || team1 == team2) {
					Arbitre.Envoie("CxINFO#Il y a plus d'une team par équipe.");
					return false;
				}
				
				for (int i = nbJoueursParEquipe - 1; i >= 0; --i) {
					if (EquipeElo1.get(i).IdTeam != team1 || EquipeElo2.get(i).IdTeam != team2) {
						Arbitre.Envoie("CxINFO#Il y a plus d'une team par équipe.");
						return false;
					}
				}
				
				partieOriginelle.setTeamsAutorisees(team1, team2);
				return true;
			}
			
			if (ActiverBlocageDiffElo && !MatchTeam) { // Vérification de la différence d'elo
				String[] PseudosEquipe1 = new String[nbJoueursParEquipe];
				String[] PseudosEquipe2 = new String[nbJoueursParEquipe];
				String Jeu;
				
				for (int i = nbJoueursParEquipe - 1; i >= 0; --i) {
					PseudosEquipe1[i] = EquipeElo1.get(i).NomJoueur;
					PseudosEquipe2[i] = EquipeElo2.get(i).NomJoueur;
				}
				
				if (partieOriginelle.PAaaah) {
					Jeu = "a";
				} else if (partieOriginelle.Bouboum) {
					Jeu = "b";
				} else {
					Jeu = "f";
				}
				
				EloJoueur[] EloModeEq1 = EloJoueur.getEloByPseudos(PseudosEquipe1, Jeu);
				EloJoueur[] EloModeEq2 = EloJoueur.getEloByPseudos(PseudosEquipe2, Jeu);
				if(Math.abs(Elo.calcDifferenceElo(EloModeEq1, EloModeEq2, (byte)partieOriginelle.ModeElo, false)) > 200) {
					serveur.Envoie(EquipeElo1, "CxINFO#Il y a une trop grande différence d'elo entre les deux équipes pour lancer le match.");
					serveur.Envoie(EquipeElo2, "CxINFO#Il y a une trop grande différence d'elo entre les deux équipes pour lancer le match.");
					Arbitre.Envoie("CxINFO#Il y a une trop grande différence d'elo entre les deux équipes pour lancer le match.");
					return false;
				}
			}
			
			return true;
		}
		
		Arbitre.Envoie("CxINFO#Conditions de base non remplies.");
		return false;
	}
	
	public void Start_Validation_Match(Joueur Arbitre, int Code) {
		/*
		  L'arbitre clique sur "Démarrer le match", envoyant la fenêtre de confirmation aux joueurs pour s'assurer
		  qu'ils affrontent bien les bonnes personnes.
		*/
		
		if (PeutDemarrer(Arbitre, Code)) {
			
			final int nbJoueursParEquipe = EquipeElo1.size();
			
			this.AccepteAjoutJoueurs = false;
			this.Arbitre = Arbitre;
			serveur.Envoie(ListeJoueurPartie, "CxINFO#La validation du match a été lancée.");
			
			//
			this.EtatValidationBleus = new boolean[EquipeElo1.size()];
			this.EtatValidationRouges = new boolean[EquipeElo1.size()];
			
			for (int i = nbJoueursParEquipe - 1; i >= 0; --i) {
				EtatValidationRouges[i] = false;
				EtatValidationBleus[i] = false;
			}
			//
			
			StringBuilder msg = new StringBuilder("CxELOVAL#");
			for (int i = 0; i < nbJoueursParEquipe; ++i) {
				msg.append(EquipeElo1.get(i).NomJoueur);
				msg.append(";");
			}
			msg.append("#");
			for (int i = 0; i < nbJoueursParEquipe; ++i) {
				msg.append(EquipeElo2.get(i).NomJoueur);
				msg.append(";");
			}
			msg.append("#");
			msg.append(PseudoArbitreElo);
			
			String m = msg.toString();
			
			serveur.Envoie(EquipeElo1, m);
			serveur.Envoie(EquipeElo2, m);
			Arbitre.Envoie(m);
		}
	}
	
	public void Refuser_Validation(Joueur J) {
		if (!this.AccepteAjoutJoueurs) {
			serveur.Envoie(EquipeElo1, "CxELOREF");
			serveur.Envoie(EquipeElo2, "CxELOREF");
			this.Arbitre.Envoie("CxELOREF");
			
			serveur.Envoie(ListeJoueurPartie, "CxINFO#" + J.NomJoueur + " a annulé le match.");
			this.AccepteAjoutJoueurs = true;
		}
	}
	
	public void Accepter_Validation(Joueur J) {
		if (!this.AccepteAjoutJoueurs) {
			for (int i = 0; i < EquipeElo1.size(); ++i) {
				if (EquipeElo1.get(i).equals(J)) {
					this.EtatValidationRouges[i] = true;
					
					serveur.Envoie(EquipeElo1, "CxELOOK#" + i + "#1");
					serveur.Envoie(EquipeElo2, "CxELOOK#" + i + "#1");
					Arbitre.Envoie("CxELOOK#" + i + "#1");
					
					Traite_Validation(); // Lancement de la partie si tout le monde a validé
					return;
				}
			}
			for (int i = 0; i < EquipeElo2.size(); ++i) {
				if (EquipeElo2.get(i).equals(J)) {
					this.EtatValidationBleus[i] = true;
					
					serveur.Envoie(EquipeElo1, "CxELOOK#" + i + "#0");
					serveur.Envoie(EquipeElo2, "CxELOOK#" + i + "#0");
					Arbitre.Envoie("CxELOOK#" + i + "#0");
					
					Traite_Validation();
					return;
				}
			}
		}
	}
	
	private void Traite_Validation() {
		/*
		  Vérifie si tout le monde a validé le match. Si c'est le cas, le match est lancé. 
		*/
		for (int i = 0; i < EtatValidationRouges.length; ++i) {
			if (this.EtatValidationRouges[i] == false || this.EtatValidationBleus[i] == false) {
				return;
			}
		}
		Start_Partie_Elo();
	}
	
	private void Start_Partie_Elo() {
		/*
		 Démarrage de la partie par l'arbitre 
		*/
		
		serveur.ListeJoueurEnAttenteElo.removeAll(EquipeElo1);
		serveur.ListeJoueurEnAttenteElo.removeAll(EquipeElo2);
		serveur.ListeJoueurEnAttenteElo.remove(Arbitre);
		
		if (partieOriginelle.PAaaah && partieOriginelle.ModeElo == Elo.RALLY_AAAAH) {
			if (MatchTeam || EquipeElo1.size() > 3)
				((Aaaah)partieOriginelle.getJeu()).SansConta = false;
			else
				((Aaaah)partieOriginelle.getJeu()).SansConta = true;
		}
		
		/*if (EquipeElo1.size() >= 3 && partieOriginelle.PAaaah && partieOriginelle.ModeElo == Elo.RALLY_AAAAH) {
			((Aaaah)partieOriginelle.getJeu()).SansConta = true;
		}*/
		
		if (partieOriginelle.PAaaah || partieOriginelle.Forto) {
			Arbitre.MortAuto = true;
			Arbitre.Envoie("CxINFO#Mort automatique en début de partie : activée.");
		}
		
		partieOriginelle.AttenteEloPartie = null;
		partieOriginelle.AttenteElo = false;
		
		for (int i = EquipeElo1.size() - 1; i >= 0; --i) { // Les noms de l'équipe 2 sont envoyés lors de la connexion
			partieOriginelle.EquipeElo1.add(EquipeElo1.get(i).NomJoueur);
			partieOriginelle.EquipeElo2.add(EquipeElo2.get(i).NomJoueur);
		}
		
		partieOriginelle.addArbitreElo(Arbitre.NomJoueur);
		partieOriginelle.Nouveau_Joueur(Arbitre, true);
		
		// On connecte tous les joueurs de chaque équipe à la partie
		for (int i = EquipeElo1.size() - 1; i >= 0; --i) {
			Joueur J1 = EquipeElo1.get(i);
			Joueur J2 = EquipeElo2.get(i);
			
			J1.Envoie("CxELOSTRT");
			J2.Envoie("CxELOSTRT");
			
			J1.PartieEnAttente = null;
			J2.PartieEnAttente = null;
			
			partieOriginelle.Nouveau_Joueur(J1, true);
			partieOriginelle.Nouveau_Joueur(J2, false);
			
			ListeJoueurPartie.remove(J1);
			ListeJoueurPartie.remove(J2);
			
			System.out.println("MTCELO " + numMatch + " [" + Elo.modeToString(partieOriginelle.ModeElo) +  "] " + PseudoArbitreElo + " [Eqp 1] " + J1.getPseudo());
			System.out.println("MTCELO " + numMatch + " [" + Elo.modeToString(partieOriginelle.ModeElo) + "] " + PseudoArbitreElo + " [Eqp 2] " + J2.getPseudo());
		}
		
		Arbitre.Envoie("CxELOSTRT");
		Arbitre.PartieEnAttente = null;
		Arbitre.Envoie("CxINFO#ID du match : " + numMatch);
		partieOriginelle.IDMatchELO = numMatch;
		numMatch++;
		
		ListeJoueurPartie.remove(Arbitre);
		
		// On éjecte les joueurs "spectateurs" qui ne sont pas dans une équipe
		for (int i = ListeJoueurPartie.size() - 1; i >= 0; --i) {
			ListeJoueurPartie.get(i).Envoie("CxEXCLELO");
			Joueur_Quitte_Attente_Elo(ListeJoueurPartie.get(i), false);
		}
		
		this.Arbitre.Envoie("CxARBT");
		this.Arbitre.AutorisationArbitreElo = true;
	}
	
	public int Generer_Code_Match_Elo() {
		CodeValidationMatchElo++;
		return CodeValidationMatchElo;
	}
	
	public int getNbJoueur() {
		return ListeJoueurPartie.size();
	}
	
	public ArrayList<Joueur> getLsJoueurPartie() {
		return ListeJoueurPartie;
	}
}
