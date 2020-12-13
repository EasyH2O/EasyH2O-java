package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.InputException;
import nl.wouterdebruijn.EasyH2O.entities.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;


public class Register {
    public JPanel register;
    private JTextField emailField;
    private JTextField nameField;
    private JButton registerButton;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JLabel fullnameLabel;
    private JLabel emailLabel;

    public Register() {
        registerButton.addActionListener(e -> {
            try {
                if (emailField.getText().isBlank()) throw new InputException("Email field can not be blank!");
                if (new String(passwordField.getPassword()).isBlank())
                    throw new InputException("Password field can not be blank!");
                if (nameField.getText().isBlank()) throw new InputException("Full Name field can not be blank!");

                User newUser = new User(1, emailField.getText(), new String(passwordField.getPassword()), nameField.getText());
                newUser.save();

                Main.jFrameManager.createDialogBox("User created! You will be send back to login.");
                Main.jFrameManager.setContentPanel(JFrameManager.Frames.login);

            } catch (InputException exception) {
                Main.jFrameManager.createDialogBox(exception.message);
            } catch (SQLException exception) {
                Main.jFrameManager.createDialogBox("SQL Error: " + exception.getMessage());
                exception.printStackTrace();
            }
        });
    }
}
