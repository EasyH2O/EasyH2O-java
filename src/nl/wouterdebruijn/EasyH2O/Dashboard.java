package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;
import nl.wouterdebruijn.EasyH2O.entities.WeatherPoint;
import nl.wouterdebruijn.EasyH2O.utils.JWeatherIcon;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
    private JLabel historyLabel;
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
    private JButton deleteAccountButton;
    private JPanel barrelSelectorPanel;
    private JButton addRainBarrelButton;
    private JButton deleteRainBarrelButton;

    private User currentUser;
    public int[] regentonIds;

    private int selectedRainbarrel = 0;

    public Dashboard() {
        // Refresh button
        button1.addActionListener(e -> Main.regentons.get(regentonIds[selectedRainbarrel]).getData());

        // Toggle pump button
        togglePumpButton.addActionListener(e -> Main.regentons.get(regentonIds[selectedRainbarrel]).switchPump());

        // Delete user button
        deleteAccountButton.addActionListener(e -> {
            currentUser.delete();
            Main.jFrameManager.setContentPanel(JFrameManager.Frames.login);
            currentUser = null;
            Main.jFrameManager.createDialogBox("Your user account was deleted!");
        });

        //  Add Barrel button
        addRainBarrelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String comPort = JOptionPane.showInputDialog(Main.jFrameManager.jFrame, "Specify COM-Port");

                try {
                    PreparedStatement preparedStatement = Main.mySQLConnector.con.prepareStatement("INSERT INTO `easy_h2o`.`regenton` (`owner`, `comPort`) VALUES (?, ?)");
                    preparedStatement.setInt(1, currentUser.id);
                    preparedStatement.setString(2, comPort);
                    preparedStatement.executeUpdate();

                    // Update main array;
                    Main.initRegentonnen();

                    // Update dashboard
                    initUser(currentUser);

                    Main.jFrameManager.createDialogBox(" Created!");
                } catch (SQLException throwables) {
                    Main.jFrameManager.createDialogBox("Error while saving to database.");
                    throwables.printStackTrace();
                }
            }
        });

        // Delete barrel button
        deleteRainBarrelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement preparedStatement = Main.mySQLConnector.con.prepareStatement("DELETE FROM `regenton` WHERE (`id` = ?)");
                    Regenton regenton = Main.regentons.get(regentonIds[selectedRainbarrel]);
                    preparedStatement.setInt(1, regenton.id);
                    preparedStatement.executeUpdate();
                    selectedRainbarrel = 0;

                    // Update main array;
                    Main.initRegentonnen();

                    // Update dashboard;
                    initUser(currentUser);
                } catch (SQLException throwables) {
                    Main.jFrameManager.createDialogBox("Could not delete rain barrel.");
                    throwables.printStackTrace();
                }
            }
        });
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

        clearDashboard();

        setUsername();
        updateRegentonnen();
        connectRegentonnen();
        initBarrelSelector();

        updateCycle();

        displayWeather();
    }

    public void initBarrelSelector() {
        ArrayList<String> barrelList = new ArrayList<>();

        for (int i=0; i < this.regentonIds.length; i++) {
            barrelList.add("Barrel " + (i+1));
        }

        // Convert arraylist to array
        String[] barrels = new String[barrelList.size()];
        barrels = barrelList.toArray(barrels);

        JComboBox barrelSelector = new JComboBox(barrels);

        barrelSelectorPanel.setLayout(new GridLayout(0, 1));

        barrelSelectorPanel.add(barrelSelector);

        barrelSelector.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            this.selectedRainbarrel = cb.getSelectedIndex();
            updateCycle();
        });
    }

    /**
     * Clear all dynamically allocated modules on dashboard start
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    private void clearDashboard() {
        weatherModule.removeAll();
        weatherFutureModule.removeAll();
        graphOutputJPanel.removeAll();
        barrelSelectorPanel.removeAll();
    }

    /**
     * Update dashboard content. Updates from user and rain barrel variables.
     *
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public void updateCycle() {
        updateRegentonnen(); // TODO: might not need this.

        // If the user doesn't have any rain barrels, we don't execute the rest of the update cycle
        if (regentonIds.length == 0) {
            Main.jFrameManager.createDialogBox("User doesn't have any rain barrels!");
            return;
        }

        // Create new arrayList where we store our information
        ArrayList<Double> YGraphPoints = new ArrayList<>();
        Regenton regenton = Main.regentons.get(regentonIds[selectedRainbarrel]);

        try {
            ResultSet resultSet = Main.mySQLConnector.query("SELECT data FROM datapoint WHERE regenton = " + regenton.id + " ORDER BY id DESC LIMIT 5;");
            while (resultSet.next()) {
                // Get String
                String rawString = resultSet.getString("data");

                // Remove last character (its a ;)
                rawString = rawString.substring(0, rawString.length() - 1);
                // split string on ,
                String[] valueArray = rawString.split(",");

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

        // Generate graph with new array (Only Generate graph if we have enough data points.
        if (doubles.length >= 5) generateGraph(doubles);
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
        } else {
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

            for (int i = 0; i < regentonnen.size(); i++) {
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
        for (int regentonId : regentonIds) {
            Regenton regenton = Main.regentons.get(regentonId);
            System.out.println("Opening Serial for ID: " + regenton.id + " @" + regenton.comPort);
            regenton.openPort();
        }
    }

    /**
     * Graph to display waterlevel
     *
     * @param resultProcents dataPoints for Graph
     * @author Emma
     */
    public void generateGraph(double[] resultProcents) {
        double[] xLaatsteMetingen = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};

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

    /**
     * Display weather information on the dashboard.
     */
    private void displayWeather() {
        try {
            // Day codes corresponding to Unix DayId
            String[] days = new String[]{
                    "Zo",
                    "Ma",
                    "Di",
                    "Wo",
                    "Do",
                    "Vr",
                    "Za",
            };

            WeatherPoint today = Main.weatherModule.getToday();
            weatherModule.add(generateWeatherModule(today, "Delft"));
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(today.date);

            WeatherPoint[] comingDays = Main.weatherModule.getUpcoming();

            weatherFutureModule.setLayout(new GridLayout(0, 5));

            for (int i = 1; i < comingDays.length && i < 6; i++) { // Hard lock on 5 (We skip the first, because that is today)
                WeatherPoint day = comingDays[i];
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(day.date);
                weatherFutureModule.add(generateWeatherModule(day, days[calendar.get(Calendar.DAY_OF_WEEK) - 1]));
            }

            Main.jFrameManager.rePack(); // Resize + update screen.

        } catch (IOException e) {
            e.printStackTrace();
            Main.jFrameManager.createDialogBox(e.getMessage());
        }
    }

    /**
     * Generate new weather module.
     *
     * @param point     Weather data that needs to be displayed.
     * @param labelText Text to add inside of the module.
     * @return Returns a new JPanel
     */
    private JPanel generateWeatherModule(WeatherPoint point, String labelText) {
        try {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(new JWeatherIcon(point.iconId));

            JPanel panelInner = new JPanel();
            panelInner.setLayout(new BoxLayout(panelInner, BoxLayout.Y_AXIS));

            panelInner.add(new JLabel(point.temp + "°C"));
            panelInner.add(new JLabel(labelText));

            panel.add(panelInner);

            return panel;

        } catch (IOException e) {
            e.printStackTrace();
            Main.jFrameManager.createDialogBox(e.getMessage());
        }
        return new JPanel();
    }
}
