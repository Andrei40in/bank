import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    private UserRepository userRepository;

    public TransactionRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public int addTransaction(Connection connection,Transaction transaction) throws SQLException{

        int id = 0;
        String sql = "INSERT INTO transactions (sender_id, recipient_id, amount) VALUES (?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setInt(1, transaction.getSender().getId());
            preparedStatement.setInt(2, transaction.getRecipient().getId());
            preparedStatement.setDouble(3, transaction.getAmount());

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        throw new SQLException("Failed to get generated transaction ID");
    }
    public List<Transaction> findAll() throws SQLException{
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                int senderId = resultSet.getInt("sender_id");
                int recipientId = resultSet.getInt("recipient_id");
                double amount = resultSet.getDouble("amount");
                User sender = userRepository.findById(senderId);
                User recipient = userRepository.findById(recipientId);

                transactions.add(new Transaction(id,sender,recipient,amount));
            }
        }return transactions;
    }
    public List<Transaction> findByUserId(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE sender_id = ? OR recipient_id = ?";

        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int senderId = resultSet.getInt("sender_id");
                int recipientId = resultSet.getInt("recipient_id");
                double amount = resultSet.getDouble("amount");

                User sender = userRepository.findById(senderId);
                User recipient = userRepository.findById(recipientId);

                transactions.add(new Transaction(id,sender,recipient,amount));
            } return transactions;
        }
    }
}
