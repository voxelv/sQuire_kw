package test;

import org.junit.Test;

import junit.framework.TestCase;
import sq.app.model.User;

public class UserTest extends TestCase{
	protected User testuser1, testuser2, testuser3;
	
	protected void setUp(){
		testuser1 = new User();
		testuser1.setUserID(11);
		testuser2 = new User(2000000000);
		testuser3 = new User();
	}
	
	@Test
	public void test(){
		assertTrue(testuser1.getUserID() == 11);
		assertTrue(testuser2.getUserID() == 2000000000);
		assertTrue(testuser3.getUserID() == 0);
		
	}
	
}
