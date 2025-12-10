# UML Diagrams Documentation

## Class Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        Account (Abstract)                    │
├─────────────────────────────────────────────────────────────┤
│ - accountId: int                                            │
│ - name: String                                              │
│ - balance: double                                           │
│ - pin: int                                                  │
│ - failedPinAttempts: AtomicInteger                          │
│ - transactionCount: AtomicInteger                           │
├─────────────────────────────────────────────────────────────┤
│ + deposit(amount: double): boolean                          │
│ + withdraw(amount: double): boolean                         │
│ + getBalance(): double                                      │
│ + validatePin(inputPin: int): boolean                       │
│ + getAccountId(): int                                       │
│ + getName(): String                                         │
│ + getFailedPinAttempts(): int                               │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │
                ┌─────────────┴─────────────┐
                │                           │
┌───────────────────────────┐  ┌───────────────────────────┐
│    SavingsAccount         │  │    SalaryAccount         │
├───────────────────────────┤  ├───────────────────────────┤
│ - MINIMUM_BALANCE: double │  │                           │
├───────────────────────────┤  ├───────────────────────────┤
│ + withdraw(amount): bool  │  │ + withdraw(amount): bool  │
└───────────────────────────┘  └───────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    AccountRepository                        │
├─────────────────────────────────────────────────────────────┤
│ - accounts: Map<Integer, Account>                          │
├─────────────────────────────────────────────────────────────┤
│ + addAccount(account: Account): void                       │
│ + getAccount(accountId: int): Account                      │
│ + accountExists(accountId: int): boolean                    │
│ + getAllAccounts(): Map<Integer, Account>                  │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      Transaction                            │
├─────────────────────────────────────────────────────────────┤
│ - type: TransactionType                                     │
│ - accountId: int                                            │
│ - targetAccountId: Integer                                 │
│ - amount: double                                            │
│ - pin: int                                                  │
│ - timestamp: String                                         │
├─────────────────────────────────────────────────────────────┤
│ + getType(): TransactionType                                │
│ + getAccountId(): int                                       │
│ + getAmount(): double                                       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   TransactionProcessor                      │
├─────────────────────────────────────────────────────────────┤
│ - accountRepository: AccountRepository                     │
│ - lockManager: LockManager                                  │
│ - fraudMonitor: FraudMonitor                                │
│ - executorService: ExecutorService                         │
├─────────────────────────────────────────────────────────────┤
│ + submitTransaction(transaction): Future<TransactionResult> │
│ - processWithdraw(transaction): TransactionResult           │
│ - processDeposit(transaction): TransactionResult            │
│ - processTransfer(transaction): TransactionResult           │
│ - processBalanceInquiry(transaction): TransactionResult     │
│ + shutdown(): void                                          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      LockManager                            │
├─────────────────────────────────────────────────────────────┤
│ - accountLocks: ConcurrentHashMap<Integer, ReentrantLock>   │
├─────────────────────────────────────────────────────────────┤
│ + lockAccounts(id1: int, id2: int): ReentrantLock[]        │
│ + unlockAccounts(locks: ReentrantLock[]): void             │
│ + lockAccount(accountId: int): ReentrantLock                │
│ + unlockAccount(lock: ReentrantLock): void                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      FraudMonitor                           │
├─────────────────────────────────────────────────────────────┤
│ - accountRepository: AccountRepository                     │
│ - rapidWithdrawalCount: ConcurrentHashMap                   │
│ - lastWithdrawalTime: ConcurrentHashMap                     │
│ - highValueWithdrawalCount: ConcurrentHashMap               │
│ - totalFraudAlerts: AtomicInteger                           │
├─────────────────────────────────────────────────────────────┤
│ + monitorTransaction(transaction: Transaction): void        │
│ - monitorWithdrawal(transaction: Transaction): void        │
│ - generateAlert(alert: FraudAlert): void                    │
│ + getTotalFraudAlerts(): int                                │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      ATMService                             │
├─────────────────────────────────────────────────────────────┤
│ - transactionProcessor: TransactionProcessor                │
├─────────────────────────────────────────────────────────────┤
│ + processRequest(request: ATMRequest): Future<Result>       │
│ + createWithdrawRequest(...): ATMRequest                    │
│ + createDepositRequest(...): ATMRequest                     │
│ + createTransferRequest(...): ATMRequest                    │
│ + createBalanceInquiryRequest(...): ATMRequest              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      ATMRequest                             │
├─────────────────────────────────────────────────────────────┤
│ - transaction: Transaction                                  │
│ - atmId: String                                            │
│ - customerName: String                                      │
├─────────────────────────────────────────────────────────────┤
│ + getTransaction(): Transaction                             │
│ + getAtmId(): String                                        │
│ + getCustomerName(): String                                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    ReportGenerator                          │
├─────────────────────────────────────────────────────────────┤
│ - accountRepository: AccountRepository                     │
├─────────────────────────────────────────────────────────────┤
│ + generateDailyReport(): void                              │
│ - parseTransactionLog(): TransactionStats                  │
│ - countFraudAlerts(): int                                  │
└─────────────────────────────────────────────────────────────┘
```

## Sequence Diagram - Withdraw Transaction

```
User          ATMService      TransactionProcessor    AccountRepository    FraudMonitor
  │                │                    │                      │                  │
  │──Withdraw─────>│                    │                      │                  │
  │                │──Submit───────────>│                      │                  │
  │                │                    │──Get Account────────>│                  │
  │                │                    │<──Account────────────│                  │
  │                │                    │──Validate PIN────────>│                  │
  │                │                    │<──PIN Valid──────────│                  │
  │                │                    │──synchronized───────>│                  │
  │                │                    │  withdraw()          │                  │
  │                │                    │<──Success────────────│                  │
  │                │                    │──Monitor─────────────┼─────────────────>│
  │                │                    │                      │                  │
  │                │<──Future───────────│                      │                  │
  │<──Result───────│                    │                      │                  │
```

## Sequence Diagram - Transfer Transaction

```
User          ATMService      TransactionProcessor    LockManager    Account1    Account2
  │                │                    │                  │            │            │
  │──Transfer─────>│                    │                  │            │            │
  │                │──Submit───────────>│                  │            │            │
  │                │                    │──Lock Accounts──>│            │            │
  │                │                    │                  │──Lock ID1──>│            │
  │                │                    │                  │──Lock ID2──────────────>│
  │                │                    │──Withdraw────────┼───────────>│            │
  │                │                    │<──Success────────│            │            │
  │                │                    │──Deposit─────────┼───────────────────────>│
  │                │                    │<──Success────────│            │            │
  │                │                    │──Unlock──────────>│            │            │
  │                │                    │                  │──Unlock────┼───────────>│
  │                │<──Future───────────│                  │            │            │
  │<──Result───────│                    │                  │            │            │
```

## State Diagram - Account Transaction

```
[Idle] ──Withdraw Request──> [Validating PIN]
                                │
                                ├─PIN Invalid──> [Failed] ──> [Idle]
                                │
                                └─PIN Valid──> [Checking Balance]
                                                 │
                                                 ├─Insufficient──> [Failed] ──> [Idle]
                                                 │
                                                 └─Sufficient──> [Processing]
                                                                   │
                                                                   └─> [Success] ──> [Idle]
```

## Component Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                  BankingSystemApp                           │
│                    (Main Application)                       │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
│  ATMService    │  │ Transaction    │  │   Fraud        │
│                │  │ Processor      │  │   Monitor      │
└────────────────┘  └────────────────┘  └────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
│   Account      │  │   Lock         │  │   Report      │
│  Repository    │  │   Manager      │  │   Generator   │
└────────────────┘  └────────────────┘  └────────────────┘
```

## Activity Diagram - Transfer Process

```
Start
  │
  ▼
[Receive Transfer Request]
  │
  ▼
[Validate Source Account]
  │
  ├─Not Found──> [Error] ──> End
  │
  └─Found──> [Validate Target Account]
              │
              ├─Not Found──> [Error] ──> End
              │
              └─Found──> [Validate PIN]
                          │
                          ├─Invalid──> [Error] ──> End
                          │
                          └─Valid──> [Lock Accounts (Ascending Order)]
                                      │
                                      ▼
                          [Check Source Balance]
                                      │
                                      ├─Insufficient──> [Unlock] ──> [Error] ──> End
                                      │
                                      └─Sufficient──> [Withdraw from Source]
                                                       │
                                                       ▼
                                          [Deposit to Target]
                                                       │
                                                       ├─Failed──> [Rollback] ──> [Unlock] ──> [Error] ──> End
                                                       │
                                                       └─Success──> [Unlock] ──> [Log Transaction] ──> [Success] ──> End
```

