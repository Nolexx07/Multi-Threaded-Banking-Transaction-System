package accounts;

import java.util.concurrent.atomic.AtomicInteger;
import util.SecurityUtil;

/**
 * Base Account class with thread-safe operations
 */
public abstract class Account {
    protected final int accountId;
    protected final String name;
    protected double balance;
    protected String pinHash;
    protected final AtomicInteger failedPinAttempts;
    protected final AtomicInteger transactionCount;
    protected boolean frozen;
    
    public Account(int accountId, String name, double initialBalance, int pin) {
        this.accountId = accountId;
        this.name = name;
        this.balance = initialBalance;
        this.pinHash = SecurityUtil.hashPin(pin);
        this.failedPinAttempts = new AtomicInteger(0);
        this.transactionCount = new AtomicInteger(0);
        this.frozen = false;
    }
    
    /**
     * Thread-safe deposit operation
     */
    public synchronized boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        transactionCount.incrementAndGet();
        return true;
    }
    
    /**
     * Thread-safe withdraw operation with overdraft prevention
     */
    public synchronized boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (balance < amount) {
            return false; // Overdraft prevention
        }
        balance -= amount;
        transactionCount.incrementAndGet();
        return true;
    }
    
    /**
     * Thread-safe balance inquiry
     */
    public synchronized double getBalance() {
        return balance;
    }
    
    /**
     * Validate PIN
     */
    public boolean validatePin(int inputPin) {
        if (SecurityUtil.hashPin(inputPin).equals(pinHash)) {
            failedPinAttempts.set(0);
            return true;
        } else {
            failedPinAttempts.incrementAndGet();
            return false;
        }
    }

    /**
     * Change PIN with old PIN verification.
     */
    public synchronized boolean changePin(int oldPin, int newPin) {
        if (!validatePin(oldPin)) {
            return false;
        }
        this.pinHash = SecurityUtil.hashPin(newPin);
        return true;
    }

    /**
     * Admin-only PIN set (bypasses old PIN).
     */
    public synchronized void adminSetPin(int newPin) {
        this.pinHash = SecurityUtil.hashPin(newPin);
        failedPinAttempts.set(0);
    }
    
    // Getters
    public int getAccountId() {
        return accountId;
    }
    
    public String getName() {
        return name;
    }
    
    public int getFailedPinAttempts() {
        return failedPinAttempts.get();
    }
    
    public int getTransactionCount() {
        return transactionCount.get();
    }
    
    public void resetFailedPinAttempts() {
        failedPinAttempts.set(0);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
    
    @Override
    public String toString() {
        return String.format("Account[ID=%d, Name=%s, Balance=%.2f, Transactions=%d]", 
            accountId, name, balance, transactionCount.get());
    }
}

