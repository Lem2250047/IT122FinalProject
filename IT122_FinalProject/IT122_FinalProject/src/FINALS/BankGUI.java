package FINALS;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BankGUI extends JFrame {
    private JTextField firstNameField, lastNameField, amountField;
    private JComboBox<String> accountTypeCombo;
    private JButton confirmCreateButton;
    private static final String FILE_NAME = "accounts.csv";
    private static int savingsAccountCounter = 0;
    private static int creditCardAccountCounter = 0;
    private static int investmentAccountCounter = 0;
    private static List<BankAccounts> accountsList = new ArrayList<>();

    public BankGUI() {
        setTitle("Bank System GUI");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load accounts from file
        loadAccountsFromFile();

        // Main container with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Transactions Tab
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Transactions", TitledBorder.CENTER, TitledBorder.TOP));

        // Top Panel for Account Creation
        JPanel createAccountPanel = new JPanel(new GridBagLayout());
        createAccountPanel.setBorder(BorderFactory.createTitledBorder("Create Account"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        createAccountPanel.add(new JLabel("Account Type:"), gbc);

        gbc.gridx = 1;
        accountTypeCombo = new JComboBox<>(new String[]{"Savings", "Credit Card", "Investment"});
        createAccountPanel.add(accountTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        createAccountPanel.add(new JLabel("First Name:"), gbc);

        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        createAccountPanel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        createAccountPanel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        createAccountPanel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        createAccountPanel.add(new JLabel("Initial Deposit:"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(15);
        createAccountPanel.add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        confirmCreateButton = new JButton("Create Account");
        createAccountPanel.add(confirmCreateButton, gbc);

        confirmCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });

        transactionsPanel.add(createAccountPanel, BorderLayout.NORTH);

        // Middle Panel for Transaction Buttons
        JPanel transactionButtonsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JButton balanceInquiryButton = new JButton("Balance Inquiry");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton displayInfoButton = new JButton("Display Info");
        JButton closeAccountButton = new JButton("Close Account");

        transactionButtonsPanel.add(balanceInquiryButton);
        transactionButtonsPanel.add(depositButton);
        transactionButtonsPanel.add(withdrawButton);
        transactionButtonsPanel.add(transferButton);
        transactionButtonsPanel.add(displayInfoButton);
        transactionButtonsPanel.add(closeAccountButton);

        balanceInquiryButton.addActionListener(e -> performBalanceInquiry());
        depositButton.addActionListener(e -> performDeposit());
        withdrawButton.addActionListener(e -> performWithdraw());
        transferButton.addActionListener(e -> performTransfer());
        displayInfoButton.addActionListener(e -> performDisplayInfo());
        closeAccountButton.addActionListener(e -> performCloseAccount());

        transactionsPanel.add(transactionButtonsPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Transactions", transactionsPanel);

        // Add tabbedPane to the frame
        add(tabbedPane);
    }

    private void createAccount() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
                throw new InvalidTransactionException("Names must contain only letters.");
            }

            String accountName = firstName + " " + lastName;
            String accountType = (String) accountTypeCombo.getSelectedItem();

            double initialDeposit;
            try {
                initialDeposit = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException e) {
                throw new InvalidTransactionException("Invalid input. Please enter a valid number for deposit.");
            }

            if (initialDeposit < 100) {
                throw new InvalidTransactionException("Initial deposit must be at least 100.");
            }

            int accountNo = generateAccountNumber(accountType);
            BankAccounts newAccount = null;

            switch (accountType) {
                case "Savings":
                    newAccount = new SavingsAccount(accountNo, accountName, 1000);
                    break;
                case "Credit Card":
                    newAccount = new CreditCardAccount(accountNo, accountName, 5000, 0);
                    break;
                case "Investment":
                    newAccount = new InvestmentAccount(accountNo, accountName, initialDeposit, 0.05);
                    break;
                default:
                    throw new InvalidTransactionException("Invalid account type.");
            }

            if (newAccount != null) {
                newAccount.deposit(initialDeposit);
                accountsList.add(newAccount);
                saveAccountsToFile();
                JOptionPane.showMessageDialog(this, "Account created successfully with Account No: " + accountNo, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (InvalidTransactionException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int generateAccountNumber(String accountType) {
        switch (accountType) {
            case "Savings":
                return 1000000 + (savingsAccountCounter++); // Prefix 1 for Savings
            case "Credit Card":
                return 2000000 + (creditCardAccountCounter++); // Prefix 2 for Credit Card
            case "Investment":
                return 3000000 + (investmentAccountCounter++); // Prefix 3 for Investment
            default:
                throw new IllegalArgumentException("Invalid account type.");
        }
    }

    private void performBalanceInquiry() {
        String accountNoInput = JOptionPane.showInputDialog(this, "Enter Account Number:");
        if (accountNoInput == null || accountNoInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No input provided.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int accountNo = Integer.parseInt(accountNoInput);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                StringBuilder details = new StringBuilder();
                details.append("Account Details:\n")
                        .append("Account Number: ").append(account.getAccountNo()).append("\n")
                        .append("Account Name: ").append(account.getAccountName()).append("\n")
                        .append("Account Type: ").append(account.getAccountType()).append("\n")
                        .append("Balance: ").append(account.inquireBalance()).append("\n")
                        .append("Status: ").append(account.getStatus());

                if (account instanceof SavingsAccount) {
                    details.append("\nOverdraft Limit: ").append(((SavingsAccount) account).getOverdraftLimit());
                } else if (account instanceof CreditCardAccount) {
                    details.append("\nCredit Limit: ").append(((CreditCardAccount) account).getCreditLimit());
                } else if (account instanceof InvestmentAccount) {
                    details.append("\nInterest Rate: ").append(((InvestmentAccount) account).getInterestRate());
                }

                JOptionPane.showMessageDialog(this, details.toString(), "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid account number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDeposit() {
        String input = JOptionPane.showInputDialog(this, "Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No input provided.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                String amountInput = JOptionPane.showInputDialog(this, "Enter deposit amount:");
                if (amountInput == null || amountInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No amount provided.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double amount = Double.parseDouble(amountInput);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Deposit amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                account.deposit(amount);
                saveAccountsToFile();
                JOptionPane.showMessageDialog(this, "Deposit successful. New balance: " + account.inquireBalance(), "Deposit", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void performWithdraw() {
        String input = JOptionPane.showInputDialog(this, "Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No input provided.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                String amountInput = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
                if (amountInput == null || amountInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No amount provided.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double amount = Double.parseDouble(amountInput);
                account.withdraw(amount);
                saveAccountsToFile();
                JOptionPane.showMessageDialog(this, "Withdrawal successful. New balance: " + account.inquireBalance(), "Withdraw", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InsufficientFundsException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performTransfer() {
        String fromInput = JOptionPane.showInputDialog(this, "Enter your account number:");
        if (fromInput == null || fromInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No input provided.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int fromAccountNo = Integer.parseInt(fromInput);
            BankAccounts fromAccount = findAccount(fromAccountNo);
            if (fromAccount != null) {
                String toInput = JOptionPane.showInputDialog(this, "Enter recipient account number:");
                if (toInput == null || toInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No recipient account number provided.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int toAccountNo = Integer.parseInt(toInput);
                BankAccounts toAccount = findAccount(toAccountNo);
                if (toAccount != null) {
                    String amountInput = JOptionPane.showInputDialog(this, "Enter transfer amount:");
                    if (amountInput == null || amountInput.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No amount provided.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double amount = Double.parseDouble(amountInput);
                    fromAccount.transferMoney(toAccount, amount);
                    saveAccountsToFile();
                    JOptionPane.showMessageDialog(this, "Transfer successful. New balance: " + fromAccount.inquireBalance(), "Transfer", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Recipient account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Your account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InsufficientFundsException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDisplayInfo() {
        String input = JOptionPane.showInputDialog(this, "Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No input provided.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                StringBuilder info = new StringBuilder();
                info.append("Account No: ").append(account.getAccountNo()).append("\n")
                        .append("Account Name: ").append(account.getAccountName()).append("\n")
                        .append("Balance: ").append(account.inquireBalance()).append("\n")
                        .append("Status: ").append(account.getStatus());

                if (account instanceof SavingsAccount) {
                    info.append("\nOverdraft Limit: ").append(((SavingsAccount) account).getOverdraftLimit());
                } else if (account instanceof CreditCardAccount) {
                    info.append("\nCredit Limit: ").append(((CreditCardAccount) account).getCreditLimit());
                } else if (account instanceof InvestmentAccount) {
                    info.append("\nInterest Rate: ").append(((InvestmentAccount) account).getInterestRate());
                }

                JOptionPane.showMessageDialog(this, info.toString(), "Account Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid account number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performCloseAccount() {
        String input = JOptionPane.showInputDialog("Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No input provided.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                account.closeAccount();
                saveAccountsToFile();
                JOptionPane.showMessageDialog(this, "Account closed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid account number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BankAccounts findAccount(int accountNo) {
        for (BankAccounts account : accountsList) {
            if (account.getAccountNo() == accountNo) {
                return account;
            }
        }
        return null;
    }

    private void loadAccountsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String type = parts[0];
                    String name = parts[1];
                    int accountNo = Integer.parseInt(parts[2]);
                    double balance = Double.parseDouble(parts[3]);
                    String status = parts[4];
                    double limit = parts.length > 5 && !parts[5].equals("N/A") ? Double.parseDouble(parts[5]) : 0;

                    BankAccounts account = null;
                    switch (type) {
                        case "Savings":
                            account = new SavingsAccount(accountNo, name, limit);
                            savingsAccountCounter = Math.max(savingsAccountCounter, accountNo % 1000000 + 1);
                            break;
                        case "CreditCard":
                            account = new CreditCardAccount(accountNo, name, limit, 0);
                            creditCardAccountCounter = Math.max(creditCardAccountCounter, accountNo % 1000000 + 1);
                            break;
                        case "Investment":
                            account = new InvestmentAccount(accountNo, name, balance, 0.05);
                            investmentAccountCounter = Math.max(investmentAccountCounter, accountNo % 1000000 + 1);
                            break;
                    }

                    if (account != null) {
                        account.deposit(balance);
                        accountsList.add(account);
                    }
                }
            }
            System.out.println("Accounts loaded from file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while loading accounts from file: " + e.getMessage());
        }
    }

    private void saveAccountsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // Sort accounts alphabetically by account name
            accountsList.sort((a, b) -> a.getAccountName().compareToIgnoreCase(b.getAccountName()));

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
            System.out.println("Accounts updated in file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating accounts in file: " + e.getMessage());
        }
    }
    private void showStartingPage() {
        JFrame startingFrame = new JFrame("Welcome");
        startingFrame.setSize(400, 200);
        startingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startingFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to the Bank System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(e -> {
            startingFrame.dispose(); // Close the starting page
            SwingUtilities.invokeLater(() -> new BankGUI().setVisible(true)); // Open the main GUI
        });
        panel.add(continueButton, BorderLayout.SOUTH);

        startingFrame.add(panel);
        startingFrame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankGUI().showStartingPage());
    }
}