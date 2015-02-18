package mta.se.chitchat.utils;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> Used for holding different constant values
 */
public class Constants {
	public static final int DIR_MIC = 0;
	public static final int DIR_SPK = 1;

	public static final int FORMAT_CODE_TELEPHONE = 1;

	public static final int[] BUFFER_SIZE_MILLIS = { 30, 40, 50, 70, 85, 100,
			130, 150, 180, 220, 400 };
	public static final String[] BUFFER_SIZE_MILLIS_STR = { "30", "40", "50",
			"70", "85", "100", "130", "150", "180", "220", "400" };
	public static final int BUFFER_SIZE_INDEX_DEFAULT = 2;

	public static final String CONNECTION_PROPERTY = "CONNECTION";
	public static final String AUDIO_PROPERTY = "AUDIO";

	public static final int PROTOCOL_MAGIC = 0x43484154;
	public static final int PROTOCOL_VERSION = 1;
	public static final int PROTOCOL_ACK = 1001;
	public static final int PROTOCOL_ERROR = 1002;

	// Socket options
	public static final boolean TCP_NODELAY = false;
	// -1 means do not set the value
	public static final int TCP_RECEIVE_BUFFER_SIZE = 1024;
	public static final int TCP_SEND_BUFFER_SIZE = 1024;

	public static final int CONNECTION_TYPE_TCP = 1;

	public static final int DEFAULT_PORT = 8765;
	public static final int DEFAULT_CONNECTION_TYPE = CONNECTION_TYPE_TCP;
	public static final int DEFAULT_FORMAT_CODE = FORMAT_CODE_TELEPHONE;
}
