package unreallight;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UnrealCommand {

    private static String theDir;
    private static int delay;
    private static String portName;

    public static void main(String[] args) {
        theDir = "/home/unrealuser/.wine/drive_c/UnrealTournament/Logs/";
        delay = 3;
        portName = "/dev/ttyACM0";
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith("-dir:")) {
                    theDir = arg.substring(5);
                } else if (arg.startsWith("-del:")) {
                    try {
                        delay = Integer.parseInt(arg.substring(5));
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid argument for delay, using default of 3");
                        delay = 3;
                    }
                } else if (arg.startsWith("-port:")) {
                    portName = arg.substring(6);
                } else {
                    System.err.println("Invalid argument, ignoring " + arg);
                }
            }
            if (!new File(args[0]).isDirectory()) {
                theDir = "/home/unrealuser/.wine/drive_c/UnrealTournament/Logs/";
            } else {
                theDir = args[0];
            }

        }
        
        new UnrealCommand();
        FileWatcherThread fwt = new FileWatcherThread(theDir);
        fwt.start();
    }

    static synchronized void setLogFile(String toString) {
        ut.setLogFile(theDir);
    }
    public static UnrealThread ut;

    public UnrealCommand() {
        try {
            ut = new UnrealThread();
            ut.setLogFile(loadDirs());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private String loadDirs() throws IOException {
        long lm = 0;
        String tf = "";
        for (String s : new File(theDir).list()) {
            File f = new File(s);
            if (f.lastModified() > lm) {
                lm = f.lastModified();
                tf = s;
            }
        }
        return tf;
    }

    public static void debug(String d) {
        System.out.println(d);
    }

    class UnrealThread extends Thread {

        Map<Integer, Integer> playerTeamMap;
        private String logFile;
        private double timestamp;
        private TreeMap<TeamID, Integer> scoreList;

        public UnrealThread() {
            logFile = "";
            timestamp = 0.0d;
            scoreList = new TreeMap<>();
            scoreList.put(TeamID.RED, 0);
            scoreList.put(TeamID.BLUE, 0);
            scoreList.put(TeamID.GREEN, 0);
            scoreList.put(TeamID.GOLD, 0);
            playerTeamMap = new HashMap<>();
            this.start();
        }

        @Override
        public void run() {
            while (true) {
                if (!logFile.equals("")) {
                    try {

                        Process p = Runtime.getRuntime().exec(new String[]{"cat", logFile});
                        DataInputStream input = new DataInputStream(p.getInputStream());
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        int i = 0;
                        while ((i = input.read()) != -1) {
                            bos.write(i);
                        }
                        bos.flush();
                        String raw = new String(bos.toByteArray(), Charset.forName("UTF-16LE"));
                        String[] lines = raw.split("\n");
                        
                        
                        for (String aLine : lines) {
                            String[] segments = aLine.split("\t");

                            if (Double.parseDouble(segments[0]) >= timestamp) {
                                timestamp = Double.parseDouble(segments[0]);
                                String action = segments[1];
                                try {
                                    Events.valueOf(action.toUpperCase()).process(this, Arrays.copyOfRange(segments, 2, segments.length));
                                } catch (IllegalArgumentException ex) {
                                    Events.valueOf("DEFAULT").process(this, Arrays.copyOfRange(segments, 1, segments.length));
                                }
                                input.close();
                            }
                        }

                    } catch (UnsupportedEncodingException ex) {
                        System.err.println(ex.getMessage());
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }

                }
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

        public synchronized void setLogFile(String nd) {
            System.out.println("Setting logFile to " + nd);
            timestamp = 0.0d;
            playerTeamMap.clear();
            scoreList.clear();
            scoreList.put(TeamID.RED, 0);
            scoreList.put(TeamID.BLUE, 0);
            scoreList.put(TeamID.GREEN, 0);
            scoreList.put(TeamID.GOLD, 0);
            this.logFile = nd;
        }

        public void score(TeamID scoringTeam) {
            scoreList.put(scoringTeam, scoreList.get(scoringTeam) + 1);
            System.out.println(scoringTeam + " Scored");
            System.out.println("Current Scores");
            for (TeamID aTeam : scoreList.keySet()) {
                System.out.println(aTeam + " " + scoreList.get(aTeam));
            }
            LightingConfig.process(scoreList);
        }

        private int getTeamIDForPlayer(int playerID) {
            return playerTeamMap.get(playerID);
        }

        private void setTeamIDForPlayer(int playerID, int teamID) {
            playerTeamMap.put(playerID, teamID);
        }

        private void setScore(int teamID, int score) {

            scoreList.put(TeamID.values()[teamID], score);
            for (TeamID aTeam : scoreList.keySet()) {
                System.out.println(aTeam + " " + scoreList.get(aTeam));
            }
            LightingConfig.process(scoreList);
        }
    }

    interface EventInterface {

        void process(UnrealThread ut, String[] items);
    }

    public static enum Events implements EventInterface {
        FLAG_CAPTURED {
            @Override
            public void process(UnrealThread ut, String[] items) {
                int teamID2 = Integer.parseInt(items[1].substring(0, 1));
                TeamID teamFlag = TeamID.values()[teamID2];
                if (teamFlag.equals(TeamID.BLUE)) {
                    ut.score(TeamID.RED);
                } else if (teamFlag.equals(TeamID.RED)) {
                    ut.score(TeamID.BLUE);
                }
            }
        },
        FLAG_DROPPED {
            @Override
            public void process(UnrealThread ut, String[] items) {
                String playerID = items[0];
                int teamID = Integer.parseInt(items[1].substring(0, 1));
                System.out.println(TeamID.values()[teamID] + " flag dropped");
            }
        },
        FLAG_RETURNED {
            @Override
            public void process(UnrealThread ut, String[] items) {
                int teamID = Integer.parseInt(items[1].substring(0, 1));
                System.out.println(TeamID.values()[teamID] + " flag returned");
            }
        },
        FLAG_TAKEN {
            @Override
            public void process(UnrealThread ut, String[] items) {
                int teamID = Integer.parseInt(items[1].substring(0, 1));
                System.out.println(TeamID.values()[teamID] + " flag taken");
            }
        },
        CONTROLPOINT_CAPTURE {
            @Override
            public void process(UnrealThread ut, String[] items) {
                String locName = items[0];
                int playerID = Integer.parseInt(items[1].substring(0, 1));
                try {
                    System.out.println(TeamID.values()[ut.getTeamIDForPlayer(playerID)] + " control " + locName);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    for (String s : items) {
                        System.out.print(s);
                    }
                    System.out.println("");
                }

            }
        },
        PLAYER {
            @Override
            public void process(UnrealThread ut, String[] items) {
                if (items[0].equals("Team")) {
                    int playerID = Integer.parseInt(items[1]);
                    int teamID = Integer.parseInt(items[2].substring(0, 1));
                    System.out.println("Player " + playerID + " is on " + TeamID.values()[teamID]);
                    ut.setTeamIDForPlayer(playerID, teamID);
                }
            }

        },
        GAME {
            @Override
            public void process(UnrealThread ut, String[] items) {

                if (items[0].equals("GameClass")) {
                    if (items[1].startsWith("Botpack.CTFGame")) {
                        LightingConfig.gMode = LightingConfig.GameModes.CAPTURE_THE_FLAG;
                    } else if (items[1].startsWith("Botpack.Domination")) {
                        LightingConfig.gMode = LightingConfig.GameModes.DOMINATION;
                    } else {
                        System.out.println(items[1]);

                    }
                }
            }

        },
        DOM_SCORE_UPDATE {
            @Override
            public void process(UnrealThread ut, String[] items) {

                int teamID = Integer.parseInt(items[0]);
                int score = (int) Double.parseDouble(items[1]);
                ut.setScore(teamID, score);

            }

        },
        GAME_END {
            @Override
            public void process(UnrealThread ut, String[] items) {
                System.out.println("Game Finished");
                ut.setLogFile("");
            }

        },
        DEFAULT {
            @Override
            public void process(UnrealThread ut, String[] items) {
//                if (verbose) {
//                    for (String s : items) {
//                        System.out.print(s);
//                    }
//                    System.out.println("");
//                }
            }
        }
    }

    public static enum TeamID {
        RED, BLUE, GREEN, GOLD, NONE
    }

}
