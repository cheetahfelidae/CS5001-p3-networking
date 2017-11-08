/**
 * This is where the main methods is for running the server.
 * <p>
 * original source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/ServerMain.java.
 */
public class WebServerMain {
    /**
     * This is used to index command-line argument variables instead of using numeric numbers due to magic number issue.
     */
    private static final int FIRST = 0;
    private static final int SECOND = 1;

    private static final String DEFAULT_LOG_FILE = System.getProperty("user.dir") + "/log"; // log file will in the current directory
    private static final int DEFAULT_MAX_CLIENTS = Integer.MAX_VALUE;

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
            new WebServer(args[FIRST], Integer.parseInt(args[SECOND]), DEFAULT_LOG_FILE, DEFAULT_MAX_CLIENTS);
        } catch (Exception e) {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
        }
    }

}
