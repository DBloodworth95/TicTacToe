import com.tictactoe.client.Symbol;
import com.tictactoe.game.Board;
import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

    @Test
    public void testAddSymbol() {
        Board board = new Board();
        board.addSymbol(Symbol.X, 1, 1);
        Assert.assertEquals(Symbol.X, board.getSymbolAt(1, 1));
    }

    @Test
    public void testAddSymbolFail() {
        Board board = new Board();
        board.addSymbol(Symbol.X, 1, 1);
        board.addSymbol(Symbol.O, 1, 1);
        Assert.assertEquals(Symbol.X, board.getSymbolAt(1, 1));
    }

    @Test
    public void testWin() {
        Board board = new Board();
        board.addSymbol(Symbol.X, 1, 0);
        board.addSymbol(Symbol.X, 1, 1);
        board.addSymbol(Symbol.X, 1, 2);
        Assert.assertTrue(board.isWin());
    }

    @Test
    public void testGetTurn() {
        Board board = new Board();
        Assert.assertEquals(1, board.getIsTurn());
    }
}
