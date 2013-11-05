package edu.hawaii.ics321f13.controller.interfaces;

import edu.hawaii.ics321f13.model.interfaces.Traversable;

public interface Controller<E> {
	
	Traversable<E> onQuery(String searchTerm);
	
	void onClose();
	
}
