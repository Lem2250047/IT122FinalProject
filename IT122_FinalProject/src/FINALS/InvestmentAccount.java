package FINALS;

public class InvestmentAccount extends BankAccounts {
    private double interestRate;

    public InvestmentAccount(int accountNo, String accountName, double balance, double interestRate) {
        super(accountNo, accountName);
        this.balance = balance; // Initial balance for investment
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }
}