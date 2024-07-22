package codesquad.was.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.was.http.exception.HttpProtocolException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("http 요청을 파싱한다.")
class HttpRequestParserTest {

    @DisplayName("- GET query string 포함")
    @Test
    void testParseRequestGETQueryString() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("user-create-request.txt");

        //when
        HttpRequest request = new HttpRequest(null);
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

    @DisplayName("- multi value parameter")
    @Test
    void testParseMultiValueParameter() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("search-querystring-multivalue.txt");

        //when
        HttpRequest request = new HttpRequest(null);
        HttpRequestParser.parse(request, input);

        //then
        Map<String, List<String>> expectedParameters = new HashMap<>();
        expectedParameters.put("query", List.of("novel", "reading"));
        expectedParameters.put("size", List.of("10"));
        assertRequestParameter(request, expectedParameters);
    }

    private void assertRequestLine(HttpRequest request,
                                   String expectedProtocol,
                                   String expectedPath,
                                   HttpMethod expectedMethod) {
        assertEquals(expectedProtocol, request.getProtocol());
        assertEquals(expectedPath, request.getPath());
        assertEquals(expectedMethod, request.getMethod());
    }

    private void assertHeader(HttpRequest request,
                              String expectedHeaderKey,
                              String expectedHeaderValue) {
        assertTrue(request.getHeader(expectedHeaderKey).isPresent());
        assertEquals(expectedHeaderValue, request.getHeader(expectedHeaderKey).get().getSingleValue().get());
    }

    private void assertRequestParameter(HttpRequest request,
                                        Map<String, List<String>> expectedParameters) {
        assertEquals(expectedParameters.size(), request.getParameterMap().size());
        for (String key : expectedParameters.keySet()) {
            List<String> actual = request.getParameterMap().get(key);
            assertThat(actual)
                    .containsExactlyInAnyOrderElementsOf(expectedParameters.get(key));
        }
    }

    @DisplayName("- POST formdata")
    @Test
    void testParseRequestPOSTFormData() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("user-create-form-data.txt");

        //when
        HttpRequest request = new HttpRequest(null);
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

    @DisplayName("- POST multipart form data")
    @Test
    void testParseRequestPOSTMultipartFormData() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("multipart-form-data.txt");
        HttpRequest request = new HttpRequest(null);

        //when
        HttpRequestParser.parse(request, input);

        //then
        assertRequestLine(request, "HTTP/1.1", "/upload", HttpMethod.POST);

        assertEquals(3, request.getHeaders().size());
        assertHeader(request, HttpHeaders.CONTENT_TYPE_HEADER,
                "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        assertHeader(request, HttpHeaders.HOST_HEADER, "example.com");
        assertHeader(request, HttpHeaders.CONTENT_LENGTH_HEADER, "346");

        assertThat(request.getParts())
                .hasSize(2);

        Part file = new Part(
                "profile_picture",
                MimeType.jpg,
                "profile.jpg",
                "binary content of profile.jpg".getBytes());
        Part usernamePart = new Part(
                "username",
                MimeType.text,
                null,
                "john_doe".getBytes()
        );
        assertEquals(usernamePart.getName(), request.getParts().get(0).getName());
        assertEquals(usernamePart.getContentType(), request.getParts().get(0).getContentType());
        assertEquals(usernamePart.getFileName(), request.getParts().get(0).getFileName());
        assertTrue(Arrays.equals(usernamePart.getContent(), request.getParts().get(0).getContent()));
        assertEquals(file.getName(), request.getParts().get(1).getName());
        assertEquals(file.getContentType(), request.getParts().get(1).getContentType());
        assertEquals(file.getFileName(), request.getParts().get(1).getFileName());
        assertTrue(Arrays.equals(file.getContent(), request.getParts().get(1).getContent()));
    }

    @DisplayName("- cookies")
    @Test
    void testParseCookies() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("request-with-cookie.txt");

        //when
        HttpRequest request = new HttpRequest(null);
        HttpRequestParser.parse(request, input);

        //then
        HttpCookie[] expectedCookies = new HttpCookie[]{
                new HttpCookie("AEC", "AVYB7cqQ9dN96UvMh5KTUbIObzmkbh5k51me9pLSomAQrxdjATp8nkhHwj8"),
                new HttpCookie("NID",
                        "515=UTcxX4Jmphp_IUN3RkUSOeqetoRzcgE6j_qh9WmOo7N0h3ofZfFKxgNn7XNf_nHLDXu478Lg5naOaQynLID7ID_4w4GCBkdT0DGeZqV7cPzayq7apAxOWURKJ4BlWnDS6U3q17pch6PqVUe7vdK7t9y7-QnjufEdi5LqsRwXu5h5hXajlrvHcdc"),
                new HttpCookie("OGPC", "19037049-1:"),
                new HttpCookie("OGP", "-19037049:")
        };
        assertThat(request.getCookies())
                .hasSize(expectedCookies.length)
                .containsExactly(expectedCookies);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-request-1-nohost.txt",
            "invalid-request-2-noblankline.txt",
            "invalid-request-3-nohttpmethod.txt"})
    @DisplayName("http 프로토콜을 지키지 않은 경우 예외를 던진다. : {0}")
    void testThrowsExceptionWhenParseInvalidRequest(String fileName) {
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        HttpRequest request = new HttpRequest(null);
        assertThrows(HttpProtocolException.class, () -> HttpRequestParser.parse(request, input));
    }
}
