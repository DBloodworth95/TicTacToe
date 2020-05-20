package com.tictactoe.server.commands;

import com.tictactoe.client.Symbol;
import com.tictactoe.game.Board;
import com.tictactoe.server.Server;
import com.tictactoe.server.ServerHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddNaughtCommand implements Command {
    @Override
    public void execute(OutputStream outputStream, String[] tokens, AtomicReference<String> login, Server server, Board board) throws IOException {
        List<ServerHandler> handlerList = server.getHandlers();
        if(tokens.length == 3) {
            String tileX = tokens[1];
            String tileY = tokens[2];
            String msg = "addnaught" + " " + tileX + " " + tileY + "\n";
            String invalidTileMsg = "Invalid tile" + "\n";
            if(board.isValidTile(Integer.parseInt(tileX), Integer.parseInt(tileY))) {
                board.addSymbol(Symbol.O, Integer.parseInt(tileX), Integer.parseInt(tileY));
                //outputStream.write(msg.getBytes());
                System.out.println("naught" + " " + tileX + " " + tileY);
                for(ServerHandler handler : handlerList) {
                    //if(!login.toString().equalsIgnoreCase(handler.getLogin()))
                        handler.send(msg);
                }
            } else {
                for(ServerHandler handler : handlerList) {
                    handler.send(invalidTileMsg);
                }
            }
        }
    }


    @Override
    public void execute() {

    }
}
