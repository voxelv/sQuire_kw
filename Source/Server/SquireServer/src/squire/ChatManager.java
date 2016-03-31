package squire;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChatManager {
	DBConnector dbc;
	
	public ChatManager() {
		try {
            dbc = new DBConnector();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            dbc.setDatabase(dbc.dbName);
            //dbc.insertUser("Jeff");
            //dbc.insertChannel("Channel1");
            //dbc.insertSubscription(1, 1, ts);
            //dbc.insertMessage(1, 1, "Hello world", ts);
        } catch (SQLException e) {
            DBConnector.printSQLException(e);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        
        
//        dbc.closeConnection();
		
	}
	
	/**
	 * Unsubscribe a user from a channel
	 * @param userID
	 * @param channelID
	 * @return JSONArray - list of channels still subscribed to
	 * @throws SQLException 
	 */
	public JSONArray leaveChannel(int userID, int channelID) throws SQLException {
		String query = "delete from Subscriptions where userID=? and channelID=?";
		String[] values = new String[2];
		values[0] = String.valueOf(userID);
		values[1] = String.valueOf(channelID);
		
		ResultSet res = this.dbc.query(query, values);
		
		
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
	 * @throws SQLException 
	 */
	public void addMessage(int userID, String message, int channelID) throws SQLException {
		String query = "insert into Message (timeSent, fromID, channelID, messageText) values (?, ?, ?, ?)";
		
		String[] values = new String[4];
		values[0] = "2016-03-30 15:30";
		values[1] = "1";
		values[2] = "1";
		values[3] = "hello, world!";
		
		this.dbc.query(query, values);
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
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getMessages(int userID, String lastMsgTime) throws SQLException {
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
		
		String[] values = new String[2];
		values[0] = String.valueOf(userID);
		values[1] = lastMsgTime;
		
		ResultSet res = this.dbc.query(query, values);
		
		JSONArray out = new JSONArray();
		while(res.next())
		{
			JSONObject msg = new JSONObject();
			
			msg.put("time", res.getString(1));
			msg.put("text", res.getString(4));
			msg.put("channelID", res.getString(3));
			msg.put("channelName", res.getString(5));
			msg.put("fromUserID", res.getString(2));
			msg.put("fromUsername", res.getString(6));
			
			out.add(msg);
		}
		
		return out;
	}
}
