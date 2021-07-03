package main;

//import java.net.*;
//import java.util.ArrayList;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import io.*;

/**
 * This is the main class of the Text Editor program.
 * It handles creating the GUI and linking the user to their i/o options.
 * 
 */
public class TextEditor  implements ActionListener//, KeyListener
{
	
	/**
	 * Run the program.
	 * 
	 * @param args Not used.
	 */
    public static void main(String[] args)
    {
        TextEditor te = new TextEditor();
        te.setStatic();
    }
    
    
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu file, edit, help;
    private JMenuItem new_pad, open, save, save_as, quit, copy, paste, cut, about;//, openCiphertext,  saveAsCiphertext;
    private JCheckBoxMenuItem word_wrap;
    private JFileChooser chooser;
    private JTextArea typeArea;

    private Action[] actions = new Action[0];
    private File currentFile;
    private FileOpener files;
    private int ctrl = InputEvent.CTRL_DOWN_MASK, shift = InputEvent.SHIFT_DOWN_MASK;//, alt = InputEvent.ALT_DOWN_MASK, index = 1;
    private Progress progress = new Progress();
    private boolean saved = false, wordWrap = false, isStatic = false;//, new_file = false;
    
    private final String preFilenameTitle = "Text Editor: ";

    
    /**
     * Create a new TextEditor object.
     */
    public TextEditor(){
        frame = new JFrame(preFilenameTitle + "Un-named Document");
        chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new TextFileFilter());
        files = new FileOpener();
        makeContent();
    }
    
    
    /**
     * Set it to exit the java virtual machine when the window is closed.
     */
    private void setStatic(){
        isStatic = true;
        //frame.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    /**
     * Build the GUI for the editor.
     */
    private void makeContent(){
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setBorder(new EmptyBorder( 10, 10, 10, 10));
        
        typeArea = new JTextArea();
        actions = typeArea.getActions();
        JScrollPane scrollPane1 = new JScrollPane(typeArea);
        contentPane.add(scrollPane1, BorderLayout.CENTER);
        
        menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        
            file = new JMenu("File");
            
                new_pad = new JMenuItem("New");
                    new_pad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ctrl));
                open = new JMenuItem("Open");
                    open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ctrl));
                //openCiphertext = new JMenuItem("Open Encrypted File");
                //    openCiphertext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ctrl+shift));
                save = new JMenuItem("Save");
                    save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl));
                save_as = new JMenuItem("Save As...");
                    save_as.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl+shift));
                //saveAsCiphertext = new JMenuItem("Save As Encrypted");
                //    saveAsCiphertext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl+shift+alt));
                quit = new JMenuItem("Quit");
                    quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ctrl));
                
                
                
                new_pad.setActionCommand("new");
                open.setActionCommand("open");
                //openCiphertext.setActionCommand("open cipher");
                save.setActionCommand("save");
                save_as.setActionCommand("save as");
                //saveAsCiphertext.setActionCommand("save cipher");
                quit.setActionCommand("quit");
                
                new_pad.addActionListener(this);
                open.addActionListener(this);
                //openCiphertext.addActionListener(this);
                save.addActionListener(this);
                save_as.addActionListener(this);
                //saveAsCiphertext.addActionListener(this);
                quit.addActionListener(this);

            file.add(new_pad);
            file.add(open);
            //file.add(openCiphertext);
            file.add(save);
            file.add(save_as);
            //file.add(saveAsCiphertext);
            file.add(quit);
            
        menuBar.add(file);
        
            edit = new JMenu("Edit");
            
                copy = new JMenuItem("Copy");
                    copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrl));
                cut = new JMenuItem("Cut");
                    cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ctrl));
                paste = new JMenuItem("Paste");
                    paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ctrl));
                word_wrap = new JCheckBoxMenuItem("Word Wrap");
                
                copy.setActionCommand("copy");
                cut.setActionCommand("cut");
                paste.setActionCommand("paste");
                word_wrap.setActionCommand("word wrap");
                
                Action copyA = getAction("copy-to-clipboard");
                if (copyA != null)
                    copy.addActionListener(copyA);
                else
                    copy.setEnabled(false);
                    
                Action cutA = getAction("cut-to-clipboard");
                if (cutA != null)
                    cut.addActionListener(cutA);
                else
                    cut.setEnabled(false);
                
                Action pasteA = getAction("paste-from-clipboard");
                if (pasteA != null)
                    paste.addActionListener(pasteA);
                else
                    paste.setEnabled(false);
                    
                word_wrap.addActionListener(this);
                
            edit.add(copy);
            edit.add(cut);
            edit.add(paste);
            edit.addSeparator();
            edit.add(word_wrap);
            
        menuBar.add(edit);
        
            help = new JMenu("Help");
            
                about = new JMenuItem("About");
                
                about.setActionCommand("about");
                
                about.addActionListener(this);
                
            help.add(about);
            
        menuBar.add(help);
        frame.setJMenuBar(menuBar);
        
        frame.pack();
        frame.setLocation(50,50);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
    
    
    /**
     * Process an action by the user.
     * 
     * @param ae An ActionEvent generated by the user performing some action.
     */
    public void actionPerformed(ActionEvent ae){
        String ac = ae.getActionCommand().trim().toLowerCase();
        
        if (ac.equals("new")){
            newDoc();
        }
        else if (ac.equals("save")){
            save();
        }
        else if (ac.equals("save as")){
            saveAs();
        }
        else if (ac.equals("open")){
            open();
        }
        //else if (ac.equals("open cipher")){
        //    openEncrypted();
        //}
        //else if (ac.equals("save cipher")){
        //    saveEncrypted();
        //}
        else if (ac.equals("quit")){
            quit();
        }
        else if (ac.equals("word wrap")){
            wordWrap();
        }
        else if (ac.equals("about")){
            about();
        }
    }
    
    
    /**
     * Create a new, blank text document.
     */
    private void newDoc(){
        frame.setTitle("Text Editor: Un-named Document");
        typeArea.setText("");
        currentFile = null;
        //new_file = true;
        saved = false;
    }
    
    
    /**
     * Save the current text document.
     */
    private void save(){
    String name = "";
        boolean save = true;
        if (currentFile == null){
            int result = chooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION){
                currentFile = chooser.getSelectedFile();
                save = true;
            }
            else if (result == JFileChooser.CANCEL_OPTION){
                save = false;
            }
        }
        if (save){
            try{
                name = getFileName(currentFile.getPath());
                files.clearFile(currentFile);
                files.addToFile(currentFile, new String[] {typeArea.getText()});
                frame.setTitle("Text Editor: " + name);
                saved = true;
            }
            catch (IOException e){}
        }
        //new_file = false;
    }
    
    
    /**
     * Save the current text document as a new file.
     */
    private void saveAs(){
        if (currentFile != null){
            if (!saved){
                int result = JOptionPane.showConfirmDialog(frame, "Save Changes To " + getFileName(currentFile.toString()) + "?", "Brent Question", JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION){
                    try{
                        files.clearFile(currentFile);
                        files.addToFile(currentFile, new String[] {typeArea.getText()});
                    }
                    catch (IOException e){}
                }
                else if (result == JOptionPane.CANCEL_OPTION){
                    return;
                }
                int result2 = chooser.showSaveDialog(frame);
                if (result2 == JFileChooser.APPROVE_OPTION){
                    currentFile = chooser.getSelectedFile();
                    if (!contains(currentFile.getName(), '.')){
                        String path = currentFile.getPath() + ".txt";
                        currentFile = new File(path);
                    }
                    String name = getFileName(currentFile.getPath());
                    try{
                        files.clearFile(currentFile);
                        files.addToFile(currentFile, new String[] {typeArea.getText()});
                        saved = true;
                        frame.setTitle("Text Editor: " + name);
                    }
                    catch (IOException e){}
                }
            }
            else{
                int result = chooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION){
                    currentFile = chooser.getSelectedFile();
                    if (!contains(currentFile.getName(), '.')){
                        String path = currentFile.getPath() + ".txt";
                        currentFile = new File(path);
                    }
                    String name = getFileName(currentFile.getPath());
                    try{
                        files.clearFile(currentFile);
                        files.addToFile(currentFile, new String[] {typeArea.getText()});
                        saved = true;
                        frame.setTitle("Text Editor: " + name);
                    }
                    catch (IOException e){}
                }
            }
        }
        else{
            int result = chooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION){
                currentFile = chooser.getSelectedFile();
                if (!contains(currentFile.getName(), '.')){
                    String path = currentFile.getPath() + ".txt";
                    currentFile = new File(path);
                }
                String name = getFileName(currentFile.getPath());
                try{
                    files.clearFile(currentFile);
                    files.addToFile(currentFile, new String[] {typeArea.getText()});
                    saved = true;
                    frame.setTitle("Text Editor: " + name);
                }
                catch (IOException e){}
            }
        }
    }
    
//    private void saveEncrypted(){
//        ArrayList al = collectLines();
//        Encryption.Datapack dp = Encryption.FileAccess.encryptAndSave(al, frame);
//        currentFile = null;
//    }
    
    
    /**
     * Open a new text document.
     */
    private void open(){
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION){
            currentFile = chooser.getSelectedFile();
            try{
                String[] fileLines = files.readFile(currentFile);
                frame.setTitle("Text Editor: " + getFileName(currentFile.toString()));
                setText(fileLines);
            }
            catch (IOException e){}
        }
        //new_file = false;
        saved = false;
    }
    
//    private void openEncrypted(){
//        Encryption.Datapack dp = Encryption.FileAccess.openAndDecryptFile(frame);
//        String[] newLines = dp.getLines();
//        if (newLines != null){
//            String fileName = dp.getFile().getName();
//            frame.setTitle("Text Editor: " + fileName);
//            setText(newLines);
//            currentFile = null;
//        }
//    }
    
    
    /**
     * Quit out of the text editor.
     */
    public void quit(){
        if (!saved)
            save();
        typeArea.setText("");
        currentFile = null;
        saved = false;
        //new_file = true;
        frame.setVisible(false);
        if (isStatic)
            System.exit(0);
    }
    
//    private boolean askSaveIfNotAlready(){
//        boolean haveText = haveText();
//        if (currentFile == null && !haveText)
//            return false;
//        if (!saved && (haveText && currentFile != null)){
//            boolean result = askQuestionYesNo("Save Changes To " + ((currentFile!=null)?currentFile.getName():"Un-named document["+index+"].txt") + "?");
//            return result;
//        }
//        return false;
//    }
    
//    private boolean haveText(){
//        return !typeArea.getText().equals("");
//    }
    
    
    /**
     * Toggle word wrap.
     */
    private void wordWrap(){
        wordWrap = !wordWrap;
        typeArea.setLineWrap(wordWrap);
    }
    
    
    /**
     * Display about information.
     */
    private void about(){
        inform("Text Editor: version 1.1" + "\n"
             + "By Brent Lofton");
    }
    
    
    /**
     * Get an Action object, as pre-defined by typeArea.getActions().
     * 
     * @param action The name of the action to retrieve.
     * @return The Action object if found, or null if not.
     */
    private Action getAction(String action){
        action = action.trim().toLowerCase();
        for (int i=0; i<actions.length; i++){
            String aName = (String) actions[i].getValue(Action.NAME);
            aName = aName.trim().toLowerCase();
            if (aName.equals(action))
                return actions[i];
        }
        return null;
    }
    
    
    /**
     * Check if a String contains a specific character.
     * 
     * @param stg The String to search.
     * @param char_one The char to find.
     * @return whether the character was in the String.
     */
    private boolean contains(String stg, char char_one){
        int length = stg.length();
        for (int i=0; i<length; i++)
            if (stg.charAt(i) == char_one)
                return true;
        return false;
    }
    
    
    /**
     * Get the filename from a String file path.
     * 
     * @param The full path to the file.
     * @return The isolated name of the file. 
     */
    private String getFileName(String fileName){
        return new File(fileName).getName();
    }
         
//    private void addToTypeArea(String text){
//        typeArea.append(text + "\n");
//        typeArea.setCaretPosition(typeArea.getDocument().getLength());
//    }

    /**
     * Set the entire text editor's contents.
     * 
     * @param lines An String[] to place into the text area.
     */
    private void setText(String[] lines){
        int minProgress = 100;
        typeArea.setText("");
        if (lines.length >= minProgress){
            progress.setTitleAndLoadType("Formatting", "formatted");
            progress.update(0, lines.length);
            progress.show();
        }
        for (int i=0; i<lines.length; i++){
            typeArea.append(lines[i] + (i<lines.length-1?"\n":""));
            if (lines.length >= minProgress);
                progress.update(i+1);
        }
        if (lines.length >= minProgress)
            progress.hide();
        typeArea.setCaretPosition(typeArea.getDocument().getLength());
    }
    
//     public void keyPressed(KeyEvent ke){
//         saved = false;
//         int keyCode = ke.getKeyCode();
//         boolean ctrl = ke.isControlDown();
//         boolean shift = ke.isShiftDown();
//         if (ctrl){
//             if (keyCode == 78)
//                 newDoc();
//             else if (keyCode == 79)
//                 open();
//             else if (keyCode == 83 && shift)
//                 saveAs();
//             else if (keyCode == 83)
//                 save();
//             else if (keyCode == 81)
//                 quit();
//         }
//     }
    
//    private ArrayList<String> collectLines(){
//        String[] lines = getLines();
//        ArrayList<String> al = new ArrayList<String>();
//        for (int i=0; i<lines.length; i++)
//            al.add(lines[i]);
//        return al;
//    }
    
//    private String[] getLines(){
//        return typeArea.getText().split("\n");
//    }
    
//    private boolean askQuestionYesNo(String question){
//        int result = JOptionPane.showConfirmDialog(frame, question, "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//        if (result == JOptionPane.YES_OPTION)
//            return true;
//        else
//            return false;
//    }
    
//    private int askQuestionYesNoCancel(String question){
//        return JOptionPane.showConfirmDialog(frame, question, "Question", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
//    }
    
//    private String promptInput(String message){
//        return  JOptionPane.showInputDialog(frame, message, "Please Input Info", JOptionPane.INFORMATION_MESSAGE);
//    }
    
    /**
     * Display a pop-up informing the user of something.
     * 
     * @param message The message to display.
     */
    private void inform(String message){
        JOptionPane.showMessageDialog(frame, message);
    }
}