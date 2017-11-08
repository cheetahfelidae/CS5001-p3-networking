/**
 * This is where the main methods is for running the extended server.
 * <p>
 * original source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/ServerMain.java.
 */
public class ExtendedWebServerMain {
    /**
     * This is used to index command-line argument variables instead of using numeric numbers due to magic number issue.
     */
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private static final int FOURTH = 3;

    /**
     * Create new server.
     * If the server is started without supplying the four command-line arguments, it simply print the usage message.
     *
     * @param args four command-lin arguments required:
     *             1. the directory from which the server will serve documents to clients,
     *             2. the port on which the server will listen,
     *             3. the path of the file to which log will write (as a part of the extension).
     *             4. the specified limit of multiple concurrent client connection requests (as a part of the extension).
     */
    public static void main(String[] args) {
        try {
            new WebServer(args[FIRST], Integer.parseInt(args[SECOND]), args[THIRD], Integer.parseInt(args[FOURTH]));
        } catch (Exception e) {
            System.out.println("Usage: java ExtendedWebServerMain <document_root> <port> <log_path> <max_clients>");
        }
    }

}
