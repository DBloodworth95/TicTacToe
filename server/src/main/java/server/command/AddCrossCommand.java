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
            int tileX = Integer.parseInt(tokens[1]);
            int tileY = Integer.parseInt(tokens[2]);

            String name = board.getPlayersTurn(board.getIsTurn());
            if (board.isValidTile(tileX, tileY)) {
                board.addSymbol(Symbol.X, tileX, tileY);
                System.out.println("cross" + " " + tileX + " " + tileY);
                for (ClientHandler handler : handlerList) {
                    handler.send("addcross" + " " + tileX + " " + tileY + " " + name + "\n");
                    if (board.isWin())
                        handler.send("win" + " " + Symbol.X.toString() + "\n");
                }
            } else {
                for (ClientHandler handler : handlerList) {
                    handler.send("Invalid tile" + "\n");
                }
            }
        }
    }

    @Override
    public void execute() {

    }
}
