package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
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
    private JButton togglePumpButton;
    private JPanel graphOutputJPanel;

    private User currentUser;
    public int[] regentonIds;

    public Dashboard() {
        // Refresh button
        button1.addActionListener(e -> Main.regentons.get(regentonIds[0]).getData());

        // Toggle pump button
        togglePumpButton.addActionListener(e -> Main.regentons.get(regentonIds[0]).switchPump());

       // generateGraph();
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

        // Create new arrayList where we store our information
        ArrayList<Double> YGraphPoints = new ArrayList<>();
        Regenton regenton = Main.regentons.get(regentonIds[0]); // TODO: Change 0 to selected barrel later @Riham

        try {
            ResultSet resultSet = Main.mySQLConnector.query("SELECT data FROM datapoint WHERE regenton = " + regenton.id + " ORDER BY ID DESC LIMIT 5;");
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

                // Add calculated % value to our list
                YGraphPoints.add((double) resultProcents); // cast our int to a double so the graph gets it.
            }

        } catch (SQLException throwables) {
            Main.jFrameManager.createDialogBox("Error while refreshing dashboard.");
            throwables.printStackTrace();
        }

        // Set Pump label
        setPumpLabel(regenton.pumpEnabled);

        Collections.reverse(YGraphPoints);

        // Convert Double ArrayList to double Array
        double[] doubles = new double[YGraphPoints.size()];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = YGraphPoints.get(i);
        }

        // Generate graph with new array
        generateGraph(doubles);
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

    /**
     * Graph to display waterlevel
     * @author Emma
     * @param resultProcents dataPoints for Graph
     */
    public void generateGraph(double[] resultProcents)
    {
        double[] xLaatsteMetingen = new double[] {1.0, 2.0, 3.0, 4.0, 5.0};

        // Create Chart
        XYChart chart = QuickChart.getChart("Waterstand", "Laatste Metingen", "Procent", "y(x)", xLaatsteMetingen, resultProcents);

        chart.getStyler().setToolTipsEnabled(true);

        // Create Panel from chart
        JPanel chartPanel = new XChartPanel<XYChart>(chart);

        //prevents duplicate graphs
        graphOutputJPanel.removeAll();

        // Add that panel to our panel!
        graphOutputJPanel.add(chartPanel); // graphOutputJPanel is aangemaakt in de .form file, de layout manager mag geen IntelIJ of JGoodies zijn, anders krijg je een null exception.
        Main.jFrameManager.rePack(); // Resize + update screen.
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
