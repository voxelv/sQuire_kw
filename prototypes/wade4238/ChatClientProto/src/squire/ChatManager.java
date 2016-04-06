package squire;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChatManager {
	private BufferedReader in;
    private PrintWriter out;
    private ServerConnection server;
    private String userID;
    private String lastMID;
    private ChatHelper autoHelper;
    private ChatHelper manualHelper;
    private int lastChannel = -1;
    
    private Boolean helperBusy;
    
    private HashMap<Integer, Chatroom> channelByLocalID;
    private HashMap<Integer, Chatroom> channelByServerID;
    private ArrayList<Chatroom> channelList;
    
    private HashMap<Integer, Chatroom> cacheChannelByLocalID;
    private HashMap<Integer, Chatroom> cacheChannelByServerID;
    
    private JTextArea messageArea;
    
    public ChatManager(JTextField dataField, JTextArea messageArea, ServerConnection server) 
    {
    	helperBusy = false;
    	
    	channelByLocalID = new HashMap<Integer, Chatroom>();
    	channelByServerID = new HashMap<Integer, Chatroom>();
    	channelList = new ArrayList<Chatroom>();
    	
    	cacheChannelByLocalID = new HashMap<Integer, Chatroom>();
    	cacheChannelByServerID = new HashMap<Integer, Chatroom>();
    	
    	
    	userID = "1";	// temp
    	lastMID = "0";	// permanent
    	this.server = server;
    	
    	this.messageArea = messageArea;
    	
    	autoHelper = new ChatHelper(this, server, userID, messageArea, dataField);
    	autoHelper.start();
    	
    	manualHelper = new ChatHelper(this, server, userID, messageArea, dataField);
    	
    	updateChannels( manualHelper.initJoinChannels() );
    }
    
    public String parseMessage(JSONObject msg)
    {
//    	System.out.println(msg);
    	String output = new String();
    	Chatroom room = this.channelByServerID.get(Integer.parseInt( (String)msg.get("channelID") ));
    	
    	output += (String) msg.get("timeSent") + " ";
    	output += "[" + (String) msg.get("channelName") + "]";
    	output += "[" + room.clientID + "] ";
    	output += (String) msg.get("userName") + ": ";
    	output += (String) msg.get("messageText");
    	
    	return output;
    }
    
    public void addMessageToGUI(String outString)
    {
    	messageArea.insert(outString + "\n", 0);
    }
    
    public void enterText(String enteredText)
    {
    	// System command
    	if (enteredText.substring(0, 1).compareTo("/") == 0)
    	{
    		int spaceIndex = enteredText.indexOf(" ");
    		String command = "";
    		String arg = "";
    		if (spaceIndex >= 0)
    		{
	    		command = enteredText.substring(1, spaceIndex);
	    		arg = enteredText.substring(spaceIndex+1);
    		}
    		else
    			command = enteredText.substring(1);
    		
//    		System.out.println("Got system command: '" + command + "'; arg: '" + arg + "'");
    		
    		try {
    			// Channel number
    			int localChannelID = Integer.parseInt(command);
    			this.lastChannel = localChannelID;
    			
    			addMessage(arg, localChannelID);
    			
			} catch (Exception e) {
				// it's a real system command, not a channel. Do stuff here
				if (command.compareToIgnoreCase("join") == 0)
				{ 
					this.joinChannel(arg);
				}
				else if (command.compareToIgnoreCase("leave") == 0)
				{
					this.leaveChannel(arg);
				}
				else if (command.compareToIgnoreCase("channels") == 0)
				{
					for (int i = 0; i < this.channelList.size(); i++)
						this.addMessageToGUI("Channel: "+ this.channelList.get(i).getChannelInfo());
				}
				else if (command.compareToIgnoreCase("server") == 0)
				{
					this.sendServerCommand(arg);
				}
			}
    	}
    	else		// plain text for chat
    	{
    		this.addMessage(enteredText, lastChannel);
    	} 
    	
    }
    
    /**
     * Only called internally. Removes references in indexes to channel, alerts user of the change.
     * @param clientID
     */
    private void removeChannelIndex(int clientID)
    {
    	Chatroom leavingRoom = this.channelByLocalID.get(clientID); 
    	String channelName = leavingRoom.name;
    	int serverID = leavingRoom.serverID;
    	
    	this.channelByLocalID.remove(clientID);
    	this.channelByServerID.remove(serverID);
    	this.channelList.remove(leavingRoom);
    	
    	// Send message to user
    	this.addMessageToGUI("Left Channel: [" + clientID + "] " + channelName);
    }
    
    /**
     * Only called internally. Creates the chatroom object, indexes it properly, alerts user of the change.
     * @param JSONObject channel
     */
    private Chatroom addChannelIndex(JSONObject channel)
    {
    	String channelName = (String) channel.get("channelName");
    	int channelID = Integer.parseInt( (String)channel.get("channelID")  );
    	String joinTime = (String) channel.get("joinTime");
    	
    	// If the chatroom already exists, just return
    	Chatroom room;
    	if ( (room = this.channelByServerID.get(channelID)) != null)
    	{
    		return room;
    	}
    	
    	room = new Chatroom( channelName, channelID, joinTime);
    	
    	this.channelByServerID.put(channelID, room);
    	
    	// Find if the chatroom has a cached clientID
    	int clientID = 0;
    	Chatroom cachedRoom = this.cacheChannelByServerID.get(channelID);
    	if (cachedRoom != null)
    	{
    		Chatroom roomWithClientID = this.channelByLocalID.get(cachedRoom.clientID);
    		
    		// If there aren't any chatrooms using the clientID, use it.
    		if ( roomWithClientID == null )
    			clientID = cachedRoom.clientID;
    	}
    	
    	// Search for a new client ID
    	if (clientID == 0)
    	{
    		clientID = 1;
			
			while (this.channelByLocalID.get(clientID) != null)
				clientID++;
    	}
    	
    	room.clientID = clientID;
    	
    	this.cacheChannelByServerID.put(channelID, room);
    	this.cacheChannelByLocalID.put(clientID, room);
    	
    	this.channelByServerID.put(channelID, room);
    	this.channelByLocalID.put(clientID, room);
    	this.channelList.add(room);
    	
    	// Send message to user
    	this.addMessageToGUI("Joined Channel: [" + clientID + "] " + channelName);
    	
    	return room;
    }
    
    public void updateChannels(JSONArray newChannelList)
    {
    	// go through the new channels
    	for (int i = 0; i < newChannelList.size(); i++)
    	{
    		JSONObject newChannel = (JSONObject) newChannelList.get(i);
    		
    		// Try to join new channel.
    		Chatroom room = this.addChannelIndex(newChannel);
    		
    		room.found = true;
    	}
    	
    	// Go through existing channels to see if any weren't found in the new list. Leave those channels
    	for (int i = 0; i < this.channelList.size(); i++)
    	{
    		Chatroom room = this.channelList.get(i);
    		if (!room.found)
    			this.removeChannelIndex(room.clientID);
    		room.found = false;
    	}
    	
    }
    
    public void sendServerCommand(String command)
    {
    	manualHelper.sendServerCommand(command);
    }
    
    public void leaveChannel(String channelName)
    {
    	updateChannels( manualHelper.leaveChannel(channelName) );
    }
    
    public void joinChannel(String channelName)
    {
    	updateChannels( manualHelper.joinChannel(channelName) );
    }
    
    public void addMessage(String msg, int channelID)
    {
    	manualHelper.addMessage(msg, channelID);
    }
    
    public void updateMessages(JTextArea messageArea)
    {
    	manualHelper.updateMessages(messageArea);
    }
    
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /************************* CHATHELPER CLASS****************************************/
    public class ChatHelper extends Thread{
    	private BufferedReader in;
        private PrintWriter out;
        private ServerConnection server;
        private String userID;
        private JTextField dataField;
        private JTextArea messageArea;
        private ChatManager manager;
        
        public ChatHelper(ChatManager manager, ServerConnection server, String userID, JTextArea messageArea, JTextField dataField)
        {
        	this.manager = manager;
        	this.server = server;
        	this.userID = userID;
        	this.messageArea = messageArea;
        	this.dataField = dataField;
        }
        
        @Override
        public void run()
        {
        	while (true) 
        	{
                // Sleep for a while
                try {
                	this.updateMessages(messageArea);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // Interrupted exception will occur if
                    // the Worker object's interrupt() method
                    // is called. interrupt() is inherited
                    // from the Thread class.
                    break;
                }
            }
        }
        
        public JSONArray initJoinChannels()
        {
        	JSONObject params;
        	
        	/**************************** START OF REQUEST ****************************/
        	params = new JSONObject();		// Create parameter object
        	Object response = server.sendSingleRequest("Chat", "GetChannels", params);	// send a single request
            
            /**************************** END OF REQUEST ****************************/
        	
        	
        	JSONArray channelList = null;
			try {
				channelList = (JSONArray) new JSONParser().parse((String) response);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				channelList = new JSONArray();
			}
        	
			
            return channelList;
        }
        
        public void sendServerCommand(String command)
        {
        	JSONObject params;
        	
        	/**************************** START OF REQUEST ****************************/
        	params = new JSONObject();		// Create parameter object
        	
            server.sendSingleRequest("Server", command, params);	// send a single request
            
            /**************************** END OF REQUEST ****************************/
        }
        
        public void addMessage(String msg, int localChannelID)
        {
        	JSONObject params;
        	
//        	String channelID = (String) this.manager.channelsObjByLocalID.get(String.valueOf(localChannelID));
        	int serverChannelID = this.manager.channelByLocalID.get(localChannelID).serverID;
        	
        	/**************************** START OF REQUEST ****************************/
        	params = new JSONObject();		// Create parameter object
	
            params.put("msg", msg);
            params.put("channelID", String.valueOf(serverChannelID) );
        	
//            System.out.println("Adding message: "+params);
            server.sendSingleRequest("Chat", "addMessage", params);	// send a single request
            
            /**************************** END OF REQUEST ****************************/
            
            
//            dataField.selectAll();
        }
        
        public JSONArray joinChannel(String channelName)
        {
        	/**************************** START OF REQUEST ****************************/
        	JSONObject params = new JSONObject();		// Create parameter object

            params.put("channel", channelName);
        	
            // sendSingleRequest(category, actioncommand, param object)
            Object response = server.sendSingleRequest("Chat", "joinChannel", params);
            /**************************** END OF REQUEST ****************************/
            
            JSONArray channelList = null;
			try {
				channelList = (JSONArray) new JSONParser().parse((String) response);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				channelList = new JSONArray();
			}
            
            return channelList;
        }
        
        public JSONArray leaveChannel(String channelName)
        {
        	/**************************** START OF REQUEST ****************************/
        	JSONObject params = new JSONObject();		// Create parameter object

            params.put("channel", channelName);
        	
            // sendSingleRequest(category, actioncommand, param object)
            Object response = server.sendSingleRequest("Chat", "leaveChannel", params);
            /**************************** END OF REQUEST ****************************/
            
            JSONArray channelList = null;
			try {
				channelList = (JSONArray) new JSONParser().parse((String) response);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				channelList = new JSONArray();
			}
            
            return channelList;
        }
        
        public void updateMessages(JTextArea messageArea)
        {
        	JSONObject params;
        	Object response;
        	JSONObject responseObj;
        	String result;
        	
        	/**************************** START OF REQUEST ****************************/
            
        	params = new JSONObject();		// Create parameter object
        	
        	// Prevent concurrent requests for messages from autoHelper and manualHelper by 
        	// Waiting until the other helper is not busy construct and do the request
        	while (this.manager.helperBusy)
        	{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	
        	this.manager.helperBusy = true;
            params.put("lastMID", this.manager.lastMID);
            params.put("userID", userID);
        	
            // sendSingleRequest(category, actioncommand, param object)
//            response = server.sendSingleRequest("Chat", "getMessages", params);
            String stringResult = (String) server.sendSingleRequest("Chat", "getMessages", params);
            /**************************** END OF REQUEST ****************************/
            
//            responseObj = (JSONObject) response;					// create the JSON response object
            
//            JSONArray msgArray = new JSONArray();
            
//            String stringResult = (String) responseObj.get("result");
//            System.out.println(stringResult);
            
            JSONArray msgArray = null;
    		try {
//    			System.out.println(stringResult);
    			msgArray = (JSONArray) new JSONParser().parse(stringResult);
    		} catch (ParseException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
            
            for (int i = 0; i < msgArray.size(); i++)
            {
            	JSONObject msg = (JSONObject) msgArray.get(i);
            	String outString = this.manager.parseMessage(msg);

            	this.manager.addMessageToGUI(outString);
            	
            	String thisMID = (String) msg.get("MID");
            	this.manager.lastMID = thisMID;
            }
            this.manager.helperBusy = false;
        }// End of last Function
    }// End of the ChatHelper Class
    
    private class Chatroom {
    	private String name;
    	private int serverID;
    	private int clientID;
    	private String joinTime;
    	private JSONArray msgList;
    	public Boolean found;
    	
    	public Chatroom(String name, int ID, String joinTime)
    	{
    		this.name = name;
    		this.serverID = ID;
    		this.joinTime = joinTime;
    		this.clientID = 0;
    		
    		msgList = new JSONArray();
    	}
    	
    	public String getChannelInfo()
    	{
    		return ( this.name + "; srvID[" + this.serverID + "]; joinTime[" + this.joinTime + "]; clientID[" + this.clientID + "]" ); 
    	}
    	
    	public void addMsg(Message m)
    	{
    		msgList.add(m);
    	}
    	
    	public JSONArray getMsgs()
    	{
    		return this.msgList;
    	}
    	
    	private class Message {
        	public int MID;
        	public String time;
        	public String text;
        	public int serverChannelID;
        	public String channelName;
        	public int fromUserID;
        	public String fromUsername;
        	
        }
    }
    
    
}
