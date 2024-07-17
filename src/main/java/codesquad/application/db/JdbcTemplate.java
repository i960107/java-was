package codesquad.application.db;

import codesquad.was.dbcp.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTemplate {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ConnectionPool connectionPool;

    public JdbcTemplate(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    public Long saveAndGetGeneratedKey(String sql, Consumer<PreparedStatement> statementConsumer) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statementConsumer.accept(pstmt);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating record failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating record failed, no ID obtained.");
                }
            }
        } catch (SQLException exception) {
            log.warn("fail to execute and generate key : {}", sql);
            throw new DBException("Failed to execute" + sql);
        }
    }

    public <T> T queryForObject(String sql, Consumer<PreparedStatement> statementConsumer, RowMapper<T> rowMapper) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            statementConsumer.accept(pstmt);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return rowMapper.mapRow(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            log.warn("fail to query for object : {}", sql);
            throw new DBException("Failed to query object : " + sql);
        }
    }

    public <T> List<T> queryForList(String sql, Consumer<PreparedStatement> statementConsumer, RowMapper<T> rowMapper) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
        ) {
            statementConsumer.accept(pstmt);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(rowMapper.mapRow(resultSet));
                }
                return results;
            }
        } catch (SQLException e) {
            log.warn("fail to query for list : {}", sql);
            throw new DBException("Failed to query list : " + sql);
        }
    }

    public int executeUpdate(String sql, Consumer<PreparedStatement> statementConsumer) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            statementConsumer.accept(pstmt);
            connection.setAutoCommit(false);
            int affectedRows = 0;
            try {
                affectedRows = pstmt.executeUpdate();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw new SQLException();
            } finally {
                connection.setAutoCommit(true);
            }
            return affectedRows;
        } catch (SQLException e) {
            log.warn("faile to execute : {}", sql);
            throw new DBException("Failed to execute : " + sql);
        }
    }
}
