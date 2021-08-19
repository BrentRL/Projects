package space.objects;

import java.awt.Polygon;
import java.awt.Color;

public class Star extends Polygon
{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 4932330595597372766L;
	
	public static final Color starColor = Color.white;
    private static final int[][] starPoints = new int[][] { {-1, -1, 0, 0},{0, -1, -1, 0}};
    
    public Star(int x, int y)
    {
        super(starPoints[0], starPoints[1], starPoints[0].length);
        translate(x, y);
    }
    
}