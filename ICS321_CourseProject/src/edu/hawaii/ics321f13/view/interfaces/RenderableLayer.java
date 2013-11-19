package edu.hawaii.ics321f13.view.interfaces;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;

public interface RenderableLayer {
	
	boolean isVisible();

	void setVisible(boolean visible);
	
	boolean isInputConsumed();
	
	void setInputConsumed(boolean inputConsumed);
	
	float getAlpha();
	
	RenderableLayer getChild();

	void setChild(RenderableLayer child);
	
	JComponent getBaseRenderer();
	
	void setBaseRenderer(JComponent renderer);
	
	boolean update();
	
	void addActionListener(ActionListener listener);
	
	void removeActionListener(ActionListener listener);
	
	void addMouseListener(MouseListener listener);
	
	void removeMouseListener(MouseListener listener);
	
	void addMouseMotionListener(MouseMotionListener listener);
	
	void removeMouseMotionListener(MouseMotionListener listener);
	
	void addMouseWheelListener(MouseWheelListener listener);
	
	void removeMouseWheelListener(MouseWheelListener listener);
	
	void addFocusListener(FocusListener listener);
	
	void removeFocusListener(FocusListener listener);
	
	void addComponentListener(ComponentListener listener);
	
	void removeComponentListener(ComponentListener listener);
	
	/**
	 * Performs the custom painting for this layer using the specified <code>Graphics</code> context. The provided
	 * <code>JComponent</code> instance is only supplied to provide information to aid in painting (if necessary)
	 * and need not be painted.
	 * 
	 * @param g - the <code>Graphics</code> context to which custom painting should be performed.
	 * @param c - the <code>JComponent</code> over which this layer is being painted.
	 */
	void paintLayer(Graphics g, JComponent c);
	
}
