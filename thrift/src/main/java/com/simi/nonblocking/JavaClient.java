package com.simi.nonblocking;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import shared.SharedStruct;
import tutorial.Calculator;
import tutorial.InvalidOperation;
import tutorial.Operation;
import tutorial.Work;

import java.util.concurrent.CountDownLatch;

public class JavaClient {
    public static void main(String[] args) {
        try {
            TNonblockingTransport transport = new TNonblockingSocket("localhost", 9090);
            TAsyncClientManager clientManager = new TAsyncClientManager();
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

            Calculator.AsyncClient client = new Calculator.AsyncClient(protocolFactory, clientManager, transport);

            perform(client);

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private static void perform(Calculator.AsyncClient client) throws Exception {
        CountDownLatch latch = new CountDownLatch(4);

        System.out.println("Calling ping");
        client.ping(new AsyncMethodCallback<Void>() {
            @Override
            public void onComplete(Void response) {
                System.out.println("ping() successful");
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.out.println("ping() failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        System.out.println("Calling add");
        client.add(1, 1, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer response) {
                System.out.println("1+1=" + response);
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                System.out.println("add() failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        Work work = new Work();
        work.op = Operation.DIVIDE;
        work.num1 = 1;
        work.num2 = 0;
        
        System.out.println("Calling calculate divide by 0");
        client.calculate(1, work, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer response) {
                System.out.println("Whoa we can divide by 0");
                latch.countDown();
            }

            @Override
            public void onError(Exception exception) {
                if (exception instanceof InvalidOperation) {
                    System.out.println("Invalid operation: " + ((InvalidOperation) exception).why);
                } else {
                    System.out.println("calculate() failed: " + exception.getMessage());
                }
                latch.countDown();
            }
        });

        work.op = Operation.SUBTRACT;
        work.num1 = 15;
        work.num2 = 10;
        
        System.out.println("Calling calculate subtract");
        client.calculate(1, work, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer response) {
                System.out.println("15-10=" + response);
                
                try {
                    client.getStruct(1, new AsyncMethodCallback<SharedStruct>() {
                        @Override
                        public void onComplete(SharedStruct log) {
                            System.out.println("Check log: " + log.value);
                            latch.countDown();
                        }
                        
                        @Override
                        public void onError(Exception exception) {
                            System.out.println("getStruct() failed: " + exception.getMessage());
                            latch.countDown();
                        }
                    });
                } catch (TException e) {
                    e.printStackTrace();
                    latch.countDown();
                }
            }

            @Override
            public void onError(Exception exception) {
                System.out.println("calculate() subtract failed: " + exception.getMessage());
                latch.countDown();
            }
        });

        latch.await();
        System.out.println("Finished all async operations");
        System.exit(0);
    }
}