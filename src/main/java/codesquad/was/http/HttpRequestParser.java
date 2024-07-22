package codesquad.was.http;

import static codesquad.was.util.IOUtil.CR;
import static codesquad.was.util.IOUtil.LF;
import static codesquad.was.util.IOUtil.endWithCRLF;
import static codesquad.was.util.IOUtil.readLine;
import static codesquad.was.util.IOUtil.readToSize;

import codesquad.was.http.exception.HeaderSyntaxException;
import codesquad.was.http.exception.HttpProtocolException;
import codesquad.was.http.exception.NotSupportedHttpMethodException;
import codesquad.was.util.ByteArrayUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 메서드 호출 순서에 따라서 파싱 결과 달라짐 주의!
public final class HttpRequestParser {

    private static final String HEADER_KEY_VALUE_DELIMITER = ":";

    private static final String HEADER_VALUES_DELIMITER = ",";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String COOKIES_DELIMITER = ";";

    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";

    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

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

        //--- body (Content-Length 헤더 있을때만 파싱, content type에 따라서 파싱 방법이 다름)
        Optional<String> headerSingleValue = headers.getHeaderSingleValue(HttpHeaders.CONTENT_LENGTH_HEADER);
        if (headerSingleValue.isEmpty()) {
            return;
        }

        int contentLength = Integer.parseInt(headerSingleValue.get());

        if (isFormData(request.getContentType())) {
            String formData = new String(parseBody(input, contentLength), DEFAULT_CHARSET);
            Map<String, List<String>> parameters = parseRequestParameters(formData);
            request.addParameters(parameters);
        } else if (isMultiPartData(request.getContentType())) {
            List<Part> parts = parseMultiPart(getBoundary(headers), input, contentLength);
            request.setParts(parts);
        } else {
            byte[] body = parseBody(input, contentLength);
            request.setBody(body);
        }
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
                    MimeType contentType = MimeType.getMimeTypeFromContentType(value);
                    request.setContentType(contentType);
                });

        List<HttpCookie> cookies = parseCookie(headers);
        request.setCookies(cookies);
    }


    private static void validateHeader(HttpHeaders headers) {
        boolean isValid = hasSingleValue(headers, HttpHeaders.HOST_HEADER);

        Optional<String> contentType = headers.getHeaderSingleValue(HttpHeaders.CONTENT_TYPE_HEADER);
        if (contentType.isPresent()) {
            MimeType type = MimeType.getMimeTypeFromContentType(contentType.get());
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

    private static boolean isFormData(MimeType contentType) {
        return contentType != null &&
                contentType.equals(MimeType.form_data);
    }

    private static boolean isMultiPartData(MimeType contentType) {
        return contentType != null &&
                contentType.equals(MimeType.multipart_fom_data);
    }

    private static byte[] parseBody(InputStream input, int contentLength) {
        try {
            return readToSize(input, contentLength);
        } catch (IOException exception) {
            throw new HttpProtocolException("incomplete body read");
        }
    }

    private static String getBoundary(HttpHeaders headers) {
        return headers.getHeaderSingleValue(HttpHeaders.CONTENT_TYPE_HEADER)
                .map(value -> value.substring(value.indexOf("boundary=") + 9))
                .orElseThrow(() -> new HttpProtocolException("boundary not found"));
    }

    private static List<Part> parseMultiPart(String boundary, InputStream input, int contentLength) {

        byte[] boundaryBytes = ("--" + boundary + CR + LF).getBytes();
        byte[] endBoundaryBytes = ("--" + boundary + "--" + CR + LF).getBytes();

        List<Part> parts = new ArrayList<>();

        try {
            byte[] inputBytes = input.readNBytes(contentLength);
            int start = ByteArrayUtil.indexOf(inputBytes, boundaryBytes, 0);
            if (start != 0) {
                throw new HttpProtocolException("start boundary not found");
            }
            int nxt = 0;
            // inputBytes[start:nxt]까지 현재 파트에 해당
            while (true) {
                nxt = ByteArrayUtil.indexOf(inputBytes, boundaryBytes, start + boundaryBytes.length);

                if (nxt == -1) {
                    nxt = ByteArrayUtil.indexOf(inputBytes, endBoundaryBytes, start + boundaryBytes.length);
                }
                if (nxt == -1) {
                    break;
                }
                //part
                byte[] partBytes = Arrays.copyOfRange(inputBytes, start + boundaryBytes.length, nxt);

                //part - header(headerline 뒤 공백까지 읽음)
                HttpHeaders headers = new HttpHeaders();
                int lineStart = 0;
                for (int i = 0; i < partBytes.length; i++) {
                    if (i < partBytes.length - 1 && partBytes[i] == CR && partBytes[i + 1] == LF) {
                        byte[] line = Arrays.copyOfRange(partBytes, lineStart, i);
                        if (line.length == 0) {
                            lineStart = i + 2;
                            break;
                        }
                        HttpHeader httpHeader = parseHeader(new String(line));
                        headers.setHeader(httpHeader);
                        lineStart = i + 2;
                        i++;
                    }
                }

                //part - body
                if (!endWithCRLF(partBytes)) {
                    throw new HttpProtocolException("part should end with crlf");
                }
                byte[] content = Arrays.copyOfRange(partBytes, lineStart, partBytes.length - 2);
                parts.add(Part.from(headers, content));

                //다음 part부터 시작
                start = nxt;
            }

            if (!Arrays.equals(
                    inputBytes, start, start + endBoundaryBytes.length,
                    endBoundaryBytes, 0, endBoundaryBytes.length)) {
                throw new IllegalArgumentException("unable to find end boundary");
            }
            return parts;
        } catch (Exception e) {
            log.warn("fail to read parts : {}", e.getMessage());
            throw new HttpProtocolException("fail to parse parts of multipart request");
        }
    }

}
