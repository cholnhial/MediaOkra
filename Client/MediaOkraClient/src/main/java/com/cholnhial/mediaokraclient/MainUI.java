/*
 * Created by JFormDesigner on Tue Jan 28 07:35:56 AEST 2020
 */

package com.cholnhial.mediaokraclient;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

import com.google.api.core.ApiService;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import commands.MediaKeys;
import net.miginfocom.swing.*;

/**
 * @author unknown
 */
public class MainUI extends JFrame {

    private boolean isListening;
    private Subscriber subscriber;
    private Thread subscriptionThread;
    private ApiService subscriptionAsync;
    private GoogleCredentials credentials;

    public MainUI(GoogleCredentials credentials) {
        this.subscriptionThread = null;
        this.subscriber = null;
        this.isListening = false;
        this.subscriptionAsync = null;
        this.credentials = credentials;
        initComponents();
    }

    private static final String PROJECT_ID = "mediaokra";


    /***
     * This is where we receive the Google Pub/Sub messages
     *
     * We are subscribed based on the USER code given by Media Okra
     */
    class MediaCommandsMessageReceiver implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            String mediaCommand = message.getData().toStringUtf8();
            MainUI.this.printLog("Media command received: " + mediaCommand);
            switch(mediaCommand) {
                case "play":
                    if (MediaOkraClient.isRunningOnWindows()) {
                        MediaKeys.songPlayPause();
                    } else {
                        executeMediaKeyScript("play");
                    }

                    break;
                case "pause":
                    if (MediaOkraClient.isRunningOnWindows()) {
                        MediaKeys.songPlayPause();
                    } else {
                        executeMediaKeyScript("pause");
                    }
                    break;
                case "mute":
                    if (MediaOkraClient.isRunningOnWindows()) {
                        MediaKeys.volumeMute();
                    } else {
                        executeMediaKeyScript("mute");
                    }
                    break;
                    default:
                        MainUI.this.printLog("Media command received: " + mediaCommand + ", is unknown");
            }
            consumer.ack();
        }
    }

    private void executeMediaKeyScript(String command)  {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "/tmp/media.py " + command);
        try {
            printLog("Executing command: " + command);
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printLog(String msg) {
        this.logTextPane.setText(this.logTextPane.getText() + "\n" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + ": " + msg);
    }

    private void startStopBtnActionPerformed(ActionEvent e) {

        if(!isListening) {
            String code = this.mediaOkraCodeTxtFld.getText();
            if(code.equals("")) {
                JOptionPane.showConfirmDialog(rootPane,"Please enter your Media Okra Code",
                        "Can't start listening", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

            } else {
                this.startStopBtn.setText("Stop listening");
                isListening = true;
                this.printLog("Listening using code: " + code);

                String subscriptionId = "MEDIAOKRA-SUB-" + code;
                ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(
                        PROJECT_ID, subscriptionId);

                // We don't want to block the UI
                subscriptionThread = new  Thread(() -> {
                    subscriber = Subscriber.newBuilder(subscriptionName, new MediaCommandsMessageReceiver())
                            .setCredentialsProvider(FixedCredentialsProvider.create(this.credentials))
                            .build();
                     subscriptionAsync = subscriber.startAsync();
                     subscriptionAsync.awaitRunning();
                     subscriber.awaitTerminated();
                     printLog("Async subscription thread stopped");
                });
                subscriptionThread.start();
            }
        } else {
            this.startStopBtn.setText("Start listening");
            this.printLog("Stopped listening");
            isListening = false;
            if(subscriptionAsync != null) {
                subscriptionAsync.stopAsync(); // this should kill subscriptionThread
            }
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        panel1 = new JPanel();
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
            "[fill]" +
            "[fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //======== panel1 ========
        {
            panel1.setMinimumSize(new Dimension(374, 110));
            panel1. addPropertyChangeListener (new java. beans.
            PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r" .
            equals (e .getPropertyName () )) throw new RuntimeException( ); }} );

            //---- label1 ----
            label1.setText("MediaOkra Code");
            label1.setFont(label1.getFont().deriveFont(label1.getFont().getStyle() | Font.BOLD, label1.getFont().getSize() + 9f));

            //---- mediaOkraCodeTxtFld ----
            mediaOkraCodeTxtFld.setHorizontalAlignment(SwingConstants.CENTER);

            //---- startStopBtn ----
            startStopBtn.setText("Start Listening");
            startStopBtn.addActionListener(e -> startStopBtnActionPerformed(e));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap(49, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(startStopBtn, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
                            .addComponent(mediaOkraCodeTxtFld, GroupLayout.PREFERRED_SIZE, 333, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(54, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mediaOkraCodeTxtFld, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startStopBtn, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(9, Short.MAX_VALUE))
            );
        }
        contentPane.add(panel1, "cell 0 0 12 7");

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
        contentPane.add(scrollPane1, "cell 0 7 12 1,hmin 200");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel panel1;
    private JLabel label1;
    private JTextField mediaOkraCodeTxtFld;
    private JButton startStopBtn;
    private JScrollPane scrollPane1;
    private JTextPane logTextPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
