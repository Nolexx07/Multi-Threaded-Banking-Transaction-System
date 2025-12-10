package concurrency;

import accounts.Account;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages locks for accounts to prevent deadlocks during transfers
 */
public class LockManager {
    private final ConcurrentHashMap<Integer, ReentrantLock> accountLocks;
    
    public LockManager() {
        this.accountLocks = new ConcurrentHashMap<>();
    }
    
    /**
     * Get or create lock for an account
     */
    private ReentrantLock getLock(int accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }
    
    /**
     * Lock accounts in sorted order to prevent deadlocks
     * Returns array of locks acquired
     */
    public ReentrantLock[] lockAccounts(int accountId1, int accountId2) {
        ReentrantLock lock1 = getLock(accountId1);
        ReentrantLock lock2 = getLock(accountId2);
        
        // Lock in ascending order to prevent deadlocks
        if (accountId1 < accountId2) {
            lock1.lock();
            lock2.lock();
            return new ReentrantLock[]{lock1, lock2};
        } else {
            lock2.lock();
            lock1.lock();
            return new ReentrantLock[]{lock2, lock1};
        }
    }
    
    /**
     * Unlock accounts
     */
    public void unlockAccounts(ReentrantLock[] locks) {
        for (ReentrantLock lock : locks) {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    /**
     * Lock single account
     */
    public ReentrantLock lockAccount(int accountId) {
        ReentrantLock lock = getLock(accountId);
        lock.lock();
        return lock;
    }
    
    /**
     * Unlock single account
     */
    public void unlockAccount(ReentrantLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}

