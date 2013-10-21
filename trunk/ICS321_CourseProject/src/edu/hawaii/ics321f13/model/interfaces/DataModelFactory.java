package edu.hawaii.ics321f13.model.interfaces;

public interface DataModelFactory {
	
	SearchableModel fromLogin(LoginInfo login, int port);
	
}
