# GUI Frontend Features

## Overview

A modern, user-friendly Swing-based desktop application that provides a graphical interface for the Multi-Threaded Banking Transaction System.

## Features

### 🔐 Login Screen
- Clean, professional login interface
- Account ID and PIN input fields
- Sample account information displayed
- Enter key support for quick login
- Error handling for invalid credentials

### 📊 Dashboard
- **Account Information Display**
  - Account ID and name
  - Real-time balance display
  - Auto-refresh after transactions

- **Transaction Buttons**
  - **Withdraw**: Withdraw money with amount input dialog
  - **Deposit**: Deposit money with amount input dialog
  - **Transfer**: Transfer money with target account and amount input
  - **Check Balance**: Instant balance inquiry

- **Transaction Log Panel**
  - Real-time transaction logging
  - Timestamp for each transaction
  - Success/failure indicators (✓/✗)
  - Scrollable history

- **Fraud Alerts Panel**
  - Real-time fraud alert display
  - Alert count indicator
  - Information about detected fraud types

### 🎨 User Interface Design
- Modern color scheme (blue theme)
- Responsive layout
- Clear visual hierarchy
- Intuitive button placement
- Professional styling

### ⚡ Technical Features
- **Asynchronous Processing**: All transactions processed in background threads
- **Thread-Safe**: Fully integrated with backend thread-safe operations
- **Real-Time Updates**: Balance and logs update immediately after transactions
- **Error Handling**: User-friendly error messages
- **Logging Integration**: All transactions logged to backend log files

## Sample Accounts

The system comes pre-loaded with 5 sample accounts:

| Account ID | Name | PIN | Type | Initial Balance |
|------------|------|-----|------|-----------------|
| 1001 | Alice Johnson | 1234 | Savings | $5,000.00 |
| 1002 | Bob Smith | 5678 | Savings | $3,000.00 |
| 1003 | Charlie Brown | 9012 | Savings | $7,500.00 |
| 2001 | Diana Prince | 3456 | Salary | $2,000.00 |
| 2002 | Edward Norton | 7890 | Salary | $4,500.00 |

## Usage Flow

1. **Launch Application**: Run `java frontend.BankingGUI` or use `run-gui.bat`
2. **Login**: Enter account ID and PIN
3. **View Dashboard**: See account info and balance
4. **Perform Transactions**: Use buttons for withdraw, deposit, transfer, or balance check
5. **Monitor Activity**: Watch transaction log and fraud alerts
6. **Logout**: Return to login screen

## Integration with Backend

The GUI seamlessly integrates with all backend components:

- ✅ **AccountRepository**: Account management
- ✅ **TransactionProcessor**: Thread-safe transaction processing
- ✅ **ATMService**: ATM request handling
- ✅ **FraudMonitor**: Real-time fraud detection
- ✅ **ReportGenerator**: Report generation (can be triggered)

## Thread Safety

All GUI operations are properly synchronized:
- Transactions processed via ExecutorService
- UI updates on Swing event thread
- No race conditions
- Thread-safe backend operations

## Error Handling

- Invalid account ID/PIN
- Insufficient funds
- Invalid amounts
- Network/processing errors
- User-friendly error dialogs

## Future Enhancements (Optional)

- Transaction history view
- Account statement generation
- Multiple account switching
- Settings panel
- Report viewer
- Export functionality

