@echo off
echo ========================================
echo Banking System GUI Launcher
echo ========================================
echo.

REM Check if classes are compiled
if not exist "frontend\BankingGUI.class" (
    echo Compiling project...
    javac -d . backend\util\*.java backend\accounts\*.java backend\transactions\*.java backend\concurrency\*.java backend\fraud\*.java backend\atm\*.java backend\reporting\*.java frontend\BankingGUI.java
    if errorlevel 1 (
        echo Compilation failed!
        pause
        exit /b 1
    )
    echo Compilation successful!
    echo.
)

echo Starting Banking System GUI...
echo.
java frontend.BankingGUI

pause

