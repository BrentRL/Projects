package space.objects;

import java.awt.*;

import space.objects.parents.SpaceObject;
import tools.StaticTools;

public class Missile extends SpaceObject
{
    public static final double defectiveMissleProb = .05; // 5% defective
    private static final double[] xP = new double[] {-2, 0, 2};
    private static final double[] yP = new double[] {-10, 10, -10};
    private static final Color fillColor = Color.green;
    private static final Color drawColor = fillColor.brighter().brighter();
    public static boolean possiblyDefective = false, wrap = true;
    public static double missileSpeed = 10, maxRadius = 20;
    
    protected boolean defective;
    protected int countdownToChangeAngle, angleChange;
    

    public Missile(Object canvas, double x, double y){
        super(canvas, x, y);
        if (possiblyDefective)
            defective = StaticTools.rnd.nextDouble() <= defectiveMissleProb;
        if (defective)
            activateDefect();
    }
    
    public double[] getBaseXs(){
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }
    
    public void setLife(int life){
        if (life > 0){
            useCounter = true;
            lifeCounter = life;
            if (defective)
                lifeCounter /= 2;
        }
        else
            useCounter = false;
    }
    
    private void activateDefect(){
        angleChange = StaticTools.rnd.nextInt(10) * StaticTools.randomPol();
        countdownToChangeAngle = StaticTools.rnd.nextInt(50);
    }
    
    public void move(){
        if (defective){
            countdownToChangeAngle--;
            angle += angleChange;
            xSpeed = -missileSpeed * Math.sin(Math.toRadians(angle));
            ySpeed = missileSpeed * Math.cos(Math.toRadians(angle));
            if (countdownToChangeAngle <= 0)
                activateDefect();
        }
        translate();
        if (wrap)
            wrap();
        draw();
        incLife();
        if (!wrap && isOffScreen())
            isActive = false;
    }
    
    public Color fillColor(){
        return fillColor;
    }
    
    public Color drawColor(){
        return drawColor;
    }
}