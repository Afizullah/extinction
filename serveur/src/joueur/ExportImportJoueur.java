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

package joueur;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import serveur.Boite;
import serveur.Serveur;

public class ExportImportJoueur {
	private static PrivateKey privateKey;
	private static PublicKey publicKey;
	
	private static final String PUBLIC_PATH = "cert/pubkey.key";
	private static final String PRIVATE_PATH = "cert/privkey.key";
	
	public ExportImportJoueur() {
		loadKeys();
	}
	
	public String exporterJoueur(Joueur j) {
		// n;i;s_ti;$joueur_r;HASH_ET_SIGN
		String codeJoueur = "";
		
		if (!j.AutorisationInscription) {
			j.Envoie("CxINFO#Impossible d'avoir un code joueur pour un invité.");
		}
		
		codeJoueur += j.getPseudo() + "$";
		codeJoueur += j.Avatar + "$";
		codeJoueur += j.Stats_Inscription + "$";
		codeJoueur += j.UneRecompense.replace(Serveur.$, "@") + "$";
		
		return codeJoueur + signToHex(codeJoueur);
	}
	
	public void loadKeys() {
		try {
			FileInputStream keyfis = new FileInputStream(PUBLIC_PATH);
			byte[] encKey = new byte[keyfis.available()];  
			keyfis.read(encKey);

			keyfis.close();
			
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			publicKey = keyFactory.generatePublic(keySpec);
			
			//
			
			if (false) { // Clé pour exporter - non fournie dans le code (laissé à titre d'exemple)
				keyfis = new FileInputStream(PRIVATE_PATH);
				encKey = new byte[keyfis.available()];  
				keyfis.read(encKey);

				keyfis.close();
				
				PKCS8EncodedKeySpec keySpecPriv = new PKCS8EncodedKeySpec(encKey);
				keyFactory = KeyFactory.getInstance("RSA");
				
				privateKey = keyFactory.generatePrivate(keySpecPriv);
			}
			
			System.out.println("Export/import prêt.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String signToHex(String message) {
		try {
			byte[] hash;
			Signature signingInstance;
			byte[] signature;
			
			signingInstance = Signature.getInstance("SHA384withRSA");
			signingInstance.initSign(privateKey);
	        signingInstance.update(message.getBytes());
	        
	        signature = signingInstance.sign();
	        
	        return bytesToHex(signature);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private byte[] hexStrToBytes(String signature) {
		byte[] bytes = new byte[signature.length() / 2];
		
		for (int i = 0; i < signature.length(); i += 2) {
			bytes[i / 2] = (byte)(Integer.valueOf(signature.substring(i, i + 2), 16) & 0x0FF);
		}
		
		return bytes;
	}
	
	public void importerJoueur(String joueur, Joueur importeur) {
		try {
			String[] infos;
			String signature;
			byte[] data;
			
			infos = joueur.split("\\$");
			signature = infos[infos.length - 1];
			data = joueur.substring(0, joueur.length() - signature.length()).getBytes();
			
			if (infos.length != 5 || signature.length() != 512) { // Format incorrect
				importeur.Envoie("CxINFO#Format incorrect.");
				return;
			}
	        
	        
	        Signature dataVerifyingInstance = Signature.getInstance("SHA384withRSA");
	        dataVerifyingInstance.initVerify(publicKey);
	        dataVerifyingInstance.update(data);
	        boolean dataVerified = dataVerifyingInstance.verify(hexStrToBytes(signature));
	        
	        if (dataVerified) {
	        	Serveur.getServeur().BOITE.Requete(Boite.IMPORTER_COMPTE, importeur, infos);
	        } else {
	        	importeur.Envoie("CxINFO#Le code d'importation est falsifié.");
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String bytesToHex(byte[] str) {
		String hex = "";
		
		for (int i = 0; i < str.length; ++i) {
			hex += Integer.toHexString((str[i] >> 4) & 0x0F) + Integer.toHexString(str[i]  & 0x0F);
		}
		
		return hex;
	}
	
	// Fonctions utilisées une seule fois
	
	/**
	 * Sauve les clés publique et privée générées.
	 */
	public void saveKeys() {
		try {
			byte[] key = privateKey.getEncoded();
			FileOutputStream keyfos = new FileOutputStream(PRIVATE_PATH);
			keyfos.write(key);
			keyfos.close();
			
			key = publicKey.getEncoded();
			keyfos = new FileOutputStream(PUBLIC_PATH);
			keyfos.write(key);
			keyfos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Génère les clés publique et privée.
	 */
	public void generateKeys() {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(2048);
			KeyPair keypair = keygen.generateKeyPair();
			
			privateKey = keypair.getPrivate();
			publicKey = keypair.getPublic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

