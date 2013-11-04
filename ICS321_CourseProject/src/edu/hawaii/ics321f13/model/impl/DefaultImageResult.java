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
	
	/**
	 * Simplified constructor for DefaultImageResult class
	 * 
	 * @param articleTitle
	 * @param imageURL
	 */
	public DefaultImageResult(String articleTitle, URL imageURL) {
		this(articleTitle, imageURL, null, null);
	}
	
	/**
	 * Constructor for DefaultImageResult class where articleURL and article Abstract are supplied
	 * 
	 * @param articleTitle
	 * @param imageURL
	 * @param articleURL
	 * @param articleAbstract
	 */
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
	
	/*
	 * Downloads and returns the relevant image from the URL of the wikicommons webpage we found using the user's search term <code>IMAGE_URL</code>. 
	 * Operates on the  
	 * 
	 * @return A <code>BufferedImage</code> containing the image.
	 */
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

	/**
	 * Accessor method to get the URL of the wikicommons page containing the image we want <code>IMAGE_URL</code>
	 */
	@Override
	public URL getImageURL() {
		return IMAGE_URL;
	}

	/**
	 * Accessor to get the title of the article <code>ARTICLE_TITLE</code>
	 */
	@Override
	public String getArticleTitle() {
		return ARTICLE_TITLE;
	}

	/**
	 * Accessor to get the abstract of the article <code>ARTICLE_ABSTRACT</code>
	 */
	@Override
	public String getArticleAbstract() {
		return ARTICLE_ABSTRACT;
	}

	/**
	 * Accessor to get the URL where the URL of the article where we are finding a picture <code>ARTICLE_URL</code>
	 */
	//TODO arent we going to have multiple URLS from one search term, and multiple images from one Article?
	@Override
	public URL getArticleURL() {
		return ARTICLE_URL;
	}

	@Override
	public boolean isLoaded() {
		return imageCache != null;
	}
}
