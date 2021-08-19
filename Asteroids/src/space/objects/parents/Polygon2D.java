package space.objects.parents;
import java.awt.*;
import java.awt.geom.*;
/**
 * Write a description of class Polygon2D_Float here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Polygon2D implements Shape
{
    protected GeneralPath path;
    
    public Polygon2D()
    {
       path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    }
    protected abstract void constructPath();
    /**Call this method after you are done with direct manipulation of the x and y points (including translate) and if you plan
     * to use any of the contains, intersects, or get bounds methods right after the
     * manipulation.  In effect this method 'updates' the polygon to its position and shape.  The polygon is always updated
     * just before drawing, so if you use any of the contains, intersects, or get bounds method
     * right after drawing and before manipulation, then these methods return accurate results.
     * Its in the period between manipulation and drawing that this method should be called if you
     * plan to use the other methods (excluding translate) before drawing.  */
    public void invalidate(){
        constructPath();
    }
    public boolean contains(double x, double y) 
        {return path.contains(x, y);} 
    public boolean contains(double x, double y, double w, double h) 
        {return path.contains(x,y,w,h);}
    public boolean contains(Point2D p)
        {return path.contains(p);}
    public boolean contains(Rectangle2D r)
        {return path.contains(r);}
    public Rectangle getBounds()
        {return path.getBounds();}
    public Rectangle2D getBounds2D()
        {return path.getBounds2D();}
    public PathIterator getPathIterator(AffineTransform at)
        {   constructPath();
            return path.getPathIterator(at);}
    public PathIterator getPathIterator(AffineTransform at, double flatness)
        {   constructPath();
            return path.getPathIterator(at, flatness);}
    public boolean intersects(double x, double y, double w, double h)
        {return path.intersects(x, y, w, h);}
    public boolean intersects(Rectangle2D r)
        {return path.intersects(r);}
}