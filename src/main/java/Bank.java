import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bank {

    private UserService userService;
    public Bank (UserService userService){
        this.userService = userService;
    }
    private int nextTransactionId = 1;
    public boolean transfer(int fromUserId, int toUserId, double amount){  //Перевод между счетами
        if(amount <= 0){
            throw new InvalidAmountException("Enter an amount greater than 0");
        }
        if(fromUserId == toUserId){
            throw new SameUserTransferException("A user can't transfer money to themselves");
        }

        User client1 = userService.findUserById(fromUserId);
        User client2 = userService.findUserById(toUserId);
        if ((client1 == null) || (client2 == null)){
            throw new UserNotFoundException("User not found");
        }
        if(client1.getBalance() < amount){
            throw new InsufficientFundsException("Not enough money");
        }
        client2.deposit(amount);
        client1.withdraw(amount);

        transactions.add(new Transaction(nextTransactionId, client1, client2, amount));
        nextTransactionId++;
        return true;
    }
    public double getTotalBalance(){  //общая сумма всех счетов
        return userService.getAllUsers().
                stream().
                mapToDouble(User::getBalance).
                sum();
    }
    public List<User> getUsersWithBalanceMoreThan(double amount){       //фИЛЬТР СУММА БОЛЬШЕ
        return userService.getAllUsers()
                .stream()
                .filter(user -> user.getBalance() > amount)
                .toList();
    }
    private List<Transaction> transactions = new ArrayList<>();  // История транзакций

    public List<Transaction> getUserTransactions(int userId){  // список транзакций по пользователям
        List<Transaction> result = new ArrayList<>();
        for(Transaction transaction: transactions){
            int sender = transaction.getSender().getId();
            int recipient = transaction.getRecipient().getId();
            if ((sender == userId) || (recipient == userId)){
                result.add(transaction);
            }
        }
        return result;
    }

    public String getUserInfo(int userId){
        User client = userService.findUserById(userId);
        if(client == null){
            return "User not found";
        }
        return "ID: " + client.getId() + "\n"
                + "Name: " + client.getName() + "\n"
                + "Age: " + client.getAge() + "\n"
                + "Balance: " + client.getBalance() + "\n"
                + "isAdult: " + client.isAdult() + ".";

    }
}