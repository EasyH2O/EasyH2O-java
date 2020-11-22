package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static MySQLConnector mySQLConnector;

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

        /*
         * TEST (Returns error when params aren't filled.)
         * Call the connect function
         */
        try {
            mySQLConnector.connect("localhost", "", "", "");

            // Test code for Connection, Returns false when connection is working.
            System.out.println("Are we closed? : " + mySQLConnector.con.isClosed());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public static void setUISystemDefault() {
        // Set JFrame look and feel to Windows instead of Java.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
