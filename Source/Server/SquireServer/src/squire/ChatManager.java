package squire;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChatManager {
	private DBConnector dbc;
	private int userID;
	
	public ChatManager() {
		try {
            dbc = new DBConnector();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            dbc.setDatabase(dbc.dbName);
        } catch (SQLException e) {
            DBConnector.printSQLException(e);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
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
						+ "`Channels`.`channelName`  "
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
		System.out.println("Joining Channel: '"+channelName +"'");
		String query = "Insert into "
					+ "Subscriptions (channelID, userID) "
						+ "select "
							+ "channelID, "
							+ "? "		// userID
						+ "from "
							+ "Channels "
						+ "where "
							+ "channelName = ?";
		
//		System.out.println("Query: '"+query + "'");
		
		String[] values = new String[2];
		values[0] = String.valueOf(this.userID);
		values[1] = channelName;
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("0:" + values[0] + "; 1: "+values[1]);
		
		JSONArray channelList = new JSONArray();
		
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
	public void addMessage(String message, String channelID) throws SQLException {
		
		String query = "insert into Messages (fromID, channelID, messageText) values (?, ?, ?)";
		
		String[] values = new String[3];
		values[0] = String.valueOf(this.userID);
		values[1] = channelID;
		values[2] = message;
		
		this.dbc.query(query, values);
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
//		out.add(0, timeObj);
		
		return out;
	}
}
