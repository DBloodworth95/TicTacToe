package com.tictactoe.server;

import com.tictactoe.server.commands.AddCrossCommand;
import com.tictactoe.server.commands.AddNaughtCommand;
import com.tictactoe.server.commands.Command;
import com.tictactoe.server.commands.LoginCommand;


import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ServerHandler extends Thread {
    private final Socket clientSocket;
    private final Server server;
    public AtomicReference<String> login = new AtomicReference<>();
    private OutputStream sendStream;
    Map<String, Command> serverCommands = new HashMap<>();

    public ServerHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        serverCommands.put("login", new LoginCommand());
        serverCommands.put("addnaught", new AddNaughtCommand());
        serverCommands.put("addcross", new AddCrossCommand());
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
       InputStream inputStream = clientSocket.getInputStream();
       this.sendStream = clientSocket.getOutputStream();
       BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
       String line;
       while((line = reader.readLine()) != null) {
           String[] tokens = line.split(" ");
           if(tokens.length > 0) {
               String cmd = tokens[0];
               if(serverCommands.containsKey(cmd)) {
                   Command c = serverCommands.get(cmd);
                   c.execute(sendStream, tokens, login, server);
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
        if(login != null)
            sendStream.write(msg.getBytes());
    }
}
