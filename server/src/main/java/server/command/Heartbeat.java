package server.command;

import server.ClientHandler;
import server.Server;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Heartbeat implements Command {

    private final OutputStream outputStream;

    private final AtomicReference<String> login;

    private final Server server;

    public Heartbeat(OutputStream outputStream, AtomicReference<String> login, Server server) {
        this.outputStream = outputStream;
        this.login = login;
        this.server = server;
    }

    @Override
    public void execute(String[] tokens) {
        List<ClientHandler> handlerList = server.getHandlers();
        String username = tokens[1];
        for (ClientHandler clientHandler : handlerList) {
            if (clientHandler.getLogin().equalsIgnoreCase(username)) {
                clientHandler.send("isalive\n");
            }
        }
    }

    @Override
    public void execute() {

    }
}
