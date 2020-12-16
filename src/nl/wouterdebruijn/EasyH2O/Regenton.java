package nl.wouterdebruijn.EasyH2O;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.mysql.cj.xdevapi.PreparableStatement;
import nl.wouterdebruijn.EasyH2O.entities.User;

import java.sql.*;

import static com.fazecast.jSerialComm.SerialPort.*;

public class Regenton {
    public final int id;
    public final String commPort;
    public final User owner;
    public static SerialPort serialPort;

    private final byte[] buffer = new byte[1024];

    public Regenton(int id, String commPort, User owner) {
        this.id = id;
        this.commPort = commPort;
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
        serialPort = SerialPort.getCommPort(commPort);
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
            String message = new String(delimitedMessage);
            if(message.equals("Switched to 0")|| message.equals("Switched to 1")) {

                System.out.println("Received the following delimited message: " + message);
            }
            else if(message.equals("0")|| message.equals("1")) {

                System.out.println("Received the following delimited message: " + message);
            }
            else {
                System.out.println("Received the following delimited message: " + message);
            }
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

    public void disconnect() {
        if (serialPort.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
        }
    }

    /**
     * Close the connection to MySQL Database
     *
     * made by Luca
     */

    public void getOldData(int regenton) {
        Connection connect = null;
        Statement statement = null;
        PreparableStatement preparableStatement = null;
        ResultSet resultSet = null;

        try {
           // getClass();
            connect = DriverManager.getConnection("hierin moet database tabel toevoegen");
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM DATABASE ");
            while (resultSet.next()){
                String Name = resultSet.getNString("get name van user name");
                String  ID  = resultSet.getNString("get Id van user");
                String Email = resultSet.getNString("Get email van user");
                String hashedPassword  = resultSet.getNString("Get hashedPassword van user");
                System.out.println("ID:" + ID + "\nName:" + Name +"\nEmail:" + Email + "\nhashedPassword:" + hashedPassword );

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Close the connection to MySQL Database
     *
     * made by Luca
     */



}
