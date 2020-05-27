package com.tictactoe.client;

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
                System.out.println("Read line");
                invokeLater(() -> {
                    if ("cross".equalsIgnoreCase(cmd) || "naught".equalsIgnoreCase(cmd)) {
                        this.symbol = SymbolAssigner.assign(cmd);
                        System.out.println(this.symbol.toString());
                    } else if ("addcross".equalsIgnoreCase(cmd) || "addnaught".equalsIgnoreCase(cmd)) {
                        String[] tokenUpdate = finalLine.split(" ", 3);
                        System.out.println("Command read");
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
            System.out.println("Sending to listeners");
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

    public void requestSymbol(int x, int y) throws IOException {
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
                System.out.println("writing message to server");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
