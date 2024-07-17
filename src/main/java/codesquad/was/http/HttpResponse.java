package codesquad.was.http;

import codesquad.was.util.IOUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpResponse {

    private static final String DEFAULT_PROTOCOL = "HTTP/1.1";

    private HttpRequest request;

    private HttpStatus status;

    private HttpHeaders headers;

    private ByteArrayOutputStream out;

    public HttpResponse(HttpRequest request) {
        this.request = request;
        this.status = HttpStatus.OK;
        this.headers = HttpHeaders.getDefault();
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

    public boolean hasBody() {
        return out.size() > 0;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public byte[] getOutputBytes() {
        return out.toByteArray();
    }

    public void setBody(byte[] body) {
        this.out.writeBytes(body);
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

    public void addHeader(HttpHeader header) {
        this.headers.setHeader(header);
    }

    public void setCookie(HttpCookie cookie) {
        addHeader(new HttpHeader(HttpHeaders.SET_COOKIE, cookie.toString()));
    }

    public void removeCookie(String key) {
        HttpCookie cookie = new HttpCookie(key, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        addHeader(new HttpHeader(HttpHeaders.SET_COOKIE, cookie.toString()));
    }

    public void sendError(HttpStatus status) {
        setStatus(status);
        addHeader(new HttpHeader(HttpHeaders.CONTENT_TYPE_HEADER, "text/html"));
        String errorPage = "static/error/" + status.getCode() + ".html";
        try {
            byte[] file = IOUtil.getClassPathResource(errorPage).readAllBytes();
            addHeader(new HttpHeader(HttpHeaders.CONTENT_LENGTH_HEADER, String.valueOf(file.length)));
            this.out.writeBytes(file);
        } catch (NullPointerException | IOException e) {
        }
    }

    public void sendRedirect(String redirectUrl) {
        setStatus(HttpStatus.FOUND);
        addHeader(new HttpHeader(HttpHeaders.LOCATION, redirectUrl));
    }

    public void send(MimeTypes contentType, byte[] body) {
        setHeaders(HttpHeaders.getDefault());
        addHeader(new HttpHeader(HttpHeaders.CONTENT_LENGTH_HEADER, String.valueOf(body.length)));
        addHeader(new HttpHeader(HttpHeaders.CONTENT_TYPE_HEADER, contentType.getMIMEType()));
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


