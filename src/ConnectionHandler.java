

import java.io.*;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private Socket conn;       // socket representing TCP/IP connection to Client
    private InputStream is;    // get data from client on this input stream
    private OutputStream os;   // can send data back to the client on this output stream
    private BufferedReader br;         // use buffered reader to read client data
    private String document_root;

    public ConnectionHandler(String document_root, Socket conn) {
        this.document_root = document_root;
        this.conn = conn;
        try {
            is = conn.getInputStream();     // get data from client on this input stream
            os = conn.getOutputStream();  // to send data back to the client on this stream
            br = new BufferedReader(new InputStreamReader(is)); // use buffered reader to read client data
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler: " + ioe.getMessage());
        }
    }

    public void run() { // run method is invoked when the Thread's start method (ch.start(); in WebServer class) is invoked
        System.out.println("new ConnectionHandler thread started .... ");
        try {
            handle_request();
        } catch (Exception e) { // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
            System.out.println("ConnectionHandler:run " + e.getMessage());
            cleanup();     // cleanup and exit
        }
    }

    private void handle_request() throws DisconnectedException, IOException {
        while (true) {
            String line = br.readLine(); // get data from client over socket

            if (line != null) {
                System.out.println("> " + line); // assuming no exception, print out line received from client
                Response.processRequest(conn, line, document_root);
            } else {
                // if readLine fails we can deduce here that the connection to the client is broken
                // and shut down the connection on this side cleanly by throwing a DisconnectedException
                // which will be passed up the call stack to the nearest handler (catch block)
                // in the run method
                throw new DisconnectedException(" ... client has closed the connection ... ");
            }
            // in this simple setup all the server does in response to messages from the client is to send
            // a single ACK byte back to client - the client uses this ACK byte to test whether the
            // connection to this server is still live, if not the client shuts down cleanly
//            os.write(Configuration.ack_byte);
        }
    }

    private void cleanup() {
        System.out.println("ConnectionHandler: ... cleaning up and exiting ... ");
        try {
            br.close();
            is.close();
            conn.close();
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
        }
    }


}
