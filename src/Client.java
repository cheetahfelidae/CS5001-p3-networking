

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Client {

    private Socket socket;
    private String host;
    private int port;

    private BufferedReader br;
    private PrintWriter pw;

    private InputStream test_is;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        runClient();
    }


    private void runClient() {
        try {
            this.socket = new Socket(host, port);
            System.out.println("Client connected to " + host + " on port " + port + ".");
            System.out.println("To exit enter a single line containing: " + Configuration.exitString);
            br = new BufferedReader(new InputStreamReader(System.in));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            test_is = socket.getInputStream();
            printUserInputToSocket(); // this runs until something goes wrong

        } catch (Exception e) { // exit cleanly for any Exception (including IOException, SocketTimeoutException, DisconnectedException)
            System.out.println("Ooops on connection to " + host + " on port " + port + ". " + e.getMessage());
            cleanup(); // execute cleanup method to close connections cleanly
        }
    }

    // test to see if connection to server is still ok by trying to
    // read the ACK byte the server returns to the client
    // set a timeout for the blocking read in case the server has died
    // or the connection has otherwise been broken. If no result is read
    // within the specified timeout the read method throws SocketTimeoutException
    // which will be passed up the call-stack to the nearest handler (catch block)
    // in the runClient method. If -1 is read, a DisconnectedException is thrown.
    private void testServerConnection() throws IOException, DisconnectedException, SocketTimeoutException {
        int old_timeout = socket.getSoTimeout();
        socket.setSoTimeout(500);
        int res = test_is.read(); // this will throw an SocketTimeoutException if the connection to the server has been reset or times out
        if (res == -1) {
            throw new DisconnectedException("... connection has been lost ...");
        }
        socket.setSoTimeout(old_timeout);
    }

    public void receiveResource() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = new FileOutputStream("/Users/cheetah/Sites/CS5001-p3-networking/Resources/beer.jpg");
            copy(in, out);
            out.close();
            in.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
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

    private void printUserInputToSocket() throws IOException, SocketTimeoutException, DisconnectedException {
        while (true) {
            String line = br.readLine(); // get user input
            pw.println(line);            // print line out on the socket's output stream
            if (line.equals(Configuration.exitString)) { // user has entered exit command
                throw new DisconnectedException(" ... user has entered exit command ... ");
            } else if (line.equalsIgnoreCase("keta")) {
                receiveResource();
            }
            testServerConnection(); // test to see if connection to server is still ok by trying to read the ACK byte the server returns to the client
        }
    }


    private void cleanup() {
        System.out.println("Client: ... cleaning up and exiting ... ");
        try {
            if (pw != null) pw.close();
            if (br != null) br.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Ooops " + ioe.getMessage());
        }
    }

}

