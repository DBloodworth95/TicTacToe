package src.com.server.commands;

import src.com.server.Server;
import src.com.server.ServerHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddNaughtCommand implements Command {
    @Override
    public void execute(OutputStream outputStream, String[] tokens, AtomicReference<String> login, Server server) throws IOException {
        if(tokens.length == 3) {
            String tileX = tokens[1];
            String tileY = tokens[2];
            String msg = "naught" + " " + tileX + " " + tileY + "\n";
            outputStream.write(msg.getBytes());
            System.out.println("naught" + " " + tileX + " " + tileY);
            List<ServerHandler> handlerList = server.getHandlers();
            for(ServerHandler handler : handlerList) {
                if(!login.toString().equalsIgnoreCase(handler.getLogin()))
                    handler.send(msg);
            }
        }
    }


    @Override
    public void execute() {

    }
}
