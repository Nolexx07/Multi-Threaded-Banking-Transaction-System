package fraud;

import accounts.Account;
import accounts.AccountRepository;
import notification.EmailNotifier;
import transactions.Transaction;
import transactions.TransactionType;
import util.DateTimeUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitors transactions for fraudulent activity
 */
public class FraudMonitor {
    private final AccountRepository accountRepository;
    private final ConcurrentHashMap<Integer, AtomicInteger> rapidWithdrawalCount;
    private final ConcurrentHashMap<Integer, AtomicLong> lastWithdrawalTime;
    private final ConcurrentHashMap<Integer, AtomicInteger> highValueWithdrawalCount;
    private final AtomicInteger totalFraudAlerts;
    private final ConcurrentHashMap<Integer, AtomicInteger> perAccountAlerts;
    private final EmailNotifier emailNotifier;
    private static final String FRAUD_REPORT_FILE = "logs/fraud_report.txt";
    private static final double HIGH_VALUE_THRESHOLD = 5000.0;
    private static final int RAPID_WITHDRAWAL_THRESHOLD = 3; // 3 withdrawals in short time
    private static final long RAPID_WITHDRAWAL_WINDOW_MS = 60000; // 1 minute
    private static final int AUTO_FREEZE_THRESHOLD = 3; // auto-freeze after 3 alerts
    
    public FraudMonitor(AccountRepository accountRepository, EmailNotifier emailNotifier) {
        this.accountRepository = accountRepository;
        this.rapidWithdrawalCount = new ConcurrentHashMap<>();
        this.lastWithdrawalTime = new ConcurrentHashMap<>();
        this.highValueWithdrawalCount = new ConcurrentHashMap<>();
        this.totalFraudAlerts = new AtomicInteger(0);
        this.perAccountAlerts = new ConcurrentHashMap<>();
        this.emailNotifier = emailNotifier;
        initializeFraudReport();
    }
    
    /**
     * Initialize fraud report file
     */
    private void initializeFraudReport() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FRAUD_REPORT_FILE, true))) {
            writer.println("=".repeat(80));
            writer.println("FRAUD DETECTION REPORT - Started at " + DateTimeUtil.getCurrentTimestamp());
            writer.println("=".repeat(80));
        } catch (IOException e) {
            System.err.println("Error initializing fraud report: " + e.getMessage());
        }
    }
    
    /**
     * Monitor a transaction for fraud
     */
    public void monitorTransaction(Transaction transaction) {
        Account account = accountRepository.getAccount(transaction.getAccountId());
        if (account == null) {
            return;
        }
        
        // Check failed PIN attempts
        if (account.getFailedPinAttempts() >= 3) {
            generateAlert(new FraudAlert(transaction.getAccountId(), 
                "Multiple failed PIN attempts (" + account.getFailedPinAttempts() + ")", 
                "HIGH"));
        }
        
        // Monitor withdrawals
        if (transaction.getType() == TransactionType.WITHDRAW) {
            monitorWithdrawal(transaction);
        }
    }
    
    /**
     * Monitor withdrawal for suspicious patterns
     */
    private void monitorWithdrawal(Transaction transaction) {
        int accountId = transaction.getAccountId();
        double amount = transaction.getAmount();
        long currentTime = System.currentTimeMillis();
        
        // Check for high-value withdrawals
        if (amount >= HIGH_VALUE_THRESHOLD) {
            highValueWithdrawalCount.computeIfAbsent(accountId, k -> new AtomicInteger(0)).incrementAndGet();
            generateAlert(new FraudAlert(accountId, 
                String.format("High-value withdrawal: $%.2f", amount), 
                "MEDIUM"));
        }
        
        // Check for rapid withdrawals
        AtomicLong lastTime = lastWithdrawalTime.computeIfAbsent(accountId, k -> new AtomicLong(0));
        long lastWithdrawal = lastTime.get();
        
        if (lastWithdrawal > 0 && (currentTime - lastWithdrawal) < RAPID_WITHDRAWAL_WINDOW_MS) {
            AtomicInteger rapidCount = rapidWithdrawalCount.computeIfAbsent(accountId, k -> new AtomicInteger(0));
            int count = rapidCount.incrementAndGet();
            
            if (count >= RAPID_WITHDRAWAL_THRESHOLD) {
                generateAlert(new FraudAlert(accountId, 
                    String.format("Rapid withdrawals detected: %d withdrawals in short time", count), 
                    "HIGH"));
                rapidCount.set(0); // Reset counter
            }
        } else {
            // Reset counter if enough time has passed
            rapidWithdrawalCount.put(accountId, new AtomicInteger(1));
        }
        
        lastTime.set(currentTime);
    }
    
    /**
     * Generate and log fraud alert
     */
    private void generateAlert(FraudAlert alert) {
        totalFraudAlerts.incrementAndGet();
        perAccountAlerts.computeIfAbsent(alert.getAccountId(), k -> new AtomicInteger(0)).incrementAndGet();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FRAUD_REPORT_FILE, true))) {
            writer.println(alert.toLogFormat());
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing fraud alert: " + e.getMessage());
        }
        
        System.out.println("⚠️  FRAUD ALERT: " + alert);
        
        // Send notification if configured
        if (emailNotifier != null) {
            String subject = "Fraud Alert for Account " + alert.getAccountId();
            String body = alert.toLogFormat();
            emailNotifier.send(subject, body);
        }

        // Auto-freeze account after threshold
        int count = perAccountAlerts.get(alert.getAccountId()).get();
        if (count >= AUTO_FREEZE_THRESHOLD) {
            accountRepository.setAccountFrozen(alert.getAccountId(), true);
            try (PrintWriter writer = new PrintWriter(new FileWriter(FRAUD_REPORT_FILE, true))) {
                writer.println(DateTimeUtil.getCurrentTimestamp() + " | Account auto-frozen due to repeated fraud alerts.");
            } catch (IOException ignored) {
            }
        }
    }
    
    /**
     * Get total fraud alerts count
     */
    public int getTotalFraudAlerts() {
        return totalFraudAlerts.get();
    }
    
    /**
     * Reset monitoring data for an account
     */
    public void resetAccountMonitoring(int accountId) {
        rapidWithdrawalCount.remove(accountId);
        lastWithdrawalTime.remove(accountId);
        highValueWithdrawalCount.remove(accountId);
    }
}

