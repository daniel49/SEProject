package mta.se.chitchat.model;

import static mta.se.chitchat.utils.Constants.AUDIO_PROPERTY;
import static mta.se.chitchat.utils.Constants.CONNECTION_PROPERTY;
import static mta.se.chitchat.utils.Constants.CONNECTION_TYPE_TCP;
import static mta.se.chitchat.utils.Constants.DIR_MIC;
import static mta.se.chitchat.utils.Constants.DIR_SPK;
import static mta.se.chitchat.utils.Constants.FORMAT_CODE_TELEPHONE;
import static mta.se.chitchat.utils.Constants.PROTOCOL_ACK;
import static mta.se.chitchat.utils.Constants.PROTOCOL_ERROR;
import static mta.se.chitchat.utils.Constants.PROTOCOL_MAGIC;
import static mta.se.chitchat.utils.Constants.PROTOCOL_VERSION;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import mta.se.chitchat.interfaces.IModelListener;
import mta.se.chitchat.interfaces.INetwork;
import mta.se.chitchat.network.TCPNetwork;
import mta.se.chitchat.security.Security;
import mta.se.chitchat.settings.AudioSettings;
import mta.se.chitchat.settings.ConnectionSettings;
import mta.se.chitchat.utils.AudioBase;
import mta.se.chitchat.utils.AudioCapture;
import mta.se.chitchat.utils.AudioPlayback;
import mta.se.chitchat.utils.AudioUtils;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The model that holds data for the MVC
 */
public class ChatModel {

	private MasterModel m_masterModel;
	private PropertyChangeSupport m_propertyChangeSupport;
	private INetwork m_network;
	private ListenThread m_listenThread;
	private DataInputStream m_receiveStream;
	private OutputStream m_sendStream;
	private AudioBase[] audio = new AudioBase[2];
	private boolean m_audioActive;
	private boolean isConnected;
	private boolean isMuted = false;
	private int listenPort;
	private List<IModelListener> mListeners;

	Security security;
	byte[] diffieHellmannSecret;
	
	public ChatModel(MasterModel master) {
		this.m_masterModel = master;
		m_propertyChangeSupport = new PropertyChangeSupport(this);
		initNetwork();
		AudioCapture ac = new AudioCapture(getConnectionSettings()
				.getFormatCode(), getAudioSettings().getSelMixer(DIR_MIC),
				getAudioSettings().getBufferSizeMillis(DIR_MIC));
		audio[DIR_MIC] = ac;
		ac.setChatModel(this);
		AudioPlayback ap = new AudioPlayback(getConnectionSettings()
				.getFormatCode(), getAudioSettings().getSelMixer(DIR_SPK),
				getAudioSettings().getBufferSizeMillis(DIR_SPK));
		audio[DIR_SPK] = ap;
		ap.setChatModel(this);
		m_audioActive = false;
		
		security = new Security();
		
	}

	/**
	 * Used for updating the view
	 * 
	 * @param listener
	 *            the data listener
	 */
	public void addModelListener(IModelListener listener) {
		if (mListeners == null) {
			mListeners = new ArrayList<IModelListener>();
		}
		mListeners.add(listener);
	}

	/**
	 * Notifies the views listeners of the changed state (value)
	 */
	private void notifyListeners() {
		if (mListeners != null && !mListeners.isEmpty()) {
			for (IModelListener listener : mListeners)
				listener.onUpdate();
		}
	}

	public boolean retIsConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
		notifyListeners();
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
		notifyListeners();
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
		this.getMasterModel().getConnectionSettings().setPort(listenPort);
		getNetwork().setListen(false);
		getNetwork().setListen(true);
	}

	private MasterModel getMasterModel() {
		return m_masterModel;
	}

	private ConnectionSettings getConnectionSettings() {
		return getMasterModel().getConnectionSettings();
	}

	private AudioSettings getAudioSettings() {
		return getMasterModel().getAudioSettings();
	}

	private void initNetwork() {
		if (getConnectionSettings().getConnectionType() == CONNECTION_TYPE_TCP) {
			m_network = new TCPNetwork(getMasterModel());
		}
	}

	private INetwork getNetwork() {
		return m_network;
	}

	public AudioBase getAudio(int d) {
		return audio[d];
	}

	/**
	 * Used for connecting to the remote host
	 * 
	 * @param strHostname
	 *            the ip of the host
	 * @param strPort
	 *            the port of the host
	 */
	public void connect(String strHostname, String strPort) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(strHostname);
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Unknown host " + strHostname);
		}
		if (addr != null) {
			getNetwork().connect(addr, strPort);
		}
		if (!getNetwork().isConnected()) {
			JOptionPane
					.showMessageDialog(null,
							new Object[] { "Could not establish connection to "
									+ strHostname }, "Error",
							JOptionPane.ERROR_MESSAGE);
		} else {
			initConnection(true);
		}
	}

	/**
	 * The disconnect operation
	 */
	public void disconnect() {
		if (retIsConnected()) {
			getNetwork().disconnect();
			notifyConnection();
		}
		setConnected(false);
		JOptionPane.showMessageDialog(null, "Call has been terminated...!");
		System.exit(1);
	}

	/**
	 * Open or terminate the listening thread
	 * 
	 * @param bListen
	 */
	public void setListen(boolean bListen) {
		if (bListen != isListening()) {
			if (bListen) {
				m_listenThread = new ListenThread(this);
				m_listenThread.start();
			} else {
				m_listenThread.setTerminate();
			}
		}
	}

	public boolean isListening() {
		return m_listenThread != null;
	}

	/**
	 * Set up after socket connection has been established. This negotiates the
	 * audio format and inits the streams.
	 * 
	 * @param bActive
	 *            true if called for the initiating (active) endpoint. false for
	 *            the accepting (passive) endpoint.
	 */
	private void initConnection(boolean bActive) {
		try {
			m_receiveStream = new DataInputStream(getNetwork()
					.createReceiveStream());
			m_sendStream = getNetwork().createSendStream();
		} catch (IOException e) {
			streamError("Problems while setting up the connection");
		}
		boolean bHandshakeSuccessful = false;
		if (bActive) {
			bHandshakeSuccessful = doHandshakeActive();
		} else {
			bHandshakeSuccessful = doHandshakePassive();
		}
		if (bHandshakeSuccessful) {
			if (isConnected()) {
				JOptionPane.showMessageDialog(null, "Connection accepted!");
				setConnected(bHandshakeSuccessful);
				initNetworkAudio();
			}
			notifyConnection();
		} else {
			JOptionPane.showMessageDialog(null,
					"Connection refused by the other host!");
			getNetwork().disconnect();
			setConnected(bHandshakeSuccessful);
			m_receiveStream = null;
			m_sendStream = null;
		}
	}

	/**
	 * Active handshakemade by the sending side
	 * 
	 * @return true if successful
	 */
	private boolean doHandshakeActive() {
		DataOutputStream dos = new DataOutputStream(getSendStream());
		try {
			dos.writeInt(PROTOCOL_MAGIC);
			dos.writeInt(PROTOCOL_VERSION);
			dos.writeInt(getConnectionSettings().getFormatCode());
		} catch (IOException e) {
			return false;
		}
		byte[] abBuffer = new byte[4];
		try {
			getReceiveStream().readFully(abBuffer);
		} catch (IOException e) {
			return false;
		}
		int w = ((abBuffer[0] & 0xFF) << 24) | ((abBuffer[1] & 0xFF) << 16)
				| ((abBuffer[2] & 0xFF) << 8) | (abBuffer[3] & 0xFF);
		if (w != PROTOCOL_ACK) {
			streamError("error on remote peer");
			return false;
		}
		
		diffieHellmannSecret = security.diffieHellmannActive(getSendStream(), getReceiveStream());
		
		return true;
	}

	/**
	 * Passive handshake made by the receiving side
	 * 
	 * @return true if successful
	 */
	private boolean doHandshakePassive() {
		boolean bSuccess = true;
		byte[] abBuffer = new byte[12];
		try {
			int nRead = getReceiveStream().read(abBuffer);
			if (nRead != 12) {
				streamError("I/O Error during handshake (passive, phase I)");
				bSuccess = false;
			}
		} catch (IOException e) {
			streamError("I/O error during handshake (passive, phase I)");
			bSuccess = false;
		}
		if (bSuccess) {
			int w = (abBuffer[0] << 24) | (abBuffer[1] << 16)
					| (abBuffer[2] << 8) | abBuffer[3];
			if (w != PROTOCOL_MAGIC) {
				streamError("wrong magic");
				bSuccess = false;
			} else {
				w = (abBuffer[4] << 24) | (abBuffer[5] << 16)
						| (abBuffer[6] << 8) | abBuffer[7];
				if (w != PROTOCOL_VERSION) {
					streamError("wrong protocol version");
					bSuccess = false;
				} else {
					w = (abBuffer[8] << 24) | (abBuffer[9] << 16)
							| (abBuffer[10] << 8) | abBuffer[11];
					if (w != FORMAT_CODE_TELEPHONE) {
						streamError("wrong format code");
						bSuccess = false;
					}
				}
			}
		}
		DataOutputStream dos = new DataOutputStream(getSendStream());
		try {
			if (bSuccess) {
				dos.writeInt(PROTOCOL_ACK);
			} else {
				dos.writeInt(PROTOCOL_ERROR);
			}
		} catch (IOException e) {
			streamError("I/O error during handshake (passive, phase II)");
			bSuccess = false;
		}
		
		diffieHellmannSecret = security.diffieHellmannPassive(getSendStream(), getReceiveStream());

		return bSuccess;
	}

	public boolean isConnected() {
		return getNetwork().isConnected();
	}

	/**
	 * Set up audio connections
	 */
	private void initNetworkAudio() {
		try {
			((AudioCapture) getAudio(DIR_MIC)).setOutputStream(getSendStream());
			((AudioPlayback) getAudio(DIR_SPK)).setAudioInputStream(AudioUtils
					.createNetAudioInputStream(getConnectionSettings()
							.getFormatCode(), getReceiveStream()));
			startAudio(DIR_MIC);
			startAudio(DIR_SPK);
			setAudioActive(true);
		} catch (Exception e) {
			streamError(e.getMessage());
		}
	}

	public void initAudioStream() {
		if (isMicrophoneTest()) {
			((AudioPlayback) getAudio(DIR_SPK))
					.setAudioInputStream(((AudioCapture) getAudio(DIR_MIC))
							.getAudioInputStream());
		}
	}

	private void closeAudio() {
		setAudioActive(false);
		closeAudio(DIR_SPK);
		closeAudio(DIR_MIC);
	}

	public boolean isMicrophoneTest() {
		return isAudioActive()
				&& (((AudioCapture) getAudio(DIR_MIC)).getOutputStream() == null);
	}

	public boolean isAudioActive() {
		return m_audioActive;
	}

	public void setAudioActive(boolean active) {
		m_audioActive = active;
		notifyAudio();
	}

	private void closeAudio(int d) {
		if (getAudio(d) != null) {
			getAudio(d).close();
		}
	}

	private void startAudio(int d) throws Exception {
		if (!isAudioActive()) {
			getAudio(d).setFormatCode(getConnectionSettings().getFormatCode());
		}
		getAudio(d).open();
		getAudio(d).start();
	}

	public void toggleTestAudio() {
		if (isConnected()) {
			return;
		}
		try {
			if (m_audioActive) {
				closeAudio(DIR_MIC);
				closeAudio(DIR_SPK);
				setAudioActive(false);
			} else {
				startAudio(DIR_MIC);
				((AudioCapture) getAudio(DIR_MIC)).setOutputStream(null);
				startAudio(DIR_SPK);
				setAudioActive(true);
				initAudioStream();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					new Object[] { "Error: ", ex.getMessage() }, "Error",
					JOptionPane.ERROR_MESSAGE);
			closeAudio(0);
			closeAudio(1);
			setAudioActive(false);
			notifyAudio();
		}
	}

	public DataInputStream getReceiveStream() {
		return m_receiveStream;
	}

	public OutputStream getSendStream() {
		return m_sendStream;
	}
	
	public byte[] getSharedSecret() {
		return diffieHellmannSecret;
		//return new byte[] {0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02,0x03,0x01,0x02};
	}

	private void streamError(String strError) {
		JOptionPane.showMessageDialog(null, new Object[] { strError,
				"Connection will be terminated" }, "Error",
				JOptionPane.ERROR_MESSAGE);
		getNetwork().disconnect();
		closeAudio();
		notifyConnection();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		m_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		m_propertyChangeSupport.removePropertyChangeListener(listener);
	}

	private void notifyConnection() {
		m_propertyChangeSupport.firePropertyChange(CONNECTION_PROPERTY,
				isConnected(), !isConnected());
	}

	private void notifyAudio() {
		m_propertyChangeSupport.firePropertyChange(AUDIO_PROPERTY,
				isAudioActive(), !isAudioActive());
	}

	/**
	 * 
	 * The thread that listens for incoming connections
	 * 
	 */
	private class ListenThread extends Thread {
		private boolean m_bTerminate;
		private ChatModel mModel;

		public ListenThread(ChatModel model) {
			mModel = model;
		}

		public void setTerminate() {
			m_bTerminate = true;
		}

		public void run() {
			getNetwork().setListen(true);
			while (!m_bTerminate) {
				if (getNetwork().listen()) {
					String strMessage = "Call received from "
							+ getNetwork().getPeer()
							+ ". Do you want to accept?";
					int nAnswer = JOptionPane.showConfirmDialog(null,
							new Object[] { strMessage }, "Confirmation",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (nAnswer == JOptionPane.YES_OPTION) {
						initConnection(false);
						setListen(false);
						mModel.setConnected(true);
					} else {
						getNetwork().disconnect();
						setConnected(false);
					}
				}
			}
			getNetwork().setListen(false);
		}
	}

}
