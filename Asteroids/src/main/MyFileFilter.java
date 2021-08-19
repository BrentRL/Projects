package main;

import java.io.File;
import java.util.Hashtable;
import java.io.FileFilter;

public class MyFileFilter implements FileFilter{

    private Hashtable<String, MyFileFilter> filters = new Hashtable<String, MyFileFilter>();

    public MyFileFilter(String extension) {
        filters.put(extension.toLowerCase(), this);
    }
    
    public MyFileFilter(String[] extensions){
        for (int i=0; i<extensions.length; i++)
            filters.put(extensions[i].toLowerCase(), this);
    }
    
    public boolean accept(File f){
        if(f != null) {
            String extension = getExtension(f);
            if(extension != null && filters.get(getExtension(f)) != null)
                return true;
        }
        return false;
    }
    
    private String getExtension(File f){
        if(f != null){
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if(i>0 && i<filename.length()-1)
                return filename.substring(i+1).toLowerCase();
        }
        return null;
    }
}