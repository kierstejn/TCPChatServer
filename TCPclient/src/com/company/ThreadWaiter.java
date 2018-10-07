package com.company;

public class ThreadWaiter {

        private final Object lock = new Object();

        public void signal() {
            synchronized (lock) {
                lock.notify();
            }
        }

        public void await() throws InterruptedException {
            synchronized (lock) {
                lock.wait();
            }
        }
    }

