package edu.hawaii.ics321f13.model.interfaces;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.net.URL;

public interface ImageResult extends Closeable {
	
	BufferedImage getImage();
	
	URL getImageURL();
	
	String getArticleTitle();
	
	String getArticleAbstract();
	
	URL getArticleURL();
	
}
