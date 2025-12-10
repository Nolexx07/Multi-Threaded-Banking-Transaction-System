package concurrency;

/**
 * Utility class for synchronization helpers
 */
public class SyncUtils {
    
    /**
     * Sleep for specified milliseconds
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Sleep for random time between min and max milliseconds
     */
    public static void sleepRandom(long minMs, long maxMs) {
        long sleepTime = minMs + (long)(Math.random() * (maxMs - minMs));
        sleep(sleepTime);
    }
}

