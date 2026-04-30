# Thrift Client-Server Test Implementation

This project demonstrates a Thrift client-server implementation with comprehensive test cases using JUnit 5.

## Project Structure

```
.
├── thrift/                          # Thrift definition files
│   ├── shared.thrift               # Shared structs and services
│   └── tutorial.thrift             # Calculator service definitions
├── src/main/
│   ├── java/com/simi/
│   │   ├── CalculatorServer.java   # Thrift server implementation
│   │   ├── CalculatorClient.java   # Thrift client implementation
│   │   └── Main.java               # Entry point
│   └── gen-java/                   # Generated Thrift code (auto-generated)
└── src/test/
    └── java/com/simi/
        └── ThriftClientServerTest.java  # JUnit 5 test cases
```

## Prerequisites

1. **Thrift Compiler**: Install from [https://thrift.apache.org/download/](https://thrift.apache.org/download/)
   - On Windows: Download the pre-built binary
   - On macOS: `brew install thrift`
   - On Linux: `apt-get install thrift-compiler`

2. **Java 11+**: Required for this project

3. **Gradle**: Uses Gradle wrapper (included)

## Thrift Services

### Calculator Service (tutorial.thrift)

- **ping()**: Simple ping operation
- **add(num1, num2)**: Adds two integers
- **calculate(logid, Work)**: Performs calculations (ADD, SUBTRACT, MULTIPLY, DIVIDE)
- **zip()**: Oneway method (no response expected)
- **getStruct(key)**: Inherited from SharedService, returns a SharedStruct

## Setup and Build

### 1. Generate Thrift Code

The Thrift code generation is automatically run as part of the build process:

```bash
# On Windows
gradlew clean compileThrift
```

This generates Java classes in `src/main/gen-java/`:
- `tutorial.Calculator`
- `tutorial.Operation`
- `tutorial.Work`
- `shared.SharedStruct`
- `shared.SharedService`
- etc.

### 2. Build the Project

```bash
# On Windows
gradlew build

# On macOS/Linux
./gradlew build
```

## Running Tests

### Run All Tests

```bash
gradlew test
```

This will:
1. Start the Thrift server in a background thread
2. Connect a client
3. Run all JUnit test cases
4. Clean up resources

### Individual Tests

```bash
# Test specific functionality
gradlew test --tests ThriftClientServerTest.testAdd
gradlew test --tests ThriftClientServerTest.testCalculateDivideByZero
gradlew test --tests ThriftClientServerTest.testMultipleOperations
```

## Manual Testing (Server & Client)

### Terminal 1 - Start Server

```bash
gradlew run --args='server'
```

Or run directly:

```bash
java -cp build/classes/main:build/resources/main com.simi.CalculatorServer
```

Expected output:
```
Starting Thrift Server on port 9090
```

### Terminal 2 - Run Client

```bash
gradlew run --args='client'
```

Or run directly:

```bash
java -cp build/classes/main:build/resources/main com.simi.CalculatorClient
```

## Test Cases

The `ThriftClientServerTest` class includes the following tests:

| Test Name | Description | Expected Behavior |
|-----------|-------------|-------------------|
| `testPing` | Tests ping operation | Server responds successfully |
| `testAdd` | Tests basic addition | 5 + 10 = 15 |
| `testAddNegative` | Tests addition with negative numbers | -5 + 10 = 5 |
| `testCalculateAdd` | Tests calculate with ADD operation | 20 + 15 = 35 |
| `testCalculateSubtract` | Tests calculate with SUBTRACT operation | 20 - 8 = 12 |
| `testCalculateMultiply` | Tests calculate with MULTIPLY operation | 7 * 6 = 42 |
| `testCalculateDivide` | Tests calculate with DIVIDE operation | 20 / 5 = 4 |
| `testCalculateDivideByZero` | Tests exception handling | Throws InvalidOperation |
| `testZip` | Tests oneway method | No response expected |
| `testGetStruct` | Tests inherited service method | Returns SharedStruct with correct key |
| `testMultipleOperations` | Tests sequence of operations | All operations complete successfully |

## Key Implementation Details

### Server (CalculatorServer.java)

- Implements `Calculator.Iface` interface generated from Thrift
- Uses `TThreadPoolServer` for handling multiple concurrent connections
- Listens on `localhost:9090`
- Handles all Calculator service methods including exception cases

### Client (CalculatorClient.java)

- Connects to server via `TSocket` with `TBinaryProtocol`
- Provides wrapper methods for all service operations
- Can be used standalone for manual testing

### Tests (ThriftClientServerTest.java)

- Uses JUnit 5 annotations (`@BeforeEach`, `@AfterEach`, `@Test`)
- Starts server in background thread before each test
- Connects client and runs assertions
- Cleans up resources after each test
- Uses `@Timeout` to prevent hanging tests

## Troubleshooting

### "thrift: command not found"

Ensure Thrift compiler is installed and in your PATH:
```bash
thrift -version
```

### Port 9090 already in use

Either:
1. Change the PORT constant in `CalculatorServer.java` and `CalculatorClient.java`
2. Kill the process using port 9090:
   - **Windows**: `netstat -ano | findstr :9090` then `taskkill /PID <PID>`
   - **macOS/Linux**: `lsof -i :9090` then `kill -9 <PID>`

### Connection refused when running client standalone

Ensure the server is running first:
```bash
# Terminal 1
gradlew run --args='server'

# Terminal 2
gradlew run --args='client'
```

## Next Steps

1. **Add more service methods** to `tutorial.thrift`
2. **Implement authentication** using Thrift headers
3. **Add async operations** using Thrift's oneway methods
4. **Performance testing** with multiple concurrent clients
5. **Integration with other languages** (Python, Go, JavaScript via Thrift)

## References

- [Thrift Documentation](https://thrift.apache.org/docs/)
- [Thrift Tutorial](https://thrift.apache.org/tutorial/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
