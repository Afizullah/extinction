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

import aaaah.Carte;
import joueur.Joueur;


public class SignalementMap {

	public String nomJoueur;
	public String adresseIP;
	public Carte carte;
	public String motif;
	public Date date;

	public SignalementMap(String nomJoueur, String adresseIP, Carte carte, String motif) {
		this.nomJoueur = nomJoueur;
		this.adresseIP = adresseIP;
		this.carte = carte;
		this.motif = motif;
		this.date = new Date();
	}

	public String toString(Joueur Modo) {
		//        SimpleDateFormat SimpleFormat = new SimpleDateFormat("dd MMM HH:mm");
		SimpleDateFormat SimpleFormat = new SimpleDateFormat("d/M");
		return "[" + SimpleFormat.format(date) + "] " + nomJoueur + " a signalé " + carte.Id + " (" + carte.ModeMap() + ") : " + motif ;
	}

}