# Compilation and Execution Guide

## Quick Start

### Step 1: Compile the Project

Navigate to the project root directory and compile all Java files:

**Windows (PowerShell):**
```powershell
cd "C:\Users\nolex\Downloads\Banking application"
javac -d . backend\util\*.java backend\accounts\*.java backend\transactions\*.java backend\concurrency\*.java backend\fraud\*.java backend\atm\*.java backend\reporting\*.java BankingSystemApp.java
```

**Windows (Command Prompt):**
```cmd
cd "C:\Users\nolex\Downloads\Banking application"
javac -d . backend\util\*.java backend\accounts\*.java backend\transactions\*.java backend\concurrency\*.java backend\fraud\*.java backend\atm\*.java backend\reporting\*.java BankingSystemApp.java
```

**Linux/Mac:**
```bash
javac -d . backend/util/*.java backend/accounts/*.java backend/transactions/*.java backend/concurrency/*.java backend/fraud/*.java backend/atm/*.java backend/reporting/*.java BankingSystemApp.java
```

### Step 2: Create Logs Directory

The application will create the logs directory automatically, but you can create it manually:

**Windows:**
```powershell
mkdir logs
```

**Linux/Mac:**
```bash
mkdir -p logs
```

### Step 3: Run the Application

```bash
java BankingSystemApp
```

## Alternative: Compile Individual Packages

If the above doesn't work, compile in order:

```bash
# 1. Utilities
javac backend/util/DateTimeUtil.java

# 2. Accounts
javac backend/accounts/Account.java
javac backend/accounts/SavingsAccount.java
javac backend/accounts/SalaryAccount.java
javac backend/accounts/AccountRepository.java

# 3. Transactions
javac backend/transactions/TransactionType.java
javac backend/transactions/Transaction.java
javac backend/transactions/TransactionResult.java
javac backend/transactions/TransactionProcessor.java

# 4. Concurrency
javac backend/concurrency/LockManager.java
javac backend/concurrency/SyncUtils.java

# 5. Fraud Detection
javac backend/fraud/FraudAlert.java
javac backend/fraud/FraudMonitor.java

# 6. ATM
javac backend/atm/ATMRequest.java
javac backend/atm/ATMService.java

# 7. Reporting
javac backend/reporting/ReportGenerator.java

# 8. Main Application
javac BankingSystemApp.java
```

## Expected Output

After running, you should see:
- Console output showing transaction processing
- Log files created in `logs/` directory:
  - `atm.log` - ATM events
  - `transactions.log` - Transaction details
  - `fraud_report.txt` - Fraud alerts
  - `daily_report.txt` - Daily summary

## Troubleshooting

### "Package does not exist" Error
Make sure you're compiling from the project root directory and using the correct package structure.

### "Cannot find symbol" Error
Compile dependencies first (e.g., compile Account before SavingsAccount).

### Log Files Not Created
Ensure the `logs/` directory exists and has write permissions.

## Requirements

- Java JDK 8 or higher
- No external dependencies required

