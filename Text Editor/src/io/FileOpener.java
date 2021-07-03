package io;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class handles reading from and writing to files.
 */
public class FileOpener
{

	/**
	 * Read from a file.
	 * 
	 * @param file The target file to read
	 * @return a String[] containing the file's contents
	 * @throws IOException if an IOException occurs.
	 */
    public String[] readFile(File file) throws IOException
    {
       InputStream fstream = openFile(file);
        if (fstream != null){
            BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
            ArrayList<String> lines = new ArrayList<String>();
            String line = in.readLine();
            while(line != null){
                lines.add(line);
                line = in.readLine();
            }
            String[] result = new String[lines.size()];
            for (int i=0; i<result.length; i++){
                result[i] = (String) lines.get(i);
            }
            return result;
        }
        else
            return null;
    }
    
    
    /**
     * Read from a file.
     * 
     * @param fileName The path of the target file to read.
     * @return a String[] containing the file's contents.
     * @throws IOException if an IOException occurs.
     */
    public String[] readFile(String fileName) throws IOException{
        InputStream fstream = openFile(fileName);

        if (fstream != null){
            BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
            ArrayList<String> lines = new ArrayList<String>();
            String line = in.readLine();
            while(line != null){
                lines.add(line);
                line = in.readLine();
            }
            String[] result = new String[lines.size()];
            for (int i=0; i<result.length; i++){
                result[i] = (String) lines.get(i);
            }
            return result;
        }
        else
            return null;
    }
    
    
    /**
     * Add lines of text to the target file.
     * 
     * @param file The target file to append to.
     * @param linesToAdd The text to add to the file.
     * @throws IOException if an IOException occurs.
     */
    public void addToFile(File file, String[] linesToAdd) throws IOException{
        String[] currentText = readFile(file);
        String[] newText;
        
        if (currentText == null)
            currentText = new String[0];
        if (linesToAdd != null){
            newText = combine(currentText,linesToAdd);
            File resultsFile = makeAbsoluteFilename(file);
            FileWriter writer = new FileWriter(resultsFile);
            for(int i = 0; i < newText.length; i++)
                writer.write(newText[i]+ "\n");
            writer.close();
        }
    }
    
    
    /**
     * Add lines of text to the target file.
     * 
     * @param fileName The path of the target file to append to.
     * @param linesToAdd The text to add to the file.
     * @throws IOException if an IOException occurs.
     */
    public void addToFile(String fileName, String[] linesToAdd) throws IOException{
        String[] currentText = readFile(fileName);
        String[] newText;
        
        if (currentText == null)
            currentText = new String[0];
        if (linesToAdd != null){
            newText = combine(currentText,linesToAdd);
            
            File resultsFile = makeAbsoluteFilename(fileName);
            FileWriter writer = new FileWriter(resultsFile);
            for(int i = 0; i < newText.length; i++)
                writer.write(newText[i] + "\n");
            writer.close();
        }
    }
    
    
    /**
     * Erases any information in the specified file.
     * 
     * @param fileName The path of the target file to clear.
     * @throws IOException if an IOException occurs.
     */
    public void clearFile(String fileName) throws IOException{
        File resultsFile = makeAbsoluteFilename(fileName);
        FileWriter writer = new FileWriter(resultsFile);
        writer.write("");
        writer.close();
    }
    
    
    /**
     * Erases any information in the specified file.
     * 
     * @param file The target file to clear.
     * @throws IOException if an IOException occurs.
     */
    public void clearFile(File file) throws IOException{
        File resultsFile = makeAbsoluteFilename(file);
        FileWriter writer = new FileWriter(resultsFile);
        writer.write("");
        writer.close();
    }
    
    
    /**
     * Combines 2 String[] objects into one.
     * 
     * @param text1 The leading String[] to combine.
     * @param text2 The trailing String[] to combine.
     * @return A String[] with all Strings from text1 and text2.
     */
    private String[] combine(String[] text1, String[] text2)
    {
        String[] result = new String[text1.length+text2.length];
        int iMain = 0;
        for (int i=0; i<text1.length; i++){
            result[iMain] = (String) text1[i];
            iMain++;
        }
        for (int i=0; i<text2.length; i++){
            result[iMain] = (String) text2[i];
            iMain++;
        }
        return result;
    }
    
    
    /**
     * Take a file with no path and, assuming it is in the project directory, complete its absolute path.
     * 
     * @param file The file to work on.
     * @return A File with an absolute path based in the project directory.
     */
    private File makeAbsoluteFilename(File file)
    {
        //File file = new File(filename);
        if(!file.isAbsolute()) {
            file = new File(getProjectFolder(), file.toString());
        }
        return file;
    }
    
    
    /**
     * Take a file with no path and, assuming it is in the project directory, complete its absolute path.
     * 
     * @param filename The name of the file to work on.
     * @return A File with an absolute path based in the project directory.
     */
    private File makeAbsoluteFilename(String filename){
        File file = new File(filename);
        if(!file.isAbsolute()) {
            file = new File(getProjectFolder(), filename);
        }
        return file;
    }
    
    
    /**
     * Get the project folder this program is running in.
     * 
     * @return A file pointing to the project folder.
     */
    private File getProjectFolder(){
         String myClassFile = getClass().getName() + ".class";
         URL url = getClass().getResource(myClassFile);
         return new File(url.getPath()).getParentFile();
    }
    
    
    /**
     * Create a file with a given name.
     * 
     * @param fileName The name of the file to create.
     */
    public void createFile(String fileName){
        File file = new File(fileName);
        if (!file.isAbsolute())
            file = new File(getProjectFolder(), fileName);
    }
    
    
    /**
     * Open a file to be able to read it.
     * 
     * @param fileName The path of the target file to read.
     * @return An InputStream containing the data at the specified file's path.
     * @throws IOException if an IOException occurs.
     */
    private InputStream openFile(String fileName) throws IOException{
        if(fileName == null)
            System.out.println("Cannot open file - filename was null.");{
            URL url = getClass().getClassLoader().getResource(fileName);
            if(url == null)
                System.out.println("File not found: " + fileName);
            else
                return url.openStream();
        }
        return null;
    }
    
    
    /**
     * Open a file to be able to read it.
     * 
     * @param file The target file to read.
     * @return An InputStream containing the data at the specified file's path.
     * @throws IOException if an IOException occurs.
     */
    private InputStream openFile(File file) throws IOException
    {
        if(file == null)
            System.out.println("Cannot open file - file was null.");
        else{
            @SuppressWarnings("deprecation")
			URL url = file.toURL();
            if(url == null)
                System.out.println("File not found: " + file);
            else
                return url.openStream();
                
//            URL url = new URL(file.getAbsolutePath());
//            return url.openStream();
        }
        return null;
    }
}
