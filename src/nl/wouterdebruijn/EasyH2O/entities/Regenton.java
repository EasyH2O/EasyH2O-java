package nl.wouterdebruijn.EasyH2O.entities;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.fazecast.jSerialComm.SerialPort;
import nl.wouterdebruijn.EasyH2O.Main;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;
import static com.fazecast.jSerialComm.SerialPort.*;

public class Regenton {

    Regenton MijnRegenTon = new Regenton();

    public void Aanmaken(){
        String Owner ;
        int value ;

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
