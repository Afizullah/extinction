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

import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.net.*;
import java.util.*;

import joueur.Joueur;

public class Nio {

	private Serveur Serveur;
	public Selector selector;
	private int Port;
	private boolean Active = true;
	private long TempsTimeOut;
	private long TempsBoite;
	private long TempsMinute;
	private long TempsHeure;
	static public boolean COURBE = false;

	static public final HashMap<String, Long> Connexion = new HashMap<String, Long>();
	static public final HashMap<String, Integer> Attaque = new HashMap<String, Integer>();
	
	// Main entry point.
	public Nio(Serveur SERVEUR, int PORT) {
		int count_min_to_h = 0;
		
		Serveur = SERVEUR;
		Port = PORT;

		TempsTimeOut = System.currentTimeMillis();
		TempsBoite = System.currentTimeMillis();
		TempsMinute = System.currentTimeMillis();
		TempsHeure = System.currentTimeMillis();

		try {
			selector = SelectorProvider.provider().openSelector();

			// Create non-blocking server socket.
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);

			// Bind the server socket to localhost.
			InetSocketAddress isa = new InetSocketAddress(Port);
			ssc.socket().bind(isa);

			// Register the socket for select events.
			//SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			
			System.out.println("Port serveur : " + PORT);

			// Loop forever.
			long AntiPlantage = System.currentTimeMillis();
			while (Active) {
				try{
					long Temps = System.currentTimeMillis();
					selector.select(1);
					Set<SelectionKey> readyKeys = selector.selectedKeys();
					Iterator<SelectionKey> i = readyKeys.iterator();
					while (i.hasNext()) {
						SelectionKey sk = (SelectionKey) i.next();
						i.remove();
						if (Temps - AntiPlantage > 50) {
							AntiPlantage = Temps;
							if (sk.isValid() && sk.isAcceptable()) {
								Connexion(sk);
							}
						}
						if (sk.isValid() && sk.isReadable()) {
							Lecture(sk);
						}
						if (sk.isValid() && sk.isWritable()) {
							Ecriture(sk);
						}
					}
					// Actions
					if (!Serveur.ListeAction.isEmpty()) {
						Serveur.Action Action = Serveur.ListeAction.get(0);
						if (Action.Activation < Temps) {
							Serveur.ListeAction.remove(0);
							Action.Déclenchement();
						}
					}
					// Base de donnée
					if (Temps - TempsBoite > 100) {
						TempsBoite = Temps;
						// Envoie des messages
						//int NbClient = Serveur.ListeClient.size();
						EnvoieMessagesBoite(Serveur.ListeClient);
						if (serveur.Serveur.WebSocketVisibles()) {
							//EnvoieMessagesBoite(serveur.Serveur.serverWS.getLsClients());
							EnvoieMessagesBoite(serveur.Serveur.serveurWS.getLsClients());
						}
						
						//
						while (!Serveur.JoueurAAjouter.isEmpty()) {
							Joueur Joueur = Serveur.JoueurAAjouter.poll();
							String NomJoueur = Joueur.NomJoueur;
							//boolean JoueurAbsent = true;
							int NbJoueur = Serveur.ListeJoueur.size();
							for (int k = 0; k < NbJoueur; k++) {
								Joueur Cible = Serveur.ListeJoueur.get(k);
								if (Cible.NomJoueur.equals(NomJoueur)) {
									Serveur.ListeJoueur.remove(Cible);
									System.out.println("GH0ST "+NomJoueur);
									break;
								}
							}
							Serveur.ListeJoueur.add(Joueur);
						}
					}
					// Time Out
					if (Temps - TempsTimeOut > 15000) {
						TempsTimeOut = Temps;
						// TODO : timeout websockets
						int NbClient = Serveur.ListeClient.size();
						for (int C = 0; C < NbClient; C++) {
							Joueur Client = Serveur.ListeClient.get(C);
							if (Client.Ronflement) {
								Client.Deconnexion("Time out");
								NbClient--;
								C--;
							} else {
								Client.Ronflement = true;
							}
						}
					}
                    if(Temps - TempsMinute > 60 * 1000 ) {
                        int TempsMax = 90 * 60 * 1000;
                        for (Joueur joueur : Serveur.ListeJoueur) {
                            if (joueur.PartieEnCours != null && joueur.PartieEnCours.hasMotDePasse()) {
                                long TempsPartie = (System.currentTimeMillis() - joueur.TempsZero);
                                if (TempsPartie > TempsMax && TempsPartie < TempsMax + 60 * 1000) {
                                    Serveur.Avertissement_Modo(joueur.NomJoueur + " est depuis 90 minutes dans la partie : " + joueur.Salon(), false);
                                }
                            }
                        }
                        TempsMinute = Temps;
                        
                        // Toutes les heures :
                        ++count_min_to_h;
                        if (count_min_to_h > 59) {
                        	System.out.println("NJC " + Serveur.ListeJoueur.size() + " [" + (System.currentTimeMillis() / 1000) + "]");
                        	count_min_to_h = 0;
                        }
                    }
                    
					// Temps heure
					if (Temps - TempsHeure > 86400000) { // 24 heures
						TempsHeure = Temps;
						Serveur.Toutes_Les_Heures();
					}
					//
					if (COURBE) {
						Serveur.Courbe_Performance(System.currentTimeMillis() - Temps);
					}
				} catch (Exception e) {
					System.err.print("3XC NioWhile "); e.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.err.print("3XC Nio "); e.printStackTrace();
			System.out.println("## Arrêt du serveur");
			System.exit(0);
		}
	}

	private void EnvoieMessagesBoite(ArrayList<Joueur> Ls) {
		for (int C = 0; C < Ls.size(); C++) {
			Joueur Client = Ls.get(C); // plantage ArrayOutOfBound
			while (!Client.MessageBoite.isEmpty()) {
				Client.Envoie(Client.MessageBoite.poll());
			}
		}
	}

	private void Connexion(SelectionKey sk) {
		ServerSocketChannel server = (ServerSocketChannel) sk.channel();
		SocketChannel clientChannel;
		try {
			clientChannel = server.accept();
			clientChannel.configureBlocking(false);

			// Register this channel for reading.
			//SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
			clientChannel.register(selector, SelectionKey.OP_READ);

			Socket Socket = clientChannel.socket();
			InetAddress INA = Socket.getInetAddress();
			String AdresseIP = INA.getHostAddress();
			Socket.setTcpNoDelay(true);


			Joueur Joueur = new Joueur(Serveur, this, clientChannel, AdresseIP);		
			if (Serveur.FiltrageHost) {
				new Host(Joueur, INA);
			}
			//clientKey.attach(Joueur);
			Serveur.ListeClient.add(Joueur);
			Serveur.ListeClefSocketJoueur.put(clientChannel, Joueur);
			System.out.println("Connexion ### " + AdresseIP);
		} catch (Exception e) {
			System.out.println("Failed to accept new client.");
			System.err.print("3XC Connexion "); e.printStackTrace();
		}
	}

	/**
	 * Read from a client. Enqueue the data on the clients output
	 * queue and set the selector to notify on OP_WRITE.
	 */
	private void Lecture(SelectionKey key) {
		try {
			SocketChannel CanalSocket = (SocketChannel) key.channel();
			Joueur Joueur = Serveur.ListeClefSocketJoueur.get(CanalSocket);
			SocketChannel channel = (SocketChannel) key.channel();
			ByteBuffer readBuffer = ByteBuffer.allocate(8192);
			int Longueur;

			try {
				Longueur = channel.read(readBuffer);
				if (Longueur < 0) {
					Deconnexion(key, "Deconnexion");
					return;
				}
			} catch (Exception e) {
				Deconnexion(key, "Impossible de lire un message");
				return;
			}

			MessageDecoupe MessageDecoupe;
			if (Joueur.MessageDecoupe == null) {
				Joueur.MessageDecoupe = new MessageDecoupe();
				MessageDecoupe = Joueur.MessageDecoupe;
			} else {
				MessageDecoupe = Joueur.MessageDecoupe;
			}

			byte[] Message = new byte[Longueur];
			System.arraycopy(readBuffer.array(), 0, Message, 0, Longueur);
			MessageDecoupe.ListeMessage.add(Message);
			MessageDecoupe.LongueurListeMessage += Longueur;
			MessageDecoupe.NombreMessage++;
			int FinMessage = Longueur - 1;

			if (Message[FinMessage] == 0) {
				byte[] MessageComplet = new byte[MessageDecoupe.LongueurListeMessage];
				int Position = 0;
				for (int i = 0; i < MessageDecoupe.NombreMessage; i++) {
					byte[] SousMessage = MessageDecoupe.ListeMessage.get(i);
					int Nb = SousMessage.length;
					for (int k = 0; k < Nb; k++) {
						MessageComplet[Position] = SousMessage[k];
						Position++;
					}
				}
				MessageDecoupe.ListeMessage.clear();
				Joueur.MessageDecoupe = null;

				String Chaine = new String(MessageComplet, "UTF8");
				
				String[] LsMessage = Chaine.split("\u0000");
				int NbMessage = LsMessage.length;
				for (int i = 0; i < NbMessage; i++) {
					Joueur.Gardien(LsMessage[i]);
				}
			}
			//
			if (Joueur.Ban) {
				Deconnexion(key, Joueur.RaisonDeco);
			} else {
				if (Joueur.DemandeDeco) {
					Deconnexion(key, "Deconnexion");
				}
			}
		} catch (Exception e) {
			try {
				System.out.println("3XC ??? " + e.toString());
			} catch (Exception z) {
				z.printStackTrace();
			} finally {
				Deconnexion(key, "Impossible de lire un message");
			}
		}
	}

	public void Ecriture(SelectionKey sk) {
		SocketChannel channel = (SocketChannel) sk.channel();
		try {
			LinkedList<ByteBuffer> outq = Serveur.ListeClefSocketJoueur.get(channel).ListeMessage;
			if (!outq.isEmpty()) {
				ByteBuffer bb = outq.getLast();
				try {
					int len = channel.write(bb);
					if (len == -1) {
						Deconnexion(sk, "Deconnexion");
						return;
					}

					if (bb.remaining() == 0) {
						// The buffer was completely written, remove it.
						outq.removeLast();
					}
				} catch (Exception e) {
					Deconnexion(sk, "Impossible d'écrire un message.");
				}

				// If there is no more data to be written, remove interest in
				// OP_WRITE.
				if (outq.size() == 0) {
					sk.interestOps(SelectionKey.OP_READ);
				}
			} else {
				sk.interestOps(SelectionKey.OP_READ);
			}
		} catch (Exception e) {
			Deconnexion(sk, "Deconnexion");
		}
	}

	public void Deconnexion(SelectionKey sk, String Message) {
		try {
			SocketChannel channel = (SocketChannel) sk.channel();
			Serveur.ListeClefSocketJoueur.get(channel).Deconnexion_Totale(Message);
		} catch (Exception e) {
		}
	}
}