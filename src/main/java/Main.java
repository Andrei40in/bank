import java.sql.SQLException;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository();
        TransactionRepository transactionRepository = new TransactionRepository(userRepository);
        Bank bank = new Bank(userRepository, transactionRepository);

        User user1 = bank.findUser(1);
        User user2 = bank.findUser(2);

        System.out.println(user1);
        System.out.println(user2);

        bank.transfer(2, 1, 1000);

        System.out.println(bank.findUser(1));
        System.out.println(bank.findUser(2));

        bank.transfer(1, 2, 500);
        System.out.println(bank.findUser(1));
        System.out.println(bank.findUser(2));
        System.out.println(bank.getUserTransactions(1));
    }
}