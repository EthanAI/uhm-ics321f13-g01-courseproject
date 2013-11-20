package edu.hawaii.ics321f13.view.interfaces;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;

public interface ImageLoader extends Closeable {
	
	int loadImages(Iterable<ImageResult> source, int loadCount, Dimension targetImageSize);
	
	void addImageLoadListener(ImageLoadListener listener);
	
	void removeImageLoadListener(ImageLoadListener listener);

}
