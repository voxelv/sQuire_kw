package sq.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sq.app.model.User;
//import sq.app.view.ChatPaneController;
import sq.app.view.LoginPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	//private User user;
	//private User chris;
	//private User chris2;
	public static ArrayList<User> list = new ArrayList<User>();
	
	
	
	public MainApp(){
		/*user = new User("username");
		user.setEmail("email@email.com");
		user.setPassword("password");
		
		chris = new User("chris");
		chris.setEmail("chris@email.com");
		chris.setPassword("abc");
		
		chris2 = new User("chris");
		chris2.setEmail("chris2@email.com");
		chris2.setPassword("abc");
		
		list.add(user);
		list.add(chris);
		list.add(chris2);
		
		for(User d : list){
	        if(d.getUsername() != null && d.getUsername().contains("chris"))
	        	System.out.println(d);
	    }
		
		System.out.println("hello " + user.getUsername() + " " + chris.getUsername() + " " );*/
	}
	
	public static List<User> getList(){
		return list;
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

	        // Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Chat");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the person into the controller.
	        //ChatPaneController controller = loader.getController();
	        //controller.setDialogStage(dialogStage);
	        //controller.setPerson(person);

	        
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

	        // Set the person into the controller.
	        LoginPaneController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        //controller.setPerson(person);

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
	
	public static void main(String[] args) {
		launch(args);
		
	}
}
