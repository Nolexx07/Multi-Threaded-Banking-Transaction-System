package frontend;

import accounts.Account;
import accounts.AccountRepository;
import accounts.SavingsAccount;
import accounts.SalaryAccount;
import atm.ATMService;
import atm.ATMRequest;
import fraud.FraudMonitor;
import notification.EmailNotifier;
import reporting.ReportGenerator;
import reporting.LogPdfExporter;
import transactions.TransactionProcessor;
import transactions.TransactionResult;
import transactions.TransactionType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import javax.swing.UIManager;

/**
 * Main GUI Application for Banking System
 */
public class BankingGUI extends JFrame {
    private AccountRepository accountRepository;
    private TransactionProcessor transactionProcessor;
    private ATMService atmService;
    private FraudMonitor fraudMonitor;
    private ReportGenerator reportGenerator;
    
    private Account currentAccount;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Components
    private JTextField accountIdField;
    private JPasswordField pinField;
    private JLabel balanceLabel;
    private JLabel accountInfoLabel;
    private JTextArea transactionLogArea;
    private JTextArea fraudAlertsArea;
    private JTable accountTable;
    private JTextArea adminFraudArea;
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";
    private final LogPdfExporter logPdfExporter = new LogPdfExporter();
    
    public BankingGUI() {
        initializeBackend();
        initializeGUI();
    }
    
    private void initializeBackend() {
        accountRepository = new AccountRepository();
        
        // Create sample accounts
        accountRepository.addAccount(new SavingsAccount(1001, "Alice Johnson", 5000.0, 1234));
        accountRepository.addAccount(new SavingsAccount(1002, "Bob Smith", 3000.0, 5678));
        accountRepository.addAccount(new SavingsAccount(1003, "Charlie Brown", 7500.0, 9012));
        accountRepository.addAccount(new SalaryAccount(2001, "Diana Prince", 2000.0, 3456));
        accountRepository.addAccount(new SalaryAccount(2002, "Edward Norton", 4500.0, 7890));
        
        EmailNotifier notifier = new EmailNotifier("alerts@example.com", "no-reply@bank-sim.local");
        fraudMonitor = new FraudMonitor(accountRepository, notifier);
        transactionProcessor = new TransactionProcessor(accountRepository, fraudMonitor);
        atmService = new ATMService(transactionProcessor);
        reportGenerator = new ReportGenerator(accountRepository);
    }
    
    private void initializeGUI() {
        setTitle("Multi-Threaded Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create panels
        JPanel loginPanel = createLoginPanel();
        JPanel adminLoginPanel = createAdminLoginPanel();
        JPanel dashboardPanel = createDashboardPanel();
        JPanel adminDashboardPanel = createAdminDashboardPanel();
        
        mainPanel.add(loginPanel, "LOGIN_USER");
        mainPanel.add(adminLoginPanel, "LOGIN_ADMIN");
        mainPanel.add(dashboardPanel, "DASHBOARD_USER");
        mainPanel.add(adminDashboardPanel, "DASHBOARD_ADMIN");
        
        add(mainPanel);
        
        // Show login panel first
        cardLayout.show(mainPanel, "LOGIN_USER");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 76, 129));
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Welcome to the Banking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
        
        // Account ID
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 1;
        JLabel accountIdLabel = new JLabel("Account ID:");
        accountIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accountIdLabel.setForeground(Color.WHITE);
        panel.add(accountIdLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        accountIdField = new JTextField(15);
        accountIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(accountIdField, gbc);
        
        // PIN
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pinLabel.setForeground(Color.WHITE);
        panel.add(pinLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pinField = new JPasswordField(15);
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(pinField, gbc);
        
        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(0, 181, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(220, 40));
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton, gbc);
        
        // Info label
        gbc.gridy = 4;
        JLabel infoLabel = new JLabel("<html><center>Sample Accounts:<br>" +
            "1001 (PIN: 1234), 1002 (PIN: 5678), 1003 (PIN: 9012)<br>" +
            "2001 (PIN: 3456), 2002 (PIN: 7890)</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(220, 230, 240));
        panel.add(infoLabel, gbc);

        // Switch to admin login
        gbc.gridy = 5;
        JButton adminSwitch = new JButton("Admin Login");
        adminSwitch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        adminSwitch.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN_ADMIN"));
        panel.add(adminSwitch, gbc);
        
        // Make Enter key trigger login
        getRootPane().setDefaultButton(loginButton);
        pinField.addActionListener(e -> handleLogin());
        
        return panel;
    }

    private JPanel createAdminLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(33, 37, 41));
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Admin Console Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel userLbl = new JLabel("Admin ID:");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLbl.setForeground(Color.WHITE);
        panel.add(userLbl, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField userField = new JTextField(15);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLbl.setForeground(Color.WHITE);
        panel.add(passLbl, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new JButton("Login as Admin");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(0, 123, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (ADMIN_USER.equals(u) && ADMIN_PASS.equals(p)) {
                JOptionPane.showMessageDialog(this, "Admin login successful");
                refreshAccountTable();
                refreshAdminFraud();
                cardLayout.show(mainPanel, "DASHBOARD_ADMIN");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginBtn, gbc);

        gbc.gridy = 4;
        JButton backBtn = new JButton("User Login");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN_USER"));
        panel.add(backBtn, gbc);

        return panel;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(243, 246, 251));
        
        // Top panel - Account Info and Logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(57, 111, 167));
        topPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        accountInfoLabel = new JLabel();
        accountInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        accountInfoLabel.setForeground(Color.WHITE);
        topPanel.add(accountInfoLabel, BorderLayout.WEST);
        
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(Color.WHITE);
        topPanel.add(balanceLabel, BorderLayout.CENTER);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> handleLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Transaction buttons
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new TitledBorder("Transactions"));
        centerPanel.setBackground(Color.WHITE);

        JPanel txnGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        txnGrid.setBackground(Color.WHITE);
        
        JButton withdrawButton = createStyledButton("Withdraw", new Color(217, 83, 79));
        withdrawButton.addActionListener(e -> showWithdrawDialog());
        txnGrid.add(withdrawButton);
        
        JButton depositButton = createStyledButton("Deposit", new Color(76, 175, 80));
        depositButton.addActionListener(e -> showDepositDialog());
        txnGrid.add(depositButton);
        
        JButton transferButton = createStyledButton("Transfer", new Color(255, 152, 0));
        transferButton.addActionListener(e -> showTransferDialog());
        txnGrid.add(transferButton);
        
        JButton balanceButton = createStyledButton("Check Balance", new Color(63, 81, 181));
        balanceButton.addActionListener(e -> handleBalanceInquiry());
        txnGrid.add(balanceButton);

        centerPanel.add(txnGrid, BorderLayout.CENTER);

        // Extra tools row
        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolsPanel.setBackground(Color.WHITE);
        JButton parallelTestBtn = createStyledButton("Parallel Test", new Color(0, 123, 167));
        parallelTestBtn.setPreferredSize(new Dimension(160, 50));
        parallelTestBtn.addActionListener(e -> runParallelTest());

        JButton fraudTestBtn = createStyledButton("Fraud Test", new Color(205, 92, 92));
        fraudTestBtn.setPreferredSize(new Dimension(160, 50));
        fraudTestBtn.addActionListener(e -> runFraudTest());

        JButton exportPdfBtn = createStyledButton("Export Logs (PDF)", new Color(46, 139, 87));
        exportPdfBtn.setPreferredSize(new Dimension(180, 50));
        exportPdfBtn.addActionListener(e -> exportLogsPdf());

        JButton exportDocsBtn = createStyledButton("Export Docs (PDF)", new Color(32, 178, 170));
        exportDocsBtn.setPreferredSize(new Dimension(180, 50));
        exportDocsBtn.addActionListener(e -> exportDocsPdf());

        JButton changePinBtn = createStyledButton("Change PIN", new Color(123, 104, 238));
        changePinBtn.setPreferredSize(new Dimension(160, 50));
        changePinBtn.addActionListener(e -> handleChangePin());

        toolsPanel.add(parallelTestBtn);
        toolsPanel.add(fraudTestBtn);
        toolsPanel.add(exportPdfBtn);
        toolsPanel.add(exportDocsBtn);
        toolsPanel.add(changePinBtn);

        centerPanel.add(toolsPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Transaction Log and Fraud Alerts
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Transaction Log
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("Transaction Log"));
        transactionLogArea = new JTextArea(8, 30);
        transactionLogArea.setEditable(false);
        transactionLogArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        transactionLogArea.setBackground(Color.WHITE);
        transactionLogArea.setForeground(new Color(33, 37, 41));
        JScrollPane logScrollPane = new JScrollPane(transactionLogArea);
        logPanel.add(logScrollPane);
        bottomPanel.add(logPanel);
        
        // Fraud Alerts
        JPanel fraudPanel = new JPanel(new BorderLayout());
        fraudPanel.setBorder(new TitledBorder("Fraud Alerts"));
        fraudAlertsArea = new JTextArea(8, 30);
        fraudAlertsArea.setEditable(false);
        fraudAlertsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        fraudAlertsArea.setBackground(new Color(255, 243, 243));
        fraudAlertsArea.setForeground(new Color(156, 39, 6));
        JScrollPane fraudScrollPane = new JScrollPane(fraudAlertsArea);
        fraudPanel.add(fraudScrollPane);
        bottomPanel.add(fraudPanel);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createAdminDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(new EmptyBorder(10,10,10,10));
        panel.setBackground(new Color(245, 247, 250));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(52, 58, 64));
        top.setBorder(new EmptyBorder(10,10,10,10));
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        top.add(title, BorderLayout.WEST);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN_ADMIN"));
        top.add(logout, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,2,10,10));

        // Accounts table
        String[] cols = {"ID","Name","Type","Balance","Frozen","FailedPINs","TxnCount"};
        DefaultTableModel model = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        accountTable = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(accountTable);
        JPanel accountsPanel = new JPanel(new BorderLayout());
        accountsPanel.setBorder(new TitledBorder("Accounts"));
        accountsPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel adminBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8,8));
        JButton freezeBtn = new JButton("Freeze");
        freezeBtn.addActionListener(e -> adminFreeze(true));
        JButton unfreezeBtn = new JButton("Unfreeze");
        unfreezeBtn.addActionListener(e -> adminFreeze(false));
        JButton resetPinAttemptsBtn = new JButton("Reset PIN Attempts");
        resetPinAttemptsBtn.addActionListener(e -> adminResetPinAttempts());
        JButton setPinBtn = new JButton("Set Temp PIN");
        setPinBtn.addActionListener(e -> adminSetTempPin());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAccountTable());
        JButton exportAdminBtn = new JButton("Export Admin Report (PDF)");
        exportAdminBtn.addActionListener(e -> exportAdminReport());
        adminBtns.add(freezeBtn);
        adminBtns.add(unfreezeBtn);
        adminBtns.add(resetPinAttemptsBtn);
        adminBtns.add(setPinBtn);
        adminBtns.add(refreshBtn);
        adminBtns.add(exportAdminBtn);
        accountsPanel.add(adminBtns, BorderLayout.SOUTH);

        // Fraud alerts area
        JPanel fraudPanel = new JPanel(new BorderLayout());
        fraudPanel.setBorder(new TitledBorder("Fraud Alerts (Log View)"));
        adminFraudArea = new JTextArea(20,40);
        adminFraudArea.setEditable(false);
        adminFraudArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        JScrollPane fraudScroll = new JScrollPane(adminFraudArea);
        JButton refreshFraudBtn = new JButton("Refresh Alerts");
        refreshFraudBtn.addActionListener(e -> refreshAdminFraud());
        fraudPanel.add(fraudScroll, BorderLayout.CENTER);
        fraudPanel.add(refreshFraudBtn, BorderLayout.SOUTH);

        center.add(accountsPanel);
        center.add(fraudPanel);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 70));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        return button;
    }
    
    private void handleLogin() {
        try {
            int accountId = Integer.parseInt(accountIdField.getText().trim());
            int pin = Integer.parseInt(new String(pinField.getPassword()));
            
            Account account = accountRepository.getAccount(accountId);
            if (account == null) {
                JOptionPane.showMessageDialog(this, "Account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (account.validatePin(pin)) {
                currentAccount = account;
                updateDashboard();
                cardLayout.show(mainPanel, "DASHBOARD_USER");
                accountIdField.setText("");
                pinField.setText("");
                logMessage("Login successful for Account: " + accountId);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid PIN! Failed attempts: " + account.getFailedPinAttempts(), 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleLogout() {
        currentAccount = null;
        cardLayout.show(mainPanel, "LOGIN_USER");
        transactionLogArea.setText("");
        fraudAlertsArea.setText("");
    }
    
    private void updateDashboard() {
        if (currentAccount != null) {
            accountInfoLabel.setText("Account: " + currentAccount.getAccountId() + 
                " | " + currentAccount.getName());
            refreshBalance();
        }
    }
    
    private void refreshBalance() {
        SwingUtilities.invokeLater(() -> {
            if (currentAccount != null) {
                balanceLabel.setText("Balance: $" + String.format("%.2f", currentAccount.getBalance()));
            }
        });
    }
    
    private void showWithdrawDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:", "Withdraw", JOptionPane.QUESTION_MESSAGE);
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Integer pin = promptForPin();
                if (pin == null) return; // cancelled
                if (!promptOtp()) return;
                processTransaction(TransactionType.WITHDRAW, amount, null, pin);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showDepositDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:", "Deposit", JOptionPane.QUESTION_MESSAGE);
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Integer pin = promptForPin();
                if (pin == null) return; // cancelled
                // OTP optional for deposit? keep simple: no OTP for deposit
                processTransaction(TransactionType.DEPOSIT, amount, null, pin);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showTransferDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField targetAccountField = new JTextField();
        JTextField amountField = new JTextField();
        
        panel.add(new JLabel("Target Account ID:"));
        panel.add(targetAccountField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Money", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int targetAccountId = Integer.parseInt(targetAccountField.getText().trim());
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Integer pin = promptForPin();
                if (pin == null) return; // cancelled
                if (!promptOtp()) return;
                processTransaction(TransactionType.TRANSFER, amount, targetAccountId, pin);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleBalanceInquiry() {
        Integer pin = promptForPin();
        if (pin == null) return;
        processTransaction(TransactionType.BALANCE_INQUIRY, 0, null, pin);
    }
    
    private Integer promptForPin() {
        JPasswordField pinInput = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, pinInput, "Enter PIN", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            try {
                return Integer.parseInt(new String(pinInput.getPassword()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "PIN must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null; // cancelled
    }

    private boolean promptOtp() {
        String otp = String.format("%06d", (int)(Math.random() * 1_000_000));
        JOptionPane.showMessageDialog(this, "Your OTP is: " + otp + "\n(Enter this to proceed)", "OTP Verification", JOptionPane.INFORMATION_MESSAGE);
        String entered = JOptionPane.showInputDialog(this, "Enter OTP:");
        if (entered == null) return false;
        if (!otp.equals(entered.trim())) {
            JOptionPane.showMessageDialog(this, "Invalid OTP.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void processTransaction(TransactionType type, double amount, Integer targetAccountId, int pin) {
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "Please login first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            logMessage("Processing " + type + " transaction...");
        });
        
        // Process transaction in background thread
        new Thread(() -> {
            try {
                ATMRequest request;
                String atmId = "GUI-ATM";
                String customerName = currentAccount.getName();
                
                switch (type) {
                    case WITHDRAW:
                        request = atmService.createWithdrawRequest(atmId, customerName, 
                            currentAccount.getAccountId(), amount, pin);
                        break;
                    case DEPOSIT:
                        request = atmService.createDepositRequest(atmId, customerName, 
                            currentAccount.getAccountId(), amount, pin);
                        break;
                    case TRANSFER:
                        if (targetAccountId == null) {
                            SwingUtilities.invokeLater(() -> 
                                JOptionPane.showMessageDialog(this, "Target account required!", 
                                    "Error", JOptionPane.ERROR_MESSAGE));
                            return;
                        }
                        request = atmService.createTransferRequest(atmId, customerName, 
                            currentAccount.getAccountId(), targetAccountId, amount, pin);
                        break;
                    case BALANCE_INQUIRY:
                        request = atmService.createBalanceInquiryRequest(atmId, customerName, 
                            currentAccount.getAccountId(), pin);
                        break;
                    default:
                        return;
                }
                
                Future<TransactionResult> future = atmService.processRequest(request);
                TransactionResult result = future.get();
                
                SwingUtilities.invokeLater(() -> {
                    if (result.isSuccess()) {
                        logMessage("✓ " + result.getMessage());
                        refreshBalance();
                        JOptionPane.showMessageDialog(this, result.getMessage(), 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        logMessage("✗ " + result.getMessage());
                        JOptionPane.showMessageDialog(this, result.getMessage(), 
                            "Transaction Failed", JOptionPane.ERROR_MESSAGE);
                    }
                    
                    // Check for fraud alerts
                    updateFraudAlerts();
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    logMessage("✗ Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, "Transaction error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    // Run multiple transactions concurrently to demonstrate thread pool / Futures
    private void runParallelTest() {
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "Please login first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer pin = promptForPin();
        if (pin == null) return;
        logMessage("Running parallel test (5 concurrent transactions)...");
        new Thread(() -> {
            try {
                Future<TransactionResult> f1 = atmService.processRequest(
                    atmService.createDepositRequest("GUI-PT", currentAccount.getName(), currentAccount.getAccountId(), 100, pin));
                Future<TransactionResult> f2 = atmService.processRequest(
                    atmService.createWithdrawRequest("GUI-PT", currentAccount.getName(), currentAccount.getAccountId(), 80, pin));
                Future<TransactionResult> f3 = atmService.processRequest(
                    atmService.createBalanceInquiryRequest("GUI-PT", currentAccount.getName(), currentAccount.getAccountId(), pin));
                Future<TransactionResult> f4 = atmService.processRequest(
                    atmService.createDepositRequest("GUI-PT", currentAccount.getName(), currentAccount.getAccountId(), 60, pin));
                Future<TransactionResult> f5 = atmService.processRequest(
                    atmService.createWithdrawRequest("GUI-PT", currentAccount.getName(), currentAccount.getAccountId(), 40, pin));

                List<Future<TransactionResult>> list = Arrays.asList(f1, f2, f3, f4, f5);
                for (Future<TransactionResult> f : list) {
                    TransactionResult r = f.get();
                    SwingUtilities.invokeLater(() -> {
                        logMessage((r.isSuccess() ? "✓ " : "✗ ") + r.getMessage());
                        refreshBalance();
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Parallel test error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // Trigger fraud scenarios: rapid withdrawals, high-value, and failed PIN
    private void runFraudTest() {
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "Please login first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer pin = promptForPin();
        if (pin == null) return;
        logMessage("Running fraud test (rapid + high-value + failed PIN)...");
        new Thread(() -> {
            try {
                // Rapid small withdrawals
                for (int i = 0; i < 4; i++) {
                    atmService.processRequest(
                        atmService.createWithdrawRequest("GUI-FT", currentAccount.getName(), currentAccount.getAccountId(), 50, pin));
                    Thread.sleep(150);
                }
                // High-value withdrawal (if funds allow)
                atmService.processRequest(
                    atmService.createWithdrawRequest("GUI-FT", currentAccount.getName(), currentAccount.getAccountId(), 6000, pin));
                // Failed PIN attempts
                for (int i = 0; i < 3; i++) {
                    atmService.processRequest(
                        atmService.createBalanceInquiryRequest("GUI-FT", currentAccount.getName(), currentAccount.getAccountId(), 9999));
                    Thread.sleep(100);
                }
                SwingUtilities.invokeLater(() -> {
                    logMessage("Fraud test triggered. Check logs/fraud_report.txt for alerts.");
                    updateFraudAlerts();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Fraud test error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // Export logs to a printable PDF
    private void exportLogsPdf() {
        String output = "logs/logs_export.pdf";
        List<String> files = Arrays.asList("logs/atm.log", "logs/transactions.log", "logs/fraud_report.txt", "logs/daily_report.txt");
        logPdfExporter.exportLogsToPdf(output, files);
        JOptionPane.showMessageDialog(this, "PDF exported to " + output, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // Export documentation (README, summary, features, compile/run guide) to PDF
    private void exportDocsPdf() {
        String output = "logs/docs_export.pdf";
        List<String> files = Arrays.asList("README.md", "PROJECT_SUMMARY.md", "GUI_FEATURES.md", "COMPILE_AND_RUN.md");
        logPdfExporter.exportLogsToPdf(output, files);
        JOptionPane.showMessageDialog(this, "Docs PDF exported to " + output, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleChangePin() {
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "Please login first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel panel = new JPanel(new GridLayout(3,2,5,5));
        JPasswordField oldPin = new JPasswordField();
        JPasswordField newPin = new JPasswordField();
        JPasswordField confirmPin = new JPasswordField();
        panel.add(new JLabel("Old PIN:")); panel.add(oldPin);
        panel.add(new JLabel("New PIN:")); panel.add(newPin);
        panel.add(new JLabel("Confirm PIN:")); panel.add(confirmPin);
        int res = JOptionPane.showConfirmDialog(this, panel, "Change PIN", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            int o = Integer.parseInt(new String(oldPin.getPassword()));
            int n = Integer.parseInt(new String(newPin.getPassword()));
            int c = Integer.parseInt(new String(confirmPin.getPassword()));
            if (n != c) {
                JOptionPane.showMessageDialog(this, "New PINs do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = currentAccount.changePin(o, n);
            if (ok) {
                JOptionPane.showMessageDialog(this, "PIN changed successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Old PIN incorrect", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "PINs must be numeric", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Admin helpers
    private Integer getSelectedAccountId() {
        int row = accountTable.getSelectedRow();
        if (row < 0) return null;
        Object val = accountTable.getValueAt(row, 0);
        if (val instanceof Integer) return (Integer) val;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void refreshAccountTable() {
        DefaultTableModel model = (DefaultTableModel) accountTable.getModel();
        model.setRowCount(0);
        for (Account acc : accountRepository.getAllAccounts().values()) {
            model.addRow(new Object[]{
                acc.getAccountId(),
                acc.getName(),
                acc instanceof SavingsAccount ? "Savings" : "Salary",
                String.format("%.2f", acc.getBalance()),
                acc.isFrozen(),
                acc.getFailedPinAttempts(),
                acc.getTransactionCount()
            });
        }
    }

    private void adminFreeze(boolean freeze) {
        Integer id = getSelectedAccountId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select an account first", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        accountRepository.setAccountFrozen(id, freeze);
        JOptionPane.showMessageDialog(this, "Account " + id + (freeze ? " frozen" : " unfrozen"));
        refreshAccountTable();
    }

    private void adminResetPinAttempts() {
        Integer id = getSelectedAccountId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select an account first", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Account acc = accountRepository.getAccount(id);
        if (acc != null) {
            acc.resetFailedPinAttempts();
            JOptionPane.showMessageDialog(this, "PIN attempts reset for " + id);
            refreshAccountTable();
        }
    }

    private void adminSetTempPin() {
        Integer id = getSelectedAccountId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select an account first", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Account acc = accountRepository.getAccount(id);
        if (acc == null) return;
        String pinStr = JOptionPane.showInputDialog(this, "Enter new temporary PIN for account " + id + ":");
        if (pinStr == null) return;
        try {
            int newPin = Integer.parseInt(pinStr.trim());
            acc.adminSetPin(newPin);
            JOptionPane.showMessageDialog(this, "Temporary PIN set for " + id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "PIN must be numeric", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAdminFraud() {
        StringBuilder sb = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("logs/fraud_report.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        } catch (Exception e) {
            sb.append("Unable to read fraud_report.txt: ").append(e.getMessage());
        }
        adminFraudArea.setText(sb.toString());
        adminFraudArea.setCaretPosition(0);
    }

    private void exportAdminReport() {
        // Build a quick admin report text then export via PDF exporter
        String tempReport = "logs/admin_report.txt";
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(tempReport))) {
            pw.println("ADMIN REPORT");
            pw.println("Generated at: " + java.time.LocalDateTime.now());
            pw.println("\nACCOUNTS:");
            for (Account acc : accountRepository.getAllAccounts().values()) {
                pw.println(String.format("ID:%d | %s | Type:%s | Bal:%.2f | Frozen:%s | FailedPINs:%d | Txn:%d",
                    acc.getAccountId(),
                    acc.getName(),
                    acc instanceof SavingsAccount ? "Savings" : "Salary",
                    acc.getBalance(),
                    acc.isFrozen(),
                    acc.getFailedPinAttempts(),
                    acc.getTransactionCount()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to build admin report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        logPdfExporter.exportLogsToPdf("logs/admin_report.pdf",
            Arrays.asList(tempReport, "logs/fraud_report.txt", "logs/transactions.log"));
        JOptionPane.showMessageDialog(this, "Admin report exported to logs/admin_report.pdf", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            transactionLogArea.append("[" + timestamp + "] " + message + "\n");
            transactionLogArea.setCaretPosition(transactionLogArea.getDocument().getLength());
        });
    }
    
    private void updateFraudAlerts() {
        SwingUtilities.invokeLater(() -> {
            int alerts = fraudMonitor.getTotalFraudAlerts();
            if (alerts > 0) {
                fraudAlertsArea.setText("Total Fraud Alerts: " + alerts + "\n\n");
                fraudAlertsArea.append("⚠️ Monitor fraud_report.txt for details.\n");
                fraudAlertsArea.append("Check for:\n");
                fraudAlertsArea.append("- Rapid withdrawals\n");
                fraudAlertsArea.append("- High-value transactions\n");
                fraudAlertsArea.append("- Failed PIN attempts\n");
            } else {
                fraudAlertsArea.setText("No fraud alerts detected.");
            }
        });
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            // Use default look and feel if system L&F fails
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new BankingGUI().setVisible(true);
        });
    }
}

