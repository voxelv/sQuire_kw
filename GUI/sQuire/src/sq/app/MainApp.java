package sq.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("sQuire");
		
		
		initRootLayout();
		
		showMainView();
		
		showLoginPane();
		
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
	        //PersonEditDialogController controller = loader.getController();
	        //controller.setDialogStage(dialogStage);
	        //controller.setPerson(person);

	        // Show the dialog and wait until the user closes it
	        dialogStage.showAndWait();

	        return false;//controller.isOkClicked();
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
