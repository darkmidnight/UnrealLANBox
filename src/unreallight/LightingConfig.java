package unreallight;

import java.io.ByteArrayOutputStream;
import java.util.TreeMap;
import unreallight.UnrealCommand.TeamID;

public class LightingConfig extends Thread {

    static DisplayModes dMode = DisplayModes.SHOW_WINNER;
    static GameModes gMode = GameModes.DOMINATION;

    public static enum DisplayModes {
        SHOW_WINNER, WINNING_PROPORTION,
    }
    public static enum GameModes {
        CAPTURE_THE_FLAG, DOMINATION
    }
    

    static void process(TreeMap<TeamID, Integer> scoreList) {
        new LightingConfig(scoreList);
    }

    private TreeMap<TeamID, Integer> scoreList;
    int runCount = 0;

    public LightingConfig(TreeMap<TeamID, Integer> scoreList) {
        this.scoreList = scoreList;
        this.start();
    }
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    @Override
    public void run() {
        
        
        int cInt = 0;
        cInt = dMode.ordinal() << 4;
        cInt = cInt + gMode.ordinal();
        bos.write(cInt);
        bos.write(0b00000000);
        for (TeamID t : scoreList.keySet()) {
            bos.write(scoreList.get(t));
        }
        
        SerialController.sendData(bos.toByteArray());
        for (byte b : bos.toByteArray()) {
            System.out.print(BinaryUtilities.byteToBinaryString(b));
            System.out.print(" ");
        }
        System.out.println("");
            
        bos.reset();
        
    }

    private int getColor(TeamID t) {
        switch (t) {
            case RED:
                return BinaryUtilities.rgbToSingleInt(255, 0, 0);
            case BLUE:
                return BinaryUtilities.rgbToSingleInt(0, 0, 255);
            case GREEN:
                return BinaryUtilities.rgbToSingleInt(0, 255, 0);
            case GOLD:
                return BinaryUtilities.rgbToSingleInt(251, 223, 18);
        }
        return BinaryUtilities.rgbToSingleInt(0, 0, 0);
    }

}
