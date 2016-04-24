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
        private AccountManager accountManager;
        private ProjectManager projectManager;
        private DBConnector dbc;
        private int userID = 0;

        public ServerThread(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
            
            // Create the dbconnector
            try {
                dbc = new DBConnector();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            // Set database to squire
            try {
                dbc.setDatabase(dbc.dbName);
            } catch (SQLException e) {
                DBConnector.printSQLException(e);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            
            chatManager = new ChatManager(dbc);
            accountManager = new AccountManager(dbc);
            projectManager = new ProjectManager(dbc);
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
                out.println("Registered as Client# " + clientNumber + ".");
                out.println("\n");

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
//        	System.out.println("Cat: " + category + "; action: "+action + ";Params: "+params);
        	String output = null;
        	
        	/************************** CHAT FUNCTIONS **************************/
        	if (category.compareToIgnoreCase("CHAT") == 0 && this.userID > 0)
        	{
        		if (action.compareToIgnoreCase("GETCHANNELS") == 0)
        		{
        			JSONArray t = chatManager.getChannels();
        			output = t.toJSONString();
        		}
        		if (action.compareToIgnoreCase("GETMESSAGES") == 0)
        		{
        			String lastMID = (String) params.get("lastMID");
        			JSONArray t;
        			t = chatManager.getMessages(lastMID);
        			output = t.toJSONString();
        		}
        		else if (action.compareToIgnoreCase("LEAVECHANNEL") == 0)
        		{
        			String channel = (String) params.get("channel");
        			
        			JSONArray t;
        			t = chatManager.leaveChannel(channel);
        			output = t.toJSONString();
        		}
        		else if (action.compareToIgnoreCase("JOINCHANNEL") == 0)
        		{
        			String channel = (String) params.get("channel");
        			
        			JSONArray t;
        			t = chatManager.joinChannel(channel);
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
        			this.projectManager.unlockMyLines();
        			System.exit(0);
        		}
        	}
        	
        	
        	/************************** USER ACCOUNT FUNCTIONS **************************/
        	else if (category.compareToIgnoreCase("USER") == 0)
        	{
        		if (action.compareToIgnoreCase("Login") == 0)
        		{
        			String uName = (String) params.get("username");
        			String pWord = (String) params.get("password");
        			
        			// login, change the userID attached to this thread, and the chat manager
        			String loginSuccess = this.accountManager.Login( uName, pWord );
        			this.userID = this.accountManager.GetUserAccountID();
        			
        			if (this.userID > 0)
        			{
	        			this.chatManager.setUserID(this.userID);
	        			this.chatManager.onLogin();
	        			
	        			this.projectManager.setUserID(this.userID);
        			}
        			
        			JSONObject ret = new JSONObject();
        			ret.put("userID", String.valueOf(this.userID));
        			
        			output = ret.toJSONString();
        		}
        		else if (action.compareToIgnoreCase("createAccount") == 0)
        		{
        			String fName = (String) params.get("fName");
        			String lName = (String) params.get("lName");
        			String uName = (String) params.get("uName");
        			String email = (String) params.get("email");
        			String pWord = (String) params.get("pWord");
        			
        			output = this.accountManager.CreateAccount(fName, lName, uName, email, pWord);
        			
        		}
				/*else if (action.compareToIgnoreCase("getUserID") == 0)
        		{
        			String uName = (String) params.get("username");
        			
        			int result = this.accountManager.GetLocalAccountID(uName);
        			output = Integer.toString(result);
        		}*/
        	}
        	
        	
        	/************************** PROJECT FUNCTIONS **************************/
        	else if (category.compareToIgnoreCase("PROJECT") == 0)
        	{
        		if (action.compareToIgnoreCase("getProjects") == 0)
        		{
        			JSONArray res = this.projectManager.getProjects();
        			output = res.toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getProjectAccessEntries") == 0)
        		{
					String projectID = (String) params.get("projectID");
					
        			output = this.projectManager.getProjectAccessEntries(projectID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getDirectories") == 0)
        		{
					String projectID = (String) params.get("projectID");
					
        			output = this.projectManager.getDirectories(projectID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getFiles") == 0)
        		{
					String projectID = (String) params.get("projectID");
					
        			output = this.projectManager.getFiles(projectID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getDirectories") == 0)
        		{
					String projectID = (String) params.get("projectID");
        					
					output = this.projectManager.getDirectories(projectID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getAllLines") == 0)
        		{
					String projectID = String.valueOf(params.get("projectID"));
        						
					output = this.projectManager.getAllLines(projectID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getLines") == 0)
        		{
					String fileID = String.valueOf(params.get("fileID"));
        						
					output = this.projectManager.getLines(fileID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getLineChanges") == 0)
        		{
					String fileID = String.valueOf(params.get("fileID"));
					String time = String.valueOf(params.get("time"));
					
					output = this.projectManager.getLineChanges(fileID, time).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("getLineLocks") == 0)
        		{
					String fileID = (String) params.get("fileID");
        						
					output = this.projectManager.getLineLocks(fileID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("createProject") == 0)
        		{
					String projectName = (String) params.get("projectName");
        						
        			output = this.projectManager.createProject(projectName).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("createProjectAccess") == 0)
        		{
					String projectID = (String) params.get("projectID");
					String newUserID = (String) params.get("newUserID");
         						
        			this.projectManager.createProjectAccess(newUserID, projectID);
        			
        		}
				else if (action.compareToIgnoreCase("createDirectory") == 0)
        		{
					String projectID = (String) params.get("projectID");
        			String dirName = (String) params.get("dirName");
					
        			this.projectManager.createDirectory(projectID, dirName);
        			
        		}
				else if (action.compareToIgnoreCase("createSubdirectory") == 0)
        		{
					String projectID = (String) params.get("projectID");
        			String dirName = (String) params.get("dirName");
					String parentDirID = (String) params.get("parentDirID");
					
        			this.projectManager.createDirectory(projectID, dirName, parentDirID);
        			
        		}
				else if (action.compareToIgnoreCase("createFile") == 0)
        		{
					String projectID = (String) params.get("projectID");
        			String dirID = (String) params.get("dirID");
					String fileName = (String) params.get("fileName");
					
					output = this.projectManager.createDirectory(fileName, projectID, dirID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("createLine") == 0)
        		{
					String text = (String) params.get("text");
        			String nextLineID = (String) params.get("nextLineID");
					
					output = this.projectManager.createLine (text, nextLineID).toJSONString();
        			
        		}
				else if (action.compareToIgnoreCase("removeProject") == 0)
        		{
					String projectID = (String) params.get("projectID");
					
        			this.projectManager.removeProject(projectID);
        			
        		}
				else if (action.compareToIgnoreCase("removeProjectAccess") == 0)
        		{
					String projectID = (String) params.get("projectID");
					String accUserID = (String) params.get("accUserID");
					
        			this.projectManager.removeProjectAccess(projectID, accUserID);
        			
        		}
				else if (action.compareToIgnoreCase("removeDirectory") == 0)
        		{
					String dirID = (String) params.get("dirID");
					
        			this.projectManager.removeDirectory(dirID);
        			
        		}
				else if (action.compareToIgnoreCase("removeFile") == 0)
        		{
					String fileID = (String) params.get("fileID");
					
        			this.projectManager.removeFile(fileID);
        			
        		}
				else if (action.compareToIgnoreCase("removeLine") == 0)
        		{
					String lineID = (String) params.get("lineID");
					
        			this.projectManager.removeLine(lineID);
        			
        		}
				else if (action.compareToIgnoreCase("changeLine") == 0)
        		{
					String lineID = String.valueOf(params.get("lineID"));
					String text = (String) params.get("text");
					if(lineID != null && lineID != ""){
						this.projectManager.changeLine(lineID, text);	
					}
        			
        		}
				else if (action.compareToIgnoreCase("lockLine") == 0)
        		{
					String lineID = String.valueOf(params.get("lineID"));
					if(lineID != null && lineID != ""){
						this.projectManager.unlockMyLines();
						this.projectManager.lockLine(lineID);	
					}
        			
        		}
				else if (action.compareToIgnoreCase("unlockLine") == 0)
        		{
					String lineID = String.valueOf(params.get("lineID"));
					
        			this.projectManager.unlockLine(lineID);
        			
        		}
        	}
        	
        	/************************** SERVER FUNCTIONS **************************/
        	else if (category.compareToIgnoreCase("SERVER") == 0)
        	{
        		if (action.compareToIgnoreCase("quit") == 0)
        		{
        			this.projectManager.unlockMyLines();
        			System.exit(0);
        		}
        	}
        	
        	/* UNKNOWN/NO CATEGORY */
        	else
        	{
        		output = new JSONObject().toJSONString();
        	}
        	
        	return output;
        }
	}
	
}
