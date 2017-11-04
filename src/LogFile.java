import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This is used to log each time requests are made, indicating data/time, request type, response code etc.
 */
public class LogFile {
    private Logger logger;
    private SimpleFormatter formatter;
    private FileHandler file_handler;

    /**
     * Configure the logger with handler and formatter
     *
     * @param file_path
     * @throws IOException
     */
    public LogFile(String file_path) throws IOException {
        logger = Logger.getLogger("LogFile");
        formatter = new SimpleFormatter();
        file_handler = new FileHandler(file_path);
        file_handler.setFormatter(formatter);
        logger.addHandler(file_handler);
    }

    public void logInfo(String msg) {
        logger.info(msg);
    }

    public void logRequest(String request_code, InetAddress inet_address) {
        logger.info("Request Code: " + request_code + " from " + inet_address);
    }

    public void logRespond(String response_code, InetAddress inet_address) {
        logger.info("Response Code: " + response_code + " from " + inet_address);
    }

    public void logWarning(String msg) {
        logger.warning(msg);
    }

    public void logSevere(String msg) {
        logger.severe(msg);
    }
}
