package edu.hawaii.ics321f13.view.impl;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.model.interfaces.Traverser;
import edu.hawaii.ics321f13.view.interfaces.ImageLoadListener;

/*
 * Naive image loader that loads images on at a time. Note: Future implementation will work 
 * asynchronously
 */
public class SynchronousImageLoader extends AbstractImageLoader {

	@Override
	public void close() throws IOException {
		// There are no thread pools to close, so do nothing.
	}

	/**
	 * Downloads a set number of images
	 * 
	 * @param source - a <code>Traversable</code> list of <code>ImageResult</code>s
	 * @param loadCount - the number of images to download
	 * 
	 * @return int - the number of images actually loaded.
	 */
	@Override
	public int loadImages(Iterable<ImageResult> source, int loadCount, Dimension targetImageSize) {
		ArrayList<ImageResult> loaded = new ArrayList<ImageResult>();
		Iterator<ImageResult> images = source.iterator();
		for(int i = 0; i < loadCount && images.hasNext(); i++) {
			ImageResult result = images.next();
			if(!result.isLoaded()) {
				try {
					result.getImage(targetImageSize); // Load the actual image.
				} catch (IOException e) {
					fireOnError(e);
				}
			}
			fireOnLoaded(result);
		}
		fireOnLoaded(loaded.toArray(new ImageResult[loaded.size()]));
		return loaded.size();
	}
	
	/*
	 * TODO
	 */
	private void fireOnLoaded(ImageResult loaded) {
		ImageLoadListener[] loadListeners = listeners.getListeners(ImageLoadListener.class);
		for(ImageLoadListener loadListener : loadListeners) {
			loadListener.onLoaded(loaded);
		}
	}
	
	/*
	 * TODO
	 */
	private void fireOnLoaded(ImageResult[] loaded) {
		ImageLoadListener[] loadListeners = listeners.getListeners(ImageLoadListener.class);
		for(ImageLoadListener loadListener : loadListeners) {
			loadListener.onLoaded(loaded);
		}
	}
	
	private void fireOnError(Exception error) {
		ImageLoadListener[] loadListeners = listeners.getListeners(ImageLoadListener.class);
		for(ImageLoadListener loadListener : loadListeners) {
			loadListener.onError(error);
		}
	}

}
