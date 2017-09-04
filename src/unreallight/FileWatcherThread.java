package unreallight;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileWatcherThread extends Thread {

    private String theDir;

    public FileWatcherThread() {
        
    }

    FileWatcherThread(String theDir) {
        this.theDir = theDir;
    }

    @Override
    public void run() {
        try {
            WatchDir wd = new WatchDir(new File(theDir).toPath(), false);
        } catch (IOException ex) {
            Logger.getLogger(FileWatcherThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
