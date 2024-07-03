package codesquad;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class IOUtil {

    private static final char CR = '\r';

    private static final char LF = '\n';

    private static final int BUFFER_SIZE = 4096;

    public static String readLine(InputStream input) throws IOException {
        int ch;
        StringBuffer sb = new StringBuffer();
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
}
