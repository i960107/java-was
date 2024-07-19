package codesquad.was.http;

import codesquad.was.http.exception.HttpProtocolException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Part {

    private static final String HEADER_VALUES_DELIMITER = ";";

    private static final String KEY_VALUE_DELIMITER = "=";

    private String name;

    private MimeType contentType;

    private String fileName;

    private byte[] content;

    public Part() {
    }

    public Part(String name, MimeType contentType, String fileName, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.fileName = fileName;
        this.content = content;
    }

    public static Part from(HttpHeaders headers, byte[] content) {
        if (!isValidMultiPartHeader(headers)) {
            throw new IllegalArgumentException();
        }
        Part part = new Part();
        String disposition = headers.getHeaderSingleValue(HttpHeaders.CONTENT_DISPOSITION).get();
        parseContentDisposition(part, disposition);
        Optional<String> contentType = headers.getHeaderSingleValue(HttpHeaders.CONTENT_TYPE_HEADER);
        parseContentType(part, contentType);
        part.content = content;
        return part;
    }

    private static boolean isValidMultiPartHeader(HttpHeaders headers) {
        return headers.contains(HttpHeaders.CONTENT_DISPOSITION)
                && headers.getHeaderSingleValue(HttpHeaders.CONTENT_DISPOSITION).get().startsWith("form-data;");
    }

    private static void parseContentDisposition(Part part, String dispositionHeaderValue) {
        String[] dispositions = dispositionHeaderValue.split(HEADER_VALUES_DELIMITER);
        if (dispositions.length <= 1 || !dispositions[0].trim().equals("form-data")) {
            throw new HttpProtocolException("invalid multipart disposition header");
        }
        for (int i = 1; i < dispositions.length; i++) {
            String disposition = dispositions[i];
            String[] pair = disposition.trim().split(KEY_VALUE_DELIMITER);
            String key = pair[0].trim();
            String value = pair[1].trim().replaceAll("\"", "");
            if (key.equals("name")) {
                part.name = value;
            } else if (key.equals("filename")) {
                part.fileName = value;
            }
        }
    }

    private static void parseContentType(Part part, Optional<String> contentType) {
        if (contentType.isPresent()) {
            part.contentType = MimeType.getMimeTypeFromContentType(contentType.get());
            return;
        }

        if (part.fileName != null) {
            part.contentType = MimeType.getMimeTypeFromExtension(part.fileName);
            return;
        }

        part.contentType = MimeType.text;
    }


    public String getName() {
        return name;
    }

    public MimeType getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Part{");
        if (fileName != null) {
            sb.append("fileName='").append(fileName).append('\'');
            sb.append(", content size='").append(content.length).append('\'');
        }
        sb.append(", contentType=").append(contentType);
        sb.append(", name='").append(name).append('\'');
        if (fileName != null) {
            sb.append("fileName='").append(fileName).append('\'');
            sb.append(", content size='").append(content.length).append('\'');
        } else {
            sb.append("='").append(new String(content)).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Part part = (Part) o;
        return Objects.equals(name, part.name) && contentType == part.contentType && Objects.equals(
                fileName, part.fileName) && Objects.deepEquals(content, part.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, contentType, fileName, Arrays.hashCode(content));
    }
}
