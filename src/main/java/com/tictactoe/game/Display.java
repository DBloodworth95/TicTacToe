package com.tictactoe.game;

import javax.swing.*;
import java.awt.*;

public class Display extends JFrame {
    Board board = new Board();
    JButton[][] tiles = new JButton[3][3];
    JPanel gamePanel = new JPanel();

    public void initialize() {
        construct();
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        setTitle("Dan's Tic-Tac-Toe!");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(400,400,300,300);
    }

    private void construct() {
        gamePanel.setLayout(new GridLayout(3,3));
        for(int i = 0; i < board.getTiles().length; i++)
           for(int j = 0; j < board.getTiles().length; j++) {
               tiles[i][j] = new JButton();
               tiles[i][j].setText("-");
               gamePanel.add(tiles[i][j]);
           }
    }

}
