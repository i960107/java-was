package codesquad.http;

import java.io.IOException;
import java.io.OutputStream;

public class WasResponse {
    private static final int BUFFER_SIZE = 8192;

    private OutputStream out;

    public WasResponse(OutputStream out) {
        this.out = out;
    }

    public void sendError(String protocol, HttpStatus status, HttpHeaders headers) throws IOException {
        HttpResponseWriter.writeStatusLine(out, protocol, status);
        HttpResponseWriter.writeHeaders(out, headers);
    }

    public void sendRedirect(String protocol, HttpHeaders headers, String redirectUrl) throws IOException {
        HttpResponseWriter.writeStatusLine(out, protocol, HttpStatus.FOUND);
        headers.setHeader(HttpHeaders.LOCATION, redirectUrl);
        HttpResponseWriter.writeHeaders(out, headers);
    }

    public void send(String protocol, HttpStatus status, HttpHeaders headers, byte[] body) throws IOException {
        HttpResponseWriter.writeStatusLine(out, protocol, status);
        HttpResponseWriter.writeHeaders(out, headers);
        HttpResponseWriter.writeBody(out, body);
    }

}


