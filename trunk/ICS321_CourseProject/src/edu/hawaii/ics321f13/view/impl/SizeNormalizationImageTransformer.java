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
	
	@Override
	public BufferedImage createComposite(BufferedImage source) {
		int width, height;
		if(WIDTH != HEIGHT) {
			width = (WIDTH < HEIGHT ? WIDTH : -1);
			height = (WIDTH < HEIGHT ? -1 : HEIGHT);
		} else {
			System.out.println("Square");
			width = (source.getWidth() <= source.getHeight() ? WIDTH : -1);
			height = (source.getWidth() <= source.getHeight() ? -1 : HEIGHT);
		}
		Image scaled = source.getScaledInstance(width,	height, BufferedImage.SCALE_SMOOTH);
		BufferedImage bufScaled = 
				new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bufScaled.getGraphics().drawImage(scaled, 0, 0, null);
		return bufScaled;
	}
	
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
