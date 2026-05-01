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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

    @FunctionalInterface
    private interface AsyncThriftCall<T> {
        void call(AsyncMethodCallback<T> callback) throws Exception;
    }

    private static <T> CompletableFuture<T> asyncCall(AsyncThriftCall<T> call) {
        CompletableFuture<T> future = new CompletableFuture<>();
        try {
            call.call(new AsyncMethodCallback<T>() {
                @Override
                public void onComplete(T response) {
                    future.complete(response);
                }

                @Override
                public void onError(Exception exception) {
                    future.completeExceptionally(exception);
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    private static void perform(Calculator.AsyncClient client) throws Exception {
        System.out.println("Calling ping");
        CompletableFuture<Void> pingFuture = asyncCall(client::ping)
                .thenRun(() -> System.out.println("ping() successful"))
                .exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                    System.out.println("ping() failed: " + cause.getMessage());
                    return null;
                });

        System.out.println("Calling add");
        CompletableFuture<Integer> addFuture = asyncCall((AsyncMethodCallback<Integer> callback) -> client.add(1, 1, callback))
                .thenApply(sum -> {
                    System.out.println("1+1=" + sum);
                    return sum;
                })
                .exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                    System.out.println("add() failed: " + cause.getMessage());
                    return null;
                });

        Work divideWork = new Work();
        divideWork.op = Operation.DIVIDE;
        divideWork.num1 = 1;
        divideWork.num2 = 0;

        System.out.println("Calling calculate divide by 0");
        CompletableFuture<Integer> calcDivideFuture = asyncCall((AsyncMethodCallback<Integer> callback) -> client.calculate(1, divideWork, callback))
                .thenApply(result -> {
                    System.out.println("Whoa we can divide by 0");
                    return result;
                })
                .exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                    if (cause instanceof InvalidOperation) {
                        System.out.println("Invalid operation: " + ((InvalidOperation) cause).why);
                    } else {
                        System.out.println("calculate() failed: " + cause.getMessage());
                    }
                    return null;
                });

        Work subtractWork = new Work();
        subtractWork.op = Operation.SUBTRACT;
        subtractWork.num1 = 15;
        subtractWork.num2 = 10;

        System.out.println("Calling calculate subtract");
        CompletableFuture<Void> calcSubtractFuture = asyncCall((AsyncMethodCallback<Integer> callback) -> client.calculate(1, subtractWork, callback))
                .thenApply(result -> {
                    System.out.println("15-10=" + result);
                    return result;
                })
                .thenCompose(result -> asyncCall((AsyncMethodCallback<SharedStruct> callback) -> client.getStruct(1, callback)))
                .thenAccept(log -> System.out.println("Check log: " + log.value))
                .exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                    System.out.println("calculate() subtract failed: " + cause.getMessage());
                    return null;
                });

        CompletableFuture.allOf(pingFuture, addFuture, calcDivideFuture, calcSubtractFuture).join();
        System.out.println("Finished all async operations");
        System.exit(0);
    }
}
