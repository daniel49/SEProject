package mta.se.chitchat.view;

import mta.se.chitchat.interfaces.IController;
import mta.se.chitchat.interfaces.IModelListener;
import mta.se.chitchat.interfaces.IView;
import mta.se.chitchat.model.ChatModel;
import mta.se.chitchat.utils.MotionPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p>
 * 
 *         The view of the MVC that creates the window
 */
public class ChatView extends JFrame implements IModelListener, IView {

	private static final long serialVersionUID = 1L;
	private ChatModel m_chatModel;
	public JFrame frmChitChat;
	private JTextField m_ipTextField;
	private JTextField m_connectPortTextField;
	private JTextField m_listenPortTextField;
	private JButton m_connectButton;
	private JCheckBox m_muteButton;
	private JButton m_setButton;

	public ChatView() {
		super();
		initialize();
	}

	private void initialize() {
		try {
			InputStream is = Font.class
					.getResourceAsStream("/resources/fonts/HelveticaNeueLTStd-Lt.otf");
			Font fH = Font.createFont(Font.PLAIN, is);

			InputStream is1 = Font.class
					.getResourceAsStream("/resources/fonts/RobotoLight.ttf");
			Font fR = Font.createFont(Font.PLAIN, is1);

			frmChitChat = new JFrame();
			frmChitChat.setSize(new Dimension(590, 370));
			frmChitChat.setUndecorated(true);
			frmChitChat.setLocationRelativeTo(null);
			frmChitChat.setIconImage(Toolkit.getDefaultToolkit().getImage(
					ChatView.class.getResource("/resources/img/chat.png")));
			frmChitChat.getContentPane().setFont(fH.deriveFont(11f));
			frmChitChat.setResizable(false);
			frmChitChat.setTitle("ChitChat");

			frmChitChat.getContentPane().setBackground(Color.BLACK);
			frmChitChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frmChitChat.getContentPane().setLayout(null);

			JLayeredPane layeredPane = new JLayeredPane();
			layeredPane.setBounds(0, 0, 590, 370);
			frmChitChat.getContentPane().add(layeredPane);
			layeredPane.setLayout(null);

			JPanel panel_1 = new JPanel();
			panel_1.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
				}
			});
			panel_1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			panel_1.setOpaque(false);
			panel_1.setBounds(18, 100, 554, 100);
			layeredPane.add(panel_1);
			panel_1.setLayout(null);

			JLabel lblRemoteConnection = new JLabel("Remote connection");
			lblRemoteConnection.setBounds(10, 10, 129, 21);
			panel_1.add(lblRemoteConnection);
			lblRemoteConnection.setForeground(new Color(255, 255, 255, 200));
			lblRemoteConnection.setFont(fR.deriveFont(15f));

			m_connectPortTextField = new JFormattedTextField();
			m_connectPortTextField.setSelectedTextColor(new Color(255, 255,
					255, 200));
			m_connectPortTextField.setSelectionColor(new Color(30, 144, 255,
					200));
			m_connectPortTextField.setCaretColor(new Color(30, 144, 255, 200));
			m_connectPortTextField.setBorder(new CompoundBorder(new LineBorder(
					new Color(30, 144, 255)), new EmptyBorder(0, 6, 0, 6)));
			m_connectPortTextField.setOpaque(false);
			m_connectPortTextField.setBounds(242, 50, 136, 27);
			panel_1.add(m_connectPortTextField);
			m_connectPortTextField.setForeground(new Color(255, 255, 255, 200));
			m_connectPortTextField.setFont(fR.deriveFont(15f));

			m_connectPortTextField.setColumns(10);

			m_connectButton = new JButton("Connect");
			m_connectButton.setOpaque(true);
			m_connectButton.setRequestFocusEnabled(false);
			m_connectButton.setBorder(new LineBorder(new Color(30, 144, 255)));
			m_connectButton.setBounds(388, 50, 129, 29);
			panel_1.add(m_connectButton);
			m_connectButton.setBackground(new Color(230, 230, 250));
			m_connectButton.setForeground(new Color(30, 144, 255, 200));
			m_connectButton.setFont(fR.deriveFont(15f));

			JLabel lblIp = new JLabel("IP");
			lblIp.setBounds(35, 52, 13, 21);
			panel_1.add(lblIp);
			lblIp.setForeground(new Color(255, 255, 255, 200));
			lblIp.setFont(fR.deriveFont(15f));

			m_ipTextField = new JTextField();
			m_ipTextField.setSelectedTextColor(new Color(255, 255, 255, 200));
			m_ipTextField.setSelectionColor(new Color(30, 144, 255, 200));
			m_ipTextField.setCaretColor(new Color(30, 144, 255, 200));
			m_ipTextField.setBorder(new CompoundBorder(new LineBorder(
					new Color(30, 144, 255)), new EmptyBorder(0, 6, 0, 6)));
			m_ipTextField.setOpaque(false);
			m_ipTextField.setBounds(59, 50, 136, 27);
			panel_1.add(m_ipTextField);
			m_ipTextField.setForeground(new Color(255, 255, 255, 200));
			m_ipTextField.setFont(fR.deriveFont(15f));
			m_ipTextField.setColumns(10);

			JLabel label = new JLabel("Port");
			label.setBounds(205, 52, 27, 21);
			panel_1.add(label);
			label.setForeground(new Color(255, 255, 255, 200));
			label.setFont(fR.deriveFont(15f));

			JLabel transparency_panel1 = new JLabel("");
			transparency_panel1.setBounds(0, 0, 554, 100);
			panel_1.add(transparency_panel1);
			transparency_panel1.setBackground(new Color(0, 0, 0, 60));
			transparency_panel1.setOpaque(true);

			JPanel panel_2 = new JPanel();
			panel_2.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
				}
			});
			panel_2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			panel_2.setOpaque(false);
			panel_2.setBorder(null);
			panel_2.setBounds(18, 212, 554, 100);
			layeredPane.add(panel_2);
			panel_2.setBackground(new Color(0, 0, 0, 60));
			panel_2.setLayout(null);

			JLabel lblConfigureListeningPort = new JLabel(
					"Configure listening port");
			lblConfigureListeningPort.setBounds(10, 10, 151, 21);
			panel_2.add(lblConfigureListeningPort);
			lblConfigureListeningPort.setForeground(new Color(255, 255, 255,
					200));
			lblConfigureListeningPort.setFont(fR.deriveFont(15f));

			m_setButton = new JButton("Set");
			m_setButton.setRequestFocusEnabled(false);
			m_setButton.setBorder(new LineBorder(new Color(30, 144, 255)));
			m_setButton.setBounds(388, 50, 129, 29);
			panel_2.add(m_setButton);
			m_setButton.setBackground(new Color(230, 230, 250));
			m_setButton.setForeground(new Color(30, 144, 255));
			m_setButton.setFont(fR.deriveFont(15f));

			m_listenPortTextField = new JFormattedTextField();
			m_listenPortTextField.setSelectedTextColor(new Color(255, 255, 255,
					200));
			m_listenPortTextField
					.setSelectionColor(new Color(30, 144, 255, 200));
			m_listenPortTextField.setCaretColor(new Color(30, 144, 255, 200));
			m_listenPortTextField.setBorder(new CompoundBorder(new LineBorder(
					new Color(30, 144, 255)), new EmptyBorder(0, 6, 0, 6)));
			m_listenPortTextField.setOpaque(false);
			m_listenPortTextField.setBounds(242, 50, 136, 27);
			panel_2.add(m_listenPortTextField);
			m_listenPortTextField.setForeground(new Color(255, 255, 255, 200));
			m_listenPortTextField.setFont(fR.deriveFont(15f));
			m_listenPortTextField.setColumns(10);

			JLabel lblPort = new JLabel("Port");
			lblPort.setBounds(205, 52, 27, 21);
			panel_2.add(lblPort);
			lblPort.setForeground(new Color(255, 255, 255, 200));
			lblPort.setFont(fR.deriveFont(15f));

			JLabel transparency_panel2 = new JLabel("");
			transparency_panel2.setOpaque(true);
			transparency_panel2.setBackground(new Color(0, 0, 0, 60));
			transparency_panel2.setBounds(0, 0, 554, 100);
			panel_2.add(transparency_panel2);
			m_setButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});

			JPanel panel_3 = new JPanel();
			panel_3.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
				}
			});
			panel_3.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			panel_3.setOpaque(false);
			panel_3.setBounds(18, 319, 554, 41);
			layeredPane.add(panel_3);
			panel_3.setLayout(null);

			m_muteButton = new JCheckBox("Mute conversation");
			m_muteButton.setOpaque(false);
			m_muteButton.setRequestFocusEnabled(false);
			m_muteButton.setRolloverSelectedIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/6.png")));
			m_muteButton.setSelectedIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/4.png")));
			m_muteButton.setIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/5.png")));
			m_muteButton.setRolloverIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/6.png")));
			m_muteButton.setPressedIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/4.png")));
			m_muteButton.setBounds(6, 6, 163, 29);
			panel_3.add(m_muteButton);
			m_muteButton.setForeground(new Color(30, 144, 255, 200));
			m_muteButton.setBackground(new Color(0, 0, 205, 0));
			m_muteButton.setFont(fR.deriveFont(15f));

			JLabel transparency_panel3 = new JLabel("");
			transparency_panel3.setBounds(0, 0, 554, 41);
			panel_3.add(transparency_panel3);
			transparency_panel3.setOpaque(true);
			transparency_panel3.setBackground(new Color(0, 0, 0, 60));

			JButton btnNewButton = new JButton("");
			btnNewButton.setRolloverIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/button_delete_violet.png")));
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			btnNewButton.setFocusPainted(false);
			btnNewButton.setBorderPainted(false);
			btnNewButton.setContentAreaFilled(false);
			btnNewButton.setIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/button_delete_blue.png")));
			btnNewButton.setBounds(554, 18, 18, 18);
			layeredPane.add(btnNewButton);

			JButton button = new JButton("");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frmChitChat.setState(Frame.ICONIFIED);
				}
			});
			button.setRolloverIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/button_minus_violet.png")));
			button.setIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/button_minus_blue.png")));
			button.setFocusPainted(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setBounds(531, 18, 18, 18);
			layeredPane.add(button);

			MotionPanel dragPanel = new MotionPanel(frmChitChat);
			dragPanel.setOpaque(false);
			dragPanel.setBounds(0, 0, 590, 370);
			layeredPane.add(dragPanel);

			JLabel lblChitChat = new JLabel("Welcome to ChitChat...");
			lblChitChat.setBounds(18, 24, 442, 65);
			layeredPane.add(lblChitChat);
			lblChitChat.setForeground(new Color(255, 255, 255, 200));
			lblChitChat.setFont(fH.deriveFont(35f));

			JLabel background = new JLabel("");
			background.setOpaque(true);
			background.setIcon(new ImageIcon(ChatView.class
					.getResource("/resources/img/bckg_purple_ios.jpg")));
			background.setBounds(0, 0, 590, 371);
			frmChitChat.getContentPane().add(background);
//			
//			JOptionPane.showMessageDialog(null, "Please wait a few seconds for the application to finish initialization...");
//			
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public String getConnectButtonText() {
		return m_connectButton.getText();
	}

	public void setConnectButtonText(String text) {
		this.m_connectButton.setText(text);
	}

	public String getIpTextFieldText() {
		return m_ipTextField.getText();
	}

	public void setIpTextFieldText(String t) {
		this.m_ipTextField.setText(t);
	}

	public String getConnectionPortTextFieldText() {
		return m_connectPortTextField.getText();
	}

	public void setConnectionPortTextFieldText(String t) {
		this.m_connectPortTextField.setText(t);
	}

	public String getListeningPortTextFieldText() {
		return m_listenPortTextField.getText();
	}

	public void setListeningPortTextFieldText(String t) {
		this.m_listenPortTextField.setText(t);
	}

	public void addModel(ChatModel model) {
		m_chatModel = model;
	}

	public void addController(IController controller) {
		m_connectButton.setActionCommand(IController.ACTION_CONNECT);
		m_connectButton.addActionListener(controller);

		m_muteButton.setActionCommand(IController.ACTION_MUTE);
		m_muteButton.addActionListener(controller);

		m_setButton.setActionCommand(IController.ACTION_SET);
		m_setButton.addActionListener(controller);
	}

	@Override
	public void onMessage(boolean isError, String message) {
		if (isError) {
			JOptionPane.showMessageDialog(this, message, "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, message, "ChatReport MVC",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Update the view based on the data in the model
	 */
	@Override
	public void onUpdate() {
		if (!(m_chatModel.retIsConnected())) {
			setConnectButtonText("Connect");
			m_connectButton.setActionCommand(IController.ACTION_CONNECT);
		} else {
			setConnectButtonText("Disconnect");
			m_connectButton.setActionCommand(IController.ACTION_DISCONNECT);
		}

		if (m_chatModel.isMuted()) {
			m_muteButton.setActionCommand(IController.ACTION_UNMUTE);
		} else {
			m_muteButton.setActionCommand(IController.ACTION_MUTE);
		}

	}
}
