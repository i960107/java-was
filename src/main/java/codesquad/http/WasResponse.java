package codesquad.http;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class WasResponse {

    private static final String DEFAULT_PROTOCOL = "HTTP/1.1";

    private WasRequest request;

    private HttpStatus status;

    private HttpHeaders headers;

    private ByteArrayOutputStream out;

    public WasResponse(WasRequest request) {
        this.request = request;
        this.status = HttpStatus.OK;
        this.headers = HttpHeaders.empty();
        this.out = new ByteArrayOutputStream();
    }

    public String getProtocol() {
        if (request == null) {
            return DEFAULT_PROTOCOL;
        }
        return request.getProtocol();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public OutputStream getOut() {
        return out;
    }

    public boolean hasBody() {
        return out.size() > 0;
    }

    public WasRequest getRequest() {
        return request;
    }

    public byte[] getOutputBytes() {
        return out.toByteArray();
    }

    public void setStatus(HttpStatus status) {
        if (status == null) {
            throw new IllegalArgumentException();
        }
        this.status = status;
    }

    public void setHeaders(HttpHeaders headers) {
        if (headers == null) {
            throw new IllegalArgumentException();
        }
        this.headers = headers;
    }

    public void sendError(HttpStatus status, HttpHeaders headers) {
        setStatus(status);
        setHeaders(headers);
    }

    public void sendRedirect(String redirectUrl) {
        setStatus(HttpStatus.FOUND);
        HttpHeaders headers = HttpHeaders.getDefault();
        headers.setHeader(HttpHeaders.LOCATION, redirectUrl);
        setHeaders(headers);
    }

    public void send(HttpHeaders headers, byte[] body) {
        setHeaders(headers);
        this.out.writeBytes(body);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WasResponse{");
        sb.append(", status=").append(status);
        sb.append(", headers=").append(headers);
        sb.append('}');
        return sb.toString();
    }
}


