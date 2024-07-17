package codesquad.application.db;

import codesquad.application.model.User;
import codesquad.was.server.authenticator.Principal;
import codesquad.was.server.authenticator.Role;
import codesquad.was.server.authenticator.UserAuthBase;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryUserRepository implements Repository<User, String>, UserAuthBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, User> users;

    public InMemoryUserRepository() {
        users = new ConcurrentHashMap<>();
    }

    @Override
    public void save(User user) {
        users.put(user.getUsername(), user);
        log.info("new user saved : " + user);
    }

    @Override
    public Optional<User> findBy(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public List<User> findAll() {
        return users.values()
                .stream()
                .toList();
    }

    @Override
    public void deleteAll() {
        this.users.clear();
    }

    @Override
    public Optional<Principal> auth(String username, String password) {
        return findBy(username)
                .map(user -> new Principal(user.getId(), user.getUsername(), Role.USER));
    }
}
