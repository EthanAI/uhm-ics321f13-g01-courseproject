package edu.hawaii.ics321f13.view.impl;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;

public class ImagePreviewLayer extends AbstractRenderableLayer {

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	
	public Image activeImage = null;
	
	public ImagePreviewLayer() {
		super();
		setAlphaMax(1.0f);
	}
	
	public void setImage(Image image) {
		activeImage = image;
	}
	
	@Override
	public void paintLayer(Graphics g, JComponent c) {
		if(activeImage == null) {
			return;
		}
		Point imageLoc = new Point(c.getWidth() / 2, c.getHeight() / 2);
		imageLoc.x -= activeImage.getWidth(null) / 2;
		imageLoc.y -= activeImage.getHeight(null) / 2;
 		g.drawImage(activeImage, imageLoc.x, imageLoc.y, null);
	}

}
