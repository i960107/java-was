package codesquad.application.model;

import codesquad.was.http.Part;

public class PostWriteRequest {
    Long userId;
    String title;
    String content;
    Part image;

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Part getImage() {
        return image;
    }

    public Post toEntity(String uniqueFileName) {
        return new Post(userId, title, content, uniqueFileName);
    }
}
