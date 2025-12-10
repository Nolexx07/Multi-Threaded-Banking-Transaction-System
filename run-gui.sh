#!/bin/bash

echo "========================================"
echo "Banking System GUI Launcher"
echo "========================================"
echo ""

# Check if classes are compiled
if [ ! -f "frontend/BankingGUI.class" ]; then
    echo "Compiling project..."
    javac -d . backend/util/*.java backend/accounts/*.java backend/transactions/*.java backend/concurrency/*.java backend/fraud/*.java backend/atm/*.java backend/reporting/*.java frontend/BankingGUI.java
    if [ $? -ne 0 ]; then
        echo "Compilation failed!"
        exit 1
    fi
    echo "Compilation successful!"
    echo ""
fi

echo "Starting Banking System GUI..."
echo ""
java frontend.BankingGUI

