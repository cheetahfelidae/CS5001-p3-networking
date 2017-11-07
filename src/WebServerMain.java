/**
 * This is where the main methods is for running the server.
 * <p>
 * original source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/ServerMain.java.
 */
public class WebServerMain {
    private static final String DEFAULT_LOG_FILE = System.getProperty("user.dir") + "/log";
    private static final int DEFAULT_NUM_CLIENTS = Integer.MAX_VALUE;

    /**
     * Create new server.
     * If the server is started without supplying the two command-line arguments, it simply print the usage message.
     *
     * @param args two command-lin arguments required:
     *             1. the directory from which the server will serve documents to clients,
     *             2. the port on which the server will listen,
     */
    public static void main(String[] args) {
        try {
            new WebServer(args[0], Integer.parseInt(args[1]), DEFAULT_LOG_FILE, DEFAULT_NUM_CLIENTS);
        } catch (Exception e) {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
        }
    }

}
