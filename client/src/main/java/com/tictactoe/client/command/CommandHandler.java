package com.tictactoe.client.command;

import com.tictactoe.client.MessageListener;
import com.tictactoe.client.SymbolAssigner;
import com.tictactoe.symbol.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CommandHandler {

    private final Map<String, Command> clientCommands = new HashMap<>();

    private final List<MessageListener> messageListeners;

    private final BlockingQueue<String> heartbeatPipe;

    public CommandHandler(List<MessageListener> messageListeners, BlockingQueue<String> heartbeatPipe) {
        this.messageListeners = messageListeners;
        this.heartbeatPipe = heartbeatPipe;
        install();
    }

    private void install() {
        clientCommands.put("addcross", new AddSymbol(messageListeners));
        clientCommands.put("addnaught", new AddSymbol(messageListeners));
        clientCommands.put("win", new Win(messageListeners));
        clientCommands.put("isalive", new Heartbeat(heartbeatPipe));
        clientCommands.put("waiting", new LobbyStatus(messageListeners));
        clientCommands.put("ready", new LobbyStatus(messageListeners));
    }

    public void handle(String cmd, String[] tokens, AtomicReference<Symbol> s) {
        if ("cross".equalsIgnoreCase(cmd) || "naught".equalsIgnoreCase(cmd)) {
            s.set(SymbolAssigner.assign(cmd));
        }
        if (clientCommands.containsKey(cmd)) {
            Command c = clientCommands.get(cmd);
            c.execute(tokens);
        }
    }
}
