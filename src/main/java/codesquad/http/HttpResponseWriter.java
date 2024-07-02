package codesquad.http;

import static codesquad.IOUtil.writeLine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponseWriter {

    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final int BUFFER_SIZE = 4096;

    public static void write(OutputStream output, Response<?> response) throws IOException {

        String statusLine = String.join(" ",
                response.getProtocol(),
                response.getStatusCode(),
                response.getStatusMessage());
        writeLine(output, statusLine);

        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            writeLine(output, String.join(": ", header.getKey(), header.getValue()));
        }

        writeLine(output, null);

        if (response.getHeader(CONTENT_LENGTH_HEADER).isPresent()
                && response.getBody() != null
                && response.getBody().getClass().equals(File.class)
        ) {
            File file = (File) response.getBody();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[BUFFER_SIZE]; // 4KB 버퍼 크기 사용
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        }

        output.flush();
    }

}
