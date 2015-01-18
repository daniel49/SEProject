package mta.se.chitchat;

import javax.swing.JOptionPane;

import mta.se.chitchat.controller.ChatController;
import mta.se.chitchat.model.ChatModel;
import mta.se.chitchat.model.MasterModel;
import mta.se.chitchat.view.ChatView;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The main entry of the program
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			// Instantiate the MVC elements
			MasterModel master = new MasterModel();
			ChatModel model = master.configureMasterModel();
			// ChatModel model = new ChatModel(master);
			ChatView view = null;
			try {
				view = new ChatView();
				view.frmChitChat.setVisible(true);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			ChatController controller = new ChatController();

			// Attach the view to the model
			model.addModelListener(view);

			// Tell the view about the model and the controller
			view.addModel(model);
			view.addController(controller);

			// Tell the controller about the model and the view
			controller.addModel(model);
			controller.addView(view);

		} catch (Throwable t) {
			System.err.println("Exception occurred in main():");
			t.printStackTrace();
			System.exit(1);
		}
	}

}
