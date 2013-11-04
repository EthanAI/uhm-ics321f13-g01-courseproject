package edu.hawaii.ics321f13.view.impl;

import javax.swing.event.EventListenerList;

import edu.hawaii.ics321f13.view.interfaces.ImageLoadListener;
import edu.hawaii.ics321f13.view.interfaces.ImageLoader;

public abstract class AbstractImageLoader implements ImageLoader {
	
	protected EventListenerList listeners = new EventListenerList();

	@Override
	public void addImageLoadListener(ImageLoadListener listener) {
		listeners.add(ImageLoadListener.class, listener);
	}
	
	@Override
	public void removeImageLoadListener(ImageLoadListener listener) {
		listeners.remove(ImageLoadListener.class, listener);
	}

}
