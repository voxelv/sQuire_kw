import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Created by Jesse on 3/24/2016.
 */



public class db_connection {

    public String dbms;
    public String jarFile;
    public String dbName;
    public String userName;
    public String password;
    public String urlString;

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
        } else if (this.dbms.equals("derby")) {
            conn = DriverManager.getConnection(
                    "jdbc:" + this.dbms + ":" +
                            this.dbName +
                            ";create=true",
                    connectionProps);
        }
        System.out.println("Connected to database");
        return conn;
    }

    public static void createDatabase(Connection connArg, String dbNameArg,
                                      String dbmsArg) {

        if (dbmsArg.equals("mysql")) {
            try {
                Statement s = connArg.createStatement();
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

    public static void closeConnection(Connection connArg) {
        System.out.println("Releasing all open resources ...");
        try {
            if (connArg != null) {
                connArg.close();
                connArg = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }

    public db_connection(String propertiesFileName) throws FileNotFoundException,
            IOException,
            InvalidPropertiesFormatException {
        super();
        this.setProperties(propertiesFileName);
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

    public static void main(String[] args) {
        db_connection dbc;
        Connection myConnection = null;
        if (args[0] == null) {
            System.err.println("Properties file not specified at command line");
            return;
        } else {
            try {
                System.out.println("Reading properties file " + args[0]);
                dbc = new db_connection(args[0]);
            } catch (Exception e) {
                System.err.println("Problem reading properties file " + args[0]);
                e.printStackTrace();
                return;
            }
        }



        try {
            myConnection = dbc.getConnection();
            //      JDBCTutorialUtilities.outputClientInfoProperties(myConnection);
            // myConnection = myJDBCTutorialUtilities.getConnection("root", "root", "jdbc:mysql://localhost:3306/");
            //       myConnection = myJDBCTutorialUtilities.
            //         getConnectionWithDataSource(myJDBCTutorialUtilities.dbName,"derby","", "", "localhost", 3306);

            // Java DB does not have an SQL create database command; it does require createDatabase
            db_connection.createDatabase(myConnection,
                    dbc.dbName,
                    dbc.dbms);

            //db_connection.cursorHoldabilitySupport(myConnection);
            //db_connection.rowIdLifetime(myConnection);

        } catch (SQLException e) {
            db_connection.printSQLException(e);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            db_connection.closeConnection(myConnection);
        }

    }
}
