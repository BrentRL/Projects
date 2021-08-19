package space.objects.parents;

import java.awt.*;
//import java.awt.geom.*;

public class Polygon2DDouble extends Polygon2D implements Cloneable{
    public double xpoints[];
    public double ypoints[];
    public int npoints;
    
    public Polygon2DDouble(){
        xpoints = new double[0];
        ypoints = new double[0];
        npoints = 0;
    }
    
    public Polygon2DDouble(double[] x, double[] y, int npoints){
        if(npoints < 0) throw new NegativeArraySizeException("negative amount of sides (npoints < 0)");
        if(npoints > x.length || npoints > y.length) throw new IndexOutOfBoundsException("more sides than points");
        if(x == null || y == null) throw new NullPointerException("null array of points");
        
        this.xpoints = x;
        this.ypoints = y;
        this.npoints = npoints;
        constructPath();
    }
    
    public Polygon2DDouble(int[] x, int[] y, int npoints){
        if(npoints < 0) throw new NegativeArraySizeException("negative amount of sides (npoints < 0)");
        if(npoints > x.length || npoints > y.length) throw new IndexOutOfBoundsException("more sides than points");
        if(x == null || y == null) throw new NullPointerException("null array of points");
        
        xpoints = new double[x.length];
        for(int index = 0; index < x.length; index++) xpoints[index] = x[index];
        ypoints = new double[y.length];
        for(int index = 0; index < y.length; index++) ypoints[index] = y[index];
        this.npoints = npoints;
        constructPath();            
   }
        
     protected synchronized void constructPath(){
        if(npoints < 0) throw new NegativeArraySizeException("negative amound of sides (nPoints < 0)");
        path.reset();
        if(xpoints.length == 0 || ypoints.length == 0) return;
        path.moveTo((float)xpoints[0], (float)ypoints[0]);
        for(int index = 0; index < npoints; index++)
            path.lineTo((float)xpoints[index],(float)ypoints[index]);
        path.closePath();
    }
    
    public synchronized void addPoint(double x, double y){
        if(xpoints.length == npoints){
            double[] temp = new double[xpoints.length+1];
            for(int index = 0; index < xpoints.length; index++) temp[index] = xpoints[index];
            xpoints = temp;
        }
        if(ypoints.length == npoints){
            double[] temp = new double[ypoints.length+1];
            for(int index = 0; index < ypoints.length; index++) temp[index] = ypoints[index];
            ypoints = temp;
        }
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
    }
    
    public synchronized void reset(){
        xpoints = new double[0];
        ypoints = new double[0];
        npoints = 0;
    }
    
     /**Translates the polygon by the specified x and y amounts.*/
    public void translate(double deltaX, double deltaY){
        for(int index = 0; index < xpoints.length; index++) xpoints[index] += deltaX;
        for(int index = 0; index < ypoints.length; index++) ypoints[index] += deltaY;
    }
    
    public void rotate(double deltaAngle, double pivotX, double pivotY){
        for(int index = 0; index < npoints; index++)
            xpoints[index] = (double) (Point.distance(pivotX, pivotY, xpoints[index], ypoints[index]) *
                             -Math.sin(deltaAngle) + (xpoints[index]));
        for(int index = 0; index < npoints; index++)
            ypoints[index] = (double) (Point.distance(pivotX, pivotY, xpoints[index], ypoints[index]) *
                              Math.cos(deltaAngle) + (ypoints[index]));
//         for(int index = 0; index < npoints; index++)
//             xpoints[index] = (double) (Point.distance(pivotX, pivotY, xpoints[index], ypoints[index]) *
//                              Math.cos(deltaAngle) + (xpoints[index]));
//         for(int index = 0; index < npoints; index++)
//             ypoints[index] = (double) (Point.distance(pivotX, pivotY, xpoints[index], ypoints[index]) *
//                               -Math.sin(deltaAngle) + (ypoints[index]));
    }
    
    public double getXMid(){
        if (npoints == 0)
            return -1;
        double min = xpoints[0];
        double max = xpoints[0];
        for (int i=1; i<npoints; i++)
            if (xpoints[i] > max)
                max = xpoints[i];
            else if (xpoints[i] < min)
                min = xpoints[i];
        return (min+max)/2.0;
    }
    
    public double getYMid(){
        if (npoints == 0)
            return -1;
        double min = ypoints[0];
        double max = ypoints[0];
        for (int i=1; i<npoints; i++)
            if (ypoints[i] > max)
                max = ypoints[i];
            else if (ypoints[i] < min)
                min = ypoints[i];
        return (min+max)/2.0;
    }
    
    public Object clone(){
        return new Polygon2DDouble(xpoints, ypoints, npoints);
    }
}