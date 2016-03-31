package squire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server extends Thread{
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
	
	
	/* START OF THE THREAD CLASS */
	/* */ 
	private static class ServerThread extends Thread {
        private Socket socket;
        private int clientNumber;
        private ChatManager chatManager;

        public ServerThread(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
            
            chatManager = new ChatManager();
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
							String category = (String) thisObj.get("category");
							String firstAction = (String) thisObj.get("action");
							JSONObject firstParams = (JSONObject) thisObj.get("parameters");
							String outString = this.runAction(category, firstAction, firstParams);
							
							outObj.put("category", category);
							outObj.put("result", outString);
							outObj.put("parameters", firstParams);
							outObj.put("action", firstAction);
							
							outArr.add(outObj);
						}
						
					} catch (ParseException e) {
						log("Was not able to parse the input");
					} catch (SQLException e) {
						log("SQL Exception");
						e.printStackTrace();
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
        
        public String runAction(String category, String action, JSONObject params) throws SQLException
        {
        	String output = null;
        	
        	/* CHAT FUNCTIONS */
        	if (category.toUpperCase().compareTo("CHAT") == 0)
        	{
        		if (action.toUpperCase().compareTo("GETMESSAGES") == 0)
        		{
        			String lastMID = (String) params.get("lastMID");
        			String userID = (String) params.get("userID");
        			JSONArray t;
        			t = chatManager.getMessages(userID, lastMID);
        			output = t.toJSONString();
        		}
        		else if (action.toUpperCase().compareTo("LEAVECHANNEL") == 0)
        		{
        			JSONArray t;
        			t = chatManager.leaveChannel(0, 0);
        			output = t.toJSONString();
        		}
        		else if (action.toUpperCase().compareTo("JOINCHANNEL") == 0)
        		{
        			JSONArray t;
        			t = chatManager.joinChannel(0, 0);
        			output = t.toJSONString();
        		}
        		else if (action.toUpperCase().compareTo("ADDMESSAGE") == 0)
        		{
        			String userID = (String) params.get("userID");
        			String msg = (String) params.get("msg");
        			String channelID = (String) params.get("channelID");
        			
        			chatManager.addMessage(userID, msg, channelID);
        		}
        		else if (action.toUpperCase().compareTo("QUIT") == 0 || action.toUpperCase().compareTo("EXIT") == 0)
        		{
        			System.exit(0);
        		}
        			
        		
        	}
        	/* FILE FUNCTIONS */
        	else if (category.toUpperCase().compareTo("FILE") == 0)
        	{
        		
        	}
        	/* UNKNOWN/NO CATEGORY */
        	else
        	{
        		output = new String("No Premade Response");
        	}
        	
        	return output;
        }
	}
}
