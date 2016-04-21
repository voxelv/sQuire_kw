package sq.app.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
//import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class ChatPaneController {
	
	private static final Pattern ADD_REGEX = Pattern.compile("/add\\s[0-9a-zA-Z]+", Pattern.CASE_INSENSITIVE);
	
	ObservableList<String> channelList = FXCollections.observableArrayList("Gen", "1", "2");
	
	@FXML
	private TextField Message;
	@FXML
	private TextArea History;
	@FXML
	private ComboBox channelBox;
	
	
	
	@FXML
	private void initialize() {
		channelBox.setValue("Gen");
		channelBox.setItems(channelList);
		History.setEditable(false);
    }
	
	
	public ChatPaneController(){
	}
	
	private boolean matches(String str){
		Matcher matcher = ADD_REGEX.matcher(str);
		return matcher.find();
	}
	
	@FXML
	public void handleEnterPressed(KeyEvent event){
		if(event.getCode() == KeyCode.ENTER){
			SendMessage();
		}
	}
	@FXML
	public void SendMessage() {
		if(Message.getText().charAt(0) == '/'){
			if(matches(Message.getText())){
				String temp = Message.getText().substring(5);
				if(temp.length() < 4){
					if(!channelList.contains(temp)){
						channelList.add(temp);
					}
				} else {
					History.appendText("ERROR: invalid channel name(Must be less than 4 characters\n");
				}
			} else {
				History.appendText("ERROR: unknown command\n");
			}
		} else {
			History.appendText(channelBox.getValue() + ": " + Message.getText() + "\n");
	        Message.clear();
		}
        
    }
}
