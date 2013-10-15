package edu.hawaii.ics321f13.view.impl;

import java.io.IOException;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.view.interfaces.ImageLoadListener;
import edu.hawaii.ics321f13.view.interfaces.ImageLoader;

public abstract class AbstractImageLoader implements ImageLoader {

	@Override
	public abstract void close() throws IOException;

	@Override
	public abstract int loadImages(Traversable<ImageResult> source, int loadCount) throws IOException;

	@Override
	public void addImageLoadListener(ImageLoadListener listener) {
		// TODO Auto-generated method stub

	}

}
