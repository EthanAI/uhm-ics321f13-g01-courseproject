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
	
	public enum ResultColumnInfo {
		ARTICLE_TITLE(1),
		IMAGE_URL(2);
		
		private final int COL_IDX;
		
		ResultColumnInfo(int columnIndex) {
			COL_IDX = columnIndex;
		}
		
		public int getColumnIndex() {
			return COL_IDX;
		}
		
	}
	
	private final Database DATABASE;
	private String TITLE_IMAGE_TABLE  = "test_table_1"; //probably this needs to get moved into MySQLDatabase instead
	
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

	/**
	 * Searches the database for the images related to the supplied key String
	 * 
	 * @param key - String containing the search temr
	 * @param resultType - Class<?> //TODO
	 * @param constraint - ResultConstraint //TODO
	 * 
	 * @return A <code>Traversable</code> containing the <code>ImageResult</code>
	 */
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
			final String SQL = 	"SELECT * FROM " + TITLE_IMAGE_TABLE + 
								//" WHERE title = '" + key + "'"; 		// Strict matching. Few results, but pure. Maybe good for testing?
								" WHERE title LIKE '%" + key + "%'"; 	// Loose matching. cat = catherine
			System.out.println(SQL);
			return executeQuery(SQL);
		} else {
			throw new UnsupportedOperationException("unsupported query result type/constraint combination");
		}
	}
	
	/**
	 * Executes the SQL command supplied upon the database
	 * 
	 * @param sql - String containing the text of the SQL query. In the same form as a typical SQL line of code.
	 * 
	 * @return <code>Traversable</code> containing the <code>ImageResult</code> 
	 */
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
	
	/**
	 * 
	 * TODO
	 */
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
	
	/*
	 * TODO
	 */
	private class ImageResultTraverser implements Traverser<ImageResult> {
		
		private final String URL_PREFIX = "http://en.wikipedia.org/wiki/File:"; // TODO Replace with real prefix.
		
		private final ResultSet QUERY_RESULTS;
		private int lastIdx = -1;
		// Next retreival.
		private boolean hasNext = false;
		private boolean nextQueried = false;
		// Previous retreival.
		private boolean hasPrevious = false;
		private boolean previousQueried = false;
		
		/*
		 * Constructor builds a traversable structure for the query results
		 */
		public ImageResultTraverser(ResultSet queryResults) {
			QUERY_RESULTS = Objects.requireNonNull(queryResults);
		}
		
		/*
		 * Moves the cursor forward one element in the query result list
		 * 
		 * @return boolean indicating if there in fact is a next member
		 */
		@Override
		public boolean hasNext() {
			// We only want to advance the cursor to the next result if the last cursor advancement was successful.
			// If there were no more results last time, skip this section and return that there are still no results.
			if(!nextQueried) {
				try {
					lastIdx = QUERY_RESULTS.getRow() - 1;
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

		/*
		 * Gets the next member in the list of results and returns the <code>ImageResult</code> associated with it
		 * 
		 * @return <code>ImageResult</code> Image associated with the next article in the list
		 */
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
		
		/*
		 * Attempts to remove an element from the list. Not supported with this version of the implementation
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException("result set not modifiable");
		}

		/*
		 * Moves the cursor back one element in the query result list
		 * 
		 * @return boolean indicating if there in fact is a previous member
		 */
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

		/*
		 * Gets the previous member in the list of results and returns the <code>ImageResult</code> associated with it
		 * 
		 * @return <code>ImageResult</code> Image associated with the previous article in the list
		 */
		@Override
		public ImageResult previous() {
			if(hasPrevious()) { //Note hasPrevious() doesnt just check, it also fetches the previous query item
				ImageResult rtnImageResult = parseResult();	// Format result data as java object.
				previousQueried = false;					// State of the previous value is now unknown.
				return rtnImageResult;
			} else {
				throw new NoSuchElementException();
			}
		}
		
		/*
		 * Gets the <code>ImageResult</code> from the currently selected result out of the query results. Takes the image name found in the database,
		 * appends it to the URL stub of the wikicommons webpage, sends that URL to the <code>DefaultImageResult</code> class which extracts the actual image
		 * from that page and returns it
		 * 
		 * @return <code>ImageResult</code> the image associated with this member of the query result
		 */
		private ImageResult parseResult() {
			String imageURLString = null;
			try {
				String articleTitle = QUERY_RESULTS.getString(ResultColumnInfo.ARTICLE_TITLE.getColumnIndex());
				imageURLString = URL_PREFIX + QUERY_RESULTS.getString(ResultColumnInfo.IMAGE_URL.getColumnIndex());
				URL imageURL = new URL(imageURLString);
				return new DefaultImageResult(articleTitle, imageURL);
			} catch (SQLException e) {
				throw new ClassCastException(
						"unable to parse query results as specified type (SQLException): " + e.getMessage());
			} catch (MalformedURLException e) {
				throw new RuntimeException(
						"'" + URL_PREFIX + imageURLString + "' is not a valid URL (MalformedURLException): " + e.getMessage(), e);
			}
		}

		@Override
		public int index() {
			try {
				if(QUERY_RESULTS.isAfterLast()) {
					return lastIdx + 1;
				} else {
					// Must subtract 1 to convert from a one-based row index to a zero-based index and to support the
					// requirement that a value of -1 is returned before next() has ever been called on the current
					// ResultSet object.
					return QUERY_RESULTS.getRow() - 1;
				}
			} catch (SQLException e) {
				throw new RuntimeException("SQLException occurred while retrieving row index: " 
						+ e.getMessage(), e);
			}
		}
		
	}

}
