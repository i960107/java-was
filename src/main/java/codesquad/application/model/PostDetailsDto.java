package codesquad.application.model;

import java.time.LocalDateTime;

public class PostDetailsDto {
    private Long postId;
    private String title;
    private String content;
    private Long userId;
    private String authorNickname;
    private LocalDateTime createdAt;

    public PostDetailsDto(Long postId, String title, String content, Long userId, String authorNickname, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
    }

    public Long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
