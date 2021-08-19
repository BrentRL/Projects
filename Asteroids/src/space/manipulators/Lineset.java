package space.manipulators;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

public class Lineset
{
    private ArrayList<double[][]> lines = new ArrayList<double[][]>();
    private Iterator<double[][]> it;
    private boolean canAdd = true;
    private Color color;
    
    public void add(double[][] line){
        if (canAdd)
            lines.add(new double[][]{ {line[0][0] , line[0][1]} , {line[1][0] , line[1][1]} });
    }
    
    public void setColor(Color c){
        color = c;
    }
    
    public void createIterator(){
        it = lines.iterator();
        canAdd = false;
    }
    
    public boolean hasNext(){
        if (it != null)
            return it.hasNext();
        return false;
    }
    
    public double[][] next(){
        return (double[][]) it.next();
    }
    
    public Color getColor(){
        return color;
    }
}