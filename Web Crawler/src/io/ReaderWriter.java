package io;

import java.io.*;
import java.util.ArrayList;
import java.net.URL;

/**
 * ReaderWriter - Class for reading and writing to and from files.
 */
public class ReaderWriter
{
    
    /**
     * Take data and write it into a file.
     * Overwrites any existing data in the file.
     * @param f The File to write.
     * @param lines the data to put in the file.
     * @return whether the write was successful.
     */
    public boolean setFile(File f, String[] lines){
        if (f == null)
            return false;
        if (lines == null)
            return false;
        try{
            FileWriter writer = new FileWriter(f, false);
            writer.write("");
            for (int i=0; i<lines.length; i++){
                String write = lines[i];
                if (i<lines.length-1)
                    write += "\n";
                writer.write(write);
            }
            writer.close();
        }
        catch (IOException ioe){; return false;}
        return true;
    }
    
    
    /**
     * Reads a text file and returns a String[] with the contents of the file.
     * @param f The File to read.
     * @return A String[] with the contents of the File.
     */
    public String[] readFile(File f){
        //f = makeAbsoluteFile(f);
        if (f == null)
            return null;
        ArrayList<String> strings = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();
            while(line != null){
                strings.add(line);
                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException ioe){System.out.println("failed to read");}
        
        String[] value = new String[strings.size()];
        
        for (int i=0; i<value.length; i++)
            value[i] = strings.get(i);
        return value;
    }
    
    
    /**
     * Read the data from a url.
     * 
     * @param targetURL The web address to read.
     * @return A String with the contents of the target web page.
     * @throws IOException if an IOException occurred while trying to read it.
     */
    public String readToString(String targetURL) throws IOException
    {
        URL url = new URL(targetURL);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(url.openStream()));
    
        StringBuilder stringBuilder = new StringBuilder();
    
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null)
        {
            stringBuilder.append(inputLine);
            stringBuilder.append(System.lineSeparator());
        }
    
        bufferedReader.close();
        return stringBuilder.toString().trim();
    }
}