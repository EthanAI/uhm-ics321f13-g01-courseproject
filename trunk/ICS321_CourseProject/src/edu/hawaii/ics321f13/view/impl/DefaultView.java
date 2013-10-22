package edu.hawaii.ics321f13.view.impl;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;

import javax.swing.ScrollPaneConstants;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;
import edu.hawaii.ics321f13.view.interfaces.View;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class DefaultView extends JFrame implements View {

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	
	private final int STD_IMAGE_HEIGHT = 100;
	private final int STD_IMAGE_WIDTH = 100;
	private final int STD_TEXT_HEIGHT = 25;
	private final int CELL_PADDING = 10;
	
	private Point lastRolloverCell = null;
	private JPanel contentPane;
	private JTable tblImageResults;
	private JTextField txtSearchField;

	/**
	 * Create the frame.
	 */
	public DefaultView() {
		setTitle("Wikipedia Image Search");
		setFont(new Font("Segoe UI", Font.PLAIN, 12));
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 775, 740);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(BorderFactory.createEmptyBorder());
		setContentPane(contentPane);
		setMinimumSize(new Dimension(500, 600));
		
		JPanel panelControls = new JPanel();
		panelControls.setBackground(Color.WHITE);
		
		JScrollPane scrollPaneImageResults = new JScrollPane();
		scrollPaneImageResults.setBackground(Color.WHITE);
		scrollPaneImageResults.setForeground(Color.WHITE);
		scrollPaneImageResults.getViewport().setBackground(Color.WHITE);
		scrollPaneImageResults.getViewport().setForeground(Color.WHITE);
		scrollPaneImageResults.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneImageResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPaneImageResults.setColumnHeaderView(null);
		scrollPaneImageResults.setViewportBorder(BorderFactory.createEmptyBorder());
		scrollPaneImageResults.setBorder(BorderFactory.createEmptyBorder());
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panelControls, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(10)
					.addComponent(scrollPaneImageResults, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panelControls, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPaneImageResults, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtSearchField = new JTextField();
		txtSearchField.setSelectionColor(Color.LIGHT_GRAY);
		txtSearchField.setSelectedTextColor(Color.WHITE);
		txtSearchField.setForeground(Color.LIGHT_GRAY);
		txtSearchField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchFieldStateChanged(true);
				txtSearchField.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				searchFieldStateChanged(false);
			}
		});
		txtSearchField.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				searchFieldStateChanged(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				searchFieldStateChanged(false);
			}
		});
		
		txtSearchField.setBorder(new CompoundBorder(new LineBorder(new Color(192, 192, 192), 1, false), 
				new EmptyBorder(0, 10, 0, 10)));
		txtSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtSearchField.setText("Search images...");
		txtSearchField.setColumns(10);
		
		// To use GUI editor, comment out this block.
		// START BLOCK
		JLabel btnSearch = new MetroButton("\u2794", new Font("Segoe UI Symbol", Font.PLAIN, 70), 
				new Color(210, 210, 210), new Color(160, 160, 160), new Color(185, 185, 185));
		btnSearch.setBackground(Color.WHITE);
		
		JLabel btnPrevious = new MetroButton("\uE071"/*"\u2770"*/, new Font("Segoe UI Symbol", Font.PLAIN, 42), 
				new Color(230, 230, 250), new Color(195, 195, 225), new Color(215, 215, 240));
		btnPrevious.setBackground(Color.WHITE);
		
		JLabel btnNext = new MetroButton("\u2771", new Font("Segoe UI Symbol", Font.PLAIN, 60), 
				new Color(230, 230, 250), new Color(195, 195, 225), new Color(215, 215, 240));
		btnNext.setBackground(Color.WHITE);
		
		MetroButton lblInfo = new MetroButton("?", new Font("Segoe UI Semibold", Font.PLAIN, 16), 
				new Color(210, 210, 210), new Color(130, 130, 130), new Color(175, 175, 175));
		lblInfo.setBackground(Color.WHITE);
		lblInfo.setToolTipText("Info");
		lblInfo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(DefaultView.this, "Credit: Ethan, Kyle, and Nathan", 
						"Wiki Images Info", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		// END BLOCK
		
		// To use GUI editor, uncomment this block.
		// START BLOCK
//		JLabel btnSearch = new JLabel("\u2794");
//		
//		JLabel btnPrevious = new JLabel("\u2770");
//		btnPrevious.setBackground(Color.WHITE);
//		
//		JLabel btnNext = new JLabel("\u2771");
//		btnNext.setBackground(Color.WHITE);
//		
//		JLabel lblInfo = new JLabel("?");
		// END BLOCK
		
		JLabel lblTitle = new JLabel("Wiki Images");
		lblTitle.setFocusable(true);
		lblTitle.setForeground(SystemColor.activeCaption);
		lblTitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 50));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		
		GroupLayout gl_panelControls = new GroupLayout(panelControls);
		gl_panelControls.setHorizontalGroup(
			gl_panelControls.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelControls.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelControls.createParallelGroup(Alignment.LEADING)
						.addComponent(lblInfo, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
						.addGroup(gl_panelControls.createSequentialGroup()
							.addComponent(btnPrevious, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(txtSearchField, GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(btnNext, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panelControls.setVerticalGroup(
			gl_panelControls.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelControls.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblInfo, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelControls.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNext, GroupLayout.PREFERRED_SIZE, 47, Short.MAX_VALUE)
						.addComponent(txtSearchField, GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
						.addComponent(btnPrevious, GroupLayout.PREFERRED_SIZE, 47, Short.MAX_VALUE)
						.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 47, Short.MAX_VALUE))
					.addGap(17))
		);
		panelControls.setLayout(gl_panelControls);
		
		tblImageResults = new JTable() {
			/** Serialization support. */
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblImageResults.setShowGrid(false);
		tblImageResults.setShowHorizontalLines(false);
		tblImageResults.setFillsViewportHeight(true);
		tblImageResults.setShowVerticalLines(false);
		tblImageResults.getColumnModel().setColumnMargin(0);
		tblImageResults.setRowMargin(0);
		tblImageResults.setRowHeight(STD_IMAGE_HEIGHT + STD_TEXT_HEIGHT + CELL_PADDING);
		tblImageResults.setRowSelectionAllowed(false);
		tblImageResults.setTableHeader(null);
		tblImageResults.setBorder(null);
		tblImageResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblImageResults.setCellSelectionEnabled(true);
		// Set column widths.
		Enumeration<TableColumn> columns = tblImageResults.getColumnModel().getColumns();
		while(columns.hasMoreElements()) {
			columns.nextElement().setWidth(STD_IMAGE_WIDTH + CELL_PADDING);
		}
		
		tblImageResults.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				tblImageResults.clearSelection();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(lastRolloverCell != null) {
					// Repaint the last rollover cell to reset it to the unselected state.
					tblImageResults.repaint(tblImageResults.getCellRect(lastRolloverCell.x, lastRolloverCell.y, true));
				} else {
					// If we do not know what the last rollover cell was, just repaint the whole table.
					tblImageResults.repaint();
				}
				// Unset the last rollover cell.
				lastRolloverCell = null;
			}
			
		});
		tblImageResults.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// Do nothing. 
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Point cellAtPoint = new Point(tblImageResults.rowAtPoint(e.getPoint()), 
						tblImageResults.columnAtPoint(e.getPoint()));
				if(lastRolloverCell == null) {
					// Repaint the dirty cell.
					tblImageResults.repaint(tblImageResults.getCellRect(cellAtPoint.x, cellAtPoint.y, true));
				} else if(lastRolloverCell != null && !lastRolloverCell.equals(cellAtPoint)) {
					// Repaint the dirty cell.
					tblImageResults.repaint(tblImageResults.getCellRect(cellAtPoint.x, cellAtPoint.y, true));
					// Repaint the last rollover cell to reset it to the unselected state.
					tblImageResults.repaint(tblImageResults.getCellRect(lastRolloverCell.x, lastRolloverCell.y, true));
				}
				lastRolloverCell = cellAtPoint;
			}
			
		});
		tblImageResults.setDefaultRenderer(Object.class, new ImageTableCellRenderer(Color.WHITE, new Color(235, 235, 255), 
				new Color(220, 220, 250), new Color(200, 200, 200), new Color(180, 180, 180), new Color(160, 160, 160), new Font("Segoe UI Light", Font.PLAIN, 15), 
				new Font("Segoe UI Light", Font.PLAIN, 15), SwingConstants.CENTER, SwingConstants.CENTER));
		// TODO Remove: test harness.
		final Icon TEST;
		Icon temp = null;
		try {
			temp = new ImageIcon(ImageIO.read(new URL("http://i285.photobucket.com/albums/ll45/M00NGRL67/Backgrounds/Green/CheckeredGreen.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		TEST = temp;
		tblImageResults.setModel(new DefaultTableModel(
			new Object[][] {
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
				{TEST, TEST, TEST, TEST, TEST, TEST, TEST, TEST},
			},
			new String[] {
				"New column", "New column", "New column", "New column", "New column", "New column", "New column", "New column"
			}
		));
		scrollPaneImageResults.setViewportView(tblImageResults);
		contentPane.setLayout(gl_contentPane);
	}

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
	
	private String lastSearchFieldText = "";
	private boolean currentState = false;
	private void searchFieldStateChanged(boolean active) {
		if(currentState != active && active) {
			currentState = active;
			txtSearchField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(150, 150, 150), 1, false), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			txtSearchField.setText(lastSearchFieldText);
		} else if(currentState != active && !active && txtSearchField.getMousePosition() == null && !txtSearchField.hasFocus()) {
			currentState = active;
			txtSearchField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1, false), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			lastSearchFieldText = txtSearchField.getText();
			txtSearchField.setText("  Search images...");
		}
	}
	
	private class ImageTableCellRenderer extends DefaultTableCellRenderer {
		
		// Background color values.
		protected final Color BG_UNSELECTED;
		protected final Color BG_SELECTED;
		protected final Color BG_ROLLOVER;
		// Foreground color values.
		protected final Color FG_UNSELECTED;
		protected final Color FG_SELECTED;
		protected final Color FG_ROLLOVER;
		// Font values.
		protected final Font SELECTED_FONT;
		protected final Font UNSELECTED_FONT;
		// Content alignment.
		protected final int VERTICAL_ALIGN;
		protected final int HORIZONTAL_ALIGN;
		
		public ImageTableCellRenderer(Color unselectedBackground, Color rolloverBackground, Color selectedBackground, 
				Color unselectedForeground, Color rolloverForeground, Color selectedForeground, Font unselectedFont, 
				Font selectedFont, int verticalAlignment, int horizontalAlignment) {
			
			// Background color values.
			BG_UNSELECTED = Objects.requireNonNull(unselectedBackground);
			BG_ROLLOVER = Objects.requireNonNull(rolloverBackground);
			BG_SELECTED = Objects.requireNonNull(selectedBackground);
			// Foreground color values.
			FG_UNSELECTED = Objects.requireNonNull(unselectedForeground);
			FG_ROLLOVER = Objects.requireNonNull(rolloverForeground);
			FG_SELECTED = Objects.requireNonNull(selectedForeground);
			// Font values.
			SELECTED_FONT = Objects.requireNonNull(selectedFont);
			UNSELECTED_FONT = Objects.requireNonNull(unselectedFont);
			// Content alignment.
			VERTICAL_ALIGN = verticalAlignment;
			HORIZONTAL_ALIGN = horizontalAlignment;
			
		}
		
		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column) {
			
			Component defaultRndrComp = 
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(defaultRndrComp instanceof JLabel) {
				JLabel rendererComp = (JLabel) defaultRndrComp;
				boolean isRollover = false;
				Point mousePos = table.getMousePosition();
				if(mousePos != null) {
					int hoverRow = table.rowAtPoint(mousePos);
					int hoverCol = table.columnAtPoint(mousePos);
					isRollover = hoverRow == row && hoverCol == column;
				}
				// Configure basic properties of the renderer component.
				rendererComp.setBorder(BorderFactory.createEmptyBorder());
				rendererComp.setVerticalAlignment(VERTICAL_ALIGN);
				rendererComp.setHorizontalAlignment(HORIZONTAL_ALIGN);
				rendererComp.setFont(isRollover ? SELECTED_FONT : UNSELECTED_FONT);
				rendererComp.setHorizontalTextPosition(SwingConstants.CENTER);
				rendererComp.setVerticalTextPosition(SwingConstants.BOTTOM);
				// Set the foreground and background colors based on selection/rollover.
				if(isSelected) {
					rendererComp.setBackground(BG_SELECTED);
					rendererComp.setForeground(FG_SELECTED);
				} else if(isRollover) {
					rendererComp.setBackground(BG_ROLLOVER);
					rendererComp.setForeground(FG_ROLLOVER);
				} else {
					rendererComp.setBackground(BG_UNSELECTED);
					rendererComp.setForeground(FG_UNSELECTED);
				}
				if(value instanceof Icon) {
					rendererComp.setIcon((Icon) value);
					rendererComp.setText("test"); // TODO Test harness: should be null.
				} else if(value instanceof Image) {
					rendererComp.setIcon(new ImageIcon((Image) value));
					rendererComp.setText(null);
				} else if(value instanceof ImageResult) {
					rendererComp.setIcon(new ImageIcon(((ImageResult) value).getImage()));
					rendererComp.setText(((ImageResult) value).getArticleTitle()); // TODO Truncate text if it is too long.
				} else {
					rendererComp.setIcon(null);
				}
				return rendererComp;
			} else {
				return defaultRndrComp;
			}
		}
		
	}
	
	private class MetroButton extends JLabel implements ButtonModel {
		
		protected final Color UNSELECTED_COLOR;
		protected final Color SELECTED_COLOR;
		protected final Color ROLLOVER_COLOR;
		
		protected boolean isPressed = false;
		protected boolean isRollover = false;
		protected EventListenerList listeners = new EventListenerList();
		protected int mnemonic = KeyEvent.VK_UNDEFINED;
		protected String actionCommand = "BUTTON_SELECT";
		protected ButtonGroup buttonGroup = null;
		
		public MetroButton(String text, Font font, Color unselected, Color selected, Color rollover) {
			// Construct the JLabel.
			super(Objects.requireNonNull(text));
			// Variable assignment;
			UNSELECTED_COLOR = Objects.requireNonNull(unselected);
			SELECTED_COLOR = Objects.requireNonNull(selected);
			ROLLOVER_COLOR = Objects.requireNonNull(rollover);
			// Configuration.
			setFocusable(true);
			setFont(Objects.requireNonNull(font));
			setForeground(unselected);
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);
			// Selection support: Mouse.
			addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// Do nothing. This is already handled by the press/release handlers.
				}

				@Override
				public void mousePressed(MouseEvent e) {
					setSelected(true);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					setSelected(false);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					setRollover(true);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if(isSelected()) {
						setSelected(false);
					}
					setRollover(false);
				}
				
			});
			// Selection support: Focus.
			addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
					setRollover(true);
				}

				@Override
				public void focusLost(FocusEvent e) {
					if(isSelected()) {
						setSelected(false);
					}
					setRollover(false);
				}
				
			});
			// Selection support: Keyboard.
			addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					// Do nothing. This is already handled by the press/release handlers.
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if(!e.isConsumed() && (e.getKeyCode() == KeyEvent.VK_ENTER)) {
						setSelected(true);
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					if(!e.isConsumed() && e.getKeyCode() == KeyEvent.VK_ENTER) {
						setSelected(false);
					}
				}
				
			});
			// Selection support: mnemonic.
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

				@Override
				public boolean dispatchKeyEvent(KeyEvent e) {
					if(!e.isConsumed() && e.getKeyCode() == KeyEvent.VK_ENTER) {
						if(e.getID() == KeyEvent.KEY_PRESSED && e.isAltDown()) {
							setSelected(true);
							e.consume();
							return true; // Halt notifications for this KeyEvent.
						} else if(e.getID() == KeyEvent.KEY_RELEASED) {
							setSelected(false);
							e.consume();
							return true; // Halt notifications for this KeyEvent.
						}
					}
					// If we will not be processing this KeyEvent as a keyboard accelerator (mnemonic), 
					// permit KeyEvent to be dispatched normally.
					return false;
				}
				
			});
		}

		@Override
		public Object[] getSelectedObjects() {
			if(isPressed) {
				return new Object[] {this};
			} else {
				return null;
			}
		}

		@Override
		public boolean isArmed() {
			return isPressed;
		}

		@Override
		public boolean isSelected() {
			return isPressed;
		}

		@Override
		public boolean isPressed() {
			return isPressed;
		}

		@Override
		public boolean isRollover() {
			return isRollover;
		}

		@Override
		public void setArmed(boolean b) {
			setPressed(b);
		}

		@Override
		public void setSelected(boolean b) {
			setPressed(b);
		}

		@Override
		public void setPressed(boolean pressed) {
			if(!isEnabled()) {
				return;
			}
			isPressed = pressed;
			setRollover(pressed ? false : getMousePosition() != null);
			if(pressed) {
				setForeground(SELECTED_COLOR);
				if(!hasFocus()) {
					requestFocusInWindow();
				}
			}
			if(buttonGroup != null) {
				buttonGroup.setSelected(this, pressed);
			}
			fireSelectionEvents(pressed);
		}

		@Override
		public void setRollover(boolean rollover) {
			if(!isEnabled()) {
				return;
			}
			isRollover = rollover;
			if(!isPressed()) {
				setForeground(rollover ? ROLLOVER_COLOR : UNSELECTED_COLOR);
			}
		}

		@Override
		public void setMnemonic(int key) {
			mnemonic = key;
		}

		@Override
		public int getMnemonic() {
			return mnemonic;
		}

		@Override
		public void setActionCommand(String s) {
			actionCommand = s;
		}

		@Override
		public String getActionCommand() {
			return actionCommand;
		}

		@Override
		public void setGroup(ButtonGroup group) {
			buttonGroup = group;
		}

		@Override
		public void addActionListener(ActionListener l) {
			listeners.add(ActionListener.class, Objects.requireNonNull(l));
		}

		@Override
		public void removeActionListener(ActionListener l) {
			listeners.remove(ActionListener.class, Objects.requireNonNull(l));
		}

		@Override
		public void addItemListener(ItemListener l) {
			listeners.add(ItemListener.class, Objects.requireNonNull(l));
		}

		@Override
		public void removeItemListener(ItemListener l) {
			listeners.remove(ItemListener.class, Objects.requireNonNull(l));
		}

		@Override
		public void addChangeListener(ChangeListener l) {
			listeners.add(ChangeListener.class, Objects.requireNonNull(l));
		}

		@Override
		public void removeChangeListener(ChangeListener l) {
			listeners.remove(ChangeListener.class, Objects.requireNonNull(l));
		}
		
		protected void fireSelectionEvents(boolean selected) {
			// Fire item state changed events.
			ItemListener[] itemListeners = listeners.getListeners(ItemListener.class);
			for(ItemListener listener : itemListeners) {
				listener.itemStateChanged(new ItemEvent(this, (selected ? ItemEvent.ITEM_FIRST : ItemEvent.ITEM_LAST), 
						this, (selected ? ItemEvent.SELECTED : ItemEvent.DESELECTED)));
			}
			// Fire change events.
			ChangeListener[] changeListeners = listeners.getListeners(ChangeListener.class);
			for(ChangeListener listener : changeListeners) {
				listener.stateChanged(new ChangeEvent(this));
			}
			// Fire action events only on button release.
			if(!selected) {
				ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
				for(ActionListener listener : actionListeners) {
					listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
				}
			}
		}
		
	}
}