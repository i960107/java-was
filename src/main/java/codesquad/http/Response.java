package codesquad.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public interface Response {

    String getProtocol();

    void setProtocol(String protocol);

    String getStatusCode();

    String getStatusMessage();

    void setStatus(HttpStatus httpStatus);

    HttpHeaders getHeaders();

    void setHeaders(HttpHeaders httpHeaders);

    Optional<HttpHeader> getHeader(String key);

    void setHeader(HttpHeader header);

    OutputStream getOutputStream() throws IOException;

    void setOutputStream(OutputStream outputStream);

    void write(byte[] data) throws IOException;
}
