package control;

import java.util.List;

/**
 * SpiderLeg acts as an interface between Spider and the user's implementation.
 */
public abstract class SpiderLeg
{
	
	
	/**
	 * This method returns a list of page(s) to start web crawling on.
	 * @return a list of page(s) to start web crawling on.
	 */
	public abstract List<String> getStartPages();
	
	
	/**
	 * This method does anything the crawler needs to do on any particular web page.
	 * 
	 * @param url The url of the web page.
	 * @return Whether the web page was successfully handled.
	 */
	public abstract boolean handlePage(String url);
	
	
	/**
	 * Get a List of urls to add to the ongoing queue.
	 * @return A List containing zero or more urls.
	 */
	public abstract List<String> getLinks();
	
	
	/**
	 * Perform any final action or cleanup after all pages have been visited.
	 */
	public abstract void conclude();
	
	
	/**
	 * Set the output folder for this SpiderLeg, if necessary.
	 * 
	 * @param outputFolder The folder to output any data to.
	 */
	public void setOutputFolder(String outputFolder)
	{
		
	}
  
}