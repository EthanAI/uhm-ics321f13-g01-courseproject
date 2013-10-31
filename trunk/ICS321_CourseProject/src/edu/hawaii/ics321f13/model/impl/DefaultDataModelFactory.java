package edu.hawaii.ics321f13.model.impl;

import java.sql.SQLException;

import edu.hawaii.ics321f13.model.interfaces.DataModelFactory;
import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.SearchableModel;

/**
 * Factory that creates the <code>SearchableModel</code> that will be used by the controller
 * Uses <code>DataModelFactory</code> interface
 */
public class DefaultDataModelFactory implements DataModelFactory {

	/**
	 * Creates the <code>SearchableModel</code> 
	 * 
	 * @param login - <code>LoginInfo</code> for the model
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
