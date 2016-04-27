package sq.app;

import java.awt.Event;
import java.beans.EventHandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.Action;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sq.app.model.ChatManager;
import sq.app.model.ServerConnection;
import sq.app.model.User;
import sq.app.view.LoginPaneController;
import sq.app.view.MainViewController;

public class MainApp extends Application {

	private Stage primaryStage;
	private Stage chatStage;
	
	private Stage loginStage;
	public BorderPane rootLayout;
	
	private static Connection conn = null;
	private static ServerConnection server;
	public static User currentUser = null;
	public static ChatManager chatManager = null;
	public static MainViewController mainController = null;
	
	
	public MainApp(){
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		currentUser = new User();
		server = new ServerConnection("squireRaspServer.ddns.net", 9898);
		chatManager = new ChatManager(server);
		
		Connect();
		launch(args);
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("sQuire");
		
		
		initRootLayout();
		
		showMainView();

		showLoginPane();
		
		showChatPane();
		
	}
	
	public void initRootLayout(){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/rootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(event->{
				sq.app.MainApp.GetServer().closeIt();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void showMainView() {
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
			AnchorPane MainView = (AnchorPane) loader.load();
			
			rootLayout.setCenter(MainView);
	    	MainView.getScene().getStylesheets().add(sq.app.model.editor.EditorCodeArea.class.getResource("resources/java-keywords.css").toExternalForm());

	    	mainController = loader.getController();
  
		} catch (IOException e){
			e.printStackTrace();
			
		}
	}
	public static void setUser(User user){
		currentUser = user;
		mainController.setUserID(user.getUserID());
		MainViewController.userID = user.getUserID();
		mainController.initialize();
	}
	public static void setUser(String userName){
		mainController.setUserName(userName);
	}
	public static User getCurrentUser(){
		return currentUser;
	}

	public void showChatPane() {
	    try {
	    	
	    	
	        // Load the fxml file and create a new stage for the popup dialog.
	        FXMLLoader loader2 = new FXMLLoader();
	        loader2.setLocation(MainApp.class.getResource("view/ChatPane.fxml"));
	        AnchorPane page = (AnchorPane) loader2.load();
	        
	        chatStage = new Stage();
	        chatStage.initOwner(primaryStage);
	        chatStage.setTitle("Chat");
	        chatStage.setAlwaysOnTop(true);
	        chatStage.initModality(Modality.NONE);
	        Scene scene = new Scene(page);
	        chatStage.setScene(scene);

	        chatStage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    MainApp.chatManager.onLogin(MainApp.getCurrentUser().getUserID());
	}
	
	public boolean showLoginPane() {
	    try {
	        // Load the fxml file and create a new stage for the popup dialog.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/LoginPane.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();

	        // Create the dialog Stage.
	        loginStage = new Stage();
	        loginStage.setTitle("Login");
	        loginStage.initModality(Modality.WINDOW_MODAL);
	        loginStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        loginStage.setScene(scene);

	        LoginPaneController controller = loader.getController();
	        controller.setDialogStage(loginStage);


	        // Show the dialog and wait until the user closes it
	        loginStage.showAndWait();

	        return controller.isOkClicked();
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public Stage getChatStage() {
		return chatStage;
	}
	
	public Stage getLoginStage() {
		return loginStage;
	}
	public static Connection GetConnection(){
    	return conn;
    }
	
	public static ServerConnection GetServer(){
    	return server;
    }
	
	
	public static void Connect() throws ClassNotFoundException{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://SquireRaspServer.ddns.net:9897/squire","remote","squire!");
			System.out.println("Connected.");
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			
		}
	}
}
