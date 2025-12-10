package accounts;

/**
 * Savings Account implementation
 */
public class SavingsAccount extends Account {
    private static final double MINIMUM_BALANCE = 100.0;
    
    public SavingsAccount(int accountId, String name, double initialBalance, int pin) {
        super(accountId, name, initialBalance, pin);
    }
    
    @Override
    public synchronized boolean withdraw(double amount) {
        // Check minimum balance requirement for savings account
        if (balance - amount < MINIMUM_BALANCE) {
            return false;
        }
        return super.withdraw(amount);
    }
    
    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }
}

