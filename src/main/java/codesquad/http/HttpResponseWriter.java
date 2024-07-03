package codesquad.http;

import static codesquad.IOUtil.writeLine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponseWriter {

    public static final int BUFFER_SIZE = 8192;

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

        if (response.getHeader(HttpHeader.CONTENT_LENGTH_HEADER).isPresent()
                && response.getBody() != null
                && response.getBody().getClass().equals(File.class)
        ) {
            File file = (File) response.getBody();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        }

        output.flush();
    }

}
