/**
 * This is where the main methods is for running the server.
 *
 * original source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/ServerMain.java.
 */
public class WebServerMain {

    /**
     * Create new server.
     * @param args two command-lin arguments required: the directory from which the server will serve documents to clients and the port on which the server will listen.
     */
    public static void main(String[] args) {
        try {
            new WebServer(args[0], Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
        }
    }

}
