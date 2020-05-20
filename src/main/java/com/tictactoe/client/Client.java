package com.tictactoe.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private final String serverName;
    private final int port;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private final String username;
    private Symbol symbol;
    private SymbolAssigner symbolAssigner = new SymbolAssigner();
    private ArrayList<LobbyStatusListener> lobbyStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

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
        if("online".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else
            return false;
    }

    public void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    public void readMessageLoop() {
        try {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                    String cmd = tokens[0];
                    if("cross".equalsIgnoreCase(cmd) || "naught".equalsIgnoreCase(cmd)) {
                        this.symbol = symbolAssigner.assign(cmd);
                        System.out.println(this.symbol.toString());
;                    } else if("addcross".equalsIgnoreCase(cmd) || "addnaught".equalsIgnoreCase(cmd)) {
                        String[] tokenUpdate = line.split(" ", 3);
                        handleUpdate(tokenUpdate);
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUpdate(String[] tokenUpdate) {
        for(MessageListener listener : messageListeners) {
            listener.onMessage(null, tokenUpdate);
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
        outputStream.write(cmd.getBytes());
    }

    public void addLobbyStatusListener(LobbyStatusListener listener) {
        lobbyStatusListeners.add(listener);
    }
    public void removeLobbyStatusListener(LobbyStatusListener listener) {
        lobbyStatusListeners.remove(listener);
    }
    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }
    public void removeMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }
    public String getUsername() {
        return username;
    }
}
