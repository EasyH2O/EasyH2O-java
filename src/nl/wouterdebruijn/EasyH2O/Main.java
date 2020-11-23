package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            mySQLConnector.connect("localhost", "root", "easy_h2o", "");

            // Test code for Connection, Returns false when connection is working.
            System.out.println("Are we closed? : " + mySQLConnector.con.isClosed());

            Boolean a = false;
            a = printdatabase();
            Boolean b = false;
            b = sendMicroBitData(microbitData);
            while (!a || !b){}
            mySQLConnector.disconnect();

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

    public static boolean printdatabase () {
        try {
            Statement statement = mySQLConnector.con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user;");

            while (resultSet.next()) {
                String naam = resultSet.getString("naam");
                String email = resultSet.getString("email");
                String klantnummer = resultSet.getString("id");

                System.out.println("Klantnummer: " + klantnummer);
                System.out.println("Naam: " + naam);
                System.out.println("E-mail: " + email);
                System.out.println();
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return true;
    }

    public static boolean sendMicroBitData(String microbitData) {
        try {

            PreparedStatement preparedStatement = mySQLConnector.con.prepareStatement("INSERT INTO `datapoint`(`regenton`, `data`) VALUES (1, ?)");
            preparedStatement.setString(1, microbitData);
            preparedStatement.executeUpdate();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return true;
    }
}
