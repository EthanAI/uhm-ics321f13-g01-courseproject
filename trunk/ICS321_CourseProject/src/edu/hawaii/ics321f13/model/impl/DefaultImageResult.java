package edu.hawaii.ics321f13.model.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

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
	public BufferedImage getImage() {
		// TODO Ethan, this is where you would put the code which loads the image from the webpage (or delegate it to a method in this class...whatever way you wanna do it).
		// Just make sure you assign the imageCache variable to the BufferedImage that you get back from ImageIO and then return the imageCache.
		String imageUrlString;
		try {
			URI page = new URI(getImageURL().toString());
			Document doc = Jsoup.connect(page.toString()).get();
			
			Element image = doc.select("img").get(0); //0 will get the picture, but a little big
			imageUrlString = image.absUrl("src");
			/*  
			//requires function passed image id name, not image url can get smaller images. Maybe nice for V2.0 of the project
			imageUrlText = image.absUrl("src");
			for(int i = 1; !imageUrlText.contains(imageName) && i < doc.select("img").size(); i++) { //check we didn't run out of links
				image = doc.select("img").get(i);
				imageUrlText = image.absUrl("src");
				//System.out.println("Replacing image selection: " + imageUrlText);
			}
			*/
			//System.out.println(imageUrlText);
			imageCache = ImageIO.read(new URL(imageUrlString));
		} 
		catch (Exception e) {
			e.printStackTrace();
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
