package codesquad.http;

import static codesquad.util.IOUtil.readLine;

import codesquad.http.exception.HttpProtocolException;
import codesquad.http.exception.NotSupportedHttpMethodException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// 메서드 호출 순서에 따라서 파싱 결과 달라짐 주의!
public class HttpRequestParser {
    private final static String CHARSET = "UTF-8";

    public static void parse(WasRequest request, InputStream input) throws IOException {
        String requestLine = parseRequestLine(input);

        String[] requestLineParts = splitRequestLine(requestLine);

        HttpMethod method;
        try {
            method = HttpMethod.valueOf(requestLineParts[0]);
        } catch (IllegalArgumentException e) {
            throw new NotSupportedHttpMethodException();
        }
        request.setMethod(method);

        String protocol = requestLineParts[2];
        request.setProtocol(protocol);

        String pathWithQueryString = URLDecoder.decode(requestLineParts[1], CHARSET);
        String path = getPath(pathWithQueryString);
        request.setPath(path);

        Map<String, String> queryPairs = getQueryPairs(pathWithQueryString);
        request.setQueryString(queryPairs);

        HttpHeaders headers = parseHeaders(input);

        String host = getHeaderValue(headers, HttpHeaders.HOST_HEADER);
        request.setHost(host);

        Map<String, String> parameters = parseParameters(input);
        request.setParameters(parameters);

        byte[] body = null;

        if (headers.contains(HttpHeaders.CONTENT_LENGTH_HEADER)) {
            int contentLength = Integer.parseInt(getHeaderValue(headers, HttpHeaders.CONTENT_LENGTH_HEADER));
            body = parseBody(input, contentLength);
        }
        request.setBody(body);
    }

    private static String parseRequestLine(InputStream input) throws IOException {
        String requestLine = readLine(input);
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("empty request line");
        }
        return requestLine;
    }

    private static String[] splitRequestLine(String requestLine) throws IOException {
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("invalid request line");
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

    private static Map<String, String> getQueryPairs(String pathWithQueryString) {
        Map<String, String> queryPairs = new HashMap<>();

        int index = pathWithQueryString.indexOf("?");
        if (index == -1 || index + 1 > pathWithQueryString.length()) {
            return queryPairs;
        }

        String queryString = pathWithQueryString.substring(index + 1);
        String[] pairs = queryString.split("&");
        Arrays.stream(pairs)
                .forEach(pair -> {
                    int separatorIndex = pair.indexOf("=");
                    if (separatorIndex != -1 && separatorIndex + 1 < pair.length()) {
                        String key = pair.substring(0, separatorIndex);
                        String value = pair.substring(separatorIndex + 1);
                        queryPairs.put(key, value);
                    }
                });
        return queryPairs;
    }

    private static HttpHeaders parseHeaders(InputStream input) throws IOException {
        String headerLine;
        HttpHeaders headers = new HttpHeaders();
        while (!(headerLine = readLine(input)).isEmpty()) {
            HttpHeader header = HttpHeader.from(headerLine);
            headers.setHeader(header);
        }
        return headers;
    }

    private static Map<String, String> parseParameters(InputStream input) throws IOException {
        //todo parameter 있는 경우 parameter로 파싱 필요
        Map<String, String> parameters = new HashMap<>();
        return parameters;
    }

    private static byte[] parseBody(InputStream input, int contentLength) throws IOException {
        byte[] body = new byte[contentLength];
        int read = input.read(body, 0, contentLength);
        if (read != contentLength || input.available() > 0) {
            throw new IOException("incomplete body read");
        }
        return body;
    }

    private static String getHeaderValue(HttpHeaders headers, String key) {
        return headers.getHeader(HttpHeaders.HOST_HEADER)
                .orElseThrow(() -> new HttpProtocolException(key + " header not exist"))
                .getOnlyValue()
                .orElseThrow(() -> new HttpProtocolException(key + " header has multi value"));
    }
}
