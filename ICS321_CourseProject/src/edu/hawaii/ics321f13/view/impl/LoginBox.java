package edu.hawaii.ics321f13.view.impl;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class LoginBox extends JFrame {

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField Username;

	/**
	 * Create the frame.
	 */
	public LoginBox() {
		setTitle("Login");
		setFont(new Font("Segoe UI", Font.PLAIN, 12));
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 775, 740);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		JPanel LoginWindow = new JPanel();
		LoginWindow.setBackground(Color.WHITE);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(LoginWindow, GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(LoginWindow, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(425, Short.MAX_VALUE))
		);
		
		Username = new JTextField();
		Username.setToolTipText("Enter the username");
		Username.setSelectionColor(Color.LIGHT_GRAY);
		Username.setSelectedTextColor(Color.WHITE);
		Username.setForeground(Color.LIGHT_GRAY);
		Username.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchFieldStateChanged(true);
				Username.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				searchFieldStateChanged(false);
			}
		});
		Username.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				searchFieldStateChanged(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				searchFieldStateChanged(false);
			}
		});
		
		Username.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), 
				BorderFactory.createEmptyBorder(0, 10, 0, 10)));
		Username.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		Username.setText("Username");
		Username.setColumns(10);
		
		JLabel Login = new JLabel("Login");
		Login.setFocusable(true);
		Login.setForeground(SystemColor.activeCaption);
		Login.setFont(new Font("Segoe UI Light", Font.PLAIN, 50));
		Login.setHorizontalAlignment(SwingConstants.CENTER);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setColumns(10);
		pwdPassword.setText("Password");
		pwdPassword.setToolTipText("Enter the password");
		pwdPassword.setForeground(Color.LIGHT_GRAY);
		
		JLabel Enterlogin = new JLabel("\u2794");
		Enterlogin.setForeground(new Color(211, 211, 211));
		Enterlogin.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 70));
		GroupLayout gl_LoginWindow = new GroupLayout(LoginWindow);
		gl_LoginWindow.setHorizontalGroup(
			gl_LoginWindow.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_LoginWindow.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_LoginWindow.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_LoginWindow.createSequentialGroup()
							.addGroup(gl_LoginWindow.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(pwdPassword, Alignment.LEADING)
								.addComponent(Username, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 394, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED))
						.addGroup(gl_LoginWindow.createSequentialGroup()
							.addComponent(Login, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
							.addGap(143)))
					.addComponent(Enterlogin, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addGap(252))
		);
		gl_LoginWindow.setVerticalGroup(
			gl_LoginWindow.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_LoginWindow.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_LoginWindow.createParallelGroup(Alignment.TRAILING)
						.addComponent(Enterlogin, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_LoginWindow.createSequentialGroup()
							.addComponent(Login, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(Username, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
							.addGap(29)
							.addComponent(pwdPassword, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
					.addGap(286))
		);
		LoginWindow.setLayout(gl_LoginWindow);
		contentPane.setLayout(gl_contentPane);
	}

	
	private String lastSearchFieldText = "";
	private boolean currentState = false;
	private JPasswordField pwdPassword;
	private void searchFieldStateChanged(boolean active) {
		if(currentState != active && active) {
			currentState = active;
			Username.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			Username.setText(lastSearchFieldText);
		} else if(currentState != active && !active && Username.getMousePosition() == null && !Username.hasFocus()) {
			currentState = active;
			Username.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			lastSearchFieldText = Username.getText();
			Username.setText("  Search images...");
		}
	}
	
	/**private class MetroButton extends JLabel {
		
		public MetroButton(String text, Font font, Color unselected, Color selected, Color depressed) {
			super(text);
			setFocusable(true);
			setFont(font);
			setForeground(unselected);
		}
		
		public void addActionListener(ActionListener listener) {
			
		}
		
	}**/
}
