package edu.hawaii.ics321f13.view.impl;

import java.io.IOException;
import java.util.ArrayList;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.model.interfaces.Traverser;
import edu.hawaii.ics321f13.view.interfaces.ImageLoadListener;

public class SynchronousImageLoader extends AbstractImageLoader {

	@Override
	public void close() throws IOException {
		// There are no thread pools to close, so do nothing.
	}

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
	
	private void fireOnLoaded(ImageResult loaded) {
		ImageLoadListener[] loadListeners = listeners.getListeners(ImageLoadListener.class);
		for(ImageLoadListener loadListener : loadListeners) {
			loadListener.onLoaded(loaded);
		}
	}
	
	private void fireOnLoaded(ImageResult[] loaded) {
		ImageLoadListener[] loadListeners = listeners.getListeners(ImageLoadListener.class);
		for(ImageLoadListener loadListener : loadListeners) {
			loadListener.onLoaded(loaded);
		}
	}

}
