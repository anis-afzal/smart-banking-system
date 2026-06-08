public class Account {
    private int accountNumber;
    private String accountHolder;
    private double balance;

    // Constructor to build the account object
    public Account(int accountNumber, String accountHolder, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
    }

    // Getters to read the data
    public int getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    // Setter to update the balance safely
    public void setBalance(double balance) {
        this.balance = balance;
    }
}