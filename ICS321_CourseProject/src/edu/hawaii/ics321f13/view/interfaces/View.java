package edu.hawaii.ics321f13.view.interfaces;

import java.awt.event.ActionListener;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;

public interface View {
	
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
