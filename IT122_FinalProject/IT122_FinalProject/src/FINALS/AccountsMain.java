package FINALS;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccountsMain {
    private static final String FILE_NAME = "accounts.csv"; // CSV file to store account information
    private static int nextAccountNo = 100000000; // Start from 100000000 for 9-digit account numbers
    private static List<BankAccounts> accountsList = new ArrayList<>(); // List to hold accounts in memory

    public static void main(String[] args) {
        loadAccountsFromFile(); // Load accounts from file at startup
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Bank System Menu ---");
            System.out.println("1. Create Bank Account");
            System.out.println("2. Balance Inquiry");
            System.out.println("3. Deposit Transaction");
            System.out.println("4. Withdrawal Transaction");
            System.out.println("5. Transfer Money");
            System.out.println("6. Display Account Information");
            System.out.println("7. Close Accounts");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            choice = getValidInteger(scanner);

            switch (choice) {
                case 1:
                    createBankAccount(scanner);
                    break;
                case 2:
                    balanceInquiry(scanner);
                    break;
                case 3:
                    depositTransaction(scanner);
                    break;
                case 4:
                    withdrawalTransaction(scanner);
                    break;
                case 5:
                    transferMoney(scanner);
                    break;
                case 6:
                    displayAccountInformation(scanner);
                    break;
                case 7:
                    closeAccounts(scanner);
                    break;
                case 8:
                    System.out.println("Exiting the system. Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 8);

        scanner.close();
    }

    private static void createBankAccount(Scanner scanner) {
        try {
            System.out.print("Enter account type (1: Savings, 2: Credit Card, 3: Investment): ");
            int type = getValidInteger(scanner);

            String firstName = getValidName(scanner, "Enter First Name: ");
            String lastName = getValidName(scanner, "Enter Last Name: ");
            String accountName = firstName + " " + lastName; // Combine names

            BankAccounts newAccount = null;

            switch (type) {
                case 1:
                    double overdraftLimit = getValidPositiveDouble(scanner, "Enter overdraft limit: ");
                    newAccount = new SavingsAccount(nextAccountNo, accountName, overdraftLimit);
                    break;
                case 2:
                    double creditLimit = getValidPositiveDouble(scanner, "Enter credit limit: ");
                    newAccount = new CreditCardAccount(nextAccountNo, accountName, creditLimit, 0);
                    break;
                case 3:
                    double minBalance = getValidPositiveDouble(scanner, "Enter minimum balance for Investment Account: ");
                    double interest = 0.05; // Example fixed interest rate
                    newAccount = new InvestmentAccount(nextAccountNo, accountName, minBalance, interest);
                    break;
                default:
                    System.out.println("Invalid account type.");
                    return; // Exit the method if the account type is invalid
            }

            if (newAccount != null) {
                double initialDeposit = getValidInitialDeposit(scanner, "Enter initial deposit amount (minimum 100): ");
                newAccount.deposit(initialDeposit);
                accountsList.add(newAccount); // Add the new account to the list
                saveAccountsToFile(); // Save the new account to the CSV file
                nextAccountNo++; // Increment the account number for the next account
                System.out.println("Account created successfully with Account No: " + newAccount.getAccountNo());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while creating the account: " + e.getMessage());
        }
    }

    private static void loadAccountsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) { // Ensure there are enough parts
                    String type = parts[0];
                    String name = parts[1];
                    int accountNo = Integer.parseInt(parts[2]); // Corrected index for account number
                    double balance = Double.parseDouble(parts[3]); // Corrected index for balance
                    String status = parts[4]; // Corrected index for status
                    double limit = parts.length > 5 && !parts[5].equals("N/A") ? Double.parseDouble(parts[5]) : 0; // Corrected index for limit

                    BankAccounts account = null;
                    switch (type) {
                        case "Savings":
                            account = new SavingsAccount(accountNo, name, limit);
                            break;
                        case "CreditCard":
                            account = new CreditCardAccount(accountNo, name, limit, 0);
                            break;
                        case "Investment":
                            account = new InvestmentAccount(accountNo, name, balance, 0.05);
                            break;
                    }

                    if (account != null) {
                        account.deposit(balance); // Set the initial balance
                        accountsList.add(account); // Add the account to the list
                        nextAccountNo = Math.max(nextAccountNo, accountNo + 1); // Update nextAccountNo
                    }
                }
            }
            System.out.println("Accounts loaded from file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while loading accounts from file: " + e.getMessage());
        }
    }

    private static void balanceInquiry(Scanner scanner) {
        try {
            System.out.print("Enter account number: ");
            int accountNo = getValidInteger(scanner);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                if (account.getStatus().equalsIgnoreCase("Closed")) {
                    System.out.println("Invalid. closed account detected.");
                } else {
                    System.out.println("Balance: " + account.inquireBalance());
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during balance inquiry: " + e.getMessage());
        }
    }

    private static void depositTransaction(Scanner scanner) {
        try {
            System.out.print("Enter account number: ");
            int accountNo = getValidInteger(scanner);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                double amount = getValidInitialDeposit(scanner, "Enter deposit amount (minimum 100): ");
                account.deposit(amount);
                saveAccountsToFile(); // Save updated accounts to file
                System.out.println("Deposit successful. New balance: " + account.inquireBalance());
            } else {
                System.out.println("Account not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during deposit transaction: " + e.getMessage());
        }
    }

    private static void withdrawalTransaction(Scanner scanner) {
        try {
            System.out.print("Enter account number: ");
            int accountNo = getValidInteger(scanner);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                double amount = getValidPositiveDouble(scanner, "Enter withdrawal amount: ");
                account.withdraw(amount);
                saveAccountsToFile(); // Save updated accounts to file
                System.out.println("Withdrawal successful. New balance: " + account.inquireBalance());
            } else {
                System.out.println("Account not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during withdrawal transaction: " + e.getMessage());
        }
    }

    private static void transferMoney(Scanner scanner) {
        try {
            System.out.print("Enter your account number: ");
            int fromAccountNo = getValidInteger(scanner);
            BankAccounts fromAccount = findAccount(fromAccountNo);
            if (fromAccount != null) {
                System.out.print("Enter recipient account number: ");
                int toAccountNo = getValidInteger(scanner);
                BankAccounts toAccount = findAccount(toAccountNo);
                if (toAccount != null) {
                    double amount = getValidPositiveDouble(scanner, "Enter transfer amount: ");
                    fromAccount.transferMoney(toAccount, amount);
                    saveAccountsToFile(); // Save updated accounts to file
                    System.out.println("Transfer successful. New balance: " + fromAccount.inquireBalance());
                } else {
                    System.out.println("Recipient account not found.");
                }
            } else {
                System.out.println("Your account not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during money transfer: " + e.getMessage());
        }
    }

    private static void displayAccountInformation(Scanner scanner) {
        try {
            System.out.print("Enter account number: ");
            int accountNo = getValidInteger(scanner);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                System.out.println("Account No: " + account.getAccountNo());
                System.out.println("Account Name: " + account.getAccountName());
                System.out.println("Balance: " + account.inquireBalance());
                System.out.println("Status: " + account.getStatus());
                if (account instanceof SavingsAccount) {
                    System.out.println("Overdraft Limit: " + ((SavingsAccount) account).getOverdraftLimit());
                } else if (account instanceof CreditCardAccount) {
                    System.out.println("Credit Limit: " + ((CreditCardAccount) account).getCreditLimit());
                } else if (account instanceof InvestmentAccount) {
                    System.out.println("Interest Rate: " + ((InvestmentAccount) account).getInterestRate());
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while displaying account information: " + e.getMessage());
        }
    }

    private static void closeAccounts(Scanner scanner) {
        try {
            System.out.print("Enter account number: ");
            int accountNo = getValidInteger(scanner);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                account.closeAccount();
                saveAccountsToFile(); // Save updated accounts to file
            } else {
                System.out.println("Account not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while closing the account: " + e.getMessage());
        }
    }

    private static void saveAccountsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (BankAccounts account : accountsList) {
                String accountType = account instanceof SavingsAccount ? "Savings" :
                        account instanceof CreditCardAccount ? "CreditCard" :
                                account instanceof InvestmentAccount ? "Investment" : "Unknown";
                writer.write(accountType + "," + account.getAccountName() + "," +
                        account.getAccountNo() + "," +
                        account.inquireBalance() + "," +
                        account.getStatus() + "," +
                        (account instanceof SavingsAccount ? ((SavingsAccount) account).getOverdraftLimit() : "N/A"));
                writer.newLine();
            }
            System.out.println("Accounts saved to file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving accounts to file: " + e.getMessage());
        }
    }

    private static BankAccounts findAccount(int accountNo) {
        for (BankAccounts account : accountsList) {
            if (account.getAccountNo() == accountNo) {
                return account; // Return the found account
            }
        }
        return null; // Account not found
    }

    private static int getValidInteger(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();

                // Check if input is a single digit (0-8)
                if (input.matches("[0-8]")) {
                    return Integer.parseInt(input);
                } else {
                    System.out.print("Invalid input. Please enter a valid number: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid integer: ");
            }
        }
    }

    private static String getValidName(Scanner scanner, String prompt) {
        String name;
        while (true) {
            System.out.print(prompt);
            name = scanner.nextLine().trim();
            if (name.matches("[a-zA-Z]+")) { // Check if the name contains only letters
                return name;
            } else {
                System.out.println("Invalid input. Please enter letters only.");
            }
        }
    }

    private static double getValidPositiveDouble(Scanner scanner, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(scanner.nextLine());
                if (value > 0) {
                    return value; // Return the positive value
                } else {
                    System.out.println("Invalid input. Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double getValidInitialDeposit(Scanner scanner, String prompt) {
        double value;
        while (true) {
            value = getValidPositiveDouble(scanner, prompt);
            if (value >= 100) {
                return value; // Return the value if it is greater than or equal to 100
            } else {
                System.out.println("Invalid input. The deposit amount must be at least 100.");
            }
        }
    }
}