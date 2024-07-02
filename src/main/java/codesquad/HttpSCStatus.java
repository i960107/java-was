package codesquad;

public enum HttpSCStatus {
    OK("200"),
    CREATED("201"),
    MOVED_PERMANENTLY("301"),
    FOUND("302"),
    NOT_MODIFIED("304"),
    BAD_REQUEST("400"),
    UNAUTHORIZED("401"),
    FORBIDDEN("403"),
    NOT_FOUND("404"),
    INTERNAL_SERVER_ERROR("500"),
    BAD_GATEWAY("502");

    private final String code;

    HttpSCStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}