package edu.hawaii.ics321f13.view.impl;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.ScrollPaneConstants;

import edu.hawaii.ics321f13.model.interfaces.ImageResult;
import edu.hawaii.ics321f13.model.interfaces.Traversable;
import edu.hawaii.ics321f13.view.interfaces.ImageTransformer;
import edu.hawaii.ics321f13.view.interfaces.View;

public class DefaultView extends JFrame implements View {

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JTextField txtSearch;

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
		contentPane.setBorder(null);
		setContentPane(contentPane);
		setMinimumSize(new Dimension(500, 600));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setForeground(Color.WHITE);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.getViewport().setForeground(Color.WHITE);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setColumnHeaderView(null);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(null);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(10)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtSearch = new JTextField();
		txtSearch.setSelectionColor(Color.LIGHT_GRAY);
		txtSearch.setSelectedTextColor(Color.WHITE);
		txtSearch.setForeground(Color.LIGHT_GRAY);
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchFieldStateChanged(true);
				txtSearch.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				searchFieldStateChanged(false);
			}
		});
		txtSearch.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				searchFieldStateChanged(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				searchFieldStateChanged(false);
			}
		});
		
		txtSearch.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), 
				BorderFactory.createEmptyBorder(0, 10, 0, 10)));
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtSearch.setText("Search images...");
		txtSearch.setColumns(10);
		
		JLabel btnNewButton = new JLabel("\u2794");
		btnNewButton.setForeground(new Color(211, 211, 211));
		btnNewButton.setFont(new Font("Segoe UI Symbol", btnNewButton.getFont().getStyle(), 70));
		
		JLabel btnNewButton_1 = new JLabel("\u2770");
		btnNewButton_1.setForeground(new Color(230, 230, 250));
		btnNewButton_1.setFont(new Font("Segoe UI Symbol", btnNewButton_1.getFont().getStyle(), 60));
		btnNewButton_1.setHorizontalAlignment(SwingConstants.CENTER);
		btnNewButton_1.setBackground(Color.WHITE);
		
		JLabel btnue = new JLabel("\u2771");
		btnue.setForeground(new Color(230, 230, 250));
		btnue.setHorizontalAlignment(SwingConstants.CENTER);
		btnue.setFont(new Font("Segoe UI Symbol", btnue.getFont().getStyle(), 60));
		
		JLabel lblWikiImages = new JLabel("Wiki Images");
		lblWikiImages.setFocusable(true);
		lblWikiImages.setForeground(SystemColor.activeCaption);
		lblWikiImages.setFont(new Font("Segoe UI Light", Font.PLAIN, 50));
		lblWikiImages.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblWikiImages, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(txtSearch, GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(btnue, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(20)
					.addComponent(lblWikiImages, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnue, GroupLayout.PREFERRED_SIZE, 47, Short.MAX_VALUE)
						.addComponent(txtSearch, GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
						.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 47, Short.MAX_VALUE)
						.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 47, Short.MAX_VALUE))
					.addGap(17))
		);
		panel.setLayout(gl_panel);
		
		table = new JTable();
		table.setShowGrid(false);
		table.setShowHorizontalLines(false);
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		table.setRowSelectionAllowed(false);
		table.setTableHeader(null);
		table.setBorder(null);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"A", "B", "C", "D", "E", "F", "G", "H"},
				{"I", "J", "K", "L", "M", "N", "O", "P"},
				{"Q", "R", "S", "T", "U", "V", "W", "X"},
				{"Y", "Z", "0", "1", "2", "3", "4", "5"},
				{"6", "7", "1", "9", "a", "b", "c", "d"},
				{"e", "f", "g", "h", "i", "j", "k", "l"},
				{"m", "n", "o", "p", "q", "r", "s", "t"},
				{"u", "v", "w", "x", "y", "z", "@", "&"},
			},
			new String[] {
				"New column", "New column", "New column", "New column", "New column", "New column", "New column", "New column"
			}
		));
		scrollPane.setViewportView(table);
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
			txtSearch.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			txtSearch.setText(lastSearchFieldText);
		} else if(currentState != active && !active && txtSearch.getMousePosition() == null && !txtSearch.hasFocus()) {
			currentState = active;
			txtSearch.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			lastSearchFieldText = txtSearch.getText();
			txtSearch.setText("  Search images...");
		}
	}
	
	private class MetroButton extends JLabel {
		
		public MetroButton(String text, Font font, Color unselected, Color selected, Color depressed) {
			super(text);
			setFocusable(true);
			setFont(font);
			setForeground(unselected);
		}
		
		public void addActionListener(ActionListener listener) {
			
		}
		
	}
}
