package codesquad.was.server.authenticator;

public class Principal {
    private String username;
    private Role role;

    public Principal(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
