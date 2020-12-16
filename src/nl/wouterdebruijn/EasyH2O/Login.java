package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    public JPanel login;
    private JLabel emailLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JButton loginButton;
    private JTextField emailField;
    private JButton registerButton;

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            /**
             * Login button
             * @Author Riham Assem
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputEmail = emailField.getText();
                String inputPassword = new String(passwordField.getPassword());

                try {
                    /*
                     * Select matching user from database searches with email.
                     */
                    PreparedStatement preparedStatement = Main.mySQLConnector.con.prepareStatement("SELECT * FROM user where email = (?);");
                    preparedStatement.setString(1, inputEmail);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("naam");
                        String hash = resultSet.getString("passwordHash");
                        String email = resultSet.getString("email");

                        User tmpUser = User.fromHash(id, email, hash, name);

                        if (tmpUser.validatePassword(inputPassword)) {
                            // Password correct!
                            Main.jFrameManager.setContentPanel(JFrameManager.Frames.dashboard);
                            Main.jFrameManager.dashboardInstance.initUser(tmpUser); // Set current user to the logged in user. Used for dashboard user variables.
                        } else {
                            Main.jFrameManager.createDialogBox("Incorrect username or password.");
                        }
                    } else {
                        Main.jFrameManager.createDialogBox("Incorrect username or password.");
                    }
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                    Main.jFrameManager.createDialogBox("SQL error, check console.");
                }
            }
        });
        /*
         * Register button event changes page to register page.
         */
        registerButton.addActionListener(e -> Main.jFrameManager.setContentPanel(JFrameManager.Frames.register));
    }
}
