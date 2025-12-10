package atm;

import transactions.Transaction;
import transactions.TransactionProcessor;
import transactions.TransactionResult;
import transactions.TransactionType;
import util.DateTimeUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Future;

/**
 * ATM Service that simulates ATM operations
 */
public class ATMService {
    private final TransactionProcessor transactionProcessor;
    private static final String ATM_LOG_FILE = "logs/atm.log";
    
    public ATMService(TransactionProcessor transactionProcessor) {
        this.transactionProcessor = transactionProcessor;
        initializeAtmLog();
    }
    
    /**
     * Initialize ATM log file
     */
    private void initializeAtmLog() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ATM_LOG_FILE, true))) {
            writer.println("=".repeat(80));
            writer.println("ATM LOG - Started at " + DateTimeUtil.getCurrentTimestamp());
            writer.println("=".repeat(80));
        } catch (IOException e) {
            System.err.println("Error initializing ATM log: " + e.getMessage());
        }
    }
    
    /**
     * Process ATM request
     */
    public Future<TransactionResult> processRequest(ATMRequest request) {
        logAtmEvent(String.format("ATM %s: Customer %s initiated %s transaction for Account %d", 
            request.getAtmId(), request.getCustomerName(), 
            request.getTransaction().getType(), request.getTransaction().getAccountId()));
        
        return transactionProcessor.submitTransaction(request.getTransaction());
    }
    
    /**
     * Create withdraw request
     */
    public ATMRequest createWithdrawRequest(String atmId, String customerName, 
                                           int accountId, double amount, int pin) {
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, accountId, amount, pin);
        return new ATMRequest(transaction, atmId, customerName);
    }
    
    /**
     * Create deposit request
     */
    public ATMRequest createDepositRequest(String atmId, String customerName, 
                                         int accountId, double amount, int pin) {
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, accountId, amount, pin);
        return new ATMRequest(transaction, atmId, customerName);
    }
    
    /**
     * Create transfer request
     */
    public ATMRequest createTransferRequest(String atmId, String customerName, 
                                         int fromAccountId, int toAccountId, double amount, int pin) {
        Transaction transaction = new Transaction(TransactionType.TRANSFER, fromAccountId, 
            toAccountId, amount, pin);
        return new ATMRequest(transaction, atmId, customerName);
    }
    
    /**
     * Create balance inquiry request
     */
    public ATMRequest createBalanceInquiryRequest(String atmId, String customerName, 
                                                int accountId, int pin) {
        Transaction transaction = new Transaction(TransactionType.BALANCE_INQUIRY, accountId, 0, pin);
        return new ATMRequest(transaction, atmId, customerName);
    }
    
    /**
     * Log ATM event
     */
    private void logAtmEvent(String event) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ATM_LOG_FILE, true))) {
            writer.println(DateTimeUtil.getCurrentTimestamp() + " | " + event);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error logging ATM event: " + e.getMessage());
        }
    }
}

