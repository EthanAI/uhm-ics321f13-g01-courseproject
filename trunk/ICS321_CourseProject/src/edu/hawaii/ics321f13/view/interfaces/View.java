package edu.hawaii.ics321f13.view.interfaces;

import java.awt.event.ActionListener;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;

public interface View {
	
	public enum ViewEventType {
		
		/** Indicates that a query has been performed. */
		QUERY(0), 
		
		/** Indicates that the <code>ResultConstraint</code> currently is use should be updated. */
		RESULT_CONSTRAINT(-1),
		
		/** Indicates that the user has requested that the application initiate shutdown. */
		CLOSE(1);
		
		private final int EVENT_ID;
		
		/**
		 * Defines a new <code>ViewEventType</code> enum value.
		 * @param eventID - the unique ID of the event.
		 */
		ViewEventType(int eventID) {
			EVENT_ID = eventID;
		}
		
		/**
		 * Returns the event ID of this <code>ViewEventType</code>.
		 * @return the event ID of this <code>ViewEventType</code>.
		 */
		public int getID() {
			return EVENT_ID;
		}
		
	}
	
	void addActionListener(ActionListener listener);
	
	void setImageSource(Traversable<ImageResult> source);
	
	void clear();
	
	void setBusy(boolean busy);
	
	void addImageTransformer(ImageTransformer transformer);
	
	void addImageTransformer(ImageTransformer transformer, int index);
	
	boolean removeImageTransformer(ImageTransformer transformer);
	
	ImageTransformer removeImageTransformer(int index);
	
	void clearImageTransformers();
	
}
