/**
 * This exception is a case of the server fail to read a textual incoming request from client over socket.
 * <p>
 * original source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/DisconnectedException.java.
 */
public class DisconnectedException extends Exception {

    /**
     * Forward the message value to Exception class.
     *
     * @param message error or warning message.
     */
    public DisconnectedException(String message) {
        super(message);
    }
}
