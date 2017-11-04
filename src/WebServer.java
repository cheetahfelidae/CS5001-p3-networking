import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class WebServer {

    /**
     * 1. The server listen for client connection requests on on a specified port and wait until client requests a connection, then returns connection (socket).
     * 2. Create new handler for this connection.
     * 3. Start handler thread
     *
     * @param document_root
     * @param port
     */
    public WebServer(String document_root, int port) {
        ServerSocket sever_socket;
        Logger logger = Logger.getLogger(WebServer.class.getName());
        try {
            sever_socket = new ServerSocket(port);
            logger.info("WebServer started ... listening on port " + port + " ...");
            while (true) {
                Socket conn = sever_socket.accept();
                logger.info("WebServer got new connection request from " + conn.getInetAddress());
                ConnectionHandler ch = new ConnectionHandler(document_root, conn);
                ch.start();
            }
        } catch (IOException ioe) {
            logger.severe("Ooops " + ioe.getMessage());
        }
    }
}
