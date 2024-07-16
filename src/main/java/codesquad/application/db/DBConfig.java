package codesquad.application.db;

import codesquad.was.server.exception.ServerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConfig {

    private static Logger log = LoggerFactory.getLogger(DBConfig.class);

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private int minIdle;
    private int maxPoolSize;
    private int connectionTimeout;
    private int idleTimeout;

    public DBConfig(String url, String username, String password, String driverClassName, int minIdle,
                    int maxPoolSize, int connectionTimeout, int idleTimeout) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
        this.minIdle = minIdle;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;
        this.idleTimeout = idleTimeout;
    }

    public static DBConfig getDBConfig(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            Properties properties = new Properties();
            properties.load(fis);
            return new DBConfig(
                    properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password"),
                    properties.getProperty("driverClassName"),
                    Integer.parseInt(properties.getProperty("minIdle")),
                    Integer.parseInt(properties.getProperty("maxPoolSize")),
                    Integer.parseInt(properties.getProperty("connectionTimeout")),
                    Integer.parseInt(properties.getProperty("idleTimeout"))
            );
        } catch (IOException e) {
            log.warn("fail to read dbconfig from {} ", fileName);
            throw new ServerException("fil to read dbconfig");
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }
}
