import accounts.*;
import atm.ATMService;
import atm.ATMRequest;
import fraud.FraudMonitor;
import notification.EmailNotifier;
import reporting.ReportGenerator;
import transactions.TransactionProcessor;
import transactions.TransactionResult;
import concurrency.SyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Main application class for Multi-Threaded Banking Transaction System
 */
public class BankingSystemApp {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("MULTI-THREADED BANKING TRANSACTION SYSTEM");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Initialize repository
        AccountRepository accountRepository = new AccountRepository();
        
        // Create sample accounts
        initializeAccounts(accountRepository);
        
        // Initialize fraud monitor
        EmailNotifier notifier = new EmailNotifier("alerts@example.com", "no-reply@bank-sim.local");
        FraudMonitor fraudMonitor = new FraudMonitor(accountRepository, notifier);
        
        // Initialize transaction processor
        TransactionProcessor processor = new TransactionProcessor(accountRepository, fraudMonitor);
        
        // Initialize ATM service
        ATMService atmService = new ATMService(processor);
        
        // Initialize report generator
        ReportGenerator reportGenerator = new ReportGenerator(accountRepository);
        
        System.out.println("System initialized with " + accountRepository.getAccountCount() + " accounts.");
        System.out.println();
        
        // Simulate concurrent transactions
        simulateConcurrentTransactions(atmService, processor);
        
        // Wait for all transactions to complete
        System.out.println("\nWaiting for all transactions to complete...");
        SyncUtils.sleep(3000);
        
        // Generate report
        System.out.println("\nGenerating daily report...");
        reportGenerator.generateDailyReport();
        
        // Shutdown processor
        processor.shutdown();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SYSTEM SHUTDOWN COMPLETE");
        System.out.println("=".repeat(80));
        System.out.println("\nCheck the following files for details:");
        System.out.println("- logs/atm.log");
        System.out.println("- logs/transactions.log");
        System.out.println("- logs/fraud_report.txt");
        System.out.println("- logs/daily_report.txt");
    }
    
    /**
     * Initialize sample accounts
     */
    private static void initializeAccounts(AccountRepository repository) {
        // Create savings accounts
        repository.addAccount(new SavingsAccount(1001, "Alice Johnson", 5000.0, 1234));
        repository.addAccount(new SavingsAccount(1002, "Bob Smith", 3000.0, 5678));
        repository.addAccount(new SavingsAccount(1003, "Charlie Brown", 7500.0, 9012));
        
        // Create salary accounts
        repository.addAccount(new SalaryAccount(2001, "Diana Prince", 2000.0, 3456));
        repository.addAccount(new SalaryAccount(2002, "Edward Norton", 4500.0, 7890));
        
        System.out.println("Created accounts:");
        repository.getAllAccounts().values().forEach(account -> 
            System.out.println("  " + account));
        System.out.println();
    }
    
    /**
     * Simulate concurrent transactions from multiple ATMs
     */
    private static void simulateConcurrentTransactions(ATMService atmService, TransactionProcessor processor) {
        List<Future<TransactionResult>> futures = new ArrayList<>();
        
        System.out.println("Starting concurrent transaction simulation...");
        System.out.println();
        
        // ATM 1 - Alice's transactions
        futures.add(atmService.processRequest(
            atmService.createBalanceInquiryRequest("ATM-001", "Alice", 1001, 1234)));
        SyncUtils.sleep(100);
        
        futures.add(atmService.processRequest(
            atmService.createWithdrawRequest("ATM-001", "Alice", 1001, 500.0, 1234)));
        SyncUtils.sleep(100);
        
        futures.add(atmService.processRequest(
            atmService.createDepositRequest("ATM-001", "Alice", 1001, 200.0, 1234)));
        
        // ATM 2 - Bob's transactions
        futures.add(atmService.processRequest(
            atmService.createWithdrawRequest("ATM-002", "Bob", 1002, 1000.0, 5678)));
        SyncUtils.sleep(50);
        
        futures.add(atmService.processRequest(
            atmService.createTransferRequest("ATM-002", "Bob", 1002, 1001, 500.0, 5678)));
        
        // ATM 3 - Charlie's transactions
        futures.add(atmService.processRequest(
            atmService.createWithdrawRequest("ATM-003", "Charlie", 1003, 2000.0, 9012)));
        SyncUtils.sleep(100);
        
        futures.add(atmService.processRequest(
            atmService.createBalanceInquiryRequest("ATM-003", "Charlie", 1003, 9012)));
        
        // ATM 4 - Diana's transactions
        futures.add(atmService.processRequest(
            atmService.createDepositRequest("ATM-004", "Diana", 2001, 1500.0, 3456)));
        SyncUtils.sleep(100);
        
        futures.add(atmService.processRequest(
            atmService.createWithdrawRequest("ATM-004", "Diana", 2001, 800.0, 3456)));
        
        // ATM 5 - Edward's transactions
        futures.add(atmService.processRequest(
            atmService.createTransferRequest("ATM-005", "Edward", 2002, 1003, 1000.0, 7890)));
        SyncUtils.sleep(100);
        
        futures.add(atmService.processRequest(
            atmService.createBalanceInquiryRequest("ATM-005", "Edward", 2002, 7890)));
        
        // Simulate rapid withdrawals (for fraud detection)
        System.out.println("\nSimulating rapid withdrawals (fraud detection test)...");
        for (int i = 0; i < 4; i++) {
            futures.add(atmService.processRequest(
                atmService.createWithdrawRequest("ATM-006", "TestUser", 1001, 100.0, 1234)));
            SyncUtils.sleep(10); // Very rapid
        }
        
        // Simulate high-value withdrawal (fraud detection)
        System.out.println("Simulating high-value withdrawal (fraud detection test)...");
        futures.add(atmService.processRequest(
            atmService.createWithdrawRequest("ATM-007", "TestUser", 1002, 6000.0, 5678)));
        
        // Simulate failed PIN attempts (fraud detection)
        System.out.println("Simulating failed PIN attempts (fraud detection test)...");
        for (int i = 0; i < 3; i++) {
            futures.add(atmService.processRequest(
                atmService.createBalanceInquiryRequest("ATM-008", "Hacker", 1003, 9999)));
            SyncUtils.sleep(50);
        }
        
        // Print results as they complete
        System.out.println("\nTransaction Results:");
        System.out.println("-".repeat(80));
        int completed = 0;
        for (Future<TransactionResult> future : futures) {
            try {
                TransactionResult result = future.get();
                completed++;
                System.out.println(String.format("[%d/%d] %s", completed, futures.size(), result));
            } catch (Exception e) {
                System.err.println("Error getting transaction result: " + e.getMessage());
            }
        }
    }
}

