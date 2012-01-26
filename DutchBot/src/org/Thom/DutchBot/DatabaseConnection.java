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
 * @author Thom
 * 
 */
public class DatabaseConnection {
    private Connection db = null;
    private static DatabaseConnection instance = null;

    public static DatabaseConnection getInstance() {
	if (instance == null) {
	    instance = new DatabaseConnection();
	}
	return instance;
    }

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
