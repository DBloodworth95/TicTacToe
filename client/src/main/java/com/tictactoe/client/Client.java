package com.tictactoe.client;

import com.tictactoe.client.command.AddSymbol;
import com.tictactoe.client.command.AssignSymbol;
import com.tictactoe.client.command.Command;
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
    private final String username;
    private Symbol symbol;
    private List<LobbyStatusListener> lobbyStatusListeners = new ArrayList<>();
    private List<MessageListener> messageListeners = new ArrayList<>();
    private final BlockingQueue<String> msgPipe = new ArrayBlockingQueue<>(100);
    private Map<String, Command> clientCommands = new HashMap<>();


    public Client(String serverName, int port, String username, Symbol symbol) {
        this.serverName = serverName;
        this.port = port;
        this.username = username;
        this.symbol = symbol;
    }

    public boolean login(String login) throws IOException {
        String cmd = "login " + login + " " + "\n";
        outputStream.write(cmd.getBytes());
        String response = bufferedReader.readLine();
        if ("online".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else
            return false;
    }

    public void startMessageReader() {
        Thread t = new Thread(this::readMessageLoop);
        Thread w = new Thread(this::startMsgWriter);
        Thread h = new Thread(this::startHeartBeat);
        t.start();
        w.start();
        h.start();
    }

    public void startHeartBeat() {
        try {
            while (true) {
                msgPipe.offer("isalive" + "\n");
                System.out.println("Sending heartbeat to server");
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                String cmd = tokens[0];
                String finalLine = line;
                invokeLater(() -> {
                    if ("cross".equalsIgnoreCase(cmd) || "naught".equalsIgnoreCase(cmd)) {
                        //this.symbol = SymbolAssigner.assign(cmd);
                    } else if ("addcross".equalsIgnoreCase(cmd) || "addnaught".equalsIgnoreCase(cmd)) {
                        String[] tokenUpdate = finalLine.split(" ", 3);
                        handleUpdate(tokenUpdate);
                    } else if ("win".equalsIgnoreCase(cmd)) {
                        String[] tokenWin = finalLine.split(" ", 2);
                        handleWin(tokenWin);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUpdate(String[] tokenUpdate) {
        for (MessageListener listener : messageListeners) {
            listener.onMessage(null, tokenUpdate);
        }
    }

    private void handleWin(String[] tokenWin) {
        for (MessageListener listener : messageListeners) {
            listener.onMessage(null, tokenWin);
        }
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

    public void requestSymbol(int x, int y) {
        String symbolCmd;
        if (symbol.toString().equalsIgnoreCase("x"))
            symbolCmd = "addcross";
        else
            symbolCmd = "addnaught";
        String cmd = symbolCmd + " " + x + " " + y + "\n";
        System.out.println(cmd);
        msgPipe.offer(cmd);
    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void startMsgWriter() {
        while (true) {
            try {
                String msg = msgPipe.take();
                outputStream.write(msg.getBytes());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void assignCmds() {
        clientCommands.put("cross", new AssignSymbol(this.symbol));
        clientCommands.put("naught", new AssignSymbol(this.symbol));
        clientCommands.put("addcross", new AddSymbol(messageListeners));
        clientCommands.put("addnaught", new AddSymbol(messageListeners));
    }
}
