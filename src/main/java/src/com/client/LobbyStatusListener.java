package src.com.client;

public interface LobbyStatusListener {
    void online(String login);
    void offline(String login);
}
