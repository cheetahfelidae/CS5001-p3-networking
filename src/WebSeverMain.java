import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class WebSeverMain {

    /**
     * 1. Create a socket to connect to a Server on a specified localhost port.
     * 2. Create a Reader and a Writer from the inputStream and OutputStream.
     * 3. Send the text "Ping" to the server.
     * 4. Read the response from the server.
     * 5. Close the socket connection.
     */
    public void client() {
        try {
            Socket socket = new Socket("localhost", 8888);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Ping");
            String rec = in.readLine();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 1. Create a TCP server socket which listens on a specified port.
     * 2. Wait for an incoming connection, when one is mad 'conn' socket serves as the endpoint.
     * 3. Create a reader and a Writer from the streams.
     * 4. Read in a line, terminated by \n from the connected client.
     * 5. Send that line back to the client.
     * 6. Close the connection.
     */
    public void server() {
        try {
            ServerSocket ss = new ServerSocket(8888);
            Socket conn = ss.accept();
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
            String line = in.readLine();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

    }
}
