package corewars.jmars;


import corewars.jmars.frontend.CoreDisplay;
import corewars.jmars.frontend.RoundCycleCounter;
import corewars.jmars.marsVM.VM;

import java.util.Vector;

public class ConfigurationSingleton {

    //Field hält Referenz auf einzigartige Instanz
    private static ConfigurationSingleton instance;

    // Privater Konstruktur verhindert Instanziierung durch Client
    private ConfigurationSingleton(){
        // Set defaults for various constants
        maxWarriorLength = 100;
        minWarriorDistance = 100;
        maxProc = 8000;
        coreSize = 8000;
        cycles = 80000;
        rounds = 10;
    }

    //Stellt Einzigartigkeit sicher. Liefert Exemplar an Client.
    //Hier: Unsynchronisierte Lazy-Loading-Variante
    public static ConfigurationSingleton getInstance(){
        if (instance == null){
            instance = new ConfigurationSingleton();
        }
        return instance;
    }

    // Common variables
    boolean useGui = false;
    int maxProc;
    int pSpaceSize;
    int coreSize;
    int cycles;
    int rounds;
    int maxWarriorLength;
    int minWarriorDistance;
    int numWarriors;
    int minWarriors;

    int runWarriors;

    public static void setInstance(ConfigurationSingleton instance) {
        ConfigurationSingleton.instance = instance;
    }

    public boolean isUseGui() {
        return useGui;
    }

    public void setUseGui(boolean useGui) {
        this.useGui = useGui;
    }

    public int getMaxProc() {
        return maxProc;
    }

    public void setMaxProc(int maxProc) {
        this.maxProc = maxProc;
    }

    public int getpSpaceSize() {
        return pSpaceSize;
    }

    public void setpSpaceSize(int pSpaceSize) {
        this.pSpaceSize = pSpaceSize;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getMaxWarriorLength() {
        return maxWarriorLength;
    }

    public void setMaxWarriorLength(int maxWarriorLength) {
        this.maxWarriorLength = maxWarriorLength;
    }

    public int getMinWarriorDistance() {
        return minWarriorDistance;
    }

    public void setMinWarriorDistance(int minWarriorDistance) {
        this.minWarriorDistance = minWarriorDistance;
    }

    public int getNumWarriors() {
        return numWarriors;
    }

    public void setNumWarriors(int numWarriors) {
        this.numWarriors = numWarriors;
    }

    public int getMinWarriors() {
        return minWarriors;
    }

    public void setMinWarriors(int minWarriors) {
        this.minWarriors = minWarriors;
    }

    public int getRunWarriors() {
        return runWarriors;
    }

    public void setRunWarriors(int runWarriors) {
        this.runWarriors = runWarriors;
    }

    public Vector setAllConfigurations(String[] args) {
        boolean pspaceChanged = false;
        Vector wArgs = new Vector();

        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].equals("-g")) {
                    useGui = true;
                } else if (args[i].equals("-r")) {
                    rounds = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-s")) {
                    coreSize = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-c")) {
                    cycles = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-p")) {
                    maxProc = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-l")) {
                    maxWarriorLength = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-d")) {
                    minWarriorDistance = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-S")) {
                    pSpaceSize = Integer.parseInt(args[++i]);
                    pspaceChanged = true;
                }
            } else {
                numWarriors++;
                wArgs.addElement(new Integer(i));
            }
        }

        if (!pspaceChanged) {
            pSpaceSize = coreSize / 16;
        }

        if (numWarriors == 0) {
            System.out.println("ERROR: no warrior files specified");
        }

        return wArgs;
    }
}
