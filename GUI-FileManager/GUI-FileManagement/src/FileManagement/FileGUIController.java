package FileManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class FileGUIController implements Initializable {
	public static Connection conn = null;
	int MyuserID = 1;
    int currPID = 0;
    int tempFileId = 0;

	String currProjectName = "";
    String tempFileData = "";

    TreeItem<StrucTree> selected = null;
    TreeItem<StrucTree> selectedFile = null;

	@FXML Text user;
    @FXML TextField curr_position;
    @FXML TreeView<StrucTree> structure_tree;
    @FXML TextArea fileData;
    @FXML AnchorPane root;
    @FXML Text info;



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
            	String pass = setPassword();
            	if( !Objects.equals(pass, "")){


            	String sql = "INSERT INTO Projects(pname,passHash,projectOwnerID)VALUE('" + currProjName + "','" + pass + "','" + MyuserID+"')";
            	st.executeUpdate(sql);
            	sql = "SELECT LAST_INSERT_ID()";
            	rs = st.executeQuery(sql);
            	if(rs.next()){
            		int currID = rs.getInt("LAST_INSERT_ID()");
                	sql = "INSERT INTO ProjectAccess(PID, userID)VALUE('" + currID + "','" + MyuserID + "')";
                	st.executeUpdate(sql);
            	}
            }else{warning("Create Project Failed");}
            }
    	}
    	IniTree();
    }

/***************************File Input***************************/

    @FXML public void locateFile() throws IOException{
    	FileChooser chooser = new FileChooser();
    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");

    	chooser.getExtensionFilters().add(extFilter);
    	extFilter = new FileChooser.ExtensionFilter("JAVA files (*.jar,*.java)", "*.jar", "*.java");
    	chooser.getExtensionFilters().add(extFilter);

    	chooser.setTitle("Open File");
    	File file = chooser.showOpenDialog(root.getScene().getWindow());
    	if(file != null){
    	String inputFileData = readFile(file);
//    		System.out.println(inputFileData);
    		fileData.setText(inputFileData);
    	}
    }
/**************************Save Input File Data**************************
 *   private void saveFile(String content, File file) throws IOException{
 *           FileWriter fileWriter = null;
 *           fileWriter = new FileWriter(file);
 *           fileWriter.write(content);
 *           fileWriter.close();
 *   }
 *
 **************************Save Input File Data**************************/


/**************************Read Input File Data**************************/
    private String readFile(File file) throws IOException{
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    	StringBuilder stringBuffer = new StringBuilder();

    	String text;
    	while ((text = bufferedReader.readLine()) != null) {
    		stringBuffer.append(text);
    	}
    	bufferedReader.close();
    	return stringBuffer.toString();
    }

/***************************TreeItem Class***************************/
    class StrucTree {
    	  String name = "";
    	  int id = 0;
    	  int parentid = 0;
    	  int pflhead = 0;
    	  String passWord = "";

    	  String getPass(){return this.passWord;}
    	  void setPass(String pass){this.passWord = pass;}
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
    	  public void setLine(int pflhead){this.pflhead = pflhead;}
    	  public int getLine(){return pflhead;}
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
        Node graphic  = new ImageView(new Image("https://duke.kenai.com/iconSized/duke4.gif"));


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
    	selectedFile = null;
    	currProjectName = "";
    	tempFileId = 0;
    	tempFileData = "";
    	fileData.setText("");
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


    		if(item.getValue().isProject()){
    			info.setText(" [Project: " + item.getValue().toString() + "]  [ID: " + item.getValue().getID() + "]");
    		} else if(item.getValue().isDirectory()){
    			info.setText(" [Directory: " + item.getValue().toString() + "]   [ID: " + item.getValue().getID() + "]  [Parent ID: " + item.getValue().getPID() + "]");
	    	} else if(item.getValue().isFile()){
	    		String query = "SELECT * FROM PFiles WHERE pfid like '" + item.getValue().getID() + "'";
	        	Statement st = conn.createStatement();
	        	ResultSet rs = st.executeQuery(query);
	        	if(rs.next()){
	        		if(rs.getInt("creatorID") == 0){
	        			info.setText(" [File: " + item.getValue().toString() + "]  [ID: " + item.getValue().getID() + "]  [Created Time: "+ rs.getTimestamp("timeCreated")+ "]");
	        		}else{
	        			query = "SELECT userName from Users WHERE userID like '" + rs.getInt("creatorID")+ "'";
	        			Statement st1 = conn.createStatement();
	    	        	ResultSet rs1 = st1.executeQuery(query);
	    	        	if(rs1.next()){info.setText(" [File: " + item.getValue().toString() + "]  [ID: " + item.getValue().getID() + "]  [Creator: " + rs1.getString("userName") + "]  [Created Time: "+ rs.getTimestamp("timeCreated")+ "]");}
	        		}
	        	}
			} else {
				info.setText(" *sQuire Project Menu*");
			}

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

            if(item.getValue().isFile()){
            	selectedFile = item;
            	readFile(selectedFile);
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
    	    	getIniLine(temptreeItem);
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
    	    	getIniLine(temptreeItem);
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


    		String query1 = "SELECT * FROM Projects WHERE PID LIKE '" + pid + "'";
    		Statement st1 = conn.createStatement();
    		ResultSet rs1 = st1.executeQuery(query1);
    		if(rs1.next()){
    			if(rs1.getInt("projectOwnerID") == MyuserID){



    	    		Statement st = conn.createStatement();
    	    		String query = "DELETE FROM ProjectAccess WHERE PID LIKE '" + pid + "'";
    	    		st.executeUpdate(query);

    	        	query = "SELECT * FROM PFiles WHERE pid LIKE '" + pid + "' AND pdid IS NULL";
    	    	    st = conn.createStatement();
    	    	    ResultSet rs = st.executeQuery(query);
    	    	    while(rs.next()){
    	    	    	query = "DELETE FROM PFiles WHERE pfid LIKE '" + rs.getInt("pfid") + "'";
    	    		    Statement st2 = conn.createStatement();
    	    		    st2.executeUpdate(query);
    	    	    	deleteFileLine(rs.getInt("pflhead"));
    	    	    }

    	    		query = "SELECT pdid FROM PDirs WHERE pid like '" + pid + "' AND parentid is NULL";
    	    		rs = st.executeQuery(query);
    	    		while(rs.next()){
    	    			deleteDirectory(rs.getInt("pdid"));
    	    		}
    	    		query = "DELETE FROM Projects WHERE pid LIKE '" + pid + "'";
    	    		st.executeUpdate(query);
    	    		IniTree();



    			}else{
    				warning("You are not the owner of this project.");
    			}
    		}








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
    		deleteFileLine(selected.getValue().getLine());
    		selected.getParent().getChildren().remove(selected);
    	}
    }

/***************************Delete File Line Routine***************************/
    private void deleteFileLine(int id) throws SQLException{
    	Statement st = conn.createStatement();
    	String query = "SELECT * FROM PFLines WHERE pflid LIKE '" + id + "'";
    	ResultSet rs = st.executeQuery(query);
    	if(rs.next()){
    		int nextid = rs.getInt("nextid");
    		query = "DELETE FROM PFLines WHERE pflid LIKE '" + id + "'";
    		st.executeUpdate(query);
    		deleteFileLine(nextid);
    	}
    }

/***************************Delete Directory Routine***************************/
    private void deleteDirectory(int id) throws SQLException{
    	String query = "SELECT * FROM PFiles WHERE pdid LIKE '" + id + "'";
	    Statement st = conn.createStatement();
	    ResultSet rs = st.executeQuery(query);
	    while(rs.next()){
	    	query = "DELETE FROM PFiles WHERE pfid LIKE '" + rs.getInt("pfid") + "'";
		    Statement st1 = conn.createStatement();
		    st1.executeUpdate(query);
	    	deleteFileLine(rs.getInt("pflhead"));
	    	System.out.println(rs.getInt("pflhead"));
	    }

    	query = "SELECT pdid FROM PDirs where parentid like '" + id + "'";
    	rs = st.executeQuery(query);
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
        			query = "INSERT INTO PFiles(pfname, pid,creatorID) VALUE('" + inputFileName + "','" + currPID + "','" + MyuserID +  "')";
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
        				iniFile(temptreeItem);
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
            			query = "INSERT INTO PFiles(pfname, pid,creatorID) VALUE('" + inputFileName + "','" + currPID + "','"+ MyuserID + "')";
            		}else {
            			query = "INSERT INTO PFiles(pfname, pid, pdid,creatorID) VALUE('" + inputFileName + "','" + currPID + "','" + selected.getParent().getValue().getID()+ "','" + MyuserID + "')";
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
            	   		iniFile(temptreeItem);
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
	    			query = "INSERT INTO PFiles(pfname, pid, pdid,creatorID) VALUE('" + inputFileName + "','" + currPID + "','" + selected.getValue().getID() + "','" + MyuserID + "')";
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
	    				iniFile(temptreeItem);
	    			}
	    		}
	    	}
	    }
    }

/***************************Initialize File Line***************************/

    private void iniFile(TreeItem<StrucTree> item) throws SQLException{
		if(item.getValue().isFile()){
			Statement st = conn.createStatement();
			String query = "INSERT INTO PFLines(text) VALUE('')";
			st.executeUpdate(query);
			query = "SELECT LAST_INSERT_ID()";
			ResultSet rs = st.executeQuery(query);
			if(rs.next()){
				int currID = rs.getInt("LAST_INSERT_ID()");
				query = "UPDATE PFiles SET pflhead='" + currID + "' WHERE pfid='" + item.getValue().getID() + "'";
				st.executeUpdate(query);
				item.getValue().setLine(currID);
			}
		}
	}

/***************************Read File Data***************************/
    private void readFile(TreeItem<StrucTree> item) throws SQLException{
        StringBuilder pos = new StringBuilder("");

    	if(item.getValue().isFile() && (Objects.equals(tempFileData,"") || tempFileId != item.getValue().getID())){
//    		System.out.println(getLine(item.getValue().getLine(),pos).toString());
    		tempFileData = getLine(item.getValue().getLine(),pos).toString();
    		tempFileId = item.getValue().getID();
    		fileData.setText(tempFileData);
    	}
    }

    private StringBuilder getLine(int id, StringBuilder temp) throws SQLException{
    	Statement st = conn.createStatement();
    	String query = "SELECT * FROM PFLines WHERE pflid like '" + id + "'";
    	ResultSet rs = st.executeQuery(query);
    	if(rs.next()){
            temp.append(rs.getString("text"));
            temp.append("\n");
    		getLine(rs.getInt("nextid"),temp);
    	}
    	return temp;
    }

/***************************Get Initial File Line***************************/
    private void getIniLine(TreeItem<StrucTree> item) throws SQLException{
    	if(item.getValue().isFile()){
    		Statement st = conn.createStatement();
    		String query = "SELECT pflhead FROM PFiles WHERE pfid LIKE '" + item.getValue().getID() + "'";
    		ResultSet rs = st.executeQuery(query);
    		if(rs.next()){
    			item.getValue().setLine(rs.getInt("pflhead"));
    		}
    	}
    }
/***************************Project Access***************************/
    @FXML public void projectAccess() throws SQLException{
    	Statement st = conn.createStatement();
    	String query = "SELECT * FROM  Projects";
    	ResultSet rs = st.executeQuery(query);


    	List<String> choices = new ArrayList<>();
    	System.out.println("0");



    	while(rs.next()){
    		if(rs.getInt("projectOwnerID") != 0){

    			Statement st1 = conn.createStatement();
    			String query1 = "SELECT * FROM Users WHERE userID LIKE '" + rs.getInt("projectOwnerID") + "'";
    			ResultSet rs1 = st1.executeQuery(query1);
    			System.out.println("3");
    			if(rs1.next()){choices.add(rs.getString("pname") + "--"+ rs1.getString("userName"));}
    		}
    	}



    		/*    		Statement st1 = conn.createStatement();
    		String query1 = "SELECT * FROM ProjectAccess WHERE pid LIKE '" + rs.getInt("PID") + "' AND userID NOT LIKE '" + MyuserID + "'" ;
    		ResultSet rs1 = st1.executeQuery(query1);
 System.out.println("1");
    		if(rs1.next()){
    			Statement st4 = conn.createStatement();
    			String query4 = "SELECT * FROM Projects WHERE pname LIKE '" + rs.getString("pname") +"' AND PID LIKE '" +rs1.getInt("PID")+ "'";
    			ResultSet rs4 = st4.executeQuery(query4);
    			System.out.println("2");
    			if(rs4.next()){

    			Statement st2 = conn.createStatement();
    			String query2 = "SELECT * FROM Users WHERE userID LIKE '" + rs4.getInt("projectOwnerID") + "'";
    			ResultSet rs2 = st2.executeQuery(query2);
    			System.out.println("3");
    			if(rs2.next()){choices.add(rs.getString("pname") + "--"+ rs2.getString("userName"));}
    			}
    		}
*/



    	ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
    	dialog.setTitle("Project Access");
    	dialog.setHeaderText("Choose the project you want to access.");
    	dialog.setContentText("Project:");

    	Optional<String> result = dialog.showAndWait();

    	if (result.isPresent()){
    		String tempPass = setPassword();
    		String[] temp= result.get().split("--");
        	Statement st3 = conn.createStatement();
        	String query3 = "SELECT * FROM Users WHERE userName LIKE '" + temp[1]+ "'";
        	ResultSet rs3 = st3.executeQuery(query3);
           	if(rs3.next()){
           		Statement st4 = conn.createStatement();
            	String query4 = "SELECT * FROM Projects WHERE pname LIKE '" + temp[0]+ "' AND projectOwnerID LIKE '"+  rs3.getInt("userID") + "'";
            	ResultSet rs4 = st4.executeQuery(query4);


            	if(rs4.next() || !Objects.equals(tempPass,"")){
            		if(Objects.equals(tempPass,rs4.getString("passHash"))){
            			Statement st6 = conn.createStatement();
            			String query6 = "SELECT * FROM ProjectAccess WHERE PID LIKE '" + rs4.getInt("PID") + "' AND userID LIKE '" + MyuserID + "'";
            			ResultSet rs6 = st6.executeQuery(query6);
            			if(rs6.next() && rs6.wasNull()){
            			warning("Successful!");
            			Statement st5 = conn.createStatement();
                    	String query5 = "INSERT INTO ProjectAccess(PID,userID)VALUE('" + rs4.getInt("PID") + "','" + MyuserID+"')";
                    	st5.executeUpdate(query5);
                    	IniTree();} else{warning("Project Access Already Exist.");}
            		} else {
            			warning("Wrong Password!");
            		}
            	}

        	}

    	}

    }







/***************************Switch User***************************/
    @FXML public void switchUser() throws SQLException{
		String query = "SELECT * FROM Users WHERE userID NOT LIKE '" + MyuserID + "'" ;
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);

		List<String> choices = new ArrayList<>();

		while(rs.next()){
			choices.add(rs.getString("userName"));
		}

	 	ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
    	dialog.setTitle("Switch User");
    	dialog.setHeaderText("Choose the user you want to be.");
    	dialog.setContentText("User:");

    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    		query = "SELECT * FROM Users WHERE userName LIKE '" + result.get()  + "'";
    		st = conn.createStatement();
    		rs = st.executeQuery(query);
    		if(rs.next()){
    			MyuserID = rs.getInt("userID");
    			user.setText(result.get());
    		}
    		IniTree();
    	}
    }

/***********/
    private String setPassword(){
    	TextInputDialog dialog = new TextInputDialog("Password");
    	dialog.setTitle("Password");
    	dialog.setHeaderText("Password Setting");

    	dialog.setContentText("Please enter the password:");

    	// Traditional way to get the response value.
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    		return result.get();
    	} else {
    		return "";
    	}

    }

    @FXML public void showPassword() throws SQLException{
     	if(selected == null){
            warning("No project selected.");
    	} else if(selected.getValue().isProject()){
    		System.out.println(selected.getValue().getID());
    		String query = "SELECT * FROM Projects WHERE PID LIKE '" + selected.getValue().getID() + "'";
    		System.out.println(query);
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(query);
    		if(rs.next()){
    			if(rs.getInt("projectOwnerID") == MyuserID){
    				warning("Password: " + rs.getString("passHash"));
    			}else{
    				warning("You are not the owner of this project.");
    			}
    		}
    	} else{
        	warning("No project selected.");
    	}
    }




}

