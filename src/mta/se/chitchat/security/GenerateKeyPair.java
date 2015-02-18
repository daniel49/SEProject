/**
 * 
 */
package mta.se.chitchat.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Cosovanu Vasile
 *
 * Class used for creating a key pair and export them into files
 */
public class GenerateKeyPair {
	
	/**
	 * Function used for generating key pair
	 * @author Cosovanu Vasile
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws java.io.IOException
	 */
	public void generateKeys() throws NoSuchAlgorithmException, IOException{

		String path = System.getProperty("user.dir");
		File file = new File(path + "/private.cert");
		if(file.exists()){
			System.out.println("Cheia privata este deja creata.");
			return;
		}
		
	    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	    keyPairGenerator.initialize(2048);
	    KeyPair keyPair = keyPairGenerator.genKeyPair();

	    SaveKeyPair(".", keyPair); // salvare chei
	}
	
	/**
	 * Function used to save the private and public key
	 * @author Cosovanu Vasile
	 * @param path
	 * @param keyPair
	 * @throws java.io.IOException
	 */
	public void SaveKeyPair(String path, KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
 
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/public.cert");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream(path + "/private.cert");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}
 
	/**
	 * Function used to load the key pair
	 * @author Cosovanu Vasile
	 * @param path
	 * @param algorithm
	 * @return
	 * @throws java.io.IOException
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws java.security.spec.InvalidKeySpecException
	 */
	public KeyPair LoadKeyPair(String path, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File(path + "/public.cert");
		FileInputStream fis = new FileInputStream(path + "/public.cert");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
 
		// Read Private Key.
		File filePrivateKey = new File(path + "/private.cert");
		fis = new FileInputStream(path + "/private.cert");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
 
		return new KeyPair(publicKey, privateKey);
	}
	
}
