


import java.io.IOException;
import java.net.*;

public class WebServer {

    private ServerSocket ss; // listen for client connection requests on this server socket

    public WebServer(String document_root, int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("WebServer started ... listening on port " + port + " ...");
            while (true) {
                Socket conn = ss.accept(); // will wait until client requests a connection, then returns connection (socket)
                System.out.println("WebServer got new connection request from " + conn.getInetAddress());
                ConnectionHandler ch = new ConnectionHandler(document_root, conn); // create new handler for this connection
                ch.start();                                         // start handler thread
            }
        } catch (IOException ioe) {
            System.out.println("Ooops " + ioe.getMessage());
        }
    }
}
