package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;

public class JFrameManager {

    public JFrame jFrame;

    public JFrameManager(String windowTitle) {
        jFrame = new JFrame(windowTitle);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void visible(Boolean visible) {
        jFrame.setVisible(visible);
    }

    public JFrame getjFrame() {
        return jFrame;
    }

    public void setContentPane(JPanel jPanel) {
        jFrame.setContentPane(jPanel);
        rePack();
    }

    public void setDefaultPanel(JPanel jPanel) {
        setContentPane(jPanel);
        rePack();
        jFrame.setVisible(true);
    }

    public static class Frames {
        public static JPanel dashboard = new Dashboard().dashboard;
        public static JPanel preLaunch = new PreLaunchForm().preLaunch;
        public static JPanel login = new Login().login;
    }

    private void rePack() {
        jFrame.pack();
    }

    /**
     * Create an dialog box. Usefull for displaying Errors or other messages.
     *
     * @param errorText Text that is shown in the dialog.
     */
    public void createDialogBox(String errorText) {
        JOptionPane.showMessageDialog(jFrame, errorText);
    }
}
