package display;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class BFrame extends JFrame implements ActionListener, WindowListener
{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3530242012750020237L;
	
	private GraphicsDevice device;
//     private DisplayMode originalDM;
//     private JButton exit = new JButton("Exit");
    private boolean isFullScreen, visible;

    public BFrame() {
        //super(device.getDefaultConfiguration());
        //super("Asteroids", GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration());
        super("Asteroids", GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        //JButton exit = new JButton("Exit");
        //GraphicsDevice[] gd_s = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
//         for (int i=0; i<gd_s.length; i++)
//             System.out.println(gd_s[i].getIDstring());
//         GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1];
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
//         originalDM = device.getDisplayMode();
//         exit.addActionListener(this);
        Container c = getContentPane();
        ((JPanel)c).setBorder(new EmptyBorder(0,0,0,0));
//         c.add(exit);
        isFullScreen = device.isFullScreenSupported();
        setUndecorated(isFullScreen);
        setResizable(!isFullScreen);
        
        if (isFullScreen) {
            // Full-screen mode
            this.device = device;
            device.setFullScreenWindow(this);
//             validate();
        } else {
            // Windowed mode
            pack();
            this.device = device;
            setVisible(true);
        }
        visible = true;
        
        addWindowListener(this);
    }
    
    public void actionPerformed(ActionEvent e){
        hide_now();
    }
    
    public void setVisible(boolean visi){
        if (visi)
            super.setVisible(true);
        else
            hide_now();
//         if (visi)
//             super.show();
//         else
//             hide();
    }
    
    public void show_now(){
        if (isFullScreen && !visible)
            device.setFullScreenWindow(this);
        visible = true;
    }
    
    public void hide_now(){
        if (isFullScreen && visible)
            device.setFullScreenWindow(null);
        super.setVisible(false);
        visible = false;
    }
    
//     public void regress(){
//         device.setFullScreenWindow(null);
//     }
    
    public void windowClosed(WindowEvent e){
        if (e.getWindow() == this)
            visible = false;
    }
    
    public void windowClosing(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
}