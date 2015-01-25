package mta.se.cryptomodule;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.simple.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Random;

/**
 * Created by ION on 25.01.2015.
 */
public class Gateway {
    public static int gtwyPort = 0;
    public static HashMap<String, ServersWrapper> servers = null;
    private static ServerSocket gtwySocket;

    public static void main(String[] args) {
        try {

            servers = new HashMap<String, ServersWrapper>();

            BufferedReader rdr = new BufferedReader(new FileReader(args[0]));
            String line = rdr.readLine();
            if (line == null) {
                System.out.println("ERROR");
                System.out.println("IO ERROR");
                System.exit(1);
            }
            String[] _line = line.split(" ");

            servers.put(_line[2], new ServersWrapper(_line[0], Integer.parseInt(_line[1]), Integer.parseInt(_line[2])));

            int i = 1;
            line = rdr.readLine();
            while (line != null) {
                i++;
                _line = line.split(" ");
                servers.put(_line[2], new ServersWrapper(_line[0], Integer.parseInt(_line[1]), Integer.parseInt(_line[2])));
                line = rdr.readLine();
            }
            if (i != 5) {
                System.out.println("ERROR");
                System.out.println("Not enough servers information in config file");
                System.exit(1);
            }

            gtwyPort = Integer.parseInt(args[1]);
            if ((gtwyPort < 1) || (gtwyPort > 65537)) {
                rdr.close();
                System.out.println("ERROR");
                System.out.println("Bad port");
                System.exit(1);
            }

            rdr.close();


            gtwySocket = new ServerSocket(gtwyPort);
            String data_from_client;
            String data_to_client;

            while (true) {
                SSLSocket sockClient = gtwySocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(sockClient.getInputStream()));
                DataOutputStream out = new DataOutputStream(sockClient.getOutputStream());
                data_from_client = in.readLine();

                JSONObject jsonObj = new JSONObject();
                JSONParser parser = new JSONParser();
                jsonObj = (JSONObject) parser.parse(data_from_client);

                String opcode = (String) jsonObj.get("Opcode");
                String req = (String) jsonObj.get("Request");

                data_to_client = contactServer(Integer.parseInt(opcode), req);
                StringBuilder jsonResponse = new StringBuilder();
                jsonResponse.append("{\"Opcode\":\"");
                jsonResponse.append(opcode);
                jsonResponse.append("\",\"Response\":\"");
                jsonResponse.append(data_to_client);
                jsonResponse.append("\"}\n");
                out.writeBytes(jsonResponse.toString());

                sockClient.close();

            }


        } catch (Exception e) {

            System.out.println("ERROR");
            System.out.println("Error ocured at gateway.");
            System.exit(1);
        }


    }

    private static String contactServer(int serverType, String request) {


        String response = null;

        String serverIP = servers.get(Integer.toString(serverType)).getIP();
        int serverPORT = servers.get(Integer.toString(serverType)).getPORT();

        try {
            Socket sock = new Socket(serverIP, serverPORT);
            DataOutputStream out_to_server = new DataOutputStream(sock.getOutputStream());
            DataInputStream in_from_server = new DataInputStream(sock.getInputStream());

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(512); // is the minium size
            KeyPair kp = kpg.generateKeyPair();
            DHParameterSpec dhSpec = ((DHPublicKey) kp.getPublic()).getParams();
            BigInteger _g = dhSpec.getG();
            BigInteger _p = dhSpec.getP();

            byte[] g = _g.toByteArray();
            byte[] p = _p.toByteArray();
            out_to_server.writeInt(g.length);
            out_to_server.write(g, 0, g.length);

            out_to_server.writeInt(p.length);
            out_to_server.write(p, 0, p.length);

            BigInteger myRandom = new BigInteger(40, 0, new Random());
            BigInteger _a = _g.modPow(myRandom, _p);
            byte[] a = _a.toByteArray();
            out_to_server.writeInt(a.length);
            out_to_server.write(a, 0, a.length);

            int b_size;
            b_size = in_from_server.readInt();
            byte[] b = new byte[b_size];
            in_from_server.read(b, 0, b_size);
            BigInteger _b = new BigInteger(b);

            BigInteger secret = _b.modPow(myRandom, _p);
            byte[] req = request.getBytes(Charset.defaultCharset());
            byte[] input = new byte[request.length()];
            System.arraycopy(req, 0, input, 0, request.length());

            byte[] sec = secret.toByteArray();
            byte[] key128 = new byte[16];
            System.arraycopy(sec, 0, key128, 0, 16);


            SecretKeySpec key = new SecretKeySpec(key128, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ciphertext = cipher.doFinal(input);


            out_to_server.writeInt(ciphertext.length);
            out_to_server.write(ciphertext, 0, ciphertext.length);

            int len_encRespone = in_from_server.readInt();
            byte[] encRes = new byte[len_encRespone];
            in_from_server.read(encRes, 0, len_encRespone);

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainResult = cipher.doFinal(encRes);
            String toClientData = new String(plainResult);


            sock.close();
            return toClientData;

        } catch (Exception e) {

            System.out.println("ERROR");
            System.out.println("Server not online");
            System.exit(1);
        }


        return response;
    }

}
