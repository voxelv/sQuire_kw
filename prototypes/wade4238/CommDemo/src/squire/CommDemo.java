package squire;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CommDemo {
	
	
	public static void main(String[] args)
	{
		// Create the connection to the server (There should only be one 
		// connection for the client, so this should be passed into your 
		// class in the constructor
		ServerConnection server;
		server = new ServerConnection("squireRaspServer.ddns.net", 9898);
		
		// Create the parameter object
		JSONObject params = new JSONObject();
		
		// Set all the parameters you need
		params.put("fileID", "1");
		
		// Set the category
		String category = "project";
		
		// Set the action
		String action = "getlines";
		
		// Send stuff to the server, await response.
		String returnValue = (String) server.sendSingleRequest(category, action, params);
		System.out.println(returnValue);
		// Get the First result's object
		
		int i = 1;
		// If you're expecting a JSONArray or JSONObject, do this...
		if (i == 0)
		{
			Object returnObj;
			JSONArray outJSONArray;
			JSONObject outJSONObject;
			try {
				// Parse the object
				returnObj = new JSONParser().parse(returnValue);
				
				// if it's a JSONArray, cast output as JSONArray
				outJSONArray = (JSONArray) returnObj;
				
				// if it's JSONObject, cast output as JSONObject
				outJSONObject = (JSONObject) returnObj;
				
				// Use get() to get specific properties from the return ----ARRAY----
				outJSONArray.get(0);
				
				// Use get() to get specific properties from the return ----OBJECT----
				outJSONObject.get("returnObjectKey");
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		JSONObject firstRow = (JSONObject) returnValue.get(0);
		
		// Get the second result
//		JSONObject secondRow = (JSONObject) returnValue.get(1);
	}
}
