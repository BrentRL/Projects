package space.objects;

import java.awt.*;

import space.objects.parents.SpaceObject;
import tools.StaticTools;

public class Enemy extends SpaceObject
{
    private static final Color fillColor = Color.red;
    private static final Color drawColor = fillColor.brighter().brighter();
    protected static final double enemySpeed = 6.0;
    private static final int maxAbsAngleInc = 3, maxCountdown = 1500, startingMissiles = 80;
    private static final double firePercentage = .01;
    private static final boolean fire = false;
    
    private double[] xP, yP;
    private int countdownToAngleChange, angleInc = 1, missiles = startingMissiles;
    
    public Enemy(Object canvas, double x, double y, double[][] points){
        super(canvas, x, y, points[0], points[1]);
        randMovement();
    }
    
    public void init(double x, double y, double[][] points){
        xPos = x;
        yPos = y;
        xP = points[0];
        yP = points[1];
        randMovement();
    }
    
    public void revamp(double[][] points){
        xP = points[0];
        yP = points[1];
        changeAngle();
    }
    
    private void randMovement(){
        angle = StaticTools.rnd.nextInt(360);
        xSpeed = -enemySpeed * Math.sin(Math.toRadians(angle));
        ySpeed = enemySpeed * Math.cos(Math.toRadians(angle));
    }
    
    public double[] getBaseXs(){
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }
    
    private void changeAngle(){
        angleInc = StaticTools.rnd.nextInt(maxAbsAngleInc) * StaticTools.randomPol();
        countdownToAngleChange = StaticTools.rnd.nextInt(maxCountdown);
    }
    
    public void move(){
        countdownToAngleChange--;
        if (countdownToAngleChange <= 0)
            changeAngle();
        angle += angleInc;
        xSpeed = -enemySpeed * Math.sin(Math.toRadians(angle));
        ySpeed = enemySpeed * Math.cos(Math.toRadians(angle));
        translate();
        wrap();
        draw();
        incLife();
    }
    
    public SpaceObject moveAndFire(){
        move();
        return randomFire();
    }
    
    protected SpaceObject randomFire(){
        if (!fire)
            return null;
        if (StaticTools.rnd.nextDouble() > firePercentage)
            return null;
            
        SpaceObject selectedFire;
        double speed = 0;
        EnemyBullet b = new EnemyBullet(canvas, xPos, yPos);
        //EnemyMissile m;
        if (missiles > 0){
            if (StaticTools.rnd.nextBoolean()){
                selectedFire = b;
                speed = EnemyBullet.speed;
            }
            else
                selectedFire = new EnemyMissile(canvas, xPos, yPos);
            if (selectedFire instanceof EnemyMissile){
                speed = EnemyMissile.missileSpeed;
                missiles--;
            }
        }
        else{
            selectedFire = b;
            speed = EnemyBullet.speed;
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