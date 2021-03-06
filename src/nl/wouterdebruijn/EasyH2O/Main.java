package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static MySQLConnector mySQLConnector;
    public static JFrameManager jFrameManager;
    public static List<Regenton> regentons;
    public static WeatherModule weatherModule;

    /**
     * Main function, creates the Dashboard instance and draws the JFrame.
     */
    public static void main(String[] args) {
        System.out.println("Program started!");

        setUISystemDefault();

        jFrameManager = new JFrameManager("EasyH2O Alpha 0.0.1");
        jFrameManager.setDefaultPanel(JFrameManager.Frames.preLaunch);
        jFrameManager.visible(true);

        /*
         * Init MySQL Class.
         */
        mySQLConnector = new MySQLConnector();

        // Init the Weather Module with API key.
        weatherModule = new WeatherModule("768c924f01b1d82fecd790a58f1417e6");
    }

    /**
     * Create Java list of all regenton objects from DB.
     *
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public static void initRegentonnen() {
        try {
            if (Main.regentons != null && Main.regentons.size() > 0) {
                for (Regenton regenton : regentons) {
                    if (regenton.isOpen()) {
                        regenton.disconnect();
                    }
                }
            }

            ResultSet resultSet = mySQLConnector.query("SELECT * FROM regenton");

            ArrayList<Regenton> regentonRawList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String comPort = resultSet.getString("comPort");
                int ownerId = resultSet.getInt("owner");
                User owner;

                ResultSet userResults = mySQLConnector.query("SELECT * FROM user WHERE id = " + ownerId); //SQL Injection??! (Probably safe because this can only be a int.)
                if (userResults.next()) {
                    // Create user object from database user values.
                    owner = User.fromHash(userResults.getInt("id"), userResults.getString("email"), userResults.getString("passwordHash"), userResults.getString("naam"), userResults.getBoolean("isAdmin"));

                    Regenton regenton = new Regenton(id, comPort, owner);
                    regentonRawList.add(regenton);
                    System.out.println("Created and Saved regenton Instance: " + regenton.id);
                }
            }
            Main.regentons = regentonRawList;
            connectRegentonnen(regentonRawList);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void connectRegentonnen(ArrayList<Regenton> regentonList) {
        regentonList.forEach(regenton -> {
            if (!regenton.isOpen()) {
                regenton.openPort();
            }
        });
    }

    /**
     * Get index in regentonArray from regentonId;
     *
     * @param regentonId id of a regenton.
     * @return index of object.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public static int indexById(int regentonId) {
        for (Regenton regenton : Main.regentons) {
            if (regenton.id == regentonId) {
                return Main.regentons.indexOf(regenton);
            }
        }
        return -1; // If we didn't find any by that id.;
    }

    /**
     * Set dashboard theme to system default (Looks nicer!)
     *
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public static void setUISystemDefault() {
        // Set JFrame look and feel to Windows instead of Java.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void repackUI() {
        if (Main.jFrameManager != null) {
            Main.jFrameManager.rePack();
        }
    }
}
