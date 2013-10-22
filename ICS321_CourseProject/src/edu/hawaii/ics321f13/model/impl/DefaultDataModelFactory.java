package edu.hawaii.ics321f13.model.impl;

import java.sql.SQLException;

import edu.hawaii.ics321f13.model.interfaces.DataModelFactory;
import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.SearchableModel;

public class DefaultDataModelFactory implements DataModelFactory {

	@Override
	public SearchableModel fromLogin(LoginInfo login, int port) {
		try {
			Database backingDb = new MySQLDatabase(login, port);
			return new DefaultSearchableModel(backingDb);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect to SQL database (SQLException): " + e.getMessage(), e);
		}
	}

}
