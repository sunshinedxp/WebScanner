package WebScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class SendRequest {
    //send GET request,format:name1=value&name2=values
    public  String sendGet(String url, String param) {
        String response = "";						
        BufferedReader in = null;				
        try {
            String urlNameString = url + "?" + Encode(param);         
            URL realUrl = new URL(urlNameString);
            // open connection
            URLConnection connection = realUrl.openConnection();
            // set property
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // connect
            connection.connect();
            // read response
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String templine = "";
            while ((templine = in.readLine()) != null) {
            	response += templine+'\n';
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
     
      //close input stream
        try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
       
        return response;
    }

    
    //send post request,format:name1=value&name2=value
    public String sendPost(String url, String param) {
        PrintWriter out = null;							
        BufferedReader in = null;                       
        String response = "";
        try {
            URL realUrl = new URL(url);
            // open connection
            URLConnection conn = realUrl.openConnection();
            // set property
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // get response
            out = new PrintWriter(conn.getOutputStream());
            // set parameters
            out.print(Encode(param));
            // flush
            out.flush();
            // read response
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String templine="";
            while ((templine = in.readLine()) != null) {
            	response += templine+'\n';
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        close output
         out.close();
         
         //close input stream
         try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
       
        return response;
    }   
    
    
    //htmlcode parameter
    @SuppressWarnings("deprecation")
	public static String Encode(String params){
    	String results[] = params.split("&");
    	String tempResult[]=null;
    	String query = "";
    	for(String result : results){
    		tempResult = result.split("=");
    		query += tempResult[0];
    		for(int i = 1 ;i < tempResult.length;i++){
    			query += "="+URLEncoder.encode(tempResult[i]);
    		}
    		query += "&";
    	}
    	
    	return query.substring(0, query.length()-1); 
    }
    

}
