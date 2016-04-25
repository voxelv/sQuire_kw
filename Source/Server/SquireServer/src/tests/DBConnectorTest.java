package tests;

import java.sql.SQLException;

import org.junit.Test;

import squire.DBConnector;

public class DBConnectorTest {
	DBConnector dbc;
	
	@Test
	public void TestSetDatabase(String dbName){
		try {
			this.dbc.setDatabase(dbName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void TestQuery(){
		
	}
	
	public void TestTransaction(){
		
	}

}
