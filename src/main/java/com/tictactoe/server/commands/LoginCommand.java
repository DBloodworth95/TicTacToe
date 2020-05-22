package com.tictactoe.server.commands;

import com.tictactoe.game.Board;
import com.tictactoe.server.Server;
import com.tictactoe.server.ServerHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LoginCommand implements Command {

    @Override
    public void execute(OutputStream outputStream, String[] tokens, AtomicReference<String> login, Server server, Board board) throws IOException {
        if (tokens.length == 2) {
            String loggedIn = tokens[1];
            System.out.println(loggedIn);
            login.set(loggedIn);
            String msg = "online\n";
            outputStream.write(msg.getBytes());
            System.out.println("User " + login + " has logged in!");
            String onlineMsg = "Online " + login + " has logged in!" + "\n";
            List<ServerHandler> handlerList = server.getHandlers();
            for (ServerHandler handler : handlerList) {
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
