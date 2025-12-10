# Architecture Documentation

## System Architecture Overview

The Multi-Threaded Banking Transaction System is designed as a modular, thread-safe banking simulation application built entirely in Java SE without external frameworks.

## Architecture Layers

### 1. Data Layer (Accounts Module)

**Purpose**: Manages account data and provides thread-safe access to account information.

**Components**:
- `Account`: Abstract base class with synchronized operations
- `SavingsAccount`: Extends Account with minimum balance requirement
- `SalaryAccount`: Extends Account without minimum balance requirement
- `AccountRepository`: Thread-safe storage using ConcurrentHashMap

**Key Features**:
- Encapsulation of account data
- Thread-safe balance operations
- PIN validation with failure tracking
- Transaction counting using AtomicInteger

### 2. Transaction Layer

**Purpose**: Processes all banking transactions with proper concurrency control.

**Components**:
- `Transaction`: Immutable transaction data structure
- `TransactionType`: Enumeration of transaction types
- `TransactionResult`: Result object with success status and details
- `TransactionProcessor`: Core engine using ExecutorService

**Concurrency Strategy**:
- **Single Account Ops**: `synchronized(account)` blocks
- **Transfers**: `ReentrantLock` with deadlock prevention
- **Thread Pool**: Fixed pool of 10 threads via ExecutorService
- **Future Pattern**: Returns Future<TransactionResult> for async processing

### 3. Concurrency Layer

**Purpose**: Provides synchronization utilities and deadlock prevention.

**Components**:
- `LockManager`: Manages ReentrantLock instances per account
- `SyncUtils`: Utility methods for thread operations

**Deadlock Prevention**:
- Locks accounts in ascending ID order during transfers
- Ensures consistent lock acquisition order
- Prevents circular wait conditions

### 4. ATM Layer

**Purpose**: Simulates ATM operations and user interactions.

**Components**:
- `ATMService`: Main service for ATM operations
- `ATMRequest`: Request wrapper with ATM and customer info

**Features**:
- Creates transaction requests
- Logs all ATM events
- Supports multiple concurrent ATMs

### 5. Fraud Detection Layer

**Purpose**: Monitors transactions for suspicious patterns.

**Components**:
- `FraudMonitor`: Background monitoring system
- `FraudAlert`: Alert data structure

**Detection Rules**:
1. **Rapid Withdrawals**: 3+ withdrawals within 60 seconds
2. **High-Value Transactions**: Withdrawals exceeding $5000
3. **Failed PIN Attempts**: 3+ consecutive failed PIN validations

**Implementation**:
- Uses AtomicInteger for thread-safe counters
- Background monitoring thread
- Real-time alert generation

### 6. Reporting Layer

**Purpose**: Generates reports from transaction logs.

**Components**:
- `ReportGenerator`: Parses logs and generates summaries

**Report Contents**:
- Total transaction statistics
- Success/failure rates
- Transaction breakdown by type
- Financial summaries
- Account summaries
- Fraud alert counts

## Data Flow

```
User Request → ATMService → TransactionProcessor → AccountRepository
                                      ↓
                              FraudMonitor (monitoring)
                                      ↓
                              Transaction Logging
                                      ↓
                              Report Generation
```

## Thread Safety Mechanisms

### 1. Synchronized Blocks
```java
synchronized (account) {
    // Single account operations
}
```

### 2. ReentrantLock
```java
ReentrantLock[] locks = lockManager.lockAccounts(id1, id2);
try {
    // Transfer operations
} finally {
    lockManager.unlockAccounts(locks);
}
```

### 3. Concurrent Collections
- `ConcurrentHashMap` for account storage
- `AtomicInteger` for counters

### 4. ExecutorService
- Fixed thread pool (10 threads)
- Callable/Future pattern for async processing

## Design Patterns Used

1. **Repository Pattern**: AccountRepository abstracts data access
2. **Factory Pattern**: ATMService creates transaction requests
3. **Strategy Pattern**: Different account types with different behaviors
4. **Observer Pattern**: FraudMonitor observes transactions
5. **Future Pattern**: Async transaction processing

## Error Handling

- Overdraft prevention: Checks balance before withdrawal
- Invalid PIN handling: Tracks failed attempts
- Account not found: Returns appropriate error messages
- File I/O errors: Graceful degradation with error messages

## Scalability Considerations

- Thread pool size configurable
- ConcurrentHashMap for O(1) account lookups
- Efficient lock management
- Minimal blocking operations

## Security Features

- PIN validation
- Failed attempt tracking
- Fraud detection
- Transaction logging for audit trail

## Performance Optimizations

- Lock ordering prevents deadlocks
- Minimal lock contention
- Efficient collection usage
- Background fraud monitoring

