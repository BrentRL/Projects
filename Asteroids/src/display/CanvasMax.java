package display;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.event.*;
//import javax.swing.border.*;
//import java.awt.image.VolatileImage;
import java.awt.image.BufferedImage;

/**
 * Class CanvasMax - Powerful class used for graphical drawing on a canvas.
 * @author Brent Lofton
 * @version 3.1
 */
public class CanvasMax extends MouseAdapter implements MouseMotionListener
{
    public static final int    defaultW        = 500; // The default width
    public static final int    defaultH        = 500; // The default height
    public static final String defaultTitle    = "CanvasMax"; // The default title
    public static final Color  defaultBgColor  = Color.white; // The default background color
    public static final Color  defaultFgColor  = Color.black; // The default foreground color
    public static final String defaultPosition = BorderLayout.CENTER;  // The default canvas position in the frame's content pane
    private static int[] inverseColor;
    
    private ArrayList<ChangeItem> changers = new ArrayList<ChangeItem>(); // A list of ChangeItems added to this canvas
    private boolean listenersAdded; // Has this canvas been added as a MouseListener and a MouseMotionListener to the canvas pane
    
    private JFrame frame; // JFrame for displaying
    
//     private CanvasPane cp; // CanvasPane for painting on
    private JPanel cp; // JPanel for painting on     
    
    private Graphics2D g2d; // Graphics2D for painting with
    private Color bgc; // Background color for erasing
    
//     private VolatileImage ci; // VolatileImage for painting
    private BufferedImage ci; // BufferedImage for painting
    
    private int[] size; // Size of the canvas for record
    private int[] imageSize; // Size of the background image
    private Image bgImage; // The background image
    private boolean haveImage = false; // Is the bgImage valid

    public CanvasMax(){
        this(new JFrame(defaultTitle), defaultW, defaultH, defaultBgColor, true, defaultPosition);
    }

    /**
     * Create a new CanvasMax with the title, width, height, bgColor, visibility, and position. 
     * @param title the title of the canvas, null for default
     * @param width the width of the canvas, make width &lt; 0 for default
     * @param height the height of the canvas, make height &lt; 0 for default
     * @param bgColor the background color of the canvas, null for default
     * @param setVisible whether or not to show the JFrame
     * @param position position like BorderLayout.CENTER or BorderLayout.SOUTH or something, null or "" for default
     */
    public CanvasMax(String title, int width, int height, Color bgColor, boolean setVisible, String position){
        this(new JFrame((title==null)?defaultTitle:title), width, height, bgColor, setVisible, position);
    }
    
    /**
     * Create a new CanvasMax with the frame, width, height, bgColor, visibility, and position. 
     * @param f the JFrame the canvas will go in, null to make a new one
     * @param width the width of the canvas, make width &lt; 0 for default
     * @param height the height of the canvas, make height &lt; 0 for default
     * @param bgColor the background color of the canvas, null for default
     * @param setVisible whether or not to show the JFrame
     * @param position position like BorderLayout.CENTER or BorderLayout.SOUTH or something, null or "" for default
     */
    public CanvasMax(JFrame f, int width, int height, Color bgColor, boolean setVisible, String position){
        f        = (f==null)          ? new JFrame(defaultTitle) : f;
        width    = (width<=0)         ? defaultW                 : width;
        height   = (height<=0)        ? defaultH                 : height;
        bgColor  = (bgColor == null)  ? defaultBgColor           : bgColor;
        position = (!valid(position)) ? defaultPosition          : position;
        
        frame = f;
        
//         cp = new CanvasPane();
        cp = new JPanel();
        
        ((JPanel)frame.getContentPane()).add(cp, position);
        size = new int[2];
        size[0] = width;
        size[1] = height;
        cp.setPreferredSize(new Dimension(width, height));
        bgc = bgColor;
        frame.pack();
        init();
        if (setVisible)
            frame.setVisible(true);
    }
    
    //******************************************* GENERAL METHODS *******************************************
    
    /**
     * Check to see if position is one of the positions declared by BorderLayout.
     * e.g. BorderLayout.NORTH, BorderLayout.EAST, 
     */
    private boolean valid(String position){
        if (position != null && (position.equals(BorderLayout.NORTH)  || position.equals(BorderLayout.WEST)   ||
            position.equals(BorderLayout.CENTER) || position.equals(BorderLayout.EAST)   ||
            position.equals(BorderLayout.SOUTH)))
                return true;
        else
            return false;
    }
    
    /**
     * Refresh the canvas image on the canvas.
     */
    public synchronized void repaint(){
        cp.getGraphics().drawImage(ci, 0, 0, null);
//         cp.repaint();  
    }
    
    /**
     * Get the size of the canvas.
     * @return new int[] {width, height}
     */
    public int[] getSize(){
        return new int[] {size[0], size[1]};
    }
    
    public synchronized boolean haveBGimage(){
        return haveImage;
    }
    
    /**
     * Get the canvas pane.
     * @return an instance CanvasMax.CanvasPane
     */
    public JPanel getCanvas(){
        return cp;
    }
    
    /**
     * Get the containing JFrame.
     * @return the JFrame
     */
    public JFrame getFrame(){
        return frame;
    }
    
    /**
     * Create the canvas image, graphics and turn the canvas
     * to the background color.
     */
    private synchronized void init(){
        if(g2d == null) {
            // first time: instantiate the off screen image and fill it with
            // the background color
            
//             ci = cp.createVolatileImage(size[0], size[1]);
            ci = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_RGB);

//             g2d = (Graphics2D)ci.createGraphics();
            g2d = (Graphics2D)ci.getGraphics();

            g2d.setColor(bgc);
            g2d.fillRect(0, 0, size[0], size[1]);
            g2d.setColor(defaultFgColor);
        }
    }
    
    /**
     * Either hides or shows the containing JFrame depending on the parameter
     * @param visible true for visible; false for hidden;
     */
    public synchronized void setVisible(boolean visible){
        frame.setVisible(visible);
    }
    
    /**
     * Check to see if the containing JFrame is visible
     * @return whether on not it is visible
     */
    public synchronized boolean isVisible(){
        return frame.isVisible();
    }
    
    /**
     * Get the drawing Graphics2D
     * @return the instance of Graphics2D this canvas uses
     */
    public synchronized Graphics2D getG2D(){
        return g2d;
    }
    
    /**
     * Set the size of the canvas
     * @param width new width of canvas
     * @param height new height of the canvas
     */
    public synchronized void setSize(int width, int height){
        size[0] = width;
        size[1] = height;
        cp.setPreferredSize(new Dimension(width, height));
        Image oldImage = ci;
        
//         ci = cp.createVolatileImage(width, height);
        ci = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_RGB);

//         g2d = (Graphics2D)ci.getGraphics();
        g2d = (Graphics2D)ci.getGraphics();

        erase();
        g2d.drawImage(oldImage, 0, 0, null);
        frame.pack();
    }
    
    //******************************************* DRAW METHODS *******************************************
    
    //      LINES
        
        /**
         * Draw a line
         * @param color color of the line
         * @param x1 starting x position
         * @param y1 starting y position
         * @param x2 ending x position
         * @param y2 ending y position
         */
        public synchronized void drawLine(Color color, int x1, int y1, int x2, int y2){
            if (color != null)
                g2d.setColor(color);
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        /**
         * Erase a line
         * @param x1 starting x position
         * @param y1 starting y position
         * @param x2 ending x position
         * @param y2 ending y position
         */
        public synchronized void eraseLine(int x1, int y1, int x2, int y2){
            g2d.setColor(bgc);
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        /**
         * Move a line
         * @param color new line color
         * @param from int[] { {x1, y1}, {x2, y2} } of the line to erase
         * @param to int[] { {x1, y1}, {x2, y2} } of the line to draw
         */
        public synchronized void moveLine(Color color, int[][] from, int[][] to){
            g2d.setColor(bgc);
            g2d.drawLine(from[0][0], from[0][1], from[1][0], from[1][1]);
            g2d.setColor(color);
            g2d.drawLine(to[0][0], to[0][1], to[1][0], to[1][1]);
        }
    
    //      END LINES
    //--------------------------------------------------------------------------
    //      SHAPES
        
        /**
         * Fill/Draw a shape
         * @param shape the shape to fill/draw
         * @param color the color to fill/draw the shape
         * @param fill whether or not to fill the shape
         * @param draw whether or not to draw the shape
         */
        public synchronized void shape(Shape shape, Color color, boolean fill, boolean draw){
            g2d.setColor(color);
            if (fill) g2d.fill(shape);
            if (draw) g2d.draw(shape);
        }
        
        public synchronized void shape(Shape[] shapes, Color color, boolean fill, boolean draw){
            g2d.setColor(color);
            for (int i=0; i<shapes.length; i++){
                if (fill) g2d.fill(shapes[i]);
                if (draw) g2d.draw(shapes[i]);
            }
        }
        
        /**
         * Erase the fill/draw of a shape
         * @param shape the shape to fill/draw
         * @param eraseFill whether or not to erase the fill of the shape
         * @param outLine whether or not to erase the outline of the shape
         */
        public synchronized void erase(Shape shape, boolean eraseFill, boolean outLine){
            g2d.setColor(bgc);
            if (eraseFill) g2d.fill(shape);
            if (outLine) g2d.draw(shape);
        }
        
        /**
         * Move a shape
         * @param from the shape to move from
         * @param to the shape to move to
         * @param color the color of the final shape
         * @param eraseFill whether or not to erase the fill of shape 'from'
         * @param eraseDraw whether or not to erase the outline of shape 'from'
         */
        public synchronized void moveShape(Shape from, Shape to, Color color, boolean eraseFill, boolean outLine){
            g2d.setColor(bgc);
            if (eraseFill) g2d.fill(from);
            if (outLine) g2d.draw(from);
            g2d.setColor(color);
            if (eraseFill) g2d.fill(to);
            if (outLine) g2d.draw(to);
        }
        
        //Ellipses
            /**
             * Draw an ellipse
             * @param xPos x position
             * @param yPos y position
             * @param width ellipse width
             * @param height ellipse height
             * @param fill whether or not to fill the ellipse
             * @param draw whether or not to draw the ellipse
             */
            public synchronized void ellipse(Color color, int xPos, int yPos, int width, int height, boolean fill, boolean draw){
                g2d.setColor(color);
                Ellipse2D.Double ellipse = new Ellipse2D.Double(xPos, yPos, width, height);
                if (fill) g2d.fill(ellipse);
                if (draw) g2d.draw(ellipse);
            }
            
            /**
             * Erase an ellipse
             * @param xPos x position
             * @param yPos y position
             * @param width ellipse width
             * @param height ellipse height
             * @param eraseFill whether or not to erase the fill of the ellipse
             * @param eraseDraw whether or not to erase the outline of the ellipse
             */
            public synchronized void eraseEllipse(int xPos, int yPos, int width, int height, boolean eraseFill, boolean outLine){
                g2d.setColor(bgc);
                Ellipse2D.Double ellipse = new Ellipse2D.Double(xPos, yPos, width, height);
                if (eraseFill) g2d.fill(ellipse);
                if (outLine) g2d.draw(ellipse);
            }
        
        //Rectangles
            /**
             * Draw a rectangle
             * @param xPos x position
             * @param yPos y position
             * @param width rectangle width
             * @param height rectangle height
             * @param fill whether or not to fill the rectangle
             * @param draw whether or not to draw the rectangle
             */
            public synchronized void rectangle(Color color, int xPos, int yPos, int width, int height, boolean fill, boolean draw){
                g2d.setColor(color);
                Rectangle rect = new Rectangle(xPos, yPos, width, height);
                if (draw) g2d.draw(rect);
                if (fill) g2d.fill(rect);
            }
            
            /**
             * Erase a rectangle
             * @param xPos x position
             * @param yPos y position
             * @param width rectangle width
             * @param height rectangle height
             * @param eraseFill whether or not to erase the rectangle fill
             * @param eraseDraw whether or not to erase the rectangle outline
             */
            public synchronized void eraseRectangle(int xPos, int yPos, int width, int height, boolean eraseFill, boolean outLine){
                g2d.setColor(bgc);
                Rectangle rect = new Rectangle(xPos, yPos, width, height);
                if (eraseFill) g2d.fill(rect);
                if (outLine) g2d.draw(rect);
            }
    
    //      END SHAPES
    //--------------------------------------------------------------------------
    //      STRINGS  
        
        /**
         * Draw A String
         * @param text the String
         * @param x x position
         * @param y y position
         */
        public synchronized void drawString(String text, Color color, int x, int y){
            if (color != null)
                g2d.setColor(color);
            g2d.drawString(text, x, y);
        }
        
        /**
         * Erase a String.
         * @param text the String
         * @param x x position
         * @param y y position
         */
        public synchronized void eraseString(String text, int x, int y){
            g2d.setColor(bgc);
            g2d.drawString(text, x, y);   
        }
    
    //      END STRINGS
    //--------------------------------------------------------------------------
    //      IMAGES
    
        /**
         * Get the canvas image.
         * @return the canvas image
         */
//         public synchronized VolatileImage getImage(){
        public synchronized BufferedImage getImage(){
            return ci;
        }
        
        /**
         * Draw an Image
         * @param image the image
         * @param x x position
         * @param y y position
         */
        public synchronized boolean drawImage(Image image, int x, int y){
            boolean result = g2d.drawImage(image, x, y, null);
            return result;
        }
        
        /**
         * Draw an Image
         * @param image the image
         * @param x x position
         * @param y y position
         * @param width desired draw width
         * @param height desired draw height
         */
        public synchronized boolean drawImage(Image image, int x, int y, int width, int height){
            boolean result = g2d.drawImage(image, x, y, width, height, null);
            return result;
        }
        
        /**
         * Set the background image.
         * @param path the String path of the image
         */
        public synchronized void setBgImage(String path){
            setBgImage(new ImageIcon(path));
        }
        
        /**
         * Set the background image.
         * @param imageFile file denoting the target image file
         */
        public synchronized void setBgImage(java.io.File imageFile){
            setBgImage(new ImageIcon(imageFile.getPath()));
        }
        
        /**
         * Set the background image.
         * @param i background image
         */
        public synchronized void setBgImage(Image i){
            setBgImage(new ImageIcon(i));
        }
        
        /**
         * Set the background image.
         * @param ii ImageIcon the image is wrapped in
         */
        public synchronized void setBgImage(ImageIcon ii){
            bgImage = ii.getImage();
            imageSize = new int[] {ii.getIconWidth(), ii.getIconHeight()};
            if (imageSize[0] > 0 && imageSize[1] > 0){
                haveImage = true;
            }
        }
        
        /**
         * Draw the background image if one exists.
         */
        public synchronized void drawBG(){
            if (!haveImage)
                return;
            if (size[0] < imageSize[0] && size[1] < imageSize[1]){
                g2d.drawImage(bgImage, 0, 0, size[0], size[1], null);
                return;
            }
            int deltaX, deltaY;
            for (deltaX = 0; deltaX < size[0]; deltaX += imageSize[0])
                for (deltaY = 0; deltaY < size[1]; deltaY += imageSize[1])
                    g2d.drawImage(bgImage, deltaX, deltaY, null);
        }
    
    //      END IMAGES
    //--------------------------------------------------------------------------
    //      COLOR
    
        //Foreground
            /**
             * Set the foreground color of the Graphics2D.
             * @param newColor the new foreground color
             */
            public synchronized void setForegroundColor(Color newColor){
                g2d.setColor(newColor);
            }
            
            /**
             * Get the foreground color of the Graphics2D.
             * @return the current foreground color of the graphics
             */
            public synchronized Color getForegroundColor(){
                return g2d.getColor();
            }
        
        //Background
            /**
             * Set the background color of the Graphics2D.
             * @param newColor the new background color
             */
            public synchronized void setBackgroundColor(Color newColor){
                bgc = newColor;   
                g2d.setBackground(newColor);
            }
            
            /**
             * Get the background color of the Graphics2D.
             * @return the current background color of the graphics
             */
            public synchronized Color getBackgroundColor(){
                return bgc;
            }
            
        /**
         * This method will return the color that contrasts the color parameter
         * @param ofColor the color to find the contrast of
         * @return the color that contrasts to "ofColor"
         */
        public static Color getInverseColor(Color ofColor){
            if (inverseColor == null)
                initInverseColors();
            return new Color(inverseColor[Math.max(0, ofColor.getRed()-1)], inverseColor[Math.max(0, ofColor.getGreen()-1)], inverseColor[Math.max(0, ofColor.getBlue()-1)]);
            
        }
        
        /**
         * This method takes an Array of Colors and crates a new color with 
         * the average red, green, and blue of all colors.
         * @param colors the colors to find the average of
         * @return the average color
         */
        public synchronized static Color getAverageColor(Color[] colors){
            int r = 0, g = 0, b = 0;
            double total = colors.length;
            for (int i=0; i<total; i++){
                Color c = colors[i];
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
            r = (int)Math.round(r/total);
            g = (int)Math.round(g/total);
            b = (int)Math.round(b/total);
            return new Color(r,g,b);
        }
        
        
        /*
         * Create the smoothest transition with the least amount of colors from "fromColor" to "toColor".
         * @param fromColor the starting transition color
         * @param toColor the ending transition color
         * @return a Color[] object that makes the transition from least to greatest index
         */
        public synchronized static Color[] transist(Color fromColor, Color toColor){
            if (fromColor.equals(toColor))
                return new Color[] {fromColor, toColor};
            double r1 = fromColor.getRed(), g1 = fromColor.getGreen(), b1 = fromColor.getBlue(), a1 = fromColor.getAlpha();
            double r2 = toColor.getRed(), g2 = toColor.getGreen(), b2 = toColor.getBlue(), a2 = fromColor.getAlpha();
            double deltaRed = Math.max(r1, r2) - Math.min(r1, r2);
            double deltaGreen = Math.max(g1, g2) - Math.min(g1, g2);
            double deltaBlue = Math.max(b1, b2) - Math.min(b1, b2);
            double deltaAlpha = Math.max(a1, a2) - Math.min(a1, a2);
            double rInc = 0, gInc = 0, bInc = 0, aInc = 0;
            Color[] transist = new Color[0];
            if (deltaRed >= Math.max(deltaBlue, deltaGreen)){
                transist = new Color[(int)deltaRed+2];
                rInc = 1.0 * (r1<r2?1:-1);
                gInc = (deltaGreen / deltaRed) * (g1<g2?1:-1);
                bInc = (deltaBlue / deltaRed) * (b1<b2?1:-1);
                aInc = (deltaAlpha / deltaRed) * (a1<a2?1:-1);
            }
            else if (deltaGreen >= Math.max(deltaRed, deltaBlue)){
                transist = new Color[(int)deltaGreen+2];
                rInc = (deltaRed / deltaGreen) * (r1<r2?1:-1);
                gInc = 1.0 * (g1<g2?1:-1);
                bInc = (deltaBlue / deltaGreen) * (b1<b2?1:-1);
                aInc = (deltaAlpha / deltaGreen) * (a1<a2?1:-1);
                
            }
            else if (deltaBlue >= Math.max(deltaRed, deltaGreen)){
                transist = new Color[(int)deltaBlue+2];
                rInc = (deltaRed / deltaBlue) * (r1<r2?1:-1);
                gInc = (deltaGreen / deltaBlue) * (g1<g2?1:-1);
                bInc = 1.0 * (b1<b2?1:-1);
                aInc = (deltaAlpha / deltaBlue) * (a1<a2?1:-1);
            }
            double rIndex = r1, gIndex = g1, bIndex = b1, aIndex = a1;
            transist[0] = fromColor;
            for (int i=1; i<transist.length-1; i++){
                rIndex += rInc;
                gIndex += gInc;
                bIndex += bInc;
                aIndex += aInc;
                transist[i] = new Color( (int) Math.round(rIndex), (int) Math.round(gIndex), (int) Math.round(bIndex), (int) Math.round(aIndex));
            }
            transist[transist.length-1] = toColor;
            return transist;
        }
        
        /**
         * Initialize the inverseColor indexing Array
         */
        private synchronized static void initInverseColors(){
            inverseColor = new int[256];
            for (int i=0, j=255; i<256; i++, j--)
                inverseColor[i] = j;
        }
        
        /**
         * Create a gradient background from c1 to c2 from left to right.
         * @param c1 the first color
         * @param c2 the last color
         */
        public synchronized void linearGradient(Color c1, Color c2){
            Color[] colors = transist(c1, c2);
            double linesPerColor = size[0] / (colors.length+.0);
            double nextTransist = linesPerColor;
            int colorIndex = 0;
            erase();
            for (int i=0; i<size[0]; i++){
                if (i > nextTransist){
                    colorIndex++;
                    nextTransist += linesPerColor;
                }
                drawLine(colors[Math.min(colorIndex, colors.length-1)], i, 0, i, size[1]);
            }
            if (listenersAdded)
                resetAll();
            repaint();
        }
    
    //      END COLOR
    //--------------------------------------------------------------------------
    //      FONT
    
        /**
         * Set the current Font of the Graphics2D.
         * @param newFont the new Font object
         */
        public synchronized void setFont(Font newFont){
            g2d.setFont(newFont);
        }
        
        /**
         * Get the current Font of the Graphics2D.
         * @return the current Font
         */
        public synchronized Font getFont(){
            return g2d.getFont();
        }
    
    //      END FONT
    //--------------------------------------------------------------------------
    //      MOUSE STUFF
        
        /**
         * Add a ChangeItem to the list.
         * @param ci the ChangeItem to add
         */
        public void addChangeItem(ChangeItem ci){
            changers.add(ci);
            ci.setCanvas(this);
            ci.mouseOut();
            if (!listenersAdded){
                cp.addMouseListener(this);
                cp.addMouseMotionListener(this);
                listenersAdded = true;
            }
            repaint();
        }
        
        /**
         * Remove a ChangeItem from the list.
         * @param ci the ChangeItem to remove
         */
        public void removeChangeItem(ChangeItem ci){
            changers.remove(ci);
            ci.erase();
            if (changers.size() == 0){
                cp.removeMouseListener(this);
                cp.removeMouseMotionListener(this);
                listenersAdded = false;
            }
            repaint();
        }
        
        /**
         * The mouse was released, invoke "click()" on the correct ChangeItem.
         */
        public void mouseReleased(MouseEvent me){
            ChangeItem ci = getItem(me.getX(), me.getY());
            if (ci != null)
                ci.click();
        }
        
        /**
         * The mouse was moved, highlight the correct ChangeItem.
         */
        public void mouseMoved(MouseEvent me){
            updateCurrent(me);
            repaint();
        }
        
        /**
         * The mouse was dragged, highlight the correct ChangeItem.
         */
        public void mouseDragged(MouseEvent me){
            updateCurrent(me);
            repaint();
        }
        
        /**
         * The mouse entered the canvas, highlight the correct ChangeItem.
         */
        public void mouseEntered(MouseEvent me){
            updateCurrent(me);
            repaint();
        }
        
        /**
         * The mouse exited the canvas, reset all ChangeItems.
         */
        public void mouseExited(MouseEvent me){
            //updateCurrent(me);
            resetAll();
            repaint();
        }
        
        /**
         * This method highlights just the ChangeItem that the MouseEvent
         * is over.
         * @param me MouseEvent from the mouse
         */
        private void updateCurrent(MouseEvent me){
            int x= me.getX(), y = me.getY();
            resetAll();
            ChangeItem ci = getItem(x, y);
            if (ci != null)
                ci.mouseOver();
        }
        
        /**
         * Reset all the ChangeItems to the default setting
         */
        public void resetAll(){
            Iterator<ChangeItem> it = changers.iterator();
            while(it.hasNext())
                ((ChangeItem)it.next()).mouseOut();
        }
        
        /**
         * Get the ChangeItem that contains the point (x, y)
         * @param x x position
         * @param y y position
         */
        private ChangeItem getItem(int x, int y){
            Iterator<ChangeItem> it = changers.iterator();
            while(it.hasNext()){
                ChangeItem ci = (ChangeItem) it.next();
                if (ci.contains(x, y))
                    return ci;
            }
            return null;
        }
    
    //      END MOUSE STUFF
    //--------------------------------------------------------------------------
    //      MISC
    
        /**
         * Erase the entire canvas
         */
        public synchronized void erase(){
            g2d.setColor(bgc);
            g2d.fill(new Rectangle(0, 0, size[0], size[1]));
        }
        
        public static synchronized ChangeItem getChangeItem(Action a){
            return new ChangeItem(a);// {public void click(a){a.act();}};
        }
    
    //      END MISC
    //--------------------------------------------------------------------------
    
//     /************************************************************************
//      * Nested class CanvasPane - the actual canvas component contained in the
//      * Canvas frame. This is essentially a JPanel with added capability to
//      * refresh the image drawn on it.
//      */
//     protected class CanvasPane extends JPanel{
//         
//         /**
//          * Repaint the canvas image on the canvas pane
//          */
//         public void paint(Graphics g){
//             g.drawImage(ci, 0, 0, null);
//         }
//     }
    
    /************************************************************************
     * Nested interface Action - This class will go into a ChangeItem and call
     * the act method when clicked.
     */
    public interface Action{
        /**
         * Perform some code.
         */
        public void act();
    }

    /************************************************************************
     * Nested class ChangeItem - Class used to create virtual buttons on the
     * Canvas that can change color. The abstract method click(); is the one
     * that is invoked when the "button" is pressed.
     */
    public static class ChangeItem
    {
        private CanvasMax canvas;
        private Color on_Color, offColor;
        private Shape bounds;
        private boolean over = false, useOn_Text, useOffText, useOn_Color, useOffColor;
        private String on_Text, offText;
        private Font on_Font, offFont;
        private Color on_Text_Color, offText_Color;
        private int[] on_Point = new int[2], offPoint = new int[2];
        private Action a;
        
        public ChangeItem(Action a){
            this.a = a;
        }
    
        // Accessor methods
        
        /**
         * Does this button contain the point?
         * @param x x position
         * @param y y position
         * @return whether the point is contained in the button
         */
        public boolean contains(int x, int y){
            return bounds.contains(x, y);
        }
        
        /**
         * Returns whether the mouse is over this button.
         */
        public boolean over(){
            return over;
        }
    
        // Init methods
        /**
         * Set the canvas this button will be on.
         * @param c CanvasMax this button will draw on
         */
        public void setCanvas(CanvasMax c){
            this.canvas = c;
        }
        
        /**
         * Set the shape the button will take.
         * @param s shape of the button
         */
        public void setShape(Shape s){
            bounds = s;
        }
        
        /**
         * Set the text of the button.
         * @param on the text of the button when the mouse is on it, null for none
         * @param off the text of the button the the mouse is not on it, null for none
         */
        public void setText(String on, String off){
            on_Text = on;
            useOn_Text = on!=null;        
            offText = off;
            useOffText = off!=null;
        }
        
        /**
         * Set the Colors of the text
         * @param on color of the text when mouse is over button
         * @param off color of the text when mouse is not over button
         */
        public void setTextColors(Color on, Color off){
            on_Text_Color = on;
            offText_Color = off;
        }
        
        /**
         * Set the background color of the button
         * @param on color of the button when mouse is over button
         * @param off color of the button when mouse is not over button
         */
        public void setBGColors(Color on, Color off){
            on_Color = on;
            useOn_Color = on!=null;
            offColor = off;
            useOffColor = off!=null;
        }
        
        /**
         * Set the fonts of the text
         * @param on_Name name of the Font when mouse is over button, ex: "Arial"
         * @param on_Style style of the Font when mouse is over the button, ex: Font.PLAIN
         * @param on_Size size  of the Font when mouse is over the button, ex: 12, 7
         * @param offName name of the Font when mouse is not over button, ex: "Times New Roman"
         * @param offStyle style of the Font when mouse is not over the button, ex: Font.BOLD
         * @param offSize size  of the Font when mouse is not over the button, ex: 14, 5
         */
        public void setFonts(String on_Name, int on_Style, int on_Size, String offName, int offStyle, int offSize){
            this.on_Font = new Font(on_Name, on_Style, on_Size);
            this.offFont = new Font(offName, offStyle, offSize);
        }
        
        /**
         * Set the fonts of the text
         * @param on_Font Font for the text when mouse is over the button
         * @param offFont Font for the text when mouse is not over the button
         */
        public void setFonts(Font on_Font, Font offFont){
            this.on_Font = on_Font;
            this.offFont = offFont;
        }
        
        /**
         * Set the position of the text
         * @param on_X x position of the text when mouse is over the button
         * @param on_Y y position of the text when mouse is over the button
         * @param offX x position of the text when mouse is not over the button
         * @param offY Y position of the text when mouse is not over the button
         */
        public void setTextPos(int on_X, int on_Y, int offX, int offY){
            on_Point[0] = on_X;
            on_Point[1] = on_Y;
            offPoint[0] = offX;
            offPoint[1] = offY;
        }
        
        // Action methods
        /**
         * The method invoked when the mouse is over the button
         */
        public void mouseOver(){
            this.erase();
            if (useOn_Color)
                this.canvas.shape(bounds, on_Color, true, false);
            if (useOn_Text){
                this.canvas.setFont(on_Font);
                this.canvas.drawString(on_Text, on_Text_Color, on_Point[0], on_Point[1]);
            }
            over = true;
        }
        
        /**
         * The method invoked when the mouse leaves the button
         */
        public void mouseOut(){
            this.erase();
            if (useOffColor)
                this.canvas.shape(bounds, offColor, true, false);
            if (useOffText){
                this.canvas.setFont(offFont);
                this.canvas.drawString(offText, offText_Color, offPoint[0], offPoint[1]);
            }
            over = false;
        }
        
        /**
         * Erase the Button
         */
        private void erase(){
            this.canvas.erase(bounds, true, false);
            //String erase = null;
            if (over && useOn_Text)
                this.canvas.eraseString(on_Text, on_Point[0], on_Point[1]);
            else if (!over && useOffText)
                this.canvas.eraseString(offText, offPoint[0], offPoint[1]);
        }
        
        /**
         * The button was clicked. Perform the action.act() code.
         */
        private void click(){
            a.act();
        }
    }
}