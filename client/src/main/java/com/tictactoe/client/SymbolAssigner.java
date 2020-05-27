package com.tictactoe.client;

import com.tictactoe.symbol.Symbol;

public final class SymbolAssigner {

    public static Symbol assign(String cmd) {
        return cmd.equalsIgnoreCase("cross") ? Symbol.X : Symbol.O;
    }

    private SymbolAssigner() {

    }
}
