package sq.app.view;

import javafx.fxml.FXML;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginPaneController {
	private boolean okClicked = false;
	private Stage dialogStage;
	
	@FXML
    private void initialize() {
    }

	
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
	
	public boolean isOkClicked() {
        return okClicked;
    }
	
	@FXML
    private void handleOk() {
            okClicked = true;
            dialogStage.close();
        
    }

}
