package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;

import javax.swing.*;

/**
 * This class is bound to the JFrame.
 */
public class Dashboard {

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

    private User currentUser;

    public Dashboard() {
    }

    public void initUser(User user) {
        this.currentUser = user;
        setUsername();
        updateProgress(60);
    }

    /**
     * Update progress bar value. (Amound of water in the rain barrel.
     *
     * @param percentage Percentage of water in rain barrel. Should be 0 - 100
     */
    public void updateProgress(int percentage) {
        progressBar.setValue(percentage);
    }

    private void setUsername() {
        usernameLabel.setText(currentUser.name);
    }
}
