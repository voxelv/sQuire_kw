package tests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import squire.DBConnector;
import squire.ProjectManager;

public class ProjectManagerTest {
	ProjectManager testManager;
	DBConnector dbc;
	int userID = 9;
	
	
	@Test
	public void testCreateRemoveProject() {
		String projName = "test project";
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
		assertNotNull(returnValue);
		assertEquals(returnValue.size(), 1);
		assertTrue(inList);
		
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
		assertFalse(inList);
	}
	
	@Test
	public void testCreateRemoveFile() {
		String projName = "test project";
		String fileName = "test file";
		String pid = null;
		testManager.setUserID(userID);
		JSONArray fileList = new JSONArray();
		JSONArray returnValue = null;
		try {
			returnValue = testManager.createProject(projName);
			pid = (String)((JSONObject)returnValue.get(0)).get("pid");
			returnValue = testManager.createFile(fileName, pid, "null");
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
		assertNotNull(returnValue);
		assertEquals(returnValue.size(), 1);
		assertTrue(inList);
		
		String pfid = (String)((JSONObject)returnValue.get(0)).get("pfid");
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
		assertEquals(fileList.size(), 0);
		assertFalse(inList);
	}
	
	@Test
	public void testCreateRemoveDir() {
		String projName = "test project";
		String dirName = "test dir";
		String pid = null;
		testManager.setUserID(userID);
		JSONArray dirList = new JSONArray();
		JSONArray returnValue = null;
		try {
			returnValue = testManager.createProject(projName);
			pid = (String)((JSONObject)returnValue.get(0)).get("pid");
			returnValue = testManager.createDirectory(dirName, pid);
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
		assertNotNull(returnValue);
		assertEquals(returnValue.size(), 1);
		assertTrue(inList);
		
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
		assertEquals(dirList.size(), 0);
		assertFalse(inList);
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
			pid = (String)((JSONObject)returnValue.get(0)).get("pid");
			returnValue = testManager.createFile(fileName, pid, "null");
			pfid = (String)((JSONObject)returnValue.get(0)).get("pfid");
			returnValue = testManager.createLine(lineText, "null");
			lineList = testManager.getLines(pfid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean inList = false;
		for(int i = 0; i < lineList.size(); i++){
			JSONObject jobj = (JSONObject) lineList.get(i);
			String listText = (String) jobj.get("text");
			if (lineText.equals(listText))
				inList = true;
		}
		assertNotNull(returnValue);
		assertEquals(returnValue.size(), 1);
		assertTrue(inList);
		
		String pflid = (String)((JSONObject)returnValue.get(0)).get("pflid");
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
		assertEquals(lineList.size(), 0);
		assertFalse(inList);
	}
	
	
	
}
