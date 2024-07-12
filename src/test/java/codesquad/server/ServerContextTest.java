package codesquad.server;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.TestUtiㅣ.get;
import static util.TestUtiㅣ.post;

import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import codesquad.was.server.Handler;
import codesquad.was.server.ServerContext;
import codesquad.was.server.authenticator.Authenticator;
import codesquad.was.server.authenticator.Principal;
import codesquad.was.server.authenticator.Role;
import codesquad.was.server.exception.AuthenticationException;
import codesquad.was.server.session.InMemorySessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServerContextTest {
    private static String testUsername = "testuser";
    private static String testPassword = "testpassword1234";
    private ServerContext context;

    @BeforeEach
    public void setUp() {
        Authenticator testAuthenticator = new Authenticator() {
            @Override
            public Principal authenticate(HttpRequest request) {
                String username = request.getParameter("username")
                        .orElseThrow(AuthenticationException::new);
                String password = request.getParameter("password")
                        .orElseThrow(AuthenticationException::new);
                if (username.equals(testUsername) && password.equals(testPassword)) {
                    throw new AuthenticationException();
                }
                return new Principal(username, Role.USER);
            }
        };
        context = new ServerContext(
                new InMemorySessionManager(),
                testAuthenticator
        );

        context.addHandler("/test", new Handler() {
            @Override
            public void doPost(HttpRequest request, HttpResponse response) {
                response.send(HttpHeaders.empty(), "doPost".getBytes());
            }

            @Override
            public void doGet(HttpRequest request, HttpResponse response) {
                response.send(HttpHeaders.empty(), "doGet".getBytes());
            }
        });

        context.addHandler("/", new Handler() {
            @Override
            public void doGet(HttpRequest request, HttpResponse response) {
                response.send(HttpHeaders.empty(), "default".getBytes());
            }
        });
    }

    @DisplayName("요청 Http Method에 따라 doMethod()가 호출된다.")
    @Test
    void testCallHandlerMethod() {
        //given
        HttpRequest getRequest = get("/test");
        HttpResponse getResponse = new HttpResponse(getRequest);

        //when
        context.handle(getRequest, getResponse);

        //then
        assertArrayEquals("doGet".getBytes(), getResponse.getOutputBytes());

        //given
        HttpRequest postRequest = post("/test", null, null);
        HttpResponse postResponse = new HttpResponse(postRequest);

        //when
        context.handle(postRequest, postResponse);

        //then
        assertArrayEquals("doPost".getBytes(), postResponse.getOutputBytes());
    }

    @DisplayName("기본 경로로 등록된 핸들러가 있다면 등록되지 않은 경로 요청시 기본 경로 핸들러가 요청된다.")
    @Test
    void testDefaultPathHandlerHandlesUnregisteredRequest() {
        //given
        HttpRequest request = get("/login_failed.html");
        HttpResponse response = new HttpResponse(request);

        //when
        context.handle(request, response);

        //then
        assertArrayEquals("default".getBytes(), response.getOutputBytes());
    }

    @DisplayName("기본 경로로 등록된 핸들러가 없다면 등록되지 않은 경로 요청시 404 응답을 반환한다.")
    @Test
    void testUnregisteredPathRequest() {
        //given
        ServerContext emptyHandlerServerContext = new ServerContext(null, null);
        HttpRequest request = get("/create-user");
        HttpResponse response = new HttpResponse(request);

        //when
        emptyHandlerServerContext.handle(request, response);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @DisplayName("지원하지 않는 메소드로 요청시 405 응답을 반환한다.")
    @Test
    void testMethodNotAllowedRequestReturn405() {
        //given
        HttpRequest request = post("/", null, null);
        HttpResponse response = new HttpResponse(request);

        //when
        context.handle(request, response);

        //then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatus());
    }

    @DisplayName("예외 발생시 500 응답을 반환한다.")
    @Test
    void testExceptionReturn500() {
        //given
        ServerContext errorContext = new ServerContext(null, null);
        errorContext.addHandler("/error", new Handler() {
            @Override
            public void doGet(HttpRequest request, HttpResponse response) {
                throw new RuntimeException();
            }
        });

        HttpRequest request = get("/error");
        HttpResponse response = new HttpResponse(request);

        //when
        errorContext.handle(request, response);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
    }

}
