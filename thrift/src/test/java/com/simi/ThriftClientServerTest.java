package com.simi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import tutorial.InvalidOperation;
import tutorial.Operation;
import shared.SharedStruct;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ThriftClientServerTest {
    private CalculatorServer serverInstance;
    private CalculatorClient client;
    private Thread serverThread;

    @BeforeEach
    public void setup() throws Exception {
        // Start server in a separate thread
        serverInstance = new CalculatorServer();
        serverThread = new Thread(() -> {
            try {
                serverInstance.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Give server time to start
        Thread.sleep(1000);
        
        // Create and connect client
        client = new CalculatorClient();
        client.connect();
    }

    @AfterEach
    public void teardown() throws Exception {
        if (client != null) {
            client.disconnect();
        }
        if (serverInstance != null) {
            serverInstance.stop();
        }
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread.join(2000);
        }
    }

    @Test
    @Timeout(5)
    public void testPing() throws Exception {
        System.out.println("\n=== Test: Ping ===");
        client.ping();
        System.out.println("✓ Ping successful");
    }

    @Test
    @Timeout(5)
    public void testAdd() throws Exception {
        System.out.println("\n=== Test: Add ===");
        int result = client.add(5, 10);
        assertEquals(15, result, "5 + 10 should equal 15");
        System.out.println("✓ Add test passed");
    }

    @Test
    @Timeout(5)
    public void testAddNegative() throws Exception {
        System.out.println("\n=== Test: Add with Negative Numbers ===");
        int result = client.add(-5, 10);
        assertEquals(5, result, "-5 + 10 should equal 5");
        System.out.println("✓ Add negative test passed");
    }

    @Test
    @Timeout(5)
    public void testCalculateAdd() throws Exception {
        System.out.println("\n=== Test: Calculate ADD ===");
        int result = client.calculate(1, 20, 15, Operation.ADD);
        assertEquals(35, result, "20 + 15 should equal 35");
        System.out.println("✓ Calculate ADD test passed");
    }

    @Test
    @Timeout(5)
    public void testCalculateSubtract() throws Exception {
        System.out.println("\n=== Test: Calculate SUBTRACT ===");
        int result = client.calculate(2, 20, 8, Operation.SUBTRACT);
        assertEquals(12, result, "20 - 8 should equal 12");
        System.out.println("✓ Calculate SUBTRACT test passed");
    }

    @Test
    @Timeout(5)
    public void testCalculateMultiply() throws Exception {
        System.out.println("\n=== Test: Calculate MULTIPLY ===");
        int result = client.calculate(3, 7, 6, Operation.MULTIPLY);
        assertEquals(42, result, "7 * 6 should equal 42");
        System.out.println("✓ Calculate MULTIPLY test passed");
    }

    @Test
    @Timeout(5)
    public void testCalculateDivide() throws Exception {
        System.out.println("\n=== Test: Calculate DIVIDE ===");
        int result = client.calculate(4, 20, 5, Operation.DIVIDE);
        assertEquals(4, result, "20 / 5 should equal 4");
        System.out.println("✓ Calculate DIVIDE test passed");
    }

    @Test
    @Timeout(5)
    public void testCalculateDivideByZero() throws Exception {
        System.out.println("\n=== Test: Calculate DIVIDE by Zero (Exception) ===");
        InvalidOperation exception = assertThrows(InvalidOperation.class, () -> {
            client.calculate(5, 10, 0, Operation.DIVIDE);
        });
        assertTrue(exception.why.contains("Cannot divide by zero"), "Should throw exception for divide by zero");
        System.out.println("✓ Calculate DIVIDE by zero exception test passed");
    }

    @Test
    @Timeout(5)
    public void testZip() throws Exception {
        System.out.println("\n=== Test: Zip (Oneway) ===");
        client.zip();
        System.out.println("✓ Zip test passed");
    }

    @Test
    @Timeout(5)
    public void testGetStruct() throws Exception {
        System.out.println("\n=== Test: GetStruct ===");
        SharedStruct result = client.getStruct(42);
        assertNotNull(result, "Result should not be null");
        assertEquals(42, result.key, "Key should be 42");
        assertTrue(result.value.contains("42"), "Value should contain the key");
        System.out.println("✓ GetStruct test passed");
    }

    @Test
    @Timeout(5)
    public void testMultipleOperations() throws Exception {
        System.out.println("\n=== Test: Multiple Operations in Sequence ===");
        
        client.ping();
        int sum = client.add(10, 20);
        assertEquals(30, sum);
        
        int calc1 = client.calculate(1, 15, 3, Operation.ADD);
        assertEquals(18, calc1);
        
        int calc2 = client.calculate(2, 15, 3, Operation.DIVIDE);
        assertEquals(5, calc2);
        
        SharedStruct struct = client.getStruct(100);
        assertEquals(100, struct.key);
        
        client.zip();
        
        System.out.println("✓ Multiple operations test passed");
    }
}
