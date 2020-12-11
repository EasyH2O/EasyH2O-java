package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;

public class JFrameManager {

    public JFrame jFrame;

    /**
     * JFrameManager instance, used for managing Jframe panels, has helper function for popups.
     *
     * @param windowTitle Title of the frame, example: "My cool application!"
     */
    public JFrameManager(String windowTitle) {
        jFrame = new JFrame(windowTitle);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Shows or hides this Window depending on the value of parameter visible.
     *
     * @param visible true makes window visible.
     */
    public void visible(Boolean visible) {
        jFrame.setVisible(visible);
    }

    /**
     * Set the current content panel. Changes panel and rePacks
     *
     * @param jPanel The panel that will be switched to.
     */
    public void setContentPanel(JPanel jPanel) {
        jFrame.setContentPane(jPanel);
        rePack();
    }

    /**
     * Set the current content panel, also changes visibly to true.
     *
     * @param jPanel The panel that will be switched to.
     */
    public void setDefaultPanel(JPanel jPanel) {
        setContentPanel(jPanel);
        rePack();
        jFrame.setVisible(true);
    }

    /**
     * Panels available for JFrame.
     */
    public static class Frames {
        public static JPanel dashboard = new Dashboard().dashboard;
        public static JPanel preLaunch = new PreLaunchForm().preLaunch;
        public static JPanel login = new Login().login;
    }

    /**
     * Changes frame size to the content.
     */
    private void rePack() {
        jFrame.pack();
    }

    /**
     * Create an dialog box. Useful for displaying Errors or other messages.
     *
     * @param errorText Text that is shown in the dialog.
     */
    public void createDialogBox(String errorText) {
        JOptionPane.showMessageDialog(jFrame, errorText);
    }
}
