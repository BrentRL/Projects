package space.objects;
import java.awt.*;

import space.objects.parents.SpaceObject;

public class Mine extends SpaceObject
{
    private static final double[] yP = new double[] {0,-8,0,8};
    private static final double[] xP = new double[] {-2,0,2,0};
    private static final Color drawColor = Color.yellow, fillColor = Color.magenta;
    public static double maxRadius = 10;
    
    private double angleInc = .5;//, inc = .05, maxInc = 3.0;
    
    public Mine(Object canvas, double x, double y){
        super(canvas, x, y);
    }
    
    public void setdefault(){
        angleInc = 0;
        //inc = .5;
    }
    
    public double[] getBaseXs(){
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }
    
    public void move(){
//         angleInc += inc;
//         if (angleInc >= maxInc){
//             inc *= -1;
//             angle = maxInc + inc;
//         }
//         else if (angleInc <= -maxInc){
//             inc *= -1;
//             angleInc = (-maxInc) + inc;
//         }
        angle += angleInc;
        wrap();
        draw();
        incLife();
    }
    
    public Color fillColor(){
        return fillColor;
    }
    
    public Color drawColor(){
        return drawColor;
    }
}