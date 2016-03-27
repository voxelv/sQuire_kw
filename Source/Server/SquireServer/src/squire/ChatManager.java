package squire;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChatManager {
	
	public ChatManager() {
		
	}
	
	/**
	 * Unsubscribe a user from a channel
	 * @param userID
	 * @param channelID
	 * @return JSONArray - list of channels still subscribed to
	 */
	public JSONArray leaveChannel(int userID, int channelID) {
		String query = "delete from Subscriptions where userID=? and channelID=?";
		
		JSONArray channelList = new JSONArray();
		String chan1 = "testChannel1";
		String chan2 = "testChannel2";
		channelList.add(chan1);
		channelList.add(chan2);
		
		return channelList;
	}
	
	/**
	 * Subscribe a user to a channel
	 * @param userID
	 * @param channelID
	 * @return JSONArray - list of channels user is subscribed to
	 */
	public JSONArray joinChannel(int userID, int channelID) {
		String query = "insert into Subscriptions (channelID, userID, joinTime) values (?, ?, ?)";
		
		JSONArray channelList = new JSONArray();
		String chan1 = "testChannel1";
		String chan2 = "testChannel2";
		channelList.add(chan1);
		channelList.add(chan2);
		
		return channelList;
	}
	
	/**
	 * Add message to channel
	 * @param userID
	 * @param message
	 * @param channelID
	 */
	public void addMessage(int userID, String message, int channelID) {
		String query = "insert into Message (timeSent, fromID, channelID, messageText) values (?, ?, ?, ?)";
		
	}
	
	/**
	 * Get all messages for a user since a given time
	 * @param userID
	 * @param lastMsgTime
	 * @return	JSONArray array [
	 * 				object {
	 * 					"time": <time>
	 * 					"text": <text>
	 * 					"channelID": <channelID>
	 * 					"channelName": <channelName>
	 * 					"fromUserID": <fromUserID>
	 * 					"fromUsername": <fromUsername>
	 * 				}
	 * 			]
	 */
	public JSONArray getMessages(int userID, int lastMsgTime) {
		String query = "select "
						+ "`Messages`.`timeSent`, " 
						+ "`Messages`.`fromID`, " 
						+ "`Messages`.`channelID`, " 
						+ "`Messages`.`messageText`, "
						+ "`Channels`.`channelName`, "
						+ "`Users`.`userName` "
					+ "from " 
						+ "`Messages`, "
						+ "`Subscriptions`, "
						+ "`Channels`, "
						+ "`Users` "
					+ "where " 
						+ "`Messages`.`channelID` = `Subscriptions`.`channelID` AND "
						+ "`Channels`.`channelID` = `Subscriptions`.`channelID` AND "
						+ "`Users`.`userID` = `Messages`.`fromID` AND "
						+ "`Messages`.`timeSent` > `Subscriptions`.`joinTime` AND "
						+ "`Subscriptions`.`userID` = ? AND "		// userID
						+ "`Messages`.`timeSent` > ? "				// timestamp of last message received
					+ "order by " 
						+ "`Messages`.`timeSent` ASC;";
		
		JSONArray out = new JSONArray();
		JSONObject msg = new JSONObject();
		msg.put("time", "2016-03-01 15:01");
		msg.put("text", "hello there!");
		msg.put("channelID", 1);
		msg.put("channelName", "generalChat");
		msg.put("fromUserID", 1);
		msg.put("fromUsername", "wade4238");
		
		JSONObject msg2 = new JSONObject();
		msg2.put("time", "2016-03-01 15:03");
		msg2.put("text", "welcome to the room, whatsup?");
		msg2.put("channelID", 1);
		msg2.put("channelName", "generalChat");
		msg2.put("fromUserID", 1);
		msg2.put("fromUsername", "slip5295");
		
		out.add(msg);
		out.add(msg2);
		
		return out;
	}
}