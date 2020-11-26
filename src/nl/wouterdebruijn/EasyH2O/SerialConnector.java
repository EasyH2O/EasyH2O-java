package nl.wouterdebruijn.EasyH2O;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.Scanner;

import static com.fazecast.jSerialComm.SerialPort.*;



public class SerialConnector {
    public static SerialPort serialPort;

    private final byte [] buffer = new byte[1024];

    public void OpenPort() {
        // SerialPort sp[] = SerialPort.getCommPorts("COM5");

        String poortName;
        // sp is lijst van seriale poorten
        SerialPort sp[] = SerialPort.getCommPorts();

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
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");

        }
    }

    public void Send(){
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
    public void Response(){
        // Krijgt antwoord van de microbit en leest tot ;
        Scanner response = new Scanner(serialPort.getInputStream()).useDelimiter(";");
        String Datafloats = response.next();
        System.out.println("readUSB: " + Datafloats);
    }


    public synchronized void serialEvent(SerialPortEvent oevent) {
        if (oevent.getEventType() != LISTENING_EVENT_DATA_AVAILABLE){
            return;
        }
        int bytesAvailable = serialPort.bytesAvailable();
        if (bytesAvailable <= 0 ){
            return;
        }
        int bytesRead = serialPort.readBytes(buffer, Math.min(buffer.length, bytesAvailable));
            byte [] data = oevent.getReceivedData() ;
            String response =  new String(buffer,0, bytesRead);
            System.out.println("readUSB: " +  response);

    }

    public void ClosePort(){
        if (serialPort.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
        }
    }


}
/*
                MassageListener listener = new MassageListener();
                serialPort.addDataListener(listener);
                try {
                String CMD = "RF;";
                byte[] msg = CMD.getBytes();
                serialPort.writeBytes(msg, CMD.length());
                } catch (Exception ex) {
                System.out.println("Fout bij schrijven naar seriële poort: " + ex);
                }

 */


/*    public static void disconnect () {
        serialPort.removeDataListener();
        serialPort.closePort();
    }


    private static final class MassageListener implements SerialPortMessageListener {
        @Override
        public int getListeningEvents() {
            return LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return ";".getBytes();
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        @Override
        public synchronized void serialEvent(SerialPortEvent oevent) {
            if (oevent.getEventType() == SerialPortEvent. )
            byte [] data = oevent.getReceivedData() ;

        }


    }
            /*        SerialPortMessageListener listener = new SerialPortMessageListener() {


     if (reponse.hasNext()) {
        String Datafloats = reponse.next();
        System.out.println("readUSB: " + Datafloats);



        };
        MessageListener listener= new MessageListener ();
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return 0;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

            }
        });

        /*   25-11-2020
        String vorigefloat = "FC,1,1,1,1,1";

        Scanner response = new Scanner(serialPort.getInputStream()).useDelimiter(";");
        if (response.hasNext()) {
            String Datafloats = response.next();
            System.out.println("readUSB: " + Datafloats);
            if (!Datafloats.equals(vorigefloat)) {
                System.out.println("float is changed ");
                System.out.println("vorige float: " + vorigefloat);
                System.out.println("nieuw float : " + Datafloats);
                vorigefloat = Datafloats;
            } else {
                System.out.println("float is not changed ");

            }

         */









