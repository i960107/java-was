package codesquad.was.dbcp;

import codesquad.was.server.exception.ServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConnectionPool implements ConnectionPool {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String url;

    private final String username;

    private final String password;

    private AtomicInteger currentPoolSize;

    private final int maxPoolSize;

    private final int minIdle;

    private final int idleTimeout;

    private final int connectionTimeout;

    private final BlockingQueue<PooledConnection> pool;

    public DefaultConnectionPool(
            String url,
            String username,
            String password,
            int maxPoolSize,
            int minIdle,
            int connectionTimeoutMilliSeconds,
            int idleTimeoutMilliSeconds
    ) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.minIdle = minIdle;
        this.idleTimeout = idleTimeoutMilliSeconds;
        this.connectionTimeout = connectionTimeoutMilliSeconds;
        this.pool = new ArrayBlockingQueue<>(maxPoolSize);
        try {
            for (int i = 0; i < minIdle; i++) {
                pool.add(createNewConnection());
            }
        } catch (SQLException exception) {
            log.warn("connection creation error : {}", exception.getMessage());
            throw new ServerException("connection creation error");
        }
        this.currentPoolSize = new AtomicInteger(minIdle);
        log.info("Connection pool created, current pool size: " + currentPoolSize.get());
        startIdleTimeoutHandler();
    }

    private void startIdleTimeoutHandler() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(idleTimeout / 2);
                    for (PooledConnection conn : pool) {
                        if (currentPoolSize.get() <= minIdle) {
                            break;
                        }
                        long now = System.nanoTime();
                        if (now - conn.getLastUsed() > idleTimeout) {
                            if (pool.remove(conn)) {
                                conn.reallyClose();
                                currentPoolSize.decrementAndGet();
                                log.info("Closed idle connection, current pool size: " + currentPoolSize.get());
                            }
                        }
                    }
                } catch (InterruptedException | SQLException e) {
                    log.warn("Idle timeout handler interrupted: {}", e.getMessage());
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private PooledConnection createNewConnection() throws SQLException {
        return new PooledConnection(DriverManager.getConnection(url, username, password), this);
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            PooledConnection conn = pool.poll(connectionTimeout, TimeUnit.MILLISECONDS);
            if (conn == null) {
                if (currentPoolSize.get() < maxPoolSize) {
                    conn = createNewConnection();
                    currentPoolSize.incrementAndGet();
                } else {
                    log.warn("connection timeout");
                    throw new SQLException("connection timeout");
                }
            }
            conn.updateLastUsed();
            return conn;
        } catch (InterruptedException exception) {
            log.warn("interrupted while waiting for connection");
            throw new SQLException("interrupted while waiting for connection");
        }
    }

    @Override
    public void releaseConnection(Connection connection) {
        try {
            if (!(connection instanceof PooledConnection)) {
                throw new IllegalArgumentException("connection is not a PooledConnection");
            }
            if (!connection.isClosed()
                    && !pool.offer((PooledConnection) connection, connectionTimeout, TimeUnit.MILLISECONDS)) {
                connection.close();
            }
        } catch (IllegalArgumentException | SQLException | InterruptedException exception) {
            log.warn("connection release error : {}", exception.getMessage());
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public int getCurrentPoolSize() {
        return currentPoolSize.get();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUser() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void shutDown() throws SQLException {
        for (PooledConnection connection : pool) {
            connection.reallyClose();
        }
    }
}
