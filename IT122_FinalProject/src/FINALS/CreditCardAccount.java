package FINALS;

public class CreditCardAccount extends BankAccounts {
    private double creditLimit;

    public CreditCardAccount(int accountNo, String accountName, double creditLimit, double balance) {
        super(accountNo, accountName);
        this.creditLimit = creditLimit;
        this.balance = balance; // Initial balance for credit card
    }

    public double getCreditLimit() {
        return creditLimit;
    }
}