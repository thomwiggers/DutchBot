/**
 * 
 * @author Thom
 */
package org.Thom.DutchBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manager for the database
 * 
 * @author Thom
 * 
 */
public class DatabaseConnection {

    /**
     * Container for the singleton instance
     */
    private Connection db = null;

    /**
     * store the instance of the object
     */
    private static DatabaseConnection instance = null;

    /**
     * Singleton accessor
     * 
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
	if (instance == null) {
	    instance = new DatabaseConnection();
	}
	return instance;
    }

    /**
     * Singleton constructor
     */
    private DatabaseConnection() {

    }

    /**
     * Connect to the db
     * 
     * @param database
     * @param username
     * @param password
     */
    public void connect(String database, String username, String password) {
	try {
	    this.db = DriverManager.getConnection("jdbc:postgresql://rded.nl/"
		    + database + "?user=" + username + "&password=" + password
		    + "&ssl=true"
		    + "&sslfactory=org.postgresql.ssl.NonValidatingFactory");
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Query the db
     * 
     * @param query
     * @return resultset of the query
     */
    public ResultSet query(String query) {
	try {
	    Statement s = db.createStatement();
	    ResultSet rs = s.executeQuery(query);
	    s.close();
	    return rs;
	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public Connection getDb() {
	return db;
    }
}
