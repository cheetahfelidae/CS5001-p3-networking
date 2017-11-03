

import java.io.*;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private Socket conn;       // socket representing TCP/IP connection to Client
    private InputStream is;    // get data from client on this input stream
    private OutputStream os;   // can send data back to the client on this output stream
    BufferedReader br;         // use buffered reader to read client data

    public ConnectionHandler(Socket conn) {
        this.conn = conn;
        try {
            is = conn.getInputStream();     // get data from client on this input stream
            os = conn.getOutputStream();  // to send data back to the client on this stream
            br = new BufferedReader(new InputStreamReader(is)); // use buffered reader to read client data
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler: " + ioe.getMessage());
        }
    }

    public void run() { // run method is invoked when the Thread's start method (ch.start(); in Server class) is invoked
        System.out.println("new ConnectionHandler thread started .... ");
        try {
            printClientData();
        } catch (Exception e) { // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
            System.out.println("ConnectionHandler:run " + e.getMessage());
            cleanup();     // cleanup and exit
        }
    }

    public void sendResource() {
        try {
            InputStream in = new FileInputStream("/Users/cheetah/Sites/CS5001-p3-networking/Resources/www/beer.jpg");
            OutputStream out = conn.getOutputStream();
            copy(in, out);
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    private void printClientData() throws DisconnectedException, IOException {
        while (true) {
            String line = br.readLine(); // get data from client over socket
            // if readLine fails we can deduce here that the connection to the client is broken
            // and shut down the connection on this side cleanly by throwing a DisconnectedException
            // which will be passed up the call stack to the nearest handler (catch block)
            // in the run method
            if (line == null || line.equals("null") || line.equals(Configuration.exitString)) {
                throw new DisconnectedException(" ... client has closed the connection ... ");
            } else if (line.equalsIgnoreCase("keta")) {
                sendResource();
            }
            // in this simple setup all the server does in response to messages from the client is to send
            // a single ACK byte back to client - the client uses this ACK byte to test whether the
            // connection to this server is still live, if not the client shuts down cleanly
            os.write(Configuration.ackByte);
            System.out.println("ConnectionHandler: " + line); // assuming no exception, print out line received from client
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
