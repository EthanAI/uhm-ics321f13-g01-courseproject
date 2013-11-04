package edu.hawaii.ics321f13.view.impl;

import java.io.IOException;
import java.util.ArrayList;

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

	/*
	 * Downloads a set number of images
	 * 
	 * @param source - a <code>Traversable</code> list of <code>ImageResult</code>s
	 * @param loadCount - the number of images to download
	 * 
	 * @return int - the number of images actually loaded
	 * 
	 * @throws <code>IOException</code>
	 */
	@Override
	public int loadImages(Traversable<ImageResult> source, int loadCount) throws IOException {
		ArrayList<ImageResult> loaded = new ArrayList<ImageResult>();
		Traverser<ImageResult> images = source.traverser();
		for(int i = 0; i < loadCount && images.hasNext(); i++) {
			ImageResult result = images.next();
			result.getImage(); // Load the actual image.
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

}
