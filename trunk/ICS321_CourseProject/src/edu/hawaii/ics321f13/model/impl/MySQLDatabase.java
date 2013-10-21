package edu.hawaii.ics321f13.model.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;

public class MySQLDatabase implements Database {
	
	private final Connection CONN;
	private boolean isClosed = false;
	
	public MySQLDatabase(LoginInfo login, int port) {
		String connectionURL = "jdbc:mysql://localhost:%d/database";
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

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		if(isClosed) {
			throw new IllegalStateException("database connection already closed");
		}
		Statement sqlStmt = CONN.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		return sqlStmt.executeQuery(sql);
	}

}
