package control;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is the engine that power SpiderLeg.
 * To run the crawler, create a Spider with a SpiderLeg parameter, then call searchAll().
 * Modify OUTPUT_FOLDER if you plan on writing anything to disk.
 */
public class Spider
{
    
    private List<String> pagesToVisit;
    private SpiderLeg leg;
    private Progress progress;
    
    // Output folder is only necessary if saving any data locally.
    private final String OUTPUT_FOLDER = "";
    
    
    /**
     * Constructor for objects of class Spider
     * @param leg the SpiderLeg to use.
     */
    public Spider(SpiderLeg leg)
    {
    	leg.setOutputFolder(OUTPUT_FOLDER);
        pagesToVisit = new LinkedList<String>();
        this.leg = leg;
        
        progress = new Progress();
        progress.setTitleAndLoadType("Web Crawler", "URLs");
        
    }
    
    
    /**
     * Retrieve the next url in the queue.
     * @return A String with the next url in the queue.
     */
    private String nextUrl()
    {
        String nextUrl = "";
        if (pagesToVisit.size() > 0)
        {
            nextUrl = pagesToVisit.remove(0);
        }
        
        return nextUrl;
    }
    
    
    /**
     * This method runs the web crawler.
     */
    public void searchAll()
    {
        /**
         * get first url in queue
         * set progress as 0/1
         * start loop
         * send url to leg
         * remove page from queue
         * ask leg for urls, add them to queue.
         * update progress to reflect new queue size
         * wait
         * end loop
         * call leg.conclude();
         */
        
        pagesToVisit.addAll(leg.getStartPages());
        int ind = 0;
        int queueSize = pagesToVisit.size();
        progress.update(ind, queueSize);
        while (pagesToVisit.size() > 0)
        {
            String url = nextUrl();
            int added = 0;
            if (leg.handlePage(url))
            {
                List<String> toAdd = leg.getLinks();
                added = toAdd.size();
                pagesToVisit.addAll(leg.getLinks());
            }
            queueSize += added;
            progress.update(++ind, queueSize);
            
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ie)
            {}
        }
        
        leg.conclude();
    }
    
}
