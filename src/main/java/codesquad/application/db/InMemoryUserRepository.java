package codesquad.application.db;

import codesquad.application.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository implements Repository<User, Long> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private AtomicLong seq;

    private Map<Long, User> users;

    public UserRepository() {
        users = new ConcurrentHashMap<>();
        seq = new AtomicLong(1);
    }

    @Override
    public void save(User user) {
        user.setId(seq.getAndIncrement());
        users.put(user.getId(), user);
        log.info("new user saved : " + user);
    }

    @Override
    public Optional<User> findBy(Long id) {
        return Optional.ofNullable(users.get(id));
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
}
