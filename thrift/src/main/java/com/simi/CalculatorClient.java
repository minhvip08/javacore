package com.simi;

import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import tutorial.Calculator;
import tutorial.InvalidOperation;
import tutorial.Operation;
import tutorial.Work;
import shared.SharedStruct;

public class CalculatorClient {
    private static final String HOST = "localhost";
    private static final int PORT = 9090;
    private TTransport transport;
    private Calculator.Client client;

    public void connect() throws Exception {
        transport = new TSocket(HOST, PORT);
        TProtocol protocol = new TBinaryProtocol(transport);
        client = new Calculator.Client(protocol);
        transport.open();
        System.out.println("Connected to Thrift Server at " + HOST + ":" + PORT);
    }

    public void disconnect() throws Exception {
        if (transport != null && transport.isOpen()) {
            transport.close();
            System.out.println("Disconnected from Thrift Server");
        }
    }

    public void ping() throws Exception {
        System.out.println("Client: Calling ping()");
        client.ping();
        System.out.println("Client: ping() successful");
    }

    public int add(int num1, int num2) throws Exception {
        System.out.println("Client: Calling add(" + num1 + ", " + num2 + ")");
        int result = client.add(num1, num2);
        System.out.println("Client: add() result = " + result);
        return result;
    }

    public int calculate(int logid, int num1, int num2, Operation op) throws Exception {
        Work work = new Work();
        work.num1 = num1;
        work.num2 = num2;
        work.op = op;
        work.comment = "Calculation";

        System.out.println("Client: Calling calculate(" + logid + ", " + work + ")");
        try {
            int result = client.calculate(logid, work);
            System.out.println("Client: calculate() result = " + result);
            return result;
        } catch (InvalidOperation io) {
            System.out.println("Client: calculate() caught exception - " + io.getWhy());
            throw io;
        }
    }

    public void zip() throws Exception {
        System.out.println("Client: Calling zip() (oneway)");
        client.zip();
        System.out.println("Client: zip() called (no response expected)");
    }

    public SharedStruct getStruct(int key) throws Exception {
        System.out.println("Client: Calling getStruct(" + key + ")");
        SharedStruct result = client.getStruct(key);
        System.out.println("Client: getStruct() result = " + result);
        return result;
    }

    public static void main(String[] args) throws Exception {
        CalculatorClient client = new CalculatorClient();
        
        try {
            client.connect();
            
            // Test ping
            client.ping();
            
            // Test add
            int sum = client.add(5, 10);
            assert sum == 15 : "Add failed";
            
            // Test calculate with different operations
            client.calculate(1, 10, 5, Operation.ADD);
            client.calculate(2, 10, 5, Operation.SUBTRACT);
            client.calculate(3, 10, 5, Operation.MULTIPLY);
            client.calculate(4, 10, 5, Operation.DIVIDE);
            
            // Test zip (oneway)
            client.zip();
            
            // Test getStruct
            SharedStruct struct = client.getStruct(123);
            assert struct.key == 123 : "getStruct failed";
            
            System.out.println("\nAll tests passed!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.disconnect();
        }
    }
}
