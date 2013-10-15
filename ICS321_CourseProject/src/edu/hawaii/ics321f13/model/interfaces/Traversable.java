package edu.hawaii.ics321f13.model.interfaces;

public interface Traversable<T> extends Iterable<T> {
	
	Traverser<T> traverser();
	
}
