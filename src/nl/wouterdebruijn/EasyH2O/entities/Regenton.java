package nl.wouterdebruijn.EasyH2O;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.sql.SQLException;
import java.util.Scanner;

import java.sql.ResultSet;
import java.sql.Statement;

import static com.fazecast.jSerialComm.SerialPort.*;

public class Regenton {

    public static SerialPort serialPort;

    private final byte[] buffer = new byte[1024];

    public void OpenPort() {
        // SerialPort sp[] = SerialPort.getCommPorts("COM5");

        String poortName;
        // sp is lijst van seriale poorten
        SerialPort[] sp = SerialPort.getCommPorts();

        // Geeft aantal poorten aan
        if (sp.length == 0) {
            System.out.println("Er zijn geen seriële poorten. Sluit je Micro:bit aan!");
            return;

        }
        if (sp.length == 1) {
            poortName = sp[0].getSystemPortName();
            System.out.println(poortName + " wordt nu gebruikt.");
        } else {
            System.out.println("Meerdere seriële poorten gedetecteerd: ");
        }
        // Vraagt om de juiste poort door te geven
        System.out.println("Type poortnaam die je wilt gebruiken en druk Enter...");
        Scanner in = new Scanner(System.in);
        poortName = in.next();

        // boven opgegeven poort wordt aan serialPort toegekend
        // kijkt of de poort kan worden geopend
        serialPort = SerialPort.getCommPort(poortName);
        if (serialPort.openPort()) {
            serialPort.setComPortParameters(9600, 8, ONE_STOP_BIT, NO_PARITY);
            serialPort.setFlowControl(FLOW_CONTROL_DISABLED);

            // Add the event bases message listener

            Regenton.MessageListener messageListener = new Regenton.MessageListener();
            serialPort.addDataListener(messageListener);

            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");

        }
    }
    /**
     * Close the connection to MySQL Database
     *
     * made by Erhan
     */

    public void GetData() {
        try {
            String CMD = "RF;";
            byte[] msg = CMD.getBytes();
            serialPort.writeBytes(msg, CMD.length());
        } catch (Exception ex) {
            System.out.println("Fout bij schrijven naar seriële poort: " + ex);
        }
        Scanner response = new Scanner(serialPort.getInputStream()).useDelimiter(";");
        String Datafloats = response.next();
        System.out.println("readUSB: " + Datafloats);
    }


    private static final class MessageListener implements SerialPortMessageListener {
        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return ";".getBytes();
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        /**
         * Incoming data event! This function runs when the microbit sends data.
         *
         * @param event Gives the event properties, not used at this time.
         * @Author Erhan
         *
         */
        @Override
        public void serialEvent(SerialPortEvent event) {
            byte[] delimitedMessage = event.getReceivedData();
            System.out.println("Received the following delimited message: " + new String(delimitedMessage));

            try {
                if (!Main.mySQLConnector.con.isClosed())
                    Main.mySQLConnector.sendMicroBitData(new String(delimitedMessage));
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }
    /**
     * Close the connection to MySQL Database
     *
     * made by Erhan
     */

    public void Disconnect() {
        if (serialPort.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
            return;
        }
    }

    /**
     * Close the connection to MySQL Database
     *
     * made by Luca
     */

    public int GetOldData() {
        try {
            Statement statement = MySQLConnector.con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `datapoint` WHERE `regenton` = 1;");

            for (int teller = 0; teller < 5 && resultSet.next(); teller++) {
                String data = resultSet.getString("data");
                String tijd = resultSet.getString("timestamp");

                System.out.println("Data: " + data);
                System.out.println("Tijd: " + tijd);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Close the connection to MySQL Database
     *
     * made by Luca
     */

    public void SwitchPump() {
        try {
            String CMD = "SP;";
            byte[] msg = CMD.getBytes();
            serialPort.writeBytes(msg, CMD.length());
        } catch (Exception ex) {
            System.out.println("Fout bij het schakelen van de pomp: " + ex);
        }
    }

    public void PumpState() {
        try {
        }
    }
}
