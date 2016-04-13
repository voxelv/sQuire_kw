package squire;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
* Connector for a mySQL database.
* @author Jesse Jutson
*/

public class DBConnector {

    public String dbms;
    public String jarFile;
    public String dbName;
    public String userName;
    public String password;
    public String urlString;

    private Connection connection;
    private String driver;
    private String serverName;
    private int portNumber;
    private Properties prop;

	/**
	* Creates and returns the Java SQL Connection, after properties have been set.
	* @return the Java SQL Connection
	*/
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        //Class.forName("com.mysql.jdbc.Driver");

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        if (this.dbms.equals("mysql")) {
            conn = DriverManager.getConnection(
                    "jdbc:" + this.dbms + "://" +
                            this.serverName +
                            ":" + this.portNumber + "/",
                    connectionProps);
        } 
        System.out.println("Connected to database");
        return conn;
    }
	
	/**
	* Creates a database with the specified name at this connection.
	* @params dbNameArg the name of the database to be created
	* @params dbmsArg   the name of the type of database to be created,
	* 					in this case, "mysql"
	*/
    public void createDatabase(String dbNameArg, String dbmsArg) {

        if (dbmsArg.equals("mysql")) {
            try {
                Statement s = this.connection.createStatement();
                String newDatabaseString =
                        "CREATE DATABASE IF NOT EXISTS " + dbNameArg;
                // String newDatabaseString = "CREATE DATABASE " + dbName;
                s.executeUpdate(newDatabaseString);

                System.out.println("Created database " + dbNameArg);
            } catch (SQLException e) {
                printSQLException(e);
            }
        }
    }

	/**
	* Closes the current connection.
	*/
    public void closeConnection() {
        System.out.println("Releasing all open resources ...");
        try {
            if (this.connection != null) {
            	this.connection.close();
            	this.connection = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }
	/**
	* Constructs this DBConnector, setting its properties and its Java SQL Connection
	*/
    public DBConnector() throws ClassNotFoundException,
    		SQLException {
//        super();
        this.setProperties();
        this.connection = this.getConnection(); 
    }

	/**
	* Sets the properties for this DBConnector.
	*/
    private void setProperties() {
//        this.prop = new Properties();
//        FileInputStream fis = new FileInputStream(fileName);
//        prop.loadFromXML(fis);

        this.dbms = "mysql";
        this.jarFile = "";
        this.driver = "com.mysql.jdbc.Driver";
        this.dbName = "squire";
        this.userName = "remote";
        this.password = "squire!";
        this.serverName = "localhost";
        this.portNumber = 9897;
        
//        this.dbms = this.prop.getProperty("dbms");
//        this.jarFile = this.prop.getProperty("jar_file");
//        this.driver = this.prop.getProperty("driver");
//        this.dbName = this.prop.getProperty("database_name");
//        this.userName = this.prop.getProperty("user_name");
//        this.password = this.prop.getProperty("password");
//        this.serverName = this.prop.getProperty("server_name");
//        this.portNumber = Integer.parseInt(this.prop.getProperty("port_number"));

//        System.out.println("Set the following properties:");
//        System.out.println("dbms: " + dbms);
//        System.out.println("driver: " + driver);
//        System.out.println("dbName: " + dbName);
//        System.out.println("userName: " + userName);
//        System.out.println("serverName: " + serverName);
//        System.out.println("portNumber: " + portNumber);

    }

	/**
	* Prints a SQLException with theSQLState, Error Code, and Message.
	* @params ex 	the SQLException to be printed
	*/
    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (ignoreSQLException(((SQLException)e).getSQLState()) == false) {
                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " + ((SQLException)e).getSQLState());
                    System.err.println("Error Code: " + ((SQLException)e).getErrorCode());
                    System.err.println("Message: " + e.getMessage());
                    Throwable t = ex.getCause();
                    while (t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }

	/**
	* Identifies if the SQLException for a given state should be ignored.
	* @params sqlState 	The string of the SQLState of the given exception.
	* @return boolean	True if the SQLException should be ignored,
	*					false otherwise.
	*/
    public static boolean ignoreSQLException(String sqlState) {
        if (sqlState == null) {
            System.out.println("The SQL state is not defined!");
            return false;
        }
        // X0Y32: Jar file already exists in schema
        if (sqlState.equalsIgnoreCase("X0Y32"))
            return true;
        // 42Y55: Table already exists in schema
        if (sqlState.equalsIgnoreCase("42Y55"))
            return true;
        return false;
    }
    
	/**
	* Sets the name of the database for the SQL Connection to use.
	* @params dbNameArg	The name of the database to use.
	*/
    public void setDatabase(String dbNameArg)
    	throws SQLException{
    	Statement s = this.connection.createStatement();
    	String statementString = "use " + dbNameArg;
    	try{
    		s.executeUpdate(statementString);
    		System.out.println("Database set to " + dbNameArg);
    	} catch (SQLException e) {
            printSQLException(e);
        }
    }

        
	/**
	* Executes a given SQL query on the database.
	* @params statementString	A string containing the query.
	* @params statementArgs		An array of the arguments to be placed into the statement.
	* @return 					The results of the query, as a JSONArray
	*/
	public JSONArray query(String statementString, String[] statementArgs)
		throws SQLException {
//		System.out.println("Query: "+statementString);
		
		PreparedStatement statement = null;
		ResultSet ret = null;
		JSONArray output = new JSONArray();
		
		try {
			statement = this.connection.prepareStatement(statementString);
			for(int i = 0; i < statementArgs.length ; i++)
				statement.setString(i+1, statementArgs[i]);
//			ret = statement.executeQuery();
			boolean queryType = statement.execute();
			
			if (queryType)	// if query type is select, get the data
			{
				ret = statement.getResultSet();
				ResultSetMetaData j = ret.getMetaData();
				
				String[] colNames = new String[j.getColumnCount()];
				for (int i = 0; i < j.getColumnCount(); i++)
				{
					colNames[i] = j.getColumnName(i+1);
				}
				
				while(ret.next())
				{
					JSONObject row = new JSONObject();
					for (int i = 0; i < j.getColumnCount(); i++)
						row.put(colNames[i], ret.getString(i+1));
					output.add(row);
				}
			}
			
		} catch(SQLException e) {
			printSQLException(e);
		} finally {
			if (statement != null) { statement.close(); }
		}
		return output;
	}
	
	/**
	* Executes a given SQL query on the database.
	* @params statementString	A string containing the query.
	* @params statementArgs		An array of the arguments to be placed into the statement.
	* @return 					The results of the query, as a JSONArray
	*/
	public JSONArray[] transaction(String[] statementString, String[][] statementArgs)
		throws SQLException {
//		System.out.println("Query: "+statementString);
		
		PreparedStatement statement[] = new PreparedStatement[statementString.length];
		ResultSet ret[] = new ResultSet[statementString.length];
		JSONArray output[] = new JSONArray[statementString.length];
		boolean queryType[] = new boolean[statementString.length];
		
		try {
			this.connection.setAutoCommit(false);
			
			//prepare each statement
			for(int j = 0; j < statementString.length; j++){
				statement[j] = this.connection.prepareStatement(statementString[j]);
				for(int i = 0; i < statementArgs[j].length ; i++)
					statement[j].setString(i+1, statementArgs[j][i]);
//				ret = statement.executeQuery();
				queryType[j] = statement[j].execute();
			}
			this.connection.commit();
			
			for(int j = 0; j < statementString.length; j++){
				output[j] = new JSONArray();
				if (queryType[j])	// if query type is select, get the data
				{
					ret[j] = statement[j].getResultSet();
					ResultSetMetaData data = ret[j].getMetaData();
				
					String[] colNames = new String[data.getColumnCount()];
					for	(int i = 0; i < data.getColumnCount(); i++)
					{
						colNames[i] = data.getColumnName(i+1);
					}
				
					while(ret[j].next())
					{
						JSONObject row = new JSONObject();
						for (int i = 0; i < data.getColumnCount(); i++)
							row.put(colNames[i], ret[j].getString(i+1));
						output[j].add(row);
					}
					
				}
			}
		} catch(SQLException e) {
			printSQLException(e);
			if (this.connection != null) {
	            try {
	                System.err.print("Transaction is being rolled back");
	                this.connection.rollback();
	            } catch(SQLException excep) {
	                printSQLException(excep);
	            }
	        }
		} finally {
			for (int j = 0; j < statement.length; j++)
			{
				if (statement[j] != null) { statement[j].close(); }
			}
			
			this.connection.setAutoCommit(true);
		}
		return output;
	}
	
	
}

