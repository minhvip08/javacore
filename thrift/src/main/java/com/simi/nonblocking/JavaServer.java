package com.simi.nonblocking;

import com.simi.CalculatorHandler;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import tutorial.Calculator;

public class JavaServer {

    public static CalculatorHandler handler;
    public static Calculator.Processor<CalculatorHandler> processor;

    public static void main(String [] args) {
        try {
            handler = new CalculatorHandler();
            processor = new Calculator.Processor<>(handler);

            Runnable simple = () -> simple(processor);
            new Thread(simple).start();

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(Calculator.Processor<CalculatorHandler> processor) {
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(9090);
            TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple non-blocking server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
