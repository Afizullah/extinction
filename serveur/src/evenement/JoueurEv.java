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
// https://docs.google.com/document/d/1ywp8QCcTeSzVF6xflAd8mYVu06rLX3Cc42TCPVWghlU/edit

import joueur.Joueur;

public class JoueurEv {
	private Joueur owner;

	public JoueurEv(Joueur joueur) {
		owner = joueur;
	}
}

/*
public static final long CODE_A_RALLY = 1501029452061l;
public static final long CODE_A_RUN = 1438393982196l;
public static final long CODE_A_MS = 1474293959125l;
public static final long CODE_A_DEF = 1486943687529l;
public static final long CODE_A_NG = 1488052987540l;*/