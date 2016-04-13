package squire;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
		params.put("testParam", "myTestParamValue");
		params.put("testParam2", "myTestParamValue2");
		
		// Set the category
		String category = "FILE";
		
		// Set the action
		String action = "CREATEFILE";
		
		// Send stuff to the server, await response.
		String returnValue = (String) server.sendSingleRequest(category, action, params);
		System.out.println(returnValue);
		// Get the First result's object
//		JSONObject firstRow = (JSONObject) returnValue.get(0);
		
		// Get the second result
//		JSONObject secondRow = (JSONObject) returnValue.get(1);
	}
}
