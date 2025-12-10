package fraud;

import util.DateTimeUtil;

/**
 * Represents a fraud alert
 */
public class FraudAlert {
    private final int accountId;
    private final String reason;
    private final String timestamp;
    private final String severity;
    
    public FraudAlert(int accountId, String reason, String severity) {
        this.accountId = accountId;
        this.reason = reason;
        this.severity = severity;
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    @Override
    public String toString() {
        return String.format("FraudAlert[AccountId=%d, Reason=%s, Severity=%s, Time=%s]",
            accountId, reason, severity, timestamp);
    }
    
    /**
     * Format for log file
     */
    public String toLogFormat() {
        return String.format("%s | AccountId: %d | Severity: %s | Reason: %s",
            timestamp, accountId, severity, reason);
    }
}

