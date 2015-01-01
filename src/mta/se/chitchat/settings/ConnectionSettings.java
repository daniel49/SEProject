package core.settings;

import static core.utils.Constants.DEFAULT_CONNECTION_TYPE;
import static core.utils.Constants.DEFAULT_FORMAT_CODE;
import static core.utils.Constants.DEFAULT_PORT;
import core.model.MasterModel;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> Connection Settings
 */
public class ConnectionSettings {
	private int m_nPort;
	private int m_nConnectionType;
	private int m_nFormatCode;

	public ConnectionSettings(MasterModel masterModel) {
		setPort(DEFAULT_PORT);
		setFormatCode(DEFAULT_FORMAT_CODE);
		setConnectionType(DEFAULT_CONNECTION_TYPE);
	}

	public void setPort(int nPort) {
		m_nPort = nPort;
	}

	public int getPort() {
		return m_nPort;
	}

	public void setConnectionType(int nConnectionType) {
		m_nConnectionType = nConnectionType;
	}

	public int getConnectionType() {
		return m_nConnectionType;
	}

	public void setFormatCode(int nFormatCode) {
		m_nFormatCode = nFormatCode;
	}

	public int getFormatCode() {
		return m_nFormatCode;
	}
}

