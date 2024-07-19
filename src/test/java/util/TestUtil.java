package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.application.db.DBConfig;
import codesquad.was.dbcp.ConnectionPool;
import codesquad.was.dbcp.DefaultConnectionPool;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpMethod;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import codesquad.was.http.MimeType;
import codesquad.was.http.Part;
import codesquad.was.server.ServerContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.h2.tools.RunScript;

public class TestUtil {
    public static HttpRequest of(
            ServerContext context,
            HttpMethod method,
            String path,
            HttpHeaders headers,
            Map<String, List<String>> parameters,
            List<Part> parts,
            byte[] body) {

        MimeType contentType = headers.contains(HttpHeaders.CONTENT_TYPE_HEADER) ?
                MimeType.getMimeTypeFromContentType(headers.getHeaderSingleValue(HttpHeaders.CONTENT_TYPE_HEADER).get())
                : null;

        return new HttpRequest(context, method, path, "HTTP/1.1", "localhost", contentType,
                headers, parameters, parts, null, body);
    }

    public static HttpRequest get(String path) {
        return of(null, HttpMethod.GET, path, HttpHeaders.getDefault(), null, null, null);
    }

    public static HttpRequest get(ServerContext context, String path, HttpHeaders headers) {
        return of(context, HttpMethod.GET, path, headers, null, null, null);
    }

    public static HttpRequest post(String path, Map<String, List<String>> parameters, byte[] body) {
        return of(null, HttpMethod.POST, path, HttpHeaders.getDefault(), parameters, null, body);
    }

    public static HttpRequest post(ServerContext context, String path, Map<String, List<String>> parameters,
                                   byte[] body) {
        return of(context, HttpMethod.POST, path, HttpHeaders.getDefault(), parameters, null, body);
    }

    public static void assertRedirectResponse(HttpResponse response, String redirectUrl) {
        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertTrue(response.getHeaders().contains(HttpHeaders.LOCATION));
        assertEquals(redirectUrl, response.getHeaders().getHeaderSingleValue(HttpHeaders.LOCATION).get());
    }

    public static void assertErrorResponse(HttpResponse response, HttpStatus status) {
        assertEquals(status, response.getStatus());
        assertEquals(MimeType.html.getMIMEType(),
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

    public static ConnectionPool setUpDbAndGetConnectionPool() {
        DBConfig config = DBConfig.getDBConfig("application.properties");
        DefaultConnectionPool pool = new DefaultConnectionPool(
                config.getUrl(),
                config.getUsername(),
                config.getPassword(),
                config.getMaxPoolSize(),
                config.getMinIdle(),
                config.getConnectionTimeout(),
                config.getIdleTimeout()
        );
        try (Connection conn = pool.getConnection()) {
            RunScript.execute(conn,
                    new InputStreamReader(TestUtil.class.getClassLoader().getResourceAsStream("init.sql")));
        } catch (SQLException e) {
            System.out.println("fail to create table");
        }
        return pool;
    }

    public static void execute(Socket client, String filename) throws IOException {
        InputStream input = TestUtil.class.getClassLoader().getResourceAsStream(filename);
        client.getOutputStream().write(input.readAllBytes());
    }

}
