package com.tictactoe.game;

import com.tictactoe.client.Symbol;

public class Board {
    private Symbol[][] tiles;

    public Board() {
        tiles = new Symbol[3][3];
    }

    public boolean addSymbol(Symbol symbol, int x, int y) {
        if(isValidTile(x, y)) {
            tiles[x][y] = symbol;
            return true;
        }
        return false;
    }

    public boolean isValidTile(int x, int y) {
        return tiles[x][y] == null;
    }

    public boolean isWin() {
        return(isRowWin() || isColWin() || isDiagWin());
    }

    private boolean isDiagWin() {
        return((checkSymbols(tiles[0][0], tiles[1][1], tiles[2][2])) ||
                (checkSymbols(tiles[0][2], tiles[1][1], tiles[2][0])));
    }

    private boolean isColWin() {
        for(int i = 0; i < 3; i++) {
            if(checkSymbols(tiles[0][i], tiles[1][i], tiles[2][i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isRowWin() {
        for(int i = 0; i < 3; i++) {
            if(checkSymbols(tiles[i][0], tiles[i][1], tiles[i][2])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSymbols(Symbol s1, Symbol s2, Symbol s3) {
        return ((s1 != null) && (s1 == s2) && (s2 == s3));
    }

    public Symbol[][] getTiles() {
        return tiles;
    }
}
