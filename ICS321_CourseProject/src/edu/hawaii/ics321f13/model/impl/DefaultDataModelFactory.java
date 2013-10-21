package edu.hawaii.ics321f13.model.impl;

import edu.hawaii.ics321f13.model.interfaces.DataModelFactory;
import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.SearchableModel;

public class DefaultDataModelFactory implements DataModelFactory {

	@Override
	public SearchableModel fromLogin(LoginInfo login, int port) {
		Database backingDb = new MySQLDatabase(login, port);
		return new DefaultSearchableModel(backingDb);
	}

}
