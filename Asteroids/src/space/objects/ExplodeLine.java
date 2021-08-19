package space.objects;

import java.awt.geom.*;
import display.CanvasMax;
import space.objects.parents.SpaceObject;

import java.awt.*;
import tools.StaticTools;

public class ExplodeLine extends Line2D.Double
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3362589166441037794L;
	
	public static final int life = 40;
    private static final double minAngleInc = 5, maxAngleInc = 10;
    private static final double maxSpeed = 5;
    private static final boolean wrap = false, fling = true;

    protected double[] rotationPoint;
    protected double radius, xSpeed, ySpeed, angle;
    protected int lifeLeft = life;
    protected double angleInc;
    protected int[] size;
    protected boolean active;
    protected CanvasMax canvas;
    protected Color color;
    
    public ExplodeLine(double[] p1, double[] p2, Object canvas, Color c){
        super(p1[0], p1[1], p2[0], p2[1]);
        this.canvas = (CanvasMax) canvas;
        size = this.canvas.getSize();
        revamp(p1, p2, c);
    }
    
    public void revamp(double[] p1, double[] p2, Color c){
        active = true;
        this.color = c;
        lifeLeft = life;
        setLine(p1[0], p1[1], p2[0], p2[1]);
        rotationPoint = StaticTools.getMidpoint(p1[0], p1[1], p2[0], p2[1]);
        radius = StaticTools.getDistance(p1, p2) / 2.0;
        xSpeed = StaticTools.rnd.nextDouble()*maxSpeed*StaticTools.randomPol();
        ySpeed = StaticTools.rnd.nextDouble()*maxSpeed*StaticTools.randomPol();
        angleInc = StaticTools.nextDouble(minAngleInc, maxAngleInc)*StaticTools.randomPol();
        initAttributes();
    }
    
    private void initAttributes(){
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        angle = reorient(Math.toDegrees(Math.atan(deltaX/deltaY)));//StaticTools.rnd.nextInt(360);
        double p2Angle = SpaceObject.correctAngle(angle+180);
        setLine(-radius*Math.sin(Math.toRadians(angle))+rotationPoint[0] , radius*Math.cos(Math.toRadians(angle))+rotationPoint[1] , -radius*Math.sin(Math.toRadians(p2Angle))+rotationPoint[0] , radius*Math.cos(Math.toRadians(p2Angle))+rotationPoint[1]);
    }
    
    private double reorient(double a){
        a = SpaceObject.correctAngle(a);
        if (a == 0 || a == 360)
            return 270;
        else if (a == 180)
            return 90;
        else if (a > 0 && a < 180){
            double b = 180 - a;
            return (180 + b) + 180;
        }
        else if (a > 180 && a < 360){
            double b = a - 180;
            return (180 - b) + 180;
        }
        return a;
    }
    
    public void draw(){
        canvas.shape(this, color, false, true);
    }
    
    public void erase(){
        canvas.erase(this, false, true);
    }
    
    public void move(){
        if (fling){
            angle = SpaceObject.correctAngle(angle+angleInc);
            rotationPoint[0] += xSpeed;
            rotationPoint[1] += ySpeed;
        }
        double[] newP1 = new double[2], newP2 = new double[2];
        newP1[0] = (-radius*Math.sin(Math.toRadians(angle)));
        newP1[1] = (radius*Math.cos(Math.toRadians(angle)));
        newP2[0] = -newP1[0];
        newP2[1] = -newP1[1];
        
        newP1[0] += rotationPoint[0];
        newP1[1] += rotationPoint[1];
        newP2[0] += rotationPoint[0];
        newP2[1] += rotationPoint[1];
        
        setLine(newP1[0], newP1[1], newP2[0], newP2[1]);
        draw();
        int r = color.getRed() - 7, g = color.getGreen() - 7, b = color.getBlue() - 7;
        color = new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
        if (color.equals(Color.black))
            active = false;
        lifeLeft--;
        if (lifeLeft <= 0)
            active = false;
        if (wrap)
            wrap();
        else if (!inBounds())
            active = false;
    }
    
    public boolean active(){
        return active;
    }
    
//    private double getLength(){
//        return StaticTools.getDistance(x1, y1, x2, y2);
//    }
    
    public boolean equals(ExplodeLine el){
        boolean match = getP1().equals(el.getP1()) && getP2().equals(el.getP2());
        if (match){
            match &= xSpeed == el.xSpeed && ySpeed == el.ySpeed;
            match &= active == el.active;
            match &= lifeLeft == el.lifeLeft && radius == el.radius;
            match &= color.equals(el.color);
        }
        else
            return false;
        return match;
    }
    
    public void wrap(){
        double x_1 = x1, y_1 = y1, x_2 = x2, y_2 = y2;
        if (rotationPoint[0] < 0){
            rotationPoint[0] += size[0];
            x_1 += size[0];
            x_2 += size[0];
        }
        else if (rotationPoint[0] > size[0]){
            rotationPoint[0] -= size[0];
            x_1 -= size[0];
            x_2 -= size[0];
        }
        if (rotationPoint[1] < 0){
            rotationPoint[1] += size[1];
            y_1 += size[1];
            y_2 += size[1];
        }
        else if (rotationPoint[1] > size[1]){
            rotationPoint[1] -= size[1];
            y_1 -= size[1];
            y_2 -= size[1];
        }
        setLine(x_1, y_1, x_2, y_2);
    }
    
    public boolean inBounds(){
        return rotationPoint[0] >= 0 && rotationPoint[0] <= size[0] && rotationPoint[1] >= 0 && rotationPoint[1] <= size[1];
    }
}