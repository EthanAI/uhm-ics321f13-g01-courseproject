package edu.hawaii.ics321f13.view.impl;

import java.awt.image.BufferedImage;
import java.util.Objects;

import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;

public class AggregateImageTransformer implements ImageTransformer {
	
	private final ImageTransformer[] XFORMS;
	
	public AggregateImageTransformer(ImageTransformer...transformers) {
		XFORMS = Objects.requireNonNull(transformers);
	}
	
	@Override
	public BufferedImage createComposite(BufferedImage source) {
		BufferedImage srcRef = source;
		for(int i = 0; i < XFORMS.length; i++) {
			if(XFORMS[i] != null) {
				srcRef = XFORMS[i].createComposite(srcRef);
			}
		}
		return srcRef;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof AggregateImageTransformer) {
			AggregateImageTransformer aitOther = (AggregateImageTransformer) other;
			if(aitOther.XFORMS.length == XFORMS.length) {
				for(int i = 0; i < XFORMS.length; i++) {
					if((XFORMS[i] == null && aitOther.XFORMS[i] != null) 
							|| (XFORMS[i] != null && aitOther.XFORMS[i] == null) 
							|| (XFORMS[i] != null && aitOther.XFORMS[i] != null && !XFORMS[i].equals(aitOther.XFORMS[i]))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else if(XFORMS.length == 1 && other instanceof ImageTransformer) {
			return XFORMS[0].equals(other);
			
		} else {
			return false;
		}
	}
	
}
