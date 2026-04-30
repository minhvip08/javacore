@echo off
REM Quick test runner for Windows - runs the test suite

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   Thrift Client-Server Test Suite                             ║
echo ║   Running all JUnit 5 tests...                                ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Build first
echo Building project...
call gradlew.bat build > nul 2>&1

if !ERRORLEVEL! NEQ 0 (
    echo ERROR: Build failed!
    echo Try running: gradlew.bat build --info
    exit /b 1
)

echo Build successful!
echo.
echo Running tests...
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.

call gradlew.bat test

if !ERRORLEVEL! EQU 0 (
    echo.
    echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    echo ✓ All tests passed successfully!
    echo.
    echo Tests run:
    echo   - testPing
    echo   - testAdd
    echo   - testAddNegative
    echo   - testCalculateAdd
    echo   - testCalculateSubtract
    echo   - testCalculateMultiply
    echo   - testCalculateDivide
    echo   - testCalculateDivideByZero
    echo   - testZip
    echo   - testGetStruct
    echo   - testMultipleOperations
    echo.
) else (
    echo.
    echo ✗ Tests failed!
    echo.
    echo For more details, run:
    echo   gradlew.bat test --info
    echo.
    exit /b 1
)

endlocal
