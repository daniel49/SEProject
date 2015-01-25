package mta.se.cryptomodule;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by ION on 25.01.2015.
 */
public class Server {
    static int srvrPort = 0;
    static int srvrType = 0;
    private static ServerSocket srvrSock;

    public static void main(String[] args) {

        try {

            srvrPort = Integer.parseInt(args[1]);
            if ((srvrPort < 1) || (srvrPort > 65535)) {

                System.out.println("ERROR");
                System.out.println("Server: Bad port");
                System.exit(1);
            }

            srvrType = Integer.parseInt(args[0]);
            if ((srvrType < 1) || (srvrType > 5)) {

                System.out.println("ERROR");
                System.out.println("Server: Bad operation type");
                System.exit(1);
            }

            srvrSock = new ServerSocket(srvrPort);
            while (true) {

                Socket socketgateway = srvrSock.accept();
                DataInputStream data_to_read = new DataInputStream(socketgateway.getInputStream());
                DataOutputStream data_to_write = new DataOutputStream(socketgateway.getOutputStream());

                int g_size, p_size;
                g_size = data_to_read.readInt();

                byte[] g = new byte[g_size];
                data_to_read.read(g, 0, g_size);
                BigInteger _g = new BigInteger(g);

                p_size = data_to_read.readInt();
                byte[] p = new byte[p_size];
                data_to_read.read(p, 0, p_size);
                BigInteger _p = new BigInteger(p);

                int a_size;
                a_size = data_to_read.readInt();
                byte[] a = new byte[a_size];
                data_to_read.read(a, 0, a_size);
                BigInteger _a = new BigInteger(a);
                BigInteger myRandom = new BigInteger(40, 0, new Random());
                BigInteger _b = _g.modPow(myRandom, _p);
                byte[] b = _b.toByteArray();
                data_to_write.writeInt(b.length);
                data_to_write.write(b, 0, b.length);

                BigInteger secret = _a.modPow(myRandom, _p);

                //From this point both server and gateway have a common symetric key
                //////////////
                int c_size = data_to_read.readInt();
                byte[] ciphertext = new byte[c_size];
                data_to_read.read(ciphertext, 0, c_size);


                byte[] sec = secret.toByteArray();
                byte[] key128 = new byte[16];
                System.arraycopy(sec, 0, key128, 0, 16);


                SecretKeySpec key = new SecretKeySpec(key128, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] plaintext = cipher.doFinal(ciphertext);
                String decrypted = new String(plaintext);
                String result = work(decrypted);

                byte[] Result = result.getBytes(Charset.defaultCharset());
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] encryptedResult = cipher.doFinal(Result);
                data_to_write.writeInt(encryptedResult.length);
                data_to_write.write(encryptedResult, 0, encryptedResult.length);

                socketgateway.close();
            }
        } catch (Exception e) {

            System.out.println("ERROR");
            System.out.println("Error at server.");
            System.exit(1);
        }

    }

    public static String work(String data) {
        String processed = null;
        switch (srvrType) {
            case 1:
                processed = data.toUpperCase();
                break;
            case 2:
                processed = data.toLowerCase();
                break;
            case 3:
                String[] tokens = data.split(" ");
                StringBuilder sb = new StringBuilder();
                sb.append(toCamelCase(tokens[0]));
                for (int i = 1; i < tokens.length; i++)
                    sb.append(" " + toCamelCase(tokens[i]));
                processed = sb.toString();
                break;
            case 4:
                String[] tok = data.split(" ");
                StringBuilder sbuilder = new StringBuilder();
                for (int i = 0; i < tok.length; i++)
                    sbuilder.append(toCamelCase(tok[i]));
                processed = sbuilder.toString();
                break;
            case 5:
                processed = data.replaceAll("\\s", "");
                break;
            default:
                System.out.println("ERROR");
                System.out.println("Wrong opcode from gateway.");
                System.exit(1);
        }

        return processed;
    }

    public static String toCamelCase(String s) {
        final String ACTIONABLE_DELIMITERS = " '-/";
        StringBuilder sb = new StringBuilder();
        boolean capNext = true;
        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0);
        }
        return sb.toString();
    }
}
