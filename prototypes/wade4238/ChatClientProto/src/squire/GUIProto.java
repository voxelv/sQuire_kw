package squire;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GUIProto {
	private JFrame frame = new JFrame("Client");
    private JTextField dataField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 60);
    private ChatManager chat;
    private ServerConnection server;
    private int userID = 0;
    
    public GUIProto()
    {
    	messageArea.setEditable(false);
        frame.getContentPane().add(dataField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        
//        server = new ServerConnection("squireraspserver.ddns.net", 9898);		// From Anywhere
//        server = new ServerConnection("192.168.0.249", 9898);					// From same network as raspberry pi server
        this.server = new ServerConnection("localhost", 9898);					// From same computer as server program
    }
    
    public void fakeLogin()
    {
    	
    	/**************************** START OF REQUEST ****************************/
    	JSONObject params = new JSONObject();		// Create parameter object

    	params.put("userID", "1");
    	
        String result = (String) server.sendSingleRequest("User", "Login", params);
    	
        
        JSONObject loginObj = null;
		try {
//			System.out.println(stringResult);
			loginObj = (JSONObject) new JSONParser().parse(result);
			
			this.userID = Integer.parseInt((String) loginObj.get("userID"));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
        
        if (this.userID > 0)
        {
        	
        	chat = new ChatManager(dataField, messageArea, this.server);
        	dataField.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		chat.enterText(dataField.getText());
            		dataField.setText("");
            		chat.updateMessages(messageArea);
            	}
            });
        }
        else
        {
        	System.exit(0);
        }
        
        
        /**************************** END OF REQUEST ****************************/
    }
    
    public static void main(String[] args) throws Exception {
    	
        GUIProto client = new GUIProto();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.pack();
        client.frame.setVisible(true);
        
        
        client.fakeLogin();
    }
    
}
