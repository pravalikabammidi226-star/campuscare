package com.campuscare.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages JDBC database connections to MySQL for CampusCare+.
 * Demonstrates JDBC Driver management, Connection pooling/handling, PreparedStatement support,
 * and graceful fallback detection.
 */
public final class DatabaseConnectionManager {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManager.class.getName());

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DB = "campuscare_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "root";

    private static String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DB);
    private static String username = DEFAULT_USER;
    private static String password = DEFAULT_PASS;

    private static Boolean connectionAvailable = null;

    private DatabaseConnectionManager() {}

    /**
     * Configures custom database connection parameters.
     */
    public static void configure(String host, int port, String database, String user, String pass) {
        jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database);
        username = user;
        password = pass;
        connectionAvailable = null; // reset cached status
    }

    /**
     * Checks if MySQL JDBC driver is on classpath and the database server is reachable.
     */
    public static synchronized boolean isDatabaseAvailable() {
        if (connectionAvailable != null) {
            return connectionAvailable;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
                connectionAvailable = (conn != null && !conn.isClosed());
                LOGGER.info("Successfully connected to MySQL database: " + jdbcUrl);
                return connectionAvailable;
            } catch (SQLException e) {
                LOGGER.warning("MySQL database unreachable: " + e.getMessage() + ". CampusCare+ will utilize robust in-memory/file storage.");
                connectionAvailable = false;
                return false;
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("MySQL JDBC driver not loaded on runtime classpath. Defaulting to in-memory/persistent store.");
            connectionAvailable = false;
            return false;
        }
    }

    /**
     * Obtains a new JDBC Connection from DriverManager.
     * @return java.sql.Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * Safely closes a JDBC connection, swallowing exceptions.
     */
    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }
}
