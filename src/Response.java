import constants.RequestCode;
import constants.ResponseCode;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

import static constants.ResponseCode.*;

public class Response {
    private static final String CR_LF = "\r\n";

    private static String getHeaderMsg(String response_code, long resource_length) {
        return "HTTP/1.1 " + response_code + CR_LF +
                "Content-Type: text/html" + CR_LF +
                "Content-Length: " + resource_length + CR_LF;
    }

    // TODO - to be improved
    private static String getBodyMsg(String respond_code, String name) {
        switch (ResponseCode.convert(respond_code)) {
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

    private static void respondHead(PrintWriter out) {
        out.println(getHeaderMsg(WORKING_OKAY.toString(), 0));
    }

    private static void respondGet(Socket conn, PrintWriter out, String document_root, String resource_name) {
        File resource = new File(document_root + resource_name);
        if (resource.exists()) {
            out.println(getHeaderMsg(WORKING_OKAY.toString(), resource.length()));
            sendResource(conn, document_root + resource_name);
        } else {
            sendNotFound(out, resource_name);
        }
    }

    /**
     * If the requested file is deleted successfully, then send HTTP 200 message, implying "resource deleted successfully".
     * @param conn
     * @param out
     * @param document_root
     * @param resource_name
     */
    private static void respondDelete(Socket conn, PrintWriter out, String document_root, String resource_name) {
        File resource = new File(document_root + resource_name);
        if (resource.exists()) {
            out.println(getHeaderMsg(WORKING_OKAY.toString(), 0));
            System.out.println(resource.delete());
        } else {
            sendNotFound(out, resource_name);
        }
    }

    private static void sendNotFound(PrintWriter out, String resource_name) {
        String body_msg = getBodyMsg(NOT_FOUND.toString(), resource_name);
        out.println(getHeaderMsg(NOT_FOUND.toString(), body_msg.length()));
        out.println(body_msg);
        Logger.getLogger(ConnectionHandler.class.getName()).warning(resource_name + " NOT FOUND");
    }

    private static void sendNotImplemented(PrintWriter out, String request_name) {
        String body_msg = getBodyMsg(NOT_IMPLEMENTED.toString(), request_name);
        out.println(getHeaderMsg(NOT_IMPLEMENTED.toString(), body_msg.length()));
        out.println(body_msg);
        Logger.getLogger(ConnectionHandler.class.getName()).warning("REQUEST TYPE: " + request_name + " IS NOT RECOGNISED");
    }

    /**
     * Handle with each type of the HTTP request (HEAD, GET and DELETE)
     * and then respond appropriately with successful messages or error messages when non-existent services or resources are requested.
     *
     * @param conn
     * @param line
     * @param document_root
     * @throws IOException
     */
    public static void processRequest(Socket conn, String line, String document_root) throws IOException {
        String[] request_header = line.split("\\s+");
        String request_code = request_header[0];
        PrintWriter out = new PrintWriter(conn.getOutputStream(), true);

        switch (RequestCode.convert(request_code)) {
            case HEAD:
                respondHead(out);
                break;
            case GET:
                respondGet(conn, out, document_root, request_header[1]);
                break;
            case DELETE:
                respondDelete(conn, out, document_root, request_header[1]);
                break;
            default:
                sendNotImplemented(out, request_code);
        }

        out.close();
    }
}
