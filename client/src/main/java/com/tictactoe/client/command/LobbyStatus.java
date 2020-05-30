package com.tictactoe.client.command;

import com.tictactoe.client.MessageListener;

import java.util.List;

public class LobbyStatus implements Command {

    private final List<MessageListener> messageListeners;

    public LobbyStatus(List<MessageListener> messageListeners) {
        this.messageListeners = messageListeners;
    }

    @Override
    public void execute(String[] tokens) {
        for (MessageListener listener : messageListeners) {
            listener.onMessage(null, tokens);
        }
    }
}
