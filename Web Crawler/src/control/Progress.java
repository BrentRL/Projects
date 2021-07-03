package control;

import javax.swing.*;
import java.awt.GridLayout;

/**
 * 
 */
public class Progress implements Runnable
{
    private JFrame frame;
    private JLabel label;
    private JLabel percent;
    //private Thread runThread;
    private JProgressBar progressBar;
    private int currentValue = 0, max = 1;
    private String title = "", loadType = "";
    
    
    /**
     * Create a new Progress object.
     */
    public Progress(){
        progressBar = new JProgressBar(currentValue, max);
        label = new JLabel("");
        percent = new JLabel("");
        JPanel panel = new JPanel(new GridLayout(3, 1));
        
        panel.add(progressBar);
        panel.add(label);
        panel.add(percent);
        
        frame = new JFrame();
        frame.getContentPane().add(panel, SwingConstants.CENTER);
        frame.pack();
        
        update();
    }
    
    
    /**
     * Set the title and units for the progress bar.
     * 
     * @param t The  title of the progress bar.
     * @param lt The type of units which are being counted.
     */
    public void setTitleAndLoadType(String t, String lt){
        if (t != null)
            title = t;
        else
            title = "";
        if (lt != null)
            loadType = lt;
        else
            loadType = "";
    }
    
    
    /**
     * Update the progress bar with new information.
     * 
     * @param currentValue The current progress made.
     * @param max The new maximum expected value.
     */
    public void update(int currentValue, int max){
        this.currentValue = currentValue;
        this.max = max;
        progressBar.setMaximum(max);
        SwingUtilities.invokeLater(this);
    }
    
    
    /**
     * Update the progress bar with new information.
     * 
     * @param currentValue The current progress made.
     */
    public void update(int currentValue){
        this.currentValue = currentValue;
        SwingUtilities.invokeLater(this);
    }
    
    /**
     * Hide the progress bar. 
     */
    public void hide(){
        frame.setVisible(false);
    }
    
    /**
     * Show the progress bar. 
     */
    public void show(){
        frame.setVisible(true);
    }
    
    
    /**
     * Update the progress bar.
     */
    public void run(){
        update();
    }
    
    
    /**
     * Update the progress bar with any added information.
     */
    private void update(){
        frame.setTitle(title);
        label.setText(currentValue + " of " + max + " " + loadType);
        percent.setText(getPercent() + "%");
        progressBar.setValue(currentValue);
    }
    
    /**
     * Get the percent done the progress bar is.
     * 
     * @return the percent done the progress bar is.
     */
    private int getPercent(){
        double perc = (currentValue+.0) / (max+.0);
        perc *= 100;
        return (int)Math.floor(perc);
    }
}