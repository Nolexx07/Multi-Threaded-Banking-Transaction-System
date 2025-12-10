package transactions;

import accounts.Account;
import util.DateTimeUtil;

/**
 * Represents a single transaction
 */
public class Transaction {
    private final TransactionType type;
    private final int accountId;
    private final Integer targetAccountId; // null for non-transfer transactions
    private final double amount;
    private final int pin;
    private final String timestamp;
    
    public Transaction(TransactionType type, int accountId, double amount, int pin) {
        this.type = type;
        this.accountId = accountId;
        this.amount = amount;
        this.pin = pin;
        this.targetAccountId = null;
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
    }
    
    public Transaction(TransactionType type, int accountId, int targetAccountId, double amount, int pin) {
        this.type = type;
        this.accountId = accountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.pin = pin;
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public Integer getTargetAccountId() {
        return targetAccountId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public int getPin() {
        return pin;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        if (type == TransactionType.TRANSFER) {
            return String.format("Transaction[Type=%s, From=%d, To=%d, Amount=%.2f, Time=%s]",
                type, accountId, targetAccountId, amount, timestamp);
        }
        return String.format("Transaction[Type=%s, Account=%d, Amount=%.2f, Time=%s]",
            type, accountId, amount, timestamp);
    }
}

