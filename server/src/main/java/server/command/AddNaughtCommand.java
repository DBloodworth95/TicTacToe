package server.command;


import com.tictactoe.symbol.Symbol;
import server.Board;
import server.ClientHandler;
import server.Server;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddNaughtCommand implements Command {
    private final OutputStream outputStream;

    private final AtomicReference<String> login;

    private final Server server;

    private final Board board;

    public AddNaughtCommand(OutputStream outputStream, AtomicReference<String> login, Server server, Board board) {
        this.outputStream = outputStream;
        this.login = login;
        this.server = server;
        this.board = board;
    }

    @Override
    public void execute(String[] tokens) {
        List<ClientHandler> handlerList = server.getHandlers();
        if (tokens.length == 3 && board.getIsTurn() % 2 != 0) {
            String tileX = tokens[1];
            String tileY = tokens[2];
            String msg = "addnaught" + " " + tileX + " " + tileY + "\n";
            String invalidTileMsg = "Invalid tile" + "\n";
            String winMsg = "win" + " " + Symbol.O.toString() + "\n";
            if (board.isValidTile(Integer.parseInt(tileX), Integer.parseInt(tileY))) {
                board.addSymbol(Symbol.O, Integer.parseInt(tileX), Integer.parseInt(tileY));
                System.out.println("naught recieved");
                for (ClientHandler handler : handlerList) {
                    handler.send(msg);
                    if (board.isWin()) {
                        System.out.println("Win");
                        handler.send(winMsg);
                    }
                }
            } else {
                for (ClientHandler handler : handlerList) {
                    handler.send(invalidTileMsg);
                }
            }
        }
    }


    @Override
    public void execute() {

    }
}
