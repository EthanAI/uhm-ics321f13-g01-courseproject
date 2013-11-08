package edu.hawaii.ics321f13.model.impl;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;

public class DefaultImageResult implements ImageResult {
	
	private final int INDEX_MEDIUM_IMAGE = 0;
	
	private final URL IMAGE_URL;
	private final URL ARTICLE_URL;
	private final String ARTICLE_TITLE;
	private final String ARTICLE_ABSTRACT;
	
	private BufferedImage imageCache = null;
	private Dimension imageCacheOriginalSize = null;
	private ImageTransformer[] xformsCache = null;
	private ImageReference[] availableImages = null;
	
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
		xformsCache = null;
	}
	
	/**
	 * Downloads and returns the relevant image from the URL of the wikicommons webpage we found using the user's 
	 * search term <code>IMAGE_URL</code>. Operates on the  
	 * 
	 * @return A <code>BufferedImage</code> containing the image.
	 */
	@Override
	public BufferedImage getImage(ImageTransformer... transformers) throws IOException {
		return getImage(null, transformers);
	}

	@Override
	public BufferedImage getImage(Dimension targetSize,	ImageTransformer... transformers) throws IOException {
		// If we have not done so already, load all available images.
		if(availableImages == null) {
			availableImages = loadAvailableImages();
			if(availableImages.length <= 0) {
				throw new FileNotFoundException("no images found at the specified URL");
			}
		}
		// Determine whether or not we need to refresh the image cache.
		if(imageCache == null || !imageCacheOriginalSize.equals(findNearestAvailableSize(targetSize).getImageSize())) {
			imageCache = findNearestAvailableSize(targetSize).getImage();
			imageCacheOriginalSize = new Dimension(imageCache.getWidth(), imageCache.getHeight());
			xformsCache = null;
		} 
		if(!xformsDeepEquals(xformsCache, transformers)) {
			imageCache = createComposite(imageCache, transformers);
			xformsCache = transformers;
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

	@Override
	public URL getImageURL(Dimension targetSize) {
		return findNearestAvailableSize(targetSize).getImageURL();
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
	@Override
	public URL getArticleURL() {
		return ARTICLE_URL;
	}

	@Override
	public boolean isLoaded() {
		return imageCache != null;
	}
	
	private boolean xformsDeepEquals(ImageTransformer[] xforms1, ImageTransformer[] xforms2) {
		if(xforms1 == null && xforms2 == null) {
			return true;
		} else if((xforms1 == null && xforms2 != null)
				|| xforms1 != null && xforms2 == null
				|| xforms1.length != xforms2.length) {
			return false;
		} else {
			for(int i = 0; i < xforms1.length; i++) {
				if((xforms1[i] == null && xforms2[i] != null) 
						|| (xforms1[i] != null && xforms2[i] == null) 
						|| (xforms1[i] != null && xforms2[i] != null && !xforms1[i].equals(xforms2[i]))) {
					return false;
				}
			}
			return true;
		}
	}
	
	private BufferedImage createComposite(BufferedImage source, ImageTransformer...transformers) {
		BufferedImage copy = cloneImage(source);
		for(int i = 0; i < transformers.length; i++) {
			copy = transformers[i].createComposite(copy);
		}
		return copy;
	}
	
	private BufferedImage cloneImage(BufferedImage source) {
		ColorModel colorModel = source.getColorModel();
		boolean premultipliedAlpha = source.isAlphaPremultiplied();
		WritableRaster raster = source.copyData(null);
		return new BufferedImage(colorModel, raster, premultipliedAlpha, null);
	}
	
	private ImageReference findNearestAvailableSize(Dimension targetSize) {
		// TODO Implement ordering of the ImageReference objects by area and then using binary search.
		// Parameter validation.
		Objects.requireNonNull(availableImages);
		if(availableImages.length <= 0 || availableImages[0] == null) {
			throw new NoSuchElementException();
		}
		// Linear search for nearest size.
		int targetArea = (targetSize == null ? Integer.MAX_VALUE : targetSize.width * targetSize.height);
		int nearestIdx = 0;
		int nearestAreaDiff = Math.abs(
				targetArea - (availableImages[0].getImageSize().width * availableImages[0].getImageSize().height));
		for(int i = 0; i < availableImages.length; i++) {
			int currentAreaDiff = Math.abs(
					targetArea - (availableImages[i].getImageSize().width * availableImages[i].getImageSize().height));
			if(currentAreaDiff < nearestAreaDiff) {
				nearestIdx = i;
				nearestAreaDiff = currentAreaDiff;
			}
		}
		return availableImages[nearestIdx];
	}
	
	private ImageReference[] loadAvailableImages() throws IOException {
		try {
			//Jsoup closes its connection after it downloads the data.
			Document doc = Jsoup.connect(getImageURL().toString()).get();
			// Possible to get other sizes of the image by venturing into .get(i) territory.
			Element image = doc.select("img").get(INDEX_MEDIUM_IMAGE); 
			String imageUrlString = image.absUrl("src");
			/*  
			//requires function passed image id name, not image url can get smaller images. Maybe nice for V2.0 of the project
			imageUrlText = image.absUrl("src");
			for(int i = INDEX_MEDIUM_IMAGE + 1; !imageUrlText.contains(imageName) && i < doc.select("img").size(); i++) { 
				//check we didn't run out of links
				image = doc.select("img").get(i);
				imageUrlText = image.absUrl("src");
			}
			*/
			// TODO Get the image URL and image dimensions for all image sizes from the HTML (no downloading).
			URL imageUrl = new URL(imageUrlString);
			if(imageCache == null) {
				// XXX Load image here to get dimensions before we implement loading of dimensions from HTML.
				imageCache = ImageIO.read(imageUrl);
				imageCacheOriginalSize = new Dimension(imageCache.getWidth(), imageCache.getHeight());
				xformsCache = null;
				
			}
			return new ImageReference[] {
					new ImageReference(imageUrl, new Dimension(imageCache.getWidth(), imageCache.getHeight()))
			};
		} 
		catch (IndexOutOfBoundsException e) { //will not be triggered while using doc.select("img").first(); implementation. If changed, should discuss how to handle exceptions
			throw new IOException(e);
		}
	}
	
	private class ImageReference {
		
		private final URL IMAGE_URL;
		private final Dimension IMAGE_SIZE;
		
		public ImageReference(URL imageUrl, Dimension imageSize) {
			IMAGE_URL = Objects.requireNonNull(imageUrl);
			IMAGE_SIZE = Objects.requireNonNull(imageSize);
		}
		
		public BufferedImage getImage() throws IOException {
			return ImageIO.read(getImageURL());
		}
		
		public URL getImageURL() {
			return IMAGE_URL;
		}
		
		public Dimension getImageSize() {
			return IMAGE_SIZE;
		}
		
	}
}
