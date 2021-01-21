package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class PreLaunchForm {
    public JPanel preLaunch;
    private JPanel mySQLForm;
    private JTextField usernameField;
    private JLabel usernameLabel;
    private JTextField hostnameField;
    private JTextField databaseField;
    private JLabel passwordLabel;
    private JLabel hostnameLabel;
    private JLabel databaseLabel;
    private JButton connectButton;
    private JLabel connectionStatusLabel;
    private JPasswordField passwordField;
    private JButton startDashboardButton;

    public PreLaunchForm() {
        connectButton.addActionListener(new ActionListener() {
            /**
             * On MySQL Connect button
             * @Author Wouter de Bruijn git@rl.hedium.nl
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Main.mySQLConnector.connect(hostnameField.getText(), usernameField.getText(), databaseField.getText(), new String(passwordField.getPassword()));
                    updateMySQLStatus();
                } catch (SQLException throwable) {
                    Main.jFrameManager.createDialogBox(throwable.getMessage());
                    throwable.printStackTrace();
                }
            }
        });
        startDashboardButton.addActionListener(new ActionListener() {
            /**
             * On Start Dashboard button
             * @Author Wouter de Bruijn git@rl.hedium.nl
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!Main.mySQLConnector.con.isClosed()) {
                        Main.jFrameManager.setContentPanel(JFrameManager.Frames.login);

                        // Fill rain barrel array.
                        Main.initRegentonnen();
                    } else {
                        throw new NullPointerException();
                    }
                } catch (SQLException | NullPointerException throwable) {
                    Main.jFrameManager.createDialogBox("MySQL Connection is not open, please make sure you are connected.");
                }
            }
        });
    }

    /**
     * Update the status text in the upper right corner of the dashboard.
     */
    private void updateMySQLStatus() {
        try {
            if (Main.mySQLConnector.con.isClosed()) {
                connectionStatusLabel.setText("Disconnected");
                connectionStatusLabel.setForeground(Color.red);
            } else {
                connectionStatusLabel.setText("Connected");
                connectionStatusLabel.setForeground(Color.green);
            }
        } catch (SQLException throwable) {
            Main.jFrameManager.createDialogBox(throwable.getMessage());
            throwable.printStackTrace();
        }
    }
}
