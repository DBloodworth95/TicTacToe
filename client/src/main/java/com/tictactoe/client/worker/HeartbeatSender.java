package com.tictactoe.client.worker;

import com.tictactoe.client.MessageListener;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class HeartbeatSender implements Runnable {

    private final BlockingQueue<String> heartbeatPipe;

    private final List<MessageListener> messageListenerList;

    private boolean running = true;

    public HeartbeatSender(BlockingQueue<String> heartbeatPipe, List<MessageListener> messageListenerList) {
        this.heartbeatPipe = heartbeatPipe;
        this.messageListenerList = messageListenerList;
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
                String[] tokens = heartbeatPipe.take().split(" ");
                for (MessageListener messageListener : messageListenerList) {
                    messageListener.onMessage(null, tokens);
                }
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
