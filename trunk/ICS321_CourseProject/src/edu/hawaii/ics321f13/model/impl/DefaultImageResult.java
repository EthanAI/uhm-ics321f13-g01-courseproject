package edu.hawaii.ics321f13.model.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;

public class DefaultImageResult implements ImageResult {
	
	private final URL IMAGE_URL;
	private final URL ARTICLE_URL;
	private final String ARTICLE_TITLE;
	private final String ARTICLE_ABSTRACT;
	
	private BufferedImage imageCache = null;
	
	public DefaultImageResult(String articleTitle, URL imageURL) {
		this(articleTitle, imageURL, null, null);
	}
	
	public DefaultImageResult(String articleTitle, URL imageURL, URL articleURL, String articleAbstract) {
		ARTICLE_TITLE = Objects.requireNonNull(articleTitle);
		IMAGE_URL = Objects.requireNonNull(imageURL);
		ARTICLE_URL = articleURL;
		ARTICLE_ABSTRACT = articleAbstract;
	}
	
	@Override
	public void close() throws IOException {
		// When the user scrolls away from this image, while we don't need the image anymore, we do need the
		// metadata about the image which is stored in this class. So allow the image itself to be garbage-collected
		// but keep the metadata alive. 
		imageCache = null;
	}
	
	@Override
	public BufferedImage getImage() throws IOException { //throw IOExceptions for controller to handle
		final int INDEX_MEDIUM_IMAGE = 0;
		String imageUrlString;
		System.out.println("Getting Image");
		try {
			Document doc = Jsoup.connect(getImageURL().toString()).get(); //Jsoup closes its connection after it downloads the data
			
			Element image = doc.select("img").get(INDEX_MEDIUM_IMAGE); //possible to get other sizes of the image by venturing into .get(i) territory
			imageUrlString = image.absUrl("src");
			/*  
			//requires function passed image id name, not image url can get smaller images. Maybe nice for V2.0 of the project
			imageUrlText = image.absUrl("src");
			for(int i = INDEX_MEDIUM_IMAGE + 1; !imageUrlText.contains(imageName) && i < doc.select("img").size(); i++) { //check we didn't run out of links
				image = doc.select("img").get(i);
				imageUrlText = image.absUrl("src");
				//System.out.println("Replacing image selection: " + imageUrlText);
			}
			*/
			//System.out.println(imageUrlText);
			imageCache = ImageIO.read(new URL(imageUrlString));
		} 
		catch (IndexOutOfBoundsException e) { //will not be triggered while using doc.select("img").first(); implementation. If changed, should discuss how to handle exceptions
			throw new IOException(e);
		}
		return imageCache;
	}

	@Override
	public URL getImageURL() {
		return IMAGE_URL;
	}

	@Override
	public String getArticleTitle() {
		return ARTICLE_TITLE;
	}

	@Override
	public String getArticleAbstract() {
		return ARTICLE_ABSTRACT;
	}

	@Override
	public URL getArticleURL() {
		return ARTICLE_URL;
	}
}
