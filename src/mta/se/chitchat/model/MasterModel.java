package mta.se.chitchat.model;

import mta.se.chitchat.model.ChatModel;
import mta.se.chitchat.settings.AudioSettings;
import mta.se.chitchat.settings.ConnectionSettings;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut
 * </p> Software Engineering Project
 * </p> Holds pointers to all other model classes.
 */
public class MasterModel
{
    private ChatModel m_chatModel;
    private ConnectionSettings m_connectionSettings;
    private AudioSettings m_audioSettings;

    public ChatModel configureMasterModel()
	{
	    m_connectionSettings = new ConnectionSettings(this);
	    m_audioSettings = new AudioSettings(this);
	    m_chatModel = new ChatModel(this);
	    m_chatModel.setListen(true);
	    return m_chatModel;
	}

    public ChatModel getChatModel()
	{
	    return m_chatModel;
	}

    public ConnectionSettings getConnectionSettings()
	{
	    return m_connectionSettings;
	}

    public AudioSettings getAudioSettings()
	{
	    return m_audioSettings;
	}
}


