package squire;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientConnection extends Thread{
	public static void main(String[] args) throws IOException {
		System.out.println("Server started.");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                new ServerThread(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }
	
	
	private static class ServerThread extends Thread {
        private Socket socket;
        private int clientNumber;

        public ServerThread(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }
        
        
        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                // Get messages from the client, line by line; return them
                // capitalized
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.equals(".")) {
                        break;
                    }
                    
                    
                    JSONArray outArr = new JSONArray();
                    try {
						Object inObj = new JSONParser().parse(input);
						JSONArray inArr = (JSONArray)inObj;
						
						
						for (int i = 0; i< inArr.size(); i++)
						{
							JSONObject outObj = new JSONObject();
							
							JSONObject thisObj = (JSONObject)inArr.get(i);
							String firstAction = (String) thisObj.get("action");
							JSONObject firstParams = (JSONObject) thisObj.get("parameters");
							String outString = this.runAction(firstAction, firstParams);
							
							outObj.put("result", outString);
							outObj.put("parameters", firstParams);
							outObj.put("action", firstAction);
							
							outArr.add(outObj);
						}
						
					} catch (ParseException e) {
						System.out.println("Was not able to parse the input");
					}
                    
                    out.println(outArr);	// send the final array back to client
                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Unknown error with sockets");
                }
//                log("Connection with client# " + clientNumber + " closed");
            }
        }
        
        private void log(String message) {
            System.out.println(message);
        }
        
        public String runAction(String action, JSONObject params)
        {
        	String output = null;
        	if (action.compareTo("chat") == 0)
        	{
        		output = new String("hello, world!");
        	}
        	else if (action.compareTo("closeServer") == 0)
        	{
        		System.exit(0);
        	}
        	else
        	{
        		output = new String("No Premade Response");
        	}
        	
        	return output;
        }
	}
}
