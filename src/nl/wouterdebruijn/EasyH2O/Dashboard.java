package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
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
    private JTextArea textArea1;
    private JLabel pumpLabel;
    private JLabel pumpStatusLabel;

    private User currentUser;
    public int[] regentonIds;

    public Dashboard() {
        button1.addActionListener(e -> Main.regentons.get(regentonIds[0]).getData());
    }

    /**
     * Init the dashboard to work for the user.
     * Sets the username and other user variables.
     * <p>
     * TODO: Implement Regenton.java
     *
     * @param user user to register as logged in user.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public void initUser(User user) {
        this.currentUser = user;
        setUsername();
        updateRegentonnen();
        connectRegentonnen();

        updateCycle();
    }

    /**
     * Update dashboard content. Updates from user and rain barrel variables.
     *
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public void updateCycle() {
        updateRegentonnen();

        StringBuilder resultTextArea = new StringBuilder();

        for (int regentonId : regentonIds) {
            Regenton regenton = Main.regentons.get(regentonId);

            try {
                // TODO: Use function from Regenton class, ex: regenton.getData() returns String
                ResultSet resultSet = Main.mySQLConnector.query("SELECT data FROM datapoint WHERE regenton = " + regenton.id + " ORDER BY id DESC LIMIT 3;");
                while (resultSet.next()) {

                    String[] valueArray = resultSet.getString("data").split(",");
                    int resultProcents = 0;

                    // Calculate progress from raw string
                    for (int i = 1; i < valueArray.length; i++) {
                        if (valueArray[i].equals("0")) {
                            resultProcents += 20;
                        }
                    }

                    if (resultSet.isFirst()) { // Send first item to progress bar
                        updateProgress(resultProcents);
                    }

                    // Generate log
                    resultTextArea.append(regenton.id).append(": ").append(resultProcents);
                    resultTextArea.append("\n");
                }

            } catch (SQLException throwables) {
                Main.jFrameManager.createDialogBox("Error while refreshing dashboard.");
                throwables.printStackTrace();
            }

            // Set Pump label
            setPumpLabel(regenton.pumpEnabled);
        }

        // Update text area.
        textArea1.setText(resultTextArea.toString());
    }

    /**
     * Set the pump label to the appropriate text and color.
     *
     * @param status true = powered on, false powered off.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    private void setPumpLabel(Boolean status) {
        if (status) {
            pumpStatusLabel.setText("Powered On");
            pumpStatusLabel.setForeground(Color.green);
        } else{
            pumpStatusLabel.setText("Powered Off");
            pumpStatusLabel.setForeground(Color.red);
        }
    }

    /**
     * Update progress bar value. (Amount of water in the rain barrel.
     *
     * @param percentage Percentage of water in rain barrel. Should be 0 - 100
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public void updateProgress(int percentage) {
        progressBar.setValue(percentage);
    }

    private void setUsername() {
        usernameLabel.setText(currentUser.name);
    }

    /**
     * Create local array with all rain barrel index that are used for this user. Used to access specified barrels in main storage ArrayList.
     *
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    private void updateRegentonnen() {
        try {
            List<Regenton> regentonnen = currentUser.getRegentonnen();

            // Create new array from regenton list.
            regentonIds = new int[regentonnen.size()];

            for (int i=0; i < regentonnen.size(); i++) {
                int index = Main.indexById(regentonnen.get(i).id);
                regentonIds[i] = index;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Connect to rain barrels from this user
     * Calls Regenton.openPort() for every barrel associated to this user.
     *
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    private void connectRegentonnen() {
        for(int regentonId : regentonIds) {
            Regenton regenton = Main.regentons.get(regentonId);
            System.out.println("Opening Serial for ID: " + regenton.id + " @" + regenton.comPort);
            regenton.openPort();
        }
    }
}
