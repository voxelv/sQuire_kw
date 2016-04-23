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
import sq.app.MainApp;
import sq.app.model.ChatManager;
//import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class ChatPaneController {
	
	private static final Pattern ADD_REGEX = Pattern.compile("/add\\s[0-9a-zA-Z]+", Pattern.CASE_INSENSITIVE);
	
//	ObservableList<String> channelList = FXCollections.observableArrayList("Gen", "1", "2");
	
	@FXML
	private TextField Message;
	@FXML
	private TextArea History;
	@FXML
	private ComboBox channelBox;
	
	@FXML
	private void initialize() {
		MainApp.chatManager.history = History;
		MainApp.chatManager.channelBox = channelBox;
		
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
		MainApp.chatManager.enterText(Message.getText());
		Message.clear();
    }
}
