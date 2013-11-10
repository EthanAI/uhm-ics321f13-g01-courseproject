package edu.hawaii.ics321f13.view.impl;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.view.interfaces.ResultsPage;

public class EmptyResultSetPage implements ResultsPage<ImageResult> {
	
	private final int DEFAULT_ROW_HEIGHT = 200;
	
	private final String TEXT;
	private final JTable RESULTS_TBL;
	private final int ROW_COUNT;
	private final int COL_COUNT;
	
	public EmptyResultSetPage(String text, JTable resultsTable, int rowCount, int colCount) {
		TEXT = Objects.requireNonNull(text);
		RESULTS_TBL = Objects.requireNonNull(resultsTable);
		ROW_COUNT = rowCount;
		COL_COUNT = colCount;
		
		if(!(RESULTS_TBL.getModel() instanceof DefaultTableModel)) {
			throw new IllegalArgumentException("table model must inherit from DefaultTableModel");
		}
		if(ROW_COUNT < 0 || COL_COUNT < 0) {
			throw new IllegalArgumentException(
					String.format("invalid number of rows/columns [rows=%d, columns=%d]", ROW_COUNT, COL_COUNT));
		}
	}
	
	@Override
	public int compareTo(ResultsPage<ImageResult> o) {
		// Incomparable, so it will always compare equal. 
		return 0;
	}

	@Override
	public void close() throws IOException {
		// Return the table to its original state.
		DefaultTableModel model = (DefaultTableModel) RESULTS_TBL.getModel();
		model.setColumnCount(COL_COUNT);
		model.setRowCount(ROW_COUNT);
		RESULTS_TBL.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	@Override
	public int getPageIndex() {
		return 0;
	}

	@Override
	public boolean hasNextPage() {
		// There is never a next page.
		return false;
	}

	@Override
	public ResultsPage<ImageResult> nextPage() {
		// There is never a next page.
		throw new NoSuchElementException();
	}

	@Override
	public boolean hasPreviousPage() {
		// There is never a previous page.
		return false;
	}

	@Override
	public ResultsPage<ImageResult> previousPage() {
		// There is never a previous page.
		throw new NoSuchElementException();
	}

	@Override
	public int populatePage() {
		DefaultTableModel model = (DefaultTableModel) RESULTS_TBL.getModel();
		model.setColumnCount(1);
		model.setRowCount(1);
		RESULTS_TBL.setRowHeight(DEFAULT_ROW_HEIGHT);
		RESULTS_TBL.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		RESULTS_TBL.setValueAt(TEXT, 0, 0);
		return 0; // No images are ever loaded.
	}

	@Override
	public void scrollToVisible(boolean animate) {
		// Do nothing. This is the only page. It is always visible.
	}

	@Override
	public void scrollToVisible(boolean animate, Runnable onComplete) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActive(ResultsPage.ActivityChangeAction... actions) {
		populatePage();
	}

	@Override
	public void setActive(Runnable onComplete, ResultsPage.ActivityChangeAction... actions) {
		populatePage();
		onComplete.run();
	}

}
