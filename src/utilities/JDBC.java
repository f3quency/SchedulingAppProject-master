package utilities;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * My java database connection class
 */
public abstract class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String dbName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + dbName + "?connectionTimeZone = SERVER";
    private static final String driverInterface = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static final String userPassword = "Passw0rd!";
    public static Connection myConnection;

    /**
     * helps open database connection
     */
    public static void openDBConnection(){
        try{
            Class.forName(driverInterface);
            myConnection = DriverManager.getConnection(jdbcUrl, userName, userPassword);
            System.out.println("Successfully Connected.");
        }
        catch(Exception exception){
            System.err.println("Failed to Connect: " + exception.getMessage() + "!");
        }
    }

    /**
     * Helps close database connection
     */
    public static void closeDBConnection(){
        try{
            myConnection.close();
            System.out.println("Connection is successfully closed.");
        }
        catch(Exception exception){
            System.err.println("Failed to close connection: " + exception.getMessage() + "!");
        }
    }
}
