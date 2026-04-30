package com.simi;

import learn.MultiplicationService;
import learn.MultiplicationService.Processor;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class MultiplicationServer {
    private static final int PORT = 9090;

    public static class MultiplicationHandler implements MultiplicationService.Iface {

        @Override
        public int multiply(int n1, int n2) throws TException {
            System.out.println("Multiplying " + n1 + " and " + n2);
            return n1 * n2;
        }
    }

    public void start() throws Exception {
        MultiplicationHandler handler = new MultiplicationHandler();

        Processor<MultiplicationHandler> processor = new MultiplicationService.Processor<>(handler);

        TServerTransport serverTransport = new TServerSocket(PORT);
        TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport)
                .processor(processor);
        TServer server = new TThreadPoolServer(args);
        System.out.println("Starting Thrift Server on port " + PORT);
        server.serve();
    }

    public static void main (String[] args) throws Exception {
        MultiplicationServer server = new MultiplicationServer();
        server.start();
    }

}
