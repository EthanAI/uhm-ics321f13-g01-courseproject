package edu.hawaii.ics321f13.view.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class SizeNormalizationImageTransformer extends AbstractImageTransformer {
	
	private final int WIDTH;
	private final int HEIGHT;
	
	public SizeNormalizationImageTransformer(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}
	
	/*
	 * Change the image size and style
	 * @see edu.hawaii.ics321f13.view.interfaces.ImageTransformer#createComposite(java.awt.image.BufferedImage)
	 */
	public BufferedImage createComposite(BufferedImage source) {
		int width = source.getWidth();
		int height = source.getHeight();
		if(width > WIDTH) {
			width = WIDTH;
			height = (width * source.getHeight()) / source.getWidth();
		}
		if(height > HEIGHT) {
			height = HEIGHT;
			width = (height * source.getWidth()) / source.getHeight();
		}
		Image scaled = source.getScaledInstance(width,	height, BufferedImage.SCALE_SMOOTH);
		BufferedImage bufScaled = 
				new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bufScaled.getGraphics().drawImage(scaled, 0, 0, null);
		return bufScaled;
	}
	/*
	 * 
	 * @see edu.hawaii.ics321f13.view.impl.AbstractImageTransformer#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if(other instanceof SizeNormalizationImageTransformer) {
			return super.equals(other) && ((SizeNormalizationImageTransformer) other).WIDTH == WIDTH 
					&& ((SizeNormalizationImageTransformer) other).HEIGHT == HEIGHT;
		} else {
			return super.equals(other);
		}
	}

}
