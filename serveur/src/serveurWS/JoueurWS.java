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

package serveurWS;

import java.nio.channels.SocketChannel;

import joueur.Joueur;
import serveur.Serveur;

public class JoueurWS extends Joueur {
	private WebSocketServer serveurWS;
	private boolean enDeconnexion = false;
	private StringBuilder fragments_messages = new StringBuilder(""); // Messages fragmentés réseau
	
	public JoueurWS(Serveur serveur, WebSocketServer serveurWS, SocketChannel sockChan, String ip) {
		super(serveur, null, sockChan, ip);
		this.serveurWS = serveurWS;
	}

////////////////// RESEAU /////////////////////

	// @Override
	public void Deconnexion(String raison) {
		//SelectionKey Clef = Socket.keyFor(ServeurNio.selector);
		//ServeurNio.Deconnexion(Clef, MESSAGE);
		
		if (!enDeconnexion) {
			enDeconnexion = true;
			serveurWS.deconnexion(Socket, raison);
		}
	}
	
	// @Override
	public void Envoie(String MESSAGE) {
		String Message = MESSAGE;// + '\u0000';
		//System.out.println(NomJoueur + " [0] <-- " + Message);
		//Message = Serveur.getServeur().Obfuscation.GetCommande((Joueur)this, Message);
		//System.out.println(NomJoueur + " <-- " + Message);
		
		try {
			if (!serveurWS.envoyer(Message, Socket)) { // Erreur durant l'envoie
				if (!enDeconnexion)
					Deconnexion("Erreur envoie données");
			}
		} catch (Exception e) {
			if (!enDeconnexion)
				Deconnexion("Erreur envoie données");
			e.printStackTrace();
		}
	}
	
	public void addMessageFragmente(String msg) {
		fragments_messages.append(msg);
	}
	
	public String getFullMessage(String msg) {
		addMessageFragmente(msg);
		String r = fragments_messages.toString();
		
		fragments_messages = new StringBuilder("");
		
		return r;
	}
	
////////////////////////////////////////////////////
	
	// @Override
	protected boolean Vérification_Mal_De_Tête(String CHAINE) {
		return false;
	}
}
