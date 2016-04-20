package sq.app;

import java.io.IOException;


import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sq.app.model.ServerConnection;
import sq.app.model.User;
import sq.app.view.LoginPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	
	public static Connection conn = null;
	public static ServerConnection server;
	public static User CurrentUser;
	
	
	public MainApp(){
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		CurrentUser = new User();
		server = new ServerConnection("squireRaspServer.ddns.net", 9898);
		
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

  
		} catch (IOException e){
			e.printStackTrace();
			
		}
	}
	
	public void showChatPane() {
	    try {
	        // Load the fxml file and create a new stage for the popup dialog.
	        FXMLLoader loader2 = new FXMLLoader();
	        loader2.setLocation(MainApp.class.getResource("view/ChatPane.fxml"));
	        AnchorPane page = (AnchorPane) loader2.load();
	        
	        Stage dialogStage = new Stage();
	        dialogStage.initOwner(primaryStage);
	        dialogStage.setTitle("Chat");
	        dialogStage.setAlwaysOnTop(true);
	        dialogStage.initModality(Modality.NONE);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);


	        dialogStage.show();
	        
	        

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean showLoginPane() {
	    try {
	        // Load the fxml file and create a new stage for the popup dialog.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/LoginPane.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();

	        // Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Login");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        LoginPaneController controller = loader.getController();
	        controller.setDialogStage(dialogStage);


	        // Show the dialog and wait until the user closes it
	        dialogStage.showAndWait();

	        return controller.isOkClicked();
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
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
