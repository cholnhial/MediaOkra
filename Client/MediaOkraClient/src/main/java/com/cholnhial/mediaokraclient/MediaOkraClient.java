package com.cholnhial.mediaokraclient;

import com.google.api.client.util.IOUtils;
import com.google.auth.oauth2.GoogleCredentials;
import commands.MediaKeys;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class MediaOkraClient {

    private final static String MEDIAKEYS = "MediaKeys";
    private static final String LIB_BIN = "/lib-bin/";

    public static  GoogleCredentials credentials;


    public void initializeGoogleCloud() throws Exception {
        credentials = GoogleCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("mediaokra-13c275654735.json"));
    }

    public static boolean isRunningOnWindows() {
        String os =  SystemUtils.OS_NAME;
        return os.contains("Windows");
    }



    /*************************
     * BEGIN:
     * Code from stack Overflow on how to package DLL into JAR:
     *https://stackoverflow.com/questions/1611357/how-to-make-a-jar-file-that-includes-dll-files
     *
     */
    public static void loadFromJar() {
        // Load LIB into root tmp dir
        loadLib("", MEDIAKEYS);
    }

    /**
     * Puts library to temp dir and loads to memory
     */
    public static void loadLib(String path, String name) {
        name = name + ".dll";
        try {
            // have to use a stream
            InputStream in = MediaOkraClient.class.getResourceAsStream(LIB_BIN + name);
            // always write to different location
            File fileOut = new File(System.getProperty("java.io.tmpdir") + "/" + path + LIB_BIN + name);
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            System.load(fileOut.toString());
        } catch (Exception e) {
            System.out.println(String.format("Unable to load DDL (%s):  %s", name,  e.getMessage()));
        }
    }
    // END STACKOVERFLOW CODE


    public static void copyMediaOkraPythonScriptForMacToTmpDir() {
        try {
            InputStream in = MediaOkraClient.class.getResourceAsStream("/tools/media.py");
            File fileOut = new File(System.getProperty("java.io.tmpdir") + "/media.py");
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            System.load(fileOut.toString());
        } catch (Exception e) {
            System.out.println(String.format("Unable to write media.py to tmp dir: %s",  e.getMessage()));
        }
    }
    public static void main(String[] args) {
        if(isRunningOnWindows()) {
            MediaKeys mediaKeysForWindows = new MediaKeys(); // load DDL Library
        }

        MediaOkraClient mediaOkraClient = new MediaOkraClient();

        try {
            mediaOkraClient.initializeGoogleCloud();
        } catch (Exception e) {
            System.out.println("There was an issue initializing Google Cloud Credentials");
            System.exit(1);
        }

        MainUI mainUI = new MainUI(credentials);
        mainUI.setPreferredSize(new Dimension(470, 420));
        mainUI.setResizable(false);
        mainUI.pack();
        mainUI.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainUI.setVisible(true);
    }
}
