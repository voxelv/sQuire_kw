package test;

import org.junit.Test;

import junit.framework.TestCase;
import sq.app.MainApp;
import sq.app.model.ServerConnection;

public class MainAppTest extends TestCase {
	protected MainApp tester = new MainApp();
	
	
	@Test
	public void testPanes() throws ClassNotFoundException{
		tester.main(null);
		ServerConnection servertest = new ServerConnection("squireRaspServer.ddns.net", 9898);
		assertNotNull("primary stage is null", tester.getPrimaryStage());
		assertNotNull("chat stage is null", tester.getChatStage());
		assertNotNull("login stage is null", tester.getLoginStage());
		
		assertNotNull("root layout is null", tester.rootLayout);
		assertNotNull("current user is null", tester.currentUser);
		assertNotNull("conn user is null", tester.GetConnection());
		assertTrue( tester.GetServer().equals(servertest));
		assertNotNull("chat manager user is null", tester.chatManager);
		assertNotNull("main controller user is null", tester.mainController);
	}
}
