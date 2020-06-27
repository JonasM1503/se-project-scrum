package corewars.jmars;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class AppliationWindow implements WindowListener {
    public static boolean exitFlag;

    void createFrame(String[] args) {
        Frame myFrame = new Frame("jMARS");
        myFrame.setSize(new Dimension(1200, 900));
        jMARS myApp = new jMARS();
        myFrame.add(myApp);
        myFrame.addWindowListener(this);
        myFrame.show();
        myApp.applicationInit(args);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        exitFlag = true;
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
