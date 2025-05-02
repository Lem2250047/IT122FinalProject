package FINALS;

public class SavingsAccount extends BankAccounts {
    private double overdraftLimit;

    public SavingsAccount(int accountNo, String accountName, double overdraftLimit) {
        super(accountNo, accountName);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}