package server;

import com.tictactoe.symbol.Symbol;

public class Board {
    private Symbol[][] tiles;
    private int isTurn;
    private Player p1, p2;
    private static final int BOARD_WIDTH = 3;
    private static final int BOARD_LENGTH = 3;

    public Board() {
        tiles = new Symbol[BOARD_LENGTH][BOARD_WIDTH];
        this.isTurn = 1;
    }

    public boolean addSymbol(Symbol symbol, int x, int y) {
        if (isValidTile(x, y)) {
            isTurn++;
            tiles[x][y] = symbol;
            return true;
        }
        return false;
    }

    public boolean isValidTile(int x, int y) {
        return tiles[x][y] == null;
    }

    public boolean isWin() {
        return (isRowWin() || isColWin() || isDiagWin());
    }

    private boolean isDiagWin() {
        return ((checkSymbols(tiles[0][0], tiles[1][1], tiles[2][2])) ||
                (checkSymbols(tiles[0][2], tiles[1][1], tiles[2][0])));
    }

    private boolean isColWin() {
        for (int i = 0; i < 3; i++) {
            if (checkSymbols(tiles[0][i], tiles[1][i], tiles[2][i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isRowWin() {
        for (int i = 0; i < 3; i++) {
            if (checkSymbols(tiles[i][0], tiles[i][1], tiles[i][2])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSymbols(Symbol s1, Symbol s2, Symbol s3) {
        return ((s1 != null) && (s1 == s2) && (s2 == s3));
    }

    public int getIsTurn() {
        return isTurn;
    }

    public Symbol getSymbolAt(int x, int y) {
        return tiles[x][y];
    }

    public void setP1(String name) {
        this.p1 = new Player(name, 1);
    }

    public void setP2(String name) {
        this.p2 = new Player(name, 2);
    }

    public String getPlayersTurn(int turn) {
        if(turn % 2 == 0) {
            return p1.getName();
        } else {
            return p2.getName();
        }
    }
}
