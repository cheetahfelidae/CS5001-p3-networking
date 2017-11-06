/**
 * This exception is a case of the server fail to read a textual incoming request from client over socket.
 */
public class DisconnectedException extends Exception {

	public DisconnectedException(String message) {
		super(message);
	}
}
