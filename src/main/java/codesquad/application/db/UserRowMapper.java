package codesquad.application.db;

import codesquad.application.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("nickname"),
                resultSet.getString("password"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
