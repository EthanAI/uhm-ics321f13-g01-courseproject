package edu.hawaii.ics321f13.model.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import edu.hawaii.ics321f13.model.interfaces.LoginInfo;

public class DefaultLoginInfo implements LoginInfo {
	
	private final String USERNAME;
	private final char[] PASSWORD;
	
	private boolean isClosed = false;
	
	/**
	 * Constructor for the <code>DefaultLoginInfo</class>
	 * 
	 * @param username - String holding the login ID for the database
	 * @param password - char[] holding the password for the database
	 */
	public DefaultLoginInfo(String username, char[] password) {
		USERNAME = Objects.requireNonNull(username);
		PASSWORD = Objects.requireNonNull(password);
	}
	
	@Override
	public void close() throws IOException {
		if(!isClosed) {
			Arrays.fill(PASSWORD, '\0');
			isClosed = true;
		}
	}

	/**
	 * Accessor for the <code>USERNAME</code>
	 */
	@Override
	public String getUserName() {
		if(isClosed) {
			throw new IllegalStateException("already closed");
		}
		return USERNAME;
	}

	/**
	 * Accessor for the <code>PASSWORD</code>
	 */
	@Override
	public char[] getPassword() {
		if(isClosed) {
			throw new IllegalStateException("already closed");
		}
		return PASSWORD;
	}

}
