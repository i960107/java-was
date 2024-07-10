package codesquad.server;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codesquad.db.UserRepository;
import codesquad.http.HttpHeaders;
import codesquad.http.HttpMethod;
import codesquad.http.WasRequest;
import codesquad.processor.UserRequestProcessor;
import codesquad.server.exception.NoMatchHandlerException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerContextTest {
    private HandlerContext handlerContext;

    @BeforeEach
    public void setUp() {
        handlerContext = HandlerContext.getInstance();
        handlerContext.addHandler(new DefaultHandler());
        handlerContext.addHandler(new UrlMappingHandler(
                Map.of(
                        "/create", new UserRequestProcessor(UserRepository.getInstance())
                )));
        this.handlerContext = HandlerContext.getInstance();
    }

    private WasRequest get(String path) {
        return new WasRequest(HttpMethod.GET, path, "HTTP1.1", "localhost", HttpHeaders.getDefault(), null, null);
    }

    private WasRequest post(String path) {
        return new WasRequest(HttpMethod.POST, path, "HTTP1.1", "localhost", HttpHeaders.getDefault(), null, null);
    }

    @DisplayName("정적 리소스에 대한 요청시 DefaultHandler가 처리한다.")
    @Test
    void testStaticResourceRequest() {
        WasRequest wasRequest = get("/index.html");

        Handler handler = handlerContext.getMappedHandler(wasRequest);
        assertEquals(handler.getClass(), DefaultHandler.class);
    }

    @DisplayName("어플리케이션 요청시 UrlMappingHandler가 처리한다.")
    @Test
    void testApplicationRequest() {

        WasRequest wasRequest = post("/create");

        Handler handler = handlerContext.getMappedHandler(wasRequest);

        assertEquals(handler.getClass(), UrlMappingHandler.class);
    }

    @DisplayName("등록되지 않은 경로로 요청시 예외가 발생한다.")
    @Test
    void testApplicationRequestToUnregisteredPath() {

        WasRequest wasRequest = get("/create-user");

        assertThrows(NoMatchHandlerException.class, () -> handlerContext.getMappedHandler(wasRequest));
    }

}
