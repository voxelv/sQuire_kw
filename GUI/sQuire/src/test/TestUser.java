/* TestUser.java
 * 
 * Created by: Tim
 * On: Apr 21, 2016 at 10:30:35 AM
*/

package test;

import static org.junit.Assert.*;

import org.junit.Test;

import sq.app.model.User;


public class TestUser {
	
	@Test
	public void test_Constructor_no_params() {
		System.out.println("Testing User constructor no params");
		User u = new User();
		
		assertEquals(u.getUserID(), 0);
	}
	
	@Test
	public void test_Constructor_int() {
		System.out.println("Testing User constructor int");
		int x = 1;
		User u = new User(x);
		assertEquals(u.getUserID(), x);
		
		x = 2;
		u = new User(x);
		assertEquals(u.getUserID(), x);
		
		x = 1234567890;
		u = new User(x);
		assertEquals(u.getUserID(), x);
		
		x = -1;
		u = new User(x);
		assertEquals(u.getUserID(), x);
	}
	
	@Test
	public void test_getUserID() {
		System.out.println("Testing User.getUserID");
		System.out.println("Testing User constructor int");
		int x = 1;
		User u = new User(x);
		assertEquals(u.getUserID(), x);
		
		x = 2;
		u = new User(x);
		assertEquals(u.getUserID(), x);
		
		x = 1234567890;
		u = new User(x);
		assertEquals(u.getUserID(), x);
		
		x = -1;
		u = new User(x);
		assertEquals(u.getUserID(), x);
	}
	
	@Test
	public void test_setUserID() {
		System.out.println("Testing User.setUserID");
		System.out.println("Testing User constructor int");
		
		User u = new User();

		int x = 1;
		u.setUserID(x);
		assertEquals(u.getUserID(), x);
		
		x = 2;
		u.setUserID(x);
		assertEquals(u.getUserID(), x);
		
		x = 1234567890;
		u.setUserID(x);
		assertEquals(u.getUserID(), x);
		
		x = -1;
		u.setUserID(x);
		assertEquals(u.getUserID(), x);
	}
}
