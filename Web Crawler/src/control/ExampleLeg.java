package control;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.ReaderWriter;

/**
 * ExampleLeg acts as a basic implementation of SpiderLeg.
 * The workflow for how legs work is as follows.
 * 1. Urls from getStartPages() are added to the queue.
 * 2. First page page from queue is sent to handlePage(String url).
 * 3. Any desired urls from handlePage should be added to pagesToAdd.
 * 4. Urls from getLinks() are added to the queue.
 * 5. Repeat from step 2 until the queue is empty.
 * 6. conclude() is called.
 * 
 * The workflow is handled in Spider, leg just needs to implement these methods.
 * This class has all aspects implemented to some degree, with the exception of
 * Looking at the contents of a web page and determining which links from it to add to pagesToAdd.
 *   --> Implement pullUrls(String html) for that functionality.
 */
public abstract class ExampleLeg extends SpiderLeg
{
	
    private LinkedList<String> startPages;
    private LinkedList<String> pagesToAdd = new LinkedList<String>();
    private LinkedList<String> collectedPages;
    private LinkedList<Integer> pageLengths;
    private ReaderWriter fileIO;
    
    /**
     * Example_Leg constructor.
     * Implement the class to use it.
     */
    public ExampleLeg()
    {
        fileIO = new ReaderWriter();
        collectedPages = new LinkedList<String>();
        pageLengths = new LinkedList<Integer>();
        startPages = new LinkedList<String>();
        populateQueue();
    }
    
    
    /**
     * This method should look at the contents of any web page and pull
     * any appropriate links to be added to the crawl queue.
     * 
     * @param html The contents of a web page.
     */
    protected abstract void pullUrls(String html);
    
    
    /**
     * Fill the startPages LinkedList with any urls.
     */
    private void populateQueue()
    {
    	// This is where any starting pages can be added to the startPages LinkedList.
    	startPages.add("https://docs.oracle.com/javase/10/docs/api/javax/xml/ws/Action.html");
    }
    
    
    /**
	 * This method returns a list of page(s) to start web crawling on.
	 * @return a list of page(s) to start web crawling on.
	 */
	public List<String> getStartPages()
    {
        return startPages;
    }
    
    
    /**
	 * Get a List of urls to add to the ongoing queue.
	 * @return A List containing zero or more urls.
	 */
    public List<String> getLinks()
    {
        return pagesToAdd;
    }
    
    
    /**
	 * This method does anything the crawler needs to do on any particular web page.
	 * getLinks is called after this method, so any urls to add should be added to
	 * pagesToAdd during this method's execution.
	 * 
	 * @param url The url of the web page.
	 * @return Whether the web page was successfully handled.
	 */
    public boolean handlePage(String url)
    {
        String html = readData(url);
        
        if (html != null)
        {
        	collectedPages.add(url);
        	pageLengths.add(Integer.valueOf(html.length()));
        	
        	pullUrls(html);
            return true;
        }
        return false;
    }
    
    
    /**
     * Read a web page.
     * @param url The path of the page.
     * @return The contents of the page.
     */
    private String readData(String url)
    {
        String html = null;
        try 
        {
            html = fileIO.readToString(url).toLowerCase();
        }
        catch (IOException ioe){}
        
        return html;
    }
    
    
    /**
	 * Perform any final action or cleanup after all pages have been visited.
	 */
    public void conclude()
    {
    	System.out.println("Pages visited: ");
    	for (int i=0; i<collectedPages.size(); i++)
    	{
    		System.out.println(collectedPages.get(i) + ", html character count: " + pageLengths.get(i));
    	}
    }
}
