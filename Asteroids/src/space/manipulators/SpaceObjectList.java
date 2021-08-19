package space.manipulators;
import java.util.ArrayList;

import space.objects.parents.SpaceObject;
import tools.StaticTools;

@SuppressWarnings("unchecked")
public class SpaceObjectList<T extends SpaceObject> implements Cloneable
{
    private int capacity;
    private int capacityIncrement = 20;
    
    private int size = 0;
    private int nextIndex = 0;
    private T[] list;
    
    //---------------------------- Constructors
    
    public SpaceObjectList(){
        this(10);
    }
    
    public SpaceObjectList(int initialCapacity){
        list = (T[]) new SpaceObject[Math.max(initialCapacity, 0)];
        capacity = Math.max(initialCapacity, 0);
    }
    
    public SpaceObjectList(SpaceObjectList<T> parent){
        if (parent == null){
            list = (T[]) new SpaceObject[10];
            capacity = 10;
        }
        else{
            int capac = parent.capacity();
            list = (T[]) new SpaceObject[capac];
            capacity = capac;
            SpaceObjectIterator<T> it = parent.iterator();
            while(it.hasNext())
                add(it.next());
        }
    }
    
    public void add(T so){
        if (so == null)
            return;
        if (list.length <= nextIndex)
            incCapacity();
        list[nextIndex] = so;
        size++;
        nextIndex++;
    }
    
    public SpaceObject get(int index){
        try{
            return list[index];
        }
        catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
    
    public T randomSpaceObject(){
        SpaceObject[] l = new SpaceObject[size];
        int count = 0;
        for (int i=0; i<size; i++)
            if (list[i].active()){
                l[count] = list[i];
                count++;
            }
        if (count == 0)
            return null;
        return (T) l[StaticTools.rnd.nextInt(count)];
    }
    
    public void replaceInactive(T so){
        if (so == null)
            return;
        boolean replace = false;
        for (int i=0; i<size; i++)
            if (!list[i].active()){
                list[i] = so;
                replace = true;
            }
        if (!replace)
            add(so);
    }
    
    public void add(T[] items){
        if (items == null)
            return;
        for (int i=0; i<items.length; i++)
            add(items[i]);
    }
    
    public void add(ArrayList<T> items){
        if (items == null)
            return;
        int size = items.size();
        for (int i=0; i<size; i++)
            add(items.get(i));
    }
    
    public void add(SpaceObjectList<T> items) {
    	if (items == null)
    		return;
    	SpaceObjectIterator<T> it = items.iterator();
    	while (it.hasNext())
    		add(it.next());
    }
    
    public T[] toArray(){
        T[] so = (T[]) new SpaceObject[size];
        for (int i=0; i<size; i++)
            so[i] = list[i];
        return so;
    }
    
    public ArrayList<T> remove(ArrayList<T> a){
        if (a == null || a.size() == 0)
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
        
    
    public T remove(int index){
        SpaceObject so = list[index];
        list[index] = null;
        size--;
        compress();
        return (T) so;
    }
    
    public void remove(T so){
        int index = getIndexOf(so);
        if (index != -1)
            remove(index);
    }
    
    public int getIndexOf(T so){
        if (so == null)
            return -1;
        for (int i=0; i<size; i++)
            if (list[i] != null)
                if (list[i].equals(so))
                    return i;
        return -1;
    }
    
    private void incCapacity(){
        SpaceObject[] list2 = new SpaceObject[list.length+capacityIncrement];
        for (int i=0; i<size; i++)
            list2[i] = list[i];
        list = (T[]) list2;
        capacity += capacityIncrement;
    }
    
    public int size(){
        return size;
    }
    
    public int capacity(){
        return capacity;
    }
    
    public int activeSize(){
        int count = 0;
        for (int i=0; i<size; i++)
            if (list[i].active())
                count++;
        return count;
    }
    
    public T getInactiveObject(){
        SpaceObject inactive = null;
        int i;
        for (i=0; i<size; i++)
            if (!list[i].active()){
                inactive = list[i];
                break;
            }
        if (inactive != null)
            remove(i);
        return (T) inactive;
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
        SpaceObject[] list2 = new SpaceObject[list.length];
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
        list = (T[]) list2;
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
    
    public SpaceObjectIterator<T> iterator(){
        return new SpaceObjectIterator<T>(list, size);
    }
    
    public SpaceObjectList<T> copyList(){
        return new SpaceObjectList<T>(this);
    }
}