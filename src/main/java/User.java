public class User {
    private final int id;
    private String name;
    private int age;
    private double balance;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)){
            return false;
        }
        User other = (User) o;
        return getId() == other.getId();
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getBalance() {
        return balance;
    }


    User(int id, String name, int age, double balance){
        this.id = id;
        this.name = name;
        this.age = age;
        this.balance = balance;
    }
    void deposit(double balances){
        balance += balances;
    }
    void withdraw(double balancem){
        if ((balance - balancem) < 0){
            System.out.println("Не хватает денег!");
        } else {
            balance -= balancem;
        }
    }
    boolean isAdult(){
        return age >= 18;

    }
}
