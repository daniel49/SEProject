/**
 * 
 */
package mta.se.chitchat.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;

/**
 * @author Ilie Daniel
 * @author Cosovanu Vasile
 * 
 * Class that implements the key pair generation, the Diffie-Hellmann handshake
 * and the AES encrypt and decrypt methods
 */
public class Security {
	
	/**
	 * Function used for creating certificate and key pair
	 * @author Cosovanu Vasile
	 */
	public void createCert(){
	
		GenerateKeyPair generateKeyPair = new GenerateKeyPair();
		try {
			generateKeyPair.generateKeys();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * For the client who creates the connection
	 * @author Ilie Daniel
	 * @param outStream
	 * @param inputStream
	 * @return
	 */
	public byte[] diffieHellmannActive(OutputStream outStream,
			DataInputStream inputStream) {

		try {

			PrintWriter out = new PrintWriter(outStream, true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			// facem schimbul de chei
			BigInteger p = new BigInteger(256, new SecureRandom());
			BigInteger g = new BigInteger(256, new SecureRandom());
			byte[] sharedSecret = null;

			try {
				GenerateKeyPair generateKeyPair = new GenerateKeyPair();
				generateKeyPair.generateKeys();
				String path = System.getProperty("user.dir");
				KeyPair kp = generateKeyPair.LoadKeyPair(path, "RSA");
				Key privateKey = kp.getPrivate();
				KeyFactory fact = KeyFactory.getInstance("RSA");
				RSAPrivateKeySpec priv = fact.getKeySpec(privateKey,
						RSAPrivateKeySpec.class);

				BigInteger privateExp = priv.getPrivateExponent();
				BigInteger publicGateKey = g.modPow(privateExp, p);

				// trimitem parametrii p si g
				out.println(p.toString());
				out.println(g.toString());
				out.println(publicGateKey.toString());

				// citesc cheia publica
				String sPair = in.readLine();
				BigInteger sKey = new BigInteger(sPair);

				BigInteger simetricKey = sKey.modPow(privateExp, p);
				sharedSecret = simetricKey.toByteArray();

				return sharedSecret;

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * For the client who receives the connection
	 * @author Ilie Daniel
	 * @param outStream
	 * @param inputStream
	 * @return
	 */
	public byte[] diffieHellmannPassive(OutputStream outStream,
			DataInputStream inputStream) {

		try {

			PrintWriter out = new PrintWriter(outStream, true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));

			BigInteger p = null;
			BigInteger g = null;
			byte[] sharedSecret = null;

			String inputLine;
			// / preluare numere
			inputLine = in.readLine();
			p = new BigInteger(inputLine);
			inputLine = in.readLine();
			g = new BigInteger(inputLine);

			try {
				GenerateKeyPair generateKeyPair = new GenerateKeyPair();
				generateKeyPair.generateKeys();
				String path = System.getProperty("user.dir");
				KeyPair kp = generateKeyPair.LoadKeyPair(path, "RSA");
				Key privateKey = kp.getPrivate();
				KeyFactory fact = KeyFactory.getInstance("RSA");
				RSAPrivateKeySpec priv = fact.getKeySpec(privateKey,
						RSAPrivateKeySpec.class);
				BigInteger privateExp = priv.getPrivateExponent();
				BigInteger publicServerKey = g.modPow(privateExp, p);
				// citesc cheia publica
				String gPair = in.readLine();
				BigInteger gKey = new BigInteger(gPair);

				// trimit cheia publica
				out.println(publicServerKey.toString());

				BigInteger simetricKey = gKey.modPow(privateExp, p);
				sharedSecret = simetricKey.toByteArray();

				return sharedSecret;

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Functie de criptare AES
	 * @author Ilie Daniel
	 * @param key1 cheia
	 * @param key2 iv
	 * @param value mesajul de criptat
	 * @return mesajul criptat
	 */
	public byte[] encrypt(byte[] key1, byte[] key2, byte[] value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(key2);

			SecretKeySpec skeySpec = new SecretKeySpec(key1, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value);
			return encrypted;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Functie de decriptare AES
	 * @author Ilie Daniel
	 * @param key1 cheia
	 * @param key2 iv
	 * @param encrypted textul de decriptat
	 * @return mesajul decriptat
	 */
	public byte[] decrypt(byte[] key1, byte[] key2, byte[] encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(key2);

			SecretKeySpec skeySpec = new SecretKeySpec(key1, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(encrypted);
			return original;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
