package unreallight;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerialController {
    private static SerialController dbConnection;
    private SerialControl sCon;
    
    private SerialController() {
        super();
        sCon = new SerialControl();
        sCon.initialize();
    }
    
    public static synchronized void sendData(byte[] b ) {
        SerialControl sc = getSerial().getControl();
        try {
            sc.sendData(b);
        } catch (IOException ex) {
            Logger.getLogger(SerialController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static synchronized void sendData(String s) {
        SerialControl sc = getSerial().getControl();
        try {
            sc.sendData(s);
        } catch (IOException ex) {
            Logger.getLogger(SerialController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized SerialControl getControl() {
        return sCon;
    }
    /**
     * Stops the class being cloned
     * @return Nothing - Throws Exception
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton. Cloning is not allowed.");
    }

    /**
     * Gets an active instance of the Singleton class
     * @return Instance of the Singleton Class
     */
    public static synchronized SerialController getSerial() {
        if (dbConnection == null) {
            dbConnection = new SerialController();
        }
        return dbConnection;
    }
}