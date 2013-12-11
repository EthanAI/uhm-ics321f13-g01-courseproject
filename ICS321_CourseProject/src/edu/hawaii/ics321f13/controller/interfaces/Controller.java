package edu.hawaii.ics321f13.controller.interfaces;

import edu.hawaii.ics321f13.model.interfaces.Traversable;

/**
 * This is the controller that manipulates the search term 
 * and loads the query for the <code>Traversable</code> then voids/clears it's self
 *
 * @param <E>
 */

public interface Controller<E> {
	
	Traversable<E> onQuery(String searchTerm);
	
	void onClose();
	
}
