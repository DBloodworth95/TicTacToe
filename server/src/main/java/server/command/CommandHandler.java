package server.command;

import com.tictactoe.symbol.Symbol;
import server.Board;
import server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CommandHandler {

    private final Map<String, Command> serverCommands = new HashMap<>();

    private final OutputStream sendStream;

    private final Server server;

    private final Board board;

    private AtomicReference<String> login;

    private AtomicReference<Symbol> symbol;

    private final BlockingQueue<String> msgPipe;

    public CommandHandler(OutputStream sendStream, Server server, Board board, AtomicReference<String> login, AtomicReference<Symbol> symbol, BlockingQueue<String> msgPipe) {
        this.sendStream = sendStream;
        this.server = server;
        this.board = board;
        this.login = login;
        this.symbol = symbol;
        this.msgPipe = msgPipe;
        install();
    }

    private void install() {
        serverCommands.put("login", new LoginCommand(sendStream, login, server, board, symbol));
        serverCommands.put("addnaught", new AddNaughtCommand(sendStream, login, server, board));
        serverCommands.put("addcross", new AddCrossCommand(sendStream, login, server, board));
        serverCommands.put("isalive", new Heartbeat(sendStream, login, server));
    }

    public void handle(String[] tokens) {
        if (tokens.length > 0) {
            String cmd = tokens[0];
            if (serverCommands.containsKey(cmd)) {
                Command c = serverCommands.get(cmd);
                try {
                    c.execute(tokens);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                String invCmd = "Invalid command: " + tokens[0] + "\n";
                msgPipe.offer(invCmd);
            }
        }
    }
}
