import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * As the name suggests, this is the server class whose responsibility is to serve requests from clients (simultaneously).
 *
 * original source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/Server.java.
 */
public class WebServer {
    private static final String LOG_FILE = System.getProperty("user.dir") + "/log.txt";

    /**
     * 1. The server listen for client connection requests on on a specified port and wait until client requests a connection, then returns connection (socket).
     * 2. Create new handler for this connection (as a new thread to support multiple concurrent client connection request).
     * 3. Start handler thread.
     * <p>
     * Once the server has responded,
     * it will flush and close the connection to the client and listen for further requests since, according to the requirement,
     * the server is not require to keep connections alive.
     *
     * @param document_root where the server serves a requested file to a client.
     * @param port which the server will be listening to.
     */
    public WebServer(String document_root, int port) {
        ServerSocket sever_socket;
        LogFile logFile;

        try {
            logFile = new LogFile(LOG_FILE);

            sever_socket = new ServerSocket(port);
            logFile.logInfo("WebServer started ... listening on port " + port + " ...");
            while (true) {
                Socket conn = sever_socket.accept();
                logFile.logInfo("WebServer got new connection request from " + conn.getInetAddress());

                ConnectionHandler ch = new ConnectionHandler(document_root, conn, logFile);
                ch.start();
            }
        } catch (IOException ioe) {
            Logger.getLogger(WebServer.class.getName()).severe("Ooops " + ioe.getMessage());
        }
    }
}
