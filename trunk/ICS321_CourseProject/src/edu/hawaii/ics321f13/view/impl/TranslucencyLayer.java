package edu.hawaii.ics321f13.view.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class TranslucencyLayer extends AbstractRenderableLayer {

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	
	private final Color FILL_COLOR;
	
	public TranslucencyLayer(Color fillColor) {
		super();
		FILL_COLOR = fillColor;
	}
	
	@Override
	public void paintLayer(Graphics g, JComponent c) {
		// Use provided compositing mode. 
		Graphics2D g2D = (Graphics2D)g.create();
		g2D.setColor(FILL_COLOR);
	    g2D.fillRect(0, 0, c.getWidth(), c.getHeight());
	}

}
