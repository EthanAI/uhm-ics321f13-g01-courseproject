package edu.hawaii.ics321f13.view.interfaces;

import java.util.EventListener;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;

public interface ImageLoadListener extends EventListener {
	
	void onLoaded(ImageResult loaded);
	
	void onLoaded(ImageResult[] loaded);
	
	void onError(Exception error);
	
}
