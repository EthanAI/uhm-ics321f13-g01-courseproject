package edu.hawaii.ics321f13.view.interfaces;

import java.io.Closeable;
import java.io.IOException;

public interface ResultsPage<E> extends Comparable<ResultsPage<E>>, Closeable {
	
	public enum ActivityChangeAction {
		ANIMATE, CONCURRENT,
		AUTOLOAD_NEXT, AUTOLOAD_PREVIOUS, AUTOLOAD_ALL}
	
	int getPageIndex();
	
	boolean hasNextPage();
	
	ResultsPage<E> nextPage();
	
	boolean hasPreviousPage();
	
	ResultsPage<E> previousPage();

	int populatePage();

	void scrollToVisible(boolean animate);
	
	void scrollToVisible(boolean animate, Runnable onComplete);
	
	void setActive(ActivityChangeAction... actions);
	
	void setActive(Runnable onComplete, ActivityChangeAction... actions);
	
}
