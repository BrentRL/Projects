package space.objects;
import java.awt.*;
import java.util.ArrayList;

import space.objects.parents.SpaceObject;
import tools.StaticTools;

public class Ally extends SpaceObject
{
    private static final double[] xP = new double[] {0,   -10, 10};
    private static final double[] yP = new double[] {12, -13, -13};
    private static final Color fillColor = Color.orange;
    private static final Color drawColor = fillColor.darker();
    private static final int totalAngles = 180, maxRotation  = 160;
    private static final double angleInc = 360.0 / totalAngles;
    private static final double firePercentage = .9;//.45

    private boolean rotating = false, shoot = true;
    private double deltaTheta, totalTheta, currentAngleInc;
    private int missiles = 100, gMissiles = 90;
    private SpaceObject fire = null;
    
    public Ally(Object canvas, double x, double y){
        super(canvas, x, y);
    }
    
    public double[] getBaseXs(){
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }

    private void rotatePath(){
        totalTheta = StaticTools.rnd.nextInt(maxRotation) * StaticTools.randomPol();
        if (totalTheta < 0)
            currentAngleInc = -angleInc;
        else 
            currentAngleInc = angleInc;
    }
    
    public void move(){
        if (rotating){
            angle += currentAngleInc;
            deltaTheta += currentAngleInc;
            if (Math.abs(deltaTheta) >= Math.abs(totalTheta)){
                currentAngleInc = deltaTheta = totalTheta = 0;
                rotating = false;
                shoot = true;
            }
            render();
            draw();
            fire = null;
        }
        else if (shoot){
            rotatePath();
            rotating = true;
            shoot = false;
            render();
            draw();
            fire = randomFire();
        }
    }

    public SpaceObject moveAndFire(){
        move();
        return fire;
    }    
    
    private SpaceObject randomFire(){
        if (StaticTools.rnd.nextDouble() > firePercentage)
            return null;
        ArrayList<SpaceObject> fire = new ArrayList<SpaceObject>();
        Bullet b = new Bullet(canvas, xPos, yPos);
        fire.add(b);
        if (missiles > 0)
            fire.add(new Missile(canvas, xPos, yPos));
        if (gMissiles > 0)
            fire.add(new GuidedMissile(canvas, xPos, yPos, null));
        SpaceObject selectedFire;
        if (gMissiles > 0 || missiles > 0)
            selectedFire = (SpaceObject) fire.get(StaticTools.rnd.nextInt(fire.size()));
        else
            selectedFire = b;
        double speed = 0;
        
        if (selectedFire instanceof Bullet)
            speed = Bullet.speed;
        else if (selectedFire instanceof GuidedMissile){
            speed = Missile.missileSpeed;
            gMissiles--;
        }
        else if (selectedFire instanceof Missile){
            speed = Missile.missileSpeed;
            missiles--;
        }
        
        double xS = -speed * Math.sin(Math.toRadians(angle));
        double yS = speed * Math.cos(Math.toRadians(angle));
        selectedFire.setActive(true);
        selectedFire.setSpeed(xS, yS);
        selectedFire.setAngle(angle, false, true);
        return selectedFire;
    }
    
    public Color fillColor(){
        return fillColor;
    }
    
    public Color drawColor(){
        return drawColor;
    }
}