import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;

    public Bank (UserRepository userRepository,TransactionRepository transactionRepository){
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public boolean transfer(int fromUserId, int toUserId, double amount){  //Перевод между счетами
        if(amount <= 0){
            throw new InvalidAmountException("Enter an amount greater than 0");
        }
        if(fromUserId == toUserId){
            throw new SameUserTransferException("A user can't transfer money to themselves");
        }

        User sender = findUser(fromUserId);
        User recipient = findUser(toUserId);
        if ((sender == null) || (recipient == null)){
            throw new UserNotFoundException("User not found");
        }
        if(sender.getBalance() < amount){
            throw new InsufficientFundsException("Not enough money");
        }
        recipient.deposit(amount);
        sender.withdraw(amount);
        try (Connection connection =
                     DriverManager.getConnection("jdbc:sqlite:bank.db")) {

            connection.setAutoCommit(false);

            try {
                userRepository.updateBalance(
                        connection, sender.getId(), sender.getBalance()
                );

                userRepository.updateBalance(
                        connection, recipient.getId(), recipient.getBalance()
                );

                Transaction transaction =
                        new Transaction(0, sender, recipient, amount);

                int id = transactionRepository.addTransaction(
                        connection, transaction
                );
                transaction.setId(id);

                connection.commit();

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Database error", e);
        }

        return true;
    }



    public List<User> findAllUsers() {
        try {
            return userRepository.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Database error", e);
        }
    }
    public double getTotalBalance(){  //общая сумма всех счетов
        return findAllUsers().
                stream().
                mapToDouble(User::getBalance).
                sum();
    }
    public List<User> getUsersWithBalanceMoreThan(double amount){       //фИЛЬТР СУММА БОЛЬШЕ
        return findAllUsers()
                .stream()
                .filter(user -> user.getBalance() > amount)
                .toList();
    }
    private List<Transaction> transactions = new ArrayList<>();  // История транзакций

    public List<Transaction> getUserTransactions(int userId) {  // список транзакций по пользователям
        try {
            return transactionRepository.findByUserId(userId);
        } catch (SQLException e) {
            throw new DatabaseException("Database error", e);
        }
    }

    public String getUserInfo(int userId){
        User user = findUser(userId);
        if(user == null){
            return "User not found";
        }
        return "ID: " + user.getId() + "\n"
                + "Name: " + user.getName() + "\n"
                + "Age: " + user.getAge() + "\n"
                + "Balance: " + user.getBalance() + "\n"
                + "isAdult: " + user.isAdult() + ".";

    }
    public int getUserTransactionCount(int userId){
        return getUserTransactions(userId).size();
    }

    public User findUser(int id){
        try{
        User user = userRepository.findById(id);
        return user;
        } catch (SQLException e) {
            throw new DatabaseException("Database error", e);
        }
    }

    public void addUser(User user){
        try {
            userRepository.addUser(user);
        } catch (SQLException e) {
            throw new DatabaseException("Database error", e);
        }
    }

    public void deleteUser(int id){
        try {
            userRepository.deleteById(id);
        } catch (SQLException e) {
            throw new DatabaseException("Database error", e);
        }
    }


}