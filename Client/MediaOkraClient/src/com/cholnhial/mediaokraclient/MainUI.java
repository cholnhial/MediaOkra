/*
 * Created by JFormDesigner on Tue Jan 28 07:35:56 AEST 2020
 */

package com.cholnhial.mediaokraclient;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import net.miginfocom.swing.*;

/**
 * @author unknown
 */
public class MainUI extends JFrame {

    private boolean isListening;
    private Subscriber subscriber = null;

    public MainUI() {
        this.isListening = false;
        initComponents();
    }

    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

    class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            System.out.println(
                    "Message Id: " + message.getMessageId() + " Data: " + message.getData().toStringUtf8());
            // Ack only after all work for the message is complete.
            MainUI.this.printLog(message.getData().toStringUtf8());
            consumer.ack();
        }
    }


    public void printLog(String msg) {
        this.logTextPane.setText(this.logTextPane.getText() + "\n" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + ": " + msg);
    }

    private void startStopBtnActionPerformed(ActionEvent e) {

        if(isListening == false) {
            String code = this.mediaOkraCodeTxtFld.getText();
            if(code.equals("")) {
                JOptionPane.showConfirmDialog(rootPane,"Please enter your Media Okra Code",
                        "Can't start listening", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

            } else {
                this.startStopBtn.setText("Stop listening");
                isListening = true;
                this.printLog("Listening using code: " + code);

                String subscriptionId = "MEDIAOKRA-SUB-J8WYXKC";
                ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(
                        PROJECT_ID, subscriptionId);
                try {
                    new Thread(() -> {

                        // create a subscriber bound to the asynchronous message receiver
                        subscriber =
                                Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
                        subscriber.startAsync().awaitRunning();
                        // Allow the subscriber to run indefinitely unless an unrecoverable error occurs.
                        subscriber.awaitTerminated();
                    }).start();

                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            this.startStopBtn.setText("Start listening");
            this.printLog("Stopped listening");
            isListening = false;
        }

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
        startStopBtn.addActionListener(e -> startStopBtnActionPerformed(e));
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
