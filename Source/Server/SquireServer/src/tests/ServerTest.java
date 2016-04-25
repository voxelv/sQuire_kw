package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ServerSocket;

import org.json.simple.JSONObject;
import org.junit.Test;

import squire.Server;
import squire.Server.ServerThread;;

public class ServerTest extends squire.Server{
	TesterThread serverRunner;
	public ServerTest()
	{
		this.serverRunner = new TesterThread(0);
		this.serverRunner.start();
		
		while (this.serverRunner.running == false)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Test
	public void test_conn()
	{
		ServerConnection conn = new ServerConnection("localhost", 9898);
		assertNotNull(conn);
		
		JSONObject params = new JSONObject();
		params.put("uName", "handstand2002");
		params.put("pWord", "testPassword");
		Object res = conn.sendSingleRequest("User", "Login", params);
		String stringRes = (String) res;
		
		assertNotNull(stringRes);
		if (stringRes.length() == 0)
			fail("response 0 char long");
	}
	
	private class TesterThread extends Thread{
		int type;
		public boolean running;
		public TesterThread(int type)
		{
			running = false;
			this.type = type;
		}
		
		@Override
        public void run()
        {
			running = true;
			if (this.type == 0)			// if it's a server-type
			{
				int clientNumber = 0;
				ServerSocket listener = null;
				try {
					listener = new ServerSocket(9898);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        try {
		            while (true) {
		                new ServerThread(listener.accept(), clientNumber++).start();
		            }
		        } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
		            try {
						listener.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
			}
			
			
        }
	}
}
