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
		Image scaled = source.getScaledInstance((source.getWidth() >= source.getHeight() ? WIDTH : -1), 
				(source.getWidth() >= source.getHeight() ? -1 : HEIGHT), BufferedImage.SCALE_SMOOTH);
		BufferedImage bufScaled = 
				new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bufScaled.getGraphics().drawImage(scaled, 0, 0, null);
		return bufScaled;
	}

}
