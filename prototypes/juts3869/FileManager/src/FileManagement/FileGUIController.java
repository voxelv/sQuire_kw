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
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
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
	ServerConnection server;
	public static Connection conn = null;
	int MyuserID = 2;
    int currPID = 0;
    int systemPID = 0;

	String currProjectName = "";
	/*
	public void FileGUIController(){
		
	}*/
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


        TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create Project");
    	dialog.setHeaderText("sQuire Project");
    	dialog.setContentText("Please enter the project name:");
    	Optional<String> result = dialog.showAndWait();
    	conn = Main.GetConnection();
    	int currID = 1;
    	boolean isExist = false;
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
    	if (result.isPresent()){
    	    currProjName = result.get();

        	query = "SELECT PID FROM ProjectAccess where userID like '" + MyuserID + "'";
            //Statement st1 = conn.createStatement();
            ResultSet rs1 = st.executeQuery(query);

            while(rs1.next()){

            	int myPid = rs1.getInt("PID");

            	String query2 = "SELECT pname FROM Projects where PID like '" + myPid + "'";
                Statement st2 = conn.createStatement();
            	ResultSet re2 = st2.executeQuery(query2);
            	if(re2.next()){
            		String projName = re2.getString("pname");
                	if( Objects.equals(projName,currProjName)){
                		isExist = true;
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Warning");
                        alert.setContentText("Project name already exist.");
                        alert.showAndWait();
                        break;
                	}
            	}
            }

            if(isExist == false){
            	Statement s = conn.createStatement();
            	String sql = "INSERT INTO Projects(PID,pname)VALUE('" + currID + "','" + currProjName + "')";
            	s.executeUpdate(sql);
            	sql = "INSERT INTO ProjectAccess(PID, userID)VALUE('" + currID + "','" + MyuserID + "')";
            	s.executeUpdate(sql);
            }
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
    	
		server = new ServerConnection("squireRaspServer.ddns.net", 9898);
    	
    	conn = Main.GetConnection();
    	IniTree();
    	curr_position.setText("sQuire Project");
 //       create_button.lookup(".arrow").setStyle("-fx-padding: 0;");

    }

    public TreeItem<String> IniTree() {
        TreeItem<String> tree_root = new TreeItem<>("sQuire Project");
    	tree_root.setExpanded(true);
    	try {
        	JSONObject params = new JSONObject();
        	String category = "project";
        	String action = "getprojects";
        	// Send stuff to the server, await response.
        	String returnValue = (String) server.sendSingleRequest(category, action, params);
        	System.out.println(returnValue);
        	// Get the First result's object
        	Object returnObj;
        	JSONArray outJSONArray;
        	JSONObject outJSONObject;
        	try {
        		// Parse the object
        		returnObj = new JSONParser().parse(returnValue);
        		
        		// if it's a JSONArray, cast output as JSONArray
        		outJSONArray = (JSONArray) returnObj;
        		
        		// if it's JSONObject, cast output as JSONObject
        		outJSONObject = (JSONObject) returnObj;
        		
        		// Use get() to get specific properties from the return ----ARRAY----
        		outJSONArray.get(0);
        		
        		// Use get() to get specific properties from the return ----OBJECT----
        		outJSONObject.get("pname");
        		
        	} catch (ParseException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
    		
    		
			String query = "SELECT * FROM ProjectAccess WHERE userID like '" + MyuserID + "'";
	        Statement st;
			st = conn.createStatement();
	        ResultSet rs = st.executeQuery(query);
	        while (rs.next()){
	        	int PID = rs.getInt("PID");
//	        	System.out.println(PID);
	        	query = "SELECT pname FROM Projects WHERE PID like '" + PID + "'";
	        	Statement st1 = conn.createStatement();
	        	ResultSet rs1 = st1.executeQuery(query);

		        rs1.next();
		        String ProjectName = rs1.getString("pname");
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
/******************************************************************************/
    @FXML TextField curr_position;
    @FXML private void file_select(MouseEvent mouse) throws SQLException{
        if(mouse.getClickCount() == 2){
            TreeItem<String> item = structure_tree.getSelectionModel().getSelectedItem();
            selected = item;
            currProjectName = item.getValue();
            curr_position.setText(item.getValue());

            if(systemPID == 0){
                systemPID = getPID(item.getValue(),MyuserID);
            	setTree();
            }

        }
    }
/**
 * @throws SQLException ****************************************************************************/
    private void setTree () throws SQLException{
    	TreeItem<String> tree_root = new TreeItem<>(currProjectName);

    	conn = Main.GetConnection();
		String query = "SELECT pdname FROM PDirs WHERE pid LIKE '" + systemPID +"' AND parentid IS NULL";
	    Statement st = conn.createStatement();
	    ResultSet rs = st.executeQuery(query);
	    while(rs.next()){
	    	String tempDirName = rs.getString("pdname");
	    	TreeItem<String> dir = new TreeItem<>(tempDirName);
	    	tree_root.getChildren().add(dir);
	    }
    	structure_tree.setRoot(tree_root);
    	tree_root.setExpanded(true);

    }

/******************************************************************************/

    @FXML private void HomeButton(){
    	conn = Main.GetConnection();
    	IniTree();
    	curr_position.setText("sQuire Project");
    	selected = null;
    	currPID = 0;
    	systemPID = 0;
    }

/******************************************************************************/
    TreeItem<String> selected = null;
    @FXML private void DeleteButton() throws SQLException{
    	conn = Main.GetConnection();
    	int deletePID = 0;
    	if(selected == null){
            warning("No project or file selected.");
    	} else if(selected.getParent()==null || selected.getParent().getValue() == "sQuire Project"){
    		System.out.println("Delete Whole Project");
    		String query = "SELECT PID FROM Projects where pname like '" + selected.getValue() + "'";
    	    Statement st = conn.createStatement();
    	    ResultSet rs = st.executeQuery(query);
    	    while(rs.next()){
    	    	int tempPID = rs.getInt("PID");
//    	    	System.out.println(tempPID);
    	    	query = "SELECT PID FROM ProjectAccess WHERE PID LIKE '" + tempPID +"' AND userID like '" + MyuserID + "' LIMIT 1";
        	    Statement st1 = conn.createStatement();
        	    ResultSet rs1 = st1.executeQuery(query);
        	    if(rs1.next()){ deletePID = rs1.getInt("PID");}
    	    }
    	    System.out.println(deletePID);
    	    if(deletePID != 0){
    	    	String deleteProj = "DELETE FROM ProjectAccess WHERE PID LIKE '" + deletePID + "' AND userID like '" + MyuserID + "' LIMIT 1";
    	  	    System.out.println(deleteProj);
    	    	Statement st2 = conn.createStatement();
    	    	st2.executeUpdate(deleteProj);

    	    	Statement st3 = conn.createStatement();
    	    	String deleteProj2 = "DELETE FROM Projects WHERE PID LIKE '" + deletePID + "'";
    	  	    System.out.println(deleteProj2);
    	    	st3.executeUpdate(deleteProj2);
    	    }
        	IniTree();
        	curr_position.setText("sQuire Project");
        	selected = null;
        	currPID = 0;
        	systemPID = 0;
    	    	/*        	query = "SELECT PID FROM ProjectAccess where userID like '" + MyuserID + "'";

    		String deleteProj = "DELETE FROM Projects WHERE pname LIKE '" + selected.getValue() + "'";
    		Statement s = conn.createStatement();
            s.executeUpdate(deleteProj);
        	IniTree();
        	curr_position.setText("sQuire Project");
        	selected = null;
        	currPID = 0;
*/
    	} else {
    		selected.getParent().getChildren().remove(selected);
    	}

    }

/******************************************************************************/

    private void warning(String text){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Warning");
        alert.setContentText(text);
        alert.showAndWait();
    }


/******************************************************************************/
    private void isFolderExist(int pid, String pdname){

    }

    private int getPID(String pname, int userID) throws SQLException{

    	
    	
    	
    	Statement st = conn.createStatement();
    	int tempPID = 0;
    	String query = "SELECT PID FROM Projects where pname like '" + pname + "'";
	    ResultSet rs = st.executeQuery(query);
	    while(rs.next()){
	    	tempPID = rs.getInt("PID");
	    	System.out.println(tempPID);
	    	String query2 = "SELECT PID FROM ProjectAccess WHERE PID LIKE '" + tempPID + "' AND userID like '" + userID + "' LIMIT 1";
	    	Statement st2 = conn.createStatement();
	    	ResultSet rs2 = st2.executeQuery(query2);
	    	if(rs2.next()){ return rs.getInt("PID");}
	    }
	    return 0;
    }

    private void addFolder(String pdname, int pid, int parentid ) throws SQLException{
	    Statement st = conn.createStatement();
if(parentid == 0){
	String query = "INSERT INTO PDirs(pdname,pid)VALUE('" + pdname + "','" + pid + "')";
    st.executeUpdate(query);

}
    }



    @FXML private void CreateFolder() throws SQLException{

    	String currFolderName = "";

    	if(Objects.equals(curr_position.getText(), "sQuire Project") || Objects.equals(currProjectName,"")){
            warning("Can't add folder under the main project");
    	} else {
        	conn = Main.GetConnection();
//    		int i = getPID(currProjectName,MyuserID);
//    		System.out.println(i);
            TextInputDialog dialog = new TextInputDialog("");
        	dialog.setTitle("Create Folder");
        	dialog.setHeaderText("Folder");
        	dialog.setContentText("Please enter the folder name:");
        	Optional<String> result = dialog.showAndWait();
        	if (result.isPresent()){ currFolderName  = result.get();}
        	addFolder(currFolderName, getPID(currProjectName,MyuserID),0);
        	setTree();

    	}

/*    	String query = "SELECT * FROM Projects";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()){
        	int id = rs.getInt("PID");
        	if(currID != id){ break;}
        	currID++;
//        	String ProjectName = rs.getString("pname");
        }
*/

    }
/******************************************************************************/


/******************************************************************************/



/*****/
    @FXML MenuButton create_button;
}
