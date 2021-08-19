package space.objects.parents;

import java.awt.*;
import java.awt.geom.*;
import display.CanvasMax;
import space.manipulators.Lineset;
import tools.StaticTools;
import space.objects.EnemyBullet;

public abstract class SpaceObject extends Polygon2DDouble
{
    public static boolean fill = true, draw = true;
    private static int[] size;

    protected Polygon2DDouble homeShape;
    protected CanvasMax canvas;
    protected boolean isActive, useCounter, targeted;
    protected double xSpeed = 0.0, ySpeed = 0.0, speed, xPos, yPos, angle = 0;
    protected int id, lifeCounter;
    protected AffineTransform at = new AffineTransform();
    
    //---------------------------- Constructors
    
    public SpaceObject(Object canvas, double xPos, double yPos){
        init((CanvasMax)canvas, xPos, yPos, getBaseXs(), getBaseYs());
    }
    
    public SpaceObject(Object canvas, double xPos, double yPos, double[] xPoints, double[] yPoints){
        init((CanvasMax)canvas, xPos, yPos, xPoints, yPoints);
    }
    
    //---------------------------- Modifier Methods
    
    public void revamp(double deltaX, double deltaY, double xLoc, double yLoc, double newAngle, boolean move){
        isActive = true;
        id = newID();
        xSpeed = deltaX;
        ySpeed = deltaY;
        updateSpeed();
        xPos = xLoc;
        yPos = yLoc;
        angle = newAngle;
        render();
        if (move)
            draw();
    }
    
    public void setAngle(double newAngle, boolean erase, boolean draw){
        if (erase)
            erase();
        reset();
        angle = newAngle;
        double[] array = new double[6];
        at.setToIdentity();
        at.setToRotation(Math.toRadians(angle), 0, 0);
        
//         for (int i=0; i<homeShape.npoints; i++)
//             addPoint(homeShape.xpoints[i]+xPos, homeShape.ypoints[i]+yPos);
//         rotate(angle, xPos, yPos);
        
        PathIterator it = homeShape.getPathIterator(at);
        while (!it.isDone()){
            it.currentSegment(array);
            addPoint(array[0]+xPos, array[1]+yPos);
            it.next();
        }

//         double angle = Math.toRadians(this.angle);
//         for (int i=0; i<homeShape.npoints; i++)
//             addPoint((int) Math.round(homeShape.xpoints[i] * Math.sin(angle) + homeShape.ypoints[i] * Math.cos(angle)) + (int) Math.round(xPos),
//                      (int) Math.round(homeShape.ypoints[i] * Math.sin(angle) - homeShape.xpoints[i] * Math.cos(angle)) + (int) Math.round(yPos));
        if (draw)
            draw();
    }
    
    public void drawTarget(){
        Color c = new Color(255,176,0);
        int x = (int)Math.round(xPos);//(int)Math.round(getXMid());
        int y = (int)Math.round(yPos);//(int)Math.round(getYMid());
        
        if (!(this instanceof EnemyBullet)){
            canvas.drawLine(c, x-5, y, x+5, y);
            canvas.drawLine(c, x, y-5, x, y+5);
        }
        
        canvas.drawLine(c, x-15, y-15, x-5, y-15);
        canvas.drawLine(c, x-15, y-15, x-15, y-5);
        
        canvas.drawLine(c, x-15, y+15, x-5, y+15);
        canvas.drawLine(c, x-15, y+15, x-15, y+5);
        
        canvas.drawLine(c, x+15, y-15, x+5, y-15);
        canvas.drawLine(c, x+15, y-15, x+15, y-5);
        
        canvas.drawLine(c, x+15, y+15, x+5, y+15);
        canvas.drawLine(c, x+15, y+15, x+15, y+5);
    }
    
    public void setLocation(double x, double y){
        xPos = x;
        yPos = y;
        render();
    }
    
    /**
     * For specific use when drawing in layers.
     * Usually use drawTarget directly from the chasing SpaceObject.
     */
    public void setTargeted(boolean t){
        targeted = t;
    }
    
    public void wrap(){
        while(!inBounds()){
            if (xPos < 0)
                xPos += size[0];
            else if (xPos > size[0])
                xPos -= size[0];
                
            if (yPos < 0)
                yPos += size[1];
            else if (yPos > size[1])
                yPos -= size[1];
        }
        
        render();
    }
    
    private void init(CanvasMax c, double x, double y, double[] xP, double[] yP){
        homeShape = new Polygon2DDouble(xP, yP, Math.min(xP.length, yP.length));
        canvas = c;
        size = canvas.getSize();
        render();
        isActive = false;
        xPos = x;
        yPos = y;
        id = newID();
        constructPath();
    }
    
    public void setLife(int life){
        if (life > 0){
            useCounter = true;
            lifeCounter = life;
        }
        else
            useCounter = false;
    }
    
    public void setPoints(int[] xP, int[] yP){
        homeShape = new Polygon2DDouble(xP, yP, Math.min(xP.length, yP.length));
        render();
        draw();
    }
    
    public void erase(){
        canvas.erase(this, fill, draw);
    }
    
    public void draw(){
        if (fill) canvas.shape(this, fillColor(), true, false);
        if (draw) canvas.shape(this, drawColor(), false, true);
    }
    
    public void setSpeed(double xVector, double yVector){
        xSpeed = xVector;
        ySpeed = yVector;
        updateSpeed();
    }
    
    protected void incLife(){
        if (useCounter)
            if (--lifeCounter <= 0)
                isActive = false;
    }
    
    public void translate(){
        xPos += xSpeed;
        yPos += ySpeed;
        render();
    }
    
    public void updateSpeed(){
        speed = Math.sqrt( Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2) );
    }
    
    public void setActive(boolean active){
        isActive = active;
    }
    
    public void render(){
        setAngle(angle, false, false);
    }
    
    //---------------------------- Data Access Methods
    
    public boolean equals(SpaceObject so){
        boolean match = id == so.id;
        if (match && so.homeShape.npoints == npoints){
            boolean stillMatch = true;
            for (int i=0; i<npoints; i++)
                stillMatch &= so.homeShape.xpoints[i] == homeShape.xpoints[i] && so.homeShape.ypoints[i] == homeShape.ypoints[i];
            match &= stillMatch;
        }
        
        if (match){
            match &= so.xSpeed == xSpeed && so.ySpeed == ySpeed;
            match &= so.xPos == xPos && so.yPos == yPos;
            match &= so.angle == angle && so.isActive == isActive;
        }
        
        return match;
    }
    
    public boolean makesContactWith(SpaceObject so){
        boolean connects = false;
        for (int i=0; i<npoints && !connects; i++)
            if (so.contains(xpoints[i], ypoints[i]))
                connects = true;
        
        if (!connects){
            for (int i=0; i<so.npoints && !connects; i++)
                if (contains(so.xpoints[i], so.ypoints[i]))
                    connects = true;
        }
        return connects;
    }
    
    public static double correctAngle(double a){
        while(a >= 360)
            a -= 360;
        while(a < 0)
            a += 360;
        return a;
    }
    
    public boolean isOffScreen(){
        boolean offScreen = true;
        for (int i=0; i<npoints; i++)
            offScreen &= isOffScreen(xpoints[i], ypoints[i]);
        return offScreen;
    }
    
    public boolean inBounds(){
        return xPos >= 0 && xPos <= size[0] && yPos >= 0 && yPos <= size[1];
    }
    
    public static double getSpeed(double xspeed, double yspeed){
        return Math.sqrt( Math.pow(xspeed, 2) + Math.pow(yspeed, 2) );
    }
    
    public boolean isOffScreen(double x, double y){
        return (x < 0 || x > size[0] || y < 0 || y > size[1]);
    }
    
    public double[] getPosition(){
        return new double[] {xPos, yPos};
    }
    
    public CanvasMax getCanvas(){
        return canvas;
    }
    
    public double getSpeed(){
        return speed;
    }
    
    public double getAngle(){
        return angle;
    }
    
    public boolean active(){
        return isActive;
    }
    
    public int getID(){
        return id;
    }
    
    public Lineset getLineComposition(){
        render();
        double[][] line = new double[][] { {xpoints[0], ypoints[0]}, {0, 0} };
        Lineset ls = new Lineset();
        for (int i=1; i<npoints; i++){
            line[1][0] = xpoints[i];
            line[1][1] = ypoints[i];
            ls.add(line);
            line[0][0] = line[1][0];
            line[0][1] = line[1][1];
        }
        ls.add(new double[][] {{xpoints[0], ypoints[0]}, {xpoints[npoints-1], ypoints[npoints-1]}});
        ls.createIterator();
        ls.setColor(drawColor());
        return ls;
    }
    
    //---------------------------- Abstract Methods
    
    public abstract double[] getBaseXs();
    public abstract double[] getBaseYs();
    public abstract void move();
    public abstract Color drawColor();
    public abstract Color fillColor();
    
    //---------------------------- Other
    
    private static int newID(){
        return StaticTools.rnd.nextInt(Integer.MAX_VALUE-1);
    }
    
    public void reHomeShape(){
        double[] xp = getBaseXs();
        double[] yp = getBaseYs();
        homeShape = new Polygon2DDouble(xp, yp, Math.min(xp.length, yp.length));
    }
    
    public void printHomeShape(){
        String x = "{";
        String y = "{";
        for (int i=0; i<homeShape.npoints; i++){
            if (i != 0){
                x += ", ";
                y += ", ";
            }
            x += homeShape.xpoints[i];
            y += homeShape.ypoints[i];
        }
        x += "}";
        y += "}";
        
        System.out.println(x + "\n" + y);
    }
    
    protected double reorient(double a){
        a = correctAngle(a);
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
    
    public void print(){
        String x = "{";
        String y = "{";
        for (int i=0; i<npoints; i++){
            if (i != 0){
                x += ", ";
                y += ", ";
            }
            x += xpoints[i];
            y += ypoints[i];
        }
        x += "}";
        y += "}";
        
        System.out.println(x + "\n" + y);
    }
    
//     public Polygon copy(){
//         return new Polygon(xpoints, ypoints, npoints);
//     }
}