package com.simi;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import tutorial.Calculator;
import tutorial.InvalidOperation;
import tutorial.Operation;
import tutorial.Work;
import shared.SharedStruct;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Asynchronous Thrift Client for Calculator Service
 * 
 * This client demonstrates non-blocking, asynchronous RPC calls using Thrift's
 * async capabilities with callbacks.
 */
public class AsyncCalculatorClient {
    private static final String HOST = "localhost";
    private static final int PORT = 9090;
    private static final int TIMEOUT_SECONDS = 5;

    private TAsyncClientManager clientManager;
    public Calculator.AsyncClient asyncClient;  // Public for tests

    /**
     * Initialize the async client manager and connect to server
     */
    public void connect() throws Exception {
        clientManager = new TAsyncClientManager();
        TNonblockingSocket nonblockingSocket = new TNonblockingSocket(HOST, PORT);
        TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
        asyncClient = new Calculator.AsyncClient(protocolFactory, clientManager, nonblockingSocket);
        System.out.println("✓ Async client connected to " + HOST + ":" + PORT);
    }

    /**
     * Disconnect and cleanup resources
     */
    public void disconnect() throws Exception {
        if (clientManager != null) {
            clientManager.stop();
            System.out.println("✓ Async client disconnected");
        }
    }

    /**
     * Example 1: Simple async ping with callback
     */
    public void asyncPing(Runnable onComplete) throws TException {
        System.out.println("\n[Async Example 1] Ping with Callback");
        System.out.println("Calling ping() asynchronously...");

        asyncClient.ping(new AsyncMethodCallback<Void>() {
            @Override
            public void onComplete(Void result) {
                System.out.println("✓ Ping completed successfully");
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Ping failed: " + exception.getMessage());
                if (onComplete != null) onComplete.run();
            }
        });
    }

    /**
     * Example 2: Async add with callback
     */
    public void asyncAdd(int num1, int num2, Runnable onComplete) throws TException {
        System.out.println("\n[Async Example 2] Add with Callback");
        System.out.println("Calling add(" + num1 + ", " + num2 + ") asynchronously...");

        asyncClient.add(num1, num2, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result) {
                System.out.println("✓ add(" + num1 + ", " + num2 + ") returned: " + result);
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ add() failed: " + exception.getMessage());
                if (onComplete != null) onComplete.run();
            }
        });
    }

    /**
     * Example 3: Async calculate with multiple operations (chained async calls)
     */
    public void asyncCalculateChained(Runnable onComplete) throws TException {
        System.out.println("\n[Async Example 3] Chained Async Calculations");
        System.out.println("Executing multiple calculations in sequence...");

        // First calculation: 10 + 5
        Work work1 = new Work();
        work1.num1 = 10;
        work1.num2 = 5;
        work1.op = Operation.ADD;
        work1.comment = "First calculation: 10 + 5";

        asyncClient.calculate(1, work1, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result) {
                System.out.println("✓ First calculation (ADD): 10 + 5 = " + result);

                // Second calculation based on result: result * 2
                Work work2 = new Work();
                work2.num1 = result;
                work2.num2 = 2;
                work2.op = Operation.MULTIPLY;
                work2.comment = "Second calculation: " + result + " * 2";

                try {
                    asyncClient.calculate(2, work2, new AsyncMethodCallback<Integer>() {
                        @Override
                        public void onComplete(Integer result2) {
                            System.out.println("✓ Second calculation (MULTIPLY): " + result + " * 2 = " + result2);
                            if (onComplete != null) onComplete.run();
                        }

                        @Override
                        public void onError(Exception exception) {
                            System.err.println("✗ Second calculation failed: " + exception.getMessage());
                            if (onComplete != null) onComplete.run();
                        }
                    });
                } catch (TException e) {
                    System.err.println("✗ Failed to queue second calculation: " + e.getMessage());
                    if (onComplete != null) onComplete.run();
                }
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ First calculation failed: " + exception.getMessage());
                if (onComplete != null) onComplete.run();
            }
        });
    }

    /**
     * Example 4: Parallel async operations with CountDownLatch
     */
    public void asyncParallelOperations(Runnable onComplete) throws InterruptedException, TException {
        System.out.println("\n[Async Example 4] Parallel Async Operations");
        System.out.println("Executing 5 operations in parallel...");

        CountDownLatch latch = new CountDownLatch(5);

        // Operation 1: Ping
        asyncClient.ping(new AsyncMethodCallback<Void>() {
            @Override
            public void onComplete(Void result) {
                System.out.println("✓ Parallel Op 1: Ping completed");
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Parallel Op 1 failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        // Operation 2: Add
        asyncClient.add(5, 10, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result) {
                System.out.println("✓ Parallel Op 2: Add(5, 10) = " + result);
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Parallel Op 2 failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        // Operation 3: Calculate ADD
        Work work3 = new Work();
        work3.num1 = 20;
        work3.num2 = 15;
        work3.op = Operation.ADD;

        asyncClient.calculate(3, work3, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result) {
                System.out.println("✓ Parallel Op 3: Calculate ADD(20, 15) = " + result);
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Parallel Op 3 failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        // Operation 4: Calculate MULTIPLY
        Work work4 = new Work();
        work4.num1 = 7;
        work4.num2 = 6;
        work4.op = Operation.MULTIPLY;

        asyncClient.calculate(4, work4, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result) {
                System.out.println("✓ Parallel Op 4: Calculate MULTIPLY(7, 6) = " + result);
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Parallel Op 4 failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        // Operation 5: GetStruct
        asyncClient.getStruct(100, new AsyncMethodCallback<SharedStruct>() {
            @Override
            public void onComplete(SharedStruct result) {
                System.out.println("✓ Parallel Op 5: GetStruct(100) = " + result);
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Parallel Op 5 failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        // Wait for all operations to complete
        System.out.println("Waiting for all parallel operations to complete...");
        if (latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            System.out.println("✓ All parallel operations completed");
        } else {
            System.out.println("✗ Timeout waiting for parallel operations");
        }

        if (onComplete != null) onComplete.run();
    }

    /**
     * Example 5: Exception handling with async divide by zero
     */
    public void asyncExceptionHandling(Runnable onComplete) throws TException {
        System.out.println("\n[Async Example 5] Exception Handling (Divide by Zero)");
        System.out.println("Calling divide by zero asynchronously...");

        Work work = new Work();
        work.num1 = 10;
        work.num2 = 0;
        work.op = Operation.DIVIDE;

        asyncClient.calculate(5, work, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer result) {
                System.out.println("✓ Calculate completed (unexpected): " + result);
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onError(Exception exception) {
                if (exception instanceof InvalidOperation) {
                    InvalidOperation io = (InvalidOperation) exception;
                    System.out.println("✓ Caught expected exception: " + io.getWhy());
                } else {
                    System.err.println("✗ Unexpected error: " + exception.getMessage());
                }
                if (onComplete != null) onComplete.run();
            }
        });
    }

    /**
     * Example 6: Async with Future-like pattern using AtomicReference
     */
    public Integer asyncAddWithFuture(int num1, int num2) throws InterruptedException, TException {
        System.out.println("\n[Async Example 6] Async with Future Pattern");
        System.out.println("Calling add(" + num1 + ", " + num2 + ") with future-like pattern...");

        AtomicReference<Integer> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        asyncClient.add(num1, num2, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer r) {
                result.set(r);
                System.out.println("✓ Async add result received: " + r);
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Async add failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        // Wait for result
        if (latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            return result.get();
        } else {
            throw new RuntimeException("Timeout waiting for async result");
        }
    }

    /**
     * Example 7: Async oneway method (zip)
     */
    public void asyncZip(Runnable onComplete) throws TException {
        System.out.println("\n[Async Example 7] Async Oneway Method (zip)");
        System.out.println("Calling zip() asynchronously (oneway, no response expected)...");

        asyncClient.zip(new AsyncMethodCallback<Void>() {
            @Override
            public void onComplete(Void result) {
                System.out.println("✓ Zip oneway call sent (no response expected)");
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("✗ Zip oneway call failed: " + exception.getMessage());
                if (onComplete != null) onComplete.run();
            }
        });
    }

    /**
     * Example 8: Rate-limited async operations (simulate flow control)
     */
    public void asyncRateLimitedOperations(Runnable onComplete) throws InterruptedException {
        System.out.println("\n[Async Example 8] Rate-Limited Async Operations");
        System.out.println("Executing operations with 100ms delay between each...");

        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 1; i <= 5; i++) {
            final int operationId = i;
            
            Thread.sleep(100); // Simulate rate limiting
            
            try {
                asyncClient.add(i * 10, i * 5, new AsyncMethodCallback<Integer>() {
                    @Override
                    public void onComplete(Integer result) {
                        System.out.println("✓ Rate-limited Op " + operationId + ": add(" + (operationId * 10) + ", " + (operationId * 5) + ") = " + result);
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception exception) {
                        System.err.println("✗ Rate-limited Op " + operationId + " failed: " + exception.getMessage());
                        latch.countDown();
                    }
                });
            } catch (org.apache.thrift.TException e) {
                System.err.println("✗ Failed to queue operation: " + e.getMessage());
                latch.countDown();
            }
        }

        System.out.println("All operations queued, waiting for responses...");
        if (latch.await(10, TimeUnit.SECONDS)) {
            System.out.println("✓ All rate-limited operations completed");
        } else {
            System.out.println("✗ Timeout waiting for rate-limited operations");
        }

        if (onComplete != null) onComplete.run();
    }

    // ============= Main Demo =============

    public static void main(String[] args) throws Exception {
        AsyncCalculatorClient client = new AsyncCalculatorClient();

        try {
            client.connect();

            // Run examples sequentially with slight delays
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   Thrift Async Client Examples                    ║");
            System.out.println("╚════════════════════════════════════════════════════╝");

            // Example 1: Simple async ping
            CountDownLatch latch1 = new CountDownLatch(1);
            client.asyncPing(latch1::countDown);
            latch1.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Thread.sleep(100);

            // Example 2: Async add
            CountDownLatch latch2 = new CountDownLatch(1);
            client.asyncAdd(5, 10, latch2::countDown);
            latch2.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Thread.sleep(100);

            // Example 3: Chained async calls
            CountDownLatch latch3 = new CountDownLatch(1);
            client.asyncCalculateChained(latch3::countDown);
            latch3.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Thread.sleep(100);

            // Example 4: Parallel async operations
            client.asyncParallelOperations(() -> {});
            Thread.sleep(500);

            // Example 5: Exception handling
            CountDownLatch latch5 = new CountDownLatch(1);
            client.asyncExceptionHandling(latch5::countDown);
            latch5.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Thread.sleep(100);

            // Example 6: Future-like pattern
            Integer futureResult = client.asyncAddWithFuture(20, 30);
            System.out.println("Final result from future-like call: " + futureResult);
            Thread.sleep(100);

            // Example 7: Async oneway method
            CountDownLatch latch7 = new CountDownLatch(1);
            client.asyncZip(latch7::countDown);
            latch7.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Thread.sleep(100);

            // Example 8: Rate-limited operations
            client.asyncRateLimitedOperations(() -> {});

            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   All async examples completed successfully!      ║");
            System.out.println("╚════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.disconnect();
        }
    }
}
