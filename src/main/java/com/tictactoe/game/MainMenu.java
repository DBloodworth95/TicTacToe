package com.tictactoe.game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenu extends JFrame implements ActionListener {

    private final JLabel title = new JLabel("Welcome to Dan's TicTacToe Game!");
    private final JLabel usernameL = new JLabel("Username:");

    private final JTextField usernameTF = new JTextField();

    private final JButton playB = new JButton("Play!");

    private Display display;

    public void construct() {
        title.setBounds(170,50,250,10);
        usernameL.setBounds(170, 252, 100, 10);
        usernameTF.setBounds(300, 250, 150, 20);
        playB.setBounds(245, 400, 100,20);
        add(title);
        add(usernameL);
        add(usernameTF);
        add(playB);
        playB.addActionListener(this);
        setSize(600,600);
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setTitle("Dan's Tic-Tac-Toe!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == playB) {
            display = new Display(usernameTF.getText());
            setVisible(false);
            try {
                display.initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
