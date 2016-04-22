package sq.app.view;


import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import sq.app.MainApp;
import sq.app.model.ServerConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class LoginPaneController {
	private boolean okClicked = false;
	private Stage dialogStage;
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private ServerConnection server;
	
	
	@FXML
	private TextField LPassword;
	@FXML
	private TextField LUsername;
	
	@FXML
	private TextField FirstName;
	@FXML
	private TextField LastName;
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
		server = MainApp.GetServer();
    }

	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
	
	public boolean isOkClicked() {
        return okClicked;
    }
	
	@FXML
    private void handleOk() {
		if(Register()){
			System.out.println("register success");
		}	  
    }
	
	@FXML
	private void handleLogin(){
		if(Login()){
			okClicked = true;
			System.out.println("login success");
			dialogStage.close();
		}
			
	}
	
	@FXML
	private void handleCancel(){
		dialogStage.close();
	}
	
	@FXML
	public void handleEnterPressed(KeyEvent event){
		if(event.getCode() == KeyCode.ENTER){
			handleLogin();
		}
	}
	
	
	private boolean Register(){
		String errorMessage = "";
		
		//User temp;
		
		if(Username.getText() == null || Username.getText().length() < 0){
			errorMessage += "No valid Username (at least 1 characters)!\n";
		} 
		
		if(Email.getText() == null || Email.getText().length() == 0 || matches(Email.getText()) == false){
			errorMessage += "No valid Email!\n";
		}
		
		if(FirstName.getText() == null || FirstName.getText().length() == 0){
			errorMessage += "No valid First Name!\n";
		}
		
		if(LastName.getText() == null || LastName.getText().length() == 0){
			errorMessage += "No valid Last Name!\n";
		}
		
		if((Password1.getText() == null || Password1.getText().length() == 0)||(Password2.getText() == null || Password2.getText().length() == 0)){
			errorMessage += "No valid Password (Enter both Text Fields)!\n";
		}else{
			if(Password1 != null && Password2 != null && Password1.getText().equals(Password2.getText())){
			}else{if(Password1.getText().equals(Password2.getText())){
			}else{
				errorMessage += "Passwords do not match!\n";
				}
			}
		}
		
		
		if (errorMessage.length() == 0) {
			if(performRegister()){
			
			return true;
			} else {
				Alert alert = new Alert(AlertType.ERROR);
	            alert.initOwner(dialogStage);
	            alert.setTitle("Failure to Register");
	            alert.setHeaderText("Something went wrong!");
	            alert.setContentText("Don't look at me. This is your fault.");
	            alert.showAndWait();
	            
	            return false;
			}
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
	
	private boolean Login(){
		String errorMessage = "";
		
		if(Username.getText() == null || Username.getText().length() < 0){
			errorMessage += "No valid Username (at least 1 characters)!\n";
		}
		
		if(LPassword.getText() == null || LPassword.getText().length() == 0){
			errorMessage += "No valid Password!\n";
		}
		
		if (errorMessage.length() == 0) {
			
			if(performLogin()){
				return true;
			} else {
				Alert alert = new Alert(AlertType.ERROR);
	            alert.initOwner(dialogStage);
	            alert.setTitle("Failure to Login");
	            alert.setHeaderText("Welcome to logging in school...");
	            alert.setContentText("Aaaaaand you fail!");
	            alert.showAndWait();
	            
	            return false;
			}
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
	
	private boolean performLogin(){
		JSONObject params = new JSONObject();
		
		// Set all the parameters you need
		params.put("username", LUsername.getText());
		params.put("password", LPassword.getText());
		
		// Set the category
		String category = "User";
		
		// Set the action
		String action = "Login";
		String returnValue = (String) server.sendSingleRequest(category, action, params);
		
		JSONObject loginObj = null;
		try {
//			System.out.println(stringResult);
			loginObj = (JSONObject) new JSONParser().parse(returnValue);
			
			MainApp.CurrentUser.setUserID(Integer.parseInt((String) loginObj.get("userID"))) ;
			//MainViewController.setUser(MainApp.CurrentUser.getUserID());
			System.out.println(Integer.toString(MainApp.CurrentUser.getUserID()));
		} catch (ParseException e1) {
			e1.printStackTrace();
			
		}
        
        if (MainApp.CurrentUser.getUserID() > 0)
        {
        	return true;
        }
        else
        {
        	return false;
        }
	}
	
	private boolean performRegister(){
		JSONObject params = new JSONObject();
		String success = "";
		
		// Set all the parameters you need
		params.put("uName", Username.getText());
		params.put("email", Email.getText());
		params.put("fName", FirstName.getText());
		params.put("lName", LastName.getText());
		params.put("pWord", Password1.getText());
		
		// Set the category
		String category = "User";
		
		// Set the action
		String action = "createAccount";
		
		String returnValue = (String) server.sendSingleRequest(category, action, params);
		System.out.println(returnValue);
		
		JSONObject registerObj = null;
		try {
//			System.out.println(stringResult);
			registerObj = (JSONObject) new JSONParser().parse(returnValue);
			
			
			System.out.println(returnValue);
		} catch (ParseException e1) {
			e1.printStackTrace();
			
		}
        
        if (returnValue.equals("Success"))
        {
        	Username.getText();
    		Email.getText();
    		FirstName.getText();
    		LastName.getText();
    		Password1.getText();
        	return true;
        }
        else
        {
        	return false;
        }
	}
	
}
