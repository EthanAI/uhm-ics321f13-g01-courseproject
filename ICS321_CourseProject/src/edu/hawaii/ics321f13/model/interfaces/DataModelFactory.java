package edu.hawaii.ics321f13.model.interfaces;
/**
 * This interface updates the view with the login information and port number
 *
 */
public interface DataModelFactory {
	
	SearchableModel fromLogin(LoginInfo login, int port);
	
}
