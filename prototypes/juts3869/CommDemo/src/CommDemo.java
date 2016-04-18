


import java.io.OutputStreamWriter;
import java.io.Writer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CommDemo {
	
	static ServerConnection server;
	static int userID = 0;
	
	public static void main(String[] args)
	{
		// Create the connection to the server (There should only be one 
		// connection for the client, so this should be passed into your 
		// class in the constructor
		server = new ServerConnection("squireRaspServer.ddns.net", 9898);
		
		fakeLogin();
		
		// Create the parameter object
		JSONObject params = new JSONObject();
		
		// Set all the parameters you need
		params.put("projectID", "21");
		
		// Set the category
		String category = "Project";
		
		// Set the action
		String action = "getAllLines";
		
		// Send stuff to the server, await response.
		String returnValue = (String) server.sendSingleRequest(category, action, params);
		System.out.println(returnValue);
		// Get the First result's object
		
		// If you're expecting a JSONArray or JSONObject, do this...
		if (0 == 0)
		{
			Object returnObj;
			JSONArray outJSONArray;
			JSONObject outJSONObject, JObj;
			try {
				// Parse the object
				returnObj = new JSONParser().parse(returnValue);
				
				// if it's a JSONArray, cast output as JSONArray
				outJSONArray = (JSONArray) returnObj;
				
				// if it's JSONObject, cast output as JSONObject
				//outJSONObject = (JSONObject) returnObj;
				
				System.out.println(outJSONArray.size()+"\n");
				
				// Use get() to get specific properties from the return ----ARRAY----
				JObj = (JSONObject) outJSONArray.get(0);
				
				
				// Use get() to get specific properties from the return ----OBJECT----
				System.out.println("PID "+(String)JObj.get("PID")+"\n");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    public static void fakeLogin()
    {
    	
    	/**************************** START OF REQUEST ****************************/
    	JSONObject params = new JSONObject();		// Create parameter object

    	params.put("username", "JCJ");
    	params.put("password", "password");
    	
    	//String fName = (String) params.put("fName", "J");
		//String lName = (String) params.put("lName", "J");
		//String uName = (String) params.put("uName", "JCJ");
		//String email = (String) params.put("email", "jcjutson@gmail.com");
		//String pWord = (String) params.put("pWord", "password");
    	
    	
        String result = (String) server.sendSingleRequest("User", "Login", params);
    	
        JSONObject loginObj = null;
		try {
//			System.out.println(stringResult);
			loginObj = (JSONObject) new JSONParser().parse(result);
			
			userID = Integer.parseInt((String) loginObj.get("userID"));
			System.out.println(Integer.toString(userID));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
        
        if (userID > 0)
        {
        	
        }
        else
        {
        	System.exit(0);
        }
        
        
        /**************************** END OF REQUEST ****************************/
    }
}
