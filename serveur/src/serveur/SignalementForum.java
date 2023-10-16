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


import java.text.SimpleDateFormat;
import java.util.Date;

import joueur.Joueur;


public class SignalementForum {

	public String NomJoueur;
	public String AdresseIP;
	public String NomCible;
	public String Sujet;
	public String Message;
	public String Forum;
	public String Motif;
	public Date Date;

	public SignalementForum(String NomJoueur,
			String AdresseIP,
			String NomCible,
			String Sujet,
			String Message,
			String Forum,
			String Motif) {
		this.NomJoueur = NomJoueur;
		this.AdresseIP = AdresseIP;
		this.NomCible = NomCible;
		this.Sujet = Sujet;
		this.Message = Message;
		this.Forum = Forum;
		this.Motif = Motif;
		this.Date = new Date();
	}

	public String toString(Joueur Modo) {
		SimpleDateFormat SimpleFormat = new SimpleDateFormat("dd MMM HH:mm");
		return "[" + SimpleFormat.format(Date) + "] " + NomJoueur + " " + (Modo.AutorisationAdmin?"(" + AdresseIP + ") ":"")+"a signalé " + NomCible + " sur [" + Forum +"] " + Sujet + ",\nmotif : {" + Motif + "}, message : " + Message;
	}

}