import constants.RequestCode;
import constants.ResponseCode;

import java.io.*;
import java.net.Socket;

import static constants.ResponseCode.*;

/**
 * This class handles with HTTP requests with only one accessible (public) method called "processRequest".
 */
public class Response {
    private static final String TEXT_HTML = "text/html";
    private static final int CHUNK_SIZE = 1500;

    private Socket conn;
    private String document_root;
    private PrintWriter print_writer;
    private LogFile log_file;

    /**
     * @param conn          established connection with a client.
     * @param document_root a path where the server serves a document to a client.
     * @throws IOException
     */
    public Response(Socket conn, String document_root, LogFile logger) throws IOException {
        this.conn = conn;
        this.document_root = document_root;
        this.log_file = logger;
    }

    /**
     * Return response header containing information about the resource identified in the request (if the file exists at the specified location in the document root).
     *
     * @param response_code   response code from the response.
     * @param content_type    type of the requested resource.
     * @param resource_length the size of the requested resource.
     * @return
     */
    private String getHeader(String response_code, String content_type, long resource_length) {
        final String CR_LF = "\r\n";

        return "HTTP/1.1 " + response_code + CR_LF +
                "Content-Type: " + content_type + CR_LF +
                "Content-Length: " + resource_length + CR_LF;
    }

    /**
     * Return a specific HTML page corresponds to a particular respond code.
     *
     * @param respond_code respond code from the client request.
     * @param name         either a requested file name for NOT FOUND code or unrecognised request code
     * @return HTML page.
     */
    private String getHTMLPage(String respond_code, String name) {

        switch (ResponseCode.convert(respond_code)) {
            case NOT_FOUND:
                return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p>The requested URL " + name + " was not found on this server.</p></body></html>";
        }

        return "";
    }

    /**
     * Return a request file (e.g. GIF, JPEG and PNG) in binary.
     *
     * @param resource_path the name of the request file from the client.
     */
    private void sendResource(String resource_path) {
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
            log_file.logWarning(resource_path + " IS NOT FOUND");
        } catch (IOException e) {
            log_file.logWarning("IOException: " + e.getMessage());
        }
    }

    /**
     * Return a requested HTTP 200 response header containing the information about the resource identified in the request (if the file exists at the specified location in the document root) to client.
     *
     * @param resource_name the name of the request file from the client.
     */
    private void respondHEAD(String resource_name) {
        File resource = new File(document_root + resource_name);

        if (resource.exists()) {
            print_writer.println(getHeader(WORKING_OKAY.toString(), TEXT_HTML, resource.length()));

            log_file.logRespond(WORKING_OKAY.toString(), conn.getInetAddress());
        } else {
            sendNotFound(resource_name);
        }
    }

    /**
     * Similarly to respondHEAD() as for the HEAD request,
     * return a requested header followed by requested file data (if the file exists at the specified location in the document root) to client.
     *
     * @param resource_name the name of the request file from the client.
     */
    private void respondGET(String resource_name) {
        File resource = new File(document_root + resource_name);

        if (resource.exists()) {
            print_writer.println(getHeader(WORKING_OKAY.toString(), TEXT_HTML, resource.length()));
            sendResource(document_root + resource_name);

            log_file.logRespond(WORKING_OKAY.toString(), conn.getInetAddress());
        } else {
            sendNotFound(resource_name);
        }
    }

    /**
     * If a requested file is found, then delete the file and
     * return only HTTP 200 response header, implying "resource deleted successfully" without the body message.
     *
     * @param resource_name the name of the request-to-be-deleted file from the client.
     */
    private void respondDELETE(String resource_name) {
        File resource = new File(document_root + resource_name);

        if (resource.exists()) {
            print_writer.println(getHeader(WORKING_OKAY.toString(), TEXT_HTML, 0));

            log_file.logWarning(resource_name +
                    (resource.delete() ? " HAS BEEN DELETED SUCCESSFULLY" : " HAS FAILED TO BE DELETED SUCCESSFULLY"));
        } else {
            sendNotFound(resource_name);
        }
    }

    /**
     * If the request file cannot be found,
     * then return 404 File Not Found response header followed by HTML page (the latter for a GET request only) to client.
     *
     * @param resource_name the name of the not-found file from the client.
     */
    private void sendNotFound(String resource_name) {
        String page = getHTMLPage(NOT_FOUND.toString(), resource_name);
        print_writer.println(getHeader(NOT_FOUND.toString(), TEXT_HTML, page.length()));
        print_writer.println(page);

        log_file.logRespond(NOT_FOUND.toString(), conn.getInetAddress());
    }

    /**
     * If the request code doesn't exist or is unsupported by the server,
     * return 501 File Not Implemented response header without body message.
     *
     * @param unrecognised_code code which has never been implemented in the server.
     */
    private void sendNotImplemented(String unrecognised_code) {
        print_writer.println(getHeader(NOT_IMPLEMENTED.toString(), TEXT_HTML, 0));

        log_file.logInfo("REQUEST CODE " + unrecognised_code + " IS NOT SUPPORTED BY THE SERVER");
        log_file.logRespond(NOT_IMPLEMENTED.toString(), conn.getInetAddress());
    }

    /**
     * Check whether the textual request from client corresponds to HEAD, GET and DELETE request
     * and then respond appropriately with successful messages or error messages when non-existent services or resources are requested.
     * If the received request is not supported, then send back 501 File Not Implemented response to the client.
     *
     * @param request request message from a client.
     * @throws IOException
     */
    public void processRequest(String request) throws IOException {
        print_writer = new PrintWriter(this.conn.getOutputStream(), true);

        String[] request_header = request.split("\\s+");
        String request_code = request_header[0];

        log_file.logRequest(request_code, conn.getInetAddress());

        switch (RequestCode.convert(request_code)) {
            case HEAD:
                respondHEAD(request_header[1]);
                break;
            case GET:
                respondGET(request_header[1]);
                break;
            case DELETE:
                respondDELETE(request_header[1]);
                break;
            default:
                sendNotImplemented(request_code);
        }

        print_writer.close();
    }


}
