package com.tictactoe.client;

public final class SymbolAssigner {

    public static Symbol assign(String cmd) {
        return cmd.equalsIgnoreCase("cross") ? Symbol.X : Symbol.O;
    }

    private SymbolAssigner() {

    }
}
