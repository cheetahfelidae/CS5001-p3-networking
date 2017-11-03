

public class WebServerMain {

    public static void main(String[] args) {
        try {
            new WebServer(args[0], Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
        }
    }

}
