package codesquad.was.server;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.application.db.DBConfig;
import codesquad.application.db.JdbcTemplate;
import codesquad.application.db.PostDao;
import codesquad.application.db.UserDao;
import codesquad.application.handler.IndexHandler;
import codesquad.application.handler.PostWriteHandler;
import codesquad.application.handler.UserListHandler;
import codesquad.application.handler.UserLoginHandler;
import codesquad.application.handler.UserLogoutHandler;
import codesquad.application.handler.UserRegisterHandler;
import codesquad.application.model.User;
import codesquad.was.http.HttpHeader;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpProtocol;
import codesquad.was.http.HttpRequest;
import codesquad.was.server.authenticator.Authenticator;
import codesquad.was.server.authenticator.DefaultAuthenticator;
import codesquad.was.server.session.InMemorySessionManager;
import codesquad.was.server.session.Session;
import codesquad.was.server.session.SessionManager;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.TestUtil;

@DisplayName("통합 테스트 - 서버 랜덤 포트 실행")
class IntegrationTest {

    private static HttpProtocol server;

    private static ServerContext context;

    private static SessionManager sessionManager;

    private static Authenticator authenticator;

    private static PostDao postDao;

    private static int port;

    private static User user;

    private static String sid;

    @BeforeAll
    public static void beforeAll() throws IOException {
        context = new ServerContext();
        // 1. session manager 설정
        sessionManager = new InMemorySessionManager();
        context.setSessionManager(sessionManager);

        // 2. connection pool 설정
        DBConfig dbConfig = DBConfig.getDBConfig("application.properties");
        context.setConnectionPool(dbConfig);

        // 3. dao 객체 생성
        JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getConnectionPool());
        UserDao userDao = new UserDao(jdbcTemplate);
        postDao = new PostDao(jdbcTemplate);

        // 4. authenticator 설정
        context.setAuthenticator(new DefaultAuthenticator(userDao));

        // 5. handler 등록
        context.addHandler("/create", new UserRegisterHandler(userDao));
        context.addHandler("/login", new UserLoginHandler());
        context.addHandler("/logout", new UserLogoutHandler());
        context.addHandler("/user/list", new UserListHandler(userDao));
        context.addHandler("/index.html", new IndexHandler(postDao));
        context.addHandler("/post", new PostWriteHandler(postDao));
        context.addHandler("/", new DefaultHandler());

        try {
            ServerSocket serverSocket = new ServerSocket(0);
            int availablePort = serverSocket.getLocalPort();
            port = availablePort;
            serverSocket.close();
            Thread.sleep(1000);
            server = new HttpProtocol(context, availablePort, 3);
            server.start();
        } catch (Exception e) {
            if (server != null) {
                server.stop();
            }
            System.exit(1);
        }

        user = new User("woowa", "woowa", "1234");

        // sid로 세션 생성하기(로그인 결과로 생긴 세션의 sid 바꾸기)
        userDao.save(user);
        sid = "2cf6097e-7077-4180-8fb1-b233622eaa96";
        HttpRequest request = TestUtil.post(context, "/login",
                Map.of("username", List.of(user.getUsername()), "password", List.of(user.getPassword())), null);
        request.login();
        Session session = request.getSession(false);
        sessionManager.changeSessionId(session, sid);
    }

    @AfterAll
    static void afterAll() throws IOException {
        server.stop();
    }

    @AfterEach
    void tearDown() {
        postDao.deleteAll();
    }

    @Nested
    @DisplayName("로그인을")
    class Login {

        @DisplayName("요청하면 로그인 페이지로 리다이렉트한다.")
        @Test
        void testGetLogin() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "get-login.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/login/index.html").toString());
            }
        }

        @DisplayName("페이지를 요청한다.")
        @Test
        void testGetLoginPage() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "get-login-page.txt");
                assertThat(response).contains("HTTP/1.1 200 OK");
                assertThat(response).contains(HttpHeaders.CONTENT_LENGTH_HEADER);
                assertThat(response).contains(new HttpHeader(HttpHeaders.CONTENT_TYPE_HEADER, "text/html").toString());
                assertThat(response).contains("로그인", "비밀번호");
            }
        }

        @DisplayName("성공하면 홈으로 리다이렉트하고 SID 쿠키를 응답받는다")
        @Test
        void testLoginSuccess() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "user-login.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/index.html").toString());
                assertThat(response).contains(new HttpHeader(HttpHeaders.SET_COOKIE, "SID=").toString());
            }
        }

        @DisplayName("실패한다 - unknown user")
        @Test
        void testLoginFailUnknownUser() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "user-login-fail-unknown-user.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/user/login_failed.html").toString());
            }
        }

        @DisplayName("실패한다 - 잘못된 패스워드")
        @Test
        void testLoginFailWrongPw() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "user-login-fail-wrong-pw.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/user/login_failed.html").toString());
            }
        }
    }

    @Nested
    @DisplayName("글쓰기")
    class Post {
        @DisplayName("페이지를 요청하면 글쓰기 페이지를 반환한다")
        @Test
        void testGetPost() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "get-post.txt");
                assertThat(response).contains("HTTP/1.1 200 OK");
                assertThat(response).contains("게시글 작성", "제목", "이미지", "글의 내용을 입력하세요");
            }
        }

        @DisplayName("페이지를 요청하면 로그인 페이지로 이동한다.")
        @Test
        void testGetPostUnauthenticated() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "get-post-unauthenticated.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/login/index.html").toString());
            }
        }

        @DisplayName("를 요청하면 파일을 생성하고 글을 쓴다.")
        @Test
        void testCreatePost() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "post-create.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/index.html").toString());
                List<codesquad.application.model.Post> posts = postDao.findAll();
                assertEquals(1, posts.size());
                assertThat(posts.get(0).getTitle()).isEqualTo("title");
                assertThat(posts.get(0).getContent()).isEqualTo("content");
                assertThat(posts.get(0).getUserId()).isEqualTo(1);
                assertThat(posts.get(0).getFileName()).contains("png");
                assertThat(posts.get(0).getFileName()).doesNotContain("그룹 미션");
                File uploadDir = new File(System.getProperty("user.dir"), PostWriteHandler.UPLOAD_DIR);
                assertTrue(uploadDir.exists());
                File uploadedImage = new File(uploadDir, posts.get(0).getFileName());
                assertTrue(uploadDir.exists());
                uploadedImage.delete();
            }
        }

        @DisplayName("를 이미지 없이 요청하면 파일을 생성하지 않고 에러 응답을 준다.")
        @Test
        void testCreatePostFailWhenMissingImage() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "post-create-without-image.txt");
                assertThat(response).contains("HTTP/1.1 400 Bad Request");
                assertThat(response).contains("Bad Request");
                List<codesquad.application.model.Post> posts = postDao.findAll();
                assertEquals(0, posts.size());
            }
        }

        @DisplayName("를 요청하면 로그인 페이지로 이동한다.")
        @Test
        void testCreatePostUnauthenticated() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "post-create-no-sid-cookie.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/login/index.html").toString());
            }
        }
    }

    @Nested
    @DisplayName("사용자 목록을")
    class UserList {
        @DisplayName("요청하면 사용자 목록을 반환한다")
        @Test
        void testGetUserListPost() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "get-user-list.txt");
                assertThat(response).contains("HTTP/1.1 200 OK");
                assertThat(response).contains("User List");
                assertThat(response).contains(user.getUsername(), user.getNickname());
            }
        }

        @DisplayName("요청하면 로그인 페이지로 이동한다.")
        @Test
        void testGetUserListUnauthenticated() throws IOException {
            //given
            try (Socket socket = new Socket("localhost", port)) {
                String response = TestUtil.execute(socket, "get-user-list-unauthenticated.txt");
                assertThat(response).contains("HTTP/1.1 302 Found");
                assertThat(response).contains(new HttpHeader("Location", "/login/index.html").toString());
            }
        }
    }
}
