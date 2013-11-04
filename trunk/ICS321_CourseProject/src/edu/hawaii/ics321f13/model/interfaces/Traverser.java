package edu.hawaii.ics321f13.model.interfaces;

import java.util.Iterator;

public interface Traverser<E> extends Iterator<E> {
	
	boolean hasPrevious();
	
	E previous();
	
	int index();
	
}
