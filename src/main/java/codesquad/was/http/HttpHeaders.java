package codesquad.was.http;

import codesquad.was.util.IOUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HttpHeaders {
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String HOST_HEADER = "Host";

    public static final String DATE_HEADER = "Date";

    public static final String CONNECTION_HEADER = "Connection";

    public static final String LOCATION = "Location";

    public static final String ACCEPT = "Accept";

    public static final String SET_COOKIE = "Set-Cookie";

    public static final String COOKIE = "Cookie";

    public static final String SID = "SID";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    private final List<HttpHeader> headers;

    public HttpHeaders() {
        this.headers = new ArrayList<>();
    }

    public HttpHeaders(List<HttpHeader> headers) {
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

    public static HttpHeaders empty() {
        return new HttpHeaders();
    }


    public List<HttpHeader> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    public Optional<HttpHeader> getHeader(String key) {
        return headers
                .stream()
                .filter(header -> header.hasKey(key))
                .findFirst();
    }

    public Optional<String> getHeaderSingleValue(String key) {
        Optional<HttpHeader> header = getHeader(key);
        if (header.isEmpty()) {
            return Optional.empty();
        }
        return header.get().getSingleValue();
    }

    public boolean contains(String key) {
        return headers.stream()
                .anyMatch(header -> header.hasKey(key));
    }

    public void setHeader(String name, List<String> values) {
        headers.add(new HttpHeader(name, values));
    }

    public void setHeader(String name, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        headers.add(new HttpHeader(name, values));
    }

    public void setHeader(HttpHeader header) {
        headers.add(header);
    }

    public int size() {
        return headers.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        headers.forEach(header -> sb.append("\n").append(header.toString()));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpHeaders headers1 = (HttpHeaders) o;
        return Objects.equals(headers, headers1.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(headers);
    }
}
