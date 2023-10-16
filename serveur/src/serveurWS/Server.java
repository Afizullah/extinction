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

/// INUTILISE
package serveurWS;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

// https://developer.mozilla.org/fr/docs/WebSockets/Writing_WebSocket_servers
public abstract class Server {
	private ServerSocketChannel servSockChan;
	private boolean connexionsAcceptees;
	
	protected Selector selecteur;
	
	public Server(short port) {
		try {
			selecteur = Selector.open();
			
			servSockChan = ServerSocketChannel.open();
			servSockChan.socket().bind(new InetSocketAddress(port));
			servSockChan.configureBlocking(false);
		} catch (Exception e) {
			System.err.println("Erreur démarrage serveur.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void handleConnection() {
		try {
			SocketChannel sockChan = servSockChan.accept();
			if (sockChan != null) {
				System.out.println("Connexion ### " + sockChan.socket().getInetAddress().getHostAddress());
				sockChan.configureBlocking(false);
				sockChan.register(selecteur, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				
				connexion(sockChan);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean envoieToChannel(SocketChannel sockChan, String message) throws Exception {
		return envoieToChannel(sockChan, message.toString().getBytes("UTF-8"));
	}
	
	/**
	 * @param sockChan : channel
	 * @param message : tableau bytes encodés UTF-8
	 */
	protected boolean envoieToChannel(SocketChannel sockChan, byte[] message) throws Exception {
		try {
			sockChan.write(ByteBuffer.wrap(message));
			//System.out.println("Envoie : " + new String(message, "UTF8"));
		} catch (IOException e) {
			return false;
		}
		
		SelectionKey key = sockChan.keyFor(selecteur);
		
		try {
			if (key != null && key.isValid() && (key.interestOps() & SelectionKey.OP_WRITE) != SelectionKey.OP_WRITE) {
				key.channel().register(selecteur, SelectionKey.OP_WRITE);
			}
		} catch (Exception e) {
			System.err.print("3XC Envoie "); e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	protected abstract void connexion(SocketChannel sockChan);
	
	public void demarrer() {
		SocketChannel sockChan;
		Set<SelectionKey> selections;
		Iterator<SelectionKey> it;
		SelectionKey key = null;
		int disponnibles;
		int i, longueur;
		
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		
		connexionsAcceptees = true;
		
		while (connexionsAcceptees) {
			try {
				/// Nouvelle connexion
				handleConnection();
				
				/// Nouveau message
				disponnibles = selecteur.selectNow();
				i = 0;
				
				selections = selecteur.selectedKeys();
				it = selections.iterator();
				
				while (it.hasNext() && i < disponnibles) {
					key = it.next();
					
					if (key.isReadable()) { // Un des clients a un message à lire
						++i;
						sockChan = (SocketChannel)key.channel();
						
						try {
							longueur = sockChan.read(buffer);
						} catch (IOException e) {
							buffer.clear();
							deconnexion(key, "IOException [read]");
							continue;
						}
						
						if (longueur < 0) {
							buffer.clear();
							deconnexion(key, "Longueur < 0 [read] :: " + sockChan.isConnected() + " [" + longueur + "]");
							continue;
						}
						
						GestionMessage(buffer, longueur, key);
						buffer.clear();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				deconnexion(key, "Exception");
			}
			
		}
	}
	
	protected void deconnexion(SelectionKey key, String raison) {
		if (key == null)
			return;
		
		try {
			SocketChannel sockChan = (SocketChannel)key.channel();
			System.out.println("Déconnexion ### " + sockChan.isConnected() + " ### "+ sockChan.socket().getInetAddress().getHostAddress()
							+ (raison == null ||raison.isEmpty()?"": " : " + raison));
			sockChan.close();
			key.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void stopper() {
		try {
			connexionsAcceptees = false;
			servSockChan.close();
		} catch (Exception e) {
			System.err.println("Erreur fermeture serveur.");
		}
		
	}
	
	
	protected void GestionMessage(ByteBuffer buffer, int longueur, SelectionKey key) throws Exception{
		/*byte[] message = new byte[longueur];
		System.arraycopy(buffer.array(), 0, message, 0, longueur);
		
		String chaine = new String(message, "UTF8");
		String[] requete = chaine.split("\r\n");
		
		if (requete.length > 0 && requete[0].startsWith("GET")) {
			String arg[] = requete[0].split("\\s+");
			
			//sockChan.write(ByteBuffer.wrap(forum.toString().getBytes("UTF-8")));
		}*/
	}
}
