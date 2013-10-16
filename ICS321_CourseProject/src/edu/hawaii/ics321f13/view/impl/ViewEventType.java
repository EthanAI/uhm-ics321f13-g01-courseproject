package edu.hawaii.ics321f13.view.impl;

public enum ViewEventType {
	
	/** Indicates that a query has been performed. */
	QUERY(0), 
	
	/** Indicates that the user has requested that the application initiate shutdown. */
	CLOSE(1);
	
	private final int EVENT_ID;
	
	/**
	 * Defines a new <code>ViewEventType</code> enum value.
	 * @param eventID - the unique ID of the event.
	 */
	ViewEventType(int eventID) {
		EVENT_ID = eventID;
	}
	
	/**
	 * Returns the event ID of this <code>ViewEventType</code>.
	 * @return the event ID of this <code>ViewEventType</code>.
	 */
	public int getID() {
		return EVENT_ID;
	}
	
	
}
