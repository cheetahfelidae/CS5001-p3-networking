import constants.FileType;
import constants.RequestCode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static constants.FileType.GIF;
import static constants.FileType.JPEG;
import static constants.FileType.PNG;
import static constants.ResponseCode.NOT_FOUND;
import static constants.ResponseCode.NOT_IMPLEMENTED;
import static constants.ResponseCode.WORKING_OKAY;

/**
 * This class handles with HTTP requests which comprises of two components: header and document body.
 * There is only one accessible (public) method which is processRequest().
 */
public class Responder {
    private static final String TEXT_HTML = "text/html";
    private static final String CR_LF = "\r\n"; // use of <CR><LF> to delimit header fields and header from content.
    private static final int CHUNK_SIZE = 1500;

    private Socket conn;
    private String document_root;
    private PrintWriter print_writer;
    private LogFile log_file;

    /**
     * Initialise variables: connection socket, path of the document and path of the log file.
     *
     * @param conn          established connection with a client.
     * @param document_root a path where the server serves a document to a client.
     * @param logger        used to track information of the requests into a file.
     */
    public Responder(Socket conn, String document_root, LogFile logger) {
        this.conn = conn;
        this.document_root = document_root;
        this.log_file = logger;
    }

    /**
     * Get response header containing information about the resource identified in the request (if the file exists at the specified location in the document root).
     *
     * @param response_code   response code from the response.
     * @param content_type    the type of the requested resource.
     * @param resource_length the size of the requested resource.
     * @return
     */
    private String getHeader(String response_code, String content_type, long resource_length) {
        return "HTTP/1.1 " + response_code + CR_LF
                + "Content-Type: " + content_type + CR_LF
                + "Content-Length: " + resource_length + CR_LF;
    }

    /**
     * Extension: Return a request file (e.g. GIF, JPEG, PNG, etc.) in binary.
     *
     * @param resource_path the name of the requested file from the client.
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
            print_writer.println(getHeader(WORKING_OKAY.toString(), getFileExtension(resource_name), resource.length()));

            log_file.logRespond(WORKING_OKAY.toString(), conn.getInetAddress());
        } else {
            respondNotFound(resource_name);
        }
    }

    /**
     * Get the file extension.
     * This method supports for the case that a directory may have a '.', but the filename itself doesn't (e.g. /path/to.a/file).
     * <p>
     * original resource: https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java.
     *
     * @param file_name the name of the requested file.
     * @return the extension of the file.
     */
    private String getFileExtension(String file_name) {
        String extension = "";

        int i = file_name.lastIndexOf('.'),
                p = Math.max(file_name.lastIndexOf('/'), file_name.lastIndexOf('\\'));

        if (i > p) {
            extension = file_name.substring(i + 1);
        }

        switch (FileType.convert(extension)) {
            case HTML:
                return TEXT_HTML;
            case GIF:
            case JPEG:
            case PNG:
                return extension;
            default:
        }

        return "";
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
            print_writer.println(getHeader(WORKING_OKAY.toString(), getFileExtension(resource_name), resource.length()));
            sendResource(document_root + resource_name);

            log_file.logRespond(WORKING_OKAY.toString(), conn.getInetAddress());
        } else {
            respondNotFound(resource_name);
        }
    }

    /**
     * Extension: If a requested file is found, then delete the file and
     * return only HTTP 200 response header, implying "resource deleted successfully" without the body message.
     *
     * @param resource_name the name of the request-to-be-deleted file from the client.
     */
    private void respondDELETE(String resource_name) {
        File resource = new File(document_root + resource_name);

        if (resource.exists()) {
            print_writer.println(getHeader(WORKING_OKAY.toString(), getFileExtension(resource_name), 0));

            log_file.logWarning(resource_name
                    + (resource.delete() ? " HAS BEEN DELETED SUCCESSFULLY" : " HAS FAILED TO BE DELETED SUCCESSFULLY"));
        } else {
            respondNotFound(resource_name);
        }
    }

    /**
     * Extension: Returns the HTTP methods that the server supports.
     */
    private void respondOPTIONS() {
        String page = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html>"
                + "<head><title> Server-Supported HTTP Methods</title></head>"
                + "<body><h1>HTTP methods which the server serves</h1><p>HEAD</p><p>GET</p><p>DELETE</p><p>OPTIONS</p></body>"
                + "</html>";

        print_writer.println(getHeader(WORKING_OKAY.toString(), TEXT_HTML, page.length()));

        print_writer.println(page);

        log_file.logRespond(WORKING_OKAY.toString(), conn.getInetAddress());
    }

    /**
     * If the request file cannot be found,
     * then return 404 File Not Found response header followed by HTML page (the latter for a GET request only) to client.
     *
     * @param resource_name the name of the not-found file from the client.
     */
    private void respondNotFound(String resource_name) {
        String page = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html>"
                + "<head><title>" + NOT_FOUND.toString() + "</title></head>"
                + "<body><h1>" + NOT_FOUND.toString() + "</h1><p>The requested URL " + resource_name + " was not found on this server.</p></body>"
                + "</html>";

        print_writer.println(getHeader(NOT_FOUND.toString(), getFileExtension(resource_name), page.length()));

        print_writer.println(page);

        log_file.logRespond(NOT_FOUND.toString(), conn.getInetAddress());
    }

    /**
     * If the request code doesn't exist or is unsupported by the server,
     * return 501 File Not Implemented response header without body message.
     *
     * @param unrecognised_code code which has never been implemented in the server.
     */
    private void respondNotImplemented(String unrecognised_code) {
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
     * @throws IOException is thrown in case of connection failed.
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
            case OPTIONS:
                respondOPTIONS();
            default:
                respondNotImplemented(request_code);
        }

        print_writer.close();
    }


}
