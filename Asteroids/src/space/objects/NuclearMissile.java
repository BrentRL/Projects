package space.objects;

import java.awt.Color;

import display.CanvasMax;


public class NuclearMissile extends Missile
{
    private static final Color fill_one = Color.white,//new Color(155,227,225);
                               fill_two = Color.red,//new Color(99,191,60);
                               intermediateColors_one[] = CanvasMax.transist(fill_one, fill_two),
                               intermediateColors_two[] = CanvasMax.transist(fill_two, fill_one);
    private static final int colorChangeCountdown = 50;
    public static final int reach = 400;
    
    private int count = colorChangeCountdown;
    private Color currentFillColor = fill_one;
    private Color currentDrawColor = currentFillColor.brighter().brighter();
    private boolean colorstate_1 = true, colorstate_2, colortranse_1, colortranse_2;

    public NuclearMissile(Object canvas, double x, double y){
        super(canvas, x, y);
    }
    
    public void inc(){
        count--;
//         if (count <= 0){
//             if (currentFillColor.equals(fill_one))
//                 currentFillColor = fill_two;
//             else
//                 currentFillColor = fill_one;
//             currentDrawColor = currentFillColor.brighter().brighter();
//             count = colorChangeCountdown;
//         }
        if (count <= 0){
            if (colorstate_1){
                colortranse_1 = true;
                colorstate_1 = colorstate_2 = colortranse_2 = false;
            }
            else if (colorstate_2){
                colortranse_2 = true;
                colorstate_1 = colorstate_2 = colortranse_1 = false;
            }
            else if (colortranse_1){
                colorstate_2 = true;
                colorstate_1 = colortranse_1 = colortranse_2 = false;
                currentFillColor = fill_two;
            }
            else if (colortranse_2){
                colorstate_1 = true;
                colorstate_2 = colortranse_1 = colortranse_2 = false;
                currentFillColor = fill_one;
            }
            currentDrawColor = currentFillColor.brighter().brighter();
            count = colorChangeCountdown;
        }
        
        if (colortranse_1){
            int colorIndex = (int) Math.round((intermediateColors_one.length / (colorChangeCountdown+.0)) * (colorChangeCountdown - count));
            colorIndex = Math.min(colorIndex, intermediateColors_one.length-1);
            currentFillColor = intermediateColors_one[colorIndex];
            currentDrawColor = currentFillColor.brighter().brighter();
        }
        else if (colortranse_2){
            int colorIndex = (int) Math.round((intermediateColors_two.length / (colorChangeCountdown+.0)) * (colorChangeCountdown - count));
            colorIndex = Math.min(colorIndex, intermediateColors_two.length-1);
            currentFillColor = intermediateColors_two[colorIndex];
            currentDrawColor = currentFillColor.brighter().brighter();
        }
    }
    
    public Color fillColor(){
        return currentFillColor;
    }
    
    public Color drawColor(){
        return currentDrawColor;
    }
}