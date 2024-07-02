package codesquad.http;

import java.util.Map;
import java.util.Optional;

public interface Request {
    Map<String, String> getHeaders();

    Optional<String> getHeader(String name);

    Map<String, String> getParameters();

    Optional<String> getParameter(String name);

    byte[] getBody();

    String getProtocol();

    String getPath();

    String getMethod();

    String getHost();
}
