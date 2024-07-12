package codesquad.was.http;

import codesquad.was.http.exception.HeaderSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HttpHeader {

    private static final String HEADER_KEY_VALUE_DELIMITER = ":";

    private static final String HEADER_VALUES_DELIMITER = ",";

    private String key;

    private List<String> values;

    public HttpHeader(String key, List<String> values) {
        validateKey(key);
        validateValues(values);
        this.key = key;
        this.values = values;
    }

    public HttpHeader(String key, String value) {
        List<String> valuesList = Arrays.asList(value);
        validateKey(key);
        validateValues(valuesList);
        this.key = key;
        this.values = valuesList;
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank() || key.strip().length() != key.length()) {
            throw new HeaderSyntaxException();
        }
    }

    private void validateValues(List<String> values) throws HeaderSyntaxException {
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

    public List<String> getValues() {
        return values;
    }

    public Optional<String> getSingleValue() {
        if (values.size() == 1) {
            return Optional.of(values.iterator().next());
        }
        return Optional.empty();
    }

    public boolean hasKey(String key) {
        return this.key.equalsIgnoreCase(key);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append(HEADER_KEY_VALUE_DELIMITER).append(" ");

        int count = 0;
        int size = values.size();
        for (String s : values) {
            sb.append(s);
            if (++count < size) {
                sb.append(HEADER_VALUES_DELIMITER);
                sb.append(" ");
            }
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
        HttpHeader that = (HttpHeader) o;
        return Objects.equals(key, that.key) && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, values);
    }
}
