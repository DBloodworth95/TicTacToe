package com.tictactoe.client.worker;

import com.tictactoe.client.Client;
import com.tictactoe.client.MessageListener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ServerWriter implements Runnable {

    private final Client client;

    private final BlockingQueue<String> msgPipe;

    private final OutputStream outputStream;

    private List<MessageListener> messageListenerList;

    private boolean running = true;

    public ServerWriter(Client client, BlockingQueue<String> msgPipe, OutputStream outputStream, List<MessageListener> messageListenerList) {
        this.client = client;
        this.msgPipe = msgPipe;
        this.outputStream = outputStream;
        this.messageListenerList = messageListenerList;
    }

    @Override
    public void run() {
        for (; ;) {
            synchronized (this) {
                if (!running) {
                    break;
                }
            }
            try {
                String msg = msgPipe.take();
                outputStream.write(msg.getBytes());
            } catch (IOException | InterruptedException e) {
                for (MessageListener messageListener : messageListenerList) {
                    messageListener.onMessage(null, "disconnect".split(" "));
                    client.stopWorkerThreads();
                }
            }
        }
    }

    public void stop() {
        synchronized (this) {
            running = false;
        }
    }
}
