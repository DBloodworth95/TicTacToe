package src.com.server.commands;

import src.com.server.Server;

import java.io.IOException;
import java.io.OutputStream;

public class CommandExecuter {
    private Command command;

    public CommandExecuter(Command command) {
        this.command = command;
    }

    public void executeCommand(OutputStream outputStream, String[] tokens, String login, Server server) throws IOException {
        command.operation(outputStream, tokens, login, server);
    }

    public void executeCommand() {
        command.operation();
    }
}
