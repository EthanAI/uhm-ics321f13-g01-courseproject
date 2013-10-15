package edu.hawaii.ics321f13.model.interfaces;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database extends Closeable {
	
	ResultSet executeQuery(String sql) throws SQLException;
	
}
