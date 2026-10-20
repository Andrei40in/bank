import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public void addUser(User user) throws SQLException {

        String sql = "INSERT INTO users (id, name, age, balance) VALUES (?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.setDouble(4, user.getBalance());

            preparedStatement.executeUpdate();
        }
    }
    public User findById(int id) throws SQLException{

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultSet =
                    preparedStatement.executeQuery();
            if (resultSet.next()) {
                int age = resultSet.getInt("age");
                String name = resultSet.getString("name");
                double balance = resultSet.getDouble("balance");
                int userId = resultSet.getInt("id");
                return new User(userId, name, age, balance);
            } return null;

        }
    }

    public void deleteById(int id) throws SQLException{
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();

        }
    }
    public void updateBalance(Connection connection, int id, double newBalance) throws SQLException{
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1,newBalance);
            preparedStatement.setInt(2,id);

            preparedStatement.executeUpdate();
        }

    }

    public List<User> findAll() throws SQLException{
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                users.add(new User(resultSet.getInt("id"), resultSet.getString("name"),resultSet.getInt("age"),resultSet.getDouble("balance")));
            }
            return users;
        }
    }

}
