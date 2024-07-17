package codesquad.was.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public final class IOUtil {

    public static final String UTF8 = "UTF-8";

    private static final char CR = '\r';

    private static final char LF = '\n';

    private IOUtil() {
    }

    public static String readLine(InputStream input) throws IOException {
        int ch;
        StringBuilder sb = new StringBuilder();
        while ((ch = input.read()) != -1) {
            if (ch == CR) {
                continue;
            } else if (ch == LF) {
                break;
            } else {
                sb.append((char) ch);
            }
        }

        return ch == -1 && sb.isEmpty() ? null : sb.toString();
    }

    public static String readLine(InputStreamReader input) throws IOException {
        int ch;
        StringBuilder sb = new StringBuilder();
        while ((ch = input.read()) != -1) {
            if (ch == CR) {
                continue;
            } else if (ch == LF) {
                break;
            } else {
                sb.append((char) ch);
            }
        }

        return ch == -1 && sb.isEmpty() ? null : sb.toString();
    }

    public static byte[] readToSize(InputStream input, int size) throws IOException {
        byte[] buffer = new byte[size];
        int read = input.read(buffer, 0, size);
        if (read != size) {
            throw new IOException("Could not read " + size + " bytes");
        }
        return buffer;
    }

    public static void writeLine(OutputStream output, String str) throws IOException {
        if (!(str == null || str.isEmpty())) {
            output.write(str.getBytes(StandardCharsets.UTF_8));
        }
        output.write(CR);
        output.write(LF);
    }

    public static String getDateStringUtc() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("EEE, dd MMM yyyy HH:mm:ss")
                .toFormatter(Locale.US);
        return now.format(formatter) + "GMT";
    }

    public static InputStream getClassPathResource(String path) {
        return IOUtil.class.getClassLoader().getResourceAsStream(path);
    }

}
