package edu.hawaii.ics321f13.model.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;

public class MySQLDatabase implements Database {
	
	private final Connection CONN;
	private boolean isClosed = false;
	
	/*
	 * Constructor establishes connection to the database using the correct JDBC URL and the supplied <code>LoginInfo</code> and port
	 * 
	 * @param login - the <code>LoginInfo</code> for the database
	 * @param port - integer holding the port number the database accepts connections on
	 */
	public MySQLDatabase(LoginInfo login, int port) throws SQLException {
		String connectionURL = String.format("jdbc:mysql://localhost:%d/my_wiki", port);
		CONN = DriverManager.getConnection(connectionURL, login.getUserName(), new String(login.getPassword()));
	}

	@Override
	public void close() throws IOException {
		if(isClosed) {
			try {
				CONN.close();
				isClosed = true;
			} catch (SQLException e) {
				throw new IOException(
						"unable to close database because an SQLException occurred: " + e.getMessage(), e);
			}
		}
	}

	/*
	 * Executes a query on the database and returns the results in a <code>ResultSet</code>
	 * 
	 * @param sql - String holding the text of the SQL code query
	 * 
	 * @return <code>ResultSet</code> - the results returned as a result of query. This this implementation, the results won't be lines of the 
	 * database, but will be a collection of <code>BufferedImage</code> obtained as a result of the database's response
	 */
	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		if(isClosed) {
			throw new IllegalStateException("database connection already closed");
		}
		Statement sqlStmt = CONN.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		return sqlStmt.executeQuery(sql);
	}

}
