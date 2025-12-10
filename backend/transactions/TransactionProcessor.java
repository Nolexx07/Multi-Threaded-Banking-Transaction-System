package transactions;

import accounts.Account;
import accounts.AccountRepository;
import concurrency.LockManager;
import fraud.FraudMonitor;
import util.DateTimeUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Core transaction processor with thread-safe operations
 */
public class TransactionProcessor {
    private final AccountRepository accountRepository;
    private final LockManager lockManager;
    private final FraudMonitor fraudMonitor;
    private final ExecutorService executorService;
    private static final String TRANSACTION_LOG_FILE = "logs/transactions.log";
    private static final int THREAD_POOL_SIZE = 10;
    
    public TransactionProcessor(AccountRepository accountRepository, FraudMonitor fraudMonitor) {
        this.accountRepository = accountRepository;
        this.lockManager = new LockManager();
        this.fraudMonitor = fraudMonitor;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        initializeTransactionLog();
    }
    
    /**
     * Initialize transaction log file
     */
    private void initializeTransactionLog() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTION_LOG_FILE, true))) {
            writer.println("=".repeat(80));
            writer.println("TRANSACTION LOG - Started at " + DateTimeUtil.getCurrentTimestamp());
            writer.println("=".repeat(80));
        } catch (IOException e) {
            System.err.println("Error initializing transaction log: " + e.getMessage());
        }
    }
    
    /**
     * Submit transaction for processing (returns Future)
     */
    public Future<TransactionResult> submitTransaction(Transaction transaction) {
        return executorService.submit(new TransactionCallable(transaction));
    }
    
    /**
     * Process withdraw transaction (synchronized)
     */
    private TransactionResult processWithdraw(Transaction transaction) {
        Account account = accountRepository.getAccount(transaction.getAccountId());
        if (account == null) {
            return new TransactionResult(false, "Account not found", 0, 
                transaction.getType(), transaction.getAccountId());
        }
        if (account.isFrozen()) {
            return new TransactionResult(false, "Account is frozen due to security reasons", account.getBalance(),
                transaction.getType(), transaction.getAccountId());
        }
        
        // Validate PIN
        if (!account.validatePin(transaction.getPin())) {
            fraudMonitor.monitorTransaction(transaction);
            return new TransactionResult(false, "Invalid PIN", account.getBalance(), 
                transaction.getType(), transaction.getAccountId());
        }
        
        // Use synchronized block for single account operation
        synchronized (account) {
            boolean success = account.withdraw(transaction.getAmount());
            double balance = account.getBalance();
            
            String message = success ? 
                String.format("Withdrawal successful: $%.2f", transaction.getAmount()) :
                "Withdrawal failed: Insufficient funds or invalid amount";
            
            TransactionResult result = new TransactionResult(success, message, balance, 
                transaction.getType(), transaction.getAccountId());
            
            logTransaction(transaction, result);
            if (success) {
                fraudMonitor.monitorTransaction(transaction);
            }
            
            return result;
        }
    }
    
    /**
     * Process deposit transaction (synchronized)
     */
    private TransactionResult processDeposit(Transaction transaction) {
        Account account = accountRepository.getAccount(transaction.getAccountId());
        if (account == null) {
            return new TransactionResult(false, "Account not found", 0, 
                transaction.getType(), transaction.getAccountId());
        }
        if (account.isFrozen()) {
            return new TransactionResult(false, "Account is frozen due to security reasons", account.getBalance(),
                transaction.getType(), transaction.getAccountId());
        }
        
        // Validate PIN
        if (!account.validatePin(transaction.getPin())) {
            fraudMonitor.monitorTransaction(transaction);
            return new TransactionResult(false, "Invalid PIN", account.getBalance(), 
                transaction.getType(), transaction.getAccountId());
        }
        
        // Use synchronized block for single account operation
        synchronized (account) {
            boolean success = account.deposit(transaction.getAmount());
            double balance = account.getBalance();
            
            String message = success ? 
                String.format("Deposit successful: $%.2f", transaction.getAmount()) :
                "Deposit failed: Invalid amount";
            
            TransactionResult result = new TransactionResult(success, message, balance, 
                transaction.getType(), transaction.getAccountId());
            
            logTransaction(transaction, result);
            return result;
        }
    }
    
    /**
     * Process transfer transaction (ReentrantLock with deadlock prevention)
     */
    private TransactionResult processTransfer(Transaction transaction) {
        if (transaction.getTargetAccountId() == null) {
            return new TransactionResult(false, "Target account not specified", 0, 
                transaction.getType(), transaction.getAccountId());
        }
        
        Account fromAccount = accountRepository.getAccount(transaction.getAccountId());
        Account toAccount = accountRepository.getAccount(transaction.getTargetAccountId());
        
        if (fromAccount == null || toAccount == null) {
            return new TransactionResult(false, "One or both accounts not found", 0, 
                transaction.getType(), transaction.getAccountId());
        }
        if (fromAccount.isFrozen()) {
            return new TransactionResult(false, "Source account is frozen due to security reasons", fromAccount.getBalance(),
                transaction.getType(), transaction.getAccountId());
        }
        if (toAccount.isFrozen()) {
            return new TransactionResult(false, "Target account is frozen due to security reasons", toAccount.getBalance(),
                transaction.getType(), transaction.getAccountId());
        }
        
        // Validate PIN
        if (!fromAccount.validatePin(transaction.getPin())) {
            fraudMonitor.monitorTransaction(transaction);
            return new TransactionResult(false, "Invalid PIN", fromAccount.getBalance(), 
                transaction.getType(), transaction.getAccountId());
        }
        
        // Lock both accounts in sorted order to prevent deadlocks
        ReentrantLock[] locks = lockManager.lockAccounts(
            transaction.getAccountId(), 
            transaction.getTargetAccountId()
        );
        
        try {
            // Check sufficient balance
            if (fromAccount.getBalance() < transaction.getAmount()) {
                return new TransactionResult(false, "Insufficient funds for transfer", 
                    fromAccount.getBalance(), transaction.getType(), transaction.getAccountId());
            }
            
            // Perform transfer
            boolean withdrawSuccess = fromAccount.withdraw(transaction.getAmount());
            if (!withdrawSuccess) {
                return new TransactionResult(false, "Transfer failed: Could not withdraw from source", 
                    fromAccount.getBalance(), transaction.getType(), transaction.getAccountId());
            }
            
            boolean depositSuccess = toAccount.deposit(transaction.getAmount());
            if (!depositSuccess) {
                // Rollback if deposit fails
                fromAccount.deposit(transaction.getAmount());
                return new TransactionResult(false, "Transfer failed: Could not deposit to target", 
                    fromAccount.getBalance(), transaction.getType(), transaction.getAccountId());
            }
            
            String message = String.format("Transfer successful: $%.2f from Account %d to Account %d", 
                transaction.getAmount(), transaction.getAccountId(), transaction.getTargetAccountId());
            
            TransactionResult result = new TransactionResult(true, message, 
                fromAccount.getBalance(), transaction.getType(), transaction.getAccountId());
            
            logTransaction(transaction, result);
            fraudMonitor.monitorTransaction(transaction);
            
            return result;
            
        } finally {
            // Always unlock
            lockManager.unlockAccounts(locks);
        }
    }
    
    /**
     * Process balance inquiry (synchronized)
     */
    private TransactionResult processBalanceInquiry(Transaction transaction) {
        Account account = accountRepository.getAccount(transaction.getAccountId());
        if (account == null) {
            return new TransactionResult(false, "Account not found", 0, 
                transaction.getType(), transaction.getAccountId());
        }
        if (account.isFrozen()) {
            return new TransactionResult(false, "Account is frozen due to security reasons", account.getBalance(),
                transaction.getType(), transaction.getAccountId());
        }
        
        // Validate PIN
        if (!account.validatePin(transaction.getPin())) {
            fraudMonitor.monitorTransaction(transaction);
            return new TransactionResult(false, "Invalid PIN", 0, 
                transaction.getType(), transaction.getAccountId());
        }
        
        synchronized (account) {
            double balance = account.getBalance();
            TransactionResult result = new TransactionResult(true, 
                String.format("Balance inquiry: $%.2f", balance), balance, 
                transaction.getType(), transaction.getAccountId());
            
            logTransaction(transaction, result);
            return result;
        }
    }
    
    /**
     * Log transaction to file
     */
    private void logTransaction(Transaction transaction, TransactionResult result) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTION_LOG_FILE, true))) {
            writer.println(String.format("%s | %s | %s", 
                result.getTimestamp(), transaction, result));
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error logging transaction: " + e.getMessage());
        }
    }
    
    /**
     * Shutdown executor service
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Callable implementation for transaction processing
     */
    private class TransactionCallable implements Callable<TransactionResult> {
        private final Transaction transaction;
        
        public TransactionCallable(Transaction transaction) {
            this.transaction = transaction;
        }
        
        @Override
        public TransactionResult call() {
            switch (transaction.getType()) {
                case WITHDRAW:
                    return processWithdraw(transaction);
                case DEPOSIT:
                    return processDeposit(transaction);
                case TRANSFER:
                    return processTransfer(transaction);
                case BALANCE_INQUIRY:
                    return processBalanceInquiry(transaction);
                default:
                    return new TransactionResult(false, "Unknown transaction type", 0, 
                        transaction.getType(), transaction.getAccountId());
            }
        }
    }
}

