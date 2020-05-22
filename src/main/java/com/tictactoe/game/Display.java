package com.tictactoe.game;

import com.tictactoe.client.Client;
import com.tictactoe.client.MessageListener;
import com.tictactoe.client.Symbol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Display extends JFrame implements ActionListener {
    Client client = new Client("localhost", 8818, "guest", null);
    JButton[][] tiles = new JButton[3][3];
    JPanel statusPanel = new JPanel();
    JPanel gamePanel = new JPanel();
    JLabel statusLabel = new JLabel("Current turn: ");
    final JOptionPane finalStatePane = new JOptionPane(" ");
    final JDialog finalDialogue = finalStatePane.createDialog((JFrame) null, " ");

    public Display() {
        client.addMessageListener((login, msg) -> {
            System.out.println(msg[0]);
            Symbol symbol;
            String x = "null";
            String y = "null";
            if (msg.length == 3) {
                x = msg[1];
                y = msg[2];
            }
            if (msg[0].equalsIgnoreCase("addnaught")) {
                symbol = Symbol.O;
                tiles[Integer.parseInt(x)][Integer.parseInt(y)].setForeground(Color.GREEN);
                tiles[Integer.parseInt(x)][Integer.parseInt(y)].setText(String.valueOf(symbol));
                statusLabel.setText("Current turn: Cross");
            } else if (msg[0].equalsIgnoreCase("addcross")) {
                symbol = Symbol.X;
                tiles[Integer.parseInt(x)][Integer.parseInt(y)].setForeground(Color.RED);
                tiles[Integer.parseInt(x)][Integer.parseInt(y)].setText(String.valueOf(symbol));
                statusLabel.setText("Current turn: Naught");
            } else if (msg[0].equalsIgnoreCase("win")) {
                String winner = msg[1];
                if (client.getSymbol().toString().equalsIgnoreCase(winner)) {
                    finalDialogue.setTitle("You win!");
                    finalDialogue.setLocationRelativeTo(getRootPane());
                    finalDialogue.setVisible(true);
                } else {
                    finalDialogue.setTitle("You lose!");
                    finalDialogue.setLocationRelativeTo(getRootPane());
                    finalDialogue.setVisible(true);
                }
            }
        });
    }

    public void initialize() throws IOException {
        client.connect();
        client.login("guest");
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
        gamePanel.setLayout(new GridLayout(3, 3));
        statusPanel.add(statusLabel);
        for (int i = 0; i < tiles.length; i++)
            for (int j = 0; j < tiles.length; j++) {
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
        try {
            client.requestSymbol(x, y);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
