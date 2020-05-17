package src.com.client;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;

public class Client {
    private final String serverName;
    private final int port;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private final String username;
    private final Symbol symbol;
    private ArrayList<LobbyStatusListener> lobbyStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public Client(String serverName, int port, String username, Symbol symbol) {
        this.serverName = serverName;
        this.port = port;
        this.username = username;
        this.symbol = symbol;
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 8818, "test", null);
        client.addLobbyStatusListener(new LobbyStatusListener() {
            @Override
            public void online(String login) {

            }

            @Override
            public void offline(String login) {

            }
        });
        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String login, String msg) {

            }
        });


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
