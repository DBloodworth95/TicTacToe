package src.com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private final int port;
    private ArrayList<ServerHandler> handlerList = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public List<ServerHandler> getHandlers() {
        return handlerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                ServerHandler handler = new ServerHandler(this, clientSocket);
                handlerList.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHandler(ServerHandler handler) {
        handlerList.remove(handler);
    }
}
