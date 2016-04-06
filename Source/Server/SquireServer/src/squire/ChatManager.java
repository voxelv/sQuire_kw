package squire;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChatManager {
	private DBConnector dbc;
	private int userID;
	
	public ChatManager(DBConnector dbc) {
		
		this.dbc = dbc;
//        dbc.closeConnection();
	}
	
	/**
	 * void setUserID (int userID)
	 * Set the userID when the user logs in
	 * @param userID
	 */
	public void setUserID(int userID)
	{
		this.userID = userID;
	}
	
	/**
	 * Unsubscribe user from a channel
	 * @param String channelName
	 * @return JSONArray - list of channels still subscribed to
	 */
	public JSONArray leaveChannel(String channelName)
	{
//		System.out.println("Leaving Channel: "+channelName);
		String query = "Delete `sub` "
				+ "FROM"
					+ "`Subscriptions` `sub` "
				+ "JOIN "
					+ "`Channels` `chan` "
				+ "ON "
					+ "`chan`.`channelID` = `sub`.`channelID` "
				+ "WHERE "
					+ "`chan`.`channelName` = ? AND "
					+ "`sub`.`userID` = ?";
		
		String[] values = new String[2];
		values[0] = channelName;
		values[1] = String.valueOf(this.userID);
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONArray channelList = this.getChannels();
		
		return channelList;
	}
	
	/**
	 * Unsubscribe user from a channel
	 * @param int channelID
	 * @return JSONArray - list of channels still subscribed to
	 */
	public JSONArray leaveChannel(int channelID) 
	{
		
		String query = "DELETE from `Subscriptions` where `userID` = ? and `channelID` = ?";
		String[] values = new String[2];
		values[0] = String.valueOf(this.userID);
		values[1] = String.valueOf(channelID);
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONArray channelList = this.getChannels();
		
		return channelList;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getChannels()
	{
		String query =	"select "
						+ "`Channels`.`channelID`, "
						+ "`Channels`.`channelName`, "
						+ "`Subscriptions`.`joinTime` "
					+ "from "
						+ "`Channels`, "
						+ "`Subscriptions` "
					+ "where "
						+ "`Subscriptions`.`channelID` = `Channels`.`channelID` AND "
						+ "`Subscriptions`.`userID` = ?";
		
		String[] values = new String[1];
		values[0] = String.valueOf(this.userID);
		
		JSONArray channelList = new JSONArray();
		try {
			channelList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return channelList;
	}
	
	
	/**
	 * Join channel by name
	 * @param channelName
	 * @return JSONArray - list of channels still subscribed to
	 */
	public JSONArray joinChannel(String channelName)
	{
//		System.out.println("Joining Channel: '"+channelName +"'");
		channelName = channelName.substring(0, 1).toUpperCase() + channelName.substring(1);
		String query = "INSERT IGNORE INTO Channels (channelName) values (?)";
		String[] values1 = new String[1];
		values1[0] = channelName;
		try {
			this.dbc.query(query, values1);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		query = "INSERT IGNORE INTO "
					+ "Subscriptions (channelID, userID) "
						+ "SELECT "
							+ "channelID, "
							+ "? "		// userID
						+ "FROM "
							+ "Channels "
						+ "WHERE "
							+ "channelName = ?";
		
		String[] values2 = new String[2];
		values2[0] = String.valueOf(this.userID);
		values2[1] = channelName;
		
		try {
			this.dbc.query(query, values2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("0:" + values[0] + "; 1: "+values[1]);
		
		JSONArray channelList = this.getChannels();
		
		return channelList;
	}
	
	/**
	 * Subscribe a user to a channel
	 * @param channelID
	 * @return JSONArray - list of channels user is subscribed to
	 */
	public JSONArray joinChannel(int channelID) {
		String query = "insert into Subscriptions (channelID, userID, joinTime) values (?, ?, ?)";
		
		JSONArray channelList = new JSONArray();
		
		return channelList;
	}
	
	/**
	 * Add message to channel
	 * @param userID
	 * @param message
	 * @param channelID
	 * @throws SQLException 
	 */
	public void addMessage(String message, String channelID) {
		
		String query = "INSERT INTO Messages (fromID, channelID, messageText) VALUES (?, ?, ?)";
		
		String[] values = new String[3];
		values[0] = String.valueOf(this.userID);
		values[1] = channelID;
		values[2] = message;
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Run just after login. Will reset the join time of channels
	 */
	public void onLogin()
	{
		String query = "UPDATE Subscriptions SET joinTime=CURRENT_TIMESTAMP where userID=?";
		String[] values = new String[1];
		values[0] = String.valueOf(this.userID);
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get all messages for a user since a given time
	 * @param lastMID - last Message ID
	 * @return	JSONArray array [
	 * 				object {
	 * 					"MID": <Message ID>
	 * 					"time": <time>
	 * 					"text": <text>
	 * 					"channelID": <channelID>
	 * 					"channelName": <channelName>
	 * 					"fromUserID": <fromUserID>
	 * 					"fromUsername": <fromUsername>
	 * 				}
	 * 			]
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getMessages(String lastMID) throws SQLException {
		String query;
		String[] values;
		JSONArray out;
		
		query = "select "
						+ "`Messages`.`MID`, "
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
						+ "`Messages`.`MID` > ? "				// timestamp of last message received
					+ "order by " 
						+ "`Messages`.`timeSent` ASC;";
		
		values = new String[2];
		values[0] = String.valueOf(this.userID);
		values[1] = lastMID;
		
		out = this.dbc.query(query, values);
		
		return out;
	}
}
