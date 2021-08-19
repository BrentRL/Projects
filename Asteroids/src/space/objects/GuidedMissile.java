package space.objects;
import java.awt.*;

import space.objects.parents.SpaceObject;

public class GuidedMissile extends Missile
{
    private static final Color fillColor = Color.cyan.darker();
    private static final Color drawColor = fillColor.brighter().brighter();
    protected static final boolean stillWait = false;
    protected double maxAngleInc = 5;
    protected SpaceObject target;
    protected int targetID;

    public GuidedMissile(Object canvas, double x, double y, SpaceObject target){
        super(canvas, x, y);
        this.target = target;
        if (target != null)
            targetID = target.getID();
    }
    
    public void setTarget(SpaceObject so){
        target = so;
        if (so != null)
            targetID = so.getID();
    }
    
    public boolean purse(){
        boolean persue = true;
        if (target == null)
            persue = false;
        else if (target.getID() != targetID)
            persue = false;
        else if (!target.active())
            persue = false;
        return persue;
    }
    
//     public void move(){
//         
//         boolean persue = purse();
//         if (defective)
//             persue = false;
//         if (!persue){
//             target = null;
//             if (!stillWait)
//                 translate();
//             wrap();
//             draw();
//             incLife();
//             return;
//         }
//         
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
//         
//         if (angleDir == compAngle)
//             angleInc = -maxAngleInc;
//     
// //         tempAngle = angle + angleInc;
//         tempAngle = angleDir;
//         tempX = missileSpeed * -Math.sin(Math.toRadians(tempAngle));
//         tempY = missileSpeed *  Math.cos(Math.toRadians(tempAngle));
//         
//         xSpeed = tempX;
//         ySpeed = tempY;
//         angle = tempAngle;
//         updateSpeed();
//         translate();
//         wrap();
//         draw();
//         incLife();
//         target.drawTarget();
//     }

    public void move(){
        boolean persue = purse();
        if (defective)
            persue = false;
        if (!persue){
            target = null;
            if (!stillWait)
                translate();
            render();
            wrap();
            draw();
            incLife();
            return;
        }
        
        angle = correctAngle(angle);
        double[] targetLoc = target.getPosition();
        double xFromMe = targetLoc[0] - xPos,
               yFromMe = Math.min(yPos,targetLoc[1]) - Math.max(yPos,targetLoc[1]);

        double angleDir = correctAngle(Math.toDegrees(Math.atan(xFromMe/yFromMe)));
        if (yPos > targetLoc[1])
            angleDir = reorient(angleDir);
        angleDir = correctAngle(angleDir);
        
//         xFromMe = Math.max(targetLoc[0],xPos) - Math.min(targetLoc[0],xPos);
//         yFromMe = Math.max(yPos,targetLoc[1]) - Math.min(yPos,targetLoc[1]);
// 
//         double angleDir = 0, theta;
//         angle = correctAngle(angle);
        
//         if (xPos > targetLoc[0]){
//             if (yPos > targetLoc[1]){
//                theta = Math.toDegrees(Math.atan(yFromMe/xFromMe));
//                angleDir = 180 - theta;
//             }
//             else if (yPos < targetLoc[1]){
//                 theta = Math.toDegrees(Math.atan(yFromMe/xFromMe));
//                 angleDir = 180 + theta;
//             }
//             else if (yPos == targetLoc[1])
//                 angleDir = 180;
//         }
//         else if (xPos < targetLoc[0]){
//             if (yPos > targetLoc[1]){
//                 theta = Math.toDegrees(Math.atan(yFromMe/xFromMe));
//                 angleDir = theta;
//             }
//             else if (yPos < targetLoc[1]){
//                 theta = Math.toDegrees(Math.atan(yFromMe/xFromMe));
//                 angleDir = 360 - theta;
//             }
//             else if (yPos == targetLoc[1])
//                 angleDir = 0;
//         }
//         else if (xPos == targetLoc[0]){
//             if (yPos > targetLoc[1])
//                 angleDir = 90;
//             else if (yPos < targetLoc[1])
//                 angleDir = 270;
//         }

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
        tempX = missileSpeed * -Math.sin(Math.toRadians(tempAngle));
        tempY = missileSpeed *  Math.cos(Math.toRadians(tempAngle));
        
        xSpeed = tempX;
        ySpeed = tempY;
        angle = tempAngle;
        updateSpeed();
        translate();
        wrap();
        draw();
        incLife();
        target.drawTarget();
    }
    
    public Color fillColor(){
        return fillColor;
    }
    
    public Color drawColor(){
        return drawColor;
    }
}