package server;

import com.tictactoe.symbol.Symbol;
import server.command.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private Board board;
    public AtomicReference<String> login = new AtomicReference<>();
    private OutputStream sendStream;
    private final BlockingQueue<String> msgPipe = new ArrayBlockingQueue<>(100);
    private AtomicReference<Symbol> symbol = new AtomicReference<>();

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
            CommandHandler commandHandler = new CommandHandler(sendStream, server, board, login, symbol, msgPipe);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                commandHandler.handle(tokens);
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
}
