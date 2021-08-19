package display;
import java.awt.Font;
import java.awt.FontMetrics;

public class BFont extends Font
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2429469532393020409L;
	
	private FontMetrics fm;
    
    public BFont(String name, int type, int size){
        super(name, type, size);
    }
    
    public FontMetrics getFontMetrics(){
        if (fm == null)
            fm = new FM((Font)this);
        return fm;
    }
}