/* Main.java
 * 
 * Created by: Tim
 * On: Mar 29, 2016 at 6:52:01 PM
*/

package sQuire_editor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Editor Test");
		
		HTMLEditor htmlEditor = new HTMLEditor();
		
		StackPane root = new StackPane();
		root.getChildren().add(htmlEditor);
		primaryStage.setScene(new Scene(root, 400, 300));
		primaryStage.show();
	}
}
