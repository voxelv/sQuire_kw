package sq.app.model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class ChatManager {
	private BufferedReader in;
    private PrintWriter out;
    private ServerConnection server;
    private String userID;
    private String lastMID;
    private ChatHelper autoHelper;
    private ChatHelper manualHelper;
    private int defaultChannel = -1;
    
    private Boolean helperBusy;
    
    private HashMap<Integer, Chatroom> channelByLocalID;
    private HashMap<Integer, Chatroom> channelByServerID;
    private ArrayList<Chatroom> channelList;
    
    private HashMap<Integer, Chatroom> cacheChannelByLocalID;
    private HashMap<Integer, Chatroom> cacheChannelByServerID;
    
    public TextArea history;
    public ComboBox channelBox;
    
    ObservableList<String> guiChannelList;
//    private JTextArea messageArea;
    
    public ChatManager(ServerConnection server) 
    {
    	helperBusy = false;
    	
    	channelByLocalID = new HashMap<Integer, Chatroom>();
    	channelByServerID = new HashMap<Integer, Chatroom>();
    	channelList = new ArrayList<Chatroom>();
    	
    	cacheChannelByLocalID = new HashMap<Integer, Chatroom>();
    	cacheChannelByServerID = new HashMap<Integer, Chatroom>();
    	
    	lastMID = "0";	// get all previous messages (after joining)
    	
    	guiChannelList = FXCollections.observableArrayList();
    	this.server = server;
    }
    
    public void onLogin(int userID)
    {
    	this.userID = String.valueOf(userID);
    	
    	autoHelper = new ChatHelper(this, server, this.userID);
    	autoHelper.start();
    	
    	manualHelper = new ChatHelper(this, server, this.userID);
    	
    	updateChannels( manualHelper.initJoinChannels() );
    }
    
    /**
     * @param JSONObject msg
     * @return Message Obj
     * 
     * Take a message Object that is returned from the server, and create a new Message Object from it.
     */
    public Message parseMessage(JSONObject msg)
    {
//    	System.out.println(msg);
    	Chatroom room = this.channelByServerID.get(Integer.parseInt( (String)msg.get("channelID") ));
    	
    	Message newMsg = new Message();
    	newMsg.channelName = (String) msg.get("channelName");
    	newMsg.fromUserID = Integer.parseInt( (String)msg.get("channelID") );
    	newMsg.fromUsername = (String) msg.get("userName");
    	newMsg.MID = Integer.parseInt( (String) msg.get("MID") );
    	newMsg.serverChannelID = Integer.parseInt( (String)msg.get("channelID") );
    	newMsg.text = (String) msg.get("messageText");
    	newMsg.time = (String) msg.get("timeSent");
    	newMsg.clientChannelID = room.clientID;
    	
    	return newMsg;
    }
    
    /**
     * @param outString
     * 
     * Add text to GUI. This will need to be changed when we move to the final GUI
     */
    public void addMessageToGUI(String outString)
    {
    	history.appendText(outString + "\n");
    }
    
    /**
     * @param enteredText
     * 
     * Take entered text, parse for system command, delegate actions to other objects if necessary.
     */
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
    		
    		try {
    			// Channel number
    			int localChannelID = Integer.parseInt(command);
    			this.defaultChannel = localChannelID;
    			
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
    		String toChannelName = (String) this.channelBox.getValue();
    		Chatroom toChannel = null;
    		for (int i = 0; i < this.channelList.size(); i++)
    		{
    			Chatroom h = this.channelList.get(i);
    			if (h.name.compareTo(toChannelName) == 0)
    			{
    				toChannel = h;
    				break;
    			}
    		}
    		if (toChannel != null)
    			this.defaultChannel = toChannel.clientID;
    		
    		if (defaultChannel >= 0)
    			this.addMessage(enteredText, defaultChannel);
    		else
    			this.alertUserNoChannels();
    	} 
    	// Set the GUI to match the default channel, even if it was changed by the text command
    	this.refreshGUIChannelList();
    	
    }
    
    /**
     * Send the message to the user that they are no longer in any channels, and how to join a new one. 
     */
    private void alertUserNoChannels()
    {
    	this.addMessageToGUI("... You are not currently part of any channels,\n... Type /join <channelName> to join a channel\n...  e.g: '/join Global'");
    }
    
    private void refreshGUIChannelList()
    {
    	this.channelBox.setItems(guiChannelList);
    	
    	String defaultChannelName = this.channelByLocalID.get(this.defaultChannel).name;
    	this.channelBox.setValue(defaultChannelName);
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
    	
    	// Remove it from the GUI list
    	this.guiChannelList.remove(channelName);
    	
    	this.channelByLocalID.remove(clientID);
    	this.channelByServerID.remove(serverID);
    	this.channelList.remove(leavingRoom);
    	
    	// Send message to user
    	this.addMessageToGUI("Left Channel: [" + clientID + "] " + channelName);
    	
    	if (this.defaultChannel == clientID)
    	{
    		this.defaultChannel = -1;
    		
    		// if still part of channels, change default channel
    		if (this.channelList.size() > 0)
    		{
    			Chatroom newDefault = this.channelList.get(0);
    			this.changeDefaultChannel(newDefault.clientID);
    		}
    	}
    	
    	this.refreshGUIChannelList();
    }
    
    /**
     * @param clientID
     * 
     * Change the default channel the user will send messages to if channelID omitted
     */
    private void changeDefaultChannel(int clientID)
    {
    	this.defaultChannel = clientID;
    	
    	Chatroom room = this.channelByLocalID.get(clientID);
    	String channelText = "[" + room.clientID + "][" + room.name + "]";  
		this.addMessageToGUI("New Default Channel: " + channelText);
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
    	
    	this.guiChannelList.add(channelName);
    	
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
    	
    	if (this.defaultChannel == -1)
    		this.changeDefaultChannel(clientID);
    	
    	this.refreshGUIChannelList();
    	
    	return room;
    }
    
    /**
     * @param JSONArray newChannelList
     * 			of JSONObject channel
     * 
     * Compare the new channel list to the current channel list. <br>
     * Leave any channels not in the new list, join any channels not<br>
     * in the old list.
     */
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
    	
    	if (newChannelList.size() == 0)
    		this.alertUserNoChannels();
    }
    
    /**
     * @param String command
     * 
     * send system command to server
     */
    public void sendServerCommand(String command)
    {
    	manualHelper.sendServerCommand(command);
    }
    
    /**
     * @param channelName
     * 
     * Leave channel specified.
     */
    public void leaveChannel(String channelName)
    {
    	updateChannels( manualHelper.leaveChannel(channelName) );
    }
    
    /**
     * @param channelName
     * 
     * Join channel specified. If channel doesn't exist, create it.
     */
    public void joinChannel(String channelName)
    {
    	updateChannels( manualHelper.joinChannel(channelName) );
    }
    
    /**
     * @param msg
     * @param channelID
     * 
     * Add message to the channel specified
     */
    public void addMessage(String msg, int channelID)
    {
    	manualHelper.addMessage(msg, channelID);
    }
    
    /**
     * 
     * Request a refresh for messages from the server
     */
    public void updateMessages()
    {
    	manualHelper.updateMessages();
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
//        private JTextField dataField;
//        private JTextArea messageArea;
        private ChatManager manager;
        
        public ChatHelper(ChatManager manager, ServerConnection server, String userID)
        {
        	this.manager = manager;
        	this.server = server;
        	this.userID = userID;
//        	this.messageArea = messageArea;
//        	this.dataField = dataField;
        }
        
        @Override
        public void run()
        {
        	while (true) 
        	{
                // Sleep for a while
                try {
                	this.updateMessages();
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
        
        /**
         * @return JSONArray
         * 
         * Fetch channel list from server when client first started up.
         */
        public JSONArray initJoinChannels()
        {
        	this.joinChannel("Global");
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
        
        /**
         * @param command
         * 
         * Send system command to Server
         */
        public void sendServerCommand(String command)
        {
        	JSONObject params;
        	
        	/**************************** START OF REQUEST ****************************/
        	params = new JSONObject();		// Create parameter object
        	
            server.sendSingleRequest("Server", command, params);	// send a single request
            
            /**************************** END OF REQUEST ****************************/
        }
        
        /**
         * @param msg
         * @param localChannelID
         * 
         * Add message to the channel specified
         */
        public void addMessage(String msg, int localChannelID)
        {
        	JSONObject params;
        	
        	int serverChannelID = this.manager.channelByLocalID.get(localChannelID).serverID;
        	
        	/**************************** START OF REQUEST ****************************/
        	params = new JSONObject();		// Create parameter object
	
            params.put("msg", msg);
            params.put("channelID", String.valueOf(serverChannelID) );
        	
            server.sendSingleRequest("Chat", "addMessage", params);	// send a single request
            
            /**************************** END OF REQUEST ****************************/
        }
        
        /**
         * @param channelName
         * @return JSONArray channelList
         * 
         * Join channel by name
         */
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
        
        /**
         * @param channelName
         * @return JSONArray channelList
         * 
         * Leave channel by name
         */
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
        
        /**
         * @param messageArea
         * 
         * Fetch new messages from Server, put them on screen
         */
        public void updateMessages()
        {
        	JSONObject params;
        	
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
        	
            String stringResult = (String) server.sendSingleRequest("Chat", "getMessages", params);
            /**************************** END OF REQUEST ****************************/
            
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
            	Message msgObj = this.manager.parseMessage(msg);
            	this.manager.channelByServerID.get( msgObj.serverChannelID ).addMsg(msgObj);
            	
            	String outString = msgObj.getPrintString();

            	this.manager.addMessageToGUI(outString);
            	
            	String thisMID = (String) msg.get("MID");
            	this.manager.lastMID = thisMID;
            }
            
            this.manager.helperBusy = false;
        }// End of last Function
    }// End of the ChatHelper Class
    
    
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /*************************** CHATROOM CLASS****************************************/
    private class Chatroom {
    	private String name;
    	private int serverID;
    	private int clientID;
    	private String joinTime;
    	private ArrayList<Message> msgList;
    	public Boolean found;
    	
    	public Chatroom(String name, int ID, String joinTime)
    	{
    		this.name = name;
    		this.serverID = ID;
    		this.joinTime = joinTime;
    		this.clientID = 0;
    		
    		msgList = new ArrayList<Message>();
    	}
    	
    	public String getChannelInfo()
    	{
    		return ( this.name + "; srvID[" + this.serverID + "]; joinTime[" + this.joinTime + "]; clientID[" + this.clientID + "]" ); 
    	}
    	
    	public void addMsg(Message m)
    	{
    		msgList.add(m);
    	}
    	
    	public ArrayList<Message> getMsgs()
    	{
    		return this.msgList;
    	}
    	
    }
    
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**************************** MESSAGE CLASS****************************************/
    private class Message {
    	public int MID = -1;
    	public String time = new String();
    	public String text = new String();
    	public int serverChannelID = -1;
    	public int clientChannelID = -1;
    	public String channelName = new String();
    	public int fromUserID = -1;
    	public String fromUsername = new String();
    	
    	
    	public String getPrintString()
    	{
    		String out = new String();
    		
    		out += "[" + clientChannelID + "]";
    		out += "[" + channelName + "] ";
//    		out += "[" + time + "] ";
    		out += "[" + fromUsername + "]: ";
    		out += text;
    		
    		return out;
    	}
    }
}
