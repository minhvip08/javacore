package com.simi;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import tutorial.Calculator;
import tutorial.InvalidOperation;
import tutorial.Operation;
import tutorial.Work;
import shared.SharedStruct;

public class CalculatorServer {
    private static final int PORT = 9090;
    private TServer server;

    public static class CalculatorHandler implements Calculator.Iface {

        @Override
        public void ping() {
            System.out.println("ping() called");
        }

        @Override
        public int add(int num1, int num2) {
            System.out.println("add(" + num1 + "," + num2 + ") called");
            return num1 + num2;
        }

        @Override
        public int calculate(int logid, Work w) throws InvalidOperation {
            System.out.println("calculate(" + logid + "," + w + ") called");
            int val = 0;
            switch (w.op) {
                case ADD:
                    val = w.num1 + w.num2;
                    break;
                case SUBTRACT:
                    val = w.num1 - w.num2;
                    break;
                case MULTIPLY:
                    val = w.num1 * w.num2;
                    break;
                case DIVIDE:
                    if (w.num2 == 0) {
                        InvalidOperation io = new InvalidOperation();
                        io.whatOp = w.op.getValue();
                        io.why = "Cannot divide by zero";
                        throw io;
                    }
                    val = w.num1 / w.num2;
                    break;
                default:
                    InvalidOperation io = new InvalidOperation();
                    io.whatOp = -1;
                    io.why = "Unknown operation";
                    throw io;
            }
            return val;
        }

        @Override
        public void zip() {
            System.out.println("zip() called (oneway)");
        }

        @Override
        public SharedStruct getStruct(int key) {
            System.out.println("getStruct(" + key + ") called");
            SharedStruct result = new SharedStruct();
            result.key = key;
            result.value = "Value for key " + key;
            return result;
        }
    }

    public void start() throws Exception {
        CalculatorHandler handler = new CalculatorHandler();
        Calculator.Processor processor = new Calculator.Processor<>(handler);
        
        TServerTransport serverTransport = new TServerSocket(PORT);
        TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport)
            .processor(processor);
        
        server = new TThreadPoolServer(args);
        System.out.println("Starting Thrift Server on port " + PORT);
        server.serve();
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    public static void main(String[] args) throws Exception {
        CalculatorServer server = new CalculatorServer();
        server.start();
    }
}
