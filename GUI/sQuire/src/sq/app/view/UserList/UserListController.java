package sq.app.view.UserList;


import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sq.app.MainApp;
import sq.app.model.ServerConnection;


public class UserListController {

	@FXML private ListView<String> userList = new ListView<String>();
	@FXML private static TextField yeah = new TextField();
	static ServerConnection server = MainApp.GetServer();
	    

	@FXML public void initialize(){
		
		ObservableList<String> names = FXCollections.observableArrayList();
        JSONObject params = new JSONObject();
        String ret =  (String) server.sendSingleRequest("User", "getOnlineUsers", params);
        
        JSONParser jpar = new JSONParser();
        
        try {
        	Object temp = jpar.parse(ret);
			JSONArray jray = (JSONArray) temp;
			
			for (int i = 0; i < jray.size(); i++)
			{
				JSONObject userObject = (JSONObject) jray.get(i);
				String username = (String) userObject.get("userName");
				names.add(username);
				System.out.println(username + " " + names);
				userList.setItems(names);
				System.out.println(userList.getItems());
			}
			
		} catch (ParseException e1) {
			e1.printStackTrace();
			
		}
        
        
	}
}
