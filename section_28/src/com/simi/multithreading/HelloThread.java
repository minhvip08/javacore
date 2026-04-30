package com.simi.multithreading;

public class HelloThread extends Thread {

    @Override
    public void run () {
        System.out.println("Hello from HelloThread class " +
                " which extends Thread");
    }

}
