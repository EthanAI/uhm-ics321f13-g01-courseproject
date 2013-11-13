package edu.hawaii.ics321f13.model.interfaces;

import java.io.Closeable;

public interface LoginInfo extends Closeable {

	String getUserName();
	char[] getPassword();

}
