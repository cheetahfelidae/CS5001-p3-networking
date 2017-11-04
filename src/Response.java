import constants.RequestCode;
import constants.ResponseCode;

import java.io.*;
import java.net.Socket;

import static constants.ContentType.*;
import static constants.ResponseCode.*;

/**
 * This class handles with HTTP requests with only one accessible (public) method called "processRequest".
 */
public class Response {
    private Socket conn;
    private String document_root;
    private PrintWriter print_writer;
    private LogFile logFile;

    /**
     * @param conn
     * @param document_root
     * @throws IOException
     */
    public Response(Socket conn, String document_root) throws IOException {
        this.conn = conn;
        this.document_root = document_root;
        logFile = new LogFile(document_root);
    }

    private String getHeader(String response_code, String content_type, long resource_length) {
        final String CR_LF = "\r\n";

        String msg = "HTTP/1.1 " + response_code + CR_LF;

        if (content_type.length() > 0) {
            msg += "Content-Type: " + content_type + CR_LF;
        }

        return msg + "Content-Length: " + resource_length + CR_LF;
    }

    /**
     * @param respond_code
     * @param name
     * @return
     */
    private String getHTMLPage(String respond_code, String name) {

        switch (ResponseCode.convert(respond_code)) {
            case NOT_FOUND:
                return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p>The requested URL " + name + " was not found on this server.</p></body></html>";
            case NOT_IMPLEMENTED:
                return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p>The request code " + name + " was not found on this server.</p></body></html>";
        }

        // TODO - to be improved
        return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>" + respond_code + "</title></head><body><h1>" + respond_code + "</h1><p></p></body></html>";
    }

    /**
     * @param resource_path
     */
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
            logFile.logWarning(resource_path + " IS NOT FOUND");
        } catch (IOException e) {
            logFile.logWarning("IOException: " + e.getMessage());
        }
    }

    /**
     * Return a requested HTTP 200 response header containing the information about the resource identified in the request (if the file exists at the specified location in the document root) to client.
     */
    private void respondHEAD() {
        print_writer.println(getHeader(WORKING_OKAY.toString(), NONE.toString(), 0));

        logFile.logRespond(WORKING_OKAY.toString(), conn.getInetAddress());
    }

    /**
     * Similarly to respondHEAD() as for the HEAD request,
     * return a requested header followed by requested file data (if the file exists at the specified location in the document root) to client.
     *
     * @param resource_name
     */
    private void respondGET(String resource_name) {
        File resource = new File(document_root + resource_name);

        if (resource.exists()) {
            print_writer.println(getHeader(WORKING_OKAY.toString(), TEXT_HTML.toString(), resource.length()));
            sendResource(document_root + resource_name);

            logFile.logRespond(WORKING_OKAY.toString(), conn.getInetAddress());
        } else {
            sendNotFound(resource_name);
        }
    }

    /**
     * If the requested file is found but will not be deleted only to be marked in the log file,
     * return only send HTTP 200 response header, implying "resource deleted successfully".
     *
     * @param resource_name
     */
    private void respondDELETE(String resource_name) {
        File resource = new File(document_root + resource_name);

        if (resource.exists()) {
            print_writer.println(getHeader(WORKING_OKAY.toString(), NONE.toString(), 0));
//            resource.delete();

            logFile.logWarning(resource_name.toString() + " IS MARKED TO BE DELETED");
        } else {
            sendNotFound(resource_name);
        }
    }

    /**
     * If the request file cannot be found,
     * then return 404 File Not Found response header followed by HTML page (the latter for a GET request only) to client.
     *
     * @param resource_name
     */
    private void sendNotFound(String resource_name) {
        String HTML_page = getHTMLPage(NOT_FOUND.toString(), resource_name);
        print_writer.println(getHeader(NOT_FOUND.toString(), TEXT_HTML.toString(), HTML_page.length()));
        print_writer.println(HTML_page);

        logFile.logRespond(NOT_FOUND.toString(), conn.getInetAddress());
    }

    /**
     * If the request code doesn't exist,
     * return 501 File Not Implemented response header followed by HTML body page to client.
     *
     * @param not_implemented_code
     */
    private void sendNotImplemented(String not_implemented_code) {
        String HTML_page = getHTMLPage(NOT_IMPLEMENTED.toString(), not_implemented_code);
        print_writer.println(getHeader(NOT_IMPLEMENTED.toString(), TEXT_HTML.toString(), HTML_page.length()));
        print_writer.println(HTML_page);

        logFile.logRespond(NOT_IMPLEMENTED.toString(), conn.getInetAddress());
    }

    /**
     * Check whether the textual request from client corresponds to HEAD, GET and DELETE request
     * and then respond appropriately with successful messages or error messages when non-existent services or resources are requested.
     * If the received request is not supported, then send back 501 File Not Implemented response to the client.
     * @param line
     * @throws IOException
     */
    public void processRequest(String line) throws IOException {
        print_writer = new PrintWriter(this.conn.getOutputStream(), true);

        String[] request_header = line.split("\\s+");
        String request_code = request_header[0];

        logFile.logRequest(request_code, conn.getInetAddress());

        switch (RequestCode.convert(request_code)) {
            case HEAD:
                respondHEAD();
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
