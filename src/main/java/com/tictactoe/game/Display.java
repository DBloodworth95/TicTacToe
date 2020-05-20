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
    JPanel gamePanel = new JPanel();

    public Display() {
        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String login, String[] msg) {
                System.out.println("message");
                Symbol symbol;
                if(msg.length == 3) {
                    if(msg[0].equalsIgnoreCase("addnaught"))
                        symbol = Symbol.O;
                    else
                        symbol = Symbol.X;
                    String x = msg[1];
                    String y = msg[2];
                    tiles[Integer.parseInt(x)][Integer.parseInt(y)].setText(String.valueOf(symbol));
                    tiles[Integer.parseInt(x)][Integer.parseInt(y)].setEnabled(false);
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
        setTitle("Dan's Tic-Tac-Toe!");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(400, 400, 300, 300);
    }

    private void construct() {
        gamePanel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < tiles.length; i++)
            for (int j = 0; j < tiles.length; j++) {
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
        try {
            client.requestSymbol(x, y);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
