package accounts;

/**
 * Salary Account implementation (no minimum balance requirement)
 */
public class SalaryAccount extends Account {
    
    public SalaryAccount(int accountId, String name, double initialBalance, int pin) {
        super(accountId, name, initialBalance, pin);
    }
    
    // Salary accounts can go to zero balance
    @Override
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
}

