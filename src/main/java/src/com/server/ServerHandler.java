package src.com.server;

import src.com.server.commands.Command;
import src.com.server.commands.CommandExecuter;
import src.com.server.commands.LoginCommand;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerHandler extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream sendStream;
    Map<String, Command> serverCommands = new HashMap<>();
    public ServerHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        serverCommands.put("login", new LoginCommand());
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
               for(Map.Entry<String, Command> command : serverCommands.entrySet()) {
                   if(cmd.equalsIgnoreCase(command.getKey())) {
                       CommandExecuter commandExecuter = new CommandExecuter(command.getValue());
                       commandExecuter.executeCommand(sendStream, tokens, this.login, server);
                   }
               }
           }
       }
    }

    public String getLogin() {
        return login;
    }

    public void send(String msg) throws IOException {
        if(login != null)
            sendStream.write(msg.getBytes());
    }
}
