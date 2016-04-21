package sq.app.view;

import org.junit.Test;

import junit.framework.TestCase;

public class LoginPaneControllerTest extends TestCase {
	protected LoginPaneController ctrllr = new LoginPaneController();
	
	// initialize things
	protected void setUp(){
		
	}
	
	@Test
	public void testConstruct(){
		assertNotNull("Login Pane Controller is null", ctrllr);
	}
	@Test
	public void testOK(){
		assertFalse("OK clicked somehow", ctrllr.isOkClicked());
	}
	@Test
	public void testInputCheck(){
		assertFalse("test input function failed", ctrllr.goodInput());
		
	}
}