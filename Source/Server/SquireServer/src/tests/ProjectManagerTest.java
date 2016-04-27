package tests;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import squire.DBConnector;
import squire.ProjectManager;

public class ProjectManagerTest {
	ProjectManager testManager;
	DBConnector dbc;
	int userID = 3;
	
	public ProjectManagerTest(){
		try {
			this.dbc = new DBConnector("mysql", "", "com.mysql.jdbc.Driver", 
					"squire", "remote", "squire!", "SquireRaspServer.ddns.net", 9897);
			//conn = (Connection) DriverManager.getConnection("jdbc:mysql://SquireRaspServer.ddns.net:9897/squire","remote","squire!");
			System.out.println("Connected.");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.dbc.setProperties("mysql", "", "com.mysql.jdbc.Driver", 
			//	"squire", "root", "squire!", "localhost", 3306);
		testManager = new ProjectManager(this.dbc);
	}
	
	
	@Test
	public void testCreateRemoveProject() {
		String projName = "test project j";
		testManager.setUserID(userID);
		JSONArray projectList = new JSONArray();
		JSONArray returnValue = null;
		try {
			returnValue = testManager.createProject(projName);
			projectList = testManager.getProjects();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean inList = false;
		for(int i = 0; i < projectList.size(); i++){
			JSONObject jobj = (JSONObject) projectList.get(i);
			String listName = (String) jobj.get("pname");
			if (projName.equals(listName))
				inList = true;
		}
		assertNotNull("Create Project return value null", returnValue);
		assertEquals("Create Project return value size != 1", returnValue.size(), 1);
		assertTrue("Created Project not found", inList);
		
		String pid = (String)((JSONObject)returnValue.get(0)).get("pid");
		testManager.removeProject(pid);
		try {
			projectList = testManager.getProjects();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inList = false;
		for(int i = 0; i < projectList.size(); i++){
			JSONObject jobj = (JSONObject) projectList.get(i);
			String listName = (String) jobj.get("pname");
			if (projName.equals(listName))
				inList = true;
		}
		assertFalse("Created project not deleted", inList);
	}
	
	@Test
	public void testCreateRemoveFile() {
		String projName = "test project j";
		String fileName = "test file j";
		String pid = null;
		testManager.setUserID(userID);
		JSONArray fileList = new JSONArray();
		JSONArray returnValue = null;
		try {
			returnValue = testManager.createProject(projName);
			System.out.println("tcrf cp returnvalue"+String.valueOf(returnValue));
			pid = (String)((JSONObject)returnValue.get(0)).get("LAST_INSERT_ID()");
			returnValue = testManager.createFile(fileName, pid);
			fileList = testManager.getFiles(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean inList = false;
		for(int i = 0; i < fileList.size(); i++){
			JSONObject jobj = (JSONObject) fileList.get(i);
			String listName = (String) jobj.get("pfname");
			if (fileName.equals(listName))
				inList = true;
		}
		assertNotNull("Create file returned null", returnValue);
		assertEquals("Create file returned <> 1 item",returnValue.size(), 1);
		assertTrue("Created item not in list", inList);
		
		String pfid = (String)((JSONObject)returnValue.get(0)).get("LAST_INSERT_ID()");
		System.out.println("tcrf cf returnvalue"+String.valueOf(returnValue));
		testManager.removeFile(pfid);
		try {
			if(pid != null){
				fileList = testManager.getFiles(pid);
				testManager.removeProject(pid);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inList = false;
		for(int i = 0; i < fileList.size(); i++){
			JSONObject jobj = (JSONObject) fileList.get(i);
			String listName = (String) jobj.get("pfname");
			if (fileName.equals(listName))
				inList = true;
		}
		assertEquals("File list not empty", fileList.size(), 0);
		assertFalse("Created file not deleted", inList);
	}
	
	@Test
	public void testCreateRemoveDir() {
		String projName = "test project j";
		String dirName = "test dir j";
		String pid = null;
		testManager.setUserID(userID);
		JSONArray dirList = new JSONArray();
		JSONArray returnValue = null;
		try {
			returnValue = testManager.createProject(projName);
			pid = (String)((JSONObject)returnValue.get(0)).get("pid");
			returnValue = testManager.createDirectory(pid, dirName);
			dirList = testManager.getDirectories(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean inList = false;
		for(int i = 0; i < dirList.size(); i++){
			JSONObject jobj = (JSONObject) dirList.get(i);
			String listName = (String) jobj.get("pdname");
			if (dirName.equals(listName))
				inList = true;
		}
		assertNotNull("createDirectory returned null", returnValue);
		assertEquals("createDirectory returned <> 1 item", returnValue.size(), 1);
		assertTrue("Created Directory not found", inList);
		
		String pdid = (String)((JSONObject)returnValue.get(0)).get("pdid");
		testManager.removeDirectory(pdid);
		try {
			if(pid != null){
				dirList = testManager.getDirectories(pid);
				testManager.removeProject(pid);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inList = false;
		for(int i = 0; i < dirList.size(); i++){
			JSONObject jobj = (JSONObject) dirList.get(i);
			String listName = (String) jobj.get("pdname");
			if (dirName.equals(listName))
				inList = true;
		}
		assertEquals("Directories still exist", dirList.size(), 0);
		assertFalse("Created directory not deleted", inList);
	}
	
	@Test
	public void testCreateRemoveLine() {
		String projName = "test project";
		String fileName = "test file";
		String lineText = "test line";
		String pid = null;
		String pfid = null;
		testManager.setUserID(userID);
		JSONArray lineList = new JSONArray();
		JSONArray returnValue = null;
		try {
			returnValue = testManager.createProject(projName);
			pid = (String)((JSONObject)returnValue.get(0)).get("LAST_INSERT_ID()");
			returnValue = testManager.createFile(fileName, pid);
			pfid = (String)((JSONObject)returnValue.get(0)).get("LAST_INSERT_ID()");
			returnValue = testManager.createLineAtEnd(lineText, pfid);
			lineList = testManager.getLines(pfid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean inList = false;
		int lineListSize = lineList.size();
		for(int i = 0; i < lineList.size(); i++){
			JSONObject jobj = (JSONObject) lineList.get(i);
			String listText = (String) jobj.get("text");
			if (lineText.equals(listText))
				inList = true;
		}
		assertNotNull("CreateLine returned null", returnValue);
		assertEquals("CreateLine returned <> 1 item", returnValue.size(), 1);
		assertTrue("Created line not found", inList);
		
		String pflid = (String)((JSONObject)returnValue.get(0)).get("LAST_INSERT_ID()");
		testManager.removeLine(pflid);
		try {
			if(pid != null){
				lineList = testManager.getLines(pfid);
				testManager.removeFile(pfid);
				testManager.removeProject(pid);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inList = false;
		for(int i = 0; i < lineList.size(); i++){
			JSONObject jobj = (JSONObject) lineList.get(i);
			String listText = (String) jobj.get("text");
			if (lineText.equals(listText))
				inList = true;
		}
		assertFalse("Created line not deleted",inList);
		assertEquals("More lines exist than should", lineList.size(), lineListSize - 1);
	}
	
	
	
}
