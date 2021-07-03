package main;

import control.Spider;

import control.ExampleLeg;
import control.SpiderLeg;


/**
 * This project is a web crawler.
 * It reads data from web pages, and decides which links from each pages to add to a read queue.
 * See and implement SpiderLeg or ExampleLeg (for more detail) on how to customize this program.
 * Modify OUTPUT_FOLDER in Spider if your implementation plans on writing anything to disk.
 */
public class Main
{
	
	/**
	 * Run the program.
	 * @param args Not Used.
	 */
	public static void main(String[] args)
	{
		SpiderLeg leg = new ExampleLeg() {
			protected void pullUrls(String html)
			{
				
			}
		};
			
				
		Spider spider = new Spider(leg);
		
		spider.searchAll();
		
		System.exit(0);
	}
	
	
	/**
	 * Don't need this.
	 */
	private Main()
	{
		
	}
}
