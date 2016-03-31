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
		
		this.dbc.query(query, values);
		
		
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
	public void addMessage(String userID, String message, String channelID) throws SQLException {
		
		String query = "insert into Messages (fromID, channelID, messageText) values (?, ?, ?)";
		
		String[] values = new String[3];
		values[0] = userID;
		values[1] = channelID;
		values[2] = message;
		
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
	public JSONArray getMessages(String userID, String lastMsgTime) throws SQLException {
		String query;
		String[] values;
		JSONArray out;
		JSONArray time;
		
		query = "select now()";
		values = new String[0];
		time = this.dbc.query(query, values);
		System.out.println("Time: "+time);
		JSONObject timeObj = (JSONObject) time.get(0);
		
		query = "select "
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
		
		values = new String[2];
		values[0] = userID;
		values[1] = lastMsgTime;
		
		out = this.dbc.query(query, values);
		out.add(0, timeObj);
		
//		JSONArray out = new JSONArray();
//		while(res.next())
//		{
//			JSONObject msg = new JSONObject();
//			
//			msg.put("time", res.getString(1));
//			msg.put("text", res.getString(4));
//			msg.put("channelID", res.getString(3));
//			msg.put("channelName", res.getString(5));
//			msg.put("fromUserID", res.getString(2));
//			msg.put("fromUsername", res.getString(6));
//			
//			out.add(msg);
//		}
		
		return out;
	}
}
