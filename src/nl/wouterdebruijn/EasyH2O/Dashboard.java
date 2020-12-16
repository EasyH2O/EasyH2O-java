package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is bound to the JFrame.
 */
public class Dashboard extends JFrame {

    public JPanel dashboard;
    private JPanel header;
    private JPanel modules;
    private JPanel progressModule;
    private JPanel weatherModule;
    private JPanel weatherFutureModule;
    private JPanel historyModule;
    private JLabel progressLabel;
    private JLabel weatherLabel;
    private JLabel historyLabel;
    private JLabel weatherFutureLabel;
    private JPanel headerUser;
    private JPanel headerBar;
    private JLabel usernameLabel;
    private JProgressBar progressBar;
    private JButton button1;

    private User currentUser;
    private Regenton[] regentonnen;

    public Dashboard() {
        button1.addActionListener(e -> regentonnen[0].getData());
    }

    /**
     * Init the dashboard to work for the user.
     * Sets the username and other user variables.
     *
     * TODO: Implement Regenton.java
     * @param user user to register as logged in user.
     * @Author Wouter de Bruijn git@electrogamez.nl
     */
    public void initUser(User user) {
        this.currentUser = user;
        setUsername();
        updateRegentonnen();
    }

    /**
     * Update progress bar value. (Amount of water in the rain barrel.
     *
     * @param percentage Percentage of water in rain barrel. Should be 0 - 100
     * @Author Wouter de Bruijn git@electrogamez.nl
     */
    public void updateProgress(int percentage) {
        progressBar.setValue(percentage);
    }

    private void setUsername() {
        usernameLabel.setText(currentUser.name);
    }

    private void updateRegentonnen() {
        try {
            List<Regenton> regentonnen = currentUser.getRegentonnen();

            for (Regenton regenton : regentonnen) {
                // Open ports for each user regenton
                System.out.println("Opening Serial for" + regenton.id + "@" + regenton.comPort);
                regenton.openPort();

                // Store regenton objects in array.
                this.regentonnen = new Regenton[regentonnen.size()];
                regentonnen.toArray(this.regentonnen);
            }
            int size = regentonnen.size();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
