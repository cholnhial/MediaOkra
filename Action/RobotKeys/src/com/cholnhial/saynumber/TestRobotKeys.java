package com.cholnhial.saynumber;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.VK_PAUSE;

public class TestRobotKeys {

    private Robot robot;

    public static void main(String[] args) {
        new TestRobotKeys();
    }

    public TestRobotKeys() {
        try {
            robot = new Robot();
            robot.setAutoDelay(250);
            robot.keyPress(VK_PAUSE);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }
}
