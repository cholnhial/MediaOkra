package com.cholnhial.mediaokraclient;

import java.awt.*;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Main {

    public static void main(String[] args) {
        MainUI mainUI = new MainUI();
        mainUI.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainUI.setVisible(true);
    }
}
