package sq.app.view;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sq.app.MainApp;
import javafx.scene.control.TextArea;

public class ChatPaneController {
	private boolean sendClicked = false;
	private Stage dialogStage;
	
	@FXML
	private TextField Message;
	@FXML
	private TextArea History;
	@FXML
	private ChoiceBox<String> Channel;
	
	public MainApp mainApp;
	
	public ChatPaneController(){
	}
	
	@FXML
    private void initialize() {
    }
	
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
	
	public boolean isSendClicked() {
        return sendClicked;
    }
}
