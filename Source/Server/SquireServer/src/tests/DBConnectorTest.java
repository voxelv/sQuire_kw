package tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.junit.Test;

import squire.DBConnector;

public class DBConnectorTest {
	DBConnector dbc;
	
	public DBConnectorTest(){
		try {
			this.dbc = new DBConnector("mysql", "", "com.mysql.jdbc.Driver", 
					"squire", "remote", "squire!", "SquireRaspServer.ddns.net", 9897);
			//conn = (Connection) DriverManager.getConnection("jdbc:mysql://SquireRaspServer.ddns.net:9897/squire","remote","squire!");
			System.out.println("Connected.");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestQuery(){
		JSONArray returnValue = new JSONArray();
		String query = "Show tables;";
		String[] values = new String[0];
		try {
			returnValue = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String expected = "[{\"TABLE_NAME\":\"Channels\"},{\"TABLE_NAME\":\"FileEdits\"},{\"TABLE_NAME\":\"LineLocks\"},{\"TABLE_NAME\":\"Messages\"},{\"TABLE_NAME\":\"PDirs\"},{\"TABLE_NAME\":\"PFLines\"},{\"TABLE_NAME\":\"PFiles\"},{\"TABLE_NAME\":\"ProjectAccess\"},{\"TABLE_NAME\":\"Projects\"},{\"TABLE_NAME\":\"Subscriptions\"},{\"TABLE_NAME\":\"Users\"}]";
		assertEquals("Expected output not found", expected, String.valueOf(returnValue));
	}
	
	@Test
	public void TestTransaction(){
		JSONArray[] returnValue = null;
		String[] query = {"Show tables;"};
		String[][] values = new String[1][0];
		values[0] = new String[0];
		try {
			returnValue = this.dbc.transaction(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String expected = "[{\"TABLE_NAME\":\"Channels\"},{\"TABLE_NAME\":\"FileEdits\"},{\"TABLE_NAME\":\"LineLocks\"},{\"TABLE_NAME\":\"Messages\"},{\"TABLE_NAME\":\"PDirs\"},{\"TABLE_NAME\":\"PFLines\"},{\"TABLE_NAME\":\"PFiles\"},{\"TABLE_NAME\":\"ProjectAccess\"},{\"TABLE_NAME\":\"Projects\"},{\"TABLE_NAME\":\"Subscriptions\"},{\"TABLE_NAME\":\"Users\"}]";
		assertEquals("Expected output not found", expected, String.valueOf(returnValue[0]));
	}
	
	

}
