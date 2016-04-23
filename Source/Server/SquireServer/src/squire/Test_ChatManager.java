package squire;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

public class Test_ChatManager {
	ChatManager testManager;
	DBConnector dbc;
	
	
	public Test_ChatManager()
	{
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
		
		testManager = new ChatManager(dbc);
	}
	
	@Test
	public void test_setUserID() {
		Random rand = new Random();
		int randomNum = rand.nextInt((10000 - 1) + 1) + 1;
		
		testManager.setUserID(randomNum);
		assertEquals(randomNum, testManager.getUserID());
	}
	
	@Test
	public void test_getChannels()
	{
		this.setTestUserID();
		
		// Join a channel, to make sure that it will retrieve at least 1 channel in the list
		testManager.joinChannel("GWCustomChannel");
		
		JSONArray channels = testManager.getChannels();
		
		// iterate through all the channels
		for (int i = 0; i < channels.size(); i++)
		{
			JSONObject channel = (JSONObject) channels.get(i);
			
			// Pull the channelID out
			String channelID = (String) channel.get("channelID");
			
			// verify not null
			assertNotNull(channelID);
			
			// parse as int, and verify valid ID num
			int intChannelID = Integer.parseUnsignedInt(channelID);
			if (intChannelID <= 0)
				fail("ChannelID is not a valid integer.");
			
			// Pull the channel name out
			String channelName = (String) channel.get("channelName");
			assertNotNull(channelName);
			
			// Verify it's longer than 0 characters
			if (channelName.length() == 0)
				fail("channelName is 0 characters long");
			
			// Pull the joinTime out
			String joinTime = (String) channel.get("joinTime");
			assertNotNull(joinTime);
			
			// Verify it's longer than 0 characters
			if (joinTime.length() == 0)
				fail("joinTime is 0 characters long");
		}
	}
	
	@Test
	public void test_joinChannel()
	{
		this.setTestUserID();
		
		// Leave all current channels
		this.leaveAllChannels();
		
		// Add capitalized letter at front, since the first char will be capitalized anyway
		String channelName = "A" + this.nextRandomName();
				
		// join new channel
		JSONArray newChannelList = testManager.joinChannel(channelName);
		
		// go through all channels now part of
		for (int i = 0; i < newChannelList.size(); i++)
		{
			JSONObject channel = (JSONObject) newChannelList.get(i);
			
			// Pull the channelID out
			String channelID = (String) channel.get("channelID");
			
			// verify not null
			assertNotNull(channelID);
			
			// parse as int, and verify valid ID num
			int intChannelID = Integer.parseUnsignedInt(channelID);
			if (intChannelID <= 0)
				fail("ChannelID is not a valid integer.");
			
			// Pull the channel name out
			String listChannelName = (String) channel.get("channelName");
			assertNotNull(listChannelName);
			
			// Verify it's longer than 0 characters
			if (listChannelName.length() == 0)
				fail("channelName is 0 characters long");
			
			// Match against the original name
			assertEquals("Channel name doesn't match", listChannelName, channelName);
			
			// Pull the joinTime out
			String joinTime = (String) channel.get("joinTime");
			assertNotNull(joinTime);
			
			// Verify it's longer than 0 characters
			if (joinTime.length() == 0)
				fail("joinTime is 0 characters long");
			
		}
	}
	
	@Test
	public void test_leaveChannel()
	{
		// Set userID for testing
		this.setTestUserID();
		
		// Leave all current channels
		this.leaveAllChannels();
		
		// Add capitalized letter at front, since the first char will be capitalized anyway
		String channelName = "A" + this.nextRandomName();
		
		// Join specific channel (name generated)
		testManager.joinChannel(channelName);
		
		// Assume it's joined, since that's covered by a different test
		
		// leave channel
		testManager.leaveChannel(channelName);
		JSONArray newChannelList = testManager.getChannels();
		
		// Go through all channels, and make sure it's no longer there
		for (int i = 0; i < newChannelList.size(); i++)
		{
			JSONObject listChannel = (JSONObject) newChannelList.get(i);
			
			String listChannelName = (String) listChannel.get("channelName");
			assertNotEquals("Found the channel that was supposed to have been left", channelName, listChannelName);
		}
	}
	
	@Test
	public void test_addMessage()
	{
		// Set userID for testing
		this.setTestUserID();
		
		// leave all current channels
		this.leaveAllChannels();
		
		// Add capitalized letter at front, since the first char will be capitalized anyway
		String channelName = "A" + this.nextRandomName();
		
		// Join specific channel (name generated)
		JSONArray channelList = testManager.joinChannel(channelName);
		
		// Get the channelID of the one we just joined
		String channelID = (String)( (JSONObject)channelList.get(0) ).get("channelID");
		
		// Generate random message
		String message = this.nextRandomName();
		
		// Add message
		testManager.addMessage(message, channelID);
		
		JSONArray msgArray = null;
		try {
			msgArray = testManager.getMessages("0");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(msgArray);
		JSONObject receivedMsg = (JSONObject) msgArray.get(0);
		String msgText = (String) receivedMsg.get("messageText");
//		String msgText = (String) ( (JSONObject)msgArray.get(0) ).get("messageText");
		System.out.println("original: '" + message + "'");
		System.out.println("Received: '" + msgText + "'");
		
		if (msgText.compareTo(message) != 0)
			fail("message sent and message received don't match");
	}
	/******************* Supporting Functions *********************/
	
	public void setTestUserID()
	{
		testManager.setUserID(1);
	}
	
	public void leaveAllChannels()
	{
		JSONArray channelList = testManager.getChannels();
		
		// Leave all channels
		for (int i = 0; i < channelList.size(); i++)
		{
			JSONObject listChannel = (JSONObject) channelList.get(i);
			String listChannelName = (String) listChannel.get("channelName");
			testManager.leaveChannel(listChannelName);
		}
	}
	
	public String nextRandomName() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
}
