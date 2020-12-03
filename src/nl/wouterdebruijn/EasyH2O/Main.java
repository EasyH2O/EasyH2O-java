package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;

public class Main {

    public static MySQLConnector mySQLConnector;
    public static SerialConnector serialConnector;

    /**
     * Main function, creates the Dashboard instance and draws the JFrame.
     */
    public static void main(String[] args) {
        System.out.println("Program started!");

        setUISystemDefault();

        Dashboard dashboard = new Dashboard();
        dashboard.createAndShow();

        /*
         * Init MySQL Class.
         */
        mySQLConnector = new MySQLConnector();
        serialConnector = new SerialConnector();

        serialConnector.OpenPort();
    }

    /**
     * Set dashboard theme to system default (Looks nicer!)
     *
     * @Author Wouter
     */
    public static void setUISystemDefault() {
        // Set JFrame look and feel to Windows instead of Java.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
