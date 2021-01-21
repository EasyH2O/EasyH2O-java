package nl.wouterdebruijn.EasyH2O.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class JWeatherIcon extends JPanel {
    private final BufferedImage image;

    public JWeatherIcon(String iconId) throws IOException {
        this.setBackground(Color.gray);
        this.image = ImageIO.read(
                new URL("http://openweathermap.org/img/wn/" + iconId + "@2x.png")
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return (new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
