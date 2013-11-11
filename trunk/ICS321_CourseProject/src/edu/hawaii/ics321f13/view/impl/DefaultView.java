package edu.hawaii.ics321f13.view.impl;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;

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
import javax.swing.UIManager;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import javax.swing.ScrollPaneConstants;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.ResultConstraint;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.model.interfaces.Traverser;
import edu.hawaii.ics321f13.view.interfaces.ImageLoader;
import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;
import edu.hawaii.ics321f13.view.interfaces.ResultsPage;
import edu.hawaii.ics321f13.view.interfaces.ResultsPage.ActivityChangeAction;
import edu.hawaii.ics321f13.view.interfaces.View;

public class DefaultView extends JFrame implements View {

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	// Image loader.
	private final ImageLoader LOADER;
	// View interface implementation objects.
	private EventListenerList listeners = new EventListenerList();
	private Traversable<ImageResult> imageSourceTraversable = null;
	private Traverser<ImageResult> imageSource = null;
	private ArrayList<ImageTransformer> imageTransformers = new ArrayList<ImageTransformer>();
	private Timer animationTimer = null;
	private BusyGlassPane busyPane = null;
	private volatile boolean isLoading = false;
	// View constants.
	private final int STD_ROW_COUNT = 4;
	private final int STD_COL_COUNT = 8;
	private final int STD_TEXT_HEIGHT = 20;
	private final double STD_PADDING_PERCENT = .06;
	private final String ERROR_ICON_KEY = "OptionPane.errorIcon";
	private final String INFO_ICON_KEY = "OptionPane.informationIcon";
	// View variables.
	private ResultsPage<ImageResult> currentPage = null;
	private ImageTransformer thumbnailXform = null;
	// View components. 
	private Point lastRolloverCell = null;
	private JPanel contentPane;
	private JTable tblImageResults;
	private JTextField txtSearchField;
	private DefaultTableModel imageResultsModel = new DefaultTableModel();
	private JScrollPane scrollPaneImageResults = new JScrollPane();
	private MetroButton btnNext;
	private MetroButton btnPrevious;
	private MetroButton btnSearch;

	/**
	 * Create the frame.
	 */
	public DefaultView(ImageLoader loader) {
		// Set the LOADER field.
		LOADER = loader;
		// Set up the view.
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
		
		txtSearchField = new MetroTextField("Search images...", 
				new Color(192, 192, 192), new Color(192, 192, 192), new Color(160, 160, 160), 
				Color.WHITE, Color.LIGHT_GRAY, Color.WHITE, 
				new Color(192, 192, 192), new Color(175, 175, 175), new Color(160, 160, 160));
		txtSearchField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				search(txtSearchField.getText());
			}
			
		});
		
		// To use GUI editor, comment out this block.
		// START BLOCK
		btnSearch = new MetroButton("\u2794", new Font("Segoe UI Symbol", Font.PLAIN, 70), 
				new Color(210, 210, 210), new Color(160, 160, 160), new Color(185, 185, 185));
		btnSearch.setBackground(Color.WHITE);
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				search(txtSearchField.getText());
			}
			
		});
		
		btnPrevious = new MetroButton("\u2770", new Font("Segoe UI Symbol", Font.PLAIN, 60), 
				new Color(230, 230, 250), new Color(195, 195, 225), new Color(215, 215, 240));
		btnPrevious.setBackground(Color.WHITE);
		btnPrevious.setEnabled(false); // Initially disabled because no results are displayed.
		btnPrevious.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Ignore invalid button presses (should not happen).
				if(currentPage == null) {
					return;
				}
				// Go to the previous page.
				final ResultsPage<ImageResult> PREV_PAGE = currentPage;
				if(currentPage.hasPreviousPage()) {
					currentPage = currentPage.previousPage();
					btnNext.setEnabled(currentPage.hasNextPage());
					btnPrevious.setEnabled(currentPage.hasPreviousPage());
					setBusy(true);
					currentPage.setActive(new Runnable() {

						@Override
						public void run() {
							try {
								setBusy(false);
								PREV_PAGE.close();
							} catch (IOException e) {
								// Should never happen because default close implementation does not throw exception.
								// This exception is carried over from java.io.Closeable interface.
								throw new RuntimeException(
										"an error occurred while closing results page: " 
												+ (e.getMessage() == null ? "<none>" : e.getMessage()), e);
							}
						}
						
					}, ActivityChangeAction.CONCURRENT);
				} else {
					// Beep when the user tries to go past valid page range.
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
		});
		
		btnNext = new MetroButton("\u2771", new Font("Segoe UI Symbol", Font.PLAIN, 60), 
				new Color(230, 230, 250), new Color(195, 195, 225), new Color(215, 215, 240));
		btnNext.setBackground(Color.WHITE);
		btnNext.setEnabled(false); // Initially disabled because no results are displayed.
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Ignore invalid button presses (should not happen).
				if(currentPage == null) {
					return;
				}
				// Go to the next page.
				final ResultsPage<ImageResult> PREV_PAGE = currentPage;
				if(currentPage.hasNextPage()) {
					currentPage = currentPage.nextPage();
					btnNext.setEnabled(currentPage.hasNextPage());
					btnPrevious.setEnabled(currentPage.hasPreviousPage());
					setBusy(true);
					currentPage.setActive(new Runnable() {

						@Override
						public void run() {
							try {
								setBusy(false);
								PREV_PAGE.close();
							} catch (IOException e) {
								// Should never happen because default close implementation does not throw exception.
								// This exception is carried over from java.io.Closeable interface.
								throw new RuntimeException(
										"an error occurred while closing results page: " 
												+ (e.getMessage() == null ? "<none>" : e.getMessage()), e);
							}
						}
						
					}, ActivityChangeAction.CONCURRENT);
				} else {
					// Beep when the user tries to go past valid page range.
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
		});
		
		MetroButton lblInfo = new MetroButton("?", new Font("Segoe UI Semibold", Font.PLAIN, 16), 
				new Color(210, 210, 210), new Color(130, 130, 130), new Color(175, 175, 175));
		lblInfo.setBackground(Color.WHITE);
		lblInfo.setToolTipText("Info");
		lblInfo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = "Credit: Ethan, Kyle, Nathan"
						+ "\nTools/Utilities: Eclipse, JDBC, MySQL, Wikipedia"
						+ "\n"
						+ "\nProject Repository: https://code.google.com/p/uhm-ics321f13-g01-courseproject/"
						+ "\n ";
				JOptionPane.showMessageDialog(DefaultView.this, text, 
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
		tblImageResults.setRowSelectionAllowed(false);
		tblImageResults.setTableHeader(null);
		tblImageResults.setBorder(null);
		tblImageResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblImageResults.setCellSelectionEnabled(true);
		tblImageResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblImageResults.setAutoscrolls(false);
		tblImageResults.setDragEnabled(false);
		
		tblImageResults.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				tblImageResults.clearSelection();
				if(e.getButton() == 1 && e.getClickCount() % 2 == 0) {
					int row = tblImageResults.rowAtPoint(e.getPoint());
					int col = tblImageResults.columnAtPoint(e.getPoint());
					if(row >= 0 && row < tblImageResults.getRowCount() 
							&& col >= 0 && col < tblImageResults.getColumnCount()
							&& tblImageResults.getValueAt(row, col) instanceof ImageResult) {
						URL imageUrl = ((ImageResult) tblImageResults.getValueAt(row, col)).getImageURL();
						if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
							try {
								Desktop.getDesktop().browse(imageUrl.toURI());
							} catch (IOException | URISyntaxException e1) {
								// TODO Display the image locally.
							}
						}
					}
				}
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
				new Color(220, 220, 250), new Color(200, 200, 200), new Color(180, 180, 180), new Color(160, 160, 160), 
				new Font("Segoe UI Light", Font.PLAIN, 15), new Font("Segoe UI Light", Font.PLAIN, 15), 
				SwingConstants.BOTTOM, SwingConstants.CENTER));
		// Initial configuration of the data model.
		imageResultsModel.setColumnCount(STD_COL_COUNT);
		imageResultsModel.setRowCount(STD_ROW_COUNT);
		tblImageResults.setModel(imageResultsModel);
		
		scrollPaneImageResults.setViewportView(tblImageResults);
		contentPane.setLayout(gl_contentPane);
		// Handle window closing event.
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				fireActionPerformed(ViewEventType.CLOSE.getID(), "CLOSE");
			}
			
		});
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				if(currentPage != null) {
					Dimension cellSize = new Dimension();
					cellSize.width = scrollPaneImageResults.getWidth() / STD_COL_COUNT - 1;
					cellSize.height = scrollPaneImageResults.getHeight() / STD_ROW_COUNT;
					for(int i = 0; i < tblImageResults.getColumnCount(); i++) {
						tblImageResults.getColumnModel().getColumn(i).setPreferredWidth(cellSize.width);
						tblImageResults.getColumnModel().getColumn(i).setWidth(cellSize.width);
					}
					tblImageResults.setRowHeight(cellSize.height);
					thumbnailXform = createThumbnailXform(cellSize, STD_TEXT_HEIGHT, STD_PADDING_PERCENT);
					currentPage.scrollToVisible(false);
				}
			}
			
		});
		
		// Set up the Glass Pane.
		busyPane = new BusyGlassPane(this);
		setGlassPane(busyPane);
		setLocationRelativeTo(null);
	}

	@Override
	public void addActionListener(ActionListener listener) {
		listeners.add(ActionListener.class, listener);
	}

	@Override
	public void setImageSource(Traversable<ImageResult> source) {
		if(thumbnailXform == null) {
			// Setup the thumbnail image transformer.
			Dimension cellSize = new Dimension();
			cellSize.width = scrollPaneImageResults.getWidth() / STD_COL_COUNT - 1;
			cellSize.height = scrollPaneImageResults.getHeight() / STD_ROW_COUNT;
			thumbnailXform = createThumbnailXform(cellSize, STD_TEXT_HEIGHT, STD_PADDING_PERCENT);
		}
		if(currentPage != null) {
			try {
				currentPage.close();
			} catch (IOException e) {
				throw new RuntimeException("an error occurred while closing page (IOException): " 
						+ (e.getMessage() == null ? "<none>" : e.getMessage()));
			}
		}
		imageSource = Objects.requireNonNull(source).traverser();
		imageSourceTraversable = source;
		clear();
		if(imageSource.index() > 0 || imageSource.hasNext()) {
			currentPage = new DefaultResultsPage(
					0, STD_ROW_COUNT, STD_COL_COUNT, imageSourceTraversable, LOADER, tblImageResults);
		} else {
			currentPage = new EmptyResultSetPage("No results found", tblImageResults, STD_ROW_COUNT, STD_COL_COUNT);
		}
		setBusy(true);
		btnNext.setEnabled(currentPage.hasNextPage());
		btnPrevious.setEnabled(currentPage.hasPreviousPage());
		currentPage.setActive(new Runnable() {

			@Override
			public void run() {
				setBusy(false);
			}
			
		}, ActivityChangeAction.CONCURRENT);
	}

	@Override
	public void clear() {
		imageResultsModel.setColumnCount(0);
	}

	@Override
	public void setBusy(boolean busy) {
		busyPane.setVisible(busy);
		if(busy) {
			busyPane.start();
		} else {
			busyPane.stop();
		}
	}

	@Override
	public void addImageTransformer(ImageTransformer transformer) {
		imageTransformers.add(Objects.requireNonNull(transformer));
	}

	@Override
	public void addImageTransformer(ImageTransformer transformer, int index) {
		imageTransformers.add(index, Objects.requireNonNull(transformer));
	}

	@Override
	public boolean removeImageTransformer(ImageTransformer transformer) {
		return imageTransformers.remove(Objects.requireNonNull(transformer));
	}

	@Override
	public ImageTransformer removeImageTransformer(int index) {
		return imageTransformers.remove(index);
	}

	@Override
	public void clearImageTransformers() {
		imageTransformers.clear();
	}
	
	private String currentSearchText = "";
	private void search(String text) {
		text = text.trim();
		if(!text.equals(currentSearchText) && !text.isEmpty()) {
			currentSearchText = text;
			fireActionPerformed(ViewEventType.QUERY.getID(), txtSearchField.getText().trim());
		}
	}
	
	private ImageTransformer createThumbnailXform(Dimension cellSize, int textHeight, double paddingPercent) {
		int imageWidth = cellSize.width - (int) (paddingPercent * cellSize.width * 2.0);
		int imageHeight = cellSize.height - textHeight - (int) (paddingPercent * cellSize.height * 2.0);
		return new AggregateImageTransformer(new SizeNormalizationImageTransformer(imageWidth, imageHeight));
	}
	
	private void fireActionPerformed(int id, String command) {
		ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
		ActionEvent event = new ActionEvent(this, id, command);
		for(ActionListener listener : actionListeners) {
			if(listener != null) {
				listener.actionPerformed(event);
			}
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
				if(value != null &&isSelected) {
					rendererComp.setBackground(BG_SELECTED);
					rendererComp.setForeground(FG_SELECTED);
				} else if(value != null && isRollover) {
					rendererComp.setBackground(BG_ROLLOVER);
					rendererComp.setForeground(FG_ROLLOVER);
				} else {
					rendererComp.setBackground(BG_UNSELECTED);
					rendererComp.setForeground(FG_UNSELECTED);
				}
				// Set the value of the renderer component.
				if(value instanceof Icon) {
					rendererComp.setIcon((Icon) value);
					rendererComp.setText("Error");
				} else if(value instanceof Image) {
					rendererComp.setIcon(new ImageIcon((Image) value));
					rendererComp.setText("Error");
				} else if(value instanceof ImageResult) {
					try {
						rendererComp.setIcon(
								new ImageIcon(
										((ImageResult) value).getImage(
												table.getCellRect(row, column, false).getSize(), 
												thumbnailXform)
								));
					} catch (IOException e) {
						// Should not happen because the image loader removes images which fail to load from results.
						rendererComp.setIcon(UIManager.getIcon(ERROR_ICON_KEY));
					}
					rendererComp.setText(((ImageResult) value).getArticleTitle()); // TODO Truncate text if it is too long.
				} else if(value instanceof String) {
					rendererComp.setIcon(UIManager.getIcon(INFO_ICON_KEY));
					rendererComp.setText((String) value);
					// Also disable the selection highlighting.
					rendererComp.setBackground(BG_UNSELECTED);
					rendererComp.setForeground(FG_UNSELECTED);
				} else {
					rendererComp.setIcon(null);
				}
				return rendererComp;
			} else {
				return defaultRndrComp;
			}
		}
		
	}
	
	private class MetroTextField extends JTextField {
		
		// State constants. Opting for integer values here due to inner class restrictions.
		private final int STATE_INACTIVE = -1;	// User is currently editing text.
		private final int STATE_ROLLOVER = 0;	// Mouse is over component.
		private final int STATE_ACTIVE = 1;		// User is currently editing text.
		
		// View constants.
		private final Color INACTIVE_TEXT;
		private final Color ACTIVE_TEXT;
		private final Color ROLLOVER_TEXT;
		
		private final Color UNSELECTED_BG;
		private final Color SELECTED_BG;
		private final Color SELECTED_TEXT;
		
		private final Color INACTIVE_BORDER;
		private final Color ACTIVE_BORDER;
		private final Color ROLLOVER_BORDER;
		
		private final String ON_INACTIVE;
		
		// State save/restore.
		private String activeText = "";
		private int currentState = -100; // Set to invalid state so inital state setting is not ignored as a duplicate.
		
		public MetroTextField(String onInactive, Color inactiveText, Color rolloverText, Color activeText,
				Color unselectedBackground, Color selectedBackground, Color selectedText, 
				Color inactiveBorder, Color rolloverBorder, Color activeBorder) {
			
			super();
			// Variables assignment.
			INACTIVE_TEXT = inactiveText;
			ACTIVE_TEXT = activeText;
			ROLLOVER_TEXT = rolloverText;
			
			UNSELECTED_BG = unselectedBackground;
			SELECTED_BG = selectedBackground;
			SELECTED_TEXT = selectedText;
			
			INACTIVE_BORDER = inactiveBorder;
			ACTIVE_BORDER = activeBorder;
			ROLLOVER_BORDER = rolloverBorder;
			
			ON_INACTIVE = onInactive;
			// Configure selection colors.
			setSelectionColor(SELECTED_BG);
			setSelectedTextColor(SELECTED_TEXT);
			// Configure permanent background state. 
			setBackground(UNSELECTED_BG);
			
			addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					setState(STATE_ACTIVE);
					selectAll();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if(getMousePosition() != null) {
						setState(STATE_ROLLOVER);
					} else {
						setState(STATE_INACTIVE);
					}
				}
			});
			addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseEntered(MouseEvent e) {
					if(currentState != STATE_ACTIVE) {
						setState(STATE_ROLLOVER);
					}
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					if(currentState != STATE_ACTIVE) {
						setState(STATE_INACTIVE);
					}
				}
			});
			
			setFont(new Font("Segoe UI", Font.PLAIN, 20));
			setColumns(10);
			// Set the initial state.
			setState(STATE_INACTIVE);
		}
		
		@Override
		public String getText() {
			if(currentState == STATE_ACTIVE) {
				return super.getText();
			} else {
				return activeText;
			}
		}
		
		private void setState(int state) {
			if(state == currentState) {
				return;
			}
			
			if(state == STATE_INACTIVE) {
				setForeground(INACTIVE_TEXT);
				setBorder(BorderFactory.createCompoundBorder(new LineBorder(INACTIVE_BORDER, 1, false), 
						BorderFactory.createEmptyBorder(0, 10, 0, 10)));
				// Use unmodified verson of the getText() method to guarantee independence of the changes made to the 
				// return value by the method override implemented by this class.
				activeText = super.getText(); 
				setText(ON_INACTIVE);
			} else if(state == STATE_ROLLOVER) {
				setForeground(ROLLOVER_TEXT);
				setBorder(BorderFactory.createCompoundBorder(new LineBorder(ROLLOVER_BORDER, 1, false), 
						BorderFactory.createEmptyBorder(0, 10, 0, 10)));
				if(currentState == STATE_INACTIVE) {
					setText(activeText);
				}
			} else {
				setForeground(ACTIVE_TEXT);
				setBorder(BorderFactory.createCompoundBorder(new LineBorder(ACTIVE_BORDER, 1, false), 
						BorderFactory.createEmptyBorder(0, 10, 0, 10)));
				if(currentState == STATE_INACTIVE) {
					setText(activeText);
				}
			}
			currentState = state;
		}
		
	}
	
	private class MetroButton extends JLabel implements ButtonModel {
		
		/** Serialization support. */
		private static final long serialVersionUID = 1L;
		// Button appearance. 
		protected final Color UNSELECTED_COLOR;
		protected final Color SELECTED_COLOR;
		protected final Color ROLLOVER_COLOR;
		// Button functionality.
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
					if(hasFocus() && !e.isConsumed() && e.getKeyCode() == KeyEvent.VK_ENTER) {
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
	
	private class DummySingletonTraversable implements Traversable<ImageResult> {
		
		private final Traverser<ImageResult> SINGLETON;
		
		public DummySingletonTraversable(Traverser<ImageResult> singleton) {
			SINGLETON = singleton;
		}
		
		@Override
		public Iterator<ImageResult> iterator() {
			return SINGLETON;
		}

		@Override
		public Traverser<ImageResult> traverser() {
			return SINGLETON;
		}
		
	}
	
	private class DummyTraverser implements Traverser<ImageResult> {
		private java.util.Random rand = new java.util.Random();
		private int currentIdx = 0;
		private int maxIdx = rand.nextInt(500);
		
		@Override
		public boolean hasNext() {
			return currentIdx < maxIdx;
		}

		@Override
		public ImageResult next() {
			return new DummyImageResult(currentIdx++ + "");
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasPrevious() {
			return currentIdx > 0;
		}
		
		@Override
		public int index() {
			return currentIdx;
		}

		@Override
		public ImageResult previous() {
			return new DummyImageResult(currentIdx-- + "");
		}
		
		private class DummyImageResult implements ImageResult {
			
			private String title;
			private BufferedImage image = null;
			
			public DummyImageResult(String articleTitle) {
				title = articleTitle;
			}
			public void close() throws IOException {
				image = null;
			}
			public BufferedImage getImage(ImageTransformer...transformers) throws IOException {
				return getImage(null, transformers);
			}
			@Override
			public BufferedImage getImage(Dimension targetSize,
					ImageTransformer... transformers) throws IOException {
				if(image == null) {
					try {
						image = ImageIO.read(new URL(
								"http://i285.photobucket.com/albums/ll45/M00NGRL67/Backgrounds/Green/CheckeredGreen.jpg"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return image;
			}
			public URL getImageURL() {return null;}
			@Override
			public URL getImageURL(Dimension targetSize) {
				return null;
			}
			public String getArticleTitle() {
				return title;
			} 
			public String getArticleAbstract() {return null;}
			public URL getArticleURL() {return null;}
			public boolean isLoaded() {
				return image != null;
			}
			
		}
		
	}
	
}
