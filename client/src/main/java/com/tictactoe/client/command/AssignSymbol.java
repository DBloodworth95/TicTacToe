package com.tictactoe.client.command;

import com.tictactoe.client.SymbolAssigner;
import com.tictactoe.symbol.Symbol;

public class AssignSymbol implements Command {
    private Symbol symbol;

    public AssignSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public void execute(String[] tokens) {
        String cmd = tokens[0];
        this.symbol = SymbolAssigner.assign(cmd);
    }
}
