package edu.hawaii.ics321f13.view.impl;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
	
	private Image activeImage = null;
	private boolean isCloseIconVisible = false;
	private final double CLOSE_ICON_SCALE = 0.03;
	private final double CLOSE_ICON_INSET_SCALE = 0.055;
	
	public ImagePreviewLayer() {
		super();
		setAlphaMax(1.0f);
	}
	
	public void setImage(Image image) {
		activeImage = image;
	}
	
	public Image getImage() {
		return activeImage;
	}
	
	public boolean isCloseIconVisible() {
		return isCloseIconVisible;
	}
	
	public void setCloseIconVisible(boolean visible) {
		isCloseIconVisible = visible;
	}
	
	@Override
	public void paintLayer(Graphics g, JComponent c) {
		if(activeImage == null) {
			return;
		}
		// Draw the image.
		Point imageLoc = new Point(c.getWidth() / 2, c.getHeight() / 2);
		imageLoc.x -= activeImage.getWidth(null) / 2;
		imageLoc.y -= activeImage.getHeight(null) / 2;
 		g.drawImage(activeImage, imageLoc.x, imageLoc.y, null);
 		// Draw the close icon.
 		if(isCloseIconVisible) {
 			int scale = Math.min(c.getWidth(), c.getHeight());
 	 		g.setColor(Color.WHITE);
 	 		g.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 
 	 				(int) (scale * CLOSE_ICON_SCALE)));
 	 		g.drawString("\u2716", c.getWidth() - (int) (scale * CLOSE_ICON_INSET_SCALE), 
 	 				(int) (scale * CLOSE_ICON_INSET_SCALE));
 		}
	}

}
