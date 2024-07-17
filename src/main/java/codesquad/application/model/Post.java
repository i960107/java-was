package codesquad.application.model;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public Post(){}

    public Post(Long userId, String contents) {
        this.userId = userId;
        this.content = contents;
        this.createdAt = LocalDateTime.now();
    }

    public Post(Long id, Long userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Post{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", content='").append(content).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append('}');
        return sb.toString();
    }
}
