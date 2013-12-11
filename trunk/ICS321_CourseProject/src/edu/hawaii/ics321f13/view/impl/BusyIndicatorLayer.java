package edu.hawaii.ics321f13.view.impl;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class BusyIndicatorLayer extends AbstractRenderableLayer {
	
	public enum Propery {INDICATOR_COLOR, TEXT_COLOR, TEXT_FONT, TEXT}
	
	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	
	private final double INDICATOR_SCALE = 1.0 / 15.0;
	private final double INDICATOR_VERT_OFFSET_SCALE = 0.0;
	private final double LINE1_TEXT_SCALE = 0.05;
	private final double LINE1_VERT_OFFSET_SCALE = 0.25;
	private final double LINE2_TEXT_SCALE = 1.0 / 30.0;
	private final double LINE2_VERT_OFFSET_SCALE = 1.0 / 3.0;
	
	private Color indicatorColor = Color.WHITE;
	private Color textColor = Color.WHITE;
	private String textFont = "Segoe UI";
	private String text = "Please wait...";
	private String infoText = "";
	
	private int angle = 0;
	
	public BusyIndicatorLayer() {
		super();
		setAlphaMax(1.0f);
	}
	
	@Override
	public boolean update() {
		super.update();
		angle += 3;
		if (angle >= 360) {
			angle = 0;
		}
		return true;
	}
	
	@Override
	public void paintLayer(Graphics g, JComponent c) {
		Dimension size = getBaseRenderer().getSize();
		Point center = new Point(size.width / 2 , size.height / 2);
		int scale = Math.min(size.width, size.height);

		Graphics2D g2 = (Graphics2D) g.create();
		// Set up the graphics context.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Prep the graphics context to paint text.
		g2.setFont(new Font(textFont, Font.BOLD, (int) (scale * LINE1_TEXT_SCALE)));
		g2.setColor(textColor);
		// Paint the "Please wait..." text.
		int textWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), text);
		Point waitTextLoc = new Point(center.x - (textWidth / 2), center.y + (int) (scale * LINE1_VERT_OFFSET_SCALE));
		g2.drawString(text, waitTextLoc.x, waitTextLoc.y);
		// Paint info text.
		g2.setFont(new Font(textFont, Font.BOLD, (int) (scale * LINE2_TEXT_SCALE)));
		textWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), infoText);
		Point infoTextLoc = new Point(center.x - (textWidth / 2), center.y + (int) (scale * LINE2_VERT_OFFSET_SCALE));
		g2.drawString(infoText, infoTextLoc.x, infoTextLoc.y);
		// Prepare the graphics to paint the wait indicator.
		int indicatorScale = (int) (scale * INDICATOR_SCALE);
		g2.setStroke(new BasicStroke(indicatorScale / 4, BasicStroke.CAP_ROUND,	BasicStroke.JOIN_ROUND));
		g2.setPaint(indicatorColor);
		// Paint the wait indicator.
		g2.rotate(Math.PI * angle / 180, center.x, center.y);
		for (int i = 0; i < 12; i++) {
			float strokeScale = (11.0f - (float) i) / 11.0f;
			g2.drawLine(center.x + indicatorScale, center.y + (int) (scale * INDICATOR_VERT_OFFSET_SCALE), 
					center.x + indicatorScale * 2, center.y + (int) (scale * INDICATOR_VERT_OFFSET_SCALE));
			g2.rotate(-Math.PI / 6, center.x, center.y);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,	strokeScale * getAlpha()));
		}

		g2.dispose();
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String line1) {
		text = (line1 == null ? "" : line1);
	}
	
	public String getInfoText() {
		return infoText;
	}
	
	public void setInfoText(String line2) {
		infoText = (line2 == null ? "" : line2);
	}
	
	public String getTextFont() {
		return textFont;
	}
	
	public void setTextFont(String fontName) {
		textFont = Objects.requireNonNull(fontName);
	}
	
	public Color getIndicatorColor() {
		return indicatorColor;
	}
	
	public void setIndicatorColor(Color color) {
		indicatorColor = Objects.requireNonNull(color);
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color color) {
		textColor = Objects.requireNonNull(color);
	}

}
