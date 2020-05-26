package com.tictactoe.server;

import com.tictactoe.game.Board;
import com.tictactoe.server.commands.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private Board board;
    public AtomicReference<String> login = new AtomicReference<>();
    private OutputStream sendStream;
    Map<String, Command> serverCommands = new HashMap<>();


    public ClientHandler(Server server, Socket clientSocket, Board board) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.board = board;

    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.sendStream = clientSocket.getOutputStream();

        assignCmds();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens.length > 0) {
                String cmd = tokens[0];
                if (serverCommands.containsKey(cmd)) {
                    Command c = serverCommands.get(cmd);
                    c.execute(tokens);
                } else {
                    String invCmd = "Invalid command: " + tokens[0] + "\n";
                    sendStream.write(invCmd.getBytes());
                }
            }
        }
    }

    public String getLogin() {
        return login.toString();
    }

    public void send(String msg) throws IOException {
        if (login != null)
            sendStream.write(msg.getBytes());
    }

    private void assignCmds() {
        serverCommands.put("login", new LoginCommand(sendStream, login, server, board));
        serverCommands.put("addnaught", new AddNaughtCommand(sendStream, login, server, board));
        serverCommands.put("addcross", new AddCrossCommand(sendStream, login, server, board));
    }
}
