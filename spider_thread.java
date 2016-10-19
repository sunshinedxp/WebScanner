package WebScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.http.client.ClientProtocolException;



public class spider_thread
{
	private static HashMap<String, String> page = new HashMap<String,String>();   //save the page content
	private static Map<String, Boolean> oldURL = new LinkedHashMap<String, Boolean>();   //老url
	
	private static String RootURL = "";
	
	public static void main(String[] args) throws ClientProtocolException, IOException
	{
		String url = "http://139.129.30.243/XSSTest";
//		System.out.println("Please input url : ");
//		@SuppressWarnings("resource")
//		Scanner in = new Scanner(System.in);
//		url = in.nextLine();
		RootURL = url;
		oldURL.put(url, false);
		oldURL = crawlLinks();	

		HashMap<String, String> temppage = new HashMap<String,String>();
		for(String temp:page.keySet()){
			if((temp.indexOf("?")) > 0){
				temppage.put(temp.split("\\?")[0],page.get(temp));
			}else{
				temppage.put(temp,page.get(temp));
			}
			
		}
		page = temppage;
		HtmlParser htmlParser = new HtmlParser();
		htmlParser.parseHtml();
		//start a new thread for XSS
		XSS XSSThead = new XSS();
		XSSThead.start();
		//start a new thread for SQL
		Start.main(args);
		
	}
	
	
	public HashMap<String, String> getPage(){
		return page;
	}
	
	
	//All pages breadth search site
		public static Map<String, Boolean> crawlLinks() 
		{
			Map<String, Boolean> newURL = new LinkedHashMap<String, Boolean>();   //新url
			String line ="";
			for (Map.Entry<String, Boolean> url : oldURL.entrySet()) {
				if(!url.getValue()){
					line = getHtml(url.getKey());
					//new HtmlParser();
					ArrayList<String> newLinks = HtmlParser.findHref(line);  //为该页面检测到的所有链接
					//Judge the url start with "http"  
					for(String newLink:newLinks){
						if (!newLink.startsWith("http")) 
						{
							if (newLink.startsWith("/"))
							{
								newLink = RootURL + newLink;
							}
							else
							{
								newLink = RootURL + "/" + newLink;
							}
						}//if
							
						//Removing the end of the url /
						if(newLink.endsWith("/"))
						{
							newLink = newLink.substring(0, newLink.length() - 1);
						}
						//Deduplication，And discards the links to other sites
						if (!oldURL.containsKey(newLink) && !newURL.containsKey(newLink) && newLink.startsWith(RootURL))
						{
							newURL.put(newLink, false);
						}
					}//for
					oldURL.put(url.getKey(), true);//set the current url ture
				}//if
			}//for
			//have new url
			if(!newURL.isEmpty()){
				oldURL.putAll(newURL);
				oldURL.putAll(crawlLinks());
			}
			return oldURL;
			
		}
		
	//get html
	public static String getHtml(String Url)
	{
		//launch GET request
    		try 
    		{
    			URL url = new URL(Url);
    			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    			connection.setRequestMethod("GET");
    			connection.setConnectTimeout(2000);
    			connection.setReadTimeout(2000);
    			
	    		connection.connect();  

    			if (connection.getResponseCode() == 200) 
    			{
    				InputStream inputStream = connection.getInputStream();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    				String line = "";
    				String tempLine = "";
    				while ((line = reader.readLine()) != null) 
    				{
    					tempLine += line +"\n";
    				}
    				page.put(Url, tempLine);            //save url and content
    				return tempLine;
    			}
    		}
    		catch (MalformedURLException e) 
    		{
    			e.printStackTrace();
    		} 
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    		
    		try 
    		{
    			Thread.sleep(1000);
    		} 
    		catch (InterruptedException e) 
    		{
    			e.printStackTrace();
    		}
    	return "";
	}
	
	
}