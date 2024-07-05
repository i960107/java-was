package codesquad.db;

import java.util.Optional;

public interface Repository<T, S> {

    void save(T t);

    Optional<T> findBy(S s);
}
