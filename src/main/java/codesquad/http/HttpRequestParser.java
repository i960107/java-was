package codesquad.http;

import static codesquad.IOUtil.readLine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public static Request parse(InputStream input) throws IOException {
        String requestLine = parseRequestLine(input);
        String[] requestLineParts = splitRequestLine(requestLine);
        String method = requestLineParts[0];
        String path = requestLineParts[1];
        String protocol = requestLineParts[2];

        HttpHeader header = parseHeaders(input);
        if (!header.containsKey(HttpHeader.HOST_HEADER)) {
            throw new IOException("host header not exist");
        }

        String host = header.getHeader(HttpHeader.HOST_HEADER)
                .orElseThrow(() -> new IOException("host header not exist"));

        Map<String, String> parameters = parseParameters(input);

        byte[] body = null;
        if (header.containsKey(HttpHeader.CONTENT_LENGTH_HEADER)) {
            int contentLength = Integer.parseInt(header.getHeader(HttpHeader.CONTENT_LENGTH_HEADER).get());
            body = parseBody(input, contentLength);
        }

        return new WasRequest(
                method,
                path,
                protocol,
                host,
                header,
                parameters,
                body
        );
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

    private static HttpHeader parseHeaders(InputStream input) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = readLine(input)).isEmpty()) {
            String[] headerParts = headerLine.split(": ");
            if (headerParts.length != 2) {
                throw new IOException("invalid header line");
            }
            headers.put(headerParts[0], headerParts[1]);
        }
        return new HttpHeader(headers);
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

}
