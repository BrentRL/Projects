package main;

//import java.util.Random;
//import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

import tools.StaticTools;

public class Demo implements Runnable
{
    private Thread runThread;
    private Asteroids a;
    private KeyEvent left, right, bullet, missile, gMissile, forward, reverse, sShot, puMissile, puGmissile, nuke, start, mine, puMine;
    private JPanel canvasPane;
    private boolean run;
    private int maxWait = 100, maxFireWait = 60, maxTurnWait = 7000, maxMoveWait = 7000;
    
    public Demo(){
        this(new Asteroids("Computer player"));
    }
    
    public Demo(Asteroids asteroids){
        a = asteroids;
        canvasPane = a.getPlayPane();
        initEvents();
        start();
    }
    
    private void initThread(){
        runThread = new Thread(this);
    }
    
    public void stop(){
        run = false;
    }
    
    private void initEvents(){
        left = new KeyEvent(canvasPane, 0, 0, 0, 37, ' ');
        right = new KeyEvent(canvasPane, 0, 0, 0, 39, ' ');
        forward = new KeyEvent(canvasPane, 0, 0, 0, 38, ' ');
        reverse = new KeyEvent(canvasPane, 0, 0, 0, 40, ' ');
        bullet = new KeyEvent(canvasPane, 0, 0, 0 ,32, ' ');
        missile = new KeyEvent(canvasPane, 0, 0, 0, 66, 'b');
        gMissile = new KeyEvent(canvasPane, 0, 0, 0, 86, 'v');
        sShot = new KeyEvent(canvasPane, 0, 0, 0 ,67, 'c');
        puMissile = new KeyEvent(canvasPane, 0, 0, 0 ,71, 'g');
        puGmissile = new KeyEvent(canvasPane, 0, 0, 0 ,70, 'f');
        mine = new KeyEvent(canvasPane, 0, 0, 0 ,68, 'd');
        nuke = new KeyEvent(canvasPane, 0, 0, 0, 10, ' ');
        start = new KeyEvent(canvasPane, 0, 0, 0, 80, 'p');
        puMine = new KeyEvent(canvasPane, 0, 0, 0, 87, 'w');
    }

    public void start(){
        if (!run){
            initThread();
            runThread.start();
        }
    }
    
    public void run(){
        run = true;
        int event = 0;
        a.keyReleased(start);
        while (run){
            if (event == 0){
                turn();
                event = 1;
            }
            else if (event == 1){
                move();
                event = 2;
            }
            else if (event == 2){
                if (a.haveActiveTargets())
                    fire();
                event = 0;
            }
            sleep(StaticTools.rnd.nextInt(maxWait));
        }
    }

    private void fire(){
        KeyEvent fire = randomFireEvent();
        a.keyPressed(fire);
        sleep(StaticTools.rnd.nextInt(maxFireWait));
        a.keyReleased(fire);
    }
    
    private KeyEvent randomFireEvent(){
        int pick = StaticTools.rnd.nextInt(9);
        if (pick == 0)
            return bullet;
        else if (pick == 1)
            return missile;
        else if (pick == 2)
            return gMissile;
        else if (pick == 3)
            return sShot;
        else if (pick == 4)
            return puMissile;
        else if (pick == 5)
            return puGmissile;
        else if (pick == 6)
            return mine;
        else if (pick == 7)
            return puMine;
        else
            return nuke;
    }
    
    private void turn(){
        KeyEvent turn = randomTurnEvent();
        a.keyPressed(turn);
        sleep(StaticTools.rnd.nextInt(maxTurnWait));
        a.keyReleased(turn);
    }
    
    private KeyEvent randomTurnEvent(){
        int pick = StaticTools.rnd.nextInt(2);
        if (pick == 0)
            return left;
        else
            return right;
    }
    
    private void move(){
        KeyEvent move = randomMoveEvent();
        a.keyPressed(move);
        sleep(StaticTools.rnd.nextInt(maxMoveWait));
        a.keyReleased(move);
    }
    
    private KeyEvent randomMoveEvent(){
        int pick = StaticTools.rnd.nextInt(2);
        if (pick == 0)
            return forward;
        else
            return reverse;
    }
    
    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e){}
    }
}