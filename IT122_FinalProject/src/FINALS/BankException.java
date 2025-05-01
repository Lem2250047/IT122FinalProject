package FINALS;

// General exception for bank operations
public class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }
}

// Specific exception for insufficient funds
class InsufficientFundsException extends BankException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// Specific exception for invalid transactions
class InvalidTransactionException extends BankException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}

