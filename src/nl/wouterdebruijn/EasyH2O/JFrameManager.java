package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;

public class JFrameManager {

    public JFrame jFrame;

    public final Dashboard dashboardInstance = new Dashboard();

    /**
     * JFrameManager instance, used for managing Jframe panels, has helper function for popups.
     *
     * @param windowTitle Title of the frame, example: "My cool application!"
     * @Author Wouter de Bruijn github@electrogamez.nl
     */
    public JFrameManager(String windowTitle) {
        jFrame = new JFrame(windowTitle);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Shows or hides this Window depending on the value of parameter visible.
     *
     * @param visible true makes window visible.
     * @Author Wouter de Bruijn github@electrogamez.nl
     */
    public void visible(Boolean visible) {
        jFrame.setVisible(visible);
    }

    /**
     * Set the current content panel. Changes panel and rePacks
     *
     * @param jPanel The panel that will be switched to.
     * @Author Wouter de Bruijn github@electrogamez.nl
     */
    public void setContentPanel(JPanel jPanel) {
        jFrame.setContentPane(jPanel);
        rePack();
    }

    /**
     * Set the current content panel, also changes visibly to true.
     *
     * @param jPanel The panel that will be switched to.
     * @Author Wouter de Bruijn github@electrogamez.nl
     */
    public void setDefaultPanel(JPanel jPanel) {
        setContentPanel(jPanel);
        rePack();
        jFrame.setVisible(true);
    }

    /**
     * Panels available for JFrame.
     * @Author Wouter de Bruijn github@electrogamez.nl
     */
    public static class Frames {
        public static JPanel dashboard = Main.jFrameManager.dashboardInstance.dashboard;
        public static JPanel preLaunch = new PreLaunchForm().preLaunch;
        public static JPanel login = new Login().login;
        public static JPanel register = new Register().register;
    }

    /**
     * Changes frame size to the content.
     * @Author Wouter de Bruijn github@electrogamez.nl
     */
    private void rePack() {
        jFrame.pack();
    }

    /**
     * Create an dialog box. Useful for displaying Errors or other messages.
     *
     * @param errorText Text that is shown in the dialog.
     * @Author Wouter de Bruijn github@electrogamez.nl
     */
    public void createDialogBox(String errorText) {
        JOptionPane.showMessageDialog(jFrame, errorText);
    }
}
