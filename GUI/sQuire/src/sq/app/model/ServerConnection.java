package sq.app.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ServerConnection {
	PrintWriter out;
	BufferedReader in;
	JSONArray requestBuffer;
	Boolean busy;
	Boolean Alive;
	
	public ServerConnection(String server, int port)
	{
		this.Alive = true;
		this.busy = false;
		this.requestBuffer = new JSONArray();
		try {
			this.connectToServer(server, port);
		} catch (IOException e) {
			System.out.println("Error: Was not able to connect to server");
			this.Alive = false;
			System.exit(0);
//			e.printStackTrace();
		}
	}
	
	public Boolean getStatus(){
		return this.Alive;
	}
	
	public void closeIt(){
		this.Alive = false;
	}

	private void connectToServer(String serverAddress, int port) throws IOException 
	{

        // Make connection and initialize streams
        Socket socket = new Socket(serverAddress, port);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Consume the initial welcoming messages from the server
        for (int i = 0; i < 3; i++) {
        	System.out.println(in.readLine() + "\n");
        }
    }
	
	public void addRequest(String category, String action, JSONObject parameters) 
	{
		JSONObject obj = new JSONObject();

		obj.put("category", category);
        obj.put("action", action);
        obj.put("parameters", parameters);
        
        requestBuffer.add(obj);
        
//        System.out.println(requestBuffer);
	}
	
	public Object sendRequestBuffer()
	{
		out.println(requestBuffer);
		requestBuffer.clear();
		
        String response;
        Object output = null;
        try {
            response = in.readLine();
            if (response == null) {
                  System.out.println("No Response from server, but I'm still running");
              }else{
                  output = JSONValue.parse(response);
            	  if (!(response.contains("Chat") || 
            			  new String(response).equals("[]")) || 
            			  new String(response).equals("getLineLocks")){
            		  System.out.println("Response from Server: " + response.toString());
            	  }
              }
        } catch (IOException ex) {
               response = "Error: " + ex;
        }
        
        return output;
	}
	
	public Object sendSingleRequest(String category, String action, JSONObject parameters)
	{
		// If the connection is busy, sleep 1ms at a time until it isn't
		synchronized (this) {
			
//		}(busy)
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//		busy = true;
		this.addRequest(category, action, parameters);
		if (!(new String(category).equals("Chat") || new String(action).equals("getLineChanges") || new String(action).equals("getLineLocks"))){
			System.out.println(category.toString() + action.toString() + parameters.toString());
		}
		
		JSONArray fullResponse = (JSONArray) this.sendRequestBuffer();
		busy = false;
		
		Object out = null;
		if (fullResponse!=null && fullResponse.size()>0){
			JSONObject singleResponse = (JSONObject) fullResponse.get(0);
			return (Object) singleResponse.get("result");
		}
		}
		return null;
	}
}