package codesquad.application.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.TestUtil.get;

import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultHandlerTest {
    private static DefaultHandler defaultHandler;

    @BeforeAll
    static void beforeAll() {
        defaultHandler = new DefaultHandler();
    }

    @Test
    @DisplayName("파일이 있으면 파일을 응답에 쓴다")
    public void testDoGet_FileFound() {
        //given
        HttpRequest request = get("/index.html");
        HttpResponse response = new HttpResponse(request);

        //when
        defaultHandler.doGet(request, response);

        //then
        assertTrue(response.getHeaders().contains(HttpHeaders.CONTENT_TYPE_HEADER));
        assertTrue(response.getHeaders().contains(HttpHeaders.CONTENT_LENGTH_HEADER));
        assertTrue(response.getHeaders().contains(HttpHeaders.DATE_HEADER));
        assertTrue(response.getHeaders().contains(HttpHeaders.CONNECTION_HEADER));
        assertEquals(
                Integer.parseInt(response.getHeaders().getHeaderSingleValue(HttpHeaders.CONTENT_LENGTH_HEADER).get()),
                response.getOutputBytes().length);
    }

    @DisplayName("파일이 없을때 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"nonexistent.txt", "noextension"})
    public void testDoGet_FileNotFound(String path) {
        //given
        HttpRequest request = get(path);
        HttpResponse response = new HttpResponse(request);

        //when & then
        assertThrows(ResourceNotFoundException.class, () -> defaultHandler.doGet(request, response));
    }
}
