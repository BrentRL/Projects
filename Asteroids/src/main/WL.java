package main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WL extends WindowAdapter
{
    private boolean exit;
    private Asteroids target;

    public WL(boolean sysExit, Asteroids a){
        target = a;
        exit = sysExit;
    }
    
    public void windowClosing(WindowEvent we){
        target.setRun(false);
        if (exit)
            System.exit(0);
    }
}
        