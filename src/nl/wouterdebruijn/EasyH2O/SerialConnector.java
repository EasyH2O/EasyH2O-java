package nl.wouterdebruijn.EasyH2O;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.sql.SQLException;
import java.util.Scanner;

import static com.fazecast.jSerialComm.SerialPort.*;


public class SerialConnector {
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
            MessageListener messageListener = new MessageListener();
            serialPort.addDataListener(messageListener);

            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");

        }
    }

    public void Send() {
        try {
            // vertaald de RF
            // stuur een RFnaar microbit
            String CMD = "RF;";
            byte[] msg = CMD.getBytes();
            serialPort.writeBytes(msg, CMD.length());
        } catch (Exception ex) {
            System.out.println("Fout bij schrijven naar seriële poort: " + ex);
        }
    }


    /**
     * Event based reading
     * Properties are defined with the @Override functions returning values.
     */
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
         * @Author Wouter
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


}









