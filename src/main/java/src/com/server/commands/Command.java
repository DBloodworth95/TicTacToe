package src.com.server.commands;

import src.com.server.Server;
import java.io.IOException;
import java.io.OutputStream;


public interface Command {
    public void operation(OutputStream outputStream, String[] tokens, String login, Server server) throws IOException;
    public void operation();
}