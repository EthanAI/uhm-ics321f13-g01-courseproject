package edu.hawaii.ics321f13.model.impl;

import java.sql.SQLException;

import edu.hawaii.ics321f13.model.interfaces.DataModelFactory;
import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.SearchableModel;

/**
 * uses the login and port number for the database and updates the view
 */
public class DefaultDataModelFactory implements DataModelFactory {

	/**
	 * Gathers the login and port to log on the database then returns an error if it failed to connect
	 * 
	 * @param login - the login information that is used for the model
	 * @param port - port for the model
	 */
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
