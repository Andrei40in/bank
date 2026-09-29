public class Transaction {
    private int id;
    private User sender;
    private User recipient;
    private double amount;

    public Transaction(int id, User sender, User recipient, double amount) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public double getAmount() {
        return amount;
    }
}
