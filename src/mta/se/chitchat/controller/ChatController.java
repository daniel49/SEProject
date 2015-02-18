package mta.se.chitchat.controller;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import mta.se.chitchat.interfaces.IController;
import mta.se.chitchat.interfaces.IView;
import mta.se.chitchat.model.ChatModel;
import mta.se.chitchat.view.ChatView;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The controller of the application
 */
public class ChatController implements IController {
	private List<IView> m_Views;
	private ChatModel m_Model;

	public ChatController() {

	}

	/**
	 * The event function that is triggered on a button press -
	 * connect,disconnect,mute,unmute,set
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(ACTION_CONNECT)) {
			JButton source = (JButton) event.getSource();
			if (source != null) {
				connectOperation();
			} else {
				notifyViews(true, "Invalid operation data");
			}
		} else if (event.getActionCommand().equals(ACTION_DISCONNECT)) {
			JButton source = (JButton) event.getSource();
			if (source != null) {
				disconnectOperation();
			} else {
				notifyViews(true, "Invalid operation data");
			}
		} else if (event.getActionCommand().equals(ACTION_MUTE)) {
			JCheckBox source = (JCheckBox) event.getSource();
			if (source != null) {
				muteOperation();
			} else {
				notifyViews(true, "Invalid operation data");
			}
		} else if (event.getActionCommand().equals(ACTION_UNMUTE)) {
			JCheckBox source = (JCheckBox) event.getSource();
			if (source != null) {
				unmuteOperation();
			} else {
				notifyViews(true, "Invalid operation data");
			}
		} else if (event.getActionCommand().equals(ACTION_SET)) {
			JButton source = (JButton) event.getSource();
			if (source != null) {
				setOperation();
			} else {
				notifyViews(true, "Invalid operation data");
			}
		}

	}

	/**
	 * Add the reference to the view
	 * 
	 * @param view
	 *            the view to be transmited to the controller
	 */
	public void addView(IView view) {
		if (m_Views == null) {
			m_Views = new ArrayList<IView>();
		}

		m_Views.add(view);
	}

	/**
	 * Adds a reference to the model, so it can update it
	 * 
	 * @param model
	 *            The data model reference
	 */
	public void addModel(ChatModel model) {
		m_Model = model;
	}

	/**
	 * Notifies the views when a message must be displayed
	 * 
	 * @param isError
	 *            {@code true} if the message is an error, {@code false}
	 *            otherwise
	 * @param message
	 *            The string to be displayed
	 */
	private void notifyViews(boolean isError, String message) {
		if (m_Views != null && !m_Views.isEmpty()) {
			for (IView view : m_Views) {
				view.onMessage(isError, message);
			}
		}
	}

	/**
	 * The connect operation
	 */
	private void connectOperation() {
		if (m_Model != null) {
			String strIp = ((ChatView) m_Views.get(0)).getIpTextFieldText();
			String strPort = ((ChatView) m_Views.get(0))
					.getConnectionPortTextFieldText();
			if (strIp.isEmpty() || strPort.isEmpty()) {
				notifyViews(true, "IP and PORT fields must be set");
				return;
			}
			m_Model.connect(strIp, strPort);

		}
	}

	/**
	 * The disconnect operation
	 */
	private void disconnectOperation() {
		if (m_Model != null) {
			m_Model.disconnect();
			m_Model.setConnected(false);
		}
	}

	/**
	 * The mute operation
	 */
	private void muteOperation() {
		if (m_Model != null) {
			m_Model.setMuted(true);
			m_Model.getAudio(0).setMuted(true);
		}
	}

	/**
	 * The unmute operation
	 */
	private void unmuteOperation() {
		if (m_Model != null) {
			m_Model.setMuted(false);
			m_Model.getAudio(0).setMuted(false);
		}
	}

	/**
	 * The set operation
	 */
	private void setOperation() {
		if (m_Model != null) {
			String strPort = ((ChatView) m_Views.get(0))
					.getListeningPortTextFieldText();
			try {
				int port = Integer.parseInt(strPort);
				if (port < 0 || port > 65535) {
					JOptionPane.showMessageDialog(null, "Invalid port "
							+ strPort
							+ ". Port number must be between 0 and 65535.");
					return;
				}
				m_Model.setListenPort(Integer.parseInt(strPort));
				JOptionPane.showMessageDialog(null,
						"Listening port has been set to \"" + strPort + "\"");
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid port \"" + strPort
						+ "\"");
				return;
			}

		}
	}
}
