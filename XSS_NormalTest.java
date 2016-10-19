package WebScanner;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class XSS_NormalTest {
	public static String url = "";			//url
	
	public static String params = "";		    
	public static String method = "";			//method POST/GET
	
	public static ArrayList<String> testRecord = new ArrayList<String>();   //format : URL?param=normalData&otherparam
	public static ArrayList<String>  test = new ArrayList<String>() ; //test for the presence of an array of legitimate response vectors
	//stored XSS vulnerabilities that may exist in the url , pending further testing
	//format:Inputurl?param1=xx&param2=xx#outputurl#method=POST/GET
	//save the original page's content hash
	public static HashMap<String, String> Hash1 = new HashMap<String,String>();
	//save after test the page's  hash
	public static HashMap<String, String> Hash2 = new HashMap<String,String>();
	//save the page content has changed pages
	public static HashMap<String , String> changeHash = new HashMap<String,String>();
	//save response not contain testData£¬format:url?param=normalData&otherpparam
	public static ArrayList<String> furtherTest = new ArrayList<String>(); 
	//Save website url and parameters of all pages ,format :url?name1=xx&name2=xx&method=POST/GET
	public static ArrayList<String> URLTable = new ArrayList<String>(); 
	public static String randomString = "123456789";
	public static void main(String[] args) throws IOException {
		
		 HtmlParser htmlParser = new HtmlParser();
		 Hash1 = htmlParser.getPageHash();
		 //get information,include url and parameters
		 URLTable = htmlParser.getUrlTable();
		 
		
		 SendRequest sendRequest = new SendRequest();
		 for(String templine : URLTable){
			 ArrayList<String> XSSparams = new ArrayList<String>();    //the parameter might exist XSS
			 XSSparams = SegURL(templine);
			 //When a page exists submit multiple parameters , each parameter for each test
			 if(method == "POST"){
				 for(int i = 0;i < XSSparams.size();i++){
					 String finalParam =  MakeNormalParams(XSSparams,XSSparams.get(i),randomString);  
					 testRecord.add(url+"?"+finalParam);	
					 String response=sendRequest.sendPost(url,finalParam);
					 //Determine whether there is a legitimate response to the vector ,
	// or -1 if the response does not contain a valid vector , and the corresponding record of the URL to a legitimate vector in furtherTest
					 if((response.indexOf(randomString)) == -1){
						// System.out.println("further : ");
						 furtherTest.add(url+"?"+finalParam);
					 }else{
						  //Vector exist legitimate response, it is stored in the test list , stand- attack vector detection
						 test.add(url+"?"+finalParam+"#"+"response"+"#"+"method=POST");  
					 }
					} 
				 
			 }else if(method == "GET"){
				 for(int i = 0;i < XSSparams.size();i++){
					 String finalParam =  MakeNormalParams(XSSparams,XSSparams.get(i),randomString); 
					 testRecord.add(url+"?"+finalParam);								
					 String response=sendRequest.sendGet(url,finalParam);
					 //Determine whether there is a legitimate response to the vector , or -1 if the page does not exist XSS
					 if(response.indexOf(randomString)>0){
						// System.out.println("test : GET");
						 test.add(url+"?"+finalParam+"#"+"response"+"#"+"method=GET");    //Vector exist legitimate response, is stored in the test list,
					} //if
				 }//for
			 }
		 }//while
		 
		 //The page will be saved HASH1 recalculated hash value Hash2,
		 Hash2 = htmlParser.getPageHash();
		 
		 //Compare hash1 and hash2 the URL and the same URL HASH value changes in the page table stored in changeHash
		 for(String key1 : Hash1.keySet())
			 for(String key2:Hash2.keySet()){
				 if(key1 == key2)
				 {
					 if(! Hash1.get(key1).equals(Hash2.get(key2))){
						 changeHash.put(key2, Hash2.get(key2));
					 }
				 }
					
			 }
		 
		 //The changeHash table and furtherTest match , according to the legal uniqueness of the vector
		 compare();
		   
	    }
	 
	 
	 //The changeHash table and furtherTest match , according to the legal uniqueness of the vector
	 public static void compare(){
		 String str = "";
		 for(String temp : furtherTest){
			 String result[] = temp.split("\\?");
			 result = result[1].split("&");
			 result = result[0].split("="); 
			 for(String key:changeHash.keySet()){
					str = changeHash.get(key);
					 if(str.contains(result[1])){
						 test.add(temp+"#"+key+"#"+"method=POST");
					 }
			 }   
		 }
	 }
	 
	 
	 //Back XSS vulnerabilities that may exist legal page information obtained detection phase
	 public ArrayList<String> getTest(){
		 return test;
	 }
	
	
	public static ArrayList<String> SegURL(String templine){
		ArrayList<String> XSSparams = new ArrayList<String>();   
		 String result[] = templine.split("\\?");
		 url = result[0];                //result[1] is parameters
		 result = result[1].split("&");  //parameters depart with "&"
		 for(int i = 0;i < result.length-1;i++){
			 if(!result[i].equals("")){
				 String temp[] = result[i].split("=");        //get parameters and value
				 XSSparams.add(temp[0]);				
			 } 
		 }//for
		
		 if(templine.contains("POST"))
			 method= "POST";
		 else
			 method = "GET";
		 return XSSparams;
		 
	}
	
	
	public static String MakeNormalParams(ArrayList<String>XSSparams,String param,String randomString){
		String finalParams = param + "=" + randomString;
		for(int i = 0;i < XSSparams.size();i++){
			if(!(XSSparams.get(i).equals(param))){
				finalParams +="&" + XSSparams.get(i) + "=" +"";
			}
		}
		
		return finalParams;
	}

}
