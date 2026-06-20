package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    // параметры подключения
    private static final String URL =
            "jdbc:mysql://localhost:3306/salon";

    private static final String USER =
            "salon_app";

    private static final String PASSWORD =
            "555666";

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Подключение к БД успешно.");

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка подключения к БД",
                    e
            );
        }
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