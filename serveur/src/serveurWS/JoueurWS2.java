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

import org.java_websocket.WebSocket;

import joueur.Joueur;
import serveur.Serveur;

public class JoueurWS2 extends Joueur {
	private WebSocket sock;
	private ServeurWS wsServeur;
	//
	
	public JoueurWS2(WebSocket sock, String ip, ServeurWS wsServeur) {
		super(Serveur.getServeur(), null, null, ip);
		this.wsServeur = wsServeur;
		this.sock = sock;
	}

	// @Override
	public void Deconnexion(String raison) {
		Deconnexion_Totale(raison);
		wsServeur.deconnexion(sock);
		sock.close();
	}
	
	// @Override
	public void Envoie(String MESSAGE) {
		try {
			//System.out.println("Envoie : " + MESSAGE);
			
			if (!sock.isClosing() && !sock.isClosed())
				sock.send(MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// @Override
	protected boolean Vérification_Mal_De_Tête(String CHAINE) {
		return false;
	}
}

