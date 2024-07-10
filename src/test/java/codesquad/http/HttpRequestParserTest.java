package codesquad.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.http.exception.HttpProtocolException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestParserTest {

    @DisplayName("http 요청을 파싱한다.- GET query string 포함")
    @Test
    void testParseRequestGETQueryString() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("user-create-request.txt");

        //when
        WasRequest request = new WasRequest();
        HttpRequestParser.parse(request, input);

        //then
        assertRequestLine(request, "HTTP/1.1", "/user/create", HttpMethod.GET);

        assertEquals(3, request.getHeaders().size());
        assertHeader(request, HttpHeaders.CONNECTION_HEADER, "keep-alive");
        assertHeader(request, HttpHeaders.HOST_HEADER, "localhost:8080");
        assertHeader(request, HttpHeaders.ACCEPT, "*/*");

        Map<String, List<String>> expectedParameters = new HashMap<>();
        expectedParameters.put("password", List.of("password"));
        expectedParameters.put("name", List.of("박재성"));
        expectedParameters.put("email", List.of("javajigi@slipp.net"));
        expectedParameters.put("userId", List.of("javajigi"));
        assertRequestParameter(request, expectedParameters);
    }

    private static void assertRequestLine(WasRequest request,
                                          String expectedProtocol,
                                          String expectedPath,
                                          HttpMethod expectedMethod) {
        assertEquals(expectedProtocol, request.getProtocol());
        assertEquals(expectedPath, request.getPath());
        assertEquals(expectedMethod, request.getMethod());
    }

    private static void assertHeader(WasRequest request,
                                     String expectedHeaderKey,
                                     String expectedHeaderValue) {
        assertTrue(request.getHeader(expectedHeaderKey).isPresent());
        assertEquals(expectedHeaderValue, request.getHeader(expectedHeaderKey).get().getSingleValue().get());
    }

    private static void assertRequestParameter(WasRequest request,
                                               Map<String, List<String>> expectedParameters) {
        assertEquals(expectedParameters.size(), request.getParameterMap().size());
        assertThat(request.getParameterMap())
                .hasSize(expectedParameters.size())
                .isEqualTo(expectedParameters);
    }

    @DisplayName("http 요청을 파싱한다. - POST formdata")
    @Test
    void testParseRequestPOSTFormData() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("user-create-form-data.txt");

        //when
        WasRequest request = new WasRequest();
        HttpRequestParser.parse(request, input);

        //then
        assertRequestLine(request, "HTTP/1.1", "/create", HttpMethod.POST);

        assertEquals(3, request.getHeaders().size());
        assertHeader(request, HttpHeaders.CONTENT_TYPE_HEADER, "application/x-www-form-urlencoded");
        assertHeader(request, HttpHeaders.HOST_HEADER, "localhost:8080");
        assertHeader(request, HttpHeaders.CONTENT_LENGTH_HEADER, "63");

        Map<String, List<String>> expectedParameters = new HashMap<>();
        expectedParameters.put("username", List.of("iwer"));
        expectedParameters.put("nickname", List.of("박재성"));
        expectedParameters.put("password", List.of("123"));
        assertRequestParameter(request, expectedParameters);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-request-1-nohost.txt",
            "invalid-request-2-noblankline.txt",
            "invalid-request-3-nohttpmethod.txt"})
    @DisplayName("http 프로토콜을 지키지 않은 경우 예외를 던진다. : {0}")
    void testThrowsExceptionWhenParseInvalidRequest(String fileName) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        WasRequest request = new WasRequest();
        assertThrows(HttpProtocolException.class, () -> HttpRequestParser.parse(request, input));
    }
}
