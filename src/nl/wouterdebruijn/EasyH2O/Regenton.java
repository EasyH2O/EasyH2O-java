package nl.wouterdebruijn.EasyH2O;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import nl.wouterdebruijn.EasyH2O.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.fazecast.jSerialComm.SerialPort.*;

public class Regenton {
    public final int id;
    public final String comPort;
    public final User owner;
    private SerialPort serialPort;
    public boolean pumpEnabled = false;

    private final byte[] buffer = new byte[1024];
    private String query;

    public Regenton(int id, String comPort, User owner) {
        this.id = id;
        this.comPort = comPort;
        this.owner = owner;
    }

    public void openPort() {
        String poortName;
        // sp is lijst van seriale poorten
        SerialPort[] sp = SerialPort.getCommPorts();

        // Geeft aantal poorten aan
        if (sp.length == 0) {
            System.out.println("Er zijn geen seriële poorten. Sluit je Micro:bit aan!");
            return;

        }
        // boven opgegeven poort wordt aan serialPort toegekend
        // kijkt of de poort kan worden geopend
        serialPort = SerialPort.getCommPort(comPort);
        if (serialPort.openPort()) {
            serialPort.setComPortParameters(9600, 8, ONE_STOP_BIT, NO_PARITY);
            serialPort.setFlowControl(FLOW_CONTROL_DISABLED);

            // Add the event bases message listener

            Regenton.MessageListener messageListener = new Regenton.MessageListener(this.id);
            serialPort.addDataListener(messageListener);

            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");

        }
    }

    public boolean isOpen() {
        if (serialPort == null) return false;
        else return serialPort.isOpen();
    }


    /**
     * Close the connection to MySQL Database
     * <p>
     * made by Erhan
     */

    public void getData() {
        try {
            String CMD = "RF;";
            byte[] msg = CMD.getBytes();
            serialPort.writeBytes(msg, CMD.length());
        } catch (Exception ex) {
            System.out.println("Fout bij schrijven naar seriële poort: " + ex);
        }
    }

    public void switchPump() {
        try {
            String cmd = "SP;";
            byte[] MSG = cmd.getBytes();
            serialPort.writeBytes(MSG, cmd.length());
        } catch (Exception ex) {
            System.out.println("Fout bij het schakelen van de pomp: " + ex);
        }
    }

    public void pumpState() {
        try {
            String cmd = "PS;";
            byte[] MSG = cmd.getBytes();
            serialPort.writeBytes(MSG, cmd.length());
        } catch (Exception ex) {
            System.out.println("Fout bij het ophalen van status van de pomp: " + ex);
        }
    }


    private static final class MessageListener implements SerialPortMessageListener {
        public final int regentonId;

        private MessageListener(int regentonId) {
            this.regentonId = regentonId;
        }

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
         */
        @Override
        public void serialEvent(SerialPortEvent event) {
            byte[] delimitedMessage = event.getReceivedData();
            String message = new String(delimitedMessage);

            /*Check for incoming values*/
            if (message.startsWith("FC")) { // Float Change, store value in DB.
                storeFloatValues(regentonId, message);

                // Refresh dashboard to update values.
                Main.jFrameManager.dashboardInstance.updateCycle();
            } else if (message.startsWith("PV")) { // Pump Value, store value in db, set local value

                // Get and Change regenton in ArrayList.
                int regentonIndex = Main.indexById(regentonId);
                Regenton newState = Main.regentons.get(regentonIndex);
                newState.pumpEnabled = message.contains("1");
                Main.regentons.set(regentonIndex, newState);

                // Refresh dashboard to update values.
                Main.jFrameManager.dashboardInstance.updateCycle();
            } else {
                System.out.println("Other return value: " + message);
            }
        }
    }

    private static void storeFloatValues(int id, String message) {
        System.out.println("Storing new float value for regenton: " + id);
        try {
            if (!Main.mySQLConnector.con.isClosed())
                Main.mySQLConnector.sendMicroBitData(id, message);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Close the connection to MySQL Database
     * <p>
     * made by Erhan
     */

    public void disconnect() {
        if (!isOpen()) return;
        if (serialPort.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
        }
    }

    /**
     * Close the connection to MySQL Database
     * <p>
     * made by Luca
     */


    public String[] getOldData(String regenton) {
        try {
            Statement statement = Main.mySQLConnector.con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `datapoint` WHERE `regenton` data = " + regenton + ";");
            String data = null;
            String tijd = null;

            for (int teller = 0; teller < 5 && resultSet.next(); teller++) {
                data = resultSet.getString("data");
                tijd = resultSet.getString("timestamp");
                ///this.getData("Data: " + data);
                ///this.getData("Tijd: " + tijd);

            }
            return new String[]{data, tijd};


        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return new String[0];

    }
}
