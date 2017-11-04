import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class Response {
    private static final String WORKING_OKAY = "200 OK";
    private static final String NOT_FOUND = "404 Not Found";
    private static final String NOT_IMPLEMENTED = "501 Not Implemented";
    private static final String CR_LF = "\r\n";

    private static String get_header_msg(String response_code, long resource_length) {
        return "HTTP/1.1" + response_code + CR_LF +
                "Server: Simple Java Http Server" + CR_LF +
                "Content-Length: " + resource_length + CR_LF +
                "Content-Type: text/html" + CR_LF;
    }

    // TODO - to be improved
    private static String get_body_msg(String respond_code, String name) {
        switch (respond_code) {
            case NOT_FOUND:
                return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p>The requested URL " + name + " was not found on this server.</p></body></html>";
            case NOT_IMPLEMENTED:
                return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p>The request code " + name + " was not found on this server.</p></body></html>";
        }
        return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p></p></body></html>";
    }

    private static void sendResource(Socket conn, String resource_path) {
        try {
            InputStream in = new FileInputStream(resource_path);
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

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    public static void respond(Socket conn, String line, String document_root) throws IOException {
        String[] request_line = line.split("\\s+");
        PrintWriter out = new PrintWriter(conn.getOutputStream(), true);

        String resource_name = request_line[1];

        File resource = new File(document_root + resource_name);

        if (resource.exists()) {
            switch (RequestCode.convert(request_line[0])) {
                case HEAD:
                    out.println(Response.get_header_msg(WORKING_OKAY, resource.length()));
                    break;
                case GET:
                    out.println(Response.get_header_msg(WORKING_OKAY, resource.length()));
                    Response.sendResource(conn, document_root + resource_name);
                    break;
                default:
                    String not_found_body = get_body_msg(NOT_IMPLEMENTED, resource_name);
                    out.println(Response.get_header_msg(NOT_IMPLEMENTED, not_found_body.length()));
                    out.println(not_found_body);
                    Logger.getLogger(ConnectionHandler.class.getName()).warning("REQUEST TYPE: " + request_line[0] + " IS NOT RECOGNISED");
            }
        } else {
            String not_found_body = get_body_msg(NOT_FOUND, resource_name);
            out.println(Response.get_header_msg(NOT_FOUND, not_found_body.length()));
            out.println(not_found_body);
            Logger.getLogger(ConnectionHandler.class.getName()).warning(resource.toString() + " NOT FOUND");
        }

        out.close();
    }
}
