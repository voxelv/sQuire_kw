package FileManagement;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FileGUIController implements Initializable {
    private static final TreeItem ProjectName = null;
	private static final TreeItem<String> treeItem = null;
	public static Connection conn = null;

/*
    public static void Main(String[] args) throws ClassNotFoundException {
        try {
        	Class.forName("com.mysql.jdbc.Driver");
        	conn = DriverManager.getConnection("jdbc:mysql://SquireRaspServer.ddns.net:9897/squire","remote","squire!");
        	System.out.println("Connected.");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
*/
/******************************************************************************/

    @FXML
    public void CreateProject() throws SQLException, ClassNotFoundException{
    	conn = Main.GetConnection();
    	int currID = 1;

    	String currProjName = "";

    	String query = "SELECT * FROM Projects";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()){
        	int id = rs.getInt("PID");
        	if(currID != id){ break;}
        	currID++;
//        	String ProjectName = rs.getString("pname");
        }

        TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create Project");
    	dialog.setHeaderText("sQuire Project");
    	dialog.setContentText("Please enter the project name:");
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    	    currProjName = result.get();
    	    Statement s = conn.createStatement();
            String sql = "INSERT INTO Projects(PID,pname)VALUE('" + currID + "','" + currProjName + "')";
            s.executeUpdate(sql);
    	}
    	IniTree();
    }

/******************************************************************************/

    @FXML AnchorPane root;
    @FXML public void locateFile(){
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Open File");
    	chooser.showOpenDialog(root.getScene().getWindow());
    }

/******************************************************************************/

    @FXML TreeView<String> structure_tree;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	conn = Main.GetConnection();
    	IniTree();
    }

    public TreeItem<String> IniTree() {
        TreeItem<String> tree_root = new TreeItem<>("sQuire");
    	tree_root.setExpanded(true);
    	try {
			String query = "SELECT * FROM Projects";
	        Statement st;
			st = conn.createStatement();
	        ResultSet rs = st.executeQuery(query);
	        while (rs.next()){
	        	String ProjectName = rs.getString("pname");
	        	int PID = rs.getInt("PID");
	        	TreeItem<String> ProjName = new TreeItem<>(ProjectName);
	        	tree_root.getChildren().add(ProjName);
	        }
	        structure_tree.setRoot(tree_root);
	        } catch (SQLException e) {
			e.printStackTrace();
		}
        return tree_root;
    }

    Object[][] array = null;

    private void StorePid(TreeItem<String> temp, int PID){
    	int i = 0;
    	while(array[i][0] != null){
    		array[0][0] = PID;
    		array[0][1] = temp;
    		i++;
    	}
    }

    private int GetPid(TreeItem<String> temp){
    	int i = 0;
    	while(array[i][1] != temp){
    		i++;
    	}
		return (int) array[i][0];
    }
/*************************************************/
    @FXML TextField curr_position;
    @FXML
    private void file_select(MouseEvent mouse){
        if(mouse.getClickCount() == 2){
            TreeItem<String> item = structure_tree.getSelectionModel().getSelectedItem();
//            url.setText(item.getValue());
            curr_position.setText(item.getValue());

        }
    }
}
