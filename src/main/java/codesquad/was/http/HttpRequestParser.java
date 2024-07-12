package codesquad.was.http;

import static codesquad.was.util.IOUtil.readLine;
import static codesquad.was.util.IOUtil.readToSize;

import codesquad.was.http.exception.HeaderSyntaxException;
import codesquad.was.http.exception.HttpProtocolException;
import codesquad.was.http.exception.NotSupportedHttpMethodException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 메서드 호출 순서에 따라서 파싱 결과 달라짐 주의!
public final class HttpRequestParser {

    private static final String HEADER_KEY_VALUE_DELIMITER = ":";

    private static final String HEADER_VALUES_DELIMITER = ",";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String COOKIES_DELIMITER = ";";

    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";

    public static void parse(HttpRequest request, InputStream input) throws IOException {
        //--- request line
        String requestLine = parseRequestLine(input);

        String[] requestLineParts = splitRequestLine(requestLine);

        HttpMethod method = getHttpMethod(requestLineParts[0]);
        request.setMethod(method);

        String protocol = requestLineParts[2];
        request.setProtocol(protocol);

        String pathWithQueryString = requestLineParts[1];
        String path = getPath(pathWithQueryString);
        request.setPath(path);

        Map<String, List<String>> queryPairs = parseQueryString(pathWithQueryString);
        if (!queryPairs.isEmpty()) {
            request.addParameters(queryPairs);
        }

        //--- header
        HttpHeaders headers = parseHeaders(input);
        setRequestHeaders(request, headers);

        //--- body (Content-Length 헤더 있을때만)
        Optional<String> headerSingleValue = headers.getHeaderSingleValue(HttpHeaders.CONTENT_LENGTH_HEADER);
        if (headerSingleValue.isEmpty()) {
            return;
        }

        int contentLength = Integer.parseInt(headerSingleValue.get());
        byte[] body = parseBody(input, contentLength);
        setRequestBody(request, body);
    }

    private static HttpMethod getHttpMethod(String parsedMethod) {
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(parsedMethod);
        } catch (IllegalArgumentException e) {
            throw new NotSupportedHttpMethodException();
        }
        return method;
    }


    private static String parseRequestLine(InputStream input) throws IOException {
        String requestLine = readLine(input);
        if (requestLine == null || requestLine.isEmpty()) {
            throw new HttpProtocolException("empty request line");
        }
        return requestLine;
    }

    private static String[] splitRequestLine(String requestLine) {
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new HttpProtocolException("invalid request line");
        }
        return parts;
    }

    private static String getPath(String pathWithQueryString) {
        int index = pathWithQueryString.indexOf("?");
        String path;
        if (index == -1) {
            path = pathWithQueryString;
        } else {
            path = pathWithQueryString.substring(0, index);
        }
        return path;
    }

    private static Map<String, List<String>> parseQueryString(String pathWithQueryString) {
        int index = pathWithQueryString.indexOf("?");
        if (index == -1 || index + 1 > pathWithQueryString.length()) {
            return Map.of();
        }

        return parseRequestParameters(pathWithQueryString.substring(index + 1));
    }

    private static Map<String, List<String>> parseRequestParameters(String parameterString) {
        Map<String, List<String>> parameters = new HashMap<>();
        String[] pairs = parameterString.split("&");
        try {
            for (String pair : pairs) {
                int separatorIndex = pair.indexOf("=");
                if (separatorIndex != -1 && separatorIndex + 1 < pair.length()) {
                    String key = URLDecoder.decode(pair.substring(0, separatorIndex), DEFAULT_CHARSET);
                    String value = URLDecoder.decode(pair.substring(separatorIndex + 1), DEFAULT_CHARSET);
                    if (!parameters.containsKey(key)) {
                        parameters.put(key, new ArrayList<>());
                    }
                    parameters.get(key).add(value);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //todo
        }
        return parameters;
    }

    private static HttpHeaders parseHeaders(InputStream input) throws IOException {
        String headerLine;
        HttpHeaders headers = new HttpHeaders();
        try {
            while (!(headerLine = readLine(input)).isEmpty()) {
                HttpHeader header = parseHeader(headerLine);
                headers.setHeader(header);
            }
        } catch (NullPointerException e) {
            throw new HttpProtocolException("blank line is required after headers");
        }
        return headers;
    }

    public static HttpHeader parseHeader(String headerLine) {
        String key = headerLine.substring(0, headerLine.indexOf(HEADER_KEY_VALUE_DELIMITER));
        String[] valuesToken = headerLine
                .substring(headerLine.indexOf(HEADER_KEY_VALUE_DELIMITER) + 1).strip()
                .split(HEADER_VALUES_DELIMITER);

        List<String> values = new ArrayList<>();
        for (String value : valuesToken) {
            String trimmed = value.trim();
            values.add(trimmed);
        }

        return new HttpHeader(key, values);
    }

    private static void setRequestHeaders(HttpRequest request, HttpHeaders headers) {

        validateHeader(headers);

        request.setHeaders(headers);

        String host = headers.getHeaderSingleValue(HttpHeaders.HOST_HEADER).get();
        request.setHost(host);

        headers.getHeaderSingleValue(HttpHeaders.CONTENT_TYPE_HEADER)
                .ifPresent(value -> {
                    MimeTypes contentType = MimeTypes.getMimeTypeFromContentType(value);
                    request.setContentType(contentType);
                });

        List<HttpCookie> cookies = parseCookie(headers);
        request.setCookies(cookies);
    }


    private static void validateHeader(HttpHeaders headers) {
        boolean isValid = hasSingleValue(headers, HttpHeaders.HOST_HEADER);

        Optional<String> contentType = headers.getHeaderSingleValue(HttpHeaders.CONTENT_TYPE_HEADER);
        if (contentType.isPresent()) {
            MimeTypes type = MimeTypes.getMimeTypeFromContentType(contentType.get());
            if (isFormData(type) && !hasSingleValue(headers, HttpHeaders.CONTENT_LENGTH_HEADER)) {
                isValid = false;
            }
        }

        if (!isValid) {
            throw new HeaderSyntaxException();
        }

    }

    private static List<HttpCookie> parseCookie(HttpHeaders headers) {
        List<HttpCookie> cookies = new ArrayList<>();

        Optional<String> headerSingleValue = headers.getHeaderSingleValue(HttpHeaders.COOKIE);
        if (headerSingleValue.isEmpty()) {
            return cookies;
        }

        String[] tokens = headerSingleValue.get().split(COOKIES_DELIMITER);
        for (String token : tokens) {
            int index = token.indexOf(COOKIE_KEY_VALUE_DELIMITER);
            String key = token.substring(0, index).strip();
            String value = token.substring(index + 1).strip();
            cookies.add(new HttpCookie(key, value));
        }
        return cookies;
    }

    private static boolean hasSingleValue(HttpHeaders headers, String key) {
        return headers.getHeaderSingleValue(key).isPresent();
    }

    private static void setRequestBody(HttpRequest request, byte[] body) throws IOException {
        if (isFormData(request.getContentType())) {
            String formData = new String(body, DEFAULT_CHARSET);
            Map<String, List<String>> parameters = parseRequestParameters(formData);
            request.addParameters(parameters);
        } else {
            request.setBody(body);
        }
    }

    private static boolean isFormData(MimeTypes contentType) {
        return contentType != null &&
                contentType.equals(MimeTypes.form_data);
    }

    private static byte[] parseBody(InputStream input, int contentLength) {
        try {
            return readToSize(input, contentLength);
        } catch (IOException exception) {
            throw new HttpProtocolException("incomplete body read");
        }
    }
}
