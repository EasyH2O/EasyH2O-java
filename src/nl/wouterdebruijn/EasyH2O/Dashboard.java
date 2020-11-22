package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * This class is bound to the JFrame.
 */
public class Dashboard {

    private JPanel Dashboard;
    private JLabel applicationTitle;
    private JPanel contentPanel;
    private JTextField formHostname;
    private JTextField formUsername;
    private JTextField formDatabase;
    private JPasswordField formPassword;
    private JLabel formHostnameLabel;
    private JLabel formUsernameLabel;
    private JLabel formDatabaseLabel;
    private JLabel formPasswordLabel;
    private JButton getLatestValueButton;
    private JTextPane outputTextPlane;
    private JLabel mySQLStatusLabel;
    private JLabel mySQLStatusField;
    private JButton formConfirm;
    private JButton randomTextButton;
    private JLabel outputLabel;

    static public JFrame jFrame;

    public Dashboard() {


        formConfirm.addActionListener(new ActionListener() {
            /**
             * "SQL Connect" button logic
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Main.mySQLConnector.disconnect();
                    Main.mySQLConnector.connect(formHostname.getText(), formUsername.getText(), formDatabase.getText(), String.valueOf(formPassword.getPassword()));

                    setMySQlStatus(Main.mySQLConnector.con.isClosed());

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    /**
     * Set the status label to corresponding text and color.
     * @param isDisconnected this could be false when the sql connection is active. Normally filled with con.isClosed()
     */
    public void setMySQlStatus(boolean isDisconnected) {
        if (!isDisconnected) {
            mySQLStatusField.setText("Connected");
            mySQLStatusField.setForeground(Color.green);
        } else {
            mySQLStatusField.setText("Disconnected");
            mySQLStatusField.setForeground(Color.red);
        }
    }

    /**
     * Creates the jFrame object, also sets the default parameters.
     */
    public void createAndShow() {
        jFrame = new JFrame("Dashboard App");
        jFrame.setContentPane(new Dashboard().Dashboard);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }
    
    /**
     * Redraws the JFrame to be sized to components.
     */
    public static void updateSize() {
        if (jFrame != null) jFrame.pack();
    }
}
