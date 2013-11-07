package edu.hawaii.ics321f13.view.impl;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.model.interfaces.Traverser;
import edu.hawaii.ics321f13.view.interfaces.ImageLoadListener;
import edu.hawaii.ics321f13.view.interfaces.ImageLoader;
import edu.hawaii.ics321f13.view.interfaces.ResultsPage;

public class DefaultResultsPage implements ResultsPage<ImageResult> {
	
	// Page data.
	private final int PAGE_IDX;
	private final JTable RESULTS_TBL;
	private final Traversable<ImageResult> RESULT_SRC_TRAVERSABLE;
	private final Traverser<ImageResult> RESULT_SRC;
	private final int ROW_COUNT;
	private final int COL_COUNT;
	private final ImageLoader LOADER;
	// Page info.
	private final int PAGE_START_IDX;
	private final int PAGE_END_IDX;
	// Doubly-linked list.
	private ResultsPage<ImageResult> previousPage = null;
	private boolean previousPageQueried = false;
	private ResultsPage<ImageResult> nextPage = null;
	private boolean nextPageQueried = false;
	
	public DefaultResultsPage(int pageIdx, int rowCount, int colCount, 
			Traversable<ImageResult> resultSrc, ImageLoader resultsLoader, JTable resultsTbl) {
		PAGE_IDX = pageIdx;
		ROW_COUNT = rowCount;
		COL_COUNT = colCount;
		PAGE_START_IDX = PAGE_IDX * (ROW_COUNT * COL_COUNT);
		PAGE_END_IDX = (PAGE_IDX + 1) * (ROW_COUNT * COL_COUNT);
		RESULT_SRC_TRAVERSABLE = Objects.requireNonNull(resultSrc);
		RESULT_SRC = Objects.requireNonNull(RESULT_SRC_TRAVERSABLE.traverser());
		LOADER = Objects.requireNonNull(resultsLoader);
		RESULTS_TBL = Objects.requireNonNull(resultsTbl);
		
		if(!(RESULTS_TBL.getModel() instanceof DefaultTableModel)) {
			throw new IllegalArgumentException("table model must subclass DefaultTableModel");
		}
		if(ROW_COUNT < 0 || COL_COUNT < 0) {
			throw new IllegalArgumentException("invalid row/column count");
		}
	}
	
	/**
	 * Internal constructor used to create other pages based on the information in the <code>model</code> page.
	 * 
	 * @param model - the page from which constructor parameters will be copied.
	 * @param pageIdx - the index of the page.
	 */
	private DefaultResultsPage(DefaultResultsPage model, int pageIdx) {
		this(pageIdx, model.ROW_COUNT, model.COL_COUNT, model.RESULT_SRC_TRAVERSABLE, 
				model.LOADER, model.RESULTS_TBL);
	}
	
	public int getPageIndex() {
		return PAGE_IDX;
	}
	
	public boolean hasNextPage() {
		if(!nextPageQueried && nextPage == null) {
			try {
				setTraversableIndex(PAGE_END_IDX);
				if(RESULT_SRC.hasNext()) {
					nextPage = new DefaultResultsPage(this, PAGE_IDX + 1);
				}
			} catch(NoSuchElementException e) {
				// Do nothing. The next page does not exist and this method will report as such.
			}
			nextPageQueried = true;
		}
		return nextPage != null;
	}
	
	public ResultsPage<ImageResult> nextPage() {
		if(hasNextPage()) {
			return nextPage;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public boolean hasPreviousPage() {
		if(!previousPageQueried && previousPage == null) {
			try {
				setTraversableIndex(PAGE_START_IDX);
				if(RESULT_SRC.hasPrevious()) {
					previousPage = new DefaultResultsPage(this, PAGE_IDX - 1);
				}
			} catch(NoSuchElementException e) {
				// Do nothing. The previous page does not exist and this method will return as such.
			}
			previousPageQueried = true;
		}
		return previousPage != null;
	}
	
	public ResultsPage<ImageResult> previousPage() {
		if(hasPreviousPage()) {
			return previousPage;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * Populates the provided <code>JTable</code> with the contents of this <code>DefaultResultsPage</code>.
	 * 
	 * @return the number of results loaded.
	 * 
	 * @throws NoSuchElementException if the contents of this page cannot be accessed by means of the specified
	 * <code>resultSrc</code>.
	 */
	public int populatePage() {
		// Rewind/Fast-forward the Traverser.
		setTraversableIndex(PAGE_START_IDX);
		// Configure the JTable: set column count.
		final int colOffset = PAGE_IDX * COL_COUNT;
		final DefaultTableModel resultsTblModel = (DefaultTableModel) RESULTS_TBL.getModel();
		prepareTable();
		// Populate the resultsPage: init listeners.
		ImageLoadListener listener = new ImageLoadListener() {
			final int UID = 10; // ID of 10 is only used by this method.
			int row = 0; 
			int col = 0;
			@Override
			public void onLoaded(ImageResult loaded) {
				resultsTblModel.setValueAt(loaded, row, colOffset + col);
				if(col >= COL_COUNT - 1) {
					row++;
					col = 0;
				} else {
					col++;
				}
			}
	
			@Override
			public void onLoaded(ImageResult[] loaded) {}
			
			@Override
			public void onError(Exception error) {
				resultsTblModel.setValueAt(UIManager.getIcon("JOptionPane.warningIcon"), row, colOffset + col);
				if(col >= COL_COUNT - 1) {
					row++;
					col = 0;
				} else {
					col++;
				}
			}

			@Override
			public int hashCode() {
				return UID;
			}
			
			@Override
			public boolean equals(Object other) {
				if(other instanceof ImageLoadListener) {
					return hashCode() == other.hashCode();
				} else {
					return false;
				}
				
			}
			
		};
		LOADER.addImageLoadListener(listener);
		// Populate  page of results.
		int loadCount = LOADER.loadImages(RESULT_SRC_TRAVERSABLE, (ROW_COUNT * COL_COUNT));
		// Once we have the image results, we don't need the listener anymore.
		LOADER.removeImageLoadListener(listener);
		return loadCount;
	}
	
	public void scrollToVisible(boolean animate) {
		scrollToVisible(animate, null);
	}
	
	public void scrollToVisible(boolean animate, Runnable onComplete) {
		final Rectangle VISIBLE = RESULTS_TBL.getVisibleRect(); // Used for animation. Visible in the current frame. 
		if(!animate) {
			final Rectangle TARGET = RESULTS_TBL.getVisibleRect(); // Will be modified, so we must allocate a new rectangle.
			int colIdx = (PAGE_IDX * COL_COUNT);
			int rowIdx = 0; // Always use the first/top row. Hoisted here for ease of modification of this algorithm.
			// Throw exception if the row/column does not exist.
			if(RESULTS_TBL.getColumnCount() <= colIdx || RESULTS_TBL.getRowCount() <= rowIdx) {
				throw new IndexOutOfBoundsException(String.format(
						"row (%d) or column (%d) value is invalid and could not be made visible", rowIdx, colIdx));
			}
			Rectangle topLeftCell = RESULTS_TBL.getCellRect(rowIdx, colIdx, true);
			TARGET.x = topLeftCell.x;
			TARGET.y = topLeftCell.y;
			RESULTS_TBL.scrollRectToVisible(TARGET);
			if(onComplete != null) {
				onComplete.run();
			}
		}
	}
	
	public void setActive(ActivityChangeAction... actions) {
		setActive(null, actions);
	}

	public void setActive(final Runnable onComplete, final ActivityChangeAction... actions) {
		Runnable action = new Runnable() {

			@Override
			public void run() {
				populatePage();
				scrollToVisible(Arrays.asList(actions).contains(ActivityChangeAction.ANIMATE), onComplete);
			}
			
		};
		if(Arrays.asList(actions).contains(ActivityChangeAction.CONCURRENT)) {
			new Thread(action).start();	// Run on new thread.
		} else {
			action.run();	// Run on the current thread (often EDT).
		}
	}
	
	@Override
	public boolean equals(Object other) {
		// Note: instanceof ensures that false is returned if other is null (contract of Object.equals()).
		if(other instanceof DefaultResultsPage) {
			return ((DefaultResultsPage) other).PAGE_IDX == PAGE_IDX;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(ResultsPage<ImageResult> other) {
		return other.getPageIndex() - PAGE_IDX;
	}

	@Override
	public void close() {
		// Clear the table cells.
		int columnOffset = PAGE_IDX * COL_COUNT;
		for(int row = 0; row < ROW_COUNT; row++) {
			for(int col = 0; col < COL_COUNT; col++) {
				RESULTS_TBL.setValueAt(null, row, columnOffset + col);
			}
		}
		// Close the ImageResult objects.
		setTraversableIndex(PAGE_START_IDX - 1);
		for(int i = 0; i < (ROW_COUNT * COL_COUNT) && RESULT_SRC.hasNext(); i++) {
			try {
				RESULT_SRC.next().close();
			} catch (IOException e) {
				// Should never happen because default close implementation does not throw exception.
				// This exception is carried over from java.io.Closeable interface.
				throw new RuntimeException(
						"an error occurred while closing image result: " 
								+ (e.getMessage() == null ? "<none>" : e.getMessage()), e);
			}
		}
	}
	
	private Dimension calculateCellSize() {
		Container dimProvider = null;
		if(RESULTS_TBL.getParent().getParent() instanceof JScrollPane) {
			dimProvider = RESULTS_TBL.getParent().getParent();
		} else {
			dimProvider = RESULTS_TBL;
		}
		int width = dimProvider.getWidth() / COL_COUNT-1;
		int height = dimProvider.getHeight() / ROW_COUNT;
		return new Dimension(width, height);
	}
	
	private void setTraversableIndex(int index) {
		if(RESULT_SRC.index() == index) {
			return;
		}
		boolean invalidPageIdx = false;
		while(RESULT_SRC.index() != index) {
			// Move the traverser cursor.
			if(RESULT_SRC.index() < index) {
				if(RESULT_SRC.hasNext()) {
					RESULT_SRC.next();
				} else {
					invalidPageIdx = true;
				}
			} else {
				if(RESULT_SRC.hasPrevious()) {
					RESULT_SRC.previous();
				} else {
					invalidPageIdx = true;
				}
			}
			// If the next/previous element could not be retreived, throw a NoSuchElementException.
			if(invalidPageIdx) {
				throw new NoSuchElementException(String.format(
						"the requested index (%d) does not exist or could not be accessed. " 
								+ "Last valid result index: %d", index, RESULT_SRC.index()));
			}
		}
	}
	
	private void prepareTable() {
		final int colOffset = PAGE_IDX * COL_COUNT;
		final DefaultTableModel resultsTblModel = (DefaultTableModel) RESULTS_TBL.getModel();
		if(RESULTS_TBL.getColumnCount() < colOffset + COL_COUNT) {
			resultsTblModel.setColumnCount(colOffset + COL_COUNT);
		}
		// Configure the JTable: set cell dimensions.
		Dimension cellSize = calculateCellSize();
		for(int i = 0; i < RESULTS_TBL.getColumnCount(); i++) {
			RESULTS_TBL.getColumnModel().getColumn(i).setPreferredWidth(cellSize.width);
			RESULTS_TBL.getColumnModel().getColumn(i).setWidth(cellSize.width);
		}
		resultsTblModel.setRowCount(ROW_COUNT);
		RESULTS_TBL.setRowHeight(cellSize.height);
	}
}
