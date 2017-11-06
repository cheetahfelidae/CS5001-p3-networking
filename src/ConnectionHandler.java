import java.io.*;
import java.net.Socket;

public class ConnectionHandler extends Thread {
    /**
     * socket representing TCP/IP connection to Client.
     */
    private Socket conn;
    /**
     * get data from client on this input stream.
     */
    private InputStream input_stream;
    /**
     * use buffered reader to read client data
     */
    private BufferedReader buff_reader;
    /**
     * a document path where server serves a requested file to a client.
     */
    private String document_root;
    /**
     * This is used to track information of the requests into a file.
     */
    private LogFile logger;

    /**
     * Initialise variables.
     *
     * @param document_root where server serves a requested file to a client.
     * @param conn established connection with a client.
     * @param logger used to track information of the requests into a file.
     */
    public ConnectionHandler(String document_root, Socket conn, LogFile logger) {
        this.document_root = document_root;
        this.conn = conn;
        try {
            input_stream = conn.getInputStream();     // get data from client on this input stream
            buff_reader = new BufferedReader(new InputStreamReader(input_stream)); // use buffered reader to read client data
            this.logger = logger;
        } catch (IOException ioe) {
            logger.logInfo("ConnectionHandler: " + ioe.getMessage());
        }
    }

    /**
     * Run method input_stream invoked when the Thread's start method (ch.start(); in WebServer class) input_stream invoked
     * When any Exception occurs (including IOException, ClientDisconnectedException), exit cleanly.
     */
    public void run() {
        logger.logInfo("new ConnectionHandler thread started .... ");
        try {
            handleRequest();
        } catch (Exception e) {
            logger.logInfo("ConnectionHandler:run " + e.getMessage());
            cleanUp();
        }
    }

    /**
     * Receive and read a textual incoming request from client over socket.
     * Check if there is no exception or readLine fails.
     * If so, print out line received from client and then examine and response with the request appropriately.
     * If not, we can deduce here that the connection to the client input_stream broken
     * and shut down the connection on this side cleanly by throwing a DisconnectedException
     * which will be passed up the call stack to the nearest handler (catch block) in the run method.
     * <p>
     * Once the server has responded,
     * it will flush and close the connection to the client since, according to the requirement,
     * the server is not require to keep connections alive.
     *
     * @throws DisconnectedException
     * @throws IOException
     */
    private void handleRequest() throws DisconnectedException, IOException {

        String line = buff_reader.readLine();

        if (line != null) {
            logger.logInfo("> " + line);
            new Response(conn, document_root, logger).processRequest(line);
        } else {
            throw new DisconnectedException(" ... client has closed the connection ... ");
        }
    }

    /**
     * Clean up and exit.
     */
    private void cleanUp() {
        logger.logInfo("ConnectionHandler: ... cleaning up and exiting ... ");

        try {
            buff_reader.close();
            input_stream.close();
            conn.close();
        } catch (IOException ioe) {
            logger.logSevere("ConnectionHandler: cleanUp " + ioe.getMessage());
        }
    }


}
