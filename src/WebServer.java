import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * As the name suggests, this is the server class whose responsibility is to serve requests from clients.
 * Extension: the server is able to support multiple concurrent client connection requests up to a specified limit.
 * <p>
 * original source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/Server.java.
 */
public class WebServer {
    private static int num_cur_clients = 0;

    /**
     * Update the number of current client connection requests which the server can serve.
     *
     * @param num the number of the current client connection requests.
     */
    public static void setNumCurClients(int num) {
        num_cur_clients = num;
    }

    /**
     * Get the number of current client connection requests which the server can serve.
     *
     * @return the number of the current client connection requests.
     */
    public static int getNumCurClients() {
        return num_cur_clients;
    }

    /**
     * 1. The server listen for client connection requests on on a specified port and wait until client requests a connection, then returns connection (socket).
     * 2. Create new handler for this connection (as a new thread to support multiple concurrent client connection request).
     * 3. Start handler thread.
     * <p>
     * Once the server has responded,
     * it will flush and close the connection to the client and listen for further requests since, according to the requirement,
     * the server is not require to keep connections alive.
     *
     * @param document_root   path where the server serves a requested file to a client.
     * @param port            socket port which the server will be listening to.
     * @param log_path        the path which log uses to track information of the requests into a file.
     * @param max_clients the maximum number of client connection requests which server can serve at the time.
     */
    public WebServer(String document_root, int port, String log_path, int max_clients) {
        ServerSocket sever_socket;
        LogFile logFile;

        try {
            logFile = new LogFile(log_path);

            sever_socket = new ServerSocket(port);
            logFile.logInfo("WebServer started ... listening on port " + port + " ...");
            while (true) {
                Socket conn = sever_socket.accept();
                logFile.logInfo("WebServer got new connection request from " + conn.getInetAddress());

                if (num_cur_clients < max_clients) {
                    setNumCurClients(getNumCurClients() + 1);
                    new ConnectionHandler(conn, document_root, logFile).start();
                } else {
                    logFile.logWarning("The number of the client connection requests is exceeding now!!");
                }
            }
        } catch (IOException ioe) {
            Logger.getLogger(WebServer.class.getName()).severe("Ooops " + ioe.getMessage());
        }
    }
}
