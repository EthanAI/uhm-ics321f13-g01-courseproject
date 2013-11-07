package edu.hawaii.ics321f13.view.impl;

import java.awt.image.BufferedImage;

import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;

public abstract class AbstractImageTransformer implements ImageTransformer {
	
	@Override
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		} else {
			return getClass().getName().equals(other.getClass().getName());
		}
	}

}
