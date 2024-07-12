package codesquad.http;

import static codesquad.was.util.IOUtil.writeLine;

import java.io.IOException;
import java.io.OutputStream;

public final class HttpResponseWriter {

    private static void writeStatusLine(OutputStream out, String protocol, HttpStatus status) throws IOException {
        String statusLine = String.join(" ",
                protocol,
                status.getCode(),
                status.name()
        );

        writeLine(out, statusLine);
    }

    private static void writeHeaders(OutputStream out, HttpHeaders headers) throws IOException {
        for (HttpHeader header : headers.getHeaders()) {
            writeLine(out, header.toString());
        }

        writeLine(out, null);
    }

    public static void writeBody(OutputStream out, byte[] data) throws IOException {
        out.write(data);
    }

    public static void write(OutputStream out, WasResponse response) throws IOException {
        writeStatusLine(out, response.getProtocol(), response.getStatus());
        writeHeaders(out, response.getHeaders());
        if (response.hasBody()) {
            writeBody(out, response.getOutputBytes());
        }
        out.flush();
    }
}
