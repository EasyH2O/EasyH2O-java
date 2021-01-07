package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.User;
import nl.wouterdebruijn.EasyH2O.utils.RowSelectListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel {
    public JPanel contolPanel;
    private JLabel usernameLabel;
    private JLabel applicationHeader;
    private JLabel usernameLabelHeader;
    private JPanel tableModulePanel;

    private User currentUser;

    /**
     * Init the controlPanel to work for the user.
     * Sets the username and other user variables.
     *
     * @param user user to register as logged in user.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public void initUser(User user) {
        this.currentUser = user;
        setUsername();
        updateCycle();

        initUserTable();
    }

    /**
     * Update controlPanel content.
     *
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public void updateCycle() {

    }

    /**
     * Set username to current user.
     */
    private void setUsername() {
        usernameLabel.setText(currentUser.name);
    }

    /**
     * Generate user table with imitate user function.
     */
    public void initUserTable() {
        try {
            ResultSet userResults = Main.mySQLConnector.query("SELECT * FROM user");

            ArrayList<Object[]> dataList = new ArrayList<>();
            ArrayList<User> userList = new ArrayList<>();

            while (userResults.next()) {
                int id = userResults.getInt("id");
                String name = userResults.getString("naam");
                String hash = userResults.getString("passwordHash");
                String email = userResults.getString("email");
                boolean isAdmin = userResults.getBoolean("isAdmin");

                if (!isAdmin) {
                    userList.add(User.fromHash(id, email, hash, name, false));

                    Object[] tempObject = {id, name, email, false};
                    dataList.add(tempObject);
                }
            }

            Object[][] data = new Object[dataList.size()][];
            data = dataList.toArray(data);

            User[] users = new User[userList.size()];
            users = userList.toArray(users);

            String[] columnNames = {
                    "ID:",
                    "Name:",
                    "Email",
                    "is Admin"
            };

            JTable table = new JTable(data, columnNames);
            tableModulePanel.setLayout(new GridLayout(0, 1));
            tableModulePanel.add(table);

            User[] finalUsers = users;
            table.addMouseListener(new RowSelectListener() {
                /**
                 * On row click event, create new dashboard as current user.
                 * @param e MouseEvent
                 */
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Row " + table.getSelectedRow() + " is selected!");

                    JFrame frame = new JFrame("Dashboard as Admin, imitating " + finalUsers[table.getSelectedRow()].name);

                    frame.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            try {
                                System.out.println("Running closing event.");
                                List<Regenton> regentonnen = currentUser.getRegentonnen();


//                                Closeing event doesnt work yet, serial connection is not closed right.

                                // Create new array from regenton list.
                                int[] regentonIds = new int[regentonnen.size()];

                                for (int i=0; i < regentonnen.size(); i++) {
                                    int index = Main.indexById(regentonnen.get(i).id);
                                    regentonIds[i] = index;
                                }

                                for (int id : regentonIds) {
                                    Regenton tmp = Main.regentons.get(id);
                                    tmp.disconnect();
                                }

                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    });

                    frame.setContentPane(Main.jFrameManager.dashboardInstance.dashboard);
                    Main.jFrameManager.dashboardInstance.initUser(finalUsers[table.getSelectedRow()]);

                    frame.pack();
                    frame.setVisible(true);
                }
            });
            Main.repackUI();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
