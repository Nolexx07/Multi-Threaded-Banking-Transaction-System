package atm;

import transactions.Transaction;
import transactions.TransactionType;

/**
 * Represents an ATM request
 */
public class ATMRequest {
    private final Transaction transaction;
    private final String atmId;
    private final String customerName;
    
    public ATMRequest(Transaction transaction, String atmId, String customerName) {
        this.transaction = transaction;
        this.atmId = atmId;
        this.customerName = customerName;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    public String getAtmId() {
        return atmId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    @Override
    public String toString() {
        return String.format("ATMRequest[ATM=%s, Customer=%s, Transaction=%s]", 
            atmId, customerName, transaction);
    }
}

