package edu.hawaii.ics321f13.model.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import edu.hawaii.ics321f13.model.interfaces.Database;
import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.ResultConstraint;
import edu.hawaii.ics321f13.model.interfaces.SearchableModel;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.model.interfaces.Traverser;

public class DefaultSearchableModel implements SearchableModel {
	
	private final Database DATABASE;
	
	private boolean isClosed = false;
	
	/**
	 * Constructs a new <code>DefaultSearchableModel</code> instance, backed by the specified <code>Database</code>.
	 * 
	 * @param database - the <code>Database</code> instance which backs this model.
	 */
	public DefaultSearchableModel(Database database) {
		DATABASE = Objects.requireNonNull(database);
	}
	
	@Override
	public void close() throws IOException {
		if(!isClosed) {
			DATABASE.close();
			isClosed = true;
		} // Otherwise do nothing to maintain idempotence.
	}

	@Override
	public Traversable<ImageResult> search(String key, Class<?> resultType,
			ResultConstraint constraint) {
		// Make sure we aren't already closed.
		if(isClosed) {
			throw new IllegalStateException("model already closed");
		}
		// Parameter validation.
		Objects.requireNonNull(key);
		Objects.requireNonNull(resultType);
		Objects.requireNonNull(constraint);
		if(key.isEmpty()) {
			// Cannot search for empty string.
			throw new IllegalArgumentException("key must not be empty");
		}
		// Check if we know how to handle this query.
		if(resultType.equals(ImageResult.class) && constraint.equals(ResultConstraint.CONTAINS)) {
			final String SQL = ""; // TODO implement once schema is defined.
			return executeQuery(SQL);
		} else {
			throw new UnsupportedOperationException("unsupported query result type/constraint combination");
		}
	}
	
	private Traversable<ImageResult> executeQuery(String sql) {
		try {
			ResultSet results = DATABASE.executeQuery(sql);
			Traverser<ImageResult> resultTraverser = new ImageResultTraverser(results);
			return new SingletonTraversable<ImageResult>(resultTraverser);
		} catch (SQLException e) {
			throw new RuntimeException("SQLException occurred while processing request: " 
					+ e.getMessage(), e);
		}
		
	}
	
	private class SingletonTraversable<E> implements Traversable<E> {
		
		private final Traverser<E> SINGLETON;
		
		public SingletonTraversable(Traverser<E> singleton) {
			SINGLETON = Objects.requireNonNull(singleton);
		}
		
		@Override
		public Iterator<E> iterator() {
			return SINGLETON;
		}

		@Override
		public Traverser<E> traverser() {
			return SINGLETON;
		}
		
	}
	
	private class ImageResultTraverser implements Traverser<ImageResult> {
		
		private final String URL_PREFIX = ""; // TODO Replace with real prefix.
		
		private final ResultSet QUERY_RESULTS;
		// Next retreival.
		private boolean hasNext = false;
		private boolean nextQueried = false;
		// Previous retreival.
		private boolean hasPrevious = false;
		private boolean previousQueried = false;
		
		public ImageResultTraverser(ResultSet queryResults) {
			QUERY_RESULTS = Objects.requireNonNull(queryResults);
		}
		
		@Override
		public boolean hasNext() {
			// We only want to advance the cursor to the next result if the last cursor advancement was successful.
			// If there were no more results last time, skip this section and return that there are still no results.
			if(!nextQueried) {
				try {
					hasNext = QUERY_RESULTS.next();	// Move cursor back one element.
					nextQueried = true;				// State of the next value is now known.
					previousQueried = false;		// State of the previous value is now unknown.
				} catch (SQLException e) {
					throw new RuntimeException("SQLException occurred while retreiving result: " 
							+ e.getMessage(), e);
				}
			}
			return hasNext;
		}

		@Override
		public ImageResult next() {
			if(hasNext()) {
				ImageResult rtnImageResult = parseResult();	// Format result data java object.
				nextQueried = false;						// State of the next value is now unknown.
				return rtnImageResult;
			} else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("result set not modifiable");
		}

		@Override
		public boolean hasPrevious() {
			if(!previousQueried) {
				try {
					hasPrevious = QUERY_RESULTS.previous();	// Move cursor back one element.
					nextQueried = false;					// State of next value is now unknown.
					previousQueried = true;					// State of previous value is now known.
				} catch (SQLException e) {
					throw new RuntimeException("SQLException occurred while retreiving result: " 
							+ e.getMessage(), e);
				}
			}
			return hasPrevious;
		}

		@Override
		public ImageResult previous() {
			if(hasPrevious()) {
				ImageResult rtnImageResult = parseResult();	// Format result data as java object.
				previousQueried = false;					// State of the previous value is now unknown.
				return rtnImageResult;
			} else {
				throw new NoSuchElementException();
			}
		}
		
		private ImageResult parseResult() {
			String imageURLString = null;
			try {
				String articleTitle = QUERY_RESULTS.getString(0);
				imageURLString = URL_PREFIX + QUERY_RESULTS.getString(1);
				URL imageURL = new URL(imageURLString);
				return new DefaultImageResult(articleTitle, imageURL);
			} catch (SQLException e) {
				throw new RuntimeException(
						"unable to parse query results as specified type (SQLException): " + e.getMessage(), e);
			} catch (MalformedURLException e) {
				throw new RuntimeException(
						"'" + URL_PREFIX + imageURLString + "' is not a valid URL (MalformedURLException): " + e.getMessage(), e);
			}
		}
		
	}

}
