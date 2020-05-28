package server;

import server.command.AddCrossCommand;
import server.command.AddNaughtCommand;
import server.command.Command;
import server.command.LoginCommand;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private Board board;
    public AtomicReference<String> login = new AtomicReference<>();
    private OutputStream sendStream;
    Map<String, Command> serverCommands = new HashMap<>();
    private final BlockingQueue<String> msgPipe = new ArrayBlockingQueue<>(100);


    public ClientHandler(Server server, Socket clientSocket, Board board) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.board = board;
    }

    @Override
    public void run() {
        Thread t = new Thread(this::handleClientSocket);
        Thread s = new Thread(this::startMsgWriter);
        t.start();
        s.start();
    }

    private void handleClientSocket() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            this.sendStream = clientSocket.getOutputStream();
            assignCmds();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens.length > 0) {
                    String cmd = tokens[0];
                    System.out.println(serverCommands.containsKey(cmd));
                    if (serverCommands.containsKey(cmd)) {
                        Command c = serverCommands.get(cmd);
                        try {
                            c.execute(tokens);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String invCmd = "Invalid command: " + tokens[0] + "\n";
                        msgPipe.offer(invCmd);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return login.toString();
    }

    public void send(String msg) {
        if (login != null) {
            msgPipe.offer(msg);
        }
    }

    public void startMsgWriter() {
        try {
            while (true) {
                String msg = msgPipe.take();
                sendStream.write(msg.getBytes());
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void assignCmds() {
        serverCommands.put("login", new LoginCommand(sendStream, login, server, board));
        serverCommands.put("addnaught", new AddNaughtCommand(sendStream, login, server, board));
        serverCommands.put("addcross", new AddCrossCommand(sendStream, login, server, board));
    }
}
