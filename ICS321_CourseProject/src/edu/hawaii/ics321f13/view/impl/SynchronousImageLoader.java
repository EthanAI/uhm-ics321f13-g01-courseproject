package edu.hawaii.ics321f13.view.impl;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.model.interfaces.Traverser;
import edu.hawaii.ics321f13.view.interfaces.ImageLoadListener;
import edu.hawaii.ics321f13.view.interfaces.ImageLoader;

/*
 * Naive image loader that loads images on at a time. Note: Future implementation will work 
 * asynchronously
 */
public class SynchronousImageLoader implements ImageLoader {

	/**
	 * Downloads a set number of images
	 * 
	 * @param source - a <code>Traversable</code> list of <code>ImageResult</code>s
	 * @param loadCount - the number of images to download
	 */
	@Override
	public void loadImages(Iterable<ImageResult> source, int loadCount, Dimension targetImageSize, ImageLoadListener observer) {
		ArrayList<ImageResult> loaded = new ArrayList<ImageResult>();
		Iterator<ImageResult> images = source.iterator();
		for(int i = 0; i < loadCount && images.hasNext(); i++) {
			ImageResult result = images.next();
			try {
				result.getImage(targetImageSize); // Load the actual image.
				loaded.add(result);
				observer.onLoaded(result);
			} catch (Exception e) {
				observer.onError(e);
				ErrorImageResult errImgRslt = new ErrorImageResult(e);
				loaded.add(errImgRslt);
				observer.onLoaded(errImgRslt);
			}
		}
		observer.onLoaded(loaded.toArray(new ImageResult[loaded.size()]));
	}

	@Override
	public void close() throws IOException {
		// There are no thread pools to close, so do nothing.
	}

}
