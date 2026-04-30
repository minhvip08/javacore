# Async Thrift Client Examples

This guide demonstrates how to use the **AsyncCalculatorClient** for non-blocking, asynchronous RPC calls using Apache Thrift's async capabilities.

## Table of Contents

1. [Overview](#overview)
2. [Key Concepts](#key-concepts)
3. [Examples](#examples)
4. [Testing](#testing)
5. [Best Practices](#best-practices)

---

## Overview

The **AsyncCalculatorClient** provides a non-blocking interface for Thrift RPC calls. Instead of waiting for a server response (blocking), async calls use callbacks to process responses when they arrive.

### When to Use Async

- **High throughput**: Handle many concurrent operations
- **Non-blocking UI**: Keep UI responsive
- **Long-running operations**: Let other code execute while waiting
- **Batch operations**: Queue multiple operations simultaneously

### Sync vs Async

```java
// Synchronous - blocks until response arrives
int result = client.add(5, 10);  // Thread waits here
System.out.println(result);      // Only runs after response

// Asynchronous - returns immediately
client.asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result) {
        System.out.println(result);  // Runs when response arrives
    }
    
    @Override
    public void onError(Exception e) {
        System.err.println(e);       // Runs if error occurs
    }
});
// Code continues immediately, doesn't wait
```

---

## Key Concepts

### 1. AsyncMethodCallback

All async operations use callbacks:

```java
new AsyncMethodCallback<ReturnType>() {
    @Override
    public void onComplete(ReturnType result) {
        // Called when operation succeeds
    }
    
    @Override
    public void onError(Exception exception) {
        // Called when operation fails
    }
}
```

### 2. CountDownLatch for Synchronization

Coordinate async operations in tests:

```java
CountDownLatch latch = new CountDownLatch(3);  // Wait for 3 ops

asyncClient.add(10, 20, callback1);
asyncClient.add(5, 15, callback2);
asyncClient.add(1, 2, callback3);

latch.await(10, TimeUnit.SECONDS);  // Wait for all 3 to complete
```

### 3. TAsyncClientManager

Manages the async I/O:

```java
TAsyncClientManager clientManager = new TAsyncClientManager();
// ... use async client ...
clientManager.stop();  // Cleanup
```

---

## Examples

### Example 1: Simple Async Ping

**File**: `AsyncCalculatorClient.java` - `asyncPing()`

```java
public void asyncPing(Runnable onComplete) {
    asyncClient.ping(new AsyncMethodCallback<Void>() {
        @Override
        public void onComplete(Void result) {
            System.out.println("✓ Ping completed");
            if (onComplete != null) onComplete.run();
        }

        @Override
        public void onError(Exception exception) {
            System.err.println("✗ Ping failed");
            if (onComplete != null) onComplete.run();
        }
    });
}
```

**Usage**:
```java
CountDownLatch latch = new CountDownLatch(1);
client.asyncPing(latch::countDown);
latch.await(5, TimeUnit.SECONDS);
```

---

### Example 2: Async Add with Result

**File**: `AsyncCalculatorClient.java` - `asyncAdd()`

```java
public void asyncAdd(int num1, int num2, Runnable onComplete) {
    asyncClient.add(num1, num2, new AsyncMethodCallback<Integer>() {
        @Override
        public void onComplete(Integer result) {
            System.out.println("add(" + num1 + ", " + num2 + ") = " + result);
            if (onComplete != null) onComplete.run();
        }

        @Override
        public void onError(Exception exception) {
            System.err.println("add() failed: " + exception.getMessage());
            if (onComplete != null) onComplete.run();
        }
    });
}
```

**Usage**:
```java
CountDownLatch latch = new CountDownLatch(1);
client.asyncAdd(5, 10, latch::countDown);
latch.await(5, TimeUnit.SECONDS);
```

---

### Example 3: Chained Async Calls

**File**: `AsyncCalculatorClient.java` - `asyncCalculateChained()`

Chain operations where the second depends on the first result:

```java
public void asyncCalculateChained(Runnable onComplete) {
    // First: 10 + 5
    Work work1 = new Work();
    work1.num1 = 10;
    work1.num2 = 5;
    work1.op = Operation.ADD;

    asyncClient.calculate(1, work1, new AsyncMethodCallback<Integer>() {
        @Override
        public void onComplete(Integer result) {
            System.out.println("First: 10 + 5 = " + result);

            // Second: result * 2
            Work work2 = new Work();
            work2.num1 = result;
            work2.num2 = 2;
            work2.op = Operation.MULTIPLY;

            asyncClient.calculate(2, work2, new AsyncMethodCallback<Integer>() {
                @Override
                public void onComplete(Integer result2) {
                    System.out.println("Second: " + result + " * 2 = " + result2);
                    if (onComplete != null) onComplete.run();
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("Second op failed: " + e.getMessage());
                    if (onComplete != null) onComplete.run();
                }
            });
        }

        @Override
        public void onError(Exception e) {
            System.err.println("First op failed: " + e.getMessage());
            if (onComplete != null) onComplete.run();
        }
    });
}
```

**Usage**:
```java
CountDownLatch latch = new CountDownLatch(1);
client.asyncCalculateChained(latch::countDown);
latch.await(5, TimeUnit.SECONDS);
// Output:
// First: 10 + 5 = 15
// Second: 15 * 2 = 30
```

---

### Example 4: Parallel Async Operations

**File**: `AsyncCalculatorClient.java` - `asyncParallelOperations()`

Execute multiple operations concurrently:

```java
public void asyncParallelOperations(Runnable onComplete) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(5);

    // Op 1: Ping
    asyncClient.ping(new AsyncMethodCallback<Void>() {
        @Override
        public void onComplete(Void result) {
            System.out.println("✓ Op 1: Ping");
            latch.countDown();
        }
        @Override
        public void onError(Exception e) { latch.countDown(); }
    });

    // Op 2: Add
    asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
        @Override
        public void onComplete(Integer result) {
            System.out.println("✓ Op 2: Add = " + result);
            latch.countDown();
        }
        @Override
        public void onError(Exception e) { latch.countDown(); }
    });

    // Op 3, 4, 5: ... similar ...

    // Wait for all 5 to complete
    if (latch.await(5, TimeUnit.SECONDS)) {
        System.out.println("✓ All operations completed");
    }
    if (onComplete != null) onComplete.run();
}
```

**Usage**:
```java
client.asyncParallelOperations(() -> System.out.println("Done"));
// All 5 operations run concurrently
```

---

### Example 5: Exception Handling

**File**: `AsyncCalculatorClient.java` - `asyncExceptionHandling()`

Handle errors in async operations:

```java
public void asyncExceptionHandling(Runnable onComplete) {
    Work work = new Work();
    work.num1 = 10;
    work.num2 = 0;  // Divide by zero
    work.op = Operation.DIVIDE;

    asyncClient.calculate(5, work, new AsyncMethodCallback<Integer>() {
        @Override
        public void onComplete(Integer result) {
            System.out.println("✓ Unexpected success: " + result);
            if (onComplete != null) onComplete.run();
        }

        @Override
        public void onError(Exception exception) {
            if (exception instanceof InvalidOperation) {
                InvalidOperation io = (InvalidOperation) exception;
                System.out.println("✓ Caught expected error: " + io.getWhy());
            }
            if (onComplete != null) onComplete.run();
        }
    });
}
```

**Usage**:
```java
CountDownLatch latch = new CountDownLatch(1);
client.asyncExceptionHandling(latch::countDown);
latch.await(5, TimeUnit.SECONDS);
// Output: ✓ Caught expected error: Cannot divide by zero
```

---

### Example 6: Future-Like Pattern

**File**: `AsyncCalculatorClient.java` - `asyncAddWithFuture()`

Make async code look more synchronous with AtomicReference:

```java
public Integer asyncAddWithFuture(int num1, int num2) throws InterruptedException {
    AtomicReference<Integer> result = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    asyncClient.add(num1, num2, new AsyncMethodCallback<Integer>() {
        @Override
        public void onComplete(Integer r) {
            result.set(r);
            latch.countDown();
        }

        @Override
        public void onError(Exception e) {
            latch.countDown();
        }
    });

    if (latch.await(5, TimeUnit.SECONDS)) {
        return result.get();
    } else {
        throw new RuntimeException("Timeout");
    }
}
```

**Usage**:
```java
Integer result = client.asyncAddWithFuture(10, 20);
System.out.println("Result: " + result);  // Blocks until result arrives
```

---

### Example 7: Async Oneway Method

**File**: `AsyncCalculatorClient.java` - `asyncZip()`

Call oneway methods (no response expected):

```java
public void asyncZip(Runnable onComplete) {
    asyncClient.zip(new AsyncMethodCallback<Void>() {
        @Override
        public void onComplete(Void result) {
            System.out.println("✓ Zip sent (oneway)");
            if (onComplete != null) onComplete.run();
        }

        @Override
        public void onError(Exception exception) {
            System.err.println("✗ Zip failed");
            if (onComplete != null) onComplete.run();
        }
    });
}
```

**Usage**:
```java
CountDownLatch latch = new CountDownLatch(1);
client.asyncZip(latch::countDown);
latch.await(5, TimeUnit.SECONDS);
```

---

### Example 8: Rate-Limited Async Operations

**File**: `AsyncCalculatorClient.java` - `asyncRateLimitedOperations()`

Control throughput by spacing out requests:

```java
public void asyncRateLimitedOperations(Runnable onComplete) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(5);

    for (int i = 1; i <= 5; i++) {
        Thread.sleep(100);  // Rate limit: 100ms between requests
        
        final int id = i;
        asyncClient.add(i * 10, i * 5, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result) {
                System.out.println("Op " + id + ": Result = " + result);
                latch.countDown();
            }

            @Override
            public void onError(Exception e) { latch.countDown(); }
        });
    }

    latch.await(10, TimeUnit.SECONDS);
    if (onComplete != null) onComplete.run();
}
```

---

## Testing

### Run All Async Tests

```bash
gradlew test --tests AsyncCalculatorClientTest
```

### Run Specific Async Test

```bash
gradlew test --tests AsyncCalculatorClientTest.testAsyncParallelOperations
```

### Run Async Client Standalone

```bash
gradlew build
java -cp build/classes/main:build/resources/main com.simi.AsyncCalculatorClient
```

---

## Best Practices

### 1. **Always Implement Both onComplete and onError**

```java
// ✓ Good
asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result) { /* handle result */ }
    @Override
    public void onError(Exception e) { /* handle error */ }
});

// ✗ Bad - what if error occurs?
asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result) { /* handle result */ }
    @Override
    public void onError(Exception e) { }  // Silent failure!
});
```

### 2. **Use CountDownLatch for Test Synchronization**

```java
CountDownLatch latch = new CountDownLatch(3);
// ... queue 3 async operations ...
assertTrue(latch.await(5, TimeUnit.SECONDS), "Operations should complete");
```

### 3. **Handle Timeouts in Production Code**

```java
if (!latch.await(30, TimeUnit.SECONDS)) {
    logger.warn("Operation timeout after 30 seconds");
    // Handle timeout gracefully
}
```

### 4. **Use AtomicReference for Thread-Safe Results**

```java
AtomicReference<Integer> result = new AtomicReference<>();
asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer r) {
        result.set(r);  // Thread-safe
    }
    // ...
});
```

### 5. **Don't Block in Callbacks**

```java
// ✗ Bad - blocks the I/O thread
asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result) {
        Thread.sleep(5000);  // BLOCKS!
    }
});

// ✓ Good - submit to executor instead
Executor executor = Executors.newFixedThreadPool(4);
asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result) {
        executor.execute(() -> {
            Thread.sleep(5000);  // Non-blocking
        });
    }
});
```

### 6. **Proper Resource Cleanup**

```java
AsyncCalculatorClient client = new AsyncCalculatorClient();
try {
    client.connect();
    // ... use client ...
} finally {
    client.disconnect();  // Always cleanup
}
```

---

## Performance Considerations

### Memory Usage

- Each async operation creates a callback object (~100-200 bytes)
- With 1000 concurrent ops: ~100-200 KB overhead

### Throughput

- **Sync client**: ~1000-5000 ops/sec (blocks on each call)
- **Async client**: ~50,000+ ops/sec (non-blocking)

### Latency

- Single operation latency similar for sync/async
- Throughput benefit comes from parallelism

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Callback never called | Check server is running, use timeout in tests |
| Port already in use | Change PORT constant or kill existing process |
| Memory leak with callbacks | Ensure clientManager.stop() is called |
| Timeout in tests | Increase @Timeout value if network is slow |

---

## Advanced Topics

### Custom AsyncMethodCallback

```java
abstract class LoggingCallback<T> implements AsyncMethodCallback<T> {
    private final String operationName;
    private final long startTime = System.currentTimeMillis();
    
    public LoggingCallback(String name) {
        this.operationName = name;
    }
    
    @Override
    public void onComplete(T result) {
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(operationName + " completed in " + elapsed + "ms");
        handle(result);
    }
    
    protected abstract void handle(T result);
    
    @Override
    public void onError(Exception exception) {
        System.err.println(operationName + " failed: " + exception);
    }
}

// Usage
asyncClient.add(5, 10, new LoggingCallback<Integer>("add") {
    protected void handle(Integer result) {
        System.out.println("Result: " + result);
    }
});
```

---

For more information, see:
- [README.md](README.md) - Project overview
- [TESTING.md](TESTING.md) - Testing guide
- [Thrift Documentation](https://thrift.apache.org/docs/)
