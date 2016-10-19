package WebScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HtmlParser {
	public static String method = "";     //get method :POST/GET
	public static String params = "" ;   //get parameters,depart with &
	public static ArrayList<String> urls = new ArrayList<String>()  ;    //get urls
	public static ArrayList<String> URLTable = new ArrayList<String>(); //save all the url and parameters,	
															//format : url?name1=xx&name2=xx&method=POST/GET

	//找出页面所有的href
	public static ArrayList<String> findHref(String content){
		
		
		int locationHref = 0;   //loaction of href
		int location1 = 0;        //the first location of "
		int location2 = 0;        //the second location of "
		
		Pattern pattern = Pattern.compile("<a.*?href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)</a>");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) 
		{
			String newLink = matcher.group(1).trim(); //url
			content = content.replace(newLink, "");  //delete id already matched content
			urls.add(newLink.split(">")[0]);
		}
		while((locationHref = content.indexOf("href",locationHref+1)) > 0){
			if((location1 = content.indexOf("\"", locationHref)) >0 ){
				if((location2 = content.indexOf("\"", location1+1))>0){
					urls.add(content.substring(location1+1, location2));
					content = content.substring(location2);
				}
			}
		}
		
		return urls;
	}
	
		//return url and parameters，format : url?name1=xx&name2=xx&method=POST/GET
		public ArrayList<String> getUrlTable(){
			return URLTable;
		}
	
		//get the content hash
		public HashMap<String , String > getPageHash(){
			spider_thread spider = new spider_thread();
			HashMap<String , String >  page = spider.getPage();
			HashMap<String , String > hash = new HashMap<String , String >();
			
			for(String temp : page.keySet()){
				hash.put(temp, spider_thread.getHtml(temp));
			}
			return hash;
		}
		
		//parser html,get url and parameters
		public void parseHtml(){
			spider_thread spider = new spider_thread();
			HashMap<String, String> page = spider.getPage();   //save htmlString
			
			for(String url:page.keySet()){
				int location1 = 0;
				int location2 = 0;
				String htmlString = page.get(url);
				//"form" first appear
				if((location1 = htmlString.indexOf("form", location2)) > 0){
					if((location2 = htmlString.indexOf("form", location1+1)) > 0){  //form second appear
						htmlString = htmlString.substring(location1+5, location2-3);  
						method = getMethod(htmlString);    //get method
						params = getParams(htmlString);     //get parameters
					
						URLTable.add(url+"?"+params+"method="+method);
						System.out.println(url+"?"+params+"method="+method);
					}
				}
			}
		}

		//get method
		public String getMethod(String htmlString){
			if(htmlString.contains("GET") || htmlString.contains("get"))
				return "GET";
			return "POST";
		}
		
		//parser string,get all the parameters,depart with "&"
		public String getParams(String htmlString){
			
			String params="";
			//replace all the ",>,/,< with ""
			htmlString = htmlString.replaceAll("\"","");
			htmlString = htmlString.replaceAll("\n","");
			htmlString = htmlString.replaceAll(">","");
			htmlString = htmlString.replaceAll("/","");
			htmlString = htmlString.replaceAll("<","");
			String tempLine[] = htmlString.split(" ");
			for(int i = 0;i < tempLine.length;i++){
				if(tempLine[i].contains("name")){
					params += tempLine[i].split("=")[1]+"=";
					i++;
					if(tempLine[i].contains("value")){
						
						params +=tempLine[i].split("=")[1] + "&";
					}else{
						params +="&";
					}
				}
			}
			
			return params;
			
		}
		
}
