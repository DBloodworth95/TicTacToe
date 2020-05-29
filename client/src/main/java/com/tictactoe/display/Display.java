package com.tictactoe.display;

import com.tictactoe.client.Client;
import com.tictactoe.symbol.Symbol;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class Display extends JFrame implements ActionListener {
    final JOptionPane finalStatePane = new JOptionPane(" ");
    final JDialog finalDialogue = finalStatePane.createDialog((JFrame) null, " ");
    final String username;
    static final int BOARD_LENGTH = 3;
    static final int BOARD_WIDTH = 3;
    static final int PORT = 8818;
    final Client client = new Client("localhost", PORT, "guest", null);
    JButton[][] tiles = new JButton[BOARD_LENGTH][BOARD_WIDTH];
    JPanel statusPanel = new JPanel();
    JPanel gamePanel = new JPanel();
    JLabel turnLabel = new JLabel("Current turn: ");
    JLabel statusLabel = new JLabel("Connected!");

    public Display(String username) {
        this.username = username;
        client.addMessageListener((login, msg) -> {
            Symbol symbol;
            int x = 0;
            int y = 0;
            String turn = "null";
            if (msg.length == 4) {
                x = Integer.parseInt(msg[1]);
                y = Integer.parseInt(msg[2]);
                turn = msg[3];
            }
            if (msg[0].equalsIgnoreCase("addnaught")) {
                symbol = Symbol.O;
                tiles[x][y].setForeground(Color.GREEN);
                tiles[x][y].setText(String.valueOf(symbol));
                turnLabel.setText("Current turn: " + turn);
            } else if (msg[0].equalsIgnoreCase("addcross")) {
                symbol = Symbol.X;
                tiles[x][y].setForeground(Color.RED);
                tiles[x][y].setText(String.valueOf(symbol));
                turnLabel.setText("Current turn: " + turn);
            } else if (msg[0].equalsIgnoreCase("win")) {
                String winner = msg[1];
                if (client.getSymbol().toString().equalsIgnoreCase(winner)) {
                    finalDialogue.setTitle("You win!");
                } else {
                    finalDialogue.setTitle("You lose!");
                }
                finalDialogue.setLocationRelativeTo(getRootPane());
                finalDialogue.setVisible(true);
            } else if (msg[0].equalsIgnoreCase("isalive")) {
                reconnect();
            } else if (msg[0].equalsIgnoreCase("disconnect")) {
                disconnected();
            }
        });
    }

    public void initialize() throws IOException {
        if (client.connect()) {
            client.login(username);
        }
        construct();
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.NORTH);
        setTitle("Dan's Tic-Tac-Toe!");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(800, 800, 600, 600);
    }

    private void construct() {
        gamePanel.setLayout(new GridLayout(BOARD_LENGTH, BOARD_WIDTH));
        statusPanel.add(turnLabel);
        statusPanel.add(statusLabel);
        for (int i = 0; i < BOARD_LENGTH; i++)
            for (int j = 0; j < BOARD_WIDTH; j++) {
                tiles[i][j] = new JButton();
                tiles[i][j].putClientProperty("x", i);
                tiles[i][j].putClientProperty("y", j);
                tiles[i][j].addActionListener(this);
                tiles[i][j].setFont(new Font("Arial", Font.PLAIN, 100));
                tiles[i][j].setOpaque(false);
                tiles[i][j].setContentAreaFilled(false);
                gamePanel.add(tiles[i][j]);
            }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        Integer x = (Integer) clickedButton.getClientProperty("x");
        Integer y = (Integer) clickedButton.getClientProperty("y");
        client.requestSymbol(x, y);
    }

    private void disconnected() {
        for (int i = 0; i < BOARD_LENGTH; i++)
            for (int j = 0; j < BOARD_WIDTH; j++)
                tiles[i][j].setEnabled(false);
        statusLabel.setText("Connection Lost - Attempting to re-connect.");
    }

    private void reconnect() {
        for (int i = 0; i < BOARD_LENGTH; i++)
            for (int j = 0; j < BOARD_WIDTH; j++)
                tiles[i][j].setEnabled(true);
        statusLabel.setText("Connected!");
    }
}
