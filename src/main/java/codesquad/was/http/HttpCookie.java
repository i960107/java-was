package codesquad.was.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpCookie {

    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";

    private static final String ATTRIBUTE_DELIMITER = ";";

    private static final String DOMAIN = "Domain";
    private static final String MAX_AGE = "Max-Age";
    private static final String PATH = "Path";
    private static final String SECURE = "Secure";
    private static final String HTTP_ONLY = "HttpOnly";

    private String key;

    private String value;

    private Map<String, String> attributes;

    public HttpCookie(String key, String value) {
        this.key = key;
        this.value = value;
        this.attributes = new HashMap<>();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Optional<String> getAttribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    public void setDomain(String domain) {
        this.attributes.put(DOMAIN, domain);
    }

    public void setMaxAge(int maxage) {
        this.attributes.put(MAX_AGE, String.valueOf(maxage));
    }

    public void setPath(String path) {
        this.attributes.put(PATH, path);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append(key).append(COOKIE_KEY_VALUE_DELIMITER)
                .append(value);

        if (attributes.isEmpty()) {
            return sb.toString();
        }

        for (String key : attributes.keySet()) {
            sb.append(ATTRIBUTE_DELIMITER);
            sb.append(" ");
            sb.append(key).append(COOKIE_KEY_VALUE_DELIMITER)
                    .append(attributes.get(key));
        }
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
        HttpCookie httpCookie = (HttpCookie) o;
        return Objects.equals(key, httpCookie.key) && Objects.equals(value, httpCookie.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
