package edu.hawaii.ics321f13.model.interfaces;

import java.io.Closeable;

public interface SearchableModel extends Closeable {
	
	Traversable<ImageResult> search(String key, Class<?> resultType, ResultConstraint constraint);
	
}
