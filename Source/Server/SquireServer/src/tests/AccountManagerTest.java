package tests;

import java.sql.SQLException;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

import squire.AccountManager;
import squire.DBConnector;

public class AccountManagerTest {
	AccountManager testManager;
	DBConnector dbc;
	String username;

	public AccountManagerTest(){
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
		//this.dbc.setProperties("mysql", "", "com.mysql.jdbc.Driver", 
			//	"squire", "root", "squire!", "localhost", 3306);
		testManager = new AccountManager(this.dbc);
	}
	
	@Test
	public void TestCreateAccount(){
		String regoutput, loginoutput;
		Random rand = new Random();
		int randomNum = rand.nextInt(10000) + 1;
		this.username ="testie"+String.valueOf(randomNum);
		regoutput = testManager.CreateAccount("testie", "mctesterson", 
				"testie"+String.valueOf(randomNum), this.username+"@email.com", "testpass");
		loginoutput = testManager.Login("testie"+String.valueOf(randomNum), "testpass");
		
		assertEquals("Did not return success" ,"Success", regoutput);
		assertEquals("Did not return success" ,"Success", loginoutput);
	}
	
	@Test
	public void TestGetID(){
		testManager.Login(username, "testpass");
		int id = testManager.GetUserAccountID();
		assertNotEquals("Invalid ID returned", -1, id);
	}
}
