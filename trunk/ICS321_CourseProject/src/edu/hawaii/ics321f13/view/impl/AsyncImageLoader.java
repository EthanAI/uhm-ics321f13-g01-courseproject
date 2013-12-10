package edu.hawaii.ics321f13.view.impl;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.view.interfaces.ImageLoadListener;
import edu.hawaii.ics321f13.view.interfaces.ImageLoader;

public class AsyncImageLoader implements ImageLoader {
	
	private final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	
	@Override
	public void close() throws IOException {
		EXECUTOR.shutdown();
	}

	@Override
	public void loadImages(Iterable<ImageResult> source, int loadCount, Dimension targetImageSize, ImageLoadListener observer) {
		if(EXECUTOR.isShutdown()) {
			throw new IllegalStateException("already closed");
		}
		if(loadCount < 0) {
			throw new IllegalArgumentException("loadCount must be non-negative");
		}
		// Must create a new AtomicInteger and Vector for every invocation of loadImages() to ensure that the onLoaded() event
		// is fired only when the CURRENT batch of images are finished loading, even if there are other images being
		// loaded in response to another invocation of loadImages().
		AtomicInteger synchronizer = new AtomicInteger(loadCount);
		// Must use a thread-safe Collection implementation, such as Vector.
		Vector<ImageResult> loaded = new Vector<ImageResult>();
		Iterator<ImageResult> imgSrc = Objects.requireNonNull(source).iterator();
		for(int i = 0; i < loadCount && imgSrc.hasNext(); i++) {
			ImageResult result = imgSrc.next();
			EXECUTOR.execute(new ImageLoaderExecutable(result, targetImageSize, loaded, synchronizer, observer));
		}
	}
	
	private class ImageLoaderExecutable implements Runnable {
		
		private final ImageResult LDR_IMAGE;
		private final Dimension LDR_SIZE;
		private final Collection<ImageResult> LOADED;
		private final AtomicInteger SYNC;
		private final ImageLoadListener OBSERVER;
		
		public ImageLoaderExecutable(ImageResult img, Dimension targetSize, Collection<ImageResult> loaded, 
				AtomicInteger synchronizer, ImageLoadListener observer) {
			LDR_IMAGE = Objects.requireNonNull(img);
			LDR_SIZE = Objects.requireNonNull(targetSize);
			LOADED = Objects.requireNonNull(loaded);
			SYNC = Objects.requireNonNull(synchronizer);
			OBSERVER = Objects.requireNonNull(observer);
		}
		
		@Override
		public void run() {
			try {
				LDR_IMAGE.getImage(LDR_SIZE); // Load the actual image.
				LOADED.add(LDR_IMAGE);
				OBSERVER.onLoaded(LDR_IMAGE);
			} catch (Exception e) {
				OBSERVER.onError(e);
			}
			if(SYNC.decrementAndGet() <= 0) {
				OBSERVER.onLoaded(LOADED.toArray(new ImageResult[LOADED.size()]));
			}
		}
		
	}

}
