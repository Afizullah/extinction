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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

// Import impossible nouvelles versions java
//import javax.xml.bind.DatatypeConverter;

import joueur.Joueur;
import serveur.Local;
import serveur.Serveur;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;

public class WebSocketServer extends Server implements Runnable {
	private Hashtable<SocketChannel, Joueur> lsClefSocketJoueur = new Hashtable<SocketChannel, Joueur>();
	private ArrayList<Joueur> lsClients = new ArrayList<Joueur>();

	public WebSocketServer() {
		super((short)(4454)); // 4445 new dev
	}

	@Override
	protected void connexion(SocketChannel sockChan) {
		try {
			Socket Socket = sockChan.socket();
			InetAddress INA = Socket.getInetAddress();
			String ip = INA.getHostAddress();
			Socket.setTcpNoDelay(true);
			
			JoueurWS j = new JoueurWS(Serveur.getServeur(), this, sockChan, ip);
			
			if (Serveur.getServeur().FiltrageHost) {
				new serveur.Host(j, INA);
			}
			
			lsClefSocketJoueur.put(sockChan, j);
			lsClients.add(j);
		} catch (Exception e) {
			System.err.println("3XC connexion "); e.printStackTrace();
		}
	}
	
	public void deconnexion(SocketChannel sockChan, String raison, SelectionKey key) {
		Joueur j = lsClefSocketJoueur.get(sockChan);
		
		if (key == null) {
			key = sockChan.keyFor(selecteur);
		}
		
		j.Deconnexion_Totale(raison);
		
		super.deconnexion(key , raison);
		lsClefSocketJoueur.remove(key);
		lsClients.remove(j);
	}
	
	public void deconnexion(SelectionKey key, String raison) {
		deconnexion((SocketChannel)key.channel(), raison, key);
	}
	
	public void deconnexion(SocketChannel sockChan, String raison) {
		deconnexion(sockChan, raison, null);
	}
	
	public ArrayList<Joueur> getLsClients() {
		return lsClients;
	}

	@Override
	protected void GestionMessage(ByteBuffer buffer, int longueur, SelectionKey key) throws Exception {
		byte[] message = new byte[longueur];
		SocketChannel sockChan = (SocketChannel)key.channel();
		
		//System.out.println("Message reçu : " + new String(buffer.array(), java.nio.charset.StandardCharsets.UTF_8));
		
		System.arraycopy(buffer.array(), 0, message, 0, longueur);
		
		String chaine = new String(message, "UTF8");
		
		//System.out.println("Reçu : " + chaine);
		
		if (chaine.endsWith("\r\n\r\n")) {
			String matchEnTete = "Sec-WebSocket-Key: ";
			int idxDebut = chaine.indexOf(matchEnTete);
			if (idxDebut == -1) {
				System.out.println("Déconnexion");
				deconnexion(key, "Bad HTTP Handshake");
				return;
			}
			
			int idxFin = chaine.indexOf("\r\n", idxDebut);
			String clefDemande = chaine.substring(idxDebut + matchEnTete.length(), idxFin);
			try {
				// Non testé (classe obsolète a priori, compatible avec les nouvelles versions de Java)
				// https://stackoverflow.com/questions/19743851/base64-java-encode-and-decode-a-string
				Base64.Encoder enc = Base64.getEncoder();

		        String clefReponse = enc.encodeToString(MessageDigest
						.getInstance("SHA-1")
						.digest((clefDemande + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
							.getBytes("UTF-8")));
				
		        //
				/*String clefReponse = DatatypeConverter.printBase64Binary(MessageDigest
					.getInstance("SHA-1")
					.digest((clefDemande + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
						.getBytes("UTF-8")));*/
				
				String msg = "HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Version: 13\r\nSec-WebSocket-Accept: " + clefReponse + "\r\n\r\n";
				envoieToChannel(sockChan, msg);
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				e.printStackTrace();
				deconnexion(key, "Encodage/Algo erreur");
				return;
			}
		} else {
			try {
				JoueurWS j = (JoueurWS)lsClefSocketJoueur.get(sockChan);
				
				chaine = analyseMessage(message, sockChan, j);
				if (chaine != null) {
					//System.out.println("Message reçu : " + chaine);
					
					if (j != null) {
						//String[] lsMessage = chaine.split("\u0000");
						//j.Gardien(lsMessage[0]);
						j.Gardien(chaine);
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized boolean envoyer(String msg, SocketChannel sockChan) throws Exception {
		byte[] msgBytes = msg.getBytes("UTF-8");
		byte[] buffer;
		int i;
		
		if (msgBytes.length < 126) { // longueur tient dans 7 bits
			buffer = new byte[msgBytes.length + 2]; // opcode (1) + playload longueur (1)
			buffer[1] = (byte)(msgBytes.length);
			
			i = 2;
		} else if (msgBytes.length < 0x0FFFF) { // longueur tient dans 15 bits
			buffer = new byte[msgBytes.length + 4]; // opcode (1) + playload longueur (3)
			buffer[1] = (byte)(0x7E);
			buffer[2] = (byte)((msgBytes.length & 0x0FF00) >> 8);
			buffer[3] = (byte)(msgBytes.length & 0x0FF);
			
			i = 4;
		} else { // longueur tient dans 63 bits
			buffer = new byte[msgBytes.length + 10]; // opcode (1) + playload longueur (9)
			buffer[1] = (byte)(0x7F);
			
			for (i = 2; i < 10; ++i) {
				buffer[i] = (byte)((msgBytes.length & (0xFF00000000000000l >> ((i - 2) * 8)) >> ((10 - (i - 2)) * 8)));
			}
		}
		
		// Opcode
		buffer[0] = (byte)0x082; // TODO test => 0x082
		
		// Copie données
		System.arraycopy(msgBytes, 0, buffer, i, msgBytes.length);
		
		/*System.out.println(" >>>> Envoie : ");
		for (i = 0; i < buffer.length; ++i)
			System.out.println("[" + i + "]   " + Integer.toHexString(0x0FF & buffer[i]) + "   ::   '" + ((char)buffer[i]) + "'");
		*/
		
		/**byte[] msgBytes = msg.getBytes();
		int i = 0;
		final int max = (int)Math.ceil((double)(msgBytes.length) / MAX_SZ_PAQUET);
		
		System.out.println(" >>> START SEND");
		
		for (i = 0; i < max; ++i)
			envoieToChannel(sockChan, creerPaquet(msgBytes, i * MAX_SZ_PAQUET));
		
		System.out.println(" >>>> STOP SEND\n\n");**/
		
		return envoieToChannel(sockChan, buffer);
	}
	
	private final int MAX_SZ_PAQUET = 1518;
	
	private byte[] creerPaquet(byte[] msgBytes, int start) throws Exception {
		// 1518
		byte[] buffer;
		int i;
		int sz = msgBytes.length - start;
		int fin = 0x080;
		int type = (start == 0 ? 0x02 : 0x00);
		
		if (sz < 126) { // longueur tient dans 7 bits
			buffer = new byte[sz + 2]; // opcode (1) + playload longueur (1)
			buffer[1] = (byte)(sz);
			
			i = 2;
			
			fin = 0x080;
		} else {
			int paquetSz = Math.min(MAX_SZ_PAQUET, sz);
			
			buffer = new byte[paquetSz + 4]; // opcode (1) + playload longueur (3)
			
			buffer[1] = (byte)(0x7E);
			
			buffer[2] = (byte)((paquetSz & 0x0FF00) >> 8);
			buffer[3] = (byte)(paquetSz & 0x0FF);
			
			fin = (sz <= MAX_SZ_PAQUET ? 0x080 : 0);
			
			i = 4;
			sz = paquetSz;
		}
		
		// Opcode
		buffer[0] = (byte)(type | fin); // TODO test => 0x082
		
		// Copie données
		System.arraycopy(msgBytes, start, buffer, i, sz);
		
		System.out.println(" * Build message: " + i + " -> " + (i + sz)
				         + " (" + sz + "/" + (msgBytes.length - start) + ")  --- FIN: " + (fin == 0x080) + " (" + (type | fin) + ")");
		
		return buffer;
	}
	
	
	/*
0               1               2               3             
0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
+-+-+-+-+-------+-+-------------+-------------------------------+
|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
|I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
|N|V|V|V|       |S|             |   (if payload len==126/127)   |
| |1|2|3|       |K|             |                               |
+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
 4               5               6               7              
+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
|     Extended payload length continued, if payload len == 127  |
+ - - - - - - - - - - - - - - - +-------------------------------+
 8               9               10              11             
+ - - - - - - - - - - - - - - - +-------------------------------+
|                               |Masking-key, if MASK set to 1  |
+-------------------------------+-------------------------------+
 12              13              14              15
+-------------------------------+-------------------------------+
| Masking-key (continued)       |          Payload Data         |
+-------------------------------- - - - - - - - - - - - - - - - +
:                     Payload Data continued ...                :
+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
|                     Payload Data continued ...                |
+---------------------------------------------------------------+
	 */
	/// TODO : Relire les conditions pour refuser un msg + traiter les msg fragmentés + sécurité msg trop gros (?)
	private String analyseMessage(byte[] buffer, SocketChannel sockChan, JoueurWS joueur) throws UnsupportedEncodingException {
		boolean fin = (buffer[0] & 0b10000000) != 0;
		int opcode = buffer[0] & 0x0F;
		int i = 1;
		long longueur = 0; // Note : non-signé
		
		if ((buffer[i] & 0x80) == 0) { // Message pas masqué => standard indique de ne pas accepter
			System.err.println("[WS] Erreur : message non masqué par le client.");
			return null;
		}
		
		// Calcul de la longueur des données
		buffer[i] = (byte)(buffer[i] & 0x7F);
		
		if (buffer[i] == 0x7E) { // Longueur playload de 16 bits
			++i;
			longueur = (buffer[i++] << 8) | buffer[i++];
		} else if (buffer[i] == 127) { // Longueur playload de 64 bits
			++i;
			longueur = ((buffer[i++] << 56) | (buffer[i++] << 48) | (buffer[i++] << 40) | (buffer[i++] << 32)
					 | (buffer[i++] << 24) | (buffer[i++] << 16) | (buffer[i++] << 8) | buffer[i++]) & 0x7FFFFFFFFFFFFFFFl;
		} else { // Longueur playload de <buffer[i]> bits
			longueur = buffer[i++];
		}
		//
		
		if ((opcode & 0x0F) == 0x09) { // Requête Ping >>> envoie Pong
			if (longueur < 125) { // Pas de pong sur requête > 125
				Pong(sockChan, buffer);
				System.out.println(" >>>> Pong envoyé");
			}
			return null;
		}
		
		// Décodage message
		int[] mask = {buffer[i++] & 0xFF, buffer[i++] & 0xFF, buffer[i++] & 0xFF, buffer[i++] & 0xFF}; // Masque d'encodage
		byte[] msgDecode = new byte[buffer.length - i];
		
		for (int j = 0; j < longueur; ++j) {
			msgDecode[j] = (byte)(buffer[i + j] ^ mask[j % 4]);
		}
		//
		
		if (!fin) {
			joueur.addMessageFragmente(new String(msgDecode, "UTF8"));
			return null;
		} else {
			return joueur.getFullMessage(new String(msgDecode, "UTF8"));
		}
	}
	
	/*
	 Le ping ou le pong sont des trames classiques dites de contrôle. Les pings disposent d'un opcode à 0x9 et
	 les pongs à 0xA. Lorsqu'un ping est envoyé, le pong doit disposer de la même donnée utile en réponse que
	 le ping (et d'une taille maximum autorisée de 125). Le pong seul (c-à-d sans ping) est ignorée.
	*/
	private void Pong(SocketChannel sockChan, byte[] buffer) {
		try { // TODO : retirer le masque dans la réponse (pas accepté pr Chrome apparemment)
			buffer[0] = (byte)((buffer[0] & 0xF0) | 0x0A);
			envoieToChannel(sockChan, buffer);
		} catch (Exception e) {
			System.err.println("Pong a échoué : " + e.getMessage());
		}
	}

	@Override
	public void run() {
		demarrer();
	}
}
