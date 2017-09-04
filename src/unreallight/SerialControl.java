package unreallight;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

public class SerialControl implements SerialPortEventListener {

    SerialPort serialPort;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
        "/dev/tty.usbserial-A9007UX1", //Mac OS X
        "/dev/ttyACM0", //Linux
        "COM7", //Windows
    };

    private static final int TIME_OUT = 2000; // Milliseconds to block while waiting for port open
    private static final int DATA_RATE = 9600;

    private InputStream input;
    private OutputStream output;
    private DataOutputStream myOut;

    public void initialize() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        //iterate through,looking for the port
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }
        try {
            //open serial port,and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);
            //set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //open the streams
            input = serialPort.getInputStream();
//            new SerialEchoThread(input).start();
            output = serialPort.getOutputStream();
//            //add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            myOut = new DataOutputStream(output);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port.Read the data and print it.
     *
     * @param oEvent
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                String cLine;
                while ((cLine = in.readLine()) != null) {
                    System.out.println(cLine);
                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    public void sendData(String data) throws IOException {
        myOut.write(data.getBytes());
    }

    public void sendData(byte[] data) throws IOException {
        myOut.write(data);
    }
}
