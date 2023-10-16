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

package forum;

import java.util.ArrayList;

import joueur.Joueur;
import serveur.Boite;
import serveur.Serveur;

public class Forum {
	public static final int FORUM_MODO = 0;
	public static final int FORUM_AAAAH = 2;
	public static final int FORUM_BOUBOUM = 3;
	public static final int FORUM_FORTERESSE = 4;
	public static final int FORUM_BUGS = 5;
	public static final int FORUM_GENERAL = 6;
	public static final int FORUM_VIP = 7;
	public static final int FORUM_ARTISTES = 8;
	public static final int FORUM_ANIM = 9;
	public static final int FORUM_POUBELLE = 10;
	public static final int FORUM_TEAM = 11;
	
	private String Nom;
	private int ID;
	private ArrayList<Topic> LsTopics;
	
	public Forum(int ID, String Nom) {
		this.ID = ID;
		this.Nom = Nom;
	}
	
	public void EnvoyerA(Joueur J) {
		if (PeutAcceder(J)) {
			Serveur.getServeur().BOITE.Requete(Boite.DEMANDE_LISTE_SUJET, J);
		} else {
			Serveur.getServeur().Avertissement_Modo(J.NomJoueur + " a tenté d'accéder au forum " + ID, false);
			J.Deconnexion("Client modifié (tentative accès forum " + ID + ")");
		}
	}
	
	private boolean PeutAcceder(Joueur J) {
		switch (ID) {
			case FORUM_MODO :
				return (J.AutorisationModo);
			case FORUM_VIP :
				return (J.AutorisationModoTeam || J.AutorisationArbitre || J.AutorisationModoForum || J.AutorisationModo || J.AutorisationDev);
			case FORUM_ANIM :
				return (J.AutorisationModo || J.AutorisationAnimateur);
		}
		
		return true;
	}
}
