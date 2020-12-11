package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener

public class Login {
    public JPanel login;
    private JTextField usernameField;
    private JLabel usernameLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JButton loginButton;

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            /**
             * Login button
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Git test



                Main.jFrameManager.createDialogBox("Login is not implemented yet. Here is a dashboard.");
                Main.jFrameManager.setContentPane(JFrameManager.Frames.dashboard);
            }
        });
    }
}
