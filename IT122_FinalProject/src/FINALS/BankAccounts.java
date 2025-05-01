package FINALS;

public abstract class BankAccounts {
    protected int accountNo;
    protected String accountName;
    protected double balance;
    protected String status;

    public BankAccounts(int accountNo, String accountName) {
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.balance = 0;
        this.status = "Active";
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public double inquireBalance() {
        return balance;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getStatus() {
        return status;
    }

    public void closeAccount() {
        this.status = "closed";
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal.");
        }
        balance -= amount;
    }

    public void transferMoney(BankAccounts toAccount, double amount) throws InsufficientFundsException {
        withdraw(amount);
        toAccount.deposit(amount);
    }
}