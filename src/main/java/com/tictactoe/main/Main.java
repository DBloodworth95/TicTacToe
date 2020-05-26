package com.tictactoe.main;

import com.tictactoe.game.Display;
import com.tictactoe.game.MainMenu;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //Display display = new Display();
        //display.initialize();
        MainMenu mainMenu = new MainMenu();
        mainMenu.construct();
    }
}
