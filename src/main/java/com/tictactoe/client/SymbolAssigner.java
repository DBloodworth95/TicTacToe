package com.tictactoe.client;

public class SymbolAssigner {

    public Symbol assign(String cmd) {
        if(cmd.equalsIgnoreCase("cross")) {
            return Symbol.CROSS;
        } else
            return Symbol.NAUGHT;
    }
}
