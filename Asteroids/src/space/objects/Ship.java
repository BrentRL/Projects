package space.objects;
import java.awt.*;

import space.objects.parents.Polygon2DDouble;
import space.objects.parents.SpaceObject;

public class Ship extends SpaceObject
{
    private static final double[] xP = new double[] {0,   -10, 10};
    private static final double[] yP = new double[] {12, -13, -13};
    private static final Color fillColor = Color.blue, ghostFillColor = new Color(163,198,237);
    private static final Color drawColor = new Color(125, 184, 232), ghostDrawColor = ghostFillColor.brighter().brighter();
    private static final int totalAngles = 120, framesPerGhost = 200;
    private static final double angleInc = 360.0 / totalAngles;
    private static final double speedInc = 0.6, absMaxSpeed = 6.87, flameProbability = .25;
    public static final double maxRadius = 15.0, half_width = 10;
    
    private boolean ghost, accel_decel;
    private int ghostFrameCount;
    private Flame flame;
    
    public Ship(Object canvas, double x, double y){
        super(canvas, x, y);
    }
    
    public void setFlame(Flame f){
        flame = f;
    }
    
    public void setHolding(boolean h){
        accel_decel = h;
    }
    
    public void accel(){
        double tempX = xSpeed - (speedInc*Math.sin(Math.toRadians(angle)));
        double tempY = ySpeed + (speedInc*Math.cos(Math.toRadians(angle)));
        if (getSpeed(tempX, tempY) > absMaxSpeed){
            tempX = -absMaxSpeed * Math.sin(Math.toRadians(angle));
            tempY = absMaxSpeed * Math.cos(Math.toRadians(angle));
        }
        xSpeed = tempX;
        ySpeed = tempY;
        updateSpeed();
    }
    
    public void decel(){
        double tempX = xSpeed + (speedInc*Math.sin(Math.toRadians(angle)));
        double tempY = ySpeed - (speedInc*Math.cos(Math.toRadians(angle)));
        if (getSpeed(tempX, tempY) > absMaxSpeed){
            tempX =  absMaxSpeed * Math.sin(Math.toRadians(angle));
            tempY = -absMaxSpeed * Math.cos(Math.toRadians(angle));
        }
        xSpeed = tempX;
        ySpeed = tempY;
        updateSpeed();
    }
    
    public void stop(){
        xSpeed = 0;
        ySpeed = 0;
        updateSpeed();
    }
    
    public double getSpeed(){
        return speed;
    }
    
    public int getGhostCountdown(){
        return ghostFrameCount;
    }
    
    public double[] getBaseXs(){
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }
    
    public void startupGhost(){
        ghost = true;
        ghostFrameCount = framesPerGhost;
    }
    
    public void activateGhost(){
        ghost = true;
        ghostFrameCount += framesPerGhost;
    }
    
    public boolean ghost(){
        return ghost;
    }
    
    public void move(){
        if (ghost){
            ghostFrameCount--;
            if (ghostFrameCount <= 0)
                ghost = false;
        }
        translate();
//         render();
        wrap();
        if (flame != null && accel_decel){
            flame.set(xPos, yPos, angle);
            if (Math.random() <= flameProbability)
                flame.draw();
        }
        draw();
        if (targeted){
            drawTarget();
            targeted = false;
        }
    }
    
    public void rotate(int direction){
        angle = correctAngle(angle-(angleInc * direction));
        render();
        draw();
    }
    
    public Color fillColor(){
        return ghost?ghostFillColor:fillColor;
    }
    
    public Color drawColor(){
        return ghost?ghostDrawColor:drawColor;
    }
    
    public void setShape(double[][] shape){
        homeShape = new Polygon2DDouble(shape[0], shape[1], Math.min(shape[0].length, shape[1].length));
        render();
    }
}