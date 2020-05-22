package com.tictactoe.server;

import com.tictactoe.game.Board;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final int port;
    private ArrayList<ClientHandler> handlerList = new ArrayList<>();
    private Board board = new Board();

    public Server(int port) {
        this.port = port;
    }

    public List<ClientHandler> getHandlers() {
        return handlerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("Accepting client connections..");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepting connection from " + clientSocket);
                ClientHandler handler = new ClientHandler(this, clientSocket, board);
                handlerList.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHandler(ClientHandler handler) {
        handlerList.remove(handler);
    }
}
