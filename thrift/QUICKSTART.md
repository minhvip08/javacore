# Quick Start Guide - Thrift Client & Server Testing

## Prerequisites

Ensure **Thrift compiler** is installed:
```bash
thrift -version
```

If not installed:
- **Windows**: Download from https://thrift.apache.org/download/
- **macOS**: `brew install thrift`
- **Linux**: `apt-get install thrift-compiler`

---

## 🚀 Quick Start (3 Steps)

### Step 1: Build the Project
```bash
gradlew build
```

### Step 2: Run All Tests
```bash
gradlew test
```

That's it! All tests should pass.

---

## 🧪 Manual Testing (Client & Server Separately)

### Terminal 1 - Start Server
```bash
gradlew runServer
```

**Expected output:**
```
Starting Thrift Server on port 9090
```

The server will wait for client connections.

### Terminal 2 - Run Client
Open a **new terminal** in the project directory and run:
```bash
gradlew runClient
```

**Expected output:**
```
Connected to Thrift Server at localhost:9090
Client: Calling ping()
Client: ping() successful
...
All tests passed!
```

---

## 📊 Running Specific Tests

```bash
# Run a single test
gradlew test --tests ThriftClientServerTest.testAdd

# Run tests matching a pattern
gradlew test --tests ThriftClientServerTest.testCalculate*

# Run with verbose output
gradlew test --info
```

---

## 📋 Available Tasks

| Command | Description |
|---------|-------------|
| `gradlew build` | Build project and generate Thrift code |
| `gradlew test` | Run all JUnit tests |
| `gradlew runServer` | Start Thrift server |
| `gradlew runClient` | Run Thrift client |
| `gradlew clean` | Clean build artifacts |
| `gradlew compileThrift` | Generate Thrift code only |

---

## 🔍 Test Coverage

The test suite includes:

✓ Basic operations (ping, add)  
✓ Arithmetic operations (ADD, SUBTRACT, MULTIPLY, DIVIDE)  
✓ Exception handling (divide by zero)  
✓ Oneway methods (zip)  
✓ Service inheritance (getStruct)  
✓ Multiple consecutive operations  

---

## 📁 Project Layout

```
└── src/
    ├── main/java/com/simi/
    │   ├── Main.java              # Entry point info
    │   ├── CalculatorServer.java  # Server implementation
    │   └── CalculatorClient.java  # Client implementation
    └── test/java/com/simi/
        └── ThriftClientServerTest.java  # JUnit 5 tests
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| "thrift: command not found" | Install Thrift compiler (see Prerequisites) |
| "Port 9090 already in use" | Change PORT in Server/Client classes or kill process using port |
| Connection refused | Ensure server is running before starting client |
| Test timeout | Increase timeout value in test file if network is slow |

---

## 💡 Next Steps

1. Add more test cases for edge cases
2. Implement client connection pooling
3. Add performance/load testing
4. Integrate with logging framework
5. Add authentication/security features

---

**Still having issues?** Check [README.md](README.md) for detailed documentation.
