package edu.hawaii.ics321f13.model.interfaces;

import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;

public interface ImageResult extends Closeable, Transferable {
	
	BufferedImage getImage(ImageTransformer... transformers) throws IOException;
	
	BufferedImage getImage(Dimension targetSize, ImageTransformer...transformers) throws IOException;
	
	URL getImageURL();
	
	URL getImageURL(Dimension targetSize);
	
	String getArticleTitle();
	
	String getArticleAbstract();
	
	URL getArticleURL();
	
}
