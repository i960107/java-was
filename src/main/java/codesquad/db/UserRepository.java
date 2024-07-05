package codesquad.db;

import codesquad.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository implements Repository<User, Long> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static UserRepository INSTANCE;

    private AtomicLong seq;

    private Map<Long, User> users;

    private UserRepository() {
        users = new ConcurrentHashMap<>();
        seq = new AtomicLong(1);
    }

    public static UserRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
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
}
