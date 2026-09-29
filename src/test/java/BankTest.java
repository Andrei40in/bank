import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BankTest {

    private UserService userService;
    private Bank bank;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        Map<Integer, User> users = new HashMap<>();

        userService = new UserService(users);

        bank = new Bank(userService);

        user1 = new User(1, "Andrey", 25, 10000);
        user2 = new User(2, "Alex", 30, 5000);

        userService.addUser(user1);
        userService.addUser(user2);
    }
    @Test
    void testTransfer() {

        bank.transfer(1, 2, 2000);
        assertEquals(8000, user1.getBalance());
        assertEquals(7000, user2.getBalance());



    }
    @Test
    void testInsufficientFunds() {

        Assertions.assertThrows(InsufficientFundsException.class, () -> bank.transfer(1, 2, 15000));


    }
    @Test
    void testUserNotFound() {

        Assertions.assertThrows(UserNotFoundException.class, () -> bank.transfer(1, 999, 15000));


    }
    @Test
    void testInvalidAmount(){

        Assertions.assertThrows(InvalidAmountException.class,() -> bank.transfer(1,2,-500));
    }

    @Test
    void testUserTransactions() {
        bank.transfer(1,2,500);
        bank.transfer(1,2,500);
        assertEquals(2,bank.getUserTransactions(1).size());
    }

    @Test
    void testTransactionData() {
        bank.transfer(1,2,500);

        List<Transaction> transactions = bank.getUserTransactions(1);

        // проверить, что сумма первой транзакции = 500
        assertEquals(500,transactions.get(0).getAmount());

        // проверить, что отправитель — user1
        assertEquals(user1, transactions.get(0).getSender());
        // проверить, что получатель — user2
        assertEquals(user2, transactions.get(0).getRecipient());
    }

    @Test
    void testTotalBalance() {
        assertEquals(15000, bank.getTotalBalance());
    }

    @Test
    void testUsersWithBalanceMoreThan() {
        // получить пользователей с балансом больше 5000
        List<User> users = bank.getUsersWithBalanceMoreThan(5000);
        // проверить, что найден 1 пользователь
        Assertions.assertEquals(1,users.size());
        // проверить, что это user1
        Assertions.assertEquals(user1,users.get(0));
    }
    @Test
    void testSameUserTransfer() {
        Assertions.assertThrows(SameUserTransferException.class,()-> bank.transfer(1, 1, 500));
    }
    @Test
    void testZeroAmount() {
        Assertions.assertThrows(InvalidAmountException.class, () -> bank.transfer(1, 2, 0));
    }
    @Test
    void testRemoveUser() {
        userService.removeUser(user2.getId());
        Assertions.assertNull(userService.findUserById(2));
    }

    @Test
    void testAddUser() {
        // создать user3
        User user3 = new User(3, "Ivan", 28, 3000);
        // добавить его
        userService.addUser(user3);
        // найти его

        // проверить, что найден именно user3
        assertEquals(user3, userService.findUserById(user3.getId()));
    }

}

