package reporting;

import accounts.Account;
import accounts.AccountRepository;
import util.DateTimeUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates reports from transaction logs
 */
public class ReportGenerator {
    private final AccountRepository accountRepository;
    private static final String TRANSACTION_LOG_FILE = "logs/transactions.log";
    private static final String FRAUD_REPORT_FILE = "logs/fraud_report.txt";
    private static final String REPORT_OUTPUT_FILE = "logs/daily_report.txt";
    
    public ReportGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    /**
     * Generate daily summary report
     */
    public void generateDailyReport() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_OUTPUT_FILE))) {
            writer.println("=".repeat(80));
            writer.println("DAILY TRANSACTION REPORT");
            writer.println("Generated at: " + DateTimeUtil.getCurrentTimestamp());
            writer.println("=".repeat(80));
            writer.println();
            
            // Parse transaction log
            TransactionStats stats = parseTransactionLog();
            
            // Write statistics
            writer.println("TRANSACTION STATISTICS:");
            writer.println("-".repeat(80));
            writer.println("Total Transactions: " + stats.totalTransactions);
            writer.println("Successful Transactions: " + stats.successfulTransactions);
            writer.println("Failed Transactions: " + stats.failedTransactions);
            writer.println("Success Rate: " + String.format("%.2f%%", 
                stats.totalTransactions > 0 ? 
                (stats.successfulTransactions * 100.0 / stats.totalTransactions) : 0));
            writer.println();
            
            writer.println("TRANSACTION BREAKDOWN:");
            writer.println("-".repeat(80));
            writer.println("Withdrawals: " + stats.withdrawals);
            writer.println("Deposits: " + stats.deposits);
            writer.println("Transfers: " + stats.transfers);
            writer.println("Balance Inquiries: " + stats.balanceInquiries);
            writer.println();
            
            writer.println("FINANCIAL SUMMARY:");
            writer.println("-".repeat(80));
            writer.println("Total Withdrawal Amount: $" + String.format("%.2f", stats.totalWithdrawalAmount));
            writer.println("Total Deposit Amount: $" + String.format("%.2f", stats.totalDepositAmount));
            writer.println("Total Transfer Amount: $" + String.format("%.2f", stats.totalTransferAmount));
            writer.println();
            
            // Account summary
            writer.println("ACCOUNT SUMMARY:");
            writer.println("-".repeat(80));
            Map<Integer, Account> accounts = accountRepository.getAllAccounts();
            for (Account account : accounts.values()) {
                writer.println(String.format("Account %d (%s): Balance = $%.2f, Transactions = %d", 
                    account.getAccountId(), account.getName(), 
                    account.getBalance(), account.getTransactionCount()));
            }
            writer.println();
            
            // Fraud alerts summary
            int fraudAlerts = countFraudAlerts();
            writer.println("FRAUD DETECTION SUMMARY:");
            writer.println("-".repeat(80));
            writer.println("Total Fraud Alerts: " + fraudAlerts);
            writer.println();
            
            writer.println("=".repeat(80));
            writer.flush();
            
            System.out.println("Daily report generated: " + REPORT_OUTPUT_FILE);
            
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
    
    /**
     * Parse transaction log file
     */
    private TransactionStats parseTransactionLog() {
        TransactionStats stats = new TransactionStats();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("TransactionResult")) {
                    stats.totalTransactions++;
                    
                    if (line.contains("Success=true")) {
                        stats.successfulTransactions++;
                    } else {
                        stats.failedTransactions++;
                    }
                    
                    // Count by type
                    if (line.contains("Type=WITHDRAW")) {
                        stats.withdrawals++;
                        // Extract amount if available
                        if (line.contains("Amount=")) {
                            try {
                                int start = line.indexOf("Amount=") + 7;
                                int end = line.indexOf(",", start);
                                if (end == -1) end = line.indexOf("]", start);
                                if (end > start) {
                                    String amountStr = line.substring(start, end).trim();
                                    stats.totalWithdrawalAmount += Double.parseDouble(amountStr);
                                }
                            } catch (Exception e) {
                                // Ignore parsing errors
                            }
                        }
                    } else if (line.contains("Type=DEPOSIT")) {
                        stats.deposits++;
                        if (line.contains("Amount=")) {
                            try {
                                int start = line.indexOf("Amount=") + 7;
                                int end = line.indexOf(",", start);
                                if (end == -1) end = line.indexOf("]", start);
                                if (end > start) {
                                    String amountStr = line.substring(start, end).trim();
                                    stats.totalDepositAmount += Double.parseDouble(amountStr);
                                }
                            } catch (Exception e) {
                                // Ignore parsing errors
                            }
                        }
                    } else if (line.contains("Type=TRANSFER")) {
                        stats.transfers++;
                        if (line.contains("Amount=")) {
                            try {
                                int start = line.indexOf("Amount=") + 7;
                                int end = line.indexOf(",", start);
                                if (end == -1) end = line.indexOf("]", start);
                                if (end > start) {
                                    String amountStr = line.substring(start, end).trim();
                                    stats.totalTransferAmount += Double.parseDouble(amountStr);
                                }
                            } catch (Exception e) {
                                // Ignore parsing errors
                            }
                        }
                    } else if (line.contains("Type=BALANCE_INQUIRY")) {
                        stats.balanceInquiries++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error parsing transaction log: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Count fraud alerts
     */
    private int countFraudAlerts() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(FRAUD_REPORT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("AccountId:")) {
                    count++;
                }
            }
        } catch (IOException e) {
            // File might not exist yet
        }
        return count;
    }
    
    /**
     * Inner class for transaction statistics
     */
    private static class TransactionStats {
        int totalTransactions = 0;
        int successfulTransactions = 0;
        int failedTransactions = 0;
        int withdrawals = 0;
        int deposits = 0;
        int transfers = 0;
        int balanceInquiries = 0;
        double totalWithdrawalAmount = 0;
        double totalDepositAmount = 0;
        double totalTransferAmount = 0;
    }
}

