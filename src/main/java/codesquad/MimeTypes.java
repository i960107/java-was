package codesquad;

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
    jpg("image/jpeg");


    private String MIMEType;

    MimeTypes(String MIMEType) {
        this.MIMEType = MIMEType;
    }

    public static String getMimeType(String fileName) {
        String extension = fileName.split("\\.")[1];
        return valueOf(extension).getMIMEType();
    }

    public String getMIMEType() {
        return MIMEType;
    }
}
