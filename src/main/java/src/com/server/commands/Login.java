package src.com.server.commands;

import src.com.server.Server;
import src.com.server.ServerHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Login implements Command {
    
    @Override
    public void operation(OutputStream outputStream, String[] tokens, String login, Server server) throws IOException {
        if(tokens.length == 2) {
            login = tokens[1];
            String msg = "online\n";
            outputStream.write(msg.getBytes());
            System.out.println("User " + login + " has logged in!");
            String onlineMsg = "Online " + login + " has logged in!" + "\n";
            List<ServerHandler> handlerList = server.getHandlers();

            for(ServerHandler handler : handlerList) {
                if(handler.getLogin() != null) {
                    if(!login.equalsIgnoreCase(handler.getLogin())) {
                        String currentOnline = "Currently online: " + handler.getLogin() + "\n";
                        handler.send(onlineMsg);
                    }
                }
            }

        }
    }

    @Override
    public void operation() {

    }
}
