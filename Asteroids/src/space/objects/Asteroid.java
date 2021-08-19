package space.objects;

import java.awt.*;

import space.objects.parents.SpaceObject;
import tools.StaticTools;

public class Asteroid extends SpaceObject
{
    private static final int minSides = 5; // inclusive
    private static final int maxSides = 9; // exclusive
    private static final int minRotation = 1;
    private static final int maxRotation = 5;
    private static final int minRadius = 10;
    public static final int maxRadius = 40;
    private static final double minSpeed = 1;
    private static final double maxSpeed = 6;
    private static final Color fillColor = Color.gray;
    private static final Color drawColor = Color.lightGray;
    private static final int maxGerenation = 3;
    private static final int minKids = 2;
    private static final int maxKids = 3;
    private final boolean infinite = false;
    
    private double rotation, xP[], yP[];
    protected int numSides, generation;
    
    public Asteroid(Object canvas, double x, double y){
        super(canvas, x, y);
        revamp2();
    }
    
    public void revamp2(){
        generation = 1;
        rotation = StaticTools.nextInt(minRotation, maxRotation) * StaticTools.randomPol();
        initSpeed();
    }
    
    private void initSpeed(){
        double speed = StaticTools.nextDouble(minSpeed, maxSpeed);//(StaticTools.rnd.nextDouble()*(maxSpeed-minSpeed)) + minSpeed;
        int angle = StaticTools.rnd.nextInt(360);
        double xSpeed = -speed*Math.sin(Math.toRadians(angle));
        double ySpeed = speed*Math.cos(Math.toRadians(angle));
        setSpeed(xSpeed, ySpeed);
    }
    
    protected void setGen(int gen){
        generation = gen;
    }
    
    public int getGen(){
        return generation;
    }
    
    public void move(){
        translate();
        angle += rotation;
        angle = correctAngle(angle);
        wrap();
        draw();
    }
    
    private int randRadius(){
        return StaticTools.nextInt(minRadius, maxRadius);
    }
    
    public double[] getBaseXs(){
        generation = 1;
        numSides = StaticTools.nextInt(minSides, maxSides);
        double deltaAngle = 360 / numSides;
        xP = new double[numSides];
        yP = new double[numSides];
        for (int i=0; i<numSides; i++){
            double angle = i*deltaAngle;
            int radius = randRadius();
            xP[i] = -radius * Math.cos( Math.toRadians( angle ) ) ;
            yP[i] = radius * Math.sin( Math.toRadians( angle ) ) ;
        }
        return xP;
    }
    
    public double[] getBaseYs(){
        return yP;
    }
    
    public Color fillColor(){
        return fillColor;
    }
    
    public Color drawColor(){
        return drawColor;
    }
    
    public Asteroid[] explode(){
        setActive(false);
        if (!infinite)
            if (generation >= maxGerenation)
                return new Asteroid[0];
        Asteroid[] as = new Asteroid[StaticTools.nextInt(minKids, maxKids)];
        double scaleFactor = 1.25/(generation*2);
//         double scaleFactor = 1;
//         for (int i=1; i<=generation; i++)
//             scaleFactor *= .75;
        for (int i=0; i<as.length; i++){
            as[i] = new Asteroid(canvas, xPos, yPos);
            as[i].setGen(generation+1);
            scale(as[i], scaleFactor);
            as[i].setActive(true);
        }
        return as;
    }
    
    public static void scale(Asteroid a, double scale){
        for (int i=0; i<a.homeShape.npoints; i++){
            a.homeShape.xpoints[i] = a.homeShape.xpoints[i]*scale;
            a.homeShape.ypoints[i] = a.homeShape.ypoints[i]*scale;
        }
        a.render();
    }
}