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


import java.util.Hashtable;


class Sujet {

	private String Titre = "";
	private int Forum = 3;
	private Hashtable<Integer, String> Pages;

	public Sujet(String Titre, int Forum, Hashtable<Integer, String> Pages) {
		this.Titre = Titre;
		this.Forum = Forum;
		this.Pages = Pages;
	}

	public Hashtable<Integer, String> getPages() {
		return Pages;
	}

	public String getTitre() {
		return Titre;
	}

	public int getForum() {
		return Forum;
	}

	public void setForum(int NouveauForum) {
		Forum = NouveauForum;
	}
}