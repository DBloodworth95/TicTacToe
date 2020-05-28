package com.tictactoe.client.command;

import java.util.concurrent.BlockingQueue;

public class Heartbeat implements Command {

    private final BlockingQueue<String> heartbeatPipe;

    public Heartbeat(BlockingQueue<String> heartbeatPipe) {
        this.heartbeatPipe = heartbeatPipe;
    }

    @Override
    public void execute(String[] tokens) {
        heartbeatPipe.offer(tokens[0]);
        System.out.println(tokens[0]);
    }
}
