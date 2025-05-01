package FINALS;

public class CheckingAccount extends BankAccounts {
    private double overdraftLimit;

    public CheckingAccount(int accountNo, String accountName, double overdraftLimit) {
        super(accountNo, accountName);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}