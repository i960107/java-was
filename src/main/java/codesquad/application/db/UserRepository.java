package codesquad.application.db;

import codesquad.application.model.User;
import codesquad.was.dbcp.ConnectionPool;
import codesquad.was.server.authenticator.UserAuthBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository implements Repository<User, String>, UserAuthBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ConnectionPool connectionPool;

    public UserRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void save(User user) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "insert into users (username, nickname, password) values (?, ?, ?)");) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setString(3, user.getPassword());

            int row = preparedStatement.executeUpdate();

            log.info(row + " row(s) inserted into user table");
        } catch (SQLException e) {
            log.warn("fail to read user from db");
            throw new DBException("fail to read user from db");
        }
    }

    @Override
    public Optional<User> findBy(String username) {
        try (
                Connection conn = connectionPool.getConnection();
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
        ) {
            User user = new User(
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("nickname")
            );
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            log.warn("fail to read user from db");
            throw new DBException("fail to read user from db");
        }
    }

    @Override
    public List<User> findAll() {
        try (
                Connection conn = connectionPool.getConnection();
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
        ) {
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("nickname")
                );
                users.add(user);
            }
            return users.stream().toList();
        } catch (SQLException e) {
            log.warn("fail to read users from db");
            throw new DBException("fail to read users from db");
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete * from users");) {

            int row = preparedStatement.executeUpdate();

            log.info(row + " row(s) deleted from users table");
        } catch (SQLException e) {
            log.warn("fail to read user from db");
            throw new DBException("fail to read user form db");
        }
    }

    @Override
    public boolean auth(String username, String password) {
        Optional<User> user = findBy(username);
        return user.map(value -> value.getPassword().equals(password)).orElse(false);
    }
}
