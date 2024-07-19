package codesquad.was.http;

import java.util.Arrays;
import java.util.Locale;

public enum MimeType {

    aac("audio/aac"),
    abw("application/x-abiword"),
    apng("image/apng"),
    arc("application/x-freearc"),
    avif("image/avif"),
    avi("video/x-msvideo"),
    svg("image/svg+xml"),
    css("text/css"),
    html("text/html"),
    text("text/plain"),
    ico("image/x-icon"),
    js("application/javascript"),
    png("image/png"),
    jpg("image/jpeg"),
    jpeg("image/jpeg"),
    json("application/json"),
    form_data("application/x-www-form-urlencoded"),
    multipart_fom_data("multipart/form-data");


    private String MIMEType;

    MimeType(String MIMEType) {
        this.MIMEType = MIMEType;
    }

    public static MimeType getMimeTypeFromExtension(String fileName) {
        int index = fileName.indexOf(".");
        if (index == -1 || index + 1 >= fileName.length()) {
            throw new IllegalArgumentException();
        }
        String extension = fileName.substring(index + 1);
        return valueOf(extension.toLowerCase(Locale.ROOT));
    }

    public static MimeType getMimeTypeFromContentType(String contentType) {
        return Arrays.stream(MimeType.values())
                .filter(mime -> contentType.contains(mime.getMIMEType()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }


    public String getMIMEType() {
        return MIMEType;
    }
}
