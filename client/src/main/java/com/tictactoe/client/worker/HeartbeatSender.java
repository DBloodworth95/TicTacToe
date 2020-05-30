package com.tictactoe.client.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class HeartbeatSender implements Runnable {

    private final BlockingQueue<String> msgPipe;

    private final String username;

    private boolean running = true;

    public HeartbeatSender(BlockingQueue<String> msgPipe, String username) {
        this.msgPipe = msgPipe;
        this.username = username;
    }

    @Override
    public void run() {
        for (; ; ) {
            synchronized (this) {
                if (!running) {
                    break;
                }
            }
            try {
                msgPipe.offer("isalive" + " " + username + "\n");
                System.out.println("Sending heartbeat to server");
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        synchronized (this) {
            running = false;
        }
    }
}
