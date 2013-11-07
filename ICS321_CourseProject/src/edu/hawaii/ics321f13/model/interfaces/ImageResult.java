package edu.hawaii.ics321f13.model.interfaces;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;

public interface ImageResult extends Closeable {
	
	BufferedImage getImage(ImageTransformer... transformers) throws IOException;
	
	URL getImageURL();
	
	String getArticleTitle();
	
	String getArticleAbstract();
	
	URL getArticleURL();
	
	boolean isLoaded();
	
}
