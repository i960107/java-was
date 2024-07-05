package codesquad.http;

import static codesquad.util.IOUtil.writeLine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpResponseWriter {

    public static final int BUFFER_SIZE = 8192;

    public static void writeStatusLine(OutputStream output, String protocol, HttpStatus status) throws IOException {
        String statusLine = String.join(" ",
                protocol,
                status.getCode(),
                status.name());

        writeLine(output, statusLine);
    }

    public static void writeHeaders(OutputStream output, HttpHeaders headers) throws IOException {
        for (HttpHeader header : headers.getHeaders()) {
            writeLine(output, header.toString());
        }

        writeLine(output, null);
    }

    public static void writeBody(OutputStream output, InputStream input) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(input)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        output.flush();
    }
}
