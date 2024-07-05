package codesquad.http;

import codesquad.util.IOUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HttpHeaders {
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String HOST_HEADER = "Host";

    public static final String DATE_HEADER = "Date";

    public static final String CONNECTION_HEADER = "Connection";

    public static final String LOCATION = "Location";

    public static final String ACCEPT = "Accept";

    private final Set<HttpHeader> headers;

    public HttpHeaders() {
        this.headers = new HashSet<>();
    }

    public HttpHeaders(Set<HttpHeader> headers) {
        this.headers = headers;
    }

    public static HttpHeaders getDefault() {
        HttpHeaders headers = new HttpHeaders();
        setCommonHeader(headers);
        return headers;
    }

    public static void setCommonHeader(HttpHeaders headers) {
        headers.setHeader(HttpHeaders.DATE_HEADER, IOUtil.getDateStringUtc());
        headers.setHeader(HttpHeaders.CONNECTION_HEADER, "close");
    }


    public Set<HttpHeader> getHeaders() {
        return Collections.unmodifiableSet(headers);
    }

    public Optional<HttpHeader> getHeader(String key) {
        return headers
                .stream()
                .filter(header -> header.hasKey(key))
                .findAny();
    }

    public boolean contains(String key) {
        return headers.stream()
                .anyMatch(header -> header.hasKey(key));
    }

    public void setHeader(String name, Set<String> values) {
        headers.add(new HttpHeader(name, values));
    }

    public void setHeader(String name, String value) {
        Set<String> values = new HashSet<>();
        values.add(value);
        headers.add(new HttpHeader(name, values));
    }

    public void setHeader(HttpHeader header) {
        headers.add(header);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        headers.forEach(header -> sb.append(header.toString()).append("\n"));
        return sb.toString();
    }
}
