package server.command;

import server.Board;
import server.ClientHandler;
import server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LoginCommand implements Command {
    private final OutputStream outputStream;

    private final AtomicReference<String> login;

    private final Server server;

    private final Board board;

    public LoginCommand(OutputStream outputStream, AtomicReference<String> login, Server server, Board board) {
        this.outputStream = outputStream;
        this.login = login;
        this.server = server;
        this.board = board;
    }

    @Override
    public void execute(String[] tokens) throws IOException {
        if (tokens.length == 2) {
            String loggedIn = tokens[1];
            login.set(loggedIn);
            outputStream.write("online\n".getBytes());
            System.out.println("User " + login + " has logged in!");
            List<ClientHandler> handlerList = server.getHandlers();
            for (ClientHandler handler : handlerList) {
                if (!login.toString().equalsIgnoreCase(handler.getLogin())) {
                    handler.send("Online " + login + " has logged in!" + "\n");
                }
            }
            if (server.getHandlers().size() == 1) {
                outputStream.write("naught\n".getBytes());
                for (ClientHandler handler : handlerList)
                    handler.send("waiting\n");
                board.setP1(login.toString());
            } else {
                outputStream.write("cross\n".getBytes());
                for (ClientHandler handler : handlerList)
                    handler.send("ready\n");
                board.setP2(login.toString());
            }
        }
    }

    @Override
    public void execute() {

    }
}
