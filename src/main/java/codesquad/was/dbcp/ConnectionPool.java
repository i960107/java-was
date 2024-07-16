package codesquad.was.dbcp;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {

    Connection getConnection() throws SQLException;

    void releaseConnection(Connection connection);

    String getUrl();

    String getUser();

    String getPassword();

    void shutDown() throws SQLException;
}
