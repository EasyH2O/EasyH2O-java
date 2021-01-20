package nl.wouterdebruijn.EasyH2O.entities;

import java.util.Date;

/**
 * WeatherPoint containing a icon, temperature, timestamp and iconId.
 *
 * @Author Wouter de Bruijn git@rl.hedium.nl
 */
public class WeatherPoint {
    public final double temp;
    public final Date date;
    public final String iconId;

    /**
     * Create a new weatherpoint
     *
     * @param temp     Weather Temperature
     * @param timeUnix Timestamp of temperature reading
     * @param iconId   Icon id corresponding to openweathermap.org
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public WeatherPoint(double temp, long timeUnix, String iconId) {
        this.date = new Date(timeUnix * 1000); // Java expects milliseconds, so we multiply
        this.temp = temp;
        this.iconId = iconId;
    }
}
