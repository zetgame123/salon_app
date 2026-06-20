package ru.ns.database;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static final String CONFIG_FILE = "database.properties";

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

        Path externalConfig = findExternalConfig();

        if (externalConfig != null) {
            try (InputStream input =
                         Files.newInputStream(externalConfig)) {

                properties.load(input);

                System.out.println(
                        "Настройки БД загружены из: " +
                                externalConfig.toAbsolutePath());

                return properties;

            } catch (IOException e) {
                throw new RuntimeException(
                        "Ошибка чтения " + externalConfig,
                        e
                );
            }
        }

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new RuntimeException(
                        "Не найден файл " + CONFIG_FILE +
                                ". Положите его рядом с JAR " +
                                "или в src/main/resources/");
            }

            properties.load(input);

            System.out.println(
                    "Настройки БД загружены из classpath");

        } catch (IOException e) {
            throw new RuntimeException(
                    "Ошибка чтения " + CONFIG_FILE,
                    e
            );
        }

        return properties;
    }

    private Path findExternalConfig() {

        Path inWorkingDir =
                Paths.get(CONFIG_FILE).toAbsolutePath();

        if (Files.isRegularFile(inWorkingDir)) {
            return inWorkingDir;
        }

        try {
            Path codeLocation = Paths.get(
                    getClass()
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());

            if (Files.isRegularFile(codeLocation)) {
                Path besideJar = codeLocation
                        .getParent()
                        .resolve(CONFIG_FILE);

                if (Files.isRegularFile(besideJar)) {
                    return besideJar;
                }
            }

        } catch (URISyntaxException e) {
            throw new RuntimeException(
                    "Не удалось определить путь к JAR",
                    e
            );
        }

        return null;
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
