import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Extension: This is used to log each time requests are made, indicating data/time, request type, response code etc.
 */
public class LogFile {
    private Logger logger;

    /**
     * Configure the logger with handler and formatter.
     *
     * @param file_path where log message will be written to.
     * @throws IOException is thrown in case of failure of reading file.
     */
    public LogFile(String file_path) throws IOException {
        logger = Logger.getLogger("LogFile");
        FileHandler file_handler = new FileHandler(file_path);
        file_handler.setFormatter(new SimpleFormatter());
        logger.addHandler(file_handler);
    }

    /**
     * Log a information message into log file.
     *
     * @param msg information message.
     */
    public void logInfo(String msg) {
        logger.info(msg);
    }

    /**
     * Used to log each time requests are made, indicating date/time request type and client address.
     *
     * @param request_code request code from the header of a client's request.
     * @param inet_address client's ip address.
     */
    public void logRequest(String request_code, InetAddress inet_address) {
        logger.info("Receive: " + request_code + " from " + inet_address);
    }

    /**
     * Used to log each time responses to clients are made, indicating date/time response type.
     *
     * @param response_code response code from the header of the response to a client.
     * @param inet_address  client's ip address.
     */
    public void logRespond(String response_code, InetAddress inet_address) {
        logger.info("Response: " + response_code + " to " + inet_address);
    }

    /**
     * Log a warning message into log file.
     *
     * @param msg warning message.
     */
    public void logWarning(String msg) {
        logger.warning(msg);
    }

    /**
     * Log a severe-level message into log file.
     *
     * @param msg severe-level message.
     */
    public void logSevere(String msg) {
        logger.severe(msg);
    }
}
