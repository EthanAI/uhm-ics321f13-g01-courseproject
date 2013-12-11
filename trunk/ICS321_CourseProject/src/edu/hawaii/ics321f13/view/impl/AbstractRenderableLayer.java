package edu.hawaii.ics321f13.view.impl;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.LayerUI;

import edu.hawaii.ics321f13.view.interfaces.RenderableLayer;

/**
 * 
 * The view that displays the results and user interface. Also has all the listeners that receive user input and 
 * sends it to the controller
 *
 */
public abstract class AbstractRenderableLayer extends LayerUI<JComponent> implements RenderableLayer {

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	
	protected static final int ANIM_FRAMERATE = 24;
	protected static final int FADE_ANIM_PERIOD = 1000 / ANIM_FRAMERATE;
	protected static final Timer ANIM_TIMER = new Timer(FADE_ANIM_PERIOD, null);
	protected static final FixedIntervalRepaintHandler REPAINT_HANDLER = new FixedIntervalRepaintHandler(ANIM_TIMER);
	protected static final long DEFAULT_ANIM_DURATION_MS;
	protected static final int NULL_MULTICLICK_ANIM_DURATION_MS = 400;
	// Rendering constants.
	protected final int FADE_MAX;
	protected final int FADE_MIN = 0;
	protected final EventListenerList LISTENERS = new EventListenerList();
	// State/rendering variables.
	protected float alphaMax = 0.5f;
	protected boolean isInputConsumed = false;
	protected boolean isDirty = false;
	protected JComponent installedUI = null;
	protected volatile boolean isVisible = false;		// Will be accessed by multiple threads concurrently.
	protected RenderableLayer childLayer = null;
	protected volatile int fadeAnimState = FADE_MIN;	// Will be accessed by multiple threads concurrently.
	protected volatile int fadeAnimGoal = FADE_MIN;		// Will be accessed by multiple threads concurrently.
	
	
	static {
		// Add the REPAINT_HANDLER to the ANIM_TIMER. This cannot be done in declaration due to logical constraints.
		ANIM_TIMER.addActionListener(REPAINT_HANDLER);
		ANIM_TIMER.setInitialDelay(0);
		ANIM_TIMER.setRepeats(true);
		// Set up default fade animation duration based on multi-click interval, if available.
		Object multiClickInterval = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
		DEFAULT_ANIM_DURATION_MS = 
				(multiClickInterval == null ? NULL_MULTICLICK_ANIM_DURATION_MS : (Integer) multiClickInterval);
	}
	
	public AbstractRenderableLayer() {
		this(null, DEFAULT_ANIM_DURATION_MS, TimeUnit.MILLISECONDS);
	}
	
	public AbstractRenderableLayer(long fadeDuration, TimeUnit fadeDurationUnit) {
		this(null, fadeDuration, fadeDurationUnit);
	}
	
	public AbstractRenderableLayer(JComponent baseRenderer, long fadeDuration, TimeUnit fadeDurationUnit) {
		if(fadeDuration < 0) {
			throw new IllegalArgumentException("fade duration must be non-negative");
		}
		FADE_MAX = (int) (((double) fadeDurationUnit.toMillis(fadeDuration) / 1000.0) * ANIM_FRAMERATE);
	}
	
	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void setVisible(boolean visible) {
		// Perform the visiblity animation.
		if(installedUI != null && (visible != isVisible || fadeAnimGoal != (visible ? FADE_MAX : FADE_MIN))) {
			if(FADE_MAX != FADE_MIN) {
				// Perform fade animation.
				fadeAnimGoal = (visible ? FADE_MAX : FADE_MIN);
				boolean unique = visible != isVisible;
				isVisible = (visible ? true : fadeAnimState > FADE_MIN);
				if(isVisible && unique) {
					fireComponentShown(new ComponentEvent(getBaseRenderer(), ComponentEvent.COMPONENT_SHOWN));
				}
				if(!ANIM_TIMER.isRunning()) {
					ANIM_TIMER.start();
				}
			} else {
				isVisible = visible;
				installedUI.repaint();
				if(isVisible) {
					fireComponentShown(new ComponentEvent(getBaseRenderer(), ComponentEvent.COMPONENT_SHOWN));
				} else {
					fireComponentHidden(new ComponentEvent(getBaseRenderer(), ComponentEvent.COMPONENT_HIDDEN));
				}
			}
		} else {
			isVisible = visible;
			fadeAnimGoal = (visible ? FADE_MAX : FADE_MIN);
		}
	}
	
	/**
	 * Internal method to indicate that the current layer is dirty and should be repainted.
	 */
	protected void markDirty() {
		isDirty = true;
	}
	
	@Override 
	public boolean isInputConsumed() {
		return isInputConsumed;
	}
	
	@Override
	public void setInputConsumed(boolean inputConsumed) {
		isInputConsumed = inputConsumed;
	}
	
	@Override
	public float getAlpha() {
		float fade = 1.0f;
		if(FADE_MAX != FADE_MIN) {
			fade = (float) fadeAnimState / (float) FADE_MAX;
		}
		return alphaMax * fade;
	}
	
	protected void setAlphaMax(float alpha) {
		if(alpha < 0.0f || alpha > 1.0f) {
			throw new IllegalArgumentException("maximum alpha value must be in the range 0.0 - 1.0");
		}
		alphaMax = alpha;
	}
	
	@Override
	public RenderableLayer getChild() {
		return childLayer;
	}
	
	@Override
	public void setChild(RenderableLayer child) {
		if(childLayer != null && childLayer != child) {
			childLayer.setBaseRenderer(null);
			childLayer = null;
		}
		if(child != null) {
			childLayer = child;
			childLayer.setBaseRenderer(getBaseRenderer());
		}
	}
	
	@Override
	public JComponent getBaseRenderer() {
		return installedUI;
	}
	
	@Override
	public void setBaseRenderer(JComponent renderer) {
		if(getBaseRenderer() != null && getBaseRenderer() != renderer) {
			uninstallUI(installedUI);
		}
		if(renderer != null) {
			installUI(renderer);
		}
	}
	
	@Override
	public boolean update() {
		if(FADE_MIN != FADE_MAX && fadeAnimState != fadeAnimGoal) {
			fadeAnimState += (fadeAnimState < fadeAnimGoal ? 1 : -1);
			boolean newVisibility = fadeAnimState > FADE_MIN;
			if(isVisible != newVisibility) {
				if(!newVisibility) {
					fireComponentHidden(new ComponentEvent(getBaseRenderer(), ComponentEvent.COMPONENT_HIDDEN));
				}
			}
			isVisible = newVisibility;
			return true;
		} else {
			return isDirty;
		}
	}

	@Override
	public void addActionListener(ActionListener listener) {
		LISTENERS.add(ActionListener.class, listener);
	}

	@Override
	public void removeActionListener(ActionListener listener) {
		LISTENERS.remove(ActionListener.class, listener);
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		LISTENERS.add(MouseListener.class, listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		LISTENERS.remove(MouseListener.class, listener);
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener listener) {
		LISTENERS.add(MouseMotionListener.class, listener);
	}

	@Override
	public void removeMouseMotionListener(MouseMotionListener listener) {
		LISTENERS.remove(MouseMotionListener.class, listener);
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		LISTENERS.add(MouseWheelListener.class, listener);
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		LISTENERS.remove(MouseWheelListener.class, listener);
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		LISTENERS.add(FocusListener.class, listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		LISTENERS.remove(FocusListener.class, listener);
	}
	
	@Override
	public void addComponentListener(ComponentListener listener) {
		LISTENERS.add(ComponentListener.class, listener);
	}

	@Override
	public void removeComponentListener(ComponentListener listener) {
		LISTENERS.remove(ComponentListener.class, listener);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		// Paint the view component.
		super.paint(g, c);
		if(c == null || g == null) {
			return;
		}
		Graphics2D g2D = (Graphics2D) g.create();
		// Paint all layers in bottom-up order.
		RenderableLayer currentLayer = this;
		while(currentLayer != null) {
			if(currentLayer.isVisible() && currentLayer.getBaseRenderer() != null) {
				// Set layer alpha to requested value.
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentLayer.getAlpha()));
				// Paint the layer.
				currentLayer.paintLayer(g2D, c);
				if(currentLayer instanceof AbstractRenderableLayer) {
					((AbstractRenderableLayer) currentLayer).isDirty = false; // Reset the isDirty flag.
				}
			}
			currentLayer = currentLayer.getChild();
		}
		g2D.dispose();
	}
	
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		// Set this layer's base renderer.
		installedUI = c;
		REPAINT_HANDLER.registerLayer(this);
		// Set the new base renderer of this layer's child.
		if(getChild() != null) {
			getChild().setBaseRenderer(c);
		}
		// Set up event mask to receive mouse, mouse motion, mouse wheel, key, and focus events.
		if(c instanceof JLayer) {
			((JLayer<?>) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK 
					| AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK 
					| AWTEvent.COMPONENT_EVENT_MASK);
		}
		if(isVisible() && fadeAnimState != fadeAnimGoal && !ANIM_TIMER.isRunning()) {
			ANIM_TIMER.start();
		}
	}
	
	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		// Remove the base renderer of this layer.
		REPAINT_HANDLER.unregisterLayer(this); // MUST be done before discarding installedUI reference!!!
		installedUI = null;
		// Remove the base renderer of this layer's child.
		if(getChild() != null) {
			getChild().setBaseRenderer(null);
		}
		// Remove layer event mask.
		if(c instanceof JLayer) {
			((JLayer<?>) c).setLayerEventMask(0);
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent evt, JLayer<? extends JComponent> layer) {
		if(isVisible() && isInputConsumed()) {
			evt.consume();
			return;
		}
		// If we are not consuming input, fire the appropriate listener.
		switch(Objects.requireNonNull(evt).getID()) {
		case MouseEvent.MOUSE_ENTERED:
			fireMouseEntered(evt);
			break;
		case MouseEvent.MOUSE_EXITED:
			fireMouseExited(evt);
			break;
		case MouseEvent.MOUSE_PRESSED:
			fireMousePressed(evt);
			break;
		case MouseEvent.MOUSE_RELEASED:
			fireMouseReleased(evt);
			break;
		case MouseEvent.MOUSE_CLICKED:
			fireMouseClicked(evt);
			break;
		default:
			throw new IllegalArgumentException("unsupported event type: " + evt.getID());
	
		}
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent evt, JLayer<? extends JComponent> layer) {
		if(isVisible() && isInputConsumed()) {
			evt.consume();
			return;
		}
		// If we are not consuming input, fire the appropriate listener.
		switch(Objects.requireNonNull(evt).getID()) {
		case MouseEvent.MOUSE_MOVED:
			fireMouseMoved(evt);
			break;
		case MouseEvent.MOUSE_DRAGGED:
			fireMouseDragged(evt);
			break;
		default:
			throw new IllegalArgumentException("unsupported event type: " + evt.getID());
	
		}
	}
	
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent evt, JLayer<? extends JComponent> layer) {
		if(isVisible() && isInputConsumed()) {
			evt.consume();
			return;
		}
		// If we are not consuming input, fire the appropriate listener.
		fireMouseWheelMoved(Objects.requireNonNull(evt));
	}
	
	@Override
	protected void processKeyEvent(KeyEvent evt, JLayer<? extends JComponent> layer) {
		if(isVisible() && isInputConsumed()) {
			evt.consume();
			return;
		}
		// If we are not consuming input, fire the appropriate listener.
		switch(Objects.requireNonNull(evt).getID()) {
		case KeyEvent.KEY_PRESSED:
			fireKeyPressed(evt);
			break;
		case KeyEvent.KEY_RELEASED:
			fireKeyReleased(evt);
			break;
		case KeyEvent.KEY_TYPED:
			fireKeyTyped(evt);
			break;
		default:
			throw new IllegalArgumentException("unsupported event type: " + evt.getID());
	
		}
	}
	
	@Override
	protected void processInputMethodEvent(InputMethodEvent evt, JLayer<? extends JComponent> layer) {
		if(isVisible() && isInputConsumed()) {
			evt.consume();
		}
	}
	
	@Override
	protected void processFocusEvent(FocusEvent evt, JLayer<? extends JComponent> layer) {
		switch(Objects.requireNonNull(evt).getID()) {
		case FocusEvent.FOCUS_GAINED:
			fireFocusGained(evt);
			break;
		case FocusEvent.FOCUS_LOST:
			fireFocusLost(evt);
			break;
		default:
			throw new IllegalArgumentException("unsupported event type: " + evt.getID());
	
		}
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent evt, JLayer<? extends JComponent> layer) {
		switch(Objects.requireNonNull(evt).getID()) {
		case ComponentEvent.COMPONENT_HIDDEN:
			fireComponentHidden(evt);
			break;
		case ComponentEvent.COMPONENT_MOVED:
			fireComponentMoved(evt);
			break;
		case ComponentEvent.COMPONENT_RESIZED:
			fireComponentResized(evt);
			break;
		case ComponentEvent.COMPONENT_SHOWN:
			fireComponentShown(evt);
			break;
		default:
			throw new IllegalArgumentException("unsupported event type: " + evt.getID());
	
		}
	}
	
	protected void fireActionPerformed(ActionEvent evt) {
		Objects.requireNonNull(evt);
		ActionListener[] actionListeners = LISTENERS.getListeners(ActionListener.class);
		for(ActionListener listener : actionListeners) {
			if(listener != null) {
				listener.actionPerformed(evt);
			}
		}
	}
	
	protected void fireMouseEntered(MouseEvent evt) {
		Objects.requireNonNull(evt);
		MouseListener[] mouseListeners = LISTENERS.getListeners(MouseListener.class);
		for(MouseListener listener : mouseListeners) {
			if(listener != null) {
				listener.mouseEntered(evt);
			}
		}
	}

	protected void fireMouseExited(MouseEvent evt) {
		Objects.requireNonNull(evt);
		MouseListener[] mouseListeners = LISTENERS.getListeners(MouseListener.class);
		for(MouseListener listener : mouseListeners) {
			if(listener != null) {
				listener.mouseExited(evt);
			}
		}
	}

	protected void fireMousePressed(MouseEvent evt) {
		Objects.requireNonNull(evt);
		MouseListener[] mouseListeners = LISTENERS.getListeners(MouseListener.class);
		for(MouseListener listener : mouseListeners) {
			if(listener != null) {
				listener.mousePressed(evt);
			}
		}
	}
	
	protected void fireMouseReleased(MouseEvent evt) {
		Objects.requireNonNull(evt);
		MouseListener[] mouseListeners = LISTENERS.getListeners(MouseListener.class);
		for(MouseListener listener : mouseListeners) {
			if(listener != null) {
				listener.mouseReleased(evt);
			}
		}
	}
	
	protected void fireMouseClicked(MouseEvent evt) {
		Objects.requireNonNull(evt);
		MouseListener[] mouseListeners = LISTENERS.getListeners(MouseListener.class);
		for(MouseListener listener : mouseListeners) {
			if(listener != null) {
				listener.mouseClicked(evt);
			}
		}
	}
	
	protected void fireMouseMoved(MouseEvent evt) {
		Objects.requireNonNull(evt);
		MouseMotionListener[] mouseMotionListeners = LISTENERS.getListeners(MouseMotionListener.class);
		for(MouseMotionListener listener : mouseMotionListeners) {
			if(listener != null) {
				listener.mouseMoved(evt);
			}
		}
	}
	
	protected void fireMouseDragged(MouseEvent evt) {
		Objects.requireNonNull(evt);
		MouseMotionListener[] mouseMotionListeners = LISTENERS.getListeners(MouseMotionListener.class);
		for(MouseMotionListener listener : mouseMotionListeners) {
			if(listener != null) {
				listener.mouseDragged(evt);
			}
		}
	}
	
	protected void fireMouseWheelMoved(MouseWheelEvent evt) {
		Objects.requireNonNull(evt);
		MouseWheelListener[] mouseWheelListeners = LISTENERS.getListeners(MouseWheelListener.class);
		for(MouseWheelListener listener : mouseWheelListeners) {
			if(listener != null) {
				listener.mouseWheelMoved(evt);
			}
		}
	}
	
	protected void fireKeyPressed(KeyEvent evt) {
		Objects.requireNonNull(evt);
		KeyListener[] keyListeners = LISTENERS.getListeners(KeyListener.class);
		for(KeyListener listener : keyListeners) {
			if(listener != null) {
				listener.keyPressed(evt);
			}
		}
	}
	
	protected void fireKeyReleased(KeyEvent evt) {
		Objects.requireNonNull(evt);
		KeyListener[] keyListeners = LISTENERS.getListeners(KeyListener.class);
		for(KeyListener listener : keyListeners) {
			if(listener != null) {
				listener.keyReleased(evt);
			}
		}
	}
	
	protected void fireKeyTyped(KeyEvent evt) {
		Objects.requireNonNull(evt);
		KeyListener[] keyListeners = LISTENERS.getListeners(KeyListener.class);
		for(KeyListener listener : keyListeners) {
			if(listener != null) {
				listener.keyTyped(evt);
			}
		}
	}
	
	protected void fireFocusGained(FocusEvent evt) {
		Objects.requireNonNull(evt);
		FocusListener[] focusListeners = LISTENERS.getListeners(FocusListener.class);
		for(FocusListener listener : focusListeners) {
			if(listener != null) {
				listener.focusGained(evt);
			}
		}
	}
	
	protected void fireFocusLost(FocusEvent evt) {
		Objects.requireNonNull(evt);
		FocusListener[] focusListeners = LISTENERS.getListeners(FocusListener.class);
		for(FocusListener listener : focusListeners) {
			if(listener != null) {
				listener.focusLost(evt);
			}
		}
	}
	
	protected void fireComponentHidden(ComponentEvent evt) {
		Objects.requireNonNull(evt);
		ComponentListener[] componentListeners = LISTENERS.getListeners(ComponentListener.class);
		for(ComponentListener listener : componentListeners) {
			if(listener != null) {
				listener.componentHidden(evt);
			}
		}
	}
	
	protected void fireComponentMoved(ComponentEvent evt) {
		Objects.requireNonNull(evt);
		ComponentListener[] componentListeners = LISTENERS.getListeners(ComponentListener.class);
		for(ComponentListener listener : componentListeners) {
			if(listener != null) {
				listener.componentMoved(evt);
			}
		}
	}
	
	protected void fireComponentResized(ComponentEvent evt) {
		Objects.requireNonNull(evt);
		ComponentListener[] componentListeners = LISTENERS.getListeners(ComponentListener.class);
		for(ComponentListener listener : componentListeners) {
			if(listener != null) {
				listener.componentResized(evt);
			}
		}
	}
	
	protected void fireComponentShown(ComponentEvent evt) {
		Objects.requireNonNull(evt);
		ComponentListener[] componentListeners = LISTENERS.getListeners(ComponentListener.class);
		for(ComponentListener listener : componentListeners) {
			if(listener != null) {
				listener.componentShown(evt);
			}
		}
	}
}

class FixedIntervalRepaintHandler implements ActionListener {
	
	private final ArrayList<CommonBaseRenderer> RENDERERS = new ArrayList<CommonBaseRenderer>();
	private final Timer HOST_TIMER;
	
	public FixedIntervalRepaintHandler(Timer hostTimer) {
		HOST_TIMER = hostTimer;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		synchronized(RENDERERS) {
			boolean isClean = true;
			for(CommonBaseRenderer renderer : RENDERERS) {
				boolean rendererDirty = renderer.update();
				if(rendererDirty) {
					isClean = false;
					renderer.repaint();
				}
			}
			if(isClean) {
				HOST_TIMER.stop();
			}
		}
	}
	
	public boolean registerLayer(RenderableLayer layer) {
		for(CommonBaseRenderer renderer : RENDERERS) {
			if(renderer.equals(layer.getBaseRenderer())) {
				return renderer.addLayer(layer);
			}
		}
		// If we reach here without returning, the renderer is not yet registered.
		return RENDERERS.add(new CommonBaseRenderer(layer.getBaseRenderer(), layer));
	}
	
	public boolean unregisterLayer(RenderableLayer layer) {
		if(RENDERERS.contains(Objects.requireNonNull(layer.getBaseRenderer()))) {
			return RENDERERS.get(RENDERERS.indexOf(layer.getBaseRenderer())).removeLayer(layer);
		} else {
			return false;
		}
	}
	
	private class CommonBaseRenderer {
		
		private final JComponent BASE_RENDERER;
		private final ArrayList<RenderableLayer> LAYERS = new ArrayList<RenderableLayer>();
		
		public CommonBaseRenderer(JComponent baseRenderer, RenderableLayer...layers) {
			BASE_RENDERER = baseRenderer;
			for(RenderableLayer layer : layers) {
				addLayer(layer);
			}
		}
		
		@SuppressWarnings("unused")
		public RenderableLayer[] getLayers() {
			return LAYERS.toArray(new RenderableLayer[LAYERS.size()]);
		}
		
		public boolean addLayer(RenderableLayer layer) {
			if(!layer.getBaseRenderer().equals(BASE_RENDERER) || LAYERS.contains(layer)) {
				return false;
			} else {
				LAYERS.add(layer);
				return true;
			}
		}
		
		public boolean removeLayer(RenderableLayer layer) {
			if(!layer.getBaseRenderer().equals(BASE_RENDERER) || LAYERS.contains(layer)) {
				return false;
			} else {
				return LAYERS.remove(layer);
			}
		}
		
		public JComponent getBaseRenderer() {
			return BASE_RENDERER;
		}
		
		public boolean update() {
			boolean isDirty = false;
			for(RenderableLayer layer : LAYERS) {
				if(layer.isVisible()) {
					boolean currentDirty = layer.update();
					isDirty = (currentDirty ? true : isDirty);
				}
			}
			return isDirty;
		}
		
		public void repaint() {
			BASE_RENDERER.repaint(); 
		}
		
		@Override
		public boolean equals(Object other) {
			if(other instanceof CommonBaseRenderer) {
				return BASE_RENDERER.equals(((CommonBaseRenderer) other).getBaseRenderer());
			} else if(other instanceof JComponent) {
				return BASE_RENDERER == other || BASE_RENDERER.equals(other);
			} else if(other instanceof RenderableLayer) {
				return BASE_RENDERER.equals(((RenderableLayer) other).getBaseRenderer());
			} else {
				return false;
			}
		}
		
	}
	
}
