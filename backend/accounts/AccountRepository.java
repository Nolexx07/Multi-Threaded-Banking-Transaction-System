package accounts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe repository for managing accounts
 */
public class AccountRepository {
    private final Map<Integer, Account> accounts;
    
    public AccountRepository() {
        // Using ConcurrentHashMap for thread-safe operations
        this.accounts = new ConcurrentHashMap<>();
    }
    
    /**
     * Add account to repository
     */
    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }
    
    /**
     * Get account by ID
     */
    public Account getAccount(int accountId) {
        return accounts.get(accountId);
    }
    
    /**
     * Freeze/unfreeze an account.
     */
    public void setAccountFrozen(int accountId, boolean frozen) {
        Account acc = accounts.get(accountId);
        if (acc != null) {
            acc.setFrozen(frozen);
        }
    }
    
    /**
     * Check if account exists
     */
    public boolean accountExists(int accountId) {
        return accounts.containsKey(accountId);
    }
    
    /**
     * Get all accounts
     */
    public Map<Integer, Account> getAllAccounts() {
        return new HashMap<>(accounts);
    }
    
    /**
     * Get total number of accounts
     */
    public int getAccountCount() {
        return accounts.size();
    }
}

