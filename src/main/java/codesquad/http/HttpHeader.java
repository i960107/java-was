package codesquad.http;

import codesquad.http.exception.HeaderSyntaxException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class HttpHeader {

    private static final String KEY_VALUE_DELIMITER = ":";

    private static final String VALUES_DELIMITER = ";";

    private String key;

    private Set<String> values;

    public HttpHeader(String key, Set<String> values) {
        validateKey(key);
        validateValues(values);
        this.key = key;
        this.values = values;
    }

    public static HttpHeader from(String headerLine) {
        String key = headerLine.substring(0, headerLine.indexOf(KEY_VALUE_DELIMITER));
        String[] valuesToken = headerLine
                .substring(headerLine.indexOf(KEY_VALUE_DELIMITER) + 1).strip()
                .split(VALUES_DELIMITER);

        Set<String> values = new HashSet<>();
        for (String value : valuesToken) {
            String trimmed = value.trim();
            values.add(trimmed);
        }

        return new HttpHeader(key, values);
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank() || key.strip().length() != key.length()) {
            throw new HeaderSyntaxException();
        }
    }

    private void validateValues(Set<String> values) throws HeaderSyntaxException {
        if (values == null || values.isEmpty()) {
            throw new HeaderSyntaxException();
        }
        boolean hasInValidValue = values.stream()
                .anyMatch(value -> value.strip().length() != value.length());

        if (hasInValidValue) {
            throw new HeaderSyntaxException();
        }
    }


    public String getKey() {
        return key;
    }

    public Set<String> getValues() {
        return values;
    }

    public Optional<String> getOnlyValue() {
        if (values.size() == 1) {
            return Optional.of(values.iterator().next());
        }
        return Optional.empty();
    }

    public boolean hasKey(String key) {
        return this.key.equals(key);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpHeader that = (HttpHeader) o;
        return Objects.equals(key, that.key) && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, values);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append(KEY_VALUE_DELIMITER).append(" ");

        int count = 0;
        int size = values.size();
        for (String s : values) {
            sb.append(s);
            if (++count < size) {
                sb.append(VALUES_DELIMITER);
            }
        }
        return sb.toString();
    }
}
