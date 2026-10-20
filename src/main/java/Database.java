import java.sql.*;


public class Database {

    public static void initialize() throws SQLException {

        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db"); Statement statement = connection.createStatement() ) { // Подключение к БД

            System.out.println("Database connected!");

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY,
                name TEXT,
                age INTEGER,
                balance REAL
                )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sender_id INTEGER,
                recipient_id INTEGER,
                amount REAL
                )
            """);

            System.out.println("Table users created!");
        } catch (SQLException e) {
            throw new SQLException("Database error", e);
        }


    }
    public static void clearDatabase() throws SQLException{
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db"); Statement statement = connection.createStatement() ) {

            statement.executeUpdate("DELETE FROM transactions");
            statement.executeUpdate("DELETE FROM users");

        } catch (SQLException e) {
            throw new SQLException("Database error", e);
        }
    }
}