package com.tender.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class DBConnection {

 public Connection getConnection() throws Exception {
	 try {
		 	String connectionURL = ApplicationProperties.getProperty("connectionURL");	
			String username = ApplicationProperties.getProperty("usernameDb");	
			String password = ApplicationProperties.getProperty("passwordDb");	
			Connection connection = null;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, username,
					password);
			return connection;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
 	} 
}
