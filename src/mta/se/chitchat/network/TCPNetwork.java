package core.network;

import static core.utils.Constants.TCP_NODELAY;
import static core.utils.Constants.TCP_RECEIVE_BUFFER_SIZE;
import static core.utils.Constants.TCP_SEND_BUFFER_SIZE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;

import core.model.MasterModel;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut
 * </p> Software Engineering Project
 * </p>  The TCP network functionalities
 */
public class TCPNetwork extends BaseNetwork {
	private ServerSocket m_serverSocket;
	private Socket m_commSocket;

	public TCPNetwork(MasterModel masterModel) {
		super(masterModel);
	}

	private static void setSocketOptions(Socket socket) {
		try {
			socket.setTcpNoDelay(TCP_NODELAY);
			if (TCP_SEND_BUFFER_SIZE > 0)
				socket.setSendBufferSize(TCP_SEND_BUFFER_SIZE);
			if (TCP_RECEIVE_BUFFER_SIZE > 0)
				socket.setReceiveBufferSize(TCP_RECEIVE_BUFFER_SIZE);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connects to the remote host represented by the ip and port
	 */
	@Override
	public void connect(InetAddress addr, String strPort) {
		try {
			if (!strPort.isEmpty()) {
				int port;
				port = Integer.parseInt(strPort);
				if (port < 0 || port > 65535) {
					JOptionPane.showMessageDialog(null, "Invalid port "
							+ strPort
							+ ". Port number must be between 0 and 65535.");
					return;
				}
				m_commSocket = new Socket(addr, port);

			} else {
				m_commSocket = new Socket(addr, getPort());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid port " + strPort);
			e.printStackTrace();
		}
		setSocketOptions(m_commSocket);
	}

	public InetAddress getPeer() {
		return m_commSocket.getInetAddress();
	}

	/**
	 * Used for disconnecting
	 */
	public void disconnect() {
		try {
			m_commSocket.close();
			m_commSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return m_commSocket != null && !m_commSocket.isClosed();
	}

	/**
	 * Recreates the server socket object used for incoming connections
	 */
	public void setListen(boolean bListen) {
		if (bListen != isListening()) {
			if (bListen) {
				try {
					m_serverSocket = new ServerSocket(getPort());
					m_serverSocket.setSoTimeout(2000);
				} catch (IOException e) {
					//e.printStackTrace();
				}
			} else {
				try {
					m_serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				m_serverSocket = null;
			}
		}
	}

	private boolean isListening() {
		return m_serverSocket != null;
	}

	/**
	 *  Listen for incomming connections
	 * 
	 */
	public boolean listen() {
		Socket s = null;
		try {
			s = m_serverSocket.accept();
			setSocketOptions(s);
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {
			//e.printStackTrace();
		}
		if (s != null) {
			m_commSocket = s;
			return true;
		} else {
			return false;
		}
	}

	public InputStream createReceiveStream() throws IOException {
		return m_commSocket.getInputStream();
	}

	public OutputStream createSendStream() throws IOException {
		return m_commSocket.getOutputStream();
	}
}

