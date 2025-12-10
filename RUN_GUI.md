# Running the Banking System GUI

## Quick Start

### Compile the Project
```bash
cd "C:\Users\nolex\Downloads\Banking application"
javac -d . backend\util\*.java backend\accounts\*.java backend\transactions\*.java backend\concurrency\*.java backend\fraud\*.java backend\atm\*.java backend\reporting\*.java frontend\BankingGUI.java
```

### Run the GUI Application
```bash
java frontend.BankingGUI
```

## GUI Features

### Login Screen
- Enter Account ID and PIN
- Sample accounts provided:
  - Account 1001, PIN: 1234 (Alice Johnson)
  - Account 1002, PIN: 5678 (Bob Smith)
  - Account 1003, PIN: 9012 (Charlie Brown)
  - Account 2001, PIN: 3456 (Diana Prince)
  - Account 2002, PIN: 7890 (Edward Norton)

### Dashboard Features
- **Account Information**: Displays account ID, name, and current balance
- **Transaction Buttons**:
  - **Withdraw**: Withdraw money from account
  - **Deposit**: Deposit money to account
  - **Transfer**: Transfer money to another account
  - **Check Balance**: View current balance
- **Transaction Log**: Real-time log of all transactions
- **Fraud Alerts**: Display of fraud detection alerts

### How to Use

1. **Login**: Enter account ID and PIN, click "Login"
2. **View Balance**: Click "Check Balance" or view at top of dashboard
3. **Withdraw**: Click "Withdraw", enter amount
4. **Deposit**: Click "Deposit", enter amount
5. **Transfer**: Click "Transfer", enter target account ID and amount
6. **Logout**: Click "Logout" to return to login screen

### Transaction Processing
- All transactions are processed asynchronously using the backend thread pool
- Results are displayed in real-time
- Balance updates automatically after successful transactions
- Failed transactions show error messages

### Fraud Detection
- The system monitors for:
  - Rapid withdrawals (3+ in 60 seconds)
  - High-value transactions (>$5000)
  - Failed PIN attempts (3+ failures)
- Alerts are displayed in the Fraud Alerts panel

## Technical Details

- **Frontend**: Java Swing GUI
- **Backend**: Multi-threaded transaction processor
- **Thread Safety**: All operations use synchronized blocks and ReentrantLock
- **Logging**: All transactions logged to files in `logs/` directory

## Troubleshooting

### GUI doesn't appear
- Check Java version (requires JDK 8+)
- Ensure all classes compiled successfully
- Check console for error messages

### Transactions fail
- Verify account has sufficient balance
- Check PIN is correct
- Review transaction log for details

### Log files
- Check `logs/` directory for:
  - `atm.log` - ATM events
  - `transactions.log` - Transaction details
  - `fraud_report.txt` - Fraud alerts
  - `daily_report.txt` - Daily summary

