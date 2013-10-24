package edu.hawaii.ics321f13.view.impl;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.Timer;

public class BusyGlassPane extends JComponent {
	
	private final String WAIT_TEXT = "Loading images...";
	
	private Component parent;
	
	private boolean isRunning;
	private boolean isFadingOut;
	private Timer animationTimer;
	private final ActionListener animationTask;
	private int frameRate = 24;
	private String infoText = "";
	private String textFont = null;
	private Color textColor = Color.WHITE;
	 
	private int angle;
	private int fadeCount;
	private int fadeLimit = 15;

	/** Serialization support.*/
	private static final long serialVersionUID = 1L;
	
	public BusyGlassPane(Component parent) {
		if(parent == null) {
			throw new NullPointerException("parent must not be null");
		}
		this.parent = parent;
		// Set the default font.
		textFont = "Segoe UI";
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				evt.consume();
			}
			
			public void mousePressed(MouseEvent evt) {
				evt.consume();
			}
			
			public void mouseReleased(MouseEvent evt) {
				evt.consume();
			}
		});
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				evt.consume();
			}
		});
		
		animationTask = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (isRunning) {
					firePropertyChange("tick", 0, 1);
					angle += 3;
					if (angle >= 360) {
						angle = 0;
					}
					if (isFadingOut) {
						if (--fadeCount <= 0) {
							isRunning = false;
							animationTimer.stop();
							setVisible(false);
						}
					} else if (fadeCount < fadeLimit) {
						fadeCount++;
					}
					repaint();
				}
			}
			
		};
	}
	
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		// TODO Set this up so it only becomes visible after one second and ONLY if the glass pane has not yet been canceled.
//		if(visible) {
//			if(isVisible() || visibleOrWaiting) {
//				return;
//			} else {
//				final BusyGlassPane THIS = this;
//				ConcurrencyManager.scheduleOnce(new TimerTask() {
//
//					@Override
//					public void run() {
//						if(THIS.visibleOrWaiting) {
//							THIS.setVisible(visible, true);
//						}
//					}
//					
//				}, 1000, true);
//			}
//		} else {
//			visibleOrWaiting = false;
//			super.setVisible(visible);
//		}
//		visibleOrWaiting = visible;
	}
	
	@SuppressWarnings("unused")
	private void setVisible(boolean visible, boolean force) {
		if(force) {
			super.setVisible(visible);
		} else {
			setVisible(visible);
		}
	}
	
	public boolean isVisible() {
		return super.isVisible();
	}
	
	public void setText(String text) {
		if(text == null) {
			infoText = "";
		} else {
			infoText = text;
		}
	}
	
	public void setFont(String fontName) {
		textFont = Objects.requireNonNull(fontName);
	}
	
	public void setTextColor(Color textColor) {
		this.textColor = Objects.requireNonNull(textColor);
	}
	
	public void setFrameRate(int frameRate) {
		if(frameRate > 0) {
			this.frameRate = frameRate;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public void start() {
	    if (isRunning) {
	      return;
	    }
	    setVisible(true);
	    // Run a thread for animation.
	    isRunning = true;
	    isFadingOut = false;
	    fadeCount = 0;
	    int tick = 1000 / frameRate;
	    animationTimer = new Timer(tick, animationTask);
	    animationTimer.start();
	}
	 
	public void stop() {
		isFadingOut = true;
	}
	
	@Override
	public void paint (Graphics g) {
	    // Paint the view.
	    super.paint (g);
	    
		if(parent == null) {
			// If we don't have a parent at this time, just return.
			return;
		}
	    int w = parent.getWidth();
	    int h = parent.getHeight();
	    int scale = Math.min(w, h);
	 
	    if (!isRunning) {
	      return;
	    }
	 
	    Graphics2D g2 = (Graphics2D)g.create();
	 
	    float fade = (float)fadeCount / (float)fadeLimit;
	    // Gray it out.
	    Composite urComposite = g2.getComposite();
	    g2.setComposite(AlphaComposite.getInstance(
	    		AlphaComposite.SRC_OVER, .5f * fade));
	    g2.fillRect(0, 0, w, h);
	    g2.setComposite(urComposite);
	    
	    // Set up the graphics context.
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	    // Prep the graphics context to paint text.
	    g2.setFont(new Font(textFont, Font.BOLD, scale/20));
	    g2.setColor(textColor);
	    FontMetrics metrics = g2.getFontMetrics();
	    // Paint the "Please Wait..." text.
	    int textWidth = metrics.stringWidth(WAIT_TEXT);
	    int waitTextX = (w/2) - (textWidth/2);
	    int waitTextY = (h/2) + (scale/4);
	    g2.drawString(WAIT_TEXT, waitTextX, waitTextY);
	    // Paint info text.
	    g2.setFont(new Font(textFont, Font.BOLD, scale/30));
	    metrics = g2.getFontMetrics();
	    textWidth = metrics.stringWidth(infoText);
	    int infoTextX = (w/2) - (textWidth/2);
	    int infoTextY = (h/2) + (scale/3);
	    g2.drawString(infoText, infoTextX, infoTextY);
	    // Paint the wait indicator.
	    int indicatorScale = scale / 15;
	    int centerX = w / 2;
	    int centerY = h / 2;
	    g2.setStroke(new BasicStroke(indicatorScale / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	    g2.setPaint(Color.white);
	    g2.rotate(Math.PI * angle / 180, centerX, centerY);
	    for (int i = 0; i < 12; i++) {
	    	float strokeScale = (11.0f - (float)i) / 11.0f;
	    	g2.drawLine(centerX + indicatorScale, centerY, centerX + indicatorScale * 2, centerY);
	    	g2.rotate(-Math.PI / 6, centerX, centerY);
	    	g2.setComposite(AlphaComposite.getInstance(
	        AlphaComposite.SRC_OVER, strokeScale * fade));
	    }
	    
	    g2.dispose();
	}
	
}
