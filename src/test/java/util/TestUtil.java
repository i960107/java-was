package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpMethod;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import codesquad.was.server.ServerContext;
import java.util.List;
import java.util.Map;

public class TestUtil {

    public static HttpRequest get(String path) {
        return new HttpRequest(null, HttpMethod.GET, path, "HTTP1.1", "localhost", HttpHeaders.getDefault(), null, null,
                null);
    }

    public static HttpRequest get(ServerContext context, String path, HttpHeaders headers) {
        return new HttpRequest(context, HttpMethod.GET, path, "HTTP1.1", "localhost", headers, null, null, null);
    }

    public static HttpRequest post(String path, Map<String, List<String>> parameters, byte[] body) {
        return new HttpRequest(null, HttpMethod.POST, path, "HTTP1.1", "localhost", HttpHeaders.getDefault(), null,
                parameters,
                body);
    }

    public static HttpRequest post(ServerContext context, String path, Map<String, List<String>> parameters,
                                   byte[] body) {
        return new HttpRequest(context, HttpMethod.POST, path, "HTTP1.1", "localhost", HttpHeaders.getDefault(), null,
                parameters,
                body);
    }

    public static void assertRedirectResponse(HttpResponse response, String redirectUrl) {
        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertTrue(response.getHeaders().contains(HttpHeaders.LOCATION));
        assertEquals(redirectUrl, response.getHeaders().getHeaderSingleValue(HttpHeaders.LOCATION).get());
    }

    public static void assertErrorResponse(HttpResponse response, HttpStatus status) {
        assertEquals(status, response.getStatus());
        assertEquals(MimeTypes.html.getMIMEType(),
                response.getHeaders().getHeaderSingleValue(HttpHeaders.CONTENT_TYPE_HEADER).get());
        assertTrue(containsSubArray(response.getOutputBytes(), status.getCode().getBytes()));
        assertEquals(status, response.getStatus());
    }

    public static void assertResponse(HttpResponse response,
                                      HttpStatus status,
                                      HttpHeaders headers,
                                      byte[] body) {
        assertEquals(status, response.getStatus());
        assertEquals(headers, response.getHeaders());
        assertEquals(body, response.getOutputBytes());
    }

    public static boolean containsSubArray(byte[] source, byte[] target) {
        if (source == null || target == null || source.length == 0 || target.length == 0) {
            return false;
        }

        if (source.length < target.length) {
            return false;
        }

        outerLoop:
        for (int i = 0; i <= source.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    continue outerLoop;
                }
            }
            return true;
        }
        return false;
    }


}
