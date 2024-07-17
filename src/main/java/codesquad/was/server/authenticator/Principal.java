package codesquad.was.server.authenticator;

public class Principal {
    private Long userId;
    private String username;
    private Role role;

    public Principal(Long userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
