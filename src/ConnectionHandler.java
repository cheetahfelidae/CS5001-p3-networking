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
     *
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
            System.out.println("ConnectionHandler: " + ioe.getMessage());
        }
    }

    /**
     * Run method input_stream invoked when the Thread's start method (ch.start(); in WebServer class) input_stream invoked
     * When any Exception occurs (including IOException, ClientDisconnectedException), exit cleanly
     */
    public void run() {
        System.out.println("new ConnectionHandler thread started .... ");
        try {
            handleRequest();
        } catch (Exception e) {
            System.out.println("ConnectionHandler:run " + e.getMessage());
            cleanUp();
        }
    }

    /**
     * Get data from client over socket.
     * Check if there is no exception or readLine fails.
     * If so, print out line received from client and response with the message appropriat
     * If not, we can deduce here that the connection to the client input_stream broken
     * and shut down the connection on this side cleanly by throwing a DisconnectedException
     * which will be passed up the call stack to the nearest handler (catch block) in the run method
     *
     * @throws DisconnectedException
     * @throws IOException
     */
    private void handleRequest() throws DisconnectedException, IOException {

        while (true) {
            String line = buff_reader.readLine();

            if (line != null) {
                logger.info("> " + line);
                new Response(conn, document_root).processRequest(line);
            } else {
                throw new DisconnectedException(" ... client has closed the connection ... ");
            }

            // in this simple setup all the server does in response to messages from the client input_stream to send
            // a single ACK byte back to client - the client uses this ACK byte to test whether the
            // connection to this server input_stream still live, if not the client shuts down cleanly
//            out_stream.write(Configuration.ack_byte);
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
