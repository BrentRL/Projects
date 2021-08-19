package space.objects;
import java.awt.Color;

public class EnemyMissile extends Missile
{
    private static final Color fillColor = new Color(249, 105, 0);
    private static final Color drawColor = fillColor.darker().darker();
    
    public EnemyMissile(Object canvas, double x, double y){
        super(canvas, x, y);
    }
    
    public Color fillColor(){
        return fillColor;
    }
    
    public Color drawColor(){
        return drawColor;
    }
}