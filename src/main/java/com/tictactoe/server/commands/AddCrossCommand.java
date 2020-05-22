package com.tictactoe.server.commands;

import com.tictactoe.client.Symbol;
import com.tictactoe.game.Board;
import com.tictactoe.server.Server;
import com.tictactoe.server.ServerHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddCrossCommand implements Command {
    @Override
    public void execute(OutputStream outputStream, String[] tokens, AtomicReference<String> login, Server server, Board board) throws IOException {
        List<ServerHandler> handlerList = server.getHandlers();
        if (tokens.length == 3 && board.getIsTurn() % 2 == 0) {
            String tileX = tokens[1];
            String tileY = tokens[2];
            String msg = "addcross" + " " + tileX + " " + tileY + "\n";
            String invalidTileMsg = "Invalid tile" + "\n";
            String winMsg = "win" + " " + Symbol.X.toString() + "\n";
            if (board.isValidTile(Integer.parseInt(tileX), Integer.parseInt(tileY))) {
                board.addSymbol(Symbol.X, Integer.parseInt(tileX), Integer.parseInt(tileY));
                System.out.println("cross" + " " + tileX + " " + tileY);
                for (ServerHandler handler : handlerList) {
                    handler.send(msg);
                    if (board.isWin())
                        handler.send(winMsg);
                }
            } else {
                for (ServerHandler handler : handlerList) {
                    handler.send(invalidTileMsg);
                }
            }
        }
    }

    @Override
    public void execute() {

    }
}
