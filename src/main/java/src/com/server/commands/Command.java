package src.com.server.commands;

import src.com.server.Server;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;


public interface Command {
    public void execute(OutputStream outputStream, String[] tokens, AtomicReference<String> login, Server server) throws IOException;
    public void execute();
}
