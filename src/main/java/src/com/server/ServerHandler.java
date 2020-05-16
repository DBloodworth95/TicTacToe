package src.com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerHandler extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream sendStream;

    public ServerHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
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
       // InputStream
    }

    public String getLogin() {
        return login;
    }

    public void send(String msg) throws IOException {
        if(login != null)
            sendStream.write(msg.getBytes());
    }
}
