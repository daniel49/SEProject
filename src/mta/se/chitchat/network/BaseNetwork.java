package mta.se.chitchat.network;

import mta.se.chitchat.interfaces.INetwork;
import mta.se.chitchat.model.MasterModel;
import mta.se.chitchat.settings.ConnectionSettings;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The abstract class representing the basic
 *         functionalities
 */
public abstract class BaseNetwork implements INetwork {
	private MasterModel m_masterModel;

	protected BaseNetwork(MasterModel masterModel) {
		m_masterModel = masterModel;
	}

	private MasterModel getMasterModel() {
		return m_masterModel;
	}

	private ConnectionSettings getConnectionSettings() {
		return getMasterModel().getConnectionSettings();
	}

	protected int getPort() {
		return getConnectionSettings().getPort();
	}
}
