package proto_database;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Some of this code is borrowed from the Oracle tutorial on JDBC


public class DBConnection {

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

    public DBConnection(String propertiesFileName) throws FileNotFoundException,
            IOException,
            InvalidPropertiesFormatException,
    		ClassNotFoundException,
    		SQLException {
        super();
        this.setProperties(propertiesFileName);
        this.connection = this.getConnection(); 
    }

    private void setProperties(String fileName) throws FileNotFoundException,
            IOException,
            InvalidPropertiesFormatException {
        this.prop = new Properties();
        FileInputStream fis = new FileInputStream(fileName);
        prop.loadFromXML(fis);

        this.dbms = this.prop.getProperty("dbms");
        this.jarFile = this.prop.getProperty("jar_file");
        this.driver = this.prop.getProperty("driver");
        this.dbName = this.prop.getProperty("database_name");
        this.userName = this.prop.getProperty("user_name");
        this.password = this.prop.getProperty("password");
        this.serverName = this.prop.getProperty("server_name");
        this.portNumber = Integer.parseInt(this.prop.getProperty("port_number"));

        System.out.println("Set the following properties:");
        System.out.println("dbms: " + dbms);
        System.out.println("driver: " + driver);
        System.out.println("dbName: " + dbName);
        System.out.println("userName: " + userName);
        System.out.println("serverName: " + serverName);
        System.out.println("portNumber: " + portNumber);

    }

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

    public void insertUser(String username)
    	throws SQLException{
    	
    	PreparedStatement statement = null;
    	String statementString = 
    			"insert into Users (username)" +
    			"values (?);";
    	
    	try {
    		this.connection.setAutoCommit(false);
    		statement = this.connection.prepareStatement(statementString);
    		statement.setString(1, username);
    		statement.executeUpdate();
    		this.connection.commit();
    		System.out.println(username + " inserted into Users");
    	} catch (SQLException e) {
    		printSQLException(e);
    		if (this.connection != null){
    			try {
                    System.err.print("Transaction is being rolled back");
                    this.connection.rollback();
                } catch(SQLException excep) {
                    printSQLException(excep);
                }
            }
    		
    	} finally {
            if (statement != null) {
                statement.close();
            }
            
            this.connection.setAutoCommit(true);
        }	
    }
    
    public void insertChannel(String cname)
        	throws SQLException{
        	
        	PreparedStatement statement = null;
        	String statementString = 
        			"insert into Channels (channelName)" +
        			"values (?);";
        	
        	try {
        		this.connection.setAutoCommit(false);
        		statement = this.connection.prepareStatement(statementString);
        		statement.setString(1, cname);
        		statement.executeUpdate();
        		this.connection.commit();
        		System.out.println(cname + " inserted into Channels");
        	} catch (SQLException e) {
        		printSQLException(e);
        		if (this.connection != null){
        			try {
                        System.err.print("Transaction is being rolled back");
                        this.connection.rollback();
                    } catch(SQLException excep) {
                        printSQLException(excep);
                    }
                }
        		
        	} finally {
                if (statement != null) {
                    statement.close();
                }
                
                this.connection.setAutoCommit(true);
            }	
        }
    public void insertSubscription(int channelID, int userID, Timestamp time)
        	throws SQLException{
        	
        	PreparedStatement statement = null;
        	String statementString = 
        			"insert into Subscriptions (ChannelID, userID, joinTime)" +
        			"values (?, ?, ?);";
        	
        	try {
        		this.connection.setAutoCommit(false);
        		statement = this.connection.prepareStatement(statementString);
        		statement.setInt(1, channelID);
        		statement.setInt(2, userID);
        		statement.setTimestamp(3, time);
        		statement.executeUpdate();
        		this.connection.commit();
        		System.out.println("User " + userID + " subscribed to channel " + channelID);
        	} catch (SQLException e) {
        		printSQLException(e);
        		if (this.connection != null){
        			try {
                        System.err.print("Transaction is being rolled back");
                        this.connection.rollback();
                    } catch(SQLException excep) {
                        printSQLException(excep);
                    }
                }
        		
        	} finally {
                if (statement != null) {
                    statement.close();
                }
                
                this.connection.setAutoCommit(true);
            }	
        }
    
	public ResultSet query(String statementString, String[] statementArgs)
		throws SQLException {
		PreparedStatement statement = null;
		ResultSet ret = null;
		
		try {
			statement = this.connection.prepareStatement(statementString);
			for(int i = 0; i < statementArgs.length ; i++)
				statement.setString(i+1, statementArgs[i]);
			ret = statement.executeQuery();
		} catch(SQLException e) {
			printSQLException(e);
		} finally {
			if (statement != null) { statement.close(); }
		}
		return ret;
	}
	
    public void insertMessage(int fromID, int channelID, String text, Timestamp time)
        	throws SQLException{
        	
        	PreparedStatement statement = null;
        	String statementString = 
        			"insert into Messages (timeSent, fromID, ChannelID, messageText)" +
        			"values (?, ?, ?, ?);";
        	
        	try {
        		this.connection.setAutoCommit(false);
        		statement = this.connection.prepareStatement(statementString);
        		statement.setTimestamp(1, time);
        		statement.setInt(2,  fromID);
        		statement.setInt(3,  channelID);
        		statement.setString(4, text);
        		statement.executeUpdate();
        		this.connection.commit();
        		System.out.println("Message sent from user " + fromID + " to channel " + channelID);
        	} catch (SQLException e) {
        		printSQLException(e);
        		if (this.connection != null){
        			try {
                        System.err.print("Transaction is being rolled back");
                        this.connection.rollback();
                    } catch(SQLException excep) {
                        printSQLException(excep);
                    }
                }
        		
        	} finally {
                if (statement != null) {
                    statement.close();
                }
                
                this.connection.setAutoCommit(true);
            }	
        }
		
	
    
    public static void main(String[] args) {
        DBConnection dbc;
        if (args[0] == null) {
            System.err.println("Properties file not specified at command line");
            return;
        } else {
            try {
                System.out.println("Reading properties file " + args[0]);
                dbc = new DBConnection(args[0]);
            } catch (Exception e) {
                System.err.println("Problem reading properties file " + args[0]);
                e.printStackTrace();
                return;
            }
        }
        Timestamp ts = new Timestamp(System.currentTimeMillis());


        try {
            dbc.setDatabase(dbc.dbName);
           // dbc.insertUser("Jeff");
            //dbc.insertChannel("Channel1");
            //dbc.insertSubscription(1, 1, ts);
            dbc.insertMessage(1, 1, "Hello world", ts);
        } catch (SQLException e) {
            DBConnection.printSQLException(e);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            dbc.closeConnection();
        }

    }
	
}
