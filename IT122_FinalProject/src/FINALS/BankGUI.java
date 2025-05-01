package FINALS;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class BankGUI extends JFrame {
    private JTextField firstNameField, lastNameField, amountField;
    private JComboBox<String> accountTypeCombo;
    private JTextArea outputArea;
    private JButton confirmCreateButton;
    private static final String FILE_NAME = "accounts.csv";
    private static int nextAccountNo = 100000000;
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

        // Create Account Tab
        JPanel createAccountPanel = new JPanel(new GridBagLayout());
        createAccountPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Create Account", TitledBorder.CENTER, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        createAccountPanel.add(new JLabel("Account Type:"), gbc);

        gbc.gridx = 1;
        accountTypeCombo = new JComboBox<>(new String[]{"Checking", "Credit Card", "Investment"});
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

        // Add action listener for account creation
        confirmCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });

        tabbedPane.addTab("Create Account", createAccountPanel);

        // Transactions Tab
        JPanel transactionsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        transactionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Transactions", TitledBorder.CENTER, TitledBorder.TOP));

        JButton balanceInquiryButton = new JButton("Balance Inquiry");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton displayInfoButton = new JButton("Display Info");
        JButton closeAccountButton = new JButton("Close Account");

        transactionsPanel.add(balanceInquiryButton);
        transactionsPanel.add(depositButton);
        transactionsPanel.add(withdrawButton);
        transactionsPanel.add(transferButton);
        transactionsPanel.add(displayInfoButton);
        transactionsPanel.add(closeAccountButton);

        // Add action listeners for transaction buttons
        balanceInquiryButton.addActionListener(e -> performBalanceInquiry());
        depositButton.addActionListener(e -> performDeposit());
        withdrawButton.addActionListener(e -> performWithdraw());
        transferButton.addActionListener(e -> performTransfer());
        displayInfoButton.addActionListener(e -> performDisplayInfo());
        closeAccountButton.addActionListener(e -> performCloseAccount());

        tabbedPane.addTab("Transactions", transactionsPanel);

        // Output Tab
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Output", TitledBorder.CENTER, TitledBorder.TOP));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Output", outputPanel);

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

            int accountNo = nextAccountNo++;
            BankAccounts newAccount = null;

            switch (accountType) {
                case "Checking":
                    newAccount = new CheckingAccount(accountNo, accountName, 1000);
                    break;
                case "Credit Card":
                    newAccount = new CreditCardAccount(accountNo, accountName, 5000, 0);
                    break;
                case "Investment":
                    newAccount = new InvestmentAccount(accountNo, accountName, initialDeposit, 0.05);
                    break;
            }

            if (newAccount != null) {
                newAccount.deposit(initialDeposit);
                accountsList.add(newAccount);
                saveAccountsToFile();
                outputArea.setText("Account created successfully with Account No: " + accountNo);
            }
        } catch (InvalidTransactionException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void performBalanceInquiry() {
        String input = JOptionPane.showInputDialog("Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            outputArea.setText("No input provided.");
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                outputArea.setText("Balance: " + account.inquireBalance());
            } else {
                outputArea.setText("Account not found.");
            }
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid account number.");
        }
    }
    private void performDeposit() {
        String input = JOptionPane.showInputDialog("Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            outputArea.setText("No input provided.");
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                String amountInput = JOptionPane.showInputDialog("Enter deposit amount:");
                if (amountInput == null || amountInput.trim().isEmpty()) {
                    outputArea.setText("No amount provided.");
                    return;
                }

                double amount = Double.parseDouble(amountInput);
                if (amount <= 0) {
                    outputArea.setText("Deposit amount must be greater than zero.");
                    return;
                }

                account.deposit(amount);
                saveAccountsToFile();
                outputArea.setText("Deposit successful. New balance: " + account.inquireBalance());
            } else {
                outputArea.setText("Account not found.");
            }
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid input. Please enter valid numbers.");
        }
    }

    private void performWithdraw() {
        String input = JOptionPane.showInputDialog("Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            outputArea.setText("No input provided.");
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                String amountInput = JOptionPane.showInputDialog("Enter withdrawal amount:");
                if (amountInput == null || amountInput.trim().isEmpty()) {
                    outputArea.setText("No amount provided.");
                    return;
                }

                double amount = Double.parseDouble(amountInput);
                account.withdraw(amount);
                saveAccountsToFile();
                outputArea.setText("Withdrawal successful. New balance: " + account.inquireBalance());
            } else {
                outputArea.setText("Account not found.");
            }
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid input. Please enter valid numbers.");
        } catch (InsufficientFundsException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void performTransfer() {
        String fromInput = JOptionPane.showInputDialog("Enter your account number:");
        if (fromInput == null || fromInput.trim().isEmpty()) {
            outputArea.setText("No input provided.");
            return;
        }

        try {
            int fromAccountNo = Integer.parseInt(fromInput);
            BankAccounts fromAccount = findAccount(fromAccountNo);
            if (fromAccount != null) {
                String toInput = JOptionPane.showInputDialog("Enter recipient account number:");
                if (toInput == null || toInput.trim().isEmpty()) {
                    outputArea.setText("No recipient account number provided.");
                    return;
                }

                int toAccountNo = Integer.parseInt(toInput);
                BankAccounts toAccount = findAccount(toAccountNo);
                if (toAccount != null) {
                    String amountInput = JOptionPane.showInputDialog("Enter transfer amount:");
                    if (amountInput == null || amountInput.trim().isEmpty()) {
                        outputArea.setText("No amount provided.");
                        return;
                    }

                    double amount = Double.parseDouble(amountInput);
                    fromAccount.transferMoney(toAccount, amount);
                    saveAccountsToFile();
                    outputArea.setText("Transfer successful. New balance: " + fromAccount.inquireBalance());
                } else {
                    outputArea.setText("Recipient account not found.");
                }
            } else {
                outputArea.setText("Your account not found.");
            }
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid input. Please enter valid numbers.");
        } catch (InsufficientFundsException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void performDisplayInfo() {
        String input = JOptionPane.showInputDialog("Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            outputArea.setText("No input provided.");
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

                if (account instanceof CheckingAccount) {
                    info.append("\nOverdraft Limit: ").append(((CheckingAccount) account).getOverdraftLimit());
                } else if (account instanceof CreditCardAccount) {
                    info.append("\nCredit Limit: ").append(((CreditCardAccount) account).getCreditLimit());
                } else if (account instanceof InvestmentAccount) {
                    info.append("\nInterest Rate: ").append(((InvestmentAccount) account).getInterestRate());
                }

                outputArea.setText(info.toString());
            } else {
                outputArea.setText("Account not found.");
            }
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid account number.");
        }
    }

    private void performCloseAccount() {
        String input = JOptionPane.showInputDialog("Enter account number:");
        if (input == null || input.trim().isEmpty()) {
            outputArea.setText("No input provided.");
            return;
        }

        try {
            int accountNo = Integer.parseInt(input);
            BankAccounts account = findAccount(accountNo);
            if (account != null) {
                account.closeAccount();
                saveAccountsToFile();
                outputArea.setText("Account closed successfully.");
            } else {
                outputArea.setText("Account not found.");
            }
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid account number.");
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
                        case "Checking":
                            account = new CheckingAccount(accountNo, name, limit);
                            break;
                        case "CreditCard":
                            account = new CreditCardAccount(accountNo, name, limit, 0);
                            break;
                        case "Investment":
                            account = new InvestmentAccount(accountNo, name, balance, 0.05);
                            break;
                    }

                    if (account != null) {
                        account.deposit(balance);
                        accountsList.add(account);
                        nextAccountNo = Math.max(nextAccountNo, accountNo + 1);
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
            for (BankAccounts account : accountsList) {
                String accountType = account instanceof CheckingAccount ? "Checking" :
                        account instanceof CreditCardAccount ? "CreditCard" :
                                account instanceof InvestmentAccount ? "Investment" : "Unknown";
                writer.write(accountType + "," + account.getAccountName() + "," +
                        account.getAccountNo() + "," +
                        account.inquireBalance() + "," +
                        account.getStatus() + "," +
                        (account instanceof CheckingAccount ? ((CheckingAccount) account).getOverdraftLimit() : "N/A"));
                writer.newLine();
            }
            System.out.println("Accounts saved to file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving accounts to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankGUI().setVisible(true));
    }
}