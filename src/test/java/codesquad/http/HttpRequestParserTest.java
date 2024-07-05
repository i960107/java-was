package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.http.exception.HttpProtocolException;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestParserTest {

    @DisplayName("http 요청을 파싱한다.")
    @Test
    void testParseRequest() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("user-create-request.txt");
        WasRequest request = new WasRequest();
        HttpRequestParser.parse(request, input);

        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals("localhost:8080", request.getHost());
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals(4, request.getQueryString().size());
        assertTrue(request.getQueryString().containsKey("password"));
        assertTrue(request.getQueryString().containsKey("name"));
        assertTrue(request.getQueryString().containsKey("email"));
        assertTrue(request.getQueryString().containsKey("userId"));
        assertEquals("password", request.getQueryString().get("password"));
        assertEquals("javajigi", request.getQueryString().get("userId"));
        assertEquals("박재성", request.getQueryString().get("name"));
        assertEquals("javajigi@slipp.net", request.getQueryString().get("email"));
        assertEquals(3, request.getHeaders().getHeaders().size());
        assertEquals("keep-alive", request.getHeader(HttpHeaders.CONNECTION_HEADER).get().getOnlyValue().get());
        assertEquals("*/*", request.getHeader(HttpHeaders.ACCEPT).get().getOnlyValue().get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-request-1.txt", "invalid-request-2.txt", "invalid-request-3.txt"})
    @DisplayName("http 프로토콜을 지키지 않은 경우 예외를 던진다.")
    void testThrowsExceptionWhenParseInvalidRequest(String fileName) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        WasRequest request = new WasRequest();
        assertThrows(HttpProtocolException.class, () -> HttpRequestParser.parse(request, input));
    }
}
