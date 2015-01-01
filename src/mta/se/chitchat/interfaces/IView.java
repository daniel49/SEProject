package core.interfaces;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The network interface
 */
public interface IView {

	/**
	 * On message received from controller
	 * 
	 * @param isError
	 *            {@code true} if the message is an error, {@code false}
	 *            otherwise
	 * @param message
	 *            The string to be displayed
	 */
	public void onMessage(boolean isError, String message);
}
