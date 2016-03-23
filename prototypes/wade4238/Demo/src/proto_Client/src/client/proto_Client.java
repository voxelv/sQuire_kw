package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.*;
import squire.ServerConnection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class proto_Client {
	private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Client");
    private JTextField dataField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 60);
    private ServerConnection server;
    
    
    
    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Enter in the
     * listener sends the textfield contents to the server.
     */
    public proto_Client() {

    	server = new ServerConnection("localhost", 9898);
        // Layout GUI
        messageArea.setEditable(false);
        frame.getContentPane().add(dataField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");

        // Add Listeners
        dataField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield
             * by sending the contents of the text field to the
             * server and displaying the response from the server
             * in the text area.  If the response is "." we exit
             * the whole application, which closes all sockets,
             * streams and windows.
             */
            public void actionPerformed(ActionEvent e) {
            	JSONObject params = new JSONObject();

                params.put("1", "parameter1");
                params.put("2", "parameter2");	// miscellaneous parameters
            	
                Object response;
                response = server.sendSingleRequest(dataField.getText(), params);	// send a single request
                
                JSONArray responseArr = (JSONArray) response;
                
                for (int i = 0; i< responseArr.size(); i++)
                {
	                JSONObject singleObj = (JSONObject) responseArr.get(0);
	                runAction(singleObj);		// For each request received back, run the actions associated.
                }
                
                dataField.selectAll();
            }
        });
    }
    
    private void runAction(JSONObject request)
    {
    	String action = (String) request.get("action");
    	JSONObject parameters = (JSONObject) request.get("parameters");
    	String result = (String) request.get("result");
    	
    	// If we want to use a more advanced method...
    	// http://stackoverflow.com/questions/160970/how-do-i-invoke-a-java-method-when-given-the-method-name-as-a-string
    	
        messageArea.append("RESULT: " + result + "\n");
    	
    	if (action.compareTo("chat") == 0)
    	{
    		System.out.println("(client) doing the chat thing!");
    	}
    	else
    		System.out.println("Some other action");
    }


    /**
     * Runs the client application.
     */
    public static void main(String[] args) throws Exception {
    	
        proto_Client client = new proto_Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.pack();
        client.frame.setVisible(true);
//        client.connectToServer();
    }
}
