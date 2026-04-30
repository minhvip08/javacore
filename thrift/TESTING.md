# Testing Guide - Thrift Client & Server

This guide explains how to run and verify the Thrift client-server test flow.

## 📊 Test Overview

The implementation includes **11 comprehensive JUnit 5 tests** that verify:

| Category | Tests |
|----------|-------|
| **Basic Operations** | ping, add, add (negative) |
| **Arithmetic** | ADD, SUBTRACT, MULTIPLY, DIVIDE |
| **Error Handling** | divide by zero exception |
| **Oneway Methods** | zip |
| **Service Inheritance** | getStruct |
| **Integration** | multiple operations sequence |

---

## 🎯 Running Tests

### Option 1: Run All Tests (Recommended for CI/CD)

```bash
gradlew test
```

**What happens:**
1. Project builds
2. Thrift code generates automatically
3. Server starts in background thread
4. Each test connects client and executes operations
5. Server and client cleanup after each test
6. Results displayed

**Sample Output:**
```
> Task :compileThrift
> Task :compileJava
> Task :test

ThriftClientServerTest > testPing PASSED (0.5s)
ThriftClientServerTest > testAdd PASSED (0.2s)
ThriftClientServerTest > testAddNegative PASSED (0.2s)
ThriftClientServerTest > testCalculateAdd PASSED (0.3s)
ThriftClientServerTest > testCalculateSubtract PASSED (0.2s)
ThriftClientServerTest > testCalculateMultiply PASSED (0.2s)
ThriftClientServerTest > testCalculateDivide PASSED (0.2s)
ThriftClientServerTest > testCalculateDivideByZero PASSED (0.3s)
ThriftClientServerTest > testZip PASSED (0.2s)
ThriftClientServerTest > testGetStruct PASSED (0.3s)
ThriftClientServerTest > testMultipleOperations PASSED (0.5s)

BUILD SUCCESSFUL

Tests summary:
- 11 passed
- 0 failed
- Total time: ~4.5s
```

### Option 2: Run Specific Test

```bash
# Single test
gradlew test --tests ThriftClientServerTest.testAdd

# Pattern matching
gradlew test --tests ThriftClientServerTest.testCalculate*

# With verbose output
gradlew test --info
```

### Option 3: Run Tests with Windows Batch File

```bash
run-tests.bat
```

Shows a formatted summary with all test names.

---

## 🖥️ Manual Testing (Client & Server)

For manual inspection and debugging:

### Terminal 1: Start Server

```bash
gradlew runServer
```

Output:
```
Starting Thrift Server on port 9090
```

The server now waits for client connections.

### Terminal 2: Run Client

```bash
gradlew runClient
```

Output:
```
Connected to Thrift Server at localhost:9090
Client: Calling ping()
ping() called
Client: ping() successful
Client: Calling add(5, 10)
add(5,10) called
Client: add() result = 15
... more operations ...
All tests passed!
```

---

## 📈 Test Details

### 1. **testPing()** - Basic Connectivity
```
Operation: ping()
Expected: Server responds
Result: ✓
```

### 2. **testAdd()** - Simple Addition
```
Operation: add(5, 10)
Expected: 15
Result: ✓
```

### 3. **testAddNegative()** - Negative Numbers
```
Operation: add(-5, 10)
Expected: 5
Result: ✓
```

### 4. **testCalculateAdd()** - Arithmetic Operation
```
Operation: calculate(1, 20, 15, ADD)
Expected: 35
Result: ✓
```

### 5. **testCalculateSubtract()** - Arithmetic Operation
```
Operation: calculate(2, 20, 8, SUBTRACT)
Expected: 12
Result: ✓
```

### 6. **testCalculateMultiply()** - Arithmetic Operation
```
Operation: calculate(3, 7, 6, MULTIPLY)
Expected: 42
Result: ✓
```

### 7. **testCalculateDivide()** - Arithmetic Operation
```
Operation: calculate(4, 20, 5, DIVIDE)
Expected: 4
Result: ✓
```

### 8. **testCalculateDivideByZero()** - Exception Handling
```
Operation: calculate(5, 10, 0, DIVIDE)
Expected: InvalidOperation exception
Result: ✓ (Exception caught: "Cannot divide by zero")
```

### 9. **testZip()** - Oneway Method
```
Operation: zip()
Expected: No response (oneway method)
Result: ✓
```

### 10. **testGetStruct()** - Service Inheritance
```
Operation: getStruct(42)
Expected: SharedStruct with key=42
Result: ✓ (key=42, value contains "42")
```

### 11. **testMultipleOperations()** - Integration Test
```
Operations: ping, add, calculate (2x), getStruct, zip in sequence
Expected: All complete successfully
Result: ✓
```

---

## 🔍 Understanding Test Output

### Successful Test Run
```
BUILD SUCCESSFUL

11 tests passed in 4.5s
```

### Test Failure Example
```
ThriftClientServerTest > testAdd FAILED

AssertionError: 5 + 10 expected <15> but was <16>
```

### Common Issues and Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| "Port 9090 already in use" | Another server running | Stop it: `netstat -ano \| findstr :9090` then `taskkill /PID <PID>` |
| "Connection refused" | Server not running | Ensure server started before client |
| "thrift: command not found" | Compiler not installed | Install Thrift (see QUICKSTART.md) |
| Test timeout (>5s) | Slow network/PC | Increase @Timeout value in test class |

---

## 🛠️ Debugging Tests

### Run with Debug Output
```bash
gradlew test --debug
```

### Run Single Test with Info
```bash
gradlew test --tests ThriftClientServerTest.testAdd --info
```

### View Server Side Logs
The server logs all RPC calls:
```
add(5,10) called
calculate(1,20,15) called
ping() called
```

### View Client Side Logs
The client logs all operations:
```
Client: Calling ping()
Client: Calling add(5, 10)
Client: add() result = 15
```

---

## ✅ Verification Checklist

After running tests, verify:

- [ ] All 11 tests pass
- [ ] No exceptions or errors logged
- [ ] Total test time < 10 seconds
- [ ] Build completes successfully
- [ ] Generated Thrift files exist in `src/main/gen-java/`
- [ ] No port conflicts on 9090

---

## 📊 Performance Metrics

Expected performance (on modern hardware):

| Metric | Value |
|--------|-------|
| Build time | ~2s |
| Thrift generation | ~0.5s |
| Test suite total | ~4-5s |
| Per-test average | ~0.4s |
| Server startup | ~1s |
| Client connection | ~0.1s |

---

## 🚀 Continuous Integration

For CI/CD pipelines, use:

```bash
# Simple all-in-one command
gradlew clean test

# Or with detailed reporting
gradlew clean test --info --tests ThriftClientServerTest
```

Expected exit codes:
- `0` = All tests passed
- `1` = One or more tests failed

---

## Next Steps

1. **Scale Testing**: Test with 10+ concurrent clients
2. **Load Testing**: Send 1000+ requests and measure throughput
3. **Security Testing**: Add authentication and test security
4. **Cross-Language**: Generate clients in Python/Go and test interop
5. **Performance Profiling**: Measure RPC latency and memory usage

---

**Need help?** See [README.md](README.md) or [QUICKSTART.md](QUICKSTART.md)
