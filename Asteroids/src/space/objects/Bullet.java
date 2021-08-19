package space.objects;
import java.awt.*;

import space.objects.parents.SpaceObject;

public class Bullet extends SpaceObject
{
    public static final double speed = 9;
    private static final double[] yP= new double[] {-2, 0, 2};//{0,2,5,0,0,-2,-5,0};
    //private static final double[] yP= new double[] {0,2,5,0,0,-2,-5,0};
    private static final double[] xP = new double[] {-1, 0, -1};//{-5,0,0,2,5,0,0,-2};
    //private static final double[] xP = new double[] {-5,0,0,2,5,0,0,-2};
    private static final Color drawColor = Color.yellow, fillColor = Color.blue;
    public  static final boolean wrap = false;
    public static double maxRadius = 10;
    protected static final double angleInc = 20;

    public Bullet(Object canvas, double x, double y){
        super(canvas, x, y);
    }
    
    public double[] getBaseXs(){
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }
    
    public void move(){
//         angle += angleInc;
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