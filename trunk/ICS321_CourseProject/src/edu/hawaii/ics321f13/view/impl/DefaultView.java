package edu.hawaii.ics321f13.view.impl;

import java.awt.event.ActionListener;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;
import edu.hawaii.ics321f13.view.interfaces.View;

public class DefaultView implements View {
	
	protected boolean isDirty = false;
	
	
	@Override
	public void addActionListener(ActionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setImageSource(Traversable<ImageResult> source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBusy(boolean busy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addImageTransformer(ImageTransformer transformer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addImageTransformer(ImageTransformer transformer, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int removeImageTransformer(ImageTransformer transformer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ImageTransformer removeImageTransformer(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearImageTransformers() {
		// TODO Auto-generated method stub
		
	}

}
