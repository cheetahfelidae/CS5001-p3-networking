import constants.RequestCode;
import constants.ResponseCode;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

import static constants.ContentType.*;
import static constants.ResponseCode.*;

/**
 * This class handles with HTTP requests with only one accessible (public) method called "processRequest".
 */
public class Response {
    private Socket conn;
    private String document_root;
    private PrintWriter print_writer;
    private Logger logger;

    public Response(Socket conn, String document_root) throws IOException {
        this.conn = conn;
        this.document_root = document_root;
        logger = Logger.getLogger(Response.class.getName());
    }

    private String getHeaderMsg(String response_code, String content_type, long resource_length) {
        final String CR_LF = "\r\n";

        String msg = "HTTP/1.1 " + response_code + CR_LF;

        if (content_type.length() > 0) {
            msg += "Content-Type: " + content_type + CR_LF;
        }

        return msg + "Content-Length: " + resource_length + CR_LF;
    }

    // TODO - to be improved
    private String getBodyMsg(String respond_code, String name) {

        switch (ResponseCode.convert(respond_code)) {
            case NOT_FOUND:
                return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p>The requested URL " + name + " was not found on this server.</p></body></html>";
            case NOT_IMPLEMENTED:
                return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p>The request code " + name + " was not found on this server.</p></body></html>";
        }

        return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p></p></body></html>";
    }

    private void sendResource(String resource_path) {
        final int CHUNK_SIZE = 1500;

        try {
            InputStream in = new FileInputStream(resource_path);
            OutputStream out = conn.getOutputStream();

            byte[] buf = new byte[CHUNK_SIZE];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respondHead() {
        print_writer.println(getHeaderMsg(WORKING_OKAY.toString(), NONE.toString(), 0));
    }

    private void respondGet(String resource_name) {
        File resource = new File(document_root + resource_name);
        if (resource.exists()) {
            print_writer.println(getHeaderMsg(WORKING_OKAY.toString(), TEXT_HTML.toString(), resource.length()));
            sendResource(document_root + resource_name);
        } else {
            sendNotFound(resource_name);
        }
    }

    /**
     * If the requested file is deleted successfully, then send HTTP 200 message, implying "resource deleted successfully".
     *
     * @param resource_name
     */
    private void respondDelete(String resource_name) {
        File resource = new File(document_root + resource_name);
        if (resource.exists()) {
            print_writer.println(getHeaderMsg(WORKING_OKAY.toString(), NONE.toString(), 0));
            resource.delete();
            logger.warning(resource_name.toString() + " IS DELETED SUCCESSFULLY");
        } else {
            sendNotFound(resource_name);
        }
    }

    private void sendNotFound(String resource_name) {
        String body_msg = getBodyMsg(NOT_FOUND.toString(), resource_name);
        print_writer.println(getHeaderMsg(NOT_FOUND.toString(), TEXT_HTML.toString(), body_msg.length()));
        print_writer.println(body_msg);
        logger.warning(resource_name + " NOT FOUND");
    }

    private void sendNotImplemented(String request_name) {
        String body_msg = getBodyMsg(NOT_IMPLEMENTED.toString(), request_name);
        print_writer.println(getHeaderMsg(NOT_IMPLEMENTED.toString(), TEXT_HTML.toString(), body_msg.length()));
        print_writer.println(body_msg);
        logger.warning("REQUEST TYPE: " + request_name + " IS NOT RECOGNISED");
    }

    /**
     * Handle with each type of the HTTP request (HEAD, GET and DELETE)
     * and then respond appropriately with successful messages or error messages when non-existent services or resources are requested.
     *
     * @param line
     * @throws IOException
     */
    public void processRequest(String line) throws IOException {
        print_writer = new PrintWriter(this.conn.getOutputStream(), true);

        String[] request_header = line.split("\\s+");
        String request_code = request_header[0];

        switch (RequestCode.convert(request_code)) {
            case HEAD:
                respondHead();
                break;
            case GET:
                respondGet(request_header[1]);
                break;
            case DELETE:
                respondDelete(request_header[1]);
                break;
            default:
                sendNotImplemented(request_code);
        }

        print_writer.close();
    }


}
