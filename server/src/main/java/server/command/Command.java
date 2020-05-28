package server.command;

import java.io.IOException;

public interface Command {

    void execute(String[] tokens) throws IOException, InterruptedException;

    void execute();
}
