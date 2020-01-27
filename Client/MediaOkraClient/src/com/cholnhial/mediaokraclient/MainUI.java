/*
 * Created by JFormDesigner on Tue Jan 28 07:35:56 AEST 2020
 */

package com.cholnhial.mediaokraclient;

import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author unknown
 */
public class MainUI extends JFrame {
    public MainUI() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        label1 = new JLabel();
        mediaOkraCodeTxtFld = new JTextField();
        startStopBtn = new JButton();
        scrollPane1 = new JScrollPane();
        logTextPane = new JTextPane();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //---- label1 ----
        label1.setText("MediaOkra Code");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getStyle() | Font.BOLD, label1.getFont().getSize() + 9f));
        contentPane.add(label1, "cell 5 1");
        contentPane.add(mediaOkraCodeTxtFld, "cell 2 3 7 1");

        //---- startStopBtn ----
        startStopBtn.setText("Start Listening");
        contentPane.add(startStopBtn, "cell 5 5");

        //======== scrollPane1 ========
        {
            scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane1.setBackground(new Color(214, 217, 223));

            //---- logTextPane ----
            logTextPane.setForeground(Color.green);
            logTextPane.setBackground(Color.black);
            scrollPane1.setViewportView(logTextPane);
        }
        contentPane.add(scrollPane1, "cell 0 6 11 1,hmin 200");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JLabel label1;
    private JTextField mediaOkraCodeTxtFld;
    private JButton startStopBtn;
    private JScrollPane scrollPane1;
    private JTextPane logTextPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
