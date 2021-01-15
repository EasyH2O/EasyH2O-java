package nl.wouterdebruijn.EasyH2O;

import nl.wouterdebruijn.EasyH2O.entities.WeatherPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * WeatherModule API, uses openweathermap.org
 * @Author Wouter de Bruijn git@rl.hedium.nl
 */
public class WeatherModule {
    private final String apiKey;

    /**
     * Create a new API manager.
     * @param apiKey openweatherAPI Key.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public WeatherModule(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Create a new API call
     * @param rawUrl openweathermap url without API key.
     * @return JSON object containing openweathermap's response
     * @throws IOException If the HTTP request fails, we throw an error.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    private JSONObject apiCall(String rawUrl) throws IOException {
        URL url = new URL(rawUrl + "&appid=" + this.apiKey);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return new JSONObject(response.toString());
    }

    /**
     * Returns the current weather from openweathermap.org
     *
     * @return A Weatherpoint containing the temperature, icon and time.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public WeatherPoint getToday() throws IOException {
        JSONObject json = apiCall("https://api.openweathermap.org/data/2.5/weather?q=Delft&units=metric");
        double temp = json.getJSONObject("main").getDouble("temp");
        String iconId = json.getJSONArray("weather").getJSONObject(0).getString("icon");
        long unixTime = json.getLong("dt");

        return new WeatherPoint(temp, unixTime, iconId);
    }

    /**
     * Returns the weather for the coming days.
     *
     * @return A Array of Weatherpoints containing the temperature, icon and time.
     * @Author Wouter de Bruijn git@rl.hedium.nl
     */
    public WeatherPoint[] getUpcoming() throws IOException {
        JSONObject json = apiCall("https://api.openweathermap.org/data/2.5/onecall?lat=52.0067&lon=4.3556&exclude=minutely,hourly&units=metric");

        JSONArray weatherPoints = json.getJSONArray("daily");

        ArrayList<WeatherPoint> weatherPointArrayList = new ArrayList<>();

        for (int i = 0; i < weatherPoints.length(); i++) {
            long unixTime = weatherPoints.getJSONObject(i).getLong("dt");
            double temp = weatherPoints.getJSONObject(i).getJSONObject("temp").getDouble("day");
            String iconId = weatherPoints.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
            weatherPointArrayList.add(new WeatherPoint(temp, unixTime, iconId));
        }

        WeatherPoint[] resultArray = new WeatherPoint[weatherPointArrayList.size()];
        resultArray = weatherPointArrayList.toArray(resultArray);

        return resultArray;
    }
}
