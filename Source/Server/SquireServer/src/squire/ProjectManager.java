package squire;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ProjectManager {
	private DBConnector dbc;
	private int userID;
	
	public ProjectManager(DBConnector dbc) {
		this.dbc = dbc;
//        dbc.closeConnection();
	}
	
	/**
	 * void setUserID (int userID)
	 * Set the userID when the user logs in
	 * @param userID
	 */
	public void setUserID(int userID)
	{
		this.userID = userID;
	}
	
	public JSONArray getProjectAccessEntries (String ProjectID) throws SQLException{
		String query =	"select "
						+ "`userID`"
					+ "from "
						+ "`ProjectAccess` "
					+ "where "
						+ "`ProjectAccess`.`PID` = ?";
		
		String[] values = new String[1];
		values[0] = ProjectID;
		
		JSONArray userList = new JSONArray();
		try {
			userList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return userList;
	}
	
	public JSONArray getProjects () throws SQLException{
		String query =	"select "
						+ "`Projects`.`PID`, "
						+ "`Projects`.`pname` "
					+ "from "
						+ "`Projects` NATURAL JOIN `ProjectAccess` "
					+ "where "
						+ "`ProjectAccess`.`userID` = ?";
		
		String[] values = new String[1];
		values[0] = String.valueOf(this.userID);
		
		System.out.println("\tQuery" + query + "\n");
		System.out.println("\tWith Values" + values[0] + "\n");
		System.out.flush();
		JSONArray projectList = new JSONArray();
		try {
			projectList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return projectList;
	}
	
	public JSONArray getDirectories (String projectID) throws SQLException{
		String query =	"select "
						+ "`pdid`, "
						+ "`pdname`, "
						+ "`parentid` "
					+ "from "
						+ "`PDirs` "
					+ "where "
						+ "`pid` = ?";
		
		String[] values = new String[1];
		values[0] = String.valueOf(projectID);
		
		JSONArray dirList = new JSONArray();
		try {
			dirList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dirList;
	}
	
	public JSONArray getFiles (String projectID) throws SQLException{
		String query =	"select "
						+ "`pfid`, "
						+ "`pfname`, "
						+ "`pdid`, "
						+ "`timeCreated`, "
						+ "`creatorID` "
					+ "from "
						+ "`PFiles` "
					+ "where "
						+ "`pid` = ?";
		
		String[] values = new String[1];
		values[0] = String.valueOf(projectID);
		
		JSONArray fileList = new JSONArray();
		try {
			fileList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileList;
	}
	
	public JSONArray getAllLines (String projectID) throws SQLException{
		
		JSONArray fileList = new JSONArray();
		JSONArray lineList = new JSONArray();
		String pfn;
		try {
			fileList = getFiles(projectID);
			for(int i = 0; i < fileList.size(); i++){
				JSONObject row = (JSONObject) ((JSONArray) fileList).get(i);
				String pfid = (String) row.get("pfid");
				lineList.addAll( getLines(pfid) );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lineList;
	}
	
	public JSONArray getLines (String PFileID) throws SQLException{
		String query =	"call PFLTraverser(( "
						+ "select "
							+ "`pflhead` "
						+ "from "
							+ "`PFiles` "
						+ "where "
							+ "`pfid` = ?"
						+ "));";
		
		String[] values = new String[1];
		values[0] = String.valueOf(PFileID);
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		JSONArray projectList = new JSONArray();
		try {
			projectList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONArray returnList = new JSONArray();
		for(int i = 0; i < projectList.size(); i++){
			JSONObject jobj = (JSONObject)projectList.get(i);
			jobj.put("requestTime", stamp.toString());
			returnList.add(jobj);
		}
		
		return returnList;
	}
	
	public JSONArray getLineChanges (String PFileID, String lastTime) throws SQLException{
		String query = 		"call PFLTimeTraverser"+
							"(( select `pflhead` from `PFiles` where `pfid` = ?), (SELECT FROM_UNIXTIME(?)))";
		
		String[] values = new String[2];
		values[0] = String.valueOf(PFileID);
		values[1] = lastTime;
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		JSONArray projectList = new JSONArray();
		try {
			projectList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONArray returnList = new JSONArray();
		for(int i = 0; i < projectList.size(); i++){
			JSONObject jobj = (JSONObject)projectList.get(i);
			jobj.put("requestTime", stamp.toString());
			returnList.add(jobj);
		}
		
		
		return returnList;
	}
	
	public JSONArray getLineLocks (String PFileID) throws SQLException{
		String query =	"call PFLLockTraverser(( "
							+ "select "
								+ "`pflhead` "
							+ "from "
								+ "`PFiles` "
							+ "where "
								+ "`pfid` = ? ))";
		
		String[] values = new String[1];
		values[0] = String.valueOf(PFileID);
		
		JSONArray lineList = new JSONArray();
		try {
			lineList = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lineList;
	}
	
	
	public JSONArray createProject (String projectName) throws SQLException{
		String query = "Insert into Projects(pname) values (?);";
		String[] values = new String[1];
		values[0] = projectName;
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		JSONArray projectID = new JSONArray();
		String pid;
		try {
			this.dbc.query(query, values);
			projectID = this.dbc.query(query2, values2);
			//Add access for creator.
			JSONObject firstRow = (JSONObject) ((JSONArray) projectID).get(0);
			pid = (String) firstRow.get("LAST_INSERT_ID()");
			createProjectAccess(String.valueOf(this.userID), pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projectID;
	}
	
	public void createProjectAccess (String newUserID, String projectID){
		String query = "Insert into ProjectAccess(userid, pid) values (?,?);";
		String[] values = new String[2];
		values[0] = newUserID;
		values[1] = projectID;
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONArray createDirectory (String projectID, String dirName, String parentDirID){
		String query = "Insert into PDirs(pdname, parentid, pid) values (?, ?, ?)";
		String[] values = new String[3];
		values[0] = dirName;
		values[1] = parentDirID;
		values[2] = projectID;
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		JSONArray dirID = new JSONArray();
		try {
			this.dbc.query(query, values);
			dirID = this.dbc.query(query2, values2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dirID;
	}
	
	public JSONArray createDirectory (String projectID, String dirName){
		String query = "Insert into PDirs(pdname, pid) values (?, ?)";
		String[] values = new String[2];
		values[0] = dirName;
		values[1] = projectID;
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		JSONArray dirID = new JSONArray();
		try {
			this.dbc.query(query, values);
			dirID = this.dbc.query(query2, values2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dirID;
	}
	
	public JSONArray createFile (String fileName, String projectID){
		JSONArray returnValue = createLine("//"+fileName);
		String pflhead = (String)((JSONObject)returnValue.get(0)).get("LAST_INSERT_ID()");
		String query = "Insert into PFiles(pfname, pid, pflhead) values (?, ?, ?)";
		String[] values = new String[3];
		values[0] = fileName;
		values[1] = projectID;
		values[2] = pflhead;
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		JSONArray fileID = new JSONArray();
		try {
			this.dbc.query(query, values);
			fileID = this.dbc.query(query2, values2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fileID;
	}
	
	public JSONArray createFile (String fileName, String projectID, String dirID){
		JSONArray returnValue = createLine("//"+fileName);
		String pflhead = (String)((JSONObject)returnValue.get(0)).get("LAST_INSERT_ID()");
		String query = "Insert into PFiles(pfname, pid, pdid, pflhead) values (?, ?, ?, ?)";
		String[] values = new String[4];
		values[0] = fileName;
		values[1] = projectID;
		values[2] = dirID;
		values[3] = pflhead;
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		JSONArray fileID = new JSONArray();
		try {
			this.dbc.query(query, values);
			fileID = this.dbc.query(query2, values2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fileID;
	}
	
	public JSONArray createLine (String text){
		String query = "Insert into PFLines(text, lastEditor, timeEdited) values (?, ?, ?)";
		String[] values = new String[3];
		values[0] = text;
		values[1] = String.valueOf(this.userID);
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		values[2] = stamp.toString();
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		JSONArray pflid = new JSONArray();
		try {
			this.dbc.query(query, values);
			pflid = this.dbc.query(query2, values2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pflid;
	}
	
	public JSONArray createLine (String text, String nextLineID){
		String query = "Insert into PFLines(text, nextid, lastEditor, timeEdited) "
				+ "values (?, ?, ?, ?)";
		String[] values = new String[4];
		values[0] = text;
		values[1] = nextLineID;
		values[2] = String.valueOf(this.userID);
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		values[3] = stamp.toString();
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		String query3 = "Update PFLines set nextid = LAST_INSERT_ID() "
				+ "where nextid = ? and pflid != LAST_INSERT_ID()";
		String[] values3 = new String[1];
		values3[0] = nextLineID;
				
		JSONArray pflid = new JSONArray();
		try {
			this.dbc.query(query, values);
			pflid = this.dbc.query(query2, values2);
			//String tempid = (String)((JSONObject)pflid.get(0)).get("LAST_INSERT_ID()");
			this.dbc.query(query3, values3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pflid;
	}
	
	public JSONArray createLineAtHead (String text, String fileID){
		String query = "Insert into PFLines(text, nextid, lastEditor, timeEdited) values (?, ?, ?, ?)";
		String[] values = new String[4];
		values[0] = text;

		values[2] = String.valueOf(this.userID);
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		values[3] = stamp.toString();
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		String getHeadQuery = "Select pflhead from PFiles where pfid = ?";
		String[] getHeadValues = new String[1];
		getHeadValues[0] = fileID;
				
		String setHeadQuery = "Update PFiles set pflhead = ? where pfid = ?";
		String[] setHeadValues = new String[2];
		setHeadValues[1] = fileID;
		
		JSONArray pflid = new JSONArray();
		JSONArray headArray = new JSONArray();
		try {
			headArray = this.dbc.query(getHeadQuery, getHeadValues);
			values[1] = (String)((JSONObject) headArray.get(0)).get("pflhead");
			this.dbc.query(query, values);
			pflid = this.dbc.query(query2, values2);
			setHeadValues[0] = (String)((JSONObject) pflid.get(0)).get("LAST_INSERT_ID()");
			this.dbc.query(setHeadQuery, setHeadValues);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pflid;
	}
	
	public JSONArray createLineAtEnd (String text, String fileID){
		String query = "Insert into PFLines(text,  lastEditor, timeEdited) values (?,  ?, ?)";
		String[] values = new String[3];
		values[0] = text;
		values[1] = String.valueOf(this.userID);
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		values[2] = stamp.toString();
		
		String query2 = "Select LAST_INSERT_ID();";
		String[] values2 = new String[0];
		
		String updateQuery = "Update PFLines set nextid = ? where pflid = ?";
		String[] updateValues = new String[2];
		
		JSONArray pflid = new JSONArray();
		try {
			this.dbc.query(query, values);
			pflid = this.dbc.query(query2, values2);
			
			String tempid = (String)((JSONObject)pflid.get(0)).get("LAST_INSERT_ID()");
			updateValues[0] = tempid;

			JSONArray lineList = getLines(fileID);
			updateValues[1] = (String)( (JSONObject) (lineList.get(lineList.size()-1))).get("pflid");
			
			this.dbc.query(updateQuery, updateValues);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pflid;
	}
	
	public void removeProject (String projectID){
		String querypa = "Delete from ProjectAccess "
						+ "where PID = ?";
		
		String query = "Delete from Projects " + 
							"where " +
								"PID = ? ";
		String[] values = new String[1];
		values[0] = projectID;
		
		try {
			this.dbc.query(querypa, values);
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeProjectAccess (String projectID, String accessUserID){
		String query = "Delete from ProjectAccess " + 
							"where " +
								"PID = ? " +
								"AND " +
								"userID = ?";
		String[] values = new String[2];
		values[0] = projectID;
		values[1] = accessUserID;
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeDirectory (String dirID){
		String query = "Delete from PDirs " + 
							"where " +
								"pdid = ?";
		String[] values = new String[1];
		values[0] = dirID;
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeFile (String fileID){
		String query = "Delete from PFiles " + 
							"where " +
								"PFID = ?";
		String[] values = new String[1];
		values[0] = fileID;
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeLine (String lineID){
		String firstquery = "Select nextid from PFLines where pflid = ?;";
		String[] firstvalues = new String[1];
		firstvalues[0] = lineID;
		
		String upquery = "Update PFLines " +
						 "set nextid = ?, timeEdited = ? " +
						 "where nextid = ? ";
		String[] upvalues = new String[3];
		upvalues[2] = lineID;
		upvalues[1] = (new Timestamp(System.currentTimeMillis())).toString();
		
		String query = "Delete from PFLines " + 
							"where " +
								"pflid = ? ";
		String[] values = new String[1];
		values[0] = lineID;

		JSONArray nextID = new JSONArray();
		try {
			//Get what line the to-be-deleted line points to
			//upvalues[0] = that
			nextID = this.dbc.query(firstquery, firstvalues);
			upvalues[0] = (String) ((JSONObject) nextID.get(0)).get("nextid");
			this.dbc.query(upquery, upvalues);
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONArray changeLine (String lineID, String text){
		String query = "Update PFLines " +
						"set text = ?, " +
						"lastEditor = ?, " + 
						"timeEdited = ? " + 
						"where pflid = ?";
		String[] values = new String[4];
		values[0] = text;
		values[1] = String.valueOf(this.userID);
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		values[2] = stamp.toString();
		values[3] = lineID;
		
		String newquery = "Select * from PFLines "
				+ "where pflid = ?";
		String[] newvalues = new String[1];
		newvalues[0] = lineID;
		
		
		
		JSONArray ret = new JSONArray();
		try {
			this.dbc.query(query, values);
			ret = this.dbc.query(newquery, newvalues);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public void lockLine (String lineID){
		
		String query = "Insert into LineLocks" +
						"(userID, pflid) " +
						"values (?,?)";
		String[] values = new String[2];
		values[0] = String.valueOf(this.userID);
		values[1] = lineID;

		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unlockLine (String lineID){
		
		String query = "Delete from LineLocks " +
							"where " +
								"pflid = ? " +
								"AND userID = ?";
		String[] values = new String[2];
		values[0] = lineID;
		values[1] = String.valueOf(this.userID);
		
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unlockMyLines (){
		
		String query = "Delete from LineLocks " +
							"where userID = ?;";
		String[] values = new String[1];
		values[0] = String.valueOf(this.userID);
		System.out.println(query);
		System.out.println(values[0]);
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
