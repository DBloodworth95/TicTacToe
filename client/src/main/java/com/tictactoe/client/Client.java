package com.tictactoe.client;

import com.tictactoe.client.command.AddSymbol;
import com.tictactoe.client.command.Command;
import com.tictactoe.client.command.Heartbeat;
import com.tictactoe.client.command.Win;
import com.tictactoe.symbol.Symbol;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import static javax.swing.SwingUtilities.invokeLater;

public class Client {

    private final String serverName;
    private final int port;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private String username;
    private Symbol symbol;
    private List<LobbyStatusListener> lobbyStatusListeners = new ArrayList<>();
    private List<MessageListener> messageListeners = new ArrayList<>();
    private final BlockingQueue<String> msgPipe = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<String> heartbeatPipe = new ArrayBlockingQueue<>(1);
    private Map<String, Command> clientCommands = new HashMap<>();
    private Thread t = new Thread(this::readMessageLoop);
    private Thread w = new Thread(this::startMsgWriter);
    private Thread h = new Thread(this::startHeartBeat);
    private Thread s = new Thread(this::startDeliveringHeartbeats);
    private boolean running = true;


    public Client(String serverName, int port, String username, Symbol symbol) {
        this.serverName = serverName;
        this.port = port;
        this.username = username;
        this.symbol = symbol;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, port);
            this.outputStream = socket.getOutputStream();
            this.inputStream = socket.getInputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void login(String login) throws IOException {
        String cmd = "login " + login + " " + "\n";
        this.username = login;
        outputStream.write(cmd.getBytes());
        String response = bufferedReader.readLine();
        if ("online".equalsIgnoreCase(response)) {
            startCommunications();
        }
    }

    private void startHeartBeat() {
        try {
            while (running) {
                msgPipe.offer("isalive" + " " + username + "\n");
                System.out.println("Sending heartbeat to server");
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readMessageLoop() {
        assignCmds();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                String cmd = tokens[0];
                invokeLater(() -> {
                    if ("cross".equalsIgnoreCase(cmd) || "naught".equalsIgnoreCase(cmd)) {
                        this.symbol = SymbolAssigner.assign(cmd);
                    }
                    if (clientCommands.containsKey(cmd)) {
                        Command c = clientCommands.get(cmd);
                        c.execute(tokens);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestSymbol(int x, int y) {
        String symbolCmd;
        if (symbol.toString().equalsIgnoreCase("x"))
            symbolCmd = "addcross";
        else
            symbolCmd = "addnaught";
        msgPipe.offer(symbolCmd + " " + x + " " + y + "\n");
    }

    private void startMsgWriter() {
        while (running) {
            try {
                String msg = msgPipe.take();
                outputStream.write(msg.getBytes());
            } catch (IOException | InterruptedException e) {
                try {
                    for (MessageListener messageListener : messageListeners) {
                        messageListener.onMessage(null, "disconnect".split(" "));
                        running = false;
                        t.join();
                        w.join();
                        h.join();
                        s.join();
                    }
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }

    private void assignCmds() {
        clientCommands.put("addcross", new AddSymbol(messageListeners));
        clientCommands.put("addnaught", new AddSymbol(messageListeners));
        clientCommands.put("win", new Win(messageListeners));
        clientCommands.put("isalive", new Heartbeat(heartbeatPipe));
    }

    private void startCommunications() {
        t.start();
        w.start();
        h.start();
        s.start();
    }

    private void startDeliveringHeartbeats() {
        try {
            while (running) {
                String[] tokens = heartbeatPipe.take().split(" ");
                for (MessageListener messageListener : messageListeners) {
                    messageListener.onMessage(null, tokens);
                }
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
