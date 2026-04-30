package com.simi;

import learn.MultiplicationService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class MultiplicationClient {
    private static final String HOST = "localhost";
    private static final int PORT = 9090;
    private MultiplicationService.Client client;
    private TTransport transport;

    public void connect () throws Exception {
        transport = new TSocket(HOST, PORT);
        TProtocol protocol = new TBinaryProtocol(transport);
        client = new MultiplicationService.Client(protocol);
        transport.open();
        System.out.println("Connected to Thrift Server at " + HOST + ":" + PORT);
    }

    public int multiply(int n1, int n2) throws Exception {
        return client.multiply(n1, n2);
    }

    public void disconnect() throws Exception {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }



    public static void main(String[] args) throws Exception {
        MultiplicationClient client = new MultiplicationClient();
        client.connect();
        System.out.println(client.multiply(10, 20));
        client.disconnect();
    }

}
