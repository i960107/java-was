package codesquad.application.db;

import codesquad.application.model.User;
import codesquad.was.server.authenticator.Principal;
import codesquad.was.server.authenticator.Role;
import codesquad.was.server.authenticator.UserAuthBase;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao implements UserAuthBase {

    private static final String INSERT_SQL = "INSERT INTO `USER`(username, nickname, password, created_at) VALUES(?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL = "SELECT * FROM `user` where id = ?";

    private static final String FIND_BY_USERNAME_SQL = "SELECT * FROM `user` where username = ?";

    private static final String FIND_ALL_SQL = "SELECT * FROM `user`";

    private static final String DELETE_ALL_SQL = "DELETE FROM `user`";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;

    private final UserRowMapper userRowMapper;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = new UserRowMapper();
    }

    public User save(User user) {
        Long generatedId = jdbcTemplate.saveAndGetGeneratedKey(INSERT_SQL, pstmt -> {
            try {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getNickname());
                pstmt.setString(3, user.getPassword());
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            } catch (SQLException e) {
                throw new DBException("error while prepare statement " + INSERT_SQL);
            }
        });
        user.setId(generatedId);
        return user;
    }

    public Optional<User> findById(Long id) {
        User user = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, pstmt -> {
                    try {
                        pstmt.setLong(1, id);
                    } catch (SQLException e) {
                        throw new DBException("error while prepare statement " + FIND_BY_ID_SQL);
                    }
                },
                userRowMapper);
        return Optional.ofNullable(user);
    }

    public Optional<User> findByUsername(String username) {
        User user = jdbcTemplate.queryForObject(FIND_BY_USERNAME_SQL, pstmt -> {
                    try {
                        pstmt.setString(1, username);
                    } catch (SQLException e) {
                        throw new DBException("error while prepare statement " + FIND_BY_USERNAME_SQL);
                    }
                },
                userRowMapper);
        return Optional.ofNullable(user);
    }

    public List<User> findAll() {
        return jdbcTemplate.queryForList(FIND_ALL_SQL, pstmt -> {}, userRowMapper);
    }

    public int deleteAll() {
        return jdbcTemplate.executeUpdate(DELETE_ALL_SQL, preparedStatement -> {});
    }

    @Override
    public Optional<Principal> auth(String username, String password) {
         return findByUsername(username)
                 .filter(user -> user.getPassword().equals(password))
                 .map(user -> new Principal(user.getId(), user.getUsername(), Role.USER));
    }
}
