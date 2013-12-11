package edu.hawaii.ics321f13.view.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.UIManager;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;

public class ErrorImageResult implements ImageResult {
	
	private static final String ERROR_ICON_KEY = "OptionPane.errorIcon";
	private static final BufferedImage ERROR_ICON;
	
	private final Exception ERROR;
	
	static {
		Icon errorIcon = UIManager.getIcon(ERROR_ICON_KEY);
		BufferedImage errorIconImg = 
				new BufferedImage(errorIcon.getIconWidth(), errorIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D iconImgGfx = (Graphics2D) errorIconImg.getGraphics();
		errorIcon.paintIcon(null, iconImgGfx, 0, 0);
		iconImgGfx.dispose();
		ERROR_ICON = errorIconImg;
	}
	
	public ErrorImageResult(Exception error) {
		ERROR = error;
	}
	
	@Override
	public void close() throws IOException {
		// Do nothing. There is nothing to close.
	}

	@Override
	public BufferedImage getImage(ImageTransformer... transformers)
			throws IOException {
		// TODO Auto-generated method stub
		return ERROR_ICON;
	}

	@Override
	public BufferedImage getImage(Dimension targetSize,
			ImageTransformer... transformers) throws IOException {
		return ERROR_ICON;
	}

	@Override
	public URL getImageURL() {
		return null;
	}

	@Override
	public URL getImageURL(Dimension targetSize) {
		return null;
	}

	@Override
	public String getArticleTitle() {
		return "Error";
	}

	@Override
	public String getArticleAbstract() {
		return ERROR.getMessage();
	}

	@Override
	public URL getArticleURL() {
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		// Dnd is not supported by the empty results page.
		return new DataFlavor[0];
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		// Dnd is not supported by the empty results page.
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		// Dnd is not supported by the empty results page.
		throw new UnsupportedFlavorException(flavor);
	}

}
