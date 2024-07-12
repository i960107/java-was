package codesquad.was.http;

import java.util.Arrays;

public enum MimeTypes {

    aac("audio/aac"),
    abw("application/x-abiword"),
    apng("image/apng"),
    arc("application/x-freearc"),
    avif("image/avif"),
    avi("video/x-msvideo"),
    svg("image/svg+xml"),
    css("text/css"),
    html("text/html"),
    ico("image/x-icon"),
    js("application/javascript"),
    png("image/png"),
    jpg("image/jpeg"),
    json("application/json"),
    form_data("application/x-www-form-urlencoded");


    private String MIMEType;

    MimeTypes(String MIMEType) {
        this.MIMEType = MIMEType;
    }

    public static String getMimeTypeFromExtension(String fileName) {
        int index = fileName.indexOf(".");
        if (index == -1 || index + 1 >= fileName.length()) {
            throw new IllegalArgumentException();
        }
        String extension = fileName.substring(index + 1);
        return valueOf(extension).getMIMEType();
    }

    public static MimeTypes getMimeTypeFromContentType(String contentType) {
        return Arrays.stream(MimeTypes.values())
                .filter(mime -> mime.getMIMEType().equals(contentType))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }


    public String getMIMEType() {
        return MIMEType;
    }
}
