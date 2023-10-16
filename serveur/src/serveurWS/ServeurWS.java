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

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import joueur.Joueur;
import serveur.Local;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ServeurWS extends WebSocketServer {
	private final boolean TLS = false;
	
	private Hashtable<WebSocket, Joueur> lsSocketJoueur = new Hashtable<WebSocket, Joueur>();
	private ArrayList<Joueur> lsClients = new ArrayList<Joueur>();
	
	/*public static void main( String[] args ) throws InterruptedException , IOException {
	int port = 8887; // 843 flash policy port
	try {
		port = Integer.parseInt( args[ 0 ] );
	} catch ( Exception ex ) {
	}
	wsServeur2 s = new wsServeur2( port );
	s.start();
	System.out.println( "ChatServer started on port: " + s.getPort() );

	BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
	while ( true ) {
		String in = sysin.readLine();
		s.broadcast( in );
		if( in.equals( "exit" ) ) {
			s.stop(1000);
			break;
		}
	}
}*/

	public ServeurWS() throws Exception {
		super(new InetSocketAddress(4499)); // 4445 new dev
		System.out.println("Port serveur WS : " + 4499);
	}
	
	public ServeurWS( InetSocketAddress address ) {
		super( address );
	}
	
	public ArrayList<Joueur> getLsClients() {
		return lsClients;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		String ip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
		
		System.out.println("Connexion ### " + ip);
		
		Joueur j = new JoueurWS2(conn, ip, this);
		lsSocketJoueur.put(conn, j);
		lsClients.add(j);
	}

	@Override
	public void onClose(WebSocket conn, int code, String raison, boolean remote) {
		Joueur  j = lsSocketJoueur.get(conn);
		if (j != null) {
		   j.Deconnexion(raison);
		}
	}
	
	public void deconnexion(WebSocket conn) {
		lsClients.remove(lsSocketJoueur.get(conn));
		lsSocketJoueur.remove(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		//System.out.println("Recu: " + message);
		Joueur  j = lsSocketJoueur.get(conn);
		if (j != null)
		   j.Gardien(message);
	}
	
	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		//broadcast( message.array() );
		//System.out.println( conn + ": " + message );
	}


	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.print("ErreurWS : ");
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(100);
	}

}