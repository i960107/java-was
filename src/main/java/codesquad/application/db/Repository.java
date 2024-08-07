package codesquad.application.db;

import java.util.List;
import java.util.Optional;

public interface Repository<T, S> {

    void save(T t);

    Optional<T> findBy(S s);

   List<T> findAll();

    void deleteAll();
}
