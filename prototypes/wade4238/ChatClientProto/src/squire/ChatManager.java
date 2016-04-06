package squire;

import java.io.BufferedReader;
import java.io.PrintWriter;
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
    private JSONArray channels;
    private JSONObject channelsObj;
    private JSONArray channelCache;
    private JTextArea messageArea;
    
    public ChatManager(JTextField dataField, JTextArea messageArea, ServerConnection server) 
    {
    	userID = "1";	// temp
    	lastMID = "0";	// permanent
    	this.server = server;
    	channels = new JSONArray();
    	channelsObj = new JSONObject();
    	channelCache = new JSONArray();
    	
    	this.messageArea = messageArea;
    	
    	autoHelper = new ChatHelper(this, server, userID, messageArea, dataField);
    	autoHelper.start();
    	
    	manualHelper = new ChatHelper(this, server, userID, messageArea, dataField);
    	updateChannels( manualHelper.initJoinChannels() );
    }
    
    public String parseMessage(JSONObject msg)
    {
    	System.out.println(msg);
    	String output = new String();
    	
    	output += (String) msg.get("timeSent") + " ";
    	output += "[" + (String) msg.get("channelName") + "] ";
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
//    	System.out.println("Received: "+enteredText);
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
    		
    		System.out.println("Got system command: '" + command + "'; arg: '" + arg + "'");
    		
    		try {
    			// Channel number
    			int channelID = Integer.parseInt(command);
    			this.lastChannel = channelID;
    			
    			addMessage(arg, channelID);
    			
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
					for (int i = 0; i < this.channels.size(); i++)
						this.addMessageToGUI("Channel: "+ this.channels.get(i));
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
    
    public void updateChannels(JSONArray newChannelList)
    {
    	
    	if (newChannelList.size() == 0)
    	{
    		for (int i = 0; i < this.channels.size(); i++)
    		{
    			this.addMessageToGUI("Left Channel: " + this.channels.get(i));
    		}
    		this.channels = newChannelList;
    		return;
    	}
    	
    	for (int i = 0; i < newChannelList.size(); i++)
    	{
    		JSONObject newChannel = (JSONObject) newChannelList.get(i);
    		
    		Boolean found = false;
    		int newChannelID = Integer.parseInt((String) newChannel.get("channelID"));
    		
    		for (int j = 0; j < this.channels.size(); j++)
    		{
    			JSONObject existingChannel = (JSONObject) this.channels.get(j);

    			int existingChannelID = Integer.parseInt((String) existingChannel.get("channelID"));
    			if (existingChannelID == newChannelID)
    			{
    				found = true;
    				if (existingChannel.get("found") == null)
    					existingChannel.put("found", true);
    				break;
    			}
    			if (existingChannel.get("found") == null)
    				existingChannel.put("found", false);
    		}
    		
    		if (found == false)	// didn't find the new channel among old channels, it's a new one. Just joined it
    		{
    			this.addMessageToGUI("Joined Channel: " + newChannel);
    			newChannel.put("found", true);
    			this.channels.add(newChannel);
    			
    			Boolean foundInCache = false;
    			for (int k = 0; k < this.channelCache.size(); k++)
    			{
    				JSONObject t = (JSONObject) this.channelCache.get(k);
    				if ( Integer.parseInt((String) t.get("channelID")) == Integer.parseInt((String) newChannel.get("channelID")) )
    					foundInCache = true;
    			}
    			if (!foundInCache)
    				this.channelCache.add(newChannel);
    		}
    	}
    	
    	
    	// Go through existing channels to see if any weren't found in the new list. Leave those channels
    	for (int i = 0; i < this.channels.size(); i++)
    	{
    		JSONObject chan = (JSONObject) this.channels.get(i);
    		if (chan.get("found") == null || (Boolean) chan.get("found") == false)	// leave channel announce
    		{
    			this.addMessageToGUI("Left Channel: " + chan);
    			this.channels.remove(i);
    		}
    		else
    			chan.remove("found");
    	}
    	
    	System.out.println("Cache: " + this.channelCache);
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
        
        public void addMessage(String msg, int channelID)
        {
        	JSONObject params;
        	
        	/**************************** START OF REQUEST ****************************/
        	params = new JSONObject();		// Create parameter object
	
            params.put("msg", msg);
            params.put("channelID", String.valueOf(channelID));
        	
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

//            	messageArea.insert(outString + "\n", 0);
            	this.manager.addMessageToGUI(outString);
            	
            	String thisMID = (String) msg.get("MID");
            	this.manager.lastMID = thisMID;
            	
            }   
        }// End of last Function
    }// End of the ChatHelper Class
    
    private class Chatroom {
    	private String name;
    	private int ID;
    	private String joinTime;
    	private JSONArray msgList;
    	
    	public Chatroom(String name, int ID, String joinTime)
    	{
    		this.name = name;
    		this.ID = ID;
    		this.joinTime = joinTime;
    		
    		msgList = new JSONArray();
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
