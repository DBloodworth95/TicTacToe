package com.tictactoe.client;

public class SymbolAssigner {

    public Symbol assign(String cmd) {
        if (cmd.equalsIgnoreCase("cross")) {
            return Symbol.X;
        } else
            return Symbol.O;
    }
}
