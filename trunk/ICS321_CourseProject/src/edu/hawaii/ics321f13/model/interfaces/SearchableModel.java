package edu.hawaii.ics321f13.model.interfaces;

import java.io.Closeable;

public interface SearchableModel extends Closeable {
	/**
	 * Queries the data model for values which match the specified search criteria. If no results are found, the
	 * returned <code>Traversable</code> will be empty.
	 * 
	 * @param key - the search key.
	 * @param resultType - the expected type of the returned results <code>Traversable</code>.
	 * @param constraint - the <code>ResultConstraint</code> by which search results will be constrained.
	 * 
	 * @return A <code>Traversable</code> containing the results of this search.
	 */
	Traversable<ImageResult> search(String key, Class<?> resultType, ResultConstraint constraint);
	
}
