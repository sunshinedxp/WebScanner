package WebScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class XSS_AttackDectect {
	
	//the pages maight be exist XSS
	public static ArrayList<String> test = new ArrayList<String>();
	public static String InputUrl = "";			//InputUrl
	public static String OutputUrl = "";		//OutputUrl
	public static String method = "";			//method :POST/GET
	public static String param = "";			//the test parameters
	public static String otherParams = "";      //other parameters
	
	
	//format:InputUrl:url?param=normalData&otherparameter#OutputUrl#method=POST/GET
	public static void main(String[] args){
		XSS_NormalTest normalTest = new XSS_NormalTest();
		test = normalTest.getTest();
		for(int i = 0; i< test.size();i++){
			//System.out.println( "TEST : "+test.get(i));
			SegURL(test.get(i));//split string
			
			//XSS playload
			String fileName = "XSS.txt";
			File file = new File(fileName);
			InputStreamReader reader;
			try {
				reader = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(reader);
				try {
					String templine = "";
					SendRequest sendRequest = new SendRequest();
					String responseText = "";
					while((templine = bufferedReader.readLine())!= null){
						if(method == "POST")
							if(OutputUrl == "response"){ 
								responseText = sendRequest.sendPost(InputUrl, param+"="+templine+"&"+otherParams);
								//judge XSS 
								if(JudgeXSS(responseText,param,templine)){
									//output result and break
									scannResult(InputUrl,param);
									break;
								}
							}else{
								responseText = sendRequest.sendGet(OutputUrl, "");
								//judge XSS
								if(JudgeXSS(responseText,param,templine)){
									//output result and break
									scannResult(InputUrl,param);
									break;
								}
							}
						else if(method == "GET"){
							responseText = sendRequest.sendGet(InputUrl,  param+"="+templine+"&"+otherParams);
							//judge XSS
							if(responseText.indexOf(templine) > 0){
								//output result and break
								scannResult(InputUrl,param);
								break;
							}
						}		
					}//while
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}//for
	}
	
	//ÅÐ¶ÏÊÇ·ñ´æÔÚÌØÊâ×Ö·û
	public static boolean JudgeXSS(String response,String XSSparam,String XSSPlayload){
		response.replaceAll("\"", "");              //replace all the " 
		response.replaceAll(">", "");               //repalce all the >
		response.replaceAll("<", "");               //replace all the <
		response.replaceAll("/", "");				//replace all the /
		String result[] = response.split(" ");      //split response with blank
		for(String str:result ){
			if(str.contains(XSSparam)){
				String temp = str.split("=")[1];
				if(!temp.isEmpty()){
					if(temp.equals(XSSPlayload)){
						return true;     //exist XSS
					}
					else if(temp.contains("alert") || temp.contains("javascript")
							||temp.contains("href") || temp.contains("onfocus")
							||temp.contains("<a")||temp.contains("onerror")||
							temp.contains("onmouseover") || temp.contains("script")){
						return true;           //exist XSS
					}
				}
				
			}
		}
		return false;   //Doesn't exist XSS
	}
	
	//output result 
	public static void scannResult(String url,String param){
		try {
			FileWriter writer = new FileWriter("XSSresult.txt",true);
			writer.write(url+"£¿ the param : "+param+" might be exist XSS"+"\r\n");
			System.out.println(url+"£¿ the param :\n"+param+" might be exist XSS"+"\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//split string :url?param=xx&param2=xx&param3xx#OutUrl#method=POST/GET
	public static void SegURL(String temp){
		 String result[] = temp.split("\\?");
		 //get inputURL
		 InputUrl = result[0];
		 result = result[1].split("&");
		 param = result[0].split("=")[0];
		 
		 result = temp.split("#");
		 OutputUrl = result[1];
		 if(result[2].indexOf("POST") > 0){
			 method = "POST";
		 }else{
			 method = "GET";
		 }
		 result = result[0].split("&");
		 for(int i = 1;i < result.length;i++){
			 otherParams += result[i];
		 }
		 
	}
	
}
