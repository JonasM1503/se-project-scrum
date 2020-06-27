/*-
 * Copyright (c) 1998 Brian Haskin jr.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */
package corewars.jmars;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import corewars.jmars.marsVM.*;
import corewars.jmars.frontend.*;
import corewars.jmars.assembler.*;

/**
 * jMARS is a corewars interpreter in which programs (warriors) battle in the
 * memory of a virtual machine (the MARS) and try to disable the other program.
 */
public class jMARS extends Panel implements Runnable, FrontEndManager {

    // constants
    static final int numDefinedColors = 4;
    static final Color wColors[][] = {{Color.green, Color.yellow},
            {Color.red, Color.magenta},
            {Color.cyan, Color.blue},
            {Color.gray, Color.darkGray}};

    // Application specific variables
    //String args[];
    //static Frame myFrame;
   // static jMARS myApp;

    static Thread myThread;
    static boolean exitFlag;

    ConfigurationSingleton configurationSingleton;

    WarriorObj allWarriors[];
    WarriorObj warriors[];
    CoreDisplay coreDisplay;
    RoundCycleCounter roundCycleCounter;
    VM MARS;

    Vector stepListeners;
    Vector cycleListeners;
    Vector roundListeners;

    int totalCycles;

    public jMARS() {
        stepListeners = new Vector();
        cycleListeners = new Vector();
        roundListeners = new Vector();
    }

    /**
     * Starting function for the application. It sets up a frame and adds the
     * applet to it.
     *
     * @param args java.lang.String[] a - array of command line arguments
     */
    public static void main(String args[]) {

        if (args.length == 0) {
            System.out.println("usage: jMARS [options] warrior1.red [warrior2.red ...]");
            return;
        }
        Frame myFrame = new Frame("jMARS");
        myFrame.setSize(new Dimension(1200, 900));
        jMARS myApp = new jMARS();
        AppliationWindow myApplicationWindow = new AppliationWindow();
        myFrame.add(myApp);
        myFrame.addWindowListener(myApplicationWindow);
        myFrame.show();
        myApp.applicationInit(args);
    }

    /**
     * Initialization function for the application.
     */
    void applicationInit(String[] args) {
        configurationSingleton = configurationSingleton.getInstance();

        Vector wArgs = configurationSingleton.setAllConfigurations(args);

        Assembler parser = initParser();

        getAllWarriors(parser, wArgs, args);

        if (configurationSingleton.isUseGui())
        {
            coreDisplay = new CoreDisplay(this, this, configurationSingleton.getCoreSize(), 100);
        }
        roundCycleCounter = new RoundCycleCounter(this, this);
        validate();
        repaint();
        update(getGraphics());
        MARS = new MarsVM(configurationSingleton.getCoreSize(), configurationSingleton.getMaxProc());
        loadWarriors();
        configurationSingleton.setMinWarriors((configurationSingleton.getNumWarriors() == 1) ? 0 : 1);
        myThread = new Thread(this);
        myThread.setPriority(Thread.NORM_PRIORITY - 1);
        myThread.start();
        return;
    }

    private void getAllWarriors(Assembler parser, Vector wArgs, String[] args){
        allWarriors = new WarriorObj[configurationSingleton.getNumWarriors()];

        for (int i = 0; i < configurationSingleton.getNumWarriors(); i++) {
            try {
                FileInputStream wFile = new FileInputStream(args[(((Integer) wArgs.elementAt(i)).intValue())]);
                try {
                    parser.parseWarrior(wFile);
                    if (parser.length() > configurationSingleton.getMaxWarriorLength()) {
                        System.out.println("Error: warrior " + args[(((Integer) wArgs.elementAt(i)).intValue())] + " to large");
                        System.exit(0);
                    }
                    allWarriors[i] = new WarriorObj(parser.getWarrior(), parser.getStart(), wColors[i % numDefinedColors][0], wColors[i % numDefinedColors][1]);
                    allWarriors[i].setName(parser.getName());
                    allWarriors[i].setAuthor(parser.getAuthor());
                    allWarriors[i].Alive = true;
                    allWarriors[i].initPSpace(configurationSingleton.getpSpaceSize());
                    allWarriors[i].setPCell(0, -1);
                } catch (AssemblerException ae) {
                    System.out.println("Error parsing warrior file " + args[(((Integer) wArgs.elementAt(i)).intValue())]);
                    System.out.println(ae.toString());
                    System.exit(0);
                } catch (IOException ioe) {
                    System.out.println("IO error while parsing warrior file " + args[(((Integer) wArgs.elementAt(i)).intValue())]);
                    System.exit(0);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Could not find warrior file " + args[(((Integer) wArgs.elementAt(i)).intValue())]);
                System.exit(0);
            }
        }
    }

    private Assembler initParser(){
        Assembler parser = new corewars.jmars.assembler.icws94p.ICWS94p();
        parser.addConstant("coresize", Integer.toString(configurationSingleton.getCoreSize()));
        parser.addConstant("maxprocesses", Integer.toString(configurationSingleton.getMaxProc()));
        parser.addConstant("maxcycles", Integer.toString(configurationSingleton.getCoreSize()));
        parser.addConstant("maxlength", Integer.toString(configurationSingleton.getMaxWarriorLength()));
        parser.addConstant("mindistance", Integer.toString(configurationSingleton.getMinWarriorDistance()));
        parser.addConstant("rounds", Integer.toString(configurationSingleton.getRounds()));
        parser.addConstant("pspacesize", Integer.toString(configurationSingleton.getpSpaceSize()));
        parser.addConstant("warriors", Integer.toString(configurationSingleton.getNumWarriors()));
        return parser;
    }


    /**
     * main function and loop for jMARS. Runs the battles and handles display.
     */
    public void run() {
        Date tStartTime;
        Date tEndTime;
        double totalTime;
        int totalCycles = 0;


        if (configurationSingleton.isUseGui())
        {
            coreDisplay.clear();
        }

        tStartTime = new Date();
        HashMap<String, Integer> statistic = runRounds();

        tEndTime = new Date();
        totalTime = ((double) tEndTime.getTime() - (double) tStartTime.getTime()) / 1000;
        System.out.println("Total time=" + totalTime + " Total Cycles=" + totalCycles + " avg. time/cycle=" + (totalTime / totalCycles));
        System.out.println("Survivor in how many rounds:");
        for (String name : statistic.keySet())
        {
            System.out.println("  " + name + ": " + statistic.get(name));
        }
    }

    private int runCycles(){
        int cycleNum = 0;
        for (; cycleNum < configurationSingleton.getCycles(); cycleNum++) {
            for (int warRun = 0; warRun < configurationSingleton.getRunWarriors(); warRun++) {
                StepReport stats = MARS.step();
                stats.warrior.numProc = stats.numProc;
                if (stats.wDeath) {
                    stats.warrior.Alive = false;
                    configurationSingleton.setRunWarriors(configurationSingleton.getRunWarriors() - 1);
                    ArrayList<WarriorObj> tmp = new ArrayList<>();
                    for (int warIdx = 0; warIdx < warriors.length; warIdx++)
                    {
                        if (warIdx != warRun)
                        {
                            tmp.add(warriors[warIdx]);
                        }
                    }
                    warriors = tmp.toArray(new WarriorObj[] { });
                    break;
                }
                notifyStepListeners(stats);
            }
            notifyCycleListeners(cycleNum);
            repaint();
            if (configurationSingleton.getRunWarriors() <= configurationSingleton.getMinWarriors()) {
                break;
            }
        }
        return cycleNum;
    }

    private HashMap<String, Integer> runRounds(){
        HashMap<String, Integer> statistic = new HashMap<>();
        Date startTime;
        Date endTime;
        double roundTime;


        startTime = new Date();

        for (int roundNum = 0; roundNum < configurationSingleton.getRounds(); roundNum++) {

            int cycleNum = runCycles();

            for (int warIdx = 0; warIdx < warriors.length; warIdx++)
            {
                String name = warriors[warIdx].getName();
                Integer count = statistic.getOrDefault(name, Integer.valueOf(0));
                count++;
                statistic.put(name, count);
            }
            notifyRoundListeners(roundNum);
            endTime = new Date();
            roundTime = ((double) endTime.getTime() - (double) startTime.getTime()) / 1000;
            System.out.println(roundNum+1 + ". Round time=" + roundTime + " Cycles=" + cycleNum + " avg. time/cycle=" + (roundTime / cycleNum));
            startTime = new Date();
            totalCycles += cycleNum;
            if (AppliationWindow.exitFlag) {
                break;
            }
            MARS.reset();
            loadWarriors();
            if (configurationSingleton.isUseGui())
            {
                coreDisplay.clear();
            }
        }
        return statistic;
    }

    /**
     * Load warriors into core
     */
    void loadWarriors() {
        warriors = new WarriorObj[allWarriors.length];
        System.arraycopy(allWarriors, 0, warriors, 0, allWarriors.length);
        configurationSingleton.setRunWarriors(configurationSingleton.getNumWarriors());
        int[] location = new int[warriors.length];

        if (!MARS.loadWarrior(warriors[0], 0)) {
            System.out.println("ERROR: could not load warrior 1.");
        }

        for (int i = 1, r = 0; i < configurationSingleton.getNumWarriors(); i++) {
            boolean validSpot;
            do {
                validSpot = true;
                r = (int) (Math.random() * configurationSingleton.getCoreSize());

                if (r < configurationSingleton.getMinWarriorDistance() || r > (configurationSingleton.getCoreSize() - configurationSingleton.getMinWarriorDistance())) {
                    validSpot = false;
                }

                for (int j = 0; j < location.length; j++) {
                    if (r < (configurationSingleton.getMinWarriorDistance() + location[j]) && r > (configurationSingleton.getMinWarriorDistance() + location[j])) {
                        validSpot = false;
                    }
                }
            } while (!validSpot);

            if (!MARS.loadWarrior(warriors[i], r)) {
                System.out.println("ERROR: could not load warrior " + (i + 1) + ".");
            }
        }
    }

    /**
     * update the display
     *
     * @param g java.awt.Graphics g - Graphics context
     */
    public void update(Graphics g) {
        paintComponents(g);
        return;
    }

    /**
     * register an object to receive step results.
     *
     * @param l StepListener - object to register
     */
    public void registerStepListener(StepListener l) {
        stepListeners.addElement(l);
    }

    /**
     * register an object to receive cycle results.
     *
     * @param c CycleListener - object to register
     */
    public void registerCycleListener(CycleListener c) {
        cycleListeners.addElement(c);
    }

    /**
     * register an object to receive round results.
     *
     * @param r RoundListener - object to register
     */
    public void registerRoundListener(RoundListener r) {
        roundListeners.addElement(r);
    }

    protected void notifyStepListeners(StepReport step) {
        for (Enumeration e = stepListeners.elements(); e.hasMoreElements();) {
            StepListener j = (StepListener) e.nextElement();
            j.stepProcess(step);
        }
    }

    protected void notifyCycleListeners(int cycle) {
        for (Enumeration e = cycleListeners.elements(); e.hasMoreElements();) {
            CycleListener j = (CycleListener) e.nextElement();
            j.cycleFinished(cycle);
        }
    }

    protected void notifyRoundListeners(int round) {
        for (Enumeration e = roundListeners.elements(); e.hasMoreElements();) {
            RoundListener j = (RoundListener) e.nextElement();
            j.roundResults(round);
        }
    }


}