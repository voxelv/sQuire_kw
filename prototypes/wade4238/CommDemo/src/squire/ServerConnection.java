package squire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ServerConnection {
	PrintWriter out;
	BufferedReader in;
	JSONArray requestBuffer;
	Boolean busy;
	
	public ServerConnection(String server, int port)
	{
		busy = false;
		requestBuffer = new JSONArray();
		try {
			this.connectToServer(server, port);
		} catch (IOException e) {
			System.out.println("Error: Was not able to connect to server");
			System.exit(0);
//			e.printStackTrace();
		}
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
        try {
            response = in.readLine();
            if (response == null || response.equals("")) {
                  System.exit(0);
              }
        } catch (IOException ex) {
               response = "Error: " + ex;
        }
//        System.out.println("Response from Server: " + response);
        
        Object output;
        output = JSONValue.parse(response);
        
        return output;
	}
	
	public Object sendSingleRequest(String category, String action, JSONObject parameters)
	{
		// If the connection is busy, sleep 1ms at a time until it isn't
		while (busy)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		busy = true;
		this.addRequest(category, action, parameters);
		
		JSONArray fullResponse = (JSONArray) this.sendRequestBuffer();
		busy = false;
		
		JSONObject singleResponse = (JSONObject) fullResponse.get(0);
		
		Object out = (Object) singleResponse.get("result");
		return out;
	}
}
