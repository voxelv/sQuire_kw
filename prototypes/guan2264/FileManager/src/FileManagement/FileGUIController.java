package FileManagement;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class FileGUIController implements Initializable {
	public static Connection conn = null;
	int MyuserID = 2;
    int currPID = 0;
	String currProjectName = "";
    TreeItem<StrucTree> selected = null;

	@FXML Text user;
    @FXML TextField curr_position;
    @FXML TreeView<StrucTree> structure_tree;


/***************************Connection***************************
 *	public static Connection conn = null;
 *
 *	public static void Connect() throws ClassNotFoundException{
 *		try {
 *			Class.forName("com.mysql.jdbc.Driver");
 *			conn = (Connection) DriverManager.getConnection("jdbc:mysql://SquireRaspServer.ddns.net:9897/squire","remote","squire!");
 *			System.out.println("Connected.");
 *		} catch (SQLException ex) {
 *			System.out.println("SQLException: " + ex.getMessage());
 *			System.out.println("SQLState: " + ex.getSQLState());
 *			System.out.println("VendorError: " + ex.getErrorCode());
 *
 *		}
 *	}
 *
 *	public static Connection GetConnection(){
 *   	return conn;
 *  }
 *
 ***************************Connection***************************/

/***************************Project Creation***************************/
    @FXML
    public void CreateProject() throws SQLException, ClassNotFoundException{
        TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create Project");
    	dialog.setHeaderText("sQuire Project");
    	dialog.setContentText("Please enter the project name:");
    	Optional<String> result = dialog.showAndWait();
        Statement st = conn.createStatement();
    	boolean isExist = false;
    	String currProjName = "";

    	if (result.isPresent()){
    	    currProjName = result.get();

        	String query = "SELECT PID FROM ProjectAccess where userID like '" + MyuserID + "'";
            ResultSet rs = st.executeQuery(query);

            while(rs.next()){

            	int myPid = rs.getInt("PID");

            	String query2 = "SELECT pname FROM Projects where PID like '" + myPid + "'";
                Statement st1 = conn.createStatement();
            	ResultSet re1 = st1.executeQuery(query2);
            	if(re1.next()){
            		String projName = re1.getString("pname");
                	if( Objects.equals(projName,currProjName)){
                		isExist = true;
                		warning("Project name already exist.");
                        break;
                	}
            	}
            }

            if(isExist == false){
            	String sql = "INSERT INTO Projects(pname)VALUE('" + currProjName + "')";
            	st.executeUpdate(sql);
            	sql = "SELECT LAST_INSERT_ID()";
            	rs = st.executeQuery(sql);
            	if(rs.next()){
            		int currID = rs.getInt("LAST_INSERT_ID()");
                	sql = "INSERT INTO ProjectAccess(PID, userID)VALUE('" + currID + "','" + MyuserID + "')";
                	st.executeUpdate(sql);
            	}
            }
    	}
    	IniTree();

    }

/***************************File Input***************************/

    @FXML AnchorPane root;
    @FXML public void locateFile(){
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Open File");
    	chooser.showOpenDialog(root.getScene().getWindow());
    }

/***************************TreeItem Class***************************/
    class StrucTree {
    	  String name;
    	  int id;
    	  int parentid;
    	  boolean isFile = false;
    	  boolean isDirectory = false;
    	  boolean isProject = false;
    	  @Override
    	  public String toString() { return name;}
    	  public int getID(){ return id;}
    	  public int getPID(){ return parentid;}
    	  public StrucTree(String name) { this.name = name;}
    	  public boolean isFile(){return isFile;}
    	  public boolean isDirectory(){return isDirectory;}
    	  public boolean isProject(){return isProject;}

    	  public void setName(String name){ this.name = name;}

    	  public boolean isExist(String name){ return Objects.equals(this.name, name);}

    	  public StrucTree(String type,String name, int id) {
    		  if(Objects.equals(type,"p")){
    			  isProject = true;
    			  this.name = name;
    			  this.id = id;
    		  }
    		  if(Objects.equals(type,"f")){
    			  this.isFile = true;
    			  this.id = id;
    			  this.name = name;
    		  }
    		  if(Objects.equals(type,"d")){
    			  this.isDirectory = true;
    			  this.id = id;
    			  this.name = name;
    		  }
    	  }

    	  public StrucTree(String type,String name, int id, int pid) {
    		  if(Objects.equals(type,"f")){
    			  this.isFile = true;
    			  this.id = id;
    			  this.name = name;
    			  this.parentid = pid;
    		  }
    		  if(Objects.equals(type,"d")){
    			  this.isDirectory = true;
    			  this.id = id;
    			  this.name = name;
    			  this.parentid = pid;
    		  }
    	  }
    	}
/***************************Display UserName***************************/
    private void setUser(int id) throws SQLException{
		String query = "SELECT userName FROM Users WHERE userID like '" + id + "' LIMIT 1";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        if(rs.next()){ user.setText(rs.getString("userName"));}
    }

/***************************Rename Function***************************/
    @FXML public void Rename() throws SQLException{
    	boolean isExist = false;

    	if(selected == null){
            warning("No project or file selected.");
    	} else if(selected.getValue().isProject()){
    		TextInputDialog dialog = new TextInputDialog("");
        	dialog.setTitle("Rename");
        	dialog.setHeaderText("Rename Project");
        	dialog.setContentText("Please enter the project name:");
        	Optional<String> result = dialog.showAndWait();
            Statement st = conn.createStatement();
        	String currProjName = "";

        	if (result.isPresent()){
        		currProjName = result.get();
            	String query = "SELECT PID FROM ProjectAccess where userID like '" + MyuserID + "'";
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){

                	int myPid = rs.getInt("PID");

                	String query2 = "SELECT pname FROM Projects where PID like '" + myPid + "'";
                    Statement st1 = conn.createStatement();
                	ResultSet re1 = st1.executeQuery(query2);
                	if(re1.next()){
                		String projName = re1.getString("pname");
                    	if( Objects.equals(projName,currProjName)){
                    		isExist = true;
                    		warning("Project name already exist.");
                            break;
                    	}
                	}
                }

                if(isExist == false){
                	String sql = "UPDATE Projects SET pname='" + currProjName + "' WHERE PID='" + selected.getValue().getID() + "'";
                	st.executeUpdate(sql);
                	selected.getValue().setName(currProjName);
                	structure_tree.refresh();
                }
        	}



    	} else if(selected.getValue().isDirectory()){
    		TextInputDialog dialog = new TextInputDialog("");
        	dialog.setTitle("Rename");
        	dialog.setHeaderText("Rename Folder");
        	dialog.setContentText("Please enter the folder name:");
        	Optional<String> result = dialog.showAndWait();

         	if (result.isPresent()){
        		final String inputFolderName  = result.get();
        		String query = "";
        		if(selected.getParent().getValue().isProject()){
        			query = "SELECT pdname FROM PDirs where pid like '" + currPID + "' AND parentid IS NULL";
        		}else {
        			query = "SELECT pdname FROM PDirs where pid like '" + currPID + "' AND parentid like '" + selected.getParent().getValue().getID() + "'";
        		}
        		Statement st = conn.createStatement();
        		ResultSet rs = st.executeQuery(query);

                while(rs.next()){
                	String tempName = rs.getString("pdname");
                	if(Objects.equals(tempName,inputFolderName)){
                		isExist = true;
                		break;
                	}
                }


                if(isExist == true){
         			warning("Directory name already exist.");
         		} else {
                	query = "UPDATE PDirs SET pdname='" + inputFolderName + "' WHERE pdid='" + selected.getValue().getID() + "'";

         			st = conn.createStatement();
         			st.executeUpdate(query);
         			selected.getValue().setName(inputFolderName);
                	structure_tree.refresh();
	         	}
         	}


    	} else if(selected.getValue().isFile){
    		TextInputDialog dialog = new TextInputDialog("");
        	dialog.setTitle("Rename");
        	dialog.setHeaderText("Rename File");
        	dialog.setContentText("Please enter the file name:");
        	Optional<String> result = dialog.showAndWait();

        	if (result.isPresent()){
        		final String inputFileName  = result.get();
        		String query = "";
        		if(selected.getParent().getValue().isProject()){
            		query = "SELECT pfname FROM PFiles where pid like '" + currPID + "' AND pdid IS NULL";
        		}else {
        			query = "SELECT pfname FROM PFiles where pid like '" + currPID + "' AND pdid LIKE '" + selected.getParent().getValue().getID() + "'";
        		}

        		Statement st = conn.createStatement();
        		ResultSet rs = st.executeQuery(query);

                    while(rs.next()){
                    	String tempName = rs.getString("pfname");
                    	if(Objects.equals(tempName,inputFileName)){
                    		isExist = true;
                    		break;
                    	}
                    }
                    if(isExist == true){
            			warning("File name already exist.");

            		} else {
                    	query = "UPDATE PFiles SET pfname='" + inputFileName + "' WHERE pfid='" + selected.getValue().getID() + "'";

            			st = conn.createStatement();
            			st.executeUpdate(query);
             			selected.getValue().setName(inputFileName);
                    	structure_tree.refresh();
            		}
        	}
    	}
    }


/***************************Initialize Tree***************************/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	conn = Main.GetConnection();
    	try {
			setUser(MyuserID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	IniTree();
    	curr_position.setText("sQuire Project");
    }


    public void IniTree() {
    	CheckBoxTreeItem<StrucTree> treeRoot = new CheckBoxTreeItem<>();
    	treeRoot.setValue(new StrucTree("sQuire Project"));
    	treeRoot.setExpanded(true);

    	try {
			String query = "SELECT * FROM ProjectAccess WHERE userID like '" + MyuserID + "'";
	        Statement st = conn.createStatement();
	        ResultSet rs = st.executeQuery(query);
	        while (rs.next()){
	        	int PID = rs.getInt("PID");
//	        	System.out.println(PID);
	        	query = "SELECT * FROM Projects WHERE PID like '" + PID + "'";
	        	Statement st1 = conn.createStatement();
	        	ResultSet rs1 = st1.executeQuery(query);

		        rs1.next();
		        String ProjectName = rs1.getString("pname");
		        int ProjectID = rs1.getInt("PID");
		    	CheckBoxTreeItem<StrucTree> treeItem = new CheckBoxTreeItem<>();
		    	treeItem.setValue(new StrucTree("p",ProjectName, ProjectID));

		        treeRoot.getChildren().add(treeItem);


	        }
	        structure_tree.setRoot(treeRoot);
	        } catch (SQLException e) {
			e.printStackTrace();
		}
    	currPID = 0;
    	selected = null;
    	currProjectName = "";
    }

/***************************Display Current Position***************************/

    private String getCurrPosition(TreeItem<StrucTree> item){
        StringBuilder pos = new StringBuilder(currProjectName);
        if(!selected.getValue().isProject()){
        	printpath(item, pos);
        }

        return pos.toString();
    }

    private StringBuilder printpath(TreeItem<StrucTree> item, StringBuilder temp){
        if(item.getParent()!= null && !item.getParent().getValue().isProject()){
            printpath(item.getParent(), temp);
        }
        if(!Objects.equals(currProjectName, "")){ temp.append("/");}
        temp.append(item.getValue().toString());
        return temp;
    }
/***************************Selection Tasks***************************/

    @FXML private void file_select(MouseEvent mouse) throws SQLException{
        TreeItem<StrucTree> item = structure_tree.getSelectionModel().getSelectedItem();

    	if(mouse.getClickCount() == 1 && item != null){
    		selected = item;
    		if(Objects.equals(currProjectName, "")){
    			curr_position.setText(item.getValue().toString());
    		}else {
    			curr_position.setText(getCurrPosition(item));
    		}
    	}
        if(mouse.getClickCount() == 2 && item != null){
            selected = item;
            curr_position.setText(item.getValue().toString());

            if(currPID == 0 && !Objects.equals(item.getValue().toString(),"sQuire Project")){
                currProjectName = item.getValue().toString();

                currPID = item.getValue().getID();
            	setTree(null, 0);
            }else{
            	curr_position.setText(getCurrPosition(item));
            }
        }
    }
/***************************Set Tree***************************/

    private void setTree (CheckBoxTreeItem<StrucTree> treeItem, int id) throws SQLException{
    	CheckBoxTreeItem<StrucTree> treeRoot = new CheckBoxTreeItem<>();
    	if(treeItem == null){
    		treeRoot.setValue(new StrucTree("p",currProjectName,currPID));
    		String query = "SELECT * FROM PDirs WHERE pid LIKE '" + currPID +"' AND parentid IS NULL";
    	    Statement st = conn.createStatement();
    	    ResultSet rs = st.executeQuery(query);
    	    while(rs.next()){
    	    	CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
    	    	temptreeItem.setValue(new StrucTree("d", rs.getString("pdname"), rs.getInt("pdid"),rs.getInt("parentid")));
    	    	treeRoot.getChildren().add(temptreeItem);
    	    	setTree(temptreeItem, rs.getInt("pdid"));
    	    }
    		query = "SELECT * FROM PFiles WHERE pid LIKE '" + currPID +"' AND pdid IS NULL";
    	    rs = st.executeQuery(query);
    	    while(rs.next()){
    	    	CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
    	    	temptreeItem.setValue(new StrucTree("f", rs.getString("pfname"), rs.getInt("pfid"),rs.getInt("pdid")));
    	    	treeRoot.getChildren().add(temptreeItem);
    	    }
    	    structure_tree.setRoot(treeRoot);
    		treeRoot.setExpanded(true);
    		selected = treeRoot;
    	} else {
    		String query = "SELECT * FROM PDirs WHERE pid LIKE '" + currPID +"' AND parentid like '" + id + "'";
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(query);
    	    while(rs.next()){
    	    	CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
    	    	temptreeItem.setValue(new StrucTree("d", rs.getString("pdname"), rs.getInt("pdid"),rs.getInt("parentid")));
    	    	treeItem.getChildren().add(temptreeItem);
    	    	setTree(temptreeItem,rs.getInt("pdid") );
    	    }

    	    query = "SELECT * FROM PFiles WHERE pid LIKE '" + currPID +"' AND pdid LIKE '" + id + "'";
    	    rs = st.executeQuery(query);
    	    while(rs.next()){
    	    	CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
    	    	temptreeItem.setValue(new StrucTree("f", rs.getString("pfname"), rs.getInt("pfid"),rs.getInt("pdid")));
    	    	treeItem.getChildren().add(temptreeItem);
    	    }
    	}
    }

/***************************Home Button Function***************************/

    @FXML private void HomeButton(){
    	IniTree();
    	curr_position.setText("sQuire Project");
    }

/***************************Delete Function***************************/

    @FXML private void DeleteButton() throws SQLException{

/*
    	else if(selected.getParent()==null || Objects.equals(selected.getParent().getValue().toString(),"sQuire Project")){
    		String query = "SELECT PID FROM Projects where pname like '" + selected.getValue().toString() + "'";
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
    	    if(deletePID != 0){
    	    	String deleteProj = "DELETE FROM ProjectAccess WHERE PID LIKE '" + deletePID + "' AND userID like '" + MyuserID + "' LIMIT 1";
    	    	Statement st2 = conn.createStatement();
    	    	st2.executeUpdate(deleteProj);

    	    	Statement st3 = conn.createStatement();
    	    	String deleteProj2 = "DELETE FROM Projects WHERE PID LIKE '" + deletePID + "'";
    	    	st3.executeUpdate(deleteProj2);
    	    }
        	IniTree();
        	curr_position.setText("sQuire Project");
        	selected = null;
        	currPID = 0;
/*        	query = "SELECT PID FROM ProjectAccess where userID like '" + MyuserID + "'";

    		String deleteProj = "DELETE FROM Projects WHERE pname LIKE '" + selected.getValue() + "'";
    		Statement s = conn.createStatement();
            s.executeUpdate(deleteProj);
        	IniTree();
        	curr_position.setText("sQuire Project");
        	selected = null;

    	}
*/
    	if(selected == null){
            warning("No project or file selected.");
    	} else if(selected.getValue().isProject()){
    		int pid = selected.getValue().getID();
    		Statement st = conn.createStatement();
    		String query = "DELETE FROM ProjectAccess WHERE PID LIKE '" + pid + "'";
    		st.executeUpdate(query);
    		query = "DELETE FROM PFiles WHERE pid LIKE '" + pid + "'";
    		st.executeUpdate(query);

    		query = "SELECT pdid FROM PDirs WHERE pid like '" + pid + "' AND parentid is NULL";
    		ResultSet rs = st.executeQuery(query);
    		while(rs.next()){
    			deleteDirectory(rs.getInt("pdid"));
    		}
    		query = "DELETE FROM Projects WHERE pid LIKE '" + pid + "'";
    		st.executeUpdate(query);
    		IniTree();

    	} else if(selected.getValue().isDirectory()){
    		System.out.println(selected.getValue().getID());
    		deleteDirectory(selected.getValue().getID());
    		System.out.println(currPID);
       		selected.getParent().getChildren().remove(selected);
    	} else if(selected.getValue().isFile()){
    		int pfid = selected.getValue().getID();
    		String deleteFile = "DELETE FROM PFiles WHERE pfid LIKE '" + pfid + "'";
    		Statement st = conn.createStatement();
    		st.executeUpdate(deleteFile);
    		selected.getParent().getChildren().remove(selected);
    	}
    }

/***************************Delete Directory Routine***************************/
    private void deleteDirectory(int id) throws SQLException{

    	String query = "DELETE FROM PFiles WHERE pdid LIKE '" + id + "'";
	    Statement st = conn.createStatement();
	    st.executeUpdate(query);

    	query = "SELECT pdid FROM PDirs where parentid like '" + id + "'";
    	ResultSet rs = st.executeQuery(query);
	    while(rs.next()){
	    	int pdid = rs.getInt("pdid");
	    	deleteDirectory(pdid);
	    }

    	query = "DELETE FROM PDirs WHERE pdid LIKE '" + id + "'";
    	st.executeUpdate(query);
    }

/***************************Warning Pop Up Window***************************/

    private void warning(String text){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Warning");
        alert.setContentText(text);
        alert.showAndWait();
    }


/***************************Store Tree Value***************************/
/*
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

*/

/***************************Directory Creation***************************/

    @FXML private void CreateFolder() throws SQLException{
    	boolean isExist = false;

    	if(Objects.equals(currProjectName,"") || selected == null){
            warning("Can't create folder under the main project");
    	} else if(selected.getValue().isProject()){
    		TextInputDialog dialog = new TextInputDialog("");
        	dialog.setTitle("Create Folder");
        	dialog.setHeaderText("Folder");
        	dialog.setContentText("Please enter the folder name:");
        	Optional<String> result = dialog.showAndWait();

        	if (result.isPresent()){
        		final String inputFolderName  = result.get();

        		String query = "SELECT pdname FROM PDirs where pid like '" + currPID + "' AND parentid IS NULL";
        		Statement st = conn.createStatement();
        		ResultSet rs = st.executeQuery(query);

                    while(rs.next()){
                    	String tempName = rs.getString("pdname");
                    	if(Objects.equals(tempName,inputFolderName)){
                    		isExist = true;
                    		break;
                    	}
                    }
        		if(isExist == true){
        			warning("Directory name already exist.");

        		} else {
        			query = "INSERT INTO PDirs(pdname, pid) VALUE('" + inputFolderName + "','" + currPID + "')";
        			st = conn.createStatement();
        			st.executeUpdate(query);
        			query = "SELECT LAST_INSERT_ID()";
        			rs = st.executeQuery(query);
        			if(rs.next()){
        				int currID = rs.getInt("LAST_INSERT_ID()");
        				CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
        				temptreeItem.setValue(new StrucTree("d", inputFolderName, currID));
        				selected.getChildren().add(temptreeItem);
        				selected.setExpanded(true);
        			}
        		}
        	}
    } else if(selected.getValue().isFile()){
		TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create Folder");
    	dialog.setHeaderText("Folder");
    	dialog.setContentText("Please enter the folder name:");
    	Optional<String> result = dialog.showAndWait();

    	if (result.isPresent()){
    		final String inputFolderName  = result.get();
    		String query = "";
    		if(selected.getParent().getValue().isProject()){
    			query = "SELECT pdname FROM PDirs where pid like '" + currPID + "' AND parentid IS NULL";
    		}else {
    			query = "SELECT pdname FROM PDirs where pid like '" + currPID + "' AND parentid like '" + selected.getParent().getValue().getID() + "'";
    		}
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(query);

                while(rs.next()){
                	String tempName = rs.getString("pdname");
                	if(Objects.equals(tempName,inputFolderName)){
                		isExist = true;
                		break;
                	}
                }
                if(isExist == true){
        			warning("Directory name already exist.");
        		} else {
        	   		if(selected.getParent().getValue().isProject()){
            			query = "INSERT INTO PDirs(pdname, pid) VALUE('" + inputFolderName + "','" + currPID + "')";
            		}else {
            			query = "INSERT INTO PDirs(pdname, pid,parentid) VALUE('" + inputFolderName + "','" + currPID + "','" + selected.getParent().getValue().getID() + "')";
            		}
        			st = conn.createStatement();
        			st.executeUpdate(query);
        			query = "SELECT LAST_INSERT_ID()";
        			rs = st.executeQuery(query);
        			if(rs.next()){
        				int currID = rs.getInt("LAST_INSERT_ID()");
        				CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
            	   		if(selected.getParent().getValue().isProject()){
            	   			temptreeItem.setValue(new StrucTree("d", inputFolderName, currID));
            	   		}else {
            	   			temptreeItem.setValue(new StrucTree("d", inputFolderName, currID, selected.getParent().getValue().getID()));
            	   		}
        				selected.getParent().getChildren().add(temptreeItem);
        				selected.getParent().setExpanded(true);
        			}
        		}
    	}
    } else if(selected.getValue().isDirectory()){
		TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create Folder");
    	dialog.setHeaderText("Folder");
    	dialog.setContentText("Please enter the folder name:");
    	Optional<String> result = dialog.showAndWait();

    	if (result.isPresent()){
    		final String inputFolderName  = result.get();
    		String query = "SELECT pdname FROM PDirs where pid like '" + currPID + "' AND parentid like '" + selected.getValue().getID() + "'";
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(query);

                while(rs.next()){
                	String tempName = rs.getString("pdname");
                	if(Objects.equals(tempName,inputFolderName)){
                		isExist = true;
                		break;
                	}
                }
    		if(isExist == true){
    			warning("Directory name already exist.");
    		} else {
    			query = "INSERT INTO PDirs(pdname, pid, parentid) VALUE('" + inputFolderName + "','" + currPID + "','" + selected.getValue().getID() + "')";
    			st = conn.createStatement();
    			st.executeUpdate(query);
    			query = "SELECT LAST_INSERT_ID()";
    			rs = st.executeQuery(query);
    			if(rs.next()){
    				int currID = rs.getInt("LAST_INSERT_ID()");
    				CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
    				temptreeItem.setValue(new StrucTree("d", inputFolderName, currID, selected.getValue().getID()));
    				selected.getChildren().add(temptreeItem);
    				selected.setExpanded(true);
    			}
    		}
    	}

    }


    }
/***************************File Creation***************************/
    @FXML private void CreateFile() throws SQLException{
    	boolean isExist = false;

    	if(Objects.equals(currProjectName,"") || selected == null){
            warning("Can't create file under the main project");
    	} else if(selected.getValue().isProject()){
    		TextInputDialog dialog = new TextInputDialog("");
        	dialog.setTitle("Create File");
        	dialog.setHeaderText("File");
        	dialog.setContentText("Please enter the file name:");
        	Optional<String> result = dialog.showAndWait();

        	if (result.isPresent()){
        		final String inputFileName  = result.get();

        		String query = "SELECT pfname FROM PFiles WHERE pid like '" + currPID + "' AND pdid IS NULL";
        		Statement st = conn.createStatement();
        		ResultSet rs = st.executeQuery(query);

        		while(rs.next()){
        			String tempName = rs.getString("pfname");
        			if(Objects.equals(tempName,inputFileName)){
        				isExist = true;
        				break;
        			}
        		}
        		if(isExist == true){
        			warning("File name already exist.");
        		} else {
        			query = "INSERT INTO PFiles(pfname, pid) VALUE('" + inputFileName + "','" + currPID + "')";
        			st = conn.createStatement();
        			st.executeUpdate(query);
        			query = "SELECT LAST_INSERT_ID()";
        			rs = st.executeQuery(query);
        			if(rs.next()){
        				int currID = rs.getInt("LAST_INSERT_ID()");
        				CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
        				temptreeItem.setValue(new StrucTree("f", inputFileName, currID));
        				selected.getChildren().add(temptreeItem);
        				selected.setExpanded(true);
        			}
        		}
        	}
    } else if(selected.getValue().isFile()){

		TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create File");
    	dialog.setHeaderText("File");
    	dialog.setContentText("Please enter the file name:");
    	Optional<String> result = dialog.showAndWait();

    	if (result.isPresent()){
    		final String inputFileName  = result.get();
    		String query = "";
    		if(selected.getParent().getValue().isProject()){
        		query = "SELECT pfname FROM PFiles where pid like '" + currPID + "' AND pdid IS NULL";
    		}else {
    			query = "SELECT pfname FROM PFiles where pid like '" + currPID + "' AND pdid LIKE '" + selected.getParent().getValue().getID() + "'";
    		}
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(query);

                while(rs.next()){
                	String tempName = rs.getString("pfname");
                	if(Objects.equals(tempName,inputFileName)){
                		isExist = true;
                		break;
                	}
                }
                if(isExist == true){
        			warning("File name already exist.");

        		} else {
        	   		if(selected.getParent().getValue().isProject()){
            			query = "INSERT INTO PFiles(pfname, pid) VALUE('" + inputFileName + "','" + currPID + "')";
            		}else {
            			query = "INSERT INTO PFiles(pfname, pid, pdid) VALUE('" + inputFileName + "','" + currPID + "','" + selected.getParent().getValue().getID() + "')";
            		}
        			st = conn.createStatement();
        			st.executeUpdate(query);
        			query = "SELECT LAST_INSERT_ID()";
        			rs = st.executeQuery(query);
        			if(rs.next()){
        				int currID = rs.getInt("LAST_INSERT_ID()");
        				CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
            	   		if(selected.getParent().getValue().isProject()){
            	   			temptreeItem.setValue(new StrucTree("f", inputFileName, currID));
            	   		}else {
            	   			temptreeItem.setValue(new StrucTree("f", inputFileName, currID, selected.getParent().getValue().getID()));
            	   		}
            	   		selected.getParent().getChildren().add(temptreeItem);
        				selected.getParent().setExpanded(true);
        			}
        		}
    		}
	    } else if(selected.getValue().isDirectory()){
			TextInputDialog dialog = new TextInputDialog("");
	    	dialog.setTitle("Create File");
	    	dialog.setHeaderText("File");
	    	dialog.setContentText("Please enter the file name:");
	    	Optional<String> result = dialog.showAndWait();

	    	if (result.isPresent()){
	    		final String inputFileName  = result.get();
	    		String query = "SELECT pfname FROM PFiles where pid like '" + currPID + "' AND pdid LIKE '" + selected.getValue().getID() + "'";

	    		Statement st = conn.createStatement();
	    		ResultSet rs = st.executeQuery(query);

	                while(rs.next()){
	                	String tempName = rs.getString("pfname");
	                	if(Objects.equals(tempName,inputFileName)){
	                		isExist = true;
	                		break;
	                	}
	                }
	    		if(isExist == true){
	    			warning("File name already exist.");
	    		} else {
	    			query = "INSERT INTO PFiles(pfname, pid, pdid) VALUE('" + inputFileName + "','" + currPID + "','" + selected.getValue().getID() + "')";
	    			st = conn.createStatement();
	    			st.executeUpdate(query);
	    			query = "SELECT LAST_INSERT_ID()";
	    			rs = st.executeQuery(query);
	    			if(rs.next()){
	    				int currID = rs.getInt("LAST_INSERT_ID()");
	    				CheckBoxTreeItem<StrucTree> temptreeItem = new CheckBoxTreeItem<>();
	    				temptreeItem.setValue(new StrucTree("f", inputFileName, currID, selected.getValue().getID()));
	    				selected.getChildren().add(temptreeItem);
	    				selected.setExpanded(true);
	    			}
	    		}
	    	}
	    }
    }
/******************************************************************************/
//	private void setFile(){}

/******************************************************************************/

}

