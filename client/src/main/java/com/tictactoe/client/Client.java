package com.tictactoe.client;

import com.tictactoe.client.command.*;
import com.tictactoe.client.worker.HeartbeatSender;
import com.tictactoe.client.worker.HeartbeatWriter;
import com.tictactoe.client.worker.ServerWriter;
import com.tictactoe.symbol.Symbol;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static javax.swing.SwingUtilities.invokeLater;

public class Client {

    private final String serverName;
    private final int port;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private String username;
    private AtomicReference<Symbol> symbol = new AtomicReference<>();
    private List<LobbyStatusListener> lobbyStatusListeners = new ArrayList<>();
    private List<MessageListener> messageListeners = new ArrayList<>();
    private final BlockingQueue<String> msgPipe = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<String> heartbeatPipe = new ArrayBlockingQueue<>(1);
    private Thread messageLoopThread;
    private Thread heartBeatSenderThread;
    private Thread serverWriterThread;
    private Thread heartbeatWriterThread;
    private HeartbeatWriter heartbeatWriter;
    private ServerWriter serverWriter;
    private HeartbeatSender heartbeatSender;

    public Client(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
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
            startWorkerThreads();
        }
    }

    private void readMessageLoop() {
        CommandHandler commandHandler = new CommandHandler(messageListeners, heartbeatPipe);
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                String cmd = tokens[0];
                invokeLater(() -> {
                    commandHandler.handle(cmd, tokens, symbol);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestSymbol(int x, int y) {
        String symbolCmd;
        if (this.symbol.toString().equalsIgnoreCase("x"))
            symbolCmd = "addcross";
        else
            symbolCmd = "addnaught";
        msgPipe.offer(symbolCmd + " " + x + " " + y + "\n");
    }

    private void startWorkerThreads() {
        heartbeatWriter = new HeartbeatWriter(msgPipe, username);
        serverWriter = new ServerWriter(this, msgPipe, outputStream, messageListeners);
        heartbeatSender = new HeartbeatSender(heartbeatPipe, messageListeners);

        messageLoopThread = new Thread(this::readMessageLoop);
        heartbeatWriterThread = new Thread(heartbeatWriter);
        serverWriterThread = new Thread(serverWriter);
        heartBeatSenderThread = new Thread(heartbeatSender);

        messageLoopThread.start();
        serverWriterThread.start();
        heartbeatWriterThread.start();
        heartBeatSenderThread.start();
    }

    public void stopWorkerThreads() {
        try {
            heartbeatSender.stop();
            heartbeatWriter.stop();
            serverWriter.stop();
            heartBeatSenderThread.join();
            heartbeatWriterThread.join();
            serverWriterThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public AtomicReference<Symbol> getSymbol() {
        return symbol;
    }
}
