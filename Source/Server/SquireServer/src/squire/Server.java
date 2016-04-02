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



/**
 * @author Grant Wade
 * 
 */
public class Server{
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
        private int userID = 0;

        public ServerThread(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
            
            chatManager = new ChatManager();
            
            chatManager.setUserID(this.userID);
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
        	
        	/************************** CHAT FUNCTIONS **************************/
        	if (category.compareToIgnoreCase("CHAT") == 0 && this.userID > 0)
        	{
        		if (action.compareToIgnoreCase("GETMESSAGES") == 0)
        		{
        			String lastMID = (String) params.get("lastMID");
        			JSONArray t;
        			t = chatManager.getMessages(lastMID);
        			output = t.toJSONString();
        		}
        		else if (action.compareToIgnoreCase("LEAVECHANNEL") == 0)
        		{
        			JSONArray t;
        			t = chatManager.leaveChannel(0);
        			output = t.toJSONString();
        		}
        		else if (action.compareToIgnoreCase("JOINCHANNEL") == 0)
        		{
        			JSONArray t;
        			t = chatManager.joinChannel(0);
        			output = t.toJSONString();
        		}
        		else if (action.compareToIgnoreCase("ADDMESSAGE") == 0)
        		{
        			String msg = (String) params.get("msg");
        			String channelID = (String) params.get("channelID");
        			
        			chatManager.addMessage(msg, channelID);
        		}
        		else if (action.compareToIgnoreCase("QUIT") == 0 || action.compareToIgnoreCase("EXIT") == 0)
        		{
        			System.exit(0);
        		}
        	}
        	
        	
        	/************************** LOGIN FUNCTIONS **************************/
        	else if (category.compareToIgnoreCase("LOGIN") == 0)
        	{
        		if (action.compareToIgnoreCase("Login") == 0)
        		{
        			// login, change the userID attached to this thread, and the chat manager
        			this.userID = 1;	// Temp
        			chatManager.setUserID(this.userID);
        		}
        	}
        	
        	
        	/************************** FILE FUNCTIONS **************************/
        	else if (category.compareToIgnoreCase("FILE") == 0)
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

/*
 * @hide @startuml
hide circle
hide empty members

Title <b>Server Classes</b>

class "Client" as sq 
class "Server" as sqs{
	-AccountManager
	-ProjectManager
	-SessionManager
	-ChatManager
	+AccountManagerMethods()
	+ProjectManagerMethods()
	+SessionManagerMethods()
	+ChatManagerMethods(GUIDs)
	}
	class "AccountManager" as sqs_ua_m{
		-AccountList
		+CreateAccount(Name, Email)
		+Login(LoginInfo)
		+Logout(UserID)
		+GetUserDetails(Name)
		+SetUserDetails(UserUpdateInfo)
		}
		class "UserAccount" as sqs_ua {
			+first_name
			+last_name
			+user_name
			-password
			+register()
			+login()
			+logout()
			}
	class "ProjectManager" as sqs_pr_m{
		-ProjectList
		+CreateProject(Name)
		+OpenProject(Name)
		+DeleteProject(Name)
		+GetProject(Name)
		+GetProject(ProjectID)
		+UpdateProject(ProjectUpdateInfo)
		}
		class "Project" as sqs_pr{
			-ProjectID
			-ProjectDescription
			-ProjectSettings
			-FileIDs
			-OwnerUserIDs
			+GetProjectID()
			+GetProjectDescription()
			+GetProjectSettings()
			+GetProjectFiles()
			+GetProjectOwners()
			+UpdateProject(Key,Value)
			+UpdateFile(FileUpdateInfo)
			}
			class "File" as sqs_fi{
				-FileID
				-FileName
				-FileDescription
				-FileContent
				+GetFileInfo()
				+UpdateFile(Key,Value)
				}

	class "ChatManager" as sqs_ch_m{
		Uses DBConnector to manipulate the database;
		manages Chat Channels, Chat Messages, etc
		==
		-DBConnector dbc
		-int userID
		__
		+setUserID (int userID) (void)
		+addMessage (String message, String channelID) (void)
		+leaveChannel (String channelName) (JSONArray)
		+leaveChannel (int channelID) (JSONArray)
		+joinChannel (String channelName) (JSONArray)
		+joinChannel (int channelID) (JSONArray)
		+getMessages(String lastMID) (JSONArray)
	}
	
sq -right- sqs : <<TCP>>

	sqs *-- sqs_pr_m 
		sqs_pr_m "1" -- "*" sqs_pr : Project List
			sqs_pr "1" -- "*" sqs_fi : File List
	sqs *-- sqs_ua_m 
		sqs_ua_m "1" -- "*" sqs_ua : User Account List
	sqs *-- sqs_ch_m



@enduml
 */
