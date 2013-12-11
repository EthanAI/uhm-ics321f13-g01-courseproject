package edu.hawaii.ics321f13.view.interfaces;

import java.awt.Dimension;
import java.io.Closeable;
import edu.hawaii.ics321f13.model.interfaces.ImageResult;

public interface ImageLoader extends Closeable {
	
	void loadImages(Iterable<ImageResult> source, int loadCount, Dimension targetImageSize, ImageLoadListener observer);

}
