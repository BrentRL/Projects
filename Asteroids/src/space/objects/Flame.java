package space.objects;
import java.awt.*;

import space.objects.parents.Polygon2DDouble;
import space.objects.parents.SpaceObject;

public class Flame extends SpaceObject
{

    private static final Color fillColor = Color.red;
    private static final Color drawColor = Color.yellow;
    private static final double[] forwardFlameX = new double[] {3  ,   5,   0,  -5,  -3};
    private static final double[] forwardFlameY = new double[] {-13, -20, -35, -20, -13};
    private static final Polygon2DDouble forwardFlame = new Polygon2DDouble(forwardFlameX, forwardFlameY, Math.min(forwardFlameX.length, forwardFlameY.length));
    
    private static final double[] reverseFlameX = new double[] {8,7,5,-5,-7,-8};
    private static final double[] reverseFlameY = new double[] {-5,10,3,3,10,-5};//{-5,10,3,3,10,-5};
    private static final Polygon2DDouble reverseFlame = new Polygon2DDouble(reverseFlameX, reverseFlameY, Math.min(reverseFlameX.length, reverseFlameY.length));
    
    public Flame(Object canvas){
        super(canvas, 0, 0);
    }
    
    public void forward(){
        homeShape = forwardFlame;
    }
    
    public void reverse(){
        homeShape = reverseFlame;
    }
    
    public void set(double x, double y, double theta){
        xPos = x;
        yPos = y;
        angle = theta;
        render();
    }
    
    public void move(){}
    
    public double[] getBaseXs(){
        return forwardFlameX;
    }
    
    public double[] getBaseYs(){
        return forwardFlameY;
    }
    
    public Color drawColor(){
        return drawColor;
    }
    
    public Color fillColor(){
        return fillColor;
    }
}