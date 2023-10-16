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

public class Topic {
	private static final int NB_MESSAGE_PAR_PAGE = 20;
	
	private int ID;
	
	private String Titre;
	private AuteurTopic Auteur;
	private long DateCreation;
	private int NbMessages;
	
	private String DernierPosteur;
	private long DateDernierPost;
	
	private boolean TopicPostit;
	private boolean TopicAnimation; // Étoile
	
	public Topic(int ID, String Titre, String PseudoAuteur, String AvatarAuteur, long DateCreation, int NbMessages, String DernierPosteur, int DateDernierPost) {
		this.ID = ID;
		this.Titre = Titre;
		this.Auteur = new AuteurTopic(PseudoAuteur, AvatarAuteur);
		this.DateCreation = DateCreation;
		this.NbMessages = NbMessages;
		this.DernierPosteur = DernierPosteur;
		this.DateDernierPost = DateDernierPost;
		
		this.TopicPostit = false;
		this.TopicAnimation = false;
	}
	
	public void setTypeTopic(boolean PostIt, boolean Animation) {
		this.TopicPostit = PostIt;
		this.TopicAnimation = Animation;
	}
	
	public int getNbPages() {
		return NbMessages / NB_MESSAGE_PAR_PAGE + 1;
	}
}
