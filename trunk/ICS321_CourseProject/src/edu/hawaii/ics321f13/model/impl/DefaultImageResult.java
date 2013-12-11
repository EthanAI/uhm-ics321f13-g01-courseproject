package edu.hawaii.ics321f13.model.impl;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;

public class DefaultImageResult implements ImageResult {
	
	private final URL IMAGE_URL;
	private final URL ARTICLE_URL;
	private final String ARTICLE_TITLE;
	private final String ARTICLE_ABSTRACT;
	
	private ImageCache cache = null;
	
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
		if(cache != null) {
			cache.close();
		}
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
		if(cache == null) {
			cache = new ImageCache();
		}
		return cache.getImage(targetSize, transformers);
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
		return cache.getImageURL(targetSize);
	}

	/**
	 * Accessor to get the title of the article <code>ARTICLE_TITLE</code>
	 */
	@Override
	public String getArticleTitle() {
		// TODO Replace with more elegant method of making string more human-readable.
		return ARTICLE_TITLE.replace('_', ' ');
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
	
	private class ImageCache implements Closeable {
		
		private final Vector<CachedImage> NO_XFORMS = new Vector<CachedImage>();
		private final Vector<CachedImage> XFORMED = new Vector<CachedImage>();
		private final ImageReference[] AVAILABLE_IMG_REFS;
		
		public ImageCache() throws IOException {
			AVAILABLE_IMG_REFS = loadAvailableImages();
		}
		
		public BufferedImage getImage(Dimension targetSize, ImageTransformer...xforms) throws IOException {
			ImageReference nearestImgSize = findNearestAvailableSize(targetSize);
			if(xforms != null && xforms.length > 0) {
				// Check if there is an image in the cache of the correct size  and with the same xforms.
				for(int i = 0; i < XFORMED.size(); i++) {
					if(nearestImgSize.getImageSize().equals(XFORMED.get(i).getImageSize()) 
							&& xformsDeepEquals(xforms, XFORMED.get(i).getXforms())) {
						return XFORMED.get(i).getImage();
					}
				}
			}
			// If we haven't returned by now, check if we have an un-transformed image of the correct size in the cache.
			BufferedImage noXforms = null;
			for(int i = 0; i < NO_XFORMS.size(); i++) {
				if(nearestImgSize.getImageSize().equals(NO_XFORMS.get(i).getImageSize())) {
					noXforms = NO_XFORMS.get(i).getImage();
				}
			}
			// If we don't load the image and cache it. 
			if(noXforms == null) {
				noXforms = nearestImgSize.getImage();
				NO_XFORMS.add(new CachedImage(noXforms, nearestImgSize.getImageSize()));
			}
			// Apply any xforms, if necessary and cache the result.
			BufferedImage xformed = null;
			if(xforms != null && xforms.length > 0) {
				xformed = createComposite(noXforms, xforms);
				XFORMED.add(new CachedImage(xformed, nearestImgSize.getImageSize(), xforms));
			} else {
				xformed = noXforms;
			}
			// Return the result.
			return xformed;
		}
		
		public URL getImageURL(Dimension targetSize) {
			return findNearestAvailableSize(targetSize).getImageURL();
		}
		
		@Override
		public void close() throws IOException {
			NO_XFORMS.clear();
			XFORMED.clear();
		}
		
		private boolean xformsDeepEquals(ImageTransformer[] xforms1, ImageTransformer[] xforms2) {
			if((xforms1 == null && xforms2 == null) 
					|| (xforms1 != null && xforms1.length == 0 && xforms2 == null) 
					|| (xforms2 != null && xforms1 == null && xforms2.length == 0)
					|| (xforms1 != null && xforms1.length == 0 && xforms2 != null && xforms2.length == 0)) {
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
			// Make a copy of the source image so that we do not alter it.
			BufferedImage copy = new BufferedImage(
					source.getColorModel(), source.copyData(null), source.isAlphaPremultiplied(), null);
			for(int i = 0; i < transformers.length; i++) {
				copy = transformers[i].createComposite(copy);
			}
			return copy;
		}
		
		private ImageReference findNearestAvailableSize(Dimension targetSize) {
			// TODO Implement ordering of the ImageReference objects by area and then using binary search.
			// Parameter validation.
			Objects.requireNonNull(AVAILABLE_IMG_REFS);
			if(AVAILABLE_IMG_REFS.length <= 0 || AVAILABLE_IMG_REFS[0] == null) {
				throw new NoSuchElementException();
			}
			// Linear search for nearest size.
			long targetArea = (targetSize == null ? Long.MAX_VALUE : targetSize.width * targetSize.height);
			int nearestIdx = 0;
			long nearestAreaDiff = Math.abs(
					targetArea - (AVAILABLE_IMG_REFS[0].getImageSize().width * AVAILABLE_IMG_REFS[0].getImageSize().height));
			for(int i = 0; i < AVAILABLE_IMG_REFS.length; i++) {
				long currentAreaDiff = Math.abs(
						targetArea - (AVAILABLE_IMG_REFS[i].getImageSize().width * AVAILABLE_IMG_REFS[i].getImageSize().height));
				if(currentAreaDiff < nearestAreaDiff) {
					nearestIdx = i;
					nearestAreaDiff = currentAreaDiff;
				}
			}
			return AVAILABLE_IMG_REFS[nearestIdx];
		}
		
		private ImageReference[] loadAvailableImages() throws IOException {
			//Jsoup closes its connection after it downloads the data.
			Document imgWebPage = Jsoup.connect(DefaultImageResult.this.getImageURL().toString()).get();
			
			Collection<ImageReference> images = new LinkedHashSet<ImageReference>();
			images.addAll(loadReferencesByTag(imgWebPage, "img", "src"));
			images.addAll(loadReferencesByTag(imgWebPage, "a[href]", "href"));
			if(images.isEmpty()) {
				Element fallbackImgElmnt = imgWebPage.select("img").first();
				if(fallbackImgElmnt != null) {
					URL fallbackImgURL = null;
					try {
						fallbackImgURL = new URL(fallbackImgElmnt.absUrl("src"));
						Dimension fallbackImgSize = new Dimension();
						try {
							fallbackImgSize.width = Integer.parseInt(fallbackImgElmnt.attr("width").replace(",", "").trim());
							fallbackImgSize.height = Integer.parseInt(fallbackImgElmnt.attr("height").replace(",", "").trim());
						} catch(NumberFormatException e) {
							fallbackImgSize.width = Integer.MAX_VALUE;
							fallbackImgSize.height = Integer.MAX_VALUE;
						}
						images.add(new ImageReference(fallbackImgURL, fallbackImgSize));
					} catch(MalformedURLException e) {
						System.out.printf("Fallback image selection failed(%s): %s%n", 
								getArticleTitle(), (getImageURL(null) != null 
								? getImageURL(null).toString() : "unable to fetch image URL"));
					} 
				} else {
					System.out.printf("Fallback image selection failed(%s): %s%n", 
							getArticleTitle(), (getImageURL(null) != null 
							? getImageURL(null).toString() : "unable to fetch image URL"));
				}
			}
			return images.toArray(new ImageReference[images.size()]);
		}
		
		private Collection<ImageReference> loadReferencesByTag(Document page, String htmlTag, String urlAttribute) {
			Collection<ImageReference> images = new LinkedHashSet<ImageReference>();
			String[] supportedImgFormats = ImageIO.getReaderFileSuffixes();
			String whDelimiter = "×";
			String urlFilePrefix = "File:";
			String unescFilename = StringEscapeUtils.unescapeHtml4(IMAGE_URL.getFile().substring(
					IMAGE_URL.getFile().indexOf(urlFilePrefix) + urlFilePrefix.length()));
			// Remove the filename extension to allow other image formats to be returned.
			if(unescFilename.contains(".")) {
				unescFilename = unescFilename.substring(0, unescFilename.lastIndexOf("."));
			}
			String unescFilenameAlt = unescFilename.replace("_", " ");
			Elements imgElements = page.select(htmlTag);
			for(Element img : imgElements) {
				String unescImgHtml = StringEscapeUtils.unescapeHtml4(img.toString());
				// Verify that we want the image.
				if((!StringUtils.containsIgnoreCase(unescImgHtml, unescFilename) 
						&& !StringUtils.containsIgnoreCase(unescImgHtml, unescFilenameAlt)) 
						|| !(StringUtils.containsIgnoreCase(unescImgHtml, "pixels") 
								|| (img.hasAttr("width") && img.hasAttr("height")))) {
					continue;
				}
				// Now that we know that we do want the image, retreive the URL.
				String imgAbsUrlStr = img.absUrl(urlAttribute);
				URL imgAbsURL = null;
				// Validate the URL.
				if(imgAbsUrlStr == null || imgAbsUrlStr.isEmpty()) {
					continue;
				} else {
					try {
						imgAbsURL = new URL(imgAbsUrlStr);
					} catch(MalformedURLException e) {
						continue;	// Skip invalid URLs.
					}
				}
				// Now we can assume that we have a valid image URL. Move on to retreiving the image height and width.
				Dimension imgSize = null;
				if(unescImgHtml.contains("pixels")) {
					imgSize = getDimensionsNearDelimiter(unescImgHtml, whDelimiter);
				} else {
					try {
						imgSize = new Dimension();
						imgSize.width = Integer.parseInt(img.attr("width").replace("", "").trim());
						imgSize.height = Integer.parseInt(img.attr("height").replace("", "").trim());
					} catch(NumberFormatException e) {
						continue;
					}
				}
				// Validate all paramters and create the ImageReference object.
				if(imgSize != null && imgSize.width > 0 && imgSize.height > 0 && imgAbsURL != null) {
					// Ignore unsupported image formats.
					String filenameExt = (imgAbsURL.getFile().contains(".") ? 
							imgAbsURL.getFile().substring(imgAbsURL.getFile().lastIndexOf(".") + 1) : "");
					boolean isFormatSupported = false;
					for(String supportedExt : supportedImgFormats) {
						if(filenameExt.equalsIgnoreCase(supportedExt)) {
							isFormatSupported = true;
							break;
						}
					}
					if(isFormatSupported) {
						images.add(new ImageReference(imgAbsURL, imgSize));
					}			}
			}
			return images;
		}
		
		private Dimension getDimensionsNearDelimiter(String input, String delimiter) {
			// Parameter validation.
			if(!Objects.requireNonNull(input).contains(Objects.requireNonNull(delimiter))) {
				throw new IllegalArgumentException(String.format(
						"delimiter '%s' must be substring of input string: %s", delimiter, input));
			}
			char[] inputAry = input.toCharArray();
			int delimiterIdx = input.indexOf(delimiter);
			StringBuilder strBuf = new StringBuilder();
			boolean isReading = false;
			Dimension rtnDim = new Dimension();
			// Find the width.
			for(int i = delimiterIdx; i >= 0; i--) {
				if(Character.isDigit(inputAry[i])) {
					isReading = true;
					strBuf.insert(0, inputAry[i]);
				} else if(isReading && inputAry[i] != ',') {
					// If we are currently reading and encounter a non-comma/digit character, stop reading.
					break;
				}
			}
			try {
				rtnDim.width = Integer.parseInt(strBuf.toString().trim());
			} catch(NumberFormatException e) {
				rtnDim.width = -1;
			}
			// Reset the variables before reading the height.
			isReading = false;
			strBuf = new StringBuilder();
			// Find the height.
			for(int i = delimiterIdx + delimiter.length(); i < inputAry.length; i++) {
				if(Character.isDigit(inputAry[i])) {
					isReading = true;
					strBuf.append(inputAry[i]);
				} else if(isReading && inputAry[i] != ',') {
					// If we are currently reading and encounter a non-comma/digit character, stop reading.
					break;
				}
			}
			try {
				rtnDim.height = Integer.parseInt(strBuf.toString().trim());
			} catch(NumberFormatException e) {
				rtnDim.height = -1;
			}
			return rtnDim;
		}
		
		private class CachedImage {
			
			private final BufferedImage IMG_CACHE;
			private final ImageTransformer[] XFORMS;
			private final Dimension IMG_SIZE;
			
			public CachedImage(BufferedImage imgCache, Dimension originalImgSize, ImageTransformer...xforms) {
				IMG_CACHE = Objects.requireNonNull(imgCache);
				XFORMS = (xforms == null ? new ImageTransformer[0] : xforms);
				IMG_SIZE = Objects.requireNonNull(originalImgSize);
			}
			
			public BufferedImage getImage() {
				return IMG_CACHE;
			}
			
			public ImageTransformer[] getXforms() {
				return XFORMS;
			}
			
			public Dimension getImageSize() {
				return IMG_SIZE;
			}
			
		}
		
		private class ImageReference implements Comparable<ImageReference> {
			
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
			
			/**
			 * Compares <i>only</i> the size of this <code>ImageReference</code> against the <code>Dimension</code>
			 * returned by <code>((ImageResult) other).getImageSize()</code> (that is, if <code>other</code> is an instance of
			 * <code>ImageReference</code>). This is because, for the purposes of image loading, since the sizes of the
			 * image files are unknown, two image files with the same dimensions are equivalent, in terms of the time
			 * required for them to download.
			 * 
			 * If <code>other</code> is <code>null</code> or otherwise <i>not</i> an instance of 
			 * <code>ImageReference</code>, <code>false</code> is returned.
			 * 
			 * @return <code>true</code> if the two objects are equivalent as defined above, <code>false</code> otherwise.
			 */
			@Override
			public boolean equals(Object other) {
				if(other instanceof ImageReference) {
					return ((ImageReference) other).getImageSize().equals(getImageSize());
				} else {
					return false;	// Note: instanceof checks for null pointer.
				}
			}
			
			/**
			 * Compares <i>only</i> the size of this <code>ImageReference</code> against the <code>Dimension</code>
			 * returned by <code>((ImageResult) other).getImageSize()</code> (that is, if <code>other</code> is an instance of
			 * <code>ImageReference</code>). This is because, for the purposes of image loading, since the sizes of the
			 * image files are unknown, two image files with the same dimensions are equivalent, in terms of the time
			 * required for them to download.
			 * 
			 * If <code>other</code> is <code>null</code> or otherwise <i>not</i> an instance of 
			 * <code>ImageReference</code>, <code>false</code> is returned.
			 * 
			 * @param other - the <code>ImageResult</code> against which this object is being compared.
			 */
			@Override
			public int compareTo(ImageReference other) {
				if(other == null) {
					return 1; // Current object is always greater than null reference.
				}
				long area = IMAGE_SIZE.width * IMAGE_SIZE.height;
				long otherArea = other.getImageSize().width * other.getImageSize().height;
				long areaDiff = area - otherArea;
				return (areaDiff > Integer.MAX_VALUE ? Integer.MAX_VALUE : 
					(areaDiff < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) areaDiff));
			}
		}
		
	}
}
