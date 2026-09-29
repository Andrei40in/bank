import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Map<Integer, User> users = new HashMap<>();

        UserService userService = new UserService(users);

        Bank bank = new Bank(userService);

        User user1 = new User(1, "Andrey", 25, 10000);
        User user2 = new User(2, "Alex", 30, 5000);


        userService.addUser(user1);
        userService.addUser(user2);

        User foundUser = userService.findUserById(2);

        System.out.println(foundUser.getName());


        List<String> names = new ArrayList<>();

        names.add("A");
        names.add("B");
        names.add("B");
        names.add("C");
        names.add("B");

        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            if (name.equals("B")){
                iterator.remove();
            }
        }
        try {
            bank.transfer(1, 2, 2000);
            bank.transfer(2, 1, 1000);
            bank.transfer(1, 2, 1000000);
        } catch (InsufficientFundsException | UserNotFoundException | InvalidAmountException e) {
            System.out.println(e.getMessage());
        }

        Collection<User> allUsers =userService.getAllUsers();

        for (User user : allUsers) {
            System.out.println(user.getName());

        }
        List<Transaction> transactions = bank.getUserTransactions(1);
        for(Transaction user : transactions) {
            System.out.println("ID: " + user.getId());
            System.out.println("Name Sender: " + user.getSender().getName());
            System.out.println("Name Recipient: " + user.getRecipient().getName());
            System.out.println("Summ: " + user.getAmount());
        }
        System.out.println("Количество транзакций: " + transactions.size());

    }
}