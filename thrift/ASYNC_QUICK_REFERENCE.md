# Async Client Quick Reference

## Overview

The **AsyncCalculatorClient** provides non-blocking, asynchronous RPC calls using Thrift's `AsyncClient` and `TAsyncClientManager`.

## When to Use Async

- High throughput applications (1000+ RPS)
- UI applications (keep UI responsive)
- Real-time systems (low latency)
- Batch operations (process multiple requests simultaneously)
- Long-running operations (don't want to block)

## Running Async Examples

### With Running Server

**Terminal 1:**
```bash
gradlew runServer
```

**Terminal 2:**
```bash
gradlew runAsyncClient
```

### Example Output

```
AsyncCalculatorClient connected to localhost:9090

[Async Example 1] Ping with Callback
Calling ping() asynchronously...
✓ Ping completed successfully

[Async Example 2] Add with Callback
Calling add(5, 10) asynchronously...
✓ add(5, 10) returned: 15

[Async Example 3] Chained Async Calculations
Executing multiple calculations in sequence...
✓ First calculation (ADD): 10 + 5 = 15
✓ Second calculation (MULTIPLY): 15 * 2 = 30

[Async Example 4] Parallel Async Operations
Executing 5 operations in parallel...
Waiting for all parallel operations to complete...
✓ Parallel Op 1: Ping completed
✓ Parallel Op 2: Add(5, 10) = 15
✓ Parallel Op 3: Calculate ADD(20, 15) = 35
✓ Parallel Op 4: Calculate MULTIPLY(7, 6) = 42
✓ Parallel Op 5: GetStruct(100) = ...
✓ All parallel operations completed

[Async Example 5] Exception Handling (Divide by Zero)
Calling divide by zero asynchronously...
✓ Caught expected exception: Cannot divide by zero

[Async Example 6] Async with Future Pattern
Calling add(20, 30) with future-like pattern...
✓ Async add result received: 50
Final result from future-like call: 50

[Async Example 7] Async Oneway Method (zip)
Calling zip() asynchronously (oneway, no response expected)...
✓ Zip oneway call sent (no response expected)

[Async Example 8] Rate-Limited Async Operations
Executing operations with 100ms delay between each...
All operations queued, waiting for responses...
✓ Rate-limited Op 1: add(10, 5) = 15
✓ Rate-limited Op 2: add(20, 10) = 30
✓ Rate-limited Op 3: add(30, 15) = 45
✓ Rate-limited Op 4: add(40, 20) = 60
✓ Rate-limited Op 5: add(50, 25) = 75
✓ All rate-limited operations completed

All async examples completed successfully!
```

## Usage Patterns

### Pattern 1: Simple Callback

```java
asyncClient.ping(new AsyncMethodCallback<Void>() {
    @Override
    public void onComplete(Void result) {
        System.out.println("Ping completed");
    }

    @Override
    public void onError(Exception e) {
        System.err.println("Error: " + e);
    }
});
```

### Pattern 2: Future-Like Blocking (When You Need Result)

```java
Integer result = client.asyncAddWithFuture(5, 10);
System.out.println("Result: " + result);  // Blocks until result arrives
```

### Pattern 3: Parallel Execution

```java
CountDownLatch latch = new CountDownLatch(3);

asyncClient.add(5, 10, callback1);
asyncClient.add(15, 20, callback2);
asyncClient.add(25, 30, callback3);

latch.await(10, TimeUnit.SECONDS);  // Wait for all 3
```

### Pattern 4: Chained Operations

```java
asyncClient.calculate(1, work1, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result1) {
        // Now use result1 to make another call
        asyncClient.calculate(2, work2, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result2) {
                System.out.println("Final: " + result2);
            }
            // ... onError ...
        });
    }
    // ... onError ...
});
```

## Architecture

```
┌─────────────────────────────────┐
│  AsyncCalculatorClient          │
│  ┌─────────────────────────────┐│
│  │ TAsyncClientManager         ││  Manages async I/O
│  └─────────────────────────────┘│
│  ┌─────────────────────────────┐│
│  │ Calculator.AsyncClient      ││  Async RPC methods
│  └─────────────────────────────┘│
│  ┌─────────────────────────────┐│
│  │ TNonblockingSocket          ││  Non-blocking socket
│  └─────────────────────────────┘│
└─────────────────────────────────┘
           ↓
┌─────────────────────────────────┐
│  Thrift Server (localhost:9090) │
└─────────────────────────────────┘
```

## Key Features

✅ Non-blocking I/O via `TNonblockingSocket`  
✅ Callback-based async operations  
✅ Support for parallel execution  
✅ Exception handling in callbacks  
✅ Oneway method support  
✅ Future-like patterns with `CountDownLatch` & `AtomicReference`  
✅ Rate limiting examples  
✅ Full source code with documentation  

## Performance Benefits

| Metric | Sync | Async |
|--------|------|-------|
| Throughput | ~1,000-5,000 ops/sec | ~50,000+ ops/sec |
| Latency (single) | ~10ms | ~10ms |
| Threads blocked | 1 per client | 0 (event-loop driven) |
| Memory overhead | Low | Low |
| CPU usage | High (blocking) | Low (non-blocking) |

## Error Handling

Always implement both callbacks:

```java
// ✓ Correct
async Client.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result) { 
        // SUCCESS: Handle result
    }

    @Override
    public void onError(Exception e) { 
        // ERROR: Handle exception
        e.printStackTrace();
    }
});

// ✗ Wrong - silently ignores errors
asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
    @Override
    public void onComplete(Integer result) { }

    @Override
    public void onError(Exception e) { }  // Silent failure!
});
```

## More Information

- See [ASYNC_EXAMPLES.md](ASYNC_EXAMPLES.md) for detailed patterns and best practices
- See [README.md](README.md) for project overview
- See [TESTING.md](TESTING.md) for test details

---

**Try it now:**
```bash
gradlew runServer  # Terminal 1
gradlew runAsyncClient  # Terminal 2
```
