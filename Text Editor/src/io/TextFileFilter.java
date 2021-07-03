package io;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
//import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * This class filters files from showing up in the open dialog box.
 * It only allows those of specified extension to be visible.
 */
public class TextFileFilter extends FileFilter
{

    //private static String TYPE_UNKNOWN = "Type Unknown", HIDDEN_FILE = "Hidden File";
    private Hashtable<String, TextFileFilter> filters = null;
    private String description = null, fullDescription = null;
    private boolean useExtensionsInDescription = true;
    
    
    /**
     * Creates a file filter that accepts the given file type.
     * Example: new ExampleFileFilter("jpg", "JPEG Image Images");
     *
     * Note that the "." before the extension is not needed. If
     * provided, it will be ignored.
     *
     * @see #addExtension
     */
    public TextFileFilter(){
        super();
        filters = new Hashtable<String, TextFileFilter>();
        addExtension("txt");
        addExtension("java");
        setDescription("Text Files & Java Files");
    }
    
    
    /**
     * Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     *
     * Files that begin with "." are ignored.
     *
     * @see #getExtension
     * 
     * @param f A File to check.
     * @return whether this filter accepts this File.
     */
    public boolean accept(File f){
        if(f != null){
            if(f.isDirectory())
                return true;
            String extension = getExtension(f);
            if(extension != null && filters.get(getExtension(f)) != null)
                return true;
        }
        return false;
    }
    
    
    /**
     * Return the extension portion of the file's name .
     *
     * @see #getExtension
     * @see FileFilter#accept
     * 
     * @param f A File object from which to retrieve its extension.
     * @return A String containing the File's extension.
     */
    public String getExtension(File f) {
        if(f != null){
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if(i>0 && i<filename.length()-1)
                return filename.substring(i+1).toLowerCase();
        }
        return null;
    }
    
    
    /**
     * Adds a file type "dot" extension to filter against.
     *
     * For example: the following code will create a filter that filters
     * out all files except those that end in ".jpg" and ".tif":
     *
     *   ExampleFileFilter filter = new ExampleFileFilter();
     *   filter.addExtension("jpg");
     *   filter.addExtension("tif");
     *
     * Note that the "." before the extension is not needed and will be ignored.
     * 
     * @param extension A String with an extension to add to the filter.
     */
    public void addExtension(String extension) {
        if(filters == null) 
            filters = new Hashtable<String, TextFileFilter>(5);
        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }
    
    
    /**
     * Returns the human readable description of this filter. For
     * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     * @see FileFilter#getDescription
     */
    public String getDescription() {
        if(fullDescription == null){
            if(description == null || isExtensionListInDescription()){
                fullDescription = description==null ? "(" : description + " (";
                // build the description from the extension list
                Enumeration<String> extensions = filters.keys();
                if(extensions != null){
                    fullDescription += "." + (String) extensions.nextElement();
                    while (extensions.hasMoreElements()){
                        fullDescription += ", ." + (String) extensions.nextElement();
                    }
                }
                fullDescription += ")";
            }
            else{
                fullDescription = description;
            }
        }
        return fullDescription;
    }
    
    
    /**
     * Sets the human readable description of this filter. For
     * example: filter.setDescription("Gif and JPG Images");
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     * 
     * @param description The description of the extensions this filter looks for.
     */
    public void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }
    
    
    /**
     * Determines whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevant if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see isExtensionListInDescription
     * 
     * @param b whether the extension list will show up in the description.
     */
    public void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        fullDescription = null;
    }
    
    
    /**
     * Returns whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevant if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see setExtensionListInDescription
     * 
     * @return whether the extension list will show up in the description.
     */
    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }
}