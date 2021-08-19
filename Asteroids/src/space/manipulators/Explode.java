package space.manipulators;
import java.util.ArrayList;

import space.objects.ExplodeLine;
import space.objects.parents.SpaceObject;
import tools.StaticTools;

public class Explode
{
    //private int capacity;
    private int capacityIncrement = 20;
    private int maxCapacity = 150;
    public static boolean useCapacityLimit = false;
    
    private int size = 0;
    private int nextIndex = 0;
    private ExplodeLine[] list;
    private Object canvas;
    private Explode inactive;
    private ArrayList<ExplodeLine> al;
    
    public Explode(Object canvas){
        this.canvas = canvas;
        list = new ExplodeLine[10];
        inactive = new Explode();
        al = new ArrayList<ExplodeLine>();
    }
    
    private Explode(){
        list = new ExplodeLine[10];
    }
    
    public void explode(SpaceObject so){
        Lineset lines = so.getLineComposition();
        java.awt.Color color = lines.getColor();
        while(lines.hasNext()){
            double[][] linePoints = lines.next();
            ExplodeLine el = inactive.getInactiveObject();
            if (el == null)
                el = new ExplodeLine(linePoints[0], linePoints[1], canvas, color);
            else
                el.revamp(linePoints[0], linePoints[1], color);
            add(el);
            if (useCapacityLimit && activeSize() > maxCapacity)
                removeFirstActive();
        }
    }
    
    public void explode(SpaceObjectList<SpaceObject> sos){
        int size = sos.size();
        for (int i=0; i<size; i++)
            explode(sos.get(i));
    }
    
    private void removeFirstActive(){
        for (int i=0; i<size; i++){
            if (list[i].active()){
                remove(i);
                return;
            }
        }
    }
    
    public void moveAll(){
        al.clear();
        for (int i=0; i<size; i++)
            if (list[i].active())
                list[i].move();
            else
                al.add(list[i]);
        inactive.add(remove(al));
    }
    
    public void add(ExplodeLine so){
        if (list.length <= nextIndex)
            incCapacity();
        list[nextIndex] = so;
        size++;
        nextIndex++;
    }
    
    public ExplodeLine get(int index){
        return list[index];
    }
    
    public ExplodeLine randomExplodeLine(){
        ExplodeLine[] l = new ExplodeLine[size];
        int count = 0;
        for (int i=0; i<size; i++)
            if (list[i].active()){
                l[count] = list[i];
                count++;
            }
        if (count == 0)
            return null;
        return l[StaticTools.rnd.nextInt(count)];
    }
    
    public void replaceInactive(ExplodeLine so){
        boolean replace = false;
        for (int i=0; i<size; i++)
            if (!list[i].active()){
                list[i] = so;
                replace = true;
            }
        if (!replace)
            add(so);
    }
    
    public void add(ArrayList<ExplodeLine> items){
        int size = items.size();
        for (int i=0; i<size; i++)
            add((ExplodeLine)items.get(i));
    }
    
    public void add(ExplodeLine[] items){
        for (int i=0; i<items.length; i++)
            add(items[i]);
    }
    
    public ExplodeLine[] toArray(){
        ExplodeLine[] so = new ExplodeLine[size];
        for (int i=0; i<size; i++)
            so[i] = list[i];
        return so;
    }
    
    public ArrayList<ExplodeLine> remove(ArrayList<ExplodeLine> a){
        if (a.size() == 0)
            return a;
        for (int i=0; i<size; i++){
            if (list[i] != null){
                if (a.contains(list[i]))
                    list[i] = null;
            }
            else
                i=size;
        }
        compress();
        return a;
    }
        
    
    public ExplodeLine remove(int index){
        ExplodeLine so = list[index];
        list[index] = null;
        size--;
        compress();
        return so;
    }
    
    public void remove(ExplodeLine so){
        int index = getIndexOf(so);
        if (index != -1)
            remove(index);
    }
    
    public int getIndexOf(ExplodeLine so){
        for (int i=0; i<size; i++)
            if (list[i] != null)
                if (list[i].equals(so))
                    return i;
        return -1;
    }
    
    private void incCapacity(){
        ExplodeLine[] list2 = new ExplodeLine[list.length+capacityIncrement];
        for (int i=0; i<size; i++)
            list2[i] = list[i];
        list = list2;
        //capacity += capacityIncrement;
    }
    
    public int size(){
        return size;
    }
    
    public int activeSize(){
        int count = 0;
        for (int i=0; i<size; i++)
            if (list[i].active())
                count++;
        return count;
    }
    
    public ExplodeLine getInactiveObject(){
        ExplodeLine el = null;
        for (int i=0; i<size; i++)
            if (!list[i].active()){
                el = list[i];
                break;
            }
        if (el != null)
            compress();
        return el;
    }
    
    public void clearInactive(){
        boolean compress = false;
        for (int i=0; i<size; i++)
            if (!list[i].active()){
                list[i] = null;
                compress = true;
            }
        if (compress)
            compress();
    }
    
    private void compress(){
        ExplodeLine[] list2 = new ExplodeLine[list.length];
        int index = 0;
        size = 0;
        for (int i=0; i<list.length; i++){
            if (list[i] != null){
                list2[index] = list[i];
                size++;
                index++;
            }
        }
        nextIndex = size;
        list = list2;
    }
    
    public void clear(){
        for (int i=0; i<size; i++)
            list[i] = null;
        size = 0;
        nextIndex = 0;
    }
    
    public boolean haveActiveObjects(){
        for (int i=0; i<size; i++)
            if (list[i].active())
                return true;
        return false;
    }
    
//     private ExplodeLineIterator iterator(){
//         return new ExplodeLineIterator();
//     }
    
//     // Explode.ExplodeLineIterator
//     private class ExplodeLineIterator
//     {
//         private int nextIndex = 0;
//         private ExplodeLine[] objects;
//         
//         public ExplodeLineIterator(){
//             objects = list;
//         }
//         
//         public ExplodeLine next(){
//             ExplodeLine item = objects[nextIndex];
//             nextIndex++;
//             return item;
//         }
//         
//         public boolean hasNext(){
//             return size > nextIndex;
//         }
//     }
}