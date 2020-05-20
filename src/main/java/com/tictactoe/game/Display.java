package com.tictactoe.game;

import com.tictactoe.client.Client;
import com.tictactoe.client.Symbol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Display extends JFrame implements ActionListener {
    Client client = new Client("localhost", 8818, "guest", null);
    Board board = new Board();
    JButton[][] tiles = new JButton[3][3];
    JPanel gamePanel = new JPanel();

    public void initialize() throws IOException {
        client.connect();
        client.login("guest");
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
               tiles[i][j].putClientProperty("x", i);
               tiles[i][j].putClientProperty("y", j);
               tiles[i][j].setText("-");
               tiles[i][j].addActionListener(this);
               gamePanel.add(tiles[i][j]);
           }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        Integer x = (Integer) clickedButton.getClientProperty("x");
        Integer y = (Integer) clickedButton.getClientProperty("y");
        if(board.isValidTile(x, y)) {
            board.addSymbol(Symbol.O, x, y);
            clickedButton.setText("O");
        } else
            System.out.println("Invalid Tile!");

    }



}
