package ru.ns.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Properties properties = loadProperties();

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(
                    url,
                    user,
                    password
            );

            System.out.println("Подключение к БД успешно.");

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка подключения к БД",
                    e
            );
        }
    }

    private Properties loadProperties() {

        Properties properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "Не найден файл database.properties");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Ошибка чтения database.properties",
                    e
            );
        }

        return properties;
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null &&
                    !connection.isClosed()) {

                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
