package codesquad.http;

import java.util.Map;
import java.util.Optional;

public interface Request {
    HttpHeaders getHeaders();

    Optional<HttpHeader> getHeader(String name);

    Map<String, String> getQueryString();

    Map<String, String> getParameters();

    Optional<String> getParameter(String name);

    byte[] getBody();

    String getProtocol();

    String getPath();

    HttpMethod getMethod();

    String getHost();
}
