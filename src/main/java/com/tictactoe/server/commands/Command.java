package com.tictactoe.server.commands;

import com.tictactoe.server.Server;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;


public interface Command {
    void execute(OutputStream outputStream, String[] tokens, AtomicReference<String> login, Server server) throws IOException;
    void execute();
}