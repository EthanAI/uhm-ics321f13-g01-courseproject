package edu.hawaii.ics321f13.model.interfaces;

import java.io.Closeable;
/*
 * Calls for the userName and Password loginInfo
 */
public interface LoginInfo extends Closeable {

	String getUserName();
	char[] getPassword();

}
