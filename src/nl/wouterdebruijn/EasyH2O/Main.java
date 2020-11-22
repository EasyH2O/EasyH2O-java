package nl.wouterdebruijn.EasyH2O;

import java.sql.SQLException;

public class Main {
    /**
     * Main function, creates the Dashboard instance and draws the JFrame.
     */
    public static void main(String[] args) {
        System.out.println("Program started!");

        Dashboard dashboard = new Dashboard();
        dashboard.createAndShow();

        /*
         * Init MySQL Class.
         */
        MySQLConnector mySQLConnector = new MySQLConnector();

        /*
         * Call the connect function
         */
        try {
            mySQLConnector.connect();

            System.out.println("Are we closed? : " + mySQLConnector.con.isClosed());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
