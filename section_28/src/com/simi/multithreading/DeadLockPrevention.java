package com.simi.multithreading;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockPrevention {
    private static final Lock L1 = new ReentrantLock();
    private static final Lock L2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> acquireLocks(L1, L2, "T1"));
        Thread thread2 = new Thread(() -> acquireLocks(L2, L1, "T2"));

        thread1.start();
        thread2.start();
    }

    private static void acquireLocks(Lock firstLock, Lock secondLock, String threadName) {
        while (true) {
            boolean gotFirstLock = firstLock.tryLock();
            boolean gotSecondLock = secondLock.tryLock();
            gotFirstLock = firstLock.tryLock();

            if (gotFirstLock && gotSecondLock) {
                try {
                    System.out.println(threadName + " acquired both locks.");
                    return;
                } finally {
                    firstLock.unlock();
                    firstLock.unlock();
                    secondLock.unlock();
                }
            }

            // Preemption: Nếu không lấy đủ 2 khóa, giải phóng khóa đã lấy được để tránh deadlock
            if (gotFirstLock) {
                firstLock.unlock();
            }
            if (gotSecondLock) {
                secondLock.unlock();
            }

            // Chờ một khoảng thời gian ngắn trước khi thử lại (Back-off) để tránh Livelock
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}