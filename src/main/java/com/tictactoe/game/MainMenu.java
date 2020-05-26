package com.tictactoe.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame implements ActionListener {

    private final JPanel mainPanel = new JPanel();

    private final JLabel title = new JLabel("Welcome to Dan's TicTacToe Game!");

    private static final int WIDTH = 3;

    private static final int HEIGHT = 3;

    public void construct() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new GridLayout(WIDTH, HEIGHT));
        mainPanel.add(title);
        setTitle("Dan's Tic-Tac-Toe!");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(800, 800, 600, 600);
    }





    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
