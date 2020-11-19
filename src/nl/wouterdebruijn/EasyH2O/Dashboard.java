package nl.wouterdebruijn.EasyH2O;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * This class is bound to the JFrame.
 */
public class Dashboard {

    private JPanel Dashboard;
    private JLabel headerField;
    private JButton randomTextButton;
    private JLabel outputLabel;

    static public JFrame jFrame;

    public Dashboard() {
        randomTextButton.addActionListener(new ActionListener() {

            /**
             * This function is automatically generated then the listener was added to the button, this will execute then the button is pressed. Also provides a Action event. Not used for this example.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                outputLabel.setText(String.format("This is some random text, added to this label via logic%nHere is a random number: %d", new Random().nextInt(10)));
                updateSize();
            }
        });
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
