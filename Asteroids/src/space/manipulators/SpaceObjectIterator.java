package space.manipulators;
import space.objects.parents.SpaceObject;

public class SpaceObjectIterator<T extends SpaceObject>
{
    private int nextIndex = 0, size;
    private T[] objects;
    
    public SpaceObjectIterator(T[] list, int size){
        objects = list;
        this.size = size;
    }
    
    public T next(){
        T item = objects[nextIndex];
        nextIndex++;
        return item;
    }
    
    public boolean hasNext(){
        return size > nextIndex;
    }
}