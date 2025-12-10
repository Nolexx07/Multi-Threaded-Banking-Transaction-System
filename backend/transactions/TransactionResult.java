package transactions;

import util.DateTimeUtil;

/**
 * Result of a transaction operation
 */
public class TransactionResult {
    private final boolean success;
    private final String message;
    private final double balanceAfter;
    private final String timestamp;
    private final TransactionType transactionType;
    private final int accountId;
    
    public TransactionResult(boolean success, String message, double balanceAfter, 
                            TransactionType transactionType, int accountId) {
        this.success = success;
        this.message = message;
        this.balanceAfter = balanceAfter;
        this.transactionType = transactionType;
        this.accountId = accountId;
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    @Override
    public String toString() {
        return String.format("TransactionResult[Success=%s, Type=%s, AccountId=%d, Balance=%.2f, Message=%s, Time=%s]",
            success, transactionType, accountId, balanceAfter, message, timestamp);
    }
}

