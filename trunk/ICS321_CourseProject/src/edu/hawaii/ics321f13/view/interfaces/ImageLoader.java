package edu.hawaii.ics321f13.view.interfaces;

import java.io.Closeable;
import java.io.IOException;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;

public interface ImageLoader extends Closeable {
	
	int loadImages(Traversable<ImageResult> source, int loadCount) throws IOException;
	
	void addImageLoadListener(ImageLoadListener listener);
	
	void removeImageLoadListener(ImageLoadListener listener);

}
