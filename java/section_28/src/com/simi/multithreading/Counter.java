package com.simi.multithreading;

public class Counter {

    private int count = 0;

    public void incrementUnSynchronized() {
        count++;
    }

    public synchronized void incrementSynchronized() {
        count++;
        synchronized (Counter.class) {
            synchronized (Counter.class) {
                count++;
            }
        }
    }

    public int getCount() {
        return count;
    }
}
