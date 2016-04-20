package sq.app.view;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
//import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class ChatPaneController {
	
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
	
	
	
	@FXML
	public void handleEnterPressed(KeyEvent event){
		if(event.getCode() == KeyCode.ENTER){
			SendMessage();
		}
	}
	@FXML
	public void SendMessage() {
        History.appendText(channelBox.getValue() + ": " + Message.getText() + "\n");
        Message.clear();
    }
}
