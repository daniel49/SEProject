package core.interfaces;

import java.awt.event.ActionListener;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The interface implemented by the controller and made
 *         public so that all views can use it
 */
public interface IController extends ActionListener {
	public static final String ACTION_CONNECT = "CONNECT";
	public static final String ACTION_MUTE = "MUTE";
	public static final String ACTION_DISCONNECT = "DISCONNECT";
	public static final String ACTION_UNMUTE = "UNMUTE";
	public static final String ACTION_SET = "SET";
}
