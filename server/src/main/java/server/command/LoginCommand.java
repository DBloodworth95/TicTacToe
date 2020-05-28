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
            System.out.println(loggedIn);
            login.set(loggedIn);
            String msg = "online\n";
            outputStream.write(msg.getBytes());
            System.out.println("User " + login + " has logged in!");
            String onlineMsg = "Online " + login + " has logged in!" + "\n";
            List<ClientHandler> handlerList = server.getHandlers();
            for (ClientHandler handler : handlerList) {
                if (!login.toString().equalsIgnoreCase(handler.getLogin())) {
                    handler.send(onlineMsg);
                }
            }
            String naughtMsg = "naught" + "\n";
            String crossMsg = "cross" + "\n";
            if (server.getHandlers().size() == 1) {
                outputStream.write(naughtMsg.getBytes());
                System.out.println("naught sent");
            } else {
                outputStream.write(crossMsg.getBytes());
                System.out.println("cross sent");
            }
        }
    }

    @Override
    public void execute() {

    }
}
