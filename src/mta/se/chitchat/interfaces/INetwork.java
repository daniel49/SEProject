package core.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The network interface
 */
public interface INetwork {
	public void connect(InetAddress addr, String strPort);

	public void disconnect();

	public boolean isConnected();

	public InetAddress getPeer();

	public void setListen(boolean bListen);

	public boolean listen();

	public InputStream createReceiveStream() throws IOException;

	public OutputStream createSendStream() throws IOException;
}


