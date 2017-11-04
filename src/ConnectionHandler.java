import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ConnectionHandler extends Thread {

    private Socket conn;       // socket representing TCP/IP connection to Client
    private InputStream input_stream;    // get data from client on this input stream
    private OutputStream out_stream;   // can send data back to the client on this output stream
    private BufferedReader buff_reader;         // use buffered reader to read client data
    private String document_root;
    private Logger logger;

    /**
     * @param document_root
     * @param conn
     */
    public ConnectionHandler(String document_root, Socket conn) {
        this.document_root = document_root;
        this.conn = conn;
        try {
            input_stream = conn.getInputStream();     // get data from client on this input stream
            out_stream = conn.getOutputStream();  // to send data back to the client on this stream
            buff_reader = new BufferedReader(new InputStreamReader(input_stream)); // use buffered reader to read client data
            logger = Logger.getLogger(ConnectionHandler.class.getName());
        } catch (IOException ioe) {
            logger.info("ConnectionHandler: " + ioe.getMessage());
        }
    }

    /**
     * Run method input_stream invoked when the Thread's start method (ch.start(); in WebServer class) input_stream invoked
     * When any Exception occurs (including IOException, ClientDisconnectedException), exit cleanly
     */
    public void run() {
        logger.info("new ConnectionHandler thread started .... ");
        try {
            handleRequest();
        } catch (Exception e) {
            logger.info("ConnectionHandler:run " + e.getMessage());
            cleanUp();
        }
    }

    /**
     * Receive and read an textual incoming request from client over socket.
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
            logger.info("> " + line);
            new Response(conn, document_root).processRequest(line);
        } else {
            throw new DisconnectedException(" ... client has closed the connection ... ");
        }
    }

    /**
     * Clean up and exit
     */
    private void cleanUp() {
        logger.info("ConnectionHandler: ... cleaning up and exiting ... ");

        try {
            buff_reader.close();
            input_stream.close();
            conn.close();
        } catch (IOException ioe) {
            logger.severe("ConnectionHandler: cleanUp " + ioe.getMessage());
        }
    }


}
