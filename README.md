# Multi-Threaded Banking Transaction System

A comprehensive Java application that simulates a banking system with concurrent transaction processing, fraud detection, and reporting capabilities.

## 🚀 Quick Start

### Prerequisites
- Java JDK 17+ (works on 11+, but 17 recommended)
- Git (to clone)
- No external libraries required

### Build & Run (Windows PowerShell)
```powershell
cd "C:\Users\nolex\Downloads\Banking application"
javac -d . backend\util\*.java backend\accounts\*.java backend\transactions\*.java backend\concurrency\*.java backend\fraud\*.java backend\atm\*.java backend\reporting\*.java backend\notification\*.java frontend\BankingGUI.java BankingSystemApp.java
java -cp . frontend.BankingGUI   # GUI
# or
java -cp . BankingSystemApp     # Console simulation
```

### Build & Run (Linux/Mac)
```bash
cd "C:/Users/nolex/Downloads/Banking application"   # adjust path as needed
javac -d . backend/util/*.java backend/accounts/*.java backend/transactions/*.java backend/concurrency/*.java backend/fraud/*.java backend/atm/*.java backend/reporting/*.java backend/notification/*.java frontend/BankingGUI.java BankingSystemApp.java
java -cp . frontend.BankingGUI   # GUI
# or
java -cp . BankingSystemApp     # Console simulation
```

### Useful Buttons in GUI
- Parallel Test: fires 5 concurrent transactions (thread-pool/Future demo)
- Fraud Test: triggers rapid + high-value withdrawals + failed PINs
- Export Logs (PDF): writes `logs/logs_export.pdf`
- Export Docs (PDF): writes `logs/docs_export.pdf`
- Admin report: `logs/admin_report.pdf` from admin dashboard

## 🎯 Features

- **Account Management**: Support for multiple account types (Savings, Salary)
- **GUI Frontend**: Modern Swing-based desktop application with user-friendly interface
- **ATM Simulation**: Text-based ATM interface with concurrent transaction processing
- **Thread-Safe Operations**: Uses synchronized blocks and ReentrantLock for safe concurrent access
- **Fraud Detection**: Monitors suspicious activities (rapid withdrawals, high-value transactions, failed PIN attempts)
- **Transaction Logging**: Comprehensive logging of all ATM events and transactions
- **Reporting System**: Generates daily summary reports with statistics

## 🏗️ Architecture

### Package Structure

```
backend/
├── accounts/          # Account management
│   ├── Account.java
│   ├── SavingsAccount.java
│   ├── SalaryAccount.java
│   └── AccountRepository.java
├── atm/               # ATM simulation
│   ├── ATMService.java
│   └── ATMRequest.java
├── transactions/      # Transaction processing
│   ├── Transaction.java
│   ├── TransactionType.java
│   ├── TransactionProcessor.java
│   └── TransactionResult.java
├── concurrency/       # Concurrency utilities
│   ├── LockManager.java
│   └── SyncUtils.java
├── fraud/             # Fraud detection
│   ├── FraudMonitor.java
│   └── FraudAlert.java
├── reporting/         # Report generation
│   └── ReportGenerator.java
└── util/              # Utilities
    └── DateTimeUtil.java
frontend/
└── BankingGUI.java    # Swing GUI application
```

## 🔧 Technologies Used

- **Core Java SE** (No external frameworks)
- **Multithreading**: ExecutorService, Callable, Future
- **Synchronization**: synchronized, ReentrantLock
- **Atomic Operations**: AtomicInteger
- **Collections**: ConcurrentHashMap, HashMap
- **File I/O**: Logging and report generation

## 🚀 Getting Started

### Prerequisites

- Java JDK 8 or higher
- No external dependencies required

### Compilation

```bash
# Compile all Java files
javac -d . backend/**/*.java BankingSystemApp.java

# Or compile individually
javac backend/util/DateTimeUtil.java
javac backend/accounts/*.java
javac backend/transactions/*.java
javac backend/concurrency/*.java
javac backend/fraud/*.java
javac backend/atm/*.java
javac backend/reporting/*.java
javac BankingSystemApp.java
```

### Execution

**Option 1: Run GUI Application (Recommended)**
```bash
# Windows
run-gui.bat

# Linux/Mac
chmod +x run-gui.sh
./run-gui.sh

# Or manually:
javac -d . backend\util\*.java backend\accounts\*.java backend\transactions\*.java backend\concurrency\*.java backend\fraud\*.java backend\atm\*.java backend\reporting\*.java frontend\BankingGUI.java
java frontend.BankingGUI
```

**Option 2: Run Console Application**
```bash
# Run the console-based application
java BankingSystemApp
```

### Create Logs Directory

The application will create a `logs/` directory automatically, but you can create it manually:

```bash
mkdir logs
```

## 📋 Concurrency Rules

1. **Single Account Operations**: Uses `synchronized(account)` for thread-safe deposit/withdraw
2. **Transfer Operations**: Uses `ReentrantLock` with deadlock prevention (locks accounts in ascending ID order)
3. **Thread Pool**: ExecutorService with fixed pool size of 10 threads
4. **Atomic Counters**: Uses AtomicInteger for fraud metrics and transaction counts

## 📊 Sample Output

The application will:
1. Initialize accounts
2. Process concurrent transactions from multiple ATMs
3. Detect fraud patterns
4. Generate logs in `logs/` directory:
   - `atm.log` - All ATM events
   - `transactions.log` - All transaction details
   - `fraud_report.txt` - Fraud alerts
   - `daily_report.txt` - Daily summary report

## 🔍 Key Classes

### Account
- Base class for all account types
- Thread-safe deposit/withdraw operations
- PIN validation
- Transaction counting

### TransactionProcessor
- Core engine for processing transactions
- Uses ExecutorService for parallel processing
- Implements synchronized and ReentrantLock patterns
- Prevents overdrafts

### FraudMonitor
- Background monitoring thread
- Detects:
  - Rapid withdrawals (3+ in 1 minute)
  - High-value withdrawals (>$5000)
  - Failed PIN attempts (3+ failures)

### ReportGenerator
- Parses transaction logs
- Generates CSV/TXT reports
- Includes statistics and summaries

## 🧪 Testing

The application includes sample test scenarios:
- Concurrent withdrawals and deposits
- Money transfers between accounts
- Balance inquiries
- Fraud detection triggers (rapid withdrawals, high-value transactions, failed PINs)

## 📝 Log Files

All logs are written to the `logs/` directory:

- **atm.log**: ATM events and user activities
- **transactions.log**: Detailed transaction records with results
- **fraud_report.txt**: Fraud alerts and suspicious activities
- **daily_report.txt**: Daily summary with statistics

## 🎓 Learning Objectives

This project demonstrates:
- Object-Oriented Programming (Encapsulation, Inheritance, Polymorphism)
- Exception Handling
- Collections Framework
- Generics
- Multithreading and Concurrency
- Thread Synchronization
- Deadlock Prevention
- File I/O
- Logging Systems

## 📄 License

This is an educational project for learning Java concurrency and banking system simulation.

## 👥 Team Structure (5 Members)

Each team includes a React member (shared where needed) plus back-end developers:

- **Team 1 — Accounts Module**  
  - React: Gowrish (vishwanadhulagowrish27@gmail.com)  
  - Devs: Yaswanth Koduru, Lakshmi Narayana Murthy Pappula

- **Team 2 — ATM Simulator**  
  - React: Indla Lilif Naidu (liliflilif21@gmail.com)  
  - Devs: Harsha Vardhan Reddy Kommuru, Mabhunni Pathan

- **Team 3 — Transaction Processor**  
  - React: Adduri Venkatachalam (avenkatachalam6789@gmail.com)  
  - Devs: Malkam Sathvik, Raviteja Pulluru

- **Team 4 — Fraud Detection**  
  - React: Gowrish (shared)  
  - Devs: Munaganti Jyothi, R A Bhargav

- **Team 5 — Reporting Module**  
  - React: Indla Lilif Naidu (shared)  
  - Devs: Reddi Pradeep Kumar, (bench/assist: spare slot)

---

**Note**: This is a simulation system for educational purposes. It does not connect to real banking systems or handle real money.

