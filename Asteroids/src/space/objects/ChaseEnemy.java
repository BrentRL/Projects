package space.objects;
import java.awt.Color;

import space.objects.parents.SpaceObject;

public class ChaseEnemy extends Enemy
{
    private SpaceObject target;
    private double maxAngleInc = 5;
    private double chaseEnemySpeed = 6.0;//6.87 changeTo 6.89
    private static Color fillColor = new Color(140,19,70);

    public ChaseEnemy(Object canvas, double x, double y, double[][] points, SpaceObject so){
        super(canvas, x, y, points);
        target = so;
    }
    
    public void setTarget(SpaceObject so){
        target = so;
    }
    
    public SpaceObject moveAndFire(){
        move();
        return randomFire();
    }
    
    public void move(){
        if (target == null || !target.active()){
            target = null;
            translate();
            wrap();
            draw();
            return;
        }
        
//         double[] targetLoc = target.getPosition();
//         double turnAngle, xFromMe, yFromMe;
//         
//         xFromMe = targetLoc[0] - xPos;
//         yFromMe = Math.min(yPos,targetLoc[1]) - Math.max(yPos,targetLoc[1]);
//         
//         angle = correctAngle(angle);
//         double angleDir = correctAngle(Math.toDegrees(Math.atan(xFromMe/yFromMe)));
//         if (yPos > targetLoc[1])
//             angleDir = reorient(angleDir);
//         double compAngle = correctAngle(angle+180);
//         double angleInc = 0;
//         double tempX, tempY, tempAngle;
// 
//         if (angle < compAngle){
//             if (angleDir > angle && angleDir < compAngle)
//                 angleInc = Math.min(maxAngleInc, Math.abs(angleDir-angle));
//             else if (angleDir < angle && angleDir > 0)
//                 angleInc = -Math.min(maxAngleInc, Math.abs(angle-angleDir));
//             else if (angleDir > compAngle && angleDir < 360){
//                 double inc = Math.abs(angle + (360 - angleDir));
//                 angleInc = -Math.min(maxAngleInc, inc);
//             }
//         }
//         else if (angle > compAngle){
//             if (angleDir < angle && angleDir > compAngle)
//                 angleInc = -Math.min(maxAngleInc, angle-angleDir);
//             else if (angleDir < compAngle && angleDir > 0){
//                 double inc = compAngle - angleDir;
//                 angleInc = Math.min(maxAngleInc, Math.abs(180-inc));
//             }
//             else if (angleDir < 360 && angleDir > angle)
//                 angleInc = Math.min(maxAngleInc, Math.abs(angleDir-angle));
//         }
//         if (angleDir == compAngle)
//            angleInc = -maxAngleInc;
//     
//         tempAngle = angle + angleInc;
//         
//         tempX = chaseEnemySpeed * -Math.sin(Math.toRadians(tempAngle));
//         tempY = chaseEnemySpeed *  Math.cos(Math.toRadians(tempAngle));
//         
//         xSpeed = tempX;
//         ySpeed = tempY;
//         angle = tempAngle;
//         updateSpeed();
//         translate();
//         wrap();
//         draw();
//         incLife();

        angle = correctAngle(angle);
        double[] targetLoc = target.getPosition();
        double xFromMe = targetLoc[0] - xPos,
               yFromMe = Math.min(yPos,targetLoc[1]) - Math.max(yPos,targetLoc[1]);

        double angleDir = correctAngle(Math.toDegrees(Math.atan(xFromMe/yFromMe)));
        if (yPos > targetLoc[1])
            angleDir = reorient(angleDir);
        angleDir = correctAngle(angleDir);

        double angleInc = 0,
               leftDistance = 0, rightDistance = 0;
               
        
        if (angle > angleDir){
            rightDistance = angle - angleDir;
            leftDistance = (360 - angle) + angleDir;
        }
        else if (angleDir > angle){
            leftDistance = angleDir - angle;
            rightDistance = (360 - angleDir) + angle;
        }   
        
        if (leftDistance < rightDistance)
            angleInc = Math.min(maxAngleInc, leftDistance);
        else if (rightDistance < leftDistance)
            angleInc = -Math.min(maxAngleInc, rightDistance);
        else 
            angleInc = -maxAngleInc;
        
        if (angle == angleDir)
            angleInc = 0;
       if (Math.abs(angleInc) > maxAngleInc)
            System.out.println("we got us a stray angle inc");
    
        double tempX, tempY, tempAngle;
        
        tempAngle = angle + angleInc;
        tempX = chaseEnemySpeed * -Math.sin(Math.toRadians(tempAngle));
        tempY = chaseEnemySpeed *  Math.cos(Math.toRadians(tempAngle));
        
        xSpeed = tempX;
        ySpeed = tempY;
        angle = tempAngle;
        updateSpeed();
        translate();
        wrap();
        draw();
        incLife();
        target.setTargeted(true);
    }
    
    public Color fillColor(){
        return fillColor;
    }
}