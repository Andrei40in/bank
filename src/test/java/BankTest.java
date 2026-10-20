import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class BankTest {


    private Bank bank;
    private User user1;
    private User user2;


    @Test
    void testFindUserDatabaseError() throws SQLException {
        UserRepository userRepository = mock(UserRepository.class);
        TransactionRepository transactionRepository = mock(TransactionRepository.class);

        Bank bank = new Bank(userRepository,transactionRepository);

        when(userRepository.findById(1)).thenThrow(new SQLException("Database error"));
        assertThrows(DatabaseException.class, () -> bank.findUser(1));
        verify(userRepository).findById(1);
    }

    @Test
    void testAddUserCallsRepository()    throws SQLException {
        UserRepository userRepository = mock(UserRepository.class);
        TransactionRepository transactionRepository = mock(TransactionRepository.class);

        User user = new User(1, "Andrey", 25, 10000);
        Bank bank = new Bank(userRepository,transactionRepository);
        bank.addUser(user);
        verify(userRepository).addUser(user);
    }

    @Test
    void testFindUser() throws SQLException {
        UserRepository userRepository = mock(UserRepository.class);
        TransactionRepository  transactionRepository = mock(TransactionRepository.class);

        User user = new User(1, "Andrey", 25, 10000);
        Bank bank = new Bank(userRepository,transactionRepository);
        when(userRepository.findById(1)).thenReturn(user);
        assertEquals(user, bank.findUser(1));
    }


    @Test
    void testSomething() throws SQLException {
        UserRepository userRepository = mock(UserRepository.class);
        TransactionRepository  transactionRepository = mock(TransactionRepository.class);

        User user = new User(1, "Andrey", 25, 10000);
        Bank bank = new Bank(userRepository,transactionRepository);
        when(userRepository.findById(1)).thenReturn(user);
        assertEquals(user, bank.findUser(1));
        verify(userRepository).findById(1);
        when(userRepository.findById(1))
                .thenThrow(new SQLException("Database error"));
        assertThrows(DatabaseException.class, () -> bank.findUser(1));
    }



    @BeforeEach
    void setUp() throws SQLException {
        Database.initialize();
        Database.clearDatabase();
        UserRepository userRepository = new UserRepository();
        TransactionRepository transactionRepository = new TransactionRepository(userRepository);
        bank = new Bank(userRepository, transactionRepository);

        user1 = new User(1, "Andrey", 25, 10000);
        user2 = new User(2, "Alex", 30, 5000);

        bank.addUser(user1);
        bank.addUser(user2);
    }
    @Test
    void testTransfer() {

        bank.transfer(1, 2, 2000);
        assertEquals(8000, bank.findUser(1).getBalance());
        assertEquals(7000, bank.findUser(2).getBalance());

    }
    @Test
    void testTransferInsufficientFunds(){
        assertThrows(
                InsufficientFundsException.class,
                () -> bank.transfer(1, 2, 20000));
        assertEquals(10000, bank.findUser(1).getBalance());
        assertEquals(5000, bank.findUser(2).getBalance());
    }

    @Test
    void testTransferUserNotFound(){
        assertThrows(UserNotFoundException.class, () -> bank.transfer(1, 999, 1000));
        assertEquals(10000, bank.findUser(1).getBalance());
    }

    @Test
    void testTransferCreatesTransaction(){
        bank.transfer(1, 2, 2000);
        List<Transaction> transactions = bank.getUserTransactions(1);
        assertEquals(1,bank.getUserTransactionCount(1));
        assertEquals(2000,transactions.get(0).getAmount());
        assertEquals(1,transactions.get(0).getSender().getId());
        assertEquals(2,transactions.get(0).getRecipient().getId());
    }

    @Test
    void testTransferToSameUser(){
        assertThrows(SameUserTransferException.class, () -> bank.transfer(1,1,1000));
        assertEquals(10000,bank.findUser(1).getBalance());
    }

    @Test
    void testTransferInvalidAmount(){
        assertThrows(InvalidAmountException.class, () -> bank.transfer(1,2,-50));
        assertEquals(10000,bank.findUser(1).getBalance());
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
        bank.deleteUser(user2.getId());
        Assertions.assertNull(bank.findUser(2));
    }

    @Test
    void testAddUser() {
        // создать user3
        User user3 = new User(3, "Ivan", 28, 3000);
        // добавить его
        bank.addUser(user3);
        // найти его

        // проверить, что найден именно user3
        assertEquals(user3, bank.findUser(user3.getId()));
    }
    @Test
    void testUserTransactionCount(){
        bank.transfer(1,2,500);
        bank.transfer(1,2,500);
        assertEquals(2,bank.getUserTransactionCount(1));
    }

    @Test
    void testFindUserTransactions() {

        bank.transfer(1,2,500);
        bank.transfer(1,2,500);
        assertEquals(2,bank.getUserTransactions(1).size());


    }

    @Test
    void testTransferRollback() throws SQLException {
        // Здесь проверим, что происходит при ошибке
        // во время добавления записи о переводе.
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
            Statement statement = connection.createStatement()) {

            statement.execute("DROP TRIGGER IF EXISTS fail_transaction_insert");
            try {
                statement.execute("""
                        CREATE TRIGGER fail_transaction_insert
                        BEFORE INSERT ON transactions
                        BEGIN 
                            SELECT RAISE(ABORT, 'Forced transaction failure');
                        END;
                        """);

                assertThrows(DatabaseException.class, () ->
                        bank.transfer(1, 2, 2000));

            }finally {
                statement.execute("DROP TRIGGER IF EXISTS fail_transaction_insert");
            }
        }

            assertEquals(10000, bank.findUser(1).getBalance());
            assertEquals(5000,bank.findUser(2).getBalance());
            assertEquals(0,bank.getUserTransactionCount(1));

    }
    @Test
    void testTransferRollbackWhenRecipientUpdateFails() throws SQLException {
        // Здесь проверим откат,
        // если не удалось обновить баланс получателя.
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
        Statement statement = connection.createStatement()){
            statement.execute("DROP TRIGGER IF EXISTS fail_recipient_update");
            try {
                statement.execute("""
                CREATE TRIGGER fail_recipient_update
                BEFORE UPDATE OF balance ON users
                WHEN OLD.id = 2
                BEGIN
                    SELECT RAISE(ABORT, 'Forced balance update failure');
                END;
                """);

                assertThrows(DatabaseException.class, () -> bank.transfer(1,2,2000));

            } finally {
                statement.execute("DROP TRIGGER IF EXISTS fail_recipient_update");
            }
        }
        assertEquals(10000, bank.findUser(1).getBalance());
        assertEquals(5000,bank.findUser(2).getBalance());
        assertEquals(0,bank.getUserTransactionCount(0));


    }

}

