package sq.app.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import sq.app.MainApp;
import sq.app.model.User;

public class LoginPaneController {
	private boolean okClicked = false;
	private Stage dialogStage;
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	@FXML
	private TextField Username;
	@FXML
	private TextField Email;
	@FXML
	private PasswordField Password1;
	@FXML
	private PasswordField Password2;
	
	public MainApp mainApp;
	
	public LoginPaneController(){
	}
	
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
		if(goodInput()){
			okClicked = true;
			dialogStage.close();
		}
			  
    }
	
	@FXML
	private void handleCancel(){
		dialogStage.close();
	}
	
	private boolean goodInput(){
		String errorMessage = "";
		
		if(Username.getText() == null || Username.getText().length() <= 4){
			errorMessage += "No valid Username (at least 4 characters)!\n";
		}
		if(Email.getText() == null || Email.getText().length() == 0 || matches(Email.getText()) == false){
			errorMessage += "No valid Email!\n";
		}
		if(Password1.getText().equals(Password2.getText())){
		}else{
			errorMessage += "Passwords do not match!\n";
		}
		if (errorMessage.length() == 0) {
            return true;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
		}
	}
	
	private boolean matches(String str){
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(str);
		return matcher.find();
	}
	//public void setMainApp(MainApp mainApp){
	//	this.mainApp = mainApp;
	//}

}
