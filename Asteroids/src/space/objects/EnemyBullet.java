package space.objects;
import java.awt.Color;

public class EnemyBullet extends Bullet
{
    private static final Color fillColor = new Color(249, 105, 0);
    private static final Color drawColor = fillColor.darker().darker();
    private static final double[] yP= new double[] {0,2,5,0,0,-2,-5,0};
    private static final double[] xP = new double[] {-5,0,0,2,5,0,0,-2};
    
    public EnemyBullet(Object canvas, double x, double y){
        super(canvas, x, y);
    }
    
    public double[] getBaseXs(){
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }
    
    public void move(){
        angle += angleInc;
        super.move();
    }
    
    public Color fillColor(){
        return fillColor;
    }
    
    public Color drawColor(){
        return drawColor;
    }
}