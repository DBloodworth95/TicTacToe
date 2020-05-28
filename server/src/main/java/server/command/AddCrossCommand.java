package server.command;


import com.tictactoe.symbol.Symbol;
import server.Board;
import server.ClientHandler;
import server.Server;

import java.io.OutputStream;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddCrossCommand implements Command {
    private final OutputStream outputStream;

    private final AtomicReference<String> login;

    private final Server server;

    private final Board board;


    public AddCrossCommand(OutputStream outputStream, AtomicReference<String> login, Server server, Board board) {
        this.outputStream = outputStream;
        this.login = login;
        this.server = server;
        this.board = board;
    }

    @Override
    public void execute(String[] tokens) {
        List<ClientHandler> handlerList = server.getHandlers();
        if (tokens.length == 3 && board.getIsTurn() % 2 == 0) {
            String tileX = tokens[1];
            String tileY = tokens[2];
            String name = board.getPlayersTurn(board.getIsTurn());
            String msg = "addcross" + " " + tileX + " " + tileY + " " + name + "\n";
            String invalidTileMsg = "Invalid tile" + "\n";
            String winMsg = "win" + " " + Symbol.X.toString() + "\n";
            if (board.isValidTile(Integer.parseInt(tileX), Integer.parseInt(tileY))) {
                board.addSymbol(Symbol.X, Integer.parseInt(tileX), Integer.parseInt(tileY));
                System.out.println("cross" + " " + tileX + " " + tileY);
                for (ClientHandler handler : handlerList) {
                    handler.send(msg);
                    if (board.isWin())
                        handler.send(winMsg);
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
