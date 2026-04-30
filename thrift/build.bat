@echo off
REM Thrift Build and Test Script for Windows

echo Building Thrift project...
call gradlew.bat clean build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful!
    echo.
    echo Available commands:
    echo   gradlew.bat test              - Run all JUnit tests
    echo   gradlew.bat runServer         - Start Thrift server
    echo   gradlew.bat runClient         - Run Thrift client
    echo.
    echo For testing: Open two terminals
    echo   Terminal 1: gradlew.bat runServer
    echo   Terminal 2: gradlew.bat runClient
    echo.
) else (
    echo Build failed!
    exit /b 1
)
